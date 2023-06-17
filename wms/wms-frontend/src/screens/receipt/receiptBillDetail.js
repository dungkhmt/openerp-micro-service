import { Box, Button, Grid, TextField, Typography } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from "screens/styles";
import { convertToVNDFormat } from "screens/utils/utils";
import { pdf, Page, Text, View, Document, Font } from '@react-pdf/renderer';
import { saveAs } from "file-saver";
import { StyleSheet } from "@react-pdf/renderer";

Font.register({
  family: "Roboto",
  src:
    "https://cdnjs.cloudflare.com/ajax/libs/ink/3.1.10/fonts/Roboto/roboto-light-webfont.ttf"
});

const ReceiptBillDocument = ( { receiptBillInfo } ) => {
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
      width: "16.66%", 
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
        <Text>Phiếu nhập hàng</Text>
      </View>
      <View style={styles.view}>
        <Text>Mã phiếu: {receiptBillInfo.receiptBillId}</Text>
      </View>
      <View style={styles.view}>
        <Text>Tổng giá trị hàng: {convertToVNDFormat(receiptBillInfo.totalPrice)}</Text>
      </View>
      <View style={styles.view}>
        <Text>Ngày tạo phiếu: {receiptBillInfo.createdStampStr}</Text>
      </View>
      <View style={styles.view}>
        <Text>Ngày cập nhật: {receiptBillInfo.lastUpdateStampStr}</Text>
      </View>
      <View style={styles.view}>
        <Text>Người xử lý: {receiptBillInfo.createdBy}</Text>
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
            <Text style={styles.tableCell}>Kho nhận</Text> 
          </View> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Kệ hàng</Text> 
          </View> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Số lô</Text> 
          </View> 
          <View style={styles.tableCol}> 
            <Text style={styles.tableCell}>Giá nhập hàng</Text> 
          </View> 
        </View>
        {
          receiptBillInfo?.processedItems != null && receiptBillInfo?.processedItems.length > 0 &&
          receiptBillInfo?.processedItems.map(item =>
            <View style={styles.tableRow}> 
              <View style={styles.tableCol}> 
                <Text style={styles.tableCell}>{item.productName}</Text> 
              </View> 
              <View style={styles.tableCol}> 
                <Text style={styles.tableCell}>{item.quantity}</Text> 
              </View> 
              <View style={styles.tableCol}>
                <Text style={styles.tableCell}>{item.warehouseName}</Text> 
              </View>
              <View style={styles.tableCol}> 
                <Text style={styles.tableCell}>{item.bayCode}</Text> 
              </View> 
              <View style={styles.tableCol}>
                <Text style={styles.tableCell}>{item.lotId}</Text> 
              </View>
              <View style={styles.tableCol}> 
                <Text style={styles.tableCell}>{convertToVNDFormat(item.importPrice)}</Text> 
              </View> 
            </View> 
          )
        }
      </View>
    </Page>
  </Document>
}

const ReceiptBilLDetail = ( props ) => {

  const receiptBillId = props.match?.params?.id;
  const [receiptBillInfo, setReceiptBillInfo] = useState({});
  const [loading, setLoading] = useState(true);
  const classes = useStyles();

  useEffect(() => {
    const fetchData = async () => {
      await request(
        'get',
        `${API_PATH.RECEIPT_BILL_BY_RECEIPT_BILL_ID}/${receiptBillId}`,
        (res) => {
          setReceiptBillInfo(res.data);
        }
      );

      setLoading(false);
    }

    fetchData();
  }, []);

  const exportPDF = async () => {
    const blob = await pdf(<ReceiptBillDocument receiptBillInfo={receiptBillInfo} />).toBlob();
    saveAs(blob, `${receiptBillId}.pdf`);
  }

  return (
    loading ? <LoadingScreen /> : 
    <Fragment>
      <Box>
        <Grid container justifyContent="space-between" className={classes.headerBox} >
          <Grid>
            <Typography variant="h5">
              Thông tin phiếu nhập hàng
            </Typography>
          </Grid>
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} onClick={exportPDF}>Tải xuống phiếu</Button>
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
                          name="receiptName"
                          value={receiptBillId}
                          InputProps={{
                            readOnly: true,
                          }}
                        ></TextField>
                    </Grid>
                    <Grid item xs={6}>
                      <Box className={classes.labelInput}>
                        Tổng giá trị hàng</Box>
                        <TextField
                          fullWidth
                          variant="outlined"
                          size="small"
                          name="receiptName"
                          value={convertToVNDFormat(receiptBillInfo?.totalPrice)}
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
                          name="receiptName"
                          value={receiptBillInfo?.createdStampStr}
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
                          name="receiptName"
                          value={receiptBillInfo?.lastUpdateStampStr}
                          InputProps={{
                            readOnly: true,
                          }}
                        ></TextField>
                    </Grid>
                    <Grid item xs={6}>
                      <Box className={classes.labelInput}>
                        Người xử lý</Box>
                        <TextField
                          fullWidth
                          variant="outlined"
                          size="small"
                          name="receiptName"
                          value={receiptBillInfo?.createdBy}
                          InputProps={{
                            readOnly: true,
                          }}
                        ></TextField>
                    </Grid>
                  </Grid>

                  <StandardTable 
                    title="Danh sách sản phẩm"
                    data={receiptBillInfo?.processedItems}
                    columns={[
                      { title: "Sản phẩm", field: "productName" },
                      { title: "Số lượng", field: "quantity" },
                      { title: "Kho nhận", field: "warehouseName" },
                      { title: "Kệ hàng", field: "bayCode" },
                      { title: "Số lô", field: "lotId" },
                      { title: "Giá nhập hàng", field: "importPrice" },
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

const SCR_ID = "SCR_WMSv2_RECEIPT_BILL_DETAIL";
export default withScreenSecurity(ReceiptBilLDetail, SCR_ID, true);