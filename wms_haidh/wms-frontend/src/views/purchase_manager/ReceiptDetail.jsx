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
    request("get", `/receipt-item-requests?receiptId=${receiptId}`, (res) => {
      setReceiptDetails(res.data);
    });
  }, [receiptId]);

  const handleApprove = () => {
    request(
      "post",
      `/receipts/${receiptId}/approve?approvedBy=admin`,
      (res) => {
        if (res.status === 200) {
          navigate(`/purchase-manager/process-receipts`);
        } else {
          alert("Error approving receipt!");
        }
      }
    );
  };

  const handleCancel = () => {
    request(
      "post",
      `/receipts/${receiptId}/cancel?cancelledBy=admin`,
      (res) => {
        if (res.status === 200) {
          navigate(`/purchase-manager/process-receipts`);
        } else {
          alert("Error rejecting receipt!");
        }
      }
    );
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate('/purchase-manager/process-receipts')} sx={{ color: 'black' }}>
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

      <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
        <Button variant="contained" color="error" onClick={handleCancel} sx={{ mr: 2 }}>
          Cancel
        </Button>
        <Button variant="contained" color="primary" onClick={handleApprove}>
          Approve
        </Button>
      </Box>
    </Box>
  );
};

export default ReceiptDetail;
