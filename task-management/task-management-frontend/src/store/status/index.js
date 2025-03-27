import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { StatusService } from "../../services/api/task-status.service";
import { clearCache } from "../project/tasks";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchStatuses = createAsyncThunk("fetchStatuses", async () => {
  const statuses = await StatusService.getStatuses();
  return statuses;
});

export const createStatus = createAsyncThunk(
  "createStatus",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const statuses = await StatusService.createStatus(data);
      await dispatch(fetchStatuses());
      return statuses;
    } catch (e) {
      return rejectWithValue(e.response?.data);
    }
  }
);

export const deleteStatus = createAsyncThunk(
  "deleteStatus",
  async ({ statusId }, { dispatch }) => {
    const statuses = await StatusService.deleteStatus(statusId);
    await dispatch(fetchStatuses());
    dispatch(clearCache());
    return statuses;
  }
);

const initialState = {
  statuses: [],
  taskStatuses: [],
  meetingStatuses: [],
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
        state.taskStatuses = action.payload.filter(
          (status) => status.type === "TASK_STATUS"
        );
        state.meetingStatuses = action.payload.filter(
          (status) => status.type === "MEETING_STATUS"
        );
        state.fetchLoading = false;
      })
      .addCase(fetchStatuses.rejected, handleRejected)
      .addCase(createStatus.rejected, handleRejected);
  },
});

export const { resetStatus } = statusSlice.actions;

export default statusSlice.reducer;
