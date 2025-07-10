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
import SaveIcon from '@mui/icons-material/Save';
import { toast, Toaster } from "react-hot-toast";

const AddReceipt = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [warehouseList, setWarehouseList] = useState([]);
  const [supplierList, setSupplierList] = useState([]);
  const [warehouseId, setWarehouseId] = useState('');
  const [supplierId, setSupplierId] = useState('');
  const [expectedReceiptDate, setExpectedReceiptDate] = useState('');
  const [requestDetails, setRequestDetails] = useState([{ productId: '', quantity: '' }]);
  const [productSuggestions, setProductSuggestions] = useState([]);
  const [productNames, setProductNames] = useState({});
  const [activeSuggestionIndex, setActiveSuggestionIndex] = useState(null);
  const [productUOMs, setProductUOMs] = useState({});


  useEffect(() => {
    request("get", "/warehouses", (res) => {
      setWarehouseList(res.data);
    }).then();
    request("get", "/suppliers", (res) => {
      setSupplierList(res.data);
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
    setProductUOMs((prev) => ({ ...prev, [index]: product.uom }));
    setProductSuggestions([]);
    setActiveSuggestionIndex(null);
  };


  const handleAddRequestDetail = () => {
    const allValid = requestDetails.every((detail, i) => isDetailValid(detail, i));
    if (!allValid) return;

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

  const isDetailValid = (detail, index) => {
    const productName = productNames[index];
    if (!detail.productId || !productName) {
      toast.error(`Product at row ${index + 1} is missing or invalid.`);
      return false;
    }
    if (!detail.quantity || Number(detail.quantity) <= 0) {
      toast.error(`Quantity at row ${index + 1} must be greater than 0.`);
      return false;
    }
    return true;
  };


  const handleSubmit = (e) => {
    e.preventDefault();

    if (!name || !warehouseId || !supplierId || !expectedReceiptDate) {
      toast.error("Please fill all required general fields.");
      return;
    }

    const allValid = requestDetails.every((detail, i) => isDetailValid(detail, i));
    if (!allValid) return;

    const updatedRequestDetails = requestDetails.map(item => ({
      ...item,
      warehouseId: warehouseId,
    }));

    const receiptData = {
      receiptName: name,
      description,
      warehouseId,
      supplierId,
      expectedReceiptDate,
      receiptItemRequests: updatedRequestDetails,
    };

    request(
      "post",
      "/receipts",
      (res) => {
        if (res.status === 200) {
          alert("Receipt saved successfully!");
          navigate(`/purchase-staff/receipts`);
        }
      },
      {
        onError: (e) => {
          toast.error(e?.response?.data || "Error occured!");
        }
      },
      receiptData
    );
  };



  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Toaster />
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton
          onClick={() => navigate('/purchase-staff/receipts')}
          sx={{ color: 'grey.700', mr: 1 }}
        >
          <ArrowBackIcon />
        </IconButton>

        <Typography variant="h6" sx={{ ml: 1 }}>
          New Purchase Order
        </Typography>
        <Button
          variant="contained"
          startIcon={<SaveIcon />}
          sx={{
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
          onClick={handleSubmit}
        >
          Save
        </Button>

      </Box>

      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>General information</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel id="supplier-label">Supplier</InputLabel>
                  <Select
                    labelId="supplier-label"
                    value={supplierId}
                    onChange={(e) => setSupplierId(e.target.value)}
                    label="Supplier"
                  >
                    {supplierList.map((supplier) => (
                      <MenuItem key={supplier.supplierId} value={supplier.supplierId}>
                        {supplier.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
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

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Receipt Name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="date"
                  label="Expected Date"
                  InputLabelProps={{ shrink: true }}
                  value={expectedReceiptDate}
                  onChange={(e) => setExpectedReceiptDate(e.target.value)}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
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
                    <Grid item xs={6}>
                      <TextField
                        fullWidth
                        label="Product Name"
                        value={productNames[index] || ''}
                        onChange={(e) => handleProductSearch(index, e.target.value)}
                      />
                      {activeSuggestionIndex === index && productSuggestions.length > 0 && (
                        <List sx={{ position: 'absolute', bgcolor: 'white', zIndex: 1, boxShadow: 2 }}>
                          {productSuggestions.map((suggestion, i) => (
                            <ListItemButton
                              key={i}
                              onClick={() => handleProductSelect(index, suggestion)}
                              sx={{ '&:hover': { backgroundColor: 'grey.100' } }}
                            >
                              <ListItemText primary={suggestion.name} />
                            </ListItemButton>
                          ))}
                        </List>
                      )}
                    </Grid>
                    <Grid item xs={2}>
                      <TextField
                        fullWidth
                        label="Quantity"
                        type="number"
                        value={detail.quantity}
                        onChange={(e) => handleRequestDetailChange(index, 'quantity', e.target.value)}
                      />
                    </Grid>
                    <Grid item xs={2}>
                      <TextField
                        fullWidth
                        label="UOM"
                        value={productUOMs[index] || ''}
                        InputProps={{ readOnly: true }}
                      />
                    </Grid>
                    <Grid item xs={2}>
                      <IconButton
                        onClick={() => handleDeleteRequestDetail(index)}
                        sx={{
                          color: 'grey.600',
                          '&:hover': { color: 'error.main' },
                        }}
                      >
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
                mt: 2,
                border: '1px solid #019160',
                color: '#019160',
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                transition: 'background-color 0.2s ease, color 0.2s ease',
                '&:hover': {
                  backgroundColor: '#019160',
                  color: '#ffffff',
                  borderColor: '#019160',
                  '& .add-icon': {
                    color: '#ffffff',
                  },
                },
                '&:active': {
                  backgroundColor: '#01b075',
                  borderColor: '#01b075',
                },
              }}
            >
              <AddIcon className="add-icon" sx={{ color: 'inherit' }} />
              Add Product
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );

};

export default AddReceipt;
