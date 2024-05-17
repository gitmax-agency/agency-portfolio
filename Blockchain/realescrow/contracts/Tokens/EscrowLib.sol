// SPDX-License-Identifier: BUSL-1.1
pragma solidity ^0.8.9;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

uint8 constant STATE_NEW = 0;
uint8 constant STATE_FUNDING_DEPOSIT = 1;
uint8 constant STATE_CONTINGENCIES = 2;
uint8 constant STATE_CONTINGENCIES_FUNDING = 3;
uint8 constant STATE_CONTINGENCIES_DISPUTE = 4;
uint8 constant STATE_FUNDING_PURCHASE = 5;
uint8 constant STATE_REGISTRATION = 6;
uint8 constant STATE_REGISTRATION_FUNDING = 7;
uint8 constant STATE_REGISTRATION_DISPUTE = 8;
uint8 constant STATE_COMPLETED = 9;
uint8 constant STATE_FAILED = 10;

// There must be at least one Buyer, one Seller, and one Arbiter.
// If no Registrator, the Arbiter receives the Registration Fee. The Registration Fee is paid immediately when the escrow is funded.
uint8 constant USER_TYPE_BUYER = 1;
uint8 constant USER_TYPE_BUYERS_LENDER = 2;
uint8 constant USER_TYPE_SELLER = 3;
uint8 constant USER_TYPE_SELLERS_LENDER = 4;
uint8 constant USER_TYPE_ARBITER = 5;
uint8 constant USER_TYPE_REGISTRATOR = 6;
uint8 constant USER_TYPE_BROKER = 7;

uint8 constant VOTE_NONE = 0;
uint8 constant VOTE_YES = 1;
uint8 constant VOTE_NO = 2;

uint8 constant VOTE_RESULT_NONE = 0;
uint8 constant VOTE_RESULT_APPROVED = 1;
uint8 constant VOTE_RESULT_DECLINED = 2;
uint8 constant VOTE_RESULT_DISPUTE = 3;

uint256 constant TEN_P_18 = 1000000000000000000;
uint256 constant MAX_UNIT256 = 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff;

struct EscrowRules {
    ERC20 paymentToken;
    uint256 depositAmount;          // set to 0 if no Holding Deposit
    uint256 registrationFee;        // paid to Registrator when purchase amount is funded
    uint256 arbiterFee;             // paid to Arbiter when escrow is completed or failed
    uint256 contingencyDisputeFee;  // if 0, the Arbiter decides
    uint256 registrationDisputeFee; // if 0, the Arbiter decides
    string contingencyConditions;   // if empty, no contingencies need to be completed by the Seller
    string registrationConditions;

    // All times are in seconds
    uint256 contingencyTime;        // time to satisfy contingency conditions after holding deposit is funded       
    uint256 fundingTime;            // time to fund the escrow after contingencies
    uint256 registrationTime;       // time to register after funding
    uint256 responseTime;           // time to fund disputes

    EscrowUser[] users;
}

struct EscrowUser {
    address userAddress;
    uint8 userType;
    uint256 amount;
}

struct EscrowData {
    uint8 state;
    uint256 saleAmount;                 // Total amount to be received by all sellers, sellers lenders, and brokers
    uint256 escrowFee;
    uint256[] userDeposits;
    uint256[] userDisputeDeposits;
    uint256 buyersDepositAmount;
    uint256 buyersDisputeDepositAmount;
    uint256 sellersDisputeDepositAmount;

    uint256 depositFundedTime;
    uint256 contingenciesCompletedTime;
    uint256 purchaseFundedTime;
    uint256 disputeFundedTime;
    uint256 registrationFeePaid;        // Registration fee remaining to be paid

    address escrowFeeRecipient;
}

struct VoteData {
    uint8[] votes;
    bool isVotePending;
    uint256 voteStartTime;
}

