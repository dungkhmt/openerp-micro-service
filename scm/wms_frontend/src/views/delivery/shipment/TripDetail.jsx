import VisibilityIcon from "@mui/icons-material/Visibility";
import {
  Backdrop,
  Box,
  Button,
  CircularProgress,
  Stack,
  Typography,
} from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import { unix } from "moment";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { useWindowSize } from "react-use";
import { Action } from "../../../components/action/Action";
import CustomDataGrid from "../../../components/datagrid/CustomDataGrid";
import { useCreateTripRoute } from "../../../controllers/query/delivery-trip-query";
import { useGetItemsOfTrip } from "../../../controllers/query/shipment-query";
import { AppColors } from "../../../shared/AppColors";
import { shipmentItemCols } from "../LocalConstant";

// var intervalID;

function TripScreen({ screenAuthorization }) {
  const location = useLocation();
  const { height } = useWindowSize();
  const [open, setOpen] = useState(false);
  const handleClose = () => {
    setOpen(false);
  };
  const handleOpen = () => {
    setOpen(true);
  };
  const [progress, setProgress] = React.useState(50);

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
  React.useEffect(() => {
    const timer = setInterval(() => {
      setProgress((prevProgress) => prevProgress + 10);
    }, 800);
    return () => {
      clearInterval(timer);
    };
  }, [open]);
  console.log("Progress: ", progress);
  useEffect(() => {
    if (progress >= 100) {
      handleClose();
    }
    // clearInterval(intervalID);
  }, [progress]);
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
        onClick={
          () => {
            handleOpen();
          }
          // async () => {
          // let tripParams = {
          //   tripCode: currTrip?.code,
          // };
          // await createTripRouteQuery.mutateAsync(tripParams);
          // }
        }
        variant={"contained"}
        sx={{ marginY: 2 }}
      >
        <Typography
          sx={{
            color: "white",
          }}
        >
          TẠO MỚI LỘ TRÌNH
        </Typography>
      </Button>
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
            >{`${Math.round(progress)}%`}</Typography>
          </Box>
        </Box>
      </Backdrop>
    </Box>
  );
}

const SCR_ID = "SCR_TRIP";
export default withScreenSecurity(TripScreen, SCR_ID, true);
