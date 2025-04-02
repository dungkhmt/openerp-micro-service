import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { EventService } from "../../../services/api/event.service";
import { clearCache } from "../tasks";

export const handleRejected = (state, action) => {
  state.errors.push(action.error);
  state.fetchLoading = false;
};

export const fetchEvents = createAsyncThunk(
  "project/fetchEvents",
  async (projectId, { rejectWithValue }) => {
    try {
      const events = await EventService.getEvents(projectId);
      return events;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const createEvent = createAsyncThunk(
  "project/createEvent",
  async (data, { dispatch, rejectWithValue }) => {
    try {
      const res = await EventService.createEvent(data);
      await dispatch(fetchEvents(data.projectId));
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const updateEvent = createAsyncThunk(
  "project/updateEvent",
  async ({ eventId, data, projectId }, { dispatch, rejectWithValue }) => {
    try {
      const res = await EventService.updateEvent(eventId, data);
      await dispatch(fetchEvents(projectId));
      dispatch(clearCache());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

export const deleteEvent = createAsyncThunk(
  "project/deleteEvent",
  async ({ projectId, eventId }, { dispatch, rejectWithValue }) => {
    try {
      const res = await EventService.deleteEvent(eventId);
      await dispatch(fetchEvents(projectId));
      dispatch(clearCache());
      return res;
    } catch (error) {
      return rejectWithValue(error.response || error.message);
    }
  }
);

const initialState = {
  events: [],
  fetchLoading: false,
  errors: [],
};

export const eventsSlice = createSlice({
  name: "events",
  initialState,
  reducers: {
    setLoading: (state, action) => {
      state.fetchLoading = action.payload;
    },
    clearErrors: (state) => {
      state.errors = [];
    },
    resetEvents: (state) => {
      state.events = initialState.events;
      state.fetchLoading = true;
      state.errors = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchEvents.pending, (state) => {
        state.fetchLoading = true;
      })
      .addCase(fetchEvents.fulfilled, (state, action) => {
        state.events = action.payload;
        state.fetchLoading = false;
      })
      .addCase(fetchEvents.rejected, handleRejected);
  },
});

export const { clearErrors, setLoading, resetEvents } = eventsSlice.actions;

export default eventsSlice.reducer;
