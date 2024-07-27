import { ethers, BigNumber } from "ethers";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Escrow } from "../../typechain-types";
import { EscrowRulesStruct } from "../../typechain-types/contracts/Escrow";
import abi from "../abi/external_contracts";
import InputRow from "./InputRow";

export default function ProposeRules({address, contract}: {address: string, contract: Escrow}) {
    // const params = useParams();
    // const address = params.address;
    const [token, setToken] = useState();
    const [depositAmount, setDepositAmount] = useState();
    const [registrationFee, setRegistrationFee] = useState();
    const [arbiterFee, setArbiterFee] = useState();
    const [contingencyDisputeFee, setContingencyDisputeFee] = useState();   // 3
    const [registrationDisputeFee, setRegistrationDisputeFee] = useState();  // 5
    const [buyer, setBuyer] = useState();
    const [seller, setSeller] = useState();
    const [arbiter, setArbiter] = useState();
    const [contingencyConditions, setContingencyConditions] = useState();
    const [registrationConditions, setRegistrationConditions] = useState();
    const [contingencyTime, setContingencyTime] = useState();        // time to satisfy contingency conditions after holding deposit is funded        
    const [fundingTime, setFundingTime] = useState();            // time to fund the escrow after contingencies
    const [registrationTime, setRegistrationTime] = useState();       // time to register after funding
    const [responseTime, setResponseTime] = useState();

    const ESCROW_FACTORY_ADDRESS = "0x1EA48D5Cb58F969734149F9Ed7818F362774D936";
    const USDC_ADDRESS = "0x5Fa0a523d040de86CB0038323B2Fb3Db7C6345f3";

    const STATE_NEW = 0;
    const STATE_FUNDING_DEPOSIT = 1;
    const STATE_CONTINGENCIES = 2;
    const STATE_CONTINGENCIES_FUNDING = 3;
    const STATE_CONTINGENCIES_DISPUTE = 4;
    const STATE_FUNDING_PURCHASE = 5;
    const STATE_REGISTRATION = 6;
    const STATE_REGISTRATION_FUNDING = 7;
    const STATE_REGISTRATION_DISPUTE = 8;
    const STATE_COMPLETED = 9;
    const STATE_FAILED = 10;

    const USER_TYPE_BUYER = 1;
    const USER_TYPE_BUYERS_LENDER = 2;
    const USER_TYPE_SELLER = 3;
    const USER_TYPE_SELLERS_LENDER = 4;
    const USER_TYPE_ARBITER = 5;
    const USER_TYPE_REGISTRATOR = 6;
    const USER_TYPE_BROKER = 7;

    async function proposeNewRules() {
        const rules: EscrowRulesStruct = {
            paymentToken: token || "0xd7fe8cfF5D2bD80973043018a2639f68BeA4267A",
            depositAmount: depositAmount || BigNumber.from("20000000000000000000"),          // 10
            registrationFee: registrationFee || BigNumber.from("2000000000000000000"),         // 2
            arbiterFee: arbiterFee || BigNumber.from("3000000000000000000"),
            contingencyDisputeFee: contingencyDisputeFee || BigNumber.from("4000000000000000000"),   // 3
            registrationDisputeFee: registrationDisputeFee || BigNumber.from("5000000000000000000"),  // 5
            users: [
                {
                    userAddress: arbiter || "0x509d314516c2dc3410461f9ffed378b9116f3f90",
                    userType: 5,
                    amount: 0
                },
                {
                    userAddress:  buyer || "0x2385008CDd76195C068Ce65CfC9Dc812A654B3D3",
                    userType: 1,
                    amount: BigNumber.from("106000000000000000000")
                },
                {   
                    userAddress: seller || "0x3cb653731e8664ECb1635A658FF2108dd54189F6",
                    userType: 3,
                    amount: BigNumber.from("100000000000000000000")
                }
            ],
            contingencyConditions: contingencyConditions || "aaa",
            registrationConditions: registrationConditions || "bbb",
            contingencyTime:  contingencyTime || 60 * 60 * 24 * 14,        // time to satisfy contingency conditions after holding deposit is funded        
            fundingTime: fundingTime || 60 * 60 * 24 * 14,            // time to fund the escrow after contingencies
            registrationTime: registrationTime || 60 * 60 * 24 * 30,       // time to register after funding
            responseTime: responseTime || 60 * 60 * 24 * 7,
        };

        // const users = [{
        //     userAddress: "0x509d314516c2dc3410461f9ffed378b9116f3f90",
        //     userType: USER_TYPE_ARBITER,
        //     amount: 0
        // },
        // {
        //     userAddress: "0x2385008CDd76195C068Ce65CfC9Dc812A654B3D3",
        //     userType: USER_TYPE_BUYER,
        //     amount: BigNumber.from("106000000000000000000") // 100 + 1 + 2 + 3
        // },
        // {
        //     userAddress: "0x3cb653731e8664ECb1635A658FF2108dd54189F6",
        //     userType: USER_TYPE_SELLER,
        //     amount: BigNumber.from("100000000000000000000") // 100
        // }];

    
        // const rules = {
        //     paymentToken: "0xd7fe8cfF5D2bD80973043018a2639f68BeA4267A",
        //     depositAmount: BigNumber.from("20000000000000000000"),          // 20
        //     registrationFee: BigNumber.from("2000000000000000000"),         // 2
        //     arbiterFee: BigNumber.from("3000000000000000000"),              // 3
        //     contingencyDisputeFee: BigNumber.from("4000000000000000000"),   // 4
        //     registrationDisputeFee: BigNumber.from("5000000000000000000"),  // 5
        //     users: users,
        //     contingencyConditions: "aaa",
        //     registrationConditions: "bbb",
        //     contingencyTime: 60 * 60 * 24 * 14, // 2 weeks
        //     fundingTime: 60 * 60 * 24 * 14, // 2 weeks
        //     registrationTime: 60 * 60 * 24 * 30, // 30 days
        //     responseTime: 60 * 60 * 24 * 7 // 1 week
        // };

        console.log(rules);

        const provider = new ethers.providers.Web3Provider(window.ethereum);
        const signer = provider.getSigner();
        // console.log(rules);
        // contract.estimateGas.proposeNewRules(rules).then((result) => console.log(result));
        // // contract.proposeNewRules(rules);
        // // contract.deleteEscrow({gasLimit: 200000});
        // console.log(contract);

        // const [owner, arbiter, buyer, seller] = await ethers.getSigners();

        // const users = [{
        //     userAddress: buyer.address,
        //     userType: USER_TYPE_BUYER,
        //     amount: BigNumber.from("103000000000000000000")
        // },
        // {
        //     userAddress: seller.address,
        //     userType: USER_TYPE_SELLER,
        //     amount: BigNumber.from("100000000000000000000")
        // },
        // {
        //     userAddress: arbiter.address,
        //     userType: USER_TYPE_ARBITER,
        //     amount: 0
        // }];

        // const escrowRules = {
        //     paymentToken: usdc.address,
        //     depositAmount: BigNumber.from("20000000000000000000"),          // 20
        //     registrationFee: BigNumber.from("2000000000000000000"),         // 2
        //     arbiterFee: 0,
        //     contingencyDisputeFee: BigNumber.from("3000000000000000000"),   // 3
        //     registrationDisputeFee: BigNumber.from("5000000000000000000"),  // 5
        //     users: users,
        //     contingencyConditions: "aaa",
        //     registrationConditions: "bbb",
        //     contingencyTime: 60 * 60 * 24 * 14, // 2 weeks
        //     fundingTime: 60 * 60 * 24 * 14, // 2 weeks
        //     registrationTime: 60 * 60 * 24 * 30, // 30 days
        //     responseTime: 60 * 60 * 24 * 7 // 1 week
        // };
        // console.log(address);
        // await contract.connect(signer).proposeNewRules(rules);
        const transaction = await contract.connect(signer).proposeNewRules(rules);
        await transaction.wait();
        // await contract.deleteEscrow();
    }

    return (
        <div style={{display: "flex", justifyContent: "center", flexDirection: "column"}}>
            <InputRow name="Token" setValue={setToken}/>
            <InputRow name="Deposit Amount" setValue={setDepositAmount}/>
            <InputRow name="Registration Fee" setValue={setRegistrationFee}/>
            <InputRow name="Arbiter Fee" setValue={setArbiterFee}/>
            <InputRow name="Contingency Dispute Fee" setValue={setContingencyDisputeFee}/>
            <InputRow name="Registration Dispute Fee" setValue={setRegistrationDisputeFee}/>
            <InputRow name="Buyer" setValue={setBuyer}/>
            <InputRow name="Seller" setValue={setSeller}/>
            <InputRow name="Arbiter" setValue={setArbiter}/>
            <InputRow name="Contingency Conditions" setValue={setContingencyConditions}/>
            <InputRow name="Registration Conditions" setValue={setRegistrationConditions}/>
            <InputRow name="Contingency Time" setValue={setContingencyTime}/>
            <InputRow name="Funding Time" setValue={setFundingTime}/>
            <InputRow name="Registration Time" setValue={setRegistrationTime}/>
            <InputRow name="Response Time" setValue={setResponseTime}/>
            <button onClick={proposeNewRules}>Propose Rules</button>
        </div>
    );
}
