import { useQuery } from 'react-query';
import { classPeriodService } from "repositories/classPeriodRepository";

export const useClassPeriodData = () => {
  const { data: classPeriods, isLoading } = useQuery(
    'classPeriods',
    classPeriodService.getAllClassPeriods,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );

  return {
    classPeriods: classPeriods?.data || [],
    isLoading
  };
};
