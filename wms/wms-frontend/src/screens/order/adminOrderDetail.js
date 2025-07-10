// // import LoadingScreen from "components/common/loading/loading";
// // import { Box, Button, Grid, Tab, TextField, Typography } from "@mui/material";
// // import { request } from "api";
// // import { BayDropDown, BayDropDownHavingProduct, ProductDropDown, WarehouseDropDown } from "components/table/DropDown";
// // import StandardTable from "components/StandardTable";
// // import React, { Fragment, useEffect, useState } from "react"
// // import { API_PATH } from "screens/apiPaths";
// // import useStyles from 'screens/styles';
// // import { convertToVNDFormat } from "screens/utils/utils";
// // import { errorNoti, successNoti } from "utils/notification";
// // import withScreenSecurity from "components/common/withScreenSecurity";
// // import { useHistory } from "react-router";
// // import { useRouteMatch } from "react-router-dom";
// // import { TabContext, TabList, TabPanel } from "@mui/lab";

// // const AdminOrderDetail = ( props ) => {
// //   const history = useHistory();
// //   const { path } = useRouteMatch();

// //   const orderId = props.match?.params?.id;
// //   const classes = useStyles();

// //   const [loading, setLoading] = useState(true);
// //   const [orderInfo, setOrderInfo] = useState({});
// //   const [processingItems, setProcessingItems] = useState([]);
// //   const [processedItems, setProcessedItems] = useState([]);
// //   const [remainingItems, setRemainingItems] = useState([]);

// //   const [selectedProductId, setSelectedProductId] = useState(null);
// //   const [selectedProductName, setSelectedProductName] = useState(null);

// //   const [selectedBayId, setSelectedBayId] = useState(null);
// //   const [selectedBayCode, setSelectedBayCode] = useState(null);

// //   const [selectedQuantity, setSelectedQuantity] = useState(null);

// //   const [allWarehouses, setAllWarehouses] = useState([]);
// //   const [warehouseList, setWarehouseList] = useState([]);
// //   const [selectedWarehouseId, setSelectedWarehouseId] = useState(null);
// //   const [selectedWarehouseName, setSelectedWarehouseName] = useState(null);
// //   const [selectedWarehouse, setSelectedWarehouse] = useState(null);
// //   const [selectedWarehouseItems, setSelectedWarehouseItems] = useState([]);
// //   const [maxQuantity, setMaxQuantity] = useState(0);
// //   const [isDoneOrder, setIsDoneOrder] = useState(false);

// //   const [tabValue, setTabValue] = useState('1');

// //   const getProductOrderQuantity = ( productId ) => {
// //     for (var i = 0; i < orderInfo?.remainingItems?.length; i++) {
// //       if (orderInfo?.remainingItems[i]?.productId == productId) {
// //         return orderInfo?.remainingItems[i]?.quantity;
// //       }
// //     }
// //     return Number.MAX_SAFE_INTEGER;
// //   }

// //   useEffect(() => {
// //     async function fetchData () {
// //       await request(
// //         "get",
// //         `${API_PATH.ADMIN_SALE_ORDER}/${orderId}`,
// //         (res) => {
// //           setOrderInfo(res.data);
// //           setRemainingItems(res.data.remainingItems)
// //           if (res.data.statusCode == 'COMPLETED' || res.data.statusCode == 'CANCELLED') {
// //             setIsDoneOrder(true);
// //           }
// //         }
// //       );

// //       await request(
// //         "get",
// //         `${API_PATH.WAREHOUSE_DETAIL_WITH_PRODUCT}/${orderId}`,
// //         (res) => {
// //           setAllWarehouses(res.data);
// //         }
// //       );

// //       setLoading(false);
// //     }

// //     fetchData();
// //   }, []);

// //   useEffect(() => {
// //     if (selectedProductId == null) {
// //       setWarehouseList(allWarehouses);
// //       return;
// //     }

// //     // Tìm tất cả warehouse có selectedProductId trong kho
// //     const newWarehouseList = [];
// //     for (var i = 0; i < allWarehouses.length; i++) {
// //       const warehouseDetail = allWarehouses[i];
// //       var itemCount = 0;
// //       for (var j = 0; j < warehouseDetail.items?.length; j++) {
// //         if (warehouseDetail.items[j]?.productId == selectedProductId) {
// //           itemCount += warehouseDetail.items[j]?.quantity;
// //         }
// //       }
// //       if (itemCount > 0) {
// //         newWarehouseList.push(warehouseDetail);
// //       }
// //       setWarehouseList(newWarehouseList);
// //     }
// //   }, [selectedProductId]);

