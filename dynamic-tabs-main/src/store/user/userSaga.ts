import jwtDecode from "jwt-decode";
import { call, fork, put, takeLatest } from "redux-saga/effects";
import { login } from "../../api/auth";
import { loginAction, loginFailed, setUser } from "./user";

function* logIn(action) {
  try {
    const user = action.payload;
    const response = yield call(login, user.username, user.password);

    if (response.statusText === "OK") {
      yield put(setUser(jwtDecode(response.data)));
    }
  } catch (e) {
    yield put(loginFailed(e.message));
  }
}

function* loginWatcher() {
  yield takeLatest(loginAction.type, logIn);
}

export function* LoginSaga() {
  yield fork(loginWatcher);
}
