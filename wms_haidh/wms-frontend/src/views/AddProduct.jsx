import React, { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  Typography,
  Grid,
  Button,
  Paper,
  Divider,
  IconButton,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
} from '@mui/material';
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { useNavigate, useParams } from 'react-router-dom';

const ProductForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [image, setImage] = useState(null);
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [inventory, setInventory] = useState([{ warehouse: '', quantity: '' }]);
  const [code, setCode] = useState('');
  const [weight, setWeight] = useState('');
  const [height, setHeight] = useState('');
  const [area, setArea] = useState('');
  const [description, setDescription] = useState('');
  const [uom, setUom] = useState('');

  const mockProductData = {
    id: '1',
    image: 'https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2023/1/8/1135972/HD13.JPG',
    name: 'Tủ lạnh Samsung',
    category: 'Tủ lạnh',
    inventory: [{ warehouse: 'Warehouse A', quantity: 10 }],
    code: 'P123',
    weight: '20',
    height: '30',
    area: '50',
    description: 'A great product for your home.',
    uom: 'kg',
  };

  useEffect(() => {
    if (id) {
      const product = mockProductData;
      setImage(product.image);
      setName(product.name);
      setCategory(product.category);
      setInventory(product.inventory);
      setCode(product.code);
      setWeight(product.weight);
      setHeight(product.height);
      setArea(product.area);
      setDescription(product.description);
      setUom(product.uom);
    }
  }, [id]);

  const handleImageUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleAddInventory = () => {
    setInventory([...inventory, { warehouse: '', quantity: '' }]);
  };

  const handleDeleteInventory = (index) => {
    const newInventory = inventory.filter((_, i) => i !== index);
    setInventory(newInventory);
  };

  const handleChangeInventory = (index, field, value) => {
    const newInventory = [...inventory];
    newInventory[index][field] = value;
    setInventory(newInventory);
  };

  const handleSubmit = () => {
    console.log({
      image,
      name,
      category,
      inventory,
      code,
      weight,
      height,
      area,
      description,
      uom,
    });
  };

  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton color="primary" onClick={() => navigate('/admin/product')} sx={{ color: 'black' }}>
          <ArrowBackIcon />
        </IconButton>

        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          {id ? 'Update Product' : 'Add New Product'}
        </Typography>
        <Button
          variant="contained"
          color="primary"
          sx={{
            marginLeft: 'auto',
            backgroundColor: 'black',
            color: 'white',
            '&:hover': {
              backgroundColor: 'black', // Giữ màu đen khi hover
              opacity: 0.75,            // Đặt độ mờ khi hover
            }
          }}
          onClick={handleSubmit}
        >
          {id ? 'Update Product' : 'Save Product'}
        </Button>



      </Box>

      <Grid container spacing={2}>
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Product Image
            </Typography>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                mb: 2,
                border: '1px dashed grey',
                borderRadius: '8px',
                p: 2,
                height: '200px',
                justifyContent: 'center',
                position: 'relative',
                overflow: 'hidden',
              }}
            >
              {image ? (
                <img
                  src={image}
                  alt="Product Preview"
                  style={{
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover'
                  }}
                />
              ) : (
                <Typography variant="body2" sx={{ color: 'grey' }}>
                  No image uploaded
                </Typography>
              )}
            </Box>
            <input
              accept="image/*"
              style={{ display: 'none' }}
              id="icon-button-file"
              type="file"
              onChange={handleImageUpload}
            />
            <label htmlFor="icon-button-file">
              <Button
                variant="contained"
                color="primary"
                component="span"
                startIcon={<PhotoCamera />}
                sx={{
                  width: '100%',
                  backgroundColor: 'black',
                  color: 'white',
                  '&:hover': {
                    backgroundColor: 'black', // Giữ màu đen khi hover
                    opacity: 0.75,            // Đặt độ mờ khi hover
                  }
                }}
              >
                Upload Image
              </Button>


            </label>
          </Paper>
        </Grid>

        <Grid item xs={12} md={8}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              General Information
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Product Name"
                  placeholder="Enter product name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel id="product-category-label">Product Category</InputLabel>
                  <Select
                    labelId="product-category-label"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    label="Product Category"
                  >
                    <MenuItem value="Tủ lạnh">Tủ lạnh</MenuItem>
                    <MenuItem value="Tivi">Tivi</MenuItem>
                    <MenuItem value="Quạt điều hòa">Quạt điều hòa</MenuItem>
                    <MenuItem value="Máy giặt">Máy giặt</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Product Code"
                  placeholder="Enter product code"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Weight"
                  placeholder="Enter weight"
                  type="number"
                  value={weight}
                  onChange={(e) => setWeight(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Height"
                  placeholder="Enter height"
                  type="number"
                  value={height}
                  onChange={(e) => setHeight(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Area"
                  placeholder="Enter area"
                  type="number"
                  value={area}
                  onChange={(e) => setArea(e.target.value)}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  placeholder="Enter product description"
                  multiline
                  rows={4}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Unit of Measure"
                  placeholder="Enter unit of measure"
                  value={uom}
                  onChange={(e) => setUom(e.target.value)}
                />
              </Grid>
            </Grid>
          </Paper>

          <Divider sx={{ my: 4 }} />

          <Paper elevation={3} sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" gutterBottom sx={{ flexGrow: 1 }}>
                Inventory Management
              </Typography>
              <IconButton color="primary" onClick={handleAddInventory}>
                <AddIcon />
              </IconButton>
            </Box>
            {inventory.map((item, index) => (
              <Grid container spacing={2} key={index} className="mb-4">
                <Grid item xs={12} sm={5}>
                  <TextField
                    fullWidth
                    label="Warehouse"
                    placeholder="Enter warehouse name"
                    value={item.warehouse}
                    onChange={(e) => handleChangeInventory(index, 'warehouse', e.target.value)}
                  />
                </Grid>
                <Grid item xs={12} sm={5}>
                  <TextField
                    fullWidth
                    label="Quantity"
                    placeholder="Enter quantity"
                    type="number"
                    value={item.quantity}
                    onChange={(e) => handleChangeInventory(index, 'quantity', e.target.value)}
                  />
                </Grid>
                <Grid item xs={12} sm={2} sx={{ display: 'flex', alignItems: 'flex-end' }}>
                  <IconButton color="secondary" onClick={() => handleDeleteInventory(index)}>
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

export default ProductForm;