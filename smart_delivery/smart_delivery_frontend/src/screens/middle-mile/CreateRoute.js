import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import {
    Box, Grid, Typography, TextField, Button, Card, CardContent,
    FormControl, InputLabel, Select, MenuItem, IconButton,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, CircularProgress, Snackbar, Alert, Divider, InputAdornment
} from '@mui/material';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';

// Icons
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteIcon from '@mui/icons-material/Delete';
import DragIndicatorIcon from '@mui/icons-material/DragIndicator';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import SaveIcon from '@mui/icons-material/Save';
import ReplayIcon from '@mui/icons-material/Replay';

// For drag-and-drop functionality
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';

const CreateRoute = () => {
    const { routeId } = useParams(); // If routeId exists, we're in edit mode
    const history = useHistory();
    const isEditMode = !!routeId;

    const [loading, setLoading] = useState(isEditMode);
    const [saving, setSaving] = useState(false);
    const [hubs, setHubs] = useState([]);
    const [routeData, setRouteData] = useState({
        routeCode: '',
        routeName: '',
        description: '',
        notes: '',
        status: 'ACTIVE'
    });
    const [stops, setStops] = useState([]);
    const [selectedHub, setSelectedHub] = useState('');
    const [waitTime, setWaitTime] = useState(15); // Default wait time in minutes
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch all hubs
                const hubsData = await request(
                    "get",
                    "/smdeli/hubmanager/hub",
                    (res) =>   setHubs(res.data || [])
            ,
                    { 401: () => errorNoti("Unauthorized access") }
                );

                // If in edit mode, fetch route data
                if (isEditMode) {
                    const routeDetails = await request(
                        "get",
                        `/smdeli/middle-mile/routes/${routeId}`,
                        (res) =>   setRouteData({
                            routeCode: res.data?.routeCode,
                            routeName: res.data?.routeName,
                            description: res.data?.description || '',
                            notes: res.data?.notes || '',
                            status: res.data?.status || 'ACTIVE',
                            routeId: res.data.routeId
                        }),
                        {
                            401: () => errorNoti("Unauthorized access"),
                            404: () => {
                                errorNoti("Route not found");
                                history.push('/routes');
                            }
                        }
                    );



                    // Fetch route stops
                    const stopsData = await request(
                        "get",
                        `/smdeli/middle-mile/routes/${routeId}/stops`,
                        (res) => {
                            const formattedStops = res.data.map(stop => ({
                                hubId: stop.id,
                                hubName: stop.hubName,
                                hubCode: stop.hubCode,
                                stopSequence: stop.stopSequence,
                                estimatedWaitTime: stop.estimatedWaitTime
                            }));
                            setStops(formattedStops);
                        },
                        { 401: () => {} }
                    );


                }
            } catch (error) {
                console.error("Error fetching data:", error);
                setError("Failed to load required data");
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [routeId, isEditMode, history]);

    const handleRouteDataChange = (e) => {
        const { name, value } = e.target;
        setRouteData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleAddStop = () => {
        if (!selectedHub) {
            errorNoti("Please select a hub");
            return;
        }

        // Check if hub is already in the stops list
        if (stops.some(stop => stop.hubId === selectedHub)) {
            errorNoti("This hub is already part of the route");
            return;
        }

        const selectedHubData = hubs.find(hub => hub.id === selectedHub);

        const newStop = {
            hubId: selectedHub,
            hubName: selectedHubData ? selectedHubData.name : 'Unknown Hub',
            stopSequence: stops.length + 1,
            estimatedWaitTime: waitTime
        };

        setStops([...stops, newStop]);
        setSelectedHub('');
        setWaitTime(15); // Reset to default wait time
    };

    const handleDeleteStop = (index) => {
        const newStops = [...stops];
        newStops.splice(index, 1);

        // Update sequence numbers
        newStops.forEach((stop, idx) => {
            stop.stopSequence = idx + 1;
        });

        setStops(newStops);
    };

    const onDragEnd = (result) => {
        if (!result.destination) return;

        const reorderedStops = Array.from(stops);
        const [removed] = reorderedStops.splice(result.source.index, 1);
        reorderedStops.splice(result.destination.index, 0, removed);

        // Update sequence numbers
        reorderedStops.forEach((stop, idx) => {
            stop.stopSequence = idx + 1;
        });

        setStops(reorderedStops);
    };

    const handleSubmit = async () => {
        // Validate form
        if (!routeData.routeCode.trim() || !routeData.routeName.trim()) {
            errorNoti("Route code and name are required");
            return;
        }

        if (stops.length < 2) {
            errorNoti("Route must have at least 2 stops");
            return;
        }

        setSaving(true);

        try {
            const requestData = {
                routeId:routeData.routeId,
                routeCode: routeData.routeCode,
                routeName: routeData.routeName,
                description: routeData.description,
                notes: routeData.notes,
                status: routeData.status,
                stops: stops.map(stop => ({
                    hubId: stop.hubId,
                    stopSequence: stop.stopSequence,
                    estimatedWaitTime: stop.estimatedWaitTime
                }))
            };
            console.log("request",requestData)

            // Both create and edit use the same endpoint but with different methods
            const endpoint = "/smdeli/middle-mile/routes";

            // If editing, include the ID in the request body instead of in URL
            if (isEditMode) {
                requestData.id = routeId;
            }

            await request(
                'post',
                endpoint,
                (res) => {
                    const successMessage = isEditMode ? "Route updated successfully" : "Route created successfully";
                    successNoti(successMessage);

                    // Navigate to the route detail page
                    const navigateToId = isEditMode ? routeId : res.data.routeId;
                    history.push(`/middle-mile/routes/${navigateToId}`);
                },
                {
                    401: () => errorNoti("Unauthorized action"),
                    400: () => errorNoti("Invalid route data")
                },
                requestData
            );
        } catch (error) {
            console.error("Error saving route:", error);
            setError("Failed to save route");
        } finally {
            setSaving(false);
        }
    };

    const handleCancel = () => {
        if (isEditMode) {
            history.push(`/middle-mile/trips`);
        } else {
            history.push('/middle-mile/trips');
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <IconButton onClick={handleCancel} sx={{ mr: 2 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h4">
                    {isEditMode ? 'Edit Route' : 'Create New Route'}
                </Typography>
            </Box>

            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Route Information
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6}>
                                    <TextField
                                        fullWidth
                                        label="Route Code"
                                        name="routeCode"
                                        value={routeData.routeCode}
                                        onChange={handleRouteDataChange}
                                        required
                                        margin="normal"
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <TextField
                                        fullWidth
                                        label="Route Name"
                                        name="routeName"
                                        value={routeData.routeName}
                                        onChange={handleRouteDataChange}
                                        required
                                        margin="normal"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Description"
                                        name="description"
                                        value={routeData.description}
                                        onChange={handleRouteDataChange}
                                        multiline
                                        rows={2}
                                        margin="normal"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Notes"
                                        name="notes"
                                        value={routeData.notes}
                                        onChange={handleRouteDataChange}
                                        multiline
                                        rows={2}
                                        margin="normal"
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <FormControl fullWidth margin="normal">
                                        <InputLabel>Status</InputLabel>
                                        <Select
                                            name="status"
                                            value={routeData.status}
                                            onChange={handleRouteDataChange}
                                            label="Status"
                                        >
                                            <MenuItem value="ACTIVE">Active</MenuItem>
                                            <MenuItem value="INACTIVE">Inactive</MenuItem>
                                            <MenuItem value="SCHEDULED">Scheduled</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>
                            </Grid>

                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                                <Button
                                    variant="outlined"
                                    onClick={handleCancel}
                                    sx={{ mr: 2 }}
                                >
                                    Cancel
                                </Button>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    startIcon={saving ? <CircularProgress size={20} color="inherit" /> : <SaveIcon />}
                                    onClick={handleSubmit}
                                    disabled={saving}
                                >
                                    {saving ? 'Saving...' : 'Save Route'}
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Route Stops
                            </Typography>
                            <Divider sx={{ mb: 2 }} />

                            <Box sx={{ display: 'flex', mb: 2 }}>
                                <FormControl sx={{ mr: 1, flexGrow: 1 }}>
                                    <InputLabel>Select Hub</InputLabel>
                                    <Select
                                        value={selectedHub}
                                        onChange={(e) => setSelectedHub(e.target.value)}
                                        label="Select Hub"
                                    >
                                        <MenuItem value="">
                                            <em>Select a hub</em>
                                        </MenuItem>
                                        {hubs.length>0 && hubs.map((hub) => (
                                            <MenuItem
                                                key={hub.id}
                                                value={hub.id}
                                                disabled={stops.some(stop => stop.hubId === hub.id)}
                                            >
                                                {hub.name}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>

                                <TextField
                                    label="Wait Time (min)"
                                    type="number"
                                    value={waitTime}
                                    onChange={(e) => setWaitTime(parseInt(e.target.value) || 0)}
                                    InputProps={{ inputProps: { min: 0 } }}
                                    sx={{ width: 150 }}
                                />

                                <Button
                                    variant="contained"
                                    color="primary"
                                    startIcon={<AddCircleOutlineIcon />}
                                    onClick={handleAddStop}
                                    disabled={!selectedHub}
                                    sx={{ ml: 1, whitespace: 'nowrap' }}
                                >
                                    Add Stop
                                </Button>
                            </Box>

                            {stops.length > 0 ? (
                                <DragDropContext onDragEnd={onDragEnd}>
                                    <Droppable droppableId="stops">
                                        {(provided) => (
                                            <TableContainer component={Paper} {...provided.droppableProps} ref={provided.innerRef}>
                                                <Table>
                                                    <TableHead>
                                                        <TableRow>
                                                            <TableCell width="10%"></TableCell>
                                                            <TableCell width="10%">Seq</TableCell>
                                                            <TableCell width="40%">Hub</TableCell>
                                                            <TableCell width="30%">Wait Time</TableCell>
                                                            <TableCell width="10%">Actions</TableCell>
                                                        </TableRow>
                                                    </TableHead>
                                                    <TableBody>
                                                        {stops.map((stop, index) => (
                                                            <Draggable key={`${stop.hubId}-${index}`} draggableId={`${stop.hubId}-${index}`} index={index}>
                                                                {(provided) => (
                                                                    <TableRow
                                                                        ref={provided.innerRef}
                                                                        {...provided.draggableProps}
                                                                    >
                                                                        <TableCell {...provided.dragHandleProps}>
                                                                            <DragIndicatorIcon />
                                                                        </TableCell>
                                                                        <TableCell>{stop.stopSequence}</TableCell>
                                                                        <TableCell>{stop.hubName}</TableCell>
                                                                        <TableCell>
                                                                            <TextField
                                                                                type="number"  // Change from "text" to "number"
                                                                                value={stop.estimatedWaitTime}
                                                                                onChange={(e) => {
                                                                                    const newStops = [...stops];
                                                                                    newStops[index].estimatedWaitTime = parseInt(e.target.value) || 0;
                                                                                    setStops(newStops);
                                                                                }}
                                                                                InputProps={{
                                                                                    inputProps: { min: 0 },
                                                                                    endAdornment: <Typography variant="caption" sx={{ ml: 1 }}>min</Typography>
                                                                                }}
                                                                                size="small"
                                                                            />
                                                                        </TableCell>
                                                                        <TableCell>
                                                                            <IconButton
                                                                                color="error"
                                                                                onClick={() => handleDeleteStop(index)}
                                                                                size="small"
                                                                            >
                                                                                <DeleteIcon />
                                                                            </IconButton>
                                                                        </TableCell>
                                                                    </TableRow>
                                                                )}
                                                            </Draggable>
                                                        ))}
                                                        {provided.placeholder}
                                                    </TableBody>
                                                </Table>
                                            </TableContainer>
                                        )}
                                    </Droppable>
                                </DragDropContext>
                            ) : (
                                <Paper sx={{ p: 2, textAlign: 'center' }}>
                                    <Typography variant="body2" color="text.secondary">
                                        No stops added yet. Use the form above to add stops to your route.
                                    </Typography>
                                </Paper>
                            )}

                            <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                                Drag and drop to reorder stops. A route must have at least two stops.
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default CreateRoute;