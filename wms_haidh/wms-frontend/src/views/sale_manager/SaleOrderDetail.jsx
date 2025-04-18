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
    request('post', `/orders/${orderId}/approve?approvedBy=admin`, (res) => {
      if (res.status === 200) {
        alert('Order approved successfully!');
        navigate('/sale-manager/sale-order');
      } else {
        alert('Failed to approve order.');
      }
    });
  };

  const handleCancel = () => {
    request('post', `/orders/${orderId}/cancel?cancelledBy=admin`, (res) => {
      if (res.status === 200) {
        alert('Order cancelled successfully!');
        navigate('/sale-manager/sale-order');
      } else {
        alert('Failed to cancel order.');
      }
    });
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate('/sale-manager/sale-order')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Order Details
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: 300 }}>
            <Typography variant="h6" gutterBottom>
              General Information
            </Typography>
            <Typography><b>Order Date:</b> {generalInfo && formatDate(generalInfo.orderDate)}</Typography>
            <Typography><b>Delivery Fee:</b> {generalInfo && formatPrice(generalInfo.deliveryFee)}</Typography>
            <Typography><b>Total Product Cost:</b> {generalInfo && formatPrice(generalInfo.totalProductCost)}</Typography>
            <Typography><b>Total Order Cost:</b> {generalInfo && formatPrice(generalInfo.totalOrderCost)}</Typography>
            <Typography><b>Description:</b> {generalInfo && generalInfo.description}</Typography>
            <Typography><b>Payment Type:</b> {generalInfo && generalInfo.paymentType}</Typography>
            <Typography><b>Order Type:</b> {generalInfo && generalInfo.orderType}</Typography>
            <Typography><b>Status:</b> {generalInfo && generalInfo.status}</Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: 300 }}>
            <Typography variant="h6" gutterBottom>
              Customer Information
            </Typography>
            <Typography><b>Customer Name:</b>  <br />{generalInfo && generalInfo.customerName}</Typography>
            <Typography><b>Phone Number:</b>  <br />{generalInfo && generalInfo.customerPhoneNumber}</Typography>
            <Typography><b>Address:</b>  <br />{customerInfo && customerInfo.addressName}</Typography>
          </Paper>
        </Grid>
      </Grid>

      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Order Items
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold' }}>Product Name</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Price Unit</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {details.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>{item.productName}</TableCell>
                    <TableCell>{item.quantity}</TableCell>
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
          <Button variant="contained" color="primary" onClick={handleApprove}>
            Approve
          </Button>
        </Box>)}
    </Box >

  );
};

export default SaleOrderDetail;
