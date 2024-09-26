import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetPurchaseOrderList = (params) => {
  return useQuery({
    queryKey: [queryKey.purchase_order.order_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getPurchaseOrder,
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
export const useGetPurchaseOrderItems = (params, data) => {
  return useQuery({
    queryKey: [queryKey.purchase_order.order_item],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getPurchaseOrderItems,
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
export const useCreatePurchaseOrder = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createPurchaseOrder,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Tạo đơn mua thành công!");
        queryClient.invalidateQueries([queryKey.purchase_order.order_list]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi tạo đơn mua, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useUpdatePurchaseOrderStatus = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updatePurchaseOrderStatus,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Phê duyệt đơn mua thành công!");
        queryClient.invalidateQueries([queryKey.purchase_order.order_list]);
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
export const useCreateSellinPrice = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createSellinPrice,
        params,
        data
      );
      if (res && res.code === 1) {
        toast.success("Tạo thành công!");
        queryClient.invalidateQueries([queryKey.purchase_order.purchase_price]);
        return res.data;
      }
    },
    onError: () => {
      toast.error("Lỗi khi tạo , vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useGetSellinPrice = (params) => {
  return useQuery({
    queryKey: [queryKey.purchase_order.purchase_price, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getSellinPrice);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
  });
};

export const useUpdateSellinPrice = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updateSellinPrice,
        params,
        data
      );
      if (res && res.code === 1) {
        toast.success("Cập nhật thành công!");
        queryClient.invalidateQueries([queryKey.purchase_order.purchase_price]);
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

export const useDeleteSellinPrice = (params) => {
  return useMutation({
    mutationFn: async () => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteSellinPrice,
        params
      );
      if (res.code === 1) {
        toast.success("Xóa thành công!");
        queryClient.invalidateQueries([queryKey.purchase_order.purchase_price]);
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
export const useDeletePurchaseOrder = () => {
  return useMutation({
    mutationFn: async (params) => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deletePurchaseOrder,
        params
      );
      if (res.code === 1) {
        toast.success("Xóa đơn mua thành công!");
        queryClient.invalidateQueries([queryKey.purchase_order.order_list]);
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
export const useUpdatePurchaseOrder = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.updatePurchaseOrder,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Cập nhật thành công!");
        queryClient.invalidateQueries([queryKey.purchase_order.order_list]);
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
