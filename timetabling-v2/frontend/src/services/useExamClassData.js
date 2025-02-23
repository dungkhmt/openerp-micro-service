import { useQuery, useMutation } from 'react-query';
import { toast } from 'react-toastify';
import { examClassService } from 'repositories/examClassRepository'

export const useExamClassData = () => {
  const { data: classOpeneds, isLoading, error, refetch } = useQuery(
    'examClasses',
    examClassService.getAllExamClass,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );

  const deleteExamClassMutation = useMutation(examClassService.deleteExamClasses, {
    onSuccess: () => {
      refetch();
      toast.success('Xóa lớp thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa lớp');
    }
  });

  const importExcelMutation = useMutation(examClassService.importExcel, {
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi import danh sách lớp');
    }
  });

  const exportConflictsMutation = useMutation(examClassService.exportExcel, {
    onSuccess: (response) => {
      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "Class_Conflict_List.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    onError: (error) => {
      console.log(error);
      toast.error('Có lỗi xảy ra khi tải xuống danh sách xung đột');
    }
  });

  const exportAllClassesMutation = useMutation(examClassService.exportExcel, {
    onSuccess: (response) => {
      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "Class_List.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    onError: (error) => {
      console.log(error);
      toast.error('Có lỗi xảy ra khi tải xuống danh sách lớp');
    }
  });

  const updateExamClass = useMutation(examClassService.updateExamClass, {
    onSuccess: () => {
      refetch();
      toast.success('Cập nhật lớp học thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi cập nhật lớp học');
    }
  });

  return {
    examClasses: classOpeneds?.data || [],
    isLoading,
    error,
    deleteExamClasses: deleteExamClassMutation.mutateAsync,
    importExcel: importExcelMutation.mutateAsync,
    isClearing: deleteExamClassMutation.isLoading,
    isImporting: importExcelMutation.isLoading,
    refetchClassOpeneds: refetch,
    exportConflict: exportConflictsMutation.mutateAsync,
    exportClasses: exportAllClassesMutation.mutateAsync,
    isExportingConflicts: exportConflictsMutation.isLoading,
    isExportingClasses: exportAllClassesMutation.isLoading,
    updateExamClass: updateExamClass.mutateAsync,
  };
};
