import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getRequestRegistration = async (param: IGetRequestRegistrationParams) => {
    const response = await apiClient.get<ICommonResponse<IRequestRegistrationResponse[]>>(`/api/v1/client/request-registration`, { params: param });
    return response.data;
}
export const useGetRequestRegistration = (param: IGetRequestRegistrationParams) => {
    return useQuery<ICommonResponse<IRequestRegistrationResponse[]>, AxiosError>({
        queryKey: ['requestRegistration', param],
        queryFn: () => getRequestRegistration(param)
    });
}

const addRequestRegistration = async (data: IAddRequestRegistrationRequest) => {
    const response = await apiClient.post('/api/v1/client/request-registration', data);
    return response.data;
}
export const useAddRequestRegistration = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IAddRequestRegistrationRequest) => addRequestRegistration(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['requestRegistration'] });
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