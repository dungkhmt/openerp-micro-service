import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getCreatedMeetingPlans: `/meeting-plans/creator/me`,
  getJoinedMeetingPlans: `/meeting-plans/member/me`,
  getMeetingPlan: (meetingPlanId) => `/meeting-plans/${meetingPlanId}`,
  createMeetingPlan: `/meeting-plans`,
  updateMeetingPlan: (meetingPlanId) => `/meeting-plans/${meetingPlanId}`,
  updateStatus: (meetingPlanId) => `/meeting-plans/${meetingPlanId}/status`,
  getMeetingPlanUsers: (meetingPlanId) =>
    `/meeting-plans/${meetingPlanId}/users`,
  addUserToMeetingPlan: (meetingPlanId) =>
    `/meeting-plans/${meetingPlanId}/users`,
  removeUserFromMeetingPlan: (meetingPlanId, userId) =>
    `/meeting-plans/${meetingPlanId}/users/${userId}`,
  getMyAssignment: (meetingPlanId) =>
    `/meeting-plans/${meetingPlanId}/users/assignments/me`,
  getMemberAssignments: (meetingPlanId) =>
    `/meeting-plans/${meetingPlanId}/users/assignments`,
  updateMemberAssignments: (meetingPlanId) =>
    `/meeting-plans/${meetingPlanId}/users/assignments`,
  autoAssignMembers: (meetingPlanId) =>
    `/meeting-plans/${meetingPlanId}/users/auto-assign`,
};

const MeetingPlanService = {
  getCreatedMeetingPlans: async (filters, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getCreatedMeetingPlans,
        {
          params: filters,
        }
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getJoinedMeetingPlans: async (filters, cb) => {
    try {
      console.log(filters);
      const response = await privateClient.get(
        endPoints.getJoinedMeetingPlans,
        {
          params: filters,
        }
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getMeetingPlan: async (meetingPlanId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getMeetingPlan(meetingPlanId)
      );

      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  createMeetingPlan: async (data, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.createMeetingPlan,
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateMeetingPlan: async (meetingPlanId, data, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateMeetingPlan(meetingPlanId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateStatus: async (meetingPlanId, statusId, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateStatus(meetingPlanId),
        statusId
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },

  getMeetingPlanUsers: async (meetingPlanId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getMeetingPlanUsers(meetingPlanId)
      );

      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  addUserToMeetingPlan: async (meetingPlanId, data, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.addUserToMeetingPlan(meetingPlanId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  removeUserFromMeetingPlan: async (meetingPlanId, userId, cb) => {
    try {
      const response = await privateClient.delete(
        endPoints.removeUserFromMeetingPlan(meetingPlanId, userId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getMyAssignment: async (meetingPlanId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getMyAssignment(meetingPlanId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getMemberAssignments: async (meetingPlanId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getMemberAssignments(meetingPlanId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateMemberAssignments: async (meetingPlanId, data, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateMemberAssignments(meetingPlanId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  autoAssignMembers: async (meetingPlanId, data, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.autoAssignMembers(meetingPlanId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { MeetingPlanService };
