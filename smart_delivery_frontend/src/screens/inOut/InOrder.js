import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import CheckIcon from '@mui/icons-material/Check';
import StandardTable from "../../components/StandardTable";
import {
    Box,
    Button,
    Typography,
    CircularProgress,
    Tabs,
    Tab,
    Chip,
    Card,
    CardContent,
    CardActions,
    Grid,
    Paper,
    Avatar
} from "@mui/material";
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import {useSelector} from "react-redux";
import {errorNoti, successNoti} from "../../utils/notification";

const InOrder = () => {
    const hubId = useSelector((state) => state.auth.user?.hubId);

    // Trạng thái tab
    const [activeTab, setActiveTab] = useState(0);

    // Đơn hàng từ nhân viên thu gom (được thu gom và mang về hub)
    const [collectorOrders, setCollectorOrders] = useState([]);

    // Đơn hàng từ tài xế (được giao đến hub từ các hub khác)
    const [driverOrders, setDriverOrders] = useState([]);

    // Đơn hàng giao thất bại (từ tài xế/nhân viên giao hàng)
    const [driverFailedOrders, setDriverFailedOrders] = useState([]);
    const [shipperFailedOrders, setShipperFailedOrders] = useState([]);

    // Cho Đơn hàng Tài xế: Xem theo chuyến/xe vs Xem danh sách đơn hàng
    const [selectedTripGroup, setSelectedTripGroup] = useState(null);

    // Trạng thái loading
    const [loading, setLoading] = useState(false);
    const [processingIds, setProcessingIds] = useState([]);

    // Lấy dữ liệu dựa trên tab đang active
    useEffect(() => {
        if (hubId) {
            switch (activeTab) {
                case 0:
                    fetchCollectorOrders();
                    break;
                case 1:
                    fetchDriverOrders();
                    break;
                case 2:
                    fetchDriverFailedOrders();
                    break;
                case 3:
                    fetchShipperFailedOrders();
                    break;
                default:
                    break;
            }
        }
    }, [hubId, activeTab]);

    // Reset chuyến đã chọn khi đổi tab
    useEffect(() => {
        setSelectedTripGroup(null);
    }, [activeTab]);

    const fetchCollectorOrders = async () => {
        setLoading(true);
        try {
            await request(
                "get",
                `/smdeli/ordermanager/order/collected-collector/${hubId}`,
                (res) => {
                    setCollectorOrders(res.data);
                    setLoading(false);
                },
                {
                    400: () => {
                        errorNoti("Dữ liệu không hợp lệ");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Lỗi máy chủ");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Không thể tải đơn hàng từ nhân viên thu gom");
            setLoading(false);
        }
    };

    const fetchDriverOrders = async () => {
        setLoading(true);
        try {
            await request(
                "get",
                `/smdeli/ordermanager/order/delivered-driver/${hubId}`,
                (res) => {
                    setDriverOrders(res.data);
                    setLoading(false);
                },
                {
                    400: () => {
                        errorNoti("Dữ liệu không hợp lệ");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Lỗi máy chủ");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Không thể tải đơn hàng từ tài xế");
            setLoading(false);
        }
    };

    const fetchDriverFailedOrders = async () => {
        setLoading(true);
        try {
            await request(
                "get",
                `/smdeli/ordermanager/order/delivered-failed/${hubId}`,
                (res) => {
                    setDriverFailedOrders(res.data);
                    setLoading(false);
                },
                {
                    400: () => {
                        errorNoti("Dữ liệu không hợp lệ");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Lỗi máy chủ");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Không thể tải đơn hàng giao thất bại của tài xế");
            setLoading(false);
        }
    };

    const fetchShipperFailedOrders = async () => {
        setLoading(true);
        try {
            await request(
                "get",
                `/smdeli/ordermanager/order/shipped-failed/${hubId}`,
                (res) => {
                    setShipperFailedOrders(res.data);
                    setLoading(false);
                },
                {
                    400: () => {
                        errorNoti("Dữ liệu không hợp lệ");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Lỗi máy chủ");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Không thể tải đơn hàng giao thất bại của nhân viên giao hàng");
            setLoading(false);
        }
    };

    // Nhóm đơn hàng theo mã chuyến và thông tin xe (dùng cho đơn hàng tài xế)
    const groupOrdersByTrip = (orders) => {
        const grouped = orders.reduce((acc, order) => {
            const tripKey = `${order.tripId}_${order.vehicleType}_${order.vehiclePlateNumber}`;
            if (!acc[tripKey]) {
                acc[tripKey] = {
                    tripId: order.tripId,
                    tripCode: order.tripCode || `CHUYẾN-${order.tripId.substring(0, 8)}`,
                    date: order.date,
                    vehicleType: order.vehicleType,
                    vehiclePlateNumber: order.vehiclePlateNumber,
                    orders: []
                };
            }
            acc[tripKey].orders.push(order);
            return acc;
        }, {});

        return Object.values(grouped);
    };

    const getOrderStatusColor = (status) => {
        switch (status) {
            case 'COLLECTED_COLLECTOR': return 'warning';
            case 'COLLECTED_HUB': return 'info';
            case 'DELIVERED': return 'primary';
            case 'DELIVERY_FAILED': return 'error';
            case 'OUT_FOR_DELIVERY': return 'secondary';
            default: return 'default';
        }
    };

    const getOrderStatusText = (status) => {
        switch (status) {
            case 'COLLECTED_COLLECTOR': return 'Đã thu gom';
            case 'COLLECTED_HUB': return 'Đã nhận tại hub';
            case 'DELIVERED': return 'Đã giao';
            case 'DELIVERY_FAILED': return 'Giao thất bại';
            case 'OUT_FOR_DELIVERY': return 'Đang giao';
            default: return status;
        }
    };

    const getVehicleTypeColor = (vehicleType) => {
        switch (vehicleType) {
            case 'TRUCK': return 'primary';
            case 'VAN': return 'secondary';
            case 'MOTORBIKE': return 'success';
            default: return 'default';
        }
    };

    const getVehicleTypeText = (vehicleType) => {
        switch (vehicleType) {
            case 'TRUCK': return 'Xe tải';
            case 'VAN': return 'Xe van';
            case 'MOTORBIKE': return 'Xe máy';
            default: return vehicleType;
        }
    };

    const getOrderTypeText = (orderType) => {
        switch (orderType) {
            case 'DELIVERY': return 'Giao hàng';
            case 'PICKUP': return 'Lấy hàng';
            case 'RETURN': return 'Trả hàng';
            default: return orderType;
        }
    };

    const handleViewOrder = (order) => {
        window.location.href = `/order/detail/${order.id}`;
    };

    // Xác nhận đơn hàng đơn lẻ
    const confirmSingleOrder = async (orderId) => {
        setProcessingIds(prev => [...prev, orderId]);

        try {
            await request(
                "put",
                `/smdeli/ordermanager/order/confirm-in-hub/${orderId}`,
                (res) => {
                    successNoti("Xác nhận đơn hàng thành công");
                    // Làm mới dữ liệu tab hiện tại
                    switch (activeTab) {
                        case 0:
                            fetchCollectorOrders();
                            break;
                        case 1:
                            fetchDriverOrders();
                            break;
                        case 2:
                            fetchDriverFailedOrders();
                            break;
                        case 3:
                            fetchShipperFailedOrders();
                            break;
                        default:
                            break;
                    }
                },
                {
                    400: () => errorNoti("Yêu cầu không hợp lệ"),
                    500: () => errorNoti("Đã xảy ra lỗi máy chủ")
                }
            );
        } catch (error) {
            errorNoti("Lỗi khi xác nhận đơn hàng");
        } finally {
            setProcessingIds(prev => prev.filter(id => id !== orderId));
        }
    };

    // Xác nhận hàng loạt đơn hàng
    const handleBulkConfirm = async (selectedIds) => {
        if (!selectedIds || selectedIds.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một đơn hàng");
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
                `/smdeli/ordermanager/order/confirm-in-hub/${idsString}`,
                (res) => {
                    successNoti(`Đã xác nhận thành công ${selectedIds.length} đơn hàng`);
                    // Làm mới dữ liệu tab hiện tại
                    switch (activeTab) {
                        case 0:
                            fetchCollectorOrders();
                            break;
                        case 1:
                            fetchDriverOrders();
                            break;
                        case 2:
                            fetchDriverFailedOrders();
                            break;
                        case 3:
                            fetchShipperFailedOrders();
                            break;
                        default:
                            break;
                    }
                },
                {
                    400: () => errorNoti("Yêu cầu không hợp lệ"),
                    500: () => errorNoti("Đã xảy ra lỗi máy chủ")
                }
            );
        } catch (error) {
            errorNoti("Lỗi khi xác nhận đơn hàng");
        } finally {
            setProcessingIds([]);
            setLoading(false);
        }
    };

    // Xác nhận hàng loạt đơn hàng theo chuyến
    const handleBulkConfirmByTrip = async (orders) => {
        const orderIds = orders.map(order => order.id);
        await handleBulkConfirm(orderIds);
    };

    // Cột chung cho tất cả các tab
    const getColumns = () => [
        {
            title: "Mã đơn hàng",
            field: "id",
            renderCell: (rowData) => rowData.id.substring(0, 8) + "..."
        },
        {
            title: "Người gửi",
            field: "senderName",
        },
        {
            title: "Người nhận",
            field: "recipientName",
        },
        {
            title: "Loại đơn hàng",
            field: "orderType",
            renderCell: (rowData) => getOrderTypeText(rowData.orderType)
        },
        {
            title: "Trạng thái",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={getOrderStatusText(rowData.status)}
                    color={getOrderStatusColor(rowData.status)}
                    size="small"
                />
            )
        },
        {
            title: "Tổng tiền",
            field: "totalPrice",
            renderCell: (rowData) => `${(rowData.totalPrice?.toLocaleString() || '0')} VNĐ`
        },
        {
            title: "Thời gian tạo",
            field: "createdAt",
            renderCell: (rowData) => new Date(rowData.createdAt).toLocaleString('vi-VN')
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <Box>
                    <IconButton
                        onClick={() => handleViewOrder(rowData)}
                        color="primary"
                        title="Xem chi tiết đơn hàng"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => confirmSingleOrder(rowData.id)}
                        color="success"
                        disabled={processingIds.includes(rowData.id)}
                        title="Xác nhận đơn hàng vào hub"
                    >
                        {processingIds.includes(rowData.id) ?
                            <CircularProgress size={24} color="inherit" /> :
                            <CheckIcon />
                        }
                    </IconButton>
                </Box>
            ),
        },
    ];

    const getCurrentData = () => {
        switch (activeTab) {
            case 0:
                return collectorOrders;
            case 1:
                return driverOrders;
            case 2:
                return driverFailedOrders;
            case 3:
                return shipperFailedOrders;
            default:
                return [];
        }
    };

    const getTabTitle = () => {
        switch (activeTab) {
            case 0:
                return `Đơn hàng từ nhân viên thu gom (${collectorOrders.length})`;
            case 1:
                return selectedTripGroup
                    ? `Đơn hàng từ chuyến: ${selectedTripGroup.tripCode} (${selectedTripGroup.orders.length})`
                    : `Chuyến giao hàng của tài xế`;
            case 2:
                return `Đơn hàng giao thất bại của tài xế (${driverFailedOrders.length})`;
            case 3:
                return `Đơn hàng giao thất bại của nhân viên giao hàng (${shipperFailedOrders.length})`;
            default:
                return "Đơn hàng";
        }
    };

    // Hiển thị bảng chuyến cho đơn hàng tài xế
    const renderDriverTripsTable = () => {
        const groupedOrders = groupOrdersByTrip(driverOrders);

        const tripColumns = [
            {
                title: "Mã chuyến",
                field: "tripCode",
                renderCell: (rowData) => rowData.tripCode || `CHUYẾN-${rowData.tripId.substring(0, 8)}`
            },
            {
                title: "Loại xe",
                field: "vehicleType",
                renderCell: (rowData) => (
                    <Chip
                        label={getVehicleTypeText(rowData.vehicleType)}
                        color={getVehicleTypeColor(rowData.vehicleType)}
                        size="small"
                    />
                )
            },
            {
                title: "Biển số xe",
                field: "vehiclePlateNumber",
            },
            {
                title: "Ngày",
                field: "date",
                renderCell: (rowData) => new Date(rowData.date).toLocaleDateString('vi-VN')
            },
            {
                title: "Số lượng đơn hàng",
                field: "ordersCount",
                renderCell: (rowData) => (
                    <Chip
                        label={`${rowData.orders.length} đơn hàng`}
                        color="primary"
                        size="small"
                    />
                )
            },
            {
                title: "Tổng giá trị",
                field: "totalValue",
                renderCell: (rowData) =>
                    `${rowData.orders.reduce((sum, order) => sum + (order.totalPrice || 0), 0).toLocaleString()} VNĐ`
            },
            {
                title: "Thao tác",
                field: "actions",
                centerHeader: true,
                sorting: false,
                renderCell: (rowData) => (
                    <Box>
                        <IconButton
                            onClick={() => setSelectedTripGroup(rowData)}
                            color="primary"
                            title="Xem đơn hàng"
                        >
                            <VisibilityIcon />
                        </IconButton>
                        <IconButton
                            onClick={() => handleBulkConfirmByTrip(rowData.orders)}
                            color="success"
                            disabled={loading}
                            title="Xác nhận tất cả đơn hàng"
                        >
                            {loading ?
                                <CircularProgress size={24} color="inherit" /> :
                                <CheckIcon />
                            }
                        </IconButton>
                    </Box>
                ),
            },
        ];

        return (
            <StandardTable
                title={`Chuyến giao hàng của tài xế (${groupedOrders.length} chuyến, ${driverOrders.length} đơn hàng)`}
                columns={tripColumns}
                data={groupedOrders}
                rowKey={(row) => `${row.tripId}_${row.vehiclePlateNumber}`}
                isLoading={loading}
                actions={[
                    {
                        iconOnClickHandle: (selectedIds, selectedRows) => {
                            // Xác nhận tất cả đơn hàng từ các chuyến đã chọn
                            const allOrderIds = selectedRows.flatMap(trip => trip.orders.map(order => order.id));
                            handleBulkConfirm(allOrderIds);
                        },
                        tooltip: "Xác nhận tất cả đơn hàng từ các chuyến đã chọn",
                        icon: () => loading ? <CircularProgress size={24} /> : <CheckIcon />,
                        disabled: loading
                    }
                ]}
                options={{
                    selection: true,
                    pageSize: 10,
                    search: true,
                    sorting: true,
                    headerStyle: {
                        backgroundColor: '#f5f5f5',
                        fontWeight: 'bold'
                    }
                }}
            />
        );
    };

    // Hiển thị bảng đơn hàng cho chuyến đã chọn
    const renderSelectedTripOrders = () => {
        return (
            <Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                    <Button
                        startIcon={<ArrowBackIcon />}
                        onClick={() => setSelectedTripGroup(null)}
                        sx={{ mr: 2 }}
                    >
                        Quay lại danh sách chuyến
                    </Button>
                    <Box sx={{ flex: 1 }}>
                        <Typography variant="h6">
                            Chuyến: {selectedTripGroup.tripCode}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                            {getVehicleTypeText(selectedTripGroup.vehicleType)} • {selectedTripGroup.vehiclePlateNumber} • {new Date(selectedTripGroup.date).toLocaleDateString('vi-VN')}
                        </Typography>
                    </Box>
                    <Button
                        variant="contained"
                        color="success"
                        startIcon={<CheckIcon />}
                        onClick={() => handleBulkConfirmByTrip(selectedTripGroup.orders)}
                        disabled={loading}
                    >
                        Xác nhận tất cả đơn hàng ({selectedTripGroup.orders.length})
                    </Button>
                </Box>

                <StandardTable
                    title=""
                    columns={getColumns()}
                    data={selectedTripGroup.orders}
                    rowKey="id"
                    isLoading={false}
                    actions={[
                        {
                            iconOnClickHandle: handleBulkConfirm,
                            tooltip: "Xác nhận các đơn hàng đã chọn",
                            icon: () => loading ? <CircularProgress size={24} /> : <CheckIcon />,
                            disabled: loading
                        }
                    ]}
                    options={{
                        selection: true,
                        pageSize: 10,
                        search: true,
                        sorting: true,
                        headerStyle: {
                            backgroundColor: '#f5f5f5',
                            fontWeight: 'bold'
                        }
                    }}
                />
            </Box>
        );
    };

    return (
        <div>
            <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
                <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)}>
                    <Tab label={`Từ nhân viên thu gom (${collectorOrders.length})`} />
                    <Tab label={`Từ tài xế (${driverOrders.length})`} />
                    <Tab label={`Tài xế giao thất bại (${driverFailedOrders.length})`} />
                    <Tab label={`Nhân viên giao thất bại (${shipperFailedOrders.length})`} />
                </Tabs>
            </Box>

            {loading && (
                <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
                    <CircularProgress />
                </Box>
            )}

            {!loading && (activeTab === 0 || activeTab === 2 || activeTab === 3) && (
                <StandardTable
                    title={getTabTitle()}
                    columns={getColumns()}
                    data={getCurrentData()}
                    rowKey="id"
                    isLoading={loading}
                    actions={[
                        {
                            iconOnClickHandle: handleBulkConfirm,
                            tooltip: "Xác nhận các đơn hàng đã chọn vào hub",
                            icon: () => loading ? <CircularProgress size={24} /> : <CheckIcon />,
                            disabled: loading
                        }
                    ]}
                    options={{
                        selection: true,
                        pageSize: 10,
                        search: true,
                        sorting: true,
                        headerStyle: {
                            backgroundColor: '#f5f5f5',
                            fontWeight: 'bold'
                        }
                    }}
                />
            )}

            {!loading && activeTab === 1 && (
                selectedTripGroup ? renderSelectedTripOrders() : renderDriverTripsTable()
            )}

            {/* Hướng dẫn cho từng tab */}
            <Box sx={{ mt: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                {activeTab === 0 && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Đơn hàng từ nhân viên thu gom:</strong> Đây là những đơn hàng đã được nhân viên thu gom
                        thu thập và mang về hub. Xác nhận để chính thức nhận chúng vào kho hub.
                    </Typography>
                )}
                {activeTab === 1 && !selectedTripGroup && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Chuyến giao hàng của tài xế:</strong> Đây là những chuyến mà tài xế đã giao đơn hàng đến hub này.
                        Nhấp vào thẻ chuyến để xem và quản lý các đơn hàng riêng lẻ trong chuyến đó.
                        Bạn có thể xác nhận tất cả đơn hàng từ một chuyến cùng lúc, hoặc quản lý từng đơn riêng lẻ.
                    </Typography>
                )}
                {activeTab === 1 && selectedTripGroup && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Đơn hàng trong chuyến:</strong> Đây là các đơn hàng riêng lẻ từ chuyến đã chọn.
                        Bạn có thể xác nhận từng đơn hàng riêng lẻ hoặc chọn nhiều đơn hàng để xác nhận cùng lúc.
                        Sử dụng nút "Quay lại danh sách chuyến" để trở về tổng quan các chuyến.
                    </Typography>
                )}
                {activeTab === 2 && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Đơn hàng giao thất bại của tài xế:</strong> Đây là những đơn hàng mà việc giao hàng đã thất bại
                        bởi tài xế. Xác nhận để đưa chúng trở lại kho hub để xử lý lại.
                    </Typography>
                )}
                {activeTab === 3 && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Đơn hàng giao thất bại của nhân viên giao hàng:</strong> Đây là những đơn hàng mà việc giao hàng đã thất bại
                        bởi nhân viên giao hàng. Xác nhận để đưa chúng trở lại kho hub để xử lý lại.
                    </Typography>
                )}
            </Box>
        </div>
    );
}

export default InOrder;