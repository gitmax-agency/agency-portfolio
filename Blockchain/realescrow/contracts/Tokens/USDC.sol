// SPDX-License-Identifier: BUSL-1.1
pragma solidity ^0.8.9;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract USDC is ERC20 {
    constructor(string memory name, string memory ticker) ERC20(name, ticker) {
        _mint(msg.sender, 1000000 * 1000000000000000000);
    }
}