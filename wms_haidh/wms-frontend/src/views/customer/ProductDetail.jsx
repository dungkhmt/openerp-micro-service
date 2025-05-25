import React, { useState, useEffect } from "react";
import { Card, IconButton, TextField, Button, Typography, Grid, useTheme } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import AddShoppingCartIcon from "@mui/icons-material/AddShoppingCart";
import { toast, Toaster } from "react-hot-toast";
import { formatPrice } from "../../utils/utils";
import BreadcrumbsCustom from "../../components/BreadcrumbsCustom";
import { request } from "../../api";
import { useParams } from "react-router-dom";

const QuantityControl = ({ quantity, onIncrease, onDecrease, onChange, onBlur }) => (
    <div className="flex items-center space-x-2">
        <IconButton
            disableRipple
            onClick={onDecrease}
            size="small"
            sx={{
                backgroundColor: "rgb(241, 245, 249)",
                borderRadius: "4px",
                "&:hover": { backgroundColor: "rgb(226, 232, 240)" },
            }}
        >
            <RemoveIcon />
        </IconButton>
        <TextField
            value={quantity}
            onChange={onChange}
            onBlur={onBlur}
            inputProps={{ style: { textAlign: "center", width: "50px", padding: "5px" } }}
            variant="outlined"
            size="small"
        />
        <IconButton
            disableRipple
            onClick={onIncrease}
            size="small"
            sx={{
                backgroundColor: "rgb(241, 245, 249)",
                borderRadius: "4px",
                "&:hover": { backgroundColor: "rgb(226, 232, 240)" },
            }}
        >
            <AddIcon />
        </IconButton>
    </div>
);

const ProductDetail = () => {

    const { id } = useParams();
    const theme = useTheme();
    const [product, setProduct] = useState({});
    const [quantity, setQuantity] = useState(1);

    useEffect(() => {
        if (id) {
            request("get", `/products/public/${id}`, (res) => {
                setProduct(res.data);
            }, {});
        }
    }, [id]);

    const handleIncrease = () => {
        if (quantity < 999) setQuantity(prev => prev + 1);
    };

    const handleDecrease = () => setQuantity(quantity > 1 ? quantity - 1 : 1);

    const handleAddToCart = () => {
        const cartItems = JSON.parse(sessionStorage.getItem("cart")) || [];
        const existingItemIndex = cartItems.findIndex(cartItem => cartItem.productId === id);
        const existingQuantity = existingItemIndex !== -1 ? cartItems[existingItemIndex].quantity : 0;

        const totalQuantity = existingQuantity + quantity;

        request(
            "post",
            "/product-warehouses/check-inventory",
            (res) => {
                if (!res.data) {
                    toast.error("Not enough stock for this product!");
                    return;
                }

                // Nếu còn hàng, tiếp tục thêm vào cart
                if (existingItemIndex !== -1) {
                    cartItems[existingItemIndex].quantity = totalQuantity;
                } else {
                    cartItems.push({
                        ...product,
                        quantity,
                    });
                }

                sessionStorage.setItem("cart", JSON.stringify(cartItems));
                toast.success("Product added to cart");
            }, {},
            {
                productId: id,
                quantity: totalQuantity,
            }
        );
    };



    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Products", link: "/customer/products" },
        { label: `${product.name}`, link: `/customer/products/${product.productId}` }
    ];

    return (
        <div style={{ padding: "16px" }}>
            <Toaster />
            <BreadcrumbsCustom paths={breadcrumbPaths} />
            <Card sx={{ p: 4, maxWidth: '900px', margin: 'auto' }}>
                <Grid container spacing={4}>
                    <Grid item xs={12} md={5}>
                        <img src={product.imageUrl} alt={product.name} style={{ width: '100%', borderRadius: '8px' }} />
                        <div style={{ marginTop: '16px' }}>
                            <Typography variant="body1" sx={{ fontWeight: 'bold', marginBottom: '10px' }}>Specifications:</Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        Height: {
                                            product.height >= 100
                                                ? `${(product.height / 100).toFixed(2)} m`
                                                : `${product.height} cm`
                                        }
                                    </Typography>
                                </Grid>

                                <Grid item xs={6}>
                                    <Typography variant="body2">
                                        Weight: {
                                            product.weight < 1
                                                ? `${(product.weight * 1000).toFixed(0)} g`
                                                : `${product.weight} kg`
                                        }
                                    </Typography>
                                </Grid>

                                <Grid item xs={6}><Typography variant="body2">Unit: {product.uom}</Typography></Grid>
                            </Grid>
                        </div>
                    </Grid>
                    <Grid item xs={12} md={7}>
                        <Typography variant="h5" sx={{ fontWeight: 'bold' }}>{product.name}</Typography>
                        <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>Code: {product.code}</Typography>
                        <Typography
                            variant="body2"
                            color={product.quantity === 0 ? 'error' : theme.palette.success.main}
                            sx={{
                                mt: 1,
                                fontSize: '1rem',
                                fontWeight: 600,
                            }}
                        >
                            {product.quantity === 0 ? 'Sold out' : `Available: ${product.quantity}`}
                        </Typography>


                        <Typography variant="h6" sx={{ mt: 2, color: "black" }}>{product.price && formatPrice(product.price)}</Typography>
                        <Typography variant="body1" sx={{ mt: 2 }}>{product.description}</Typography>
                        <div style={{ marginTop: '16px' }}>
                            <QuantityControl
                                quantity={quantity}
                                onIncrease={handleIncrease}
                                onDecrease={handleDecrease}
                                onChange={(e) => setQuantity(Math.min(999, Math.max(1, Number(e.target.value.replace(/\D/g, '')) || 1)))}
                                onBlur={() => setQuantity(quantity || 1)}
                            />
                        </div>
                        <Button
                            variant="contained"
                            color="primary"
                            startIcon={<AddShoppingCartIcon />}
                            onClick={handleAddToCart}
                            disabled={product.quantity === 0}
                            sx={{
                                mt: 3,
                                width: '33%',
                                backgroundColor: '#6fd649',
                                color: '#fff',
                                '&:hover': {
                                    backgroundColor: '#4caf50', // hover
                                },
                                '&:active': {
                                    backgroundColor: '#3e8e41', // nhấn
                                }
                            }}
                        >
                            Add to cart
                        </Button>

                    </Grid>
                </Grid>
            </Card>
        </div>
    );
};

export default ProductDetail;