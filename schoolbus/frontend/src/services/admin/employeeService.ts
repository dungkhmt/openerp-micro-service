import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery, UseMutationOptions } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getListEmployee = async (params: IGetListEmployeeParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IEmployeeTable>>>('/api/v1/admin/employee/pagination', { params });
    return response.data;
}
export const useGetListEmployee = (params: IGetListEmployeeParams) => {
    return useQuery<ICommonResponse<Page<IEmployeeTable>>, AxiosError>({
        queryKey: ['employeeList', params],
        queryFn: () => getListEmployee(params)
    });
};

const addEmployee = async (data: IAddEmployee) => {
    const response = await apiClient.post('/api/v1/admin/employee', data);
    return response.data;
}
export const useAddEmployee = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IAddEmployee) => addEmployee(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['employeeList'] });
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

const updateEmployee = async (data: IEmployee) => {
    const response = await apiClient.put('/api/v1/admin/employee', data);
    return response.data;
}
export const useUpdateEmployee = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IEmployee) => updateEmployee(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['employeeList'] });
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

const deleteEmployee = async (id: number) => {
    const response = await apiClient.delete<ICommonResponse<any>>(`/api/v1/admin/employee`, { data: { id } });
    return response.data;
}
export const useDeleteEmployee = (callback: any) => {
    return useMutation(
        {
            mutationFn: (id: number) => deleteEmployee(id),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['employeeList'] });
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

const getAvailableEmployees = async (role: EmployeeRole | null, query: string | null) => {
    const response = await apiClient.get<ICommonResponse<IEmployee[]>>(`/api/v1/admin/employee/available`, { params: { role, query } });
    return response.data;
}
export const useGetAvailableEmployees = (role: EmployeeRole | null, query: string | null) => {
    return useQuery<ICommonResponse<IEmployee[]>, AxiosError>({
        queryKey: ['availableEmployees', role, query],
        queryFn: () => getAvailableEmployees(role, query)
    });
};