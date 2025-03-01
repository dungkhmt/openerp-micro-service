import { useState, useCallback } from "react";
import { useQuery, useMutation, useQueryClient } from "react-query";
import { toast } from "react-toastify";
import { generalScheduleRepository } from "../repositories/generalScheduleRepository";

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
  const [isOpenSelectedDialog, setOpenSelectedDialog] = useState(false);
  const [selectedTimeLimit, setSelectedTimeLimit] = useState(5);

  const queryKey = [
    "generalClasses",
    selectedSemester?.semester,
    selectedGroup?.groupName,
  ];

  const { data: classes = [], isLoading: isClassesLoading } = useQuery(
    queryKey,
    () => {
      setLoading(true);
      return generalScheduleRepository
        .getClasses(selectedSemester?.semester, selectedGroup?.groupName)
        .finally(() => setLoading(false));
    },
    {
      enabled: !!selectedSemester,
      staleTime: 30 * 1000, // Data becomes stale after 30 seconds
      cacheTime: 5 * 60 * 1000, // Cache for 5 minutes
      refetchOnWindowFocus: true, // Refetch when window regains focus
      refetchOnMount: true,
      refetchOnReconnect: true,
      refetchInterval: 60 * 1000, // Poll every 60 seconds
      retry: 3,
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 10000),
      select: (data) => {
        if (!Array.isArray(data)) return [];

        let generalClasses = [];
        data.forEach((classObj) => {
          // Calculate total used duration from timeSlots
          const usedDuration =
            classObj.timeSlots?.reduce((total, slot) => {
              return total + (slot.duration || 0);
            }, 0) || 0;

          // Calculate remaining duration for parent class
          const remainingDuration = classObj.duration - usedDuration;

          // Handle child classes (time slots)
          if (classObj.timeSlots && classObj.timeSlots.length > 0) {
            // Only add valid child classes (with duration not null)
            classObj.timeSlots.forEach((timeSlot, index) => {
              if (timeSlot.duration !== null) {
                const cloneObj = JSON.parse(
                  JSON.stringify({
                    ...classObj,
                    ...timeSlot,
                    classCode: classObj.classCode,
                    roomReservationId: timeSlot.id,
                    id: classObj.id + `-${index + 1}`,
                    crew: classObj.crew,
                    duration: timeSlot.duration,
                    isChild: true,
                    parentId: classObj.id,
                  })
                );
                delete cloneObj.timeSlots;
                generalClasses.push(cloneObj);
              }
            });
          }

          if (remainingDuration > 0 || !classObj.timeSlots?.length) {
            generalClasses.push({
              ...classObj,
              generalClassId: String(
                classObj.generalClassId || classObj.id || ""
              ),
              duration: remainingDuration,
              isParent: true,
            });
          }
        });

        // Sort to group parent-child together
        generalClasses.sort((a, b) => {
          // First sort by parent ID to group families together
          const parentIdA = a.parentId || a.id;
          const parentIdB = b.parentId || b.id;

          if (parentIdA !== parentIdB) {
            return parentIdA - parentIdB;
          }

          // Then put parents before children
          if (a.isParent && !b.isParent) return -1;
          if (!a.isParent && b.isParent) return 1;

          return 0;
        });

        return generalClasses;
      },
    }
  );

  const {
    data: classesNoSchedule = [],
    isLoading: isClassesNoScheduleLoading,
  } = useQuery(
    [
      "generalClassesNoSchedule",
      selectedSemester?.semester,
      selectedGroup?.groupName,
    ],
    () =>
      generalScheduleRepository.getClassesNoSchedule(
        selectedSemester?.semester,
        selectedGroup?.groupName
      ),
    {
      enabled: !!selectedSemester,
      staleTime: 30 * 1000,
      cacheTime: 5 * 60 * 1000,
      refetchOnWindowFocus: true,
      refetchOnMount: true,
      refetchOnReconnect: true,
      refetchInterval: 60 * 1000,
      retry: 3,
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 10000),
    }
  );

  const forceRefetch = useCallback(() => {
    setLoading(true);
    return generalScheduleRepository
      .getClasses(selectedSemester?.semester, selectedGroup?.groupName, true)
      .then((data) => {
        queryClient.setQueryData(
          [
            "generalClasses",
            selectedSemester?.semester,
            selectedGroup?.groupName,
          ],
          data
        );
      })
      .finally(() => setLoading(false));
  }, [selectedSemester, selectedGroup, queryClient]);

  const resetMutation = useMutation(
    ({ semester, ids }) =>
      generalScheduleRepository.resetSchedule(semester, ids),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(queryKey);
        setSelectedRows([]);
        toast.success("Reset thời khóa biểu thành công!");
      },
      onError: (error) => {
        toast.error(error.response?.data || "Có lỗi khi reset thời khóa biểu!");
      },
    }
  );

  const autoScheduleTimeMutation = useMutation(
    ({ semester, groupName, timeLimit }) =>
      generalScheduleRepository.autoScheduleTime(
        semester,
        groupName,
        timeLimit
      ),
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: (response) => {
        queryClient.invalidateQueries(queryKey); // Force refetch
        setSelectedRows([]);
        setOpenTimeslotDialog(false);
        toast.success("Tự động xếp thời khóa biểu thành công!");
      },
      onError: (error) => {
        if (error.response?.status === 410 || error.response?.status === 420) {
          toast.error(error.response.data);
        } else {
          console.log("Auto schedule error:", error);
          toast.error("Có lỗi khi tự động thời khóa biểu!");
        }
        setOpenTimeslotDialog(false);
      },
    }
  );

  const autoScheduleRoomMutation = useMutation(
    ({ semester, groupName, timeLimit }) =>
      generalScheduleRepository.autoScheduleRoom(
        semester,
        groupName,
        timeLimit
      ),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(queryKey); // Force refetch
        setSelectedRows([]);
        setOpenClassroomDialog(false);
        toast.success("Tự động xếp phòng thành công!");
      },
      onError: (error) => {
        const message =
          error.response?.status === 410
            ? error.response.data
            : "Có lỗi khi tự động xếp phòng!";
        toast.error(message);
      },
    }
  );

  const saveTimeSlotMutation = useMutation(
    ([semester, saveRequest]) => {
      if (saveRequest.endTime > 6) {
        toast.error("Thời gian lớp học không hợp lệ!");
        return;
      }
      return generalScheduleRepository.updateTimeSlot(
        semester,
        saveRequest,
        (error) => {
          toast.error(
            error.response?.data || "Lịch học đã thay đổi, không thể cập nhật!"
          );
        }
      );
    },
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: (response) => {
        if (response && response.status === 200) {
          queryClient.invalidateQueries(queryKey); // Force refetch
          toast.success("Lưu TKB thành công!");
        }
      },
      onError: (error) => {
        const message =
          error?.response?.data || error.message || "Có lỗi khi lưu TKB!";
        toast.error(message);
      },
    }
  );

  const addTimeSlotMutation = useMutation(
    (params) =>
      generalScheduleRepository.addTimeSlot(selectedSemester?.semester, params),
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: () => {
        queryClient.invalidateQueries(queryKey); // Force refetch
        toast.success("Thêm ca học thành công!");
      },
      onError: (error) => {
        if (error.response?.status === 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Thêm ca học thất bại!");
        }
      },
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
        queryClient.invalidateQueries(queryKey); // Force refetch
        toast.success("Xóa ca học thành công!");
      },
      onError: (error) => {
        if (error.response?.status === 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Xóa ca học thất bại!");
        }
      },
    }
  );

  const exportExcelMutation = useMutation(
    (semester) => generalScheduleRepository.exportExcel(semester),
    {
      onSuccess: (response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute(
          "download",
          `timetable_${selectedSemester?.semester}.xlsx`
        );
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
        toast.success("Tải xuống thành công!");
      },
      onError: (error) => {
        toast.error(error.response?.data || "Có lỗi khi tải xuống file!");
      },
    }
  );

  const deleteClassesMutation = useMutation(
    (semester) => generalScheduleRepository.deleteClasses(semester),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(queryKey); // Force refetch
        toast.success("Xóa danh sách thành công!");
      },
      onError: (error) => {
        toast.error(error.response?.data || "Có lỗi khi xóa danh sách!");
      },
    }
  );

  const uploadFileMutation = useMutation(
    ([semester, file]) => generalScheduleRepository.uploadFile(semester, file),
    {
      onSuccess: async () => {
        queryClient.invalidateQueries([
          "generalClassesNoSchedule",
          selectedSemester?.semester,
        ]);
        await forceRefetch();
        setSelectedRows([]);
        toast.success("Upload file thành công!");
      },
      onError: (error) => {
        toast.error(error.response?.data || "Có lỗi khi upload file!");
      },
    }
  );

  const deleteBySemesterMutation = useMutation(
    (semester) => generalScheduleRepository.deleteBySemester(semester),
    {
      onSuccess: () => {
        queryClient.invalidateQueries([
          "generalClassesNoSchedule",
          selectedSemester?.semester,
        ]);
        toast.success("Xóa danh sách lớp thành công!");
      },
      onError: (error) => {
        toast.error(error.response?.data || "Xóa danh sách lớp thất bại!");
      },
    }
  );

  const autoScheduleSelectedMutation = useMutation(
    ({ classIds, timeLimit, semester }) => {
      // Clean up classIds by removing the -[number] suffix if it exists
      const cleanClassIds = classIds.map((id) => id.split("-")[0]);

      return generalScheduleRepository.autoScheduleSelected(
        cleanClassIds,
        timeLimit,
        semester
      );
    },
    {
      onMutate: () => {
        setLoading(true);
      },
      onSettled: () => {
        setLoading(false);
      },
      onSuccess: () => {
        queryClient.invalidateQueries(queryKey); // Force refetch
        setSelectedRows([]);
        setOpenSelectedDialog(false);
        toast.success("Tự động xếp lịch các lớp đã chọn thành công!");
      },
      onError: (error) => {
        const message =
          error.response?.status === 410
            ? error.response.data
            : "Có lỗi khi tự động xếp lịch các lớp đã chọn!";
        toast.error(message);
      },
    }
  );

  const updateClassesGroupMutation = useMutation(
    (params) =>
      generalScheduleRepository.updateClassesGroup(
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
        queryClient.invalidateQueries(queryKey); // Force refetch
        toast.success("Thêm nhóm thành công!");
      },
      onError: (error) => {
        toast.error(
          error.response?.data ||
            "Thêm nhóm lỗi, nhóm đã có hoặc mã lớp không tồn tại!"
        );
      },
    }
  );

  const getClassGroups = useCallback(async (classId) => {
    if (!classId) {
      toast.error("Không có ID lớp học!");
      return [];
    }

    try {
      const data = await generalScheduleRepository.getClassGroups(classId);
      return data;
    } catch (error) {
      toast.error("Không thể tải danh sách nhóm lớp học!");
      console.error("Failed to fetch class groups", error);
      return [];
    }
  }, []);

  const handleRefreshClasses = useCallback(() => {
    forceRefetch();
  }, [forceRefetch]);

  const setClassesNoSchedule = useCallback(
    (newClasses) => {
      queryClient.setQueryData(
        [
          "generalClassesNoSchedule",
          selectedSemester?.semester,
          selectedGroup?.groupName,
        ],
        newClasses
      );
    },
    [selectedSemester, selectedGroup, queryClient]
  );

  const handleMutationSuccess = useCallback(async (message) => {
    await forceRefetch();
    setSelectedRows([]);
    toast.success(message);
  }, [forceRefetch]);

  const deleteByIdsMutation = useMutation(
    (ids) => generalScheduleRepository.deleteByIds(ids),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(queryKey); // Invalidate main query
        queryClient.invalidateQueries(["generalClassesNoSchedule", selectedSemester?.semester]); // Invalidate no schedule query
        setSelectedRows([]);
        toast.success("Xóa các lớp đã chọn thành công!");
      },
      onError: (error) => {
        toast.error(error.response?.data || "Có lỗi khi xóa các lớp đã chọn!");
      },
    }
  );

  const handleDeleteByIds = useCallback(() => {
    if (selectedRows.length === 0) {
      toast.error("Vui lòng chọn lớp cần xóa!");
      return;
    }

    const cleanIds = selectedRows.map(id => {
      if (typeof id === 'number') {
        return id;
      }
      const strId = String(id);
      return parseInt(strId.includes('-') ? strId.split('-')[0] : strId);
    }).filter(id => !isNaN(id));
    
    if (cleanIds.length === 0) {
      toast.error("Không tìm thấy ID hợp lệ!");
      return;
    }

    deleteByIdsMutation.mutate(cleanIds);
  }, [selectedRows, deleteByIdsMutation]);

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
      refetchSchedule: () => queryClient.refetchQueries(queryKey),
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
      isOpenSelectedDialog,
      selectedTimeLimit,
      isUpdatingClassesGroup: updateClassesGroupMutation.isLoading,
      isDeletingByIds: deleteByIdsMutation.isLoading,
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
      setOpenSelectedDialog,
      setSelectedTimeLimit,
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
        return uploadFileMutation.mutateAsync([
          selectedSemester.semester,
          file,
        ]);
      },
      handleDeleteBySemester: () => {
        if (!selectedSemester?.semester) {
          toast.error("Vui lòng chọn học kỳ!");
          return;
        }
        deleteBySemesterMutation.mutate(selectedSemester.semester);
      },
      handleAutoScheduleSelected: () => {
        autoScheduleSelectedMutation.mutate({
          classIds: selectedRows,
          timeLimit: selectedTimeLimit,
          semester: selectedSemester?.semester,
        });
      },
      getClassGroups,
      updateClassGroup: async (classId, groupId) => {
        return await generalScheduleRepository.updateClassGroup(
          classId,
          groupId
        );
      },
      deleteClassGroup: async (classId, groupId) => {
        return await generalScheduleRepository.deleteClassGroup(
          classId,
          groupId
        );
      },
      updateClassesGroup: (params) => {
        return updateClassesGroupMutation.mutateAsync(params);
      },
      handleDeleteByIds,
    },
  };
};
