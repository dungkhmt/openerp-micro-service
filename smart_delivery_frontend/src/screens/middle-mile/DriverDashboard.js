import React, { useState, useEffect } from 'react';
import { Box, Grid, Typography, Card, CardContent, Button, CircularProgress, Chip, Divider } from '@mui/material';
import { useSelector } from 'react-redux';
import { request } from 'api';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import RouteIcon from '@mui/icons-material/Route';
import InventoryIcon from '@mui/icons-material/Inventory';
import PersonIcon from '@mui/icons-material/Person';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import { useHistory } from 'react-router-dom';
import { errorNoti, successNoti } from 'utils/notification';

const DriverDashboard = () => {
    const [loading, setLoading] = useState(true);
    const [vehicle, setVehicle] = useState(null);
    const username = useSelector((state) => state.auth.username);
    const [assignments, setAssignments] = useState([]);
    const [currentOrders, setCurrentOrders] = useState([]);
    const [pendingOrders, setPendingOrders] = useState([]);
    const [hubDetails, setHubDetails] = useState({});
    const [driverId, setDriverId] = useState(null);
    const history = useHistory();
    const authState = useSelector((state) => state.auth);

    // New state for weekday selection
    const [selectedDay, setSelectedDay] = useState(new Date().getDay() || 1); // Default to today or Sunday if today is 0
    const [filteredAssignments, setFilteredAssignments] = useState([]);

    // Map numeric day of week to string
    const dayOfWeekMap = {
        1: 'MONDAY',
        2: 'TUESDAY',
        3: 'WEDNESDAY',
        4: 'THURSDAY',
        5: 'FRIDAY',
        6: 'SATURDAY',
        7: 'SUNDAY'
    };

    // Map for displaying proper names
    const dayDisplayNames = {
        1: 'Monday',
        2: 'Tuesday',
        3: 'Wednesday',
        4: 'Thursday',
        5: 'Friday',
        6: 'Saturday',
        7: 'Sunday'
    };

    useEffect(() => {
        async function fetchId() {
            await request(
                "get",
                `/user/get-driver/${username}`,
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
                await request('get', `/smdeli/schedule-assignments/driver/get`, (res) => {
                    setAssignments(res.data || []);
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

    // Effect to filter assignments by selected day of week
    useEffect(() => {
        if (assignments.length > 0) {
            const selectedDayString = dayOfWeekMap[selectedDay];
            const filtered = assignments.filter(
                assignment => assignment.routeScheduleDto?.dayOfWeek === selectedDayString
            );
            setFilteredAssignments(filtered);
        }
    }, [selectedDay, assignments]);

    // Get assignment count for a day of week
    const getAssignmentCountForDay = (day) => {
        const dayString = dayOfWeekMap[day];
        return assignments.filter(assignment => assignment.dayOfWeek === dayString).length;
    };

    // Check if day has assignments
    const hasAssignmentsForDay = (day) => {
        return getAssignmentCountForDay(day) > 0;
    };

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
                 Dashboard
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

                {/* Weekly Assignment View - New section that replaces "Assigned Assignments" */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <CalendarTodayIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Typography variant="h6">
                                    Weekly Assignments
                                </Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />

                            {/* Simple 7 day of week selector */}
                            <Box sx={{ mb: 3 }}>
                                <Grid container spacing={1}>
                                    {[1, 2, 3, 4, 5, 6, 7].map((day) => (
                                        <Grid item xs={12/7} key={day}>
                                            <Button
                                                fullWidth
                                                variant={selectedDay === day ? "contained" : "outlined"}
                                                color="primary"
                                                sx={{
                                                    height: 60,
                                                    position: 'relative',
                                                    borderWidth: hasAssignmentsForDay(day) ? 2 : 1,
                                                    borderColor: hasAssignmentsForDay(day) ? 'primary.main' : undefined
                                                }}
                                                onClick={() => setSelectedDay(day)}
                                            >
                                                <Typography>{dayDisplayNames[day]}</Typography>
                                                {hasAssignmentsForDay(day) && (
                                                    <Chip
                                                        label={getAssignmentCountForDay(day)}
                                                        size="small"
                                                        color="primary"
                                                        sx={{
                                                            position: 'absolute',
                                                            top: 5,
                                                            right: 5,
                                                            height: 16,
                                                            minWidth: 16,
                                                            fontSize: '0.6rem',
                                                            p: 0,
                                                            bgcolor: selectedDay === day ? 'primary.light' : 'primary.main',
                                                            color: selectedDay === day ? 'primary.main' : 'primary.contrastText',
                                                        }}
                                                    />
                                                )}
                                            </Button>
                                        </Grid>
                                    ))}
                                </Grid>
                            </Box>

                            {/* Selected day assignments */}
                            <Box>
                                <Typography variant="h6" gutterBottom>
                                    Assignments for {dayDisplayNames[selectedDay]}
                                </Typography>
                                <Divider sx={{ mb: 2 }} />

                                {filteredAssignments.length > 0 ? (
                                    <Grid container spacing={2}>
                                        {filteredAssignments.map((assignment) => (
                                            <Grid item xs={12} md={6} key={assignment.id}>
                                                <Card variant="outlined">
                                                    <CardContent>
                                                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                                            <LocalShippingIcon color="primary" sx={{ mr: 1 }} fontSize="small" />
                                                            <Typography variant="subtitle1">
                                                                Vehicle: {assignment.vehiclePlateNumber}
                                                            </Typography>
                                                        </Box>

                                                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                                            Route Code: {assignment.routeDto?.routeCode}
                                                        </Typography>

                                                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                                            Route Name: {assignment.routeDto?.routeName}
                                                        </Typography>

                                                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Schedule: {assignment.routeScheduleDto?.startTime?.substring(0,5)} - {assignment.routeScheduleDto?.endTime?.substring(0,5)}
                                                            </Typography>
                                                        </Box>

                                                        {assignment.startTime && assignment.endTime && (
                                                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                                                <Typography variant="body2" color="text.secondary">
                                                                    Time:
                                                                </Typography>
                                                                <Typography variant="body2">
                                                                    {assignment.startTime} - {assignment.endTime}
                                                                </Typography>
                                                            </Box>
                                                        )}

                                                        <Box sx={{ mt: 2, display: 'flex', justifyContent: 'right' }}>
                                                            <Button
                                                                variant="contained"
                                                                size="small"
                                                                color="primary"                                                                onClick={() => handleViewRoute(assignment.routeScheduleId)}

                                                                onClick={() => handleViewRoute(assignment.routeScheduleId)}

                                                            >
                                                                View Details
                                                            </Button>
                                                        </Box>
                                                    </CardContent>
                                                </Card>
                                            </Grid>
                                        ))}
                                    </Grid>
                                ) : (
                                    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 4 }}>
                                        <CalendarTodayIcon sx={{ fontSize: 60, color: 'text.disabled', mb: 2 }} />
                                        <Typography variant="h6" color="text.secondary">
                                            No assignments for {dayDisplayNames[selectedDay]}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Select a different day or check back later
                                        </Typography>
                                    </Box>
                                )}
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>


            </Grid>
        </Box>
    );
};

export default DriverDashboard;