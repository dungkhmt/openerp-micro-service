import React, { useEffect, useState } from 'react';
import { request } from "../../api";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from '@mui/icons-material/Add';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import RouteIcon from '@mui/icons-material/Route';

import StandardTable from "../../components/StandardTable";
import { Box, Button, Modal, Typography, FormControl, InputLabel, Select, MenuItem, TextField, FormGroup, FormControlLabel, Checkbox } from "@mui/material";
import { useSelector } from "react-redux";
import { errorNoti, successNoti } from "../../utils/notification";

const VehicleScheduler = () => {
    const [schedules, setSchedules] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedSchedule, setSelectedSchedule] = useState(null);
    const [routeSchedules, setRouteSchedules] = useState([]);
    const [selectedRouteSchedule, setSelectedRouteSchedule] = useState('');
    const [daysOfWeek, setDaysOfWeek] = useState([
        { value: 'MONDAY', label: 'Thứ Hai', selected: false },
        { value: 'TUESDAY', label: 'Thứ Ba', selected: false },
        { value: 'WEDNESDAY', label: 'Thứ Tư', selected: false },
        { value: 'THURSDAY', label: 'Thứ Năm', selected: false },
        { value: 'FRIDAY', label: 'Thứ Sáu', selected: false },
        { value: 'SATURDAY', label: 'Thứ Bảy', selected: false },
        { value: 'SUNDAY', label: 'Chủ Nhật', selected: false }
    ]);
    const [tripsPerDay, setTripsPerDay] = useState(2);
    const [startTime, setStartTime] = useState('08:00');
    const [endTime, setEndTime] = useState('17:00');
    const userId = useSelector((state) => state.auth.userId);

    useEffect(() => {
        // Fetch route-schedule combinations
        request("get", `/smdeli/scheduler/route-schedules`, (res) => {
            setRouteSchedules(res.data);
        }).catch(err => {
            errorNoti("Không thể tải danh sách lịch trình tuyến đường");
        });

        // Fetch existing vehicle schedule assignments
        fetchSchedules();
    }, []);

    const fetchSchedules = () => {
        request("get", `/smdeli/scheduler/vehicle-assignments`, (res) => {
            setSchedules(res.data);
        }).catch(err => {
            errorNoti("Không thể tải lịch trình");
        });
    };

    const columns = [
        {
            title: "Biển số xe",
            field: "vehiclePlateNumber",
        },
        {
            title: "Tuyến đường",
            field: "routeName",
        },
        {
            title: "Ngày trong tuần",
            field: "dayOfWeek",
            lookup: {
                'MONDAY': 'Thứ Hai',
                'TUESDAY': 'Thứ Ba',
                'WEDNESDAY': 'Thứ Tư',
                'THURSDAY': 'Thứ Năm',
                'FRIDAY': 'Thứ Sáu',
                'SATURDAY': 'Thứ Bảy',
                'SUNDAY': 'Chủ Nhật'
            }
        },
        {
            title: "Thời gian",
            field: "timeRange",
            render: (rowData) => `${rowData.startTime} - ${rowData.endTime}`
        },
        {
            title: "Số chuyến mỗi ngày",
            field: "numberOfTrips",
            type: "numeric"
        },
        {
            title: "Trạng thái",
            field: "isActive",
            lookup: { true: 'Đang hoạt động', false: 'Tạm dừng' }
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div>
                    <IconButton
                        onClick={() => handleViewSchedule(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => handleEditSchedule(rowData)}
                        color="primary"
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => handleDeleteSchedule(rowData.id)}
                        color="error"
                    >
                        <DeleteIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    const handleDayChange = (day) => {
        setDaysOfWeek(daysOfWeek.map(d =>
            d.value === day.value ? { ...d, selected: !d.selected } : d
        ));
    };

    const handleViewSchedule = (schedule) => {
        setSelectedSchedule(schedule);
        setOpenModal(true);
    };

    const handleEditSchedule = (schedule) => {
        // Redirect to edit page or open edit modal
        window.location.href = `/scheduler/edit/${schedule.id}`;
    };

    const handleDeleteSchedule = (scheduleId) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa lịch trình này?")) {
            request(
                "delete",
                `/smdeli/scheduler/vehicle-assignments/${scheduleId}`,
                (res) => {
                    successNoti("Xóa lịch trình thành công");
                    fetchSchedules(); // Refresh data
                },
                {
                    500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
                }
            );
        }
    };

    const handleCreateSchedule = () => {
        const selectedDays = daysOfWeek.filter(day => day.selected).map(day => day.value);

        if (!selectedRouteSchedule || selectedDays.length === 0) {
            errorNoti("Vui lòng chọn lịch trình tuyến đường và ít nhất một ngày trong tuần");
            return;
        }

        request(
            "post",
            `/smdeli/scheduler/vehicle-assignments`,
            (res) => {
                successNoti("Tạo lịch trình thành công");
                fetchSchedules(); // Refresh data
                handleCloseModal();
            },
            {
                400: () => errorNoti("Dữ liệu không hợp lệ"),
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            },
            {
                routeScheduleId: selectedRouteSchedule,
                days: selectedDays,
                tripsPerDay: tripsPerDay,
                startTime: startTime,
                endTime: endTime
            }
        );
    };

    const handleGenerateTrips = () => {
        request(
            "post",
            `/smdeli/scheduler/generate-trips/today`,
            (res) => {
                successNoti(`Đã tạo ${res.data} chuyến xe cho hôm nay theo lịch trình`);
            },
            {
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            }
        );
    };

    const handleCloseModal = () => {
        setOpenModal(false);
        setSelectedSchedule(null);
        setSelectedRouteSchedule('');
        setDaysOfWeek(daysOfWeek.map(day => ({ ...day, selected: false })));
        setTripsPerDay(2);
        setStartTime('08:00');
        setEndTime('17:00');
    };

    return (
        <div>
            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenModal(true)}
                >
                    Tạo lịch trình mới
                </Button>
                <Button
                    variant="contained"
                    color="success"
                    startIcon={<PlayArrowIcon />}
                    onClick={handleGenerateTrips}
                >
                    Tạo chuyến cho hôm nay
                </Button>
            </Box>

            <StandardTable
                title="Danh sách lịch trình xe"
                columns={columns}
                data={schedules}
                rowKey="id"
                editable={false}
                deletable={false}
                options={{
                    selection: true,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />

            <Modal
                open={openModal}
                onClose={handleCloseModal}
                aria-labelledby="modal-title"
                aria-describedby="modal-description"
            >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: 600,
                    bgcolor: 'background.paper',
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2
                }}>
                    <Typography id="modal-title" variant="h6" component="h2" sx={{ mb: 3 }}>
                        {selectedSchedule ? "Chi tiết lịch trình" : "Tạo lịch trình mới"}
                    </Typography>

                    {selectedSchedule ? (
                        // View schedule details
                        <Box>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Phương tiện:</strong> {selectedSchedule.vehiclePlateNumber}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Tuyến đường:</strong> {selectedSchedule.routeName}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Ngày trong tuần:</strong> {
                                columns.find(c => c.field === 'dayOfWeek').lookup[selectedSchedule.dayOfWeek]
                            }
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Thời gian:</strong> {selectedSchedule.startTime} - {selectedSchedule.endTime}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Số chuyến mỗi ngày:</strong> {selectedSchedule.numberOfTrips}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Trạng thái:</strong> {
                                selectedSchedule.isActive ? 'Đang hoạt động' : 'Tạm dừng'
                            }
                            </Typography>
                        </Box>
                    ) : (
                        // Create schedule form
                        <Box component="form" sx={{ mt: 3 }}>
                            <FormControl fullWidth sx={{ mb: 3 }}>
                                <InputLabel id="route-schedule-label">Lịch trình tuyến đường</InputLabel>
                                <Select
                                    labelId="route-schedule-label"
                                    value={selectedRouteSchedule}
                                    onChange={(e) => setSelectedRouteSchedule(e.target.value)}
                                    label="Lịch trình tuyến đường"
                                >
                                    {routeSchedules.map((rs) => (
                                        <MenuItem key={rs.id} value={rs.id}>
                                            {rs.vehiclePlateNumber} - {rs.routeName}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            <Typography variant="subtitle1" sx={{ mb: 1 }}>Chọn ngày trong tuần:</Typography>
                            <FormGroup row sx={{ mb: 3 }}>
                                {daysOfWeek.map((day) => (
                                    <FormControlLabel
                                        key={day.value}
                                        control={
                                            <Checkbox
                                                checked={day.selected}
                                                onChange={() => handleDayChange(day)}
                                            />
                                        }
                                        label={day.label}
                                    />
                                ))}
                            </FormGroup>

                            <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                                <FormControl fullWidth>
                                    <InputLabel id="trips-label">Số chuyến mỗi ngày</InputLabel>
                                    <Select
                                        labelId="trips-label"
                                        value={tripsPerDay}
                                        onChange={(e) => setTripsPerDay(e.target.value)}
                                        label="Số chuyến mỗi ngày"
                                    >
                                        {[1, 2, 3, 4, 5].map((num) => (
                                            <MenuItem key={num} value={num}>{num} chuyến</MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Box>

                            <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                                <TextField
                                    label="Giờ bắt đầu"
                                    type="time"
                                    value={startTime}
                                    onChange={(e) => setStartTime(e.target.value)}
                                    fullWidth
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    inputProps={{
                                        step: 300, // 5 min
                                    }}
                                />
                                <TextField
                                    label="Giờ kết thúc"
                                    type="time"
                                    value={endTime}
                                    onChange={(e) => setEndTime(e.target.value)}
                                    fullWidth
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    inputProps={{
                                        step: 300, // 5 min
                                    }}
                                />
                            </Box>

                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 3 }}>
                                <Button variant="outlined" onClick={handleCloseModal}>Hủy</Button>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handleCreateSchedule}
                                >
                                    Tạo lịch trình
                                </Button>
                            </Box>
                        </Box>
                    )}
                </Box>
            </Modal>
        </div>
    );
};

export default VehicleScheduler;