import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  currentData: {},
};

const accountSlice = createSlice({
  name: "account",
  initialState,
  reducers: {
    set_current_account: (state, action) => {
      state.currentData = action.payload || {};
    },
  },
});

export const { set_current_account } = accountSlice.actions;

export default accountSlice.reducer;
