import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getCategories: "/task-categories",
  createCategory: "/task-categories",
  deleteCategory: (categoryId) => `/task-categories/${categoryId}`,
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
  createCategory: async (data, cb) => {
    try {
      const res = await privateClient.post(endPoints.createCategory, data);
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
  deleteCategory: async (categoryId, cb) => {
    try {
      const res = await privateClient.delete(endPoints.deleteCategory(categoryId));
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
