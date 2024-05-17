import { React } from "react";

export default function ShortText({text, big, extraStyle}) {
  const priceStyle = big ? {
    fontSize: "24px",
    fontWeight: "400"
  } : {
    fontSize: "18px",
    fontWeight: "400"
  }
  const style = Object.assign(extraStyle ? extraStyle : {}, priceStyle);
  return (
    <div style={style}>
        {text}
    </div>
  );
}
