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
import { formatDate } from '../../utils/utils';
const ReceiptDetail = () => {
  const navigate = useNavigate();
  const { receiptId } = useParams();
  const [receiptDetails, setReceiptDetails] = useState([]);
  const [generalInfo, setGeneralInfo] = useState(null);

  useEffect(() => {
    request('get', `/receipts/${receiptId}`, (res) => {
      setGeneralInfo(res.data);
    });
    request("get", `/receipt-item-requests?receiptId=${receiptId}`, (res) => {
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

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: 200 }}>
            <Typography variant="h6" gutterBottom>
              General
            </Typography>
            <Typography><b>Receipt Name:</b> {generalInfo && generalInfo.receiptName}</Typography>
            <Typography><b>Warehouse:</b> {generalInfo && generalInfo.warehouseName}</Typography>
            <Typography><b>Expected Receipt Date:</b> {generalInfo && formatDate(generalInfo.expectedReceiptDate)}</Typography>
            <Typography><b>Status:</b> {generalInfo && generalInfo.status}</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: 200 }}>
            <Typography variant="h6" gutterBottom>
              Details
            </Typography>
            <Typography><b>Created By:</b>  {generalInfo && generalInfo.createdBy}</Typography>
            <Typography><b>Created Reason:</b>  {generalInfo && generalInfo.createdReason}</Typography>
            <Typography><b>Created Time:</b> {generalInfo && formatDate(generalInfo.createdStamp)}</Typography>         
            <Typography><b>Description:</b> {generalInfo && generalInfo.description}</Typography>
          </Paper>
        </Grid>
      </Grid>

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
                </TableRow>
              </TableHead>
              <TableBody>
                {receiptDetails.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>{item.productName}</TableCell>
                    <TableCell sx={{ textAlign: 'center' }}>{item.quantity}</TableCell>
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
