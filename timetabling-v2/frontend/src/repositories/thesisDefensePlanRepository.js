
import { request } from "api";

export const thesisDefensePlanServices = {
  getAllPlans: async () => {
    const response = await request("get", "/thesis-defense-plan/get-all");
    return response;
  }
};