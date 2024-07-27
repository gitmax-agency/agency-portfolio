import React from "react";
//StaticData
import { OptionsIcon } from "../../assets/icons";
import withDropdown from "../../components/Dropdown/withDropdown";
import withLeftDropdown from "../../components/Dropdown/withLeftDropdown";
import { ITab } from "../../@types/types";
import withIconContainer from "../IconComponent/withIconContainer";

const optionsList: Array<ITab> = [
  {
    path: "/reports",
    title: "Reports",
  },
  {
    path: "/toolbox",
    title: "Toolbox",
  },
  {
    path: "/notifications",
    title: "Notifications",
  },
  {
    path: "/udf",
    title: "UDF",
  },
  {
    path: "/database-table",
    title: "Database Table",
  },
  {
    path: "/settings",
    title: "Settings",
  },
];

const OptionsContent = () => {
  return (
    <div className="menu-list">
      {optionsList.map((option: ITab) => {
        return (
          <div key={option.title} className="menu-list__item">
            {option.title}
          </div>
        );
      })}
    </div>
  );
};

export default withLeftDropdown(
  withDropdown(withIconContainer(OptionsIcon), OptionsContent)
);
