import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Divider, Stack, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetDeliveryTripList } from "controllers/query/delivery-trip-query";
import { unix } from "moment";
import { useState } from "react";
import { useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { tripCols } from "../LocalConstant";
import CreateTripForm from "./components/CreateTripForm";
function ShipmentDetailScreen() {
  const location = useLocation();
  const history = useHistory();
  let { path } = useRouteMatch();
  const currShipment = location.state.shipment;
  const [params, setParams] = useState({
    page: 1,
    pageSize: 5,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);

  const { isLoading, data } = useGetDeliveryTripList({
    shipment_code: currShipment?.code,
    ...params,
  });

  const handleButtonClick = (params) => {
    history.push(`${path}/trip-detail`, {
      trip: params,
    });
  };
  let actions = [
    {
      title: "Tạo chuyến giao hàng",
      callback: (pre) => {
        setIsAdd((pre) => !pre);
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Xem",
      callback: async (item) => {
        handleButtonClick(item);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
    {
      title: "Sửa",
      callback: async (item) => {
        // setIsApproved((pre) => !pre);
        // setUpdateOrder(item);
      },
      icon: <EditIcon />,
      color: AppColors.secondary,
    },
    {
      title: "Xóa",
      callback: (item) => {
        // setIsRemove();
        // setItemSelected(item);
      },
      icon: <DeleteIcon />,
      color: AppColors.error,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={actions} containSearch={false} />
      </Box>
      <Box>
        <Stack direction={"row"} spacing={2} alignItems={"center"}>
          <Stack
            sx={{
              borderRadius: 50,
              background: "gray",
              width: 30,
              height: 30,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Typography sx={{ color: "white" }}>1</Typography>
          </Stack>
          <Typography
            sx={{
              color: AppColors.secondary,
              fontSize: 18,
              fontWeight: "600",
            }}
          >
            THÔNG TIN CƠ BẢN
          </Typography>
        </Stack>
        <Typography
          sx={{
            fontSize: 16,
            marginTop: 2,
          }}
        >
          1. Mã đợt: {currShipment?.code}
        </Typography>
        <Typography>
          2. Ngày bắt đầu:
          {unix(currShipment?.startedDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>
          3. Dự kiến kết thúc:
          {unix(currShipment?.endedDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>4. Số đơn: {currShipment?.maxSize}</Typography>
      </Box>
      <Divider variant="fullWidth" sx={{ marginY: 2 }} />
      <Stack direction={"row"} spacing={2} alignItems={"center"}>
        <Stack
          sx={{
            borderRadius: 50,
            background: "gray",
            width: 30,
            height: 30,
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Typography sx={{ color: "white" }}>2</Typography>
        </Stack>
        <Typography
          sx={{
            color: AppColors.secondary,
            fontSize: 18,
            fontWeight: "600",
          }}
        >
          DANH SÁCH CHUYẾN GIAO HÀNG
        </Typography>
      </Stack>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20, marginTop: 2 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={data?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...tripCols,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            flex: 1,
            type: "actions",
            getActions: (params) => [
              ...extraActions.map((extraAction, index) => (
                <Action
                  item={params.row}
                  key={index}
                  extraAction={extraAction}
                  onActionCall={extraAction.callback}
                  disabled={false}
                />
              )),
            ],
          },
        ]}
        rows={data ? data?.content : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Tạo mới chuyến giao hàng"
      >
        <CreateTripForm setIsAdd={setIsAdd} currShipment={currShipment} />
      </CustomModal>
    </Box>
  );
}

const SCR_ID = "SCR_SHIPMENT_DETAIL";
export default withScreenSecurity(ShipmentDetailScreen, SCR_ID, true);
