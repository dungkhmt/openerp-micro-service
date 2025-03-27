import React, { useState, useEffect } from 'react';
import {
    Container,
    Typography,
    Box,
    Paper,
    Button,
    Tabs,
    Tab,
    Chip,
    List,
    ListItem,
    ListItemText,
    Checkbox,
    CircularProgress,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    TextField,
    Grid,
    IconButton,
    Card,
    CardContent,
    CardHeader,
    Divider,
    Alert,
    Snackbar
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import QrCodeScannerIcon from '@mui/icons-material/QrCodeScanner';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PlaceIcon from '@mui/icons-material/Place';
import InventoryIcon from '@mui/icons-material/Inventory';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';
import DeleteIcon from "@mui/icons-material/Delete";

const DriverHubOperations = () => {
    // URL parameters and navigation
    const { hubId, operationType } = useParams();
    const history = useHistory();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const tripId = queryParams.get('tripId');
    const routeVehicleId = queryParams.get('routeVehicleId');

    // Redux state
    const username = useSelector((state) => state.auth.username);

    // State variables
    const [activeTab, setActiveTab] = useState(0);
    const [loading, setLoading] = useState(true);
    const [pendingOrders, setPendingOrders] = useState([]);
    const [selectedOrders, setSelectedOrders] = useState([]);
    const [hub, setHub] = useState(null);
    const [vehicle, setVehicle] = useState(null);
    const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
    const [operationLoading, setOperationLoading] = useState(false);
    const [scannerOpen, setScannerOpen] = useState(false);
    const [scannedOrderId, setScannedOrderId] = useState("");
    const [notes, setNotes] = useState("");
    const [signatureOpen, setSignatureOpen] = useState(false);
    const [signatureData, setSignatureData] = useState(null);
    const [tripDetails, setTripDetails] = useState(null);
    const [recipientName, setRecipientName] = useState("");
    const [filterDialogOpen, setFilterDialogOpen] = useState(false);
    const [filters, setFilters] = useState({
        status: [],
        destination: ""
    });
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("info");
    const [processingComplete, setProcessingComplete] = useState(false);

    // Load data on component mount
    useEffect(() => {
        fetchHubData();
    }, [hubId, operationType, tripId]);

    // Fetch hub data
    const fetchHubData = async () => {
        setLoading(true);
        try {
            // Get hub details
            await request('get', `/smdeli/hubmanager/hub/${hubId}`, (res) => {
                setHub(res.data);
            });

            // Get driver's vehicle
            await request('get', `/smdeli/driver/vehicle`, (res) => {
                setVehicle(res.data);
            });

            // Get trip details if tripId is provided
            if (tripId) {
                await request('get', `/smdeli/driver/trips/${tripId}`, (res) => {
                    setTripDetails(res.data);
                });
            }

            // Get pending orders based on operation type
            await fetchOrderData();

        } catch (error) {
            console.error("Error loading data: ", error);
            errorNoti("Failed to load hub data");
        } finally {
            setLoading(false);
        }
    };

    // Fetch order data
    const fetchOrderData = async () => {
        try {
            let endpoint;
            if (operationType === 'pickup') {
                endpoint = `/smdeli/driver/hub/${hubId}/pending-pickups`;
            } else if (operationType === 'delivery') {
                // Since there's no specific endpoint for pending deliveries in the backend,
                // we'll use the current orders and filter them client-side
                endpoint = `/smdeli/driver/current-orders`;
            } else {
                showNotification("Invalid operation type", "error");
                return;
            }

            await request('get', endpoint, (res) => {
                let orders = res.data || [];

                // If we're in delivery mode, filter for orders that should be delivered to this hub
                if (operationType === 'delivery') {
                    orders = orders.filter(order => {
                        // Add your delivery filtering logic here based on your business rules
                        // For example, orders with DELIVERING status that match this hub
                        return order.status === 'DELIVERING';
                    });
                }

                setPendingOrders(orders);

                if (tripDetails.currentStopIndex === tripDetails.stops.length) {
                    setProcessingComplete(true);
                    showNotification("No orders to process at this hub", "info");
                } else {
                    showNotification(`${orders.length} orders ready for ${operationType}`, "info");
                }
            });
        } catch (error) {
            console.error("Error fetching orders: ", error);
            errorNoti("Failed to load orders");
        }
    };

    // Handle tab change
    const handleTabChange = (event, newValue) => {
        setActiveTab(newValue);
    };

    // Toggle order selection
    const toggleOrderSelection = (orderId) => {
        if (selectedOrders.includes(orderId)) {
            setSelectedOrders(selectedOrders.filter(id => id !== orderId));
        } else {
            setSelectedOrders([...selectedOrders, orderId]);
        }
    };

    // Select all orders
    const selectAllOrders = () => {
        if (selectedOrders.length === getFilteredOrders().length) {
            setSelectedOrders([]);
        } else {
            setSelectedOrders(getFilteredOrders().map(order => order.id));
        }
    };

    // Handle scanner input
    const handleScannerInput = (event) => {
        setScannedOrderId(event.target.value);
    };

    // Process scanned order
    const processScannedOrder = () => {
        // Check if the order ID is valid
        if (!scannedOrderId || scannedOrderId.trim() === "") {
            showNotification("Please enter a valid order ID", "warning");
            return;
        }

        const order = pendingOrders.find(o => o.id === scannedOrderId);

        if (order) {
            if (!selectedOrders.includes(scannedOrderId)) {
                setSelectedOrders([...selectedOrders, scannedOrderId]);
                showNotification(`Order added: ${scannedOrderId.substring(0, 8)}...`, "success");
            } else {
                showNotification(`Order already selected`, "info");
            }
            setScannedOrderId("");
        } else {
            showNotification(`Order not found or not eligible for ${operationType}`, "error");
        }
        setScannerOpen(false);
    };

    // Handle Enter key press in scanner input
    const handleScannerKeyPress = (event) => {
        if (event.key === 'Enter') {
            processScannedOrder();
        }
    };

    // Open confirm dialog
    const openConfirmDialog = () => {
        if (selectedOrders.length === 0) {
            showNotification("Please select at least one order", "warning");
            return;
        }

        // If delivery operation, open signature dialog instead
        if (operationType === 'delivery') {
            setSignatureOpen(true);
        } else {
            setConfirmDialogOpen(true);
        }
    };
    const isLastStop = tripDetails &&
        tripDetails.stops &&
        tripDetails.currentStopIndex === tripDetails.stops.length - 1;

    // Process orders based on operation type
    const processOrders = async () => {
        if (selectedOrders.length === 0) {
            showNotification("No orders selected", "warning");
            return;
        }

        setOperationLoading(true);

        try {
            if (operationType === 'pickup') {
                // Call pickup API
                await request(
                    'put',
                    '/smdeli/driver/pickup-orders',
                    (res) => {
                        showNotification(`${selectedOrders.length} orders picked up successfully`, "success");

                        // Update orders in state by removing the processed ones
                        setPendingOrders(pendingOrders.filter(order => !selectedOrders.includes(order.id)));

                        // Check if all orders have been processed
                        const remainingOrders = pendingOrders.filter(order => !selectedOrders.includes(order.id));
                        if (remainingOrders.length === 0) {
                            setProcessingComplete(true);
                        }

                        setSelectedOrders([]);

                        // If this is part of a trip and all orders were processed, offer to advance
                        if (tripId && remainingOrders.length === 0) {
                            if (window.confirm("All orders processed. Advance to next stop?")) {
                                handleAdvanceToNextStop();
                            }
                        }
                    },
                    {
                        401: () => showNotification("Unauthorized action", "error"),
                        400: () => showNotification("Unable to pick up orders", "error")
                    },
                    {
                        orderIds: selectedOrders,
                        tripId: tripId
                    }
                );
            } else if (operationType === 'delivery') {
                // Call deliver API with signature
                const signatureDataURL = signatureData || "no-signature-provided";

                await request(
                    'put',
                    '/smdeli/driver/deliver-orders',
                    (res) => {
                        showNotification(`${selectedOrders.length} orders delivered successfully`, "success");

                        // Update orders in state by removing the processed ones
                        setPendingOrders(pendingOrders.filter(order => !selectedOrders.includes(order.id)));

                        // Check if all orders have been processed
                        const remainingOrders = pendingOrders.filter(order => !selectedOrders.includes(order.id));
                        if (remainingOrders.length === 0) {
                            setProcessingComplete(true);
                        }

                        setSelectedOrders([]);

                        // If this is part of a trip and all orders were processed, offer to advance
                        if (tripId && remainingOrders.length === 0) {
                            if (window.confirm("All orders processed. Advance to next stop?")) {
                                handleAdvanceToNextStop();
                            }
                        }
                    },
                    {
                        401: () => showNotification("Unauthorized action", "error"),
                        400: () => showNotification("Unable to deliver orders", "error")
                    },
                    selectedOrders
                );
            }
        } catch (error) {
            console.error("Error processing orders: ", error);
            showNotification("Failed to process orders", "error");
        } finally {
            setOperationLoading(false);
            setConfirmDialogOpen(false);
            setSignatureOpen(false);
        }
    };

    const handleCompleteTrip = async () => {
        if (!tripId) return;

        try {
            setOperationLoading(true);
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/complete`,
                () => {
                    showNotification("Trip completed successfully", "success");
                    // Navigate back to the dashboard
                    history.push('/middle-mile/driver/dashboard');
                },
                {
                    401: () => showNotification("Unauthorized action", "error"),
                    400: (err) => showNotification(err.response?.data?.message || "Failed to complete trip", "error")
                }
            );
        } catch (error) {
            console.error("Error completing trip: ", error);
            showNotification("Failed to complete trip", "error");
        } finally {
            setOperationLoading(false);
        }
    };
    // Handle signature capture
    const handleSignatureCapture = () => {
        // Simulate signature capture (in a real app, use a signature pad library)
        setSignatureData("signature-data-capture");
        showNotification("Signature captured", "success");
    };

    // Clear signature
    const clearSignature = () => {
        setSignatureData(null);
    };

    // Handle advancing to next stop in a trip
    const handleAdvanceToNextStop = async () => {
        if (!tripId) return;

        try {
            setOperationLoading(true);
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/advance`,
                (res) => {
                    showNotification("Advanced to next stop", "success");
                    // Navigate back to the trip view
                    history.push(`/middle-mile/driver/route/${routeVehicleId}?tripId=${tripId}`);
                },
                {
                    401: () => showNotification("Unauthorized action", "error"),
                    400: (err) => showNotification(err.response?.data?.message || "Failed to advance to next stop", "error")
                }
            );
        } catch (error) {
            console.error("Error advancing to next stop: ", error);
            showNotification("Failed to advance to next stop", "error");
        } finally {
            setOperationLoading(false);
        }
    };

    // Apply filters to orders
    const getFilteredOrders = () => {
        return pendingOrders.filter(order => {
            // Filter by status if any statuses are selected
            if (filters.status.length > 0 && !filters.status.includes(order.status)) {
                return false;
            }

            // Filter by destination if specified
            if (filters.destination &&
                !order.recipientAddress?.toLowerCase().includes(filters.destination.toLowerCase()) &&
                !order.recipientName?.toLowerCase().includes(filters.destination.toLowerCase())) {
                return false;
            }

            return true;
        });
    };

    // Apply filters
    const applyFilters = () => {
        setFilterDialogOpen(false);
        const filteredCount = getFilteredOrders().length;
        showNotification(`Showing ${filteredCount} of ${pendingOrders.length} orders`, "info");
    };

    // Reset filters
    const resetFilters = () => {
        setFilters({
            status: [],
            destination: ""
        });
    };

    // Show notification
    const showNotification = (message, severity) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    };

    // Go back to dashboard or trip view
    const handleBack = () => {
        if (tripId) {
            // If we came from a trip, go back to trip view
            history.push(`/middle-mile/driver/route/${routeVehicleId}?tripId=${tripId}`);
        } else {
            // Otherwise go to dashboard
            history.push('/middle-mile/driver/dashboard');
        }
    };

    // Show loading indicator when fetching data
    if (loading) {
        return (
            <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" height="80vh">
                <CircularProgress size={60} sx={{ mb: 3 }} />
                <Typography variant="h6">Loading hub operations...</Typography>
            </Box>
        );
    }

    // Get filtered orders
    const filteredOrders = getFilteredOrders();

    return (
        <Container maxWidth="lg">
            <Box sx={{ mt: 3, mb: 6 }}>
                {/* Header with back button */}
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                    <IconButton onClick={handleBack} sx={{ mr: 2 }}>
                        <ArrowBackIcon />
                    </IconButton>
                    <Typography variant="h4" component="h1">
                        {operationType === 'pickup' ? 'Pickup Orders' : 'Deliver Orders'}
                    </Typography>
                </Box>

                {/* Hub Information Card */}
                {hub && (
                    <Card sx={{ mb: 4, boxShadow: 3 }}>
                        <CardHeader
                            avatar={<PlaceIcon fontSize="large" color="primary" />}
                            title={
                                <Typography variant="h5">{hub.name} ({hub.code})</Typography>
                            }
                            subheader={hub.address}
                        />
                        <CardContent>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={4}>
                                    <Typography variant="body2" color="text.secondary">Operation Type:</Typography>
                                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                        {operationType === 'pickup' ? 'Picking Up Packages' : 'Delivering Packages'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <Typography variant="body2" color="text.secondary">Vehicle:</Typography>
                                    <Typography variant="body1">
                                        {vehicle?.plateNumber} ({vehicle?.vehicleType})
                                    </Typography>
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <Typography variant="body2" color="text.secondary">Pending Orders:</Typography>
                                    <Typography variant="body1" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
                                        {pendingOrders.length} {operationType === 'pickup' ? 'to pick up' : 'to deliver'}
                                    </Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                )}

                {/* Trip Information (if part of a trip) */}
                {tripDetails && (

// Then let's update the Trip Information card to show the appropriate button
                    <Card sx={{ mb: 4, boxShadow: 3, bgcolor: 'primary.light', color: 'white' }}>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <LocalShippingIcon sx={{ mr: 2, fontSize: 28 }} />
                                <Typography variant="h6">
                                    Active Trip - {isLastStop ? 'Final Stop' : `Stop ${tripDetails.currentStopIndex + 1} of ${tripDetails.stops?.length}`}
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2, borderColor: 'rgba(255,255,255,0.3)' }} />

                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <Typography variant="body2" sx={{ opacity: 0.8 }}>Current Stop:</Typography>
                                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                                        {tripDetails.stops?.[tripDetails.currentStopIndex]?.hubName || 'Unknown'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <Typography variant="body2" sx={{ opacity: 0.8 }}>
                                        {isLastStop ? 'Final Destination' : 'Next Stop:'}
                                    </Typography>
                                    <Typography variant="body1">
                                        {isLastStop
                                            ? 'End of Route'
                                            : tripDetails.stops?.[tripDetails.currentStopIndex + 1]?.hubName || 'End of Route'}
                                    </Typography>
                                </Grid>
                            </Grid>

                            <Button
                                variant="contained"
                                color={isLastStop ? "success" : "secondary"}
                                sx={{ mt: 2 }}
                                onClick={isLastStop ? handleCompleteTrip : handleAdvanceToNextStop}
                            >
                                {isLastStop ? 'Complete Trip' : 'Advance to Next Stop'}
                            </Button>
                        </CardContent>
                    </Card>

                )}

                {processingComplete && (
                    <Alert
                        severity="success"
                        sx={{ mb: 3 }}
                        action={
                            tripId && (
                                <Button
                                    color="inherit"
                                    size="small"
                                    onClick={isLastStop ? handleCompleteTrip : handleAdvanceToNextStop}
                                >
                                    {isLastStop ? 'COMPLETE TRIP' : 'NEXT STOP'}
                                </Button>
                            )
                        }
                    >
                        <Typography variant="subtitle1">
                            All orders have been processed at this hub
                        </Typography>
                        {tripId
                            ? isLastStop
                                ? "You have reached the final stop. You can now complete your trip."
                                : "You can now proceed to the next stop in your route."
                            : "You can now return to your dashboard."
                        }
                    </Alert>
                )}

                {/* Orders Section */}
                <Card sx={{ mb: 3, boxShadow: 2 }}>
                    <CardContent sx={{ pb: 1 }}>
                        <Tabs value={activeTab} onChange={handleTabChange} sx={{ mb: 2 }}>
                            <Tab label={`Pending Orders (${filteredOrders.length})`} />
                            <Tab label={`Selected (${selectedOrders.length})`} disabled={selectedOrders.length === 0} />
                        </Tabs>

                        {activeTab === 0 && (
                            <>
                                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                                    <Box>
                                        <Button
                                            variant="outlined"
                                            startIcon={<DeleteIcon />}
                                            onClick={() => setFilterDialogOpen(true)}
                                            sx={{ mr: 1 }}
                                        >
                                            Filter
                                        </Button>

                                        <Button
                                            variant="outlined"
                                            onClick={selectAllOrders}
                                            disabled={filteredOrders.length === 0}
                                        >
                                            {selectedOrders.length === filteredOrders.length && filteredOrders.length > 0 ? 'Unselect All' : 'Select All'}
                                        </Button>
                                    </Box>

                                    <Button
                                        variant="outlined"
                                        startIcon={<QrCodeScannerIcon />}
                                        onClick={() => setScannerOpen(true)}
                                        disabled={pendingOrders.length === 0}
                                    >
                                        Scan Order
                                    </Button>
                                </Box>

                                {filteredOrders.length === 0 ? (
                                    <Paper sx={{ p: 4, textAlign: 'center', bgcolor: 'background.default' }}>
                                        <InventoryIcon color="disabled" sx={{ fontSize: 48, mb: 2, opacity: 0.6 }} />
                                        <Typography variant="h6" color="text.secondary">
                                            No pending orders found
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {pendingOrders.length > 0 ? 'Try changing your filters' : 'All orders have been processed'}
                                        </Typography>

                                        {pendingOrders.length === 0 && tripId && (
                                            <Button
                                                variant="contained"
                                                color={isLastStop ? "success" : "primary"}
                                                sx={{ mt: 3 }}
                                                onClick={isLastStop ? handleCompleteTrip : handleAdvanceToNextStop}
                                            >
                                                {isLastStop ? 'Complete Trip' : 'Proceed to Next Stop'}
                                            </Button>
                                        )}
                                    </Paper>
                                ) : (
                                    <List sx={{ maxHeight: '60vh', overflow: 'auto' }}>
                                        {filteredOrders.map((order) => (
                                            <Paper
                                                key={order.id}
                                                sx={{
                                                    mb: 2,
                                                    boxShadow: 1,
                                                    borderLeft: selectedOrders.includes(order.id) ?
                                                        '5px solid #2196f3' : 'none'
                                                }}
                                            >
                                                <ListItem
                                                    button
                                                    onClick={() => toggleOrderSelection(order.id)}
                                                    secondaryAction={
                                                        <Checkbox
                                                            edge="end"
                                                            checked={selectedOrders.includes(order.id)}
                                                            onChange={() => toggleOrderSelection(order.id)}
                                                        />
                                                    }
                                                >
                                                    <ListItemText
                                                        primary={
                                                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', pr: 4 }}>
                                                                <Typography variant="subtitle1">
                                                                    #{order.id.substring(0, 8)}...
                                                                </Typography>
                                                                <Chip
                                                                    label={order.status}
                                                                    color={
                                                                        order.status === 'COLLECTED_HUB' ? 'info' :
                                                                            order.status === 'DELIVERING' ? 'warning' : 'default'
                                                                    }
                                                                    size="small"
                                                                />
                                                            </Box>
                                                        }
                                                        secondary={
                                                            <Grid container spacing={1} sx={{ mt: 0.5 }}>
                                                                <Grid item xs={12} sm={6}>
                                                                    <Typography variant="caption" color="text.secondary" component="span">
                                                                        From:
                                                                    </Typography>
                                                                    <Typography variant="body2" component="span" sx={{ ml: 0.5 }}>
                                                                        {order.senderName || "Unknown"}
                                                                    </Typography>
                                                                </Grid>
                                                                <Grid item xs={12} sm={6}>
                                                                    <Typography variant="caption" color="text.secondary" component="span">
                                                                        To:
                                                                    </Typography>
                                                                    <Typography variant="body2" component="span" sx={{ ml: 0.5 }}>
                                                                        {order.recipientName || "Unknown"}
                                                                    </Typography>
                                                                </Grid>
                                                                {order.createdAt && (
                                                                    <Grid item xs={12}>
                                                                        <Typography variant="caption" color="text.secondary">
                                                                            Created: {new Date(order.createdAt).toLocaleString()}
                                                                        </Typography>
                                                                    </Grid>
                                                                )}
                                                            </Grid>
                                                        }
                                                    />
                                                </ListItem>
                                            </Paper>
                                        ))}
                                    </List>
                                )}
                            </>
                        )}

                        {activeTab === 1 && (
                            <>
                                <Box sx={{ mb: 2 }}>
                                    <Alert severity="info">
                                        {selectedOrders.length} orders selected for {operationType === 'pickup' ? 'pickup' : 'delivery'}
                                    </Alert>
                                </Box>

                                <List sx={{ maxHeight: '60vh', overflow: 'auto' }}>
                                    {pendingOrders
                                        .filter(order => selectedOrders.includes(order.id))
                                        .map(order => (
                                            <Paper key={order.id} sx={{ mb: 2, boxShadow: 1, borderLeft: '5px solid #2196f3' }}>
                                                <ListItem
                                                    secondaryAction={
                                                        <IconButton edge="end" onClick={() => toggleOrderSelection(order.id)}>
                                                            <Checkbox checked={true} />
                                                        </IconButton>
                                                    }
                                                >
                                                    <ListItemText
                                                        primary={
                                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                                <Typography variant="subtitle1">
                                                                    #{order.id.substring(0, 8)}...
                                                                </Typography>
                                                                <Chip
                                                                    label={order.status}
                                                                    color={
                                                                        order.status === 'COLLECTED_HUB' ? 'info' :
                                                                            order.status === 'DELIVERING' ? 'warning' : 'default'
                                                                    }
                                                                    size="small"
                                                                    sx={{ ml: 1 }}
                                                                />
                                                            </Box>
                                                        }
                                                        secondary={
                                                            <Grid container spacing={1} sx={{ mt: 0.5 }}>
                                                                <Grid item xs={12} sm={6}>
                                                                    <Typography variant="caption" color="text.secondary" component="span">
                                                                        From:
                                                                    </Typography>
                                                                    <Typography variant="body2" component="span" sx={{ ml: 0.5 }}>
                                                                        {order.senderName || "Unknown"}
                                                                    </Typography>
                                                                </Grid>
                                                                <Grid item xs={12} sm={6}>
                                                                    <Typography variant="caption" color="text.secondary" component="span">
                                                                        To:
                                                                    </Typography>
                                                                    <Typography variant="body2" component="span" sx={{ ml: 0.5 }}>
                                                                        {order.recipientName || "Unknown"}
                                                                    </Typography>
                                                                </Grid>
                                                                {order.createdAt && (
                                                                    <Grid item xs={12}>
                                                                        <Typography variant="caption" color="text.secondary">
                                                                            Created: {new Date(order.createdAt).toLocaleString()}
                                                                        </Typography>
                                                                    </Grid>
                                                                )}
                                                            </Grid>
                                                        }
                                                    />
                                                </ListItem>
                                            </Paper>
                                        ))}
                                </List>
                            </>
                        )}
                    </CardContent>
                </Card>

                {/* Action Button - Only show if there are pending orders */}
                {!processingComplete && pendingOrders.length > 0 && (
                    <Box sx={{ position: 'fixed', bottom: 24, left: 0, right: 0, textAlign: 'center', zIndex: 100 }}>
                        <Button
                            variant="contained"
                            color="primary"
                            size="large"
                            disabled={selectedOrders.length === 0 || operationLoading}
                            onClick={openConfirmDialog}
                            startIcon={operationType === 'pickup' ? <LocalShippingIcon /> : <CheckCircleIcon />}
                            sx={{
                                px: 6,
                                py: 1.5,
                                borderRadius: 8,
                                boxShadow: 4
                            }}
                        >
                            {operationLoading ? (
                                <CircularProgress size={24} color="inherit" />
                            ) : (
                                operationType === 'pickup'
                                    ? `Pick Up ${selectedOrders.length} Orders`
                                    : `Deliver ${selectedOrders.length} Orders`
                            )}
                        </Button>
                    </Box>
                )}

                {/* Confirmation Dialog */}
                <Dialog
                    open={confirmDialogOpen}
                    onClose={() => !operationLoading && setConfirmDialogOpen(false)}
                    maxWidth="sm"
                    fullWidth
                >
                    <DialogTitle>
                        {operationType === 'pickup' ? 'Confirm Pickup' : 'Confirm Delivery'}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            {operationType === 'pickup'
                                ? `Are you sure you want to pick up ${selectedOrders.length} orders from ${hub?.name}?`
                                : `Are you sure you want to deliver ${selectedOrders.length} orders to ${hub?.name}?`
                            }
                        </DialogContentText>

                        <Box sx={{ mt: 3, mb: 2 }}>
                            <Typography variant="subtitle2" gutterBottom>
                                Selected Orders:
                            </Typography>
                            <Paper variant="outlined" sx={{ p: 1, maxHeight: 120, overflow: 'auto' }}>
                                {selectedOrders.map(id => {
                                    const order = pendingOrders.find(o => o.id === id);
                                    return order ? (
                                        <Chip
                                            key={id}
                                            label={`#${id.substring(0, 8)}`}
                                            size="small"
                                            sx={{ m: 0.5 }}
                                        />
                                    ) : null;
                                })}
                            </Paper>
                        </Box>

                        <TextField
                            margin="dense"
                            label="Notes (optional)"
                            fullWidth
                            multiline
                            rows={3}
                            value={notes}
                            onChange={(e) => setNotes(e.target.value)}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={() => setConfirmDialogOpen(false)}
                            disabled={operationLoading}
                        >
                            Cancel
                        </Button>
                        <Button
                            onClick={processOrders}
                            variant="contained"
                            color="primary"
                            disabled={operationLoading}
                        >
                            {operationLoading ? (
                                <CircularProgress size={24} color="inherit" />
                            ) : (
                                'Confirm'
                            )}
                        </Button>
                    </DialogActions>
                </Dialog>

                {/* Signature Dialog for Delivery */}
                <Dialog
                    open={signatureOpen}
                    onClose={() => !operationLoading && setSignatureOpen(false)}
                    maxWidth="md"
                    fullWidth
                >
                    <DialogTitle>Delivery Confirmation Signature</DialogTitle>
                    <DialogContent>
                        <DialogContentText sx={{ mb: 2 }}>
                            Please obtain a signature to confirm delivery of {selectedOrders.length} orders to {hub?.name}.
                        </DialogContentText>

                        <Grid container spacing={3}>
                            <Grid item xs={12} md={7}>
                                <Typography variant="subtitle2" gutterBottom>
                                    Signature Area:
                                </Typography>
                                <Paper
                                    variant="outlined"
                                    sx={{
                                        height: 200,
                                        width: '100%',
                                        border: '1px solid #ccc',
                                        position: 'relative'
                                    }}
                                >
                                    {/* This simulates a signature pad component */}
                                    {/* In a real implementation, you would use a proper signature component */}
                                    <Box
                                        sx={{
                                            position: 'absolute',
                                            top: 0,
                                            left: 0,
                                            width: '100%',
                                            height: '100%',
                                            backgroundColor: '#fafafa',
                                            cursor: 'crosshair',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center'
                                        }}
                                        onClick={handleSignatureCapture}
                                    >
                                        {signatureData ? (
                                            <Typography variant="h6" color="primary">
                                                ✓ Signature Captured
                                            </Typography>
                                        ) : (
                                            <Typography color="text.secondary">
                                                Click here to sign
                                            </Typography>
                                        )}
                                    </Box>
                                </Paper>

                                <Button
                                    variant="outlined"
                                    size="small"
                                    sx={{ mt: 1 }}
                                    onClick={clearSignature}
                                    disabled={!signatureData}
                                >
                                    Clear Signature
                                </Button>
                            </Grid>

                            <Grid item xs={12} md={5}>
                                <Typography variant="subtitle2" gutterBottom>
                                    Delivery Details:
                                </Typography>

                                <TextField
                                    margin="dense"
                                    label="Recipient Name"
                                    fullWidth
                                    required
                                    value={recipientName}
                                    onChange={(e) => setRecipientName(e.target.value)}
                                    sx={{ mb: 2 }}
                                />

                                <TextField
                                    margin="dense"
                                    label="Notes (optional)"
                                    fullWidth
                                    multiline
                                    rows={3}
                                    value={notes}
                                    onChange={(e) => setNotes(e.target.value)}
                                />
                            </Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={() => setSignatureOpen(false)}
                            disabled={operationLoading}
                        >
                            Cancel
                        </Button>
                        <Button
                            onClick={processOrders}
                            variant="contained"
                            color="primary"
                            disabled={operationLoading || !signatureData || !recipientName}
                        >
                            {operationLoading ? (
                                <CircularProgress size={24} color="inherit" />
                            ) : (
                                'Confirm Delivery'
                            )}
                        </Button>
                    </DialogActions>
                </Dialog>

                {/* Scanner Dialog */}
                <Dialog
                    open={scannerOpen}
                    onClose={() => setScannerOpen(false)}
                    maxWidth="sm"
                    fullWidth
                >
                    <DialogTitle>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <QrCodeScannerIcon sx={{ mr: 1 }} />
                            Scan Order QR Code
                        </Box>
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText sx={{ mb: 2 }}>
                            Enter or scan the order ID using a barcode scanner
                        </DialogContentText>
                        <TextField
                            autoFocus
                            margin="dense"
                            label="Order ID"
                            fullWidth
                            variant="outlined"
                            value={scannedOrderId}
                            onChange={handleScannerInput}
                            onKeyPress={handleScannerKeyPress}
                            sx={{ mb: 2 }}
                        />
                        <Alert severity="info" sx={{ mb: 2 }}>
                            Make sure the scanner is configured to emit an Enter key after scanning
                        </Alert>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setScannerOpen(false)}>Cancel</Button>
                        <Button
                            onClick={processScannedOrder}
                            variant="contained"
                            color="primary"
                            disabled={!scannedOrderId}
                        >
                            Add Order
                        </Button>
                    </DialogActions>
                </Dialog>

                {/* Filter Dialog */}
                <Dialog
                    open={filterDialogOpen}
                    onClose={() => setFilterDialogOpen(false)}
                    maxWidth="sm"
                    fullWidth
                >
                    <DialogTitle>Filter Orders</DialogTitle>
                    <DialogContent>
                        <Typography variant="subtitle2" gutterBottom sx={{ mt: 1 }}>
                            Status:
                        </Typography>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 3 }}>
                            {['COLLECTED_HUB', 'DELIVERING', 'PENDING'].map(status => (
                                <Chip
                                    key={status}
                                    label={status}
                                    color={filters.status.includes(status) ? 'primary' : 'default'}
                                    onClick={() => {
                                        if (filters.status.includes(status)) {
                                            setFilters({
                                                ...filters,
                                                status: filters.status.filter(s => s !== status)
                                            });
                                        } else {
                                            setFilters({
                                                ...filters,
                                                status: [...filters.status, status]
                                            });
                                        }
                                    }}
                                />
                            ))}
                        </Box>

                        <Typography variant="subtitle2" gutterBottom>
                            Destination:
                        </Typography>
                        <TextField
                            fullWidth
                            placeholder="Search by destination address or recipient name"
                            value={filters.destination}
                            onChange={(e) => setFilters({
                                ...filters,
                                destination: e.target.value
                            })}
                            sx={{ mb: 2 }}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={resetFilters} color="inherit">
                            Reset Filters
                        </Button>
                        <Button onClick={() => setFilterDialogOpen(false)}>
                            Cancel
                        </Button>
                        <Button onClick={applyFilters} variant="contained" color="primary">
                            Apply Filters
                        </Button>
                    </DialogActions>
                </Dialog>

                {/* Snackbar for notifications */}
                <Snackbar
                    open={snackbarOpen}
                    autoHideDuration={4000}
                    onClose={() => setSnackbarOpen(false)}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
                >
                    <Alert
                        onClose={() => setSnackbarOpen(false)}
                        severity={snackbarSeverity}
                        sx={{ width: '100%' }}
                    >
                        {snackbarMessage}
                    </Alert>
                </Snackbar>
            </Box>
        </Container>
    );
};

export default DriverHubOperations;