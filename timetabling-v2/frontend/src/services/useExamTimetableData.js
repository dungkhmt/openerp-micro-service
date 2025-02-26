export const useExamTimetableData = (planId) => {
  // Mock data for timetables
  const mockTimetables = [
    { 
      id: 1, 
      name: 'Lịch thi học kỳ 1 (2023-2024)', 
      createdAt: '2023-12-15T09:30:00', 
      progress: 100,
      examPlanId: planId
    },
    { 
      id: 2, 
      name: 'Lịch thi học kỳ 1 (bản nháp)', 
      createdAt: '2023-12-12T14:45:00', 
      progress: 75,
      examPlanId: planId
    },
    { 
      id: 3, 
      name: 'Lịch thi học kỳ 1 (phương án dự phòng)', 
      createdAt: '2023-12-10T11:20:00', 
      progress: 90,
      examPlanId: planId
    },
    { 
      id: 4, 
      name: 'Lịch thi học kỳ 1 (2023-2024)', 
      createdAt: '2023-12-15T09:30:00', 
      progress: 100,
      examPlanId: planId
    },
    { 
      id: 5, 
      name: 'Lịch thi học kỳ 1 (bản nháp)', 
      createdAt: '2023-12-12T14:45:00', 
      progress: 75,
      examPlanId: planId
    },
    { 
      id: 6, 
      name: 'Lịch thi học kỳ 1 (phương án dự phòng)', 
      createdAt: '2023-12-10T11:20:00', 
      progress: 90,
      examPlanId: planId
    },
    { 
      id: 7, 
      name: 'Lịch thi học kỳ 1 (phương án dự phòng)', 
      createdAt: '2023-12-10T11:20:00', 
      progress: 90,
      examPlanId: planId
    }
  ];

  return {
    examTimetables: mockTimetables,
    isLoading: false,
    createTimetable: () => Promise.resolve({}),
    deleteTimetable: () => Promise.resolve({})
  };
};
