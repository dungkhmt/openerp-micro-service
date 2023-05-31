import { Box, Grid, TextField, Typography } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from 'screens/styles.js';
import { convertTimeStampToDate } from "screens/utils/utils";

const ShipmentDetail = ( props ) => {
  const shipmentId = props.match?.params?.id;
  const classes = useStyles();
  const [shipmentInfo, setShipmentInfo] = useState({});
  const [tripTableData, setTripTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);

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
            Thông tin đơn hàng</Typography>
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
                value={convertTimeStampToDate(shipmentInfo?.expectedDeliveryStamp)}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>
        </Grid>
      </Box>

      <StandardTable 
        title="Danh sách các chuyến giao hàng"
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        data={tripTableData}
        columns={[
          { title: "Mã chuyến", field: "deliveryTripId"},
          { title: "Người giao hàng", field: "deliveryPersonName" },
          { title: "Quãng đường", field: "distance" },
          { title: "Số điểm giao hàng", field: "totalLocations" }
        ]}
      />
    </Box>
  </Fragment>);
};

const SCR_ID = "SCR_WMSv2_SHIPMENT_DETAIL";
export default withScreenSecurity(ShipmentDetail, SCR_ID, true);
