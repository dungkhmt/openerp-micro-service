import { TabContext, TabList, TabPanel } from "@mui/lab";
import { Box, Grid, Tab, TextField, Typography } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from 'screens/styles.js';
import { getCurrentDateInString } from "screens/utils/utils";
import { errorNoti, successNoti } from "utils/notification";
import { useRouteMatch } from "react-router-dom";

const ShipmentDetail = ( props ) => {
  const shipmentId = props.match?.params?.id;
  const classes = useStyles();
  const [shipmentInfo, setShipmentInfo] = useState({});
  const [tripTableData, setTripTableData] = useState([]);
  const [deliveryTripItems, setDeliveryTripItems] = useState([]);
  const [isLoading, setLoading] = useState(true);
  const [tabValue, setTabValue] = useState('1');
  const [userLoginId, setUserLoginId] = useState(null);
  const now = getCurrentDateInString();
  const { path } = useRouteMatch();

  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        `${API_PATH.DELIVERY_MANAGER_SHIPMENT}/${shipmentId}`,
        (res) => {
          setShipmentInfo(res.data);
          setTripTableData(res.data.trips);
        }
      );

      await request(
        "get",
        API_PATH.DELIVERY_MANAGER_ASSIGN_ORDER_ITEM,
        (res) => {
          setDeliveryTripItems(res.data);
        }
      );

      await request(
        "get",
        API_PATH.GET_USER_LOGIN_ID,
        (res) => {
          setUserLoginId(res.data);
        }
      );
      setLoading(false);
    }
    fetchData();
  }, []);

  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <Box>
      <Grid container justifyContent="space-between" 
        className={classes.headerBox} >
        <Grid>
          <Typography variant="h5">
            Thông tin đợt giao hàng</Typography>
        </Grid>
      </Grid>
    </Box>

    <Box className={classes.bodyBox}>
      <Box className={classes.formWrap}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Ngày tạo đợt giao hàng
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={shipmentInfo?.createdStamp}
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
                value={shipmentInfo?.lastUpdatedStamp}
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
                value={shipmentInfo?.createdBy}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>
          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Ngày thực hiện giao hàng (dự kiến) 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={shipmentInfo?.expectedDeliveryStr}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>
        </Grid>
      </Box>

      <TabContext value={tabValue}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <TabList onChange={(event, newValue) => setTabValue(newValue)} >
            <Tab label="Danh sách sản phẩm chưa xử lý" value="1" />
            <Tab label="Danh sách các chuyến giao hàng" value="2" />
          </TabList>
        </Box>
        <TabPanel value="1">
          <StandardTable 
            title='Danh sách sản phẩm chưa xử lý'
            hideCommandBar={true}
            columns={[
              { title: "Sản phẩm", field: "productName" },
              { title: "Số lượng", field: "quantity" },
              { title: "Kệ hàng", field: "bayCode" },
              { title: "Lô", field: "lotId" },
              { title: "Kho", field: "warehouseName" },
              { title: "Địa chỉ nhận hàng", field: "customerAddressName" }
            ]}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            data={deliveryTripItems}
          />
        </TabPanel>
        <TabPanel value="2">
          <StandardTable 
            rowKey="deliveryTripId"
            title="Danh sách các chuyến giao hàng"
            options={{
              selection: true,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            data={tripTableData}
            columns={[
              { title: "Mã chuyến", field: "deliveryTripId",
                editComponent: <TextField InputProps={{readOnly: true}}/> },
              { title: "Ngày tạo", field: "createdStamp",
                editComponent: <TextField value={now}/> },
              { title: "Người tạo", field: "createdBy", 
                editComponent: <TextField value={userLoginId}/> },
              { title: "Trạng thái", field: "deliveryTripStatus",
                editComponent: <TextField value={"Khởi tạo"} InputProps={{readOnly: true}}/> }
            ]}
            editable={{
              onRowAdd: newData => new Promise((resolve, reject) => {
                setTimeout(() => {
                  request(
                    "put",
                    API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP,
                    (res) => {
                      if (res.status == 200) {
                        const adder = {
                          deliveryTripId: res.data.deliveryTripId,
                          createdStamp: now,
                          createdBy: userLoginId,
                          shipmentId: shipmentId,
                          deliveryTripStatus: "Khởi tạo"
                        };
                        setTripTableData([adder, ...tripTableData]);
                        successNoti("Thêm mới chuyến giao hàng thành công");
                      }
                    },
                    {
                      500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
                    },
                    {
                      shipmentId: shipmentId
                    }
                  )
                  resolve();
                });
              }),
              onRowDelete: selectedIds => new Promise((resolve, reject) => {
                setTimeout(() => {
                  console.log("Old data => ", selectedIds);
                  for (var i = 0; i < selectedIds.length; i++) {
                    request(
                      "delete",
                      `${API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP}/${selectedIds[i]}`,
                      (res) => {
                      }
                    );
                  }
                  const deleteTable = tripTableData.filter(
                    trip => !selectedIds.includes(trip["deliveryTripId"]));
                  setTripTableData(deleteTable);
                  successNoti(`Đã xóa ${selectedIds.length} bản ghi`);
                  resolve();
                })
              })
            }}
            onRowClick={(event, rowData) => {
              window.location.href = `${path.replace('/shipments/:id', '/delivery-trips')}/${rowData.deliveryTripId}`;
            }}
          />
        </TabPanel>
      </TabContext>

     
    </Box>
  </Fragment>);
};

const SCR_ID = "SCR_WMSv2_SHIPMENT_DETAIL";
export default withScreenSecurity(ShipmentDetail, SCR_ID, true);
