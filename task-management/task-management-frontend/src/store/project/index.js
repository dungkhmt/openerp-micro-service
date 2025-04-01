import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { ProjectService } from "../../services/api/project.service";
import { fetchEvents } from "./events";
import { clearCache } from "./tasks";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchProject = createAsyncThunk(
  "project/fetchProject",
  async (projectId, { rejectWithValue }) => {
    try {
      const project = await ProjectService.getProject(projectId);
      return project;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
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
  async (projectId, { rejectWithValue }) => {
    try {
      const members = await ProjectService.getMembers(projectId);
      return members;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
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

export const deleteMember = createAsyncThunk(
  "project/deleteMember",
  async ({ projectId, memberId, roleId }, { dispatch, rejectWithValue }) => {
    try {
      const members = await ProjectService.deleteMember(
        projectId,
        memberId,
        roleId
      );
      await dispatch(fetchMembers(projectId));
      await dispatch(fetchEvents(projectId));
      dispatch(clearCache());
      return members;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const updateMemberRole = createAsyncThunk(
  "project/updateMemberRole",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const members = await ProjectService.updateMemberRole(data);
      await dispatch(fetchMembers(data.projectId));
      return members;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchMyRole = createAsyncThunk(
  "project/fetchMyRole",
  async (projectId, { rejectWithValue }) => {
    try {
      const role = await ProjectService.getMyRole(projectId);
      return role;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

const initialState = {
  project: null,
  members: [],
  myRole: null,
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
      state.myRole = initialState.myRole;
      state.members = initialState.members;
      state.fetchLoading = initialState.fetchLoading;
      state.errors = initialState.errors;
    },
    clearErrors: (state) => {
      state.errors = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProject.fulfilled, (state, action) => {
        state.project = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchProject.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchProject.rejected, handleRejected)
      .addCase(fetchMembers.fulfilled, (state, action) => {
        state.members = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMembers.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMembers.rejected, handleRejected)
      .addCase(fetchMyRole.fulfilled, (state, action) => {
        state.myRole = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMyRole.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMyRole.rejected, handleRejected)
      .addCase(deleteMember.rejected, handleRejected)
      .addCase(updateMemberRole.rejected, handleRejected);
  },
});

export const { setLoading, resetProject, clearErrors } = projectSlice.actions;

export default projectSlice.reducer;
