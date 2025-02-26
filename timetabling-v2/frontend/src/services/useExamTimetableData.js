import { useQuery, useMutation} from 'react-query';
import { toast } from 'react-toastify';
import { examTimetableService } from "repositories/examTimetableRepository";
import { queryClient } from 'queryClient';

export const useExamTimetableData = (examPlanId = null) => {
  
  const { data: examTimetables, isLoading, error } = useQuery(
    'examTimetables',
    () => examTimetableService.getAllExamTimetables(examPlanId),
    {
      staleTime: 5 * 60 * 1000, // Cache for 5 minutes
      cacheTime: 30 * 60 * 1000, // Keep cache for 30 minutes
      enabled: !!examPlanId,
    }
  );

  const createMutation = useMutation(examTimetableService.createExamTimetable, {
    onSuccess: () => {
      queryClient.invalidateQueries('examTimetables');
      toast.success('Tạo kế hoạc thi mới thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi tạo kế hoạch thi');
    }
  });

  const updateMutation = useMutation(examTimetableService.updateExamTimetable, {
    onSuccess: () => {
      queryClient.invalidateQueries('examTimetables');
      toast.success('Cập nhật kế hoạc thi thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi cập nhật kế hoạc thi');
    }
  });

  const deleteMutation = useMutation(examTimetableService.deleteExamTimetable, {
    onSuccess: () => {
      queryClient.invalidateQueries('examTimetables');
      toast.success('Xóa kế hoạc thi thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa kế hoạc thi');
    }
  });

  return {
    examTimetables: examTimetables?.data || [],
    isLoading,
    error,
    createExamTimetable: createMutation.mutateAsync,
    updateExamTimetable: updateMutation.mutateAsync,
    deleteExamTimetable: deleteMutation.mutateAsync,
    isCreating: createMutation.isLoading,
    isUpdating: updateMutation.isLoading,
    isDeleting: deleteMutation.isLoading,
  };
};