// //   useEffect(() => {
// //     for (var i = 0; i < warehouseList.length; i++) {
// //       if (warehouseList[i]?.info?.id == selectedWarehouseId) {
// //         setSelectedWarehouse(warehouseList[i]);
// //         setSelectedWarehouseItems(warehouseList[i].items);
// //         return;
// //       }
// //     }
// //   }, [selectedWarehouseId]);

// //   useEffect(() => {
// //     if (selectedProductId != null && selectedBayId != null) {
// //       var totalProductOnBay = 0;
// //       for (var i = 0; i < allWarehouses.length; i++) {
// //         for (var j = 0; j < allWarehouses[i]?.items?.length; j++) {
// //           const item = allWarehouses[i]?.items[j];
// //           if (item?.productId == selectedProductId && item?.bayId == selectedBayId) {
// //             totalProductOnBay += item?.quantity;
// //           }
// //         }
// //       }
// //       setMaxQuantity(Math.min(totalProductOnBay, getProductOrderQuantity(selectedProductId)));
// //     }
// //   }, [selectedBayId]);

// //   const saveProcessingItems = () => {
// //     var isDone = remainingItems.length == 0;
// //     request(
// //       "put",
// //       API_PATH.ASSIGN_ORDER_ITEM,
// //       (res) => {
// //         if (res.status == 200) {
// //           successNoti("Phân phối hàng hóa thành công");
// //           history.push(`${path.substring(0, path.lastIndexOf('/'))}`);
// //         }
// //       },
// //       {
// //         500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
// //       },
// //       {
// //         orderId: orderId,
// //         items: processingItems,
// //         done: isDone
// //       }
// //     )
// //   }

// //   const autoAssignButtonHandle = () => {
// //     setLoading(true);
// //     request(
// //       "put",
// //       `${API_PATH.AUTO_ASSIGN_ORDER_ITEM}/${orderId}`,
// //       (res) => {
// //         const data = res.data;
// //         setProcessingItems(data.processingItems);
// //         setRemainingItems(data.remainingItems);
// //         setLoading(false);
// //         setOrderInfo({...orderInfo, remainingItems: data.remainingItems});

// //         // cập nhật quantity của các items trong warehouse (tạm thời ở phía front end chứ ko đẩy về back end)
// //         console.log("Processing items => ", data.processingItems);
// //         var newAllWarehouses = allWarehouses;
// //         for (var i = 0; i < newAllWarehouses.length; i++) {
// //           var warehouse = newAllWarehouses[i];
// //           for (var j = 0; j < data.processingItems.length; j++) {
// //             var item = data.processingItems[j];
// //             if (item.warehouseId == warehouse.info.id) {
// //               for (var k = 0; k < warehouse.items.length; k++) {
// //                 var warehouseItem = warehouse.items[k];
// //                 if (warehouseItem.bayId == item.bayId && warehouseItem.productId == item.productId) {
// //                   const newQuantity = warehouseItem.quantity - item.quantity;
// //                   warehouseItem.quantity = newQuantity;
// //                   warehouse.items[k] = warehouseItem;
// //                 }
// //               }
// //             }
// //           }
// //           newAllWarehouses[i] = warehouse;
// //         }
// //         setAllWarehouses(newAllWarehouses);
// //         console.log("New all warehouses => ", newAllWarehouses);

// //       },
// //       {
// //         500: () => {
// //           errorNoti("Không tìm được phân phối phù hợp");
// //           setLoading(false);
// //         }
// //       }
// //     );
// //     console.log("new order info => ", orderInfo);
// //     console.log("all warehouse => ", allWarehouses);
// //   }

// //   return (
// //     loading ? <LoadingScreen /> :
// //     <Fragment>
// //     <Box>
// //     <Grid container justifyContent="space-between"
// //         className={classes.headerBox} >
// //         <Grid>
// //           <Typography variant="h5">
// //             Thông tin đơn hàng</Typography>
// //         </Grid>
// //       </Grid>
// //     </Box>

// //     <Box className={classes.bodyBox}>
// //       <Box className={classes.formWrap}>
// //         <Grid container spacing={3}>
// //           <Grid item xs={12}>
// //             <Grid container spacing={3}>
// //               <Grid item xs={6}>
// //                 <Box className={classes.inputWrap}>
// //                   <Box className={classes.labelInput}>
// //                     Ngày tạo đơn hàng
// //                   </Box>
// //                   <TextField
// //                     fullWidth
// //                     variant="outlined"
// //                     size="small"
// //                     value={orderInfo?.createdDate}
// //                     InputProps={{
// //                       readOnly: true,
// //                     }}
// //                   />
// //                 </Box>
// //               </Grid>

