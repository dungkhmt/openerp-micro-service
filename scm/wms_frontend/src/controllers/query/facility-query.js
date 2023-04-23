import { useQuery } from "@tanstack/react-query";
import { request } from "../api-middleware";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetFacilityList = (params) => {
  return useQuery({
    queryKey: [queryKey.facility.facility_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getFacility,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
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
      const res = await request(
        "get",
        endPoint.getFacilityInventory,
        (res) => {},
        () => {},
        params
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
