import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import dayjs from "dayjs";
import { TaskService } from "../../../services/api/task.service";

export const fetchCalendarTasks = createAsyncThunk(
  "project/fetchCalendarTasks",
  async ({ projectId, from, to, q }) => {
    // TODO: separate the logic to get tasks from the service
    const tasks = await TaskService.getTasksGantt(projectId, from, to, q);
    return tasks;
  }
);

const initialState = {
  tasks: [],
  view: "dayGridMonth",
  filters: {
    condition: "AND",
    items: [],
  },
  range: {
    startDate: dayjs().startOf("month").startOf("week").toDate(),
    endDate: dayjs().endOf("month").endOf("week").toDate(),
  },
  selectedTask: null,
  fetchLoading: false,
  errors: [],
};

export const calendarTasks = createSlice({
  name: "calendar",
  initialState,
  reducers: {
    setView: (state, action) => {
      state.view = action.payload;
    },
    resetView: (state) => {
      state.view = initialState.view;
    },
    setSelectedTask: (state, action) => {
      state.selectedTask = action.payload;
    },
    resetSelectedTask: (state) => {
      state.selectedTask = initialState.selectedTask;
    },
    setRange: (state, action) => {
      state.range = action.payload;
    },
    resetRange: (state) => {
      state.range = initialState.range;
    },
    setFilters: (state, action) => {
      state.filters = action.payload;
    },
    resetFilters: (state) => {
      state.filters = initialState.filters;
    },
    resetCalendarData: (state) => {
      state.tasks = initialState.tasks;
      state.filters = initialState.filters;
      state.fetchLoading = initialState.fetchLoading;
      state.view = initialState.view;
      state.range = initialState.range;
      state.errors = initialState.errors;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCalendarTasks.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchCalendarTasks.fulfilled, (state, action) => {
        state.tasks = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchCalendarTasks.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const {
  setFilters,
  resetFilters,
  setSelectedTask,
  resetSelectedTask,
  setView,
  resetView,
  setRange,
  resetRange,
  resetCalendarData,
} = calendarTasks.actions;

export default calendarTasks.reducer;
