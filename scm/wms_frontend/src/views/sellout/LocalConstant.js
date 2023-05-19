import { Button, Typography } from "@mui/material";
import { unix } from "moment";
import { AppColors } from "../../shared/AppColors";
import {
  ITEM_STATUS_COLOR_MAPPING,
  ORDER_STATUS_COLOR_MAPPING,
} from "../../shared/AppConstants";

export const saleOrderCols = [
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
    field: "totalMoney",
    headerAlign: "center",
    align: "center",
    headerName: "Tổng tiền đặt",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "totalPayment",
    headerAlign: "center",
    align: "center",
    headerName: "Tổng tiền trả",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "discount",
    headerAlign: "center",
    align: "center",
    headerName: "Khuyến mãi (%)",
    sortable: false,
    minWidth: 150,
    renderCell: (params) => {
      return <Typography>{`${params?.row?.discount} %`}</Typography>;
    },
  },
  {
    field: "createdBy",
    headerName: "Tạo bởi",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.user.id;
    },
  },
  {
    field: "boughtBy",
    headerAlign: "center",
    align: "center",
    headerName: "Mua bởi",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params?.row?.customer?.name;
    },
  },
  {
    field: "createdDate",
    headerAlign: "center",
    align: "center",
    headerName: "Thời điểm tạo",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return unix(params?.row?.createdDate).format("DD-MM-YYYY");
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
          variant="contained"
          sx={{
            borderRadius: "2px",
            borderWidth: "1px",
            paddingY: 1,
            paddingX: 2,
            height: "24px",
            background:
              ORDER_STATUS_COLOR_MAPPING[params?.row?.status.toLowerCase()],
          }}
        >
          <Typography
            sx={{
              textTransform: "lowercase",
              fontSize: 14,
              color: "white",
            }}
          >
            {params?.row?.status}
          </Typography>
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
    headerAlign: "center",
    align: "center",
    minWidth: 150,
    maxWidth: 200,
  },
  {
    field: "name",
    headerName: "Tên sản phẩm",
    sortable: false,
    headerAlign: "center",
    align: "center",
    minWidth: 200,
    maxWidth: 200,
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
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
              textTransform: "lowercase",
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

export const deliveryBillCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 200,
    renderCell: (params) => {
      return (
        <Typography sx={{ color: AppColors.secondary, fontWeight: "500" }}>
          {params?.row?.code}
        </Typography>
      );
    },
  },
  {
    field: "createdDate",
    headerAlign: "center",
    align: "center",
    headerName: "Thời điểm tạo",
    sortable: false,
    minWidth: 200,
    valueGetter: (params) => {
      return unix(params?.row?.createdDate).format("DD-MM-YYYY");
    },
  },
  {
    field: "facility",
    headerAlign: "center",
    align: "center",
    headerName: "Kho trực thuộc",
    sortable: false,
    minWidth: 200,
    valueGetter: (params) => {
      return params.row?.saleOrder?.customer?.facility?.name;
    },
  },
  {
    field: "order",
    headerName: "Mã đơn hàng",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 200,
    valueGetter: (params) => {
      return params.row?.saleOrder?.code;
    },
  },
];