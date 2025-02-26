import { useQuery, useMutation} from 'react-query';
import { toast } from 'react-toastify';
import { examPlanService } from "repositories/examPlanRepository";
import { queryClient } from 'queryClient';

export const useExamPlanData = () => {
  
  const { data: examPlans, isLoading, error } = useQuery(
    'examPlans',
    examPlanService.getAllExamPlans,
    {
      staleTime: 5 * 60 * 1000, // Cache for 5 minutes
      cacheTime: 30 * 60 * 1000 // Keep cache for 30 minutes
    }
  );

  const createMutation = useMutation(examPlanService.createExamPlan, {
    onSuccess: () => {
      queryClient.invalidateQueries('examPlans');
      toast.success('Tạo kế hoạc thi mới thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi tạo kế hoạc thi');
    }
  });

  const updateMutation = useMutation(examPlanService.updateExamPlan, {
    onSuccess: () => {
      queryClient.invalidateQueries('examPlans');
      toast.success('Cập nhật kế hoạc thi thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi cập nhật kế hoạc thi');
    }
  });

  const deleteMutation = useMutation(examPlanService.deleteExamPlan, {
    onSuccess: () => {
      queryClient.invalidateQueries('examPlans');
      toast.success('Xóa kế hoạc thi thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa kế hoạc thi');
    }
  });

  return {
    examPlans: examPlans?.data || [],
    isLoading,
    error,
    createExamPlan: createMutation.mutateAsync,
    updateExamPlan: updateMutation.mutateAsync,
    deleteExamPlan: deleteMutation.mutateAsync,
    isCreating: createMutation.isLoading,
    isUpdating: updateMutation.isLoading,
    isDeleting: deleteMutation.isLoading
  };
};
