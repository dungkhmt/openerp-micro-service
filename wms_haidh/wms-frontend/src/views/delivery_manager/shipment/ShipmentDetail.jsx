import React, { useState, useEffect } from 'react';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import Skeleton from "@mui/material/Skeleton";
import { request } from "../../../api";
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
    TableRow,
    Button
} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';

const ShipmentDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [deliveryTrips, setDeliveryTrips] = useState([]);
    const [loading, setLoading] = useState(false);
    const [totalItems, setTotalItems] = useState(0);
    const [hasFetched, setHasFetched] = useState(false);


    useEffect(() => {
        if (id) {
            setLoading(true);
            request("get", `/delivery-trips/by-shipment?shipmentId=${id}&page=${page}&size=${rowsPerPage}`, (res) => {
                setDeliveryTrips(res.data.content);
                setTotalItems(res.data.totalElements);
                setLoading(false);
                setHasFetched(true); // đánh dấu là đã load xong lần đầu
            }).catch(() => {
                setLoading(false);
                setHasFetched(true);
            });
        }
    }, [id, page, rowsPerPage]);


    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };


    return (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <IconButton color="primary" onClick={() => navigate('/delivery-manager/shipments')} sx={{ color: 'grey.700', mr: 1 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
                    Shipment Detail
                </Typography>
                {hasFetched && !loading && deliveryTrips.length === 0 && (
                    <Button
                        variant="contained"
                        startIcon={<AutoAwesomeIcon/>}
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
                        onClick={() => navigate('auto-routing')}
                    >
                        Smart planning
                    </Button>
            )}
            </Box>           

            <Box sx={{ mt: 1 }}>
                <Paper elevation={3} sx={{ p: 3, mt: 4 }}>
                    <Typography variant="h6" gutterBottom>
                        Delivery trips
                    </Typography>
                    <div className='mb-4'>
                        <Typography variant="h7" gutterBottom className="text-green-500">
                            Total deliveries : {totalItems}
                        </Typography>
                    </div>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Delivery trip ID</TableCell>
                                    <TableCell align="center">Distance (m)</TableCell>
                                    <TableCell align="center">Total locations</TableCell>
                                    <TableCell align="center">Status</TableCell>
                                    <TableCell>Description</TableCell>
                                    <TableCell align="center">Detail</TableCell>
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
                                            <TableCell width={200}>
                                                <Skeleton variant="text" />
                                            </TableCell>
                                            <TableCell width={150}>
                                                <Skeleton variant="text" />
                                            </TableCell>
                                        </TableRow>
                                    ))
                                    : deliveryTrips.length === 0 ? (
                                        <TableRow>
                                            <TableCell colSpan={5} align="center">
                                                No delivery trips found.
                                            </TableCell>
                                        </TableRow>
                                    ) : deliveryTrips.map((trip) => (
                                        <TableRow key={trip.deliveryTripId}>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{trip.deliveryTripId}</TableCell>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{Math.round(trip.distance)}</TableCell>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{trip.totalLocations}</TableCell>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>{trip.status}</TableCell>
                                            <TableCell width={200}>{trip.description}</TableCell>
                                            <TableCell sx={{ textAlign: 'center' }} width={150}>
                                                <IconButton
                                                    color="primary"
                                                    onClick={() => navigate(`/delivery-manager/delivery-trip/${trip.deliveryTripId}`)}
                                                >
                                                    <VisibilityIcon />
                                                </IconButton>
                                            </TableCell>
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

export default ShipmentDetail;