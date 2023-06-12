import LoadingScreen from "components/common/loading/loading";
import { Box, Button, Grid, Tab, TextField, Typography } from "@mui/material";
import { request } from "api";
import { BayDropDown, BayDropDownHavingProduct, ProductDropDown, WarehouseDropDown } from "components/table/DropDown";
import StandardTable from "components/StandardTable";
import React, { Fragment, useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import useStyles from 'screens/styles';
import { convertToVNDFormat } from "screens/utils/utils";
import { errorNoti, successNoti } from "utils/notification";
import withScreenSecurity from "components/common/withScreenSecurity";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";
import { TabContext, TabList, TabPanel } from "@mui/lab";

const AdminOrderDetail = ( props ) => {
  const history = useHistory();
  const { path } = useRouteMatch();

  const orderId = props.match?.params?.id;
  const classes = useStyles();

  const [loading, setLoading] = useState(true);
  const [orderInfo, setOrderInfo] = useState({});
  const [processingItems, setProcessingItems] = useState([]);
  const [processedItems, setProcessedItems] = useState([]);
  const [remainingItems, setRemainingItems] = useState([]);

  const [selectedProductId, setSelectedProductId] = useState(null);
  const [selectedProductName, setSelectedProductName] = useState(null);

  const [selectedBayId, setSelectedBayId] = useState(null);
  const [selectedBayCode, setSelectedBayCode] = useState(null);

  const [selectedQuantity, setSelectedQuantity] = useState(null);

  const [allWarehouses, setAllWarehouses] = useState([]);
  const [warehouseList, setWarehouseList] = useState([]);
  const [selectedWarehouseId, setSelectedWarehouseId] = useState(null);
  const [selectedWarehouseName, setSelectedWarehouseName] = useState(null);
  const [selectedWarehouse, setSelectedWarehouse] = useState(null);
  const [selectedWarehouseItems, setSelectedWarehouseItems] = useState([]);
  const [maxQuantity, setMaxQuantity] = useState(0);

  const [tabValue, setTabValue] = useState('1');

  const getProductOrderQuantity = ( productId ) => {
    for (var i = 0; i < orderInfo?.remainingItems?.length; i++) {
      if (orderInfo?.remainingItems[i]?.productId == productId) {
        return orderInfo?.remainingItems[i]?.quantity;
      }
    }
    return Number.MAX_SAFE_INTEGER;
  }

  useEffect(() => {
    async function fetchData () {
      await request(
        "get",
        `${API_PATH.ADMIN_SALE_ORDER}/${orderId}`,
        (res) => {
          setOrderInfo(res.data);
          setRemainingItems(res.data.remainingItems)
        }
      );

      await request(
        "get",
        `${API_PATH.WAREHOUSE_DETAIL_WITH_PRODUCT}/${orderId}`,
        (res) => {
          setAllWarehouses(res.data);
        }
      );

      setLoading(false);
    }

    fetchData();
  }, []);

  useEffect(() => {
    if (selectedProductId == null) {
      setWarehouseList(allWarehouses);
      return;
    }

    // Tìm tất cả warehouse có selectedProductId trong kho
    const newWarehouseList = [];
    for (var i = 0; i < allWarehouses.length; i++) {
      const warehouseDetail = allWarehouses[i];
      var itemCount = 0;
      for (var j = 0; j < warehouseDetail.items?.length; j++) {
        if (warehouseDetail.items[j]?.productId == selectedProductId) {
          itemCount += warehouseDetail.items[j]?.quantity;
        }
      }
      if (itemCount > 0) {
        newWarehouseList.push(warehouseDetail);
      }
      setWarehouseList(newWarehouseList);
    }
  }, [selectedProductId]);

  useEffect(() => {
    for (var i = 0; i < warehouseList.length; i++) {
      if (warehouseList[i]?.info?.id == selectedWarehouseId) {
        setSelectedWarehouse(warehouseList[i]);
        setSelectedWarehouseItems(warehouseList[i].items);
        return;
      }
    }
  }, [selectedWarehouseId]);

  useEffect(() => {
    if (selectedProductId != null && selectedBayId != null) {
      var totalProductOnBay = 0;
      console.log("All warehouse => ", allWarehouses);
      console.log("selectedProductId => ", selectedProductId);
      console.log("selectedBayId => ", selectedBayId);
      for (var i = 0; i < allWarehouses.length; i++) {
        for (var j = 0; j < allWarehouses[i]?.items?.length; j++) {
          const item = allWarehouses[i]?.items[j];
          if (item?.productId == selectedProductId && item?.bayId == selectedBayId) {
            totalProductOnBay += item?.quantity;
          }
        }
      }
      setMaxQuantity(Math.min(totalProductOnBay, getProductOrderQuantity(selectedProductId)));
    }
  }, [selectedBayId]);

  const saveProcessingItems = () => {
    var isDone = remainingItems.length == 0;
    request(
      "put",
      API_PATH.ASSIGN_ORDER_ITEM,
      (res) => {
        if (res.status == 200) {
          successNoti("Phân phối hàng hóa thành công");
          history.push(`${path.substring(0, path.lastIndexOf('/'))}`);
        }
      },
      {
        500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
      },
      {
        orderId: orderId,
        items: processingItems,
        done: isDone
      }
    )
  }

  const autoAssignButtonHandle = () => {
    setLoading(true);
    request(
      "put",
      `${API_PATH.AUTO_ASSIGN_ORDER_ITEM}/${orderId}`,
      (res) => {
        const data = res.data;
        setProcessingItems(data.processingItems);
        setRemainingItems(data.remainingItems);
        setLoading(false);
      }
    )
  }
  
  return (
    loading ? <LoadingScreen /> :
    <Fragment>
    <Box>
    <Grid container justifyContent="space-between" 
        className={classes.headerBox} >
        <Grid>
          <Typography variant="h5">
            Thông tin đơn hàng</Typography>
        </Grid>
        <Grid className={classes.buttonWrap}>
          <Button variant="contained" className={classes.addButton} 
            type="submit" onClick={saveProcessingItems} >Lưu</Button>
        </Grid>
        <Grid className={classes.buttonWrap}>
          <Button variant="contained" className={classes.addButton} 
            type="submit" onClick={autoAssignButtonHandle} >Phân phối tự động</Button>
        </Grid>
      </Grid>
    </Box>

    <Box className={classes.bodyBox}>
      <Box className={classes.formWrap}>
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Grid container spacing={3}>
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
          </Grid>

          <Grid item xs={12}>
            <Grid container spacing={3}>
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

          <Grid item xs={12}>
            <Grid container spacing={3}>
              <Grid item xs={6}>
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

              <Grid item xs={6}>
              <Box className={classes.inputWrap}>
                  <Box className={classes.labelInput}>
                    Tên người nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    value={orderInfo?.customerName}
                    InputProps={{
                      readOnly: true,
                    }}
                  />
                </Box>
              </Grid>
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

        </Grid>
      </Box>

      <TabContext value={tabValue}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <TabList onChange={(event, newValue) => setTabValue(newValue)} >
            <Tab label="Sản phẩm cần phân phối giao hàng" value="1" />
            <Tab label="Sản phẩm đang phân phối" value="2" />
            <Tab label="Sản phẩm đã phân phối" value="3" />
          </TabList>
        </Box>
        <TabPanel value="1">
          <StandardTable
            title="Danh sách sản phẩm cần phân phối giao hàng"
            hideCommandBar={true}
            columns={[
              { title: "Tên sản phẩm", field: "productName" },
              { title: "Số lượng", field: "quantity" }
            ]}
            data={remainingItems}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
          />
        </TabPanel>
        <TabPanel value="2">
          <StandardTable
            rowKey="productId"
            title="Danh sách sản phẩm đang phân phối"
            hideCommandBar={true}
            columns={[
              { title: "Tên sản phẩm", field: "productName",
                editComponent: <ProductDropDown 
                  productList={orderInfo?.items} 
                  setSelectedProductId={setSelectedProductId}
                  setSelectedProductName={setSelectedProductName} /> },
              { title: "Kho", field: "warehouseName",
                editComponent: <WarehouseDropDown
                  warehouseList={warehouseList.map(warehouse => warehouse.info)}
                  setSelectedWarehouseId={setSelectedWarehouseId}
                  setSelectedWarehouseName={setSelectedWarehouseName} />},
              { title: "Vị trí kệ hàng", field: "bayCode",
                editComponent: <BayDropDownHavingProduct
                  selectedWarehouseItems={selectedWarehouseItems} 
                  productId={selectedProductId}
                  setSelectedBayId={setSelectedBayId}
                  setSelectedBayCode={setSelectedBayCode} /> },
              { title: "Số lượng", field: "quantity", 
                editComponent: <TextField
                  type="number"
                  InputProps={{
                    inputProps: { 
                        max: maxQuantity, min: 1 
                    }
                  }}
                  value={selectedQuantity}
                  onChange={(e) => setSelectedQuantity(e.target.value)}
                  /> },
            ]}
            data={processingItems}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            editable={{
              onRowAdd: newData => new Promise((resolve, reject) => {
                setTimeout(() => {
                  const adder = {
                    productId: selectedProductId,
                    productName: selectedProductName,
                    warehouseId: selectedWarehouseId,
                    warehouseName: selectedWarehouseName,
                    bayId: selectedBayId,
                    bayCode: selectedBayCode,
                    quantity: selectedQuantity
                  };
                  setProcessingItems([...processingItems, adder]);

                  // update số lượng sản phẩm cần phân phối
                  var newOrderInfo = {...orderInfo};
                  for (var i = 0; i < newOrderInfo?.remainingItems?.length; i++) {
                    if (newOrderInfo?.remainingItems[i]?.productId == selectedProductId) {
                      const newQuantity = newOrderInfo?.remainingItems[i]?.quantity - selectedQuantity;
                      newOrderInfo.remainingItems[i].quantity = newQuantity;
                      if (newQuantity == 0) {
                        newOrderInfo.remainingItems.splice(i, 1);
                      }
                      break;
                    }
                  }
                  setOrderInfo(newOrderInfo);
                  setRemainingItems(newOrderInfo.remainingItems);
                  setSelectedQuantity(0);
                  resolve();
                })
              }),
              onRowDelete: oldData => new Promise((resolve, reject) => {
                setTimeout(() => {
                  console.log("Old data => ", oldData);
                  // TODO: on row delete implementation....
                })
              })
            }}
          />
        </TabPanel>
        <TabPanel value="3">
          <StandardTable
            title="Danh sách sản phẩm đã phân phối"
            hideCommandBar={true}
            columns={[
              { title: "Tên sản phẩm", field: "productName" },
              { title: "Số lượng", field: "quantity" },
              { title: "Kho", field: "warehouseName" },
              { title: "Vị trí kệ hàng", field: "bayCode" },
              { title: "Trạng thái", field: "status"},
              { title: "Số lô", field: "lotId" },
              { title: "Ngày phân phối", field: "createdDate"}
            ]}
            data={orderInfo?.processedItems}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
          />
        </TabPanel>
      </TabContext>
    </Box>
  </Fragment>)
};

const SCR_ID = "SCR_WMSv2_ORDER_DETAIl";
export default withScreenSecurity(AdminOrderDetail, SCR_ID, true);