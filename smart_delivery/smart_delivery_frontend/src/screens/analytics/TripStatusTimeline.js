import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Stepper,
    Step,
    StepLabel,
    StepContent,
    Paper,
    Divider,
    Chip,
    LinearProgress,
    Grid,
    Card,
    CardContent,
    CircularProgress,
    Alert
} from '@mui/material';
import {
    CheckCircle as CompletedIcon,
    LocalShipping as TruckIcon,
    PlayArrow as StartIcon,
    Flag as FlagIcon,
    Home as HubIcon,
    Done as DoneIcon,
    Schedule as ScheduledIcon,
    WarningAmber as WarningIcon,
    Cancel as CancelIcon,
    DateRange as DateIcon,
    Person as PersonIcon,
    Info as InfoIcon
} from '@mui/icons-material';
import { request } from '../../api';
import { API_PATH } from '../apiPaths';
import { errorNoti } from '../../utils/notification';

// Status to display name mapping
const STATUS_DISPLAY_NAME = {
    'PLANNED': 'Kế hoạch',
    'CAME_FIRST_STOP': 'Đến điểm dừng đầu tiên',
    'IN_PROGRESS': 'Đang thực hiện',
    'CAME_LAST_STOP': 'Đến điểm dừng cuối cùng',
    'CONFIRMED_IN': 'Xác nhận vào kho',
    'COMPLETED': 'Hoàn thành',
    'CANCELLED': 'Đã hủy',
    'DELAYED': 'Trì hoãn'
};

// Status to Material UI color mapping - for Chip components
const STATUS_COLOR = {
    'PLANNED': 'info',
    'CAME_FIRST_STOP': 'primary',
    'IN_PROGRESS': 'primary',
    'CAME_LAST_STOP': 'warning',
    'CONFIRMED_IN': 'success',
    'COMPLETED': 'success',
    'CANCELLED': 'error',
    'DELAYED': 'warning'
};

// Status to color mapping - HEX values for direct styling
const STATUS_COLOR_HEX = {
    'PLANNED': '#42a5f5',
    'CAME_FIRST_STOP': '#1976d2',
    'IN_PROGRESS': '#1976d2',
    'CAME_LAST_STOP': '#ff9800',
    'CONFIRMED_IN': '#4caf50',
    'COMPLETED': '#4caf50',
    'CANCELLED': '#f44336',
    'DELAYED': '#ff9800'
};

