import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getListParent = async (params: IGetListParentParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IParent>>>('/api/v1/admin/account/parent/pagination', { params });
    return response.data;
}
export const useGetListParent = (params: IGetListParentParams) => {
    return useQuery<ICommonResponse<Page<IParent>>, AxiosError>({
        queryKey: ['parentList', params],
        queryFn: () => getListParent(params)
    });
};
const getParentDetail = async (id: number) => {
    const response = await apiClient.get<ICommonResponse<IParentDetail>>(`/api/v1/admin/account/parent/${id}`);
    return response.data;
}
export const useGetParentDetail = (id: any) => {
    return useQuery<ICommonResponse<IParentDetail>, AxiosError>({
        queryKey: ['parentDetail', id],
        queryFn: () => getParentDetail(id),
        enabled: !!id
    });
};
const addParent = async (data: IParentAdd) => {
    const response = await apiClient.post('/api/v1/admin/account/parent', data);
    return response.data;
}
export const useAddParent = (callBack: any) => {
    return useMutation(
        {
            mutationFn: (data: IParentAdd) => addParent(data),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['parentList'] });
                toast.success(result.message);
                callBack();
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

const updateParent = async (data: IParentUpdate) => {
    const response = await apiClient.put(`/api/v1/admin/account/parent/${data.id}`, data);
    return response.data;
}
export const useUpdateParent = (callBackSuccess: any) => {
    return useMutation(
        {
            mutationFn: (data: IParentUpdate) => updateParent(data),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['parentList'] });
                toast.success(result.message);
                callBackSuccess();
            },
            onError: (error: any) => {
                if (error.response && error.response.data && typeof error.response.data === 'object') {
                    const response: ICommonResponse<any> = error.response.data;
                    toast.error(error.response.data.message || 'An error occurred')
                } else {
                    // Handle any other errors
                    console.log("error: ", error)

                    toast.error('An error occurred')
                }
            }
        }
    )
};

const deleteParent = async (id: number) => {
    const response = await apiClient.delete(`/api/v1/admin/account/parent/${id}`);
    return response.data;
}
export const useDeleteParent = () => {
    return useMutation(
        {
            mutationFn: (id: number) => deleteParent(id),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['parentList'] });
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


//student 

const getStudentDetail = async (id: number) => {
    const response = await apiClient.get<ICommonResponse<IStudentDetail>>(`/api/v1/admin/account/student/${id}`);
    return response.data;
}

export const useGetStudentDetail = (id: any) => {
    return useQuery<ICommonResponse<IStudentDetail>, AxiosError>({
        queryKey: ['studentDetail', id],
        queryFn: () => getStudentDetail(id),
        enabled: !!id
    });
};

const getListStudent = async (params: IGetListStudentParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IStudent>>>('/api/v1/admin/account/student/pagination', { params });
    return response.data;
}
export const useGetListStudent = (params: IGetListStudentParams) => {
    return useQuery<ICommonResponse<Page<IStudent>>, AxiosError>({
        queryKey: ['studentList', params],
        queryFn: () => getListStudent(params)
    });
};

const addStudent = async (data: IStudenAdd) => {
    const response = await apiClient.post('/api/v1/admin/account/student', data);
    return response.data;
}
export const useAddStudent = (callBack:any) => {
    return useMutation(
        {
            mutationFn: (data: IStudenAdd) => addStudent(data),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['studentList'] });
                toast.success(result.message);
                callBack();
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

const updateStudent = async (data: IStudentUpdate) => {
    const response = await apiClient.put(`/api/v1/admin/account/student/${data.id}`, data);
    return response.data;
}
export const useUpdateStudent = (callBackSuccess: any) => {
    return useMutation(
        {
            mutationFn: (data: IStudentUpdate) => updateStudent(data),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['studentList'] });
                toast.success(result.message);
                callBackSuccess();
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

const deleteStudent = async (id: number) => {
    const response = await apiClient.delete(`/api/v1/admin/account/student/${id}`);
    return response.data;
}
export const useDeleteStudent = () => {
    return useMutation(
        {
            mutationFn: (id: number) => deleteStudent(id),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['studentList'] });
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