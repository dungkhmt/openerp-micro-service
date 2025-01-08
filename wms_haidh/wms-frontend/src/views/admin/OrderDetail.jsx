import React, { useState, useEffect } from 'react';
import {
  Box,
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
import { request } from "../../api";

const OrderDetail = () => {
  const navigate = useNavigate();
  const { id1 } = useParams();
  const [details, setDetails] = useState([]);

  useEffect(() => {
    request("get", `/admin/orders/${id1}`, (res) => {
      setDetails(res.data);
    });
  }, [id1]);

  const formatPrice = (price) => {
    return price.toLocaleString("vi-VN", {
      style: "currency",
      currency: "VND",
    });
  };

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate('/admin/orders')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Sale order items
        </Typography>
      </Box>

      {/* Table */}
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Product Name</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Quantity</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Price Unit</TableCell>
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Completed</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {details && details.map((detail, index) => (
                  <TableRow key={index}>
                    <TableCell>{detail.productName}</TableCell>
                    <TableCell sx={{ textAlign: 'center' }}>{detail.quantity}</TableCell>
                    <TableCell sx={{ textAlign: 'center' }}>{formatPrice(detail.priceUnit)}</TableCell>

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
                        <Typography sx={{ ml: 1, fontSize: '0.875rem' }}>{detail.completed}%</Typography>
                      </Box>
                    </TableCell>


                    {/* Action Icon */}
                    <TableCell sx={{ textAlign: 'center' }}>
                      <IconButton
                        color="primary"
                        onClick={() => navigate(`/admin/orders/${id1}/${detail.saleOrderItemId}`)}
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
