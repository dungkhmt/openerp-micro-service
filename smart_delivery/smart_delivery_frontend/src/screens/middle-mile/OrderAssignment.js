import React, { useState, useEffect } from 'react';
import { Box, Grid, Typography, Button, Card, CardContent, CircularProgress } from '@mui/material';
import StandardTable from 'components/StandardTable';
import { request } from 'api';
import { useSelector } from "react-redux";
import { successNoti, errorNoti } from 'utils/notification';
import RouteIcon from '@mui/icons-material/Route';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import DriveEtaIcon from '@mui/icons-material/DriveEta';
import PersonIcon from '@mui/icons-material/Person';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const OrderAssignment = () => {
    const [loading, setLoading] = useState(true);
    const [orders, setOrders] = useState([]);
    const [drivers, setDrivers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [routes, setRoutes] = useState([]);
    const [selectedOrders, setSelectedOrders] = useState([]);
    const [selectedDriver, setSelectedDriver] = useState(null);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [selectedRoute, setSelectedRoute] = useState(null);
    const [assignmentInProgress, setAssignmentInProgress] = useState(false);

    const hubId = useSelector((state) => state.auth.hubId);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch pending orders
                const ordersRes = await request(
                    "get",
                    `/smdeli/middle-mile/orders/pending`,
                    (res) => res.data,
                    { 401: () => errorNoti("Unauthorized access") }
                );
                setOrders(ordersRes || []);

                // Fetch available drivers
                const driversRes = await request(
                    "get",
                    `/smdeli/humanresource/driver/hub/${hubId}`,
                    (res) => res.data,
                    { 401: () => errorNoti("Unauthorized access") }
                );
                setDrivers(driversRes || []);

                // Fetch available vehicles
                const vehiclesRes = await request(
                    "get",
                    `/smdeli/vehicle/getAll/${hubId}`,
                    (res) => res.data,
                    { 401: () => errorNoti("Unauthorized access") }
                );
                setVehicles(vehiclesRes || []);

                // Fetch available routes
                const routesRes = await request(
                    "get",
                    `/smdeli/middle-mile/routes`,
                    (res) => res.data,
                    { 401: () => errorNoti("Unauthorized access") }
                );
                setRoutes(routesRes || []);

            } catch (error) {
                console.error("Error fetching data:", error);
                errorNoti("Failed to load required data");
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [hubId]);

    const handleOrderSelection = (selected) => {
        setSelectedOrders(selected);
    };

    const handleDriverSelection = (driverId) => {
        setSelectedDriver(driverId);
    };

    const handleVehicleSelection = (vehicleId) => {
        setSelectedVehicle(vehicleId);
    };

    const handleRouteSelection = (routeId) => {
        setSelectedRoute(routeId);
    };

    const handleAssignOrders = async () => {
        if (!selectedDriver || !selectedVehicle || !selectedRoute || selectedOrders.length === 0) {
            errorNoti("Please select driver, vehicle, route, and at least one order");
            return;
        }

        setAssignmentInProgress(true);

        try {
            const result = await request(
                "post",
                "/smdeli/middle-mile/trips/assign",
                (res) => res.data,
                {
                    401: () => errorNoti("Unauthorized action"),
                    400: () => errorNoti("Invalid assignment parameters")
                },
                {
                    driverId: selectedDriver,
                    vehicleId: selectedVehicle,
                    routeId: selectedRoute,
                    orderIds: selectedOrders
                }
            );

            successNoti("Orders assigned successfully");

            // Refresh orders
            const updatedOrders = await request(
                "get",
                `/smdeli/middle-mile/orders/pending`,
                (res) => res.data,
                { 401: () => {} }
            );
            setOrders(updatedOrders || []);

            // Reset selections
            setSelectedOrders([]);
            setSelectedDriver(null);
            setSelectedVehicle(null);
            setSelectedRoute(null);

        } catch (error) {
            console.error("Error assigning orders:", error);
            errorNoti("Failed to assign orders");
        } finally {
            setAssignmentInProgress(false);
        }
    };

    const orderColumns = [
        { title: "Order ID", field: "id" },
        { title: "Origin Hub", field: "originHubName" },
        { title: "Destination Hub", field: "destinationHubName" },
        { title: "Status", field: "status" },
        { title: "Created At", field: "createdAt",
            render: rowData => new Date(rowData.createdAt).toLocaleString()
        },
        { title: "Priority", field: "priority" }
    ];

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Order Assignment
            </Typography>

            <Grid container spacing={3}>
                <Grid item xs={12} md={8}>
                    <StandardTable
                        title="Pending Orders"
                        columns={orderColumns}
                        data={orders}
                        options={{
                            selection: true,
                            pageSize: 10,
                            search: true,
                            sorting: true,
                        }}
                        onSelectionChange={handleOrderSelection}
                    />
                </Grid>

                <Grid item xs={12} md={4}>
                    <Box sx={{ mb: 3 }}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    <PersonIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                    Select Driver
                                </Typography>
                                <Grid container spacing={1}>
                                    {drivers.map(driver => (
                                        <Grid item xs={6} key={driver.id}>
                                            <Button
                                                variant={selectedDriver === driver.id ? "contained" : "outlined"}
                                                fullWidth
                                                onClick={() => handleDriverSelection(driver.id)}
                                                sx={{ justifyContent: 'flex-start', textTransform: 'none', mb: 1 }}
                                            >
                                                {driver.name}
                                            </Button>
                                        </Grid>
                                    ))}
                                </Grid>
                            </CardContent>
                        </Card>
                    </Box>

                    <Box sx={{ mb: 3 }}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    <DriveEtaIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                    Select Vehicle
                                </Typography>
                                <Grid container spacing={1}>
                                    {vehicles.map(vehicle => (
                                        <Grid item xs={6} key={vehicle.vehicleId}>
                                            <Button
                                                variant={selectedVehicle === vehicle.vehicleId ? "contained" : "outlined"}
                                                fullWidth
                                                onClick={() => handleVehicleSelection(vehicle.vehicleId)}
                                                sx={{ justifyContent: 'flex-start', textTransform: 'none', mb: 1 }}
                                            >
                                                {vehicle.plateNumber}
                                            </Button>
                                        </Grid>
                                    ))}
                                </Grid>
                            </CardContent>
                        </Card>
                    </Box>

                    <Box sx={{ mb: 3 }}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    <RouteIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                    Select Route
                                </Typography>
                                <Grid container spacing={1}>
                                    {routes.map(route => (
                                        <Grid item xs={6} key={route.routeId}>
                                            <Button
                                                variant={selectedRoute === route.routeId ? "contained" : "outlined"}
                                                fullWidth
                                                onClick={() => handleRouteSelection(route.routeId)}
                                                sx={{ justifyContent: 'flex-start', textTransform: 'none', mb: 1 }}
                                            >
                                                {route.routeName}
                                            </Button>
                                        </Grid>
                                    ))}
                                </Grid>
                            </CardContent>
                        </Card>
                    </Box>

                    <Box sx={{ textAlign: 'center' }}>
                        <Button
                            variant="contained"
                            color="primary"
                            size="large"
                            startIcon={assignmentInProgress ? <CircularProgress size={20} color="inherit" /> : <LocalShippingIcon />}
                            onClick={handleAssignOrders}
                            disabled={!selectedDriver || !selectedVehicle || !selectedRoute || selectedOrders.length === 0 || assignmentInProgress}
                            sx={{ px: 4, py: 1.5 }}
                        >
                            {assignmentInProgress ? "Assigning..." : "Assign Orders"}
                        </Button>

                        {selectedOrders.length > 0 && selectedDriver && selectedVehicle && selectedRoute && (
                            <Box sx={{ mt: 2, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                <CheckCircleIcon color="success" sx={{ mr: 1 }} />
                                <Typography variant="body2" color="success.main">
                                    {`Ready to assign ${selectedOrders.length} order(s)`}
                                </Typography>
                            </Box>
                        )}
                    </Box>
                </Grid>
            </Grid>
        </Box>
    );
};

export default OrderAssignment;