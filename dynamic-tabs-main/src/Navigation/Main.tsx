import React from "react";
import { Route, Switch } from "react-router";

//Components
import Home from "../pages/Home";
import SQA from "../Sections/ENG/SQA/SQA";
import NotFound from "./NotFound";

const Main = () => {
  return (
    <div className="main">
      <Switch>
        {/* Add which component should be rendered at what path:  */}
        <Route exact path="/" component={Home} />
        <Route path="/sqa">
          <SQA />
        </Route>
        <Route path="*">
          <NotFound />
        </Route>
        {/* Should stay as the last route*/}
      </Switch>
    </div>
  );
};

export default Main;
