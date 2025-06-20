import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { OrganizationService } from "../../../services/api/organization.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchPendingInvitationsByOrgId = createAsyncThunk(
  "invitation/fetchPendingInvitationsByOrgId",
  async (id, { rejectWithValue }) => {
    try {
      return await OrganizationService.getPendingInvitationsByOrgId(id);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchPendingInvitationsByMe = createAsyncThunk(
  "invitation/fetchPendingInvitationsByMe",
  async (_, { rejectWithValue }) => {
    try {
      return await OrganizationService.getPendingInvitationsByMe();
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const inviteUsers = createAsyncThunk(
  "invitation/inviteUsers",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const res = await OrganizationService.inviteUsers(data);
      dispatch(fetchPendingInvitationsByOrgId(data.organizationId));
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const validateToken = createAsyncThunk(
  "invitation/validateToken",
  async (token, { rejectWithValue }) => {
    try {
      return await OrganizationService.validateToken(token);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const acceptInvitation = createAsyncThunk(
  "invitation/acceptInvitation",
  async (token, { dispatch, rejectWithValue }) => {
    try {
      const res = await OrganizationService.acceptInvitation(token);
      dispatch(fetchPendingInvitationsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const declineInvitation = createAsyncThunk(
  "invitation/declineInvitation",
  async (token, { dispatch, rejectWithValue }) => {
    try {
      const res = await OrganizationService.declineInvitation(token);
      dispatch(fetchPendingInvitationsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

const initialState = {
  orgInvitations: [],
  myInvitations: [],
  currentInvitation: null,
  fetchLoading: false,
  errors: [],
};

export const invitationSlice = createSlice({
  name: "invitation",
  initialState,
  reducers: {
    clearErrors: (state) => {
      state.errors = [];
    },
    resetInvitation: (state) => {
      state.orgInvitations = [];
      state.myInvitations = [];
      state.currentInvitation = null;
      state.fetchLoading = false;
      state.errors = [];
    },
  },
  extraReducers: (builder) => {
    builder

      .addCase(fetchPendingInvitationsByOrgId.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchPendingInvitationsByOrgId.fulfilled, (state, action) => {
        state.orgInvitations = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchPendingInvitationsByOrgId.rejected, handleRejected)
      .addCase(fetchPendingInvitationsByMe.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchPendingInvitationsByMe.fulfilled, (state, action) => {
        state.myInvitations = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchPendingInvitationsByMe.rejected, handleRejected)
      .addCase(validateToken.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(validateToken.fulfilled, (state, action) => {
        state.currentInvitation = action.payload;
        state.fetchLoading = false;
      })
      .addCase(validateToken.rejected, handleRejected)
      .addCase(inviteUsers.rejected, handleRejected)
      .addCase(acceptInvitation.rejected, handleRejected)
      .addCase(declineInvitation.rejected, handleRejected);
  },
});

export const { clearErrors, resetInvitation } = invitationSlice.actions;

export default invitationSlice.reducer;
