import { useState, useCallback } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';
import { generalScheduleRepository } from '../repositories/generalScheduleRepository';

export const useGeneralSchedule = () => {
  const queryClient = useQueryClient();
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [selectedRows, setSelectedRows] = useState([]);
  const [isOpenClassroomDialog, setOpenClassroomDialog] = useState(false);
  const [isOpenTimeslotDialog, setOpenTimeslotDialog] = useState(false);
  const [classroomTimeLimit, setClassroomTimeLimit] = useState(5);
  const [timeSlotTimeLimit, setTimeSlotTimeLimit] = useState(5);
  const [loading, setLoading] = useState(false);

  const { data: classes = [], isLoading: isClassesLoading } = useQuery(
    ["generalClasses", selectedSemester?.semester, selectedGroup?.groupName],
    () => {
      setLoading(true);
      return generalScheduleRepository.getClasses(
        selectedSemester?.semester,
        selectedGroup?.groupName
      ).finally(() => setLoading(false));
    },
    {
      enabled: !!selectedSemester,
      staleTime: Infinity, // Keep data fresh indefinitely
      cacheTime: 30 * 60 * 1000, // Cache for 30 minutes
      refetchOnWindowFocus: false,
      refetchOnMount: false,
      refetchOnReconnect: false,
      retry: 2,
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
      select: (data) => {
        if (!Array.isArray(data)) return [];
        
        let generalClasses = [];
        data.forEach((classObj) => {
          if (classObj.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(JSON.stringify({
                ...classObj,
                ...timeSlot,
                classCode: classObj.classCode,
                roomReservationId: timeSlot.id,
                id: classObj.id + `-${index + 1}`,
                crew: classObj.crew,
              }));
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          } else {
            generalClasses.push({
              ...classObj,
              generalClassId: String(classObj.generalClassId || '')
            });
          }
        });

        return generalClasses;
      }
    }
  );

  const { data: classesNoSchedule = [], isLoading: isClassesNoScheduleLoading } = useQuery(
    ["generalClassesNoSchedule", selectedSemester?.semester, selectedGroup?.groupName],
    () => generalScheduleRepository.getClassesNoSchedule(
      selectedSemester?.semester,
      selectedGroup?.groupName
    ),
    {
      enabled: !!selectedSemester,
      staleTime: Infinity,
      cacheTime: 30 * 60 * 1000,
      refetchOnWindowFocus: false
    }
  );

  const forceRefetch = useCallback(() => {
    setLoading(true);
    return generalScheduleRepository.getClasses(
      selectedSemester?.semester,
      selectedGroup?.groupName,
      true // Force refresh
    ).then((data) => {
      queryClient.setQueryData(
        ["generalClasses", selectedSemester?.semester, selectedGroup?.groupName],
        data
      );
    }).finally(() => setLoading(false));
  }, [selectedSemester, selectedGroup, queryClient]);

  const resetMutation = useMutation(
    ({ semester, ids }) => generalScheduleRepository.resetSchedule(semester, ids),
    {
      onSuccess: () => {
        forceRefetch();
        setSelectedRows([]);
        toast.success('Reset thời khóa biểu thành công!');
      },
      onError: (error) => {
        toast.error(error.response?.data || 'Có lỗi khi reset thời khóa biểu!');
      }
    }
  );

  const autoScheduleTimeMutation = useMutation(
    ({ semester, groupName, timeLimit }) => 
      generalScheduleRepository.autoScheduleTime(semester, groupName, timeLimit),
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: (response) => {
        forceRefetch(); // Force reload data instead of manual update
        setSelectedRows([]);
        setOpenTimeslotDialog(false);
        toast.success("Tự động xếp thời khóa biểu thành công!");
      },
      onError: (error) => {
        if (error.response?.status === 410 || error.response?.status === 420) {
          toast.error(error.response.data);
        } else {
          console.log('Auto schedule error:', error);
          toast.error("Có lỗi khi tự động thời khóa biểu!");
        }
        setOpenTimeslotDialog(false);
      }
    }
  );

  const autoScheduleRoomMutation = useMutation(
    ({ semester, groupName, timeLimit }) => 
      generalScheduleRepository.autoScheduleRoom(semester, groupName, timeLimit),
    {
      onSuccess: () => {
        forceRefetch();
        setSelectedRows([]);
        setOpenClassroomDialog(false);
        toast.success('Tự động xếp phòng thành công!');
      },
      onError: (error) => {
        const message = error.response?.status === 410 ? error.response.data 
          : 'Có lỗi khi tự động xếp phòng!';
        toast.error(message);
      }
    }
  );

  const saveTimeSlotMutation = useMutation(
    ([semester, saveRequest]) => generalScheduleRepository.updateTimeSlot(
      semester, 
      saveRequest
    ),
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: () => {
        forceRefetch();
        toast.success("Lưu TKB thành công!");
      },
      onError: (error) => {
        if (error?.response?.status === 410) {
          forceRefetch(); // Even on error 410, force reload
          toast.warn(error.response?.data || 'Dữ liệu đã thay đổi');
        } else if (error?.response?.status === 420) {
          toast.error(error.response?.data || 'Lỗi xác thực dữ liệu');
        } else {
          console.error('Save time slot error:', error);
          toast.error(error?.response?.data || error?.message || "Có lỗi khi lưu TKB!");
        }
      }
    }
  );

  const addTimeSlotMutation = useMutation(
    (params) => generalScheduleRepository.addTimeSlot(
      selectedSemester?.semester,
      params
    ),
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: () => {
        forceRefetch();
        toast.success("Thêm ca học thành công!");
      },
      onError: (error) => {
        if (error.response?.status === 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Thêm ca học thất bại!");
        }
      }
    }
  );

  const removeTimeSlotMutation = useMutation(
    (params) => 
      generalScheduleRepository.removeTimeSlot(
        selectedSemester?.semester,
        params
      ),
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: () => {
        forceRefetch();
        toast.success("Xóa ca học thành công!");
      },
      onError: (error) => {
        if (error.response?.status === 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Xóa ca học thất bại!");
        }
      }
    }
  );

  const exportExcelMutation = useMutation(
    (semester) => generalScheduleRepository.exportExcel(semester),
    {
      onSuccess: (response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `timetable_${selectedSemester?.semester}.xlsx`);
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
        toast.success('Tải xuống thành công!');
      },
      onError: (error) => {
        toast.error(error.response?.data || 'Có lỗi khi tải xuống file!');
      }
    }
  );

  const deleteClassesMutation = useMutation(
    (semester) => generalScheduleRepository.deleteClasses(semester),
    {
      onSuccess: () => {
        forceRefetch();
        toast.success('Xóa danh sách thành công!');
      },
      onError: (error) => {
        toast.error(error.response?.data || 'Có lỗi khi xóa danh sách!');
      }
    }
  );

  const uploadFileMutation = useMutation(
    ([semester, file]) => generalScheduleRepository.uploadFile(semester, file),
    {
      onSuccess: (response) => {
        forceRefetch();
        toast.success('Upload file thành công!');
        return response;
      },
      onError: (error) => {
        toast.error(error.response?.data || 'Có lỗi khi upload file!');
      }
    }
  );

  const deleteBySemesterMutation = useMutation(
    (semester) => generalScheduleRepository.deleteBySemester(semester),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(["generalClassesNoSchedule", selectedSemester?.semester]);
        toast.success('Xóa danh sách lớp thành công!');
      },
      onError: (error) => {
        toast.error(error.response?.data || 'Xóa danh sách lớp thất bại!');
      }
    }
  );

  const handleRefreshClasses = useCallback(() => {
    forceRefetch();
  }, [forceRefetch]);

  const setClassesNoSchedule = useCallback((newClasses) => {
    queryClient.setQueryData(
      ["generalClassesNoSchedule", selectedSemester?.semester, selectedGroup?.groupName],
      newClasses
    );
  }, [selectedSemester, selectedGroup, queryClient]);

  return {
    states: {
      selectedSemester,
      selectedGroup,
      selectedRows,
      isOpenClassroomDialog,
      isOpenTimeslotDialog,
      classroomTimeLimit,
      timeSlotTimeLimit,
      classes,
      isLoading: isClassesLoading,
      isResetLoading: resetMutation.isLoading,
      isAutoSaveLoading:
        autoScheduleTimeMutation.isLoading ||
        autoScheduleRoomMutation.isLoading,
      loading: loading || isClassesLoading,
      isSavingTimeSlot: saveTimeSlotMutation.isLoading,
      isAddingTimeSlot: addTimeSlotMutation.isLoading,
      isRemovingTimeSlot: removeTimeSlotMutation.isLoading,
      isExportExcelLoading: exportExcelMutation.isLoading,
      isDeleting: deleteClassesMutation.isLoading,
      isUploading: uploadFileMutation.isLoading,
      classesNoSchedule,
      isClassesNoScheduleLoading,
      isDeletingBySemester: deleteBySemesterMutation.isLoading,
    },
    setters: {
      setSelectedSemester,
      setSelectedGroup,
      setSelectedRows,
      setOpenClassroomDialog,
      setOpenTimeslotDialog,
      setClassroomTimeLimit,
      setTimeSlotTimeLimit,
      setClassesNoSchedule,
    },
    handlers: {
      handleResetTimeTabling: () => {
        resetMutation.mutate({
          semester: selectedSemester?.semester,
          ids: selectedRows,
        });
      },
      handleAutoScheduleTimeSlotTimeTabling: () => {
        autoScheduleTimeMutation.mutate({
          semester: selectedSemester?.semester,
          groupName: selectedGroup?.groupName,
          timeLimit: timeSlotTimeLimit,
        });
      },
      handleAutoScheduleClassroomTimeTabling: () => {
        autoScheduleRoomMutation.mutate({
          semester: selectedSemester?.semester,
          groupName: selectedGroup?.groupName,
          timeLimit: classroomTimeLimit,
        });
      },
      handleRefreshClasses, // Use new refresh handler
      handleSaveTimeSlot: (semester, data) =>
        saveTimeSlotMutation.mutateAsync([semester, data]),
      handleAddTimeSlot: addTimeSlotMutation.mutateAsync,
      handleRemoveTimeSlot: removeTimeSlotMutation.mutateAsync,
      handleExportTimeTabling: (semester) => {
        if (!semester) {
          toast.error("Vui lòng chọn học kỳ!");
          return;
        }
        exportExcelMutation.mutate(semester);
      },
      handleDeleteClasses: () => {
        if (!selectedSemester?.semester) {
          toast.error("Vui lòng chọn học kỳ!");
          return;
        }
        deleteClassesMutation.mutate(selectedSemester.semester);
      },
      handleUploadFile: (file) => {
        if (!selectedSemester?.semester) {
          toast.error("Vui lòng chọn học kỳ!");
          return;
        }
        if (!file) {
          toast.error("Vui lòng chọn file!");
          return;
        }
        return uploadFileMutation.mutateAsync([selectedSemester.semester, file]);
      },
      handleDeleteBySemester: () => {
        if (!selectedSemester?.semester) {
          toast.error("Vui lòng chọn học kỳ!");
          return;
        }
        deleteBySemesterMutation.mutate(selectedSemester.semester);
      },
    },
  };
};
