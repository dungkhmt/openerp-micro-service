import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { TaskService } from "../../../services/api/task.service";

export const fetchTasks = createAsyncThunk(
  "project/fetchTasks",
  async ({ projectId, filters }) => {
    const paginationTasks = await TaskService.getTasks(projectId, filters);
    return paginationTasks;
  }
);

const initialState = {
  tasksCache: {},
  totalCount: 0,
  filters: {
    categoryId: "",
    statusId: "",
    priorityId: "",
    assigneeId: "",
    q: "",
  },
  pagination: {
    page: 0,
    size: 10,
  },
  sort: { sort: "desc", field: "createdStamp" },
  fetchLoading: false,
  errors: [],
};

export const tasksSlice = createSlice({
  name: "tasks",
  initialState,
  reducers: {
    setFilters: (state, action) => {
      state.filters = action.payload;
    },
    resetFilters: (state) => {
      state.filters = initialState.filters;
    },
    setPagination: (state, action) => {
      state.pagination = action.payload;
    },
    resetPagination: (state) => {
      state.pagination = initialState.pagination;
    },
    setSort: (state, action) => {
      state.sort = action.payload;
    },
    resetSort: (state) => {
      state.sort = initialState.sort;
    },
    clearCache: (state) => {
      state.tasksCache = {};
    },
    resetTasksData: (state) => {
      state.tasksCache = initialState.tasksCache;
      state.totalCount = initialState.totalCount;
      state.filters = initialState.filters;
      state.pagination = initialState.pagination;
      state.sort = initialState.sort;
      state.fetchLoading = initialState.fetchLoading;
      state.errors = initialState.errors;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchTasks.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchTasks.fulfilled, (state, action) => {
        state.tasksCache[state.pagination.page] = action.payload.data;
        state.totalCount = action.payload.totalElements;
        state.fetchLoading = false;
      })
      .addCase(fetchTasks.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const {
  setFilters,
  setPagination,
  setSort,
  resetTasksData,
  resetFilters,
  resetPagination,
  resetSort,
  clearCache,
} = tasksSlice.actions;

export default tasksSlice.reducer;
