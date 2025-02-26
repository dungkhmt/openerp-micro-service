import { request } from "api";

export const classRegistrationServices = {
  getClassesBySemester: async (semester, page, limit, search) => {
    const searchParam = search ? `&search=${encodeURIComponent(search)}` : "";
    const response = await request(
      "get",
      `/class-call/get-class-by-semester/${semester}?page=${page}&limit=${limit}${searchParam}`
    );
    return response;
  },

  getMyRegisteredClasses: async (semester) => {
    const response = await request(
      "get",
      `/class-call/get-my-registered-class/${semester}`
    );
    return response;
  },

  getAllSemesters: async () => {
    const response = await request(
      "get", 
      "/ta-semester/get-all-semester"
    );
    return response;
  },

  getCurrentSemester: async () => {
    const response = await request(
      "get", 
      "/ta-semester/get-current-semester"
    );
    return response;
  }
};
