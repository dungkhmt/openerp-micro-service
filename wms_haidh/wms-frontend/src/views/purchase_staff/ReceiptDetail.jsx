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

const ReceiptDetail = () => {
  const navigate = useNavigate();
  const { receiptId } = useParams();
  const [receiptDetails, setReceiptDetails] = useState([]);

  useEffect(() => {
    request("get", `/receipts/${receiptId}`, (res) => {
      setReceiptDetails(res.data);
    });
  }, [receiptId]);

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate('/purchase-staff/receipts')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Receipt Details
        </Typography>
      </Box>

      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Receipt Items
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold' }}>Product Name</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold' }}>Warehouse</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {receiptDetails.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>{item.productName}</TableCell>
                    <TableCell sx={{ textAlign: 'center' }}>{item.quantity}</TableCell>
                    <TableCell sx={{ textAlign: 'center' }}>{item.warehouseName}</TableCell>
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

export default ReceiptDetail;
