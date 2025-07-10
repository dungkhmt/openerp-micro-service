import React, { useState, useEffect } from 'react';
import {
    Paper,
    Typography,
    Button,
    Box,
    CircularProgress,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
    Chip,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Stack,
    Divider,
    IconButton,
    Tooltip,
    Snackbar,
    Alert
} from '@mui/material';
import {
    Refresh as RefreshIcon,
    LocationOn as LocationIcon,
    Schedule as ScheduleIcon,
    DirectionsCar as CarIcon,
    Info as InfoIcon
} from '@mui/icons-material';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const RouteOptimization = () => {
    const { shipperId } = useParams();
    const [route, setRoute] = useState(null);
    const [loading, setLoading] = useState(true);
    const [optimizing, setOptimizing] = useState(false);
    const [detailsDialog, setDetailsDialog] = useState({ open: false, stop: null });
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    useEffect(() => {
        fetchRoute();
    }, [shipperId]);

    const fetchRoute = async () => {
        setLoading(true);
        try {
            const response = await axios.get(`/api/third-mile/shipper/${shipperId}/route`);
            setRoute(response.data);
            setLoading(false);
        } catch (error) {
            setSnackbar({
                open: true,
                message: 'Failed to fetch route',
                severity: 'error'
            });
            setLoading(false);
        }
    };

    const optimizeRoute = async () => {
        setOptimizing(true);
        try {
            await axios.post(`/api/third-mile/shipper/${shipperId}/optimize-route`);
            setSnackbar({
                open: true,
                message: 'Route optimized successfully',
                severity: 'success'
            });
            fetchRoute();
        } catch (error) {
            setSnackbar({
                open: true,
                message: 'Failed to optimize route',
                severity: 'error'
            });
        } finally {
            setOptimizing(false);
        }
    };

    const showDeliveryDetails = (stop) => {
        setDetailsDialog({ open: true, stop });
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
            <Paper elevation={3} sx={{ p: 3 }}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                    <Box>
                        <Typography variant="h4" gutterBottom>
                            Delivery Route
                        </Typography>
                        <Typography variant="subtitle1">
                            Shipper: {route?.shipperName}
                        </Typography>
                    </Box>
                    <Button
                        variant="contained"
                        startIcon={<RefreshIcon />}
                        onClick={optimizeRoute}
                        disabled={optimizing}
                    >
                        {optimizing ? 'Optimizing...' : 'Optimize Route'}
                    </Button>
                </Box>

                <Paper elevation={1} sx={{ p: 2, mb: 3 }}>
                    <Stack direction="row" spacing={2}>
                        <Chip
                            icon={<LocationIcon />}
                            label={`Starting Hub: ${route?.startingHub}`}
                            color="primary"
                        />
                        <Chip
                            icon={<CarIcon />}
                            label={`Total Distance: ${route?.totalDistance?.toFixed(2)} km`}
                            color="success"
                        />
                        <Chip
                            icon={<ScheduleIcon />}
                            label={`Duration: ${route?.estimatedDuration} minutes`}
                            color="warning"
                        />
                    </Stack>
                </Paper>

                <List>
                    {route?.stops?.map((stop, index) => (
                        <React.Fragment key={stop.orderId}>
                            {index > 0 && <Divider />}
                            <ListItem>
                                <ListItemIcon>
                                    <Box
                                        sx={{
                                            width: 32,
                                            height: 32,
                                            borderRadius: '50%',
                                            bgcolor: 'primary.main',
                                            color: 'white',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center'
                                        }}
                                    >
                                        {index + 1}
                                    </Box>
                                </ListItemIcon>
                                <ListItemText
                                    primary={
                                        <Box display="flex" alignItems="center">
                                            <Typography variant="subtitle1" sx={{ mr: 2 }}>
                                                {stop.recipientName}
                                            </Typography>
                                            <Chip
                                                size="small"
                                                icon={<LocationIcon />}
                                                label={stop.recipientAddress.substring(0, 30) + '...'}
                                            />
                                        </Box>
                                    }
                                    secondary={
                                        <Stack direction="row" spacing={1} mt={1}>
                                            <Chip
                                                size="small"
                                                icon={<ScheduleIcon />}
                                                label={stop.estimatedDeliveryTime}
                                            />
                                            <Chip
                                                size="small"
                                                icon={<CarIcon />}
                                                label={`${stop.distanceFromPreviousStop.toFixed(2)} km from previous`}
                                            />
                                        </Stack>
                                    }
                                />
                                <IconButton onClick={() => showDeliveryDetails(stop)}>
                                    <InfoIcon />
                                </IconButton>
                            </ListItem>
                        </React.Fragment>
                    ))}
                </List>
            </Paper>

            <Dialog
                open={detailsDialog.open}
                onClose={() => setDetailsDialog({ open: false, stop: null })}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle>Delivery Details</DialogTitle>
                <DialogContent>
                    {detailsDialog.stop && (
                        <Stack spacing={2}>
                            <Typography><strong>Recipient:</strong> {detailsDialog.stop.recipientName}</Typography>
                            <Typography><strong>Address:</strong> {detailsDialog.stop.recipientAddress}</Typography>
                            <Typography><strong>Phone:</strong> {detailsDialog.stop.recipientPhone}</Typography>
                            <Typography>
                                <strong>Distance from previous stop:</strong> {detailsDialog.stop.distanceFromPreviousStop.toFixed(2)} km
                            </Typography>
                            <Typography>
                                <strong>Estimated delivery time:</strong> {detailsDialog.stop.estimatedDeliveryTime}
                            </Typography>
                            {detailsDialog.stop.notes && (
                                <Typography><strong>Notes:</strong> {detailsDialog.stop.notes}</Typography>
                            )}
                        </Stack>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDetailsDialog({ open: false, stop: null })}>
                        Close
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
            >
                <Alert
                    severity={snackbar.severity}
                    onClose={() => setSnackbar({ ...snackbar, open: false })}
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default RouteOptimization; 