import React, { useState, useEffect } from 'react';
import { Box, Typography, Card, CardContent, Button, Chip, CircularProgress, IconButton, Divider, Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem, Select, FormControl, InputLabel, Grid } from '@mui/material';
import { useParams, useHistory } from 'react-router-dom';
import { request } from 'api';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import InventoryIcon from '@mui/icons-material/Inventory';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import { errorNoti, successNoti } from 'utils/notification';
import StandardTable from 'components/StandardTable';

const DriverOrderManagement = () => {
    const { tripId } = useParams();
    const history = useHistory();

    const [loading, setLoading] = useState(true);
    const [orders, setOrders] = useState([]);
    const [trip, setTrip] = useState(null);
    const [route, setRoute] = useState(null);
    const [openStatusDialog, setOpenStatusDialog] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [newStatus, setNewStatus] = useState('');
    const [statusNote, setStatusNote] = useState('');
    const [pickupDialogOpen, setPickupDialogOpen] = useState(false);
    const [selectedOrdersForPickup, setSelectedOrdersForPickup] = useState([]);
    const [deliverDialogOpen, setDeliverDialogOpen] = useState(false);
    const [selectedOrdersForDelivery, setSelectedOrdersForDelivery] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);

                // Get orders for this route vehicle
                await request('get', `/smdeli/ordermanager/middle-mile/for-out/${tripId}`, (res) => {
                    setOrders(res.data || []);
                });

                // Get route vehicle details
                await request('get', `/smdeli/driver/trips/${tripId}`, (res) => {
                    setTrip(res.data);
                    if(res.data.routeScheduleId)
                    request('get', `/smdeli/route-scheduler/schedule/${res.data.routeScheduleId}`, (res) => {
                        // Get route details
                        if (res.data?.routeId) {
                            request('get', `/smdeli/middle-mile/routes/${res.data.routeId}`, (routeRes) => {
                                setRoute(routeRes.data);
                            });
                        }
                    })

                });

                setLoading(false);
            } catch (error) {
                console.error("Error fetching orders data:", error);
                errorNoti("Failed to load orders data");
                setLoading(false);
            }
        };

        fetchData();
    }, [tripId]);

    const handleBack = () => {
        history.push('/driver/dashboard');
    };

    const handleStatusChange = (order) => {
        setSelectedOrder(order);
        setNewStatus(order.status);
        setStatusNote('');
        setOpenStatusDialog(true);
    };

    const handleStatusUpdate = () => {
        if (!selectedOrder || !newStatus) {
            errorNoti("Please select a valid status");
            return;
        }

        request(
            'put',
            `/smdeli/driver/orders/${selectedOrder.id}/status`,
            () => {
                successNoti("Order status updated successfully");
                // Update the order status in the local state
                setOrders(orders.map(o => o.id === selectedOrder.id ? { ...o, status: newStatus } : o));
                setOpenStatusDialog(false);
            },
            {
                401: () => errorNoti("Unauthorized action"),
                400: () => errorNoti("Unable to update order status")
            },
            { status: newStatus, notes: statusNote }
        );
    };

    const handleSelectOrderForPickup = (selected) => {
        setSelectedOrdersForPickup(selected);
    };

    const handlePickupOrders = () => {
        if (selectedOrdersForPickup.length === 0) {
            errorNoti("Please select at least one order to pick up");
            return;
        }

        setPickupDialogOpen(true);
    };

    const confirmPickupOrders = () => {
        const orderIds = selectedOrdersForPickup.map(order => order.id);

        request(
            'put',
            '/smdeli/driver/pickup-orders',
            () => {
                successNoti("Orders picked up successfully");
                // Update the order statuses in the local state
                setOrders(orders.map(o => selectedOrdersForPickup.find(so => so.id === o.id) ? { ...o, status: 'DELIVERING' } : o));
                setPickupDialogOpen(false);
                setSelectedOrdersForPickup([]);
            },
            {
                401: () => errorNoti("Unauthorized action"),
                400: () => errorNoti("Unable to pick up orders")
            },
            orderIds
        );
    };

    const handleSelectOrderForDelivery = (selected) => {
        setSelectedOrdersForDelivery(selected);
    };

    const handleDeliverOrders = () => {
        if (selectedOrdersForDelivery.length === 0) {
            errorNoti("Please select at least one order to deliver");
            return;
        }

        setDeliverDialogOpen(true);
    };

    const confirmDeliverOrders = () => {
        const orderIds = selectedOrdersForDelivery.map(order => order.id);

        request(
            'put',
            '/smdeli/driver/deliver-orders',
            () => {
                successNoti("Orders delivered successfully");
                // Update the order statuses in the local state
                setOrders(orders.map(o => selectedOrdersForDelivery.find(so => so.id === o.id) ? { ...o, status: 'DELIVERED' } : o));
                setDeliverDialogOpen(false);
                setSelectedOrdersForDelivery([]);
            },
            {
                401: () => errorNoti("Unauthorized action"),
                400: () => errorNoti("Unable to deliver orders")
            },
            orderIds
        );
    };

    const columns = [
        {
            title: "Order ID",
            field: "id",
            render: (rowData) => (
                <Typography variant="body2" noWrap>
                    {rowData.id.substring(0, 8)}...
                </Typography>
            )
        },
        {
            title: "From",
            field: "sender",
            render: (rowData) => (
                <Box>
                    <Typography variant="body2">{rowData.senderName}</Typography>
                    <Typography variant="caption" color="textSecondary">{rowData.senderAddress?.substring(0, 20)}...</Typography>
                </Box>
            )
        },
        {
            title: "To",
            field: "recipient",
            render: (rowData) => (
                <Box>
                    <Typography variant="body2">{rowData.recipientName}</Typography>
                    <Typography variant="caption" color="textSecondary">{rowData.recipientAddress?.substring(0, 20)}...</Typography>
                </Box>
            )
        },
        {
            title: "Status",
            field: "status",
            render: (rowData) => {
                let color;
                switch (rowData.status) {
                    case 'COLLECTED_HUB':
                        color = 'info';
                        break;
                    case 'DELIVERING':
                        color = 'warning';
                        break;
                    case 'DELIVERED':
                        color = 'success';
                        break;
                    default:
                        color = 'default';
                }

                return (
                    <Chip
                        label={rowData.status}
                        color={color}
                        size="small"
                    />
                );
            }
        },
        {
            title: "Actions",
            field: "actions",
            render: (rowData) => (
                <Box>
                    <Button
                        variant="outlined"
                        size="small"
                        onClick={() => handleStatusChange(rowData)}
                    >
                        Update Status
                    </Button>
                </Box>
            )
        }
    ];

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="80vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={handleBack}
                sx={{ mb: 2 }}
            >
                Back to Dashboard
            </Button>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4">
                     {orders.length} orders - {orders.reduce((total, item) => total + item.packagesCount, 0)} packages
                </Typography>

                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={<InventoryIcon />}
                        onClick={handlePickupOrders}
                        disabled={selectedOrdersForPickup.length === 0}
                    >
                        Pickup Selected ({selectedOrdersForPickup.length})
                    </Button>

                    <Button
                        variant="contained"
                        color="success"
                        startIcon={<CheckCircleIcon />}
                        onClick={handleDeliverOrders}
                        disabled={selectedOrdersForDelivery.length === 0}
                    >
                        Deliver Selected ({selectedOrdersForDelivery.length})
                    </Button>
                </Box>
            </Box>

            <StandardTable
                title="Orders"
                columns={columns}
                data={orders}
                options={{
                    selection: true,
                    search: true,
                    pageSize: 10
                }}
                onSelectionChange={(selectedRows) => {
                    // Check if the selected orders can be picked up or delivered
                    const collectableOrders = selectedRows.filter(order => order.status === 'COLLECTED_HUB');
                    const deliverableOrders = selectedRows.filter(order => order.status === 'DELIVERING');

                    if (collectableOrders.length > 0) {
                        setSelectedOrdersForPickup(collectableOrders);
                        setSelectedOrdersForDelivery([]);
                    } else if (deliverableOrders.length > 0) {
                        setSelectedOrdersForDelivery(deliverableOrders);
                        setSelectedOrdersForPickup([]);
                    } else {
                        setSelectedOrdersForPickup([]);
                        setSelectedOrdersForDelivery([]);
                    }
                }}
            />

            {/* Status Update Dialog */}
            <Dialog open={openStatusDialog} onClose={() => setOpenStatusDialog(false)}>
                <DialogTitle>Update Order Status</DialogTitle>
                <DialogContent>
                    <Box sx={{ minWidth: 400, mt: 2 }}>
                        <FormControl fullWidth sx={{ mb: 2 }}>
                            <InputLabel>New Status</InputLabel>
                            <Select
                                value={newStatus}
                                onChange={(e) => setNewStatus(e.target.value)}
                                label="New Status"
                            >
                                <MenuItem value="COLLECTED_HUB">COLLECTED_HUB</MenuItem>
                                <MenuItem value="DELIVERING">DELIVERING</MenuItem>
                                <MenuItem value="DELIVERED">DELIVERED</MenuItem>
                                <MenuItem value="DELIVERY_FAILED">DELIVERY_FAILED</MenuItem>
                            </Select>
                        </FormControl>

                        <TextField
                            label="Notes"
                            multiline
                            rows={3}
                            fullWidth
                            value={statusNote}
                            onChange={(e) => setStatusNote(e.target.value)}
                            placeholder="Add any notes about this status change..."
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenStatusDialog(false)}>Cancel</Button>
                    <Button onClick={handleStatusUpdate} variant="contained" color="primary">
                        Update Status
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Pickup Confirmation Dialog */}
            <Dialog open={pickupDialogOpen} onClose={() => setPickupDialogOpen(false)}>
                <DialogTitle>Confirm Pickup</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>
                        Are you sure you want to pick up {selectedOrdersForPickup.length} orders?
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                        This will update their status to DELIVERING.
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setPickupDialogOpen(false)}>Cancel</Button>
                    <Button onClick={confirmPickupOrders} variant="contained" color="primary">
                        Confirm Pickup
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Delivery Confirmation Dialog */}
            <Dialog open={deliverDialogOpen} onClose={() => setDeliverDialogOpen(false)}>
                <DialogTitle>Confirm Delivery</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>
                        Are you sure you want to mark {selectedOrdersForDelivery.length} orders as delivered?
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                        This will update their status to DELIVERED.
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeliverDialogOpen(false)}>Cancel</Button>
                    <Button onClick={confirmDeliverOrders} variant="contained" color="success">
                        Confirm Delivery
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default DriverOrderManagement;