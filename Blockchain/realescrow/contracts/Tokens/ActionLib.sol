// SPDX-License-Identifier: BUSL-1.1
pragma solidity ^0.8.9;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

import "./EscrowLib.sol";

library ActionLib {
    function approveContingencies(EscrowRules storage rules, EscrowData storage data) public {
        console.log("approveContingencies");

        console.log("buyersDisputeDeposit = %s, contingencyDisputeFee = %s", data.buyersDisputeDepositAmount, rules.contingencyDisputeFee);

        uint256 arbiterIndex;
        uint256 remainingDisputeFee = rules.contingencyDisputeFee;

        for (uint256 i = 0; i < rules.users.length; i++) {
            if (0 < data.buyersDisputeDepositAmount) {
                // Confiscate buyers' dispute deposits
                if (EscrowLib.isBuyerOrLender(rules, i) && 0 < data.userDisputeDeposits[i] && 0 < remainingDisputeFee) {
                    uint256 disputeFee = EscrowLib.min(remainingDisputeFee, data.userDisputeDeposits[i]);

                    console.log("i = %s, remainingDeposit = %s, amount = %s", i, remainingDisputeFee, disputeFee);

                    data.userDisputeDeposits[i] -= disputeFee;
                    data.buyersDisputeDepositAmount -= disputeFee;
                    remainingDisputeFee -= disputeFee;
                }
            }

            if (rules.users[i].userType == USER_TYPE_ARBITER) arbiterIndex = i;
        }

        // Send buyers' dispute deposits to arbiter
        if (0 < rules.contingencyDisputeFee - remainingDisputeFee) EscrowLib.transferTokenToUser(rules, data, arbiterIndex, rules.contingencyDisputeFee - remainingDisputeFee, false);

        data.sellersDisputeDepositAmount = 0;
        data.contingenciesCompletedTime = block.timestamp;

        EscrowLib.changeState(rules, data, EscrowEvent.ContingenciesApproved);
    }

    function declineContingencies(EscrowRules storage rules, EscrowData storage data) public {
        console.log("declineContingencies");

        console.log("sellersDisputeDepositAmount = %s, contingencyDisputeFee = %s", data.sellersDisputeDepositAmount, rules.contingencyDisputeFee);

        uint256 remainingDisputeFee = rules.contingencyDisputeFee;
        uint256 totalEscrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
        uint256 remainingEscrowFee = totalEscrowFee;
        uint256 remainingArbiterFee = rules.arbiterFee;

        (uint256 arbiterIndex, uint256 buyersAmount) = _getArbiterAndBuyersAmount(rules);

        for (uint256 i = 0; i < rules.users.length; i++) {
            if (0 < data.sellersDisputeDepositAmount) {
                // Confiscate sellers' dispute deposits
                if (EscrowLib.isSellerOrLender(rules, i) && 0 < data.userDisputeDeposits[i] && 0 < remainingDisputeFee) {
                    uint256 disputeFee = EscrowLib.min(remainingDisputeFee, data.userDisputeDeposits[i]);

                    console.log("i = %s, remainingDisputeFee = %s, disputeFee = %s", i, remainingDisputeFee, disputeFee);

                    data.userDisputeDeposits[i] -= disputeFee;
                    data.sellersDisputeDepositAmount -= disputeFee;
                    remainingDisputeFee -= disputeFee;
                }
            }

            // Deduct the escrow fee from buyers
            if (rules.users[i].userType == USER_TYPE_BUYER && 0 < data.userDeposits[i] && 0 < remainingEscrowFee + remainingArbiterFee) {
                uint256 escrowFee = EscrowLib.computeShare(totalEscrowFee, rules.users[i].amount, data.userDeposits[i], buyersAmount);
                data.userDeposits[i] -= escrowFee;
                remainingEscrowFee -= escrowFee;

                uint256 arbiterFee = EscrowLib.computeShare(rules.arbiterFee, rules.users[i].amount, data.userDeposits[i], buyersAmount);
                data.userDeposits[i] -= arbiterFee;
                remainingArbiterFee -= arbiterFee;

                data.buyersDepositAmount -= escrowFee + arbiterFee;
            }
        }

        // Send the arbiter fee plus sellers' dispute deposits to arbiter (rules ensure that there is always enough for the arbiter fee)
        EscrowLib.transferTokenToUser(rules, data, arbiterIndex, rules.arbiterFee + rules.contingencyDisputeFee - remainingDisputeFee, false);

        // Send remaining funds to escrow fee recipient
        EscrowLib.transferToEscrowRecipient(rules, data);

        data.buyersDepositAmount = 0;
        data.buyersDisputeDepositAmount = 0;
        data.sellersDisputeDepositAmount = 0;

        EscrowLib.changeState(rules, data, EscrowEvent.ContingenciesDeclined);
    }

    // Send the holding deposit amount to sellers
    // Return the rest of the funds
    // Takes the deposit amounts only from the buyers, not from lenders
    function processFundingDefault(EscrowRules storage rules, EscrowData storage data) public {
        console.log("processFundingDefault");

        EscrowLib.EscrowUserData memory escrowUserData = EscrowLib.getEscrowUserData(rules, data);

        uint256 totalEscrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
        uint256 remainingArbiterFee = rules.arbiterFee;
        uint256 remainingEscrowFee = totalEscrowFee;

        // Escrow is in the contingency state if the buyer funds conver the holding deposit without fees
        uint256 depositLessFees = EscrowLib.min(rules.depositAmount, escrowUserData.buyersDeposit - totalEscrowFee - rules.arbiterFee);
        uint256 remainingDeposit = depositLessFees;

        console.log("buyersDeposit = %s, buyersDeposit - fees = %s", escrowUserData.buyersDeposit, escrowUserData.buyersDeposit - totalEscrowFee - rules.arbiterFee);
        console.log("totalEscrowFee = %s, arbiterFee = %s, depositLessFees = %s", totalEscrowFee, rules.arbiterFee, depositLessFees);

        // Deduct the escrow fee, arbiter fee, and holding deposit share from buyers
        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userType == USER_TYPE_BUYER && 0 < data.userDeposits[i] && 0 < remainingEscrowFee + remainingArbiterFee + remainingDeposit) {
                uint256 escrowFee = EscrowLib.computeShare(totalEscrowFee, rules.users[i].amount, data.userDeposits[i], escrowUserData.buyersAmount);
                data.userDeposits[i] -= escrowFee;
                remainingEscrowFee -= escrowFee;

                uint256 arbiterFee = EscrowLib.computeShare(rules.arbiterFee, rules.users[i].amount, data.userDeposits[i], escrowUserData.buyersAmount);
                data.userDeposits[i] -= arbiterFee;
                remainingArbiterFee -= arbiterFee;

                uint256 depositAmount = EscrowLib.computeShare(depositLessFees, rules.users[i].amount, data.userDeposits[i], escrowUserData.buyersAmount);
                data.userDeposits[i] -= depositAmount;
                remainingDeposit -= depositAmount;

                data.buyersDepositAmount -= escrowFee + arbiterFee + depositAmount;

                console.log("First i = %s, user.amount = %s", i, rules.users[i].amount);
                console.log("escrowFee = %s, arbiterFee = %s, depositAmount = %s", escrowFee, arbiterFee, depositAmount);
            }
        }

        console.log("remainingEscrowFee = %s, remainingArbiterFee = %s, remainingDeposit = %s", remainingEscrowFee, remainingArbiterFee, remainingDeposit);

        uint256 remainingDepositToSend = depositLessFees - remainingDeposit;

        for (uint256 i = 0; i < rules.users.length; i++) {
            // Send the remaining amounts to buyers, buyers' lenders, and sellers lenders
            if ((EscrowLib.isBuyerOrLender(rules, i) || rules.users[i].userType == USER_TYPE_SELLERS_LENDER) && 0 < data.userDeposits[i] + data.userDisputeDeposits[i]) {
                console.log("Sending to buyer i = %s, amount = %s", i, data.userDeposits[i] + data.userDisputeDeposits[i]);

                EscrowLib.transferTokenToUser(rules, data, i, data.userDeposits[i] + data.userDisputeDeposits[i], false);
            }

            // Send the share of the holding deposit to sellers
            if (rules.users[i].userType == USER_TYPE_SELLER) {
                uint256 depositToSend = i == escrowUserData.lastSellerIndex ? 
                    remainingDepositToSend : EscrowLib.computeShare(remainingDepositToSend, rules.users[i].amount, MAX_UNIT256, escrowUserData.sellersAmount);
                remainingDepositToSend -= depositToSend;

                console.log("Sending to seller i = %s, depositToSend = %s", i, depositToSend);

                EscrowLib.transferTokenToUser(rules, data, i, depositToSend + data.userDeposits[i] + data.userDisputeDeposits[i], false);
            }

            data.userDeposits[i] = 0;
            data.userDisputeDeposits[i] = 0;
        }

        data.buyersDisputeDepositAmount = 0;
        data.sellersDisputeDepositAmount = 0;

        // Send the arbiter fee to arbiter (rules ensure that there is always enough for the arbiter fee)
        EscrowLib.transferTokenToUser(rules, data, escrowUserData.arbiterIndex, rules.arbiterFee, false);

        console.log("Remaining funds = %s", rules.paymentToken.balanceOf(address(this)));

        // Send all remaining funds to the escrow fee recipient
        EscrowLib.transferToEscrowRecipient(rules, data);

        EscrowLib.changeState(rules, data, EscrowEvent.FundingDefaulted);
    }

    // If buyer deposits exceed the sale amount, they are taken on the first come first serve basis regardless of amount
    // because it's not possible to compute proper shares due to amount underflow
    function approveRegistration(EscrowRules storage rules, EscrowData storage data) public {
        console.log("approveRegistration");

        console.log("buyersDisputeDeposit = %s, registrationDisputeFee = %s", data.buyersDisputeDepositAmount, rules.registrationDisputeFee);

        uint256 remainingDisputeFee = rules.registrationDisputeFee;
        uint256 totalEscrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
        uint256 remainingEscrowFee = totalEscrowFee;
        uint256 remainingArbiterFee = rules.arbiterFee;
        uint256 remainingSaleAmount = data.saleAmount;

        (uint256 arbiterIndex, uint256 buyersAmount) = _getArbiterAndBuyersAmount(rules);

        console.log("saleAmount = %s, totalEscrowFee = %s, buyersAmount = %s", data.saleAmount, totalEscrowFee, buyersAmount);

        for (uint256 i = 0; i < rules.users.length; i++) {
            // Confiscate buyers' and buyers' lenders' dispute deposits
            if (EscrowLib.isBuyerOrLender(rules, i)) {
                if (0 < data.userDisputeDeposits[i] && 0 < remainingDisputeFee) {
                    uint256 disputeFee = EscrowLib.min(remainingDisputeFee, data.userDisputeDeposits[i]);
                    data.userDisputeDeposits[i] -= disputeFee;
                    data.buyersDisputeDepositAmount -= disputeFee;
                    remainingDisputeFee -= disputeFee;
                }

                // Deduct a share of the escrow and arbiter fee from buyers only
                if (rules.users[i].userType == USER_TYPE_BUYER && 0 < data.userDeposits[i] && 0 < remainingEscrowFee + remainingArbiterFee) {
                    uint256 escrowFee = EscrowLib.computeShare(totalEscrowFee, rules.users[i].amount, data.userDeposits[i], buyersAmount);
                    data.userDeposits[i] -= escrowFee;
                    remainingEscrowFee -= escrowFee;

                    uint256 arbiterFee = EscrowLib.computeShare(rules.arbiterFee, rules.users[i].amount, data.userDeposits[i], buyersAmount);
                    data.userDeposits[i] -= arbiterFee;
                    remainingArbiterFee -= arbiterFee;

                    console.log("Escrow fee share, i = %s, escrowFee = %s, userDeposit = %s", i, escrowFee, data.userDeposits[i]);

                    data.buyersDepositAmount -= escrowFee + arbiterFee;
                }

                // First, deduct no more than the amount supposed to pay from all buyers and lenders
                if (0 < data.userDeposits[i] && 0 < remainingSaleAmount) {
                    uint256 saleAmount = EscrowLib.min(EscrowLib.min(remainingSaleAmount, data.userDeposits[i]), rules.users[i].amount);

                    data.userDeposits[i] -= saleAmount;
                    data.buyersDepositAmount -= saleAmount;
                    remainingSaleAmount -= saleAmount;

                    console.log("First round, i = %s, saleAmount = %s, remainingSaleAmount = %s", i, saleAmount, remainingSaleAmount);
                }
            }

            // Send the sale amount plus deposited dispute amounts to sellers, sellers' lenders, and brokers
            // Sellers/Lenders are not allowed to make deposits
            if (EscrowLib.isSellerLenderOrBroker(rules, i) && 0 < rules.users[i].amount + data.userDisputeDeposits[i]) {
                EscrowLib.transferTokenToUser(rules, data, i, rules.users[i].amount + data.userDisputeDeposits[i], false);
                data.userDisputeDeposits[i] = 0;
            }
        }

        data.sellersDisputeDepositAmount = 0;

        console.log("remainingSaleAmount = %s, remainingEscrowFee = %s, remainingArbiterFee = %s", remainingSaleAmount, remainingEscrowFee, remainingArbiterFee);

        data.buyersDisputeDepositAmount = 0;

        console.log("remainingSaleAmount = %s", remainingSaleAmount);

        console.log("registrationDisputeFee = %s, remainingDeposit = %s", rules.registrationDisputeFee, remainingDisputeFee);

        // Send the aribiter fee plus buyers' dispute deposits to arbiter (rules ensure that there is always enough for the arbiter fee)
        EscrowLib.transferTokenToUser(rules, data, arbiterIndex, rules.arbiterFee + rules.registrationDisputeFee - remainingDisputeFee, false);

        console.log("Remaining funds = %s", rules.paymentToken.balanceOf(address(this)));

        // Send all remaining funds to the escrow fee recipient
        EscrowLib.transferToEscrowRecipient(rules, data);

        EscrowLib.changeState(rules, data, EscrowEvent.RegistrationApproved);
    }

    function declineRegistration(EscrowRules storage rules, EscrowData storage data) public {
        console.log("declineRegistration");

        console.log("sellersDisputeDepositAmount = %s, registrationDisputeFee = %s", data.sellersDisputeDepositAmount, rules.registrationDisputeFee);

        uint256 remainingDisputeFee = rules.registrationDisputeFee;
        uint256 totalEscrowFee = data.saleAmount * data.escrowFee / TEN_P_18;
        uint256 remainingEscrowFee = totalEscrowFee;
        uint256 remainingArbiterFee = rules.arbiterFee;

        (uint256 arbiterIndex, uint256 buyersAmount) = _getArbiterAndBuyersAmount(rules);

        console.log("saleAmount = %s, totalEscrowFee = %s, buyersAmount = %s", data.saleAmount, totalEscrowFee, buyersAmount);

        for (uint256 i = 0; i < rules.users.length; i++) {
            // Confiscate sellers' and sellers' lenders' dispute deposits
            if (EscrowLib.isSellerOrLender(rules, i) && 0 < data.userDisputeDeposits[i] && 0 < remainingDisputeFee) {
                uint256 disputeFee = EscrowLib.min(remainingDisputeFee, data.userDisputeDeposits[i]);
                data.userDisputeDeposits[i] -= disputeFee;
                data.sellersDisputeDepositAmount -= disputeFee;
                remainingDisputeFee -= disputeFee;
            }

            // Deduct a share of the escrow and arbiter fee from buyers only
            if (rules.users[i].userType == USER_TYPE_BUYER && 0 < data.userDeposits[i] && 0 < remainingEscrowFee + remainingArbiterFee) {
                uint256 escrowFee = EscrowLib.computeShare(totalEscrowFee, rules.users[i].amount, data.userDeposits[i], buyersAmount);
                data.userDeposits[i] -= escrowFee;
                remainingEscrowFee -= escrowFee;

                uint256 arbiterFee = EscrowLib.computeShare(rules.arbiterFee, rules.users[i].amount, data.userDeposits[i], buyersAmount);
                data.userDeposits[i] -= arbiterFee;
                remainingArbiterFee -= arbiterFee;

                console.log("Escrow fee share, i = %s, escrowFee = %s, userDeposit = %s", i, escrowFee, data.userDeposits[i]);

                data.buyersDepositAmount -= escrowFee + arbiterFee;
            }
        }

        console.log("remainingEscrowFee = %s, remainingArbiterFee = %s", remainingEscrowFee, remainingArbiterFee);

        data.buyersDisputeDepositAmount = 0;
        data.sellersDisputeDepositAmount = 0;

        console.log("registrationDisputeFee = %s, remainingDisputeFee = %s", rules.registrationDisputeFee, remainingDisputeFee);

        // Send the arbiter fee plus selllers' dispute deposits to arbiter (rules ensure that there is always enough for the arbiter fee)
        EscrowLib.transferTokenToUser(rules, data, arbiterIndex, rules.arbiterFee + rules.registrationDisputeFee - remainingDisputeFee, false);

        console.log("Remaining funds = %s", rules.paymentToken.balanceOf(address(this)));

        // Send all remaining funds to the escrow fee recipient
        EscrowLib.transferToEscrowRecipient(rules, data);

        EscrowLib.changeState(rules, data, EscrowEvent.RegistrationDeclined);
    }

    function _getArbiterAndBuyersAmount(EscrowRules storage rules) private view returns (uint256, uint256) {
        uint256 arbiterIndex;
        uint256 buyersAmount = 0;

        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userType == USER_TYPE_ARBITER) {
                arbiterIndex = i;
            } else if (rules.users[i].userType == USER_TYPE_BUYER) {
                buyersAmount += rules.users[i].amount;
            }
        }

        return (arbiterIndex, buyersAmount);
    }
}
