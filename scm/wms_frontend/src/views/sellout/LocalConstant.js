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
        <Typography sx={{ fontSize: 14 }}>
          {`${formatVietnameseCurrency(params?.row?.totalPayment)}`}
        </Typography>
      );
    },
  },
  {
    field: "discount",
    headerAlign: "center",
    align: "center",
    headerName: "Khuyến mãi",
    sortable: false,
    minWidth: 130,
    renderCell: (params) => {
      return (
        <Typography
          sx={{ fontSize: 14 }}
        >{`${params?.row?.discount} %`}</Typography>
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
      return convertUserToName(params?.row?.user);
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
      return formatVietnameseCurrency(
        (params?.row?.sellinPrice?.priceBeforeVat *
          (100 + params?.row?.sellinPrice?.vat)) /
          100
      );
    },
  },
  {
    field: "massDiscount",
    headerName: "Chiết khấu sỉ",
    sortable: false,
    headerAlign: "center",
    align: "center",
    minWidth: 150,
    maxWidth: 200,
    valueGetter: (params) => {
      return params?.row?.selloutPrice?.massDiscount;
    },
  },
  {
    field: "contractDiscount",
    headerName: "Chiết khấu hợp đồng",
    sortable: false,
    headerAlign: "center",
    align: "center",
    minWidth: 150,
    maxWidth: 200,
    valueGetter: (params) => {
      return params?.row?.selloutPrice?.contractDiscount;
    },
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

export const saleOrderPrices = [
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
          {params?.row?.productEntity?.code}
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
    renderCell: (params) => {
      return (
        <Typography sx={{ fontWeight: "500" }}>
          {params?.row?.productEntity?.name}
        </Typography>
      );
    },
  },
  {
    field: "contract",
    headerAlign: "center",
    align: "center",
    headerName: "Tên hợp đồng",
    sortable: false,
    minWidth: 150,
    maxWidth: 200,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontWeight: "500" }}>
          {params?.row?.contract?.name}
        </Typography>
      );
    },
  },
  {
    field: "priceBeforeVat",
    headerName: "Giá trước thuế",
    sortable: false,
    minWidth: 150,
    headerAlign: "center",
    align: "center",
    renderCell: (params) => {
      return (
        <Typography sx={{ fontSize: 14 }}>
          {`${formatVietnameseCurrency(params?.row?.priceBeforeVat)}`}
        </Typography>
      );
    },
  },
  {
    field: "vat",
    headerName: "Thuế VAT",
    sortable: false,
    minWidth: 120,
    headerAlign: "center",
    align: "center",
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
              ITEM_STATUS_COLOR_MAPPING[
                params?.row?.productEntity?.status.toLowerCase()
              ],
          }}
        >
          <Typography
            sx={{
              textTransform: "lowercase",
              fontSize: 14,
              color: "white",
            }}
          >
            {params?.row?.productEntity?.status}
          </Typography>
        </Button>
      );
    },
  },
];
export const saleStaff = [
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
