import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import {
    Box, Grid, Typography, Card, CardContent, Divider,
    Chip, CircularProgress, Button, Paper, List, ListItem,
    ListItemText, ListItemIcon, IconButton, Tooltip
} from '@mui/material';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';

// Icons
import MapIcon from '@mui/icons-material/Map';
import DirectionsIcon from '@mui/icons-material/Directions';
import PlaceIcon from '@mui/icons-material/Place';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import TimerIcon from '@mui/icons-material/Timer';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

const RouteDetail = () => {
    const { routeId } = useParams();
    const history = useHistory();
    const [loading, setLoading] = useState(true);
    const [route, setRoute] = useState(null);
    const [stops, setStops] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [deleteConfirmation, setDeleteConfirmation] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [copySuccess, setCopySuccess] = useState(false);

    useEffect(() => {
        const fetchRouteData = async () => {
            try {
                // Fetch route details
                const fetchRouteData = await request(
                    "get",
                    `/smdeli/middle-mile/routes/${routeId}`,
                    (res) => setRoute(res.data),
                    {
                        401: () => errorNoti("Unauthorized access"),
                        404: () => {
                            errorNoti("Route not found");
                            history.push('/middle-mile/trips');
                        }
                    }
                );

                // Fetch route stops
                const stopsData = await request(
                    "get",
                    `/smdeli/middle-mile/routes/${routeId}/stops`,
                    (res) => setStops(res.data || []),
                    { 401: () => {} }
                );


                // Fetch assigned vehicles
                const vehiclesData = await request(
                    "get",
                    `/smdeli/middle-mile/routes/${routeId}/vehicles`,
                    (res) => setVehicles(res.data || []),
                    { 401: () => {} }
                );


            } catch (error) {
                console.error("Error fetching route data:", error);
                errorNoti("Failed to load route information");
            } finally {
                setLoading(false);
            }
        };

        fetchRouteData();
    }, [routeId, history]);

    const handleEdit = () => {
        history.push(`/middle-mile/routes/edit/${routeId}`);
    };

    const handleDelete = async () => {
        setDeleting(true);
        try {
            await request(
                "delete",
                `/smdeli/middle-mile/routes/${routeId}`,
                () => {
                    successNoti("Route deleted successfully");
                    history.push('/middle-mile/trips');                },
                {
                    401: () => errorNoti("Unauthorized action"),
                    404: () => errorNoti("Route not found"),
                    400: () => errorNoti("Cannot delete route with active trips")
                }
            );
        } catch (error) {
            console.error("Error deleting route:", error);
            errorNoti("Failed to delete route");
        } finally {
            setDeleting(false);
            setDeleteConfirmation(false);
        }
    };

    const handleCopyRouteCode = () => {
        if (route && route.routeCode) {
            navigator.clipboard.writeText(route.routeCode)
                .then(() => {
                    setCopySuccess(true);
                    successNoti("Route code copied to clipboard");
                    setTimeout(() => setCopySuccess(false), 2000);
                })
                .catch(err => {
                    console.error('Could not copy text: ', err);
                    errorNoti("Failed to copy route code");
                });
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'ACTIVE':
                return 'success';
            case 'INACTIVE':
                return 'error';
            case 'SCHEDULED':
                return 'info';
            default:
                return 'default';
        }
    };

    const getDirectionColor = (direction) => {
        switch (direction.toUpperCase()) {
            case 'OUTBOUND':
                return '#2196f3';
            case 'INBOUND':
                return '#4caf50';
            case 'BIDIRECTIONAL':
                return '#9c27b0';
            default:
                return '#757575';
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <CircularProgress />
            </Box>
        );
    }

    if (!route) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <Typography variant="h6" color="error">
                    Route not found
                </Typography>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <IconButton onClick={() => history.push('/middle-mile/trips')} sx={{ mr: 2 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h4">
                    Route Details
                </Typography>
                <Box sx={{ flexGrow: 1 }} />
                <Button
                    variant="outlined"
                    startIcon={<EditIcon />}
                    onClick={handleEdit}
                    sx={{ mr: 2 }}
                >
                    Edit
                </Button>
                {deleteConfirmation ? (
                    <>
                        <Button
                            variant="contained"
                            color="error"
                            onClick={handleDelete}
                            disabled={deleting}
                            sx={{ mr: 1 }}
                        >
                            {deleting ? 'Deleting...' : 'Confirm Delete'}
                        </Button>
                        <Button
                            variant="outlined"
                            onClick={() => setDeleteConfirmation(false)}
                            disabled={deleting}
                        >
                            Cancel
                        </Button>
                    </>
                ) : (
                    <Button
                        variant="outlined"
                        color="error"
                        startIcon={<DeleteIcon />}
                        onClick={() => setDeleteConfirmation(true)}
                    >
                        Delete
                    </Button>
                )}
            </Box>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Route Information
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Route Code
                                    </Typography>
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <Typography variant="body1" sx={{ mb: 2 }}>
                                            {route.routeCode}
                                        </Typography>
                                        <IconButton
                                            size="small"
                                            onClick={handleCopyRouteCode}
                                            color={copySuccess ? "success" : "default"}
                                            sx={{ ml: 1, mb: 2 }}
                                        >
                                            <ContentCopyIcon fontSize="small" />
                                        </IconButton>
                                    </Box>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Route Name
                                    </Typography>
                                    <Typography variant="body1" sx={{ mb: 2 }}>
                                        {route.routeName}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Status
                                    </Typography>
                                    <Chip
                                        label={route.status}
                                        color={getStatusColor(route.status)}
                                        size="small"
                                        sx={{ mt: 0.5 }}
                                    />
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Total Distance
                                    </Typography>
                                    <Typography variant="body1" sx={{ mb: 2 }}>
                                        {route.totalDistance ? `${route.totalDistance.toFixed(1)} km` : 'N/A'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Estimated Duration
                                    </Typography>
                                    <Typography variant="body1" sx={{ mb: 2 }}>
                                        {route.estimatedDuration ? `${Math.floor(route.estimatedDuration / 60)}h ${route.estimatedDuration % 60}m` : 'N/A'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Description
                                    </Typography>
                                    <Typography variant="body1" sx={{ mb: 2 }}>
                                        {route.description || 'No description provided'}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Notes
                                    </Typography>
                                    <Typography variant="body1">
                                        {route.notes || 'No notes provided'}
                                    </Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>

                    <Card sx={{ mt: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                <LocalShippingIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                Assigned Vehicles
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            {vehicles.length > 0 ? (
                                vehicles.map((vehicle) => (
                                    <Paper key={vehicle.id} elevation={1} sx={{ p: 2, mb: 2 }}>
                                        <Grid container spacing={2}>
                                            <Grid item xs={6}>
                                                <Typography variant="subtitle2" color="text.secondary">
                                                    Vehicle
                                                </Typography>
                                                <Typography variant="body1">
                                                    {vehicle.vehicle ? vehicle.vehicle.plateNumber : 'N/A'}
                                                </Typography>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Typography variant="subtitle2" color="text.secondary">
                                                    Direction
                                                </Typography>
                                                <Typography variant="body1" sx={{ color: getDirectionColor(vehicle.direction) }}>
                                                    {vehicle.direction}
                                                </Typography>
                                            </Grid>
                                            {vehicle.driver && (
                                                <>
                                                    <Grid item xs={6}>
                                                        <Typography variant="subtitle2" color="text.secondary">
                                                            Driver
                                                        </Typography>
                                                        <Typography variant="body1">
                                                            {vehicle.driver.name}
                                                        </Typography>
                                                    </Grid>
                                                    <Grid item xs={6}>
                                                        <Typography variant="subtitle2" color="text.secondary">
                                                            Contact
                                                        </Typography>
                                                        <Typography variant="body1">
                                                            {vehicle.driver.phone}
                                                        </Typography>
                                                    </Grid>
                                                </>
                                            )}
                                        </Grid>
                                    </Paper>
                                ))
                            ) : (
                                <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 2 }}>
                                    No vehicles assigned to this route
                                </Typography>
                            )}

                            {/*<Button*/}
                            {/*    variant="outlined"*/}
                            {/*    fullWidth*/}
                            {/*    onClick={() => history.push(`/vehicle-assignments/new?routeId=${routeId}`)}*/}
                            {/*    sx={{ mt: 2 }}*/}
                            {/*>*/}
                            {/*    Assign Vehicle*/}
                            {/*</Button>*/}
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                                <Typography variant="h6">
                                    <DirectionsIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                    Route Stops
                                </Typography>
                                <Tooltip title="View on Map">
                                    <IconButton color="primary" onClick={() => history.push(`/routes/map/${routeId}`)}>
                                        <MapIcon />
                                    </IconButton>
                                </Tooltip>
                            </Box>
                            <Divider sx={{ mb: 2 }} />

                            <List sx={{
                                width: '100%',
                                bgcolor: 'background.paper',
                                position: 'relative',
                                overflow: 'auto',
                                maxHeight: 500,
                                '& ul': { padding: 0 },
                            }}>
                                { stops.length>0 &&  stops.map((stop, index) => (
                                    <React.Fragment key={stop.id}>
                                        <ListItem alignItems="flex-start">
                                            <ListItemIcon>
                                                <Box sx={{
                                                    width: 24,
                                                    height: 24,
                                                    borderRadius: '50%',
                                                    backgroundColor: 'primary.main',
                                                    color: 'white',
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    fontWeight: 'bold',
                                                    fontSize: '14px'
                                                }}>
                                                    {index + 1}
                                                </Box>
                                            </ListItemIcon>
                                            <ListItemText
                                                primary={
                                                    <Typography variant="subtitle1">
                                                        {stop.hubName}
                                                    </Typography>
                                                }
                                                secondary={
                                                    <React.Fragment>
                                                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                            <PlaceIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                                                            <Typography variant="body2" color="text.secondary">
                                                                Hub ID: {stop.hubId}
                                                            </Typography>
                                                        </Box>
                                                        <Box sx={{ display: 'flex', alignItems: 'center', mt: 0.5 }}>
                                                            <TimerIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                                                            <Typography variant="body2" color="text.secondary">
                                                                Estimated Wait Time: {stop.estimatedWaitTime} min
                                                            </Typography>
                                                        </Box>
                                                    </React.Fragment>
                                                }
                                            />
                                        </ListItem>
                                        {index < stops.length - 1 && (
                                            <Box sx={{
                                                ml: 7,
                                                borderLeft: '2px dashed',
                                                borderColor: 'primary.light',
                                                height: 30
                                            }} />
                                        )}
                                    </React.Fragment>
                                ))}

                                {stops.length === 0 && (
                                    <ListItem>
                                        <ListItemText
                                            primary="No stops defined for this route"
                                            sx={{ textAlign: 'center', color: 'text.secondary' }}
                                        />
                                    </ListItem>
                                )}
                            </List>

                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
                                <Typography variant="subtitle2" color="text.secondary">
                                    Total Stops: {stops.length}
                                </Typography>
                                {route.totalDistance && route.estimatedDuration && (
                                    <Typography variant="subtitle2" color="text.secondary">
                                        {route.totalDistance.toFixed(1)} km • {Math.floor(route.estimatedDuration / 60)}h {route.estimatedDuration % 60}m
                                    </Typography>
                                )}
                            </Box>
                        </CardContent>
                    </Card>

                    <Card sx={{ mt: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Active Trips
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            {/* This section would display active trips for this route */}
                            <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 2 }}>
                                No active trips for this route
                            </Typography>

                            <Button
                                variant="contained"
                                fullWidth
                                onClick={() => history.push(`/order-assignment?routeId=${routeId}`)}
                                sx={{ mt: 2 }}
                            >
                                Assign Orders to This Route
                            </Button>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default RouteDetail;