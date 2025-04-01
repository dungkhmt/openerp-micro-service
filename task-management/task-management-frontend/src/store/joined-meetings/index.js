import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { MeetingPlanService } from "../../services/api/meeting-plan.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchJoinedMeetingPlans = createAsyncThunk(
  "joinedMeetingPlans/fetchJoinedMeetingPlans",
  async (filters, { rejectWithValue }) => {
    try {
      return await MeetingPlanService.getJoinedMeetingPlans(filters);
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
  fetchLoading: false,
  errors: [],
};

export const joinedMeetingPlansSlice = createSlice({
  name: "joinedMeetings",
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
    clearCache: (state) => {
      state.plansCache = {};
    },
    resetJoinedMeetings: (state) => {
      // eslint-disable-next-line no-unused-vars
      state = { ...initialState };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchJoinedMeetingPlans.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchJoinedMeetingPlans.fulfilled, (state, action) => {
        state.plansCache[state.pagination.page] = action.payload.data;
        state.totalCount = action.payload.totalElements;
        state.fetchLoading = false;
      })
      .addCase(fetchJoinedMeetingPlans.rejected, handleRejected);
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
  clearCache,
  resetJoinedMeetings,
} = joinedMeetingPlansSlice.actions;

export default joinedMeetingPlansSlice.reducer;
