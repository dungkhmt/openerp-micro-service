import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Typography,
  Paper,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  LinearProgress,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../../api";
import { formatDate, formatPrice } from '../../../utils/utils';

const OrderDetail = () => {
  const navigate = useNavigate();
  const { id1 } = useParams();
  const [generalInfo, setGeneralInfo] = useState(null);
  const [customerInfo, setCustomerInfo] = useState(null);
  const [details, setDetails] = useState([]);

  useEffect(() => {
    request('get', `/orders/${id1}`, (res) => {
      setGeneralInfo(res.data);
    });

    request('get', `/orders/${id1}/customer-address`, (res) => {
      setCustomerInfo(res.data);
    });
    request("get", `/order-items?orderId=${id1}`, (res) => {
      setDetails(res.data);
    });
  }, [id1]);

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate('/warehouse-manager/orders')} sx={{ color: 'grey.700', mr: 1 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Sales Order Detail
        </Typography>
      </Box>

      <Paper elevation={3} sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
          General information
        </Typography>
        {generalInfo && customerInfo &&  (
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <Typography><strong>Order date:</strong> {formatDate(generalInfo.orderDate)}</Typography>
              <Typography><strong>Order type:</strong> {generalInfo.orderType}</Typography>
              <Typography><strong>Status:</strong> {generalInfo.status.replace(/_/g, ' ')}</Typography>
              <Typography><strong>Description:</strong> {generalInfo.description}</Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography><strong>Customer name:</strong> {generalInfo.customerName}</Typography>
              <Typography><strong>Address:</strong> {customerInfo.addressName}</Typography>
            </Grid>
          </Grid>
        )}

      </Paper>

      {/* Table */}
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold' }}>Product Name</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold' }}>Unit</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Completed</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold' }}>Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {details && details.map((detail, index) => (
                  <TableRow key={index}>
                    <TableCell>{detail.productName}</TableCell>
                    <TableCell sx={{ textAlign: 'center' }}>{detail.quantity}</TableCell>
                    <TableCell sx={{ textAlign: 'center' }}>{detail.uom}</TableCell>

                    {/* Progress Bar */}
                    <TableCell sx={{ textAlign: 'center' }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', width: '150px' }}> {/* Tăng chiều dài lên 150px */}
                        <LinearProgress
                          variant="determinate"
                          value={detail.completed}
                          sx={{
                            width: '100px',
                            height: 6,
                            borderRadius: 5,
                            backgroundColor: '#E0E0E0', // Màu xám cho phần chưa hoàn thành
                            '& .MuiLinearProgress-bar': {
                              backgroundColor: detail.completed === 100 ? '#66BB6A' : '#FF9800', // Xanh lá khi 100%, Cam khi dưới 100%
                            },
                          }}
                        />
                        <Typography sx={{ ml: 1, fontSize: '0.875rem' }}>{Math.round(detail.completed)}%</Typography>
                      </Box>
                    </TableCell>


                    {/* Action Icon */}
                    <TableCell sx={{ textAlign: 'center' }}>
                      <IconButton
                        color="primary"
                        onClick={() => navigate(`/warehouse-manager/orders/${id1}/${detail.saleOrderItemId}`)}
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
      </Box>
    </Box>
  );
};

export default OrderDetail;
