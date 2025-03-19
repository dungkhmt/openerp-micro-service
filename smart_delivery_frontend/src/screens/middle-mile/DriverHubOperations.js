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
    FormControlLabel,
    Grid,
    IconButton,
    Card,
    CardContent,
    CardActions
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import QrCodeScannerIcon from '@mui/icons-material/QrCodeScanner';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import { useParams, useHistory } from 'react-router-dom';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';

/**
 * Component for handling hub-to-hub transfer operations:
 * 1. Pickup confirmation: Driver confirms packages picked up from origin hub
 * 2. Delivery confirmation: Driver confirms packages delivered to destination hub
 */
const DriverHubOperations = () => {
    const { hubId, operationType } = useParams();
    const history = useHistory();
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

    // Get tab title based on direction
    const getTabLabel = () => {
        if (operationType === 'pickup') {
            return "Pending Pickup Orders";
        } else if (operationType === 'delivery') {
            return "Orders to Deliver";
        }
        return "Orders";
    };

    // Load data on component mount
    useEffect(() => {
        const fetchData = async () => {
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

                // Get pending orders based on direction
                if (operationType === 'pickup') {
                    await request('get', `/smdeli/driver/hub/${hubId}/pending-pickups`, (res) => {
                        setPendingOrders(res.data || []);
                    });
                } else if (operationType === 'delivery') {
                    await request('get', `/smdeli/driver/current-orders`, (res) => {
                        // Filter orders with destination matching this hub
                        const ordersForHub = res.data.filter(order => order.finalHubId === hubId);
                        setPendingOrders(ordersForHub || []);
                    });
                }

                setLoading(false);
            } catch (error) {
                console.error("Error loading data: ", error);
                errorNoti("Failed to load orders data");
                setLoading(false);
            }
        };

        fetchData();
    }, [hubId, operationType]);

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
        if (selectedOrders.length === pendingOrders.length) {
            setSelectedOrders([]);
        } else {
            setSelectedOrders(pendingOrders.map(order => order.id));
        }
    };

    // Handle scanner input
    const handleScannerInput = (event) => {
        setScannedOrderId(event.target.value);
    };

    // Process scanned order
    const processScannedOrder = () => {
        const order = pendingOrders.find(o => o.id === scannedOrderId);
        if (order) {
            if (!selectedOrders.includes(scannedOrderId)) {
                setSelectedOrders([...selectedOrders, scannedOrderId]);
            }
            setScannedOrderId("");
            successNoti(`Order ${scannedOrderId} added to selection`);
        } else {
            errorNoti(`Order ${scannedOrderId} not found in pending orders`);
        }
        setScannerOpen(false);
    };

    // Open confirm dialog
    const openConfirmDialog = () => {
        if (selectedOrders.length === 0) {
            errorNoti("Please select at least one order");
            return;
        }
        setConfirmDialogOpen(true);
    };

    // Process orders based on direction
    const processOrders = async () => {
        if (selectedOrders.length === 0) {
            errorNoti("No orders selected");
            return;
        }

        setOperationLoading(true);

        try {
            if (operationType === 'pickup') {
                // Call pickup API
                await request(
                    'put',
                    '/smdeli/driver/pickup-orders',
                    () => {
                        successNoti(`${selectedOrders.length} orders picked up successfully`);
                        // Update orders in state
                        setPendingOrders(pendingOrders.filter(order => !selectedOrders.includes(order.id)));
                        setSelectedOrders([]);
                    },
                    {
                        401: () => errorNoti("Unauthorized action"),
                        400: () => errorNoti("Unable to pick up orders")
                    },
                    selectedOrders
                );
            } else if (operationType === 'delivery') {
                // Call deliver API
                await request(
                    'put',
                    '/smdeli/driver/deliver-orders',
                    () => {
                        successNoti(`${selectedOrders.length} orders delivered successfully`);
                        // Update orders in state
                        setPendingOrders(pendingOrders.filter(order => !selectedOrders.includes(order.id)));
                        setSelectedOrders([]);
                    },
                    {
                        401: () => errorNoti("Unauthorized action"),
                        400: () => errorNoti("Unable to deliver orders")
                    },
                    selectedOrders
                );
            }
        } catch (error) {
            console.error("Error processing orders: ", error);
            errorNoti("Failed to process orders");
        } finally {
            setOperationLoading(false);
            setConfirmDialogOpen(false);
        }
    };

    // Go back to dashboard
    const handleBack = () => {
        history.push('/middle-mile/driver/dashboard');
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="80vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container maxWidth="md">
            <Box sx={{ mt: 2, mb: 4 }}>
                <IconButton onClick={handleBack}>
                    <ArrowBackIcon />
                </IconButton>

                <Typography variant="h4" component="h1" gutterBottom>
                    {operationType === 'pickup' ? 'Pickup from Hub' : 'Deliver to Hub'}
                </Typography>

                {hub && (
                    <Paper sx={{ p: 2, mb: 3 }}>
                        <Typography variant="h6">
                            {hub.name} ({hub.code})
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            {hub.address}
                        </Typography>
                    </Paper>
                )}

                <Tabs value={activeTab} onChange={handleTabChange} sx={{ mb: 2 }}>
                    <Tab label={getTabLabel()} />
                    <Tab label="Selected" disabled={selectedOrders.length === 0} />
                </Tabs>

                {activeTab === 0 && (
                    <>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={selectAllOrders}
                            >
                                {selectedOrders.length === pendingOrders.length ? 'Unselect All' : 'Select All'}
                            </Button>

                            <Button
                                variant="outlined"
                                startIcon={<QrCodeScannerIcon />}
                                onClick={() => setScannerOpen(true)}
                            >
                                Scan Order
                            </Button>
                        </Box>

                        {pendingOrders.length === 0 ? (
                            <Paper sx={{ p: 3, textAlign: 'center' }}>
                                <Typography variant="body1">
                                    No pending orders found for this hub
                                </Typography>
                            </Paper>
                        ) : (
                            <List>
                                {pendingOrders.map((order) => (
                                    <Paper key={order.id} sx={{ mb: 2 }}>
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
                                                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                        <Typography variant="subtitle1">
                                                            Order #{order.id.substring(0, 8)}...
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
                                                    <Grid container spacing={1} sx={{ mt: 1 }}>
                                                        <Grid item xs={12} sm={6}>
                                                            <Typography variant="body2" component="span">
                                                                From: {order.originHub || order.originHubName}
                                                            </Typography>
                                                        </Grid>
                                                        <Grid item xs={12} sm={6}>
                                                            <Typography variant="body2" component="span">
                                                                To: {order.destinationHub || order.finalHubName}
                                                            </Typography>
                                                        </Grid>
                                                        <Grid item xs={6}>
                                                            <Typography variant="body2" component="span">
                                                                Items: {order.itemCount || '?'}
                                                            </Typography>
                                                        </Grid>
                                                        <Grid item xs={6}>
                                                            <Typography variant="body2" component="span">
                                                                Weight: {order.totalWeight || '?'} kg
                                                            </Typography>
                                                        </Grid>
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
                    <List>
                        {pendingOrders
                            .filter(order => selectedOrders.includes(order.id))
                            .map(order => (
                                <Paper key={order.id} sx={{ mb: 2 }}>
                                    <ListItem>
                                        <ListItemText
                                            primary={
                                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                    <Typography variant="subtitle1">
                                                        Order #{order.id.substring(0, 8)}...
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
                                                <Grid container spacing={1} sx={{ mt: 1 }}>
                                                    <Grid item xs={12} sm={6}>
                                                        <Typography variant="body2" component="span">
                                                            From: {order.originHub || order.originHubName}
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={12} sm={6}>
                                                        <Typography variant="body2" component="span">
                                                            To: {order.destinationHub || order.finalHubName}
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={6}>
                                                        <Typography variant="body2" component="span">
                                                            Items: {order.itemCount || '?'}
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={6}>
                                                        <Typography variant="body2" component="span">
                                                            Weight: {order.totalWeight || '?'} kg
                                                        </Typography>
                                                    </Grid>
                                                </Grid>
                                            }
                                        />
                                        <IconButton edge="end" onClick={() => toggleOrderSelection(order.id)}>
                                            <Checkbox checked={true} />
                                        </IconButton>
                                    </ListItem>
                                </Paper>
                            ))}
                    </List>
                )}

                {/* Action Button */}
                <Box sx={{ position: 'fixed', bottom: 16, left: 0, right: 0, textAlign: 'center' }}>
                    <Button
                        variant="contained"
                        color="primary"
                        size="large"
                        disabled={selectedOrders.length === 0 || operationLoading}
                        onClick={openConfirmDialog}
                        startIcon={operationType === 'pickup' ? <LocalShippingIcon /> : <CheckCircleIcon />}
                        sx={{ px: 4, py: 1.5 }}
                    >
                        {operationLoading ? (
                            <CircularProgress size={24} color="inherit" />
                        ) : (
                            operationType === 'pickup' ? `Pickup ${selectedOrders.length} Orders` : `Deliver ${selectedOrders.length} Orders`
                        )}
                    </Button>
                </Box>

                {/* Confirm Dialog */}
                <Dialog open={confirmDialogOpen} onClose={() => setConfirmDialogOpen(false)}>
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
                        <Button onClick={() => setConfirmDialogOpen(false)}>Cancel</Button>
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

                {/* Scanner Dialog */}
                <Dialog open={scannerOpen} onClose={() => setScannerOpen(false)}>
                    <DialogTitle>Scan Order QR Code</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Enter or scan the order ID
                        </DialogContentText>
                        <TextField
                            autoFocus
                            margin="dense"
                            label="Order ID"
                            fullWidth
                            value={scannedOrderId}
                            onChange={handleScannerInput}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setScannerOpen(false)}>Cancel</Button>
                        <Button onClick={processScannedOrder} variant="contained" color="primary">
                            Add Order
                        </Button>
                    </DialogActions>
                </Dialog>
            </Box>
        </Container>
    );
};

export default DriverHubOperations;