import { useQuery } from 'react-query';
import { weekDayService } from "repositories/weekDayRepository";

export const useWeekDayData = () => {
  const { data: weekDays, isLoading } = useQuery(
    'weekDays',
    weekDayService.getAllWeekDays,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );

  return {
    weekDays: weekDays?.data || [],
    isLoading
  };
};