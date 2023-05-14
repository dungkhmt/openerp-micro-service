import { useQuery } from "@tanstack/react-query";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetAllUsersExist = () => {
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
