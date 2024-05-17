import React, { useEffect, useState } from "react";
import { Escrow, EscrowUserStruct } from "../../typechain-types/contracts/Escrow";

export default function Vote({contract, users, address}: {contract: Escrow, users: EscrowUserStruct[], address: string}) {
    
    function vote(accept: boolean) {
        contract.voteOnRules(accept);
    }

    return (
        <div style={{display: "flex", justifyContent: "center", flexDirection: "row"}}>
            {users.findIndex((user) => user.userAddress === address) !== -1 && (
                <>
                    <button onClick={() => vote(true)}>Accept</button>
                    <button onClick={() => vote(false)}>Decline</button>
                </>
            )}
        </div>
    );
}
