import { Button, Typography } from "@mui/material";
import { AppColors } from "../../shared/AppColors";
import { ITEM_STATUS_COLOR_MAPPING } from "../../shared/AppConstants";

export const staticProductFields = [
  {
    field: "name",
    headerName: "Tên sản phẩm",
    sortable: false,
    minWidth: 200,
    valueGetter: (params) => {
      return params?.row?.product?.name;
    },
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "inventoryQty",
    headerName: "Còn trong kho",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "qtyThreshold",
    headerName: "Ngưỡng tồn kho",
    sortable: false,
    minWidth: 150,
  },
];
export const staticWarehouseCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
    renderCell: (params) => {
      return (
        <Typography sx={{ color: AppColors.secondary, fontWeight: "500" }}>
          {params?.row?.code}
        </Typography>
      );
    },
  },
  {
    field: "name",
    headerAlign: "center",
    align: "center",
    headerName: "Tên kho",
    sortable: false,
    minWidth: 200,
    maxWidth: 300,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontWeight: "500" }}>{params?.row?.name}</Typography>
      );
    },
  },
  {
    field: "createdBy",
    headerName: "Người tạo",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params?.row?.creator?.id;
    },
  },
  {
    field: "address",
    headerName: "Địa chỉ",
    sortable: false,
    pinnable: true,
    minWidth: 200,
    maxWidth: 300,
  },
  {
    field: "managedBy",
    headerAlign: "center",
    align: "center",
    headerName: "Thủ kho",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params?.row?.manager?.id ? params?.row?.manager?.id : "Chưa có";
    },
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
    renderCell: (params) => {
      return (
        <Button
          variant="outlined"
          sx={{
            borderRadius: "30px",
            borderWidth: "1px",
            height: "30px",
            borderColor: ITEM_STATUS_COLOR_MAPPING[params?.row?.status],
          }}
        >
          <Typography
            sx={{
              textTransform: "capitalize",
              color: ITEM_STATUS_COLOR_MAPPING[params?.row?.status],
            }}
          >
            {params?.row?.status}
          </Typography>
        </Button>
      );
    },
  },
];
export const staticSaleOrderCols = [
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
    width: 125,
    minWidth: 150,
    maxWidth: 200,
    renderCell: (params) => {
      return (
        <Button
          variant="outlined"
          sx={{
            borderRadius: "30px",
            borderWidth: "1px",
            height: "30px",
            borderColor: ITEM_STATUS_COLOR_MAPPING[params?.row?.status],
          }}
        >
          <Typography
            sx={{
              textTransform: "capitalize",
              color: ITEM_STATUS_COLOR_MAPPING[params?.row?.status],
            }}
          >
            {params?.row?.status}
          </Typography>
        </Button>
      );
    },
  },
];
