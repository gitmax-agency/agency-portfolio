import { BigNumber, Contract, ethers } from "ethers";
// import { JsonRpcSigner } from "ethers/providers";
import React, { useEffect, useState } from "react";
import { Params, useParams } from "react-router-dom";
import abi from "../abi/external_contracts";
import ProposeRules from "../components/ProposeRules";
import Rules from "../components/Rules";
import {Escrow as EscrowContract, USDC} from "../../typechain-types";
import Vote from "../components/Vote";
import { EscrowUserStruct, EscrowUserStructOutput } from "../../typechain-types/contracts/Escrow";
import DepositInput from "../components/DepositInput";
import { Constants as constants } from "../constants";

export function Escrow() {

    interface Rules {
        paymentToken: string;
        depositAmount: BigNumber;
        registrationFee: BigNumber;
        contingencyDisputeFee: BigNumber;
        registrationDisputeFee: BigNumber;
        contingencyConditions: string;
        registrationConditions: string;
        contingencyTime: BigNumber;
        fundingTime: BigNumber;
        registrationTime: BigNumber;
        responseTime: BigNumber;
    }

    interface VoteData {
        isVotePending: boolean;
        voteStartTime: BigNumber;
    }

    interface Data {
        state: number;
        saleAmount: BigNumber;
        escrowFee: BigNumber;
        buyersDepositAmount: BigNumber;
        buyersDisputeDepositAmount: BigNumber;
        sellersDisputeDepositAmount: BigNumber;
        depositFundedTime: BigNumber;
        contingenciesCompletedTime: BigNumber;
        purchaseFundedTime: BigNumber;
        disputeFundedTime: BigNumber;
        registrationFeePaid: BigNumber;
        escrowFeeRecipient: string;
    }

    const { address } = useParams<{address: string}>();
    const [signer, setSigner] = useState<ethers.providers.JsonRpcSigner>();
    const [contract, setContract] = useState<EscrowContract>();
    const [tokenContract, setTokenContract] = useState<USDC>();
    const [owner, setOwner] = useState<string>();
    const [data, setData] = useState<Data>();
    const [voteData, setVoteData] = useState<VoteData>();
    const [currentUser, setCurrentUser] = useState<EscrowUserStruct>();
    const [rules, setRules] = useState<Rules>();
    const [proposedRules, setProposedRules] = useState<Rules>();
    const [showRules, setShowRules] = useState<boolean>();
    const [showProposeRules, setShowProposeRules] = useState<boolean>();
    const [showProposedRules, setShowProposedRules] = useState<boolean>();
    const [users, setUsers] = useState<EscrowUserStructOutput[]>();
    const [buyerAddress, setBuyerAddress] = useState<string>();
    const [proposedUsers, setProposedUsers] = useState<EscrowUserStructOutput[]>();

    useEffect(() => {
        const provider = new ethers.providers.Web3Provider(window.ethereum);
        const signer = provider.getSigner();
        setSigner(signer);
        const escrowContract: EscrowContract = new ethers.Contract(address!, abi.escrow.abi, provider) as unknown as EscrowContract;
        const escrowContractWithSigner: EscrowContract = escrowContract.connect(signer);
        setContract(escrowContractWithSigner);
    }, []);

    useEffect(() => {
        if (rules && rules.paymentToken) {
            const provider = new ethers.providers.Web3Provider(window.ethereum);
            const tokenContract: USDC = new ethers.Contract(rules.paymentToken, abi.erc20.abi, provider) as unknown as USDC;
            const tokenContractWithSigner: USDC = tokenContract.connect(signer!);
            setTokenContract(tokenContractWithSigner);
        }
    }, [rules]);

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
        if (!contract) {
            return;
        }
        const signerAddress = await signer?.getAddress();

        const owner = await contract.creator();
        setOwner(owner);

        const data = await contract.data();
        setData(data);
        console.log("Data:", data);

        const voteData = await contract.rulesVoteData();
        console.log(voteData);
        const rules = await contract.rules();
        setRules(rules);
        // if (voteData.rulesVotePending) {
            const proposedRules = await contract.proposedRules();
            console.log("Rules:", proposedRules);
            setProposedRules(proposedRules);
        // }
        const users = await contract.getUsers();
        setUsers(users);
        console.log(users);
        if (users && users.find(user => user.userType === 1)) {
            setBuyerAddress(users.find(user => user.userType === 1)?.userAddress);
        }
        const proposedUsers = await contract.getProposedUsers();
        setProposedUsers(proposedUsers);

        if (users && users.length > 0) {
            const user = users.find((element) => element.userAddress === signerAddress);

            setCurrentUser(user);
        } else {
            const defaultUser: EscrowUserStruct = {
                userAddress: signerAddress!,
                userType: 255,
                amount: ethers.utils.parseEther("0")
            }
            setCurrentUser(defaultUser);
        }

        // console.log(currentUser, currentUser?.userAddress.toString());
        
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

            {rules && (
                <div className="collapsible">
                    <div className="collapsible-title" onClick={() => {setShowRules(!showRules)}}>Rules</div>
                    {showRules && (
                        <Rules
                            rules={rules}
                            users={users}
                        />
                    )}
                </div>
            )}

            {data && (
                <div>
                    State: {data.state}
                </div>
            )}

            {voteData && (
                <div>
                    Vote: {voteData!.isVotePending ? "Active" : "Inactive"}
                </div>
            )}
            
            {voteData && voteData.isVotePending && (
                <div>
                    Start Time: {ethers.utils.formatEther(voteData.voteStartTime)}
                </div>
            )}

            {constants.STATE_NEW === data?.state && currentUser?.userAddress === owner && voteData && !voteData.isVotePending && (
                <div className="collapsible">
                    <div className="collapsible-title" onClick={() => {setShowProposeRules(!showProposeRules)}}>Propose Rules</div>
                    {showProposeRules && (
                        <ProposeRules
                            contract={contract!}
                            address={address!}
                        />
                    )}
                </div>
            )}

            {voteData && voteData.isVotePending && proposedUsers && (
                <div className="collapsible">
                    <div className="collapsible-title" onClick={() => {setShowProposedRules(!showProposedRules)}}>Vote for Rules</div>
                    {showProposedRules && (
                        <>
                            <Rules
                                rules={proposedRules}
                                users={proposedUsers}
                            />
                            <Vote contract={contract!} users={proposedUsers} address={currentUser!.userAddress.toString()}/>
                        </>
                    )}
                </div>
            )}

            

            {currentUser && (currentUser.userType === constants.USER_TYPE_BUYER ||
                                currentUser.userType === constants.USER_TYPE_BUYERS_LENDER) &&
                                (data?.state === constants.STATE_FUNDING_DEPOSIT ||
                                data?.state === constants.STATE_CONTINGENCIES_FUNDING ||
                                data?.state === constants.STATE_REGISTRATION_FUNDING) && (
                <div>
                    <DepositInput contract={contract!} name="Deposit" tokenContract={tokenContract!}/> 
                </div>
            )}
        </div>
    );
}
