import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getStatuses: "/task-statuses",
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
};
