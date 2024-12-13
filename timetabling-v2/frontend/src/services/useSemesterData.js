import { useQuery, useMutation} from 'react-query';
import { toast } from 'react-toastify';
import { semesterService } from "repositories/semesterRepository";
import { queryClient } from 'queryClient';

export const useSemesterData = () => {
  
  const { data: semesters, isLoading, error } = useQuery(
    'semesters',
    semesterService.getAllSemesters,
    {
      staleTime: 5 * 60 * 1000, // Cache for 5 minutes
      cacheTime: 30 * 60 * 1000 // Keep cache for 30 minutes
    }
  );

  const createMutation = useMutation(semesterService.createSemester, {
    onSuccess: () => {
      queryClient.invalidateQueries('semesters');
      toast.success('Tạo kỳ học mới thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi tạo kỳ học');
    }
  });

  const updateMutation = useMutation(semesterService.updateSemester, {
    onSuccess: () => {
      queryClient.invalidateQueries('semesters');
      toast.success('Cập nhật kỳ học thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi cập nhật kỳ học');
    }
  });

  const deleteMutation = useMutation(semesterService.deleteSemester, {
    onSuccess: () => {
      queryClient.invalidateQueries('semesters');
      toast.success('Xóa kỳ học thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa kỳ học');
    }
  });

  return {
    semesters: semesters?.data || [],
    isLoading,
    error,
    createSemester: createMutation.mutateAsync,
    updateSemester: updateMutation.mutateAsync,
    deleteSemester: deleteMutation.mutateAsync,
    isCreating: createMutation.isLoading,
    isUpdating: updateMutation.isLoading,
    isDeleting: deleteMutation.isLoading
  };
};