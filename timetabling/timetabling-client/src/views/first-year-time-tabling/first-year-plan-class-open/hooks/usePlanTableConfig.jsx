export const usePlanTableConfig = () => {
  return [
    {
      headerName: "Mã lớp tạm thời",
      field: "id",
      width: 100,
    },
    {
      headerName: "Tổng số lớp",
      field: "numberOfClasses",
      width: 120,
    },
    {
      headerName: "Max SV/LT",
      field: "lectureMaxQuantity",
      width: 120,
    },
    {
      headerName: "Max SV/BT",
      field: "exerciseMaxQuantity",
      width: 120,
    },
    {
      headerName: "Max SV/LT+BT",
      field: "lectureExerciseMaxQuantity",
      width: 120,
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
      headerName: "Mã HP",
      field: "moduleCode",
      width: 100,
    },
    {
      headerName: "Tên HP",
      field: "moduleName",
      width: 100,
    },
    {
      headerName: "CTĐT",
      field: "programName",
      width: 100,
    },
  ];
};
