import React, { useState, useEffect } from 'react';
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
  TableRow,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from '../../api';
import { formatDate, formatPrice } from '../../utils/utils';

const SaleOrderDetail = () => {
  const navigate = useNavigate();
  const { orderId } = useParams();
  const [generalInfo, setGeneralInfo] = useState(null);
  const [customerInfo, setCustomerInfo] = useState(null);
  const [details, setDetails] = useState([]);

  useEffect(() => {
    request('get', `/orders/${orderId}`, (res) => {
      setGeneralInfo(res.data);
    });

    request('get', `/orders/${orderId}/customer-address`, (res) => {
      setCustomerInfo(res.data);
    });

    request('get', `/order-items?orderId=${orderId}`, (res) => {
      setDetails(res.data);
    });
  }, [orderId]);

  const handleApprove = () => {
    request('post', `/orders/${orderId}/approve`, (res) => {
      if (res.status === 200) {
        alert('Order approved successfully!');
        navigate('/sale-manager/sale-order');
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      },
    });
  };

  const handleCancel = () => {
    request('post', `/orders/${orderId}/cancel`, (res) => {
      if (res.status === 200) {
        alert('Order cancelled successfully!');
        navigate('/sale-manager/sale-order');
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      },
    });
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate('/sale-manager/sale-order')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Sales Order Details
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: 300 }}>
            <Typography variant="h6" gutterBottom>
              Order information
            </Typography>
            <Typography><b>Order date:</b> {generalInfo && formatDate(generalInfo.orderDate)}</Typography>
            <Typography><b>Delivery fee:</b> {generalInfo && formatPrice(generalInfo.deliveryFee)}</Typography>
            <Typography><b>Total product cost:</b> {generalInfo && formatPrice(generalInfo.totalProductCost)}</Typography>
            <Typography><b>Total order cost:</b> {generalInfo && formatPrice(generalInfo.totalOrderCost)}</Typography>
            <Typography><b>Description:</b> {generalInfo && generalInfo.description}</Typography>
            <Typography><b>Payment type:</b> {generalInfo && generalInfo.paymentType}</Typography>
            <Typography><b>Order type:</b> {generalInfo && generalInfo.orderType}</Typography>
            <Typography><b>Status:</b> {generalInfo && generalInfo.status}</Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: 300 }}>
            <Typography variant="h6" gutterBottom>
              Customer information
            </Typography>
            <Typography><b>Customer name:</b>  <br />{generalInfo && generalInfo.customerName}</Typography>
            <Typography><b>Phone number:</b>  <br />{generalInfo && generalInfo.customerPhoneNumber}</Typography>
            <Typography><b>Address:</b>  <br />{customerInfo && customerInfo.addressName}</Typography>
          </Paper>
        </Grid>
      </Grid>

      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Sales order items
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold' }}>Product Name</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Unit</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Price Unit</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {details.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>{item.productName}</TableCell>
                    <TableCell>{item.quantity}</TableCell>
                    <TableCell>{item.uom}</TableCell>
                    <TableCell>{formatPrice(item.priceUnit)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      </Box>

      {generalInfo && generalInfo.status === 'CREATED' && (
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
          <Button variant="contained" color="error" onClick={handleCancel} sx={{ mr: 2 }}>
            Cancel
          </Button>
          <Button
            variant="contained"
            onClick={handleApprove}
            sx={{
              backgroundColor: '#019160',
              color: '#fff',
              '&:hover': {
                backgroundColor: '#2fbe8e',
              },
              '&:active': {
                backgroundColor: '#01b075',
              },
            }}
          >
            Approve
          </Button>

        </Box>)}
    </Box >

  );
};

export default SaleOrderDetail;
