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
import {Link, useRouteMatch} from "react-router-dom";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { useSelector} from "react-redux";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const AssignOrder = (props) => {
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
    const hubId = useSelector((state) => state.auth.hubId);

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
                `/smdeli/humanresource/collector/hub/${hubId}`,
                (res) => {
                    setCollectors(res.data);
                    if (
                        res.data.statusCode == "COMPLETED" ||
                        res.data.statusCode == "CANCELLED"
                    ) {
                        setIsDoneOrder(true);
                        setIsDoneCollectors(true);
                    }
                }
            );
            await request(
                "get",
                `/smdeli/ordermanager/order/assign/today/${hubId}`,
                (res) => {
                    setAssigmentData(res.data);
                }
            );

            await request(
                "get",
                `/smdeli/ordermanager/order/hub/today/${hubId}`,
                (res) => {
                    setOrders(res.data);
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


    };

    // Auto assign
    const autoAssignButtonHandle = () => {
        setLoading(true);
        const updatedCollectors = collectors.map(collector => ({
            id: collector.id,
            hubId: hubId,
        }));
        request(
            "post",
            "/smdeli/ordermanager/order/assign/collector",
            (res) => {
                setAssigmentData(res.data);
                successNoti("Phân công thành công");
                setLoading(false);

            },
            {
                500: () => {
                    errorNoti("Không tìm được phân phối phù hợp");
                    setLoading(false);
                },
                400: () => {
                    successNoti("Phân công thành công");
                    setLoading(false);
                },
            },
            {
                collectors: updatedCollectors,
                orders: orders,
                hubId: hubId,
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
                <TabContext value={tabValue}>
                    <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                        <TabList
                            onChange={(event, newValue) =>
                                setTabValue(newValue)
                            }
                        >
                            <Tab label="Bảng phân công hôm nay" value="4" />
                            <Tab
                                label="Đơn hàng cần phân công"
                                value="1"
                            />
                            {/*<Tab label="Danh sách nhân viên lấy hàng" value="2" />*/}
                            {/*<Tab label="Đơn hàng đã phân công" value="3" />*/}

                        </TabList>
                    </Box>

                    <TabPanel value="1">
                        <StandardTable
                            rowKey="id"
                            title="Danh sách đơn hàng chưa phân công"
                            columns={[
                                { title: "Mã đơn hàng", field: "id" },
                                { title: "Ngày tạo đơn", field: "createdAt" },
                                { title: "Trạng thái", field: "status" },
                                {
                                    title: "Thao tác",
                                    sorting: false,
                                    cellStyle: {
                                        textAlign: 'center', // Align the content to the left

                                    },
                                    headerStyle: {
                                        textAlign:"center"
                                    },
                                    render: (rowData) => (
                                        <div style={{ display: 'flex', justifyContent: 'center', gap: '5px' }}>
                                            <IconButton
                                                style={{ padding: '5px' }}
                                                // onClick={() => handleEdit(rowData)}
                                                color="success"
                                            >
                                                <VisibilityIcon />
                                            </IconButton>
                                            <IconButton
                                                style={{ padding: '5px' }}
                                                // onClick={() => handleEdit(rowData)}
                                            >
                                                <EditIcon />
                                            </IconButton>
                                            <IconButton
                                                style={{ padding: '5px' }}
                                                // onClick={() => handleDelete(rowData)}
                                                color="error"
                                            >
                                                <DeleteIcon />
                                            </IconButton>
                                        </div>
                                    ),
                                },
                            ]}
                                data={orders}
                            options={{
                                selection: false,
                                pageSize: 10,
                                search: true,
                                sorting: true,
                            }}
                            editable={
                                {
                                    onRowDelete: onRowDelete,
                                }
                            }
                            actions={[

                                {
                                    tooltip: "Phân công tự động",
                                    iconOnClickHandle:
                                    autoAssignButtonHandle,
                                },
                            ]}
                        />
                    </TabPanel>

                    {/* Tab 2: San pham dang phan phoi*/}
                    <TabPanel value="2">
                        <StandardTable
                            rowKey="id"
                            title="Danh sách nhân viên lấy hàng"
                            hideCommandBar={true}
                            columns={[
                                { title: "Mã nhân viên", field: "id" },
                                { title: "Số điện thoại", field: "phone" },
                                { title: "Trạng thái", field: "" },

                            ]}
                            data={processingItems}
                            options={{
                                selection: false,
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

                    <TabPanel value="4">
                    <StandardTable
                        title="Bảng phân công lấy hàng hôm nay"
                        columns={[
                            { title: "Mã nhân viên", field: "collectorId" },
                            { title: "Tên nhân viên", field: "collectorName" },
                            { title: "Số đơn hàng", field: "numOfOrders" },

                            { title: "Đã hoàn thành", field: "numOfCompleted" },
                            {
                                title: "Thao tác",
                                field: "actions", // Field này vẫn cần để tránh lỗi nếu StandardTable sử dụng nó
                                centerHeader: true,
                                sorting: false,
                                renderCell: (rowData) => ( // Sử dụng renderCell thay vì render
                                    <div style={{ display: 'flex', gap: '5px', padding: '0px'}}>
                                        <IconButton
                                            style={{ padding: '5px' }}
                                            onClick={() => {
                                                window.location.href = `${window.location.pathname}/today/${rowData.collectorId}`;
                                            }}
                                            color="success"
                                        >
                                            <VisibilityIcon />
                                        </IconButton>
                                        <IconButton
                                            style={{ padding: '5px' }}
                                            onClick={() => {
                                                window.location.href = `${window.location.pathname}/today/${rowData.collectorId}`;
                                            }}                                        >
                                            <EditIcon />
                                        </IconButton>
                                        <IconButton
                                            style={{ padding: '5px' }}
                                            onClick={() => console.log("Delete", rowData)}
                                            color="error"
                                        >
                                            <DeleteIcon />
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
                    </TabPanel>

                </TabContext>
            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_ASSIGN_ORDER";
export default withScreenSecurity(AssignOrder, SCR_ID, true);
