import apiClient from "@/config/axiosClient";
import { queryClient } from "@/providers/TanstackProvider";
import { useMutation, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { toast } from "react-toastify";

const getParentDetailClient = async () => {
    const response = await apiClient.get<ICommonResponse<IParentDetail>>(`/api/v1/client/account/parent`);
    return response.data;
}
export const useGetParentDetailClient = () => {
    return useQuery<ICommonResponse<IParentDetail>, AxiosError>({
        queryKey: ['parentDetail'],
        queryFn: () => getParentDetailClient(),
    });
};
// const addParent = async (data: IParentAdd) => {
//     const response = await apiClient.post('/api/v1/client/account/parent', data);
//     return response.data;
// }
// export const useAddParent = (callBack: any) => {
//     return useMutation(
//         {
//             mutationFn: (data: IParentAdd) => addParent(data),
//             onSuccess: (result) => {
//                 queryClient.invalidateQueries({ queryKey: ['parentList'] });
//                 toast.success(result.message);
//                 callBack();
//             },
//             onError: (error: any) => {
//                 if (error.response && error.response.data && typeof error.response.data === 'object') {
//                     const response: ICommonResponse<any> = error.response.data;
//                     toast.error(error.response.data.message || 'An error occurred')
//                 } else {
//                     // Handle any other errors
//                     toast.error('An error occurred')
//                 }
//             }
//         }
//     )
// };

const updateParentClient = async (data: IParentUpdate) => {
    const response = await apiClient.put(`/api/v1/client/account/parent/${data.id}`, data);
    return response.data;
}
export const useUpdateParentClient = (callBackSuccess: any) => {
    return useMutation(
        {
            mutationFn: (data: IParentUpdate) => updateParentClient(data),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['parentDetail'] });
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

const deleteParentClient = async (id: number) => {
    const response = await apiClient.delete(`/api/v1/client/account/parent/${id}`);
    return response.data;
}
export const useDeleteParentClient = () => {
    return useMutation(
        {
            mutationFn: (id: number) => deleteParentClient(id),
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

const getListStudentClient = async (params: IGetListStudentParams) => {
    const response = await apiClient.get<ICommonResponse<Page<IStudent>>>('/api/v1/client/account/student/pagination', { params });
    return response.data;
}
export const useGetListStudentClient = (params: IGetListStudentParams) => {
    return useQuery<ICommonResponse<Page<IStudent>>, AxiosError>({
        queryKey: ['studentList', params],
        queryFn: () => getListStudentClient(params)
    });
};

const getStudentDetailClient = async (id: number) => {
    const response = await apiClient.get<ICommonResponse<IStudentDetail>>(`/api/v1/admin/account/student/${id}`);
    return response.data;
}

export const useGetStudentDetailClient = (id: any) => {
    return useQuery<ICommonResponse<IStudentDetail>, AxiosError>({
        queryKey: ['studentDetail', id],
        queryFn: () => getStudentDetailClient(id),
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

const addStudentClient = async (data: IStudenAdd) => {
    const response = await apiClient.post('/api/v1/client/account/student', data);
    return response.data;
}
export const useAddStudentClient = (callBack:any) => {
    return useMutation(
        {
            mutationFn: (data: IStudenAddClient) => addStudentClient(data),
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

const updateStudentClient = async (data: IStudentUpdate) => {
    const response = await apiClient.put(`/api/v1/client/account/student/${data.id}`, data);
    return response.data;
}
export const useUpdateStudentClient = (callBackSuccess: any) => {
    return useMutation(
        {
            mutationFn: (data: IStudentUpdate) => updateStudentClient(data),
            onSuccess: (result) => {
                queryClient.invalidateQueries({ queryKey: ['studentList'] });
                queryClient.invalidateQueries({ queryKey: ['studentDetail'] });
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

const deleteStudentClient = async (id: number) => {
    const response = await apiClient.delete(`/api/v1/client/account/student/${id}`);
    return response.data;
}
export const useDeleteStudentClient = () => {
    return useMutation(
        {
            mutationFn: (id: number) => deleteStudentClient(id),
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