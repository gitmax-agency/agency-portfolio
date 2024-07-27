import React, { useState } from "react";
import { RestProps } from "../../@types/types";
import "./style.scss";

interface Props {
  position?: "top" | "right" | "left";
  style?: React.CSSProperties;
  mainStyle?: React.CSSProperties;
  contentStyle?: React.CSSProperties;
}

const withDropdown = (
  Component: React.JSXElementConstructor<RestProps>,
  Content: React.JSXElementConstructor<RestProps>
) => {
  const WithDropdown: React.FC<Props & RestProps> = ({
    position = "top",
    style,
    mainStyle,
    contentStyle,
    ...props
  }) => {
    const [showDropdown, setShowDropdown] = useState(false);

    return (
      <div
        className="dropdown-container"
        onMouseLeave={() => setShowDropdown(false)}
        style={style}
      >
        <div
          className="dropdown__main"
          onMouseEnter={() => setShowDropdown(true)}
          style={mainStyle}
        >
          <Component setShowDropdown={setShowDropdown} {...props} />
        </div>
        <div
          className={`dropdown__content ${position} ${
            showDropdown ? "open" : ""
          }`}
          style={contentStyle}
        >
          <Content setShowDropdown={setShowDropdown} {...props} />
        </div>
      </div>
    );
  };
  return WithDropdown;
};

export default withDropdown;
