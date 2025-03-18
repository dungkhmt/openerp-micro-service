import React, { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, Grid, Button,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField,
    FormControl, InputLabel, Select, MenuItem, Chip, CircularProgress,
    Alert
} from '@mui/material';
import { request } from 'api';
import StandardTable from 'components/StandardTable';
import { errorNoti, successNoti } from 'utils/notification';
import AddIcon from '@mui/icons-material/Add';
import PersonIcon from '@mui/icons-material/Person';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useHistory } from "react-router-dom";
import LoadingScreen from "components/common/loading/loading";

const DriverManagement = () => {
    const [drivers, setDrivers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [openDriverDialog, setOpenDriverDialog] = useState(false);
    const [driverForm, setDriverForm] = useState({
        name: '',
        phone: '',
        email: '',
        address: '',
        username: '',
        password: '',
        role: 'DRIVER',
        hub: ''
    });
    const [hubs, setHubs] = useState([]);
    const [filteredDrivers, setFilteredDrivers] = useState([]);
    const [filterStatus, setFilterStatus] = useState('ALL');
    const [searchTerm, setSearchTerm] = useState('');
    const history = useHistory();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Fetch hubs for dropdown
            await request('get', '/smdeli/hubmanager/hub', (res) => {
                setHubs(res.data || []);
            });

            // Fetch all drivers
            await request('get', '/smdeli/humanresource/driver', (res) => {
                setDrivers(res.data || []);
                setFilteredDrivers(res.data || []);
            });

            setLoading(false);
        } catch (error) {
            console.error("Error loading data:", error);
            setError("Failed to load driver data. Please try again later.");
            setLoading(false);
        }
    };

    useEffect(() => {
        // Apply filters whenever filterStatus or searchTerm changes
        applyFilters();
    }, [filterStatus, searchTerm, drivers]);

    const applyFilters = () => {
        let result = [...drivers];

        // Apply search term filter
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            result = result.filter(driver =>
                driver.name?.toLowerCase().includes(term) ||
                driver.phone?.toLowerCase().includes(term) ||
                driver.email?.toLowerCase().includes(term)
            );
        }

        // Apply status filter if not ALL
        if (filterStatus !== 'ALL') {
            if (filterStatus === 'AVAILABLE') {
                // Logic for available drivers (those without vehicle assignments)
                // This would need actual assignment data to work properly
                // For now, we'll use a placeholder implementation
                result = result.filter(driver => !driver.vehicleId);
            } else if (filterStatus === 'ASSIGNED') {
                // Logic for assigned drivers
                result = result.filter(driver => driver.vehicleId);
            }
        }

        setFilteredDrivers(result);
    };

    const handleOpenDriverDialog = () => {
        setDriverForm({
            name: '',
            phone: '',
            email: '',
            address: '',
            username: '',
            password: '',
            role: 'DRIVER',
            hub: hubs.length > 0 ? hubs[0].id : ''
        });
        setOpenDriverDialog(true);
    };

    const handleCloseDriverDialog = () => {
        setOpenDriverDialog(false);
    };

    const handleDriverFormChange = (e) => {
        const { name, value } = e.target;
        setDriverForm({
            ...driverForm,
            [name]: value
        });
    };

    const handleCreateDriver = () => {
        // Validate form
        if (!driverForm.name || !driverForm.phone || !driverForm.username || !driverForm.password) {
            errorNoti('Please fill in all required fields');
            return;
        }

        request(
            'post',
            '/smdeli/humanresource/add',
            (res) => {
                successNoti('Driver created successfully!');
                fetchData();
                handleCloseDriverDialog();
            },
            {
                400: (err) => errorNoti('Invalid data. Please check your inputs.'),
                500: () => errorNoti('Server error occurred.')
            },
            driverForm
        );
    };

    const handleViewDriver = (driver) => {
        history.push(`/employee/driver/update/${driver.id}`);
    };

    const handleEditDriver = (driver) => {
        history.push(`/employee/driver/update/${driver.id}`);
    };

    const handleDeleteDriver = (driver) => {
        if (window.confirm(`Are you sure you want to delete driver ${driver.name}?`)) {
            request(
                'delete',
                `/smdeli/humanresource/driver/${driver.id}`,
                () => {
                    successNoti('Driver deleted successfully!');
                    fetchData();
                },
                {
                    400: () => errorNoti('Error deleting driver.'),
                    404: () => errorNoti('Driver not found.'),
                    500: () => errorNoti('Server error occurred.')
                }
            );
        }
    };

    const driverColumns = [
        {
            title: "ID",
            field: "id",
        },
        {
            title: "Name",
            field: "name",
        },
        {
            title: "Phone",
            field: "phone",
        },
        {
            title: "Email",
            field: "email",
        },
        {
            title: "Hub",
            field: "hubName",
            render: (rowData) => rowData.hubName || 'Not assigned'
        },
        {
            title: "Status",
            field: "status",
            render: (rowData) => {
                const hasVehicle = rowData.vehicleId != null;
                return (
                    <Chip
                        label={hasVehicle ? 'Assigned' : 'Available'}
                        color={hasVehicle ? 'primary' : 'success'}
                        size="small"
                    />
                );
            }
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
                        onClick={() => handleViewDriver(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEditDriver(rowData)}
                        color="secondary"
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleDeleteDriver(rowData)}
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
                Driver Management
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <PersonIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Total Drivers
                                    </Typography>
                                    <Typography variant="h5">
                                        {drivers.length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <DirectionsCarIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Assigned Drivers
                                    </Typography>
                                    <Typography variant="h5">
                                        {drivers.filter(d => d.vehicleId).length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <PersonIcon fontSize="large" color="success" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Available Drivers
                                    </Typography>
                                    <Typography variant="h5">
                                        {drivers.filter(d => !d.vehicleId).length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between' }}>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                    <FormControl sx={{ minWidth: 150 }}>
                        <InputLabel>Status</InputLabel>
                        <Select
                            value={filterStatus}
                            onChange={(e) => setFilterStatus(e.target.value)}
                            label="Status"
                            size="small"
                        >
                            <MenuItem value="ALL">All Drivers</MenuItem>
                            <MenuItem value="AVAILABLE">Available</MenuItem>
                            <MenuItem value="ASSIGNED">Assigned</MenuItem>
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
                    onClick={handleOpenDriverDialog}
                >
                    Add New Driver
                </Button>
            </Box>

            <StandardTable
                title="Drivers"
                columns={driverColumns}
                data={filteredDrivers}
                options={{
                    selection: false,
                    search: false, // We handle search ourselves
                    sorting: true,
                    pageSize: 10
                }}
                rowKey="id"
            />

            {/* Driver Creation Dialog */}
            <Dialog open={openDriverDialog} onClose={handleCloseDriverDialog} maxWidth="md" fullWidth>
                <DialogTitle>Add New Driver</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 2 }}>
                        <Grid container spacing={2}>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Full Name"
                                    name="name"
                                    value={driverForm.name}
                                    onChange={handleDriverFormChange}
                                    fullWidth
                                    required
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Phone Number"
                                    name="phone"
                                    value={driverForm.phone}
                                    onChange={handleDriverFormChange}
                                    fullWidth
                                    required
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Email"
                                    name="email"
                                    value={driverForm.email}
                                    onChange={handleDriverFormChange}
                                    fullWidth
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <FormControl fullWidth margin="dense">
                                    <InputLabel>Hub</InputLabel>
                                    <Select
                                        name="hub"
                                        value={driverForm.hub}
                                        onChange={handleDriverFormChange}
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
                            <Grid item xs={12}>
                                <TextField
                                    label="Address"
                                    name="address"
                                    value={driverForm.address}
                                    onChange={handleDriverFormChange}
                                    fullWidth
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Username"
                                    name="username"
                                    value={driverForm.username}
                                    onChange={handleDriverFormChange}
                                    fullWidth
                                    required
                                    margin="dense"
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Password"
                                    name="password"
                                    type="password"
                                    value={driverForm.password}
                                    onChange={handleDriverFormChange}
                                    fullWidth
                                    required
                                    margin="dense"
                                />
                            </Grid>
                        </Grid>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDriverDialog}>Cancel</Button>
                    <Button
                        onClick={handleCreateDriver}
                        variant="contained"
                    >
                        Create Driver
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default DriverManagement;