import React, { useState, useEffect } from "react";
import { Card, TextField, Button, MenuItem, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import { toast, Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import BreadcrumbsCustom from "../../components/BreadcrumbsCustom";
import { formatPrice } from "../../utils/utils";
import { request } from "../../api";

const Checkout = () => {
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [address, setAddress] = useState("");
    const [description, setDescription] = useState("");
    const [savedAddresses, setSavedAddresses] = useState([]);
    const [cartItems, setCartItems] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);


    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Cart", link: "/customer/cart" },
        { label: "Checkout", link: "/customer/cart/checkout" },
    ];

    const totalCost = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const totalWeight = cartItems.reduce((sum, item) => sum + item.weight * item.quantity, 0);
    const deliveryFee = totalWeight * 5000;
    const totalOrderCost = totalCost + deliveryFee;


    useEffect(() => {
        request("get", `/customer-addresses`, (res) => {
            setSavedAddresses(res.data);
        }).then();
    }, [])

    useEffect(() => {
        setCartItems(JSON.parse(sessionStorage.getItem("cart")) || []);
    }, []);

    useEffect(() => {
        const savedName = sessionStorage.getItem("name");
        const savedPhone = sessionStorage.getItem("phone");
        const savedDescription = sessionStorage.getItem("description");

        if (savedName) setName(savedName);
        if (savedPhone) setPhone(savedPhone);
        if (savedDescription) setDescription(savedDescription);
    }, []);

    const handleSelectNewAddress = () => {
        sessionStorage.setItem("name", name);
        sessionStorage.setItem("phone", phone);
        sessionStorage.setItem("description", description);
        navigate("new-address");
    };

    const handleSubmit = () => {
        if (!name || !phone || !address) {
            toast.error("Please fill in all required fields.");
            return;
        }

        if (!/^[0-9]{8,15}$/.test(phone)) {
            toast.error("Phone number must be 8–15 digits");
            return false;
        }

        if (address === "new") {
            toast.error("Please select a saved address or add a new one.");
            return;
        }

        const items = cartItems.map((item) => ({
            productId: item.productId,
            quantity: item.quantity,
            priceUnit: item.price
        }));

        const payload = {
            deliveryFee,
            totalProductCost: totalCost,
            totalOrderCost,
            customerAddressId: address,
            customerName: name,
            customerPhoneNumber: phone,
            description,
            paymentType: "COD",
            orderType: "ONLINE",
            items
        };

        request(
            "post",
            "/orders",
            (res) => {
                sessionStorage.removeItem("cart");
                setOpenDialog(true);
            },
            {
                onError: (e) => {
                    const msg = e?.response?.data || "Error occured!";
                    toast.error(msg);
                },
            },
            payload
        );



    };

    return (
        <div className="flex justify-center p-6">
            <Toaster />

            <div className="w-full max-w-4xl space-y-6">
                <BreadcrumbsCustom paths={breadcrumbPaths} />
                <Card className="p-6 space-y-4">
                    <h3 className="text-lg font-semibold">Shipping Information</h3>
                    <div className="flex space-x-4">
                        <TextField label="Full Name" className="w-1/2" value={name} onChange={(e) => setName(e.target.value)} />
                        <TextField label="Phone Number" className="w-1/2" value={phone} onChange={(e) => setPhone(e.target.value)} />
                    </div>
                    <TextField select label="Address" fullWidth value={address} onChange={(e) => setAddress(e.target.value)}>
                        {savedAddresses.map((addr) => (
                            <MenuItem key={addr.customerAddressId} value={addr.customerAddressId}>{addr.addressName}</MenuItem>
                        ))}
                        <MenuItem value="new" onClick={handleSelectNewAddress}>Add new address</MenuItem>
                    </TextField>
                    <TextField label="Description" fullWidth value={description} onChange={(e) => setDescription(e.target.value)} />
                </Card>

                <Card className="p-6">
                    <h3 className="text-lg font-semibold">Order Summary</h3>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Product</TableCell>
                                    <TableCell align="center">Quantity</TableCell>
                                    <TableCell align="center">Unit</TableCell>
                                    <TableCell align="center">Unit Price</TableCell>
                                    <TableCell align="center">Total</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {cartItems.map((item) => (
                                    <TableRow key={item.productId}>
                                        <TableCell>
                                            <img src={item.imageUrl} alt={item.name} className="w-24 h-24 object-contain" />
                                            {item.name}
                                        </TableCell>
                                        <TableCell align="center">{item.quantity}</TableCell>
                                        <TableCell align="center">{item.uom}</TableCell>
                                        <TableCell align="center">{formatPrice(item.price)}</TableCell>
                                        <TableCell align="center">{formatPrice(item.price * item.quantity)}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Card>

                <Card className="p-6 space-y-4">
                    <h3 className="text-lg font-semibold">Payment Details</h3>
                    <div className="flex justify-between">
                        <p>Total Product Cost:</p>
                        <p>{totalCost.toLocaleString()} VND</p>
                    </div>
                    <div className="flex justify-between">
                        <p>Delivery Fee:</p>
                        <p>{deliveryFee.toLocaleString()} VND</p>
                    </div>
                    <div className="flex justify-between font-bold">
                        <p>Total Order Cost:</p>
                        <p>{totalOrderCost.toLocaleString()} VND</p>
                    </div>

                    {/* Hiển thị phương thức thanh toán COD */}
                    <div className="flex justify-between items-center font-bold">
                        <p>Payment Method:</p>
                        <p className="font-semibold">Cash on Delivery (COD)</p>
                    </div>

                    <div className="flex justify-center">
                        <Button
                            variant="contained"
                            sx={{
                                width: '40%',
                                margin: 'auto',
                                backgroundColor: '#6fd649',
                                color: '#fff',
                                '&:hover': {
                                    backgroundColor: '#4caf50',
                                },
                                '&:active': {
                                    backgroundColor: '#3e8e41',
                                },
                            }}
                            onClick={handleSubmit}
                        >
                            Place Order
                        </Button>
                    </div>
                </Card>

            </div>
            <Dialog
                open={openDialog}
                onClose={(event, reason) => {
                    if (reason === "backdropClick" || reason === "escapeKeyDown") return;
                    setOpenDialog(false);
                }}
                disableEscapeKeyDown
                slotProps={{
                    backdrop: {
                        style: { pointerEvents: "none" }
                    }
                }}
                sx={{
                    ".MuiDialog-paper": {
                        padding: "1.5rem",
                        borderRadius: "0.75rem",
                        maxWidth: "25rem",
                        width: "90%",
                        textAlign: "center",
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                    }
                }}
            >
                <DialogTitle className="font-semibold text-lg">🎉 Order Placed Successfully!</DialogTitle>
                <DialogContent className="flex flex-col items-center space-y-4">
                    <CheckCircleOutlineIcon color="success" sx={{ fontSize: "4rem" }} />
                    <p>Your order has been successfully placed and is now being processed.</p>
                </DialogContent>
                <DialogActions sx={{ justifyContent: "center" }}>
                    <Button
                        variant="contained"
                         sx={{
                                margin: 'auto',
                                backgroundColor: '#6fd649',
                                color: '#fff',
                                '&:hover': {
                                    backgroundColor: '#4caf50',
                                },
                                '&:active': {
                                    backgroundColor: '#3e8e41',
                                },
                            }}
                        onClick={() => navigate("/customer/order-history")}
                    >
                        View my orders
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
};

export default Checkout;