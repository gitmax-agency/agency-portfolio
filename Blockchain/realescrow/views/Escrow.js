import { ethers } from "ethers";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import abi from "../src/abi/external_contracts";
import ProposeRules from "../components/ProposeRules";

export default function Escrow() {
    const params = useParams();
    const address = params.address;
    const [signer, setSigner] = useState();
    const [contract, setContract] = useState();
    const [owner, setOwner] = useState();
    const [voteData, setVoteData] = useState();

    useEffect(() => {
        const provider = new ethers.providers.Web3Provider(window.ethereum);
        const signer = provider.getSigner();
        setSigner(signer);
        const escrowContract = new ethers.Contract(address, abi.escrow.abi, provider);
        const escrowContractWithSigner = escrowContract.connect(signer);
        setContract(escrowContractWithSigner);
    }, []);

    useEffect(() => {
        if (contract) {
            getData();
        }
    }, [contract])

    // async function getFee() {
    //     const provider = new ethers.providers.Web3Provider(window.ethereum);
    //     const signer = provider.getSigner();

    //     const escrowFactoryContract = new ethers.Contract(abi.escrowFactory.address, abi.escrowFactory.abi, provider);

    //     const escrowFactoryContractWithSigner = escrowFactoryContract.connect(signer);
    //     const fee = await escrowFactoryContractWithSigner.escrowFee();
    //     console.log(fee);
    // }

    async function getData() {
        const owner = await contract.creator();
        setOwner(owner);
        const voteData = await contract.voteData();
        console.log(voteData);
        setVoteData(voteData);
    }

    return (
        <div>
            <div>
                Address: {address}
            </div>
            <div>
                Admin: {owner}
            </div>

            {voteData && (
                <div>
                    Vote: {voteData.rulesVotePending ? "Active" : "Inactive"}
                </div>
            )}
            
            {voteData && voteData.rulesVotePending && (
                <div>
                    Start Time: {ethers.utils.formatEther(voteData.voteStartTime)}
                </div>
            )}
        
            {signer && signer._address === owner && (<ProposeRules
                    contract={contract}
                    address={address}
                />)
            }
        </div>
    );
}
