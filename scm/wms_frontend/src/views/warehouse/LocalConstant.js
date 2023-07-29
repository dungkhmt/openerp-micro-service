import { Button, Typography } from "@mui/material";
import { unix } from "moment";
import { AppColors } from "../../shared/AppColors";
import {
  ITEM_STATUS_COLOR_MAPPING,
  ORDER_STATUS_COLOR_MAPPING,
} from "../../shared/AppConstants";
import {
  convertUserToName,
  formatVietnameseCurrency,
} from "../../utils/GlobalUtils";

export const staticProductFields = [
  {
    field: "name",
    headerName: "Tên sản phẩm",
    headerAlign: "center",
    align: "center",
    sortable: false,
    maxWidth: 400,
    minWidth: 300,
    valueGetter: (params) => {
      return params?.row?.product?.name;
    },
    renderCell: (params) => {
      return (
        <Typography sx={{ fontWeight: "bold" }}>
          {params?.row?.product?.name}
        </Typography>
      );
    },
  },
  {
    field: "inventoryQty",
    headerAlign: "center",
    align: "center",
    headerName: "Còn trong kho",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "qtyThreshold",
    headerAlign: "center",
    align: "center",
    headerName: "Ngưỡng tồn kho",
    sortable: false,
    flex: 1,
    // maxWidth: 300,
    // minWidth: 200,
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
    minWidth: 300,
    maxWidth: 400,
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
    renderCell: (params) => {
      return (
        <Typography sx={{ color: AppColors.warning, fontWeight: "500" }}>
          {convertUserToName(params?.row?.creator)}
        </Typography>
      );
    },
  },
  {
    field: "address",
    headerName: "Địa chỉ",
    sortable: false,
    pinnable: true,
    minWidth: 300,
    maxWidth: 350,
  },
  {
    field: "managedBy",
    headerAlign: "center",
    align: "center",
    headerName: "Thủ kho",
    sortable: false,
    minWidth: 150,
    renderCell: (params) => {
      return (
        <Typography sx={{ color: AppColors.warning, fontWeight: "500" }}>
          {convertUserToName(params?.row?.manager)}
        </Typography>
      );
    },
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 100,
    headerAlign: "center",
    align: "center",
    flex: 1,
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
export const staticSaleOrderCols = [
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
    headerAlign: "center",
    align: "center",
    field: "totalMoney",
    headerName: "Tổng tiền đặt",
    sortable: false,
    minWidth: 150,
  },
  {
    headerAlign: "center",
    align: "center",
    field: "totalPayment",
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
      return convertUserToName(params.row.user);
    },
  },
  {
    field: "boughtBy",
    headerName: "Mua bởi",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.customer.name;
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
/**
 * @type {import("@mui/x-data-grid").GridColDef[]}
 */
export const acceptedOrderCols = [
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
    headerAlign: "center",
    align: "center",
    field: "supplierCode",
    headerName: "Mã nhà sản xuất",
    sortable: false,
    minWidth: 150,
    maxWidth: 200,
  },
  {
    field: "facility",
    headerAlign: "center",
    align: "center",
    headerName: "Kho trực thuộc",
    sortable: false,
    minWidth: 250,
    valueGetter: (params) => {
      return params.row.facility.name;
    },
  },
  {
    headerAlign: "center",
    align: "center",
    field: "totalMoney",
    headerName: "Tổng tiền đặt",
    sortable: false,
    minWidth: 130,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontSize: 14 }}>
          {`${formatVietnameseCurrency(params?.row?.totalMoney)}`}{" "}
        </Typography>
      );
    },
  },
  {
    headerAlign: "center",
    align: "center",
    field: "totalPayment",
    headerName: "Tổng tiền trả",
    sortable: false,
    minWidth: 130,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontSize: 14 }}>{`${formatVietnameseCurrency(
          params?.row?.totalPayment
        )}`}</Typography>
      );
    },
  },
  {
    field: "vat",
    headerAlign: "center",
    align: "center",
    headerName: "VAT",
    sortable: false,
    minWidth: 100,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontSize: 14 }}>{`${params?.row?.vat} %`}</Typography>
      );
    },
  },
  {
    field: "status",
    headerAlign: "center",
    align: "center",
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
  {
    field: "createdBy",
    headerName: "Tạo bởi",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return convertUserToName(params.row.user);
    },
  },
  {
    field: "createdDate",
    headerAlign: "center",
    align: "center",
    headerName: "Thời điểm tạo",
    sortable: false,
    minWidth: 120,
    valueGetter: (params) => {
      return unix(params?.row?.createdDate).format("DD-MM-YYYY");
    },
  },
];
