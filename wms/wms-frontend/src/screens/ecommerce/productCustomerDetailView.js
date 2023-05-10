import { RequireStar } from "components/common/requireStar";
import React, { useEffect, useState } from "react";
import { Fragment } from "react";
import { Box, Grid, Button, Typography, TextField, Select, MenuItem } from "@mui/material";
import useStyles from "screens/styles";
import { errorNoti, successNoti } from 'utils/notification';
import { request } from 'api';
import { API_PATH } from "../apiPaths";
import { LOCAL_STORAGE } from "components/constants";

const ProductCustomerDetailView = ( props ) => {
  const classes = useStyles();

  const [imageURL, setImageURL] = useState(null);
  const [productCategories, setProductCategories] = useState([]);
  const [warehouseDetails, setWarehouseDetails] = useState([]);

  const productId = props.match?.params?.id;
  const [productInfo, setProductInfo] = useState(null);

  // for selection field
  const [categoryId, setCategoryId] = useState(null);
  const [uom, setUom] = useState(null);

  const addToCart = () => {
    // check if product is already added to cart
    const items = JSON.parse(localStorage.getItem(LOCAL_STORAGE.CART_ITEMS));
    console.log("Items => ", items);
    
    if (items != null) {
      var quantity = 1;
      var oldIndex;
      for (var i = 0; i < items?.length; i++) {
        if (items[i].productId == productId) {
          quantity = items[i].quantity + 1;
          oldIndex = i;
          break;
        }
      }
      if (oldIndex != undefined) {
        items.splice(oldIndex, 1);
      }
      const newItem = {
        productId: productId,
        quantity: quantity
      };
      localStorage.setItem(LOCAL_STORAGE.CART_ITEMS, JSON.stringify([...items, newItem]));
    } else {
      const newItem = {
        productId: productId,
        quantity: 1
      };
      localStorage.setItem(LOCAL_STORAGE.CART_ITEMS, JSON.stringify([newItem]));
    }
    
    successNoti("Đã thêm 1 sản phẩm " + productInfo?.productInfo?.name)
  };

  useEffect(() => {

    async function fetchData() {
      const categoryResponse = request(
        "get",
        API_PATH.PRODUCT_CATEGORY,
        (res) => {
          console.log("Response product category request -> ", res);
          setProductCategories(res.data);
        }
      );
      const warehouseResponse = request(
        "get",
        API_PATH.WAREHOUSE_DETAIL,
        (res) => {
          setWarehouseDetails(res.data);
        }
      );
      console.log("Get information of product with id ", productId);
      request(
        "get",
        API_PATH.PRODUCT + "/" + productId,
        (res) => {
          setProductInfo(res.data);
          setCategoryId(res.data.productInfo.categoryId);
          setUom(res.data.productInfo.uom);
          const imageBytes = res.data.productInfo.imageData;
          setImageURL("data:" + res.data.productInfo.imageContentType + ";base64," + imageBytes);
        },
        {
          401: () => { },
          503: () => { errorNoti("Có lỗi khi tải dữ liệu của sản phẩm") }
        }
      )
    }

    fetchData();

  }, []);
  return (
    <Fragment>

      <Box>
        <Grid container justifyContent="space-between" 
          className={classes.headerBox} >
          <Grid>
            <Typography variant="h5">
              {"Xem thông tin sản phẩm"}
            </Typography>
          </Grid>
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={addToCart} >Thêm vào giỏ hàng</Button>
          </Grid>
        </Grid>
      </Box>

      <Box className={classes.formWrap}
          component="form">
        <Box>
          <Grid container spacing={2}>
            <Grid item xs={8}>
              <Box className={classes.boxInfor}>
                <Typography className={classes.inforTitle} variant="h6">
                  Thông tin cơ bản
                </Typography>

                <Grid container spacing={3} className={classes.inforWrap}>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>
                      Tên sản phẩm <RequireStar /></Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      name="name"
                      value={productInfo?.productInfo?.name}
                      InputProps={{
                        readOnly: true,
                      }}
                    ></TextField>
                  </Grid>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>
                      Mã sản phẩm <RequireStar /></Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      name="code"
                      value={productInfo?.productInfo?.code}
                      InputProps={{
                        readOnly: true,
                      }}
                    ></TextField>
                  </Grid>
                </Grid>
                
              </Box>
            </Grid>
            <Grid item xs={4}>
              <Box className={classes.boxInfor}>
                <Typography className={classes.inforTitle} variant="h6">
                  Ảnh sản phẩm</Typography>
                <Box>
                  <img src={imageURL} width={"100%"} height={"100%"} />
                </Box>
              </Box>
            </Grid>
          </Grid>

          <Grid container spacing={2}>
            <Grid item xs={8}>
              <Box className={classes.boxInfor}>
                <Typography className={classes.inforTitle} variant="h6">
                  Thông tin bổ sung
                </Typography>

                <Grid container spacing={3} className={classes.inforWrap}>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>Phân loại</Box>
                    <Select
                      label="categoryId"
                      fullWidth
                      value={categoryId}
                      defaultValue={categoryId}
                      onChange={(e) => setCategoryId(e.target.value)}
                      inputProps={{
                        readOnly: true
                      }}
                    >
                      {
                        productCategories.length > 0 &&
                        productCategories.map(category =>
                          category.categoryId == productInfo?.productInfo?.categoryId ? 
                          <MenuItem selected value={category.categoryId}>
                            {category.name}
                          </MenuItem> : 
                          <MenuItem value={category.categoryId}>
                            {category.name}
                          </MenuItem>
                          )
                      }
                    </Select>
                  </Grid>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>Đơn vị tính</Box>
                    <Select
                      label="uom"
                      value={uom}
                      defaultValue={uom}
                      onChange={(e) => setUom(e.target.value)}
                      fullWidth
                      inputProps={{
                        readOnly: true
                      }}
                    >
                      <MenuItem value={"Cái"} >Cái</MenuItem>
                      <MenuItem value={"Kg"}>Kg</MenuItem>
                      <MenuItem value={"Gói"}>Gói</MenuItem>
                    </Select>
                  </Grid>
                </Grid>

                <Grid container spacing={3} className={classes.inforWrap}>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>Chiều cao (cm)</Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      name="height"
                      type={"number"}
                      value={productInfo?.productInfo?.height}
                      InputProps={{
                        readOnly: true,
                      }}
                    ></TextField>
                  </Grid>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>Diện tích đáy (cm2)</Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      name="area"
                      type={"number"}
                      value={productInfo?.productInfo?.area}
                      InputProps={{
                          readOnly: true,
                        }}
                    ></TextField>
                  </Grid>
                </Grid>

                <Grid container spacing={3} className={classes.inforWrap}>
                  <Grid item xs={6}>
                    <Box className={classes.labelInput}>Khối lượng (kg)</Box>
                    <TextField
                      fullWidth
                      variant="outlined"
                      size="small"
                      name="weight"
                      type={"number"}
                      value={productInfo?.productInfo?.weight}
                      InputProps={{
                          readOnly: true,
                        }}
                    ></TextField>
                  </Grid>
                </Grid>
              </Box>
            </Grid>
            <Grid item xs={4}>
              <Box className={classes.boxInfor}>
                <Typography className={classes.inforTitle} variant="h6">
                  Mô tả thêm</Typography> 
                <TextField
                  name="description"
                  fullWidth
                  variant="outlined"
                  size="small"
                  multiline
                  rows={4}
                  value={productInfo?.productInfo?.description}
                  InputProps={{
                    readOnly: true,
                  }}
                ></TextField>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </Fragment>
  );

}

export default ProductCustomerDetailView;