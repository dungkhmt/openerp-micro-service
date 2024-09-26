import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { SearchService } from "../../services/api/search.service";

export const fetchRecentActivity = createAsyncThunk(
  "fetchRecentActivity",
  async () => await SearchService.getRecentActivity()
);

const initialState = {
  recent: {
    tasks: [],
    projects: [],
  },
};

export const searchSlice = createSlice({
  name: "search",
  initialState,
  reducers: {
    resetRecent: (state) => {
      state.recent = initialState.recent;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(fetchRecentActivity.fulfilled, (state, action) => {
      state.recent.tasks = action.payload.tasks ?? [];
      state.recent.projects = action.payload.projects ?? [];
      state.recent.loading = false;
    });
  },
});

export const { resetRecent } = searchSlice.actions;

export default searchSlice.reducer;
