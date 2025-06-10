import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  FormControl,
  Grid,
  IconButton,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Typography,
  List,
  ListItem
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { request } from "../../../api";
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import { toast, Toaster } from "react-hot-toast";

const RoutingRecommendation = () => {
  const navigate = useNavigate();
  const [deliveryTrips, setDeliveryTrips] = useState([{ warehouseId: '', vehicleId: '' }]);
  const [warehouseList, setWarehouseList] = useState([]);
  const [vehicleList, setVehicleList] = useState([]);

  useEffect(() => {
    request("get", "/warehouses/with-picked-assigned-items", (res) => {
      setWarehouseList(res.data);
    }).then();

    request("get", "/vehicles", (res) => {
      setVehicleList(res.data);
    }).then();
  }, []);

  const handleAddTrip = () => {
    const isAllValid = deliveryTrips.every(
      trip => trip.warehouseId && trip.vehicleId
    );

    if (!isAllValid) {
      toast.error("Please complete all existing trips before adding a new one.");
      return;
    }

    setDeliveryTrips([...deliveryTrips, { warehouseId: '', vehicleId: '' }]);
  };


  const handleDeleteTrip = (index) => {
    const updatedTrips = deliveryTrips.filter((_, i) => i !== index);
    setDeliveryTrips(updatedTrips);
  };

  const handleTripChange = (index, field, value) => {
    const updatedTrips = [...deliveryTrips];
    updatedTrips[index][field] = value;
    setDeliveryTrips(updatedTrips);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Map deliveryTrips để thêm maxWeight dựa theo vehicleId
    const vehicles = deliveryTrips.map(trip => {
      const vehicle = vehicleList.find(v => v.vehicleId === trip.vehicleId);
      return {
        ...trip,
        maxWeight: vehicle.maxWeight
      };
    });

    request(
      "post",
      "/routing-recommendations",
      (res) => {
        if (res.status === 200) {
          navigate("result", { state: { routingResult: res.data } });
        }
      },
      {
        onError: (e) => {
          toast.error(e?.response?.data || "Error occured!");
        }
      },
      { vehicles }
    );
  };


  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Toaster />
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton onClick={() => navigate('/delivery-manager/shipments')} sx={{ color: 'grey.700', mr: 1 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 1 }}>
          Smart Planning
        </Typography>
        <Button
          disabled={deliveryTrips.some(trip => !trip.warehouseId || !trip.vehicleId)}
          variant="contained"
          startIcon={<PlayArrowIcon />}
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
          Generate Plan
        </Button>
      </Box>

      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>Delivery Trips</Typography>
            <List>
              {deliveryTrips.map((trip, index) => (
                <ListItem key={index} disablePadding sx={{ mb: 2 }}>
                  <Grid container spacing={2} alignItems="center">
                    <Grid item xs={5}>
                      <FormControl fullWidth>
                        <InputLabel id={`warehouse-label-${index}`}>Departure Warehouse</InputLabel>
                        <Select
                          labelId={`warehouse-label-${index}`}
                          value={trip.warehouseId}
                          onChange={(e) => handleTripChange(index, 'warehouseId', e.target.value)}
                          label="Departure Warehouse"
                        >
                          {warehouseList.map((warehouse) => (
                            <MenuItem key={warehouse.warehouseId} value={warehouse.warehouseId}>
                              {warehouse.name}
                            </MenuItem>
                          ))}
                        </Select>
                      </FormControl>
                    </Grid>
                    <Grid item xs={5}>
                      <FormControl fullWidth>
                        <InputLabel id={`vehicle-label-${index}`}>Vehicle</InputLabel>
                        <Select
                          labelId={`vehicle-label-${index}`}
                          value={trip.vehicleId}
                          onChange={(e) => handleTripChange(index, 'vehicleId', e.target.value)}
                          label="Vehicle"
                        >
                          {vehicleList
                            .filter(vehicle =>
                              !deliveryTrips.some((trip, i) =>
                                i !== index && trip.vehicleId === vehicle.vehicleId
                              )
                            )
                            .map(vehicle => (
                              <MenuItem key={vehicle.vehicleId} value={vehicle.vehicleId}>
                                {vehicle.name}
                              </MenuItem>
                            ))}
                        </Select>
                      </FormControl>

                    </Grid>
                    <Grid item xs={2}>
                      <IconButton sx={{
                        color: 'grey.600',
                        '&:hover': {
                          color: 'error.main',
                        }
                      }} onClick={() => handleDeleteTrip(index)}>
                        <DeleteIcon />
                      </IconButton>
                    </Grid>
                  </Grid>
                </ListItem>
              ))}
            </List>

            <Button
              onClick={handleAddTrip}
              variant="outlined"
              sx={{
                mt: 2,
                border: '1px solid #019160',
                color: '#019160',
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                transition: 'background-color 0.2s ease, color 0.2s ease',
                '&:hover': {
                  backgroundColor: '#019160',
                  color: '#ffffff',
                  borderColor: '#019160',
                  '& .add-icon': {
                    color: '#ffffff',
                  },
                },
                '&:active': {
                  backgroundColor: '#01b075',
                  borderColor: '#01b075',
                },
              }}
            >
              <AddIcon className="add-icon" sx={{ color: 'inherit' }} />
              Add Delivery Trip
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default RoutingRecommendation;
