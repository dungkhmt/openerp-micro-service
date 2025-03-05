import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getPriorities: "/task-priorities",
  createPriority: "/task-priorities",
  deletePriority: (priorityId) => `/task-priorities/${priorityId}`,
};

export const PriorityService = {
  getPriorities: async (cb) => {
    try {
      const res = await privateClient.get(endPoints.getPriorities);
      if (res?.data && isFunction(cb)) {
        cb(null, res.data);
      }
      return res?.data;
    } catch (error) {
      if (isFunction(cb)) {
        cb(error);
      } else throw error;
    }
  },
  createPriority: async (data, cb) => {
    try {
      const res = await privateClient.post(endPoints.createPriority, data);
      if (res?.data && isFunction(cb)) {
        cb(null, res.data);
      }
      return res?.data;
    } catch (error) {
      if (isFunction(cb)) {
        cb(error);
      } else throw error;
    }
  },
  deletePriority: async (priorityId, cb) => {
    try {
      const res = await privateClient.delete(endPoints.deletePriority(priorityId));
      if (res?.data && isFunction(cb)) {
        cb(null, res.data);
      }
      return res?.data;
    } catch (error) {
      if (isFunction(cb)) {
        cb(error);
      } else throw error;
    }
  },
};
