import { Button } from "@mui/material";

export const staticDatagridCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 150,
  },
  {
    field: "totalMoney",
    headerName: "Tổng tiền đặt",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "totalPayment",
    headerName: "Tổng tiền trả",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "discount",
    headerName: "Khuyến mãi (%)",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "createdBy",
    headerName: "Tạo bởi",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.user.id;
    },
  },
  {
    field: "boughtBy",
    headerName: "Mua bởi",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.customer.name;
    },
  },
  {
    field: "createdDate",
    headerName: "Thời điểm tạo",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 150,
    renderCell: (params) => {
      return (
        <Button variant="outlined" color="info">
          {params?.row?.status}
        </Button>
      );
    },
  },
];

export const staticProductFields = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
  },
  {
    field: "name",
    headerName: "Tên sản phẩm",
    sortable: false,
    minWidth: 200,
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 100,
  },
];

export const staticFormControlFields = [
  {
    name: "discount",
    label: "Discount",
    type: "text",
    component: "input",
  },
];
