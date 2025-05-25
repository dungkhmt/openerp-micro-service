import React, { useEffect, useState } from "react";
import { Card, IconButton, TextField, Button } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import { toast, Toaster } from "react-hot-toast";
import BreadcrumbsCustom from "../../components/BreadcrumbsCustom";
import { useNavigate } from "react-router-dom";
import { formatPrice } from "../../utils/utils";
import { request } from "../../api";

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

const Cart = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);

    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Cart", link: "/cart" },
    ];

    useEffect(() => {
        const storedCart = JSON.parse(sessionStorage.getItem("cart")) || [];
        setCartItems(storedCart);
    }, []);

    const handleQuantityChange = (id, value) => {
        const newQuantity = Math.max(1, Math.min(999, value));

        request(
            "post",
            `/product-warehouses/check-inventory`,
            (res) => {
                if (!res.data) {
                    toast.error("Not enough stock for this product!");
                    return;
                }

                setCartItems((prevCart) => {
                    const updatedCart = prevCart.map((item) =>
                        item.productId === id ? { ...item, quantity: newQuantity } : item
                    );
                    sessionStorage.setItem("cart", JSON.stringify(updatedCart));
                    return updatedCart;
                });
            }, {},
            {
                productId: id,
                quantity: newQuantity,
            }
        );
    };


    const handleRemoveItem = (id) => {
        const updatedCart = cartItems.filter((item) => item.productId !== id);
        setCartItems(updatedCart);
        sessionStorage.setItem("cart", JSON.stringify(updatedCart));
        toast.success("Product removed from cart");
    };

    const totalCost = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

    return (
        <div className="flex justify-center p-6">
            <Toaster />
            <div className="w-full max-w-5xl">
                <BreadcrumbsCustom paths={breadcrumbPaths} />
                <div className="flex gap-6 mt-6">
                    {/* Danh sách sản phẩm trong giỏ hàng */}
                    <div className="w-2/3">
                        {cartItems.length === 0 ? (
                            <p>Your cart is empty.</p>
                        ) : (
                            cartItems.map((item) => (
                                <Card
                                    key={item.productId}
                                    className="p-6 mb-4 flex justify-between items-center"
                                    sx={{ height: "120px", width: "100%" }}
                                >
                                    <div className="flex items-center space-x-4">
                                        <img src={item.imageUrl} alt={item.name} className="w-24 h-24 object-contain" />
                                        <div className="w-[200px]">
                                            <p
                                                className="font-semibold whitespace-nowrap overflow-hidden text-ellipsis"
                                                style={{ maxWidth: "100%" }}
                                            >
                                                {item.name}
                                            </p>
                                            <p className="text-sm text-gray-500">{item.price && formatPrice(item.price)}</p>
                                        </div>
                                    </div>

                                    <QuantityControl
                                        quantity={item.quantity}
                                        onIncrease={() => handleQuantityChange(item.productId, item.quantity + 1)}
                                        onDecrease={() => handleQuantityChange(item.productId, item.quantity - 1)}
                                        onChange={(e) => handleQuantityChange(item.productId, parseInt(e.target.value) || 1)}
                                        onBlur={() => handleQuantityChange(item.productId, item.quantity || 1)}
                                    />
                                    <IconButton sx={{
                                        color: 'grey.600',
                                        '&:hover': {
                                            color: 'error.main',
                                        }
                                    }} onClick={() => handleRemoveItem(item.productId)}>
                                        <DeleteIcon />
                                    </IconButton>
                                </Card>
                            ))
                        )}
                    </div>

                    {/* Order Summary */}
                    {cartItems.length > 0 && (
                        <Card className="w-1/3 p-6 space-y-6" sx={{ height: "fit-content" }}>
                            <h3 className="text-lg font-semibold text-center">Order Summary</h3>
                            <div className="flex justify-between">
                                <p>Total Product Cost:</p>
                                <p className="font-semibold">{formatPrice(totalCost)}</p>
                            </div>
                            <Button
                                onClick={() => navigate("checkout")}
                                variant="contained"
                                color="primary"
                                sx={{
                                    width: "100%",
                                    height: "40px",
                                    backgroundColor: '#6fd649',
                                    color: '#fff',
                                    fontSize: "16px",
                                    '&:hover': {
                                        backgroundColor: '#4caf50', // hover hiệu ứng
                                    },
                                    '&:active': {
                                        backgroundColor: '#3e8e41', // nhấn
                                    },
                                }}
                            >
                                Checkout
                            </Button>

                        </Card>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Cart;
