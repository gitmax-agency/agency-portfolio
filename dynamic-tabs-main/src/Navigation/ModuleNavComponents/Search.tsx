import React from "react";
import { SearchIcon } from "../../assets/icons";
import Card from "../../components/Cards/Card";
import withDropdown from "../../components/Dropdown/withDropdown";
import withLeftDropdown from "../../components/Dropdown/withLeftDropdown";
import withIconContainer from "../IconComponent/withIconContainer";

const SearchContent = ({ content }) => {
  return <Card content={content} />;
};
export default withLeftDropdown(
  withDropdown(withIconContainer(SearchIcon), SearchContent)
);
