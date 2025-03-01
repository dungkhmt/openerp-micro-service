import LoadingScreen from "components/common/loading/loading";
import {Box, Button, Grid, Modal, Tab, Typography} from "@mui/material";
import {request} from "api";
import StandardTable from "components/StandardTable";
import React, {Fragment, useEffect, useState} from "react";
import useStyles from "screens/styles";
import {errorNoti, processingNoti, successNoti} from "utils/notification";
import {useHistory} from "react-router";
import {useParams, useRouteMatch} from "react-router-dom";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import {useSelector} from "react-redux";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import Maps from "../../components/map/map";
import SearchBox from "../../components/map/searchBox";
import {EnhancedMap} from "../../components/map/EnhancedMap";
import {API_PATH} from "../apiPaths";

const TodayOrder = (props) => {
    const history = useHistory();
    const {path} = useRouteMatch();
    const email = useSelector((state) => state.auth.email);
    const orderId = props.match?.params?.id;
    const classes = useStyles();
    const [selectPosition, setSelectPosition] = useState(null);
    const [loading, setLoading] = useState(true);
    const [openModal, setOpenModal] = useState(false);
    const [allWarehouses, setAllWarehouses] = useState([]);
    const [hubId, setHubId] = useState();
    const [hub, setHub] = useState();
    const {employeeId} = useParams();
    const [nextOrder, setNextOrder] = useState(null);
    const [collectorId, setCollectorId] = useState(null);
    const [assigmentData, setAssigmentData] = useState([]);
    const [tabValue, setTabValue] = useState('1');
    useEffect(() => {
        async function fetchId() {
            var productIds;
            await request(
                "get",
                `/user/get-collector/${email}`
        ,
            (res) => {
                console.log(res.data.id);
                setCollectorId(res.data.id);
                setHubId(res.data.hubId);
                console.log("hubId", res.data.hubId);
            }
        )


        }

        fetchId();
    }, []);
    useEffect(() => {
        const fetchHubLocation = async () => {
            await request(
                "get",
                `${API_PATH.HUB}/${hubId}`,
                (res) => {
                    setHub(res.data);

                },
                {
                    401: () => { },
                    503: () => { errorNoti("Có lỗi khi tải dữ liệu của kho") }
                }
            );}

        fetchHubLocation();
    }, [hubId]);

    const handleGoToDetails = () => {
        const nextOrderFromStorage = sessionStorage.getItem("nextOrder");
        if (nextOrderFromStorage) {
            const nextOrder = JSON.parse(nextOrderFromStorage); // Parse lại thành object
            history.push({
                pathname: `/order/collector/${nextOrder.orderId}`,
                state: { assignmentId: nextOrder.id }
            });
        } else {
            errorNoti("Không tìm thấy đơn hàng tiếp theo");
        }
    };
    const handleNextOrder = (order) => {
        setNextOrder(order);
    };
    const NextOrderInfo = ({ }) => {
        const nextOrder = JSON.parse(sessionStorage.getItem('nextOrder'))

        return (
            <Box sx={{paddingTop: 1.25, paddingLeft:2, paddingBottom: 1.25, paddingRight: 2, backgroundColor: '#f5f5f5', borderRadius: 2, marginBottom: 0.8}}>
                {<Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    <Box>
                        {nextOrder != null ? <Typography>Tên người gửi: {nextOrder?.senderName}</Typography> : "Không có đơn hàng!"}
                        {nextOrder != null && <Typography>Địa chỉ: {nextOrder?.senderAddress}</Typography>}
                    </Box>
                    {nextOrder != null &&<Box sx={{display: 'flex', alignItems: 'center', gap: 2}}>
                        <Box sx={{ display: 'flex', flexDirection: 'column' }}> {/* Thêm Box này để căn dọc */}
                            <Typography>Số điện thoại: {nextOrder?.senderPhone}</Typography>
                            <Typography>Số lượng package: {nextOrder?.numOfItem}</Typography>
                        </Box>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleGoToDetails}
                        >
                            Thao tác
                        </Button>
                    </Box>}
                </Box>}
            </Box>
        );
    };

    useEffect(() => {
        const nextOrderFromStorage = sessionStorage.getItem('nextOrder');
        if (nextOrderFromStorage) {
            setNextOrder(JSON.parse(nextOrderFromStorage)); // Parse lại thành object
            console.log(nextOrderFromStorage)
        }
    }, []);
    useEffect(() => {
        async function fetchData() {
            await request(
                "get",
                `/smdeli/ordermanager/order/assign/today/collector/${collectorId}`,
                (res) => {
                    // Sắp xếp dữ liệu theo sequenceNumber
                    if (res.data) {
                        const sortedData = res.data.sort((a, b) => a.sequenceNumber - b.sequenceNumber);

                        setAssigmentData(sortedData);
                        sessionStorage.setItem('assignments', JSON.stringify(sortedData)); // Lưu dữ liệu dưới dạng chuỗi JSON

                        if (sortedData.length > 0 && !sessionStorage.getItem('savedNextOrder') ) {
                            sessionStorage.setItem('nextOrder', JSON.stringify(sortedData[0])); // Lưu dữ liệu dưới dạng chuỗi JSON
                            sessionStorage.setItem('savedNextOrder',"1")
                            setNextOrder(sortedData[0])
                        }
                    }
                }
            );
        }


            const assignmentsFromStorage = sessionStorage.getItem("assignments");

            const parsedAssignments = JSON.parse(assignmentsFromStorage);
            const sortedAssignments = parsedAssignments?.sort((a, b) => a.sequenceNumber - b.sequenceNumber);

            setAssigmentData(sortedAssignments);
            if (sortedAssignments?.length > 0 && !sessionStorage.getItem('nextOrder')) {
                sessionStorage.setItem('nextOrder', JSON.stringify(sortedAssignments[0])); // Lưu dữ liệu dưới dạng chuỗi JSON
            }
            fetchData();
        setLoading(false);
    }, [collectorId]);


    const routingPoints = assigmentData?.map(order => ({
        lat: order.senderLatitude,
        lng: order.senderLongitude
    })).filter(point => point.lat && point.lng);




    console.log("route",routingPoints)
    const handleShowRoute = () => {
        setOpenModal(true);
    };


    console.log("matching option: ", allWarehouses);
    return loading ? (
        <LoadingScreen/>
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


                {/* Xu ly hang hoa */}
                <TabContext value={tabValue}>
                    <Box sx={{borderBottom: 0, borderColor: "divider"}}>
                        <TabList
                            onChange={(event, newValue) =>
                                setTabValue(newValue)
                            }
                        >
                            <Tab label="Danh sách đơn hàng" value="1"/>
                            <Tab label="Bản đồ" value="2"/>
                            <Tab label="Tiến trình" value="3"/>

                        </TabList>
                    </Box>

                    <TabPanel value="1">

                        <StandardTable
                            title="Bảng phân công lấy hàng hôm nay"
                            columns={[
                                {title: "STT", field: "sequenceNumber"},
                                {title: "Mã đơn hàng", field: "orderId"},
                                {title: "Tên người gửi", field: "senderName"},
                                {title: "Địa chỉ", field: "senderAddress"},
                                {title: "Số điện thoại", field: "senderPhone"},
                                {title: "Thời gian tạo", field: "orderCreatedAt"},

                                {title: "Trạng thái", field: "assignmentStatus"},
                                {
                                    title: "Thao tác",
                                    field: "actions", // Field này vẫn cần để tránh lỗi nếu StandardTable sử dụng nó
                                    centerHeader: true,
                                    sorting: false,
                                    renderCell: (rowData) => ( // Sử dụng renderCell thay vì render
                                        <div>
                                            <IconButton
                                                onClick={() => {
                                                    history.push({
                                                        pathname: `/order/collector/${rowData.orderId}`,
                                                        state: { assignmentId: rowData.id }
                                                    });
                                                }}
                                                color="success"
                                            >
                                                <VisibilityIcon/>
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
                            defaultOrderBy="sequenceNumber" // Thiết lập orderBy mặc định



                        />
                        <Modal
                            open={openModal}
                            onClose={() => setOpenModal(false)}
                            aria-labelledby="modal-modal-title"
                            aria-describedby="modal-modal-description"
                        >
                            <Box sx={{
                                position: 'absolute',
                                top: '50%',
                                left: '50%',
                                width: '75%',
                                height: '90%',
                                transform: 'translate(-50%, -50%)',
                                bgcolor: 'background.paper',
                                border: '2px solid #000',
                                boxShadow: 24,
                                p: 4,
                            }}>
                                <Typography variant="h6" id="modal-modal-title">
                                </Typography>
                                <Maps
                                    selectPosition={selectPosition}
                                    setSelectPosition={setSelectPosition}
                                />
                                <div style={{width: "50%", height: "90%"}}>
                                    <SearchBox selectPosition={selectPosition}
                                               setSelectPosition={setSelectPosition}/>
                                </div>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={() => setOpenModal(false)}
                                    style={{marginTop: 16}}
                                >
                                    Đóng
                                </Button>
                            </Box>
                        </Modal>
                    </TabPanel>
                    <TabPanel value="2">
                        <Box sx={{

                            marginTop: '-3%',
                            width: '100%',
                            height: '100%', // Set explicit height
                            position: 'relative'  // Important for map rendering
                        }}>
                            <TabPanel value="2">
                                <Box sx={{width: '100%', height: '500px', position: 'relative'}}>
                                    <NextOrderInfo
                                        nextOrder={nextOrder}
                                    />
                                    <EnhancedMap
                                        points={routingPoints}
                                        assignments={assigmentData}
                                        onNextOrder={setNextOrder}
                                        nextOrder={nextOrder}
                                        hub={hub}// Truyền nextOrder vào EnhancedMap
                                    />
                                </Box>
                            </TabPanel>
                        </Box>
                    </TabPanel>


                    <TabPanel value="3">
                        <NextOrderInfo
                            nextOrder={nextOrder}
                        />
                        <Box sx={{ padding: 2, backgroundColor: '#f5f5f5', borderRadius: 2 }}>
                            <Typography variant="h6" sx={{ marginBottom: 2 }}>
                                Tiến trình thu gom hàng
                            </Typography>
                            <Grid container spacing={2}>
                                {/* Tổng số điểm dừng */}
                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Tổng số điểm dừng
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'primary.main' }}>
                                            {assigmentData?.length}
                                        </Typography>
                                    </Box>
                                </Grid>

                                {/* Số điểm dừng đã qua */}
                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Số điểm dừng hoàn thành
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'secondary.main' }}>
                                            {assigmentData?.filter(order => order.assignmentStatus === "COMPLETED").length}
                                        </Typography>
                                    </Box>
                                </Grid>

                                {/* Tổng số package */}
                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Tổng số package
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'primary.main' }}>
                                            {assigmentData?.reduce((total, order) => total + order.numOfItem, 0)}
                                        </Typography>
                                    </Box>
                                </Grid>

                                {/* Tổng số package đã thu */}
                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Tổng số package đã thu
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'secondary.main' }}>
                                            {assigmentData?.filter(order => order.assignmentStatus === "COMPLETED")
                                                .reduce((total, order) => total + order.numOfItem, 0)}
                                        </Typography>
                                    </Box>
                                </Grid>
                            </Grid>
                        </Box>
                    </TabPanel>

                </TabContext>
            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_ASSIGN_ORDER";
// export default withScreenSecurity(AssignOrder, SCR_ID, true);
export default TodayOrder;