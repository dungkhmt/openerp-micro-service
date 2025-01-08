import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { TaskService } from "../../services/api/task.service";
import { UserService } from "../../services/api/user.service";


export const fetchAllUsers = createAsyncThunk(
  "userManagement/fetchAllUsers",
  async (params) => {
    const users = await UserService.getAll(params);
    return users;
  }
);

export const fetchAssignedTasksForUser = createAsyncThunk(
  "userManagement/fetchAssignedTasksForUser",
  async ({ id, filters }) => {
    const paginationTasks = await TaskService.getAssignedTasksForUser(id, filters);
    return paginationTasks;
  }
);

const initialState = {
  usersCache: [],
  tasksCache: {},
  totalCount: 0,
  search: "",
  pagination: {
    page: 0,
    size: 10,
  },
  sort: { sort: "desc", field: "createdStamp" },
  fetchLoading: false,
  errors: [],
};

export const userManagementSlice = createSlice({
  name: "userManagement",
  initialState,
  reducers: {
    setSearch: (state, action) => {
      state.search = action.payload;
    },
    resetSearch: (state) => {
      state.search = initialState.search;
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
      state.pagination = initialState.pagination;
      state.sort = initialState.sort;
      state.fetchLoading = initialState.fetchLoading;
      state.errors = initialState.errors;
    },
    resetUsersData: (state) => {
      state.usersCache = initialState.usersCache;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAllUsers.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchAllUsers.fulfilled, (state, action) => {
        state.usersCache = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchAllUsers.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      })
      .addCase(fetchAssignedTasksForUser.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchAssignedTasksForUser.fulfilled, (state, action) => {
        state.tasksCache[state.pagination.page] = action.payload.data;
        state.totalCount = action.payload.totalElements;
        state.fetchLoading = false;
      })
      .addCase(fetchAssignedTasksForUser.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
        throw action.error;
      });
  },
});

export const {
  setPagination,
  setSort,
  resetTasksData,
  resetPagination,
  resetSort,
  clearCache,
  setSearch,
  resetSearch,
} = userManagementSlice.actions;

export default userManagementSlice.reducer;
