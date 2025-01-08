import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { SkillService } from "../../services/api/skill.service";

export const fetchSkills = createAsyncThunk(
  "fetchSkills",
  async () => {
    const skills = await SkillService.getAllSkills();
    return skills;
  }
);

const initialState = {
  skills: [],
  fetchLoading: false,
  errors: [],
};

export const skillSlice = createSlice({
  name: "skill",
  initialState,
  reducers: {
    resetSkills: (state) => {
      const newState = { ...initialState };
      // eslint-disable-next-line no-unused-vars
      state = newState;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchSkills.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchSkills.fulfilled, (state, action) => {
        state.skills = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchSkills.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const { resetSkills } = skillSlice.actions;

export default skillSlice.reducer;
