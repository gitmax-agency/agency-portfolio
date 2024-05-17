import React, { Component, useState } from 'react';

import { ethers } from "ethers";
import abi from '../src/abi/external_contracts';

function Metamask (props) {

    const [selectedAddress, setSelectedAddress] = useState();
    const [balance, setBalance] = useState();
    const [escrowAddress, setEscrowAddress] = useState();

    async function connectToMetamask() {
        const provider = new ethers.providers.Web3Provider(window.ethereum);
        const accounts = await provider.send("eth_requestAccounts", []);
        const balance = await provider.getBalance(accounts[0]);
        const balanceInEther = ethers.utils.formatEther(balance);
        setSelectedAddress(accounts[0]);
        setBalance(balanceInEther);
    }

    function renderMetamask() {
        if (!selectedAddress) {
            return (
                <button onClick={() => connectToMetamask()}>Connect to Metamask</button>
            )
        } else {
            getEscrowList();
            return (
                <div>
                    <p>Welcome {selectedAddress}</p>
                    <p>Your ETH Balance is: {balance}</p>
                </div>
            );
        }
    }

    async function getEscrowList() {
        const provider = new ethers.providers.Web3Provider(window.ethereum);
        const signer = provider.getSigner();
    
        const escrowFactoryContract = new ethers.Contract(abi.escrowFactory.address, abi.escrowFactory.abi, provider);
    
        const escrowFactoryContractWithSigner = escrowFactoryContract.connect(signer);
        // const result = await daiContractWithSigner.createEscrow();
        const result = await escrowFactoryContractWithSigner.getEscrow(selectedAddress);
        setEscrowAddress(result);
        return result;
    }

    async function createEscrow() {
        const provider = new ethers.providers.Web3Provider(window.ethereum);
        const signer = provider.getSigner();
    
        const escrowFactoryContract = new ethers.Contract(abi.escrowFactory.address, abi.escrowFactory.abi, provider);
    
        const escrowFactoryContractWithSigner = escrowFactoryContract.connect(signer);
        await escrowFactoryContractWithSigner.createEscrow();
    }

    
    return (
        <div>
            {renderMetamask()}
            {escrowAddress}
            <button onClick={createEscrow}>Create Escrow</button>
        </div>
    )
}

export default Metamask;