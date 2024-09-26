import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetSaleOrderList = (params) => {
  return useQuery({
    queryKey: [queryKey.sale_order.order_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getSaleOrder, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useGetSaleOrderItems = (params, data) => {
  return useQuery({
    queryKey: [queryKey.sale_order.order_item],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getSaleOrderItems,
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
export const useCreateSaleOrder = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createSaleOrder,
        params,
        data
      );
      if (res.code === 1) {
        toast.success("Tạo đơn bán thành công!");
        queryClient.invalidateQueries([queryKey.sale_order.order_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi tạo đơn bán, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useUpdateSaleOrderStatus = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updateSaleOrderStatus,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Cập nhật đơn bán thành công!");
        queryClient.invalidateQueries([queryKey.sale_order.order_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi cập nhật, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useCreateSelloutPrice = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createSelloutPrice,
        params,
        data
      );
      if (res && res.code === 1) {
        toast.success("Tạo thành công!");
        queryClient.invalidateQueries([queryKey.sale_order.sale_price]);
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
export const useGetSelloutPrice = (params) => {
  return useQuery({
    queryKey: [queryKey.sale_order.sale_price, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getSelloutPrice);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};

export const useUpdateSelloutPrice = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updateSelloutPrice,
        params,
        data
      );
      if (res && res.code === 1) {
        toast.success("Cập nhật thành công!");
        queryClient.invalidateQueries([queryKey.sale_order.sale_price]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi , vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useDeleteSelloutPrice = (params) => {
  return useMutation({
    mutationFn: async () => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteSelloutPrice,
        params
      );
      if (res.code === 1) {
        toast.success("Xóa thành công!");
        queryClient.invalidateQueries([queryKey.sale_order.sale_price]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi xóa dữ liệu, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useDeleteSaleOrder = () => {
  return useMutation({
    mutationFn: async (params) => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteSaleOrder,
        params
      );
      if (res.code === 1) {
        toast.success("Xóa đơn bán thành công!");
        queryClient.invalidateQueries([queryKey.sale_order.order_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi xóa dữ liệu, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useUpdateSaleOrder = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.updateSaleOrder,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Cập nhật thành công!");
        queryClient.invalidateQueries([queryKey.sale_order.order_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
