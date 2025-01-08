import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getAll: "/users",
  sync: "/",
  getUser: "/users/me",
  updateUser: "/users/me",
  getAssignedTaskCreator: "/users/assigned-task-creator",
  getMeCreatedAssignee: "/users/assigned-task-assignee",
  getUserSkills: "/user-skills",
  updateUserSkills: "/user-skills",
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
  async getUserSkills(cb) {
    try {
      const response = await privateClient.get(endPoints.getUserSkills);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  async getUser(cb) {
    try {
      const response = await privateClient.get(endPoints.getUser);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  async updateUser(data, cb) {
    try {
      const response = await privateClient.put(endPoints.updateUser, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  async updateUserSkills(data, cb) {
    try {
      const response = await privateClient.put(endPoints.updateUserSkills, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};