// //               <Grid item xs={6}>
// //                 <Box className={classes.inputWrap}>
// //                   <Box className={classes.labelInput}>
// //                     Phương thức thanh toán
// //                   </Box>
// //                   <TextField
// //                     fullWidth
// //                     variant="outlined"
// //                     size="small"
// //                     value={orderInfo?.paymentMethod}
// //                     InputProps={{
// //                       readOnly: true,
// //                     }}
// //                   />
// //                 </Box>
// //               </Grid>
// //             </Grid>
// //           </Grid>

// //           <Grid item xs={12}>
// //             <Grid container spacing={3}>
// //               <Grid item xs={6}>
// //                 <Box className={classes.inputWrap}>
// //                   <Box className={classes.labelInput}>
// //                     Tổng giá trị đơn hàng
// //                   </Box>
// //                   <TextField
// //                     fullWidth
// //                     variant="outlined"
// //                     size="small"
// //                     value={convertToVNDFormat(orderInfo?.totalOrderCost)}
// //                     InputProps={{
// //                       readOnly: true,
// //                     }}
// //                   />
// //                 </Box>
// //               </Grid>
// //               <Grid item xs={6}>
// //                 <Box className={classes.inputWrap}>
// //                   <Box className={classes.labelInput}>
// //                     Trạng thái
// //                   </Box>
// //                   <TextField
// //                     fullWidth
// //                     variant="outlined"
// //                     size="small"
// //                     value={orderInfo?.status}
// //                     InputProps={{
// //                       readOnly: true,
// //                     }}
// //                   />
// //                 </Box>
// //               </Grid>
// //             </Grid>
// //           </Grid>

// //           <Grid item xs={12}>
// //             <Grid container spacing={3}>
// //               <Grid item xs={6}>
// //                 <Box className={classes.inputWrap}>
// //                   <Box className={classes.labelInput}>
// //                     Tài khoản mua hàng
// //                   </Box>
// //                   <TextField
// //                     fullWidth
// //                     variant="outlined"
// //                     size="small"
// //                     value={orderInfo?.userLoginId}
// //                     InputProps={{
// //                       readOnly: true,
// //                     }}
// //                   />
// //                 </Box>
// //               </Grid>

// //               <Grid item xs={6}>
// //               <Box className={classes.inputWrap}>
// //                   <Box className={classes.labelInput}>
// //                     Tên người nhận hàng
// //                   </Box>
// //                   <TextField
// //                     fullWidth
// //                     variant="outlined"
// //                     size="small"
// //                     value={orderInfo?.customerName}
// //                     InputProps={{
// //                       readOnly: true,
// //                     }}
// //                   />
// //                 </Box>
// //               </Grid>
// //             </Grid>
// //           </Grid>

// //           <Grid item xs={12}>
// //             <Box className={classes.inputWrap}>
// //               <Box className={classes.labelInput}>
// //                 Địa chỉ nhận hàng
// //               </Box>
// //               <TextField
// //                 fullWidth
// //                 variant="outlined"
// //                 size="small"
// //                 value={orderInfo.receiptAddress}
// //                 InputProps={{
// //                   readOnly: true,
// //                 }}
// //               />
// //             </Box>
// //           </Grid>

// //         </Grid>
// //       </Box>

