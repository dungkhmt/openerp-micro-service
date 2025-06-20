import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { GroupService } from "../../../services/api/group.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchGroupsByMe = createAsyncThunk(
  "userGroup/fetchGroupsByMe",
  async (_, { rejectWithValue }) => {
    try {
      return await GroupService.getGroupsByMe();
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchGroupById = createAsyncThunk(
  "userGroup/fetchGroupById",
  async (id, { rejectWithValue }) => {
    try {
      return await GroupService.getGroupById(id);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const createGroup = createAsyncThunk(
  "userGroup/createGroup",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const res = await GroupService.createGroup(data);
      dispatch(fetchGroupsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const updateGroup = createAsyncThunk(
  "userGroup/updateGroup",
  async ({ id, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await GroupService.updateGroup(id, data);
      dispatch(fetchGroupById(id));
      dispatch(fetchGroupsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchUsersByGroupId = createAsyncThunk(
  "userGroup/fetchUsersByGroupId",
  async (id, { rejectWithValue }) => {
    try {
      return await GroupService.getUsersByGroupId(id);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const addUserToGroup = createAsyncThunk(
  "userGroup/addUserToGroup",
  async ({ id, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await GroupService.addUserToGroup(id, data);
      dispatch(fetchUsersByGroupId(id));
      dispatch(fetchGroupsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const removeUserFromGroup = createAsyncThunk(
  "userGroup/removeUserFromGroup",
  async ({ id, userId }, { dispatch, rejectWithValue }) => {
    try {
      const res = await GroupService.removeUserFromGroup(id, userId);
      dispatch(fetchUsersByGroupId(id));
      dispatch(fetchGroupsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

const initialState = {
  groups: [],
  currentGroup: null,
  groupMembers: [],
  fetchLoading: false,
  errors: [],
};

export const userGroupSlice = createSlice({
  name: "userGroup",
  initialState,
  reducers: {
    clearErrors: (state) => {
      state.errors = [];
    },
    resetUserGroup: (state) => {
      state.groups = [];
      state.currentGroup = null;
      state.groupMembers = [];
      state.fetchLoading = false;
      state.errors = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchGroupsByMe.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchGroupsByMe.fulfilled, (state, action) => {
        state.groups = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchGroupsByMe.rejected, handleRejected)
      .addCase(fetchGroupById.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchGroupById.fulfilled, (state, action) => {
        state.currentGroup = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchGroupById.rejected, handleRejected)
      .addCase(fetchUsersByGroupId.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchUsersByGroupId.fulfilled, (state, action) => {
        state.groupMembers = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchUsersByGroupId.rejected, handleRejected)
      .addCase(createGroup.rejected, handleRejected)
      .addCase(updateGroup.rejected, handleRejected)
      .addCase(addUserToGroup.rejected, handleRejected)
      .addCase(removeUserFromGroup.rejected, handleRejected);
  },
});

export const { clearErrors, resetUserGroup } = userGroupSlice.actions;

export default userGroupSlice.reducer;
