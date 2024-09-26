import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getCategories: "/task-categories",
};

export const CategoryService = {
  getCategories: async (cb) => {
    try {
      const res = await privateClient.get(endPoints.getCategories);
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
