import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery, UseMutationOptions } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const upsertStudentAssign = async (data: IUpsertStudentAssignRequest) => {
    const response = await apiClient.post('/api/v1/admin/student-assign', data);
    return response.data;
}
export const useUpsertStudentAssign = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IUpsertStudentAssignRequest) => upsertStudentAssign(data),
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
};