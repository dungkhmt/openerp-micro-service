import { Avatar, Button, Typography } from "@mui/material";
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

export const purchaseOrderCols = [
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
    field: "supplierCode",
    headerAlign: "center",
    align: "center",
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
    field: "totalMoney",
    headerAlign: "center",
    align: "center",
    headerName: "Tổng tiền đặt",
    sortable: false,
    minWidth: 130,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontSize: 14 }}>
          {`${formatVietnameseCurrency(params?.row?.totalMoney)}`}
        </Typography>
      );
    },
  },
  {
    field: "totalPayment",
    headerAlign: "center",
    align: "center",
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
    minWidth: 150,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontSize: 14 }}>{`${params?.row?.vat} %`}</Typography>
      );
    },
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
    headerAlign: "center",
    align: "center",
    headerName: "Tạo bởi",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return convertUserToName(params.row?.user);
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
];

export const staticProductFields = [
  {
    field: "name",
    headerName: "Tên sản phẩm",
    sortable: false,
    headerAlign: "center",
    align: "center",
    minWidth: 200,
    maxWidth: 250,
  },
  {
    field: "price",
    headerName: "Giá sản phẩm",
    sortable: false,
    headerAlign: "center",
    align: "center",
    minWidth: 150,
    maxWidth: 200,
    valueGetter: (params) => {
      return formatVietnameseCurrency(params?.row?.price?.priceBeforeVat);
    },
  },
  {
    field: "vat",
    headerName: "Thuế VAT",
    sortable: false,
    headerAlign: "center",
    align: "center",
    minWidth: 150,
    maxWidth: 200,
    valueGetter: (params) => {
      return `${params?.row?.price?.vat} %`;
    },
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 150,
    maxWidth: 200,
    headerAlign: "center",
    align: "center",
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

export const receiptBillCols = [
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
      return params.row.facility.name;
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
      return params.row.purchaseOrder.code;
    },
  },
];

export const purchaseOrderPrice = [
  {
    field: "productCode",
    headerName: "Mã sản phẩm",
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
    headerName: "Tên sản phẩm",
    sortable: false,
    minWidth: 150,
    maxWidth: 200,
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
              ITEM_STATUS_COLOR_MAPPING[params?.row?.status.toLowerCase()],
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

export const purchaseStaff = [
  {
    field: "id",
    headerName: "Mã nhân viên",
    sortable: false,
    pinnable: true,
    minWidth: 200,
    maxWidth: 250,
    renderCell: (params) => {
      return (
        <Typography sx={{ color: AppColors.secondary, fontWeight: "500" }}>
          {params?.row?.id}
        </Typography>
      );
    },
  },
  {
    field: "affiliations",
    headerName: "Avatar",
    headerAlign: "center",
    align: "center",
    sortable: false,
    pinnable: true,
    minWidth: 150,
    maxWidth: 200,
    renderCell: (params) => {
      return (
        <Avatar
          alt="No name"
          src={params?.row?.affiliations}
          sx={{ width: 36, height: 36 }}
        />
      );
    },
  },

  {
    field: "name",
    headerName: "Tên nhân viên",
    sortable: false,
    // pinnable: true,
    minWidth: 150,
    maxWidth: 200,
    renderCell: (params) => {
      return (
        <Typography sx={{ color: AppColors.warning, fontWeight: "500" }}>
          {params?.row?.firstName +
            " " +
            params?.row?.middleName +
            " " +
            params?.row?.lastName}
        </Typography>
      );
    },
  },
  {
    field: "email",
    headerAlign: "center",
    align: "center",
    headerName: "Email",
    sortable: false,
    minWidth: 250,
    maxWidth: 400,
  },
  {
    field: "registeredRoles",
    headerAlign: "center",
    align: "center",
    headerName: "Vai trò",
    sortable: false,
    minWidth: 250,
    maxWidth: 400,
  },
  {
    field: "status",
    headerName: "Trạng thái",
    headerAlign: "center",
    align: "center",
    sortable: false,
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
              ITEM_STATUS_COLOR_MAPPING[params?.row?.status_id?.toLowerCase()],
          }}
        >
          <Typography
            sx={{
              textTransform: "lowercase",
              fontSize: 14,
              color: "white",
            }}
          >
            {params?.row?.status_id}
          </Typography>
        </Button>
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
      return unix(params?.row?.createdStamp).format("DD-MM-YYYY hh:mm:ss");
    },
  },
];
