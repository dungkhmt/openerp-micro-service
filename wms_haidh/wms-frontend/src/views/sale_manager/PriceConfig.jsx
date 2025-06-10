import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    Grid,
    Paper,
    TextField,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TablePagination,
    Skeleton,
    IconButton
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { request } from '../../api';
import { useNavigate, useParams } from 'react-router-dom';
import { formatPrice, formatDatev2 } from '../../utils/utils';
import { toast, Toaster } from "react-hot-toast";
import SaveIcon from '@mui/icons-material/Save';

const PriceConfig = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [price, setPrice] = useState('');
    const [description, setDescription] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [priceList, setPriceList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [totalItems, setTotalItems] = useState(0);

    const fetchPriceList = () => {
        setLoading(true);
        request("get", `/product-prices?productId=${id}&page=${page}&size=${rowsPerPage}`, (res) => {
            setPriceList(res.data.content);
            setTotalItems(res.data.totalElements);
            setLoading(false);
        }).catch(() => setLoading(false));
    }
    useEffect(() => {
        if (id) fetchPriceList();
    }, [id, page, rowsPerPage]);

    const handleAddPrice = () => {
        if (!id) {
            toast.error("Product not found!");
            return;
        }
        if (!price) {
            toast.error("Price is required!");
            return;
        }
        if (!description) {
            toast.error("Description is required!");
            return;
        }
        if (!startDate) {
            toast.error("Start date is required!");
            return;
        }
        if (!endDate) {
            toast.error("End date is required!");
            return;
        }
        if (new Date(endDate) < new Date(startDate)) {
            toast.error("End date cannot be earlier than start date!");
            return;
        }
        const payload = {
            productId: id,
            price,
            description,
            startDate,
            endDate,
        };
        request("post", `/product-prices`, (res) => {
            if (res.status === 200) {
                fetchPriceList();
                toast.success("Add new price successfully!")
            }
        }, {
            onError: (e) => {
                toast.error(e?.response?.data || "Error occured!");
            }
        }, payload);

    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <Box sx={{ p: 3 }}>
            <Toaster />
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <IconButton color="primary" onClick={() => navigate('/sale-manager/price-config')} sx={{ color: 'black' }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
                    Price Configuration
                </Typography>
            </Box>

            <Paper sx={{ p: 3, mb: 4 }}>
                <Typography variant="h6" gutterBottom>
                    Add new price
                </Typography>
                <Grid container spacing={2}>
                    {/* Row 1: Price + Description */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            label="Price"
                            fullWidth
                            value={price}
                            onChange={(e) => setPrice(e.target.value)}
                            type="number"
                        />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <TextField
                            label="Description"
                            fullWidth
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </Grid>

                    {/* Row 2: Start Date + End Date */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            label="Start Date"
                            fullWidth
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={startDate}
                            onChange={(e) => setStartDate(e.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <TextField
                            label="End Date"
                            fullWidth
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={endDate}
                            onChange={(e) => setEndDate(e.target.value)}
                        />
                    </Grid>

                    <Grid item xs={12}>
                        <Box display="flex" justifyContent="flex-end">
                            <Button
                                startIcon={<SaveIcon />}
                                onClick={handleAddPrice}
                                variant="outlined"
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
                            >
                                Save
                            </Button>

                        </Box>
                    </Grid>
                </Grid>
            </Paper>

            <Box sx={{ mt: 4 }}>
                <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
                    <Typography variant="h6" gutterBottom>
                        Prices
                    </Typography>
                    <div className='mb-4'>
                        <Typography variant="h7" gutterBottom className="text-green-500">
                            Total prices : {totalItems}
                        </Typography>
                    </div>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Price</TableCell>
                                    <TableCell align="center">Description</TableCell>
                                    <TableCell align="center">Start Date</TableCell>
                                    <TableCell align="center">End Date</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {loading
                                    ? Array.from({ length: rowsPerPage }).map((_, index) => (
                                        <TableRow key={`skeleton-${index}`}>
                                            <TableCell width={150}>
                                                <Skeleton variant="text" />
                                            </TableCell>
                                            <TableCell width={150}>
                                                <Skeleton variant="text" />
                                            </TableCell>
                                            <TableCell width={150}>
                                                <Skeleton variant="text" />
                                            </TableCell>
                                            <TableCell width={150}>
                                                <Skeleton variant="text" />
                                            </TableCell>
                                        </TableRow>
                                    ))
                                    : priceList.length === 0 ? (
                                        <TableRow>
                                            <TableCell colSpan={5} align="center">
                                                No prices found.
                                            </TableCell>
                                        </TableRow>
                                    ) : priceList.map((item) => (
                                        <TableRow key={item.productPriceId}>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{formatPrice(item.price)}</TableCell>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{item.description}</TableCell>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{formatDatev2(item.startDate)}</TableCell>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{item.endDate ? formatDatev2(item.endDate) : "No end date"}</TableCell>
                                        </TableRow>
                                    ))}
                            </TableBody>
                        </Table>
                        {/* Thêm phân trang */}
                        <TablePagination
                            rowsPerPageOptions={[3, 5, 10,]}
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

export default PriceConfig;
