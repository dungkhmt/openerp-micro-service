import React, { useState, useEffect } from 'react';
import {
    Box,
    Card,
    CardContent,
    Typography,
    Grid,
    Tabs,
    Tab,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Chip,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    List,
    ListItem,
    ListItemText,
    CircularProgress,
    Alert,
    LinearProgress,
    Divider,
    IconButton,
    Tooltip
} from '@mui/material';
import {
    DirectionsCar as CarIcon,
    LocalShipping as TruckIcon,
    CheckCircle as CompletedIcon,
    Schedule as ScheduledIcon,
    PlayArrow as ActiveIcon,
    Info as InfoIcon,
    Assessment as StatsIcon,
    History as HistoryIcon,
    Visibility as ViewIcon
} from '@mui/icons-material';
import { request } from '../../api';
import { API_PATH } from '../apiPaths';
import { errorNoti, successNoti } from '../../utils/notification';

// Custom styles
const useStyles = {
    container: {
        padding: '20px',
        backgroundColor: '#f5f5f5',
        minHeight: '100vh'
    },
    headerCard: {
        marginBottom: '20px',
        background: 'linear-gradient(135deg, #1976d2 0%, #42a5f5 100%)',
        color: 'white'
    },
    statsCard: {
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
    },
    statsContent: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
    },
    statsIcon: {
        fontSize: '40px',
        opacity: 0.8
    },
    statsValue: {
        fontSize: '32px',
        fontWeight: 'bold',
        marginBottom: '8px'
    },
    tabPanel: {
        padding: '20px 0'
    },
    tripCard: {
        marginBottom: '16px',
        borderLeft: '4px solid',
        transition: 'all 0.3s ease',
        '&:hover': {
            boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
            transform: 'translateY(-2px)'
        }
    }
};

