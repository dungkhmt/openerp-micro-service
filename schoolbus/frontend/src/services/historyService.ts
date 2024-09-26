import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery, UseMutationOptions } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getAdminHistory = async (params: IAdminHistoryRideFilterParam) => {
    const response = await apiClient.get<ICommonResponse<Page<IHistoryResponse>>>('/api/v1/admin/history/ride/pagination', { params });
    return response.data;
}
export const useGetAdminHistory = (params: IAdminHistoryRideFilterParam) => {
    return useQuery<ICommonResponse<Page<IHistoryResponse>>, AxiosError>({
        queryKey: ['adminRideHistory', params],
        queryFn: () => getAdminHistory(params)
    });
};

const getClientHistory = async (params: IClientHistoryRideFilterParam) => {
    const response = await apiClient.get<ICommonResponse<Page<IHistoryResponse>>>('/api/v1/client/history/ride/pagination', { params });
    return response.data;
}
export const useGetClientHistory = (params: IClientHistoryRideFilterParam) => {
    return useQuery<ICommonResponse<Page<IHistoryResponse>>, AxiosError>({
        queryKey: ['clientRideHistory', params],
        queryFn: () => getClientHistory(params)
    });
};

const getEmployeeHistory = async (params: IEmployeeHistoryRideFilterParam) => {
    const response = await apiClient.get<ICommonResponse<Page<IHistoryResponse>>>('/api/v1/employee/history/ride/pagination', { params });
    return response.data;
}
export const useGetEmployeeHistory = (params: IEmployeeHistoryRideFilterParam) => {
    return useQuery<ICommonResponse<Page<IHistoryResponse>>, AxiosError>({
        queryKey: ['employeeRideHistory', params],
        queryFn: () => getEmployeeHistory(params)
    });
};