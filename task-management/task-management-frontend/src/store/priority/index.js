import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { PriorityService } from "../../services/api/task-priority.service";

export const fetchPriorities = createAsyncThunk("fetchPriorities", async () => {
  const priorities = await PriorityService.getPriorities();
  return priorities;
});

const initialState = {
  priorities: [],
  fetchLoading: false,
  errors: [],
};

export const prioritySlice = createSlice({
  name: "priority",
  initialState,
  reducers: {
    resetPriorities: (state) => {
      const newState = { ...initialState };
      // eslint-disable-next-line no-unused-vars
      state = newState;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchPriorities.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchPriorities.fulfilled, (state, action) => {
        state.priorities = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchPriorities.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const { resetPriorities } = prioritySlice.actions;

export default prioritySlice.reducer;
