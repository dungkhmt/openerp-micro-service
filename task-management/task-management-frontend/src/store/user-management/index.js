import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { TaskService } from "../../services/api/task.service";
import { OrganizationService } from "../../services/api/organization.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchAllUsers = createAsyncThunk(
  "userManagement/fetchAllUsers",
  async (organizationId) => {
    const users = await OrganizationService.getUsersByOrganizationId(
      organizationId
    );
    return users;
  }
);

export const fetchAssignedTasksForUser = createAsyncThunk(
  "userManagement/fetchAssignedTasksForUser",
  async ({ id, filters }) => {
    const paginationTasks = await TaskService.getAssignedTasksForUser(
      id,
      filters
    );
    return paginationTasks;
  }
);

const initialState = {
  usersCache: [],
  currentUser: null,
  tasksCache: {},
  totalCount: 0,
  search: "",
  pagination: {
    page: 0,
    size: 10,
  },
  sort: { sort: "desc", field: "createdStamp" },
  tabValue: 0,
  fetchLoading: false,
  errors: [],
};

export const userManagementSlice = createSlice({
  name: "userManagement",
  initialState,
  reducers: {
    setCurrentUser: (state, action) => {
      state.currentUser = action.payload;
    },
    setTabValue: (state, action) => {
      state.tabValue = action.payload;
    },
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
    },
    resetUsersData: (state) => {
      state.usersCache = initialState.usersCache;
    },
    resetUserManagement: (state) => {
      state.usersCache = initialState.usersCache;
      state.tasksCache = initialState.tasksCache;
      state.totalCount = initialState.totalCount;
      state.search = initialState.search;
      state.pagination = initialState.pagination;
      state.sort = initialState.sort;
      state.tabValue = initialState.tabValue;
      state.fetchLoading = initialState.fetchLoading;
      state.errors = initialState.errors;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAllUsers.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchAllUsers.fulfilled, (state, action) => {
        state.usersCache = action.payload.map(({ user }) => user);
        state.fetchLoading = false;
      })
      .addCase(fetchAllUsers.rejected, handleRejected)
      .addCase(fetchAssignedTasksForUser.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchAssignedTasksForUser.fulfilled, (state, action) => {
        state.tasksCache[state.pagination.page] = action.payload.data;
        state.totalCount = action.payload.totalElements;
        state.fetchLoading = false;
      })
      .addCase(fetchAssignedTasksForUser.rejected, handleRejected);
  },
});

export const {
  setCurrentUser,
  setTabValue,
  setPagination,
  setSort,
  resetTasksData,
  resetPagination,
  resetSort,
  clearCache,
  setSearch,
  resetSearch,
  resetUserManagement,
} = userManagementSlice.actions;

export default userManagementSlice.reducer;
