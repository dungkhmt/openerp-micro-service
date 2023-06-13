import { Box, Grid, TextField, Typography } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from "screens/styles";
import { convertToVNDFormat } from "screens/utils/utils";

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
                        Ngày cập nhật cuối</Box>
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