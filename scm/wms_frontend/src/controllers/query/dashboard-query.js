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

export const useGetNewCustomerMonthly = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.new_customer_month, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getNewcustomerMonthly,
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
export const useGetPurchaseOrderQuarterly = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.quarter_purchase_order, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getQuarterPurchaseProduct,
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
export const useGetTopCustomerBuying = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.top_customer, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getTopCustomerBuying,
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
export const useGetTripCustomerPerProvince = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.trip_per_province, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getTripCustomerPerProvince,
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
export const useGetProductCategoryRate = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.product_category_rate, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getProductCategoryRate,
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
export const useGetSaleAnnually = (params) => {
  return useQuery({
    queryKey: [queryKey.dashboard.sale_annually, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getSaleAnnually,
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
