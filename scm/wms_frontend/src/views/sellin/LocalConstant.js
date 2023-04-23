import { Button } from "@mui/material";

export const staticDatagridCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
  },
  {
    field: "supplierCode",
    headerName: "Mã nhà sản xuất",
    sortable: false,
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
    field: "vat",
    headerName: "VAT",
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
  {
    field: "facility",
    headerName: "Kho trực thuộc",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.facility.name;
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
    name: "code",
    label: "Mã đơn",
    type: "text",
    component: "input",
  },
  {
    name: "supplierCode",
    label: "Mã NSX",
    type: "text",
    component: "input",
  },
  {
    name: "vat",
    label: "VAT",
    type: "text",
    component: "input",
  },
];
