import React, { useState, useEffect } from 'react';
import { Box, Paper, Typography, Grid, TextField, Button, Skeleton } from '@mui/material';
import { request } from '../../../api';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import { Table, TableHead, TableRow, TableCell, TableBody, TableContainer } from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import { toast, Toaster } from "react-hot-toast";

const RoutingResult = () => {
    const { id } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const [trips, setTrips] = useState([]);
    const [loading, setLoading] = useState(false);
    const [addressInfoMap, setAddressInfoMap] = useState({});
    const [vehicleInfoMap, setVehicleInfoMap] = useState({});
    const [deliveryItemInfoMap, setDeliveryItemInfoMap] = useState({});
    const [inputData, setInputData] = useState({});
    const [deliveryPersonOptions, setDeliveryPersonOptions] = useState([]);

    useEffect(() => {
        if (location.state && location.state.routingResult) {
            const trucks = location.state.routingResult.trucks || [];
            setTrips(trucks);

            const initialInputData = {};
            trucks.forEach(trip => {
                initialInputData[trip.vehicleId] = {
                    deliveryPerson: '',
                    description: ''
                };
            });
            setInputData(initialInputData);

            fetchDetailInfo(trucks);
        } else {
            alert('No routing result found!');
        }
    }, []);

    useEffect(() => {
        request("get", "/delivery-persons/available", (res) => {
            setDeliveryPersonOptions(res.data);
        }).then();
    }, []);

    const fetchDetailInfo = (trucks) => {
        setLoading(true);

        const warehouseIds = new Set();
        const vehicleIds = new Set();
        const customerAddressIds = new Set();
        const deliveryItemIds = new Set();

        trucks.forEach(trip => {
            if (trip.warehouse) warehouseIds.add(trip.warehouse);
            if (trip.vehicleId) vehicleIds.add(trip.vehicleId);

            trip.route.forEach(pointId => {
                if (pointId !== trip.route[0] && pointId !== trip.route[trip.route.length - 1]) {
                    customerAddressIds.add(pointId);
                } else {
                    warehouseIds.add(pointId);
                }
            });

            trip.loadedItems.forEach(itemId => deliveryItemIds.add(itemId));
        });

        const newAddressInfoMap = {};
        const newVehicleInfoMap = {};
        const newDeliveryItemInfoMap = {};

        [...warehouseIds].forEach(id => {
            request("get", `/warehouses/${id}`, (res) => {
                newAddressInfoMap[id] = { name: res.data.name, lat: res.data.latitude, lng: res.data.longitude };
                setAddressInfoMap(prev => ({ ...prev, ...newAddressInfoMap }));
            }).then();
        });

        // fetch vehicles
        [...vehicleIds].forEach(id => {
            request("get", `/vehicles/${id}`, (res) => {
                newVehicleInfoMap[id] = { name: res.data.name };
                setVehicleInfoMap(prev => ({ ...prev, ...newVehicleInfoMap }));
            }).then();
        });

        // fetch customer addresses 1 lần
        if (customerAddressIds.size > 0) {
            const payload = [...customerAddressIds];
            request("post", "/customer-addresses/list", (res) => {
                res.data.forEach(address => {
                    newAddressInfoMap[address.customerAddressId] = { name: address.addressName, lat: address.latitude, lng: address.longitude };
                });
                setAddressInfoMap(prev => ({ ...prev, ...newAddressInfoMap }));
            }, {
                onError: (e) => {
                    toast.error(e?.response?.data || "Error occured!");
                }
            }, payload).then();
        }

        // fetch delivery items 1 lần
        if (deliveryItemIds.size > 0) {
            const payload = [...deliveryItemIds];
            request("post", "/assigned-order-items/delivery-items", (res) => {
                res.data.forEach(item => {
                    newDeliveryItemInfoMap[item.assignedOrderItemId] = {
                        orderId: item.orderId,
                        productName: item.productName,
                        weight: item.weight,
                        originalQuantity: item.originalQuantity,
                        bayCode: item.bayCode,
                        lotId: item.lotId,
                    };
                });
                setDeliveryItemInfoMap(prev => ({ ...prev, ...newDeliveryItemInfoMap }));
            }, {
                onError: (e) => {
                    toast.error(e?.response?.data || "Error occured!");
                }
            }, payload).then();
        }

        setLoading(false);
    };


    const handleInputChange = (tripId, field, value) => {
        setInputData(prev => ({
            ...prev,
            [tripId]: {
                ...prev[tripId],
                [field]: value
            }
        }));
    };

    const handleSubmit = async () => {
        const missingDelivery = trips.filter(
            trip => !inputData[trip.vehicleId]?.deliveryPerson
        );

        if (missingDelivery.length > 0) {
            toast.error("Please assign a delivery person for all trips before saving.");
            return;
        }
        const payload = trips.map(trip => {
            return {
                warehouseId: trip.warehouse,
                vehicleId: trip.vehicleId,
                deliveryPersonId: inputData[trip.vehicleId]?.deliveryPerson,
                description: inputData[trip.vehicleId]?.description || '',
                shipmentId: id,
                totalWeight: trip.totalWeight,
                totalLocations: trip.route.length > 2 ? trip.route.length - 2 : 0,
                distance: trip.routeDistance,
                coordinates: trip.route.map(pointId => ({
                    lat: addressInfoMap[pointId]?.lat,
                    lng: addressInfoMap[pointId]?.lng
                })),
                items: trip.loadedItems.map((itemId, idx) => ({
                    orderId: deliveryItemInfoMap[itemId]?.orderId,
                    assignedOrderItemId: itemId,
                    sequence: trip.itemSequences[idx],
                    quantity: deliveryItemInfoMap[itemId]?.originalQuantity,
                }))
            };
        });

        console.log('Submit payload:', payload);
        const requestUrl = "/delivery-trips/batch";
        request("post", requestUrl, (res) => {
            if (res.status === 200) {
                alert("Trips created successfully!");
                navigate(`/delivery-manager/shipments/${id}`);
            }
        }, {
            onError: (e) => {
                toast.error(e?.response?.data || "Error occured!");
            }
        }, payload);
    };


    return (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
            <Toaster />
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <IconButton color="primary" onClick={() => navigate(`/delivery-manager/shipments/${id}/auto-routing`)} sx={{ color: 'grey.700', mr: 1 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
                    Routing Result
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<SaveIcon />}
                    sx={{
                        marginLeft: 'auto',
                        backgroundColor: '#019160',
                        color: '#fff',
                        '&:hover': {
                            backgroundColor: '#2fbe8e',
                        },
                        '&:active': {
                            backgroundColor: '#01b075',
                        },
                    }}
                    onClick={handleSubmit}
                >
                    Save
                </Button>
            </Box>
            {location.state?.routingResult?.totalDistance && (
                <Paper elevation={1} sx={{ p: 2, mb: 3, backgroundColor: '#f5f5f5' }}>
                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                        Total Distance: {(location.state.routingResult.totalDistance / 1000).toFixed(2)} km
                    </Typography>
                </Paper>
            )}


            {loading ? (
                <Skeleton variant="rectangular" height={400} />
            ) : (
                <Grid container spacing={3}>
                    {trips.map((trip, index) => (
                        <Grid item xs={12} key={trip.vehicleId}>
                            <Paper elevation={3} sx={{ p: 3 }}>
                                <Typography variant="h6" gutterBottom>
                                    Delivery Trip {index + 1}
                                </Typography>

                                <Box sx={{ mt: 2, display: 'flex', alignItems: 'center' }}>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mr: 1 }}>
                                        Departure Warehouse:
                                    </Typography>
                                    <Typography variant="body2">
                                        {addressInfoMap[trip.warehouse]?.name || trip.warehouse}
                                    </Typography>
                                </Box>

                                <Box sx={{ mt: 1, display: 'flex', alignItems: 'center' }}>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mr: 1 }}>
                                        Vehicle:
                                    </Typography>
                                    <Typography variant="body2">
                                        {vehicleInfoMap[trip.vehicleId]?.name || trip.vehicleId}
                                    </Typography>
                                </Box>

                                <Box sx={{ mt: 1, display: 'flex', alignItems: 'center' }}>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mr: 1 }}>
                                        Distance:
                                    </Typography>
                                    <Typography variant="body2">
                                        {(trip.routeDistance / 1000).toFixed(2)} km
                                    </Typography>
                                </Box>


                                <Box sx={{ mt: 2 }}>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                        Delivery Route:
                                    </Typography>
                                    <TableContainer component={Paper} variant="outlined">
                                        <Table size="small">
                                            <TableHead>
                                                <TableRow>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>#</TableCell>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>Location</TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {trip.route.map((pointId, idx) => (
                                                    <TableRow key={idx}>
                                                        <TableCell>{idx + 1}</TableCell>
                                                        <TableCell>{addressInfoMap[pointId]?.name || pointId}</TableCell>
                                                    </TableRow>
                                                ))}
                                            </TableBody>
                                        </Table>
                                    </TableContainer>
                                </Box>

                                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', backgroundColor: '#f5f5f5', padding: '10px', borderRadius: '4px' }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mr: 1 }}>
                                            Total Locations:
                                        </Typography>
                                        <Typography variant="body2">
                                            {trip.route.length > 2 ? trip.route.length - 2 : 0}
                                        </Typography>
                                    </Box>
                                </Box>


                                <Box sx={{ mt: 2 }}>
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                        Delivery Items:
                                    </Typography>
                                    <TableContainer component={Paper} variant="outlined">
                                        <Table size="small">
                                            <TableHead>
                                                <TableRow>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>Sequence</TableCell>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>Product</TableCell>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>Weight (kg)</TableCell>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>Bay Code</TableCell>
                                                    <TableCell sx={{ fontWeight: 'bold' }}>Lot ID</TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {[...trip.loadedItems.map((itemId, idx) => ({
                                                    itemId,
                                                    sequence: trip.itemSequences[idx],
                                                }))]
                                                    .sort((a, b) => a.sequence - b.sequence) 
                                                    .map(({ itemId, sequence }, idx) => {
                                                        const item = deliveryItemInfoMap[itemId];
                                                        if (!item) return null;
                                                        return (
                                                            <TableRow key={idx}>
                                                                <TableCell>{sequence}</TableCell>
                                                                <TableCell>{item.productName}</TableCell>
                                                                <TableCell>{item.weight}</TableCell>
                                                                <TableCell>{item.originalQuantity}</TableCell>
                                                                <TableCell>{item.bayCode}</TableCell>
                                                                <TableCell>{item.lotId}</TableCell>
                                                            </TableRow>
                                                        );
                                                    })}
                                            </TableBody>

                                        </Table>
                                    </TableContainer>
                                </Box>

                                {/* Total Weight and Max Weight */}
                                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', backgroundColor: '#f5f5f5', padding: '10px', borderRadius: '4px' }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mr: 1 }}>
                                            Total Weight:
                                        </Typography>
                                        <Typography variant="body2">
                                            {trip.totalWeight} kg
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mr: 1 }}>
                                            Max Weight:
                                        </Typography>
                                        <Typography variant="body2">
                                            {trip.maxWeight} kg
                                        </Typography>
                                    </Box>
                                </Box>



                                <Box sx={{ mt: 3 }}>
                                    <FormControl fullWidth sx={{ mt: 2 }}>
                                        <InputLabel>Delivery Person</InputLabel>
                                        <Select
                                            value={inputData[trip.vehicleId]?.deliveryPerson || ''}
                                            label="Delivery Person"
                                            onChange={(e) => handleInputChange(trip.vehicleId, 'deliveryPerson', e.target.value)}
                                        >
                                            {deliveryPersonOptions
                                                .filter(person => {
                                                    // Kiểm tra xem person.id đã được chọn ở trip khác chưa
                                                    return !Object.entries(inputData).some(([vid, data]) =>
                                                        vid !== trip.vehicleId && data.deliveryPerson === person.userLoginId
                                                    );
                                                })
                                                .map(person => (
                                                    <MenuItem key={person.userLoginId} value={person.userLoginId}>
                                                        {person.fullName}
                                                    </MenuItem>
                                                ))
                                            }
                                        </Select>
                                    </FormControl>


                                    <TextField
                                        label="Description"
                                        fullWidth
                                        multiline
                                        rows={2}
                                        margin="normal"
                                        value={inputData[trip.vehicleId]?.description || ''}
                                        onChange={(e) => handleInputChange(trip.vehicleId, 'description', e.target.value)}
                                    />
                                </Box>
                            </Paper>
                        </Grid>
                    ))}
                </Grid>
            )}
        </Box>
    );
};

export default RoutingResult;