// //       <TabContext value={tabValue}>
// //         <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
// //           <TabList onChange={(event, newValue) => setTabValue(newValue)} >
// //             <Tab label="Sản phẩm cần phân phối giao hàng" value="1" />
// //             <Tab label="Sản phẩm đang phân phối" value="2" />
// //             <Tab label="Sản phẩm đã phân phối" value="3" />
// //           </TabList>
// //         </Box>
// //         <TabPanel value="1">
// //           <StandardTable
// //             title="Danh sách sản phẩm cần phân phối giao hàng"
// //             hideCommandBar={true}
// //             columns={[
// //               { title: "Tên sản phẩm", field: "productName" },
// //               { title: "Số lượng", field: "quantity" }
// //             ]}
// //             data={remainingItems}
// //             options={{
// //               selection: false,
// //               pageSize: 5,
// //               search: true,
// //               sorting: true,
// //             }}
// //           />
// //         </TabPanel>
// //         <TabPanel value="2">
// //           <StandardTable
// //             rowKey="productId"
// //             title="Danh sách sản phẩm đang phân phối"
// //             hideCommandBar={true}
// //             columns={[
// //               { title: "Tên sản phẩm", field: "productName",
// //                 editComponent: <ProductDropDown
// //                   productList={orderInfo?.remainingItems}
// //                   setSelectedProductId={setSelectedProductId}
// //                   setSelectedProductName={setSelectedProductName} /> },
// //               { title: "Kho", field: "warehouseName",
// //                 editComponent: <WarehouseDropDown
// //                   warehouseList={warehouseList.map(warehouse => warehouse.info)}
// //                   setSelectedWarehouseId={setSelectedWarehouseId}
// //                   setSelectedWarehouseName={setSelectedWarehouseName} />},
// //               { title: "Vị trí kệ hàng", field: "bayCode",
// //                 editComponent: <BayDropDownHavingProduct
// //                   selectedWarehouseItems={selectedWarehouseItems}
// //                   productId={selectedProductId}
// //                   setSelectedBayId={setSelectedBayId}
// //                   setSelectedBayCode={setSelectedBayCode} /> },
// //               { title: "Số lượng", field: "quantity",
// //                 editComponent: <TextField
// //                   type="number"
// //                   InputProps={{
// //                     inputProps: {
// //                         max: maxQuantity, min: 1
// //                     }
// //                   }}
// //                   value={selectedQuantity}
// //                   onChange={(e) => setSelectedQuantity(e.target.value)}
// //                   /> },
// //             ]}
// //             data={processingItems}
// //             options={{
// //               selection: false,
// //               pageSize: 5,
// //               search: true,
// //               sorting: true,
// //             }}
// //             editable={!isDoneOrder && {
// //               onRowAdd: newData => new Promise((resolve, reject) => {
// //                 setTimeout(() => {
// //                   if (selectedWarehouseId == null || selectedBayId == null || selectedQuantity < 1) {
// //                     errorNoti("Giá trị thêm mới không hợp lệ");
// //                     return ;
// //                   }
// //                   const adder = {
// //                     productId: selectedProductId,
// //                     productName: selectedProductName,
// //                     warehouseId: selectedWarehouseId,
// //                     warehouseName: selectedWarehouseName,
// //                     bayId: selectedBayId,
// //                     bayCode: selectedBayCode,
// //                     quantity: selectedQuantity
// //                   };
// //                   setProcessingItems([...processingItems, adder]);
// //                   // update số lượng sản phẩm cần phân phối
// //                   var newOrderInfo = {...orderInfo};
// //                   for (var i = 0; i < newOrderInfo?.remainingItems?.length; i++) {
// //                     if (newOrderInfo?.remainingItems[i]?.productId == selectedProductId) {
// //                       const newQuantity = newOrderInfo?.remainingItems[i]?.quantity - selectedQuantity;
// //                       newOrderInfo.remainingItems[i].quantity = newQuantity;
// //                       if (newQuantity == 0) {
// //                         newOrderInfo.remainingItems.splice(i, 1);
// //                       }
// //                       break;
// //                     }
// //                   }
// //                   console.log("New order info => ", newOrderInfo);

// //                   // update lại số lượng hàng tại kho, do đã được gọi bằng API nên được lưu tại front end, biến allWarehouses
// //                   console.log("All warehouse => ", allWarehouses);
// //                   var newAllWarehouses = allWarehouses;
// //                   for (var i = 0; i < newAllWarehouses.length; i++) {
// //                     var warehouse = newAllWarehouses[i];
// //                     if (warehouse?.info?.id == selectedWarehouseId) {
// //                       for (var j = 0; j < warehouse.items.length; j++) {
// //                         var item = warehouse.items[j];
// //                         if (item.bayId == selectedBayId && item.productId == selectedProductId) {
// //                           var newQuantity = item.quantity - selectedQuantity;
// //                           item.quantity = newQuantity;
// //                           warehouse.items[j] = item;
// //                           newAllWarehouses[i] = warehouse;
// //                         }
// //                       }
// //                     }
// //                   }
// //                   console.log("New all warehouses => ", newAllWarehouses);
// //                   setAllWarehouses(newAllWarehouses);

// //                   setOrderInfo(newOrderInfo);
// //                   setRemainingItems(newOrderInfo.remainingItems);
// //                   setSelectedQuantity(1);

// //                   // update lại giá trị đã chọn cho adder
// //                   setSelectedProductId(null);
// //                   setSelectedProductName(null);
// //                   setSelectedBayId(null);
// //                   setSelectedBayCode(null);
// //                   setSelectedWarehouseId(null);
// //                   setSelectedWarehouseName(null);

