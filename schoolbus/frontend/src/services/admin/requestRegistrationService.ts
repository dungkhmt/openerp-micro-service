import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getPageRequestRegistration = async (param: IPageRequestRegistrationParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IRequestRegistrationResponse>>>(`/api/v1/admin/request-registration/pagination`, { params: param });
    return response.data;
}
export const useGetPageRequestRegistration = (param: IPageRequestRegistrationParams) => {
    return useQuery<ICommonResponse<Page<IRequestRegistrationResponse>>, AxiosError>({
        queryKey: ['pageRequestRegistration', param],
        queryFn: () => getPageRequestRegistration(param)
    });
}

const handleRequestRegistration = async (data: IHandleRequestRegistrationRequest) => {
    const response = await apiClient.post('/api/v1/admin/request-registration/handle', data);
    return response.data;
}
export const useHandleRequestRegistration = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IHandleRequestRegistrationRequest) => handleRequestRegistration(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['pageRequestRegistration'] });
                toast.success(result.message);
            },
            onError: (error: any) => {
                if (error.response && error.response.data && typeof error.response.data === 'object') {
                    const response: ICommonResponse<any> = error.response.data;
                    toast.error(error.response.data.message || 'An error occurred');
                } else {
                    // Handle any other errors
                    toast.error('An error occurred');
                }
            }
        }
    )
};