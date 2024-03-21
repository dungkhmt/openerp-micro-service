import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getProjects: "/projects",
  getProject: (id) => `/projects/${id}`,
  createProject: "/projects",
  updateProject: (id) => `/projects/${id}`,
  getMembers: (id) => `/project-members/${id}`,
  addMember: `/project-members`,
};

const ProjectService = {
  getProjects: async (queries, cb) => {
    try {
      const response = await privateClient.get(endPoints.getProjects, {
        params: queries,
        paramsSerializer: (params) => {
          return Object.entries(params)
            .map(([key, value]) => `${key}=${value}`)
            .join("&");
        },
      });
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getProject: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getProject(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  createProject: async (data, cb) => {
    try {
      const response = await privateClient.post(endPoints.createProject, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateProject: async (id, data, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateProject(id),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getMembers: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getMembers(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  addMember: async (data, cb) => {
    try {
      const response = await privateClient.post(endPoints.addMember, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { ProjectService };
