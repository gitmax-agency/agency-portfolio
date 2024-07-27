import { all } from "redux-saga/effects";
import { LoginSaga } from "./user/userSaga";

export default function* rootSaga() {
  yield all([LoginSaga()]);
}
