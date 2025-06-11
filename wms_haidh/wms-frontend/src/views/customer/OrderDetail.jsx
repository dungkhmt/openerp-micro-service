import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Grid,
    Button,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { formatDate, formatPrice } from '../../utils/utils';
import BreadcrumbsCustom from '../../components/BreadcrumbsCustom';
import { request } from '../../api';

const OrderDetail = () => {
    const navigate = useNavigate();
    const { orderId } = useParams();
    const [generalInfo, setGeneralInfo] = useState(null);
    const [customerInfo, setCustomerInfo] = useState(null);
    const [details, setDetails] = useState([]);

    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Order history", link: "/customer/order-history" },
        { label: "Order details", link: "/customer/order-history/:orderId" }

    ];

    useEffect(() => {
        request('get', `/orders/${orderId}`, (res) => {
            setGeneralInfo(res.data);
        });

        request('get', `/orders/${orderId}/customer-address`, (res) => {
            setCustomerInfo(res.data);
        });

        request('get', `/order-items?orderId=${orderId}`, (res) => {
            setDetails(res.data);
        });
    }, [orderId]);

    const handleCancel = () => {
        request('post', `/orders/${orderId}/cancel`, (res) => {
            if (res.status === 200) {
                alert('Order cancelled successfully!');
                navigate('/customer/order-history');
            } else {
                alert('Failed to cancel order.');
            }
        });

    };

    return (
        <Box sx={{ p: 3 }}>
            <BreadcrumbsCustom paths={breadcrumbPaths} />
            <Grid container spacing={3} sx={{ mt: 1 }}>
                <Grid item xs={12} md={6}>
                    <Paper elevation={3} sx={{ p: 3, height: 300 }}>
                        <Typography variant="h6" gutterBottom>
                            General Information
                        </Typography>
                        <Typography><b>Order Date:</b> {generalInfo && formatDate(generalInfo.orderDate)}</Typography>
                        <Typography><b>Delivery Fee:</b> {generalInfo && formatPrice(generalInfo.deliveryFee)}</Typography>
                        <Typography><b>Total Product Cost:</b> {generalInfo && formatPrice(generalInfo.totalProductCost)}</Typography>
                        <Typography><b>Total Order Cost:</b> {generalInfo && formatPrice(generalInfo.totalOrderCost)}</Typography>
                        <Typography><b>Description:</b> {generalInfo && generalInfo.description}</Typography>
                        <Typography><b>Payment Type:</b> {generalInfo && generalInfo.paymentType}</Typography>
                        <Typography><b>Order Type:</b> {generalInfo && generalInfo.orderType}</Typography>
                        <Typography>
                            <b>Status:</b> {generalInfo && generalInfo.status?.replaceAll('_', ' ')}
                        </Typography>

                    </Paper>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Paper elevation={3} sx={{ p: 3, height: 300 }}>
                        <Typography variant="h6" gutterBottom>
                            Customer Information
                        </Typography>
                        <Typography><b>Customer Name:</b>  <br />{generalInfo && generalInfo.customerName}</Typography>
                        <Typography><b>Phone Number:</b>  <br />{generalInfo && generalInfo.customerPhoneNumber}</Typography>
                        <Typography><b>Address:</b>  <br />{customerInfo && customerInfo.addressName}</Typography>
                    </Paper>
                </Grid>
            </Grid>

            <Box sx={{ mt: 4 }}>
                <Paper elevation={3} sx={{ p: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Order Items
                    </Typography>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell sx={{ fontWeight: 'bold' }}>Product Name</TableCell>
                                    <TableCell align="center" sx={{ fontWeight: 'bold' }}>Quantity</TableCell>
                                    <TableCell align="center" sx={{ fontWeight: 'bold' }}>Unit</TableCell>
                                    <TableCell align="center" sx={{ fontWeight: 'bold' }}>Price Unit</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {details.map((item, index) => (
                                    <TableRow key={index}>
                                        <TableCell >{item.productName}</TableCell>
                                        <TableCell align="center">{item.quantity}</TableCell>
                                        <TableCell align="center">{item.uom}</TableCell>
                                        <TableCell align="center">{formatPrice(item.priceUnit)}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Paper>
            </Box>

            {generalInfo?.status === 'CREATED' && (
                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                    <Button variant="contained" color="error" onClick={handleCancel}>
                        Cancel
                    </Button>
                </Box>
            )}
        </Box>
    );
};

export default OrderDetail;
