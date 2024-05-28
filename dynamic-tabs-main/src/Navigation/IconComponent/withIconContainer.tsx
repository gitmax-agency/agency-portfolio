import React from "react";
//style
import "./style.scss";

const withIconContainer = (Component) => {
  const withIconContainer = ({ ...props }) => {
    return (
      <div className="icon-container">
        <Component />
      </div>
    );
  };
  return withIconContainer;
};

export default withIconContainer;
