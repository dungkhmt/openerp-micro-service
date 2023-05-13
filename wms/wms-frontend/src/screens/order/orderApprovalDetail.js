import { Box, Button, Grid, TextField, Typography } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from 'screens/styles.js';
import { convertToVNDFormat } from "screens/utils/utils";
import { errorNoti, successNoti } from "utils/notification";
import { useRouteMatch } from "react-router-dom";
import { useHistory } from "react-router";
import LoadingScreen from "components/common/loading/loading";

const OrderApprovalDetail = ( props ) => {
  const { path } = useRouteMatch();
  const history = useHistory();
  const orderId = props.match?.params?.id;
  const classes = useStyles();
  const [orderInfo, setOrderInfo] = useState({});
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData () {
      await request(
        "get",
        `${API_PATH.ADMIN_SALE_ORDER}/${orderId}`,
        (res) => {
          var data = res.data;
          for (var i = 0; i < data?.items?.length; i++) {
            const cost = data?.items[i]?.priceUnit;
            const costFormated = convertToVNDFormat(cost);
            data.items[i].priceUnit = costFormated;
          }
          setOrderInfo(data);
        }
      );

      setLoading(false);
    }

    fetchData();
  }, []);

  const approveOrderButtonHandle = () => {
    request(
      "put",
      `${API_PATH.APPROVE_SALE_ORDER}/${orderId}`,
      (res) => {
        if (res.status == 200) {
          successNoti("Phê duyệt đơn hàng thành công");
          history.push(`${path.substring(0, path.lastIndexOf('/'))}`);
        }
      },
      {
        500: () => errorNoti("Phê duyệt đơn hàng thất bại. Vui lòng thử lại")
      }
    )
  }

  const cancelOrderButtonHandle = () => {
    request(
      "put",
      `${API_PATH.CANCEL_SALE_ORDER}/${orderId}`,
      (res) => {
        if (res.status == 200) {
          successNoti("Hủy đơn hàng thành công");
          history.push(`${path.substring(0, path.lastIndexOf('/'))}`);
        }
      },
      {
        500: () => errorNoti("Hủy đơn hàng thất bại. Vui lòng thử lại")
      }
    )
  }

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
        {
          orderInfo?.statusCode == 'CREATED' &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton}
              type="submit" onClick={approveOrderButtonHandle} >Phê duyệt</Button>
          </Grid>
        }
        {
          orderInfo?.statusCode == 'CREATED' &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton}
              type="submit" onClick={cancelOrderButtonHandle} >Hủy đơn hàng</Button>
          </Grid>
        }
      </Grid>

      <Box className={classes.bodyBox}>
        <Box className={classes.formWrap}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Ngày tạo đơn hàng
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={orderInfo?.createdDate}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Phương thức thanh toán
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={orderInfo?.paymentMethod}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
              </Grid>
              <Grid item xs={12}>
                <Box className={classes.inputWrap}>
                  <Box className={classes.labelInput}>
                    Địa chỉ nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    value={orderInfo.receiptAddress}
                    InputProps={{
                      readOnly: true,
                    }}
                  />
                </Box>
              </Grid>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Tổng giá trị đơn hàng
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={convertToVNDFormat(orderInfo?.totalOrderCost)}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Trạng thái
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={orderInfo?.status}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
              </Grid>
            </Grid>
            <Grid item xs={6}>
              <Grid item xs={12}>
                <Box className={classes.inputWrap}>
                  <Box className={classes.labelInput}>
                    Tài khoản mua hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    value={orderInfo?.userLoginId}
                    InputProps={{
                      readOnly: true,
                    }}
                  />
                </Box>
              </Grid>

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Số lượng đơn hàng giao thành công
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={orderInfo?.totalSuccessOrderCount}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Tổng giá trị đơn hàng giao thành công
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={convertToVNDFormat(orderInfo?.totalSuccessOrderCost)}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
              </Grid>

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Tổng số lượng đơn hàng giao thất bại
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={orderInfo?.totalCancelledOrderCount}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.inputWrap}>
                    <Box className={classes.labelInput}>
                      Tổng giá trị đơn hàng giao thất bại
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      value={convertToVNDFormat(orderInfo?.totalCancelledOrderCost)}
                      InputProps={{
                        readOnly: true,
                      }}
                    />
                  </Box>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </Box>
      </Box>

      <StandardTable
        title="Danh sách sản phẩm"
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        data={orderInfo?.items}
        columns={orderInfo?.statusCode == "COMPLETED" ? [
          { title: "Tên sản phẩm", field: "productName" },
          { title: "Số lượng", field: "quantity" },
          { title: "Đơn giá", field: "priceUnit" },
          { title: "Trạng thái", field: "deliveryStatus" }
        ] : [
          { title: "Tên sản phẩm", field: "productName" },
          { title: "Số lượng", field: "quantity" },
          { title: "Đơn giá", field: "priceUnit" }
        ]}
      />
    </Box>
  </Fragment>)
}

export default OrderApprovalDetail;