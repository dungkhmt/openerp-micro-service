import VisibilityIcon from "@mui/icons-material/Visibility";
import {
  Backdrop,
  Box,
  Button,
  CircularProgress,
  Stack,
  Typography,
} from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import {
  useCreateTripRoute,
  useDeleteTripRoute,
  useGetTripRouteList,
} from "controllers/query/delivery-trip-query";
import { useGetItemsOfTrip } from "controllers/query/shipment-query";
import { unix } from "moment";
import { useEffect, useState } from "react";
import { useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { shipmentItemCols } from "../LocalConstant";

// var intervalID;

function TripScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const location = useLocation();
  const { height } = useWindowSize();
  const [open, setOpen] = useState(false);
  const [count, setCount] = useState(0);
  const history = useHistory();
  let { path } = useRouteMatch();

  const currTrip = location.state.trip;
  const { isLoading, data: itemOfTrip } = useGetItemsOfTrip({
    tripCode: currTrip?.code,
  });
  const { isLoading: isLoadingTripRoute, data: tripRoute } =
    useGetTripRouteList({
      tripCode: currTrip?.code,
    });
  const createTripRouteQuery = useCreateTripRoute();
  const deleteTripRouteQuery = useDeleteTripRoute({
    tripCode: currTrip?.code,
  });
  const extraActions = [
    {
      title: "Xem",
      callback: (item) => {},
      icon: <VisibilityIcon />,
      color: AppColors.green,
    },
  ];

  const handleClose = () => {
    setOpen(false);
  };
  const handleOpen = () => {
    setOpen(true);
  };

  const handleButtonClick = () => {
    history.push(`${path}/route`, {
      tripRoute: tripRoute,
    });
  };
  useEffect(() => {
    // Start the interval
    const interval = setInterval(() => {
      setCount((prevCount) => prevCount + 1);
    }, 1000);

    // Terminate the interval when count reaches a certain value
    if (count >= 10) {
      clearInterval(interval);
    }

    // Cleanup function to clear the interval on component unmount
    return () => {
      clearInterval(interval);
    };
  }, [count]);
  useEffect(() => {
    if (count >= 10) {
      handleClose();
    }
  }, [count]);

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
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20, marginTop: 2 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading || isLoadingTripRoute}
        totalItem={tripRoute?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
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
        rows={itemOfTrip ? itemOfTrip?.content : []}
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
      <Stack direction={"row"}>
        <Button
          disabled={itemOfTrip?.content?.length === 0 || tripRoute}
          onClick={async () => {
            setCount((pre) => {
              handleOpen();
              return 0;
            });
            let tripParams = {
              tripCode: currTrip?.code,
            };
            await createTripRouteQuery.mutateAsync(tripParams);
          }}
          variant={"contained"}
          sx={{ marginY: 2, marginRight: 5 }}
        >
          <Typography
            sx={{
              color: "white",
            }}
          >
            TẠO MỚI LỘ TRÌNH
          </Typography>
        </Button>
        {tripRoute && (
          <Button
            onClick={() => {
              handleButtonClick();
            }}
            variant={"contained"}
            sx={{ marginY: 2, marginRight: 5 }}
          >
            <Typography
              sx={{
                color: "white",
              }}
            >
              XEM LỘ TRÌNH ĐÃ TẠO
            </Typography>
          </Button>
        )}
        {tripRoute && (
          <Button
            onClick={async () => {
              await deleteTripRouteQuery.mutateAsync();
            }}
            variant={"contained"}
            color={"error"}
            sx={{ marginY: 2 }}
          >
            <Typography
              sx={{
                color: "white",
              }}
            >
              XÓA LỘ TRÌNH ĐÃ TẠO
            </Typography>
          </Button>
        )}
      </Stack>
      <Backdrop
        sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
        open={open}
        onClick={handleClose}
      >
        <Box sx={{ position: "relative", display: "inline-flex" }}>
          <CircularProgress color="primary" />
          <Box
            sx={{
              top: 0,
              left: 0,
              bottom: 0,
              right: 0,
              position: "absolute",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Typography
              variant="caption"
              component="div"
              color="white"
            >{`${Math.round(count * 10)}%`}</Typography>
          </Box>
        </Box>
      </Backdrop>
    </Box>
  );
}

const SCR_ID = "SCR_SCM_TRIP";
export default withScreenSecurity(TripScreen, SCR_ID, true);