// Tab Panel Component
function TabPanel({ children, value, index, ...other }) {
    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`trip-tabpanel-${index}`}
            aria-labelledby={`trip-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box sx={useStyles.tabPanel}>
                    {children}
                </Box>
            )}
        </div>
    );
}

const DriverTripHistory = () => {
    const [tabValue, setTabValue] = useState(0);
    const [trips, setTrips] = useState({
        today: [],
        all: []
    });
    const [statistics, setStatistics] = useState({
        totalTrips: 0,
        completedTrips: 0,
        activeTrips: 0,
        totalOrders: 0,
        completedOrders: 0,
        successRate: 0
    });
    const [vehicle, setVehicle] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [tripDetails, setTripDetails] = useState(null);
    const [openTripDialog, setOpenTripDialog] = useState(false);
    const [loadingDetails, setLoadingDetails] = useState(false);

    // Fetch data on component mount
    useEffect(() => {
        fetchAllData();
    }, []);

    const fetchAllData = async () => {
        setLoading(true);
        try {
            await Promise.all([
                fetchTodayTrips(),
                fetchAllTrips(),
                fetchVehicle(),
                calculateStatistics()
            ]);
        } catch (error) {
            errorNoti("Có lỗi khi tải dữ liệu");
        } finally {
            setLoading(false);
        }
    };

    const fetchTodayTrips = async () => {
        try {
            await request(
                "get",
                `${API_PATH.DRIVER}/trips/today`,
                (res) => {
                    setTrips(prev => ({ ...prev, today: res.data }));
                },
                {
                    401: () => errorNoti("Không có quyền truy cập"),
                    404: () => setTrips(prev => ({ ...prev, today: [] }))
                }
            );
        } catch (error) {
            console.error("Error fetching today trips:", error);
        }
    };

    const fetchAllTrips = async () => {
        try {
            await request(
                "get",
                `${API_PATH.DRIVER}/trips`,
                (res) => {
                    setTrips(prev => ({ ...prev, all: res.data }));
                },
                {
                    401: () => errorNoti("Không có quyền truy cập"),
                    404: () => setTrips(prev => ({ ...prev, all: [] }))
                }
            );
        } catch (error) {
            console.error("Error fetching all trips:", error);
        }
    };

    const fetchVehicle = async () => {
        try {
            await request(
                "get",
                `${API_PATH.DRIVER}/vehicle`,
                (res) => {
                    setVehicle(res.data);
                },
                {
                    401: () => errorNoti("Không có quyền truy cập"),
                    404: () => setVehicle(null)
                }
            );
        } catch (error) {
            console.error("Error fetching vehicle:", error);
        }
    };

    const calculateStatistics = () => {
        // This would typically be calculated from the trips data
        // For now, we'll use placeholder logic
        const allTrips = trips.all;
        const completedTrips = allTrips.filter(trip => trip.status === 'COMPLETED').length;
        const activeTrips = allTrips.filter(trip => trip.status === 'IN_PROGRESS').length;

        const totalOrders = allTrips.reduce((sum, trip) => sum + (trip.ordersCount || 0), 0);
        const completedOrders = allTrips.reduce((sum, trip) => sum + (trip.ordersDelivered || 0), 0);

        const successRate = totalOrders > 0 ? Math.round((completedOrders / totalOrders) * 100) : 0;

        setStatistics({
            totalTrips: allTrips.length,
            completedTrips,
            activeTrips,
            totalOrders,
            completedOrders,
            successRate
        });
    };

    const fetchTripDetails = async (tripId) => {
        setLoadingDetails(true);
        try {
            await request(
                "get",
                `${API_PATH.DRIVER}/trips/${tripId}`,
                (res) => {
                    setTripDetails(res.data);
                    setOpenTripDialog(true);
                },
                {
                    401: () => errorNoti("Không có quyền truy cập"),
                    404: () => errorNoti("Không tìm thấy chi tiết chuyến đi")
                }
            );
        } catch (error) {
            errorNoti("Có lỗi khi tải chi tiết chuyến đi");
        } finally {
            setLoadingDetails(false);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'COMPLETED': return 'success';
            case 'IN_PROGRESS': return 'primary';
            case 'PLANNED': return 'warning';
            case 'CANCELLED': return 'error';
            default: return 'default';
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'COMPLETED': return <CompletedIcon />;
            case 'IN_PROGRESS': return <ActiveIcon />;
            case 'PLANNED': return <ScheduledIcon />;
            default: return <InfoIcon />;
        }
    };

    const getBorderColor = (status) => {
        switch (status) {
            case 'COMPLETED': return '#4caf50';
            case 'IN_PROGRESS': return '#2196f3';
            case 'PLANNED': return '#ff9800';
            case 'CANCELLED': return '#f44336';
            default: return '#9e9e9e';
        }
    };

    const formatDateTime = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    const StatisticsCard = ({ title, value, icon, color = 'primary' }) => (
        <Card sx={useStyles.statsCard}>
            <CardContent>
                <Box sx={useStyles.statsContent}>
                    <Box>
                        <Typography variant="h6" color="text.secondary" gutterBottom>
                            {title}
                        </Typography>
                        <Typography variant="h4" sx={useStyles.statsValue} color={color}>
                            {value}
                        </Typography>
                    </Box>
                    <Box sx={{ color: color === 'primary' ? '#1976d2' : '#4caf50' }}>
                        {React.cloneElement(icon, { sx: useStyles.statsIcon })}
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );

    const TripCard = ({ trip, showDate = false }) => (
        <Card
            sx={{
                ...useStyles.tripCard,
                borderLeftColor: getBorderColor(trip.status)
            }}
        >
            <CardContent>
                <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} md={8}>
                        <Box display="flex" alignItems="center" gap={1} marginBottom={1}>
                            <Typography variant="h6">
                                {trip.routeName || `Tuyến ${trip.routeCode}`}
                            </Typography>
                            <Chip
                                icon={getStatusIcon(trip.status)}
                                label={trip.status}
                                color={getStatusColor(trip.status)}
                                size="small"
                            />
                        </Box>
                        <Grid container spacing={2}>
                            {showDate && (
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        <strong>Ngày:</strong> {formatDate(trip.date)}
                                    </Typography>
                                </Grid>
                            )}
                            <Grid item xs={6}>
                                <Typography variant="body2" color="text.secondary">
                                    <strong>Bắt đầu:</strong> {formatDateTime(trip.startTime)}
                                </Typography>
                            </Grid>
                            <Grid item xs={6}>
                                <Typography variant="body2" color="text.secondary">
                                    <strong>Kết thúc:</strong> {formatDateTime(trip.endTime)}
                                </Typography>
                            </Grid>
                            <Grid item xs={6}>
                                <Typography variant="body2" color="text.secondary">
                                    <strong>Điểm dừng:</strong> {trip.currentStopIndex + 1}/{trip.totalStops}
                                </Typography>
                            </Grid>
                            <Grid item xs={6}>
                                <Typography variant="body2" color="text.secondary">
                                    <strong>Đơn hàng:</strong> {trip.ordersDelivered}/{trip.ordersCount}
                                </Typography>
                            </Grid>
                        </Grid>
                        {trip.ordersCount > 0 && (
                            <Box marginTop={1}>
                                <Typography variant="caption" color="text.secondary">
                                    Tiến độ giao hàng
                                </Typography>
                                <LinearProgress
                                    variant="determinate"
                                    value={(trip.ordersDelivered / trip.ordersCount) * 100}
                                    sx={{ marginTop: 0.5, height: 6, borderRadius: 3 }}
                                />
                            </Box>
                        )}
                    </Grid>
                    <Grid item xs={12} md={4}>
                        <Box display="flex" justifyContent="flex-end">
                            <Tooltip title="Xem chi tiết">
                                <IconButton
                                    color="primary"
                                    onClick={() => fetchTripDetails(trip.id)}
                                    disabled={loadingDetails}
                                >
                                    <ViewIcon />
                                </IconButton>
                            </Tooltip>
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress size={60} />
            </Box>
        );
    }

    return (
        <Box sx={useStyles.container}>
            {/* Header */}
            <Card sx={useStyles.headerCard}>
                <CardContent>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} md={8}>
                            <Typography variant="h4" gutterBottom>
                                Lịch sử chuyến đi
                            </Typography>
                            <Typography variant="h6" sx={{ opacity: 0.9 }}>
                                Quản lý và theo dõi các chuyến đi của bạn
                            </Typography>
                        </Grid>
                        <Grid item xs={12} md={4}>
                            {vehicle && (
                                <Box textAlign="right">
                                    <Typography variant="h6">
                                        Phương tiện: {vehicle.plateNumber}
                                    </Typography>
                                    <Typography variant="body1" sx={{ opacity: 0.9 }}>
                                        {vehicle.vehicleType} - {vehicle.manufacturer}
                                    </Typography>
                                </Box>
                            )}
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>

            {/* Statistics */}
            <Grid container spacing={3} marginBottom={3}>
                <Grid item xs={12} sm={6} md={3}>
                    <StatisticsCard
                        title="Tổng chuyến đi"
                        value={statistics.totalTrips}
                        icon={<TruckIcon />}
                        color="#1976d2"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatisticsCard
                        title="Hoàn thành"
                        value={statistics.completedTrips}
                        icon={<CompletedIcon />}
                        color="#4caf50"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatisticsCard
                        title="Đang thực hiện"
                        value={statistics.activeTrips}
                        icon={<ActiveIcon />}
                        color="#2196f3"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatisticsCard
                        title="Tỷ lệ thành công"
                        value={`${statistics.successRate}%`}
                        icon={<StatsIcon />}
                        color="#4caf50"
                    />
                </Grid>
            </Grid>

            {/* Tabs */}
            <Card>
                <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                    <Tabs
                        value={tabValue}
                        onChange={(e, newValue) => setTabValue(newValue)}
                        aria-label="trip tabs"
                    >
                        <Tab
                            icon={<ScheduledIcon />}
                            label={`Hôm nay (${trips.today.length})`}
                            id="trip-tab-0"
                            aria-controls="trip-tabpanel-0"
                        />
                        <Tab
                            icon={<HistoryIcon />}
                            label={`Tất cả (${trips.all.length})`}
                            id="trip-tab-1"
                            aria-controls="trip-tabpanel-1"
                        />
                    </Tabs>
                </Box>

                {/* Today's Trips */}
                <TabPanel value={tabValue} index={0}>
                    {trips.today.length === 0 ? (
                        <Alert severity="info" sx={{ margin: 3 }}>
                            Không có chuyến đi nào hôm nay
                        </Alert>
                    ) : (
                        <Box>
                            {trips.today.map((trip) => (
                                <TripCard key={trip.id} trip={trip} />
                            ))}
                        </Box>
                    )}
                </TabPanel>

                {/* All Trips */}
                <TabPanel value={tabValue} index={1}>
                    {trips.all.length === 0 ? (
                        <Alert severity="info" sx={{ margin: 3 }}>
                            Chưa có chuyến đi nào
                        </Alert>
                    ) : (
                        <Box>
                            {trips.all.map((trip) => (
                                <TripCard key={trip.id} trip={trip} showDate={true} />
                            ))}
                        </Box>
                    )}
                </TabPanel>
            </Card>

            {/* Trip Details Dialog */}
            <Dialog
                open={openTripDialog}
                onClose={() => setOpenTripDialog(false)}
                maxWidth="md"
                fullWidth
            >
                <DialogTitle>
                    Chi tiết chuyến đi
                    {tripDetails && (
                        <Chip
                            icon={getStatusIcon(tripDetails.status)}
                            label={tripDetails.status}
                            color={getStatusColor(tripDetails.status)}
                            size="small"
                            sx={{ marginLeft: 2 }}
                        />
                    )}
                </DialogTitle>
                <DialogContent>
                    {loadingDetails ? (
                        <Box display="flex" justifyContent="center" padding={4}>
                            <CircularProgress />
                        </Box>
                    ) : tripDetails ? (
                        <Box>
                            {/* Trip Information */}
                            <Typography variant="h6" gutterBottom>
                                Thông tin chuyến đi
                            </Typography>
                            <Grid container spacing={2} marginBottom={3}>
                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        <strong>Tuyến đường:</strong> {tripDetails.routeName}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        <strong>Thời gian bắt đầu:</strong> {formatDateTime(tripDetails.startTime)}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        <strong>Thời gian kết thúc:</strong> {formatDateTime(tripDetails.endTime)}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        <strong>Tổng đơn hàng:</strong> {tripDetails.ordersCount}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        <strong>Đã giao:</strong> {tripDetails.ordersDelivered}
                                    </Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        <strong>Điểm dừng hiện tại:</strong> {tripDetails.currentStopIndex + 1}/{tripDetails.totalStops}
                                    </Typography>
                                </Grid>
                            </Grid>

                            <Divider sx={{ marginY: 2 }} />

                            {/* Stops */}
                            {tripDetails.stops && tripDetails.stops.length > 0 && (
                                <Box>
                                    <Typography variant="h6" gutterBottom>
                                        Điểm dừng
                                    </Typography>
                                    <List>
                                        {tripDetails.stops.map((stop, index) => (
                                            <ListItem key={stop.id} divider={index < tripDetails.stops.length - 1}>
                                                <ListItemText
                                                    primary={
                                                        <Box display="flex" alignItems="center" gap={1}>
                                                            <Typography variant="body1">
                                                                {stop.stopSequence}. {stop.hubName}
                                                            </Typography>
                                                            <Chip
                                                                label={stop.status}
                                                                color={
                                                                    stop.status === 'COMPLETED' ? 'success' :
                                                                        stop.status === 'CURRENT' ? 'primary' : 'default'
                                                                }
                                                                size="small"
                                                            />
                                                        </Box>
                                                    }
                                                    secondary={
                                                        <Box>
                                                            <Typography variant="body2">
                                                                Địa chỉ: {stop.address}
                                                            </Typography>
                                                            <Typography variant="body2">
                                                                Thời gian dự kiến: {stop.estimatedArrivalTime}
                                                            </Typography>
                                                            <Typography variant="body2">
                                                                Đơn hàng: {stop.orderCount}
                                                            </Typography>
                                                        </Box>
                                                    }
                                                />
                                            </ListItem>
                                        ))}
                                    </List>
                                </Box>
                            )}

                            <Divider sx={{ marginY: 2 }} />

                            {/* Orders */}
                            {tripDetails.orders && tripDetails.orders.length > 0 && (
                                <Box>
                                    <Typography variant="h6" gutterBottom>
                                        Đơn hàng ({tripDetails.orders.length})
                                    </Typography>
                                    <TableContainer component={Paper} variant="outlined">
                                        <Table size="small">
                                            <TableHead>
                                                <TableRow>
                                                    <TableCell>Mã đơn hàng</TableCell>
                                                    <TableCell>Người gửi</TableCell>
                                                    <TableCell>Người nhận</TableCell>
                                                    <TableCell>Trạng thái</TableCell>
                                                    <TableCell align="right">Tổng tiền</TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {tripDetails.orders.map((order) => (
                                                    <TableRow key={order.id}>
                                                        <TableCell>
                                                            #{order.id.substring(0, 8)}
                                                        </TableCell>
                                                        <TableCell>{order.senderName}</TableCell>
                                                        <TableCell>{order.recipientName}</TableCell>
                                                        <TableCell>
                                                            <Chip
                                                                label={order.status}
                                                                color={getStatusColor(order.status)}
                                                                size="small"
                                                            />
                                                        </TableCell>
                                                        <TableCell align="right">
                                                            {order.totalPrice ? `${order.totalPrice.toLocaleString('vi-VN')}đ` : 'N/A'}
                                                        </TableCell>
                                                    </TableRow>
                                                ))}
                                            </TableBody>
                                        </Table>
                                    </TableContainer>
                                </Box>
                            )}
                        </Box>
                    ) : (
                        <Alert severity="error">
                            Không thể tải chi tiết chuyến đi
                        </Alert>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenTripDialog(false)}>
                        Đóng
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default DriverTripHistory;