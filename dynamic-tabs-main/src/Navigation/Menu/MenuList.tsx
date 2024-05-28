import React, { Dispatch } from "react";
//Data
import { MenuListData } from "./MenuListData";
//State
import { useDispatch, useSelector } from "react-redux";
import { tabAdd } from "../../store/tabs/tabs";
//Components
import MenuListItem from "./MenuListItem";
import { IUser, ITab } from "../../@types/types";
import { RootState } from "../../store/configureStore";
import { logout } from "../../store/user/user";

interface Props {
  setShowDropdown?: React.Dispatch<boolean>;
}
interface ISection {
  title: string;
  path: string;
  module: Array<ITab>;
}

const MenuList: React.FC<Props> = ({ setShowDropdown, ...rest }) => {
  const dispatch = useDispatch<Dispatch<any>>();
  const user: IUser = useSelector((state: RootState) => state.user);
  const handleModuleClick = (tab: ITab) => {
    dispatch(tabAdd(tab));
    setShowDropdown(false);
  };
  const handleLogout = () => {
    dispatch(logout());
    setShowDropdown(false);
  };
  return (
    <div className="menu-list">
      {MenuListData.map((section: ISection) => {
        return (
          <MenuListItem
            key={section.title}
            position="right"
            style={{ display: "flex" }}
            mainStyle={{ zIndex: 100 }}
            contentStyle={{ marginLeft: "20rem", zIndex: 90 }}
            title={section.title}
            modules={section.module}
            onModuleClick={handleModuleClick}
          />
        );
      })}
      <div className="menu-list__item">{user.email}</div>
      <div className="menu-list__item" onClick={handleLogout}>
        Logout
      </div>
    </div>
  );
};

export default MenuList;
