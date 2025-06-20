import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { MeetingPlanService } from "../../services/api/meeting-plan.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchCreatedMeetingPlans = createAsyncThunk(
  "createdMeetingPlans/fetchCreatedMeetingPlans",
  async (filters, { rejectWithValue }) => {
    try {
      return await MeetingPlanService.getCreatedMeetingPlans(filters);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const createMeetingPlan = createAsyncThunk(
  "createdMeetingPlans/create",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.createMeetingPlan(data);
      dispatch(clearCache());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

const initialState = {
  plansCache: {},
  totalCount: 0,
  search: "",
  pagination: {
    page: 0,
    size: 10,
  },
  sort: {
    sort: "desc",
    field: "createdStamp",
  },
  statusCategory: "upcoming",
  fetchLoading: false,
  errors: [],
};

export const createdMeetingPlansSlice = createSlice({
  name: "createdMeetings",
  initialState,
  reducers: {
    clearErrors: (state) => {
      state.errors = [];
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
    setStatusCategory: (state, action) => {
      state.statusCategory = action.payload;
    },
    resetStatusCategory: (state) => {
      state.statusCategory = initialState.statusCategory;
    },
    clearCache: (state) => {
      state.plansCache = {};
    },
    resetCreatedMeetings: (state) => {
      state.plansCache = initialState.plansCache;
      state.totalCount = initialState.totalCount;
      state.search = initialState.search;
      state.sort = initialState.sort;
      state.pagination = initialState.pagination;
      state.statusCategory = initialState.statusCategory;
      state.fetchLoading = initialState.fetchLoading;
      state.errors = initialState.errors;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCreatedMeetingPlans.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchCreatedMeetingPlans.fulfilled, (state, action) => {
        state.plansCache[state.pagination.page] = action.payload.data;
        state.totalCount = action.payload.totalElements;
        state.fetchLoading = false;
      })
      .addCase(fetchCreatedMeetingPlans.rejected, handleRejected)
      .addCase(createMeetingPlan.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(createMeetingPlan.fulfilled, (state) => {
        state.fetchLoading = false;
      })
      .addCase(createMeetingPlan.rejected, handleRejected);
  },
});

export const {
  clearErrors,
  setSearch,
  resetSearch,
  setPagination,
  resetPagination,
  setSort,
  resetSort,
  setStatusCategory,
  resetStatusCategory,
  clearCache,
  resetCreatedMeetings,
} = createdMeetingPlansSlice.actions;

export default createdMeetingPlansSlice.reducer;
