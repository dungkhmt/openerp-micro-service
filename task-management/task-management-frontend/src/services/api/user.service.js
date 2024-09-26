import privateClient from "../client/private.client";

const endPoints = {
  getAll: "/users",
  sync: "/",
  getAssignedTaskCreator: "/users/assigned-task-creator",
  getMeCreatedAssignee: "/users/assigned-task-assignee",
};

export const UserService = {
  async getAll(params) {
    const res = await privateClient.get(endPoints.getAll, {
      params,
    });
    return res.data;
  },
  async sync() {
    try {
      const res = await privateClient.get(endPoints.sync);
      return res.data;
    } catch (e) {
      console.error("Sync user error");
    }
  },
  async getAllAssignedTaskCreator() {
    const res = await privateClient.get(endPoints.getAssignedTaskCreator);
    return res.data;
  },
  async getAllMeCreatedAssignee() {
    const res = await privateClient.get(endPoints.getMeCreatedAssignee);
    return res.data;
  },
};
