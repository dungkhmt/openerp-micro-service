import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getAll: "/users",
  sync: "/users/sync",
  getMyProfile: "/users/me",
  updateMyProfile: "/users/me",
  getAssignedTaskCreator: "/users/assigned-task-creator",
  getMeCreatedAssignee: "/users/assigned-task-assignee",
  getMySkills: "/user-skills/me",
  updateMySkills: "/user-skills/me",
};

export const UserService = {
  async getAll(params) {
    const res = await privateClient.get(endPoints.getAll, {
      params,
    });
    return res.data;
  },
  async sync() {
    try {
      const res = await privateClient.get(endPoints.sync);
      return res.data;
    } catch (e) {
      console.error("Sync user error");
    }
  },
  async getAllAssignedTaskCreator() {
    const res = await privateClient.get(endPoints.getAssignedTaskCreator);
    return res.data;
  },
  async getAllMeCreatedAssignee() {
    const res = await privateClient.get(endPoints.getMeCreatedAssignee);
    return res.data;
  },
  async getMySkills(cb) {
    try {
      const response = await privateClient.get(endPoints.getMySkills);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  async getMyProfile(cb) {
    try {
      const response = await privateClient.get(endPoints.getMyProfile);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  async updateMyProfile(data, cb) {
    try {
      const response = await privateClient.put(endPoints.updateMyProfile, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  async updateMySkills(data, cb) {
    try {
      const response = await privateClient.put(endPoints.updateMySkills, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};
