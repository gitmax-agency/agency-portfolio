import { React } from "react";

export default function Price({number, big, extraStyle, decimals}) {
  const priceStyle = big ? {
    fontSize: "32px",
    fontWeight: "600"
  } : {
    fontSize: "24px",
    fontWeight: "400"
  }
  number = (+number).toLocaleString("en-US", {maximumFractionDigits: decimals ? decimals : 2});
  const text = "$" + number;
  const style = Object.assign(extraStyle ? extraStyle : {}, priceStyle);
  return (
    <div style={style}>
        {text}
    </div>
  );
}
