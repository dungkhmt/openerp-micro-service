import { request } from "api";

export const groupRepository = {
  getAllGroups: () => request("get", "/group/get-all"),
  createGroup: (data) => request("post", "/group/create", null, null, data),
  updateGroup: (data) => request("post", "/group/update", null, null, data),
  deleteGroup: (id) => request("delete", `/group/delete?id=${id}`),
};
