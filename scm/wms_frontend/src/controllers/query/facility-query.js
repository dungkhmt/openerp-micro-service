import { useQuery } from "@tanstack/react-query";
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
