import { useQuery, useMutation } from 'react-query';
import { toast } from 'react-toastify';
import { classOpenedService } from 'repositories/classOpenedRepository';

export const useClassOpenedData = () => {
  const { data: classOpeneds, isLoading, error, refetch } = useQuery(
    'classOpeneds',
    classOpenedService.getAllClassOpeneds,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );

  const clearAllMutation = useMutation(classOpenedService.clearAll, {
    onSuccess: () => {
      refetch();
      toast.success('Xóa danh sách lớp thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa danh sách lớp');
    }
  });

  const importExcelMutation = useMutation(classOpenedService.importExcel, {
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi import danh sách lớp');
    }
  });

  const exportConflictsMutation = useMutation(classOpenedService.exportConflicts, {
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
      toast.error('Có lỗi xảy ra khi tải xuống danh sách xung đột');
    }
  });

  return {
    classOpeneds: classOpeneds?.data || [],
    isLoading,
    error,
    clearAll: clearAllMutation.mutateAsync,
    importExcel: importExcelMutation.mutateAsync,
    isClearing: clearAllMutation.isLoading,
    isImporting: importExcelMutation.isLoading,
    refetchClassOpeneds: refetch,
    exportConflicts: exportConflictsMutation.mutateAsync,
    isExporting: exportConflictsMutation.isLoading,
  };
};
