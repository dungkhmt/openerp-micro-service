
import { request } from "api";

export const weekDayService = {
  getAllWeekDays: async () => {
    return request("get", "/weekday/get-all");
  }
};