import { Avatar, Button, Typography } from "@mui/material";
import { unix } from "moment";
import { AppColors } from "../../shared/AppColors";
import { ITEM_STATUS_COLOR_MAPPING } from "../../shared/AppConstants";

export const allUsers = [
  {
    field: "id",
    headerName: "Mã nhân viên",
    sortable: false,
    pinnable: true,
    minWidth: 150,
    maxWidth: 200,
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
