import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import { request } from "../api-middleware";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetReceiptBillList = (params) => {
  return useQuery({
    queryKey: [queryKey.receipt_bill.receipt_bill_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getReceiptBills,
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
export const useGetBillItemOfOrder = (params) => {
  return useQuery({
    queryKey: [queryKey.receipt_bill.bill_item_of_order, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getBillItemOfPurchaseOrder,
        (res) => {},
        () => {},
        params
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useCreateBill = (params) => {
  return useMutation({
    mutationFn: async (params) => {
      const res = await request(
        "post",
        endPoint.createBill,
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
