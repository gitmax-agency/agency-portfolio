import { ethers } from "ethers";
import React, { useState } from "react";
import { Escrow, USDC } from "../../typechain-types";
import InputRow from "./InputRow";

export default function DepositInput({name, contract, tokenContract}: {name: string, contract: Escrow, tokenContract: USDC}) {
    const [amount, setAmount] = useState<string>();

    async function approve(amount: string) {
        tokenContract.approve(contract.address, ethers.utils.parseEther(amount));
    }

    async function deposit(amount: string) {
        if (!amount) {
            return;
        }

        contract.deposit(ethers.utils.parseEther(amount));
    }

    return (
        <div style={{display: "flex"}}>
            <InputRow name={name} setValue={setAmount}/>
            <button onClick={() => approve(amount!)}>Approve</button>
            <button onClick={() => deposit(amount!)}>Deposit</button>
        </div>
    )
}