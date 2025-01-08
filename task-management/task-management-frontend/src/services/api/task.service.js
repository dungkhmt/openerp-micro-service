import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getTasks: (projectId) => `/tasks?projectId=${projectId}`,
  getTask: (id) => `/tasks/${id}`,
  createTask: `/tasks`,
  updateTask: (id) => `/tasks/${id}`,
  getAssignedTasks: `/tasks/assigned-me`,
  getAssignedTasksForUser: (userId) => `/tasks/assigned-user/${userId}`,
  getMemberTasks:(projectId, assigneeId) => `/tasks/member-tasks?projectId=${projectId}&assigneeId=${assigneeId}`,
  getCreatedTasks: `/tasks/created-by-me`,
  getLogs: (id) => `/tasks/${id}/logs`,
  getTaskSkills: (id) => `/task-skills/${id}`,
  addTaskSkills: (id) => `/task-skills/${id}`,
  updateTaskSkills: (id) => `/task-skills/${id}`,
  getEventTasks: (eventId) => `tasks/event-tasks?eventId=${eventId}`,
  addExistingTasksToEvent: (eventId) => `tasks/event-tasks?eventId=${eventId}`,
  getTasksWithoutEvent: (projectId) => `tasks/without-event?projectId=${projectId}`,
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
      console.log(data);
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
  getAssignedTasksForUser: async (id, filter, cb) => {
    try {
      const response = await privateClient.get(endPoints.getAssignedTasksForUser(id), {
        params: filter,
      });
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getMemberTasks: async (projectId, assigneeId, cb) => {
    try {
      const response = await privateClient.get(endPoints.getMemberTasks(projectId, assigneeId));
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
  getTaskSkills: async (id, cb) => {
    try {
      const response = await privateClient.get(endPoints.getTaskSkills(id));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  addTaskSkills: async (id, data, cb) => {
    try {
      const response = await privateClient.post(endPoints.addTaskSkills(id), data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateTaskSkills: async (id, data, cb) => {
    try {
      const response = await privateClient.put(endPoints.updateTaskSkills(id), data);
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
  getEventTasks: async (eventId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getEventTasks(eventId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  addExistingTasksToEvent: async (eventId, data, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.addExistingTasksToEvent(eventId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getTasksWithoutEvent: async (projectId, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getTasksWithoutEvent(projectId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { TaskService };
