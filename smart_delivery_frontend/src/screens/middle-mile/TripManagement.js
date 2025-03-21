import React, { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, Grid, Button, Tabs, Tab,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField,
    FormControl, InputLabel, Select, MenuItem, Chip, FormGroup, FormControlLabel, Checkbox, Modal
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

const TripManagement = () => {
    const [tabValue, setTabValue] = useState(0);
    const [routes, setRoutes] = useState([]);
    const [selectedRoute, setSelectedRoute] = useState(null);
    const [vehicles, setVehicles] = useState([]);
    const [drivers, setDrivers] = useState([]);
    const [routeSchedules, setRouteSchedules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openModal, setOpenModal] = useState(false);
    const [openRouteDialog, setOpenRouteDialog] = useState(false);
    const [hubs, setHubs] = useState([]);
    const [hubId, setHubId] = useState(null);
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const history = useHistory();

    // Form state for new route schedule assignment
    const [assignmentForm, setAssignmentForm] = useState({
        routeId: '',
        vehicleId: '',
        scheduleType: 'WEEKLY',
        days: []
    });
    const [daysOfWeek, setDaysOfWeek] = useState([
        { value: 'MONDAY', label: 'Thứ Hai', selected: false },
        { value: 'TUESDAY', label: 'Thứ Ba', selected: false },
        { value: 'WEDNESDAY', label: 'Thứ Tư', selected: false },
        { value: 'THURSDAY', label: 'Thứ Năm', selected: false },
        { value: 'FRIDAY', label: 'Thứ Sáu', selected: false },
        { value: 'SATURDAY', label: 'Thứ Bảy', selected: false },
        { value: 'SUNDAY', label: 'Chủ Nhật', selected: false }
    ]);
    // Form state for new route
    const [routeForm, setRouteForm] = useState({
        routeCode: '',
        routeName: '',
        description: '',
        notes: '',
        stops: [{ hubId: '', stopSequence: 1, estimatedWaitTime: 15 }]
    });
    const handleDayChange = (day) => {
        setDaysOfWeek(daysOfWeek.map(d =>
            d.value === day.value ? { ...d, selected: !d.selected } : d
        ));
    };
    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);

                // Fetch hubs
                await request('get', '/smdeli/hubmanager/hub', (res) => {
                    setHubs(res.data);
                    if (res.data.length > 0) {
                        setHubId(res.data[0].id);
                    }
                });

                // Fetch routes
                await request('get', '/smdeli/middle-mile/routes', (res) => {
                    setRoutes(res.data);
                });

                // Fetch vehicles
                await request('get', '/smdeli/vehicle/getAll', (res) => {
                    setVehicles(res.data);
                });

                // Fetch route schedules
                await request('get', '/smdeli/route-scheduler/schedules', (res) => {
                    setRouteSchedules(res.data || []);
                });

                // Fetch drivers
                await request('get', '/smdeli/humanresource/driver', (res) => {
                    setDrivers(res.data);
                });

                setLoading(false);
            } catch (error) {
                errorNoti('Error loading data');
                setLoading(false);
            }
        };

        fetchData();
    }, [hubId]);

    const handleOpenModal = () => {
        setOpenModal(true);
    }

    const handleCreateSchedule = () => {
        const selectedDays = daysOfWeek.filter(d => d.selected).map(d => d.value);

        if(!selectedDays.length || !selectedRoute){
            errorNoti("Vui lòng chọn lịch trình tuyến đường và ít nhất một ngày trong tuần");
            return;
        }

        const timeSlots = [{
            start: startTime,
            end: endTime
        }];

        request(
            "post",
            `/smdeli/route-scheduler/schedule`,
            (res) => {
                successNoti("Tạo lịch trình thành công");
                loadRouteSchedules(); // Refresh data
                handleCloseModal();
            },
            {
                400: () => errorNoti("Dữ liệu không hợp lệ"),
                500: (e) => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau" + e.message)
            },
            {
                routeId: selectedRoute,
                days: selectedDays,
                timeSlots: timeSlots
            }
        );
    }

    const loadRouteSchedules = () => {
        request('get', '/smdeli/route-scheduler/schedules', (res) => {
            setRouteSchedules(res.data || []);
        });
    }

    const handleCloseModal = () => {
        setOpenModal(false);
        setSelectedRoute(null);
        setStartTime('');
        setEndTime('');
        setDaysOfWeek(daysOfWeek.map(d => ({ ...d, selected: false })));
    }

    const loadRoutes = () => {
        setLoading(true);
        request(
            "get",
            "/smdeli/middle-mile/routes",
            (res) => {
                setRoutes(res.data);
                setLoading(false);
            },
            {
                401: () => {
                    errorNoti("Không có quyền truy cập");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Có lỗi xảy ra khi tải dữ liệu");
                    setLoading(false);
                }
            }
        );
    };

    const handleView = (route) => {
        history.push(`/middle-mile/routes/${route.routeId}`);
    };

    const handleEdit = (route) => {
        history.push(`/middle-mile/routes/edit/${route.routeId}`);
    };

    const handleDelete = (route) => {
        if (window.confirm(`Bạn có chắc chắn muốn xóa tuyến đường ${route.routeName}?`)) {
            request(
                "delete",
                `/smdeli/middle-mile/routes/${route.routeId}`,
                () => {
                    successNoti("Xóa tuyến đường thành công");
                    loadRoutes();
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

    const handleDaysChange = (e) => {
        setAssignmentForm({
            ...assignmentForm,
            days: e.target.value
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

    const handleViewSchedule = (schedule) => {
        history.push(`/route-scheduler/schedule/${schedule.id}`);
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

    const handleSubmitRoute = () => {
        // Check if all stops have hubId
        if (routeForm.stops.some(stop => !stop.hubId)) {
            errorNoti('All stops must have a hub selected');
            return;
        }

        request(
            'post',
            '/smdeli/middle-mile/routes',
            (res) => {
                successNoti('Route created successfully!');
                setRoutes([...routes, res.data]);
                handleCloseRouteDialog();
            },
            {
                400: () => errorNoti('Invalid data. Please check your inputs.'),
                500: () => errorNoti('Server error occurred.')
            },
            routeForm
        );
    };

    const handleDeleteRouteSchedule = (id) => {
        if (window.confirm('Are you sure you want to delete this route schedule?')) {
            request(
                'delete',
                `/smdeli/route-scheduler/schedule/${id}`,
                () => {
                    successNoti('Route schedule deleted successfully!');
                    loadRouteSchedules();
                },
                {
                    400: () => errorNoti('Error deleting route schedule.'),
                    500: () => errorNoti('Server error occurred.')
                }
            );
        }
    };

    const vehiclesColumns = [
        { title: "Loại xe", field: "vehicleType" },
        { title: "Biển số xe", field: "plateNumber" },
        { title: "Tài xế", field: "driverName" },
        { title: "Tình trạng", field: "status" },
        { title: "Nhà sản xuất", field: "manufacturer" },
        { title: "Mẫu xe", field: "model" },
        {
            title: "Thao tác",
            field: "actions",
            sorting: false,
            renderCell: (rowData) => (
                <div style={{ alignItems:'center', display: 'flex', gap: '5px' }}>
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
    ];

    const routeColumns = [
        { title: "Route Code", field: "routeCode" },
        { title: "Route Name", field: "routeName" },
        { title: "Description", field: "description" },
        {
            title: "Status",
            field: "status",
            render: (rowData) => (
                <Chip
                    label={rowData.status}
                    color={rowData.status === "ACTIVE" ? "success" : "default"}
                />
            )
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

    const routeScheduleColumns = [
        { title: "Route Code", field: "routeCode" },
        { title: "Route Name", field: "routeName" },
        { title: "Day of Week", field: "dayOfWeek" },
        { title: "Start Time", field: "startTime" },
        { title: "End Time", field: "endTime" },
        {
            title: "Status",
            field: "isActive",
            render: (rowData) => (
                <Chip
                    label={rowData.isActive ? "Active" : "Inactive"}
                    color={rowData.isActive ? "success" : "default"}
                />
            )
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: false,
            sorting: false,
            renderCell: (rowData) => (
                <div>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleViewSchedule(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleDeleteRouteSchedule(rowData.id)}
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
                Transport Management
            </Typography>

            {/*<Grid container spacing={3} sx={{ mb: 3 }}>*/}
            {/*    <Grid item xs={12} md={6} lg={3}>*/}
            {/*        <Card>*/}
            {/*            <CardContent>*/}
            {/*                <Box display="flex" alignItems="center">*/}
            {/*                    <RouteIcon fontSize="large" color="primary" sx={{ mr: 2 }} />*/}
            {/*                    <Box>*/}
            {/*                        <Typography variant="body2" color="textSecondary">*/}
            {/*                            Total Routes*/}
            {/*                        </Typography>*/}
            {/*                        <Typography variant="h5">*/}
            {/*                            {routes.length}*/}
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
            {/*                    <ScheduleIcon fontSize="large" color="primary" sx={{ mr: 2 }} />*/}
            {/*                    <Box>*/}
            {/*                        <Typography variant="body2" color="textSecondary">*/}
            {/*                            Active Schedules*/}
            {/*                        </Typography>*/}
            {/*                        <Typography variant="h5">*/}
            {/*                            {routeSchedules.length}*/}
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
                    <Tab label="Routes" icon={<RouteIcon />} iconPosition="start" />
                    <Tab label="Route Schedules" icon={<ScheduleIcon />} iconPosition="start" />
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
                        columns={routeColumns}
                        data={routes}
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
                        <Button
                            variant="contained"
                            startIcon={<AddIcon />}
                            onClick={handleOpenModal}
                        >
                            Create New Route Schedule
                        </Button>
                    </Box>

                    <StandardTable
                        title="Route Schedules"
                        columns={routeScheduleColumns}
                        data={routeSchedules}
                        options={{
                            selection: false,
                            search: true,
                            sorting: true,
                            pageSize: 10
                        }}
                    />
                </Box>
            )}

            <Modal
                open={openModal}
                onClose={handleCloseModal}
                aria-labelledby="modal-title"
                aria-describedby="modal-description"
            >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: 600,
                    bgcolor: 'background.paper',
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2
                }}>
                    <Typography id="modal-title" variant="h6" component="h2" sx={{ mb: 3 }}>
                        {"Tạo lịch trình mới"}
                    </Typography>

                    <Box component="form" sx={{ mt: 3 }}>
                        <FormControl fullWidth sx={{ mb: 3 }}>
                            <InputLabel id="route-schedule-label">Route</InputLabel>
                            <Select
                                labelId="route-schedule-label"
                                value={selectedRoute || ''}
                                onChange={(e) => setSelectedRoute(e.target.value)}
                                label="Route"
                            >
                                {routes.map((route) => (
                                    <MenuItem key={route.routeId} value={route.routeId}>
                                        {route.routeCode} - {route.routeName}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <Typography variant="subtitle1" sx={{ mb: 1 }}>Chọn ngày trong tuần:</Typography>
                        <FormGroup row sx={{ mb: 3 }}>
                            {daysOfWeek.map((day) => (
                                <FormControlLabel
                                    key={day.value}
                                    control={
                                        <Checkbox
                                            checked={day.selected}
                                            onChange={() => handleDayChange(day)}
                                        />
                                    }
                                    label={day.label}
                                />
                            ))}
                        </FormGroup>

                        <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                            <TextField
                                label="Giờ bắt đầu"
                                type="time"
                                value={startTime}
                                onChange={(e) => setStartTime(e.target.value)}
                                fullWidth
                                InputLabelProps={{
                                    shrink: true,
                                }}
                                inputProps={{
                                    step: 300, // 5 min
                                }}
                            />
                            <TextField
                                label="Giờ kết thúc"
                                type="time"
                                value={endTime}
                                onChange={(e) => setEndTime(e.target.value)}
                                fullWidth
                                InputLabelProps={{
                                    shrink: true,
                                }}
                                inputProps={{
                                    step: 300, // 5 min
                                }}
                            />
                        </Box>

                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 3 }}>
                            <Button variant="outlined" onClick={handleCloseModal}>Hủy</Button>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleCreateSchedule}
                            >
                                Tạo lịch trình
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Modal>

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

export default TripManagement;