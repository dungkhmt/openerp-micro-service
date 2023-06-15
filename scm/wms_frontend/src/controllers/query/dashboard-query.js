import { useQuery } from "@tanstack/react-query";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetNewFacilityMonthly = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.new_facility_month, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getNewFacilityMonthly,
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
export const useGetImportedProduct = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.imported_product_cate, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getImportedProduct,
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
