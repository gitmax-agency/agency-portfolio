import { ethers } from 'ethers';
import {useState} from "react";

export default function MetaMask(connectMetamask: () => void   ) {
    const address = "";
    if(address){
        return (
            <div className="available-container">
                        <p>{address}</p>
            </div>
        )
    }
    return(
        <div className="button">
                <button onClick={() => connectMetamask()} className="default-button">
                            CONNECT WALLET
                </button>
         </div>
    )
}