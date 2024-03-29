import privateClient from "../client/private.client";

const endPoints = {
  getAll: "/users",
  sync: "/",
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
};
