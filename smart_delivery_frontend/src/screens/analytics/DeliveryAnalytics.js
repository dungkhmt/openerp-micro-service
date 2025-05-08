import React, { useState, useEffect } from 'react';
import {
    Paper,
    Grid,
    Typography,
    Box,
    CircularProgress,
    MenuItem,
    Select,
    FormControl,
    InputLabel,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    LinearProgress,
    Card,
    CardContent,
    Stack,
    TextField
} from '@mui/material';
import {
    CheckCircle as CheckCircleIcon,
    Cancel as CancelIcon,
    Schedule as ScheduleIcon,
    Timeline as TimelineIcon
} from '@mui/icons-material';
import axios from 'axios';

const DeliveryAnalytics = () => {
    const [hubId, setHubId] = useState(null);
    const [hubs, setHubs] = useState([]);
    const [analytics, setAnalytics] = useState(null);
    const [dateRange, setDateRange] = useState({
        startDate: new Date().toISOString().split('T')[0],
        endDate: new Date().toISOString().split('T')[0]
    });
    const [loading, setLoading] = useState(true);
    const [shipperPerformance, setShipperPerformance] = useState([]);

    useEffect(() => {
        fetchHubs();
    }, []);

    useEffect(() => {
        if (hubId) {
            fetchAnalytics();
        }
    }, [hubId, dateRange]);

    const fetchHubs = async () => {
        try {
            const response = await axios.get('/api/hubs');
            setHubs(response.data);
            if (response.data.length > 0) {
                setHubId(response.data[0].id);
            }
        } catch (error) {
            console.error('Failed to fetch hubs:', error);
        }
    };

    const fetchAnalytics = async () => {
        setLoading(true);
        try {
            const response = await axios.get(`/api/third-mile/analytics/hub/${hubId}/range`, {
                params: {
                    startDate: dateRange.startDate,
                    endDate: dateRange.endDate
                }
            });
            setAnalytics(response.data);
            setShipperPerformance(response.data.shipperPerformance || []);
            setLoading(false);
        } catch (error) {
            console.error('Failed to fetch analytics:', error);
            setLoading(false);
        }
    };

    if (loading && !analytics) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box p={3}>
            <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
                <Grid container spacing={3} alignItems="center">
                    <Grid item xs={12} md={4}>
                        <Typography variant="h5">Delivery Analytics</Typography>
                    </Grid>
                    <Grid item xs={12} md={4}>
                        <FormControl fullWidth>
                            <InputLabel>Select Hub</InputLabel>
                            <Select
                                value={hubId || ''}
                                onChange={(e) => setHubId(e.target.value)}
                                label="Select Hub"
                            >
                                {hubs.map(hub => (
                                    <MenuItem key={hub.id} value={hub.id}>
                                        {hub.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} md={4}>
                        <Stack direction="row" spacing={2}>
                            <TextField
                                label="Start Date"
                                type="date"
                                value={dateRange.startDate}
                                onChange={(e) => setDateRange(prev => ({ ...prev, startDate: e.target.value }))}
                                InputLabelProps={{ shrink: true }}
                                fullWidth
                            />
                            <TextField
                                label="End Date"
                                type="date"
                                value={dateRange.endDate}
                                onChange={(e) => setDateRange(prev => ({ ...prev, endDate: e.target.value }))}
                                InputLabelProps={{ shrink: true }}
                                fullWidth
                            />
                        </Stack>
                    </Grid>
                </Grid>
            </Paper>

            {analytics && (
                <>
                    <Grid container spacing={3}>
                        <Grid item xs={12} sm={6} md={3}>
                            <Card>
                                <CardContent>
                                    <Typography color="textSecondary" gutterBottom>
                                        Total Deliveries
                                    </Typography>
                                    <Typography variant="h4">
                                        <TimelineIcon color="primary" sx={{ mr: 1 }} />
                                        {analytics.totalDeliveries}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <Card>
                                <CardContent>
                                    <Typography color="textSecondary" gutterBottom>
                                        Completed
                                    </Typography>
                                    <Typography variant="h4" sx={{ color: 'success.main' }}>
                                        <CheckCircleIcon sx={{ mr: 1 }} />
                                        {analytics.completedDeliveries}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <Card>
                                <CardContent>
                                    <Typography color="textSecondary" gutterBottom>
                                        Failed
                                    </Typography>
                                    <Typography variant="h4" sx={{ color: 'error.main' }}>
                                        <CancelIcon sx={{ mr: 1 }} />
                                        {analytics.failedDeliveries}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <Card>
                                <CardContent>
                                    <Typography color="textSecondary" gutterBottom>
                                        Pending
                                    </Typography>
                                    <Typography variant="h4" sx={{ color: 'warning.main' }}>
                                        <ScheduleIcon sx={{ mr: 1 }} />
                                        {analytics.pendingDeliveries}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    </Grid>

                    <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
                        <Typography variant="h6" gutterBottom>Success Rate</Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <Box sx={{ width: '100%', mr: 1 }}>
                                <LinearProgress
                                    variant="determinate"
                                    value={Math.round(analytics.successRate)}
                                    sx={{ height: 20, borderRadius: 5 }}
                                />
                            </Box>
                            <Box sx={{ minWidth: 35 }}>
                                <Typography variant="body2">
                                    {Math.round(analytics.successRate)}%
                                </Typography>
                            </Box>
                        </Box>
                    </Paper>

                    <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
                        <Typography variant="h6" gutterBottom>Shipper Performance</Typography>
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Shipper Name</TableCell>
                                        <TableCell align="right">Total Assignments</TableCell>
                                        <TableCell align="right">Completed Deliveries</TableCell>
                                        <TableCell align="right">Success Rate</TableCell>
                                        <TableCell align="right">Avg Delivery Time (min)</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {shipperPerformance.map((shipper) => (
                                        <TableRow key={shipper.shipperId}>
                                            <TableCell>{shipper.shipperName}</TableCell>
                                            <TableCell align="right">{shipper.totalAssignments}</TableCell>
                                            <TableCell align="right">{shipper.completedDeliveries}</TableCell>
                                            <TableCell align="right">
                                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                    <Box sx={{ width: '100%', mr: 1 }}>
                                                        <LinearProgress
                                                            variant="determinate"
                                                            value={Math.round(shipper.successRate)}
                                                        />
                                                    </Box>
                                                    <Box sx={{ minWidth: 35 }}>
                                                        <Typography variant="body2">
                                                            {Math.round(shipper.successRate)}%
                                                        </Typography>
                                                    </Box>
                                                </Box>
                                            </TableCell>
                                            <TableCell align="right">
                                                {Math.round(shipper.averageDeliveryTime)}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Paper>
                </>
            )}
        </Box>
    );
};

export default DeliveryAnalytics; 