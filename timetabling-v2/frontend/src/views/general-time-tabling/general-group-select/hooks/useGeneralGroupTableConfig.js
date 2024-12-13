
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
  ];
};
