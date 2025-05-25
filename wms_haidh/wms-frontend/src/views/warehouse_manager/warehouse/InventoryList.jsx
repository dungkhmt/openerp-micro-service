import React, { useState, useEffect } from 'react';
import {
    Box,
    Grid,
    Typography,
    Paper,
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TablePagination,
    TableHead,
    TableRow,
    TextField,
    MenuItem,
    InputAdornment
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from '../../../api';
import Skeleton from '@mui/material/Skeleton';
import { formatDate } from '../../../utils/utils';
import SearchIcon from '@mui/icons-material/Search';
import CloseIcon from '@mui/icons-material/Close';

const InventoryList = () => {
    const navigate = useNavigate();
    const { id1, id2 } = useParams();

    const [items, setItems] = useState([]);
    const [lotIds, setLotIds] = useState([]);
    const [search, setSearch] = useState('');
    const [debouncedSearchTerm, setDebouncedSearchTerm] = useState("");
    const [selectedLotId, setSelectedLotId] = useState('all');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [totalItems, setTotalItems] = useState(0);
    const [loading, setLoading] = useState(false);
    const [bayInfo, setBayInfo] = useState(null);

    // Load lotId list
    useEffect(() => {
        if (id2) {
            request("get", `/bays/${id2}`, (res) => {
                setBayInfo(res.data);
            });

            request('get', `/inventory/lot-ids?bayId=${id2}`, (res) => {
                setLotIds(res.data);
            });
        }
    }, [id2]);

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedSearchTerm(search);  // Chỉ cập nhật sau khi người dùng ngừng gõ
        }, 500); // 1000ms = 1 giây

        // Hủy bỏ timeout nếu người dùng tiếp tục gõ
        return () => clearTimeout(timer);
    }, [search]);

    // Load inventory data
    useEffect(() => {
        if (id2) {
            setLoading(true);
            request('get', `/inventory?bayId=${id2}&lotId=${selectedLotId}&search=${debouncedSearchTerm}&page=${page}&size=${rowsPerPage}`, (res) => {
                setItems(res.data.content);
                setTotalItems(res.data.totalElements);
                setLoading(false);
            }).catch(() => setLoading(false));
        }
    }, [id2, page, rowsPerPage, debouncedSearchTerm, selectedLotId]);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <Box sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <IconButton color="primary" onClick={() => navigate(`/warehouse-manager/warehouse/${id1}`)} sx={{ color: 'grey.700', mr: 1 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" sx={{ ml: 2 }}>
                    Inventory Details
                </Typography>
            </Box>

            {
                bayInfo && (
                    <Box sx={{ mb: 2 }}>
                        <Paper elevation={2} sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 1 }}>
                            <Typography variant="h6">
                                Bay code: {bayInfo.code}
                            </Typography>
                        </Paper>
                    </Box>
                )
            }



            <Paper elevation={3} sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>
                    Inventory Items
                </Typography>

                <Grid container spacing={2} sx={{ mb: 2 }}>
                    <Grid item xs={12} md={4}>
                        <TextField
                            placeholder="Search by name ..."
                            variant="outlined"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            fullWidth
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon />
                                    </InputAdornment>
                                ),
                                endAdornment: search && (
                                    <InputAdornment position="end">
                                        <IconButton
                                            size="small"
                                            onClick={() => setSearch('')}
                                        >
                                            <CloseIcon fontSize="small" />
                                        </IconButton>
                                    </InputAdornment>
                                ),
                                sx: { height: 48, pl: 1 }
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <TextField
                            select
                            aria-label="Lot ID"
                            value={selectedLotId}
                            onChange={(e) => setSelectedLotId(e.target.value)}
                            fullWidth
                            InputProps={{
                                sx: { height: 48 }
                            }}
                        >
                            <MenuItem value="all">All Lots</MenuItem>
                            {lotIds.map((lotId) => (
                                <MenuItem key={lotId} value={lotId}>
                                    {lotId}
                                </MenuItem>
                            ))}
                        </TextField>
                    </Grid>
                </Grid>


                <div className="mb-4">
                    <Typography variant="h7" gutterBottom className="text-green-500">
                        Total items: {totalItems}
                    </Typography>
                </div>

                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Product</TableCell>
                                <TableCell>Lot ID</TableCell>
                                <TableCell>Available quantity</TableCell>
                                <TableCell>Total Quantity</TableCell>
                                <TableCell>Last Updated</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {loading
                                ? Array.from({ length: rowsPerPage }).map((_, index) => (
                                    <TableRow key={`skeleton-${index}`}>
                                        <TableCell width={300}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell width={150}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                    </TableRow>
                                ))
                                : items.map((item) => (
                                    <TableRow key={item.inventoryItemId}>
                                        <TableCell width={300}>{item.productName}</TableCell>
                                        <TableCell width={100}>{item.lotId}</TableCell>
                                        <TableCell width={100}>{item.availableQuantity}</TableCell>
                                        <TableCell width={100}>{item.quantityOnHandTotal}</TableCell>
                                        <TableCell width={150}>{formatDate(item.lastUpdatedStamp)}</TableCell>
                                    </TableRow>
                                ))}
                        </TableBody>
                    </Table>
                    <TablePagination
                        rowsPerPageOptions={[5, 10, 20]}
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
    );
};

export default InventoryList;
