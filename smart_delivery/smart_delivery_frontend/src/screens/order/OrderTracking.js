import React, { useState } from "react";
import {
    Box,
    Button,
    TextField,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    List,
    ListItem,
    ListItemText,
    Chip,
    Paper,
    Container,
    Alert,
    CircularProgress,
    Stepper,
    Step,
    StepLabel,
    StepContent,
    Card,
    CardContent,
    Divider
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import { request } from "../../api";
import { API_PATH } from "../apiPaths";

const OrderTracking = () => {
    const [trackingCode, setTrackingCode] = useState('');
    const [orderHistory, setOrderHistory] = useState([]);
    const [orderInfo, setOrderInfo] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [hasSearched, setHasSearched] = useState(false);

    // Function to format status for display
    const formatStatus = (status) => {
        const statusMap = {
            'PENDING': 'Đang xử lý',
            'ASSIGNED': 'Đã phân công lấy hàng',
            'COLLECTED_COLLECTOR': 'Đã lấy hàng',
            'COLLECTED_HUB': 'Đã về hub nguồn',
            'CONFIRMED_IN': 'Đã về hub',
            'CONFIRMED_OUT': 'Đã xác nhận xuất hub nguồn',
            'DELIVERING': 'Đang vận chuyển đến hub đích',
            'DELIVERED': 'Đã giao đến hub đích',
            'CONFIRMED_IN_FINAL_HUB': 'Đã xác nhận nhập hub đích',
            'ASSIGNED_SHIPPER': 'Đã phân công giao hàng',
            'SHIPPING': 'Đang giao hàng',
            'SHIPPED': 'Đã giao hàng',
            'COMPLETED': 'Giao hàng thành công',
            'CANCELLED': 'Đã hủy',
            'DELIVERED_FAILED': 'Giao hàng đến hub thất bại',
            'SHIPPED_FAILED': 'Shipper giao thất bại'
        };
        return statusMap[status] || status;
    };

    // Function to get status color
    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING':
                return 'default';
            case 'ASSIGNED':
            case 'COLLECTED_COLLECTOR':
            case 'COLLECTED_HUB':
            case 'CONFIRMED_OUT':
                return 'primary';
            case 'DELIVERING':
            case 'DELIVERED':
            case 'CONFIRMED_IN_FINAL_HUB':
            case 'ASSIGNED_SHIPPER':
            case 'SHIPPING':
            case 'SHIPPED':
                return 'info';
            case 'COMPLETED':
                return 'success';
            case 'CANCELLED':
            case 'DELIVERED_FAILED':
            case 'SHIPPED_FAILED':
                return 'error';
            default:
                return 'default';
        }
    };

    // Function to get status step number for stepper
    const getStatusStep = (status) => {
        const stepMap = {
            'PENDING': 0,
            'ASSIGNED': 1,
            'COLLECTED_COLLECTOR': 2,
            'COLLECTED_HUB': 3,
            'CONFIRMED_OUT': 4,
            'DELIVERING': 5,
            'DELIVERED': 6,
            'CONFIRMED_IN_FINAL_HUB': 7,
            'ASSIGNED_SHIPPER': 8,
            'SHIPPING': 9,
            'SHIPPED': 10,
            'COMPLETED': 11
        };
        return stepMap[status] || 0;
    };

    const steps = [
        'Đang xử lý',
        'Đã phân công lấy hàng',
        'Đã lấy hàng',
        'Đã về hub nguồn',
        'Đã xuất hub nguồn',
        'Đang vận chuyển',
        'Đã đến hub đích',
        'Đã nhập hub đích',
        'Đã phân công giao hàng',
        'Đang giao hàng',
        'Đã giao hàng',
        'Hoàn thành'
    ];

    // Function to track order
    const trackOrder = async () => {
        if (!trackingCode.trim()) {
            setError('Vui lòng nhập mã vận đơn');
            return;
        }

        setIsLoading(true);
        setError('');
        setHasSearched(true);

        try {
            // Fetch order info
            await request(
                "get",
                `${API_PATH.ORDER}/${trackingCode}`,
                (res) => {
                    setOrderInfo(res.data);
                },
                {
                    404: () => {
                        setError("Không tìm thấy đơn hàng với mã vận đơn này");
                        setOrderInfo(null);
                        setOrderHistory([]);
                    },
                    500: () => {
                        setError("Có lỗi xảy ra khi tìm kiếm đơn hàng");
                        setOrderInfo(null);
                        setOrderHistory([]);
                    }
                }
            );

            // Fetch order history
            await request(
                "get",
                `${API_PATH.ORDER}/${trackingCode}/history`,
                (res) => {
                    setOrderHistory(res.data);
                },
                {
                    404: () => {
                        setOrderHistory([]);
                    },
                    500: () => {
                        setError("Có lỗi khi tải lịch sử đơn hàng");
                        setOrderHistory([]);
                    }
                }
            );
        } catch (error) {
            setError("Có lỗi xảy ra khi tra cứu đơn hàng");
            setOrderInfo(null);
            setOrderHistory([]);
        } finally {
            setIsLoading(false);
        }
    };

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            trackOrder();
        }
    };

    const getCurrentStatus = () => {
        if (orderHistory.length === 0) return null;
        return orderHistory[orderHistory.length - 1].status;
    };

    const currentStep = getCurrentStatus() ? getStatusStep(getCurrentStatus()) : 0;

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Header */}
            <Box textAlign="center" mb={4}>
                <LocalShippingIcon sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                <Typography variant="h4" component="h1" gutterBottom>
                    Tra cứu đơn hàng
                </Typography>
                <Typography variant="subtitle1" color="textSecondary">
                    Nhập mã vận đơn để xem trạng thái và lịch sử giao hàng
                </Typography>
            </Box>

            {/* Search Box */}
            <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
                <Box display="flex" gap={2} alignItems="flex-start">
                    <TextField
                        fullWidth
                        label="Nhập mã vận đơn"
                        variant="outlined"
                        value={trackingCode}
                        onChange={(e) => setTrackingCode(e.target.value)}
                        onKeyPress={handleKeyPress}
                        placeholder="Ví dụ: ORD123456789"
                        error={!!error && !isLoading}
                        helperText={error && !isLoading ? error : "Mã vận đơn bao gồm cả chữ và số"}
                    />
                    <Button
                        variant="contained"
                        size="large"
                        onClick={trackOrder}
                        disabled={isLoading}
                        startIcon={isLoading ? <CircularProgress size={20} /> : <SearchIcon />}
                        sx={{ minWidth: 140, height: 56 }}
                    >
                        {isLoading ? 'Đang tìm...' : 'Tra cứu'}
                    </Button>
                </Box>
            </Paper>

            {/* Results */}
            {hasSearched && !isLoading && (
                <>
                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    {orderInfo && (
                        <>
                            {/* Order Information */}
                            <Card sx={{ mb: 4 }}>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        Thông tin đơn hàng #{trackingCode}
                                    </Typography>
                                    <Divider sx={{ mb: 2 }} />

                                    <Box display="flex" flexWrap="wrap" gap={4}>
                                        <Box flex={1} minWidth={250}>
                                            <Typography variant="subtitle2" color="textSecondary">
                                                Người gửi
                                            </Typography>
                                            <Typography variant="body1" gutterBottom>
                                                {orderInfo.senderName}
                                            </Typography>
                                            <Typography variant="body2" color="textSecondary">
                                                {orderInfo.senderPhone}
                                            </Typography>
                                            <Typography variant="body2" color="textSecondary">
                                                {orderInfo.senderAddress}
                                            </Typography>
                                        </Box>

                                        <Box flex={1} minWidth={250}>
                                            <Typography variant="subtitle2" color="textSecondary">
                                                Người nhận
                                            </Typography>
                                            <Typography variant="body1" gutterBottom>
                                                {orderInfo.recipientName}
                                            </Typography>
                                            <Typography variant="body2" color="textSecondary">
                                                {orderInfo.recipientPhone}
                                            </Typography>
                                            <Typography variant="body2" color="textSecondary">
                                                {orderInfo.recipientAddress}
                                            </Typography>
                                        </Box>

                                        <Box flex={1} minWidth={200}>
                                            <Typography variant="subtitle2" color="textSecondary">
                                                Thông tin giao hàng
                                            </Typography>
                                            <Typography variant="body2">
                                                <strong>Dịch vụ:</strong> {orderInfo.orderType || 'Bình thường'}
                                            </Typography>
                                            <Typography variant="body2">
                                                <strong>Tiền thu hộ:</strong> {orderInfo.totalprice?.toLocaleString()}đ
                                            </Typography>
                                            {getCurrentStatus() && (
                                                <Box mt={1}>
                                                    <Chip
                                                        label={formatStatus(getCurrentStatus())}
                                                        color={getStatusColor(getCurrentStatus())}
                                                        size="small"
                                                    />
                                                </Box>
                                            )}
                                        </Box>
                                    </Box>
                                </CardContent>
                            </Card>

                            {/* Status Stepper */}


                            {/* Order History */}
                            <Card>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        Lịch sử trạng thái
                                    </Typography>
                                    <Divider sx={{ mb: 1 }} />

                                    {orderHistory.length === 0 ? (
                                        <Typography variant="body2" color="textSecondary">
                                            Chưa có lịch sử trạng thái
                                        </Typography>
                                    ) : (
                                        <List>
                                            {orderHistory
                                                .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                                                .map((history, index) => (
                                                    <ListItem
                                                        key={history.id}
                                                        divider={index < orderHistory.length - 1}
                                                        sx={{ pl: 0 }}
                                                    >
                                                        <ListItemText
                                                            primary={
                                                                <Box display="flex" alignItems="center" gap={1} mb={1}>
                                                                    <Chip
                                                                        label={formatStatus(history.status)}
                                                                        color={getStatusColor(history.status)}
                                                                        size="small"
                                                                    />
                                                                    {index === 0 && (
                                                                        <Chip
                                                                            label="Mới nhất"
                                                                            size="small"
                                                                            variant="outlined"
                                                                            color="primary"
                                                                        />
                                                                    )}
                                                                </Box>
                                                            }
                                                            secondary={
                                                                <Box>
                                                                    <Typography variant="body2" color="textSecondary">
                                                                        <strong>Thời gian:</strong> {new Date(history.createdAt).toLocaleString('vi-VN', {
                                                                        year: 'numeric',
                                                                        month: '2-digit',
                                                                        day: '2-digit',
                                                                        hour: '2-digit',
                                                                        minute: '2-digit'
                                                                    })}
                                                                    </Typography>
                                                                    {history.note && (
                                                                        <Typography variant="body2" color="textSecondary">
                                                                            <strong>Ghi chú:</strong> {history.note}
                                                                        </Typography>
                                                                    )}
                                                                    {history.location && (
                                                                        <Typography variant="body2" color="textSecondary">
                                                                            <strong>Vị trí:</strong> {history.location}
                                                                        </Typography>
                                                                    )}
                                                                </Box>
                                                            }
                                                        />
                                                    </ListItem>
                                                ))}
                                        </List>
                                    )}
                                </CardContent>
                            </Card>
                        </>
                    )}

                    {!orderInfo && !error && hasSearched && !isLoading && (
                        <Alert severity="info">
                            Không tìm thấy thông tin đơn hàng
                        </Alert>
                    )}
                </>
            )}

            {/* Footer Info */}
            <Box mt={6} textAlign="center">
                <Typography variant="body2" color="textSecondary">
                    Để được hỗ trợ thêm, vui lòng liên hệ hotline: <strong>1900-1234</strong>
                </Typography>
                <Typography variant="body2" color="textSecondary">
                    Hoặc email: support@smdeli.com
                </Typography>
            </Box>
        </Container>
    );
};

export default OrderTracking;