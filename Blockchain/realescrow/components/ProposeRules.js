import { ethers } from "ethers";
import { React, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import abi from "../src/abi/external_contracts";
import InputRow from "./InputRow";

export default function ProposeRules({address, contract}) {
    // const params = useParams();
    // const address = params.address;
    const [token, setToken] = useState();
    const [depositAmount, setDepositAmount] = useState();
    const [registrationFee, setRegistrationFee] = useState();
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

    function proposeNewRules() {
        const rules = {
            paymentToken: token || "0x509Ee0d083DdF8AC028f2a56731412edD63223B9",
            depositAmount: depositAmount || ethers.utils.parseEther("10000000000000000000"),          // 10
            registrationFee: registrationFee || ethers.utils.parseEther("2000000000000000000"),         // 2
            contingencyDisputeFee: contingencyDisputeFee || ethers.utils.parseEther("3000000000000000000"),   // 3
            registrationDisputeFee: registrationDisputeFee || ethers.utils.parseEther("5000000000000000000"),  // 5
            users: [
                {
                    userAddress: arbiter || "0x509d314516c2dc3410461f9ffed378b9116f3f90",
                    userType: 5,
                    amount: 0
                },
                {
                    userAddress:  buyer || "0x509d314516c2dc3410461f9ffed378b9116f3f91",
                    userType: 1,
                    amount: ethers.utils.parseEther("102000000000000000000")
                },
                {   
                    userAddress: seller || "0x509d314516c2dc3410461f9ffed378b9116f3f92",
                    userType: 3,
                    amount: ethers.utils.parseEther("99700000000000000000")
                }],
            contingencyConditions: contingencyConditions || "Contingency",
            registrationConditions: registrationConditions || "0617b02f4ab3285a02c548c2692c9160310f9f0bcfb7ec44598123703d4598cc",
            contincencyTime:  contingencyTime || 60 * 60 * 24 * 14,        // time to satisfy contingency conditions after holding deposit is funded        
            fundingTime: fundingTime || 60 * 60 * 24 * 14,            // time to fund the escrow after contingencies
            registrationTime: registrationTime || 60 * 60 * 24 * 30,       // time to register after funding
            responseTime: responseTime || 60 * 60 * 24 * 7,
        };

        contract.proposeNewRules(rules);
    }

    return (
        <div style={{display: "flex", justifyContent: "center", flexDirection: "column"}}>
            <InputRow name="Token" setValue={setToken}/>
            <InputRow name="Deposit Amount" setValue={setDepositAmount}/>
            <InputRow name="Registration Fee" setValue={setRegistrationFee}/>
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
