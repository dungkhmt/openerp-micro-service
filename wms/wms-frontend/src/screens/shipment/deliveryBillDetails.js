import { Box, Button, Grid, TextField, Typography } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from "screens/styles";
import { pdf, Page, Text, View, Document, Font } from '@react-pdf/renderer';
import { saveAs } from "file-saver";
import { StyleSheet } from "@react-pdf/renderer";

Font.register({
  family: "Roboto",
  src:
    "https://cdnjs.cloudflare.com/ajax/libs/ink/3.1.10/fonts/Roboto/roboto-light-webfont.ttf"
});

const DeliveryBillDocument = ( { deliveryBillInfo } ) => {
  const styles = StyleSheet.create({
    page: {
      fontFamily: "Roboto",
      fontSize: 18
    },
    centerView: {
      textAlign: 'center',
      marginTop: 20,
      fontSize: 24
    },
    wrapView: {
      justifyContent: 'space-around',
      flex: '1',
      flexDirection: 'row'
    },
    view: {
      marginLeft: 15,
      marginTop: 10,
      fontSize: 18
    },
    table: { 
      marginTop: 5,
      display: "table", 
      width: "auto"
    }, 
    tableRow: { 
      margin: "auto", 
      flexDirection: "row",
    }, 
    tableCol: { 
      width: "20%", 
      borderStyle: "solid", 
      borderWidth: 1, 
      borderLeftWidth: 1, 
      borderTopWidth: 1 
    }, 
    tableCell: { 
      margin: "auto", 
      marginTop: 5, 
      fontSize: 18 
    }
  });

  return <Document>
    <Page size="A4" style={styles.page}>
      <View style={styles.centerView}>
        <Text>Phiếu xuất hàng</Text>
      </View>
      <View style={styles.view}>
        <Text>Mã phiếu: {deliveryBillInfo.deliveryBillId}</Text>
      </View>
      <View style={styles.view}>
        <Text>Mã chuyến giao hàng: {deliveryBillInfo.deliveryTripId}</Text>
      </View>
      <View style={styles.view}>
        <Text>Người xử lý: {deliveryBillInfo.createdBy}</Text>
      </View>
      <View style={styles.view}>
        <Text>Ngày tạo phiếu: {deliveryBillInfo.createdStampStr}</Text>
      </View>
      <View style={styles.view}>
        <Text>Ngày cập nhật: {deliveryBillInfo.lastUpdateStampStr}</Text>
      </View>
      <View style={styles.view}>
        <Text>Danh sách sản phẩm:</Text>
      </View>
      <View style={styles.table}> 
        <View style={styles.tableRow}> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Sản phẩm</Text> 
          </View> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Số lượng</Text> 
          </View> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Kệ hàng</Text> 
          </View> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Lô</Text> 
          </View> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Kho</Text> 
          </View> 
        </View>
        {
          deliveryBillInfo?.items != null && deliveryBillInfo?.items.length > 0 &&
          deliveryBillInfo?.items.map(item =>
            <View style={styles.tableRow}> 
              <View style={styles.tableCol}> 
                <Text style={styles.tableCell}>{item.productName}</Text> 
              </View> 
              <View style={styles.tableCol}> 
                <Text style={styles.tableCell}>{item.quantity}</Text> 
              </View> 
              <View style={styles.tableCol}>
                <Text style={styles.tableCell}>{item.bayCode}</Text> 
              </View>
              <View style={styles.tableCol}>
                <Text style={styles.tableCell}>{item.lotId}</Text> 
              </View>
              <View style={styles.tableCol}> 
                <Text style={styles.tableCell}>{item.warehouseName}</Text> 
              </View> 
            </View> 
          )
        }
      </View>
    </Page>
  </Document>
}

const DeliveryBillDetail = ( props ) => {
  const classes = useStyles();
  const deliveryBillId = props.match?.params?.id;
  const [deliveryBillInfo, setDeliveryBillInfo] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      await request(
        'get',
        `${API_PATH.DELIVERY_BILL}/${deliveryBillId}`,
        (res) => {
          setDeliveryBillInfo(res.data);
        }
      )

      setLoading(false);
    };

    fetchData();
  }, []);

  const exportPDF = async () => {
    const blob = await pdf(<DeliveryBillDocument deliveryBillInfo={deliveryBillInfo} />).toBlob();
    saveAs(blob, `${deliveryBillId}.pdf`);
  }

  return (
    loading ?
    <LoadingScreen /> :
    <Fragment>
      <Box>
        <Grid container justifyContent="space-between" className={classes.headerBox}>
          <Grid>
            <Typography variant="h5">Thông tin phiếu xuất hàng</Typography>
          </Grid>
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} onClick={exportPDF} >Tải xuống phiếu</Button>
          </Grid>
        </Grid>
      </Box>

      <Box className={classes.formWrap}
        component="form">
        <Box>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Box className={classes.boxInfor}>
                <Typography className={classes.inforTitle} variant="h6">
                  Thông tin chung
                </Typography>

                <Grid container spacing={2} className={classes.inforWrap}>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>
                      Mã phiếu</Box>
                      <TextField
                        fullWidth
                        variant="outlined"
                        size="small"
                        value={deliveryBillId}
                        InputProps={{
                          readOnly: true,
                        }}
                      ></TextField>
                  </Grid>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>
                      Người tạo</Box>
                      <TextField
                        fullWidth
                        variant="outlined"
                        size="small"
                        value={deliveryBillInfo?.createdBy}
                        InputProps={{
                          readOnly: true,
                        }}
                      ></TextField>
                  </Grid>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>
                      Ngày tạo</Box>
                      <TextField
                        fullWidth
                        variant="outlined"
                        size="small"
                        value={deliveryBillInfo?.createdStampStr}
                        InputProps={{
                          readOnly: true,
                        }}
                      ></TextField>
                  </Grid>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>
                      Ngày cập nhật</Box>
                      <TextField
                        fullWidth
                        variant="outlined"
                        size="small"
                        value={deliveryBillInfo?.lastUpdateStampStr}
                        InputProps={{
                          readOnly: true,
                        }}
                      ></TextField>
                  </Grid>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>
                      Mã chuyến giao hàng</Box>
                      <TextField
                        fullWidth
                        variant="outlined"
                        size="small"
                        value={deliveryBillInfo?.deliveryTripId}
                        InputProps={{
                          readOnly: true,
                        }}
                      ></TextField>
                  </Grid>
                </Grid>

                <StandardTable 
                  title="Danh sách sản phẩm"
                  data={deliveryBillInfo?.items}
                  columns={[
                    { title: "Sản phẩm", field: "productName" },
                    { title: "Số lượng", field: "quantity" },
                    { title: "Kệ hàng", field: "bayCode" },
                    { title: "Lô", field: "lotId" },
                    { title: "Kho", field: "warehouseName" }
                  ]}
                />
              </Box>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </Fragment>
  )
}


const SCR_ID = "SCR_WMSv2_DELIVERY_BILL_DETAIL";
export default withScreenSecurity(DeliveryBillDetail, SCR_ID, true);