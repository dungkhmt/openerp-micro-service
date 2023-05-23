import { Fragment } from "react"
import { Box, Button, Grid, MenuItem, Modal, Select, 
  TextField, Typography } from "@mui/material";
import { request } from "api";
import { RouteMap } from 'components/map/maps';
import StandardTable from "components/StandardTable";
import { useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from 'screens/styles.js';
import { errorNoti, successNoti } from 'utils/notification';
import CommandBarButton from "components/button/commandBarButton";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";


const DeliveryPersonTripDetail = ( props ) => {

  const tripId = props.match?.params?.id;
  const classes = useStyles();
  const [tripInfo, setTripInfo] = useState({});
  const [deliveryItemsTableData, setDeliveryItemsTableData] = useState([]);
  const [maxSequence, setMaxSequence] = useState(0);
  const [routeDetails, setRouteDetails] = useState({});
  const [isShowRouteMapModal, setShowRouteMapModal] = useState(false);
  const [runnedAutoRoute, setRunnedAutoRoute] = useState(false);
  const [selectedWarehouseName, setSelectedWarehouseName] = useState(null);
  const [selectedDeliveryItems, setSelectedDeliveryItems] = useState([]);
  const [isHideCommandBar, setHideCommandBar] = useState(true);
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        `${API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP}/${tripId}`,
        (res) => {
          setTripInfo(res.data);
          setMaxSequence(res.data.totalLocations);
          setDeliveryItemsTableData(res.data.items);
          if (res.data.warehouseName != null) {
            setSelectedWarehouseName(res.data.warehouseName);
          }
        }
      );

      await request(
        "get",
        `${API_PATH.DELIVREY_MANAGER_AUTO_ROUTE}/${tripId}`,
        (res) => {
          setRouteDetails(res.data);
          if (res.data.points.length > 0) {
            setRunnedAutoRoute(true);
          }
        }
      );
        
      setLoading(false);
    }

    fetchData();
  }, []);

  const onSelectionChangeHandle = (rows) => {
    console.log("Row => ", rows);
    if (rows.length === 0) {
      setHideCommandBar(true);
    } else {
      setHideCommandBar(false);
    }
  }

  const startDeliveryTrip = () => {
    request(
      "put",
      `${API_PATH.DELIVERY_TRIP_START}/${tripId}`,
      (res) => {
        successNoti("Bắt đầu giao hàng thành công");
        window.location.reload();
      },
      {
        500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
      }
    )
  }

  const completeDeliveryTrip = () => {
    request(
      "put",
      `${API_PATH.DELIVERY_TRIP_COMPLETE}/${tripId}`,
      (res) => {
        successNoti("Cập nhật trạng thái thành công");
        window.location.reload();
      },
      {
        500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
      }
    )
  }

  const doneItemsButtonHandle = (selectedItemIds) => {
    // const selectedItemIds = deliveryItemsTableData
    //   .filter((item) => item.tableData.checked == true)
    //   .map((obj) => obj.deliveryTripItemId);
    console.log("Selected item to mark done => ", selectedItemIds);
    request(
      "put",
      `${API_PATH.DELIVERY_TRIP_ITEM_COMPLETE}/${selectedItemIds.join(',')}`,
      (res) => {
        successNoti("Cập nhật trạng thái cho sản phẩm thành công");
        window.location.reload();
      },
      {
        500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
      }
    )
  }

  const failItemsButtonHandle = (selectedItemIds) => {
    // const selectedItemIds = deliveryItemsTableData
    //   .filter((item) => item.tableData.checked == true)
    //   .map((obj) => obj.deliveryTripItemId);
    console.log("Selected item to mark fail => ", selectedItemIds);
    request(
      "put",
      `${API_PATH.DELIVERY_TRIP_ITEM_FAIL}/${selectedItemIds.join(',')}`,
      (res) => {
        successNoti("Cập nhật trạng thái cho sản phẩm thành công");
        window.location.reload();
      },
      {
        500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
      }
    )
  }

  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <Modal open={isShowRouteMapModal}
      onClose={() => setShowRouteMapModal(!isShowRouteMapModal)}
      aria-labelledby="modal-modal-title" 
      aria-describedby="modal-modal-description"
    >
      <Box sx={{
        position: 'absolute',
        top: '50%',
        left: '50%',
        width: '75%',
        height: '70%',
        transform: 'translate(-50%, -50%)',
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
      }}>
        <RouteMap 
          points={routeDetails?.points}
          customers={routeDetails?.customers}
          warehouse={routeDetails?.warehouse}
        />
      </Box>
    </Modal>

    <Box>
      <Grid container justifyContent="space-between" 
        className={classes.headerBox} >
        <Grid>
          <Typography variant="h5">
            Thông tin chuyến giao hàng</Typography>
        </Grid>

        {
          tripInfo?.deliveryTripStatusCode == 'CREATED' &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={startDeliveryTrip}>Bắt đầu giao hàng</Button>
          </Grid>
        }

        {
          tripInfo?.deliveryTripStatusCode == 'DELIVERING' &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={completeDeliveryTrip}>Hoàn thành chuyến</Button>
          </Grid>
        }

        {
          runnedAutoRoute &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={() => setShowRouteMapModal(true)}>Xem bản đồ hành trình</Button>
          </Grid>
        }
      </Grid>
    </Box>

    <Box className={classes.bodyBox}>
      <Box className={classes.formWrap}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Ngày tạo chuyến giao hàng
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.createdStamp}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Ngày cập nhật gần nhất
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.lastUpdatedStamp}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Người tạo 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.createdBy}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Nhân viên giao hàng 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.deliveryPersonName}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Tổng quãng đường 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.distance}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Số điểm cần giao hàng 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={maxSequence}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Kho lấy hàng 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={selectedWarehouseName}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Trạng thái đơn hàng 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.deliveryTripStatus}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>
        </Grid>
      </Box>

      <StandardTable
        rowKey="deliveryTripItemId"
        title="Danh sách sản phẩm"
        hideCommandBar={isHideCommandBar}
        columns={[
          { title: "Số thứ tự giao hàng", field: "sequence" },
          { title: "Sản phẩm", field: "productName" },
          { title: "Số lượng", field: "quantity" },
          { title: "Kệ hàng", field: "bayCode" },
          { title: "Lô", field: "lotId" },
          { title: "Kho", field: "warehouseName" },
          { title: "Người nhận", field: "customerName" },
          { title: "Số điện thoại", field: "customerPhone" },
          { title: "Địa chỉ nhận hàng", field: "customerAddressName" },
          { title: "Trạng thái", field: "statusCode" },
        ]}
        options={{
          selection: tripInfo?.deliveryTripStatusCode == 'DELIVERING',
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        onSelectionChange={onSelectionChangeHandle}
        data={deliveryItemsTableData}
        actions={tripInfo?.deliveryTripStatusCode == 'DELIVERING' && [
          {
            tooltip: "Giao thành công",
            iconOnClickHandle: doneItemsButtonHandle
          },
          {
            tooltip: "Giao thất bại",
            iconOnClickHandle: failItemsButtonHandle
          }
        ]}
        deletable={false}
      />
    </Box>
  </Fragment>);
}

const SCR_ID = "SCR_WMSv2_TODAY_DELIVERY_TRIP_DETAIL";
export default withScreenSecurity(DeliveryPersonTripDetail, SCR_ID, true);
