import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { UserService } from "../../services/api/user.service";


export const fetchMyProfile = createAsyncThunk(
  "fetchMyProfile",
  async () => {
    const user = await UserService.getMyProfile();
    return user;
  }
);

export const updateMyProfile = createAsyncThunk(
  "updateMyProfile",
  async (data, { dispatch }) => {
    const user = await UserService.updateMyProfile(data);
    await dispatch(fetchMyProfile());
    return user;
  }
);

export const fetchMySkills = createAsyncThunk(
  "fetchUserSkills",
  async () => {
    const userSkills = await UserService.getMySkills();
    return userSkills;
  }
);

export const updateMySkills = createAsyncThunk(
  "updateUserSkills",
  async (data, { dispatch }) => {
    const userSkills = await UserService.updateMySkills(data);
    await dispatch(fetchMySkills());
    return userSkills;
  }
);

const initialState = {
  user: null,
  userSkills: [],
  fetchLoading: false,
  errors: [],
};

export const myProfileSlice = createSlice({
  name: "myProfile",
  initialState,
  reducers: {
    resetMyProfile: (state) => {
      // eslint-disable-next-line no-unused-vars
      state = { ...initialState };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMySkills.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMySkills.fulfilled, (state, action) => {
        state.userSkills = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMySkills.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      })
      .addCase(fetchMyProfile.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMyProfile.fulfilled, (state, action) => {
        state.user = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMyProfile.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const { resetMyProfile } = myProfileSlice.actions;

export default myProfileSlice.reducer;
