import { request } from "api";

const API_ENDPOINTS = {
  GET_ALL: "/semester/get-all",
  CREATE: "/semester/create",
  UPDATE: "/semester/update",
  DELETE: "/semester/delete"
};

class SemesterService {
  async getAllSemesters() {
    return await request("get", API_ENDPOINTS.GET_ALL);
  }

  async createSemester(data) {
    return await request("post", API_ENDPOINTS.CREATE, null, null, data);
  }

  async updateSemester(data) {
    return await request("post", API_ENDPOINTS.UPDATE, null, null, data);
  }

  async deleteSemester(id) {
    return await request("delete", `${API_ENDPOINTS.DELETE}?id=${id}`);
  }
}

export const semesterService = new SemesterService();
