export const usePlanTableConfig = (
  ) => {
    return [
      {
        headerName: "Mã lớp tạm thời",
        field: "id",
        width: 100,
      },
      {
        headerName: "Mã lớp cha",
        field: "parentClassId",
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
      }
    ];
  };