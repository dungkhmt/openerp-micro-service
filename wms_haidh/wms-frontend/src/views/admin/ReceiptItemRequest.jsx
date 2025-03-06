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
  TextField,
  Radio,
  RadioGroup,
  FormControlLabel,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { CircularProgress } from "@nextui-org/react";
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../api";
import { formatDate, formatPrice } from '../../utils/utils';

const ReceiptPage = () => {
  const navigate = useNavigate();
  const { id1, id2 } = useParams();
  const [generalInfo, setGeneralInfo] = useState(null);
  const [bayOptions, setBayOptions] = useState([]);
  const [quantity, setQuantity] = useState('');
  const [bayCode, setBayCode] = useState('');
  const [importPrice, setImportPrice] = useState('');
  const [expiredDate, setExpiredDate] = useState('');
  const [lotId, setLotId] = useState('');
  const [billOption, setBillOption] = useState('new');
  const [billName, setBillName] = useState('');
  const [existingBill, setExistingBill] = useState('');
  const [existingBills, setExistingBills] = useState([]);
  const [details, setDetails] = useState([]);

  useEffect(() => {
    request("get", `/admin/receipt/general-info/${id2}`, (res) => {
      setGeneralInfo(res.data);
    });

    request("get", `/admin/receipt/bay/${id2}`, (res) => {
      setBayOptions(res.data);
    });

    request("get", `/admin/receipt/detail-info/${id2}`, (res) => {
      setDetails(res.data);
    });

    request("get", `/admin/receipt/bill/${id2}`, (res) => {
      setExistingBills(res.data);
    });
  }, [id2]);

  const handleBillOptionChange = (event) => {
    setBillOption(event.target.value);
  };

  const handleSubmit = () => {
    if (billOption === 'new') {
      const bilInfo = {
        receiptBillId: billName,
        description: "Create new bill",
        createdBy: "admin",
        receiptItemRequestId: id2
      };
      request("post", `/admin/receipt/bill/create`, (res) => {
        if (res.status !== 200) {
          alert("Error occcured while creating bill!");
        }
      }, {}, bilInfo);
    };
    const payload = {
      quantity,
      bayId: bayCode,
      lotId,
      importPrice,
      expiredDate,
      receiptItemRequestId: id2,
      receiptBillId: billOption === 'new' ? billName : existingBill
    };
    console.log(payload);
    request("post", `/admin/receipt/receipt-item/create`, (res) => {
      if (res.status === 200) {
        request("get", `/admin/receipt/general-info/${id2}`, (res) => {
          setGeneralInfo(res.data);
        });
        request("get", `/admin/receipt/detail-info/${id2}`, (res) => {
          setDetails(res.data);
        });
        request("get", `/admin/receipt/bill/${id2}`, (res) => {
          setExistingBills(res.data);
        });
        alert("Add new receipt item successfully !")
      } else {
        alert("Error occcured while creating receipt item!");
      }
    }, {}, payload);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton color="primary" onClick={() => navigate(`/admin/receipts/${id1}`)} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 2 }}>
          Receipt Management
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
                {generalInfo ? generalInfo.completed : 0}%
              </Typography>
              <Typography variant="subtitle1" >
                {generalInfo && generalInfo.completed === 100 ? "Completed" : "In progress"}
              </Typography>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={5}>
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
              Receipt request
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
              <b>Quantity:</b>
              <br />
              {generalInfo?.quantity}
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
                <TextField
                  fullWidth
                  label="Quantity"
                  type="number"
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                />
              </Grid>
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
                  label="Import Price"
                  type="number"
                  value={importPrice}
                  onChange={(e) => setImportPrice(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Expired Date"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  value={expiredDate}
                  onChange={(e) => setExpiredDate(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Lot ID"
                  value={lotId}
                  onChange={(e) => setLotId(e.target.value)}
                />
              </Grid>
              <Grid item xs={12}>
                <FormControl component="fieldset">
                  <RadioGroup
                    row
                    value={billOption}
                    onChange={handleBillOptionChange}
                  >
                    <FormControlLabel
                      value="new"
                      control={<Radio />}
                      label="Create New Bill"
                    />
                    <FormControlLabel
                      value="existing"
                      control={<Radio />}
                      label="Add to Existing Bill"
                      disabled={existingBills.length === 0}
                    />
                  </RadioGroup>
                </FormControl>
              </Grid>
              {billOption === 'new' && (
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Bill ID"
                    value={billName}
                    onChange={(e) => setBillName(e.target.value)}
                  />
                </Grid>
              )}
              {billOption === 'existing' && (
                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Existing Bill</InputLabel>
                    <Select
                      value={existingBill}
                      onChange={(e) => setExistingBill(e.target.value)}
                      label="Existing bill"
                    >
                      {existingBills.map((bill) => (
                        <MenuItem key={bill} value={bill}>
                          {bill}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
              )}
            </Grid>
            <Box sx={{ mt: 3, textAlign: 'right' }}>
              <Button variant="contained" color="primary" onClick={handleSubmit}>
                Submit
              </Button>
            </Box>
          </Paper>
        </Box>
      }
      <Box sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Detail Information
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Lot ID</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Bay Code</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Import Price</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Expired Date</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Receipt Bill ID</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {details.map((detail, index) => (
                  <TableRow key={index}>
                    <TableCell>{detail.quantity}</TableCell>
                    <TableCell>{detail.lotId}</TableCell>
                    <TableCell>{detail.bayCode}</TableCell>
                    <TableCell>{formatPrice(detail.importPrice)}</TableCell>
                    <TableCell>{formatDate(detail.expiredDate)}</TableCell>
                    <TableCell>{detail.receiptBillId}</TableCell>
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

export default ReceiptPage;
