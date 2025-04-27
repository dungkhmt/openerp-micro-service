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

const RoutingRecommendation = () => {
  const navigate = useNavigate();
  const [deliveryTrips, setDeliveryTrips] = useState([{ warehouseId: '', vehicleId: '' }]);
  const [warehouseList, setWarehouseList] = useState([]);
  const [vehicleList, setVehicleList] = useState([]);

  useEffect(() => {
    request("get", "/warehouses", (res) => {
      setWarehouseList(res.data);
    }).then();

    request("get", "/vehicles", (res) => {
      setVehicleList(res.data);
    }).then();
  }, []);

  const handleAddTrip = () => {
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
        } else {
          alert("Error occurred!");
        }
      },
      {},
      { vehicles }
    );
  };


  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton onClick={() => navigate('/delivery-manager/shipments')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 1 }}>
          Routing Recommendation
        </Typography>
        <Button
          variant="contained"
          sx={{
            marginLeft: 'auto',
            backgroundColor: 'black',
            color: 'white',
            '&:hover': { backgroundColor: 'black', opacity: 0.75 }
          }}
          onClick={handleSubmit}
        >
          Submit
        </Button>
      </Box>

      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Delivery Trips</Typography>
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
                          {vehicleList.map((vehicle) => (
                            <MenuItem key={vehicle.vehicleId} value={vehicle.vehicleId}>
                              {vehicle.name}
                            </MenuItem>
                          ))}
                        </Select>
                      </FormControl>
                    </Grid>
                    <Grid item xs={2}>
                      <IconButton onClick={() => handleDeleteTrip(index)}>
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
                mt: 1,
                color: 'black',
                borderColor: 'black',
                border: '1px solid',
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                '&:hover': {
                  backgroundColor: 'black',
                  color: 'white',
                  borderColor: 'black',
                  '& .add-icon': {
                    color: 'white',
                  }
                }
              }}
            >
              <AddIcon className="add-icon" sx={{ color: 'black' }} />
              Add delivery trip
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default RoutingRecommendation;
