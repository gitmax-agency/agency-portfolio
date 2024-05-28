import React from "react";
import { ITab } from "../../@types/types";

interface Props {
  modules: Array<ITab>;
  onModuleClick: (module: ITab) => void;
  setShowDropdown?: React.Dispatch<boolean>;
}

const MenuSubList: React.FC<Props> = ({
  modules,
  onModuleClick,
  setShowDropdown,
  ...rest
}) => {
  return (
    <div className="menu-list">
      {modules.map((module: ITab) => {
        return (
          <div
            key={module.title}
            className="menu-list__item module__item"
            onClick={() => {
              onModuleClick(module);
              setShowDropdown(false);
            }}
          >
            {module.title}
          </div>
        );
      })}
    </div>
  );
};

export default MenuSubList;
