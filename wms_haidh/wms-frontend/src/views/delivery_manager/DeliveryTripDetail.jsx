import React, { useState, useEffect, useRef, useMemo } from 'react';
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
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from '@mui/material';
import Map from '../../components/Map';
import VisibilityIcon from '@mui/icons-material/Visibility';
import fetchRoute from '../../utils/fetchRoute';
import { formatDate, formatPrice } from '../../utils/utils';

const DeliveryTripDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [deliverySequence, setDeliverySequence] = useState([]);
  const [customerInfo, setCustomerInfo] = useState({});
  const [route, setRoute] = useState([]);
  const [loadingMap, setLoadingMap] = useState(true);
  const [distance, setDistance] = useState(0);
  const [isMapOpen, setIsMapOpen] = useState(false);


  const handleSubmit = async () => {

  };

  const handleToggleMap = () => {
    setIsMapOpen((prev) => !prev);
  };



  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/delivery-manager/delivery-trip')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          Delivery Trip Detail
        </Typography>
        <Button
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
        </Button>
      </Box>
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
            General information
          </Typography>

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
                  <TableCell>Customer Name</TableCell>
                  <TableCell>Phone Number</TableCell>
                  <TableCell>Customer Address</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {deliverySequence &&
                  deliverySequence.map((item, index) => (
                    <TableRow key={item.orderId}>
                      <TableCell>{index + 1}</TableCell>
                      <TableCell>{customerInfo[item.orderId]?.customerName || 'Loading...'}</TableCell>
                      <TableCell>{customerInfo[item.orderId]?.customerPhoneNumber || 'Loading...'}</TableCell>
                      <TableCell>{customerInfo[item.orderId]?.addressName || 'Loading...'}</TableCell>
                      {/* Action Icon */}
                      <TableCell sx={{ textAlign: 'center' }}>
                        <IconButton
                          color="primary"
                          onClick={() => navigate(`/delivery-manager/delivery-trip`)}
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
                  Distance : {(distance / 1000).toFixed(2)} km
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