// Màn hình tạo mới đơn xin nhập hàng dành cho quản lý bán hàng

import { Button, Grid, MenuItem, Select, TextField, Typography } from "@mui/material";
import { Box } from "@mui/system";
import useStyles from "screens/styles";
import { useForm } from "react-hook-form";
import StandardTable from "components/StandardTable";
import { request } from "api";
import { API_PATH } from "../apiPaths";
import { getProductNameFromProductId, getWarehouseNameByWarehouseId } from "../utils/utils";
import { errorNoti, successNoti } from "utils/notification";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";

import { Fragment, useState, useEffect } from "react";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";

const ReceiptRequestDetail = ( props ) => {
  const history = useHistory();
  const { path } = useRouteMatch();
  const classes = useStyles();
  const { register, errors, handleSubmit, watch, getValues } = useForm();
  const [productTableData, setProductTableData] = useState([]);
  const [selectedProductId, setSelectedProductId] = useState(null);
  const [selectedWarehouseId, setSelectedWarehouseId] = useState(null);

  const [newQuantity, setNewQuantity] = useState(0);

  const receiptId = props.match?.params?.id;
  const isCreateForm = receiptId == null;
  const [receiptInfo, setReceiptInfo] = useState(null);

  // Data fetched from server
  const [productList, setProductList] = useState([]);
  const [warehouseList, setWarehouseList] = useState([]);
  const [isLoading, setLoading] = useState(true);

  const ProductDropDown = ({ productList, setSelectedProduct }) => {
    return <Select onChange={(e) => setSelectedProduct(e.target.value)}>
      {
        productList.length > 0 &&
        productList.map(product => 
          <MenuItem value={product.productId}>
            {product.name}
          </MenuItem>)
      }
    </Select>
  }

  const WarehouseDropDown = ({ warehouseList, setSelectedWarehouseId }) => {
    return <Select onChange={(e) => setSelectedWarehouseId(e.target.value)}>
      {
        warehouseList.length > 0 &&
        warehouseList.map(warehouse => 
          <MenuItem value={warehouse.warehouseId}>
            {warehouse.name}
          </MenuItem>)
      }
    </Select>
  }

  const submitForm = ( data ) => {
    const requestBody = {
      "createdReason": data.createdReason,
      "expectedReceiveDate": data.expectedReceiveDate,
      "receiptItemList": productTableData
    }
    request(
      "put",
      API_PATH.RECEIPT,
      (res) => {
        if (res.status == 200) {
          successNoti("Tạo đơn xin nhập hàng hóa thành công");
          setProductTableData([]);
          history.push(`${path}`);
        } else {
          errorNoti("Có lỗi khi tạo đơn. Vui lòng thử lại sau");
        }
      },
      {

      },
      requestBody
    );
  }

  useEffect(() => {
    
    async function fetchData() {
      await request(
        "get",
        API_PATH.PRODUCT,
        (res) => {
          setProductList(res.data);
        }
      );

      await request(
        "get",
        API_PATH.WAREHOUSE,
        (res) => {
          setWarehouseList(res.data);
        }
      )

      if (!isCreateForm) {
        await request(
          "get",
          API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "/" + receiptId,
          (res) => {
            setReceiptInfo(res.data);
            setProductTableData(res.data?.items);
          }
        )
      }

      setLoading(false)
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
              Tạo mới đơn xin nhập hàng
            </Typography>
          </Grid>
          {
            isCreateForm && 
            <Grid className={classes.buttonWrap}>
              <Button variant="contained" className={classes.addButton} 
                type="submit" onClick={handleSubmit(submitForm)} >Lưu</Button>
            </Grid>
          }
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
                      Ngày muốn nhận hàng
                    </Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      inputRef={register({ required: false })}
                      name="expectedReceiveDate"
                      type={"date"} 
                      InputProps={{
                        readOnly: !isCreateForm,
                      }}
                      value={receiptInfo?.expectedReceiveDate} />
                  </Grid>

                  <Grid item xs={12}>
                    <Box className={classes.labelInput}>
                      Lý do tạo đơn
                    </Box>
                    <TextField 
                      fullWidth
                      variant="outlined"
                      size="small"
                      inputRef={register({ required: false })}
                      name="createdReason"
                      multiline
                      rows={4}
                      InputProps={{
                        readOnly: !isCreateForm,
                      }}
                      value={receiptInfo?.createdReason}
                    />
                  </Grid>
                </Grid>
              </Box>

              <Box className={classes.boxInfor}>
                {/* <Typography className={classes.inforTitle} variant="h6">
                  Danh sách hàng hóa
                </Typography> */}
                <StandardTable 
                  rowKey="productName"
                  title="Danh sách hàng hóa"
                  columns={[
                    { title: "Tên hàng hóa", field: "productName", 
                      editComponent: <ProductDropDown 
                        setSelectedProduct={setSelectedProductId}
                        productList={productList} /> },
                    { title: "Số lượng", field: "quantity",
                      editComponent: <TextField
                        type={"number"}
                        value={newQuantity}
                        onChange={(e) => setNewQuantity(parseInt(e.target.value))} /> },
                    { title: "Kho nhận (không bắt buộc)", field: "warehouseName", 
                      editComponent: <WarehouseDropDown 
                        setSelectedWarehouseId={setSelectedWarehouseId}
                        warehouseList={warehouseList} />}
                  ]}
                  data={productTableData}
                  options={{
                    selection: false,
                    pageSize: 5,
                    search: true,
                    sorting: true,
                  }}
                  hideCommandBar={true}
                  editable={isCreateForm && {
                    onRowAdd: newData => new Promise((resolve, reject) => {
                      setTimeout(() => {
                        console.log("new data => ", newData);
                        if (newQuantity == undefined 
                          || selectedProductId == null 
                          || newQuantity == null 
                          || newQuantity < 1) {
                          errorNoti("Vui lòng kiểm tra lại thông tin đơn hàng vừa nhập.");
                          reject();
                        } else {
                          const newRow = {
                            "productId": selectedProductId,
                            "productName": getProductNameFromProductId(selectedProductId, productList),
                            "quantity": newQuantity,
                            "warehouseId": selectedWarehouseId,
                            "warehouseName": getWarehouseNameByWarehouseId(selectedWarehouseId, warehouseList)
                          }
                          setProductTableData([...productTableData, newRow]);
                          setSelectedProductId(null);
                          setSelectedWarehouseId(null);
                          resolve();
                        }
                      });
                    }),
                    onRowDelete: selectedProductNames => new Promise((resolve, reject) => {
                      setTimeout(() => {
                        console.log("selected data => ", selectedProductNames);
                        const dataDelete = productTableData.filter(
                          product => !selectedProductNames.includes(product["productName"]));
                        setProductTableData([...dataDelete]);
                        resolve();
                      });
                    })
                  }}
                />
              </Box>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </Fragment>
  )
}

const SCR_ID = "SCR_WMSv2_CREATE_RECEIPT_REQUEST";
export default withScreenSecurity(ReceiptRequestDetail, SCR_ID, true);
