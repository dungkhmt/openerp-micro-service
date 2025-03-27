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
import SearchIcon from '@mui/icons-material/Search';

import StandardTable from "../../components/StandardTable";
import {
    Box,
    Button,
    Modal,
    Typography,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    TextField,
    FormGroup,
    FormControlLabel,
    Checkbox,
    InputAdornment,
    Autocomplete
} from "@mui/material";
import { useSelector } from "react-redux";
import { errorNoti, successNoti } from "../../utils/notification";
import { useHistory } from "react-router-dom";

const VehicleScheduler = () => {
    const [assignments, setAssignments] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedAssignment, setSelectedAssignment] = useState(null);
    const [routeSchedules, setRouteSchedules] = useState([]);
    const [selectedRouteSchedule, setSelectedRouteSchedule] = useState(null);
    const [vehicles, setVehicles] = useState([]);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [assignmentDate, setAssignmentDate] = useState(new Date().toISOString().split('T')[0]);
    const userId = useSelector((state) => state.auth.userId);
    const history = useHistory();

    useEffect(() => {
        // Fetch route schedules
        request("get", `/smdeli/route-scheduler/schedules`, (res) => {
            setRouteSchedules(res.data || []);
        }).catch(err => {
            errorNoti("Không thể tải danh sách lịch trình tuyến đường");
        });

        // Fetch vehicles
        request("get", `/smdeli/vehicle/getAll`, (res) => {
            setVehicles(res.data);
        }).catch(err => {
            errorNoti("Không thể tải danh sách phương tiện");
        });

        // Fetch existing vehicle assignments
        fetchAssignments();
    }, []);

    const fetchAssignments = () => {
        request("get", `/smdeli/schedule-assignments/active`, (res) => {
            setAssignments(res.data || []);
        }).catch(err => {
            errorNoti("Không thể tải lịch trình phân công");
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
            title: "Ngày phân công",
            field: "assignmentDate",
        },
        {
            title: "Lịch trình",
            field: "routeScheduleDto",
            renderCell: (rowData) => {
                const schedule = rowData.routeScheduleDto;
                if (schedule) {
                    return `${schedule.dayOfWeek} (${schedule.startTime} - ${schedule.endTime})`;
                }
                return "N/A";
            }     },
        {
            title: "Trạng thái",
            field: "active",
            renderCell: (rowData) => {
                if (rowData.active === true) {
                    return "ACTIVE";
                }
                else if (rowData.active === false) {
                    return "INACTIVE";
                }
                return "N/A";
            }
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div>
                    <IconButton
                        onClick={() => handleViewAssignment(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => handleDeleteAssignment(rowData.id)}
                        color="error"
                    >
                        <DeleteIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    const handleViewAssignment = (assignment) => {
        setSelectedAssignment(assignment);
        setOpenModal(true);
    };

    const handleDeleteAssignment = (assignmentId) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa phân công này?")) {
            request(
                "delete",
                `/smdeli/schedule-assignments/${assignmentId}`,
                (res) => {
                    successNoti("Xóa phân công thành công");
                    fetchAssignments(); // Refresh data
                },
                {
                    500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
                }
            );
        }
    };

    const handleCreateAssignment = () => {
        if (!selectedRouteSchedule || !selectedVehicle) {
            errorNoti("Vui lòng chọn lịch trình tuyến đường và phương tiện");
            return;
        }

        request(
            "post",
            `/smdeli/schedule-assignments/assign`,
            (res) => {
                successNoti("Phân công phương tiện thành công");
                fetchAssignments(); // Refresh data
                handleCloseModal();
            },
            {
                400: () => errorNoti("Dữ liệu không hợp lệ"),
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            },
            {
                scheduleId: selectedRouteSchedule.id,
                vehicleId: selectedVehicle.vehicleId,
                assignmentDate: assignmentDate
            }
        );
    };

    const handleGenerateTrips = () => {
        request(
            "post",
            `/smdeli/route-scheduler/generate-trips/today`,
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
        setSelectedAssignment(null);
        setSelectedRouteSchedule(null);
        setSelectedVehicle(null);
        setAssignmentDate(new Date().toISOString().split('T')[0]);
    };

    // Get route schedule option label
    const getRouteScheduleLabel = (option) => {
        return `${option.routeCode} - ${option.routeName} (${option.dayOfWeek}: ${option.startTime}-${option.endTime})`;
    };

    // Get vehicle option label
    const getVehicleLabel = (option) => {
        return `${option.plateNumber} - ${option.vehicleType} (${option.status})`;
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
                    Phân công phương tiện
                </Button>
            </Box>

            <StandardTable
                title="Danh sách phân công phương tiện"
                columns={columns}
                data={assignments}
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
                        {selectedAssignment ? "Chi tiết phân công" : "Phân công phương tiện mới"}
                    </Typography>

                    {selectedAssignment ? (
                        // View assignment details
                        <Box>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Phương tiện:</strong> {selectedAssignment.vehiclePlateNumber}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Tuyến đường:</strong> {selectedAssignment.routeName}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Ngày phân công:</strong> {selectedAssignment.assignmentDate}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Ngày trong tuần:</strong> {selectedAssignment.dayOfWeek}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Thời gian:</strong> {selectedAssignment.startTime} - {selectedAssignment.endTime}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Trạng thái:</strong> {
                                selectedAssignment.isActive ? 'Đang hoạt động' : 'Tạm dừng'
                            }
                            </Typography>
                        </Box>
                    ) : (
                        // Create assignment form
                        <Box component="form" sx={{ mt: 3 }}>
                            <FormControl fullWidth sx={{ mb: 3 }}>
                                <Autocomplete
                                    id="route-schedule-autocomplete"
                                    options={routeSchedules}
                                    getOptionLabel={getRouteScheduleLabel}
                                    value={selectedRouteSchedule}
                                    onChange={(event, newValue) => {
                                        setSelectedRouteSchedule(newValue);
                                    }}
                                    renderInput={(params) => (
                                        <TextField
                                            {...params}
                                            label="Lịch trình tuyến đường"
                                            InputProps={{
                                                ...params.InputProps,
                                                startAdornment: (
                                                    <>
                                                        <InputAdornment position="start">
                                                            <RouteIcon />
                                                        </InputAdornment>
                                                        {params.InputProps.startAdornment}
                                                    </>
                                                )
                                            }}
                                        />
                                    )}
                                />
                            </FormControl>

                            <FormControl fullWidth sx={{ mb: 3 }}>
                                <Autocomplete
                                    id="vehicle-autocomplete"
                                    options={vehicles}
                                    getOptionLabel={getVehicleLabel}
                                    value={selectedVehicle}
                                    onChange={(event, newValue) => {
                                        setSelectedVehicle(newValue);
                                    }}
                                    renderInput={(params) => (
                                        <TextField
                                            {...params}
                                            label="Phương tiện"
                                            InputProps={{
                                                ...params.InputProps,
                                                startAdornment: (
                                                    <>
                                                        <InputAdornment position="start">
                                                            <DirectionsCarIcon />
                                                        </InputAdornment>
                                                        {params.InputProps.startAdornment}
                                                    </>
                                                )
                                            }}
                                        />
                                    )}
                                />
                            </FormControl>

                            <TextField
                                label="Ngày phân công"
                                type="date"
                                value={assignmentDate}
                                onChange={(e) => setAssignmentDate(e.target.value)}
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

                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 3 }}>
                                <Button variant="outlined" onClick={handleCloseModal}>Hủy</Button>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handleCreateAssignment}
                                >
                                    Phân công
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