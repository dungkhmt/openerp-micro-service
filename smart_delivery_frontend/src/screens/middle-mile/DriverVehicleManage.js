import React, { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, Grid, Button, Tabs, Tab,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField,
    FormControl, InputLabel, Select, MenuItem, Chip
} from '@mui/material';
import { request } from 'api';
import StandardTable from 'components/StandardTable';
import { errorNoti, successNoti } from 'utils/notification';
import AddIcon from '@mui/icons-material/Add';
import DirectionsBusIcon from '@mui/icons-material/DirectionsBus';
import RouteIcon from '@mui/icons-material/Route';
import ScheduleIcon from '@mui/icons-material/Schedule';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PersonIcon from '@mui/icons-material/Person';
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import {useHistory} from "react-router-dom";

const DriverVehicleManage = () => {
    const [tabValue, setTabValue] = useState(0);
    const [drivers, setDrivers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openAssignDialog, setOpenAssignDialog] = useState(false);
    const [openRouteDialog, setOpenRouteDialog] = useState(false);
    const [hubs, setHubs] = useState([]);
    const [hubId, setHubId] = useState(null);
    const history = useHistory();
    // Form state for new route vehicle assignment
    const [assignmentForm, setAssignmentForm] = useState({
        routeId: '',
        vehicleId: '',
        direction: 'OUTBOUND'
    });

    // Form state for new route
    const [routeForm, setRouteForm] = useState({
        routeCode: '',
        routeName: '',
        description: '',
        notes: '',
        stops: [{ hubId: '', stopSequence: 1, estimatedWaitTime: 15 }]
    });

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);

                // Fetch hubs
                await request('get', '/smdeli/humanresource/driver', (res) => {
                    setDrivers(res.data);
                });

                // Fetch routes
                await request('get', '/smdeli/vehicle/getAll', (res) => {
                    setVehicles(res.data);
                });

                // Fetch vehicles
                await request('get', '/smdeli/vehicle/getAll', (res) => {
                    setVehicles(res.data);
                });

                // Fetch route vehicles
                await request('get', '/smdeli/driver-vehicle/assignments', (res) => {
                    setAssignments(res.data);
                });

                setLoading(false);
            } catch (error) {
                errorNoti('Error loading data');
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    const handleView = (route) => {
        history.push(`/middle-mile/routes`);
    };

    const handleEdit = (route) => {
        history.push(`/middle-mile/routes/edit/}`);
    };

    const handleDelete = (route) => {
        if (window.confirm(`Bạn có chắc chắn muốn xóa tuyến đường ?`)) {
            request(
                "delete",
                `/smdeli/middle-mile/routes/}`,
                () => {
                    successNoti("Xóa tuyến đường thành công");
                    // loadRoutes();
                },
                {
                    401: () => errorNoti("Không có quyền thực hiện hành động này"),
                    500: () => errorNoti("Có lỗi xảy ra khi xóa tuyến đường")
                }
            );
        }
    };
    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    const handleOpenAssignDialog = () => {
        setOpenAssignDialog(true);
    };

    const handleCloseAssignDialog = () => {
        setOpenAssignDialog(false);
        setAssignmentForm({
            routeId: '',
            vehicleId: '',
            direction: 'OUTBOUND'
        });
    };

    const handleOpenRouteDialog = () => {
        setOpenRouteDialog(true);
    };

    const handleCloseRouteDialog = () => {
        setOpenRouteDialog(false);
        setRouteForm({
            routeCode: '',
            routeName: '',
            description: '',
            notes: '',
            stops: [{ hubId: '', stopSequence: 1, estimatedWaitTime: 15 }]
        });
    };

    const handleAssignmentFormChange = (e) => {
        const { name, value } = e.target;
        setAssignmentForm({
            ...assignmentForm,
            [name]: value
        });
    };

    const handleRouteFormChange = (e) => {
        const { name, value } = e.target;
        setRouteForm({
            ...routeForm,
            [name]: value
        });
    };

    const handleStopChange = (index, field, value) => {
        const newStops = [...routeForm.stops];
        newStops[index] = {
            ...newStops[index],
            [field]: value
        };
        setRouteForm({
            ...routeForm,
            stops: newStops
        });
    };

    const handleViewVehicle = (vehicle) => {
        history.push(`/middle-mile/vehicles/${vehicle.vehicleId}`);
    }

    const addStop = () => {
        setRouteForm({
            ...routeForm,
            stops: [
                ...routeForm.stops,
                {
                    hubId: '',
                    stopSequence: routeForm.stops.length + 1,
                    estimatedWaitTime: 15
                }
            ]
        });
    };

    const removeStop = (index) => {
        const newStops = [...routeForm.stops];
        newStops.splice(index, 1);

        // Update sequences
        const updatedStops = newStops.map((stop, idx) => ({
            ...stop,
            stopSequence: idx + 1
        }));

        setRouteForm({
            ...routeForm,
            stops: updatedStops
        });
    };

    const handleSubmitAssignment = () => {
        request(
            'post',
            '/smdeli/middle-mile/vehicle-assignments',
            (res) => {
                successNoti('Vehicle assigned to route successfully!');
                setAssignments([...assignments, res.data]);
                handleCloseAssignDialog();
            },
            {
                400: () => errorNoti('Invalid data. Please check your inputs.'),
                500: () => errorNoti('Server error occurred.')
            },
            assignmentForm
        );
    };

    const handleSubmitRoute = () => {

    };

    const handleDeleteRouteVehicle = (id) => {
        if (window.confirm('Are you sure you want to unassign this vehicle?')) {
            request(
                'delete',
                `/smdeli/middle-mile/vehicle-assignments/${id}`,
                () => {
                    successNoti('Vehicle unassigned successfully!');
                    setAssignments(assignments.filter(rv => rv.id !== id));
                },
                {
                    400: () => errorNoti('Error unassigning vehicle.'),
                    500: () => errorNoti('Server error occurred.')
                }
            );
        }
    };
    const vehiclesColumn = [
        {
            title: "Loại xe",
            field: "vehicleType",
        },
        {
            title: "Biển số xe",
            field: "plateNumber",
        },
        {
            title: "Tài xế",
            field: "driverName"
        },
        {
            title: "Tình trạng",
            field: "status",
        },
        {
            title: "Nhà sản xuất",
            field: "manufacturer"
        },
        {
            title: "Mẫu xe",
            field: "model",
        },
        {
            title: "Thao tác",
            field: "actions",
            sorting: false,
            renderCell: (rowData) => (
                <div style={{ alignItems:'center',display: 'flex', gap: '5px' }}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleViewVehicle(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                </div>
            ),
        },

    ]
    const driverColumns = [
        {
            title: "Mã nhân viên",
            field: "id",
        },
        {
            title: "Tên nhân viên",
            field: "name",
        },
        {
            title: "Số điện thoại",
            field: "phone",
        },
        {
            title: "Email",
            field: "email",
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div style={{ display: 'flex', gap: '5px' }}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleView(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEdit(rowData)}
                        color="secondary"
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleDelete(rowData)}
                        color="error"
                    >
                        <DeleteIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    const vehicleAssignmentColumns = [
        {
            title: "Loại xe",
            field: "vehicleType",

        },
        {
            title: "Biển số xe",
            field: "plateNumber",

        },
        {
            title: "Nhà sản xuất",
            field: "manufacturer",
        },
        {
            title: "Tài xế",
            field: "driverName",
        },
        {
            title: "Mã tài xế",
            field: "driverCode",
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: false,
            sorting: false,
            renderCell: (rowData) => (
                <div >
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleView(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEdit(rowData)}
                        color="secondary"
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleDelete(rowData)}
                        color="error"
                    >
                        <DeleteIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Route Management
            </Typography>

            {/*<Grid container spacing={3} sx={{ mb: 3 }}>*/}
            {/*    <Grid item xs={12} md={6} lg={3}>*/}
            {/*        <Card>*/}
            {/*            <CardContent>*/}
            {/*                <Box display="flex" alignItems="center">*/}
            {/*                    <RouteIcon fontSize="large" color="primary" sx={{ mr: 2 }} />*/}
            {/*                    <Box>*/}
            {/*                        <Typography variant="body2" color="textSecondary">*/}
            {/*                            Total Driver*/}
            {/*                        </Typography>*/}
            {/*                        <Typography variant="h5">*/}
            {/*                            {drivers.length}*/}
            {/*                        </Typography>*/}
            {/*                    </Box>*/}
            {/*                </Box>*/}
            {/*            </CardContent>*/}
            {/*        </Card>*/}
            {/*    </Grid>*/}

            {/*    <Grid item xs={12} md={6} lg={3}>*/}
            {/*        <Card>*/}
            {/*            <CardContent>*/}
            {/*                <Box display="flex" alignItems="center">*/}
            {/*                    <LocalShippingIcon fontSize="large" color="primary" sx={{ mr: 2 }} />*/}
            {/*                    <Box>*/}
            {/*                        <Typography variant="body2" color="textSecondary">*/}
            {/*                            Total Vehicles*/}
            {/*                        </Typography>*/}
            {/*                        <Typography variant="h5">*/}
            {/*                            {vehicles.length}*/}
            {/*                        </Typography>*/}
            {/*                    </Box>*/}
            {/*                </Box>*/}
            {/*            </CardContent>*/}
            {/*        </Card>*/}
            {/*    </Grid>*/}

            {/*    <Grid item xs={12} md={6} lg={3}>*/}
            {/*        <Card>*/}
            {/*            <CardContent>*/}
            {/*                <Box display="flex" alignItems="center">*/}
            {/*                    <DirectionsBusIcon fontSize="large" color="primary" sx={{ mr: 2 }} />*/}
            {/*                    <Box>*/}
            {/*                        <Typography variant="body2" color="textSecondary">*/}
            {/*                            Active Assignments*/}
            {/*                        </Typography>*/}
            {/*                        <Typography variant="h5">*/}
            {/*                            {assignments.length}*/}
            {/*                        </Typography>*/}
            {/*                    </Box>*/}
            {/*                </Box>*/}
            {/*            </CardContent>*/}
            {/*        </Card>*/}
            {/*    </Grid>*/}

            {/*    <Grid item xs={12} md={6} lg={3}>*/}
            {/*        <Card>*/}
            {/*            <CardContent>*/}
            {/*                <Box display="flex" alignItems="center">*/}
            {/*                    <PersonIcon fontSize="large" color="primary" sx={{ mr: 2 }} />*/}
            {/*                    <Box>*/}
            {/*                        <Typography variant="body2" color="textSecondary">*/}
            {/*                            Drivers*/}
            {/*                        </Typography>*/}
            {/*                        <Typography variant="h5">*/}
            {/*                            {drivers.length}*/}
            {/*                        </Typography>*/}
            {/*                    </Box>*/}
            {/*                </Box>*/}
            {/*            </CardContent>*/}
            {/*        </Card>*/}
            {/*    </Grid>*/}
            {/*</Grid>*/}

            <Box sx={{ mb: 3 }}>
                <Tabs value={tabValue} onChange={handleTabChange}>
                    <Tab label="Tài xế" icon={<RouteIcon />} iconPosition="start" />
                    <Tab label="Phương tiện" icon={<LocalShippingIcon />} iconPosition="start" />
                    <Tab label="Danh sách gán" icon={<DirectionsBusIcon />} iconPosition="start" />
                </Tabs>
            </Box>

            {tabValue === 0 && (
                <Box>
                    <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
                        <Button
                            variant="contained"
                            startIcon={<AddIcon />}
                            onClick={handleOpenRouteDialog}
                        >
                            Create New Route
                        </Button>
                    </Box>

                    <StandardTable
                        title="Routes"
                        columns={driverColumns}
                        data={drivers}
                        options={{
                            selection: false,
                            search: true,
                            sorting: true,
                            pageSize: 10
                        }}
                    />
                </Box>
            )}

            {tabValue === 1 && (
                <Box>
                    <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>

                    </Box>

                    <StandardTable
                        title="Vehicles"
                        columns={vehiclesColumn}
                        data={vehicles}
                        options={{
                            selection: false,
                            search: true,
                            sorting: true,
                            pageSize: 10
                        }}
                    />
                </Box>
            )}
            {tabValue === 2 && (
                <Box>
                    <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
                        <Button
                            variant="contained"
                            startIcon={<AddIcon />}
                            onClick={handleOpenAssignDialog}
                        >
                            Assign Vehicle to Route
                        </Button>
                    </Box>

                    <StandardTable
                        title="Vehicle Assignments"
                        columns={vehicleAssignmentColumns}
                        data={assignments}
                        options={{
                            selection: false,
                            search: true,
                            sorting: true,
                            pageSize: 10
                        }}
                    />
                </Box>
            )}
            {/* Vehicle Assignment Dialog */}
            <Dialog open={openAssignDialog} onClose={handleCloseAssignDialog} maxWidth="sm" fullWidth>
                <DialogTitle>Assign Vehicle to Route</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 2 }}>
                        <FormControl fullWidth sx={{ mb: 2 }}>
                            <InputLabel>Route</InputLabel>
                            <Select
                                name="routeId"
                                value={assignmentForm.routeId}
                                onChange={handleAssignmentFormChange}
                                label="Route"
                            >
                                {assignments.map((route) => (
                                    <MenuItem key={route.routeId} value={route.routeId}>
                                        {route.routeCode} - {route.routeName}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl fullWidth sx={{ mb: 2 }}>
                            <InputLabel>Vehicle</InputLabel>
                            <Select
                                name="vehicleId"
                                value={assignmentForm.vehicleId}
                                onChange={handleAssignmentFormChange}
                                label="Vehicle"
                            >
                                {vehicles.map((vehicle) => (
                                    <MenuItem key={vehicle.vehicleId} value={vehicle.vehicleId}>
                                        {vehicle.plateNumber} ({vehicle.vehicleType}) - {vehicle.status}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl fullWidth>
                            <InputLabel>Direction</InputLabel>
                            <Select
                                name="direction"
                                value={assignmentForm.direction}
                                onChange={handleAssignmentFormChange}
                                label="Direction"
                            >
                                <MenuItem value="OUTBOUND">Outbound</MenuItem>
                                <MenuItem value="INBOUND">Inbound</MenuItem>
                                <MenuItem value="LOOP">Loop</MenuItem>
                            </Select>
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseAssignDialog}>Cancel</Button>
                    <Button
                        onClick={handleSubmitAssignment}
                        variant="contained"
                        disabled={!assignmentForm.routeId || !assignmentForm.vehicleId}
                    >
                        Assign
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Create Route Dialog */}
            <Dialog open={openRouteDialog} onClose={handleCloseRouteDialog} maxWidth="md" fullWidth>
                <DialogTitle>Create New Route</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 2 }}>
                        <Grid container spacing={2}>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Route Code"
                                    name="routeCode"
                                    value={routeForm.routeCode}
                                    onChange={handleRouteFormChange}
                                    fullWidth
                                    required
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Route Name"
                                    name="routeName"
                                    value={routeForm.routeName}
                                    onChange={handleRouteFormChange}
                                    fullWidth
                                    required
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    label="Description"
                                    name="description"
                                    value={routeForm.description}
                                    onChange={handleRouteFormChange}
                                    fullWidth
                                    multiline
                                    rows={2}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    label="Notes"
                                    name="notes"
                                    value={routeForm.notes}
                                    onChange={handleRouteFormChange}
                                    fullWidth
                                    multiline
                                    rows={2}
                                />
                            </Grid>
                        </Grid>

                        <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
                            Route Stops
                        </Typography>

                        {routeForm.stops.map((stop, index) => (
                            <Grid container spacing={2} key={index} sx={{ mb: 2 }}>
                                <Grid item xs={12} md={6}>
                                    <FormControl fullWidth>
                                        <InputLabel>Hub</InputLabel>
                                        <Select
                                            value={stop.hubId}
                                            onChange={(e) => handleStopChange(index, 'hubId', e.target.value)}
                                            label="Hub"
                                            required
                                        >
                                            {hubs.map((hub) => (
                                                <MenuItem key={hub.id} value={hub.id}>
                                                    {hub.name} - {hub.address}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={6} md={3}>
                                    <TextField
                                        label="Sequence"
                                        type="number"
                                        value={stop.stopSequence}
                                        onChange={(e) => handleStopChange(index, 'stopSequence', parseInt(e.target.value))}
                                        fullWidth
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={6} md={2}>
                                    <TextField
                                        label="Wait Time (min)"
                                        type="number"
                                        value={stop.estimatedWaitTime}
                                        onChange={(e) => handleStopChange(index, 'estimatedWaitTime', parseInt(e.target.value))}
                                        fullWidth
                                    />
                                </Grid>
                                <Grid item xs={12} md={1} sx={{ display: 'flex', alignItems: 'center' }}>
                                    {routeForm.stops.length > 1 && (
                                        <Button
                                            color="error"
                                            onClick={() => removeStop(index)}
                                            size="small"
                                        >
                                            Remove
                                        </Button>
                                    )}
                                </Grid>
                            </Grid>
                        ))}

                        <Button
                            variant="outlined"
                            onClick={addStop}
                            sx={{ mt: 1 }}
                            startIcon={<AddIcon />}
                        >
                            Add Stop
                        </Button>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseRouteDialog}>Cancel</Button>
                    <Button
                        onClick={handleSubmitRoute}
                        variant="contained"
                        disabled={!routeForm.routeCode || !routeForm.routeName || routeForm.stops.some(stop => !stop.hubId)}
                    >
                        Create Route
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default DriverVehicleManage;