import { Button, Typography } from "@mui/material";
import { AppColors } from "../../shared/AppColors";
import { ITEM_STATUS_COLOR_MAPPING } from "../../shared/AppConstants";

export const staticCustomerField = [
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
    headerName: "Tên khách hàng",
    headerAlign: "center",
    // align: "center",
    sortable: false,
    minWidth: 250,
    maxWidth: 400,
    renderCell: (params) => {
      return (
        <Typography sx={{ fontWeight: "500" }}>{params?.row?.name}</Typography>
      );
    },
  },
  {
    field: "phone",
    headerName: "Số điện thoại",
    sortable: false,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
  },
  {
    headerAlign: "center",
    field: "address",
    headerName: "Địa chỉ",
    sortable: false,
    minWidth: 250,
    maxWidth: 350,
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    width: 125,
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
  {
    field: "facility",
    headerName: "Kho trực thuộc",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 250,
    valueGetter: (params) => {
      return params?.row?.facility?.name;
    },
  },
  {
    field: "customerType",
    headerName: "Loại khách hàng",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params?.row?.customerType?.name;
    },
  },
  {
    field: "contractType",
    headerName: "Loại hợp đồng",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params?.row?.contractType?.name;
    },
  },
  {
    field: "createdBy",
    headerName: "Người tạo",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    flex: 1,
    renderCell: (params) => {
      return (
        <Typography sx={{ color: AppColors.warning, fontWeight: "500" }}>
          {params?.row?.user?.firstName +
            " " +
            params?.row?.user?.middleName +
            " " +
            params?.row?.user?.lastName}
        </Typography>
      );
    },
  },
];
/**
 * @type {import("@mui/x-data-grid").GridColDef[]}
 */
export const productColumns = [
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
    headerName: "Tên sản phẩm",
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
    field: "brand",
    headerAlign: "center",
    align: "center",
    headerName: "Nhãn hiệu",
    sortable: false,
    minWidth: 200,
    maxWidth: 300,
  },
  {
    field: "status",
    headerAlign: "center",
    align: "center",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 150,
    // maxWidth: 200,
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
  {
    field: "sku",
    headerName: "Mã SKU",
    sortable: false,
    // width: 125,
    minWidth: 150,
    maxWidth: 200,
  },
  {
    field: "product_category_name",
    headerName: "Loại sản phẩm",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.productCategory.name;
    },
  },
  {
    field: "product_unit_name",
    headerName: "Đơn vị tính",
    headerAlign: "center",
    align: "center",
    sortable: false,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
    valueGetter: (params) => {
      return params.row.productUnit.name;
    },
  },
  {
    field: "unitPerBox",
    headerName: "Số lượng/đơn vị",
    headerAlign: "center",
    align: "center",
    sortable: false,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
  },
];
export const unitColumns = [
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
    field: "name",
    headerAlign: "center",
    align: "center",
    headerName: "Tên đơn vị sản phẩm",
    sortable: false,
    flex: 1,
  },
];
export const categoryColumns = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 250,
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
    headerName: "Tên danh mục sản phẩm",
    sortable: false,
    flex: 1,
  },
];
export const distChannelCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 250,
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
    headerName: "Tên kênh phân phối",
    sortable: false,
    flex: 1,
  },
  {
    field: "promotion",
    headerAlign: "center",
    align: "center",
    headerName: "Chiết khấu mua hàng (%)",
    sortable: false,
    minWidth: 250,
    valueFormatter: (params) => {
      return `${params?.value} %`;
    },
  },
];
export const customerTypeCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 250,
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
    headerName: "Tên loại khách hàng",
    sortable: false,
    flex: 1,
  },
];
export const contractTypeCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
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
    headerName: "Tên hợp đồng giao dịch",
    sortable: false,
    flex: 1,
  },
  {
    field: "channel",
    headerName: "Kênh phân phối",
    headerAlign: "center",
    align: "center",
    sortable: false,
    flex: 1,
    valueGetter: (params) => {
      return params?.row?.channel?.name;
    },
  },
];
