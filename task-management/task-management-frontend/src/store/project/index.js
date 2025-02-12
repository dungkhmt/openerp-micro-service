import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { ProjectService } from "../../services/api/project.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.error);
  state.fetchLoading = false;
};

export const fetchProject = createAsyncThunk(
  "project/fetchProject",
  async (projectId, { dispatch }) => {
    dispatch(setLoading(true));
    const project = await ProjectService.getProject(projectId);
    return project;
  }
);

export const updateProject = createAsyncThunk(
  "project/updateProject",
  async ({ id: projectId, data }, { dispatch }) => {
    const project = await ProjectService.updateProject(projectId, data);

    await dispatch(fetchProject(projectId));
    return project;
  }
);

export const fetchMembers = createAsyncThunk(
  "project/fetchMembers",
  async (projectId) => {
    const members = await ProjectService.getMembers(projectId);
    return members;
  }
);

export const addMember = createAsyncThunk(
  "project/addMember",
  async (data, { dispatch }) => {
    const members = await ProjectService.addMember(data);

    await dispatch(fetchMembers(data.projectId));

    return members;
  }
);

const initialState = {
  project: null,
  members: [],
  fetchLoading: false,
  errors: [],
};

export const projectSlice = createSlice({
  name: "project",
  initialState,
  reducers: {
    setLoading: (state, action) => {
      state.fetchLoading = action.payload;
    },
    resetProject: (state) => {
      state.project = initialState.project;
      state.members = initialState.members;
      state.fetchLoading = true;
      state.errors = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProject.fulfilled, (state, action) => {
        state.project = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchProject.rejected, handleRejected)
      .addCase(fetchMembers.fulfilled, (state, action) => {
        state.members = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMembers.rejected, handleRejected);
  },
});

export const { setLoading, resetProject } = projectSlice.actions;

export default projectSlice.reducer;
