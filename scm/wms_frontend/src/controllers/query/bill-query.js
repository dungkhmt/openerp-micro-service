import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetReceiptBillList = (params) => {
  return useQuery({
    queryKey: [queryKey.receipt_bill.receipt_bill_list, params],
    queryFn: async (data) => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getReceiptBills,
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
export const useGetBillItemOfPurchaseOrder = (params) => {
  return useQuery({
    queryKey: [queryKey.receipt_bill.bill_item_of_purchase_order, params],
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
export const useGetBillItemOfPurchaseOrderPaging = (params) => {
  return useQuery({
    queryKey: [
      queryKey.receipt_bill.bill_item_of_purchase_order_paging,
      params,
    ],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getBillItemOfPurchaseOrderPaging,
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
export const useCreateReceiptBill = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createReceiptBill,
        params,
        data
      );
      if (res.code === 1) {
        toast.success(res.message);
        return res.data;
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [queryKey.receipt_bill.bill_item_of_purchase_order],
      });
      queryClient.invalidateQueries({
        queryKey: [queryKey.receipt_bill.receipt_bill_list],
      });
    },
  });
};

export const useGetDeliveryBillList = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_bill.delivery_bill_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getDeliveryBills,
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
export const useGetBillItemOfSaleOrder = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_bill.bill_item_of_delivery_order, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getBillItemOfSaleOrder,
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
export const useGetBillItemsOfBill = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_bill.bill_item_of_bill, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getBillItemOfBill,
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
export const useCreateDeliveryBill = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createDeliveryBill,
        params,
        data
      );
      if (res.code === 1) {
        toast.success(res.message);
        queryClient.invalidateQueries({
          queryKey: [queryKey.delivery_bill.bill_item_of_delivery_order],
        });
        queryClient.invalidateQueries({
          queryKey: [queryKey.facility.delivery_bill_list],
        });
        return res.data;
      }
    },
  });
};
export const useGetSplittedBillItem = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_bill.splitted_bill_item, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getSplittedBillItems,
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
export const useCreateSplitBillItem = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createSplitBillItems,
        params,
        data
      );
      if (res.code === 1) {
        toast.success(res.message);

        return res.data;
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [queryKey.delivery_bill.splitted_bill_item],
      });
      queryClient.invalidateQueries({
        queryKey: [queryKey.facility.facility_inventory],
      });
      queryClient.invalidateQueries({
        queryKey: [queryKey.delivery_bill.bill_item_of_bill],
      });
    },
  });
};

export const useGetDeliveryBillItemBySeq = () => {
  return useQuery({
    queryKey: [queryKey.delivery_bill.bill_item_by_seq],
    queryFn: async (params) => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getBillItemBySeq,
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
