import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetPurchaseOrderList = (params) => {
  return useQuery({
    queryKey: [queryKey.purchase_order.order_list],
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
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo đơn mua thành công!");
      queryClient.invalidateQueries([queryKey.purchase_order.order_list]);
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
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Phê duyệt đơn mua thành công!");
      queryClient.invalidateQueries([queryKey.purchase_order.order_list]);
    },
    onError: () => {
      toast.error("Lỗi khi cập nhật, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
