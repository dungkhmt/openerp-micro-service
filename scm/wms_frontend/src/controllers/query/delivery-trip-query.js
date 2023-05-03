import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useCreateDeliveryTrip = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createDeliveryTrip,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo chuyến giao hàng thành công!");
      queryClient.invalidateQueries([queryKey.delivery_trip.trip_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo chuyến, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useGetDeliveryTripList = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_trip.trip_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getDeliveryTrips,
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

export const useGetDeliveryTripToAssignBill = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_trip.trip_assign_bill, params],
    queryFn: async () => {
      const res = await axiosSendRequest(
        "get",
        endPoint.getTripToAssignBill,
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

export const useGetListTruck = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_trip.truck_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getTruck, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useCreateTruck = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createTruck,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo thành công!");
      queryClient.invalidateQueries([queryKey.delivery_trip.truck_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useGetDroneList = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_trip.drone_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getDrone, params);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};

export const useCreateDrone = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createDrone,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo thành công!");
      queryClient.invalidateQueries([queryKey.delivery_trip.drone_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
