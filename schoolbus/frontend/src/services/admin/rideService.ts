import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery, UseMutationOptions } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

// add ride
export const addRide = async (data: IUpsertRideRequest) => {
    const response = await apiClient.post('/api/v1/admin/ride/upsert', data);
    return response.data;
}
export const useAddRide = (callback: any) => {
    return useMutation(
        {
            mutationFn: (data: IUpsertRideRequest) => addRide(data),
            onSuccess: (result) => {
                callback();
                queryClient.invalidateQueries({ queryKey: ['rideList', 'manipulateBusList'] });
                queryClient.invalidateQueries({ queryKey: ['manipulateBusList'] });
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