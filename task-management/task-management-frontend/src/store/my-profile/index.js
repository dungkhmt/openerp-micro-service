import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { UserService } from "../../services/api/user.service";


export const fetchUser = createAsyncThunk(
  "fetchUser",
  async () => {
    const user = await UserService.getUser();
    return user;
  }
);

export const updateUser = createAsyncThunk(
  "updateUser",
  async (data, { dispatch }) => {
    const user = await UserService.updateUser(data);
    await dispatch(fetchUser());
    return user;
  }
);

export const fetchUserSkills = createAsyncThunk(
  "fetchUserSkills",
  async () => {
    const userSkills = await UserService.getUserSkills();
    return userSkills;
  }
);

export const updateUserSkills = createAsyncThunk(
  "updateUserSkills",
  async (data, { dispatch }) => {
    const userSkills = await UserService.updateUserSkills(data);
    await dispatch(fetchUserSkills());
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
      .addCase(fetchUserSkills.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchUserSkills.fulfilled, (state, action) => {
        state.userSkills = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchUserSkills.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      })
      .addCase(fetchUser.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchUser.fulfilled, (state, action) => {
        state.user = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchUser.rejected, (state, action) => {
        state.errors.push(action.error);
        state.fetchLoading = false;
      });
  },
});

export const { resetMyProfile } = myProfileSlice.actions;

export default myProfileSlice.reducer;
