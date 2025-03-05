import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { SkillService } from "../../services/api/skill.service";
import { clearCache } from "../project/tasks";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchSkills = createAsyncThunk("fetchSkills", async () => {
  const skills = await SkillService.getAllSkills();
  return skills;
});

export const addSkill = createAsyncThunk(
  "addSkill",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const skills = await SkillService.addSkill(data);
      await dispatch(fetchSkills());
      return skills;
    } catch (e) {
      return rejectWithValue(e.response?.data);
    }
  }
);

export const deleteSkill = createAsyncThunk(
  "deleteSkill",
  async ({ skillId }, { dispatch }) => {
    const skills = await SkillService.deleteSkill(skillId);
    await dispatch(fetchSkills());
    dispatch(clearCache());
    return skills;
  }
);

export const updateSkill = createAsyncThunk(
  "updateSkill",
  async ({ skillId, data }, { dispatch }) => {
    const skills = await SkillService.updateSkill(data, skillId);
    await dispatch(fetchSkills());
    dispatch(clearCache());
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
      // eslint-disable-next-line no-unused-vars
      state = { ...initialState };
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
      .addCase(fetchSkills.rejected, handleRejected)
      .addCase(addSkill.rejected, handleRejected)
  },
});

export const { resetSkills } = skillSlice.actions;

export default skillSlice.reducer;
