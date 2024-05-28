import React from "react";

import { CloseIcon } from "../../assets/icons";
import { ITab } from "../../@types/types";
import "./style.scss";

interface Props {
  tab: ITab;
  onTabClick: () => void;
  onTabClose: () => void;
}

const Tab: React.FC<Props> = ({ tab, onTabClick, onTabClose }) => {
  return (
    <div className="tab-container">
      <div
        className={`tab ${tab.active ? "" : "non-active"}`}
        onClick={onTabClick}
      >
        <span className="tab__title">{tab.title}</span>
      </div>
      <CloseIcon className="tab__close-icon" onClick={onTabClose} />
    </div>
  );
};

export default Tab;
