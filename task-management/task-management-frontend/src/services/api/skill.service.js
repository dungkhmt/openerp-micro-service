import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getAllSkills: `/skills`,
  addSkill: `/skills`,
  deleteSkill: (skillId) => `/skills/${skillId}`,
  updateSkill: (skillId) => `/skills/${skillId}`,
};

const SkillService = {
  getAllSkills: async (cb) => {
    try {
      const response = await privateClient.get(endPoints.getAllSkills);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  addSkill: async (data, cb) => {
    try {
      const response = await privateClient.post(endPoints.addSkill, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  deleteSkill: async (skillId, cb) => {
    try {
      const response = await privateClient.delete(
        endPoints.deleteSkill(skillId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateSkill: async (data, skillId, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateSkill(skillId),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { SkillService };
