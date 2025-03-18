import React, { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, Grid, Button,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField,
    FormControl, InputLabel, Select, MenuItem, Chip, CircularProgress,
    Alert, FormHelperText
} from '@mui/material';
import { request } from 'api';
import StandardTable from 'components/StandardTable';
import { errorNoti, successNoti } from 'utils/notification';
import AddIcon from '@mui/icons-material/Add';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import TwoWheelerIcon from '@mui/icons-material/TwoWheeler';
import AirportShuttleIcon from '@mui/icons-material/AirportShuttle';
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useHistory } from "react-router-dom";
import LoadingScreen from "components/common/loading/loading";

const VehicleManagement = () => {
    const [vehicles, setVehicles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [openVehicleDialog, setOpenVehicleDialog] = useState(false);
    const [vehicleForm, setVehicleForm] = useState({
        vehicleType: '',
        plateNumber: '',
        manufacturer: '',
        model: '',
        yearOfManufacture: '',
        weightCapacity: '',
        volumeCapacity: '',
        status: 'AVAILABLE'
    });
    const [filteredVehicles, setFilteredVehicles] = useState([]);
    const [filterStatus, setFilterStatus] = useState('ALL');
    const [filterType, setFilterType] = useState('ALL');
    const [searchTerm, setSearchTerm] = useState('');
    const [hubs, setHubs] = useState([]);
    const [selectedHub, setSelectedHub] = useState('');
    const history = useHistory();

    const vehicleTypes = [
        { value: 'CAR', label: 'Car' },
        { value: 'TRUCK', label: 'Truck' },
        { value: 'MOTORCYCLE', label: 'Motorcycle' },
        { value: 'VAN', label: 'Van' }
    ];

    const vehicleStatuses = [
        { value: 'AVAILABLE', label: 'Available' },
        { value: 'BUSY', label: 'Busy' },
        { value: 'MAINTENANCE', label: 'Maintenance' }
    ];

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Fetch hubs for filtering
            await request('get', '/smdeli/hubmanager/hub', (res) => {
                setHubs(res.data || []);
                if (res.data && res.data.length > 0) {
                    setSelectedHub(res.data[0].id);
                }
            });

            // Fetch all vehicles
            await request('get', '/smdeli/vehicle/getAll', (res) => {
                setVehicles(res.data || []);
                setFilteredVehicles(res.data || []);
            });

            setLoading(false);
        } catch (error) {
            console.error("Error loading data:", error);
            setError("Failed to load vehicle data. Please try again later.");
            setLoading(false);
        }
    };

    useEffect(() => {
        // Apply filters whenever filterStatus, filterType, or searchTerm changes
        applyFilters();
    }, [filterStatus, filterType, searchTerm, vehicles, selectedHub]);

    const applyFilters = () => {
        let result = [...vehicles];

        // Apply search term filter
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            result = result.filter(vehicle =>
                vehicle.plateNumber?.toLowerCase().includes(term) ||
                vehicle.manufacturer?.toLowerCase().includes(term) ||
                vehicle.model?.toLowerCase().includes(term)
            );
        }

        // Apply status filter if not ALL
        if (filterStatus !== 'ALL') {
            result = result.filter(vehicle => vehicle.status === filterStatus);
        }

        // Apply type filter if not ALL
        if (filterType !== 'ALL') {
            result = result.filter(vehicle => vehicle.vehicleType === filterType);
        }

        // Apply hub filter if selected
        if (selectedHub) {
            // This assumes vehicles have a hubId property
            // If the API structure is different, this will need adjustment
            result = result.filter(vehicle => vehicle.hubId === selectedHub);
        }

        setFilteredVehicles(result);
    };

    const handleOpenVehicleDialog = () => {
        setVehicleForm({
            vehicleType: 'CAR',
            plateNumber: '',
            manufacturer: '',
            model: '',
            yearOfManufacture: '',
            weightCapacity: '',
            volumeCapacity: '',
            status: 'AVAILABLE'
        });
        setOpenVehicleDialog(true);
    };

    const handleCloseVehicleDialog = () => {
        setOpenVehicleDialog(false);
    };

    const handleVehicleFormChange = (e) => {
        const { name, value } = e.target;
        setVehicleForm({
            ...vehicleForm,
            [name]: value
        });
    };

    const handleCreateVehicle = () => {
        // Validate form
        if (!vehicleForm.plateNumber || !vehicleForm.vehicleType) {
            errorNoti('Please fill in all required fields');
            return;
        }

        request(
            'post',
            '/smdeli/vehicle/vehicle',
            (res) => {
                successNoti('Vehicle created successfully!');
                fetchData();
                handleCloseVehicleDialog();
            },
            {
                400: (err) => errorNoti('Invalid data. Please check your inputs.'),
                500: () => errorNoti('Server error occurred.')
            },
            vehicleForm
        );
    };

    const handleViewVehicle = (vehicle) => {
        history.push(`/middle-mile/vehicles/${vehicle.vehicleId}`);
    };

    const handleEditVehicle = (vehicle) => {
        // This would typically navigate to an edit page
        // For now, let's show a notification
        errorNoti("Vehicle edit functionality will be implemented soon");
    };

    const handleDeleteVehicle = (vehicle) => {
        if (window.confirm(`Are you sure you want to delete vehicle ${vehicle.plateNumber}?`)) {
            request(
                'delete',
                `/smdeli/vehicle/vehicle/${vehicle.vehicleId}`,
                () => {
                    successNoti('Vehicle deleted successfully!');
                    fetchData();
                },
                {
                    400: () => errorNoti('Error deleting vehicle.'),
                    404: () => errorNoti('Vehicle not found.'),
                    500: () => errorNoti('Server error occurred.')
                }
            );
        }
    };

    const getVehicleIcon = (type) => {
        switch (type) {
            case 'CAR':
                return <DirectionsCarIcon />;
            case 'TRUCK':
                return <LocalShippingIcon />;
            case 'MOTORCYCLE':
                return <TwoWheelerIcon />;
            case 'VAN':
                return <AirportShuttleIcon />;
            default:
                return <LocalShippingIcon />;
        }
    };

    const vehicleColumns = [
        {
            title: "Type",
            field: "vehicleType",
            renderCell: (rowData) => (
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    {getVehicleIcon(rowData.vehicleType)}
                    <span style={{ marginLeft: '8px' }}>{rowData.vehicleType}</span>
                </Box>
            )
        },
        {
            title: "Plate Number",
            field: "plateNumber",
        },
        {
            title: "Manufacturer",
            field: "manufacturer",
        },
        {
            title: "Model",
            field: "model",
        },
        {
            title: "Driver",
            field: "driverName",
            render: (rowData) => rowData.driverName || 'Unassigned'
        },
        {
            title: "Status",
            field: "status",
            render: (rowData) => (
                <Chip
                    label={rowData.status}
                    color={rowData.status === 'AVAILABLE' ? 'success' :
                        rowData.status === 'BUSY' ? 'warning' :
                            rowData.status === 'MAINTENANCE' ? 'error' : 'default'}
                    size="small"
                />
            )
        },
        {
            title: "Actions",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div style={{ display: 'flex', gap: '5px' }}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleViewVehicle(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEditVehicle(rowData)}
                        color="secondary"
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleDeleteVehicle(rowData)}
                        color="error"
                    >
                        <DeleteIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    if (loading) {
        return <LoadingScreen />;
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Vehicle Management
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={3}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <LocalShippingIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Total Vehicles
                                    </Typography>
                                    <Typography variant="h5">
                                        {vehicles.length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={3}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <DirectionsCarIcon fontSize="large" color="success" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Available Vehicles
                                    </Typography>
                                    <Typography variant="h5">
                                        {vehicles.filter(v => v.status === 'AVAILABLE').length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={3}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <DirectionsCarIcon fontSize="large" color="warning" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Busy Vehicles
                                    </Typography>
                                    <Typography variant="h5">
                                        {vehicles.filter(v => v.status === 'BUSY').length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={3}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <DirectionsCarIcon fontSize="large" color="error" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Maintenance
                                    </Typography>
                                    <Typography variant="h5">
                                        {vehicles.filter(v => v.status === 'MAINTENANCE').length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between' }}>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                    <FormControl sx={{ minWidth: 120 }}>
                        <InputLabel>Hub</InputLabel>
                        <Select
                            value={selectedHub}
                            onChange={(e) => setSelectedHub(e.target.value)}
                            label="Hub"
                            size="small"
                        >
                            {hubs.map((hub) => (
                                <MenuItem key={hub.id} value={hub.id}>
                                    {hub.name}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <FormControl sx={{ minWidth: 120 }}>
                        <InputLabel>Status</InputLabel>
                        <Select
                            value={filterStatus}
                            onChange={(e) => setFilterStatus(e.target.value)}
                            label="Status"
                            size="small"
                        >
                            <MenuItem value="ALL">All</MenuItem>
                            <MenuItem value="AVAILABLE">Available</MenuItem>
                            <MenuItem value="BUSY">Busy</MenuItem>
                            <MenuItem value="MAINTENANCE">Maintenance</MenuItem>
                        </Select>
                    </FormControl>

                    <FormControl sx={{ minWidth: 120 }}>
                        <InputLabel>Type</InputLabel>
                        <Select
                            value={filterType}
                            onChange={(e) => setFilterType(e.target.value)}
                            label="Type"
                            size="small"
                        >
                            <MenuItem value="ALL">All Types</MenuItem>
                            {vehicleTypes.map((type) => (
                                <MenuItem key={type.value} value={type.value}>
                                    {type.label}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <TextField
                        label="Search"
                        variant="outlined"
                        size="small"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </Box>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleOpenVehicleDialog}
                >
                    Add New Vehicle
                </Button>
            </Box>

            <StandardTable
                title="Vehicles"
                columns={vehicleColumns}
                data={filteredVehicles}
                options={{
                    selection: false,
                    search: false, // We handle search ourselves
                    sorting: true,
                    pageSize: 10
                }}
                rowKey="vehicleId"
            />

            {/* Vehicle Creation Dialog */}
            <Dialog open={openVehicleDialog} onClose={handleCloseVehicleDialog} maxWidth="md" fullWidth>
                <DialogTitle>Add New Vehicle</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 2 }}>
                        <Grid container spacing={2}>
                            <Grid item xs={12} md={6}>
                                <FormControl fullWidth margin="dense" required>
                                    <InputLabel>Vehicle Type</InputLabel>
                                    <Select
                                        name="vehicleType"
                                        value={vehicleForm.vehicleType}
                                        onChange={handleVehicleFormChange}
                                        label="Vehicle Type"
                                    >
                                        {vehicleTypes.map((type) => (
                                            <MenuItem key={type.value} value={type.value}>
                                                {type.label}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Plate Number"
                                    name="plateNumber"
                                    value={vehicleForm.plateNumber}
                                    onChange={handleVehicleFormChange}
                                    fullWidth
                                    required
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Manufacturer"
                                    name="manufacturer"
                                    value={vehicleForm.manufacturer}
                                    onChange={handleVehicleFormChange}
                                    fullWidth
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Model"
                                    name="model"
                                    value={vehicleForm.model}
                                    onChange={handleVehicleFormChange}
                                    fullWidth
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={4}>
                                <TextField
                                    label="Year of Manufacture"
                                    name="yearOfManufacture"
                                    value={vehicleForm.yearOfManufacture}
                                    onChange={handleVehicleFormChange}
                                    fullWidth
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={4}>
                                <TextField
                                    label="Weight Capacity (kg)"
                                    name="weightCapacity"
                                    value={vehicleForm.weightCapacity}
                                    onChange={handleVehicleFormChange}
                                    fullWidth
                                    type="number"
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={4}>
                                <TextField
                                    label="Volume Capacity (m³)"
                                    name="volumeCapacity"
                                    value={vehicleForm.volumeCapacity}
                                    onChange={handleVehicleFormChange}
                                    fullWidth
                                    type="number"
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <FormControl fullWidth margin="dense">
                                    <InputLabel>Status</InputLabel>
                                    <Select
                                        name="status"
                                        value={vehicleForm.status}
                                        onChange={handleVehicleFormChange}
                                        label="Status"
                                    >
                                        {vehicleStatuses.map((status) => (
                                            <MenuItem key={status.value} value={status.value}>
                                                {status.label}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <FormControl fullWidth margin="dense">
                                    <InputLabel>Hub</InputLabel>
                                    <Select
                                        name="hubId"
                                        value={vehicleForm.hubId || ''}
                                        onChange={handleVehicleFormChange}
                                        label="Hub"
                                    >
                                        {hubs.map((hub) => (
                                            <MenuItem key={hub.id} value={hub.id}>
                                                {hub.name}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                        </Grid>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseVehicleDialog}>Cancel</Button>
                    <Button
                        onClick={handleCreateVehicle}
                        variant="contained"
                    >
                        Create Vehicle
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default VehicleManagement;