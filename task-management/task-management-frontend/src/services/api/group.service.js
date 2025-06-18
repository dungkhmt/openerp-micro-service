import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getGroupsByMe: `/groups`,
  getGroupById: (id) => `/groups/${id}`,
  createGroup: `/groups`,
  updateGroup: (id) => `/groups/${id}`,
  getUsersByGroupId: (id) => `/groups/${id}/users`,
  addUserToGroup: (id) => `/groups/${id}/users`,
  removeUserFromGroup: (id, userId) => `/groups/${id}/users/${userId}`,
};

const GroupService = {
  getGroupsByMe: async (cb) => {
    try {
      const response = await privateClient.get(endPoints.getGroupsByMe);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getGroupById: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getGroupById(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  createGroup: async (data, cb) => {
    try {
      const response = await privateClient.post(endPoints.createGroup, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateGroup: async (id, data, cb) => {
    try {
      const response = await privateClient.put(endPoints.updateGroup(id), data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getUsersByGroupId: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getUsersByGroupId(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  addUserToGroup: async (id, data, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.addUserToGroup(id),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  removeUserFromGroup: async (id, userId, cb) => {
    try {
      const response = await privateClient.delete(
        endPoints.removeUserFromGroup(id, userId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { GroupService };
