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
      const res = await axiosSendRequest("get", endPoint.getFacility, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useGetFacilityListNoPaging = () => {
  return useQuery({
    queryKey: [queryKey.facility.facility_list],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getFacilityNoPaging);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useGetFacilityCustomersPaging = (params) => {
  return useQuery({
    queryKey: [queryKey.facility.facility_customer, params],
    queryFn: async (data) => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getFacilityCustomers,
        params,
        data
      );
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

export const useUpdateFacility = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updateFacility,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Cập nhật thành công");
        queryClient.invalidateQueries([queryKey.facility.facility_list]);
        return res.data;
      } else throw Error;
    },
    onError: () => {
      toast.error("Lỗi khi thay đổi, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useImportFacility = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.importFacility,
        params,
        data
      );
      if (res.code === 1) {
        toast.success("Tạo kho hàng mới thành công!");
        queryClient.invalidateQueries([queryKey.category.customer_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi tạo kho hàng, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
