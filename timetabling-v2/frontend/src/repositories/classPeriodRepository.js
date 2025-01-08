import { request } from "api";

export const classPeriodService = {
  getAllClassPeriods: async () => {
    return request("get", "/class-period/get-all");
  }
};
