import React from "react";

export default function InputRow({name, setValue}) {
    return (
        <div style={{display: "flex", justifyContent: "space-between"}}>
            <div>
                {name}
            </div>
            <div>
                <input onChange={(e) => setValue(e.target.value)}/>
            </div>
        </div>
    )
}