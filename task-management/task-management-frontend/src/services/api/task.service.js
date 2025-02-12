import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getTasks: (projectId) => `/tasks?projectId=${projectId}`,
  getTask: (id) => `/tasks/${id}`,
  createTask: `/tasks`,
  updateTask: (id) => `/tasks/${id}`,
  getAssignedTasks: `/tasks/assigned-me`,
  getCreatedTasks: `/tasks/created-by-me`,
  getLogs: (id) => `/tasks/${id}/logs`,
  tasksGantt: "/tasks/gantt",
};

const TaskService = {
  getTask: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getTask(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getTasks: async (projectId, filter, cb) => {
    try {
      const response = await privateClient.get(endPoints.getTasks(projectId), {
        params: filter,
      });
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  createTask: async (data, cb) => {
    try {
      const response = await privateClient.post(endPoints.createTask, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateTask: async (id, data, cb) => {
    try {
      const response = await privateClient.put(endPoints.updateTask(id), data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getAssignedTasks: async (filter, cb) => {
    try {
      const response = await privateClient.get(endPoints.getAssignedTasks, {
        params: filter,
      });
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getLogs: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getLogs(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getTasksGantt: async (id, from, to, q, cb) => {
    try {
      const response = await privateClient.get(endPoints.tasksGantt, {
        params: {
          projectId: id,
          from,
          to,
          q,
        },
      });
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getCreatedTasks: async (filter, cb) => {
    try {
      const response = await privateClient.get(endPoints.getCreatedTasks, {
        params: filter,
      });
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { TaskService };
