import { combineReducers } from "redux";

import tabs from "./tabs/tabs";
import user from "./user/user";

export default combineReducers({
  tabs,
  user,
});
