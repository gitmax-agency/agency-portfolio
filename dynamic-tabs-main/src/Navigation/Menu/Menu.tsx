import React from "react";

//styles
import "./style.scss";
//icons
import { MenuIcon } from "../../assets/icons";
//components
import withDropdown from "../../components/Dropdown/withDropdown";
import MenuList from "./MenuList";
import withIconContainer from "../IconComponent/withIconContainer";

interface Props {
  setShowDropdown?: React.Dispatch<boolean>;
}

const Menu: React.FC<Props> = () => {
  return <MenuIcon />;
};

export default withDropdown(withIconContainer(Menu), MenuList);
