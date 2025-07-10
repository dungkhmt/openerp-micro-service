import React, { useState, useEffect } from 'react';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../../api";
import {
  Box,
  Typography,
  Grid,
  Button,
  Paper,
  IconButton,
  Stack
} from '@mui/material';
import Map from '../../../components/Map';
import { formatDate } from '../../../utils/utils';
import PhoneIcon from '@mui/icons-material/Phone';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import MapIcon from '@mui/icons-material/Map';
import ListAltIcon from '@mui/icons-material/ListAlt';

const DeliveryTripDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [generalInfo, setGeneralInfo] = useState(null);
  const [deliverySequence, setDeliverySequence] = useState([]);
  const [route, setRoute] = useState([]);
  const [loadingMap, setLoadingMap] = useState(true);
  const [isMapOpen, setIsMapOpen] = useState(false);
  const [waypoints, setWaypoints] = useState([]);

  useEffect(() => {
    request('get', `/delivery-trips/${id}/general-info`, (res) => {
      setGeneralInfo(res.data);
    });

    request("get", `/delivery-trip-items/customers?deliveryTripId=${id}`, (res) => {
      setDeliverySequence(res.data);
    });
  }, [id]);

  const handleSubmit = async () => {
    request("post", `/delivery-trips/${id}/cancel`, (res) => {
      if (res.status === 200) {
        alert("Trip cancelled successfully !");
        navigate(`/delivery-manager/delivery-trip`); // Redirect after success
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      },
    });
  };

  const handleToggleMap = () => {
    setIsMapOpen((prev) => !prev);
  };

  useEffect(() => {
    if (isMapOpen && id && route.length === 0) {
      setLoadingMap(true);
      request('get', `/delivery-trip-paths?deliveryTripId=${id}`, (res) => {
        if (res.status === 200) {
          setRoute(res.data.path);
        } 
        setLoadingMap(false);
      });
      request('get', `/delivery-trip-paths/waypoints?deliveryTripId=${id}`, (res) => {
        if (res.status === 200) {
          setWaypoints(res.data);
        }   
      });
    }
  }, [isMapOpen, id, route]);



  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/delivery-manager/delivery-trip')} sx={{ color: 'grey.700', mr: 1 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          Delivery Trip Detail
        </Typography>
        {
          generalInfo && generalInfo.status === 'CREATED' &&
          (<Button
            variant="contained"
            color="error"
            sx={{
              marginLeft: 'auto'
            }}
            onClick={handleSubmit}
          >
            Cancel
          </Button>)
        }
      </Box>
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
            General information
          </Typography>
          {generalInfo && (
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography><strong>Warehouse:</strong> {generalInfo.warehouseName}</Typography>
                <Typography><strong>Delivery person:</strong> {generalInfo.deliveryPersonName}</Typography>
                <Typography><strong>Total weight:</strong> {generalInfo.totalWeight} kg</Typography>
                <Typography><strong>Vehicle:</strong> {generalInfo.vehicleName}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography><strong>Expected delivery:</strong> {formatDate(generalInfo.expectedDeliveryStamp)}</Typography>
                <Typography><strong>Total locations:</strong> {generalInfo.totalLocations}</Typography>
                <Typography><strong>Status:</strong> {generalInfo.status}</Typography>
                <Typography><strong>Description:</strong> {generalInfo.description}</Typography>
              </Grid>
            </Grid>
          )}

        </Paper>
        <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
          <Typography variant="h6" gutterBottom>
            Delivery sequence
          </Typography>
          <div className='mb-4'>
            <Typography variant="h7" gutterBottom className="text-green-500">
              Total orders : {deliverySequence.length}
            </Typography>
          </div>
          <Stack spacing={2}>
            {deliverySequence && deliverySequence.map((item) => (
              <Paper key={item.orderId} elevation={2} sx={{ p: 2, mb: 2 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                  #{item.sequence} - {item.customerName}
                </Typography>

                <Stack direction="row" alignItems="center" spacing={1} sx={{ mt: 1 }}>
                  <PhoneIcon fontSize="small" />
                  <Typography>{item.customerPhoneNumber}</Typography>
                </Stack>

                <Stack direction="row" alignItems="center" spacing={1} sx={{ mt: 1 }}>
                  <LocationOnIcon fontSize="small" />
                  <Typography>{item.customerAddress}</Typography>
                </Stack>

                <Typography sx={{ mt: 1 }}>
                  <strong>Status:</strong> {item.status}
                </Typography>

                <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
                  {/* Left side: View Items */}
                  <Button
                    variant="contained"
                    startIcon={<ListAltIcon />}
                    color="primary"
                    onClick={() => navigate(`/delivery-manager/delivery-trip/${id}/${item.orderId}`)}
                    sx={{
                      backgroundColor: '#019160',
                      color: '#fff',
                      '&:hover': {
                        backgroundColor: '#2fbe8e', // hiệu ứng hover
                      },
                      '&:active': {
                        backgroundColor: '#01b075', // hiệu ứng nhấn
                      },
                    }}
                  >
                    View Items
                  </Button>
                </Box>

              </Paper>
            ))}
          </Stack>
        </Paper>


        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Button
            variant="contained"
            color="primary"
            sx={{
              mt: 4,
              backgroundColor: '#019160',
              color: '#fff',
              '&:hover': {
                backgroundColor: '#2fbe8e', // hiệu ứng hover
              },
              '&:active': {
                backgroundColor: '#01b075', // hiệu ứng nhấn
              },
            }}
            onClick={handleToggleMap}
            startIcon={<MapIcon />}
          >
            {isMapOpen ? 'Close Map' : 'Open Map'}
          </Button>

        </Box>

        {isMapOpen &&
          <Grid container sx={{ mt: 4 }}>
            {/* Map Container */}
            <Grid item xs={12}>
              <Paper elevation={3} sx={{ p: 3, height: 540 }}>
                <Typography
                  variant="h6"
                  gutterBottom
                  sx={{
                    textAlign: 'center'
                  }}
                >
                  Delivery Route
                </Typography>
                <Typography variant="h6" gutterBottom className="text-green-500" sx={{
                  textAlign: 'center',
                  marginBottom: 2,    // Spacing below title
                }}>
                  Distance : {(generalInfo.distance / 1000).toFixed(2)} km
                </Typography>

                <Box sx={{ height: '100%', borderRadius: 1, overflow: 'hidden' }}>
                  {/* Pass the route data to the Map component */}
                  {loadingMap ? (
                    <Typography variant="h6" sx={{ textAlign: 'center' }}>
                      Loading route...
                    </Typography>
                  ) : (
                    <Map route={route} markerCoordinates={waypoints} />
                  )}
                </Box>
              </Paper>
            </Grid>

          </Grid>
        }
      </Box>
    </Box>
  );
};

export default DeliveryTripDetail;