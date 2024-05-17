import { React } from "react";

export default function Button({click, title, buttonClass, extraStyle}) {
  const style = Object.assign({padding: "9px 16px", cursor: "pointer"}, extraStyle ? extraStyle : {});
  return (
    <button className={buttonClass + " button"} onClick={click} style={style}>
        {title}
    </button>
  );
}
