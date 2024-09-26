import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getStudentRides = async (param: IGetStudentRidesParams) => {
    const response = await apiClient.get<ICommonResponse<IStudentRide[]>>(`/api/v1/client/parent-student/student-rides`, { params: param });
    return response.data;
}
export const useGetStudentRides = (param: IGetStudentRidesParams) => {
    return useQuery<ICommonResponse<IStudentRide[]>, AxiosError>({
        queryKey: ['studentRides', param],
        queryFn: () => getStudentRides(param)
    });
}