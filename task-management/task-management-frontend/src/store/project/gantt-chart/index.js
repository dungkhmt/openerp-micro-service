import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import dayjs from "dayjs";
import { TaskService } from "../../../services/api/task.service";
import { ViewMode } from "gantt-task-react";

export const fetchGanttChartTasks = createAsyncThunk(
  "project/fetchGanttChartTasks",
  async ({ projectId, from, to, q }) => {
    // TODO: separate the logic to get tasks from the service
    const tasks = await TaskService.getTasksGantt(projectId, from, to, q);
    return tasks;
  }
);

const initialState = {
  tasks: [],
  view: ViewMode.Week,
  search: "",
  filters: {
    condition: "AND",
    items: [],
  },
  range: {
    duration: 3, // 3 months
    startDate: dayjs().subtract(1, "months").startOf("month").toDate(),
  },
  fetchLoading: false,
  errors: [],
};

export const ganttChartTasks = createSlice({
  name: "gantt",
  initialState,
  reducers: {
    setSearch: (state, action) => {
      state.search = action.payload;
    },
    resetSearch: (state) => {
      state.search = initialState.search;
    },
    setView: (state, action) => {
      state.view = action.payload;
    },
    resetView: (state) => {
      state.view = initialState.view;
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
    resetGanttData: (state) => {
      state.tasks = initialState.tasks;
      state.search = initialState.search;
      state.filters = initialState.filters;
      state.fetchLoading = initialState.fetchLoading;
      state.view = initialState.view;
      state.range = initialState.range;
      state.errors = initialState.errors;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchGanttChartTasks.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchGanttChartTasks.fulfilled, (state, action) => {
        state.tasks = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchGanttChartTasks.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const {
  setFilters,
  resetFilters,
  setSearch,
  resetSearch,
  setView,
  resetView,
  setRange,
  resetRange,
  resetGanttData,
} = ganttChartTasks.actions;

export default ganttChartTasks.reducer;