// //                   resolve();
// //                 })
// //               }),
// //               onRowDelete: oldData => new Promise((resolve, reject) => {
// //                 setTimeout(() => {
// //                   console.log("Old data => ", oldData);
// //                   // TODO: on row delete implementation....
// //                 })
// //               })
// //             }}
// //             actions={!isDoneOrder && [
// //               {
// //                 tooltip: "Lưu",
// //                 iconOnClickHandle: saveProcessingItems
// //               },
// //               {
// //                 tooltip: "Phân phối tự động",
// //                 iconOnClickHandle: autoAssignButtonHandle
// //               }
// //             ]}
// //           />
// //         </TabPanel>
// //         <TabPanel value="3">
// //           <StandardTable
// //             title="Danh sách sản phẩm đã phân phối"
// //             hideCommandBar={true}
// //             columns={[
// //               { title: "Tên sản phẩm", field: "productName" },
// //               { title: "Số lượng", field: "quantity" },
// //               { title: "Kho", field: "warehouseName" },
// //               { title: "Vị trí kệ hàng", field: "bayCode" },
// //               { title: "Trạng thái", field: "status"},
// //               { title: "Số lô", field: "lotId" },
// //               { title: "Ngày phân phối", field: "createdDate"}
// //             ]}
// //             data={orderInfo?.processedItems}
// //             options={{
// //               selection: false,
// //               pageSize: 5,
// //               search: true,
// //               sorting: true,
// //             }}
// //           />
// //         </TabPanel>
// //       </TabContext>
// //     </Box>
// //   </Fragment>)
// // };

// // const SCR_ID = "SCR_WMSv2_ORDER_DETAIl";
// // export default withScreenSecurity(AdminOrderDetail, SCR_ID, true);

// By Diep
import LoadingScreen from "components/common/loading/loading";
import { Box, Button, Grid, Tab, TextField, Typography } from "@mui/material";
import { request } from "api";
import {
    ProductDropDown,
    WarehouseDropDown,
    BayDropDownWithSelectedPrdAndWh,
} from "components/table/DropDown";
import StandardTable from "components/StandardTable";
import React, { Fragment, useEffect, useState } from "react";
import { API_PATH, API_PATH_2 } from "screens/apiPaths";
import useStyles from "screens/styles";
import { convertToVNDFormat } from "screens/utils/utils";
import { errorNoti, processingNoti, successNoti } from "utils/notification";
import withScreenSecurity from "components/common/withScreenSecurity";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";
import { TabContext, TabList, TabPanel } from "@mui/lab";

