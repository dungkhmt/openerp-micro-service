import React, { useEffect, useState } from 'react';
import { request } from 'api';
import { useParams, useHistory } from 'react-router-dom';
import {
    Box,
    Button,
    Card,
    CardContent,
    CardHeader,
    Chip,
    Divider,
    Grid,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from '@mui/material';
import { errorNoti } from '../../utils/notification';
import LoadingScreen from '../../components/common/loading/loading';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import TwoWheelerIcon from '@mui/icons-material/TwoWheeler';
import AirportShuttleIcon from '@mui/icons-material/AirportShuttle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EngineeringIcon from '@mui/icons-material/Engineering';
import ScheduleIcon from '@mui/icons-material/Schedule';
import TimelineIcon from '@mui/icons-material/Timeline';

const VehicleDetail = () => {
    const { id } = useParams();
    const history = useHistory();
    const [vehicle, setVehicle] = useState(null);
    const [loading, setLoading] = useState(true);
    const [tripHistory, setTripHistory] = useState([]); // This would be populated from an API call

    useEffect(() => {
        const fetchVehicleData = async () => {
            try {
                await request("get", `/smdeli/vehicle/${id}`, (res) => {
                    setVehicle(res.data);
                    setLoading(false);
                });

                // This would be a separate API call to get trip history
                // For now, we'll use dummy data
                await request("get", `/smdeli/middle-mile/vehicles/${id}/routes`, (res) => {
                    setTripHistory(res.data);
                    setLoading(false);
                });

            } catch (error) {
                errorNoti("Có lỗi khi tải thông tin phương tiện");
                setLoading(false);
            }
        };

        fetchVehicleData();
    }, [id]);

    useEffect(
        ()=> {
            console.log('vehicle',vehicle)

        },[vehicle]
    )
    const handleBack = () => {
        history.goBack();
    };

    const getVehicleIcon = (type) => {
        if (!type) return <AirportShuttleIcon fontSize="large" />;

        switch(type) {
            case 'CAR':
                return <DirectionsCarIcon fontSize="large" />;
            case 'TRUCK':
                return <LocalShippingIcon fontSize="large" />;
            case 'MOTORCYCLE':
                return <TwoWheelerIcon fontSize="large" />;
            default:
                return <AirportShuttleIcon fontSize="large" />;
        }
    };

    const getStatusColor = (status) => {
        if (!status) return "default";

        switch(status) {
            case 'AVAILABLE':
                return 'success';
            case 'BUSY':
                return 'primary';
            case 'MAINTENANCE':
                return 'error';
            default:
                return 'default';
        }
    };


    const formatVehicleStatus = (status) => {
        if (!status) return "Không xác định";

        switch(status) {
            case "AVAILABLE": return "Sẵn sàng";
            case "BUSY": return "Đang sử dụng";
            case "MAINTENANCE": return "Bảo trì";
            default: return status;
        }
    };

    if (loading) {
        return <LoadingScreen />;
    }

    return (
        <Box sx={{ p: 3 }}>
            <Button
                variant="outlined"
                startIcon={<ArrowBackIcon />}
                onClick={handleBack}
                sx={{ mb: 2 }}
            >
                Quay lại
            </Button>

            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Card>
                        <CardHeader
                            title="Thông tin phương tiện"
                            subheader={`ID: ${vehicle.vehicleId}`}
                            avatar={getVehicleIcon(vehicle.vehicleType)}
                            action={
                                <Chip
                                    label={formatVehicleStatus(vehicle.status)}
                                    color={getStatusColor(vehicle.status)}
                                    sx={{ mt: 1 }}
                                />
                            }
                        />
                        <Divider />
                        <CardContent>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <Box sx={{ mb: 2 }}>
                                        <Typography variant="subtitle1" color="text.secondary">
                                            Thông tin cơ bản
                                        </Typography>
                                        <Box sx={{ mt: 2 }}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Biển số xe
                                                    </Typography>
                                                    <Typography variant="body1" fontWeight="bold">
                                                        {vehicle.plateNumber}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Loại xe
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.vehicleType}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Hãng sản xuất
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.manufacturer || 'Không có thông tin'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Mẫu xe
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.model || 'Không có thông tin'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Năm sản xuất
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.yearOfManufacture || 'Không có thông tin'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Dung tích - Tải trọng tối đa
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.volumeCapacity ? `${vehicle.volumeCapacity} m³` : 'Không có thông tin'} -  {vehicle.weightCapacity ? `${vehicle.weightCapacity} kg` : 'Không có thông tin'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Tài xế hiện tại
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.driverName}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Mã tài xế
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.driverCode}
                                                    </Typography>
                                                </Grid>
                                            </Grid>
                                        </Box>
                                    </Box>
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <Box sx={{ mb: 2 }}>
                                        <Typography variant="subtitle1" color="text.secondary">
                                            Thông tin bảo trì
                                        </Typography>
                                        <Box sx={{ mt: 2 }}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Lần bảo trì gần nhất
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.lastMaintenance || 'Chưa có'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Lần bảo trì tiếp theo
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.nextMaintenance || 'Chưa đặt lịch'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Số km hiện tại
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.currentMileage ? `${vehicle.currentMileage} km` : 'Không có thông tin'}
                                                    </Typography>
                                                </Grid>
                                                <Grid item xs={6}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Trạng thái kỹ thuật
                                                    </Typography>
                                                    <Typography variant="body1">
                                                        {vehicle.technicalStatus || 'Bình thường'}
                                                    </Typography>
                                                </Grid>
                                            </Grid>
                                        </Box>
                                    </Box>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12}>
                    <Card>
                        <CardHeader
                            title="Tuyến đường được gán"
                            avatar={<TimelineIcon />}
                        />
                        <Divider />
                        <CardContent>
                            <TableContainer component={Paper}>
                                <Table>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Code</TableCell>
                                            <TableCell>Tên</TableCell>
                                            <TableCell>Mô tả</TableCell>
                                            <TableCell>Hướng đi</TableCell>
                                            <TableCell>Trạng thái</TableCell>

                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {tripHistory.length > 0 ? (
                                            tripHistory.map((trip) => (
                                                <TableRow key={trip.id}>
                                                    <TableCell>{trip.routeCode}</TableCell>
                                                    <TableCell>{trip.routeName}</TableCell>
                                                    <TableCell>{trip.description}</TableCell>
                                                    <TableCell>{trip.routeVehicleDto.direction}</TableCell>

                                                    <TableCell>
                                                        <Chip
                                                            label={trip.status}
                                                            color={trip.status === 'ACTIVE' ? 'success' : 'error'}
                                                            size="small"
                                                        />
                                                    </TableCell>
                                                    <TableCell>{trip.driver}</TableCell>
                                                </TableRow>
                                            ))
                                        ) : (
                                            <TableRow>
                                                <TableCell colSpan={5} align="center">
                                                    Không có dữ liệu lịch sử chuyến đi
                                                </TableCell>
                                            </TableRow>
                                        )}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </CardContent>
                    </Card>
                </Grid>

                </Grid>

        </Box>
    );
};

export default VehicleDetail;