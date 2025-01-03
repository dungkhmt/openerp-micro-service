import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { EventService } from "../../../services/api/event.service";

export const handleRejected = (state, action) => {
  state.errors.push(action.error);
  state.fetchLoading = false;
};

export const fetchEvents = createAsyncThunk(
  "project/fetchEvents",
  async (projectId) => {
    const events = await EventService.getEvents(projectId);
    return events;
  }
);

export const createEvent = createAsyncThunk(
  "project/createEvent",
  async (data, { dispatch }) => {
    const res = await EventService.createEvent(data);
    await dispatch(fetchEvents(data.projectId));
    return res;
  }
);

export const updateEvent = createAsyncThunk(
  "project/updateEvent",
  async ({ eventId, data, projectId }, { dispatch }) => {
    const res = await EventService.updateEvent(eventId, data);
    await dispatch(fetchEvents(projectId));
    return res;
  }
);

export const deleteEvent = createAsyncThunk(
  "project/deleteEvent",
  async ({ projectId, eventId }, { dispatch }) => {
    const res = await EventService.deleteEvent(eventId);
    await dispatch(fetchEvents(projectId));
    return res;
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

export const { setLoading, resetEvents } = eventsSlice.actions;

export default eventsSlice.reducer;
