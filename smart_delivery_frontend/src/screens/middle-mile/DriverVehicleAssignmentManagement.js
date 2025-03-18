import React, { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, Grid, Button,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField,
    FormControl, InputLabel, Select, MenuItem, Chip, CircularProgress,
    Alert, Paper, Divider
} from '@mui/material';
import { request } from 'api';
import StandardTable from 'components/StandardTable';
import { errorNoti, successNoti } from 'utils/notification';
import AddIcon from '@mui/icons-material/Add';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PersonIcon from '@mui/icons-material/Person';
import AssignmentIcon from '@mui/icons-material/Assignment';
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import DeleteIcon from "@mui/icons-material/Delete";
import { useHistory } from "react-router-dom";
import LoadingScreen from "components/common/loading/loading";

const DriverAssignmentManagement = () => {
    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [openAssignDialog, setOpenAssignDialog] = useState(false);
    const [unassignedVehicles, setUnassignedVehicles] = useState([]);
    const [unassignedDrivers, setUnassignedDrivers] = useState([]);
    const [drivers, setDrivers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [assignmentForm, setAssignmentForm] = useState({
        driverId: '',
        vehicleId: ''
    });
    const [searchTerm, setSearchTerm] = useState('');
    const [filteredAssignments, setFilteredAssignments] = useState([]);
    const history = useHistory();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Fetch all drivers
            await request('get', '/smdeli/humanresource/driver', (res) => {
                setDrivers(res.data || []);
            });

            // Fetch all vehicles
            await request('get', '/smdeli/vehicle/getAll', (res) => {
                setVehicles(res.data || []);
            });

            // Fetch all driver-vehicle assignments
            await request('get', '/smdeli/driver-vehicle/assignments', (res) => {
                setAssignments(res.data || []);
                setFilteredAssignments(res.data || []);
            });

            // Fetch unassigned vehicles
            await request('get', '/smdeli/driver-vehicle/vehicles/unassigned', (res) => {
                setUnassignedVehicles(res.data || []);
            });

            setLoading(false);
        } catch (error) {
            console.error("Error loading data:", error);
            setError("Failed to load assignment data. Please try again later.");
            setLoading(false);
        }
    };

    // Compute unassigned drivers based on assignments and all drivers
    useEffect(() => {
        if (drivers.length > 0 && assignments.length >= 0) {
            const assignedDriverIds = assignments.map(a => a.driverId);
            const unassigned = drivers.filter(driver => !assignedDriverIds.includes(driver.id));
            setUnassignedDrivers(unassigned);
        }
    }, [drivers, assignments]);

    // Apply search filter
    useEffect(() => {
        if (!searchTerm) {
            setFilteredAssignments(assignments);
        } else {
            const term = searchTerm.toLowerCase();
            const filtered = assignments.filter(assignment =>
                assignment.plateNumber?.toLowerCase().includes(term) ||
                assignment.driverName?.toLowerCase().includes(term) ||
                assignment.driverCode?.toLowerCase().includes(term)
            );
            setFilteredAssignments(filtered);
        }
    }, [searchTerm, assignments]);

    const handleOpenAssignDialog = () => {
        setAssignmentForm({
            driverId: '',
            vehicleId: ''
        });
        setOpenAssignDialog(true);
    };

    const handleCloseAssignDialog = () => {
        setOpenAssignDialog(false);
    };

    const handleAssignmentFormChange = (e) => {
        const { name, value } = e.target;
        setAssignmentForm({
            ...assignmentForm,
            [name]: value
        });
    };

    const handleSubmitAssignment = () => {
        if (!assignmentForm.driverId || !assignmentForm.vehicleId) {
            errorNoti("Please select both a driver and a vehicle");
            return;
        }

        const requestData = {
            driverId: assignmentForm.driverId,
            vehicleId: assignmentForm.vehicleId
        };

        request(
            'post',
            '/smdeli/driver-vehicle/assign',
            (res) => {
                successNoti('Driver assigned to vehicle successfully!');
                fetchData();
                handleCloseAssignDialog();
            },
            {
                400: () => errorNoti('Invalid data. Please check your inputs.'),
                500: () => errorNoti('Server error occurred.')
            },
            requestData
        );
    };

    const handleUnassign = (assignment) => {
        if (window.confirm(`Are you sure you want to unassign driver ${assignment.driverName} from vehicle ${assignment.plateNumber}?`)) {
            request(
                'post',
                `/smdeli/driver-vehicle/unassign/${assignment.vehicleId}`,
                () => {
                    successNoti('Driver unassigned from vehicle successfully!');
                    fetchData();
                },
                {
                    400: () => errorNoti('Error unassigning driver.'),
                    500: () => errorNoti('Server error occurred.')
                }
            );
        }
    };

    const handleViewVehicle = (vehicleId) => {
        history.push(`/middle-mile/vehicles/${vehicleId}`);
    };

    const handleViewDriver = (driverId) => {
        history.push(`/employee/driver/update/${driverId}`);
    };

    const assignmentColumns = [
        {
            title: "Vehicle Type",
            field: "vehicleType",
        },
        {
            title: "Plate Number",
            field: "plateNumber",
            renderCell: (rowData) => (
                <Box
                    sx={{
                        cursor: 'pointer',
                        color: 'primary.main',
                        '&:hover': { textDecoration: 'underline' }
                    }}
                    onClick={() => handleViewVehicle(rowData.vehicleId)}
                >
                    {rowData.plateNumber}
                </Box>
            )
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
            renderCell: (rowData) => (
                <Box
                    sx={{
                        cursor: 'pointer',
                        color: 'primary.main',
                        '&:hover': { textDecoration: 'underline' }
                    }}
                    onClick={() => handleViewDriver(rowData.driverId)}
                >
                    {rowData.driverName}
                </Box>
            )
        },
        {
            title: "Driver Code",
            field: "driverCode",
        },
        {
            title: "Status",
            field: "status",
            render: (rowData) => (
                <Chip
                    label={rowData.status || 'Active'}
                    color={rowData.status === 'UNAVAILABLE' ? 'error' : 'success'}
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
                        onClick={() => handleUnassign(rowData)}
                        color="error"
                        title="Unassign Driver"
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
                Driver-Vehicle Assignments
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
                                <AssignmentIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Total Assignments
                                    </Typography>
                                    <Typography variant="h5">
                                        {assignments.length}
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
                                <PersonIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Available Drivers
                                    </Typography>
                                    <Typography variant="h5">
                                        {unassignedDrivers.length}
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
                                <LocalShippingIcon fontSize="large" color="primary" sx={{ mr: 2 }} />
                                <Box>
                                    <Typography variant="body2" color="textSecondary">
                                        Available Vehicles
                                    </Typography>
                                    <Typography variant="h5">
                                        {unassignedVehicles.length}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between' }}>
                <TextField
                    label="Search"
                    variant="outlined"
                    size="small"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    sx={{ width: '300px' }}
                />
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleOpenAssignDialog}
                    disabled={unassignedDrivers.length === 0 || unassignedVehicles.length === 0}
                >
                    Create New Assignment
                </Button>
            </Box>

            <StandardTable
                title="Driver-Vehicle Assignments"
                columns={assignmentColumns}
                data={filteredAssignments}
                options={{
                    selection: false,
                    search: false, // We handle search ourselves
                    sorting: true,
                    pageSize: 10
                }}
                rowKey="vehicleId"
            />

            {/* Assignment Creation Dialog */}
            <Dialog open={openAssignDialog} onClose={handleCloseAssignDialog} maxWidth="md" fullWidth>
                <DialogTitle>Assign Driver to Vehicle</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 2 }}>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <FormControl fullWidth margin="dense" required>
                                    <InputLabel>Driver</InputLabel>
                                    <Select
                                        name="driverId"
                                        value={assignmentForm.driverId}
                                        onChange={handleAssignmentFormChange}
                                        label="Driver"
                                    >
                                        {unassignedDrivers.map((driver) => (
                                            <MenuItem key={driver.id} value={driver.id}>
                                                {driver.name} - {driver.phone}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>

                            <Grid item xs={12}>
                                <FormControl fullWidth margin="dense" required>
                                    <InputLabel>Vehicle</InputLabel>
                                    <Select
                                        name="vehicleId"
                                        value={assignmentForm.vehicleId}
                                        onChange={handleAssignmentFormChange}
                                        label="Vehicle"
                                    >
                                        {unassignedVehicles.map((vehicle) => (
                                            <MenuItem key={vehicle.vehicleId} value={vehicle.vehicleId}>
                                                {vehicle.plateNumber} - {vehicle.manufacturer} {vehicle.model} ({vehicle.vehicleType})
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                        </Grid>

                        {(unassignedDrivers.length === 0 || unassignedVehicles.length === 0) && (
                            <Alert severity="warning" sx={{ mt: 2 }}>
                                {unassignedDrivers.length === 0 ? 'No available drivers to assign. ' : ''}
                                {unassignedVehicles.length === 0 ? 'No available vehicles to assign.' : ''}
                            </Alert>
                        )}
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseAssignDialog}>Cancel</Button>
                    <Button
                        onClick={handleSubmitAssignment}
                        variant="contained"
                        disabled={!assignmentForm.driverId || !assignmentForm.vehicleId}
                    >
                        Create Assignment
                    </Button>
                </DialogActions>
            </Dialog>

            {assignments.length === 0 && !loading && (
                <Paper sx={{ p: 3, mt: 2, textAlign: 'center' }}>
                    <Typography variant="h6" color="textSecondary" gutterBottom>
                        No assignments found
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                        Start by assigning a driver to a vehicle using the button above.
                    </Typography>
                </Paper>
            )}
        </Box>
    );
};

export default DriverAssignmentManagement;