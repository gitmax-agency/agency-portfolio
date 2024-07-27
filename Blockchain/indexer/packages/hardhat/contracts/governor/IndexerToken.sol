//SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.2;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
//import "@openzeppelin/contracts/token/ERC20/extensions/draft-ERC20Permit.sol";
import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Votes.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/* TODO: also maybe: ERC20Permit, */
contract IndexerToken is ERC20, ERC20Votes, Ownable {
    // staked values: 0 - never staked, 1 - unstaked, other value - 1 == stake amount.
    mapping(address => uint256) public staked;
    address[] public stakers;
    uint256 public totalStaked;
    // erc20 token -> address -> dividend
    mapping(address => mapping (address => uint256)) public dividend; 

    constructor(uint256 total_supply) ERC20("Indexer.fi", "IDX") ERC20Permit("MyToken") {
        _mint(msg.sender, total_supply);
    }

    function stake(uint256 amount) external {
        // Burn fails if there is not enough tokens.
        _burn(msg.sender, amount);
        if (staked[msg.sender] == 0) {
            stakers.push(msg.sender);
        }
        staked[msg.sender] += amount + 1;
        totalStaked += amount;
    }

    // TODO: implement delay or schedule for unstaking
    function unstake(uint256 amount) external {
        require(amount < staked[msg.sender], "IT: Can only unstake what is staked.");
        unchecked {
            staked[msg.sender] -= amount;
            totalStaked -= amount;
        }
        _mint(msg.sender, amount);
    }

    function postReward(address erc20token, uint256 amount) external {
        require(amount > 0);
    }

    function _afterTokenTransfer(address from, address to, uint256 amount)
        internal
        override(ERC20, ERC20Votes)
    {
        super._afterTokenTransfer(from, to, amount);
    }

    function _mint(address to, uint256 amount)
        internal
        override(ERC20, ERC20Votes)
    {
        super._mint(to, amount);
    }

    function _burn(address account, uint256 amount)
        internal
        override(ERC20, ERC20Votes)
    {
        super._burn(account, amount);
    }
}
