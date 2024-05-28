import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useHistory } from "react-router";
import { RootState } from "../../store/configureStore";
import { selectedTabSelector, tabClick, tabClose } from "../../store/tabs/tabs";
import { ITab } from "../../@types/types";
import Tab from "./Tab";

const Tabs = () => {
  const history = useHistory<History>();
  const dispatch = useDispatch();
  const tabs: Array<ITab> = useSelector((state: RootState) => state.tabs);
  const selectedTab: ITab = useSelector((state: RootState) =>
    selectedTabSelector(state)
  );

  useEffect(() => {
    selectedTab ? history.push(selectedTab.path) : history.push("/");
  }, [selectedTab, history]);

  return (
    <div className="tabs-container">
      {tabs.map((tab: ITab) => {
        return (
          <Tab
            tab={tab}
            key={tab.title}
            onTabClick={() => {
              dispatch(tabClick(tab));
            }}
            onTabClose={() => dispatch(tabClose(tab))}
          />
        );
      })}
    </div>
  );
};

export default Tabs;
