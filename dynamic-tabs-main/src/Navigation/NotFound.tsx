import React from "react";
import { useLocation } from "react-router";

const NotFound = () => {
  let location = useLocation<Location>();

  return (
    <div className="not-found">
      <h3>
        No match for <span>{location.pathname}</span>
      </h3>
    </div>
  );
};

export default NotFound;