const AdminOrderDetail = (props) => {
    const history = useHistory();
    const { path } = useRouteMatch();

    const orderId = props.match?.params?.id;
    const classes = useStyles();

    const [loading, setLoading] = useState(true);
    const [orderInfo, setOrderInfo] = useState({});
    const [remainingItems, setRemainingItems] = useState([]);
    const [processedItems, setProcessedItems] = useState([]);
    const [processingItems, setProcessingItems] = useState([]);
    const [allWarehouses, setAllWarehouses] = useState([]);
    const [tabValue, setTabValue] = useState("1");

    const [selectedProductId, setSelectedProductId] = useState(null);
    const [selectedProductName, setSelectedProductName] = useState(null);

    const [bayList, setBayList] = useState([]);
    const [selectedBayId, setSelectedBayId] = useState(null);
    const [selectedBayCode, setSelectedBayCode] = useState(null);

    const [selectedQuantity, setSelectedQuantity] = useState(null);

    const [warehouseList, setWarehouseList] = useState([]);
    const [selectedWarehouseId, setSelectedWarehouseId] = useState(null);
    const [selectedWarehouseName, setSelectedWarehouseName] = useState(null);

    const [selectedWarehouse, setSelectedWarehouse] = useState(null);
    const [selectedWarehouseItems, setSelectedWarehouseItems] = useState([]);
    const [maxQuantity, setMaxQuantity] = useState(0);
    const [isDoneOrder, setIsDoneOrder] = useState(false);

    const findWarehousesForProducts = (products, productIdList) => {
        const result = {};

        productIdList.forEach((productId) => {
            result[productId] = [];

            products.forEach((warehouse) => {
                const matchingBays = warehouse.bayWithProducts.filter(
                    (bay) => bay.productId === productId
                );
                if (matchingBays.length > 0) {
                    result[productId].push({
                        id: warehouse.id,
                        name: warehouse.name,
                        bays: matchingBays.map((bay) => ({
                            id: bay.bayId,
                            code: bay.code,
                            quantityOnHand: bay.quantityOnHandTotal,
                        })),
                    });
                }
            });
        });

        return result;
    };

    // fetch Data
    useEffect(() => {
        async function fetchData() {
            var productIds;
            await request(
                "get",
                `${API_PATH_2.ADMIN_WH_MANAGER_SALE_ORDER}/${orderId}`,
                (res) => {
                    setOrderInfo(res.data);
                    setRemainingItems(res.data.remainingItems);
                    setProcessedItems(res.data.processedItems);
                    productIds = res.data.items.map((item) => item.productId);

                    if (
                        res.data.statusCode == "COMPLETED" ||
                        res.data.statusCode == "CANCELLED"
                    ) {
                        setIsDoneOrder(true);
                    }
                }
            );

            await request(
                "put",
                `${API_PATH_2.WAREHOUSE_DETAIL_WITH_PRODUCT_2}`,
                (res) => {
                    setAllWarehouses(
                        findWarehousesForProducts(res.data, productIds)
                    );
                },
                {},
                productIds
            );

            setLoading(false);
        }

        fetchData();
    }, []);

    const getProductOrderQuantity = (productId) => {
        for (var i = 0; i < remainingItems?.length; i++) {
            if (remainingItems[i]?.productId == productId) {
                return remainingItems[i]?.quantity;
            }
        }
        return Number.MAX_SAFE_INTEGER;
    };

    useEffect(() => {
        setWarehouseList(allWarehouses[selectedProductId]);
    }, [selectedProductId]);

    useEffect(() => {
        setBayList(
            allWarehouses[selectedProductId]?.find(
                (wh) => wh.id === selectedWarehouseId
            ).bays
        );
    }, [selectedWarehouseId]);

    useEffect(() => {
        let totalProductOnBay = bayList?.find(
            (bay) => bay.id === selectedBayId
        )?.quantityOnHand;
        setMaxQuantity(
            Math.min(
                totalProductOnBay,
                getProductOrderQuantity(selectedProductId)
            )
        );
    }, [selectedBayId]);

    // Save processing item
    const saveProcessingItems = () => {
        var isDone = remainingItems.length == 0 || remainingItems.every(item => item.quantity == 0);

        request(
            "put",
            API_PATH_2.ASSIGN_ORDER_ITEM,
            (res) => {
                if (res.status == 200) {
                    successNoti("Phân phối hàng hóa thành công");
                    let newOrderInfo = orderInfo;
                    if (isDone) {
                        newOrderInfo.status = 'Đã phân phối';
                    }
                    newOrderInfo.remainingItems = remainingItems;
                    newOrderInfo.processedItems = [...processedItems, ...res.data.assignedOrderItemList];
                    setOrderInfo(newOrderInfo);
                    setProcessingItems([])
                    setProcessedItems(newOrderInfo.processedItems);
                    setRemainingItems(newOrderInfo.remainingItems);

                }
            },
            {
                500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau"),
            },
            {
                orderId: orderId,
                items: processingItems,
                done: isDone,
            }
        );
    };

    // Auto assign
    const autoAssignButtonHandle = () => {
        setLoading(true);
        request(
            "put",
            `${API_PATH_2.AUTO_ASSIGN_ORDER_ITEM}/${orderId}`,
            (res) => {
                const data = res.data;
                setProcessingItems(data.processingItems);
                setRemainingItems(data.remainingItems);
                setLoading(false);
                setOrderInfo({
                    ...orderInfo,
                    remainingItems: data.remainingItems,
                });

                // cập nhật quantity của các items trong warehouse (tạm thời ở phía front end chứ ko đẩy về back end)
                console.log("Processing items => ", data.processingItems);
                var newAllWarehouses = allWarehouses;
                for (var i = 0; i < newAllWarehouses.length; i++) {
                    var warehouse = newAllWarehouses[i];
                    for (var j = 0; j < data.processingItems.length; j++) {
                        var item = data.processingItems[j];
                        if (item.warehouseId == warehouse.info.id) {
                            for (var k = 0; k < warehouse.items.length; k++) {
                                var warehouseItem = warehouse.items[k];
                                if (
                                    warehouseItem.bayId == item.bayId &&
                                    warehouseItem.productId == item.productId
                                ) {
                                    const newQuantity =
                                        warehouseItem.quantity - item.quantity;
                                    warehouseItem.quantity = newQuantity;
                                    warehouse.items[k] = warehouseItem;
                                }
                            }
                        }
                    }
                    newAllWarehouses[i] = warehouse;
                }
                setAllWarehouses(newAllWarehouses);
                console.log("New all warehouses => ", newAllWarehouses);
            },
            {
                500: () => {
                    errorNoti("Không tìm được phân phối phù hợp");
                    setLoading(false);
                },
            }
        );
        console.log("new order info => ", orderInfo);
        console.log("all warehouse => ", allWarehouses);
    };

    const columns = [
        {
            title: "Tên sản phẩm",
            field: "productName",
            editComponent: (
                <ProductDropDown
                    productList={remainingItems.filter(
                        (item) => item.quantity > 0
                    )}
                    setSelectedProductId={setSelectedProductId}
                    setSelectedProductName={setSelectedProductName}
                />
            ),
        },
        {
            title: "Kho",
            field: "warehouseName",
            editComponent: (
                <WarehouseDropDown
                    warehouseList={warehouseList}
                    setSelectedWarehouseId={setSelectedWarehouseId}
                    setSelectedWarehouseName={setSelectedWarehouseName}
                />
            ),
        },
        {
            title: "Vị trí kệ hàng",
            field: "bayCode",
            editComponent: (
                <BayDropDownWithSelectedPrdAndWh
                    bayList={bayList}
                    setSelectedBayId={setSelectedBayId}
                    setSelectedBayCode={setSelectedBayCode}
                />
            ),
        },
        {
            title: "Số lượng",
            field: "quantity",
            editComponent: (
                <TextField
                    type="number"
                    InputProps={{
                        inputProps: {
                            max: maxQuantity,
                            min: 1,
                        },
                    }}
                    value={selectedQuantity}
                    onChange={(e) => setSelectedQuantity(e.target.value)}
                />
            ),
        },
    ];

    const onRowAdd = (newData) => {
        new Promise((resolve, reject) => {
            setTimeout(() => {
                if (
                    selectedProductId == null ||
                    selectedWarehouseId == null ||
                    selectedBayId == null ||
                    selectedQuantity < 1 ||
                    selectedQuantity > maxQuantity
                ) {
                    errorNoti("Giá trị thêm mới không hợp lệ");
                    return;
                }
                const adder = {
                    productId: selectedProductId,
                    productName: selectedProductName,
                    warehouseId: selectedWarehouseId,
                    warehouseName: selectedWarehouseName,
                    bayId: selectedBayId,
                    bayCode: selectedBayCode,
                    quantity: selectedQuantity,
                };
                setProcessingItems([...processingItems, adder]);

                // update số lượng sản phẩm cần phân phối
                setRemainingItems((prevItems) => {
                    return prevItems.map((item) => {
                        if (item.productId === selectedProductId) {
                            const newQuantity =
                                item.quantity - selectedQuantity;
                            return { ...item, quantity: newQuantity };
                        }
                        return item;
                    });
                });

                // update lại số lượng hàng tại kho ở FE
                let newAllWarehouses = { ...allWarehouses };

                const warehouseList = newAllWarehouses[selectedProductId];
                if (warehouseList) {
                    for (let i = 0; i < warehouseList.length; i++) {
                        if (warehouseList[i].id === selectedWarehouseId) {
                            for (
                                let j = 0;
                                j < warehouseList[i].bays.length;
                                j++
                            ) {
                                if (
                                    warehouseList[i].bays[j].id ===
                                    selectedBayId
                                ) {
                                    const newBays = [...warehouseList[i].bays];
                                    newBays[j].quantityOnHand -=
                                        selectedQuantity;

                                    const updatedWarehouse = {
                                        ...warehouseList[i],
                                        bays: newBays,
                                    };
                                    newAllWarehouses[selectedProductId][i] =
                                        updatedWarehouse;
                                }
                            }
                        }
                    }
                }
                setAllWarehouses(newAllWarehouses);

                // update lại giá trị đã chọn cho adder
                setSelectedProductId(null);
                setSelectedProductName(null);
                setSelectedBayId(null);
                setSelectedBayCode(null);
                setSelectedWarehouseId(null);
                setSelectedWarehouseName(null);
                setSelectedQuantity(null);

                resolve();
            });
        });
    };

    const onRowDelete = (oldData) => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                for (var i = 0; i < oldData.length; i++) {
                    console.log("onRowDelete, data: ", oldData[i], 'processingItem: ', processingItems);
                    // Xóa bản ghi khỏi dữ liệu UI
                    let isDeleted = false;
                    const newProcessingItems = processingItems.filter(
                        (item) => {
                            if (
                                !isDeleted &&
                                item.productId === oldData[i].productId &&
                                item.warehouseId === oldData[i].warehouseId &&
                                item.bayId === oldData[i].bayId &&
                                item.quantity === oldData[i].quantity
                            ) {
                                isDeleted = true;
                                return false;
                            }
                            return true;
                        }
                    );
                    setProcessingItems(newProcessingItems);

                    // Cập nhật lại số lượng sản phẩm cần phân phối
                    const newRemainingItems = remainingItems.map((item) => {
                        if (item.productId === oldData[i].productId) {
                            const newQuantity =
                                parseInt(item.quantity, 10) + parseInt(oldData[i].quantity, 10);
                            return { ...item, quantity: newQuantity };
                        }
                        
                        return item;
                    });
                    setRemainingItems(newRemainingItems);

                    // Cập nhật lại số lượng hàng tại kho ở FE
                    const newAllWarehouses = { ...allWarehouses };
                    
                
                    const warehouseList =
                        newAllWarehouses[oldData[i].productId];
                        console.log('Start Cập nhật lại số lượng hàng tại kho ở FE: ', warehouseList[i].id, oldData[i].warehouseId);
                    if (warehouseList) {
                        for (let i = 0; i < warehouseList.length; i++) {
                            if ( warehouseList[i]?.id == oldData[i]?.warehouseId ) {
                                for (
                                    let j = 0;
                                    j < warehouseList[i].bays.length;
                                    j++
                                ) {
                                    if (
                                        warehouseList[i].bays[j].id ==
                                        oldData[i].bayId
                                    ) {
                                        const newBays = [
                                            ...warehouseList[i].bays,
                                        ];
                                        newBays[j].quantityOnHand +=
                                            parseInt(oldData[i].quantity, 10);

                                        const updatedWarehouse = {
                                            ...warehouseList[i],
                                            bays: newBays,
                                        };
                                        newAllWarehouses[oldData[i].productId][
                                            i
                                        ] = updatedWarehouse;
                                    }
                                }
                            }
                        }
                    }
                    console.log('Done Cập nhật lại số lượng hàng tại kho ở FE: ', newAllWarehouses);
                    setAllWarehouses(newAllWarehouses);
                }

                resolve();
            });
        });
    };

    console.log("matching option: ", allWarehouses);
    return loading ? (
        <LoadingScreen />
    ) : (
        <Fragment>
            <Box>
                <Grid
                    container
                    justifyContent="space-between"
                    className={classes.headerBox}
                >
                    <Grid>
                        <Typography variant="h5">Thông tin đơn hàng</Typography>
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
                                            value={convertToVNDFormat(
                                                orderInfo?.totalOrderCost
                                            )}
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

                {/* Xu ly hang hoa */}
                <TabContext value={tabValue}>
                    <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                        <TabList
                            onChange={(event, newValue) =>
                                setTabValue(newValue)
                            }
                        >
                            <Tab
                                label="Sản phẩm cần phân phối giao hàng"
                                value="1"
                            />
                            <Tab label="Sản phẩm đang phân phối" value="2" />
                            <Tab label="Sản phẩm đã phân phối" value="3" />
                        </TabList>
                    </Box>

                    {/* Tab 1: San pham chua phan phoi chinh thuc*/}
                    <TabPanel value="1">
                        <StandardTable
                            title="Danh sách sản phẩm cần phân phối giao hàng"
                            hideCommandBar={true}
                            columns={[
                                { title: "Tên sản phẩm", field: "productName" },
                                { title: "Số lượng", field: "quantity" },
                            ]}
                            data={remainingItems.filter(
                                (item) => item.quantity > 0
                            )}
                            options={{
                                selection: false,
                                pageSize: 5,
                                search: true,
                                sorting: true,
                            }}
                        />
                    </TabPanel>

                    {/* Tab 2: San pham dang phan phoi*/}
                    <TabPanel value="2">
                        <StandardTable
                            rowKey="productId"
                            title="Danh sách sản phẩm đang phân phối"
                            hideCommandBar={true}
                            columns={columns}
                            data={processingItems}
                            options={{
                                selection: true,
                                pageSize: 5,
                                search: true,
                                sorting: true,
                            }}
                            editable={
                                !isDoneOrder && {
                                    onRowAdd: onRowAdd,
                                    onRowDelete: onRowDelete,
                                }
                            }
                            actions={
                                !isDoneOrder && [
                                    {
                                        tooltip: "Lưu",
                                        iconOnClickHandle: saveProcessingItems,
                                    },
                                    {
                                        tooltip: "Phân phối tự động",
                                        iconOnClickHandle:
                                            autoAssignButtonHandle,
                                    },
                                ]
                            }
                        />
                    </TabPanel>

                    {/* Tab 3: San pham da phan phoi */}
                    <TabPanel value="3">
                        <StandardTable
                            title="Danh sách sản phẩm đã phân phối"
                            hideCommandBar={true}
                            columns={[
                                { title: "Tên sản phẩm", field: "productName" },
                                { title: "Số lượng", field: "quantity" },
                                { title: "Kho", field: "warehouseName" },
                                { title: "Vị trí kệ hàng", field: "bayCode" },
                                { title: "Trạng thái", field: "status" },
                                { title: "Số lô", field: "lotId" },
                                {
                                    title: "Ngày phân phối",
                                    field: "createdDate",
                                },
                            ]}
                            data={processedItems}
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
        </Fragment>
    );
};

const SCR_ID = "SCR_WMSv2_ORDER_DETAIl";
export default withScreenSecurity(AdminOrderDetail, SCR_ID, true);
