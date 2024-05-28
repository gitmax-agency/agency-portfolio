import React from "react";
import { HistoryIcon } from "../../assets/icons";
import Card from "../../components/Cards/Card";
import withDropdown from "../../components/Dropdown/withDropdown";
import withLeftDropdown from "../../components/Dropdown/withLeftDropdown";
import withIconContainer from "../IconComponent/withIconContainer";

const HistoryContent = () => {
  return <Card content={"test history content"} />;
};
export default withLeftDropdown(
  withDropdown(withIconContainer(HistoryIcon), HistoryContent)
);
