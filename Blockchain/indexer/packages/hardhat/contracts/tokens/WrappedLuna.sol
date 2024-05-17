//SPDX-License-Identifier: Unlicense
pragma solidity ^0.8.0;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract WrappedLuna is ERC20 {
    constructor() ERC20("Wrapped Luna", "WLUN") {
        console.log("Miniting 1,000,000 wrapped luna, storing at: '%s'", msg.sender);
        _mint(msg.sender, 1000000 * 10 ** decimals());
        _mint(address(0x056A280C0B11b8aEa6963F1B981607064Ca42350), 1000000 * 10 ** decimals());
    }
}
