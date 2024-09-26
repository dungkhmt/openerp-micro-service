import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getPriorities: "/task-priorities",
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
};
