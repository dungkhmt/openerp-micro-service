import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { TaskService } from "../../../services/api/task.service";

export const fetchTasks = createAsyncThunk(
  "project/fetchTasks",
  async ({ projectId, filters }) => {
    const paginationTasks = await TaskService.getTasks(projectId, filters);
    return paginationTasks;
  }
);

export const fetchTasksForMember = createAsyncThunk(
  "project/fetchTasksForMember",
  async ({projectId, assigneeId}) => {
    const tasks = await TaskService.getMemberTasks(projectId, assigneeId);
    return tasks;
  }
);

const initialState = {
  tasksCache: {},
  memberTasks: [],
  totalCount: 0,
  search: "",
  filters: {
    condition: "AND",
    items: [],
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
    setSearch: (state, action) => {
      state.search = action.payload;
    },
    resetSearch: (state) => {
      state.search = initialState.search;
    },
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
      state.memberTasks = initialState.memberTasks;
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
      })
      .addCase(fetchTasksForMember.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchTasksForMember.fulfilled, (state, action) => {
        state.memberTasks = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchTasksForMember.rejected, (state, action) => {
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
  setSearch,
  resetSearch,
} = tasksSlice.actions;

export default tasksSlice.reducer;
