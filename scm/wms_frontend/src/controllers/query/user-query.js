import { useQuery } from "@tanstack/react-query";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetAllUsersExist = (params) => {
  return useQuery({
    queryKey: [queryKey.user.user_list_all],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getAllUsersWithoutPagination
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useGetUserPagination = (params) => {
  return useQuery({
    queryKey: [queryKey.user.user_list_pagination, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getAllUsers, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useGetAllUsersByRoles = (params) => {
  return useQuery({
    queryKey: [queryKey.user.user_list_all_by_role],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getAllUsersByRole,
        params
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
