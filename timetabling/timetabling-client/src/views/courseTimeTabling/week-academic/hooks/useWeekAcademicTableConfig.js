export const useWeekAcademicTableConfig = () => {
    return [
        {
          headerName: "Mã lớp",
          field: "classCode",
          width: 100,
          sortable: true,
          sort: 'asc',
        },
        {
          headerName: "Lớp học",
          field: "studyClass",
          width: 150,
        },
        {
          headerName: "Nhóm",
          field: "groupName",
          width: 120,
        },
        {
          headerName: "Mã học phần",
          field: "moduleCode",
          width: 100,
        },
        {
          headerName: "Tên học phần",
          field: "moduleName",
          width: 100,
        },
        {
          headerName: "SL thực",
          field: "quantity",
          width: 100,
        },
        {
          headerName: "SL MAX",
          field: "quantityMax",
          width: 100,
        },
        {
          headerName: "Loại lớp",
          field: "classType",
          width: 100,
        },
    
        {
          headerName: "Thời lượng",
          field: "mass",
          width: 100,
        },
        {
          headerName: "Trạng thái",
          field: "state",
          width: 100,
        },
        {
          headerName: "Kíp",
          field: "crew",
          width: 100,
        },
        {
          headerName: "Đợt",
          field: "openBatch",
          width: 100,
        },
        {
          headerName: "Khóa",
          field: "course",
          width: 100,
        },
        {
          headerName: "Tiết BĐ",
          field: "startTime",
          width: 80,
        },
        {
          headerName: "Ngày",
          field: "weekday",
          width: 80,
        },
        {
          headerName: "Phòng học",
          field: "room",
          width: 120,
        },
      ];
}
