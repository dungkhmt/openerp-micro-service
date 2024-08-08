import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery, UseMutationOptions } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getListPickupPoint = async (params: IGetListPickupPointParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IPickupPointTable>>>('/api/v1/admin/pickup-point/pagination', { params });
    return response.data;
}
export const useGetListPickupPoint = (params: IGetListPickupPointParams) => {
    return useQuery<ICommonResponse<Page<IPickupPointTable>>, AxiosError>({
        queryKey: ['getListPickupPoint', params],
        queryFn: () => getListPickupPoint(params)
    });
};


const getListStudentPickupPointClient = async (params: IGetListPickupPointParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IStudentPickupPoint>>>('/api/v1/client/pickup-point/pagination', { params });
    return response.data;
}
export const useGetListStudentPickupPointClient = (params: IGetListPickupPointParams) => {
    return useQuery<ICommonResponse<Page<IStudentPickupPoint>>, AxiosError>({
        queryKey: ['getListPickupPointClient', params],
        queryFn: () => getListStudentPickupPointClient(params)
    });
};


const createPickupPoint = async (data: IPickupPoint) => {
    const response = await apiClient.post<ICommonResponse<IPickupPoint>>('/api/v1/admin/pickup-point', data);
    return response.data;
}
export const useCreatePickupPoint = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IPickupPoint) => createPickupPoint(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['getListPickupPoint'] });
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

const updatePickupPoint = async (data: IPickupPoint) => {
    const response = await apiClient.put<ICommonResponse<IPickupPoint>>('/api/v1/admin/pickup-point', data);
    return response.data;
}
export const useUpdatePickupPoint = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IPickupPoint) => updatePickupPoint(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['getListPickupPoint'] });
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

const deletePickupPoint = async (id: number) => {
    const response = await apiClient.delete<ICommonResponse<any>>(`/api/v1/admin/pickup-point/${id}`);
    return response.data;
}
export const useDeletePickupPoint = (callback: any) => {
    return useMutation(
        {
            mutationFn: (id: number) => deletePickupPoint(id),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['getListPickupPoint'] });
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