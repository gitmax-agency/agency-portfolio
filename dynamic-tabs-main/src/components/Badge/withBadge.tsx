import React from "react";
import { RestProps } from "../../@types/types";
import Badge, { badgePosition } from "./Badge";

interface Props {
  position: badgePosition;
  count: number;
}
const withBadge = (Component: React.JSXElementConstructor<RestProps>) => {
  const withBadge: React.FC<Props> = ({ position, count, ...props }) => {
    return (
      <div className="icon-badge">
        <Component {...props} />
        <Badge count={count} position={position} />
      </div>
    );
  };
  return withBadge;
};

export default withBadge;
