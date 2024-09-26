import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery, UseMutationOptions } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

// get bus page
const getListBus = async (params: IGetListBusParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IBusTable>>>('/api/v1/admin/bus/pagination', { params });
    return response.data;
}
export const useGetListBus = (params: IGetListBusParams) => {
    return useQuery<ICommonResponse<Page<IBusTable>>, AxiosError>({
        queryKey: ['busList', params],
        queryFn: () => getListBus(params)
    });
};

// get bus detail
const getBusDetail = async (id: number) => {
    const response = await apiClient.get<ICommonResponse<IGetBusDetailResponse>>(`/api/v1/admin/bus/${id}`);
    return response.data;
}
export const useGetBusDetail = (id: number) => {
    return useQuery<ICommonResponse<IGetBusDetailResponse>, AxiosError>({
        queryKey: ['busDetail', id],
        queryFn: () => getBusDetail(id)
    });
};

// add bus
const addBus = async (data: IBus) => {
    const response = await apiClient.post('/api/v1/admin/bus', data);
    return response.data;
}
export const useAddBus = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IBus) => addBus(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['busList'] });
                queryClient.invalidateQueries({ queryKey: ['availableEmployees'] });
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

// update bus
const updateBus = async (data: IBus) => {
    const response = await apiClient.put('/api/v1/admin/bus', data);
    return response.data;
}
export const useUpdateBus = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IBus) => updateBus(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['busList'] });
                queryClient.invalidateQueries({ queryKey: ['availableEmployees'] });
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

// delete bus
const deleteBus = async (id: number) => {
    const response = await apiClient.delete(`/api/v1/admin/bus`, { data: { id } });
    return response.data;
}
export const useDeleteBus = (callback: any) => {
    return useMutation(
        {
            mutationFn: (id: number) => deleteBus(id),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['busList'] });
                queryClient.invalidateQueries({ queryKey: ['availableEmployees'] });
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
}

// get bus have available status
const getAvailableBuses = async (role: string | null, numberPlate: String | null) => {
    const response = await apiClient.get<ICommonResponse<IBus[]>>(`/api/v1/admin/bus/available`, { params: { role, numberPlate } });
    return response.data;
}
export const useGetAvailableBuses = (role: string | null, numberPlate: String | null) => {
    return useQuery<ICommonResponse<IBus[]>, AxiosError>({
        queryKey: ['availableBuses', role, numberPlate],
        queryFn: () => getAvailableBuses(role, numberPlate)
    });
};

// get list manipulate bus
const getListManipulateBus = async (params: IGetListManipulateBusParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IManipulateBus>>>('/api/v1/admin/bus/manipulate', { params });
    return response.data;
}
export const useGetListManipulateBus = (params: IGetListManipulateBusParams) => {
    return useQuery<ICommonResponse<Page<IManipulateBus>>, AxiosError>({
        queryKey: ['manipulateBusList', params],
        queryFn: () => getListManipulateBus(params)
    });
};