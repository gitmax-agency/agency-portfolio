// SPDX-License-Identifier: BUSL-1.1
pragma solidity ^0.8.9;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

import "./EscrowLib.sol";
import "./RulesLib.sol";
import "./VoteLib.sol";
import "./EscrowFactory.sol";

contract Escrow {
    EscrowRules public rules;
    EscrowRules public proposedRules;

    EscrowData public data;
    VoteData public voteData;
    VoteData public rulesVoteData;
    address public creator;
    address public admin;
    address public newAdmin;
    address[] public newUserAddresses;

    EscrowFactory private escrowFactory;

    constructor(address newCreator, address newAdminAddress, address newEscrowFeeRecipient, uint256 newEscrowFee, EscrowFactory newEscrowFactory) {
        console.log("Escrow creator = %s", newCreator);

        data.state = STATE_NEW;
        data.escrowFee = newEscrowFee;
        data.escrowFeeRecipient = newEscrowFeeRecipient;
        admin = newAdminAddress;
        creator = newCreator;
        escrowFactory = newEscrowFactory;
    }

    function proposeNewRules(EscrowRules calldata newEscrowRules) external {
        RulesLib.proposeNewRules(rules, data, rulesVoteData, newEscrowRules, creator, msg.sender);
        proposedRules = newEscrowRules;

        for (uint256 i = 0; i < proposedRules.users.length; i++) {
            if (proposedRules.users[i].userAddress == msg.sender) {
                _voteOnRules(true);
                break;
            }
        }
    }

    function voteOnRules(bool vote) external {
        _voteOnRules(vote);
    }

    function deposit(uint256 amount) external {
        EscrowLib.deposit(rules, data, msg.sender, amount);
    }

    function withdraw(uint256 amount) external {
        EscrowLib.withdraw(rules, data, msg.sender, amount);
    }

    function depositDisputeFee(uint256 amount) external {
        EscrowLib.depositDisputeFee(rules, data, msg.sender, amount);
    }

    function voteOnContingencies(bool vote) external {
        VoteLib.voteOnContingencies(rules, data, voteData, msg.sender, vote);
    }

    function claimContingenciesDefault() external {
        VoteLib.claimContingenciesDefault(rules, data, voteData, msg.sender);
    }

    function claimFundingDefault() external {
        VoteLib.claimFundingDefault(rules, data, msg.sender);
    }

    function voteOnRegistration(bool vote) external {
        VoteLib.voteOnRegistration(rules, data, voteData, msg.sender, vote);
    }

    function claimRegistrationDefault() external {
        VoteLib.claimRegistrationDefault(rules, data, voteData, msg.sender);
    }

    function deleteEscrow() external {
        require(data.state == STATE_NEW || data.state == STATE_COMPLETED || data.state == STATE_FAILED, "The escrow must be in new, completed, or failed state.");
        escrowFactory.deleteEscrow();
        selfdestruct(payable(address(this)));
    }

    function changeAddress(address newAddress) external {
        bool isFound = false;
        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userAddress == msg.sender) {
                RulesLib.checkForUserConflict(rules.users, newAddress, rules.users[i].userType, i, true);
                newUserAddresses[i] = newAddress;
                isFound = true;
            }
        }
        require(isFound, "The sender must be a user.");
    }

    function confirmAddressChange() external {
        bool isFound = false;
        for (uint256 i = 0; i < rules.users.length; i++) {
            if (newUserAddresses[i] == msg.sender) {
                rules.users[i].userAddress = msg.sender;
                isFound = true;
            }
        }
        require(isFound, "The sender must be one of the changed addresses.");
    }

    function changeArbiter(address newAddress) external {
        require(msg.sender == admin, "Can only be called by admin.");

        for (uint256 i = 0; i < rules.users.length; i++) {
            if (rules.users[i].userType == USER_TYPE_ARBITER) {
                rules.users[i].userAddress = newAddress;
                break;
            }
        }
    }

    function changeAdmin(address newAddress) external {
        require(msg.sender == admin, "Can only be called by admin.");
        newAdmin = newAddress;
    }

    function confirmAdminChange() external {
        require(msg.sender == newAdmin, "Can only be called by new admin.");
        admin = newAdmin;
    }

    function changeEscrowFeeRecipient(address newEscrowFeeRecipient) public {
        require(msg.sender == admin, "Can only be called by admin.");
        data.escrowFeeRecipient = newEscrowFeeRecipient;
    }

    function getUsers() external view returns (EscrowUser[] memory) {
        return rules.users;
    }

    function getProposedUsers() external view returns (EscrowUser[] memory) {
        return proposedRules.users;
    }

    function getUserDeposits() external view returns (uint256[] memory) {
        return data.userDeposits;
    }

    function getUserDisputeDeposits() external view returns (uint256[] memory) {
        return data.userDisputeDeposits;
    }

    function _voteOnRules(bool vote) private {
        if (RulesLib.voteOnRules(rulesVoteData, proposedRules, msg.sender, vote) != VOTE_RESULT_APPROVED) return;

        rules = proposedRules;
        RulesLib.implementNewRules(rules, data);
        if (voteData.votes.length != rules.users.length) voteData.votes = new uint8[](rules.users.length);
        if (newUserAddresses.length != rules.users.length) newUserAddresses = new address[](rules.users.length);
        EscrowLib.changeStateNewRules(rules, data, voteData);
    }
}
