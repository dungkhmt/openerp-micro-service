import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Stack, Typography } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import { unix } from "moment";
import { useLocation } from "react-router-dom";
import { useWindowSize } from "react-use";
import { Action } from "../../../components/action/Action";
import CustomDataGrid from "../../../components/datagrid/CustomDataGrid";
import { useCreateTripRoute } from "../../../controllers/query/delivery-trip-query";
import { useGetItemsOfTrip } from "../../../controllers/query/shipment-query";
import { AppColors } from "../../../shared/AppColors";
import { shipmentItemCols } from "../LocalConstant";
function TripScreen({ screenAuthorization }) {
  const location = useLocation();
  const { height } = useWindowSize();
  const currTrip = location.state.trip;
  const { isLoading, data } = useGetItemsOfTrip({
    tripCode: currTrip?.code,
  });
  const createTripRouteQuery = useCreateTripRoute();
  const extraActions = [
    {
      title: "Xem",
      callback: (item) => {},
      icon: <VisibilityIcon />,
      color: AppColors.green,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <Stack
          direction={"row"}
          spacing={2}
          alignItems={"center"}
          sx={{
            marginTop: 5,
          }}
        >
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
            marginTop: 2,
          }}
        >
          1. Mã chuyến: {currTrip?.code}
        </Typography>
        <Typography>
          2. Ngày bắt đầu:
          {unix(currTrip?.startedDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>
          3. Người thực hiện:
          {currTrip?.userInCharge?.id}
        </Typography>
        <Typography>4. Đơn hàng:</Typography>
      </Box>
      <CustomDataGrid
        // params={params}
        // setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20, marginTop: 2 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={100}
        columns={[
          ...shipmentItemCols,
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
      <Stack
        sx={{
          marginTop: 5,
        }}
        direction={"row"}
        spacing={2}
        alignItems={"center"}
      >
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
          TẠO MỚI LỘ TRÌNH GIAO HÀNG
        </Typography>
      </Stack>
      <Button
        onClick={async () => {
          let tripParams = {
            tripCode: currTrip?.code,
          };
          await createTripRouteQuery.mutateAsync(tripParams);
        }}
        variant={"outlined"}
      >
        Tạo mới lộ trình
      </Button>
    </Box>
  );
}

const SCR_ID = "SCR_TRIP";
export default withScreenSecurity(TripScreen, SCR_ID, true);
