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
    align: "center",
    sortable: false,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
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
    field: "address",
    headerName: "Địa chỉ",
    sortable: false,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
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
  {
    field: "customerType",
    headerName: "Loại khách hàng",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.customerType.name;
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
      return params.row.contractType.name;
    },
  },
  {
    field: "createdBy",
    headerName: "Mã người tạo",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.user.id;
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
    width: 125,
    minWidth: 150,
    maxWidth: 200,
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
    width: 125,
    minWidth: 150,
    maxWidth: 200,
  },
  {
    field: "sku",
    headerName: "Mã SKU",
    sortable: false,
    width: 125,
    minWidth: 150,
    maxWidth: 200,
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
    headerName: "Tên đơn vị sản phẩm",
    sortable: false,
    minWidth: 200,
  },
  {
    field: "",
    headerAlign: "center",
    align: "center",
    headerName: "Hành động",
    sortable: false,
    minWidth: 200,
  },
];
export const categoryColumns = [
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
    headerName: "Tên danh mục sản phẩm",
    sortable: false,
    minWidth: 250,
  },
  {
    field: "",
    headerAlign: "center",
    align: "center",
    headerName: "Hành động",
    sortable: false,
    minWidth: 200,
  },
];
export const distChannelCols = [
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
    headerName: "Tên kênh phân phối",
    sortable: false,
    minWidth: 250,
  },
  {
    field: "",
    headerAlign: "center",
    align: "center",
    headerName: "Hành động",
    sortable: false,
    minWidth: 200,
  },
];
export const customerTypeCols = [
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
    headerName: "Tên loại khách hàng",
    sortable: false,
    minWidth: 250,
  },
  {
    field: "",
    headerAlign: "center",
    align: "center",
    headerName: "Hành động",
    sortable: false,
    minWidth: 200,
  },
];
export const contractTypeCols = [
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
    headerName: "Tên hợp đồng giao dịch",
    sortable: false,
    minWidth: 250,
  },
  {
    field: "channel",
    headerName: "Kênh phân phối",
    headerAlign: "center",
    align: "center",
    sortable: false,
    minWidth: 200,
    valueGetter: (params) => {
      return params?.row?.channel?.name;
    },
  },
  {
    field: "",
    headerAlign: "center",
    align: "center",
    headerName: "Hành động",
    sortable: false,
    minWidth: 200,
  },
];