enum EscrowEvent {
    Funding,
    DisputeFunding,
    ContingenciesApproved,
    ContingenciesDeclined,
    ContingenciesDisputed,
    FundingDefaulted,
    RegistrationApproved,
    RegistrationDeclined,
    RegistrationDisputed
}

library EscrowLib {
    event RulesProposed();
    event RulesApproved();
    event RulesDeclined();
    event VotingStarted();
    event StateChanged(uint8 state);
    event Funded(address userAddress, int256 amount);
    event DepositedForDispute(address userAddress, uint256 amount);

    function changeState(EscrowRules storage rules, EscrowData storage data, EscrowEvent escrowEvent) public {
        uint8 oldState = data.state;

        if (escrowEvent == EscrowEvent.Funding) {
            if (data.state == STATE_FUNDING_DEPOSIT) {
                if (rules.depositAmount <= data.buyersDepositAmount) {
                    _changeStateOverDeposit(rules, data);
                }
            } else if (data.state == STATE_FUNDING_PURCHASE) {
                uint256 escrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
                if (data.saleAmount + escrowFee + rules.arbiterFee + rules.registrationFee - data.registrationFeePaid <= data.buyersDepositAmount) {
                    data.state = STATE_REGISTRATION;
                    data.purchaseFundedTime = block.timestamp;
                    payRegistrationFee(rules, data);
                }
            }
        } else if (escrowEvent == EscrowEvent.DisputeFunding) {
            if (data.state == STATE_CONTINGENCIES_FUNDING) {
                if (rules.contingencyDisputeFee <= data.buyersDisputeDepositAmount || rules.contingencyDisputeFee <= data.sellersDisputeDepositAmount) {
                    data.state = STATE_CONTINGENCIES_DISPUTE;
                    data.disputeFundedTime = block.timestamp;
                }
            } else if (data.state == STATE_REGISTRATION_FUNDING) {
                if (rules.registrationDisputeFee <= data.buyersDisputeDepositAmount || rules.registrationDisputeFee <= data.sellersDisputeDepositAmount) {
                    data.state = STATE_REGISTRATION_DISPUTE;
                    data.disputeFundedTime = block.timestamp;
                }
            }
        } else if (escrowEvent == EscrowEvent.ContingenciesApproved) {
            uint256 escrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
            if (data.saleAmount + escrowFee + rules.arbiterFee + rules.registrationFee - data.registrationFeePaid <= data.buyersDepositAmount) {
                data.state = STATE_REGISTRATION;
                data.purchaseFundedTime = block.timestamp;
                payRegistrationFee(rules, data);
            } else {
                data.state = STATE_FUNDING_PURCHASE;
            }
        } else if (escrowEvent == EscrowEvent.ContingenciesDeclined) {
            data.state = STATE_FAILED;
            data.depositFundedTime = 0;
        } else if (escrowEvent == EscrowEvent.ContingenciesDisputed) {
            data.state = STATE_CONTINGENCIES_FUNDING;
        } else if (escrowEvent == EscrowEvent.FundingDefaulted) {
            data.state = STATE_FAILED;
        } else if (escrowEvent == EscrowEvent.RegistrationApproved) {
            data.state = STATE_COMPLETED;
        } else if (escrowEvent == EscrowEvent.RegistrationDeclined) {
            data.state = STATE_FAILED;
        } else if (escrowEvent == EscrowEvent.RegistrationDisputed) {
            data.state = STATE_REGISTRATION_FUNDING;
        }

        if (oldState != data.state) emit StateChanged(data.state);
    }

    // New rules can change the holding deposit amount, the sale amount, the registration fee, and the dispute fees
    function changeStateNewRules(EscrowRules storage rules, EscrowData storage data, VoteData storage voteData) public {
        uint8 oldState = data.state;

        if (data.state == STATE_NEW) {
            // No deposits could be made in the new state
            data.state = 0 < rules.depositAmount ? STATE_FUNDING_DEPOSIT : STATE_FUNDING_PURCHASE;
        } else if (data.state == STATE_FUNDING_DEPOSIT) {
            if (rules.depositAmount <= data.buyersDepositAmount) {
                _changeStateOverDeposit(rules, data);
            }
        } else if (data.buyersDepositAmount < rules.depositAmount) {
            data.state = STATE_FUNDING_DEPOSIT;
            data.depositFundedTime = 0;
            data.contingenciesCompletedTime = 0;
            data.purchaseFundedTime = 0;
            data.disputeFundedTime = 0;
            voteData.isVotePending = false;
            voteData.voteStartTime = 0;
        // Nothing to check for STATE_CONTINGENCIES, as they need to be completed first
        } else if (data.state == STATE_CONTINGENCIES_FUNDING) {
            if (rules.contingencyDisputeFee <= data.buyersDisputeDepositAmount || rules.contingencyDisputeFee <= data.sellersDisputeDepositAmount) {
                data.state = STATE_CONTINGENCIES_DISPUTE;
                data.disputeFundedTime = block.timestamp;
            }
        } else if (data.state == STATE_CONTINGENCIES_DISPUTE) {
            if (data.buyersDisputeDepositAmount < rules.contingencyDisputeFee && data.sellersDisputeDepositAmount < rules.contingencyDisputeFee) 
                data.state = STATE_CONTINGENCIES_FUNDING;
        } else if (data.state == STATE_FUNDING_PURCHASE) {
            uint256 escrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
            if (data.saleAmount + escrowFee + rules.arbiterFee + rules.registrationFee - data.registrationFeePaid <= data.buyersDepositAmount) {
                data.state = STATE_REGISTRATION;
                data.purchaseFundedTime = block.timestamp;
                payRegistrationFee(rules, data);
            }
        } else if (data.state == STATE_REGISTRATION) {
            uint256 escrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
            if (data.buyersDepositAmount < data.saleAmount + escrowFee + rules.arbiterFee + rules.registrationFee - data.registrationFeePaid) {
                console.log("STATE_FUNDING_PURCHASE");

                data.state = STATE_FUNDING_PURCHASE;
                data.purchaseFundedTime = 0;
            }
        } else if (data.state == STATE_REGISTRATION_FUNDING) {
            if (rules.registrationDisputeFee <= data.buyersDisputeDepositAmount || rules.registrationDisputeFee <= data.sellersDisputeDepositAmount) {
                data.state = STATE_REGISTRATION_DISPUTE;
                data.disputeFundedTime = block.timestamp;
            }
        } else if (data.state == STATE_REGISTRATION_DISPUTE) {
            if (data.buyersDisputeDepositAmount < rules.registrationDisputeFee && data.sellersDisputeDepositAmount < rules.registrationDisputeFee) 
                data.state = STATE_REGISTRATION_FUNDING;
        }

        if (oldState != data.state) emit StateChanged(data.state);
    }

    // Deposits will revert when the escrow is in the "new" state, as there are no users
    function deposit(EscrowRules storage rules, EscrowData storage data, address userAddress, uint256 amount) public {
        uint256 index;
        for (index = 0; index < rules.users.length; index++) 
            if (userAddress == rules.users[index].userAddress && isBuyerOrLender(rules, index)) break;
        require(index < rules.users.length, "The sender must be a buyer or buyer's lender."); 

        console.log("Deposit: index = %s, amount = %s", index, amount);

        data.userDeposits[index] += amount;
        data.buyersDepositAmount += amount;

        rules.paymentToken.transferFrom(userAddress, address(this), amount);

        emit Funded(userAddress, int256(amount));
        changeState(rules, data, EscrowEvent.Funding);
    }

    function withdraw(EscrowRules storage rules, EscrowData storage data, address userAddress, uint256 amount) external {
        require((rules.depositAmount != 0 && data.state == STATE_FUNDING_DEPOSIT) || (rules.depositAmount == 0 && data.state == STATE_FUNDING_PURCHASE), 
            "Can only withdraw funds in the holding deposit funding or purchase funding states.");

        uint256 index;
        for (index = 0; index < rules.users.length; index++) 
            if (userAddress == rules.users[index].userAddress && isBuyerOrLender(rules, index)) break;
        require(index < rules.users.length, "The sender must be a buyer or buyer's lender."); 

        require(amount <= data.userDeposits[index], "Can only withdraw up to the amount deposited by sender."); 

        transferTokenToUser(rules, data, index, amount, true);

        emit Funded(userAddress, -int256(amount));
        changeState(rules, data, EscrowEvent.Funding);
    }

    function depositDisputeFee(EscrowRules storage rules, EscrowData storage data, address userAddress, uint256 amount) external {
        uint256 index;
        for (index = 0; index < rules.users.length; index++) 
            if (userAddress == rules.users[index].userAddress && (isBuyerOrLender(rules, index) || isSellerOrLender(rules, index))) break;
        require(index < rules.users.length, "The sender must be a buyer, seller, or lender."); 

        console.log("depositDisputeFee: index = %s, amount = %s", index, amount);

        data.userDisputeDeposits[index] += amount;
        if (isBuyerOrLender(rules, index)) data.buyersDisputeDepositAmount += amount;
        else if (isSellerOrLender(rules, index)) data.sellersDisputeDepositAmount += amount;

        rules.paymentToken.transferFrom(userAddress, address(this), amount);

        emit DepositedForDispute(userAddress, amount);
        changeState(rules, data, EscrowEvent.DisputeFunding);
    }

    function payRegistrationFee(EscrowRules storage rules, EscrowData storage data) private {
        if (rules.registrationFee == data.registrationFeePaid) return;

        EscrowLib.EscrowUserData memory escrowUserData = getEscrowUserData(rules, data);

        uint256 feeAmount = rules.registrationFee - data.registrationFeePaid;
        uint256 remainingAmount = feeAmount;
        
        console.log("Paying registration fee to user %s, amount %s", escrowUserData.registratorIndex, rules.registrationFee - data.registrationFeePaid);

        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userType == USER_TYPE_BUYER) {
                uint256 amount = computeShare(feeAmount, rules.users[i].amount, data.userDeposits[i], escrowUserData.buyersAmount);
                data.userDeposits[i] -= amount;
                remainingAmount -= amount;

                console.log("User %s, amount %s", i, amount);
            }
        }

        for (uint256 i = 0; i < rules.users.length && 0 < remainingAmount; i++) {
            if (rules.users[i].userType == USER_TYPE_BUYER) {
                uint256 amount = min(data.userDeposits[i], remainingAmount);
                data.userDeposits[i] -= amount;
                remainingAmount -= amount;
            }
        }

        data.buyersDepositAmount -= feeAmount - remainingAmount;
        data.registrationFeePaid += feeAmount - remainingAmount;

        transferTokenToUser(rules, data, escrowUserData.registratorIndex, feeAmount, false);
    }

    struct EscrowUserData {
        uint256 arbiterIndex;
        uint256 registratorIndex;
        uint256 lastBuyerIndex;
        uint256 lastSellerIndex;
        uint256 buyersAmount;
        uint256 buyersDeposit;
        uint256 sellersAmount;
    }

    function getEscrowUserData(EscrowRules storage rules, EscrowData storage data) public view returns (EscrowUserData memory) {
        EscrowUserData memory escrowUserData;
        int256 registratorIndex = -1;

        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userType == USER_TYPE_REGISTRATOR) {
                registratorIndex = int256(i);
            } else if (rules.users[i].userType == USER_TYPE_ARBITER) {
                escrowUserData.arbiterIndex = i;
            } else if (rules.users[i].userType == USER_TYPE_BUYER) {
                escrowUserData.lastBuyerIndex = i;
                escrowUserData.buyersAmount += rules.users[i].amount;
                escrowUserData.buyersDeposit += data.userDeposits[i];
            } else if (rules.users[i].userType == USER_TYPE_SELLER) {
                escrowUserData.lastSellerIndex = i;
                escrowUserData.sellersAmount += rules.users[i].amount;
            }
        }

        escrowUserData.registratorIndex = registratorIndex == -1 ? escrowUserData.arbiterIndex : uint256(registratorIndex);

        return escrowUserData;
    }

    function transferTokenToUser(EscrowRules storage rules, EscrowData storage data, uint256 userIndex, uint256 amount, bool updateBalance) public {
        if (amount == 0) return;

        console.log("Transferring to user %s, type %s, amount %s", userIndex, rules.users[userIndex].userType, amount);

        if (updateBalance) {
            data.userDeposits[userIndex] -= amount;
            if (isBuyerOrLender(rules, userIndex)) data.buyersDepositAmount -= amount;
        }

        rules.paymentToken.transferFrom(address(this), rules.users[userIndex].userAddress, amount);
    }

    function transferToEscrowRecipient(EscrowRules storage rules, EscrowData storage data) public {
        uint256 amount = rules.paymentToken.balanceOf(address(this));

        if (amount == 0) return;

        console.log("Transferring to escrow fee recipient, amount %s", amount);

        rules.paymentToken.transferFrom(address(this), data.escrowFeeRecipient, amount);
    }

    function isBuyerOrLender(EscrowRules storage rules, uint256 userIndex) public view returns (bool) {
        return rules.users[userIndex].userType == USER_TYPE_BUYER || rules.users[userIndex].userType == USER_TYPE_BUYERS_LENDER;
    }

    function isSellerOrLender(EscrowRules storage rules, uint256 userIndex) public view returns (bool) {
        return rules.users[userIndex].userType == USER_TYPE_SELLER || rules.users[userIndex].userType == USER_TYPE_SELLERS_LENDER;
    }

    function isSellerLenderOrBroker(EscrowRules storage rules, uint256 userIndex) public view returns (bool) {
        return rules.users[userIndex].userType == USER_TYPE_SELLER || rules.users[userIndex].userType == USER_TYPE_SELLERS_LENDER || rules.users[userIndex].userType == USER_TYPE_BROKER;
    }

    function computeShare(uint256 fee, uint256 userAmount, uint256 userDeposit, uint256 totalUserAmount) public pure returns (uint256) {
        if (fee == 0 || userAmount == 0 || userDeposit == 0) return 0;
        uint256 amount = fee * userAmount / totalUserAmount;
        if (_abs(int(amount) - int(userAmount)) * 100 < userAmount) amount = userAmount;
        return min(amount, userDeposit);
    }

    function resetVotes(VoteData storage voteData) public {
        for (uint256 i = 0; i < voteData.votes.length; i++) voteData.votes[i] = VOTE_NONE;
    }

    function min(uint256 a, uint256 b) public pure returns (uint256) {
        return a < b ? a : b;
    }

    function _changeStateOverDeposit(EscrowRules storage rules, EscrowData storage data) private {
        data.depositFundedTime = block.timestamp;
        if (bytes(rules.contingencyConditions).length == 0) {
            uint256 escrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
            if (data.saleAmount + escrowFee + rules.arbiterFee + rules.registrationFee - data.registrationFeePaid <= data.buyersDepositAmount) {
                data.state = STATE_REGISTRATION;
                data.purchaseFundedTime = block.timestamp;
                payRegistrationFee(rules, data);
            } else {
                data.state = STATE_FUNDING_PURCHASE;
            }
        } else {
            data.state = STATE_CONTINGENCIES;
        }
    }

    function _abs(int x) private pure returns (uint256) {
        return x >= 0 ? uint256(x) : uint256(-x);
    }
}
