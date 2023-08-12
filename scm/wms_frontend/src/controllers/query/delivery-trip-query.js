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
        toast.success("Tạo chuyến giao hàng thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.trip_list]);
        return res.data;
      }
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
export const useCreateTripRoute = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createTripRoute,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Tạo thành công!");
        queryClient.invalidateQueries({
          queryKey: [queryKey.delivery_trip.trip_route_list],
        });
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useGetTripRouteList = (params) => {
  return useQuery({
    queryKey: [queryKey.delivery_trip.trip_route_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getTripRoutes, params);
      if (res.code === 1) {
        return res.data ? res.data : null;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
export const useDeleteTripRoute = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteTripRoute,
        params,
        data
      );
      if (res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Xóa chuyến giao hàng thành công!");
      queryClient.invalidateQueries({
        queryKey: [queryKey.delivery_trip.trip_route_list],
      });
    },
    onError: () => {
      toast.error("Lỗi khi xóa chuyến, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
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
        toast.success("Tạo thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.truck_list]);
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useUpdateTruck = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updateTruck,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Cập nhật thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.truck_list]);
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useDeleteTruck = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteTruck,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Xóa thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.truck_list]);
        return res.data;
      } else throw Error;
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi xóa, vui lòng kiểm tra lại");
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
        toast.success("Tạo thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.drone_list]);
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useDeleteDrone = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteDrone,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Xóa thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.drone_list]);
        return res.data;
      } else throw Error;
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi xóa, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useUpdateDrone = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updateDrone,
        params,
        data
      );
      if (res.data && res.code === 1) {
        toast.success("Cập nhật thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.drone_list]);
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useUpdateTrip = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "put",
        endPoint.updateTrip,
        params,
        data
      );
      if (res.code === 1) {
        toast.success("Cập nhật thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.trip_list]);
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi tạo, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};

export const useDeleteTrip = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "delete",
        endPoint.deleteTrip,
        params,
        data
      );
      if (res.code === 1) {
        toast.success("Xóa thành công!");
        queryClient.invalidateQueries([queryKey.delivery_trip.trip_list]);
        return res.data;
      } else throw Error;
    },
    onSuccess: (res, variables, context) => {},
    onError: () => {
      toast.error("Lỗi khi xóa, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
