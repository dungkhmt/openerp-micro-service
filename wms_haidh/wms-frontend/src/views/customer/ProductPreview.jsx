import React, { useState } from "react";
import IconButton from "@mui/material/IconButton";
import AddShoppingCartIcon from "@mui/icons-material/AddShoppingCart";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import { Link } from "react-router-dom";
import { toast } from "react-hot-toast";
import { Card } from "@mui/material";
import { TextField } from "@mui/material";
import Divider from "@mui/material/Divider";
import Button from "@mui/material/Button";
import { formatPrice } from "../../utils/utils";
import { request } from "../../api";

const ProductPreview = ({ item }) => {
    const [quantity, setQuantity] = useState(1);

    const handleIncrease = () => {
        if (quantity < 999) setQuantity(prev => prev + 1);
    };

    const handleDecrease = () => setQuantity(quantity > 1 ? quantity - 1 : 1);

    const handleAddToCart = () => {
        const cartItems = JSON.parse(sessionStorage.getItem("cart")) || [];
        const existingItemIndex = cartItems.findIndex(cartItem => cartItem.productId === item.productId);
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
                        ...item,
                        quantity,
                    });
                }

                sessionStorage.setItem("cart", JSON.stringify(cartItems));
                toast.success("Product added to cart");
            }, {},
            {
                productId: item.productId,
                quantity: totalQuantity,
            }
        );
    };




    const handleQuantityChange = (e) => {
        let value = e.target.value.replace(/\D/g, ""); // Chỉ giữ số
        if (value === "") {
            setQuantity("");
        } else {
            const num = parseInt(value, 10);
            if (num <= 999) {
                setQuantity(num);
            }
        }
    };

    const handleBlurQuantity = () => {
        if (!quantity) setQuantity(1);
    };

    return (
        <Card
            className="p-4 space-y-4 text-center w-50"
            sx={{
                transition: "box-shadow 0.3s ease-in-out",
                "&:hover": {
                    boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.15)"
                },
            }}
        >
            <div
                className="w-48 h-48 bg-slate-100 shrink-0 flex justify-center items-center relative rounded-md m-auto"
                style={{
                    transition: "transform 0.2s ease-in-out",
                }}
            >
                <img
                    src={item.imageUrl}
                    alt="poster"
                    className="w-full h-full object-contain"
                    style={{
                        transition: "transform 0.2s ease-in-out",
                    }}
                />
            </div>
            <Divider />
            <div className="space-y-2 w-full text-center">
                <p
                    className="font-semibold whitespace-nowrap overflow-hidden text-ellipsis mx-auto"
                    style={{ maxWidth: "200px" }}
                >
                    {item.name}
                </p>
                <p className={`text-md ${item.quantity === 0 ? 'text-red-500' : 'text-green-500'}`}>
                    {item.quantity === 0 ? 'Sold out' : ' Available : ' + item.quantity}
                </p>


            </div>

            <p className="shrink-0 font-medium">{formatPrice(item.price)}</p>
            <div className="flex items-center justify-center space-x-2">
                <IconButton
                    disableRipple
                    onClick={handleDecrease}
                    size="small"
                    sx={{
                        backgroundColor: "rgb(241, 245, 249)",
                        borderRadius: "4px",
                        "&:hover": {
                            backgroundColor: "rgb(226, 232, 240)",
                        },
                    }}
                >
                    <RemoveIcon />
                </IconButton>

                <TextField
                    value={quantity}
                    onChange={handleQuantityChange}
                    onBlur={handleBlurQuantity}
                    inputProps={{
                        style: { textAlign: "center", width: "50px", padding: "5px" },
                    }}
                    variant="outlined"
                    size="small"
                    sx={{
                        "& .MuiOutlinedInput-root": {
                            "& fieldset": { borderColor: "rgba(0, 0, 0, 0.3)" },
                            "&:hover fieldset": { borderColor: "black" },
                            "&.Mui-focused fieldset": { borderColor: "black" },
                        },
                    }}
                />

                <IconButton
                    disableRipple
                    onClick={handleIncrease}
                    size="small"
                    sx={{
                        backgroundColor: "rgb(241, 245, 249)",
                        borderRadius: "4px",
                        "&:hover": {
                            backgroundColor: "rgb(226, 232, 240)",
                        },
                    }}
                >
                    <AddIcon />
                </IconButton>
            </div>

            <Button
                variant="contained"
                color="primary"
                startIcon={<AddShoppingCartIcon />}
                onClick={handleAddToCart}
                disabled={item.quantity === 0}
                sx={{
                    marginLeft: 'auto',
                    backgroundColor: '#6fd649',
                    color: '#fff',
                    '&:hover': {
                        backgroundColor: '#4caf50', // màu hover
                    },
                    '&:active': {
                        backgroundColor: '#3e8e41', // màu khi nhấn
                    },
                }}
                fullWidth
            >
                Add to cart
            </Button>




            <Link to={`/customer/products/${item.productId}`} className="block">
                <Button
                    variant="outlined"
                    fullWidth
                    sx={{
                        backgroundColor: "white",
                        color: "black",
                        borderColor: "rgba(0, 0, 0, 0.5)",
                        "&:hover": {
                            backgroundColor: "#f0f0f0",
                            borderColor: "black",
                        },
                    }}
                >
                    View details
                </Button>
            </Link>
        </Card>
    );
};

export default ProductPreview;