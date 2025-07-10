import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  Button,
  Paper,
  IconButton,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { CircularProgress } from "@heroui/react";
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../../api";
import { formatDate, formatPrice } from '../../../utils/utils';
import { toast, Toaster } from "react-hot-toast";
import SaveIcon from '@mui/icons-material/Save';

const ReceiptItem = () => {
  const navigate = useNavigate();
  const { id1, id2 } = useParams();
  const [generalInfo, setGeneralInfo] = useState(null);
  const [bayOptions, setBayOptions] = useState([]);
  const [quantity, setQuantity] = useState('');
  const [bayCode, setBayCode] = useState('');
  const [importPrice, setImportPrice] = useState('');
  const [expiredDate, setExpiredDate] = useState('');
  const [lotId, setLotId] = useState('');
  const [details, setDetails] = useState([]);
  const remainingQuantity = generalInfo ? Math.round(generalInfo.quantity * (1 - generalInfo.completed / 100)) : 0;


  useEffect(() => {
    request("get", `/receipt-item-requests/${id2}/general-info`, (res) => {
      setGeneralInfo(res.data);
    });

    request("get", `/receipt-item-requests/${id2}/bays`, (res) => {
      setBayOptions(res.data);
    });

    request("get", `/receipt-items?requestId=${id2}`, (res) => {
      setDetails(res.data);
    });

  }, [id2]);

  const handleSubmit = () => {

    // Kiểm tra các trường bắt buộc
    if (!bayCode) {
      toast.error("Bay code is required!");
      return;
    }
    if (!lotId) {
      toast.error("Lot ID is required!");
      return;
    }

    if (!importPrice || importPrice <= 0) {
      toast.error("Invalid import price!");
      return;
    }

    if (!quantity || quantity > remainingQuantity || quantity <= 0) {
      toast.error("Invalid quantity!");
      return;
    }

    // Tạo payload và gửi
    const payload = {
      quantity,
      bayId: bayCode,
      lotId,
      importPrice,
      expiredDate,
      receiptItemRequestId: id2
    };

    request("post", `/receipt-items`, (res) => {
      if (res.status === 200) {
        request("get", `/receipt-item-requests/${id2}/general-info`, (res) => {
          setGeneralInfo(res.data);
        });
        request("get", `/receipt-items?requestId=${id2}`, (res) => {
          setDetails(res.data);
        });
        toast.success("Create new receipt item successfully!");
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      }
    }, payload);
  };


  return (
    <Box sx={{ p: 3 }}>
      <Toaster />
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate(`/warehouse-manager/receipts/${id1}`)} sx={{ color: 'grey.700', mr: 1 }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Putaway assignment
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Box chứa vòng tròn */}
        <Grid item xs={12} md={4} sx={{ maxWidth: '30%' }}>
          <Paper
            elevation={3}
            sx={{
              p: 3,
              textAlign: 'center',
              height: 300,
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              position: 'relative', // Quan trọng để dùng positioning bên trong
            }}
          >
            <CircularProgress
              aria-label="Loading..."
              classNames={{
                svg: "w-48 h-48 drop-shadow-lg", // Kích thước vòng tròn
                indicator: (generalInfo && generalInfo.completed === 100) ? "stroke-green-400" : "stroke-orange-400",
                track: "stroke-gray-300",        // Màu nền vòng
              }}
              strokeWidth={2}
              value={generalInfo ? generalInfo.completed : 0} // Giá trị 100%
            />
            {/* Nội dung trong tâm */}
            <Box
              sx={{
                position: 'absolute', // Canh giữa
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
              }}
            >
              <Typography variant="h4" >
                {generalInfo ? Math.round(generalInfo.completed) : 0}%
              </Typography>
              <Typography variant="subtitle1" >
                {generalInfo && generalInfo.completed === 100 ? "Completed" : "In progress"}
              </Typography>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Paper
            elevation={3}
            sx={{
              p: 3,
              height: 300, // Chiều cao bằng với box bên trái
            }}
          >
            <Typography
              variant="h6"
              gutterBottom
              sx={{
                textAlign: 'center', // Căn giữa
                marginBottom: 2, // Khoảng cách bên dưới
              }}
            >
              Purchase order item
            </Typography>
            <Typography>
              <b>Product:</b>
              <br />
              {generalInfo?.productName}
            </Typography>
            <Typography>
              <b>Warehouse:</b>
              <br />
              {generalInfo?.warehouseName}
            </Typography>
            <Typography>
              <b>Quantity: </b>
              {generalInfo?.quantity}
            </Typography>
            {remainingQuantity > 0 && (
              <Typography>
                <b>Qty remaining: </b>
                {remainingQuantity}
              </Typography>
            )
            }
            <Typography>
              <b>Unit of measure: </b>
              {generalInfo?.uom}
            </Typography>
          </Paper>
        </Grid>


      </Grid>

      {generalInfo && generalInfo.completed < 100 &&
        <Box sx={{ mt: 4 }}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              New receipt item
            </Typography>
            <Grid container spacing={3}>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel>Bay Code</InputLabel>
                  <Select
                    value={bayCode}
                    onChange={(e) => setBayCode(e.target.value)}
                    label="Bay Code"
                  >
                    {bayOptions.map((bay) => (
                      <MenuItem key={bay.bayId} value={bay.bayId}>
                        {bay.code}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Lot ID"
                  value={lotId}
                  onChange={(e) => setLotId(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Import Price"
                  type="number"
                  value={importPrice}
                  onChange={(e) => setImportPrice(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Expired Date (Optional)"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  value={expiredDate}
                  onChange={(e) => setExpiredDate(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Quantity"
                  type="number"
                  inputProps={{ min: 1, max: generalInfo.quantity }}
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                />
              </Grid>

            </Grid>
            <Box sx={{ mt: 3, textAlign: 'right' }}>
              <Button variant="contained" color="primary" startIcon={<SaveIcon />} sx={{
                marginLeft: 'auto',
                backgroundColor: '#019160',
                color: '#fff',
                '&:hover': {
                  backgroundColor: '#2fbe8e',
                },
                '&:active': {
                  backgroundColor: '#01b075',
                },
              }}
                onClick={handleSubmit}>
                Save
              </Button>
            </Box>
          </Paper>
        </Box>
      }
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Receipt items
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold' }}>Bay Code</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Lot ID</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Import Price</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Expired Date</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {details.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} align="center">
                      No receipt items found.
                    </TableCell>
                  </TableRow>
                ) : (
                  details.map((detail, index) => (
                    <TableRow key={index}>
                      <TableCell>{detail.bayCode}</TableCell>
                      <TableCell>{detail.lotId}</TableCell>
                      <TableCell>{formatPrice(detail.importPrice)}</TableCell>
                      <TableCell>{detail.expiredDate ? formatDate(detail.expiredDate) : "No expiry date"}</TableCell>
                      <TableCell>{detail.quantity}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      </Box>
    </Box>
  );
};

export default ReceiptItem;
