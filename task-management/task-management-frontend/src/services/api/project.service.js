import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getProjects: "/projects",
  getProjectsCode: "/projects/code",
  getProjectsForUser: (userId) => `/projects/user-projects/${userId}`,
  getProject: (id) => `/projects/${id}`,
  createProject: "/projects",
  updateProject: (id) => `/projects/${id}`,
  getMembers: (id) => `/project-members/${id}`,
  addMember: `/project-members`,
  deleteMember: (projectId, memberId, roleId) => `/project-members/${projectId}/${memberId}/${roleId}`,
  updateMemberRole: `/project-members`,
  getMyRole: (id) => `/project-members/${id}/my-role`,
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
  getProjectsCode: async (cb) => {
    try {
      const response = await privateClient.get(endPoints.getProjectsCode);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getProjectsForUser: async (id, queries, cb) => {
    try {
      const response = await privateClient.get(endPoints.getProjectsForUser(id), {
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
  deleteMember: async (projectId, memberId, roleId, cb) => {
    try {
      const response = await privateClient.delete(endPoints.deleteMember(projectId, memberId, roleId));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateMemberRole: async (data, cb) => {
    try {
      const response = await privateClient.put(endPoints.updateMemberRole, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getMyRole: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getMyRole(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { ProjectService };
