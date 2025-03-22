import React, { useState, useEffect } from 'react';
import {
    Box,
    Grid,
    Typography,
    Card,
    CardContent,
    CardHeader,
    Button,
    CircularProgress,
    Chip,
    Divider,
    List,
    ListItem,
    ListItemText,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Paper,
    IconButton,
    Alert
} from '@mui/material';
import { useSelector } from 'react-redux';
import { request } from 'api';
import { useHistory } from 'react-router-dom';
import { errorNoti, successNoti } from 'utils/notification';

// Icons
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import DirectionsIcon from '@mui/icons-material/Directions';
import InventoryIcon from '@mui/icons-material/Inventory';
import EventIcon from '@mui/icons-material/Event';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import CheckIcon from '@mui/icons-material/Check';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ScheduleIcon from '@mui/icons-material/Schedule';

const DriverSchedule = () => {
    const [loading, setLoading] = useState(true);
    const [trips, setTrips] = useState({
        activeTrips: [],
        scheduledTrips: [],
        completedTrips: []
    });
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [tripDetails, setTripDetails] = useState(null);
    const [completionNotes, setCompletionNotes] = useState('');
    const [completeTripDialogOpen, setCompleteTripDialogOpen] = useState(false);
    const [vehicle, setVehicle] = useState(null);

    const username = useSelector((state) => state.auth.username);
    const history = useHistory();

    // Fetch all trips and vehicle data on component mount
    useEffect(() => {
        fetchDriverData();
    }, []);

    // Fetch trip details when a trip is selected
    useEffect(() => {
        if (selectedTrip) {
            fetchTripDetails(selectedTrip);
        }
    }, [selectedTrip]);

    const fetchDriverData = async () => {
        setLoading(true);
        try {
            // Get all trips for the driver
            await request('get', '/smdeli/driver/trips', (res) => {
                setTrips(res.data || { activeTrips: [], scheduledTrips: [], completedTrips: [] });
            });

            // Get assigned vehicle
            await request('get', '/smdeli/driver/vehicle', (res) => {
                setVehicle(res.data);
            });

            setLoading(false);
        } catch (error) {
            console.error("Error fetching driver data:", error);
            errorNoti("Failed to load driver data");
            setLoading(false);
        }
    };

    const fetchTripDetails = async (tripId) => {
        try {
            await request('get', `/smdeli/driver/trips/${tripId}`, (res) => {
                setTripDetails(res.data);
            });
        } catch (error) {
            console.error("Error fetching trip details:", error);
            errorNoti("Failed to load trip details");
        }
    };

    const handleStartTrip = async (tripId) => {
        try {
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/start`,
                (res) => {
                    successNoti("Trip started successfully");
                    setTripDetails(res.data);
                    // Refresh all trips data
                    fetchDriverData();
                }
            );
        } catch (error) {
            console.error("Error starting trip:", error);
            errorNoti("Failed to start trip");
        }
    };

    // Helper to get formatted day of week
    const formatDayOfWeek = (dayOfWeek) => {
        if (!dayOfWeek) return '';
        // Convert MONDAY to Monday
        return dayOfWeek.charAt(0) + dayOfWeek.slice(1).toLowerCase();
    };

    const handleAdvanceTrip = async (tripId) => {
        try {
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/advance`,
                (res) => {
                    successNoti("Advanced to next stop");
                    setTripDetails(res.data);
                    // Refresh all trips data
                    fetchDriverData();
                }
            );
        } catch (error) {
            console.error("Error advancing trip:", error);
            errorNoti("Failed to advance trip");
        }
    };

    const handleCompleteTrip = async (tripId) => {
        try {
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/complete`,
                (res) => {
                    successNoti("Trip completed successfully");
                    setTripDetails(null);
                    setSelectedTrip(null);
                    setCompleteTripDialogOpen(false);
                    // Refresh all trips data
                    fetchDriverData();
                },
                {
                    400: () => errorNoti("Unable to complete trip"),
                    401: () => errorNoti("Unauthorized action")
                },
                { notes: completionNotes }
            );
        } catch (error) {
            console.error("Error completing trip:", error);
            errorNoti("Failed to complete trip");
        }
    };

    const handleHubOperation = (tripId, hubId, operationType) => {
        history.push(`/driver/hub/${hubId}/${operationType}?tripId=${tripId}`);
    };

    // If user wants to create a trip for today
    const handleCreateTrip = (routeScheduleId) => {
        if (!routeScheduleId) {
            errorNoti("No route schedule selected");
            return;
        }

        try {
            request(
                'post',
                '/smdeli/driver/trip/start',
                (res) => {
                    successNoti("Trip created successfully");
                    // Set the new trip as selected
                    setSelectedTrip(res.data.id);
                    // Refresh trips data
                    fetchDriverData();
                },
                {
                    401: () => errorNoti("Unauthorized action"),
                    400: () => errorNoti("Unable to create trip")
                },
                { routeScheduleId }
            );
        } catch (error) {
            console.error("Error creating trip:", error);
            errorNoti("Failed to create trip");
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="80vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
                <ScheduleIcon sx={{ mr: 2 }} />
                Driver Schedule & Orders
            </Typography>

            <Grid container spacing={3}>
                {/* Today's Trips Section */}
                <Grid item xs={12} md={selectedTrip ? 4 : 12}>
                    <Card>
                        <CardHeader
                            title={
                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    <EventIcon sx={{ mr: 1 }} />
                                    <Typography variant="h6">Today's Trips</Typography>
                                </Box>
                            }
                        />
                        <Divider />

                        <CardContent>
                            {/* Active Trips */}
                            {trips.activeTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, color: 'warning.main' }}>
                                        In Progress
                                    </Typography>
                                    <List>
                                        {trips.activeTrips.map(trip => (
                                            <Paper
                                                key={trip.id}
                                                elevation={trip.id === selectedTrip ? 3 : 1}
                                                sx={{
                                                    mb: 2,
                                                    borderLeft: trip.id === selectedTrip ? '4px solid #2196f3' : 'none',
                                                    bgcolor: trip.id === selectedTrip ? 'action.hover' : 'background.paper'
                                                }}
                                            >
                                                <ListItem
                                                    button
                                                    onClick={() => setSelectedTrip(trip.id)}
                                                >
                                                    <ListItemText
                                                        primary={
                                                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                                <Typography variant="subtitle1">
                                                                    {trip.routeName || `Trip #${trip.id.substring(0, 8)}`}
                                                                </Typography>
                                                                <Chip
                                                                    label={trip.status}
                                                                    color="warning"
                                                                    size="small"
                                                                />
                                                            </Box>
                                                        {trip.dayOfWeek && (
                                                            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                                                        Day: {formatDayOfWeek(trip.dayOfWeek)}
                                                    </Typography>
                                                    )}
                                                    }
                                                    secondary={
                                                        <Box sx={{ mt: 1 }}>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Stop {trip.currentStopIndex + 1} of {trip.totalStops}
                                                            </Typography>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Orders: {trip.ordersDelivered}/{trip.ordersCount}
                                                            </Typography>
                                                            {trip.startTime && (
                                                                <Typography variant="body2" color="text.secondary">
                                                                    Started: {new Date(trip.startTime).toLocaleTimeString()}
                                                                </Typography>
                                                            )}
                                                        </Box>
                                                    }
                                                />
                                            </ListItem>
                                            </Paper>
                                            ))}
                                    </List>
                                </>
                            )}

                            {/* Scheduled Trips */}
                            {trips.scheduledTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, mt: 2, color: 'info.main' }}>
                                        Scheduled
                                    </Typography>
                                    <List>
                                        {trips.scheduledTrips.map(trip => (
                                            <Paper
                                                key={trip.id}
                                                elevation={trip.id === selectedTrip ? 3 : 1}
                                                sx={{
                                                    mb: 2,
                                                    borderLeft: trip.id === selectedTrip ? '4px solid #2196f3' : 'none',
                                                    bgcolor: trip.id === selectedTrip ? 'action.hover' : 'background.paper'
                                                }}
                                            >
                                                <ListItem
                                                    button
                                                    onClick={() => setSelectedTrip(trip.id)}
                                                >
                                                    <ListItemText
                                                        primary={
                                                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                                <Typography variant="subtitle1">
                                                                    {trip.routeName || `Trip #${trip.id.substring(0, 8)}`}
                                                                </Typography>
                                                                <Chip
                                                                    label={trip.status}
                                                                    color="info"
                                                                    size="small"
                                                                />
                                                            </Box>
                                                        {trip.dayOfWeek && (
                                                            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                                                        Day: {formatDayOfWeek(trip.dayOfWeek)}
                                                    </Typography>
                                                    )}
                                                    }
                                                    secondary={
                                                        <Box sx={{ mt: 1 }}>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Stops: {trip.totalStops}
                                                            </Typography>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Orders: {trip.ordersCount}
                                                            </Typography>
                                                        </Box>
                                                    }
                                                />
                                            </ListItem>
                                            </Paper>
                                            ))}
                                    </List>
                                </>
                            )}

                            {/* Completed Trips */}
                            {trips.completedTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, mt: 2, color: 'success.main' }}>
                                        Completed Today
                                    </Typography>
                                    <List>
                                        {trips.completedTrips.map(trip => (
                                            <Paper
                                                key={trip.id}
                                                elevation={trip.id === selectedTrip ? 3 : 1}
                                                sx={{
                                                    mb: 2,
                                                    borderLeft: trip.id === selectedTrip ? '4px solid #2196f3' : 'none',
                                                    bgcolor: trip.id === selectedTrip ? 'action.hover' : 'background.paper'
                                                }}
                                            >
                                                <ListItem
                                                    button
                                                    onClick={() => setSelectedTrip(trip.id)}
                                                >
                                                    <ListItemText
                                                        primary={
                                                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                                <Typography variant="subtitle1">
                                                                    {trip.routeName || `Trip #${trip.id.substring(0, 8)}`}
                                                                </Typography>
                                                                <Chip
                                                                    label={trip.status}
                                                                    color="success"
                                                                    size="small"
                                                                />
                                                            </Box>
                                                        {trip.dayOfWeek && (
                                                            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                                                        Day: {formatDayOfWeek(trip.dayOfWeek)}
                                                    </Typography>
                                                    )}
                                                    }
                                                    secondary={
                                                        <Box sx={{ mt: 1 }}>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Completed at: {new Date(trip.endTime).toLocaleTimeString()}
                                                            </Typography>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Orders: {trip.ordersDelivered}/{trip.ordersCount}
                                                            </Typography>
                                                        </Box>
                                                    }
                                                />
                                            </ListItem>
                                            </Paper>
                                            ))}
                                    </List>
                                </>
                            )}

                            {trips.activeTrips.length === 0 && trips.scheduledTrips.length === 0 && trips.completedTrips.length === 0 && (
                                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 4 }}>
                                    <ScheduleIcon sx={{ fontSize: 60, color: 'text.disabled', mb: 2 }} />
                                    <Typography variant="h6" color="text.secondary">No trips scheduled for today</Typography>
                                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                        Check with your manager for your daily schedule
                                    </Typography>

                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={() => history.push('/driver/create-trip')}
                                        sx={{ mt: 3 }}
                                    >
                                        Create New Trip
                                    </Button>
                                </Box>
                            )}
                        </CardContent>
                    </Card>

                    {/* Vehicle Information Card */}
                    {vehicle && !selectedTrip && (
                        <Card sx={{ mt: 3 }}>
                            <CardHeader
                                title={
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <LocalShippingIcon sx={{ mr: 1 }} />
                                        <Typography variant="h6">Your Vehicle</Typography>
                                    </Box>
                                }
                            />
                            <Divider />
                            <CardContent>
                                <Grid container spacing={2}>
                                    <Grid item xs={6}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Plate Number
                                        </Typography>
                                        <Typography variant="body1" gutterBottom fontWeight="bold">
                                            {vehicle.plateNumber}
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={6}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Type
                                        </Typography>
                                        <Typography variant="body1" gutterBottom>
                                            {vehicle.vehicleType}
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={6}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Status
                                        </Typography>
                                        <Chip
                                            label={vehicle.status}
                                            color={vehicle.status === 'AVAILABLE' ? 'success' : 'warning'}
                                            size="small"
                                        />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Model
                                        </Typography>
                                        <Typography variant="body1" gutterBottom>
                                            {vehicle.manufacturer} {vehicle.model}
                                        </Typography>
                                    </Grid>
                                </Grid>
                            </CardContent>
                        </Card>
                    )}
                </Grid>

                {/* Trip Details Section */}
                {selectedTrip && (
                    <Grid item xs={12} md={8}>
                        <Card>
                            <CardHeader
                                title={
                                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                            <IconButton onClick={() => {
                                                setSelectedTrip(null);
                                                setTripDetails(null);
                                            }}>
                                                <ArrowBackIcon />
                                            </IconButton>
                                            <Typography variant="h6">
                                                Trip Details
                                            </Typography>
                                        </Box>

                                        {/* Action buttons based on trip status */}
                                        {tripDetails && (
                                            <Box>
                                                {tripDetails.status === 'PLANNED' && (
                                                    <Button
                                                        variant="contained"
                                                        color="primary"
                                                        startIcon={<PlayArrowIcon />}
                                                        onClick={() => handleStartTrip(tripDetails.id)}
                                                    >
                                                        Start Trip
                                                    </Button>
                                                )}
                                                {tripDetails.status === 'IN_PROGRESS' && (
                                                    <>
                                                        <Button
                                                            variant="outlined"
                                                            color="primary"
                                                            startIcon={<ArrowForwardIcon />}
                                                            onClick={() => handleAdvanceTrip(tripDetails.id)}
                                                            sx={{ mr: 1 }}
                                                        >
                                                            Next Stop
                                                        </Button>
                                                        <Button
                                                            variant="contained"
                                                            color="success"
                                                            startIcon={<CheckIcon />}
                                                            onClick={() => setCompleteTripDialogOpen(true)}
                                                        >
                                                            Complete
                                                        </Button>
                                                    </>
                                                )}
                                            </Box>
                                        )}
                                    </Box>
                                }
                            />
                            <Divider />

                            {!tripDetails ? (
                                <CardContent>
                                    <Box display="flex" justifyContent="center" alignItems="center" height="50vh">
                                        <CircularProgress />
                                    </Box>
                                </CardContent>
                            ) : (
                                <CardContent>
                                    <Grid container spacing={3}>
                                        {/* Trip Summary */}
                                        <Grid item xs={12}>
                                            <Paper sx={{ p: 2, bgcolor: 'background.default' }}>
                                                <Typography variant="subtitle1" gutterBottom>
                                                    {tripDetails.routeName || `Trip #${tripDetails.id.substring(0, 8)}`}
                                                </Typography>
                                                <Grid container spacing={2}>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" color="text.secondary">Status</Typography>
                                                        <Chip
                                                            label={tripDetails.status}
                                                            color={
                                                                tripDetails.status === 'PLANNED' ? 'info' :
                                                                    tripDetails.status === 'IN_PROGRESS' ? 'warning' : 'success'
                                                            }
                                                            size="small"
                                                        />
                                                    </Grid>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" color="text.secondary">Current Stop</Typography>
                                                        <Typography variant="body1">
                                                            {tripDetails.currentStopIndex !== undefined
                                                                ? `${tripDetails.currentStopIndex + 1} of ${tripDetails.totalStops}`
                                                                : 'N/A'}
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={4}>
                                                        <Typography variant="body2" color="text.secondary">Orders</Typography>
                                                        <Typography variant="body1">
                                                            {tripDetails.ordersDelivered}/{tripDetails.ordersCount}
                                                        </Typography>
                                                    </Grid>
                                                    {tripDetails.startTime && (
                                                        <Grid item xs={6}>
                                                            <Typography variant="body2" color="text.secondary">Started</Typography>
                                                            <Typography variant="body1">
                                                                {new Date(tripDetails.startTime).toLocaleString()}
                                                            </Typography>
                                                        </Grid>
                                                    )}
                                                    {tripDetails.endTime && (
                                                        <Grid item xs={6}>
                                                            <Typography variant="body2" color="text.secondary">Completed</Typography>
                                                            <Typography variant="body1">
                                                                {new Date(tripDetails.endTime).toLocaleString()}
                                                            </Typography>
                                                        </Grid>
                                                    )}
                                                </Grid>
                                            </Paper>
                                        </Grid>

                                        {/* Stops */}
                                        <Grid item xs={12} md={6}>
                                            <Typography variant="h6" gutterBottom>
                                                <DirectionsIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                                Stops
                                            </Typography>
                                            <List sx={{ bgcolor: 'background.paper', borderRadius: 1 }}>
                                                {tripDetails.stops && tripDetails.stops.map((stop, index) => (
                                                    <Paper
                                                        key={stop.id || index}
                                                        sx={{
                                                            mb: 1,
                                                            borderLeft: stop.status === 'CURRENT' ? '4px solid #ff9800' :
                                                                stop.status === 'COMPLETED' ? '4px solid #4caf50' : 'none'
                                                        }}
                                                    >
                                                        <ListItem
                                                            secondaryAction={
                                                                stop.status === 'CURRENT' && tripDetails.status === 'IN_PROGRESS' ? (
                                                                    <Box>
                                                                        <Button
                                                                            variant="contained"
                                                                            size="small"
                                                                            onClick={() => handleHubOperation(
                                                                                tripDetails.id,
                                                                                stop.hubId,
                                                                                'pickup'
                                                                            )}
                                                                            sx={{ mr: 1 }}
                                                                        >
                                                                            Pickup
                                                                        </Button>
                                                                        <Button
                                                                            variant="contained"
                                                                            size="small"
                                                                            color="secondary"
                                                                            onClick={() => handleHubOperation(
                                                                                tripDetails.id,
                                                                                stop.hubId,
                                                                                'delivery'
                                                                            )}
                                                                        >
                                                                            Deliver
                                                                        </Button>
                                                                    </Box>
                                                                ) : (
                                                                    <Chip
                                                                        label={stop.status}
                                                                        color={
                                                                            stop.status === 'CURRENT' ? 'warning' :
                                                                                stop.status === 'COMPLETED' ? 'success' : 'default'
                                                                        }
                                                                        size="small"
                                                                    />
                                                                )
                                                            }
                                                        >
                                                            <ListItemText
                                                                primary={
                                                                    <Typography variant="subtitle2">
                                                                        {index + 1}. {stop.hubName}
                                                                    </Typography>
                                                                }
                                                                secondary={
                                                                    <>
                                                                        <Typography variant="body2" color="text.secondary">
                                                                            {stop.address}
                                                                        </Typography>
                                                                        <Typography variant="body2" color="text.secondary">
                                                                            Orders: {stop.orderCount || 0}
                                                                        </Typography>
                                                                        {stop.estimatedArrivalTime && (
                                                                            <Typography variant="body2" color="text.secondary">
                                                                                ETA: {stop.estimatedArrivalTime}
                                                                            </Typography>
                                                                        )}
                                                                    </>
                                                                }
                                                            />
                                                        </ListItem>
                                                    </Paper>
                                                ))}
                                            </List>
                                        </Grid>

                                        {/* Orders */}
                                        <Grid item xs={12} md={6}>
                                            <Typography variant="h6" gutterBottom>
                                                <InventoryIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                                Orders
                                            </Typography>
                                            <Box sx={{ maxHeight: '60vh', overflow: 'auto' }}>
                                                {tripDetails.orders && tripDetails.orders.length > 0 ? (
                                                    <List sx={{ bgcolor: 'background.paper', borderRadius: 1 }}>
                                                        {tripDetails.orders.map((order) => (
                                                            <Paper key={order.id} sx={{ mb: 1 }}>
                                                                <ListItem>
                                                                    <ListItemText
                                                                        primary={
                                                                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                                                <Typography variant="subtitle2">
                                                                                    #{order.id.substring(0, 8)}...
                                                                                </Typography>
                                                                                <Chip label={order.status} size="small" />
                                                                            </Box>
                                                                        }
                                                                        secondary={
                                                                            <>
                                                                                <Typography variant="body2" color="text.secondary">
                                                                                    From: {order.senderName}
                                                                                </Typography>
                                                                                <Typography variant="body2" color="text.secondary">
                                                                                    To: {order.recipientName}
                                                                                </Typography>
                                                                                <Typography variant="body2" color="text.secondary">
                                                                                    Created: {new Date(order.createdAt).toLocaleDateString()}
                                                                                </Typography>
                                                                            </>
                                                                        }
                                                                    />
                                                                </ListItem>
                                                            </Paper>
                                                        ))}
                                                    </List>
                                                ) : (
                                                    <Alert severity="info">No orders associated with this trip</Alert>
                                                )}
                                            </Box>
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            )}
                        </Card>
                    </Grid>
                )}
            </Grid>

            {/* Complete Trip Dialog */}
            <Dialog
                open={completeTripDialogOpen}
                onClose={() => setCompleteTripDialogOpen(false)}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle>Complete Trip</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>
                        Are you sure you want to mark this trip as completed?
                    </Typography>
                    <TextField
                        label="Completion Notes (optional)"
                        fullWidth
                        multiline
                        rows={4}
                        value={completionNotes}
                        onChange={(e) => setCompletionNotes(e.target.value)}
                        margin="normal"
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setCompleteTripDialogOpen(false)}>Cancel</Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleCompleteTrip(selectedTrip)}
                    >
                        Complete Trip
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default DriverSchedule;