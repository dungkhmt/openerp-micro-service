import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const initialState = {
  hubId: null,
  name: null,
  loading: false,
  error: null
};

export const fetchHubIdByUsername = createAsyncThunk(
  'hub/fetchHubIdByUsername',
  async (username) => {
    try {
      const response = await axios.get(`/api/smdeli/hub/username/${username}`);
      return {
        hubId: response.data.hubId,
        name: response.data.name
      };
    } catch (error) {
      console.error('Error fetching hub ID:', error);
      throw error;
    }
  }
);

const hubSlice = createSlice({
  name: 'hub',
  initialState,
  reducers: {
    clearHubInfo: (state) => {
      state.hubId = null;
      state.name = null;
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchHubIdByUsername.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchHubIdByUsername.fulfilled, (state, action) => {
        state.loading = false;
        state.hubId = action.payload.hubId;
        state.name = action.payload.name;
      })
      .addCase(fetchHubIdByUsername.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch hub information';
      });
  }
});

export const { clearHubInfo } = hubSlice.actions;
export default hubSlice.reducer; 