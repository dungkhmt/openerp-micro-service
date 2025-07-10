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
import useStyles from "screens/styles";
import { convertToVNDFormat } from "screens/utils/utils";
import { errorNoti, processingNoti, successNoti } from "utils/notification";
import withScreenSecurity from "components/common/withScreenSecurity";
import { useHistory } from "react-router";
import {Link, useParams, useRouteMatch} from "react-router-dom";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { useSelector} from "react-redux";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const TodayOrder = (props) => {
    const history = useHistory();
    const { path } = useRouteMatch();
    const employeeId1 = useParams(employeeId1)

    const orderId = props.match?.params?.id;
    const classes = useStyles();

    const [loading, setLoading] = useState(true);
    const [orderInfo, setOrderInfo] = useState({});
    const [remainingItems, setRemainingItems] = useState([]);
    const [processedItems, setProcessedItems] = useState([]);
    const [processingItems, setProcessingItems] = useState([]);
    const [allWarehouses, setAllWarehouses] = useState([]);
    const [tabValue, setTabValue] = useState("1");
    const hubId = useSelector((state) => state.auth.hubId);
    const { employeeId } = useParams();
    console.log("collectorId", employeeId);
    const [collectorId, setCollectorId] = useState(null);
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
    const [collectors, setCollectors] = useState([]);
    const [orders, setOrders] = useState([]);
    const [isDoneCollectors, setIsDoneCollectors] = useState(false);
    const [assigmentData, setAssigmentData] = useState([]);

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
                `/user/get-collector/${email}`,
                (res) => {
                    console.log(res.data.id);
                    setCollectorId(res.data.id);
                }
            );



        }

        fetchData();
    }, []);
    useEffect(() => {
        async function fetchData() {
            await request(
                "get",
                `/smdeli/ordermanager/order/assign/today/collector/${collectorId}`,
                (res) => {
                    setAssigmentData(res.data);
                }
            );
        }
        fetchData()
        setLoading(false);

    },[collectorId])

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


    };

    // Auto assign
    const autoAssignButtonHandle = () => {
        setLoading(true);
        const updatedCollectors = collectors.map(collector => ({
            id: collector.id,
            hubId: hubId,
        }));


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
            {/*<Box>*/}
            {/*    <Grid*/}
            {/*        container*/}
            {/*        justifyContent="space-between"*/}
            {/*        className={classes.headerBox}*/}
            {/*    >*/}
            {/*        <Grid>*/}
            {/*            <Typography variant="h5">Phân công lấy hàng</Typography>*/}
            {/*        </Grid>*/}
            {/*    </Grid>*/}
            {/*</Box>*/}

            <Box className={classes.bodyBox}>


                {/* Xu ly hang hoa */}




                <StandardTable
                    title="Bảng phân công lấy hàng hôm nay"
                    columns={[
                        { title: "Mã đơn hàng", field: "orderId" },
                        { title: "Tên người gửi", field: "senderName" },
                        { title: "Địa chỉ", field: "senderAddress" },
                        { title: "Số điện thoại", field: "senderPhone" },
                        { title: "Thời gian tạo", field: "orderCreatedAt" },

                        { title: "Trạng thái", field: "status" },
                        {
                            title: "Thao tác",
                            field: "actions", // Field này vẫn cần để tránh lỗi nếu StandardTable sử dụng nó
                            centerHeader: true,
                            sorting: false,
                            renderCell: (rowData) => ( // Sử dụng renderCell thay vì render
                                <div >
                                    <IconButton
                                        onClick={() => {
                                            window.location.href = `${window.location.origin}/order/update/${rowData.orderId}`;
                                        }}
                                        color="success"
                                    >
                                        <VisibilityIcon />
                                    </IconButton>
                                </div>
                            ),
                        },

                    ]}
                    data={assigmentData}
                    options={{
                        selection: false,
                        pageSize: 10,
                        search: true,
                        sorting: true,
                    }}


                />

            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_ASSIGN_ORDER";
// export default withScreenSecurity(AssignOrder, SCR_ID, true);
export default TodayOrder;