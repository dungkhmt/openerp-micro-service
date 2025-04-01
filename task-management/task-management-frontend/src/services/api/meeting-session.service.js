import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getMeetingSessions: (planId) => `/meeting-plans/${planId}/sessions`,
  createMeetingSessions: (planId) => `/meeting-plans/${planId}/sessions`,
  deleteMeetingSession: (planId, sessionId) =>
    `/meeting-plans/${planId}/sessions/${sessionId}`,
  getSessionsByMe: (planId) => `/meeting-plans/${planId}/session-user/me`,
  updateMyMeetingSessions: (planId) => `/meeting-plans/${planId}/session-user`,
  getAllSessionRegistrations: (planId) =>
    `/meeting-plans/${planId}/session-user`,
};

const MeetingSessionService = {
  getMeetingSessions: async (planId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getMeetingSessions(planId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  createMeetingSessions: async (planId, data, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.createMeetingSessions(planId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  deleteMeetingSession: async (planId, sessionId, cb) => {
    try {
      const response = await privateClient.delete(
        endPoints.deleteMeetingSession(planId, sessionId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getSessionsByMe: async (planId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getSessionsByMe(planId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateMyMeetingSessions: async (planId, data, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateMyMeetingSessions(planId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getAllSessionRegistrations: async (planId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getAllSessionRegistrations(planId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { MeetingSessionService };
