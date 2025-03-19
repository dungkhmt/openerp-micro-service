import React, { useState, useEffect } from 'react';
import { Box, Grid, Typography, Card, CardContent, Button, CircularProgress, Chip, Divider } from '@mui/material';
import { useSelector } from 'react-redux';
import { request } from 'api';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import RouteIcon from '@mui/icons-material/Route';
import InventoryIcon from '@mui/icons-material/Inventory';
import PersonIcon from '@mui/icons-material/Person';
import { useHistory } from 'react-router-dom';
import { errorNoti, successNoti } from 'utils/notification';

const DriverDashboard = () => {
    const [loading, setLoading] = useState(true);
    const [vehicle, setVehicle] = useState(null);
    const username = useSelector((state) => state.auth.username);
    const [routes, setRoutes] = useState([]);
    const [currentOrders, setCurrentOrders] = useState([]);
    const [pendingOrders, setPendingOrders] = useState([]);
    const [hubDetails, setHubDetails] = useState({});
    const [driverId, setDriverId] = useState(null);
    const history = useHistory();
    const authState = useSelector((state) => state.auth);
    useEffect(() => {
        async function fetchId() {
            await request(
                "get",
                `/user/get-driver/${username}`
                ,
                (res) => {
                    setDriverId(res.data.id);
                }
            )


        }

        fetchId();
    }, []);
    useEffect(() => {
        const fetchDriverData = async () => {
            try {
                setLoading(true);
                // Get vehicle assigned to driver
                await request('get', `/smdeli/driver/vehicle`, (res) => {
                    setVehicle(res.data);
                });
                // Get routes assigned to the driver
                await request('get', `/smdeli/driver/routes`, (res) => {
                    setRoutes(res.data || []);
                });
                // Get current orders being delivered
                await request('get', `/smdeli/driver/current-orders`, (res) => {
                    setCurrentOrders(res.data || []);
                });
                // Get hub details
                if (authState.hubId) {
                    await request('get', `/smdeli/hubmanager/hub/${authState.hubId}`, (res) => {
                        setHubDetails(res.data);
                    });
                    // Get pending orders for pickup at the current hub
                    await request('get', `/smdeli/driver/hub/${authState.hubId}/pending-pickups`, (res) => {
                        setPendingOrders(res.data || []);
                    });
                }
                setLoading(false);
            } catch (error) {
                console.error("Error fetching driver data:", error);
                errorNoti("Failed to load driver data");
                setLoading(false);
            }
        };
        fetchDriverData();
    }, [authState.hubId]);

    const handleViewOrders = (routeVehicleId) => {
        history.push(`/middle-mile/driver/orders/${routeVehicleId}`);
    };

    const handleViewRoute = (routeVehicleId) => {
        history.push(`/middle-mile/driver/route/${routeVehicleId}`);
    };

    const handleStartTrip = (routeVehicleId) => {
        request(
            'put',
            `/smdeli/middle-mile/vehicle-assignments/${routeVehicleId}/start`,
            () => {
                successNoti("Trip started successfully");
                // Refresh data
                window.location.reload();
            },
            {
                401: () => errorNoti("Unauthorized action"),
                400: () => errorNoti("Unable to start trip")
            }
        );
    };

    const handleCompleteTrip = (routeVehicleId) => {
        if (window.confirm("Are you sure you want to complete this trip? This will mark all orders as delivered.")) {
            request(
                'post',
                `/smdeli/driver/trips/${routeVehicleId}/complete`,
                () => {
                    successNoti("Trip completed successfully");
                    // Refresh data
                    window.location.reload();
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
            <Typography variant="h4" gutterBottom>
                Driver Dashboard
            </Typography>
            <Grid container spacing={3}>
                {/* Vehicle Information */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <LocalShippingIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Assigned Vehicle
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            {vehicle ? (
                                <Grid container spacing={2}>
                                    <Grid item xs={6}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Vehicle ID
                                        </Typography>
                                        <Typography variant="body1" gutterBottom>
                                            {vehicle.vehicleId}
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={6}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Plate Number
                                        </Typography>
                                        <Typography variant="body1" fontWeight="bold" gutterBottom>
                                            {vehicle.plateNumber}
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={6}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Vehicle Type
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
                                    <Grid item xs={12}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Model
                                        </Typography>
                                        <Typography variant="body1" gutterBottom>
                                            {vehicle.manufacturer} {vehicle.model} ({vehicle.yearOfManufacture || 'N/A'})
                                        </Typography>
                                    </Grid>
                                </Grid>
                            ) : (
                                <Typography variant="body1" color="text.secondary" align="center">
                                    No vehicle assigned to you
                                </Typography>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
                {/* Driver Information & Current Hub */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <PersonIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Driver Information
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Driver Name
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {authState.username}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Role
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {authState.role}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Current Hub
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {hubDetails?.name || 'Not assigned to a hub'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Hub Address
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {hubDetails?.address || 'N/A'}
                                    </Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                </Grid>
                {/* Active Routes */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <RouteIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Assigned Routes
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            {routes.length > 0 ? (
                                <Grid container spacing={2}>
                                    {routes.map((route) => (
                                        <Grid item xs={12} md={6} key={route.routeVehicleDto?.id}>
                                            <Card variant="outlined">
                                                <CardContent>
                                                    <Typography variant="h6" gutterBottom>
                                                        {route.routeName} ({route.routeCode})
                                                    </Typography>
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                                        <Typography variant="body2" color="text.secondary">
                                                            Direction:
                                                        </Typography>
                                                        <Typography variant="body2" fontWeight="bold">
                                                            {route.routeVehicleDto?.direction}
                                                        </Typography>
                                                    </Box>
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                                        <Typography variant="body2" color="text.secondary">
                                                            Status:
                                                        </Typography>
                                                        <Chip
                                                            label={route.routeVehicleDto?.status || 'PLANNED'}
                                                            color={
                                                                route.routeVehicleDto?.status === 'IN_PROGRESS' ? 'warning' :
                                                                    route.routeVehicleDto?.status === 'COMPLETED' ? 'success' : 'primary'
                                                            }
                                                            size="small"
                                                        />
                                                    </Box>
                                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                                        <Typography variant="body2" color="text.secondary">
                                                            Created:
                                                        </Typography>
                                                        <Typography variant="body2">
                                                            {new Date(route.routeVehicleDto?.createdAt).toLocaleString()}
                                                        </Typography>
                                                    </Box>
                                                    <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between' }}>
                                                        <Button
                                                            variant="outlined"
                                                            size="small"
                                                            onClick={() => handleViewRoute(route.routeVehicleDto?.id)}
                                                        >
                                                            View Route
                                                        </Button>
                                                        <Button
                                                            variant="outlined"
                                                            size="small"
                                                            onClick={() => handleViewOrders(route.routeVehicleDto?.id)}
                                                        >
                                                            View Orders
                                                        </Button>
                                                        {route.routeVehicleDto?.status === 'PLANNED' && (
                                                            <Button
                                                                variant="contained"
                                                                color="primary"
                                                                size="small"
                                                                onClick={() => handleStartTrip(route.routeVehicleDto?.id)}
                                                            >
                                                                Start Trip
                                                            </Button>
                                                        )}
                                                        {route.routeVehicleDto?.status === 'IN_PROGRESS' && (
                                                            <Button
                                                                variant="contained"
                                                                color="success"
                                                                size="small"
                                                                onClick={() => handleCompleteTrip(route.routeVehicleDto?.id)}
                                                            >
                                                                Complete Trip
                                                            </Button>
                                                        )}
                                                    </Box>
                                                </CardContent>
                                            </Card>
                                        </Grid>
                                    ))}
                                </Grid>
                            ) : (
                                <Typography variant="body1" color="text.secondary" align="center">
                                    No routes assigned
                                </Typography>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
                {/* Current Orders */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <InventoryIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Current Orders ({currentOrders.length})
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            {currentOrders.length > 0 ? (
                                <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
                                    {currentOrders.map((order) => (
                                        <Card variant="outlined" sx={{ mb: 1, p: 1 }} key={order.id}>
                                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                                <Typography variant="subtitle2" noWrap>
                                                    Order #{order.id.substring(0, 8)}...
                                                </Typography>
                                                <Chip
                                                    label={order.status}
                                                    color={order.status === 'DELIVERING' ? 'warning' : 'primary'}
                                                    size="small"
                                                />
                                            </Box>
                                            <Box sx={{ mt: 1 }}>
                                                <Typography variant="body2" color="text.secondary">
                                                    From: {order.senderName} ({order.senderAddress})
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    To: {order.recipientName} ({order.recipientAddress})
                                                </Typography>
                                            </Box>
                                        </Card>
                                    ))}
                                </Box>
                            ) : (
                                <Typography variant="body1" color="text.secondary" align="center">
                                    No orders in transit
                                </Typography>
                            )}
                            {currentOrders.length > 0 && (
                                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
                                    <Button
                                        variant="contained"
                                        onClick={() => history.push('/driver/current-orders')}
                                    >
                                        View All Current Orders
                                    </Button>
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
                {/* Pending Pickups */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <InventoryIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Pending Pickups at Hub ({pendingOrders.length})
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            {pendingOrders.length > 0 ? (
                                <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
                                    {pendingOrders.map((order) => (
                                        <Card variant="outlined" sx={{ mb: 1, p: 1 }} key={order.id}>
                                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                                <Typography variant="subtitle2" noWrap>
                                                    Order #{order.id.substring(0, 8)}...
                                                </Typography>
                                                <Chip
                                                    label={order.status}
                                                    color="info"
                                                    size="small"
                                                />
                                            </Box>
                                            <Box sx={{ mt: 1 }}>
                                                <Typography variant="body2" color="text.secondary">
                                                    Destination: {order.recipientName} ({order.recipientAddress})
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    Created: {new Date(order.createdAt).toLocaleString()}
                                                </Typography>
                                            </Box>
                                        </Card>
                                    ))}
                                </Box>
                            ) : (
                                <Typography variant="body1" color="text.secondary" align="center">
                                    No orders waiting for pickup
                                </Typography>
                            )}
                            {pendingOrders.length > 0 && (
                                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
                                    <Button
                                        variant="contained"
                                        onClick={() => history.push('/driver/pending-pickups')}
                                    >
                                        View All Pending Pickups
                                    </Button>
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default DriverDashboard;