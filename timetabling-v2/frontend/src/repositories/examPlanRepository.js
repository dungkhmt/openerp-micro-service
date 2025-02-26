import { request } from "api";

const API_ENDPOINTS = {
  GET_ALL: "/exam-plan",
  CREATE: "/exam-plan/create",
  UPDATE: "/exam-plan/update",
  DELETE: "/exam-plan/delete"
};

class ExamPlanService {
  async getAllExamPlans() {
    return await request("get", API_ENDPOINTS.GET_ALL);
  }

  async createExamPlan(data) {
    return await request("post", API_ENDPOINTS.CREATE, null, null, data, {
      headers: {
        "Content-Type": "application/json"
      } 
    });
  }

  async updateExamPlan(data) {
    return await request("post", API_ENDPOINTS.UPDATE, null, null, data, {
      headers: {
        "Content-Type": "application/json"
      }
    });
  }

  async deleteExamPlan(id) {
    return await request("post", `${API_ENDPOINTS.DELETE}/${id}`);
  }

  async getExamPlanById(id) {
    return await request("get", `${API_ENDPOINTS.GET_ALL}/${id}`);
  }

  async getPlanStatistics(id) {
    return await request("get", `/exam-plan/${id}/statistics`);
  }
}

export const examPlanService = new ExamPlanService();
