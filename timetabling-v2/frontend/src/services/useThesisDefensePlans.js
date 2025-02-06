
import { useQuery } from 'react-query';
import { toast } from 'react-toastify';
import { thesisDefensePlanServices } from "repositories/thesisDefensePlanRepository";

export const useThesisDefensePlans = () => {
  const { data, isLoading, error, refetch } = useQuery(
    'thesisDefensePlans',
    thesisDefensePlanServices.getAllPlans,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000,
      onError: () => {
        toast.error('Có lỗi khi lấy danh sách đợt bảo vệ!');
      }
    }
  );

  return {
    plans: data?.data || [],
    isLoading,
    error,
    refetch
  };
};