import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import { request } from "../api-middleware";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetProductList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getProduct,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};
export const useGetCustomerList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.customer_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getCustomer,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};
export const useGetProductCateList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_cate_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getProductCategory,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};

export const useGetProductUnitList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_unit_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getProductUnit,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};

export const useGetDistChannelList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.dist_channel_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getDistChannel,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};

export const useGetCustomerType = (params) => {
  return useQuery({
    queryKey: [queryKey.category.customer_type_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getCustomerType,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};

export const useGetContractType = (params) => {
  return useQuery({
    queryKey: [queryKey.category.contract_type_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getContractType,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};

export const useCreateProduct = (params) => {
  return useMutation({
    mutationFn: async (params) => {
      const res = await request(
        "post",
        endPoint.createProduct,
        (res) => {},
        () => {},
        params
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
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
