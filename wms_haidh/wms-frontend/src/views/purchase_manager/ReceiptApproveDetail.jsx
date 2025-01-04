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

const ReceiptApproveDetail = () => {
 const navigate = useNavigate();
   const { receiptId } = useParams();
   const [receiptDetails, setReceiptDetails] = useState([]);
 
   useEffect(() => {
     request("get", `/purchase-manager/receipts/${receiptId}`, (res) => {
       setReceiptDetails(res.data);
     });
   }, [receiptId]);
 
   const handleApprove = () => {
     request(
       "post",
       `/purchase-manager/process-receipts/approve/${receiptId}?approvedBy=admin`,
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
       `/purchase-manager/process-receipts/cancel/${receiptId}?cancelledBy=admin`,
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
                  <TableCell sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Product Name</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Quantity</TableCell>
                  <TableCell align="center" sx={{ fontWeight: 'bold', borderTop: '1px solid rgba(224, 224, 224, 1)' }}>Warehouse</TableCell>
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

export default ReceiptApproveDetail;
