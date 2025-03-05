import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getStatuses: "/task-statuses",
  deleteStatus: (statusId) => `/task-statuses/${statusId}`,
  createStatus: "/task-statuses",
};

export const StatusService = {
  getStatuses: async (cb) => {
    try {
      const res = await privateClient.get(endPoints.getStatuses);
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
  createStatus: async (data, cb) => {
    try {
      const res = await privateClient.post(endPoints.createStatus, data);
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
  deleteStatus: async (statusId, cb) => {
    try {
      const res = await privateClient.delete(endPoints.deleteStatus(statusId));
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
