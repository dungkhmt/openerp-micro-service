import { useQuery, useMutation } from "react-query";
import { toast } from "react-toastify";
import { classroomService } from "repositories/classroomRepository";

export const useClassroomData = () => {
  const {
    data: classrooms,
    isLoading,
    error,
    refetch,
  } = useQuery("classrooms", classroomService.getAllClassrooms, {
    staleTime: 5 * 60 * 1000,
    cacheTime: 30 * 60 * 1000,
  });

  const { data: buildings, isLoading: isBuildingsLoading } = useQuery(
    "buildings",
    classroomService.getAllBuildings,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000,
    }
  );

  const createMutation = useMutation(classroomService.createClassroom, {
    onSuccess: () => {
      refetch(); // Add explicit refetch
      toast.success("Tạo phòng học mới thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi tạo phòng học");
    },
  });

  const updateMutation = useMutation(classroomService.updateClassroom, {
    onSuccess: () => {
      refetch(); // Add explicit refetch
      toast.success("Cập nhật phòng học thành công!");
    },
    onError: (error) => {
      toast.error(
        error.response?.data || "Có lỗi xảy ra khi cập nhật phòng học"
      );
    },
  });

  const deleteMutation = useMutation(classroomService.deleteClassroom, {
    onSuccess: () => {
      refetch(); // Add explicit refetch
      toast.success("Xóa phòng học thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi xóa phòng học");
    },
  });

  const importMutation = useMutation(classroomService.importExcel, {
    onSuccess: () => {
      refetch(); // Add explicit refetch
      toast.success("Import danh sách phòng học thành công!");
    },
    onError: (error) => {
      toast.error(
        error.response?.data || "Có lỗi xảy ra khi import danh sách phòng học"
      );
    },
  });

  const clearAllMutation = useMutation(classroomService.clearAll, {
    onSuccess: () => {
      refetch();
      toast.success("Xóa tất cả phòng học thành công!");
    },
    onError: (error) => {
      toast.error(
        error.response?.data || "Có lỗi xảy ra khi xóa tất cả phòng học"
      );
    },
  });

  return {
    classrooms: classrooms?.data || [],
    isLoading,
    error,
    buildings: buildings?.data || [],
    isBuildingsLoading,
    createClassroom: createMutation.mutateAsync,
    updateClassroom: updateMutation.mutateAsync,
    deleteClassroom: deleteMutation.mutateAsync,
    importExcel: importMutation.mutateAsync,
    isCreating: createMutation.isLoading,
    isUpdating: updateMutation.isLoading,
    isDeleting: deleteMutation.isLoading,
    isImporting: importMutation.isLoading,
    refetchClassrooms: refetch,
    clearAll: clearAllMutation.mutateAsync,
    isClearing: clearAllMutation.isLoading,
  };
};
