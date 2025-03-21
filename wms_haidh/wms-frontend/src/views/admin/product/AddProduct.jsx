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

const ProductForm = () => {

  const navigate = useNavigate();
  const { id } = useParams();
  const [image, setImage] = useState(null);
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [code, setCode] = useState('');
  const [weight, setWeight] = useState('');
  const [height, setHeight] = useState('');
  const [area, setArea] = useState('');
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
      const formData = new FormData();

      request("get", `/products/${id}`, (res) => {
        const product = res.data;
        setName(product.name);
        setCategory(product.categoryId);
        setCode(product.code);
        setWeight(product.weight);
        setHeight(product.height);
        setArea(product.area);
        setDescription(product.description);
        setUom(product.uom);
        setImage(product.imageUrl);


      }, {}, formData); // Gửi FormData trong body của yêu cầu
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


  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();

    // Serialize product data as a JSON string and append it to the form data
    const productData = JSON.stringify({
      productId: id, // Only include the id for update, omit it for create
      name,
      categoryId: category,
      code,
      weight,
      height,
      area,
      description,
      uom
    });
    formData.append("productData", productData);

    // If an image is selected, append it to the FormData
    if (image instanceof File) {
      formData.append("image", image); // This sends the binary data of the image
    }

    const requestUrl = id ? "/products/update" : "/products";

    // Make the request
    request("post", requestUrl, (res) => {
      if (res.status === 200) {
        navigate(`/admin/product`); // Redirect after success
      }
    }, {}, formData);
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

        </Grid>
      </Grid>
    </Box>
  );
};

export default ProductForm;