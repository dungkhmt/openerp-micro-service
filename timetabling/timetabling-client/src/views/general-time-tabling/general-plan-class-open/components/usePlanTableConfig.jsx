export const usePlanTableConfig = (
  ) => {
  
    return [
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
        headerName: "CTĐT",
        field: "programName",
        editable: true,
        width: 100,
      },
      {
        headerName: "Mã lớp cha",
        field: "parentClassId",
        editable: true,
        width: 100,
      },
      {
        headerName: "Mã lớp tạm thời",
        field: "tempClassId",
        editable: true,
        width: 100,
      },
    ];
  };