import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { CategoryService } from "../../services/api/task-category.service";

export const fetchCategories = createAsyncThunk("fetchCategories", async () => {
  const categories = await CategoryService.getCategories();
  return categories;
});

const initialState = {
  categories: [],
  fetchLoading: false,
  errors: [],
};

export const categorySlice = createSlice({
  name: "category",
  initialState,
  reducers: {
    resetCategories: (state) => {
      const newState = { ...initialState };
      // eslint-disable-next-line no-unused-vars
      state = newState;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCategories.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchCategories.fulfilled, (state, action) => {
        state.categories = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchCategories.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const { resetCategories } = categorySlice.actions;

export default categorySlice.reducer;
