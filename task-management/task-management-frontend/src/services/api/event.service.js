import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getEvents: (projectId) => `/events?projectId=${projectId}`,
  getEvent: (eventId) => `/events/${eventId}`,
  createEvent: `/events`,
  updateEvent: (eventId) => `/events/${eventId}`,
  getEventUsers: (eventId) => `/event-users?eventId=${eventId}`,
  addEventUser: `/event-users`,
  deleteEvent: (eventId) => `/events/${eventId}`,
};

const EventService = {
  getEvents: async (projectId, cb) => {
    try {
      const response = await privateClient.get(endPoints.getEvents(projectId));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getEvent: async (eventId, cb) => {
    try {
      const response = await privateClient.get(endPoints.getEvent(eventId));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  createEvent: async (data, cb) => {
    try {
      const response = await privateClient.post(endPoints.createEvent, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateEvent: async (eventId, data, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateEvent(eventId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getEventUsers: async (eventId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getEventUsers(eventId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  deleteEvent: async (eventId, cb) => {
    try {
      const response = await privateClient.delete(
        endPoints.deleteEvent(eventId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { EventService };
