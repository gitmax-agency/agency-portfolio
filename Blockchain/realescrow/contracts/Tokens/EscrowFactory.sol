// SPDX-License-Identifier: BUSL-1.1
pragma solidity ^0.8.9;

import "./Escrow.sol";

uint256 constant DEFAULT_ESCROW_FEE = 3000000000000000; // 0.3%
address constant ZERO_ADDRESS = 0x0000000000000000000000000000000000000000;

contract EscrowFactory {
    address public admin;
    address public newAdmin;
    address public escrowFeeRecipient;

    uint256 public escrowFee = DEFAULT_ESCROW_FEE;
    uint256 public escrowCount = 0;

    mapping (address => Escrow) private usersEscrows;
    mapping (address => address) private escrowsUsers;

    constructor() {
        admin = msg.sender;
        escrowFeeRecipient = msg.sender;
    }

    function createEscrow() external returns (Escrow) {
        Escrow escrow = new Escrow(msg.sender, admin, escrowFeeRecipient, escrowFee, this);
        usersEscrows[msg.sender] = escrow;
        escrowsUsers[address(escrow)] = msg.sender;
        escrowCount++;
        return escrow;
    }

    function getEscrow() external view returns (Escrow) {
        Escrow escrow = usersEscrows[msg.sender];
        require(address(escrow) != ZERO_ADDRESS, "No escrow found for this sender.");
        return usersEscrows[msg.sender];
    }

    function deleteEscrow() external {
        address user = escrowsUsers[msg.sender];
        require(address(user) != ZERO_ADDRESS, "Escrow not found.");
        if (usersEscrows[user] == Escrow(msg.sender)) delete usersEscrows[user];
        delete escrowsUsers[msg.sender];
    }

    function setEscrowFee(uint256 newEscrowFee) external {
        require(msg.sender == admin, "Can only be called by admin.");
        escrowFee = newEscrowFee;
    }

    function setEscrowFeeRecipient(address newEscrowFeeRecipient) external {
        require(msg.sender == admin, "Can only be called by admin.");
        escrowFeeRecipient = newEscrowFeeRecipient;
    }

    function changeAdmin(address newAddress) external {
        require(msg.sender == admin, "Can only be called by admin.");
        newAdmin = newAddress;
    }

    function confirmAdminChange() external {
        require(msg.sender == newAdmin, "Can only be called by new admin.");
        admin = newAdmin;
    }
}
