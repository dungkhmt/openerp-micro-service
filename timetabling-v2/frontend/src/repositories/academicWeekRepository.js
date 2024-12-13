
import { request } from "api";

export const academicWeekServices = {
  getAcademicWeeks: async (semester) => {
    if (!semester) return null;
    const response = await request(
      "get",
      `/academic-weeks/?semester=${semester}`
    );
    return response;
  },

  deleteAcademicWeeks: async (semester) => {
    return await request(
      "delete",
      `/academic-weeks/?semester=${semester}`
    );
  },

  createAcademicWeeks: async (data) => {
    return await request(
      "post",
      "/academic-weeks/",
      null,
      null,
      data
    );
  }
};