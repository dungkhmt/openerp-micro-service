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
const OrderHistory = () => {
    const navigate = useNavigate();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(false);
    const [totalItems, setTotalItems] = useState(0);

    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Order history", link: "/order-history" }

    ];

    useEffect(() => {
        setLoading(true);
        // Mock data for testing
        setTimeout(() => {
            const mockOrders = [
                { date: '2024-04-01', customerName: 'John Doe', totalAmount: 150.00, status: 'Completed' },
                { date: '2024-04-02', customerName: 'Jane Smith', totalAmount: 200.50, status: 'Pending' },
                { date: '2024-04-03', customerName: 'Alice Brown', totalAmount: 99.99, status: 'Shipped' },
            ];
            setOrders(mockOrders);
            setTotalItems(mockOrders.length);
            setLoading(false);
        }, 100);
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
                        Order List
                    </Typography>
                    <Typography variant="subtitle1" gutterBottom className="text-green-500">
                        Total orders: {totalItems}
                    </Typography>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Order date</TableCell>
                                    <TableCell align="center">Customer name</TableCell>
                                    <TableCell align="center">Total order cost</TableCell>
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
                                                <TableCell align="center">{order.date}</TableCell>
                                                <TableCell align="center">{order.customerName}</TableCell>
                                                <TableCell align="center">{formatPrice(order.totalAmount)}</TableCell>
                                                <TableCell align="center">{order.status}</TableCell>
                                                <TableCell align="center">
                                                    <IconButton
                                                        color="primary"
                                                        onClick={() => navigate(`${index}`)}
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
