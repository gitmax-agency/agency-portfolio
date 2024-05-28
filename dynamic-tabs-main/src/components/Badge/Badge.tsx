import React, { FC } from "react";
//style
import "./Badge.scss";

interface Props {
  position: badgePosition;
  count: number;
}

export enum badgePosition {
  topRight = "badge--top-right",
  topLeft = "badge--top-left",
  bottomRight = "badge--bottom-right",
  bottomLeft = "badge--bottom-left",
}

const Badge: FC<Props> = ({ position, count }) => {
  return count === 0 ? null : (
    <span className={`badge ${position}`}>{count}</span>
  );
};
export default Badge;
