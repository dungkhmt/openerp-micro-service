import React, { useState, useEffect } from 'react';
import { Box, Typography, Card, CardContent, Button, CircularProgress, Chip, Divider, Paper, List, ListItem, ListItemText, IconButton, Grid } from '@mui/material';
import { useParams, useHistory } from 'react-router-dom';
import { request } from 'api';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import RouteIcon from '@mui/icons-material/Route';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PlaceIcon from '@mui/icons-material/Place';
import DateRangeIcon from '@mui/icons-material/DateRange';
import MapIcon from '@mui/icons-material/Map';
import DirectionsIcon from '@mui/icons-material/Directions';
import MyLocationIcon from '@mui/icons-material/MyLocation';
import { errorNoti, successNoti } from 'utils/notification';

// Import the map component from your codebase
import { EnhancedMap } from 'components/map/EnhancedMap';

const DriverRouteMap = () => {
    const { routeVehicleId } = useParams();
    const history = useHistory();

    const [loading, setLoading] = useState(true);
    const [routeVehicle, setRouteVehicle] = useState(null);
    const [route, setRoute] = useState(null);
    const [stopSequence, setStopSequence] = useState([]);
    const [orders, setOrders] = useState([]);
    const [hub, setHub] = useState(null);
    const [nextOrder, setNextOrder] = useState(null);
    const [currentLocation, setCurrentLocation] = useState(null);

    // Convert stop sequence to map points
    const [mapPoints, setMapPoints] = useState([]);
    const [assignments, setAssignments] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);

                // Get route vehicle details
                await request('get', `/smdeli/middle-mile/vehicle-assignments/${routeVehicleId}`, (res) => {
                    setRouteVehicle(res.data);

                    // Get route details and stops
                    if (res.data?.routeId) {
                        request('get', `/smdeli/middle-mile/routes/${res.data.routeId}`, (routeRes) => {
                            setRoute(routeRes.data);

                            // Get route stops
                            request('get', `/smdeli/middle-mile/routes/${res.data.routeId}/stops`, (stopsRes) => {
                                setStopSequence(stopsRes.data || []);

                                // Create map points from stops
                                const points = stopsRes.data.map(stop => ({
                                    lat: stop.latitude || stop.hubLatitude,
                                    lng: stop.longitude || stop.hubLongitude,
                                    name: stop.hubName,
                                    hubId: stop.hubId
                                }));

                                setMapPoints(points);
                            });
                        });
                    }
                });

                // Get orders for this route vehicle
                await request('get', `/smdeli/driver/routes/${routeVehicleId}/orders`, (res) => {
                    setOrders(res.data || []);

                    // Find next order to be delivered (first DELIVERING order)
                    const nextToDeliver = res.data?.find(order => order.status === 'DELIVERING');
                    if (nextToDeliver) {
                        setNextOrder(nextToDeliver);
                    }

                    // Create assignments for map component
                    const assignments = res.data.map(order => ({
                        id: order.id,
                        orderId: order.id,
                        senderName: order.senderName,
                        senderPhone: order.senderPhone,
                        senderAddress: order.senderAddress,
                        status: order.status,
                        orderCreatedAt: order.createdAt
                    }));

                    setAssignments(assignments);
                });

                // Get driver's hub
                const hubId = localStorage.getItem('hubId');
                if (hubId) {
                    request('get', `/smdeli/hubmanager/hub/${hubId}`, (res) => {
                        setHub({
                            name: res.data.name,
                            address: res.data.address,
                            latitude: res.data.latitude,
                            longitude: res.data.longitude
                        });
                    });
                }

                // Get current location if available
                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(
                        (position) => {
                            setCurrentLocation({
                                lat: position.coords.latitude,
                                lng: position.coords.longitude
                            });
                        },
                        (error) => {
                            console.error("Error getting location:", error);
                        }
                    );
                }

                setLoading(false);
            } catch (error) {
                console.error("Error fetching route data:", error);
                errorNoti("Failed to load route data");
                setLoading(false);
            }
        };

        fetchData();

        // Set up periodic location updates
        const locationInterval = setInterval(() => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        setCurrentLocation({
                            lat: position.coords.latitude,
                            lng: position.coords.longitude
                        });
                    },
                    (error) => {
                        console.error("Error updating location:", error);
                    }
                );
            }
        }, 30000); // Update every 30 seconds

        return () => {
            clearInterval(locationInterval);
        };
    }, [routeVehicleId]);

    const handleBack = () => {
        history.push('/driver/dashboard');
    };

    const handleViewOrders = () => {
        history.push(`/driver/orders/${routeVehicleId}`);
    };

    const getNextStop = () => {
        if (!nextOrder || !stopSequence.length) return null;

        // Find the destination hub for the next order
        const destinationHubId = nextOrder.destinationHubId;
        return stopSequence.find(stop => stop.hubId === destinationHubId);
    };

    const handleCompleteTrip = () => {
        if (window.confirm("Are you sure you want to complete this trip? This will mark all orders as delivered.")) {
            request(
                'post',
                `/smdeli/driver/trips/${routeVehicleId}/complete`,
                () => {
                    successNoti("Trip completed successfully");
                    history.push('/driver/dashboard');
                },
                {
                    401: () => errorNoti("Unauthorized action"),
                    400: () => errorNoti("Unable to complete trip")
                }
            );
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
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={handleBack}
                sx={{ mb: 2 }}
            >
                Back to Dashboard
            </Button>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4">
                    Route Map: {route?.routeName || 'Loading...'}
                </Typography>

                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="outlined"
                        startIcon={<MapIcon />}
                        onClick={handleViewOrders}
                    >
                        View Orders
                    </Button>

                    {routeVehicle?.status === 'IN_PROGRESS' && (
                        <Button
                            variant="contained"
                            color="success"
                            onClick={handleCompleteTrip}
                        >
                            Complete Trip
                        </Button>
                    )}
                </Box>
            </Box>

            <Grid container spacing={3}>
                <Grid item xs={12} md={4}>
                    <Card sx={{ mb: 3 }}>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <RouteIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Route Information
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />

                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Route
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {route?.routeName} ({route?.routeCode})
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Vehicle
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {routeVehicle?.vehiclePlateNumber || 'N/A'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Direction
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {routeVehicle?.direction || 'N/A'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Status
                                    </Typography>
                                    <Chip
                                        label={routeVehicle?.status || 'UNKNOWN'}
                                        color={
                                            routeVehicle?.status === 'IN_PROGRESS' ? 'warning' :
                                                routeVehicle?.status === 'COMPLETED' ? 'success' : 'primary'
                                        }
                                        size="small"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Scheduled Departure
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {routeVehicle?.scheduledDepartureTime ? new Date(routeVehicle?.scheduledDepartureTime).toLocaleString() : 'Not scheduled'}
                                    </Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <DateRangeIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Route Stops
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />

                            <List sx={{ maxHeight: 400, overflow: 'auto' }}>
                                {stopSequence.map((stop, index) => {
                                    const isNextStop = nextOrder && getNextStop()?.hubId === stop.hubId;

                                    return (
                                        <Paper
                                            key={stop.id || index}
                                            elevation={isNextStop ? 3 : 1}
                                            sx={{
                                                mb: 1,
                                                p: 1,
                                                border: isNextStop ? '2px solid #4caf50' : 'none',
                                                position: 'relative'
                                            }}
                                        >
                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                <Typography
                                                    variant="h6"
                                                    sx={{
                                                        width: 24,
                                                        height: 24,
                                                        borderRadius: '50%',
                                                        bgcolor: isNextStop ? 'success.main' : 'primary.main',
                                                        color: 'white',
                                                        display: 'flex',
                                                        alignItems: 'center',
                                                        justifyContent: 'center',
                                                        mr: 1,
                                                        fontSize: '0.875rem'
                                                    }}
                                                >
                                                    {index + 1}
                                                </Typography>
                                                <Box sx={{ flexGrow: 1 }}>
                                                    <Typography variant="subtitle2">
                                                        {stop.hubName}
                                                    </Typography>
                                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                        <PlaceIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                                                        <Typography variant="body2" color="text.secondary" noWrap>
                                                            {stop.hubAddress || 'No address available'}
                                                        </Typography>
                                                    </Box>
                                                </Box>
                                                {isNextStop && (
                                                    <Chip
                                                        label="NEXT STOP"
                                                        color="success"
                                                        size="small"
                                                        sx={{ ml: 1 }}
                                                    />
                                                )}
                                            </Box>
                                            <Typography variant="caption" color="text.secondary">
                                                Est. Wait Time: {stop.estimatedWaitTime} min
                                            </Typography>
                                        </Paper>
                                    );
                                })}
                            </List>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={8}>
                    <Card sx={{ height: 'calc(100vh - 200px)', minHeight: 500 }}>
                        <CardContent sx={{ height: '100%', p: 1 }}>
                            {mapPoints.length > 0 ? (
                                <EnhancedMap
                                    points={mapPoints}
                                    assignments={assignments}
                                    onNextOrder={(order) => setNextOrder(order)}
                                    nextOrder={nextOrder}
                                    hub={hub}
                                />
                            ) : (
                                <Box
                                    display="flex"
                                    justifyContent="center"
                                    alignItems="center"
                                    height="100%"
                                >
                                    <Typography>No map points available</Typography>
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default DriverRouteMap;