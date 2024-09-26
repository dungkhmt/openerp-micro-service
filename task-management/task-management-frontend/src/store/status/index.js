import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { StatusService } from "../../services/api/task-status.service";

export const fetchStatuses = createAsyncThunk(
  "project/fetchStatuses",
  async () => {
    const statuses = await StatusService.getStatuses();
    return statuses;
  }
);

const initialState = {
  statuses: [],
  fetchLoading: false,
  errors: [],
};

export const statusSlice = createSlice({
  name: "status",
  initialState,
  reducers: {
    resetStatus: (state) => {
      const newState = { ...initialState };
      // eslint-disable-next-line no-unused-vars
      state = newState;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchStatuses.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchStatuses.fulfilled, (state, action) => {
        state.statuses = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchStatuses.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const { resetStatus } = statusSlice.actions;

export default statusSlice.reducer;
