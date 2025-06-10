import React, { useState, useEffect } from 'react';
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
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../../api";
import SaveIcon from '@mui/icons-material/Save';
import { toast, Toaster } from "react-hot-toast";

const ProductForm = () => {

  const navigate = useNavigate();
  const { id } = useParams();
  const [image, setImage] = useState(null);
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [code, setCode] = useState('');
  const [weight, setWeight] = useState('');
  const [height, setHeight] = useState('');
  const [description, setDescription] = useState('');
  const [uom, setUom] = useState('');

  const [categories, setCategories] = useState([]);

  useEffect(() => {
    request("get", "/categories", (res) => {
      setCategories(res.data);
    }).then();
  }, [])

  useEffect(() => {
    if (id) {
      request("get", `/products/${id}`, (res) => {
        const product = res.data;
        setName(product.name);
        setCategory(product.categoryId);
        setCode(product.code);
        setWeight(product.weight);
        setHeight(product.height);
        setDescription(product.description);
        setUom(product.uom);
        setImage(product.imageUrl);
      }, {});
    }
  }, [id]);


  const handleCategoryChange = (e) => {
    setCategory(e.target.value); // Lưu categoryId khi người dùng thay đổi lựa chọn
  };

  const handleImageUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      setImage(file);
    }
  };


  const validateForm = () => {
    if (!name.trim()) {
      toast.error("Product name is required");
      return false;
    }
    if (!category) {
      toast.error("Product category is required");
      return false;
    }
    if (!code.trim()) {
      toast.error("Product code is required");
      return false;
    }
    if (!weight || parseFloat(weight) <= 0) {
      toast.error("Weight must be a positive number");
      return false;
    }
    if (!height || parseFloat(height) <= 0) {
      toast.error("Height must be a positive number");
      return false;
    }
    if (!uom.trim()) {
      toast.error("Unit of measure is required");
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    const formData = new FormData();

    const productData = JSON.stringify({
      productId: id,
      name,
      categoryId: category,
      code,
      weight,
      height,
      description,
      uom
    });
    formData.append("productData", productData);

    if (image instanceof File) {
      formData.append("image", image);
    }

    const requestUrl = id ? "/products/update" : "/products";

    request("post", requestUrl, (res) => {
      if (res.status === 200) {
        alert("Product saved successfully!");
        navigate(`/warehouse-manager/product`);
      } 
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      },
    }, formData);
  };


  return (
    <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
      <Toaster />
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <IconButton
          onClick={() => navigate('/warehouse-manager/product')}
          sx={{ color: 'grey.700', mr: 1 }}
        >
          <ArrowBackIcon />
        </IconButton>

        <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
          {id ? 'Update Product' : 'Add New Product'}
        </Typography>
        <Button
          variant="contained"
          color="primary"
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
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Product image
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
                // If image is a file, preview it
                image instanceof File ? (
                  <img
                    src={URL.createObjectURL(image)} // Create a preview URL for the selected file
                    alt="Product Preview"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "contain",
                    }}
                  />
                ) : (
                  // If it's an existing image URL (from the product data), display that
                  <img
                    src={image}
                    alt="Product Preview"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "contain", // Đảm bảo ảnh vừa vặn với chiều cao và chiều rộng
                    }}
                  />

                )
              ) : (
                <Typography variant="body2" sx={{ color: "grey" }}>
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
              >
                Upload image
              </Button>


            </label>
          </Paper>
        </Grid>

        <Grid item xs={12} md={8}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              General information
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
                    onChange={handleCategoryChange}
                    label="Product Category"
                  >
                    {categories.map((cat) => (
                      <MenuItem key={cat.categoryId} value={cat.categoryId}>
                        {cat.name}
                      </MenuItem>
                    ))}
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
                  label="Weight (kg)"
                  placeholder="Enter weight"
                  type="number"
                  value={weight}
                  onChange={(e) => setWeight(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Height (cm)"
                  placeholder="Enter height"
                  type="number"
                  value={height}
                  onChange={(e) => setHeight(e.target.value)}
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

            </Grid>
          </Paper>

        </Grid>
      </Grid>
    </Box>
  );
};

export default ProductForm;