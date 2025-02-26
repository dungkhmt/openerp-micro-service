import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getAllSkills: `/skills`,
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
}

export { SkillService };
