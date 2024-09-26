import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { TaskService } from "../../services/api/task.service";
import { UserService } from "../../services/api/user.service";

export const fetchCreatedTasks = createAsyncThunk(
  "fetchCreatedTasks",
  async (filters) => {
    const paginationTasks = await TaskService.getCreatedTasks(filters);
    return paginationTasks;
  }
);

export const fetchAllAssignees = createAsyncThunk(
  "fetchAllAssignees",
  async () => {
    const taskCreators = await UserService.getAllMeCreatedAssignee();
    return taskCreators;
  }
);

const initialState = {
  tasksCache: {},
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
  assignees: [],
};

export const createdTasksSlice = createSlice({
  name: "createdTasks",
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
      state.totalCount = initialState.totalCount;
      state.filters = initialState.filters;
      state.pagination = initialState.pagination;
      state.sort = initialState.sort;
      state.fetchLoading = initialState.fetchLoading;
      state.errors = initialState.errors;
      state.assignees = initialState.assignees;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCreatedTasks.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchCreatedTasks.fulfilled, (state, action) => {
        state.tasksCache[state.pagination.page] = action.payload.data;
        state.totalCount = action.payload.totalElements;
        state.fetchLoading = false;
      })
      .addCase(fetchCreatedTasks.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
        throw action.error;
      })
      .addCase(fetchAllAssignees.fulfilled, (state, action) => {
        state.assignees = action.payload;
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
} = createdTasksSlice.actions;

export default createdTasksSlice.reducer;
