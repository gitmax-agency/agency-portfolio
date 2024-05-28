import React from "react";
import withDropdown from "../../components/Dropdown/withDropdown";
import MenuSubList from "./MenuSubList";

const MenuListItem = ({ title }: { title: string }) => {
  return <div className="menu-list__item">{title}</div>;
};

export default withDropdown(MenuListItem, MenuSubList);
