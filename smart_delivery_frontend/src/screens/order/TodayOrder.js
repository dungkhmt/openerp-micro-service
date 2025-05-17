import LoadingScreen from "components/common/loading/loading";
import {Box, Button, Grid, Modal, Tab, Typography} from "@mui/material";
import {request} from "api";
import StandardTable from "components/StandardTable";
import React, {Fragment, useEffect, useState, useCallback} from "react";
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
    const username = useSelector((state) => state.auth.user?.username);
    const role = useSelector((state) => state.auth.user?.role);
    const classes = useStyles();
    const [selectPosition, setSelectPosition] = useState(null);
    const [loading, setLoading] = useState(true);
    const [openModal, setOpenModal] = useState(false);
    const hubId = useSelector((state) => state.auth.user?.hubId);
    const [hub, setHub] = useState();
    const [nextOrder, setNextOrder] = useState(null);
    const [employeeId, setEmployeeId] = useState(null);
    const [assignmentData, setAssignmentData] = useState([]);
    const [tabValue, setTabValue] = useState('1');
    const [expandedRows, setExpandedRows] = useState({});

    console.log("role",role)
    // Determine if user is collector or shipper
    const isCollector = role === 'COLLECTOR';
    const isShipper = role === 'SHIPPER';

    // Session storage keys based on role
    const assignmentsStorageKey = isCollector ? 'collector_assignments' : 'shipper_assignments';
    const nextOrderStorageKey = isCollector ? 'collector_nextOrder' : 'shipper_nextOrder';
    const savedNextOrderKey = isCollector ? 'collector_savedNextOrder' : 'shipper_savedNextOrder';

    // Role-specific strings
    const roleText = isCollector ? 'lấy hàng' : 'giao hàng';
    const personTypeText = isCollector ? 'người gửi' : 'người nhận';
    const actionProcessText = isCollector ? 'thu gom' : 'giao';
    const packageActionText = isCollector ? 'đã thu' : 'đã giao';

    // Role-specific API endpoints
    const userEndpoint = isCollector
        ? `/user/get-collector/${username}`
        : `/user/get-shipper/${username}`;

    const assignmentsEndpoint = useCallback((id) => {
        return isCollector
            ? `/smdeli/ordermanager/order/assign/today/collector/${id}`
            : `/smdeli/ordermanager/order/assign/today/shipper/${id}`;
    }, [isCollector]);

    // Role-specific navigation paths
    const detailPath = isCollector ? '/order/collector/' : '/order/shipper/';

    useEffect(() => {
        async function fetchId() {
            await request(
                "get",
                userEndpoint,
                (res) => {
                    console.log(res.data.id);
                    setEmployeeId(res.data.id);
                    console.log("hubId", res.data.hubId);
                }
            );
        }
        fetchId();
    }, [userEndpoint]);

    useEffect(() => {
        const fetchHubLocation = async () => {
            if (!hubId) return;

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
            );
        }
        fetchHubLocation();
    }, [hubId]);

    const handleGoToDetails = () => {
        const nextOrderFromStorage = sessionStorage.getItem(nextOrderStorageKey);
        if (nextOrderFromStorage) {
            const nextOrder = JSON.parse(nextOrderFromStorage);
            history.push({
                pathname: `${detailPath}${nextOrder.orderId}`,
                state: { assignmentId: nextOrder.id }
            });
        } else {
            errorNoti(`Không tìm thấy đơn hàng ${roleText} tiếp theo`);
        }
    };

    const handleNextOrder = (order) => {
        setNextOrder(order);
    };

    // Role-specific order information component
    const NextOrderInfo = () => {
        const nextOrderData = JSON.parse(sessionStorage.getItem(nextOrderStorageKey));

        const nameField = isCollector ? 'senderName' : 'recipientName';
        const addressField = isCollector ? 'senderAddress' : 'recipientAddress';
        const phoneField = isCollector ? 'senderPhone' : 'recipientPhone';

        return (
            <Box sx={{paddingTop: 1.25, paddingLeft:2, paddingBottom: 1.25, paddingRight: 2, backgroundColor: '#f5f5f5', borderRadius: 2, marginBottom: 0.8}}>
                <Box sx={{display: 'flex', alignItems: 'center', marginBottom: '8px'}}>
                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', marginRight: 2, minWidth: '110px', paddingTop: '4px', color: 'primary.main' }}>
                        Đơn tiếp theo:
                    </Typography>


                    <Box sx={{width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                        <Box>
                            {nextOrderData != null ?
                                <Typography>Tên {personTypeText}: {nextOrderData?.[nameField]}</Typography> :
                                "Không có đơn hàng!"}
                            {nextOrderData != null && <Typography>Địa chỉ: {nextOrderData?.[addressField]}</Typography>}
                        </Box>
                        {nextOrderData != null && <Box sx={{display: 'flex', alignItems: 'center', gap: 2}}>
                            <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                                <Typography>Số điện thoại: {nextOrderData?.[phoneField]}</Typography>
                                <Typography>Số lượng package: {nextOrderData?.numOfItem}</Typography>
                            </Box>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleGoToDetails}
                            >
                                Thao tác
                            </Button>
                        </Box>}
                    </Box>
                </Box>
            </Box>
        );
    };

    useEffect(() => {
        const nextOrderFromStorage = sessionStorage.getItem(nextOrderStorageKey);
        if (nextOrderFromStorage) {
            setNextOrder(JSON.parse(nextOrderFromStorage));
        }
    }, [nextOrderStorageKey]);

    useEffect(() => {
        if (!employeeId) {
            setLoading(false);  // Still stop loading even if employeeId is not available
            return;
        }
        async function fetchData() {
            await request(
                "get",
                assignmentsEndpoint(employeeId),
                (res) => {
                    if (res.data) {
                        const sortedData = res.data.sort((a, b) => a.sequenceNumber - b.sequenceNumber);
                        setAssignmentData(sortedData);
                        sessionStorage.setItem(assignmentsStorageKey, JSON.stringify(sortedData));

                        if (sortedData.length > 0 && !sessionStorage.getItem(savedNextOrderKey)) {
                            sessionStorage.setItem(nextOrderStorageKey, JSON.stringify(sortedData[0]));
                            sessionStorage.setItem(savedNextOrderKey, "1");
                            setNextOrder(sortedData[0]);
                        }
                    }
                }
            );
        }

        const assignmentsFromStorage = sessionStorage.getItem(assignmentsStorageKey);
        if (assignmentsFromStorage) {
            const parsedAssignments = JSON.parse(assignmentsFromStorage);
            const sortedAssignments = parsedAssignments?.sort((a, b) => a.sequenceNumber - b.sequenceNumber);
            setAssignmentData(sortedAssignments);

            if (sortedAssignments?.length > 0 && !sessionStorage.getItem(nextOrderStorageKey)) {
                sessionStorage.setItem(nextOrderStorageKey, JSON.stringify(sortedAssignments[0]));
            }
        }

        fetchData();
        setLoading(false);
    }, [employeeId, assignmentsEndpoint, assignmentsStorageKey, nextOrderStorageKey, savedNextOrderKey]);

    // Get role-specific coordinates for routes
    const routingPoints = assignmentData?.map(order => {
        if (isCollector) {
            return {
                lat: order.senderLatitude,
                lng: order.senderLongitude
            };
        } else {
            return {
                lat: order.recipientLatitude,
                lng: order.recipientLongitude
            };
        }
    }).filter(point => point.lat && point.lng);

    const handleShowRoute = () => {
        setOpenModal(true);
    };

    // Role-specific table columns
    const getTableColumns = () => {
        const commonColumns = [
            {title: "STT", field: "sequenceNumber"},
            {
                title: "Mã đơn hàng",
                field: "orderId",
                renderCell: (rowData) => {
                    const isExpanded = expandedRows[rowData.orderId] || false;

                    return (
                        <Typography
                            onClick={(e) => {
                                e.stopPropagation(); // Prevent row selection
                                setExpandedRows({
                                    ...expandedRows,
                                    [rowData.orderId]: !isExpanded
                                });
                            }}
                            sx={{
                                cursor: 'pointer',
                                '&:hover': { textDecoration: 'underline' },
                                maxWidth: isExpanded ? 'none' : '100px',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                whiteSpace: isExpanded ? 'normal' : 'nowrap'
                            }}
                        >
                            {rowData.orderId}
                        </Typography>
                    );
                }
            },
            {title: "Trạng thái", field: "assignmentStatus"},
            {
                title: "Thao tác",
                field: "actions",
                centerHeader: true,
                sorting: false,
                renderCell: (rowData) => (
                    <div>
                        <IconButton
                            onClick={() => {
                                history.push({
                                    pathname: `${detailPath}${rowData.orderId}`,
                                    state: { assignmentId: rowData.id }
                                });
                            }}
                            color="success"
                        >
                            <VisibilityIcon/>
                        </IconButton>
                    </div>
                ),
            }
        ];

        if (isCollector) {
            return [
                ...commonColumns.slice(0, 2),
                {title: "Tên người gửi", field: "senderName"},
                {title: "Địa chỉ", field: "senderAddress"},
                {title: "Số điện thoại", field: "senderPhone"},
                ...commonColumns.slice(2)
            ];
        } else {
            return [
                ...commonColumns.slice(0, 2),
                {title: "Tên người nhận", field: "recipientName"},
                {title: "Địa chỉ", field: "recipientAddress"},
                {title: "Số điện thoại", field: "recipientPhone"},
                ...commonColumns.slice(2)
            ];
        }
    };

    return loading ? (
        <LoadingScreen/>
    ) : (
        <Fragment>
            <Box className={classes.bodyBox}>
                <TabContext value={tabValue}>
                    <Box sx={{borderBottom: 0, borderColor: "divider"}}>
                        <TabList
                            onChange={(event, newValue) => setTabValue(newValue)}
                        >
                            <Tab label="Danh sách đơn hàng" value="1"/>
                            <Tab label="Bản đồ" value="2"/>
                            <Tab label="Tiến trình" value="3"/>
                            {isShipper && <Tab label="Báo cáo phát" value="4"/>}
                        </TabList>
                    </Box>

                    <TabPanel value="1">
                        <StandardTable
                            title={`Bảng phân công ${roleText} hôm nay`}
                            columns={getTableColumns()}
                            data={assignmentData}
                            options={{
                                selection: false,
                                pageSize: 10,
                                search: true,
                                sorting: true,
                            }}
                            defaultOrderBy="sequenceNumber"
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
                                    <SearchBox
                                        selectPosition={selectPosition}
                                        setSelectPosition={setSelectPosition}
                                    />
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
                            height: '100%',
                            position: 'relative'
                        }}>
                            <Box sx={{width: '100%', height: '500px', position: 'relative'}}>
                                <NextOrderInfo />
                                <EnhancedMap
                                    points={routingPoints}
                                    assignments={assignmentData}
                                    onNextOrder={setNextOrder}
                                    nextOrder={nextOrder}
                                    hub={hub}
                                    role={role}
                                />
                            </Box>
                        </Box>
                    </TabPanel>

                    <TabPanel value="3">
                        <NextOrderInfo />
                        <Box sx={{ padding: 2, backgroundColor: '#f5f5f5', borderRadius: 2 }}>
                            <Typography variant="h6" sx={{ marginBottom: 2 }}>
                                Tiến trình {actionProcessText} hàng
                            </Typography>
                            <Grid container spacing={2}>
                                {/* Tổng số điểm dừng */}
                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Tổng số điểm dừng
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'primary.main' }}>
                                            {assignmentData?.length}
                                        </Typography>
                                    </Box>
                                </Grid>

                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Số điểm dừng hoàn thành
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'secondary.main' }}>
                                            {assignmentData?.filter(order =>
                                                order.assignmentStatus === "COMPLETED" ||
                                                order.assignmentStatus === "FAILED_ONCE"
                                            ).length}
                                        </Typography>
                                    </Box>
                                </Grid>

                                {/* Tổng số package */}
                                {/* Tổng số package */}
                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Tổng số đơn
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'primary.main' }}>
                                            {assignmentData?.length || 0}
                                        </Typography>
                                    </Box>
                                </Grid>

                                {/* Tổng số package đã thu/giao */}
                                {/* Tổng số đơn đã thu/giao */}
                                <Grid item xs={6}>
                                    <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                            Tổng số đơn {packageActionText}
                                        </Typography>
                                        <Typography variant="h4" sx={{ color: 'secondary.main' }}>
                                            {assignmentData?.filter(order => order.assignmentStatus === "COMPLETED").length || 0}
                                        </Typography>
                                    </Box>
                                </Grid>
                            </Grid>
                        </Box>
                    </TabPanel>

                    {/* Shipper-specific tab */}
                    {isShipper && (
                        <TabPanel value="4">
                            <Box sx={{ padding: 2, backgroundColor: '#f5f5f5', borderRadius: 2 }}>
                                <Typography variant="h6" sx={{ marginBottom: 2 }}>
                                    Báo cáo giao hàng
                                </Typography>
                                <Grid container spacing={2}>
                                    {/* Giao thành công */}
                                    <Grid item xs={4}>
                                        <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                            <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                                Giao thành công
                                            </Typography>
                                            <Typography variant="h4" sx={{ color: '#4caf50' }}>
                                                {assignmentData?.filter(order => order.assignmentStatus === "COMPLETED").length}
                                            </Typography>
                                        </Box>
                                    </Grid>

                                    {/* Giao không thành công */}
                                    <Grid item xs={4}>
                                        <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                            <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                                Giao không thành công
                                            </Typography>
                                            <Typography variant="h4" sx={{ color: '#f44336' }}>
                                                {assignmentData?.filter(order => order.assignmentStatus === "FAILED").length}
                                            </Typography>
                                        </Box>
                                    </Grid>

                                    {/* Chưa giao */}
                                    <Grid item xs={4}>
                                        <Box sx={{ padding: 2, backgroundColor: 'white', borderRadius: 2, textAlign: 'center' }}>
                                            <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                                Chưa giao
                                            </Typography>
                                            <Typography variant="h4" sx={{ color: '#ff9800' }}>
                                                {assignmentData?.filter(order =>
                                                    order.assignmentStatus !== "COMPLETED" &&
                                                    order.assignmentStatus !== "FAILED"
                                                ).length}
                                            </Typography>
                                        </Box>
                                    </Grid>
                                </Grid>
                            </Box>
                        </TabPanel>
                    )}
                </TabContext>
            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_ASSIGN_ORDER";
// export default withScreenSecurity(AssignOrder, SCR_ID, true);
export default TodayOrder;