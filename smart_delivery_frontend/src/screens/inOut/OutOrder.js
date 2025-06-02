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

    // Tab state
    const [activeTab, setActiveTab] = useState(0);

    // Driver related states
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

    // Shipper related states
    const [shipperPickupRequests, setShipperPickupRequests] = useState([]);
    const [shipperAssignments, setShipperAssignments] = useState([]);

    // Loading states
    const [loading, setLoading] = useState(false);
    const [processingIds, setProcessingIds] = useState([]);

    // Filter orders based on search query
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

    // Separate trips when tripsWithVehicles updates
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

        // Check capacity limits
        const totalWeight = selectedOrders.reduce((sum, order) => sum + (order.weight || 0), 0);
        const totalVolume = selectedOrders.reduce((sum, order) => sum + (order.volume || 0), 0);

        const maxWeight = selectedTrip.vehicle?.weightCapacity || 0;
        const maxVolume = selectedTrip.vehicle?.volumeCapacity || 0;

        if (maxWeight > 0 && totalWeight > maxWeight) {
            errorNoti(`Total weight (${totalWeight.toFixed(1)} kg) exceeds vehicle capacity (${maxWeight} kg)`);
            return;
        }

        if (maxVolume > 0 && totalVolume > maxVolume) {
            errorNoti(`Total volume (${totalVolume.toFixed(2)} m³) exceeds vehicle capacity (${maxVolume} m³)`);
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
                setSearchQuery('');
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

    // Trip table columns for CAME_FIRST_STOP trips
    const cameFirstStopColumns = [
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
                    <IconButton
                        onClick={() => handleAssignOrders(rowData)}
                        color="secondary"
                        title="Assign Orders"
                    >
                        <AssignmentIcon />
                    </IconButton>
                </Box>
            ),
        },
    ];

    // Trip table columns for other trips
    const otherTripColumns = [
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

    // Calculate capacity warnings
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
                    {/* Trips Ready for Order Assignment */}
                    {cameFirstStopTrips.length > 0 && (
                        <>
                            <StandardTable
                                title={ <Box display="flex" alignItems="center" gap={1} mb={2}>
                                    <AssignmentIcon color="warning" />
                                    <Typography variant="h6" color="warning.main">
                                        Trip đã đến hub - {cameFirstStopTrips.length}
                                    </Typography>
                                </Box>}
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

                    {/* All Other Trips */}
                    <StandardTable
                        title="Các trip khác sẽ khởi hành từ hub"
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

                        <DialogContent sx={{ px: 3, py: 1 }}>
                            {loading ? (
                                <Box display="flex" justifyContent="center" alignItems="center" p={4}>
                                    <CircularProgress size={24} />
                                    <Typography sx={{ ml: 2 }}>Loading suggested orders...</Typography>
                                </Box>
                            ) : (
                                <Box>
                                    {/* Vehicle Capacity Info */}
                                    {selectedTrip?.vehicle && (
                                        <Card sx={{ mb: 2 }}>
                                            <CardContent sx={{ py: 1, px: 2 }}>
                                                <Typography variant="body2" fontWeight="bold">Vehicle Capacity</Typography>
                                                <Grid container spacing={2}>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>
                                                            Max Weight: {selectedTrip.vehicle.weightCapacity || 'N/A'} kg
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>
                                                            Max Volume: {selectedTrip.vehicle.volumeCapacity || 'N/A'} m³
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>
                                                            Selected: {selectedOrders.length} orders
                                                        </Typography>
                                                    </Grid>
                                                </Grid>
                                            </CardContent>
                                        </Card>
                                    )}

                                    {/* Search and Filter Section */}
                                    <Grid container spacing={1} alignItems="center" sx={{ mb: 2 }}>
                                        <Grid item xs={8}>
                                            <TextField
                                                fullWidth
                                                placeholder="Search orders..."
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
                                                    {selectedOrders.length === filteredOrders.length && filteredOrders.length > 0 ? 'Deselect All' : 'Select All'}
                                                </Button>
                                            </Stack>
                                        </Grid>
                                    </Grid>

                                    <Typography variant="body2" fontWeight="bold" sx={{ mb: 1 }}>
                                        Suggested Orders ({filteredOrders.length})
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
                                                            {/* Order Code and Priority */}
                                                            <Grid item xs={2}>
                                                                <Typography variant="body2" fontWeight="bold" color="primary.main">
                                                                    {order.orderCode}
                                                                </Typography>
                                                                <Chip
                                                                    label={`"Độ ưu tiên:"  {order.priority}`}
                                                                    color={getPriorityColor(order.priority)}
                                                                    size="small"
                                                                    sx={{ fontSize: '0.65rem', height: '16px' }}
                                                                />
                                                            </Grid>

                                                            {/* Sender and Recipient */}
                                                            <Grid item xs={3}>
                                                                <Typography variant="caption" color="textSecondary" sx={{ fontSize: '0.7rem' }}>
                                                                    FROM
                                                                </Typography>
                                                                <Typography variant="body2" fontWeight="medium" noWrap sx={{ fontSize: '0.8rem' }}>
                                                                    {order.senderName}
                                                                </Typography>
                                                                <Typography variant="caption" color="textSecondary" sx={{ fontSize: '0.7rem' }}>
                                                                    TO
                                                                </Typography>
                                                                <Typography variant="body2" fontWeight="medium" noWrap sx={{ fontSize: '0.8rem' }}>
                                                                    {order.recipientName}
                                                                </Typography>
                                                            </Grid>

                                                            {/* Weight and Volume */}
                                                            <Grid item xs={2}>
                                                                <Typography variant="caption" color="textSecondary">
                                                                    Weight: {order.weight} kg
                                                                </Typography>
                                                                <br />
                                                                <Typography variant="caption" color="textSecondary">
                                                                    Volume: {order.volume} m³
                                                                </Typography>
                                                            </Grid>

                                                            {/* Hub and Stop */}
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

                                                            {/* Status */}
                                                            <Grid item xs={2}>
                                                                <Chip
                                                                    label={order.currentStatus}
                                                                    color={getOrderStatusColor(order.currentStatus)}
                                                                    size="small"
                                                                    sx={{ fontSize: '0.8rem', height: '16px' }}
                                                                />
                                                            </Grid>

                                                            {/* Selection Indicator */}
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

                                    {/* Capacity Warning */}
                                    {(() => {
                                        const warning = getCapacityWarning();
                                        if (!warning) return null;

                                        return (
                                            <Card sx={{ mt: 1, bgcolor: 'error.50', border: '1px solid', borderColor: 'error.main' }}>
                                                <CardContent sx={{ py: 1, px: 2 }}>
                                                    <Typography variant="body2" fontWeight="bold" color="error.main" gutterBottom>
                                                        Capacity Exceeded
                                                    </Typography>
                                                    {warning.weightExceeded && (
                                                        <Typography variant="caption" color="error.main">
                                                            Weight: {warning.totalWeight.toFixed(1)} kg / {warning.maxWeight} kg (Exceeded by {(warning.totalWeight - warning.maxWeight).toFixed(1)} kg)
                                                        </Typography>
                                                    )}
                                                    {warning.volumeExceeded && (
                                                        <Typography variant="caption" color="error.main" sx={{ display: 'block' }}>
                                                            Volume: {warning.totalVolume.toFixed(2)} m³ / {warning.maxVolume} m³ (Exceeded by {(warning.totalVolume - warning.maxVolume).toFixed(2)} m³)
                                                        </Typography>
                                                    )}
                                                </CardContent>
                                            </Card>
                                        );
                                    })()}

                                    {/* Summary Section */}
                                    {selectedOrders.length > 0 && (
                                        <Card sx={{ mt: 2, bgcolor: 'success.50', border: '1px solid', borderColor: 'success.200' }}>
                                            <CardContent sx={{ py: 1, px: 2 }}>
                                                <Typography variant="body2" fontWeight="bold" color="success.main" gutterBottom>
                                                    Selection Summary
                                                </Typography>
                                                <Grid container spacing={1}>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Orders</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {selectedOrders.length}
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Total Weight</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {selectedOrders.reduce((sum, order) => sum + (order.weight || 0), 0).toFixed(1)} kg
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Total Volume</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {selectedOrders.reduce((sum, order) => sum + (order.volume || 0), 0).toFixed(2)} m³
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={3}>
                                                        <Typography variant="caption" color="textSecondary">Avg Priority</Typography>
                                                        <Typography variant="body2" fontWeight="bold" color="success.main">
                                                            {(selectedOrders.reduce((sum, order) => sum + (order.priority || 0), 0) / selectedOrders.length).toFixed(1)}
                                                        </Typography>
                                                    </Grid>
                                                </Grid>
                                            </CardContent>
                                        </Card>
                                    )}

                                    {/* Empty State */}
                                    {filteredOrders.length === 0 && !loading && (
                                        <Typography color="textSecondary" align="center" sx={{ mt: 2, fontSize: '0.9rem' }}>
                                            {searchQuery ? 'No orders match your search' : 'No orders available for this trip route.'}
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
                                Cancel
                            </Button>
                            <Button
                                onClick={confirmAssignOrders}
                                variant="contained"
                                disabled={loading || selectedOrders.length === 0}
                                startIcon={loading ? <CircularProgress size={20} /> : <AssignmentIcon />}
                                size="large"
                                sx={{ minWidth: 200 }}
                            >
                                {loading ? 'Assigning...' : `Assign ${selectedOrders.length} Orders`}
                            </Button>
                        </DialogActions>
                    </Dialog>

                    {/* Instructions for Driver Tab */}
                    <Box sx={{ mt: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
                        <Typography variant="body2" color="textSecondary">
                            <strong>Driver Trips:</strong> Trips with status "CAME_FIRST_STOP" are displayed in the priority section above
                            and are ready for order assignment. Use the "Assign Orders" button to load orders into these vehicles.
                            All other trips are shown in the table below for monitoring purposes.
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
};

export default OutOrder;