import React from "react";
import { useState } from "react";
import withIconContainer from "../IconComponent/withIconContainer";

const Timer = () => {
  const [timer, setTimer] = useState(0);
  setInterval(() => {
    setTimer(timer + 1);
  }, 60 * 1000);
  return (
    <div className="timer">
      :{timer < 10 ? "0" : ""}
      {timer}
    </div>
  );
};

export default withIconContainer(Timer);
