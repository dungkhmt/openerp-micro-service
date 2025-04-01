import { request } from "api";

export const groupRepository = {
  // Updated to call the new endpoint by groupId
  getGroupByGroupId: (groupId) =>
    request("get", `/group/get-by-group-id/${groupId}`),
  createPriority: (data) =>
    request("post", "/group/create-priority", null, null, data),
  updateGroup: (data) => request("post", "/group/update", null, null, data),
  deleteGroup: async (data) => {
    const response = await request("delete", "/group/delete", null, null, data);
    return response;
  },
  getAllGroupsList: () => request("get", "/group/get-all-group"),
  updateGroupName: (data) =>
    request("put", "/group/update-group-name", null, null, data),
  deleteById: (id) => request("delete", `/group/delete-by-id?id=${id}`),
  createGroup: (groupName) => 
    request("post", "/group/create-group", null, null, { groupName }),
};
