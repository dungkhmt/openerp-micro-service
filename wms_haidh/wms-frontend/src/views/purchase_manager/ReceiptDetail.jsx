import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  Button,
  Paper,
  IconButton,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../api";

const ReceiptDetail = () => {
  const navigate = useNavigate();
  const { receiptId } = useParams();
  const [receiptDetails, setReceiptDetails] = useState([]);

  useEffect(() => {
    request("get", `/purchase-manager/receipts/${receiptId}`, (res) => {
      setReceiptDetails(res.data);
    });
  }, [receiptId]);

  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/purchase-manager/receipts')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          Receipt Details
        </Typography>
      </Box>

      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Receipt Items
            </Typography>
            {receiptDetails.map((item, index) => (
              <Box key={index} sx={{ mb: 2, p: 2, border: '1px solid #ccc', borderRadius: '4px' }}>
                <Typography><strong>Product Name:</strong> {item.productName}</Typography>
                <Typography><strong>Quantity:</strong> {item.quantity}</Typography>
                <Typography><strong>Warehouse Name:</strong> {item.warehouseName}</Typography>
              </Box>
            ))}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ReceiptDetail;
