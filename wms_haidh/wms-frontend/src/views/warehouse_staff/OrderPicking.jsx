import React, { useState, useEffect, useRef } from 'react';
import {
    Box,
    Typography,
    IconButton,
    Tooltip,
    Paper,
    TextField,
    MenuItem,
    Grid,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TablePagination,
    TableHead,
    TableRow,
    Skeleton
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../api";
import { CheckCircle } from '@mui/icons-material';
import { toast, Toaster } from "react-hot-toast";

const OrderPicking = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [warehouseInfo, setWarehouseInfo] = useState(null);
    const [bays, setBays] = useState([]);
    const [shelf, setShelf] = useState(0);
    const containerRef = useRef(null);
    const [containerSize, setContainerSize] = useState({ width: 800, height: 400 });
    const [bayId, setBayId] = useState(null);
    const [status, setStatus] = useState('CREATED');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [totalItems, setTotalItems] = useState(0);
    const [loading, setLoading] = useState(false);
    const [items, setItems] = useState([]);
    useEffect(() => {
        if (id) {
            request("get", `/warehouses/${id}`, (res) => {
                setWarehouseInfo(res.data);
            }, {});
        }
    }, [id]);

    useEffect(() => {
        if (bayId) {
            setLoading(true);
            request('get', `/assigned-order-items/assigned?bayId=${bayId}&status=${status}&page=${page}&size=${rowsPerPage}`, (res) => {
                setItems(res.data.content);
                setTotalItems(res.data.totalElements);
                setLoading(false);
            }).catch(() => setLoading(false));
        }
    }, [page, rowsPerPage, status, bayId]);

    useEffect(() => {
        if (id) {
            request("get", `/bays/full?warehouseId=${id}&shelf=${shelf}`, (res) => {
                setBays(res.data);
            }, {});
        }
    }, [id, shelf]);

    useEffect(() => {
        const observer = new ResizeObserver((entries) => {
            for (let entry of entries) {
                const width = entry.contentRect.width;
                if (warehouseInfo) {
                    const aspectRatio = warehouseInfo.width / warehouseInfo.length; // 15/30 = 0.5
                    const height = width * aspectRatio;
                    setContainerSize({ width, height });
                }
            }
        });

        if (containerRef.current) {
            observer.observe(containerRef.current);
        }

        return () => {
            if (containerRef.current) {
                observer.unobserve(containerRef.current);
            }
        };
    }, [warehouseInfo]);

    const handleCopy = (text) => {
        navigator.clipboard.writeText(text);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleMarkAsPicked = (assignedOrderItemId) => {
        request('post', `/assigned-order-items/${assignedOrderItemId}/mark-as-picked`, (res) => {
            if (res.status === 200) {
                request('get', `/assigned-order-items/assigned?bayId=${bayId}&status=${status}&page=${page}&size=${rowsPerPage}`, (res) => {
                    setItems(res.data.content);
                    setTotalItems(res.data.totalElements);
                });
                toast.success("Item picked successfully!");
            }
        }, {
            onError: (e) => {
                toast.error(e?.response?.data || "Error occured!");
            },
        });
    };


    const renderBays = () => {
        if (!warehouseInfo || !containerSize.width) return null;

        const unit = containerSize.width / warehouseInfo.length;

        return bays.map((bay) => {
            const isSelected = bay.bayId === bayId;
            return (
                <Tooltip
                    key={bay.bayId}
                    title={`Length: ${bay.xlong}, Width: ${bay.ylong}`}
                    arrow
                >
                    <Box
                        onClick={() => setBayId(bay.bayId)}
                        sx={{
                            position: 'absolute',
                            left: bay.x * unit,
                            top: bay.y * unit,
                            width: bay.xlong * unit,
                            height: bay.ylong * unit,
                            backgroundColor: isSelected ? '#1976d2' : '#90caf9', // đậm hơn khi chọn
                            border: isSelected ? '2px solid #0d47a1' : '1px solid #42a5f5', // border rõ hơn
                            borderRadius: 1,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: 'bold',
                            color: '#fff',
                            boxShadow: 1,
                            cursor: 'pointer',
                            '&:hover': {
                                backgroundColor: isSelected ? '#1565c0' : '#64b5f6', // hover nhẹ nếu không chọn, đậm nếu chọn
                            }
                        }}
                    >
                        {bay.code}
                    </Box>
                </Tooltip>
            );
        });
    };



    return (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
            <Toaster />
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <IconButton onClick={() => navigate('/warehouse-staff/warehouse')} sx={{ color: 'grey.700', mr: 1 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
                    Order picking
                </Typography>
            </Box>

            {warehouseInfo && (
                <>
                    <Box sx={{ mb: 2 }}>
                        <Paper elevation={2} sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 1 }}>
                            <Typography variant="h6">
                                {warehouseInfo.name} ({warehouseInfo.code})
                            </Typography>
                            <Typography variant="body2">
                                <strong>Address:</strong> {warehouseInfo.address}
                                <IconButton size="small" onClick={() => handleCopy(warehouseInfo.address)}>
                                    <ContentCopyIcon fontSize="inherit" />
                                </IconButton>
                            </Typography>
                            <Typography variant="body2">
                                <strong>Dimensions:</strong> {warehouseInfo.length}m (L)  × {warehouseInfo.width}m (W)
                            </Typography>
                            <Typography variant="body2">
                                <strong>Area:</strong> {warehouseInfo.width * warehouseInfo.length} m²
                            </Typography>
                            <Typography variant="body2">
                                <strong>Coordinates:</strong> ({warehouseInfo.latitude?.toFixed(6)}, {warehouseInfo.longitude?.toFixed(6)})
                                <IconButton size="small" onClick={() => handleCopy(`${warehouseInfo.latitude}, ${warehouseInfo.longitude}`)}>
                                    <ContentCopyIcon fontSize="inherit" />
                                </IconButton>
                            </Typography>
                        </Paper>
                    </Box>

                    <Box sx={{ mb: 2, mt: 2, width: "20%" }}>
                        <TextField
                            select
                            aria-label="Shelf"
                            value={shelf}
                            onChange={(e) => setShelf(e.target.value)}
                            fullWidth
                            InputProps={{ sx: { height: 48 } }}
                        >
                            {[0, 1, 2, 3, 4].map((s) => (
                                <MenuItem key={s} value={s}>
                                    Shelf {s}
                                </MenuItem>
                            ))}
                        </TextField>
                    </Box>

                    <Box ref={containerRef} sx={{ width: '100%' }}>
                        <Paper
                            elevation={3}
                            sx={{
                                position: 'relative',
                                width: '100%',
                                height: containerSize.height,
                                backgroundColor: '#e3f2fd',
                                border: '1px solid #90caf9',
                                overflow: 'hidden',
                                mt: 2,
                            }}
                        >
                            {renderBays()}
                        </Paper>
                    </Box>

                </>
            )}
            <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>
                    Picking tasks
                </Typography>

                <Grid container spacing={2} sx={{ mb: 2 }}>
                    <Grid item xs={12} md={2}>
                        <TextField
                            select
                            aria-label="Status"
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                            fullWidth
                            InputProps={{
                                sx: { height: 48 }
                            }}
                        >
                            {['CREATED', 'PICKED'].map((status) => (
                                <MenuItem key={status} value={status}>
                                    {status}
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
                                <TableCell align='center'>Quantity</TableCell>
                                <TableCell align='center'>Bay Code</TableCell>
                                <TableCell align='center'>Lot ID</TableCell>
                                <TableCell align='center'>Status</TableCell>
                                <TableCell align='center'>Action</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {loading
                                ? Array.from({ length: rowsPerPage }).map((_, index) => (
                                    <TableRow key={`skeleton-${index}`}>
                                        <TableCell width={300}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell align='center' width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell align='center' width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell align='center' width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell align='center' width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                        <TableCell align='center' width={100}>
                                            <Skeleton variant="text" />
                                        </TableCell>
                                    </TableRow>
                                ))
                                : items.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={6} align="center">
                                            No assigned order items found.
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    items.map((item) => (
                                        <TableRow key={item.assignedOrderItemId}>
                                            <TableCell width={300}>{item.productName}</TableCell>
                                            <TableCell align='center' width={100}>{item.originalQuantity}</TableCell>
                                            <TableCell align='center' width={100}>{item.bayCode}</TableCell>
                                            <TableCell align='center' width={100}>{item.lotId}</TableCell>
                                            <TableCell align='center' width={100}>{item.status}</TableCell>
                                            <TableCell align='center' width={100}>
                                                {item.status !== 'PICKED' && (
                                                    <Tooltip title="Mark as Picked">
                                                        <IconButton onClick={() => handleMarkAsPicked(item.assignedOrderItemId)} color="success">
                                                            <CheckCircle />
                                                        </IconButton>
                                                    </Tooltip>
                                                )}
                                            </TableCell>
                                        </TableRow>
                                    ))
                                )
                            }
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

export default OrderPicking;
