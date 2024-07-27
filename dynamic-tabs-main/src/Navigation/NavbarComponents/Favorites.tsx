import React from "react";
//icons
import { FavoritesIcon } from "../../assets/icons";
//components
import MenuSubList from "../Menu/MenuSubList";
import withDropdown from "../../components/Dropdown/withDropdown";
import withIconContainer from "../IconComponent/withIconContainer";
import { ITab } from "../../@types/types";

const FavoritesData: Array<ITab> = [
  {
    title: "Warehouse Management System (WMS)",
    path: "/wms",
  },
  {
    title: "Supplier and Contacts",
    path: "/supplier-contacts",
  },
  {
    title: "Work Order and Scheduling (WO)",
    path: "/wo",
  },
  {
    title: "Material planning and procurement (MRP/PO)",
    path: "/mrp-po",
  },
  {
    title: "Statistical Quality Analytics (SQA)",
    path: "/sqa",
  },
];

const FavoritesContent = ({ ...rest }) => {
  const handleModuleClick = (tab: ITab) => {};
  return (
    <MenuSubList
      modules={FavoritesData}
      onModuleClick={handleModuleClick}
      {...rest}
    />
  );
};

export default withDropdown(withIconContainer(FavoritesIcon), FavoritesContent);
