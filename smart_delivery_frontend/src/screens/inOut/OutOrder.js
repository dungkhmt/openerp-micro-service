import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import AssignmentIcon from "@mui/icons-material/Assignment";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import DirectionsCarIcon from "@mui/icons-material/DirectionsCar";
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
    CircularProgress
} from "@mui/material";
import {useSelector} from "react-redux";
import {errorNoti, successNoti} from "../../utils/notification";

const OutOrder = () => {
    const hubId = useSelector((state) => state.auth.user?.hubId);

    // Tab state
    const [activeTab, setActiveTab] = useState(0);

    // Driver related states
    const [trips, setTrips] = useState([]);
    const [tripsWithVehicles, setTripsWithVehicles] = useState([]);
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [suggestedOrders, setSuggestedOrders] = useState([]);
    const [selectedOrders, setSelectedOrders] = useState([]);
    const [assignOrdersDialog, setAssignOrdersDialog] = useState(false);

    // Shipper related states
    const [shipperPickupRequests, setShipperPickupRequests] = useState([]);
    const [shipperAssignments, setShipperAssignments] = useState([]);

    // Loading states
    const [loading, setLoading] = useState(false);
    const [processingIds, setProcessingIds] = useState([]);

    // Fetch data based on active tab
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

    // Driver related functions
    useEffect(() => {
        if (trips.length > 0) {
            const tripIds = trips.map(trip => trip.id);
            fetchVehiclesForTrips(tripIds);
        }
    }, [trips]);

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
                    errorNoti("Invalid data");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Server error, please try again later");
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
                plateNumber: vehicleMap[trip.id]?.plateNumber || 'Not assigned'
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
                setLoading(false);
            },
            {
                400: () => {
                    errorNoti("Failed to fetch suggested orders");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Server error, please try again later");
                    setLoading(false);
                }
            }
        );
    };

    const confirmAssignOrders = () => {
        if (!selectedTrip) {
            errorNoti("Please select a trip");
            return;
        }

        if (selectedOrders.length === 0) {
            errorNoti("Please select orders to assign");
            return;
        }

        const orderIds = selectedOrders.map(order => order.orderId);
        setLoading(true);

        request(
            "post",
            `smdeli/middle-mile/trip/${selectedTrip.id}/assign-orders`,
            (res) => {
                successNoti("Orders assigned to driver successfully");
                setAssignOrdersDialog(false);
                setSelectedOrders([]);
                fetchTripsForToday();
                setLoading(false);
            },
            {
                400: (error) => {
                    errorNoti(error.response?.data || "Invalid data");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Server error, please try again later");
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

    // Shipper related functions
    const fetchShipperPickupRequests = () => {
        setLoading(true);
        request(
            "get",
            `smdeli/order/assign/hub/shipper/today/${hubId}`,
            (res) => {
                // Filter to only show assigned but not yet picked up orders
                const pendingPickup = res.data.filter(assignment =>
                    assignment.status !== 'COMPLETED' && assignment.numOfOrders > assignment.numOfCompleted
                );
                setShipperPickupRequests(pendingPickup);
                setLoading(false);
            },
            {
                400: () => {
                    errorNoti("Failed to fetch shipper pickup requests");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Server error, please try again later");
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
                400: () => errorNoti("Failed to fetch shipper assignments"),
                500: () => errorNoti("Server error, please try again later")
            }
        );
    };

    // Confirm individual shipper pickup
    const confirmShipperPickup = async (shipperId) => {
        setProcessingIds(prev => [...prev, shipperId]);

        try {
            await request(
                "put",
                `/smdeli/ordermanager/order/confirm-shipper-pickup/${shipperId}`,
                (res) => {
                    successNoti("Shipper pickup confirmed successfully");
                    fetchShipperPickupRequests();
                    fetchShipperAssignments();
                },
                {
                    400: () => errorNoti("Invalid request"),
                    500: () => errorNoti("Server error occurred")
                }
            );
        } catch (error) {
            errorNoti("Error confirming shipper pickup");
        } finally {
            setProcessingIds(prev => prev.filter(id => id !== shipperId));
        }
    };

    // Bulk confirm shipper pickups
    const handleBulkConfirmPickup = async (selectedIds) => {
        if (!selectedIds || selectedIds.length === 0) {
            errorNoti("Please select at least one pickup request");
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
                    successNoti(`Successfully confirmed ${selectedIds.length} pickup requests`);
                    fetchShipperPickupRequests();
                    fetchShipperAssignments();
                },
                {
                    400: () => errorNoti("Invalid request"),
                    500: () => errorNoti("Server error occurred")
                }
            );
        } catch (error) {
            errorNoti("Error confirming pickup requests");
        } finally {
            setProcessingIds([]);
            setLoading(false);
        }
    };

    const viewShipperDetails = (assignment) => {
        window.location.href = `/order/shipper/${assignment.shipperId}/assignments`;
    };

    // Utility functions
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

    // Trip table columns for driver tab
    const tripColumns = [
        {
            title: "Trip ID",
            field: "id",
            renderCell: (rowData) => rowData.id.substring(0, 8) + "..."
        },
        {
            title: "Route",
            field: "routeName",
        },
        {
            title: "Vehicle Plate",
            field: "plateNumber",
        },
        {
            title: "Status",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={rowData.status}
                    color={getTripStatusColor(rowData.status)}
                    size="small"
                />
            )
        },
        {
            title: "Date",
            field: "date",
        },
        {
            title: "Orders Count",
            field: "ordersCount",
            renderCell: (rowData) => rowData.ordersCount || 0
        },
        {
            title: "Actions",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <Box>
                    <IconButton
                        onClick={() => handleTripSelection(rowData)}
                        color="primary"
                        title="View Trip Details"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    {rowData.status === 'CAME_FIRST_STOP' && (
                        <IconButton
                            onClick={() => handleAssignOrders(rowData)}
                            color="secondary"
                            title="Assign Orders"
                        >
                            <AssignmentIcon />
                        </IconButton>
                    )}
                </Box>
            ),
        },
    ];

    // Shipper pickup request table columns
    const shipperPickupColumns = [
        {
            title: "Shipper Name",
            field: "shipperName",
        },
        {
            title: "Phone",
            field: "shipperPhone",
        },
        {
            title: "Total Orders",
            field: "numOfOrders",
        },
        {
            title: "Pending Pickup",
            field: "pendingPickup",
            renderCell: (rowData) => rowData.numOfOrders - rowData.numOfCompleted
        },
        {
            title: "Status",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={rowData.status || 'WAITING_PICKUP'}
                    color={rowData.status === 'COMPLETED' ? 'success' : 'warning'}
                    size="small"
                />
            )
        },
        {
            title: "Actions",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <Box>
                    <IconButton
                        onClick={() => viewShipperDetails(rowData)}
                        color="primary"
                        title="View Shipper Orders"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => confirmShipperPickup(rowData.shipperId)}
                        color="success"
                        disabled={processingIds.includes(rowData.shipperId)}
                        title="Confirm Pickup"
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

    // Shipper assignment table columns
    const shipperAssignmentColumns = [
        {
            title: "Shipper Name",
            field: "shipperName",
        },
        {
            title: "Total Orders",
            field: "numOfOrders",
        },
        {
            title: "Completed",
            field: "numOfCompleted",
        },
        {
            title: "Completion Rate",
            field: "completionRate",
            renderCell: (rowData) => {
                const rate = rowData.numOfOrders > 0
                    ? ((rowData.numOfCompleted / rowData.numOfOrders) * 100).toFixed(1)
                    : '0.0';
                return `${rate}%`;
            }
        },
        {
            title: "Phone",
            field: "shipperPhone",
        },
        {
            title: "Status",
            field: "status",
            renderCell: (rowData) => (
                <Chip
                    label={rowData.status || 'ACTIVE'}
                    color={rowData.status === 'COMPLETED' ? 'success' : 'primary'}
                    size="small"
                />
            )
        },
        {
            title: "Actions",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <IconButton
                    onClick={() => viewShipperDetails(rowData)}
                    color="primary"
                    title="View Shipper Details"
                >
                    <VisibilityIcon />
                </IconButton>
            ),
        },
    ];

    const handleViewOrder = (order) => {
        window.location.href = `/order/detail/${order.id}`;
    };

    return (
        <div>
            <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
                <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)}>
                    <Tab
                        label={
                            <Box display="flex" alignItems="center" gap={1}>
                                <DirectionsCarIcon />
                                {`Driver Trips (${trips.length})`}
                            </Box>
                        }
                    />
                    <Tab
                        label={
                            <Box display="flex" alignItems="center" gap={1}>
                                <LocalShippingIcon />
                                {`Shipper Pickups (${shipperPickupRequests.length})`}
                            </Box>
                        }
                    />
                </Tabs>
            </Box>

            {/* Driver Tab */}
            {activeTab === 0 && (
                <Box>
                    <StandardTable
                        title="Today's Driver Trips - Outbound Orders"
                        columns={tripColumns}
                        data={tripsWithVehicles}
                        rowKey="id"
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

                    {/* Driver Order Assignment Dialog */}
                    <Dialog
                        open={assignOrdersDialog}
                        onClose={() => setAssignOrdersDialog(false)}
                        maxWidth="lg"
                        fullWidth
                    >
                        <DialogTitle>
                            <Typography variant="h6">
                                Assign Orders to Trip {selectedTrip?.id?.substring(0, 8)}...
                            </Typography>
                            {selectedTrip && (
                                <Typography variant="body2" color="textSecondary">
                                    Route: {selectedTrip.routeName} | Vehicle: {selectedTrip.plateNumber}
                                </Typography>
                            )}
                        </DialogTitle>

                        <DialogContent>
                            {loading ? (
                                <Box display="flex" justifyContent="center" alignItems="center" p={4}>
                                    <CircularProgress />
                                    <Typography sx={{ ml: 2 }}>Loading suggested orders...</Typography>
                                </Box>
                            ) : (
                                <Box>
                                    <Typography variant="h6" gutterBottom>
                                        Suggested Orders ({suggestedOrders.length})
                                    </Typography>

                                    {selectedTrip?.vehicle && (
                                        <Card sx={{ mb: 2 }}>
                                            <CardContent>
                                                <Typography variant="subtitle1">Vehicle Capacity</Typography>
                                                <Grid container spacing={2}>
                                                    <Grid item xs={6}>
                                                        <Typography variant="body2">
                                                            Max Weight: {selectedTrip.vehicle.weightCapacity || 'N/A'} kg
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={6}>
                                                        <Typography variant="body2">
                                                            Max Volume: {selectedTrip.vehicle.volumeCapacity || 'N/A'} m³
                                                        </Typography>
                                                    </Grid>
                                                </Grid>
                                            </CardContent>
                                        </Card>
                                    )}

                                    <Typography variant="subtitle1" gutterBottom>
                                        Selected Orders: {selectedOrders.length}
                                    </Typography>

                                    <Box sx={{ maxHeight: 400, overflowY: 'auto' }}>
                                        {suggestedOrders.map((order) => {
                                            const isSelected = selectedOrders.some(selected => selected.orderId === order.orderId);
                                            return (
                                                <Card
                                                    key={order.orderId}
                                                    sx={{
                                                        mb: 1,
                                                        cursor: 'pointer',
                                                        border: isSelected ? '2px solid #1976d2' : '1px solid #e0e0e0',
                                                        '&:hover': { backgroundColor: '#f5f5f5' }
                                                    }}
                                                    onClick={() => handleOrderSelection(order)}
                                                >
                                                    <CardContent sx={{ py: 1 }}>
                                                        <Grid container spacing={2} alignItems="center">
                                                            <Grid item xs={2}>
                                                                <Typography variant="body2" fontWeight="bold">
                                                                    {order.orderCode}
                                                                </Typography>
                                                                <Chip
                                                                    label={`Priority: ${order.priority}`}
                                                                    color={getPriorityColor(order.priority)}
                                                                    size="small"
                                                                />
                                                            </Grid>
                                                            <Grid item xs={3}>
                                                                <Typography variant="body2">
                                                                    From: {order.senderName}
                                                                </Typography>
                                                                <Typography variant="body2">
                                                                    To: {order.recipientName}
                                                                </Typography>
                                                            </Grid>
                                                            <Grid item xs={2}>
                                                                <Typography variant="body2">
                                                                    Weight: {order.weight} kg
                                                                </Typography>
                                                                <Typography variant="body2">
                                                                    Volume: {order.volume} m³
                                                                </Typography>
                                                            </Grid>
                                                            <Grid item xs={3}>
                                                                <Typography variant="body2">
                                                                    Hub: {order.hubName}
                                                                </Typography>
                                                                <Typography variant="body2">
                                                                    Stop: {order.stopSequence}
                                                                </Typography>
                                                            </Grid>
                                                            <Grid item xs={2}>
                                                                {isSelected && (
                                                                    <CheckCircleIcon color="primary" />
                                                                )}
                                                            </Grid>
                                                        </Grid>
                                                    </CardContent>
                                                </Card>
                                            );
                                        })}
                                    </Box>

                                    {suggestedOrders.length === 0 && !loading && (
                                        <Typography color="textSecondary" align="center" sx={{ mt: 2 }}>
                                            No orders available for this trip route.
                                        </Typography>
                                    )}
                                </Box>
                            )}
                        </DialogContent>

                        <DialogActions>
                            <Button
                                onClick={() => setAssignOrdersDialog(false)}
                                disabled={loading}
                            >
                                Cancel
                            </Button>
                            <Button
                                onClick={confirmAssignOrders}
                                variant="contained"
                                disabled={loading || selectedOrders.length === 0}
                                startIcon={loading ? <CircularProgress size={20} /> : <AssignmentIcon />}
                            >
                                Assign {selectedOrders.length} Orders
                            </Button>
                        </DialogActions>
                    </Dialog>

                    {/* Instructions for Driver Tab */}
                    <Box sx={{ mt: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                        <Typography variant="body2" color="textSecondary">
                            <strong>Driver Trips:</strong> Review and assign orders to driver trips for middle-mile delivery.
                            Click "Assign Orders" for trips with status "CAME_FIRST_STOP" to load orders into the vehicle.
                        </Typography>
                    </Box>
                </Box>
            )}

            {/* Shipper Tab */}
            {activeTab === 1 && (
                <Box>
                    <Grid container spacing={3}>
                        {/* Shipper Pickup Requests Section */}
                        <Grid item xs={12}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        Shipper Pickup Requests ({shipperPickupRequests.length})
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                                        These are shippers waiting to pick up their assigned orders from the hub.
                                    </Typography>
                                    <StandardTable
                                        columns={shipperPickupColumns}
                                        data={shipperPickupRequests}
                                        rowKey="shipperId"
                                        isLoading={loading}
                                        actions={[
                                            {
                                                iconOnClickHandle: handleBulkConfirmPickup,
                                                tooltip: "Confirm Selected Pickup Requests",
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

                        {/* All Shipper Assignments Section */}
                        <Grid item xs={12}>
                            <StandardTable
                                title="Today's All Shipper Assignments"
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

                    {/* Instructions for Shipper Tab */}
                    <Box sx={{ mt: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                        <Typography variant="body2" color="textSecondary">
                            <strong>Shipper Pickups:</strong> Confirm pickup requests from shippers who are ready to collect
                            their assigned orders from the hub. Use the bulk confirm feature to process multiple pickup requests
                            at once, or confirm individual pickups using the action buttons.
                        </Typography>
                    </Box>
                </Box>
            )}
        </div>
    );
}

export default OutOrder;