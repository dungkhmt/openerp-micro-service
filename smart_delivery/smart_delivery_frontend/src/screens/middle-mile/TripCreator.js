import React, { useEffect, useState } from 'react';
import { request } from "../../api";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import AddIcon from '@mui/icons-material/Add';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import EventIcon from '@mui/icons-material/Event';

import StandardTable from "../../components/StandardTable";
import {
    Box,
    Button,
    Modal,
    Typography,
    TextField,
    Card,
    CardContent,
    Grid,
    Chip,
    Alert,
    InputAdornment,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from "@mui/material";
import { useSelector } from "react-redux";
import { errorNoti, successNoti } from "../../utils/notification";
import { useHistory } from "react-router-dom";

const TripCreator = () => {
    const [trips, setTrips] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [openCreateModal, setOpenCreateModal] = useState(false);
    const [openEditModal, setOpenEditModal] = useState(false);
    const [startDate, setStartDate] = useState(new Date().toISOString().split('T')[0]);
    const [endDate, setEndDate] = useState(new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]);
    const [isCreating, setIsCreating] = useState(false);
    const [editingTrip, setEditingTrip] = useState(null);
    const [newStatus, setNewStatus] = useState('');
    const userId = useSelector((state) => state.auth.userId);
    const history = useHistory();

    useEffect(() => {
        fetchTrips();
    }, []);
    const shortenId = (uuid) => {
        if (!uuid) return "N/A";
        if (typeof uuid === 'string' && uuid.length > 8) {
            return uuid.substring(0, 8) ;
        }
        return uuid;
    };
    const fetchTrips = () => {
        request("get", `/smdeli/trip-assignments/all`, (res) => {
            setTrips(res.data || []);
        }).catch(err => {
            errorNoti("Không thể tải danh sách chuyến xe");
        });
    };

    const columns = [
        {
            title: "Mã chuyến",
            field: "id",
            renderCell: (rowData) => {
                return (
                    <Typography
                        variant="body2"
                        sx={{
                            fontFamily: 'monospace',
                            fontSize: '0.875rem',
                            cursor: 'pointer'
                        }}
                        title={rowData.id} // Hiển thị full ID khi hover
                    >
                        {shortenId(rowData.id)}
                    </Typography>
                );
            }
        },
        {
            title: "Tuyến đường",
            field: "routeName",
        },
        {
            title: "Ngày",
            field: "date",
            renderCell: (rowData) => {
                return new Date(rowData.date).toLocaleDateString('vi-VN');
            }
        },
        {
            title: "Thời gian bắt đầu",
            field: "plannedStartTime",
            renderCell: (rowData) => {
                if (rowData.plannedStartTime) {
                    return new Date(`1970-01-01T${rowData.plannedStartTime}`).toLocaleTimeString('vi-VN', {
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                }
                return "N/A";
            }
        },
        {
            title: "Trạng thái",
            field: "status",
            renderCell: (rowData) => {
                const statusColors = {
                    PENDING: 'default',
                    PLANNED: 'info',
                    IN_PROGRESS: 'primary',
                    CAME_FIRST_STOP: 'secondary',
                    CAME_LAST_STOP: 'secondary',
                    COMPLETED: 'success',
                    CONFIRMED_IN: 'info',
                    CANCELLED: 'error',
                    PICKED_UP: 'warning',
                    CAME_STOP: 'secondary',
                    DELIVERED: 'success',
                    DONE_STOP: 'success',
                    CONFIRMED_OUT: 'success',
                    DELAYED: 'warning'
                };

                const statusLabels = {
                    PENDING: 'Chờ xử lý',
                    PLANNED: 'Đã lên kế hoạch',
                    IN_PROGRESS: 'Đang thực hiện',
                    CAME_FIRST_STOP: 'Đến điểm đầu',
                    CAME_LAST_STOP: 'Đến điểm cuối',
                    COMPLETED: 'Hoàn thành',
                    CONFIRMED_IN: 'Đã xác nhận vào',
                    CANCELLED: 'Đã hủy',
                    PICKED_UP: 'Đã lấy hàng',
                    CAME_STOP: 'Đến điểm dừng',
                    DELIVERED: 'Đã giao hàng',
                    DONE_STOP: 'Hoàn thành điểm dừng',
                    CONFIRMED_OUT: 'Đã xác nhận ra',
                    DELAYED: 'Bị trễ'
                };

                return (
                    <Chip
                        label={statusLabels[rowData.status] || rowData.status}
                        color={statusColors[rowData.status] || 'default'}
                        size="small"
                    />
                );
            }
        },
        {
            title: "Đơn đã lấy",
            field: "ordersPickedUp",
            renderCell: (rowData) => rowData.ordersPickedUp || 0
        },
        {
            title: "Đơn đã giao",
            field: "ordersDelivered",
            renderCell: (rowData) => rowData.ordersDelivered || 0
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div>
                    <IconButton
                        onClick={() => handleViewTrip(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    {/* Chỉ cho edit status nếu chưa hoàn thành hoặc hủy */}
                    {(rowData.status !== 'COMPLETED' && rowData.status !== 'CANCELLED') && (
                        <IconButton
                            onClick={() => handleEditTripStatus(rowData)}
                            color="info"
                        >
                            <EditIcon />
                        </IconButton>
                    )}
                    {/* Chỉ cho xóa nếu là PENDING hoặc PLANNED */}
                    {(rowData.status === 'PENDING' || rowData.status === 'PLANNED') && (
                        <IconButton
                            onClick={() => handleDeleteTrip(rowData.id)}
                            color="error"
                        >
                            <DeleteIcon />
                        </IconButton>
                    )}
                </div>
            ),
        },
    ];

    const handleViewTrip = (trip) => {
        // Fetch detailed trip info
        request("get", `/smdeli/trip-assignments/${trip.id}`, (res) => {
            setSelectedTrip(res.data);
            setOpenModal(true);
        }).catch(err => {
            errorNoti("Không thể tải chi tiết chuyến xe");
        });
    };

    const handleEditTripStatus = (trip) => {
        setEditingTrip(trip);
        setNewStatus(trip.status);
        setOpenEditModal(true);
    };

    const handleUpdateTripStatus = () => {
        if (!editingTrip || !newStatus) {
            errorNoti("Vui lòng chọn trạng thái mới");
            return;
        }

        const updateData = {
            status: newStatus
        };

        // Thêm thời gian bắt đầu nếu chuyển sang IN_PROGRESS
        if (newStatus === 'IN_PROGRESS' && editingTrip.status === 'PLANNED') {
            updateData.startTime = new Date().toISOString();
        }

        // Thêm thời gian kết thúc nếu chuyển sang COMPLETED
        if (newStatus === 'COMPLETED') {
            updateData.endTime = new Date().toISOString();
            // Yêu cầu số đơn hàng nếu chưa có
            if (!editingTrip.ordersPickedUp && !editingTrip.ordersDelivered) {
                updateData.ordersPickedUp = 0;
                updateData.ordersDelivered = 0;
            }
        }

        request(
            "put",
            `/smdeli/trip-assignments/${editingTrip.id}`,
            (res) => {
                successNoti("Cập nhật trạng thái chuyến xe thành công");
                fetchTrips();
                setOpenEditModal(false);
                setEditingTrip(null);
                setNewStatus('');
            },
            {
                400: (error) => errorNoti("Chuyển đổi trạng thái không hợp lệ"),
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            },
            updateData
        );
    };

    const handleDeleteTrip = (tripId) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa chuyến xe này?")) {
            request(
                "delete",
                `/smdeli/trip-assignments/${tripId}`,
                (res) => {
                    successNoti("Xóa chuyến xe thành công");
                    fetchTrips();
                },
                {
                    500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
                }
            );
        }
    };

    const handleCreateTrips = () => {
        if (!startDate || !endDate) {
            errorNoti("Vui lòng chọn ngày bắt đầu và ngày kết thúc");
            return;
        }

        if (new Date(startDate) > new Date(endDate)) {
            errorNoti("Ngày bắt đầu không thể sau ngày kết thúc");
            return;
        }

        setIsCreating(true);

        request(
            "post",
            `/smdeli/trip-assignments/create-from-to?startDate=${startDate}&endDate=${endDate}`,
            (res) => {
                const createdTripsCount = res.data ? res.data.length : 0;
                successNoti(`Đã tạo thành công ${createdTripsCount} chuyến xe`);
                fetchTrips();
                setOpenCreateModal(false);
                setIsCreating(false);
            },
            {
                400: () => {
                    errorNoti("Dữ liệu không hợp lệ");
                    setIsCreating(false);
                },
                404: () => {
                    errorNoti("Không tìm thấy lịch trình tuyến đường nào");
                    setIsCreating(false);
                },
                500: () => {
                    errorNoti("Có lỗi xảy ra, vui lòng thử lại sau");
                    setIsCreating(false);
                }
            }
        );
    };

    const handleCloseModal = () => {
        setOpenModal(false);
        setSelectedTrip(null);
    };

    const handleCloseCreateModal = () => {
        setOpenCreateModal(false);
        setStartDate(new Date().toISOString().split('T')[0]);
        setEndDate(new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]);
    };

    const handleCloseEditModal = () => {
        setOpenEditModal(false);
        setEditingTrip(null);
        setNewStatus('');
    };

    const formatDateTime = (dateTimeString) => {
        if (!dateTimeString) return "N/A";
        return new Date(dateTimeString).toLocaleString('vi-VN');
    };

    // Lấy danh sách trạng thái hợp lệ có thể chuyển đổi
    const getValidNextStatuses = (currentStatus) => {
        switch (currentStatus) {
            case 'PENDING':
                return ['PLANNED', 'CANCELLED'];
            case 'PLANNED':
                return ['IN_PROGRESS', 'DELAYED', 'CANCELLED'];
            case 'IN_PROGRESS':
                return ['CAME_FIRST_STOP', 'CAME_STOP', 'DELAYED', 'CANCELLED'];
            case 'CAME_FIRST_STOP':
                return ['PICKED_UP', 'CAME_STOP', 'DELAYED', 'CANCELLED'];
            case 'CAME_STOP':
                return ['PICKED_UP', 'DELIVERED', 'DONE_STOP', 'DELAYED', 'CANCELLED'];
            case 'PICKED_UP':
                return ['DELIVERED', 'CAME_STOP', 'CAME_LAST_STOP', 'DELAYED', 'CANCELLED'];
            case 'DELIVERED':
                return ['DONE_STOP', 'CAME_STOP', 'CAME_LAST_STOP', 'DELAYED', 'CANCELLED'];
            case 'DONE_STOP':
                return ['CAME_STOP', 'CAME_LAST_STOP', 'CONFIRMED_OUT', 'DELAYED', 'CANCELLED'];
            case 'CAME_LAST_STOP':
                return ['CONFIRMED_OUT', 'COMPLETED', 'DELAYED', 'CANCELLED'];
            case 'CONFIRMED_IN':
                return ['IN_PROGRESS', 'CAME_FIRST_STOP', 'DELAYED', 'CANCELLED'];
            case 'CONFIRMED_OUT':
                return ['COMPLETED', 'CANCELLED'];
            case 'DELAYED':
                return ['IN_PROGRESS', 'CAME_FIRST_STOP', 'CAME_STOP', 'CAME_LAST_STOP',
                    'PICKED_UP', 'DELIVERED', 'DONE_STOP', 'CONFIRMED_OUT', 'CANCELLED'];
            default:
                return [];
        }
    };

    const statusLabels = {
        PENDING: 'Chờ xử lý',
        PLANNED: 'Đã lên kế hoạch',
        IN_PROGRESS: 'Đang thực hiện',
        CAME_FIRST_STOP: 'Đến điểm đầu',
        CAME_LAST_STOP: 'Đến điểm cuối',
        COMPLETED: 'Hoàn thành',
        CONFIRMED_IN: 'Đã xác nhận vào',
        CANCELLED: 'Đã hủy',
        PICKED_UP: 'Đã lấy hàng',
        CAME_STOP: 'Đến điểm dừng',
        DELIVERED: 'Đã giao hàng',
        DONE_STOP: 'Hoàn thành điểm dừng',
        CONFIRMED_OUT: 'Đã xác nhận ra',
        DELAYED: 'Bị trễ'
    };

    return (
        <div>
            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenCreateModal(true)}
                >
                    Tạo chuyến xe
                </Button>
            </Box>

            <StandardTable
                title="Danh sách chuyến xe"
                columns={columns}
                data={trips}
                rowKey="id"
                editable={false}
                deletable={false}
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />

            {/* Modal xem chi tiết chuyến xe */}
            <Modal
                open={openModal}
                onClose={handleCloseModal}
                aria-labelledby="trip-detail-modal"
            >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: 700,
                    bgcolor: 'background.paper',
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2,
                    maxHeight: '80vh',
                    overflow: 'auto'
                }}>
                    <Typography variant="h6" component="h2" sx={{ mb: 3 }}>
                        Chi tiết chuyến xe
                    </Typography>

                    {selectedTrip && (
                        <Grid container spacing={3}>
                            <Grid item xs={12} md={6}>
                                <Card variant="outlined">
                                    <CardContent>
                                        <Typography variant="h6" color="primary" gutterBottom>
                                            Thông tin cơ bản
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Tuyến đường:</strong> {selectedTrip.routeName}
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Mã lịch trình:</strong> {selectedTrip.routeScheduleId}
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Ngày:</strong> {new Date(selectedTrip.date || new Date()).toLocaleDateString('vi-VN')}
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Trạng thái:</strong>
                                            <Chip
                                                label={selectedTrip.status}
                                                size="small"
                                                sx={{ ml: 1 }}
                                            />
                                        </Typography>
                                    </CardContent>
                                </Card>
                            </Grid>

                            <Grid item xs={12} md={6}>
                                <Card variant="outlined">
                                    <CardContent>
                                        <Typography variant="h6" color="primary" gutterBottom>
                                            Thống kê
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Tổng số điểm dừng:</strong> {selectedTrip.totalStops || 0}
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Điểm dừng hiện tại:</strong> {selectedTrip.currentStopIndex || 0}
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Số đơn hàng:</strong> {selectedTrip.ordersCount || 0}
                                        </Typography>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            <strong>Đã giao:</strong> {selectedTrip.ordersDelivered || 0}
                                        </Typography>
                                    </CardContent>
                                </Card>
                            </Grid>
                        </Grid>
                    )}

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                        <Button variant="outlined" onClick={handleCloseModal}>
                            Đóng
                        </Button>
                    </Box>
                </Box>
            </Modal>

            {/* Modal tạo chuyến xe */}
            <Modal
                open={openCreateModal}
                onClose={handleCloseCreateModal}
                aria-labelledby="create-trip-modal"
            >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: 500,
                    bgcolor: 'background.paper',
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2
                }}>
                    <Typography variant="h6" component="h2" sx={{ mb: 3 }}>
                        Tạo chuyến xe theo khoảng thời gian
                    </Typography>

                    <Alert severity="info" sx={{ mb: 3 }}>
                        Hệ thống sẽ tự động tạo chuyến xe dựa trên các lịch trình tuyến đường đã được phân công phương tiện và tài xế.
                    </Alert>

                    <Box component="form" sx={{ mt: 2 }}>
                        <TextField
                            label="Ngày bắt đầu"
                            type="date"
                            value={startDate}
                            onChange={(e) => setStartDate(e.target.value)}
                            fullWidth
                            sx={{ mb: 3 }}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <CalendarTodayIcon />
                                    </InputAdornment>
                                ),
                            }}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />

                        <TextField
                            label="Ngày kết thúc"
                            type="date"
                            value={endDate}
                            onChange={(e) => setEndDate(e.target.value)}
                            fullWidth
                            sx={{ mb: 3 }}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <EventIcon />
                                    </InputAdornment>
                                ),
                            }}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />

                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={handleCloseCreateModal}
                                disabled={isCreating}
                            >
                                Hủy
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleCreateTrips}
                                disabled={isCreating}
                                startIcon={isCreating ? null : <PlayArrowIcon />}
                            >
                                {isCreating ? "Đang tạo..." : "Tạo chuyến xe"}
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Modal>

            {/* Modal chỉnh sửa trạng thái chuyến xe */}
            <Modal
                open={openEditModal}
                onClose={handleCloseEditModal}
                aria-labelledby="edit-trip-status-modal"
            >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: 400,
                    bgcolor: 'background.paper',
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2
                }}>
                    <Typography variant="h6" component="h2" sx={{ mb: 3 }}>
                        Thay đổi trạng thái chuyến xe
                    </Typography>

                    {editingTrip && (
                        <>
                            <Typography variant="body2" sx={{ mb: 2 }}>
                                <strong>Mã chuyến:</strong> {editingTrip.tripCode}
                            </Typography>
                            <Typography variant="body2" sx={{ mb: 2 }}>
                                <strong>Trạng thái hiện tại:</strong>
                                <Chip
                                    label={statusLabels[editingTrip.status]}
                                    size="small"
                                    sx={{ ml: 1 }}
                                />
                            </Typography>

                            <FormControl fullWidth sx={{ mb: 3 }}>
                                <InputLabel>Trạng thái mới</InputLabel>
                                <Select
                                    value={newStatus}
                                    onChange={(e) => setNewStatus(e.target.value)}
                                    label="Trạng thái mới"
                                >
                                    {getValidNextStatuses(editingTrip.status).map(status => (
                                        <MenuItem key={status} value={status}>
                                            {statusLabels[status]}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </>
                    )}

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                        <Button variant="outlined" onClick={handleCloseEditModal}>
                            Hủy
                        </Button>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleUpdateTripStatus}
                            disabled={!newStatus || newStatus === editingTrip?.status}
                        >
                            Cập nhật
                        </Button>
                    </Box>
                </Box>
            </Modal>
        </div>
    );
};

export default TripCreator;