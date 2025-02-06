import { useQuery } from 'react-query';
import { classRegistrationServices } from "repositories/classRegistrationRepository";

export const useClasses = (semester, page, limit, search) => {
  return useQuery(
    ['classes', semester, page, limit, search],
    () => classRegistrationServices.getClassesBySemester(semester, page, limit, search),
    {
      enabled: !!semester,
      keepPreviousData: true,
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );
};

export const useRegisteredClasses = (semester) => {
  return useQuery(
    ['registeredClasses', semester],
    () => classRegistrationServices.getMyRegisteredClasses(semester),
    {
      enabled: !!semester,
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );
};

export const useSemesters = () => {
  return useQuery(
    'semesters',
    classRegistrationServices.getAllSemesters,
    {
      staleTime: 30 * 60 * 1000,
      cacheTime: 60 * 60 * 1000
    }
  );
};

export const useCurrentSemester = () => {
  return useQuery(
    'currentSemester',
    classRegistrationServices.getCurrentSemester,
    {
      staleTime: 30 * 60 * 1000,
      cacheTime: 60 * 60 * 1000
    }
  );
};
