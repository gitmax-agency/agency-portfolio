import { React } from "react";

export default function Label({text, extraStyle}) {
  const labelStyle = {
    fontSize: "18px"
  }
  const style = Object.assign(extraStyle ? extraStyle : {}, labelStyle);
  return (
    <div className="label" style={style}>
        {text}
    </div>
  );
}
