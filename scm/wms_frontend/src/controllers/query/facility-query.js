import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetFacilityList = (params) => {
  return useQuery({
    queryKey: [queryKey.facility.facility_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getFacility);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useGetFacilityInventory = (params) => {
  return useQuery({
    queryKey: [queryKey.facility.facility_inventory, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getFacilityInventory,
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
export const useCreateFacility = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createFacility,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo thành công!");
      queryClient.invalidateQueries([queryKey.facility.facility_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
