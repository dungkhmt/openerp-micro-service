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

    // Helper function to translate status to Vietnamese
    const getStatusText = (status) => {
        switch (status) {
            case 'PLANNED': return 'PLANNED';
            case 'IN_PROGRESS': return 'Đang thực hiện';
            case 'CONFIRMED_IN': return 'Đã xác nhận';
            case 'CAME_FIRST_STOP': return 'Đã đến điểm đầu';
            case 'READY_FOR_PICKUP': return 'Sẵn sàng lấy hàng';
            case 'COMPLETED': return 'Hoàn thành';
            case 'CANCELLED': return 'Đã hủy';
            default: return status;
        }
    };

    // Helper function to format time display
    const formatTime = (timeString) => {
        if (!timeString) return '';
        // Handle both full datetime and time-only strings
        if (timeString.includes('T')) {
            return new Date(timeString).toLocaleTimeString();
        } else {
            // For time-only strings like "12:32:00"
            return timeString.substring(0, 5); // Remove seconds, show HH:MM
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
            errorNoti("Không thể tải dữ liệu tài xế");
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
            errorNoti("Không thể tải chi tiết chuyến đi");
        }
    };

    const handleStartTrip = async (tripId) => {
        try {
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/start`,
                (res) => {
                    successNoti("Bắt đầu chuyến đi thành công");
                    setTripDetails(res.data);
                    // Refresh all trips data
                    fetchDriverData();
                }
            );
        } catch (error) {
            console.error("Error starting trip:", error);
            errorNoti("Không thể bắt đầu chuyến đi");
        }
    };

    // Helper to get formatted day of week
    const formatDayOfWeek = (dayOfWeek) => {
        if (!dayOfWeek) return '';
        const days = {
            'MONDAY': 'Thứ Hai',
            'TUESDAY': 'Thứ Ba',
            'WEDNESDAY': 'Thứ Tư',
            'THURSDAY': 'Thứ Năm',
            'FRIDAY': 'Thứ Sáu',
            'SATURDAY': 'Thứ Bảy',
            'SUNDAY': 'Chủ Nhật'
        };
        return days[dayOfWeek] || dayOfWeek;
    };

    const handleAdvanceTrip = async (tripId) => {
        try {
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/advance`,
                (res) => {
                    successNoti("Đã chuyển đến điểm dừng tiếp theo");
                    setTripDetails(res.data);
                    // Refresh all trips data
                    fetchDriverData();
                }
            );
        } catch (error) {
            console.error("Error advancing trip:", error);
            errorNoti("Không thể chuyển đến điểm dừng tiếp theo");
        }
    };

    const handleCompleteTrip = async (tripId) => {
        try {
            await request(
                'post',
                `/smdeli/driver/trips/${tripId}/complete`,
                (res) => {
                    successNoti("Hoàn thành chuyến đi thành công");
                    setTripDetails(null);
                    setSelectedTrip(null);
                    setCompleteTripDialogOpen(false);
                    // Refresh all trips data
                    fetchDriverData();
                },
                {
                    400: () => errorNoti("Không thể hoàn thành chuyến đi"),
                    401: () => errorNoti("Không có quyền thực hiện hành động này")
                },
                { notes: completionNotes }
            );
        } catch (error) {
            console.error("Error completing trip:", error);
            errorNoti("Không thể hoàn thành chuyến đi");
        }
    };

    // Navigate to hub operations for pickup/delivery
    const handleHubOperation = (tripId, hubId, operationType) => {
        history.push(`/middle-mile/driver/hub/${hubId}/${operationType}?tripId=${tripId}`);
    };

    // If user wants to create a trip for today
    const handleCreateTrip = (routeScheduleId) => {
        if (!routeScheduleId) {
            errorNoti("Không có lịch trình tuyến đường được chọn");
            return;
        }
        try {
            request(
                'post',
                '/smdeli/driver/trip/start',
                (res) => {
                    successNoti("Tạo chuyến đi thành công");
                    // Set the new trip as selected
                    setSelectedTrip(res.data.id);
                    // Refresh trips data
                    fetchDriverData();
                },
                {
                    401: () => errorNoti("Không có quyền thực hiện hành động này"),
                    400: () => errorNoti("Không thể tạo chuyến đi")
                },
                { routeVehicleId: routeScheduleId }
            );
        } catch (error) {
            console.error("Error creating trip:", error);
            errorNoti("Không thể tạo chuyến đi");
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
                                {trip.routeName || `Chuyến #${trip.id.substring(0, 8)}`}
                                {trip.routeCode && (
                                    <Typography component="span" variant="body2" color="text.secondary" sx={{ ml: 1 }}>
                                        ({trip.routeCode})
                                    </Typography>
                                )}
                            </Typography>
                            <Chip
                                label={getStatusText(trip.status)}
                                color={getStatusColor(trip.status)}
                                size="small"
                            />
                        </Box>
                    }
                    secondary={
                        <Box sx={{ mt: 1 }}>
                            {trip.dayOfWeek && (
                                <Typography variant="body2" color="text.secondary">
                                    Ngày: {formatDayOfWeek(trip.dayOfWeek)}
                                </Typography>
                            )}
                            <Typography variant="body2" color="text.secondary">
                                Ngày tháng: {trip.date}
                            </Typography>
                            {trip.plannedStartTime && (
                                <Typography variant="body2" color="text.secondary">
                                    Thời gian bắt đầu dự kiến: {formatTime(trip.plannedStartTime)}
                                </Typography>
                            )}
                            {trip.status === 'IN_PROGRESS' || trip.status === 'CONFIRMED_IN' ? (
                                <Typography variant="body2" color="text.secondary">
                                    Điểm dừng {trip.currentStopIndex + 1} / {trip.totalStops}
                                </Typography>
                            ) : (
                                <Typography variant="body2" color="text.secondary">
                                    Điểm dừng: {trip.totalStops}
                                </Typography>
                            )}
                            <Typography variant="body2" color="text.secondary">
                                Đơn hàng: {trip.ordersDelivered || 0}/{trip.ordersCount || 0}
                            </Typography>
                            {trip.startTime && (
                                <Typography variant="body2" color="text.secondary">
                                    Bắt đầu: {formatTime(trip.startTime)}
                                </Typography>
                            )}
                            {trip.endTime && (
                                <Typography variant="body2" color="text.secondary">
                                    Hoàn thành: {formatTime(trip.endTime)}
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
                Lịch trình các chuyến đi hôm nay
            </Typography>

            <Grid container spacing={3}>
                {/* Today's Trips Section */}
                <Grid item xs={12} md={selectedTrip ? 6 : 12}>
                    <Card>
                        <CardHeader
                            title={
                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    <EventIcon sx={{ mr: 1 }} />
                                    <Typography variant="h6">Chuyến đi hôm nay</Typography>
                                </Box>
                            }
                        />
                        <Divider />
                        <CardContent>
                            {/* Other Status Trips (CAME_FIRST_STOP, READY_FOR_PICKUP, etc.) - Top Priority */}
                            {trips.otherTrips.length > 0 && (
                                <>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1, color: 'secondary.main' }}>
                                        Chuyến đi khác ({trips.otherTrips.length})
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
                                        Đã lên lịch ({trips.scheduledTrips.length})
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
                                        Đang thực hiện ({trips.activeTrips.length})
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
                                        Đã hoàn thành hôm nay ({trips.completedTrips.length})
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
                                        <Typography variant="h6" color="text.secondary">Không có chuyến đi nào được lên lịch hôm nay</Typography>
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
                                                Chi tiết chuyến đi
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
                                                        Bắt đầu
                                                    </Button>
                                                )}
                                                {tripDetails.status !== 'PLANNED' && tripDetails.status !== 'COMPLETED' && (
                                                    <Button
                                                        variant="contained"
                                                        color="success"
                                                        startIcon={<CheckIcon />}
                                                        onClick={() => history.push(`/middle-mile/driver/route/${tripDetails.routeScheduleId}?tripId=${tripDetails.id}`)}
                                                    >
                                                        Chi tiết
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
                                                Tổng quan chuyến đi
                                            </Typography>
                                            <Box sx={{ mb: 2 }}>
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Tuyến đường:</strong> {tripDetails.routeName}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Mã tuyến:</strong> {tripDetails.routeCode}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Ngày:</strong> {tripDetails.date}
                                                </Typography>
                                                {tripDetails.plannedStartTime && (
                                                    <Typography variant="body2" color="text.secondary">
                                                        <strong>Thời gian dự kiến:</strong> {formatTime(tripDetails.plannedStartTime)}
                                                    </Typography>
                                                )}
                                                <Typography variant="body2" color="text.secondary">
                                                    <strong>Trạng thái:</strong>
                                                    <Chip
                                                        label={getStatusText(tripDetails.status)}
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
                                                Điểm dừng ({tripDetails.currentStopIndex + 1}/{tripDetails.totalStops})
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
                                                                    label={stop.status === 'CURRENT' ? 'Hiện tại' :
                                                                        stop.status === 'COMPLETED' ? 'Hoàn thành' : stop.status}
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
                                                                            Đơn hàng: {stop.orderCount || 0}
                                                                        </Typography>
                                                                        {stop.estimatedArrivalTime && (
                                                                            <Typography variant="body2" color="text.secondary">
                                                                                Thời gian dự kiến đến: {stop.estimatedArrivalTime}
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
                <DialogTitle>Hoàn thành chuyến đi</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>
                        Bạn có chắc chắn muốn đánh dấu chuyến đi này là đã hoàn thành?
                    </Typography>
                    <TextField
                        label="Ghi chú hoàn thành (tùy chọn)"
                        fullWidth
                        multiline
                        rows={4}
                        value={completionNotes}
                        onChange={(e) => setCompletionNotes(e.target.value)}
                        margin="normal"
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setCompleteTripDialogOpen(false)}>Hủy</Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleCompleteTrip(selectedTrip)}
                    >
                        Hoàn thành
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default DriverSchedule;