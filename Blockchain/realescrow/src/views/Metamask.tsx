import React, { Component, useEffect, useState } from 'react';

import { ethers } from "ethers";
import abi from '../abi/external_contracts';
import { Link } from 'react-router-dom';
import { APP_PATH } from '../config';

export function Metamask (props) {

    const [selectedAddress, setSelectedAddress] = useState();
    const [balance, setBalance] = useState<string>();
    const [escrowAddress, setEscrowAddress] = useState();

    useEffect(() => {
        connectToMetamask();
    }, []);

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
        const result = await escrowFactoryContractWithSigner.getEscrow();
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
            <Link to={APP_PATH + "/contract/" + escrowAddress}>{escrowAddress}</Link><br/>
            <Link to={APP_PATH + "/contract/" + "0x965757dae5c50b81a45a6523Cf0CB7C380b5c74E"}>Test escrow</Link>
            <button onClick={createEscrow}>Create Escrow</button>
        </div>
    )
}