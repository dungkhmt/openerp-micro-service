import React, { useState, useEffect } from 'react';
import {
    Paper,
    Typography,
    Button,
    TextField,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    CircularProgress,
    Box,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Divider,
    Card,
    CardContent,
    Stack,
    Snackbar,
    Alert
} from '@mui/material';
import {
    CheckCircle as CheckCircleIcon,
    Schedule as ScheduleIcon,
    Error as ErrorIcon
} from '@mui/icons-material';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const DeliveryTracking = () => {
    const { orderId } = useParams();
    const [orderStatus, setOrderStatus] = useState(null);
    const [statusHistory, setStatusHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [otpDialogOpen, setOtpDialogOpen] = useState(false);
    const [otp, setOtp] = useState('');
    const [failureReason, setFailureReason] = useState('');
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    useEffect(() => {
        fetchOrderStatus();
        fetchStatusHistory();
    }, [orderId]);

    const fetchOrderStatus = async () => {
        try {
            const response = await axios.get(`/api/third-mile/order/${orderId}/tracking-info`);
            setOrderStatus(response.data);
            setLoading(false);
        } catch (error) {
            setSnackbar({ open: true, message: 'Failed to fetch order status', severity: 'error' });
            setLoading(false);
        }
    };

    const fetchStatusHistory = async () => {
        try {
            const response = await axios.get(`/api/third-mile/order/${orderId}/status-history`);
            setStatusHistory(response.data);
        } catch (error) {
            setSnackbar({ open: true, message: 'Failed to fetch status history', severity: 'error' });
        }
    };

    const generateOTP = async () => {
        try {
            const response = await axios.post(`/api/third-mile/order/${orderId}/generate-code`);
            setSnackbar({ open: true, message: 'OTP generated successfully', severity: 'success' });
            setOtpDialogOpen(true);
        } catch (error) {
            setSnackbar({ open: true, message: 'Failed to generate OTP', severity: 'error' });
        }
    };

    const confirmDelivery = async () => {
        try {
            const response = await axios.post(`/api/third-mile/order/${orderId}/confirm-delivery`, {
                type: 'otp',
                value: otp
            });
            setSnackbar({ open: true, message: 'Delivery confirmed successfully', severity: 'success' });
            setOtpDialogOpen(false);
            fetchOrderStatus();
            fetchStatusHistory();
        } catch (error) {
            setSnackbar({ open: true, message: 'Failed to confirm delivery', severity: 'error' });
        }
    };

    const reportFailedDelivery = async () => {
        try {
            await axios.post(`/api/third-mile/order/${orderId}/failed-attempt`, {
                reason: failureReason
            });
            setSnackbar({ open: true, message: 'Failed delivery reported successfully', severity: 'success' });
            setFailureReason('');
            fetchOrderStatus();
            fetchStatusHistory();
        } catch (error) {
            setSnackbar({ open: true, message: 'Failed to report delivery attempt', severity: 'error' });
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'COMPLETED':
                return <CheckCircleIcon color="success" />;
            case 'IN_PROGRESS':
                return <CircularProgress size={20} />;
            default:
                return <ScheduleIcon color="warning" />;
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box p={3}>
            <Paper elevation={3}>
                <Box p={3}>
                    <Typography variant="h4" gutterBottom>Delivery Tracking</Typography>
                    <Typography variant="subtitle1" gutterBottom>Order ID: {orderId}</Typography>

                    <Card sx={{ mt: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>Current Status</Typography>
                            <Typography>{orderStatus}</Typography>
                        </CardContent>
                    </Card>

                    <Card sx={{ mt: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>Status History</Typography>
                            <List>
                                {statusHistory.map((status, index) => (
                                    <React.Fragment key={index}>
                                        {index > 0 && <Divider />}
                                        <ListItem>
                                            <ListItemIcon>
                                                {getStatusIcon(status)}
                                            </ListItemIcon>
                                            <ListItemText primary={status} />
                                        </ListItem>
                                    </React.Fragment>
                                ))}
                            </List>
                        </CardContent>
                    </Card>

                    <Stack direction="row" spacing={2} sx={{ mt: 3 }}>
                        <Button variant="contained" color="primary" onClick={generateOTP}>
                            Generate Delivery OTP
                        </Button>
                        <Button variant="contained" color="error" onClick={() => reportFailedDelivery()}>
                            Report Failed Delivery
                        </Button>
                    </Stack>

                    <TextField
                        fullWidth
                        multiline
                        rows={4}
                        variant="outlined"
                        label="Failure Reason"
                        value={failureReason}
                        onChange={(e) => setFailureReason(e.target.value)}
                        sx={{ mt: 3 }}
                    />
                </Box>
            </Paper>

            <Dialog open={otpDialogOpen} onClose={() => setOtpDialogOpen(false)}>
                <DialogTitle>Confirm Delivery with OTP</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Enter OTP"
                        fullWidth
                        variant="outlined"
                        value={otp}
                        onChange={(e) => setOtp(e.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOtpDialogOpen(false)}>Cancel</Button>
                    <Button onClick={confirmDelivery} variant="contained" color="primary">
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
            >
                <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default DeliveryTracking; 