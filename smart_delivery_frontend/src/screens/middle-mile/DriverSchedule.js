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
import PauseIcon from '@mui/icons-material/Pause';
import StopIcon from '@mui/icons-material/Stop';

const DriverSchedule = () => {
    const [loading, setLoading] = useState(true);
    const [trips, setTrips] = useState({
        activeTrips: [],
        scheduledTrips: [],
        completedTrips: [],
        otherTrips: [] // For trips with other statuses
    });
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [tripDetails, setTripDetails] = useState(null);
    const [completionNotes, setCompletionNotes] = useState('');
    const [completeTripDialogOpen, setCompleteTripDialogOpen] = useState(false);
    const [vehicle, setVehicle] = useState(null);
    const username = useSelector((state) => state.auth.username);
    const history = useHistory();

    // Helper function to get status color
    const getStatusColor = (status) => {
        switch (status) {
            case 'PLANNED': return 'info';
            case 'IN_PROGRESS': return 'warning';
            case 'CONFIRMED_IN': return 'warning';
            case 'CAME_FIRST_STOP': return 'secondary';
            case 'READY_FOR_PICKUP': return 'primary';
            case 'COMPLETED': return 'success';
            case 'CANCELLED': return 'error';
            default: return 'default';
        }
    };

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
            await request('get', '/smdeli/driver/trips/today', (res) => {
                // If the backend returns a structured object with trip categories
                if (res.data && typeof res.data === 'object' && (res.data.activeTrips || res.data.scheduledTrips)) {
                    setTrips(res.data);
                } else {
                    // If the backend returns a flat list of trips, categorize them here
                    const allTrips = res.data || [];
                    const categorizedTrips = {
                        activeTrips: allTrips.filter(trip =>
                            trip.status === 'IN_PROGRESS' ||
                            trip.status === 'CONFIRMED_IN'
                        ),
                        scheduledTrips: allTrips.filter(trip => trip.status === 'PLANNED'),
                        completedTrips: allTrips.filter(trip => trip.status === 'COMPLETED'),
                        // Capture all other statuses that don't fit the above categories
                        otherTrips: allTrips.filter(trip =>
                            !['IN_PROGRESS', 'CONFIRMED_IN', 'PLANNED', 'COMPLETED'].includes(trip.status)
                        )
                    };
                    setTrips(categorizedTrips);
                }
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

    // Navigate to hub operations for pickup/delivery
    const handleHubOperation = (tripId, hubId, operationType) => {
        history.push(`/middle-mile/driver/hub/${hubId}/${operationType}?tripId=${tripId}`);
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
                { routeVehicleId: routeScheduleId }
            );
        } catch (error) {
            console.error("Error creating trip:", error);
            errorNoti("Failed to create trip");
        }
    };

    // Render trip item component
    const renderTripItem = (trip) => (
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
                                {trip.routeCode && (
                                    <Typography component="span" variant="body2" color="text.secondary" sx={{ ml: 1 }}>
                                        ({trip.routeCode})
                                    </Typography>
                                )}
                            </Typography>
                            <Chip
                                label={trip.status}
                                color={getStatusColor(trip.status)}
                                size="small"
                            />
                        </Box>
                    }
                    secondary={
                        <Box sx={{ mt: 1 }}>
                            {trip.dayOfWeek && (
                                <Typography variant="body2" color="text.secondary">
                                    Day: {formatDayOfWeek(trip.dayOfWeek)}
                                </Typography>
                            )}
                            <Typography variant="body2" color="text.secondary">
                                Date: {trip.date}
                            </Typography>
                            {trip.status === 'IN_PROGRESS' || trip.status === 'CONFIRMED_IN' ? (
                                <Typography variant="body2" color="text.secondary">
                                    Stop {trip.currentStopIndex + 1} of {trip.totalStops}
                                </Typography>
                            ) : (
                                <Typography variant="body2" color="text.secondary">
                                    Stops: {trip.totalStops}
                                </Typography>
                            )}
                            <Typography variant="body2" color="text.secondary">
                                Orders: {trip.ordersDelivered || 0}/{trip.ordersCount || 0}
                            </Typography>
                            {trip.startTime && (
                                <Typography variant="body2" color="text.secondary">
                                    Started: {new Date(trip.startTime).toLocaleTimeString()}
                                </Typography>
                            )}
                            {trip.endTime && (
                                <Typography variant="body2" color="text.secondary">
                                    Completed: {new Date(trip.endTime).toLocaleTimeString()}
                                </Typography>
                            )}
                        </Box>
                    }
                />
            </ListItem>
        </Paper>
    );

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
                <Grid item xs={12} md={selectedTrip ? 6 : 12}>
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
                            {/* Other Status Trips (CAME_FIRST_STOP, READY_FOR_PICKUP, etc.) - Top Priority */}
                            {trips.otherTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, color: 'secondary.main' }}>
                                        Other Trips ({trips.otherTrips.length})
                                    </Typography>
                                    <List>
                                        {trips.otherTrips.map(renderTripItem)}
                                    </List>
                                </>
                            )}

                            {/* Scheduled Trips */}
                            {trips.scheduledTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, mt: trips.otherTrips.length > 0 ? 2 : 0, color: 'info.main' }}>
                                        Scheduled ({trips.scheduledTrips.length})
                                    </Typography>
                                    <List>
                                        {trips.scheduledTrips.map(renderTripItem)}
                                    </List>
                                </>
                            )}

                            {/* Active Trips */}
                            {trips.activeTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, mt: (trips.otherTrips.length > 0 || trips.scheduledTrips.length > 0) ? 2 : 0, color: 'warning.main' }}>
                                        In Progress ({trips.activeTrips.length})
                                    </Typography>
                                    <List>
                                        {trips.activeTrips.map(renderTripItem)}
                                    </List>
                                </>
                            )}

                            {/* Completed Trips */}
                            {trips.completedTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, mt: (trips.otherTrips.length > 0 || trips.scheduledTrips.length > 0 || trips.activeTrips.length > 0) ? 2 : 0, color: 'success.main' }}>
                                        Completed Today ({trips.completedTrips.length})
                                    </Typography>
                                    <List>
                                        {trips.completedTrips.map(renderTripItem)}
                                    </List>
                                </>
                            )}

                            {/* No trips message */}
                            {trips.activeTrips.length === 0 && trips.scheduledTrips.length === 0 &&
                                trips.completedTrips.length === 0 && trips.otherTrips.length === 0 && (
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
                </Grid>

                {/* Trip Details Section */}
                {selectedTrip && (
                    <Grid item xs={12} md={6}>
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
                                                {tripDetails.status !== 'PLANNED' && tripDetails.status !== 'COMPLETED' && (
                                                    <Button
                                                        variant="contained"
                                                        color="success"
                                                        startIcon={<CheckIcon />}
                                                        onClick={() => history.push(`/middle-mile/driver/route/${tripDetails.routeScheduleId}?tripId=${tripDetails.id}`)}
                                                    >
                                                        Details
                                                    </Button>
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
                                            <Typography variant="h6" gutterBottom>
                                                Trip Summary
                                            </Typography>
                                            <Box sx={{ mb: 2 }}>
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Route:</strong> {tripDetails.routeName}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Route Code:</strong> {tripDetails.routeCode}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Date:</strong> {tripDetails.date}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Status:</strong>
                                                    <Chip
                                                        label={tripDetails.status}
                                                        color={getStatusColor(tripDetails.status)}
                                                        size="small"
                                                        sx={{ ml: 1 }}
                                                    />
                                                </Typography>
                                            </Box>
                                        </Grid>

                                        {/* Stops */}
                                        <Grid item xs={12}>
                                            <Typography variant="h6" gutterBottom>
                                                <DirectionsIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                                Stops ({tripDetails.currentStopIndex + 1}/{tripDetails.totalStops})
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
                                                                <Chip
                                                                    label={stop.status}
                                                                    color={
                                                                        stop.status === 'CURRENT' ? 'warning' :
                                                                            stop.status === 'COMPLETED' ? 'success' : 'default'
                                                                    }
                                                                    size="small"
                                                                />
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