import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { CategoryService } from "../../services/api/task-category.service";
import { clearCache } from "../project/tasks";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchCategories = createAsyncThunk(
  "fetchCategories", async () => {
  const categories = await CategoryService.getCategories();
  return categories;
});

export const createCategory = createAsyncThunk(
  "createCategory",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const categories = await CategoryService.createCategory(data);
      await dispatch(fetchCategories());
      return categories;
    } catch (e) {
      return rejectWithValue(e.response?.data);
    }
  }
);

export const deleteCategory = createAsyncThunk(
  "deleteCategory",
  async ({ categoryId }, { dispatch }) => {
    const categories = await CategoryService.deleteCategory(categoryId);
    await dispatch(fetchCategories());
    dispatch(clearCache());
    return categories;
  }
);

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
      .addCase(fetchCategories.rejected, handleRejected)
      .addCase(createCategory.rejected, handleRejected);
  },
});

export const { resetCategories } = categorySlice.actions;

export default categorySlice.reducer;
