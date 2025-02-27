import { request } from "api";

const API_ENDPOINTS = {
  GET_ALL: "/exam-timetable/plan",
  CREATE: "/exam-timetable/create",
  UPDATE: "/exam-timetable/update",
  DELETE: "/exam-timetable/delete"
};

class ExamTimetableService {
  async getAllExamTimetables(planId) {
    return await request("get", `${API_ENDPOINTS.GET_ALL}/${planId}`);
  }

  async createExamTimetable(data) {
    return await request("post", API_ENDPOINTS.CREATE, null, null, data, {
      headers: {
        "Content-Type": "application/json"
      } 
    });
  }

  async updateExamTimetable(data) {
    return await request("post", API_ENDPOINTS.UPDATE, null, null, data, {
      headers: {
        "Content-Type": "application/json"
      }
    });
  }

  async deleteExamTimetable(id) {
    return await request("post", `${API_ENDPOINTS.DELETE}/${id}`);
  }
}

export const examTimetableService = new ExamTimetableService();
