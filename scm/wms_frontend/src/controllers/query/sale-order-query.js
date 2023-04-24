import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetSaleOrderList = (params) => {
  return useQuery({
    queryKey: [queryKey.sale_order.order_list],
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
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo đơn bán thành công!");
      queryClient.invalidateQueries([queryKey.sale_order.order_list]);
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
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Cập nhật đơn bán thành công!");
      queryClient.invalidateQueries([queryKey.sale_order.order_list]);
    },
    onError: () => {
      toast.error("Lỗi khi cập nhật, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
