import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
import withScreenSecurity from "components/common/withScreenSecurity";
import { unix } from "moment";
import { useLocation } from "react-router-dom";
import { useWindowSize } from "react-use";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import { useGetItemsOfTrip } from "../../controllers/query/shipment-query";
import { Action } from "../sellin/PurchaseOrder";
function TripScreen({ screenAuthorization }) {
  const location = useLocation();
  const { height } = useWindowSize();
  const currTrip = location.state.trip;
  const { isLoading, data } = useGetItemsOfTrip({
    tripCode: currTrip?.code,
  });
  const extraActions = [
    {
      title: "Xem",
      callback: (item) => {},
      icon: <VisibilityIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          boxShadow: 3,
          margin: "0px -16px 0 -16px",
          paddingX: 2,
          paddingY: 1,
          position: "sticky",
          backgroundColor: "white",
          zIndex: 1000,
        }}
      >
        <Typography
          id="modal-modal-title"
          variant="h6"
          textTransform="capitalize"
          letterSpacing={1}
          color={green[800]}
          fontSize={17}
        >
          {"CHI TIẾT CHUYẾN GIAO HÀNG"}
        </Typography>
      </Box>
      <Box>
        <Typography>Thông tin cơ bản</Typography>
        <Typography></Typography>
        <Typography>1. Mã chuyến : {currTrip?.code}</Typography>
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
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={100}
        columns={[
          {
            field: "code",
            headerName: "Mã code",
            sortable: false,
            pinnable: true,
            minWidth: 150,
          },
          {
            field: "createdDate",
            headerName: "Thời điểm tạo",
            sortable: false,
            minWidth: 200,
          },
          {
            field: "boughtBy",
            headerName: "Mua bởi",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params?.row?.deliveryBill?.saleOrder?.customer?.name;
            },
          },
          {
            field: "status",
            headerName: "Trạng thái",
            sortable: false,
            minWidth: 150,
            renderCell: (params) => {
              return (
                <Button variant="outlined" color="info">
                  {"IN PROGRESS"}
                </Button>
              );
            },
          },
          {
            field: "quantity",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
            type: "actions",
            renderCell: (params) => (
              <Action
                disabled={false}
                extraAction={extraActions[0]}
                item={params.row}
                onActionCall={extraActions[0].callback}
              />
            ),
          },
        ]}
        rows={data ? data?.content : []}
      />
      <Typography
        id="modal-modal-title"
        variant="h6"
        textTransform="capitalize"
        letterSpacing={1}
        color={green[800]}
        fontSize={17}
      >
        {"LỘ TRÌNH GIAO HÀNG"}
      </Typography>
      <Button onClick={() => {}} variant={"outlined"}>
        Tạo mới lộ trình
      </Button>
    </Box>
  );
}

const SCR_ID = "SCR_TRIP";
export default withScreenSecurity(TripScreen, SCR_ID, true);
