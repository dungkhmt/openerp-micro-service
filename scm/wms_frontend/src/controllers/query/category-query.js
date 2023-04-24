import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetProductList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getProduct);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useGetCustomerList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.customer_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getCustomer);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useGetProductCateList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_cate_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getProductCategory);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useGetProductUnitList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_unit_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getProductUnit);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useGetDistChannelList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.dist_channel_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getDistChannel);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useGetCustomerType = (params) => {
  return useQuery({
    queryKey: [queryKey.category.customer_type_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getCustomerType);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useGetContractType = (params) => {
  return useQuery({
    queryKey: [queryKey.category.contract_type_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getContractType);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useCreateProduct = (params) => {
  return useMutation({
    mutationFn: async (params) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createProduct,
        params
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo sản phẩm thành công!");
      queryClient.invalidateQueries([queryKey.category.product_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo sản phẩm, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useCreateCustomer = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createCustomer,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo khách hàng mới thành công!");
      queryClient.invalidateQueries([queryKey.category.customer_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo khách hàng, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
