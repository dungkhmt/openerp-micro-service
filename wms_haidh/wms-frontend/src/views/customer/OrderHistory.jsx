import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Skeleton from "@mui/material/Skeleton";
import {
    Box,
    Typography,
    Paper,
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TablePagination,
    TableHead,
    TableRow
} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import BreadcrumbsCustom from "../../components/BreadcrumbsCustom";
import { formatDate, formatPrice } from '../../utils/utils';
import { request } from '../../api';

const OrderHistory = () => {
    const navigate = useNavigate();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(3);
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(false);
    const [totalItems, setTotalItems] = useState(0);

    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Order history", link: "/order-history" }

    ];

    useEffect(() => {
        setLoading(true);
        request("get", `/orders/by-user?page=${page}&size=${rowsPerPage}`, (res) => {
            setOrders(res.data.content);
            setTotalItems(res.data.totalElements);
            setLoading(false);
        }).then();
    }, [page, rowsPerPage]);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
            <BreadcrumbsCustom paths={breadcrumbPaths} />
            <Box>
                <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        My orders
                    </Typography>
                    <Typography variant="subtitle1" gutterBottom className="text-green-500">
                        Total orders: {totalItems}
                    </Typography>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Order Date</TableCell>
                                    <TableCell align="center">Customer Name</TableCell>
                                    <TableCell align="center">Total Order Cost</TableCell>
                                    <TableCell align="center">Status</TableCell>
                                    <TableCell align="center">Action</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {loading
                                    ? Array.from({ length: rowsPerPage }).map((_, index) => (
                                        <TableRow key={`skeleton-${index}`}>
                                            <TableCell colSpan={5} align="center">
                                                <Skeleton variant="text" />
                                            </TableCell>
                                        </TableRow>
                                    ))
                                    : orders.length === 0 ? (
                                        <TableRow>
                                            <TableCell colSpan={5} align="center">
                                                No orders found.
                                            </TableCell>
                                        </TableRow>
                                    ) : (
                                        orders.map((order, index) => (
                                            <TableRow key={index}>
                                                <TableCell align="center">{formatDate(order.orderDate)}</TableCell>
                                                <TableCell align="center">{order.customerName}</TableCell>
                                                <TableCell align="center">{formatPrice(order.totalOrderCost)}</TableCell>
                                                <TableCell align="center">{order.status?.replace(/_/g, ' ')}</TableCell>
                                                <TableCell align="center">
                                                    <IconButton
                                                        color="primary"
                                                        onClick={() => navigate(order.orderId)}
                                                    >
                                                        <VisibilityIcon />
                                                    </IconButton>
                                                </TableCell>
                                            </TableRow>
                                        ))
                                    )}
                            </TableBody>
                        </Table>
                        <TablePagination
                            rowsPerPageOptions={[3, 5, 10]}
                            component="div"
                            count={totalItems}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </TableContainer>
                </Paper>
            </Box>
        </Box>
    );
};

export default OrderHistory;
