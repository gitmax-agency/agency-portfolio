import { createSlice } from "@reduxjs/toolkit";

const sqaSlice = createSlice({
  name: "sqa",
  initialState: {
    moduleNav: {
      search: {},
      plus: {},
      timer: {},
      history: {},
      options: {},
    },
    data: {},
  },
  reducers: {
    increment: (counter, action) => {
      console.log("increment");
    },
    decrement: (counter, action) => {
      console.log("decrement");
    },
  },
});
export const { increment, decrement } = sqaSlice.actions;
export default sqaSlice.reducer;
