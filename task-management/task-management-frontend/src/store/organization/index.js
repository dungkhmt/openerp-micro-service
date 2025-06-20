import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { OrganizationService } from "../../services/api/organization.service";
import { fetchOrganizationData, resetOrganizationData } from "../utils/organizationContext";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchOrganizationsByMe = createAsyncThunk(
  "organization/fetchOrganizationsByMe",
  async (_, { rejectWithValue }) => {
    try {
      return await OrganizationService.getOrganizationsByMe();
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchOrganizationById = createAsyncThunk(
  "organization/fetchOrganizationById",
  async (id, { rejectWithValue }) => {
    try {
      return await OrganizationService.getOrganizationById(id);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchLastOrganizationByMe = createAsyncThunk(
  "organization/fetchLastOrganizationByMe",
  async (_, { rejectWithValue }) => {
    try {
      return await OrganizationService.getLastOrganizationByMe();
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const switchOrganization = createAsyncThunk(
  "organization/switchOrganization",
  async (id, { dispatch, rejectWithValue }) => {
    try {
      const res = await dispatch(fetchOrganizationById(id));
      await resetOrganizationData(dispatch);
      await fetchOrganizationData(dispatch, res.payload.id);
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const createOrganization = createAsyncThunk(
  "organization/createOrganization",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const res = await OrganizationService.createOrganization(data);
      dispatch(fetchOrganizationsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const updateOrganization = createAsyncThunk(
  "organization/updateOrganization",
  async ({ id, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await OrganizationService.updateOrganization(id, data);
      dispatch(fetchOrganizationById(id));
      dispatch(fetchOrganizationsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchUsersByOrganizationId = createAsyncThunk(
  "organization/fetchUsersByOrganizationId",
  async (id, { rejectWithValue }) => {
    try {
      return await OrganizationService.getUsersByOrganizationId(id);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const removeUserFromOrganization = createAsyncThunk(
  "organization/removeUserFromOrganization",
  async ({ id, userId }, { dispatch, rejectWithValue }) => {
    try {
      const res = await OrganizationService.removeUserFromOrganization(
        id,
        userId
      );
      dispatch(fetchUsersByOrganizationId(id));
      dispatch(fetchOrganizationsByMe());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

const initialState = {
  organizations: [],
  currentOrganization: null,
  organizationMembers: [],
  fetchLoading: false,
  errors: [],
};

export const organizationSlice = createSlice({
  name: "organization",
  initialState,
  reducers: {
    clearErrors: (state) => {
      state.errors = [];
    },
    resetOrganization: (state) => {
      // eslint-disable-next-line no-unused-vars
      state = { ...initialState };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchOrganizationsByMe.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchOrganizationsByMe.fulfilled, (state, action) => {
        state.organizations = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchOrganizationsByMe.rejected, handleRejected)
      .addCase(fetchOrganizationById.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchOrganizationById.fulfilled, (state, action) => {
        state.currentOrganization = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchOrganizationById.rejected, handleRejected)
      .addCase(fetchLastOrganizationByMe.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchLastOrganizationByMe.fulfilled, (state, action) => {
        state.currentOrganization = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchLastOrganizationByMe.rejected, handleRejected)
      .addCase(fetchUsersByOrganizationId.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchUsersByOrganizationId.fulfilled, (state, action) => {
        state.organizationMembers = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchUsersByOrganizationId.rejected, handleRejected)
      .addCase(createOrganization.rejected, handleRejected)
      .addCase(updateOrganization.rejected, handleRejected)
      .addCase(removeUserFromOrganization.rejected, handleRejected);
  },
});

export const { clearErrors, resetOrganization } = organizationSlice.actions;

export default organizationSlice.reducer;
