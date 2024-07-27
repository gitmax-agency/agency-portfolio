import { ethers } from "ethers";
import React, { useEffect, useState } from "react";
import ValueRow from "./ValueRow";

export default function Rules({ rules, users }) {

    const buyer = users.find((user) => user.userType === 1) || {};
    const seller = users.find((user) => user.userType === 3) || {};
    const arbiter = users.find((user) => user.userType === 5) || {};
    
    return (
        <div style={{display: "flex", justifyContent: "center", flexDirection: "column"}}>
            <ValueRow name="Token" value={rules.paymentToken}/>
            <ValueRow name="Deposit Amount" value={ethers.utils.formatEther(rules.depositAmount)}/>
            <ValueRow name="Registration Fee" value={ethers.utils.formatEther(rules.registrationFee)}/>
            <ValueRow name="Contingency Dispute Fee" value={ethers.utils.formatEther(rules.contingencyDisputeFee)}/>
            <ValueRow name="Registration Dispute Fee" value={ethers.utils.formatEther(rules.registrationDisputeFee)}/>
            <ValueRow name="Buyer" value={buyer.userAddress}/>
            <ValueRow name="Seller" value={seller.userAddress}/>
            <ValueRow name="Arbiter" value={arbiter.userAddress}/>
            <ValueRow name="Contingency Conditions" value={rules.contingencyConditions}/>
            <ValueRow name="Registration Conditions" value={rules.registrationConditions}/>
            <ValueRow name="Contingency Time" value={ethers.utils.formatEther(rules.contingencyTime)}/>
            <ValueRow name="Funding Time" value={ethers.utils.formatEther(rules.fundingTime)}/>
            <ValueRow name="Registration Time" value={ethers.utils.formatEther(rules.registrationTime)}/>
            <ValueRow name="Response Time" value={ethers.utils.formatEther(rules.responseTime)}/>
        </div>
    );
}
