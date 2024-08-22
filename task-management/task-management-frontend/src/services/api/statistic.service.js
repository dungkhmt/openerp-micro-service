import privateClient from "../client/private.client";

const endpoints = {
  byStatus: "/task-statistics/by-status",
  workloadByStatus: "/task-statistics/by-workload",
};

export const StatisticService = {
  async getTaskStatisticByStatus(
    projectId,
    status,
    startDate,
    endDate,
    includeTasks
  ) {
    return (
      await privateClient.get(endpoints.byStatus, {
        params: { projectId, status, startDate, endDate, includeTasks },
      })
    ).data;
  },
  async getWorkloadByStatus(projectId, startDate, endDate) {
    return (
      await privateClient.get(endpoints.workloadByStatus, {
        params: { projectId, startDate, endDate },
      })
    ).data;
  },
};
