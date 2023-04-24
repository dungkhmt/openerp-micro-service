export const staticCustomerField = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 150,
  },
  {
    field: "name",
    headerName: "Tên khách hàng",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "phone",
    headerName: "Số điện thoại",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "address",
    headerName: "Địa chỉ",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "customerType",
    headerName: "Loại khách hàng",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.customerType.name;
    },
  },
  {
    field: "contractType",
    headerName: "Loại hợp đồng",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.contractType.name;
    },
  },
  {
    field: "createdBy",
    headerName: "Mã người tạo",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.user.id;
    },
  },
  {
    field: "",
    headerName: "Hành động",
    sortable: false,
    minWidth: 150,
  },
];
