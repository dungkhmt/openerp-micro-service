import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { MeetingPlanService } from "../../services/api/meeting-plan.service";
import { clearCache } from "../created-meetings";
import { fetchAllSessionRegistrations } from "./meeting-sessions";

export const handleRejected = (state, action) => {
  state.errors.push(action.payload || action.error);
  state.fetchLoading = false;
};

export const fetchMeetingPlan = createAsyncThunk(
  "meetingPlans/fetchOne",
  async (meetingPlanId, { rejectWithValue }) => {
    try {
      return await MeetingPlanService.getMeetingPlan(meetingPlanId);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchMeetingPlanMembers = createAsyncThunk(
  "meetingPlans/fetchMembers",
  async (meetingPlanId, { rejectWithValue }) => {
    try {
      return await MeetingPlanService.getMeetingPlanUsers(meetingPlanId);
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const updateMeetingPlan = createAsyncThunk(
  "meetingPlans/update",
  async ({ meetingPlanId, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.updateMeetingPlan(
        meetingPlanId,
        data
      );
      dispatch(fetchMeetingPlan(meetingPlanId));
      dispatch(clearCache());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const updateStatus = createAsyncThunk(
  "meetingPlans/updateStatus",
  async ({ meetingPlanId, statusId }, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.updateStatus(
        meetingPlanId,
        statusId
      );
      dispatch(fetchMeetingPlan(meetingPlanId));
      dispatch(clearCache());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const addMemberToMeetingPlan = createAsyncThunk(
  "meetingPlans/addMember",
  async ({ meetingPlanId, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.addUserToMeetingPlan(
        meetingPlanId,
        data
      );
      dispatch(fetchMeetingPlanMembers(meetingPlanId));
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const removeMemberFromMeetingPlan = createAsyncThunk(
  "meetingPlans/removeMember",
  async ({ meetingPlanId, userId }, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.removeUserFromMeetingPlan(
        meetingPlanId,
        userId
      );
      dispatch(fetchMeetingPlanMembers(meetingPlanId));
      dispatch(fetchAllSessionRegistrations(meetingPlanId));
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchMyAssignment = createAsyncThunk(
  "meetingPlans/fetchMyAssignment",
  async (meetingPlanId, { rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.getMyAssignment(meetingPlanId);
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const fetchMemberAssignments = createAsyncThunk(
  "meetingPlans/fetchMemberAssignments",
  async (meetingPlanId, { rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.getMemberAssignments(meetingPlanId);
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const updateMemberAssignments = createAsyncThunk(
  "meetingPlans/updateMemberAssignments",
  async ({ meetingPlanId, data }, { dispatch, rejectWithValue }) => {
    try {
      const res = await MeetingPlanService.updateMemberAssignments(
        meetingPlanId,
        data
      );
      dispatch(fetchMemberAssignments(meetingPlanId));
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

const initialState = {
  currentPlan: null,
  members: [],
  assignments: [],
  myAssignment: null,
  isCreator: false,
  fetchLoading: false,
  errors: [],
};

export const meetingPlansSlice = createSlice({
  name: "meetingPlan",
  initialState,
  reducers: {
    clearErrors: (state) => {
      state.errors = [];
    },
    setIsCreator: (state, action) => {
      state.isCreator = action.payload;
    },
    resetMeetingPlans: (state) => {
      // eslint-disable-next-line no-unused-vars
      state = { ...initialState };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMeetingPlan.fulfilled, (state, action) => {
        state.currentPlan = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMeetingPlan.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMeetingPlan.rejected, handleRejected)
      .addCase(fetchMeetingPlanMembers.fulfilled, (state, action) => {
        state.members = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMeetingPlanMembers.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMeetingPlanMembers.rejected, handleRejected)

      .addCase(fetchMemberAssignments.fulfilled, (state, action) => {
        state.assignments = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMemberAssignments.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMemberAssignments.rejected, handleRejected)

      .addCase(fetchMyAssignment.fulfilled, (state, action) => {
        state.myAssignment = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchMyAssignment.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchMyAssignment.rejected, handleRejected)

      .addCase(updateMemberAssignments.rejected, handleRejected)

      .addCase(addMemberToMeetingPlan.rejected, handleRejected)

      .addCase(removeMemberFromMeetingPlan.rejected, handleRejected)

      .addCase(updateMeetingPlan.rejected, handleRejected);
  },
});

export const { clearErrors, setIsCreator, resetMeetingPlans } =
  meetingPlansSlice.actions;

export default meetingPlansSlice.reducer;
