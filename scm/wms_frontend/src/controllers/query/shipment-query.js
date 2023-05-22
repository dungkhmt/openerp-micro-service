import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useCreateShipment = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createShipment,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Tạo đợt giao hàng thành công!");
        return res.data;
      }
    },
    // onSuccess: (res, variables, context) => {
    //   toast.success("Tạo đợt giao hàng thành công!");
    //   queryClient.invalidateQueries([queryKey.shipment.shipment_list]);
    // },
    // onError: () => {
    //   toast.error("Lỗi khi tạo kế hoạch, vui lòng kiểm tra lại");
    // },
    // // befor mutation function actually triggers.
    // onMutate: (variables) => {},
  });
};
export const useGetShipmentList = (params) => {
  return useQuery({
    queryKey: [queryKey.shipment.shipment_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getShipments, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useGetShipmentItems = (params) => {
  return useQuery({
    queryKey: [queryKey.shipment.shipment_items, params],
    queryFn: async (data) => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getShipmentItems,
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
export const useGetItemsOfTrip = (params) => {
  return useQuery({
    queryKey: [queryKey.shipment.trip_items],
    queryFn: async (data) => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getItemOfTrip,
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
export const useAssignShipmentToTrip = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.assignShipmentToTrip,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Phân thành công!");
        return res.data;
      } else {
        toast.error("Lỗi khi phân, vui lòng kiểm tra lại");
      }
    },
    // onSuccess: (res, variables, context) => {
    //   toast.success("Phân thành công!");
    //   queryClient.invalidateQueries([
    //     queryKey.shipment.shipment_list,
    //     queryKey.delivery_bill.splitted_bill_item,
    //   ]);
    // },
    // onError: () => {
    //   toast.error("Lỗi khi phân, vui lòng kiểm tra lại");
    // },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
