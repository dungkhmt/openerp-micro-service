export const useGroupTableConfig = () => {

  return [
    {
      headerName: "Mã lớp",
      field: "classCode",
      editable: true,
      width: 100,
      sortable: true,
      sort: 'asc',
    },
    {
      headerName: "Lớp học",
      field: "studyClass",
      editable: true,
      width: 150,
    },
    {
      headerName: "Nhóm",
      field: "groupName",
      editable: true,
      width: 120,
    },
    {
      headerName: "Tuần học",
      field: "learningWeeks",
      editable: true,
      width: 120,
    },
    {
      headerName: "Mã học phần",
      field: "moduleCode",
      editable: true,
      width: 100,
    },
    {
      headerName: "Tên học phần",
      field: "moduleName",
      editable: true,
      width: 100,
    },
    {
      headerName: "SL thực",
      field: "quantity",
      editable: true,
      width: 100,
    },
    {
      headerName: "SL MAX",
      field: "quantityMax",
      editable: true,
      width: 100,
    },
    {
      headerName: "Loại lớp",
      field: "classType",
      editable: true,
      width: 100,
    },

    {
      headerName: "Thời lượng",
      field: "mass",
      editable: true,
      width: 100,
    },
    {
      headerName: "Trạng thái",
      field: "state",
      editable: true,
      width: 100,
    },
    {
      headerName: "Kíp",
      field: "crew",
      editable: true,
      width: 100,
    },
    {
      headerName: "Đợt",
      field: "openBatch",
      editable: true,
      width: 100,
    },
    {
      headerName: "Khóa",
      field: "course",
      editable: true,
      width: 100,
    },
    {
      headerName: "Tiết BĐ",
      field: "startTime",
      width: 80,
      editable: true,
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
};
