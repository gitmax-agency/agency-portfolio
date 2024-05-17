import { React } from "react";

export default function TextBlock({header, image, text, imagePosition, extraStyle}) {

    const style = Object.assign(extraStyle ? extraStyle : {}, {
                                                                display: "flex",
                                                                flexDirection: imagePosition === "left" ? "row" : "row-reverse",
                                                                alignItems: "center"
                                                              });

    return (
        <div style={style} className="text-block">
            <div style={{width: "100%", height: "200px", textAlign: imagePosition}}>
                <img style={{maxHeight: "100%", maxWidth: "100%"}} src={image}></img>
            </div>
            <div style={{textAlign: "left",
                        width: "100%",
                        marginLeft: imagePosition === "left" ? "20px" : 0,
                        marginRight: imagePosition === "left" ? 0 : "20px"}}>
                <h2>{header}</h2>
                <p style={{marginBottom: 0}}>{text}</p>
            </div>
        </div>
    );
}
