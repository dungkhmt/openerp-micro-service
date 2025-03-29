import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { MeetingSessionService } from "../../../services/api/meeting-session.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchMeetingSessions = createAsyncThunk(
  "meetingSessions/fetchAll",
  async (meetingPlanId, { rejectWithValue }) => {
    try {
      return await MeetingSessionService.getMeetingSessions(meetingPlanId);
    } catch (err) {
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);

export const createMeetingSessions = createAsyncThunk(
  "meetingSessions/create",
  async ({ meetingPlanId, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingSessionService.createMeetingSessions(
        meetingPlanId,
        data
      );
      dispatch(fetchMeetingSessions(meetingPlanId));
      return res;
    } catch (err) {
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);

export const deleteMeetingSession = createAsyncThunk(
  "meetingSessions/delete",
  async (
    { meetingPlanId, meetingSessionId },
    { dispatch, rejectWithValue }
  ) => {
    try {
      const res = await MeetingSessionService.deleteMeetingSession(
        meetingPlanId,
        meetingSessionId
      );
      dispatch(fetchMeetingSessions(meetingPlanId));
      dispatch(fetchAllSessionRegistrations(meetingPlanId));
      return res;
    } catch (err) {
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);

export const fetchMyMeetingSessions = createAsyncThunk(
  "meetingSessions/fetchMy",
  async (meetingPlanId, { rejectWithValue }) => {
    try {
      return await MeetingSessionService.getSessionsByMe(meetingPlanId);
    } catch (err) {
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);

export const updateMyMeetingSessions = createAsyncThunk(
  "meetingSessions/updateMy",
  async ({ meetingPlanId, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingSessionService.updateMyMeetingSessions(
        meetingPlanId,
        data
      );
      dispatch(fetchMyMeetingSessions(meetingPlanId));
      return res;
    } catch (err) {
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);

export const fetchAllSessionRegistrations = createAsyncThunk(
  "meetingSessions/fetchAllSessionRegistrations",
  async (meetingPlanId, { rejectWithValue }) => {
    try {
      return await MeetingSessionService.getAllSessionRegistrations(
        meetingPlanId
      );
    } catch (err) {
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);

const initialState = {
  sessions: [],
  myRegistrations: [],
  memberRegistrations: [],
  fetchLoading: false,
  errors: [],
};

const meetingSessionsSlice = createSlice({
  name: "meetingSessions",
  initialState,
  reducers: {
    clearErrors: (state) => {
      state.errors = [];
    },
    resetMeetingSessions: (state) => {
      // eslint-disable-next-line no-unused-vars
      state = { ...initialState };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMeetingSessions.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMeetingSessions.fulfilled, (state, action) => {
        state.fetchLoading = false;
        state.sessions = action.payload;
      })
      .addCase(fetchMeetingSessions.rejected, handleRejected)
      .addCase(fetchMyMeetingSessions.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMyMeetingSessions.fulfilled, (state, action) => {
        state.fetchLoading = false;
        state.myRegistrations = action.payload;
      })
      .addCase(fetchMyMeetingSessions.rejected, handleRejected)
      .addCase(fetchAllSessionRegistrations.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchAllSessionRegistrations.fulfilled, (state, action) => {
        state.fetchLoading = false;
        state.memberRegistrations = action.payload;
      })
      .addCase(fetchAllSessionRegistrations.rejected, handleRejected)
      .addCase(updateMyMeetingSessions.rejected, handleRejected)
      .addCase(createMeetingSessions.rejected, handleRejected)
      .addCase(deleteMeetingSession.rejected, handleRejected);
  },
});

export const { clearErrors, resetMeetingSessions } =
  meetingSessionsSlice.actions;

export default meetingSessionsSlice.reducer;
