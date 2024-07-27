import { IUser } from "../../@types/types";
import { createSelector, createSlice } from "@reduxjs/toolkit";
import { RootState } from "../configureStore";

const initialState: IUser = {
  isLoggedIn: false,
};
const userSlice = createSlice({
  name: "user",
  initialState: initialState,
  reducers: {
    loginAction: (state: IUser, action) => {},
    setUser: (state: IUser, action) => {
      return { ...{ isLoggedIn: true }, ...action.payload };
    },

    logout: () => {
      return {
        isLoggedIn: false,
      };
    },
    loginFailed: (state, action) => {
      state.error = action.payload;
    },
  },
});

export const { loginAction, logout, setUser, loginFailed } = userSlice.actions;
export default userSlice.reducer;
