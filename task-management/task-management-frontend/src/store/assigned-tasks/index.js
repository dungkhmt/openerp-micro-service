import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { TaskService } from "../../services/api/task.service";
import { UserService } from "../../services/api/user.service";

export const fetchAssignedTasks = createAsyncThunk(
  "fetchAssignedTasks",
  async (filters) => {
    const paginationTasks = await TaskService.getAssignedTasks(filters);
    return paginationTasks;
  }
);

export const fetchAllAssignedTaskCreator = createAsyncThunk(
  "fetchAllAssignedTaskCreator",
  async () => {
    const taskCreators = await UserService.getAllAssignedTaskCreator();
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
  creators: [],
};

export const assignedTasksSlice = createSlice({
  name: "assignedTasks",
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
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAssignedTasks.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchAssignedTasks.fulfilled, (state, action) => {
        state.tasksCache[state.pagination.page] = action.payload.data;
        state.totalCount = action.payload.totalElements;
        state.fetchLoading = false;
      })
      .addCase(fetchAssignedTasks.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
        throw action.error;
      })
      .addCase(fetchAllAssignedTaskCreator.fulfilled, (state, action) => {
        state.creators = action.payload;
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
} = assignedTasksSlice.actions;

export default assignedTasksSlice.reducer;
