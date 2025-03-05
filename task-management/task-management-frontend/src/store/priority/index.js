import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { PriorityService } from "../../services/api/task-priority.service";
import { clearCache } from "../project/tasks";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchPriorities = createAsyncThunk("fetchPriorities", async () => {
  const priorities = await PriorityService.getPriorities();
  return priorities;
});

export const createPriority = createAsyncThunk(
  "createPriority",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const priorities = await PriorityService.createPriority(data);
      await dispatch(fetchPriorities());
      return priorities;
    } catch (e) {
      return rejectWithValue(e.response?.data);
    }
  }
);

export const deletePriority = createAsyncThunk(
  "deletePriority",
  async ({ priorityId }, { dispatch }) => {
    const priorities = await PriorityService.deletePriority(priorityId);
    await dispatch(fetchPriorities());
    dispatch(clearCache());
    return priorities;
  }
);

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
      .addCase(fetchPriorities.rejected, handleRejected)
      .addCase(createPriority.rejected, handleRejected);
  },
});

export const { resetPriorities } = prioritySlice.actions;

export default prioritySlice.reducer;
