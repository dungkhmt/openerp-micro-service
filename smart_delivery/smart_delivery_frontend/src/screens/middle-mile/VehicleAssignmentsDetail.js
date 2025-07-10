import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import {
    Box,
    Card,
    CardContent,
    Typography,
    Grid,
    Divider,
    Chip,
    Button,
    IconButton,
    Paper,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
    CircularProgress,
    Tooltip
} from '@mui/material';
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import TableSortLabel from "@mui/material/TableSortLabel";
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';

// Icons
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PersonIcon from '@mui/icons-material/Person';
import RouteIcon from '@mui/icons-material/Route';
import DirectionsIcon from '@mui/icons-material/Directions';
import PlaceIcon from '@mui/icons-material/Place';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import ScheduleIcon from '@mui/icons-material/Schedule';
import EventIcon from '@mui/icons-material/Event';
import MapIcon from '@mui/icons-material/Map';
import CommuteIcon from '@mui/icons-material/Commute';
import Loading from "../../components/common/loading/loading";

const VehicleAssignmentDetail = () => {
    const { assignmentId } = useParams();
    const history = useHistory();
    const [loading, setLoading] = useState(true);
    const [assignment, setAssignment] = useState(null);
    const [route, setRoute] = useState(null);
    const [vehicle, setVehicle] = useState(null);
    const [driver, setDriver] = useState(null);
    const [orders, setOrders] = useState([]);
    const [stopSequence, setStopSequence] = useState([]);
    const [deleteConfirmation, setDeleteConfirmation] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [dataFetchState, setDataFetchState] = useState({
        assignmentFetched: false,
        routeFetched: false,
        vehicleFetched: false,
        driverFetched: false,
        stopsFetched: false,
        ordersFetched: false
    });

    // 1. First fetch basic assignment data
    useEffect(() => {
        // Only fetch assignment if it hasn't been fetched already
        if (!dataFetchState.assignmentFetched) {
            setLoading(true);
            request(
                "get",
                `/smdeli/middle-mile/vehicle-assignments/${assignmentId}`,
                (res) => {
                    console.log("Assignment data fetched:", res.data);
                    setAssignment(res.data);
                    setDataFetchState(prevState => ({
                        ...prevState,
                        assignmentFetched: true
                    }));
                },
                {
                    401: () => {
                        errorNoti("Unauthorized access");
                    },
                    404: () => {
                        errorNoti("Assignment not found");
                        history.push('/middle-mile/trips');
                    },
                    onError: (error) => {
                        console.error("Error fetching assignment data:", error);
                        errorNoti("Failed to load assignment information");
                    }
                }
            );
        }
    }, [assignmentId, history, dataFetchState.assignmentFetched]);

    // 2. After assignment data is fetched, fetch route data
    useEffect(() => {
        if (dataFetchState.assignmentFetched && !dataFetchState.routeFetched && assignment && assignment.routeId) {
            console.log("Fetching route data for routeId:", assignment.routeId);
            request(
                "get",
                `/smdeli/middle-mile/routes/${assignment.routeId}`,
                (res) => {
                    console.log("Route data fetched:", res.data);
                    setRoute(res.data);
                    setDataFetchState(prevState => ({
                        ...prevState,
                        routeFetched: true
                    }));
                },
                {
                    401: () => {},
                    onError: (error) => {
                        console.error("Error fetching route data:", error);
                        setDataFetchState(prevState => ({
                            ...prevState,
                            routeFetched: true // Mark as fetched even on error to avoid infinite retry
                        }));
                    }
                }
            );
        } else if (dataFetchState.assignmentFetched && !assignment?.routeId) {
            // Mark as fetched if no routeId exists
            setDataFetchState(prevState => ({
                ...prevState,
                routeFetched: true
            }));
        }
    }, [assignment, dataFetchState.assignmentFetched, dataFetchState.routeFetched]);

    // 3. After assignment data is fetched, fetch vehicle data
    useEffect(() => {
        if (dataFetchState.assignmentFetched && !dataFetchState.vehicleFetched && assignment && assignment.vehicleId) {
            console.log("Fetching vehicle data for vehicleId:", assignment.vehicleId);
            request(
                "get",
                `/smdeli/vehicle/${assignment.vehicleId}`,
                (res) => {
                    console.log("Vehicle data fetched:", res.data);
                    setVehicle(res.data);
                    setDataFetchState(prevState => ({
                        ...prevState,
                        vehicleFetched: true
                    }));
                },
                {
                    401: () => {},
                    onError: (error) => {
                        console.error("Error fetching vehicle data:", error);
                        setDataFetchState(prevState => ({
                            ...prevState,
                            vehicleFetched: true
                        }));
                    }
                }
            );
        } else if (dataFetchState.assignmentFetched && !assignment?.vehicleId) {
            // Mark as fetched if no vehicleId exists
            setDataFetchState(prevState => ({
                ...prevState,
                vehicleFetched: true
            }));
        }
    }, [assignment, dataFetchState.assignmentFetched, dataFetchState.vehicleFetched]);

    // 4. After assignment data is fetched, fetch driver data
    useEffect(() => {
        if (dataFetchState.assignmentFetched && !dataFetchState.driverFetched && assignment && assignment.driverId) {
            console.log("Fetching driver data for driverId:", assignment.driverId);
            request(
                "get",
                `/smdeli/humanresource/driver/${assignment.driverId}`,
                (res) => {
                    console.log("Driver data fetched:", res.data);
                    setDriver(res.data);
                    setDataFetchState(prevState => ({
                        ...prevState,
                        driverFetched: true
                    }));
                },
                {
                    401: () => {},
                    onError: (error) => {
                        console.error("Error fetching driver data:", error);
                        setDataFetchState(prevState => ({
                            ...prevState,
                            driverFetched: true
                        }));
                    }
                }
            );
        } else if (dataFetchState.assignmentFetched && !assignment?.driverId) {
            // Mark as fetched if no driverId exists
            setDataFetchState(prevState => ({
                ...prevState,
                driverFetched: true
            }));
        }
    }, [assignment, dataFetchState.assignmentFetched, dataFetchState.driverFetched]);

    // 5. After route data is fetched, fetch route stops
    useEffect(() => {
        if (dataFetchState.routeFetched && !dataFetchState.stopsFetched && route && route.routeId) {
            console.log("Fetching route stops for routeId:", route.routeId);
            request(
                "get",
                `/smdeli/middle-mile/routes/${route.routeId}/stops`,
                (res) => {
                    console.log("Route stops fetched:", res.data);
                    setStopSequence(res.data || []);
                    setDataFetchState(prevState => ({
                        ...prevState,
                        stopsFetched: true
                    }));
                },
                {
                    401: () => {},
                    onError: (error) => {
                        console.error("Error fetching route stops:", error);
                        setDataFetchState(prevState => ({
                            ...prevState,
                            stopsFetched: true
                        }));
                    }
                }
            );
        } else if (dataFetchState.routeFetched && (!route || !route.routeId)) {
            // Mark as fetched if no route data exists
            setDataFetchState(prevState => ({
                ...prevState,
                stopsFetched: true
            }));
        }
    }, [route, dataFetchState.routeFetched, dataFetchState.stopsFetched]);

    // 6. After assignment data is fetched, fetch order data
    useEffect(() => {
        if (dataFetchState.assignmentFetched && !dataFetchState.ordersFetched) {
            console.log("Fetching orders for assignmentId:", assignmentId);
            request(
                "get",
                `/smdeli/middle-mile/vehicle-assignments/${assignmentId}/orders`,
                (res) => {
                    console.log("Orders fetched:", res.data);
                    setOrders(res.data || []);
                    setDataFetchState(prevState => ({
                        ...prevState,
                        ordersFetched: true
                    }));
                },
                {
                    401: () => {},
                    onError: (error) => {
                        console.error("Error fetching orders:", error);
                        setDataFetchState(prevState => ({
                            ...prevState,
                            ordersFetched: true
                        }));
                    }
                }
            );
        }
    }, [assignmentId, dataFetchState.assignmentFetched, dataFetchState.ordersFetched]);

    // Set loading state based on all fetches completing
    useEffect(() => {
        const { assignmentFetched, routeFetched, vehicleFetched, driverFetched, stopsFetched, ordersFetched } = dataFetchState;
        if (assignmentFetched && routeFetched && vehicleFetched && driverFetched && stopsFetched && ordersFetched) {
            console.log("All data fetched, ending loading state");
            setLoading(false);
        }
    }, [dataFetchState]);

    const handleBack = () => {
        history.goBack();
    };

    const handleEdit = () => {
        history.push(`/middle-mile/vehicle-assignments/edit/${assignmentId}`);
    };

    const handleDelete = async () => {
        setDeleting(true);
        try {
            await request(
                "delete",
                `/smdeli/middle-mile/vehicle-assignments/${assignmentId}`,
                () => {
                    successNoti("Vehicle assignment deleted successfully");
                    history.push('/middle-mile/trips');
                },
                {
                    401: () => errorNoti("Unauthorized action"),
                    404: () => errorNoti("Assignment not found"),
                    400: () => errorNoti("Cannot delete assignment with active trips")
                }
            );
        } catch (error) {
            console.error("Error deleting assignment:", error);
            errorNoti("Failed to delete assignment");
        } finally {
            setDeleting(false);
            setDeleteConfirmation(false);
        }
    };

    const handleStartTrip = async () => {
        try {
            await request(
                "put",
                `/smdeli/middle-mile/vehicle-assignments/${assignmentId}/start`,
                () => {
                    successNoti("Trip started successfully");
                    // Refresh data
                    setAssignment({
                        ...assignment,
                        status: "IN_PROGRESS"
                    });
                },
                {
                    401: () => errorNoti("Unauthorized action"),
                    400: () => errorNoti("Unable to start trip")
                }
            );
        } catch (error) {
            console.error("Error starting trip:", error);
            errorNoti("Failed to start trip");
        }
    };

    const handleCompleteTrip = async () => {
        try {
            await request(
                "put",
                `/smdeli/middle-mile/vehicle-assignments/${assignmentId}/complete`,
                () => {
                    successNoti("Trip completed successfully");
                    // Refresh data
                    setAssignment({
                        ...assignment,
                        status: "COMPLETED"
                    });
                },
                {
                    401: () => errorNoti("Unauthorized action"),
                    400: () => errorNoti("Unable to complete trip")
                }
            );
        } catch (error) {
            console.error("Error completing trip:", error);
            errorNoti("Failed to complete trip");
        }
    };

    const getStatusColor = (status) => {
        if (!status) return "default";

        switch(status) {
            case 'PLANNED':
                return 'info';
            case 'IN_PROGRESS':
                return 'warning';
            case 'COMPLETED':
                return 'success';
            case 'CANCELLED':
                return 'error';
            default:
                return 'default';
        }
    };

    const getStatusText = (status) => {
        if (!status) return "Unknown";

        switch(status) {
            case 'PLANNED':
                return 'Planned';
            case 'IN_PROGRESS':
                return 'In Progress';
            case 'COMPLETED':
                return 'Completed';
            case 'CANCELLED':
                return 'Cancelled';
            default:
                return status;
        }
    };

    const getDirectionColor = (direction) => {
        switch(direction) {
            case 'OUTBOUND':
                return '#2196f3';
            case 'INBOUND':
                return '#4caf50';
            case 'LOOP':
                return '#9c27b0';
            default:
                return '#757575';
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return "Not scheduled";
        return new Date(dateString).toLocaleString();
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <CircularProgress />
            </Box>
        );
    }

    if (!assignment) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <Typography variant="h6" color="error">
                    Assignment not found
                </Typography>
            </Box>
        );
    }

    return (
        loading ? (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <Loading />
            </Box>
        ) : (
        <Box sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <IconButton onClick={handleBack} sx={{ mr: 2 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h4">
                    Vehicle Assignment Details
                </Typography>
                <Box sx={{ flexGrow: 1 }} />
                {/*<Button*/}
                {/*    variant="outlined"*/}
                {/*    startIcon={<EditIcon />}*/}
                {/*    onClick={handleEdit}*/}
                {/*    sx={{ mr: 2 }}*/}
                {/*>*/}
                {/*    Edit*/}
                {/*</Button>*/}
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
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Assignment Status
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            <Box sx={{ mb: 2, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                <Chip
                                    label={getStatusText(assignment.status)}
                                    color={getStatusColor(assignment.status)}
                                    icon={assignment.status === 'COMPLETED' ? <CheckCircleIcon /> : null}
                                    sx={{ fontSize: '1.1rem', py: 2, px: 1 }}
                                />
                            </Box>

                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
                                {assignment.status === 'PLANNED' && (
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        fullWidth
                                        onClick={handleStartTrip}
                                        startIcon={<CommuteIcon />}
                                    >
                                        Start Trip
                                    </Button>
                                )}

                                {assignment.status === 'IN_PROGRESS' && (
                                    <Button
                                        variant="contained"
                                        color="success"
                                        fullWidth
                                        onClick={handleCompleteTrip}
                                        startIcon={<CheckCircleIcon />}
                                    >
                                        Complete Trip
                                    </Button>
                                )}

                                {(assignment.status === 'COMPLETED' || assignment.status === 'CANCELLED') && (
                                    <Typography variant="body1" color="text.secondary" textAlign="center" width="100%">
                                        This trip has been {assignment.status.toLowerCase()}
                                    </Typography>
                                )}
                            </Box>

                            <Box sx={{ mt: 3 }}>
                                <Typography variant="subtitle2" color="text.secondary">
                                    Schedule
                                </Typography>
                                <Box sx={{ mt: 1 }}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={6}>
                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                <EventIcon fontSize="small" color="action" sx={{ mr: 1 }} />
                                                <Typography variant="body2">
                                                    Created:
                                                </Typography>
                                            </Box>
                                            <Typography variant="body1" sx={{ mt: 0.5 }}>
                                                {formatDate(assignment.createdAt)}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                <ScheduleIcon fontSize="small" color="action" sx={{ mr: 1 }} />
                                                <Typography variant="body2">
                                                    Scheduled:
                                                </Typography>
                                            </Box>
                                            <Typography variant="body1" sx={{ mt: 0.5 }}>
                                                {formatDate(assignment.scheduledDepartureTime)}
                                            </Typography>
                                        </Grid>
                                        {assignment.startTime && (
                                            <Grid item xs={6}>
                                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                    <CommuteIcon fontSize="small" color="action" sx={{ mr: 1 }} />
                                                    <Typography variant="body2">
                                                        Started:
                                                    </Typography>
                                                </Box>
                                                <Typography variant="body1" sx={{ mt: 0.5 }}>
                                                    {formatDate(assignment.startTime)}
                                                </Typography>
                                            </Grid>
                                        )}
                                        {assignment.completionTime && (
                                            <Grid item xs={6}>
                                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                    <CheckCircleIcon fontSize="small" color="action" sx={{ mr: 1 }} />
                                                    <Typography variant="body2">
                                                        Completed:
                                                    </Typography>
                                                </Box>
                                                <Typography variant="body1" sx={{ mt: 0.5 }}>
                                                    {formatDate(assignment.completionTime)}
                                                </Typography>
                                            </Grid>
                                        )}
                                    </Grid>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>

                    <Card sx={{ mt: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                <LocalShippingIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                Vehicle Information
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            {vehicle ? (
                                <Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Vehicle ID
                                        </Typography>
                                        <Typography variant="body1">
                                            {vehicle.vehicleId}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Plate Number
                                        </Typography>
                                        <Typography variant="body1" fontWeight="bold">
                                            {vehicle.plateNumber}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Type
                                        </Typography>
                                        <Typography variant="body1">
                                            {vehicle.vehicleType}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Model
                                        </Typography>
                                        <Typography variant="body1">
                                            {vehicle.manufacturer} {vehicle.model}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Capacity
                                        </Typography>
                                        <Typography variant="body1">
                                            {vehicle.volumeCapacity} m³ / {vehicle.weightCapacity} kg
                                        </Typography>
                                    </Box>
                                </Box>
                            ) : (
                                <Typography variant="body2" color="text.secondary" align="center">
                                    Vehicle information not available
                                </Typography>
                            )}
                        </CardContent>
                    </Card>

                    <Card sx={{ mt: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                <PersonIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                Driver Information
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            {driver ? (
                                <Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Driver Name
                                        </Typography>
                                        <Typography variant="body1" fontWeight="bold">
                                            {driver.name}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Contact
                                        </Typography>
                                        <Typography variant="body1">
                                            {driver.phone}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                        <Typography variant="subtitle2" color="text.secondary">
                                            Email
                                        </Typography>
                                        <Typography variant="body1">
                                            {driver.email}
                                        </Typography>
                                    </Box>
                                </Box>
                            ) : (
                                <Typography variant="body2" color="text.secondary" align="center">
                                    Driver information not available
                                </Typography>
                            )}
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={8}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                                <Typography variant="h6">
                                    <RouteIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                    Route Information
                                </Typography>
                                <Box>
                                    <Chip
                                        label={assignment.direction}
                                        sx={{
                                            mr: 1,
                                            fontWeight: 'bold',
                                            color: 'white',
                                            backgroundColor: getDirectionColor(assignment.direction)
                                        }}
                                    />
                                    <Tooltip title="View on Map">
                                        <IconButton
                                            color="primary"
                                            onClick={() => history.push(`/middle-mile/routes/map/${assignment.routeId}`)}
                                        >
                                            <MapIcon />
                                        </IconButton>
                                    </Tooltip>
                                </Box>
                            </Box>
                            <Divider sx={{ mb: 2 }} />

                            {route ? (
                                <Box>
                                    <Box sx={{ mb: 3 }}>
                                        <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                                            {route.routeName}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Route Code: {route.routeCode}
                                        </Typography>
                                        <Typography variant="body2" sx={{ mt: 1 }}>
                                            {route.description}
                                        </Typography>
                                    </Box>

                                    <Box sx={{ mb: 3 }}>
                                        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                            Route Details
                                        </Typography>
                                        <Grid container spacing={2}>
                                            <Grid item xs={4}>
                                                <Paper sx={{ p: 2, textAlign: 'center' }}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Total Distance
                                                    </Typography>
                                                    <Typography variant="h6">
                                                        {route.totalDistance ? `${route.totalDistance.toFixed(1)} km` : 'N/A'}
                                                    </Typography>
                                                </Paper>
                                            </Grid>
                                            <Grid item xs={4}>
                                                <Paper sx={{ p: 2, textAlign: 'center' }}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Estimated Duration
                                                    </Typography>
                                                    <Typography variant="h6">
                                                        {route.estimatedDuration ? `${Math.floor(route.estimatedDuration / 60)}h ${route.estimatedDuration % 60}m` : 'N/A'}
                                                    </Typography>
                                                </Paper>
                                            </Grid>
                                            <Grid item xs={4}>
                                                <Paper sx={{ p: 2, textAlign: 'center' }}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Total Stops
                                                    </Typography>
                                                    <Typography variant="h6">
                                                        {stopSequence.length}
                                                    </Typography>
                                                </Paper>
                                            </Grid>
                                        </Grid>
                                    </Box>
                                </Box>
                            ) : (
                                <Typography variant="body2" color="text.secondary" align="center">
                                    Route information not available
                                </Typography>
                            )}

                            <Box sx={{ mt: 3 }}>
                                <Typography variant="subtitle1" gutterBottom>
                                    <DirectionsIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                    Stops Sequence
                                </Typography>
                                <Divider sx={{ mb: 2 }} />

                                <List sx={{
                                    width: '100%',
                                    bgcolor: 'background.paper',
                                    position: 'relative',
                                    overflow: 'auto',
                                    maxHeight: 300,
                                    '& ul': { padding: 0 },
                                }}>
                                    {stopSequence.length > 0 ? (
                                        stopSequence.map((stop, index) => (
                                            <React.Fragment key={stop.id || index}>
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
                                                                <Box sx={{ display: 'flex', alignItems: 'center', mt: 0.5 }}>
                                                                    <PlaceIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                                                                    <Typography variant="body2" color="text.secondary">
                                                                        {stop.hubCode || 'No code'}
                                                                    </Typography>
                                                                </Box>
                                                                <Box sx={{ display: 'flex', alignItems: 'center', mt: 0.5 }}>
                                                                    <ScheduleIcon fontSize="small" color="action" sx={{ mr: 0.5 }} />
                                                                    <Typography variant="body2" color="text.secondary">
                                                                        Wait Time: {stop.estimatedWaitTime} min
                                                                    </Typography>
                                                                </Box>
                                                            </React.Fragment>
                                                        }
                                                    />
                                                </ListItem>
                                                {index < stopSequence.length - 1 && (
                                                    <Box sx={{
                                                        ml: 7,
                                                        borderLeft: '2px dashed',
                                                        borderColor: 'primary.light',
                                                        height: 20
                                                    }} />
                                                )}
                                            </React.Fragment>
                                        ))
                                    ) : (
                                        <ListItem>
                                            <ListItemText
                                                primary="No stops defined for this route"
                                                sx={{ textAlign: 'center', color: 'text.secondary' }}
                                            />
                                        </ListItem>
                                    )}
                                </List>
                            </Box>
                        </CardContent>
                    </Card>

                    <Card sx={{ mt: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Orders
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            {orders.length > 0 ? (
                                <TableContainer component={Paper} sx={{ maxHeight: 300, overflow: 'auto' }}>
                                    <Table stickyHeader>
                                        <TableHead>
                                            <TableRow>
                                                <TableCell>Order ID</TableCell>
                                                <TableCell>Origin</TableCell>
                                                <TableCell>Destination</TableCell>
                                                <TableCell>Status</TableCell>
                                                <TableCell>Created</TableCell>
                                                <TableCell>Actions</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {orders.map((order) => (
                                                <TableRow key={order.id}>
                                                    <TableCell>{order.id}</TableCell>
                                                    <TableCell>{order.originHubName}</TableCell>
                                                    <TableCell>{order.destinationHubName}</TableCell>
                                                    <TableCell>
                                                        <Chip
                                                            label={order.status}
                                                            size="small"
                                                            color={order.status === 'DELIVERED' ? 'success' : 'primary'}
                                                        />
                                                    </TableCell>
                                                    <TableCell>
                                                        {formatDate(order.createdAt)}
                                                    </TableCell>
                                                    <TableCell>
                                                        <Button
                                                            variant="outlined"
                                                            size="small"
                                                            onClick={() => history.push(`/orders/${order.id}`)}
                                                        >
                                                            View
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            ) : (
                                <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 3 }}>
                                    No orders assigned to this trip
                                </Typography>
                            )}

                            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
                                <Button
                                    variant="outlined"
                                    onClick={() => history.push(`/middle-mile/orders/assign?vehicleAssignmentId=${assignmentId}`)}
                                >
                                    Assign Orders
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>)
    );
};

export default VehicleAssignmentDetail;