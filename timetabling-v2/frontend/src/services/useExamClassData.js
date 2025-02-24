import { useQuery, useMutation } from 'react-query';
import { toast } from 'react-toastify';
import { examClassService } from 'repositories/examClassRepository'

export const useExamClassData = (examPlanId = null) => {
  const { data: classOpeneds, isLoading, error, refetch } = useQuery(
    ['examClasses', examPlanId],
    () => examClassService.getAllExamClass(examPlanId),
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000,
      enabled: !!examPlanId
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

  const importExcelMutation = useMutation(
    (formData) => examClassService.importExcel(formData,examPlanId),
    {
      onError: (error) => {
        toast.error(error.response?.data || 'Có lỗi xảy ra khi import danh sách lớp');
      }
    }
  );

  const exportConflictsMutation = useMutation(examClassService.exportExcel, {
    onSuccess: (response) => {
      refetch();
      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "Danh_sách_lớp_thi_xung_đột.xlsx";
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
      refetch();
      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "Danh_sách_lớp_thi.xlsx";
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

  const createExamClass = useMutation(examClassService.createExamClass, {
    onSuccess: () => {
      refetch();
      toast.success('Tạo lớp học mới thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi tạo lớp học');
    }
  });

  const downloadSample = useMutation(examClassService.downloadSample, {
    onSuccess: (response) => {
      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "File_Lớp_thi_mẫu.xlsx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    onError: (error) => {
      console.log(error);
      toast.error('Có lỗi xảy ra khi tải xuống danh sách lớp');
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
    exportConflicts: exportConflictsMutation.mutateAsync,
    exportClasses: exportAllClassesMutation.mutateAsync,
    isExportingConflicts: exportConflictsMutation.isLoading,
    isExportingClasses: exportAllClassesMutation.isLoading,
    updateExamClass: updateExamClass.mutateAsync,
    createExamClass: createExamClass.mutateAsync,
    downloadSample: downloadSample.mutateAsync
  };
};
