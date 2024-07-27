// SPDX-License-Identifier: BUSL-1.1
pragma solidity ^0.8.9;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

import "./EscrowLib.sol";
import "./ActionLib.sol";

library VoteLib {
    function voteOnContingencies(EscrowRules storage rules, EscrowData storage data, VoteData storage voteData, address userAddress, bool vote) public {
        require(data.state == STATE_CONTINGENCIES
            || data.state == STATE_CONTINGENCIES_FUNDING
            || data.state == STATE_CONTINGENCIES_DISPUTE, "Can only vote on contingeincies in the contingency state.");

        if (!voteData.isVotePending) {
            checkCanAcceptVote(rules, data, userAddress, vote, false);
            voteData.isVotePending = true;
            voteData.voteStartTime = block.timestamp;
            EscrowLib.resetVotes(voteData);

            emit EscrowLib.VotingStarted();
        }

        uint8 voteResult = processVote(rules, voteData, userAddress, vote, false);

        if (voteResult == VOTE_RESULT_NONE) {
            console.log("Contingency vote incomplete.");
        } else if (voteResult == VOTE_RESULT_DISPUTE) {
            console.log("Contingencies disputed.");

            EscrowLib.changeState(rules, data, EscrowEvent.ContingenciesDisputed);
        } else if (voteResult == VOTE_RESULT_APPROVED) {
            console.log("Contingencies approved!");

            voteData.isVotePending = false;
            ActionLib.approveContingencies(rules, data);
        } else if (voteResult == VOTE_RESULT_DECLINED) {
            console.log("Contingencies declined.");

            voteData.isVotePending = false;
            ActionLib.declineContingencies(rules, data);
        }
    }

    function voteOnRegistration(EscrowRules storage rules, EscrowData storage data, VoteData storage voteData, address userAddress, bool vote) public {
        require(data.state == STATE_REGISTRATION
            || data.state == STATE_REGISTRATION_FUNDING
            || data.state == STATE_REGISTRATION_DISPUTE, "Can only vote on registration in the registration state.");

        if (!voteData.isVotePending) {
            checkCanAcceptVote(rules, data, userAddress, vote, true);
            voteData.isVotePending = true;
            voteData.voteStartTime = block.timestamp;
            EscrowLib.resetVotes(voteData);

            emit EscrowLib.VotingStarted();
        }

        uint8 voteResult = processVote(rules, voteData, userAddress, vote, true);

        if (voteResult == VOTE_RESULT_NONE) {
            console.log("Registration vote incomplete.");
        } else if (voteResult == VOTE_RESULT_DISPUTE) {
            console.log("Registration disputed.");

            EscrowLib.changeState(rules, data, EscrowEvent.RegistrationDisputed);
        } else if (voteResult == VOTE_RESULT_APPROVED) {
            console.log("Registration approved!");

            voteData.isVotePending = false;
            ActionLib.approveRegistration(rules, data);
        } else if (voteResult == VOTE_RESULT_DECLINED) {
            console.log("Registration declined.");

            voteData.isVotePending = false;
            ActionLib.declineRegistration(rules, data);
        }
    }

    // Contingency default can be claimed if:
    //     1) Contingecies time elapsed since deposit funding and no Sellers voted on contingencies
    //     2) Sellers voted for approving contingenceis, and Buyers didn't vote after ActionTime
    //     3) There was a dispute, and only one party diposed the dispute deposit amount after responseTime
    function claimContingenciesDefault(EscrowRules storage rules, EscrowData storage data, VoteData storage voteData, address userAddress) public {
        require(data.state == STATE_CONTINGENCIES
            || data.state == STATE_CONTINGENCIES_DISPUTE, "Can only claim contingencies default in contingency state.");

        uint256 index;
        for (index = 0; index < rules.users.length; index++) 
            if (userAddress == rules.users[index].userAddress 
                && (rules.users[index].userType == USER_TYPE_BUYER || rules.users[index].userType == USER_TYPE_SELLER)) break;
        require(index < rules.users.length, "Only a buyer or a seller can claim contingencies default.");

        if (data.state == STATE_CONTINGENCIES) {
            (, uint8 buyersVote, uint8 sellersVote) = countVotes(rules, voteData, false);

            // console.log("voteStartTime = %s, data.depositFundedTime + rules.contingencyTime = %s, block.timestamp = %s", voteStartTime, data.depositFundedTime + rules.contingencyTime, block.timestamp);
            // console.log("condition = %s", (voteStartTime == 0 || sellersVote == VOTE_NONE) && data.depositFundedTime + rules.contingencyTime <= block.timestamp);

            if ((!voteData.isVotePending || sellersVote == VOTE_NONE) && data.depositFundedTime + rules.contingencyTime <= block.timestamp) {
                voteData.isVotePending = false;
                ActionLib.declineContingencies(rules, data);
            } else if (voteData.isVotePending && sellersVote == VOTE_YES && buyersVote == VOTE_NONE && voteData.voteStartTime + rules.responseTime <= block.timestamp) {
                voteData.isVotePending = false;
                ActionLib.approveContingencies(rules, data);
            } else {
                require(false, "Cannot claim contingencies default.");
            }
        } else if (data.state == STATE_CONTINGENCIES_DISPUTE) {
            require(data.disputeFundedTime + rules.responseTime <= block.timestamp, "Cannot claim contingecnies default.");

            if (rules.contingencyDisputeFee <= data.buyersDisputeDepositAmount && rules.contingencyDisputeFee > data.sellersDisputeDepositAmount) {
                voteData.isVotePending = false;
                ActionLib.declineContingencies(rules, data);
            } else if (rules.contingencyDisputeFee > data.buyersDisputeDepositAmount && rules.contingencyDisputeFee <= data.sellersDisputeDepositAmount) {
                voteData.isVotePending = false;
                ActionLib.approveContingencies(rules, data);
            } else {
                require(false, "Cannot claim contingencies default.");
            }
        }
    }

    // Funding default can be claimed if the escrow was not funded during FundingTime after the contingencies have been compeleted
    function claimFundingDefault(EscrowRules storage rules, EscrowData storage data, address userAddress) public {
        require(data.state == STATE_FUNDING_PURCHASE
            && 0 < rules.depositAmount
            && data.buyersDepositAmount < data.saleAmount + rules.registrationFee - data.registrationFeePaid
            && data.contingenciesCompletedTime + rules.fundingTime <= block.timestamp, "Cannot claim funding default.");

        uint256 index;
        for (index = 0; index < rules.users.length; index++) 
            if (userAddress == rules.users[index].userAddress
                && (rules.users[index].userType == USER_TYPE_BUYER || rules.users[index].userType == USER_TYPE_SELLER)) break;
        require(index < rules.users.length, "Only a buyer or a seller can claim funding default.");

        ActionLib.processFundingDefault(rules, data);
    }

    // Registration default can be claimed if:
    //     1) Registration time elapsed since purchase funding and no Sellers voted on registration
    //     2) Sellers voted for approving registration, and Buyers didn't vote after ActionTime
    //     3) There was a dispute, and only one party diposed the dispute deposit amount after responseTime
    function claimRegistrationDefault(EscrowRules storage rules, EscrowData storage data, VoteData storage voteData, address userAddress) public {
        require(data.state == STATE_REGISTRATION
            || data.state == STATE_REGISTRATION_DISPUTE, "Can only claim contingencies default in registration state.");

        uint256 index;
        for (index = 0; index < rules.users.length; index++) 
            if (userAddress == rules.users[index].userAddress 
                && (rules.users[index].userType == USER_TYPE_BUYER || rules.users[index].userType == USER_TYPE_BUYERS_LENDER
                    || rules.users[index].userType == USER_TYPE_SELLER || rules.users[index].userType == USER_TYPE_SELLERS_LENDER)) break;
        require(index < rules.users.length, "Only a buyer, a seller, or a lender can claim registration default.");

        if (data.state == STATE_REGISTRATION) {
            (, uint8 buyersVote, uint8 sellersVote) = countVotes(rules, voteData, false);

            // console.log("voteStartTime = %s, data.purchaseFundedTime + rules.contingencyTime = %s, block.timestamp = %s", voteStartTime, data.purchaseFundedTime + rules.contingencyTime, block.timestamp);
            // console.log("condition = %s", (voteStartTime == 0 || sellersVote == VOTE_NONE) && data.purchaseFundedTime + rules.contingencyTime <= block.timestamp);

            if ((!voteData.isVotePending || sellersVote == VOTE_NONE) && data.purchaseFundedTime + rules.registrationTime <= block.timestamp) {
                voteData.isVotePending = false;
                ActionLib.declineRegistration(rules, data);
            } else if (voteData.isVotePending && sellersVote == VOTE_YES && buyersVote == VOTE_NONE && voteData.voteStartTime + rules.responseTime <= block.timestamp) {
                voteData.isVotePending = false;
                ActionLib.approveRegistration(rules, data);
            } else {
                require(false, "Cannot claim registration default.");
            }
        } else if (data.state == STATE_REGISTRATION_DISPUTE) {
            require(data.disputeFundedTime + rules.responseTime <= block.timestamp, "Cannot claim registration default.");

            if (rules.registrationDisputeFee <= data.buyersDisputeDepositAmount && rules.registrationDisputeFee > data.sellersDisputeDepositAmount) {
                voteData.isVotePending = false;
                ActionLib.declineRegistration(rules, data);
            } else if (rules.registrationDisputeFee > data.buyersDisputeDepositAmount && rules.registrationDisputeFee <= data.sellersDisputeDepositAmount) {
                voteData.isVotePending = false;
                ActionLib.approveRegistration(rules, data);
            } else {
                require(false, "Cannot claim registration default.");
            }
        }
    }

    function checkCanAcceptVote(EscrowRules storage rules, EscrowData storage data, address userAddress, bool vote, bool isRegistration) private view {
        bool isFound = false;
        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userAddress == userAddress) {
                // The Arbiter can vote any time for basic escrow
                if (rules.users[i].userType == USER_TYPE_ARBITER
                    && ((isRegistration && rules.registrationDisputeFee == 0) || (!isRegistration && rules.contingencyDisputeFee == 0))) return;
                
                // Anyone can vote after the time has elapsed
                if ((isRegistration && data.purchaseFundedTime + rules.registrationTime <= block.timestamp)
                    || (!isRegistration && data.depositFundedTime + rules.contingencyTime <= block.timestamp)) return;

                // Seller can always vote yes or no
                if (rules.users[i].userType == USER_TYPE_SELLER) return;

                // Buyer can only approve
                if (rules.users[i].userType == USER_TYPE_BUYER && vote) return;

                isFound = true;
            }
        }   
        require(isFound, "The sender must be an escrow user.");

        require(false, "The user type and vote combination is not allowed prior to contingeny time elapsing.");
    }

    // If Buyers voted yes, the decision is approved reagardless of all other votes
    // If Sellers voted no, the decision is declined reagardless of all other votes
    // If Buyers and Sellers voted differently, it's a dispute
    //      - The Arbiter's vote decides the dispute if disputeFee is not zero
    //      - The Arbiter's vote decides the dispute regardless of all other votes if disputeFee is zero
    function processVote(EscrowRules storage rules, VoteData storage voteData, address userAddress, bool vote, bool isRegistration) private returns (uint8) {
        bool isFound = false;
        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userAddress == userAddress) {
                voteData.votes[i] = vote ? VOTE_YES : VOTE_NO;

                if (rules.users[i].userType == USER_TYPE_ARBITER
                    && ((isRegistration && rules.registrationDisputeFee == 0) || (!isRegistration && rules.contingencyDisputeFee == 0)))
                    return vote ? VOTE_RESULT_APPROVED : VOTE_RESULT_DECLINED;

                isFound = true;
            }
        }   
        require(isFound, "The sender must be an escrow user.");

        (uint8 arbitersVote, uint8 buyersVote, uint8 sellersVote) = countVotes(rules, voteData, isRegistration);

        uint8 result = VOTE_RESULT_NONE;

        if (buyersVote == VOTE_YES) result = VOTE_RESULT_APPROVED;
        else if (sellersVote == VOTE_NO) result = VOTE_RESULT_DECLINED;
        else if ((isRegistration && rules.registrationDisputeFee == 0) || (!isRegistration && rules.contingencyDisputeFee == 0)) result = VOTE_RESULT_NONE;
        else {
            if (sellersVote == VOTE_YES && arbitersVote == VOTE_YES) result = VOTE_RESULT_APPROVED;
            else if (buyersVote == VOTE_NO && arbitersVote == VOTE_NO) result = VOTE_RESULT_DECLINED;
            else if (buyersVote == VOTE_NO && sellersVote == VOTE_YES) result = VOTE_RESULT_DISPUTE;
        }

        console.log("vote result = %s", result);

        return result;
    }

    // Buyers' vote is YES if ALL buyers voted yes
    // Buyers' vote is NO, if at least one vote no
    // If not all Buyers voted, the vote is NONE
    //
    // Sellers' vote is how the majority voted (by amount)
    // If the majority is not reached, the vote is NONE
    //
    // Lenders votes are not counted for contingencies, but counted for registration (for buyers and sellers)
    function countVotes(EscrowRules storage rules, VoteData storage voteData, bool isRegistration) private view returns (uint8, uint8, uint8) {
        uint8 arbitersVote = VOTE_NONE;        
        uint8 sellersVote = VOTE_NONE;        
        uint8 buyersVote = VOTE_NONE;        
        uint256 buyersCount = 0;
        uint256 buyersVoteCount = 0;
        uint256 sellersYesAmount = 0;
        uint256 sellersNoAmount = 0;
        uint256 sellersTotalAmount = 0;

        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userType == USER_TYPE_ARBITER) {
                arbitersVote = voteData.votes[i];
            } else if (rules.users[i].userType == USER_TYPE_BUYER || (isRegistration && rules.users[i].userType == USER_TYPE_BUYERS_LENDER)) {
                buyersCount++;
                if (voteData.votes[i] == VOTE_NO) buyersVote = VOTE_NO;
                if (voteData.votes[i] != VOTE_NONE) buyersVoteCount++;
            } else if (rules.users[i].userType == USER_TYPE_SELLER || (isRegistration && rules.users[i].userType == USER_TYPE_SELLERS_LENDER)) {
                sellersTotalAmount += rules.users[i].amount;
                if (voteData.votes[i] == VOTE_YES) sellersYesAmount += rules.users[i].amount;
                else if (voteData.votes[i] == VOTE_NO) sellersNoAmount += rules.users[i].amount;
            }
        }

        if (buyersVote != VOTE_NO && buyersCount == buyersVoteCount) buyersVote = VOTE_YES;

        if (sellersTotalAmount < sellersYesAmount * 2) sellersVote = VOTE_YES;
        else if (sellersTotalAmount < sellersNoAmount * 2) sellersVote = VOTE_NO;

        console.log("buyersCount = %s, buyersVoteCount = %s", buyersCount, buyersVoteCount);
        console.log("buyersCount = %s, buyersVoteCount = %s", buyersCount, buyersVoteCount);
        console.log("sellersTotalAmount = %s, sellersYesAmount = %s, sellersNoAmount = %s", sellersTotalAmount, sellersYesAmount, sellersNoAmount);
        console.log("buyersVote = %s, sellersVote = %s, arbitersVote = %s", buyersVote, sellersVote, arbitersVote);

        return (arbitersVote, buyersVote, sellersVote);
    }
}
