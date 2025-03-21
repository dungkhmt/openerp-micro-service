import React, { useState, useEffect} from 'react';
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
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from '@mui/material';
import Map from '../../../components/Map';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { formatDate } from '../../../utils/utils';

const DeliveryTripDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [generalInfo, setGeneralInfo] = useState(null);
  const [deliverySequence, setDeliverySequence] = useState([]);
  const [route, setRoute] = useState([]);
  const [loadingMap, setLoadingMap] = useState(true);
  const [isMapOpen, setIsMapOpen] = useState(false);

  useEffect(() => {
    request('get', `/delivery-trips/${id}/general-info`, (res) => {
      setGeneralInfo(res.data);
    });

    request("get", `/delivery-trip-items/customers?deliveryTripId=${id}`, (res) => {
      setDeliverySequence(res.data);
    });
  }, [id]);

  const handleSubmit = async () => {
    request("post", `/delivery-trip/${id}/cancel`, (res) => {
      if (res.status === 200) {
        alert("Trip cancelled successfully !")
        navigate(`/delivery-manager/delivery-trip`); // Redirect after success
      }
    }, {});
  };

  const handleToggleMap = () => {
    setIsMapOpen((prev) => !prev);
  };

  useEffect(() => {
    if (isMapOpen && id && route.length === 0) {
      setLoadingMap(true);
      request('get', `/delivery-trip-paths?deliveryTripId=${id}`, (res) => {
        if (res.status === 200) {
          setRoute(res.data);
        } else {
          alert('Error fetching route data!');
        }
        setLoadingMap(false);
      });
    }
  }, [isMapOpen, id, route]);



  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/delivery-manager/delivery-trip')} sx={{ color: 'black' }}>
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
              backgroundColor: 'black',
              color: 'white',
              '&:hover': {
                backgroundColor: 'black',
                opacity: 0.75,
              }
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
                <Typography><strong>Delivery Person:</strong> {generalInfo.deliveryPersonName}</Typography>
                <Typography><strong>Total Weight:</strong> {generalInfo.totalWeight} kg</Typography>
                <Typography><strong>Total Locations:</strong> {generalInfo.totalLocations}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography><strong>Expected Delivery:</strong> {formatDate(generalInfo.expectedDeliveryStamp)}</Typography>
                <Typography><strong>Status:</strong> {generalInfo.status}</Typography>
                <Typography><strong>Description:</strong> {generalInfo.description}</Typography>
              </Grid>
            </Grid>
          )}

        </Paper>
        <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
          <Typography variant="h6" gutterBottom>
            Delivery Sequence
          </Typography>
          <div className='mb-4'>
            <Typography variant="h7" gutterBottom className="text-green-500">
              Total locations : {deliverySequence.length}
            </Typography>
          </div>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>#</TableCell>
                  <TableCell align="center">Customer Name</TableCell>
                  <TableCell align="center">Phone Number</TableCell>
                  <TableCell>Customer Address</TableCell>
                  <TableCell align="center" >Items</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {deliverySequence &&
                  deliverySequence.map(item => (
                    <TableRow key={item.orderId}>
                      <TableCell>{item.sequence}</TableCell>
                      <TableCell sx={{ textAlign: 'center' }}>{item.customerName}</TableCell>
                      <TableCell sx={{ textAlign: 'center' }}>{item.customerPhoneNumber}</TableCell>
                      <TableCell>{item.customerAddress}</TableCell>
                      {/* Action Icon */}
                      <TableCell sx={{ textAlign: 'center' }}>
                        <IconButton
                          color="primary"
                          onClick={() => navigate(`/delivery-manager/delivery-trip/${id}/${item.orderId}`)}
                        >
                          <VisibilityIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </TableContainer>

        </Paper>


        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Button
            variant="contained"
            color="primary"
            sx={{
              mt: 4,
              marginLeft: 'auto',
              backgroundColor: 'black',
              color: 'white',
              '&:hover': {
                backgroundColor: 'black',
                opacity: 0.75,
              }
            }}
            onClick={handleToggleMap}
          >
            {isMapOpen ? "Close Map" : "Open Map"}
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
                  Optimized Delivery Route
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
                    <Map route={route} />
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