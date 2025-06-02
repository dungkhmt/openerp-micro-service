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

    // Tab state
    const [activeTab, setActiveTab] = useState(0);

    // Collector orders (collected by collectors and brought to hub)
    const [collectorOrders, setCollectorOrders] = useState([]);

    // Driver orders (delivered to hub by drivers from other hubs)
    const [driverOrders, setDriverOrders] = useState([]);

    // Failed delivery orders (from drivers/shippers delivery attempts)
    const [driverFailedOrders, setDriverFailedOrders] = useState([]);
    const [shipperFailedOrders, setShipperFailedOrders] = useState([]);

    // For Driver Orders: Trip/Vehicle view vs Order list view
    const [selectedTripGroup, setSelectedTripGroup] = useState(null);

    // Loading states
    const [loading, setLoading] = useState(false);
    const [processingIds, setProcessingIds] = useState([]);

    // Fetch data based on active tab
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

    // Reset selected trip when changing tabs
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
                        errorNoti("Invalid data");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Server error");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Cannot load collector orders");
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
                        errorNoti("Invalid data");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Server error");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Cannot load driver orders");
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
                        errorNoti("Invalid data");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Server error");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Cannot load driver failed orders");
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
                        errorNoti("Invalid data");
                        setLoading(false);
                    },
                    500: () => {
                        errorNoti("Server error");
                        setLoading(false);
                    }
                }
            );
        } catch (error) {
            errorNoti("Cannot load shipper failed orders");
            setLoading(false);
        }
    };

    // Group orders by tripId and vehicleInfo (used for driver orders)
    const groupOrdersByTrip = (orders) => {
        const grouped = orders.reduce((acc, order) => {
            const tripKey = `${order.tripId}_${order.vehicleType}_${order.vehiclePlateNumber}`;
            if (!acc[tripKey]) {
                acc[tripKey] = {
                    tripId: order.tripId,
                    tripCode: order.tripCode || `TRIP-${order.tripId.substring(0, 8)}`,
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

    const getVehicleTypeColor = (vehicleType) => {
        switch (vehicleType) {
            case 'TRUCK': return 'primary';
            case 'VAN': return 'secondary';
            case 'MOTORBIKE': return 'success';
            default: return 'default';
        }
    };

    const handleViewOrder = (order) => {
        window.location.href = `/order/detail/${order.id}`;
    };

    // Confirm single order
    const confirmSingleOrder = async (orderId) => {
        setProcessingIds(prev => [...prev, orderId]);

        try {
            await request(
                "put",
                `/smdeli/ordermanager/order/confirm-in-hub/${orderId}`,
                (res) => {
                    successNoti("Order confirmed successfully");
                    // Refresh current tab data
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
                    400: () => errorNoti("Invalid request"),
                    500: () => errorNoti("Server error occurred")
                }
            );
        } catch (error) {
            errorNoti("Error confirming order");
        } finally {
            setProcessingIds(prev => prev.filter(id => id !== orderId));
        }
    };

    // Bulk confirm orders
    const handleBulkConfirm = async (selectedIds) => {
        if (!selectedIds || selectedIds.length === 0) {
            errorNoti("Please select at least one order");
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
                    successNoti(`Successfully confirmed ${selectedIds.length} orders`);
                    // Refresh current tab data
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
                    400: () => errorNoti("Invalid request"),
                    500: () => errorNoti("Server error occurred")
                }
            );
        } catch (error) {
            errorNoti("Error confirming orders");
        } finally {
            setProcessingIds([]);
            setLoading(false);
        }
    };

    // Bulk confirm orders by trip
    const handleBulkConfirmByTrip = async (orders) => {
        const orderIds = orders.map(order => order.id);
        await handleBulkConfirm(orderIds);
    };

    // Common columns for all tabs
    const getColumns = () => [
        {
            title: "Order ID",
            field: "id",
            renderCell: (rowData) => rowData.id.substring(0, 8) + "..."
        },
        {
            title: "Sender",
            field: "senderName",
        },
        {
            title: "Recipient",
            field: "recipientName",
        },
        {
            title: "Order Type",
            field: "orderType"
        },
        {
            title: "Status",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={rowData.status}
                    color={getOrderStatusColor(rowData.status)}
                    size="small"
                />
            )
        },
        {
            title: "Total Price",
            field: "totalPrice",
            renderCell: (rowData) => `$${rowData.totalPrice?.toFixed(2) || '0.00'}`
        },
        {
            title: "Created At",
            field: "createdAt",
            renderCell: (rowData) => new Date(rowData.createdAt).toLocaleString()
        },
        {
            title: "Actions",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <Box>
                    <IconButton
                        onClick={() => handleViewOrder(rowData)}
                        color="primary"
                        title="View Order Details"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => confirmSingleOrder(rowData.id)}
                        color="success"
                        disabled={processingIds.includes(rowData.id)}
                        title="Confirm Order Into Hub"
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
                return `Orders from Collectors (${collectorOrders.length})`;
            case 1:
                return selectedTripGroup
                    ? `Orders from Trip: ${selectedTripGroup.tripCode} (${selectedTripGroup.orders.length})`
                    : `Driver Delivery Trips`;
            case 2:
                return `Driver Failed Delivery Orders (${driverFailedOrders.length})`;
            default:
                return "Orders";
        }
    };

    // Render trips table for driver orders
    const renderDriverTripsTable = () => {
        const groupedOrders = groupOrdersByTrip(driverOrders);

        const tripColumns = [
            {
                title: "Trip Code",
                field: "tripCode",
                renderCell: (rowData) => rowData.tripCode || `TRIP-${rowData.tripId.substring(0, 8)}`
            },
            {
                title: "Vehicle Type",
                field: "vehicleType",
                renderCell: (rowData) => (
                    <Chip
                        label={rowData.vehicleType}
                        color={getVehicleTypeColor(rowData.vehicleType)}
                        size="small"
                    />
                )
            },
            {
                title: "Plate Number",
                field: "vehiclePlateNumber",
            },
            {
                title: "Date",
                field: "date",
                renderCell: (rowData) => new Date(rowData.date).toLocaleDateString()
            },
            {
                title: "Orders Count",
                field: "ordersCount",
                renderCell: (rowData) => (
                    <Chip
                        label={`${rowData.orders.length} orders`}
                        color="primary"
                        size="small"
                    />
                )
            },
            {
                title: "Total Value",
                field: "totalValue",
                renderCell: (rowData) =>
                    `${rowData.orders.reduce((sum, order) => sum + (order.totalPrice || 0), 0).toFixed(2)}`
            },
            {
                title: "Actions",
                field: "actions",
                centerHeader: true,
                sorting: false,
                renderCell: (rowData) => (
                    <Box>
                        <IconButton
                            onClick={() => setSelectedTripGroup(rowData)}
                            color="primary"
                            title="View Orders"
                        >
                            <VisibilityIcon />
                        </IconButton>
                        <IconButton
                            onClick={() => handleBulkConfirmByTrip(rowData.orders)}
                            color="success"
                            disabled={loading}
                            title="Confirm All Orders"
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
                title={`Driver Delivery Trips (${groupedOrders.length} trips, ${driverOrders.length} orders)`}
                columns={tripColumns}
                data={groupedOrders}
                rowKey={(row) => `${row.tripId}_${row.vehiclePlateNumber}`}
                isLoading={loading}
                actions={[
                    {
                        iconOnClickHandle: (selectedIds, selectedRows) => {
                            // Confirm all orders from selected trips
                            const allOrderIds = selectedRows.flatMap(trip => trip.orders.map(order => order.id));
                            handleBulkConfirm(allOrderIds);
                        },
                        tooltip: "Confirm All Orders from Selected Trips",
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

    // Render orders table for selected trip
    const renderSelectedTripOrders = () => {
        return (
            <Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                    <Button
                        startIcon={<ArrowBackIcon />}
                        onClick={() => setSelectedTripGroup(null)}
                        sx={{ mr: 2 }}
                    >
                        Back to Trips
                    </Button>
                    <Box sx={{ flex: 1 }}>
                        <Typography variant="h6">
                            Trip: {selectedTripGroup.tripCode}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                            {selectedTripGroup.vehicleType} • {selectedTripGroup.vehiclePlateNumber} • {new Date(selectedTripGroup.date).toLocaleDateString()}
                        </Typography>
                    </Box>
                    <Button
                        variant="contained"
                        color="success"
                        startIcon={<CheckIcon />}
                        onClick={() => handleBulkConfirmByTrip(selectedTripGroup.orders)}
                        disabled={loading}
                    >
                        Confirm All Orders ({selectedTripGroup.orders.length})
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
                            tooltip: "Confirm Selected Orders",
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
                    <Tab label={`From Collectors (${collectorOrders.length})`} />
                    <Tab label={`From Drivers (${driverOrders.length})`} />
                    <Tab label={`Driver Failed (${driverFailedOrders.length})`} />
                    <Tab label={`Shipper Failed (${shipperFailedOrders.length})`} />
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
                            tooltip: "Confirm Selected Orders Into Hub",
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

            {!loading && activeTab === 2 && (
                <StandardTable
                    title={getTabTitle()}
                    columns={getColumns()}
                    data={getCurrentData()}
                    rowKey="id"
                    isLoading={loading}
                    actions={[
                        {
                            iconOnClickHandle: handleBulkConfirm,
                            tooltip: "Confirm Selected Orders Into Hub",
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

            {/* Instructions for each tab */}
            <Box sx={{ mt: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                {activeTab === 0 && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Collector Orders:</strong> These are orders that have been collected by collectors
                        and brought to the hub. Confirm to officially receive them into the hub inventory.
                    </Typography>
                )}
                {activeTab === 1 && !selectedTripGroup && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Driver Delivery Trips:</strong> These are trips where drivers have delivered orders to this hub.
                        Click on a trip card to view and manage the individual orders within that trip.
                        You can confirm all orders from a trip at once, or manage them individually.
                    </Typography>
                )}
                {activeTab === 1 && selectedTripGroup && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Trip Orders:</strong> These are the individual orders from the selected trip.
                        You can confirm orders individually or select multiple orders to confirm them together.
                        Use the "Back to Trips" button to return to the trips overview.
                    </Typography>
                )}
                {activeTab === 2 && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Driver Failed Delivery Orders:</strong> These are orders where delivery attempts failed
                        by drivers. Confirm to return them to hub inventory for re-processing.
                    </Typography>
                )}
                {activeTab === 3 && (
                    <Typography variant="body2" color="textSecondary">
                        <strong>Shipper Failed Delivery Orders:</strong> These are orders where delivery attempts failed
                        by shippers. Confirm to return them to hub inventory for re-processing.
                    </Typography>
                )}
            </Box>
        </div>
    );
}

export default InOrder;