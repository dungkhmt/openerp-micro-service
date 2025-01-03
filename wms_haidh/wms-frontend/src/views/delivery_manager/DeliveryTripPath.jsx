import React, { useState, useEffect } from 'react';
import Map from './Map';
import { Box, Paper, Typography, Grid, IconButton } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from '../../api'; // Assuming you have a utility function for making API requests

const DeliveryTripPath = () => {
  const navigate = useNavigate();
  const { deliveryTripId } = useParams();  // Get deliveryTripId from URL params
  const [route, setRoute] = useState([]);   // State to hold route data
  const [loading, setLoading] = useState(true); // State for loading indicator

  // Fetch route data from API
  useEffect(() => {
    setLoading(true);
    request('get', `/delivery-manager/delivery-trips/paths/${deliveryTripId}`, (res) => {
      if (res.status === 200) {
        setRoute(res.data); // Assuming the API response contains the route data
      } else {
        alert('Error fetching route data!');
      }
      setLoading(false);
    });
  }, [deliveryTripId]);

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate('/delivery-manager/delivery-trip')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Delivery Trip Path
        </Typography>
      </Box>

      {/* Main Content */}
      <Grid container spacing={3}>
        {/* Map Container */}
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3, height: 500 }}>
            <Typography
              variant="h6"
              gutterBottom
              sx={{
                textAlign: 'center', // Center title
                marginBottom: 2,    // Spacing below title
              }}
            >
              Delivery Route Map
            </Typography>
            <Box sx={{ height: '100%', borderRadius: 1, overflow: 'hidden' }}>
              {/* Pass the route data to the Map component */}
              {loading ? (
                <Typography variant="h6" sx={{ textAlign: 'center' }}>
                  Loading route...
                </Typography>
              ) : (
                <Map route={route} />
              )}
            </Box>
          </Paper>
        </Grid>
        
      </Grid>
    </Box>
  );
};

export default DeliveryTripPath;

