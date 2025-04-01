export const useWeekAcademicTableConfig = () => {
  return [
    {
      headerName: "Tuần",
      field: "weekIndex",
      width: 100,
      sortable: true,
      sort: "asc",
    },
    {
      headerName: "Ngày bắt đầu",
      field: "startDayOfWeek",
      width: 150,
    },
  ];
};
