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
  List,
  ListItem,
  ListItemButton,
  ListItemText
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import { request } from "../../api";
import { debounce } from '../../utils/utils';

const AddReceipt = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [date, setDate] = useState('');
  const [warehouseList, setWarehouseList] = useState([]);
  const [warehouseId, setWarehouseId] = useState('');
  const [reason, setReason] = useState('');
  const [expectedReceiptDate, setExpectedReceiptDate] = useState('');
  const [createdBy] = useState('hoanglotar2000');
  const [requestDetails, setRequestDetails] = useState([{ productId: '', quantity: '' }]);
  const [productSuggestions, setProductSuggestions] = useState([]);
  const [productNames, setProductNames] = useState({});
  const [activeSuggestionIndex, setActiveSuggestionIndex] = useState(null);

  useEffect(() => {
    request("get", "/warehouses", (res) => {
      setWarehouseList(res.data);
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
    }, 500),
    []
  );

  const handleProductSearch = (index, value) => {
    setProductNames((prev) => ({ ...prev, [index]: value }));
    setActiveSuggestionIndex(index);
    fetchProductSuggestions(value);
  };

  const handleProductSelect = (index, product) => {
    const updatedDetails = [...requestDetails];
    updatedDetails[index].productId = product.productId;
    setRequestDetails(updatedDetails);

    setProductNames((prev) => ({ ...prev, [index]: product.name }));
    setProductSuggestions([]);
    setActiveSuggestionIndex(null);
  };

  const handleAddRequestDetail = () => {
    setRequestDetails([...requestDetails, { productId: '', quantity: '' }]);
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
      setActiveSuggestionIndex(null);
    }
  };

  const handleRequestDetailChange = (index, field, value) => {
    const updatedDetails = [...requestDetails];
    updatedDetails[index][field] = value;
    setRequestDetails(updatedDetails);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const updatedRequestDetails = requestDetails.map(item => ({
      ...item,
      warehouseId: warehouseId, 
    }));

    const receiptData = {
      receiptName: name,
      description,
      receiptDate: date,
      warehouseId,
      createdReason: reason,
      expectedReceiptDate,
      createdBy,
      receiptItemRequests: updatedRequestDetails, 
    };

    request(
      "post",
      "/receipts",
      (res) => {
        if (res.status === 200) {
          navigate(`/purchase-staff/receipts`);
        } else {
          alert("Error occurred!");
        }
      },
      {},
      receiptData
    );
  };


  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton onClick={() => navigate('/purchase-staff/receipts')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h6" sx={{ ml: 1 }}>
          Add New Receipt
        </Typography>
        <Button
          variant="contained"
          sx={{
            marginLeft: 'auto',
            backgroundColor: 'black',
            color: 'white',
            '&:hover': { backgroundColor: 'black', opacity: 0.75 }
          }}
          onClick={handleSubmit}
        >
          Save Receipt
        </Button>
      </Box>

      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>General Information</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Receipt Name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel id="warehouse-label">Warehouse</InputLabel>
                  <Select
                    labelId="warehouse-label"
                    value={warehouseId}
                    onChange={(e) => setWarehouseId(e.target.value)}
                    label="Warehouse"
                  >
                    {warehouseList.map((warehouse) => (
                      <MenuItem key={warehouse.warehouseId} value={warehouse.warehouseId}>
                        {warehouse.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Reason"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="date"
                  label="Receipt Date"
                  InputLabelProps={{ shrink: true }}
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="date"
                  label="Expected Receipt Date"
                  InputLabelProps={{ shrink: true }}
                  value={expectedReceiptDate}
                  onChange={(e) => setExpectedReceiptDate(e.target.value)}
                />
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3, mt: 2 }}>
            <Typography variant="h6" gutterBottom>Products</Typography>
            <List>
              {requestDetails.map((detail, index) => (
                <ListItem key={index} disablePadding sx={{ mb: 2 }}>
                  <Grid container spacing={2} alignItems="center">
                    <Grid item xs={8}>
                      <TextField
                        fullWidth
                        label="Product Name"
                        value={productNames[index] || ''}
                        onChange={(e) => handleProductSearch(index, e.target.value)}
                      />
                      {activeSuggestionIndex === index && productSuggestions.length > 0 && (
                        <List sx={{ position: 'absolute', bgcolor: 'white', zIndex: 1 }}>
                          {productSuggestions.map((suggestion, i) => (
                            <ListItemButton key={i} onClick={() => handleProductSelect(index, suggestion)}>
                              <ListItemText primary={suggestion.name} />
                            </ListItemButton>
                          ))}
                        </List>
                      )}
                    </Grid>
                    <Grid item xs={3}>
                      <TextField
                        fullWidth
                        label="Quantity"
                        type="number"
                        value={detail.quantity}
                        onChange={(e) => handleRequestDetailChange(index, 'quantity', e.target.value)}
                      />
                    </Grid>
                    <Grid item xs={1}>
                      <IconButton onClick={() => handleDeleteRequestDetail(index)}>
                        <DeleteIcon />
                      </IconButton>
                    </Grid>
                  </Grid>
                </ListItem>
              ))}
            </List>
            <Button
              onClick={handleAddRequestDetail}
              variant="outlined"
              sx={{
                mt: 1,
                color: 'black',
                borderColor: 'black',
                border: '1px solid',
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                '&:hover': {
                  backgroundColor: 'black',
                  color: 'white',
                  borderColor: 'black',
                  '& .add-icon': {
                    color: 'white',
                  }
                }
              }}
            >
              <AddIcon className="add-icon" sx={{ color: 'black' }} />
              Add Product
            </Button>

          </Paper>
        </Grid>
      </Grid>
    </Box>
  );

};

export default AddReceipt;
