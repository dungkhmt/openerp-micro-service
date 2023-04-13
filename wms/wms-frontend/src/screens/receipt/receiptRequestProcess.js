import { Fragment, useState, useEffect } from "react";
import useStyles from "screens/styles";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";
import { Box, Button, Grid, TextField, Typography } from "@mui/material";
import { request } from "api";
import { API_PATH } from "../apiPaths";
import StandardTable from "components/table/StandardTable";
import { convertTimeStampToDate } from "../utils/utils";

const ReceiptRequestProcess = ( props ) => {
  const receiptId = props.match?.params?.id;
  const classes = useStyles();

  const [receiptInfo, setReceiptInfo] = useState(null);

  const history = useHistory();
  const { path } = useRouteMatch();

  useEffect(() => {
    request(
      "get",
      API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "/" + receiptId,
      (res) => {
        setReceiptInfo(res.data);
      }
    )
  }, []);

  return <Fragment>
    <Box>
      <Grid container justifyContent="space-between" className={classes.headerBox}>
        <Grid>
          <Typography variant="h5">Phê duyệt đơn xin nhập hàng</Typography>
        </Grid>
        <Grid className={classes.buttonWrap}>
          <Button variant="contained" className={classes.addButton} 
            type="submit" >Phê duyệt</Button>
        </Grid>
        <Grid classNam={classes.buttonWrap}>
          <Button variant="contained" className={classes.addButton} 
            type="submit" >Hủy</Button>
        </Grid>
      </Grid>
    </Box>

    <Box className={classes.formWrap} component="form">
      <Box>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Box className={classes.boxInfor}>
              <Typography className={classes.inforTitle} variant="h6">
                Thông tin chung
              </Typography>
              <Grid container spacing={3} className={classes.inforWrap}>
                <Grid item xs={12}>
                  <Box className={classes.labelInput}>
                    Người tạo đơn
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="createdBy"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.createdBy} />
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Ngày tạo đơn nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="createdDate"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={convertTimeStampToDate(receiptInfo?.createdDate)} />
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Ngày muốn nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="expectedReceiveDate"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={convertTimeStampToDate(receiptInfo?.expectedReceiveDate)} />
                </Grid>
                <Grid item xs={12}>
                  <Box className={classes.labelInput}>
                    Lý do tạo đơn
                  </Box>
                  <TextField 
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="createdReason"
                    multiline
                    rows={4}
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.createdReason}
                  />
                </Grid>
              </Grid>
            </Box>

            <Box className={classes.boxInfor}>
              <StandardTable 
                title="Danh sách hàng hóa cần xử lý"
              />
            </Box>

            <Box className={classes.boxInfor}>
              <StandardTable 
                title="Danh sách hàng hóa đã xử lý"
              />
            </Box>
          </Grid>
        </Grid>
      </Box>
    </Box>
  </Fragment>
}

export default ReceiptRequestProcess;