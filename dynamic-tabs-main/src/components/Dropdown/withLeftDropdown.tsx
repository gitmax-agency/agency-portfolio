import React from "react";

interface RestProps {
  [x: string]: any;
}

const withLeftDropdown = (
  Component: React.JSXElementConstructor<RestProps>
) => {
  const WithDropdown: React.FC<RestProps> = ({ ...props }) => {
    return (
      <Component
        position="top"
        style={{ display: "flex", justifyContent: "flex-end" }}
        contentStyle={{ top: "4.5rem" }}
        {...props}
      />
    );
  };
  return WithDropdown;
};

export default withLeftDropdown;
