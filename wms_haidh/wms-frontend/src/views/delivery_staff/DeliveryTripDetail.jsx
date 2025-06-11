import React, { useState, useEffect } from 'react';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../api";
import {
  Box,
  Typography,
  Grid,
  Button,
  Paper,
  IconButton,
  Stack
} from '@mui/material';
import Map from '../../components/Map';
import { formatDate } from '../../utils/utils';
import PhoneIcon from '@mui/icons-material/Phone';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import MapIcon from '@mui/icons-material/Map';
import ListAltIcon from '@mui/icons-material/ListAlt';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { toast, Toaster } from "react-hot-toast";

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
    request("post", `/delivery-trips/${id}/start`, (res) => {
      if (res.status === 200) {
        request('get', `/delivery-trips/${id}/general-info`, (res) => {
          setGeneralInfo(res.data);
        });
        toast.success("Your delivery trip has started. Good luck!");
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      }
    });
  };

  const handleToggleMap = () => {
    setIsMapOpen((prev) => !prev);
  };

  const handleMarkDelivered = (item) => {
    request('post', `/delivery-trips/${id}/mark-delivered?orderId=${item.orderId}`, (res) => {
      if (res.status === 200) {
        request('get', `/delivery-trips/${id}/general-info`, (res) => {
          setGeneralInfo(res.data);
        });

        request("get", `/delivery-trip-items/customers?deliveryTripId=${id}`, (res) => {
          setDeliverySequence(res.data);
        });

        toast.success(`Delivery to ${item.customerName} completed.`);
      } else {
        toast.error("Failed to mark as delivered.");
      }
    });
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
      <Toaster />
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/delivery-staff/delivery-trip')} sx={{ color: 'grey.700', mr: 1 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          Delivery Trip Detail
        </Typography>
        {
          generalInfo && generalInfo.status === 'CREATED' &&
          (<Button
            variant="contained"
            color="primary"
            sx={{
              marginLeft: 'auto',
              backgroundColor: '#019160',
              color: '#fff',
              '&:hover': {
                backgroundColor: '#2fbe8e', // hiệu ứng khi hover
              },
              '&:active': {
                backgroundColor: '#01b075', // hiệu ứng khi nhấn
              }
            }}
            onClick={handleSubmit}
          >
            Start
          </Button>
          )
        }
      </Box>

      {/* General Info */}
      <Paper elevation={3} sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>General information</Typography>
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

      {/* Delivery Cards */}
      <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
        <Typography variant="h6" gutterBottom>Delivery sequence</Typography>
        <Typography className="text-green-500" sx={{ mb: 2 }}>
          Total orders: {deliverySequence.length}
        </Typography>

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
                  onClick={() => navigate(`/delivery-staff/delivery-trip/${id}/${item.orderId}`)}
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


                {generalInfo && generalInfo.status === 'STARTED' && item.status === 'CREATED' && (
                  <Stack direction="row" spacing={1}>
                    <Button
                      variant="contained"
                      startIcon={<CheckCircleIcon />}
                      onClick={() => handleMarkDelivered(item)}
                      sx={{
                        backgroundColor: '#019160',
                        color: '#fff',
                        '&:hover': {
                          backgroundColor: '#2fbe8e', // hover
                        },
                        '&:active': {
                          backgroundColor: '#01b075', // active
                        },
                      }}
                    >
                      Delivered
                    </Button>

                  </Stack>
                )}
              </Box>
            </Paper>
          ))}
        </Stack>
      </Paper>

      {/* Toggle Map */}
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

      {/* Map Section */}
      {isMapOpen && (
        <Grid container sx={{ mt: 4 }}>
          <Grid item xs={12}>
            <Paper elevation={3} sx={{ p: 3, height: 540 }}>
              <Typography variant="h6" gutterBottom align="center">
                Delivery route
              </Typography>
              <Typography variant="h6" gutterBottom align="center" className="text-green-500">
                Distance : {(generalInfo?.distance / 1000).toFixed(2)} km
              </Typography>
              <Box sx={{ height: '100%', borderRadius: 1, overflow: 'hidden' }}>
                {loadingMap ? (
                  <Typography variant="h6" align="center">Loading route...</Typography>
                ) : (
                  <Map route={route} markerCoordinates={waypoints} />
                )}
              </Box>
            </Paper>
          </Grid>
        </Grid>
      )}
    </Box>
  );
};

export default DeliveryTripDetail;
