import { React } from "react";

export default function PriceChange({number, big, extraStyle}) {
  const priceStyle = big ? {
    fontSize: "32px",
    fontWeight: "600"
  } : {
    fontSize: "24px",
    fontWeight: "400"
  }
  number = (+number).toLocaleString();
  const text = number + "%";
  const style = Object.assign(extraStyle ? extraStyle : {}, priceStyle);
  return (
    <div className={number < 0 ? "red" : "green"} style={style}>
      {text}
    </div>
  );
}
