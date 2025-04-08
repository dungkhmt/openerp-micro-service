import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  TextField,
  Typography,
  Grid,
  Button,
  Paper,
  IconButton,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../api";
import {debounce}  from '../../utils/utils';


const AddReceipt = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [date, setDate] = useState('');
  const [warehouse, setWarehouse] = useState([]);
  const [reason, setReason] = useState('');
  const [expectedReceiptDate, setExpectedReceiptDate] = useState('');
  const [createdBy] = useState('admin');
  const [requestDetails, setRequestDetails] = useState([
    { productId: '', quantity: '', warehouseId: '' },
  ]);
  const [productSuggestions, setProductSuggestions] = useState([]);
  const [activeSuggestionIndex, setActiveSuggestionIndex] = useState(null); // Lưu dòng hiển thị suggestion
  const [productNames, setProductNames] = useState({}); // Lưu tạm `productName`

  useEffect(() => {
    request("get", "/warehouses", (res) => {
      setWarehouse(res.data);
    }).then();
  }, []);

  const fetchProductSuggestions = useCallback(
    debounce((searchTerm) => {
      if (searchTerm) {
        request("get", `/products/names?search=${searchTerm}`, (res) => {
          setProductSuggestions(res.data);
        }).then();
      } else {
        setProductSuggestions([]);
      }
    }, 500), // Debounce delay là 500ms
    []
  );

  const handleProductSearch = (index, value) => {
    setProductNames((prev) => ({ ...prev, [index]: value }));
    setActiveSuggestionIndex(index); // Chỉ kích hoạt dòng hiện tại
    fetchProductSuggestions(value); // Sử dụng debounce để tìm kiếm
  };

  const handleProductSelect = (index, product) => {
    const updatedDetails = [...requestDetails];
    updatedDetails[index].productId = product.productId; // Chỉ lưu `productId`
    setRequestDetails(updatedDetails);

    setProductNames((prev) => ({ ...prev, [index]: product.name })); // Hiển thị `productName`
    setProductSuggestions([]); // Xóa danh sách gợi ý sau khi chọn
    setActiveSuggestionIndex(null); // Reset dòng hiển thị suggestion
  };

  const handleAddRequestDetail = () => {
    setRequestDetails([...requestDetails, { productId: '', quantity: '', warehouseId: '' }]);
  };

  const handleDeleteRequestDetail = (index) => {
    const updatedDetails = requestDetails.filter((_, i) => i !== index);
    setRequestDetails(updatedDetails);
    setProductNames((prev) => {
      const newNames = { ...prev };
      delete newNames[index];
      return newNames;
    });
    if (activeSuggestionIndex === index) {
      setActiveSuggestionIndex(null); // Tắt suggestion nếu xóa dòng hiện tại
    }
  };

  const handleRequestDetailChange = (index, field, value) => {
    const updatedDetails = [...requestDetails];
    updatedDetails[index][field] = value;
    setRequestDetails(updatedDetails);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const receiptData = {
      receiptName: name,
      description,
      receiptDate: date,
      warehouseId: null,
      createdReason: reason,
      expectedReceiptDate,
      createdBy,
      receiptItemRequests: requestDetails,
    };
    // console.log(receiptData);
    const requestUrl = "/receipts";
    request("post", requestUrl, (res) => {
      if (res.status === 200) {
        navigate(`/purchase-staff/receipts`);
      } else {
        alert("Error occcured !");
      }
    }, {}, receiptData);
  };

  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/purchase-staff/receipts')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          Add New Receipt
        </Typography>
        <Button
          variant="contained"
          color="primary"
          sx={{
            marginLeft: 'auto',
            backgroundColor: 'black',
            color: 'white',
            '&:hover': {
              backgroundColor: 'black',
              opacity: 0.75,
            }
          }}
          onClick={handleSubmit}
        >
          Save Receipt
        </Button>
      </Box>
      <Grid container spacing={2}>
        {/* General Information */}
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              General Information
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Receipt Name"
                  placeholder="Enter receipt name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Description"
                  placeholder="Enter description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Date"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Reason"
                  placeholder="Enter created reason"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Expected Receipt Date"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  value={expectedReceiptDate}
                  onChange={(e) => setExpectedReceiptDate(e.target.value)}
                />
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" gutterBottom sx={{ flexGrow: 1 }}>
                Receipt Detail
              </Typography>
              <IconButton color="primary" onClick={handleAddRequestDetail}>
                <AddIcon />
              </IconButton>
            </Box>
            {requestDetails.map((item, index) => (
              <Grid container spacing={2} key={index} className='mb-4'>
                <Grid item xs={12} sm={5}>
                  <TextField
                    fullWidth
                    label="Product"
                    placeholder="Search product"
                    value={productNames[index] || ''}
                    onChange={(e) => handleProductSearch(index, e.target.value)}
                  />
                  {activeSuggestionIndex === index && productSuggestions.length > 0 && (
                    <Box>
                      {productSuggestions.map((product) => (
                        <MenuItem
                          key={product.productId}
                          onClick={() => handleProductSelect(index, product)}
                        >
                          {product.name}
                        </MenuItem>
                      ))}
                    </Box>
                  )}
                </Grid>
                <Grid item xs={12} sm={3}>
                  <TextField
                    fullWidth
                    label="Quantity"
                    placeholder="Enter quantity"
                    type="number"
                    value={item.quantity}
                    onChange={(e) => handleRequestDetailChange(index, 'quantity', e.target.value)}
                  />
                </Grid>
                <Grid item xs={12} sm={3}>
                  <FormControl fullWidth>
                    <InputLabel id={`warehouse-label-${index}`}>Warehouse</InputLabel>
                    <Select
                      labelId={`warehouse-label-${index}`}
                      value={item.warehouseId}
                      onChange={(e) => handleRequestDetailChange(index, 'warehouseId', e.target.value)}
                      label="Warehouse"
                    >
                      {warehouse.map((wh) => (
                        <MenuItem key={wh.warehouseId} value={wh.warehouseId}>
                          {wh.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} sm={1}>
                  <IconButton color="secondary" onClick={() => handleDeleteRequestDetail(index)}>
                    <RemoveIcon />
                  </IconButton>
                </Grid>
              </Grid>
            ))}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AddReceipt;
