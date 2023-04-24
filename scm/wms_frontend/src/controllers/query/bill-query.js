import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetReceiptBillList = (params) => {
  return useQuery({
    queryKey: [queryKey.receipt_bill.receipt_bill_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getReceiptBills);
      if (res.data && res.code === 1) {
        return res.data;
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
      const res = await axiosSendRequest(
        "get",
        endPoint.getBillItemOfPurchaseOrder,
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

export const useCreateBill = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createBill,
        params,
        data
      );
      if (res.code === 1) {
        toast.success(res.message);
        queryClient.invalidateQueries([
          queryKey.receipt_bill.bill_item_of_order,
        ]);
        return res.data;
      } else {
        // toast.error(res.message);
      }
    },
    onSuccess: (res, variables, context) => {
      queryClient.invalidateQueries([queryKey.receipt_bill.bill_item_of_order]);
    },
    onError: (err) => {
      toast.error(err);
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
