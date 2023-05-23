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
      const res = await axiosSendRequest("get", endPoint.getProduct, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};
export const useGetCustomerList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.customer_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getCustomer, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};
export const useGetProductCateList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_cate_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getProductCategory,
        params
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};

export const useGetProductUnitList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_unit_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getProductUnit,
        params
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};

export const useGetDistChannelList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.dist_channel_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getDistChannel,
        params
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};

export const useGetCustomerType = (params) => {
  return useQuery({
    queryKey: [queryKey.category.customer_type_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getCustomerType,
        params
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};

export const useGetContractType = (params) => {
  return useQuery({
    queryKey: [queryKey.category.contract_type_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getContractType,
        params
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};

export const useCreateProduct = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createProduct,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      } else throw Error;
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
        toast.success("Tạo khách hàng mới thành công!");
        queryClient.invalidateQueries([queryKey.category.customer_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi tạo khách hàng, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useCreateProductUnit = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createProductUnit,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo unit thành công!");
      queryClient.invalidateQueries([queryKey.category.product_unit_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useCreateProductCategory = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createProductCategory,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo thành công!");
      queryClient.invalidateQueries([queryKey.category.product_cate_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useCreateCustomerType = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createCustomerType,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo thành công!");
      queryClient.invalidateQueries([queryKey.category.customer_type_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useDeleteCustomer = () => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteCustomer,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      } else throw Error;
    },
    onSuccess: (res, variables, context) => {
      toast.success("Xóa thành công!");
      queryClient.invalidateQueries([queryKey.category.customer_list]);
    },
    onError: () => {
      toast.error("Lỗi khi xóa, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useCreateContractType = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createContractType,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      } else throw Error;
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo thành công!");
      queryClient.invalidateQueries([queryKey.category.contract_type_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useCreateDistChannel = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createDistChannel,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Tạo thành công!");
        queryClient.invalidateQueries([queryKey.category.dist_channel_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
