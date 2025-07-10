import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import AssignmentIcon from "@mui/icons-material/Assignment";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import DirectionsCarIcon from "@mui/icons-material/DirectionsCar";
import SearchIcon from "@mui/icons-material/Search";
import ClearIcon from "@mui/icons-material/Clear";
import StandardTable from "../../components/StandardTable";
import {
    Box,
    Button,
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Typography,
    Card,
    CardContent,
    Grid,
    Tabs,
    Tab,
    Divider,
    CircularProgress,
    TextField,
    InputAdornment,
    Stack,
    Avatar,
    Paper
} from "@mui/material";
import {useSelector} from "react-redux";
import {errorNoti, successNoti} from "../../utils/notification";

const OutOrder = () => {
    const hubId = useSelector((state) => state.auth.user?.hubId);
    // Trạng thái tab
    const [activeTab, setActiveTab] = useState(0);
    // Trạng thái liên quan đến tài xế
    const [trips, setTrips] = useState([]);
    const [tripsWithVehicles, setTripsWithVehicles] = useState([]);
    const [cameFirstStopTrips, setCameFirstStopTrips] = useState([]);
    const [otherTrips, setOtherTrips] = useState([]);
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [suggestedOrders, setSuggestedOrders] = useState([]);
    const [filteredOrders, setFilteredOrders] = useState([]);
    const [selectedOrders, setSelectedOrders] = useState([]);
    const [assignOrdersDialog, setAssignOrdersDialog] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    // Trạng thái liên quan đến nhân viên giao hàng
    const [shipperPickupRequests, setShipperPickupRequests] = useState([]);
    const [shipperAssignments, setShipperAssignments] = useState([]);
    // Trạng thái loading
    const [loading, setLoading] = useState(false);
    const [processingIds, setProcessingIds] = useState([]);

    // Lọc đơn hàng dựa trên từ khóa tìm kiếm
    useEffect(() => {
        if (!searchQuery.trim()) {
            setFilteredOrders(suggestedOrders);
        } else {
            const filtered = suggestedOrders.filter(order =>
                order.orderCode?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                order.senderName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                order.recipientName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                order.hubName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                order.currentStatus?.toLowerCase().includes(searchQuery.toLowerCase())
            );
            setFilteredOrders(filtered);
        }
    }, [searchQuery, suggestedOrders]);

    // Lấy dữ liệu dựa trên tab đang active
    useEffect(() => {
        if (hubId) {
            if (activeTab === 0) {
                fetchTripsForToday();
            } else {
                fetchShipperPickupRequests();
                fetchShipperAssignments();
            }
        }
    }, [hubId, activeTab]);

    // Các chức năng liên quan đến tài xế
    useEffect(() => {
        if (trips.length > 0) {
            const tripIds = trips.map(trip => trip.id);
            fetchVehiclesForTrips(tripIds);
        }
    }, [trips]);

    // Phân tách chuyến khi tripsWithVehicles cập nhật
    useEffect(() => {
        if (tripsWithVehicles.length > 0) {
            const cameFirstStop = tripsWithVehicles.filter(trip => trip.status === 'CAME_FIRST_STOP');
            const others = tripsWithVehicles.filter(trip => trip.status !== 'CAME_FIRST_STOP');
            setCameFirstStopTrips(cameFirstStop);
            setOtherTrips(others);
        }
    }, [tripsWithVehicles]);

    const fetchTripsForToday = () => {
        setLoading(true);
        request(
            "get",
            `smdeli/trip-assignments/hub/${hubId}/today/start`,
            (res) => {
                setTrips(res.data);
                setLoading(false);
            },
            {
                400: () => {
                    errorNoti("Dữ liệu không hợp lệ");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Lỗi máy chủ, vui lòng thử lại sau");
                    setLoading(false);
                }
            }
        );
    };

    const fetchVehiclesForTrips = (tripIds) => {
        if (!tripIds || tripIds.length === 0) return;
        const promises = tripIds.map(tripId =>
            new Promise((resolve) => {
                request(
                    "get",
                    `smdeli/vehicle/trip/${tripId}`,
                    (res) => {
                        resolve({ tripId, vehicle: res.data });
                    },
                    {
                        400: () => {
                            resolve({ tripId, vehicle: null });
                        },
                        500: () => {
                            resolve({ tripId, vehicle: null });
                        }
                    }
                );
            })
        );
        Promise.all(promises).then(results => {
            const vehicleMap = results.reduce((map, result) => {
                map[result.tripId] = result.vehicle;
                return map;
            }, {});
            const updatedTrips = trips.map(trip => ({
                ...trip,
                vehicle: vehicleMap[trip.id] || null,
                plateNumber: vehicleMap[trip.id]?.plateNumber || 'Chưa phân công'
            }));
            setTripsWithVehicles(updatedTrips);
        });
    };

    const handleTripSelection = (trip) => {
        window.location.href = `/order/trip/orders/${trip.id}/out`;
    };

    const handleAssignOrders = (trip) => {
        setSelectedTrip(trip);
        setSelectedOrders([]);
        setSearchQuery('');
        fetchSuggestedOrders(trip.id);
        setAssignOrdersDialog(true);
    };

    const fetchSuggestedOrders = (tripId) => {
        setLoading(true);
        request(
            "get",
            `smdeli/middle-mile/trip/${tripId}/suggested-orders`,
            (res) => {
                setSuggestedOrders(res.data);
                setFilteredOrders(res.data);
                setLoading(false);
            },
            {
                400: () => {
                    errorNoti("Không thể tải danh sách đơn hàng đề xuất");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Lỗi máy chủ, vui lòng thử lại sau");
                    setLoading(false);
                }
            }
        );
    };

    const confirmAssignOrders = () => {
        if (!selectedTrip) {
            errorNoti("Vui lòng chọn chuyến đi");
            return;
        }
        if (selectedOrders.length === 0) {
            errorNoti("Vui lòng chọn đơn hàng để phân công");
            return;
        }
        // Kiểm tra giới hạn tải trọng
        const totalWeight = selectedOrders.reduce((sum, order) => sum + (order.weight || 0), 0);
        const totalVolume = selectedOrders.reduce((sum, order) => sum + (order.volume || 0), 0);
        const maxWeight = selectedTrip.vehicle?.weightCapacity || 0;
        const maxVolume = selectedTrip.vehicle?.volumeCapacity || 0;
        if (maxWeight > 0 && totalWeight > maxWeight) {
            errorNoti(`Tổng trọng lượng (${totalWeight.toFixed(1)} kg) vượt quá khả năng chở của xe (${maxWeight} kg)`);
            return;
        }
        if (maxVolume > 0 && totalVolume > maxVolume) {
            errorNoti(`Tổng thể tích (${totalVolume.toFixed(2)} m³) vượt quá khả năng chở của xe (${maxVolume} m³)`);
            return;
        }
        const orderIds = selectedOrders.map(order => order.orderId);
        setLoading(true);
        request(
            "post",
            `smdeli/middle-mile/trip/${selectedTrip.id}/assign-orders`,
            (res) => {
                successNoti("Gán đơn hàng cho chuyến đi thành công");
                setAssignOrdersDialog(false);
                setSelectedOrders([]);
                setSearchQuery('');
                fetchTripsForToday();
                setLoading(false);
            },
            {
                400: (error) => {
                    errorNoti(error.response?.data || "Dữ liệu không hợp lệ");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Lỗi máy chủ, vui lòng thử lại sau");
                    setLoading(false);
                }
            },
            { orderIds }
        );
    };

    const handleOrderSelection = (order) => {
        const isSelected = selectedOrders.some(selected => selected.orderId === order.orderId);
        if (isSelected) {
            setSelectedOrders(selectedOrders.filter(selected => selected.orderId !== order.orderId));
        } else {
            setSelectedOrders([...selectedOrders, order]);
        }
    };

    const handleClearSearch = () => {
        setSearchQuery('');
    };

    const handleSelectAll = () => {
        if (selectedOrders.length === filteredOrders.length) {
            setSelectedOrders([]);
        } else {
            setSelectedOrders([...filteredOrders]);
        }
    };

    // Các chức năng liên quan đến nhân viên giao hàng
    const fetchShipperPickupRequests = () => {
        setLoading(true);
        request(
            "get",
            `smdeli/order/assign/hub/shipper/today/${hubId}`,
            (res) => {
                // Lọc để chỉ hiển thị các đơn hàng đã được phân công nhưng chưa được lấy
                const pendingPickup = res.data.filter(assignment =>
                    assignment.status !== 'COMPLETED' && assignment.numOfOrders > assignment.numOfCompleted
                );
                setShipperPickupRequests(pendingPickup);
                setLoading(false);
            },
            {
                400: () => {
                    errorNoti("Không thể tải yêu cầu lấy hàng của nhân viên giao hàng");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Lỗi máy chủ, vui lòng thử lại sau");
                    setLoading(false);
                }
            }
        );
    };

    const fetchShipperAssignments = () => {
        request(
            "get",
            `smdeli/order/assign/hub/shipper/today/${hubId}`,
            (res) => {
                setShipperAssignments(res.data);
            },
            {
                400: () => errorNoti("Không thể tải phân công nhân viên giao hàng"),
                500: () => errorNoti("Lỗi máy chủ, vui lòng thử lại sau")
            }
        );
    };

    // Xác nhận lấy hàng cá nhân của nhân viên giao hàng
    const confirmShipperPickup = async (shipperId) => {
        setProcessingIds(prev => [...prev, shipperId]);
        try {
            await request(
                "put",
                `/smdeli/ordermanager/order/confirm-shipper-pickup/${shipperId}`,
                (res) => {
                    successNoti("Xác nhận lấy hàng của nhân viên giao hàng thành công");
                    fetchShipperPickupRequests();
                    fetchShipperAssignments();
                },
                {
                    400: () => errorNoti("Yêu cầu không hợp lệ"),
                    500: () => errorNoti("Đã xảy ra lỗi máy chủ")
                }
            );
        } catch (error) {
            errorNoti("Lỗi khi xác nhận lấy hàng của nhân viên giao hàng");
        } finally {
            setProcessingIds(prev => prev.filter(id => id !== shipperId));
        }
    };

    // Xác nhận hàng loạt lấy hàng của nhân viên giao hàng
    const handleBulkConfirmPickup = async (selectedIds) => {
        if (!selectedIds || selectedIds.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một yêu cầu lấy hàng");
            return;
        }
        setProcessingIds(prev => [...prev, ...selectedIds]);
        setLoading(true);
        const idsString = Array.isArray(selectedIds)
            ? selectedIds.join(',')
            : selectedIds.toString();
        try {
            await request(
                "put",
                `/smdeli/ordermanager/order/confirm-shipper-pickups/${idsString}`,
                (res) => {
                    successNoti(`Đã xác nhận thành công ${selectedIds.length} yêu cầu lấy hàng`);
                    fetchShipperPickupRequests();
                    fetchShipperAssignments();
                },
                {
                    400: () => errorNoti("Yêu cầu không hợp lệ"),
                    500: () => errorNoti("Đã xảy ra lỗi máy chủ")
                }
            );
        } catch (error) {
            errorNoti("Lỗi khi xác nhận yêu cầu lấy hàng");
        } finally {
            setProcessingIds([]);
            setLoading(false);
        }
    };

    const viewShipperDetails = (assignment) => {
        window.location.href = `/order/shipper/${assignment.shipperId}/assignments`;
    };

    // Các hàm tiện ích
    const getTripStatusColor = (status) => {
        switch (status) {
            case 'PLANNED': return 'default';
            case 'CAME_FIRST_STOP': return 'warning';
            case 'READY_FOR_PICKUP': return 'primary';
            case 'IN_PROGRESS': return 'secondary';
            case 'COMPLETED': return 'success';
            case 'CANCELLED': return 'error';
            default: return 'default';
        }
    };

    const getTripStatusText = (status) => {
        switch (status) {
            case 'PLANNED': return 'Đã lên kế hoạch';
            case 'CAME_FIRST_STOP': return 'Đã đến điểm đầu';
            case 'READY_FOR_PICKUP': return 'Sẵn sàng lấy hàng';
            case 'IN_PROGRESS': return 'Đang thực hiện';
            case 'COMPLETED': return 'Hoàn thành';
            case 'CANCELLED': return 'Đã hủy';
            default: return status;
        }
    };

    const getPriorityColor = (priority) => {
        if (priority >= 80) return 'error';
        if (priority >= 60) return 'warning';
        return 'success';
    };

    const getOrderStatusColor = (status) => {
        switch (status) {
            case 'COLLECTED_HUB': return 'primary';
            case 'SHIPPER_ASSIGNED': return 'warning';
            case 'OUT_FOR_DELIVERY': return 'info';
            case 'DRIVER_ASSIGNED': return 'secondary';
            default: return 'default';
        }
    };

    const getOrderStatusText = (status) => {
        switch (status) {
            case 'COLLECTED_HUB': return 'Đã thu tại hub';
            case 'SHIPPER_ASSIGNED': return 'Đã phân công NV giao hàng';
            case 'OUT_FOR_DELIVERY': return 'Đang giao hàng';
            case 'DRIVER_ASSIGNED': return 'Đã phân công tài xế';
            default: return status;
        }
    };

    // Cột bảng chuyến cho các chuyến CAME_FIRST_STOP
    const cameFirstStopColumns = [
        {
            title: "Mã chuyến",
            field: "id",
            renderCell: (rowData) => rowData.id.substring(0, 8) + "..."
        },
        {
            title: "Tuyến đường",
            field: "routeName",
        },
        {
            title: "Biển số xe",
            field: "plateNumber",
        },
        {
            title: "Ngày",
            field: "date",
        },
        {
            title: "Số lượng đơn hàng",
            field: "ordersCount",
            renderCell: (rowData) => rowData.ordersCount || 0
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <Box>
                    <IconButton
                        onClick={() => handleTripSelection(rowData)}
                        color="primary"
                        title="Xem chi tiết chuyến"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => handleAssignOrders(rowData)}
                        color="secondary"
                        title="Gán đơn hàng"
                    >
                        <AssignmentIcon />
                    </IconButton>
                </Box>
            ),
        },
    ];

    // Cột bảng chuyến cho các chuyến khác
    const otherTripColumns = [
        {
            title: "Mã chuyến",
            field: "id",
            renderCell: (rowData) => rowData.id.substring(0, 8) + "..."
        },
        {
            title: "Tuyến đường",
            field: "routeName",
        },
        {
            title: "Biển số xe",
            field: "plateNumber",
        },
        {
            title: "Trạng thái",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={getTripStatusText(rowData.status)}
                    color={getTripStatusColor(rowData.status)}
                    size="small"
                />
            )
        },
        {
            title: "Ngày",
            field: "date",
        },
        {
            title: "Số lượng đơn hàng",
            field: "ordersCount",
            renderCell: (rowData) => rowData.ordersCount || 0
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <Box>
                    <IconButton
                        onClick={() => handleTripSelection(rowData)}
                        color="primary"
                        title="Xem chi tiết chuyến"
                    >
                        <VisibilityIcon />
                    </IconButton>
                </Box>
            ),
        },
    ];

    // Cột bảng yêu cầu lấy hàng của nhân viên giao hàng
    const shipperPickupColumns = [
        {
            title: "Tên nhân viên giao hàng",
            field: "shipperName",
        },
        {
            title: "Số điện thoại",
            field: "shipperPhone",
        },
        {
            title: "Tổng số đơn hàng",
            field: "numOfOrders",
        },
        {
            title: "Chờ lấy hàng",
            field: "pendingPickup",
            renderCell: (rowData) => rowData.numOfOrders - rowData.numOfCompleted
        },
        {
            title: "Trạng thái",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={rowData.status === 'COMPLETED' ? 'Hoàn thành' : 'Đang chờ lấy hàng'}
                    color={rowData.status === 'COMPLETED' ? 'success' : 'warning'}
                    size="small"
                />
            )
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <Box>
                    <IconButton
                        onClick={() => viewShipperDetails(rowData)}
                        color="primary"
                        title="Xem đơn hàng của nhân viên giao hàng"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => confirmShipperPickup(rowData.shipperId)}
                        color="success"
                        disabled={processingIds.includes(rowData.shipperId)}
                        title="Xác nhận lấy hàng"
                    >
                        {processingIds.includes(rowData.shipperId) ?
                            <CircularProgress size={24} color="inherit" /> :
                            <CheckCircleIcon />
                        }
                    </IconButton>
                </Box>
            ),
        },
    ];

    // Cột bảng phân công nhân viên giao hàng
    const shipperAssignmentColumns = [
        {
            title: "Tên nhân viên giao hàng",
            field: "shipperName",
        },
        {
            title: "Tổng số đơn hàng",
            field: "numOfOrders",
        },
        {
            title: "Đã hoàn thành",
            field: "numOfCompleted",
        },
        {
            title: "Tỷ lệ hoàn thành",
            field: "completionRate",
            renderCell: (rowData) => {
                const rate = rowData.numOfOrders > 0
                    ? ((rowData.numOfCompleted / rowData.numOfOrders) * 100).toFixed(1)
                    : '0.0';
                return `${rate}%`;
            }
        },
        {
            title: "Số điện thoại",
            field: "shipperPhone",
        },
        {
            title: "Trạng thái",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={rowData.status === 'COMPLETED' ? 'Hoàn thành' : 'Đang hoạt động'}
                    color={rowData.status === 'COMPLETED' ? 'success' : 'primary'}
                    size="small"
                />
            )
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <IconButton
                    onClick={() => viewShipperDetails(rowData)}
                    color="primary"
                    title="Xem chi tiết nhân viên giao hàng"
                >
                    <VisibilityIcon />
                </IconButton>
            ),
        },
    ];

    const handleViewOrder = (order) => {
        window.location.href = `/order/detail/${order.id}`;
    };

    // Tính toán cảnh báo tải trọng
    const getCapacityWarning = () => {
        if (selectedOrders.length === 0 || !selectedTrip?.vehicle) return null;
        const totalWeight = selectedOrders.reduce((sum, order) => sum + (order.weight || 0), 0);
        const totalVolume = selectedOrders.reduce((sum, order) => sum + (order.volume || 0), 0);
        const maxWeight = selectedTrip.vehicle.weightCapacity || 0;
        const maxVolume = selectedTrip.vehicle.volumeCapacity || 0;
        const weightExceeded = maxWeight > 0 && totalWeight > maxWeight;
        const volumeExceeded = maxVolume > 0 && totalVolume > maxVolume;
        if (!weightExceeded && !volumeExceeded) return null;
        return {
            weightExceeded,
            volumeExceeded,
            totalWeight,
            totalVolume,
            maxWeight,
            maxVolume
        };
    };

    return (
        <div>
            <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
                <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)}>
                    <Tab
                        label={
                            <Box display="flex" alignItems="center" gap={1}>
                                <DirectionsCarIcon />
                                {`Chuyến tài xế (${trips.length})`}
                            </Box>
                        }
                    />
                    <Tab
                        label={
                            <Box display="flex" alignItems="center" gap={1}>
                                <LocalShippingIcon />
                                {`Lấy hàng NV giao hàng (${shipperPickupRequests.length})`}
                            </Box>
                        }
                    />
                </Tabs>
            </Box>
            {/* Tab Tài xế */}
            {activeTab === 0 && (
                <Box>
                    {/* Chuyến sẵn sàng để phân công đơn hàng */}
                    {cameFirstStopTrips.length > 0 && (
                        <>
                            <StandardTable
                                title={
                                    <Box display="flex" alignItems="center" gap={1} mb={2}>
                                        <AssignmentIcon color="warning" />
                                        <Typography variant="h6" color="warning.main">
                                            Chuyến đã đến hub - {cameFirstStopTrips.length}
                                        </Typography>
                                    </Box>
                                }
                                columns={cameFirstStopColumns}
                                data={cameFirstStopTrips}
                                rowKey="id"
                                defaultOrderBy="date"
                                defaultOrder="desc"
                                isLoading={loading}
                                options={{
                                    pageSize: 5,
                                    search: true,
                                    sorting: true,
                                    headerStyle: {
                                        backgroundColor: '#fff3e0',
                                        fontWeight: 'bold'
                                    }
                                }}
                            />
                        </>
                    )}
                    {/* Tất cả các chuyến khác */}
                    <StandardTable
                        title="Các chuyến khác sẽ khởi hành từ hub"
                        columns={otherTripColumns}
                        data={otherTrips}
                        rowKey="id"
                        defaultOrderBy="status"
                        defaultOrder="desc"
                        isLoading={loading}
                        options={{
                            pageSize: 10,
                            search: true,
                            sorting: true,
                            headerStyle: {
                                backgroundColor: '#f5f5f5',
                                fontWeight: 'bold'
                            }
                        }}
                    />
                    {/* Dialog phân công đơn hàng cho tài xế */}
                    <Dialog
                        open={assignOrdersDialog}
                        onClose={() => setAssignOrdersDialog(false)}
                        maxWidth="lg"
                        fullWidth
                    >
                        <DialogTitle>
                            <Typography variant="h6">
                                Gán đơn hàng cho chuyến {selectedTrip?.id?.substring(0, 8)}...
                            </Typography>
                            {selectedTrip && (
                                <Typography variant="body2" color="textSecondary">
                                    Tuyến đường: {selectedTrip.routeName} | Xe: {selectedTrip.plateNumber}
                                </Typography>
                            )}
                        </DialogTitle>
                        <DialogContent sx={{ px: 3, py: 1 }}>
                            {loading ? (
                                <Box display="flex" justifyContent="center" alignItems="center" p={4}>
                                    <CircularProgress size={24} />
                                    <Typography sx={{ ml: 2 }}>Đang tải danh sách đơn hàng đề xuất...</Typography>
                                </Box>
                            ) : (
                                <Box>
                                    {/* Thông tin tải trọng xe */}
                                    {selectedTrip?.vehicle && (
                                        <Card sx={{ mb: 2 }}>
                                            <CardContent sx={{ py: 1, px: 2 }}>
                                                <Typography variant="body2" fontWeight="bold">Tải trọng xe</Typography>
                                                <Grid container spacing={2}>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>
                                                            Trọng lượng tối đa: {selectedTrip.vehicle.weightCapacity || 'N/A'} kg
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>
                                                            Thể tích tối đa: {selectedTrip.vehicle.volumeCapacity || 'N/A'} m³
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>
                                                            Đã chọn: {selectedOrders.length} đơn hàng
                                                        </Typography>
                                                    </Grid>
                                                </Grid>
                                            </CardContent>
                                        </Card>
                                    )}
                                    {/* Phần tìm kiếm và lọc */}
                                    <Grid container spacing={1} alignItems="center" sx={{ mb: 2 }}>
                                        <Grid item xs={8}>
                                            <TextField
                                                fullWidth
                                                placeholder="Tìm kiếm đơn hàng..."
                                                value={searchQuery}
                                                onChange={(e) => setSearchQuery(e.target.value)}
                                                InputProps={{
                                                    startAdornment: (
                                                        <InputAdornment position="start">
                                                            <SearchIcon color="action" />
                                                        </InputAdornment>
                                                    ),
                                                    endAdornment: searchQuery && (
                                                        <InputAdornment position="end">
                                                            <IconButton onClick={handleClearSearch} size="small">
                                                                <ClearIcon />
                                                            </IconButton>
                                                        </InputAdornment>
                                                    ),
                                                }}
                                                size="small"
                                            />
                                        </Grid>
                                        <Grid item xs={4}>
                                            <Stack direction="row" spacing={1} alignItems="center">
                                                <Button
                                                    variant={selectedOrders.length === filteredOrders.length && filteredOrders.length > 0 ? "contained" : "outlined"}
                                                    onClick={handleSelectAll}
                                                    disabled={filteredOrders.length === 0}
                                                    size="small"
                                                >
                                                    {selectedOrders.length === filteredOrders.length && filteredOrders.length > 0 ? 'Bỏ chọn tất cả' : 'Chọn tất cả'}
                                                </Button>
                                            </Stack>
                                        </Grid>
                                    </Grid>
                                    <Typography variant="body2" fontWeight="bold" sx={{ mb: 1 }}>
                                        Đơn hàng đề xuất ({filteredOrders.length})
                                    </Typography>
                                    <Box sx={{ maxHeight: 350, overflowY: 'auto', pr: 1 }}>
                                        {filteredOrders.map((order, index) => {
                                            const isSelected = selectedOrders.some(selected => selected.orderId === order.orderId);
                                            return (
                                                <Card
                                                    key={order.orderId}
                                                    sx={{
                                                        mb: 1,
                                                        cursor: 'pointer',
                                                        border: isSelected ? '1px solid' : '1px solid',
                                                        borderColor: isSelected ? 'primary.main' : 'grey.300',
                                                        bgcolor: isSelected ? 'primary.50' : 'background.paper',
                                                        '&:hover': {
                                                            bgcolor: isSelected ? 'primary.100' : 'grey.50',
                                                        }
                                                    }}
                                                    onClick={() => handleOrderSelection(order)}
                                                >
                                                    <CardContent sx={{ py: 1, px: 2 }}>
                                                        <Grid container spacing={1} alignItems="center">
                                                            {/* Mã đơn hàng và Order ID */}
                                                            <Grid item xs={2}>
                                                                <Typography variant="body2" fontWeight="bold" color="primary.main">
                                                                    {order.orderCode}
                                                                </Typography>
                                                                <Typography variant="caption" color="textSecondary" sx={{ fontSize: '0.65rem' }}>
                                                                    ID: {order.orderId?.substring(0, 8)}...
                                                                </Typography>
                                                            </Grid>
                                                            {/* Người gửi và người nhận */}
                                                            <Grid item xs={3}>
                                                                <Typography variant="caption" color="textSecondary" sx={{ fontSize: '0.7rem' }}>
                                                                    TỪ
                                                                </Typography>
                                                                <Typography variant="body2" fontWeight="medium" noWrap sx={{ fontSize: '0.8rem' }}>
                                                                    {order.senderName}
                                                                </Typography>
                                                                <Typography variant="caption" color="textSecondary" sx={{ fontSize: '0.7rem' }}>
                                                                    ĐẾN
                                                                </Typography>
                                                                <Typography variant="body2" fontWeight="medium" noWrap sx={{ fontSize: '0.8rem' }}>
                                                                    {order.recipientName}
                                                                </Typography>
                                                            </Grid>
                                                            {/* Trọng lượng và thể tích */}
                                                            <Grid item xs={2}>
                                                                <Typography variant="caption" color="textSecondary">
                                                                    Trọng lượng: {order.weight} kg
                                                                </Typography>
                                                                <br />
                                                                <Typography variant="caption" color="textSecondary">
                                                                    Thể tích: {order.volume} m³
                                                                </Typography>
                                                            </Grid>
                                                            {/* Hub và điểm dừng */}
                                                            <Grid item xs={2}>
                                                                <Typography variant="caption" color="textSecondary">
                                                                    Hub: {order.hubName}
                                                                </Typography>
                                                                <br />
                                                                <Chip
                                                                    label={`#${order.stopSequence}`}
                                                                    color="info"
                                                                    size="small"
                                                                    variant="outlined"
                                                                    sx={{ fontSize: '0.65rem', height: '16px' }}
                                                                />
                                                            </Grid>
                                                            {/* Trạng thái */}
                                                            <Grid item xs={2}>
                                                                <Chip
                                                                    label={getOrderStatusText(order.currentStatus)}
                                                                    color={getOrderStatusColor(order.currentStatus)}
                                                                    size="small"
                                                                    sx={{ fontSize: '0.8rem', height: '16px' }}
                                                                />
                                                            </Grid>
                                                            {/* Chỉ báo lựa chọn */}
                                                            <Grid item xs={1}>
                                                                <Box display="flex" justifyContent="center">
                                                                    {isSelected && (
                                                                        <CheckCircleIcon color="primary" sx={{ fontSize: '20px' }} />
                                                                    )}
                                                                </Box>
                                                            </Grid>
                                                        </Grid>
                                                    </CardContent>
                                                </Card>
                                            );
                                        })}
                                    </Box>
                                    {/* Cảnh báo tải trọng */}
                                    {(() => {
                                        const warning = getCapacityWarning();
                                        if (!warning) return null;
                                        return (
                                            <Card sx={{ mt: 1, bgcolor: 'error.50', border: '1px solid', borderColor: 'error.main' }}>
                                                <CardContent sx={{ py: 1, px: 2 }}>
                                                    <Typography variant="body2" fontWeight="bold" color="error.main" gutterBottom>
                                                        Vượt quá tải trọng
                                                    </Typography>
                                                    {warning.weightExceeded && (
                                                        <Typography variant="caption" color="error.main">
                                                            Trọng lượng: {warning.totalWeight.toFixed(1)} kg / {warning.maxWeight} kg (Vượt quá {(warning.totalWeight - warning.maxWeight).toFixed(1)} kg)
                                                        </Typography>
                                                    )}
                                                    {warning.volumeExceeded && (
                                                        <Typography variant="caption" color="error.main" sx={{ display: 'block' }}>
                                                            Thể tích: {warning.totalVolume.toFixed(2)} m³ / {warning.maxVolume} m³ (Vượt quá {(warning.totalVolume - warning.maxVolume).toFixed(2)} m³)
                                                        </Typography>
                                                    )}
                                                </CardContent>
                                            </Card>
                                        );
                                    })()}
                                    {/* Phần tóm tắt */}
                                    {selectedOrders.length > 0 && (
                                        <Card sx={{ mt: 2, bgcolor: 'success.50', border: '1px solid', borderColor: 'success.200' }}>
                                            <CardContent sx={{ py: 1, px: 2 }}>
                                                <Typography variant="body2" fontWeight="bold" color="success.main" gutterBottom>
                                                    Tóm tắt lựa chọn
                                                </Typography>
                                                <Grid container spacing={1}>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Đơn hàng</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {selectedOrders.length}
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Tổng trọng lượng</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {selectedOrders.reduce((sum, order) => sum + (order.weight || 0), 0).toFixed(1)} kg
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Tổng thể tích</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {selectedOrders.reduce((sum, order) => sum + (order.volume || 0), 0).toFixed(2)} m³
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Order ID</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {selectedOrders.length > 0 ? selectedOrders[0].orderId?.substring(0, 8) + '...' : 'N/A'}
                                                        </Typography>
                                                    </Grid>
                                                </Grid>
                                            </CardContent>
                                        </Card>
                                    )}
                                    {/* Trạng thái trống */}
                                    {filteredOrders.length === 0 && !loading && (
                                        <Typography color="textSecondary" align="center" sx={{ mt: 2, fontSize: '0.9rem' }}>
                                            {searchQuery ? 'Không có đơn hàng nào phù hợp với tìm kiếm' : 'Không có đơn hàng nào khả dụng cho tuyến đường này.'}
                                        </Typography>
                                    )}
                                </Box>
                            )}
                        </DialogContent>
                        <DialogActions sx={{ p: 3, bgcolor: 'grey.50' }}>
                            <Button
                                onClick={() => setAssignOrdersDialog(false)}
                                disabled={loading}
                                size="large"
                                sx={{ minWidth: 120 }}
                            >
                                Hủy
                            </Button>
                            <Button
                                onClick={confirmAssignOrders}
                                variant="contained"
                                disabled={loading || selectedOrders.length === 0}
                                startIcon={loading ? <CircularProgress size={20} /> : <AssignmentIcon />}
                                size="large"
                                sx={{ minWidth: 200 }}
                            >
                                {loading ? 'Đang phân công...' : `Gán ${selectedOrders.length} đơn hàng`}
                            </Button>
                        </DialogActions>
                    </Dialog>
                    {/* Hướng dẫn cho Tab Tài xế */}
                    <Box sx={{ mt: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                        <Typography variant="body2" color="textSecondary">
                            <strong>Chuyến tài xế:</strong> Những chuyến có trạng thái "CAME_FIRST_STOP" được hiển thị ở phần ưu tiên phía trên
                            và sẵn sàng để gán đơn hàng. Sử dụng nút "Gán đơn hàng" để tải đơn hàng lên những xe này.
                            Tất cả các chuyến khác được hiển thị trong bảng bên dưới để theo dõi.
                        </Typography>
                    </Box>
                </Box>
            )}
            {/* Tab Nhân viên giao hàng */}
            {activeTab === 1 && (
                <Box>
                    <Grid container spacing={3}>
                        {/* Phần yêu cầu lấy hàng của nhân viên giao hàng */}
                        <Grid item xs={12}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        Yêu cầu lấy hàng của nhân viên giao hàng ({shipperPickupRequests.length})
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                                        Đây là những nhân viên giao hàng đang chờ lấy đơn hàng được phân công từ hub.
                                    </Typography>
                                    <StandardTable
                                        columns={shipperPickupColumns}
                                        data={shipperPickupRequests}
                                        rowKey="shipperId"
                                        isLoading={loading}
                                        actions={[
                                            {
                                                iconOnClickHandle: handleBulkConfirmPickup,
                                                tooltip: "Xác nhận các yêu cầu lấy hàng đã chọn",
                                                icon: () => loading ? <CircularProgress size={24} /> : <LocalShippingIcon />,
                                                disabled: loading
                                            }
                                        ]}
                                        options={{
                                            selection: true,
                                            pageSize: 5,
                                            search: true,
                                            sorting: true,
                                            headerStyle: {
                                                backgroundColor: '#f5f5f5',
                                                fontWeight: 'bold'
                                            }
                                        }}
                                    />
                                </CardContent>
                            </Card>
                        </Grid>
                        <Grid item xs={12}>
                            <Divider sx={{ my: 2 }} />
                        </Grid>
                        {/* Phần tất cả phân công nhân viên giao hàng */}
                        <Grid item xs={12}>
                            <StandardTable
                                title="Tất cả phân công nhân viên giao hàng hôm nay"
                                columns={shipperAssignmentColumns}
                                data={shipperAssignments}
                                rowKey="shipperId"
                                isLoading={loading}
                                options={{
                                    pageSize: 10,
                                    search: true,
                                    sorting: true,
                                    headerStyle: {
                                        backgroundColor: '#f5f5f5',
                                        fontWeight: 'bold'
                                    }
                                }}
                            />
                        </Grid>
                    </Grid>
                    {/* Hướng dẫn cho Tab Nhân viên giao hàng */}
                    <Box sx={{ mt: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                        <Typography variant="body2" color="textSecondary">
                            <strong>Lấy hàng nhân viên giao hàng:</strong> Xác nhận yêu cầu lấy hàng từ những nhân viên giao hàng sẵn sàng thu thập
                            đơn hàng được gán từ hub. Sử dụng tính năng xác nhận hàng loạt để xử lý nhiều yêu cầu lấy hàng
                            cùng lúc, hoặc xác nhận từng yêu cầu lấy hàng bằng các nút thao tác.
                        </Typography>
                    </Box>
                </Box>
            )}
        </div>
    );
};

export default OutOrder;