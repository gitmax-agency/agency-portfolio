// SPDX-License-Identifier: BUSL-1.1
pragma solidity ^0.8.9;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

import "./EscrowLib.sol";

library RulesLib {
    function proposeNewRules(EscrowRules storage rules, EscrowData storage data, VoteData storage voteData, EscrowRules calldata newEscrowRules, address creator, address userAddress) public {
        if (userAddress != creator) {
            if (data.state == STATE_NEW) {
                require(false, "Only creator can initialize a new escrow.");
            } else {
                uint256 index;
                for (index = 0; index < rules.users.length; index++) 
                    if (rules.users[index].userAddress == userAddress) break;
                require(index < rules.users.length, "The sender must be a creator or an escrow user."); 
            }
        }
        _validateNewRules(rules, data, newEscrowRules);

        voteData.isVotePending = true;
        EscrowLib.resetVotes(voteData);
        voteData.voteStartTime = block.timestamp;
        if (voteData.votes.length != newEscrowRules.users.length) voteData.votes = new uint8[](newEscrowRules.users.length);

        emit EscrowLib.RulesProposed();
    }

    function voteOnRules(VoteData storage voteData, EscrowRules storage proposedRules, address userAddress, bool vote) public returns (uint8) {
        require(voteData.isVotePending, "There must be a new rules proposal pending.");

        uint8 votingResult = _processRulesVote(proposedRules.users, voteData, userAddress, vote);

        if (votingResult == VOTE_RESULT_DECLINED) {
            voteData.isVotePending = false;

            console.log("New rules declined");

            emit EscrowLib.RulesDeclined();
            return VOTE_RESULT_DECLINED;
        }

        if (votingResult == VOTE_RESULT_NONE) return VOTE_RESULT_NONE;

        voteData.isVotePending = false;

        console.log("New rules adopted!");

        emit EscrowLib.RulesApproved();
        return VOTE_RESULT_APPROVED;
    }
 
    function implementNewRules(EscrowRules storage rules, EscrowData storage data) public {
        if (data.state == STATE_NEW) {
            data.userDeposits = new uint256[](rules.users.length);
            data.userDisputeDeposits = new uint256[](rules.users.length);
            rules.paymentToken.approve(address(this), MAX_UNIT256);
        }

        data.saleAmount = _computeSaleAmount(rules);
    }

    function checkForUserConflict(EscrowUser[] calldata users, address userAddress, uint8 userType, uint256 userIndex, bool checkAll) public pure {
        if (userType == USER_TYPE_BUYER 
            || userType == USER_TYPE_BUYERS_LENDER
            || userType == USER_TYPE_SELLER
            || userType == USER_TYPE_SELLERS_LENDER) {
            uint256 quantity = checkAll ? users.length : userIndex;

            for (uint256 j = 0; j < quantity; j++) {
                require(userAddress != users[j].userAddress
                    || (((userType == USER_TYPE_BUYER || userType == USER_TYPE_BUYERS_LENDER)
                            && users[j].userType != USER_TYPE_SELLER && users[j].userType != USER_TYPE_SELLERS_LENDER)
                        || ((userType == USER_TYPE_SELLER || userType == USER_TYPE_SELLERS_LENDER)
                            && users[j].userType != USER_TYPE_BUYER && users[j].userType != USER_TYPE_BUYERS_LENDER)), 
                    "Buyer or buyers lender cannot also be a seller or sellers lender.");
            }
        }
    }

    function _validateNewRules(EscrowRules storage rules, EscrowData storage data, EscrowRules calldata newEscrowRules) private view {
        if (data.state != STATE_NEW) require(rules.users.length == newEscrowRules.users.length, "Cannot change the number of users after the initialization.");

        uint256 sumIn = 0;
        uint256 sumOut = 0;
        uint256 sumBuyers = 0;
        uint256 arbiterCount = 0;
        uint256 registratorCount = 0;
        uint256 buyerCount = 0;
        uint256 sellerCount = 0;

        for (uint256 i = 0; i < newEscrowRules.users.length; i++) {
            if (data.state == STATE_NEW) {
                if (newEscrowRules.users[i].userType == USER_TYPE_BUYER) buyerCount++;
                else if (newEscrowRules.users[i].userType == USER_TYPE_SELLER) sellerCount++;
                else if (newEscrowRules.users[i].userType == USER_TYPE_ARBITER) arbiterCount++;
                else if (newEscrowRules.users[i].userType == USER_TYPE_REGISTRATOR) registratorCount++;
                checkForUserConflict(newEscrowRules.users, newEscrowRules.users[i].userAddress, newEscrowRules.users[i].userType, i, false);
            } else {
                require(rules.users[i].userType == newEscrowRules.users[i].userType, "Cannot change user types after the initialization.");
                if (rules.users[i].userType != USER_TYPE_ARBITER && rules.users[i].userType != USER_TYPE_REGISTRATOR)
                    require(rules.users[i].userAddress == newEscrowRules.users[i].userAddress, "Cannot change user addresses after the initialization.");
            }

            if (newEscrowRules.users[i].userType == USER_TYPE_BUYER || newEscrowRules.users[i].userType == USER_TYPE_BUYERS_LENDER) {
                sumIn += newEscrowRules.users[i].amount;
                if (newEscrowRules.users[i].userType == USER_TYPE_BUYER) sumBuyers += newEscrowRules.users[i].amount;
            } else {
                sumOut += newEscrowRules.users[i].amount;
            }
        }

        if (data.state == STATE_NEW)
            require(0 < buyerCount && 0 < sellerCount && registratorCount < 2 && arbiterCount == 1, 
                "Users must contain at least one Buyer, at least one Seller, one Arbiter, and not more than one Registrator.");
        else
            require(data.registrationFeePaid == 0 || rules.registrationFee <= newEscrowRules.registrationFee, 
                "Cannot decrease the regitration fee after it was paid.");
        
        uint256 escrowFee = sumOut * data.escrowFee / TEN_P_18;

        console.log("registrationFee = %s, arbiterFee = %s, escrowFee = %s", newEscrowRules.registrationFee, newEscrowRules.arbiterFee, escrowFee);
        console.log("sumOut = %s, sumIn = %s, sumOut + fees = %s", sumOut, sumIn, sumOut + newEscrowRules.registrationFee + newEscrowRules.arbiterFee + escrowFee);

        require(sumIn == sumOut + newEscrowRules.registrationFee + newEscrowRules.arbiterFee + escrowFee, 
            "The sum of incoming amounts has to be equal to the sum of outgoing amounts plus fees.");

        require(newEscrowRules.depositAmount < sumOut, 
            "The holding deposit amount must be below the sum of outgoing amounts.");

        require(newEscrowRules.registrationFee + newEscrowRules.arbiterFee + escrowFee <= sumBuyers, 
            "Total buyer amounts must not be below the registration + arbiter + escrow fees.");

        require(newEscrowRules.depositAmount == 0 || newEscrowRules.arbiterFee + escrowFee <= newEscrowRules.depositAmount, 
            "The holding deposit amount must not be below arbiter + escrow fee.");
    }

    // All escrow users must agree to approve. If it least one disagrees, the rules are declined. There are no disputes.
    function _processRulesVote(EscrowUser[] storage users, VoteData storage voteData, address userAddress, bool vote) private returns (uint8) {
        uint256 userCount = 0;
        uint256 voteCount = 0;
        bool isFound = false;
        uint8 result = VOTE_RESULT_NONE;

        for (uint256 i = 0; i < users.length; i++) {
            if (users[i].userAddress == userAddress) {
                voteData.votes[i] = vote ? VOTE_YES : VOTE_NO;
                isFound = true;
            }

            if (voteData.votes[i] == VOTE_NO) result = VOTE_RESULT_DECLINED;

            userCount++;
            if (voteData.votes[i] != VOTE_NONE) voteCount++;
        }

        require(isFound, "The user must be one of the voting users.");

        if (result != VOTE_RESULT_DECLINED && voteCount == userCount) result = VOTE_RESULT_APPROVED;
        
        return result;
    }

    function _computeSaleAmount(EscrowRules storage rules) private view returns (uint256) {
        uint256 saleAmount = 0;
        for (uint256 i = 0; i < rules.users.length; i++) if (EscrowLib.isSellerLenderOrBroker(rules, i)) saleAmount += rules.users[i].amount;
        return saleAmount;
    }
}
