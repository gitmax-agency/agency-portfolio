import React from "react";

export default function ValueRow({name, value}) {
    return (
        <div style={{display: "flex", justifyContent: "space-between"}}>
            <div>
                {name}
            </div>
            <div>
                {value}
            </div>
        </div>
    )
}