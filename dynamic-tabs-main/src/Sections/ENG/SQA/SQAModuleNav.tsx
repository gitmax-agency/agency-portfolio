import React from "react";
import History from "../../../Navigation/ModuleNavComponents/History";
import Options from "../../../Navigation/ModuleNavComponents/Options";
import Plus from "../../../Navigation/ModuleNavComponents/Plus";
import Search from "../../../Navigation/ModuleNavComponents/Search";
import Timer from "../../../Navigation/ModuleNavComponents/Timer";

const SQAModuleNav = () => {
  return (
    <>
      <Search />
      <Plus />
      {/* <Timer /> */}
      <History />
      <Options />
    </>
  );
};

export default SQAModuleNav;
