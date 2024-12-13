import { useQuery, useMutation } from 'react-query';
import { toast } from 'react-toastify';
import { courseService } from "repositories/courseRepository";
import { queryClient } from 'queryClient';

export const useCourseData = () => {
  const { data: courses, isLoading, error } = useQuery(
    'courses',
    courseService.getAllCourses,
    {
      staleTime: 5 * 60 * 1000, 
      cacheTime: 30 * 60 * 1000, 
      select: (response) => response.data
    }
  );

  const createMutation = useMutation(courseService.createCourse, {
    onSuccess: () => {
      queryClient.invalidateQueries('courses');
      toast.success('Tạo môn học mới thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi tạo môn học');
    }
  });

  const updateMutation = useMutation(courseService.updateCourse, {
    onSuccess: () => {
      queryClient.invalidateQueries('courses');
      toast.success('Cập nhật môn học thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi cập nhật môn học');
    }
  });

  const deleteMutation = useMutation(courseService.deleteCourse, {
    onSuccess: () => {
      queryClient.invalidateQueries('courses');
      toast.success('Xóa môn học thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa môn học');
    }
  });

  return {
    courses: courses || [],
    isLoading,
    error,
    createCourse: createMutation.mutateAsync,
    updateCourse: updateMutation.mutateAsync,
    deleteCourse: deleteMutation.mutateAsync,
    isCreating: createMutation.isLoading,
    isUpdating: updateMutation.isLoading,
    isDeleting: deleteMutation.isLoading
  };
};