// Status to icon mapping
const STATUS_ICON = {
    'PLANNED': <ScheduledIcon />,
    'CAME_FIRST_STOP': <TruckIcon />,
    'IN_PROGRESS': <StartIcon />,
    'CAME_LAST_STOP': <FlagIcon />,
    'CONFIRMED_IN': <DoneIcon />,
    'COMPLETED': <CompletedIcon />,
    'CANCELLED': <CancelIcon />,
    'DELAYED': <WarningIcon />
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

const TripStatusTimeline = ({ trip }) => {
    const [tripHistory, setTripHistory] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (trip && trip.id) {
            fetchTripHistory(trip.id);
        }
    }, [trip]);

    const fetchTripHistory = async (tripId) => {
        setLoading(true);
        try {
            await request(
                "get",
                `${API_PATH.DRIVER_ORDER}/trips/${tripId}/history`,
                (res) => {
                    setTripHistory(res.data);
                    setError(null);
                },
                {
                    401: () => {
                        setError("Không có quyền truy cập");
                        errorNoti("Không có quyền truy cập");
                    },
                    404: () => {
                        setError("Không tìm thấy lịch sử chuyến đi");
                        errorNoti("Không tìm thấy lịch sử chuyến đi");
                    }
                }
            );
        } catch (err) {
            setError("Có lỗi khi tải lịch sử chuyến đi");
            errorNoti("Có lỗi khi tải lịch sử chuyến đi");
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" p={4}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Alert severity="error" sx={{ mb: 3 }}>
                {error}
            </Alert>
        );
    }

    // If we don't have detailed history, fallback to basic timeline
    if (!tripHistory) {
        return renderBasicTimeline(trip);
    }

    return (
        <Box>
            {/* Trip summary card */}
            <Card elevation={1} sx={{ mb: 3 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <TruckIcon color="primary" />
                        Tổng quan chuyến đi
                    </Typography>

                    <Divider sx={{ mb: 2 }} />

                    <Grid container spacing={2}>
                        <Grid item xs={12} md={6}>
                            <Typography variant="body2">
                                <strong>Mã chuyến đi:</strong> {tripHistory.tripCode}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Tuyến đường:</strong> {tripHistory.routeName} ({tripHistory.routeCode})
                            </Typography>
                            <Typography variant="body2">
                                <strong>Trạng thái:</strong>{' '}
                                <Chip
                                    label={STATUS_DISPLAY_NAME[tripHistory.currentStatus] || tripHistory.currentStatus}
                                    color={STATUS_COLOR[tripHistory.currentStatus] || 'default'}
                                    size="small"
                                />
                            </Typography>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Typography variant="body2">
                                <strong>Tạo lúc:</strong> {formatDateTime(tripHistory.createdAt)}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Bắt đầu:</strong> {formatDateTime(tripHistory.startTime)}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Kết thúc:</strong> {formatDateTime(tripHistory.endTime)}
                            </Typography>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Typography variant="body2">
                                <strong>Đơn hàng:</strong> {tripHistory.deliveredOrders}/{tripHistory.totalOrders}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Điểm dừng:</strong> {tripHistory.currentStopIndex + 1}/{tripHistory.totalStops}
                            </Typography>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            {tripHistory.totalDurationMinutes > 0 && (
                                <Typography variant="body2">
                                    <strong>Tổng thời gian:</strong> {tripHistory.totalDurationMinutes} phút
                                </Typography>
                            )}
                            {tripHistory.completionNotes && (
                                <Typography variant="body2">
                                    <strong>Ghi chú:</strong> {tripHistory.completionNotes}
                                </Typography>
                            )}
                        </Grid>
                    </Grid>

                    {/* Progress bar */}
                    {tripHistory.totalOrders > 0 && (
                        <Box sx={{ mt: 2 }}>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                                <Typography variant="caption" color="text.secondary">Tiến độ giao hàng</Typography>
                                <Typography variant="caption" color="text.secondary">
                                    {Math.round((tripHistory.deliveredOrders / tripHistory.totalOrders) * 100)}%
                                </Typography>
                            </Box>
                            <LinearProgress
                                variant="determinate"
                                value={(tripHistory.deliveredOrders / tripHistory.totalOrders) * 100}
                                sx={{ height: 8, borderRadius: 4 }}
                            />
                        </Box>
                    )}
                </CardContent>
            </Card>

            {/* History timeline using Stepper instead of Timeline */}
            <Card elevation={1} sx={{ mb: 3 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <DateIcon color="primary" />
                        Lịch sử trạng thái
                    </Typography>

                    <Divider sx={{ mb: 2 }} />

                    <Stepper orientation="vertical" activeStep={tripHistory.historyEntries.length}>
                        {tripHistory.historyEntries.map((entry, index) => (
                            <Step key={entry.id || index} completed={true}>
                                <StepLabel
                                    StepIconProps={{
                                        icon: STATUS_ICON[entry.status] || <InfoIcon />,
                                        style: {
                                            color: STATUS_COLOR_HEX[entry.status] || 'grey'
                                        }
                                    }}
                                >
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                                        <Typography variant="subtitle1" fontWeight="bold">
                                            {STATUS_DISPLAY_NAME[entry.status] || entry.status}
                                        </Typography>
                                        <Typography variant="caption" color="text.secondary">
                                            {formatDateTime(entry.timestamp)}
                                        </Typography>
                                    </Box>
                                </StepLabel>
                                <StepContent>
                                    <Paper elevation={2} sx={{ p: 2, bgcolor: '#f9f9f9' }}>
                                        {entry.notes && (
                                            <Typography variant="body2">
                                                {entry.notes}
                                            </Typography>
                                        )}

                                        {entry.changedBy && (
                                            <Typography variant="caption" sx={{
                                                mt: 1,
                                                display: 'flex',
                                                alignItems: 'center',
                                                color: 'text.secondary'
                                            }}>
                                                <PersonIcon sx={{ fontSize: 12, mr: 0.5 }} />
                                                {entry.changedBy}
                                            </Typography>
                                        )}

                                        {entry.currentStopName && (
                                            <Typography variant="body2" sx={{ mt: 1 }}>
                                                <strong>Điểm dừng:</strong> {entry.currentStopName}
                                            </Typography>
                                        )}
                                    </Paper>
                                </StepContent>
                            </Step>
                        ))}
                    </Stepper>
                </CardContent>
            </Card>

            {/* Stops timeline */}
            {tripHistory.stopTimeline && tripHistory.stopTimeline.length > 0 && (
                <Card elevation={1}>
                    <CardContent>
                        <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <HubIcon color="primary" />
                            Chi tiết điểm dừng
                        </Typography>

                        <Divider sx={{ mb: 2 }} />

                        <Stepper orientation="vertical" activeStep={tripHistory.currentStopIndex}>
                            {tripHistory.stopTimeline.map((stop, index) => (
                                <Step key={index} completed={stop.status === 'COMPLETED'}>
                                    <StepLabel
                                        StepIconProps={{
                                            icon: <HubIcon />,
                                            style: {
                                                color:
                                                    stop.status === 'COMPLETED' ? '#4caf50' :
                                                        stop.status === 'CURRENT' ? '#1976d2' : undefined
                                            }
                                        }}
                                    >
                                        <Typography variant="subtitle2">
                                            {stop.stopSequence}. {stop.hubName}
                                        </Typography>
                                    </StepLabel>
                                    <StepContent>
                                        <Typography variant="body2">
                                            <strong>Địa chỉ:</strong> {stop.address}
                                        </Typography>

                                        {stop.arrivalTime && (
                                            <Typography variant="body2">
                                                <strong>Thời gian đến:</strong> {formatDateTime(stop.arrivalTime)}
                                            </Typography>
                                        )}

                                        {stop.orderCount && stop.orderCount > 0 && (
                                            <Typography variant="body2">
                                                <strong>Đơn hàng:</strong> {stop.orderCount}
                                            </Typography>
                                        )}

                                        <Chip
                                            label={
                                                stop.status === 'COMPLETED' ? 'Hoàn thành' :
                                                    stop.status === 'CURRENT' ? 'Hiện tại' : 'Chưa đến'
                                            }
                                            color={
                                                stop.status === 'COMPLETED' ? 'success' :
                                                    stop.status === 'CURRENT' ? 'primary' : 'default'
                                            }
                                            size="small"
                                            sx={{ mt: 1 }}
                                        />
                                    </StepContent>
                                </Step>
                            ))}
                        </Stepper>
                    </CardContent>
                </Card>
            )}
        </Box>
    );
};

// Fallback to basic timeline if we don't have detailed history
const renderBasicTimeline = (trip) => {
    if (!trip) return null;

    const TRIP_STATUS_ORDER = [
        'PLANNED',
        'CAME_FIRST_STOP',
        'IN_PROGRESS',
        'CAME_LAST_STOP',
        'CONFIRMED_IN',
        'COMPLETED'
    ];

    const currentStatusIndex = TRIP_STATUS_ORDER.indexOf(trip.status);
    const progressPercentage = (currentStatusIndex / (TRIP_STATUS_ORDER.length - 1)) * 100;

    return (
        <Paper elevation={1} sx={{ padding: 3, marginBottom: 3 }}>
            <Box sx={{ marginBottom: 2 }}>
                <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <TruckIcon color="primary" />
                    Lịch sử trạng thái chuyến đi
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    Trạng thái hiện tại:
                    <Chip
                        label={STATUS_DISPLAY_NAME[trip.status]}
                        color={STATUS_COLOR[trip.status]}
                        icon={STATUS_ICON[trip.status]}
                        size="small"
                        sx={{ ml: 1 }}
                    />
                </Typography>
            </Box>

            <Divider sx={{ mb: 2 }} />

            {/* Progress indicator */}
            <Box sx={{ mb: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="caption" color="text.secondary">Tiến độ chuyến đi</Typography>
                    <Typography variant="caption" color="text.secondary">{Math.round(progressPercentage)}%</Typography>
                </Box>
                <LinearProgress
                    variant="determinate"
                    value={progressPercentage}
                    sx={{ height: 8, borderRadius: 4 }}
                />
            </Box>

            {/* Basic timeline - simpler version without detailed history */}
            <Stepper orientation="vertical" activeStep={currentStatusIndex}>
                {TRIP_STATUS_ORDER.map((status, index) => {
                    const isActive = index <= currentStatusIndex;
                    const isCompleted = index < currentStatusIndex;

                    return (
                        <Step key={status} completed={isCompleted}>
                            <StepLabel
                                StepIconProps={{
                                    icon: STATUS_ICON[status],
                                    style: {
                                        color: isActive ? STATUS_COLOR_HEX[status] : undefined
                                    }
                                }}
                            >
                                <Typography
                                    variant="subtitle1"
                                    color={isActive ? "text.primary" : "text.secondary"}
                                    fontWeight={isActive ? 'bold' : 'normal'}
                                    sx={{ color: isActive ? STATUS_COLOR_HEX[status] : undefined }}
                                >
                                    {STATUS_DISPLAY_NAME[status]}
                                </Typography>
                            </StepLabel>
                            <StepContent>
                                <Typography variant="body2" color="text.secondary">
                                    {getStatusBasicDescription(status)}
                                </Typography>

                                {status === trip.status && trip.stops && trip.stops.length > 0 && (
                                    <Box sx={{ mt: 1 }}>
                                        <Typography variant="body2">
                                            <strong>Điểm dừng hiện tại:</strong> {trip.currentStopIndex + 1}/{trip.stops.length}
                                        </Typography>
                                        {trip.ordersCount > 0 && (
                                            <Typography variant="body2">
                                                <strong>Đơn hàng đã giao:</strong> {trip.ordersDelivered}/{trip.ordersCount}
                                            </Typography>
                                        )}
                                    </Box>
                                )}
                            </StepContent>
                        </Step>
                    );
                })}
            </Stepper>

            {trip.stops && trip.stops.length > 0 && (
                <Box sx={{ mt: 4 }}>
                    <Typography variant="subtitle1" gutterBottom>
                        Chi tiết điểm dừng
                    </Typography>
                    <Stepper orientation="vertical" activeStep={trip.currentStopIndex}>
                        {trip.stops.map((stop, index) => (
                            <Step key={stop.id} completed={stop.status === 'COMPLETED'}>
                                <StepLabel
                                    StepIconProps={{
                                        icon: <HubIcon />,
                                        style: {
                                            color:
                                                stop.status === 'COMPLETED' ? '#4caf50' :
                                                    stop.status === 'CURRENT' ? '#1976d2' : undefined
                                        }
                                    }}
                                >
                                    <Typography variant="subtitle2">
                                        {stop.hubName}
                                    </Typography>
                                </StepLabel>
                                <StepContent>
                                    <Typography variant="body2">
                                        <strong>Địa chỉ:</strong> {stop.address}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Thời gian dự kiến:</strong> {stop.estimatedArrivalTime}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Đơn hàng:</strong> {stop.orderCount}
                                    </Typography>
                                    <Chip
                                        label={stop.status}
                                        color={
                                            stop.status === 'COMPLETED' ? 'success' :
                                                stop.status === 'CURRENT' ? 'primary' : 'default'
                                        }
                                        size="small"
                                        sx={{ mt: 1 }}
                                    />
                                </StepContent>
                            </Step>
                        ))}
                    </Stepper>
                </Box>
            )}
        </Paper>
    );
};

const getStatusBasicDescription = (status) => {
    switch (status) {
        case 'PLANNED':
            return 'Chuyến đi đã được lên kế hoạch và sẵn sàng để bắt đầu.';
        case 'CAME_FIRST_STOP':
            return 'Tài xế đã đến điểm dừng đầu tiên và bắt đầu hành trình.';
        case 'IN_PROGRESS':
            return 'Chuyến đi đang được thực hiện, tài xế đang di chuyển giữa các điểm dừng.';
        case 'CAME_LAST_STOP':
            return 'Tài xế đã đến điểm dừng cuối cùng của hành trình.';
        case 'CONFIRMED_IN':
            return 'Đã xác nhận hoàn thành tất cả các điểm dừng và đang chuẩn bị kết thúc.';
        case 'COMPLETED':
            return 'Chuyến đi đã hoàn thành thành công.';
        case 'CANCELLED':
            return 'Chuyến đi đã bị hủy.';
        default:
            return '';
    }
};

export default TripStatusTimeline;