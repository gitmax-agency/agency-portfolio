//SPDX-License-Identifier: Unlicense
pragma solidity ^0.8.0;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract WrappedEther is ERC20 {
    constructor() ERC20("Wrapped Ether", "WETH") {
        console.log("Miniting 1,000,000 wrapped ether, storing at: '%s'", msg.sender);
        _mint(msg.sender, 1000000 * 10 ** decimals());
    }
}
