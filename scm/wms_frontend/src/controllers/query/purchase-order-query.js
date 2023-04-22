import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import { request } from "../api-middleware";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetPurchaseOrderList = (params) => {
  return useQuery({
    queryKey: [queryKey.purchase_order.order_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getPurchaseOrder,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useCreatePurchaseOrder = (params) => {
  return useMutation({
    mutationFn: async (params) => {
      const res = await request(
        "post",
        endPoint.createPurchaseOrder,
        (res) => {},
        () => {},
        params
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
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
