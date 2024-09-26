import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

// get manipulate pickup point
const getListManipulatePickupPoint = async (date: string, rideId: number | null) => {
    const response = await apiClient.get<ICommonResponse<IManipulatePickupPointOutput>>('/api/v1/employee/pickup-point/manipulate',{ params: { date, rideId } });
    return response.data;
}
export const useGetListManipulatePickupPoint = (date: string, rideId: number | null) => {
    return useQuery<ICommonResponse<IManipulatePickupPointOutput>, AxiosError>({
        queryKey: ['manipulatePickupPoint', date, rideId],
        queryFn: () => getListManipulatePickupPoint(date, rideId)
    });
};

// get list ride at that day
const getListRideAtThatDay = async (date: string) => {
    const response = await apiClient.get<ICommonResponse<IRide[]>>('/api/v1/employee/pickup-point/list-ride-id',{ params: { date } });
    return response.data;
}
export const useGetListRideAtThatDay = (date: string) => {
    return useQuery<ICommonResponse<IRide[]>, AxiosError>({
        queryKey: ['listRideAtThatDay', date],
        queryFn: () => getListRideAtThatDay(date)
    });
};

// update bus
const updateEmployeeBus = async (data: IEmployeeUpdateBusRequest) => {
    const response = await apiClient.put('/api/v1/employee/bus', data);
    return response.data;
}
export const useUpdateEmployeeBus = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IEmployeeUpdateBusRequest) => updateEmployeeBus(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['manipulatePickupPoint'] });
                toast.success(result.message);
            },
            onError: (error: any) => {
                if (error.response && error.response.data && typeof error.response.data === 'object') {
                    const response: ICommonResponse<any> = error.response.data;
                    toast.error(error.response.data.message || 'An error occurred')
                } else {
                    // Handle any other errors
                    toast.error('An error occurred')
                }
            }
        }
    )
};

// update ride
const updateEmployeeRide = async (data: IEmployeeUpdateRideRequest) => {
    const response = await apiClient.put('/api/v1/employee/ride', data);
    return response.data;
}
export const useUpdateEmployeeRide = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IEmployeeUpdateRideRequest) => updateEmployeeRide(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['manipulatePickupPoint'] });
                toast.success(result.message);
            },
            onError: (error: any) => {
                if (error.response && error.response.data && typeof error.response.data === 'object') {
                    const response: ICommonResponse<any> = error.response.data;
                    toast.error(error.response.data.message || 'An error occurred')
                } else {
                    // Handle any other errors
                    toast.error('An error occurred')
                }
            }
        }
    )
};

// update ride pickup point
const updateEmployeeRidePickupPoint = async (data: IEmployeeUpdateRidePickupPointRequest) => {
    const response = await apiClient.put('/api/v1/employee/ride-pickup-point', data);
    return response.data;
}
export const useUpdateEmployeeRidePickupPoint = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IEmployeeUpdateRidePickupPointRequest) => updateEmployeeRidePickupPoint(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['manipulatePickupPoint'] });
                toast.success(result.message);
            },
            onError: (error: any) => {
                if (error.response && error.response.data && typeof error.response.data === 'object') {
                    const response: ICommonResponse<any> = error.response.data;
                    toast.error(error.response.data.message || 'An error occurred')
                } else {
                    // Handle any other errors
                    toast.error('An error occurred')
                }
            }
        }
    )
};

// update student pickup point
const updateEmployeeStudentPickupPoint = async (data: IEmployeeUpdateStudentPickupPointRequest) => {
    const response = await apiClient.put('/api/v1/employee/student-pickup-point', data);
    return response.data;
}
export const useUpdateEmployeeStudentPickupPoint = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IEmployeeUpdateStudentPickupPointRequest) => updateEmployeeStudentPickupPoint(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['manipulatePickupPoint'] });
                toast.success(result.message);
            },
            onError: (error: any) => {
                if (error.response && error.response.data && typeof error.response.data === 'object') {
                    const response: ICommonResponse<any> = error.response.data;
                    toast.error(error.response.data.message || 'An error occurred')
                } else {
                    // Handle any other errors
                    toast.error('An error occurred')
                }
            }
        }
    )
};