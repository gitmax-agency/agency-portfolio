import React from "react";
//style
import "./style.scss";
//Router
import { Route, Switch } from "react-router";
//components
import SQAModuleNav from "../../Sections/ENG/SQA/SQAModuleNav";

interface Props {}

const ModuleNavItems: React.FC<Props> = () => {
  return (
    <div className="module-nav">
      <Switch>
        <Route path="/sqa">
          <SQAModuleNav />
        </Route>
        <Route path="*"></Route>
      </Switch>
    </div>
  );
};

export default ModuleNavItems;
