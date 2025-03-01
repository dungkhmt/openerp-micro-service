import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import IconButton from "@mui/material/IconButton";
import MapIcon from "@mui/icons-material/Map";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckIcon from '@mui/icons-material/Check';
import SearchIcon from '@mui/icons-material/Search';
import InputAdornment from '@mui/material/InputAdornment';
import StandardTable from "../../components/StandardTable";
import {Box, Button, FormControl, InputLabel, MenuItem, Modal, Select, TextField, Typography} from "@mui/material";
import Maps from "../../components/map/map";
import {useSelector} from "react-redux";
import SaveIcon from "@mui/icons-material/Save";
import {errorNoti, successNoti} from "../../utils/notification";
import Toolbar from "@mui/material/Toolbar";
import Tooltip from "@mui/material/Tooltip";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableSortLabel from "@mui/material/TableSortLabel";
import TableBody from "@mui/material/TableBody";

const OutOrder = () =>{
    const [orders, setOrders] = useState([]);
    const [showAddModal, setShowAddModal] = useState(false);
    const [selectedHub, setSelectedHub] = useState(null);
    const hubId = useSelector((state) => state.auth.hubId);
    const [vehicles, setVehicles] = useState([]);
    const [filteredVehicles, setFilteredVehicles] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedVehicle, setSelectedVehicle] = useState('');
    const [selectedOrderIds, setSelectedOrderIds] = useState([]);

    useEffect(() => {
        request("get", `/smdeli/ordermanager/order/collected-hub/${hubId}`, (res) => {
            setOrders(res.data);
        }).then();
    }, []);

    useEffect(() => {
        // Filter vehicles when search term changes
        if (vehicles.length > 0) {
            const results = vehicles.filter(vehicle =>
                (vehicle.plateNumber && vehicle.plateNumber.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (vehicle.vehicleType && vehicle.vehicleType.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (vehicle.status && vehicle.status.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (vehicle.manufacturer && vehicle.manufacturer.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (vehicle.model && vehicle.model.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (vehicle.driverName && vehicle.driverName.toLowerCase().includes(searchTerm.toLowerCase()))
            );
            setFilteredVehicles(results);
        }
    }, [searchTerm, vehicles]);

    const columns = [
        {
            title: "Mã đơn hàng",
            field: "id",
        },
        {
            title: "Người gửi",
            field: "senderName",
        },
        {
            title: "Người nhận",
            field: "recipientName",
        },
        {
            title: "Loại hình",
            field: "orderType"
        },
        {
            title: "Trạng thái",
            field: "status"
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div>
                    <IconButton
                        onClick={() => handleEdit(rowData)}
                        color="success"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => confirmHandle(rowData.id)}
                        color="success"
                    >
                        <CheckIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    const handleShowLocation = (hub) => {
        setSelectedHub(hub);
    };

    const handleEdit = (hub) => {
        window.location.href = `/hubmanager/hub/update/${hub.id}`;
    };

    const confirmHandle = (selectedIds) => {
        const ids = Array.isArray(selectedIds) && selectedIds.length > 1
            ? selectedIds
            : [selectedIds];

        setSelectedOrderIds(ids);
        setShowAddModal(true);
        setSearchTerm('');

        request(
            "get",
            `smdeli/vehicle/getAll/${hubId}`,
            (res) => {
                setVehicles(res.data);
                setFilteredVehicles(res.data);
            },
            {
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            }
        );
    };

    const handleVehicleChange = (event) => {
        setSelectedVehicle(event.target.value);
    };

    const handleSearchChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleSaveVehicleSelection = () => {
        if (!selectedVehicle) {
            errorNoti("Vui lòng chọn phương tiện vận chuyển");
            return;
        }

        // Gửi yêu cầu để lưu việc chọn phương tiện cho các đơn hàng
        request(
            "post",
            "smdeli/ordermanager/order/assign-vehicle",
            (res) => {
                successNoti("Đã chọn phương tiện vận chuyển thành công");
                setShowAddModal(false);
                // Cập nhật lại danh sách đơn hàng
                request("get", `/smdeli/ordermanager/order/collected-hub/${hubId}`, (res) => {
                    setOrders(res.data);
                }).then();
            },
            {
                400: () => errorNoti("Dữ liệu không hợp lệ"),
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            },
            {
                orderIds: selectedOrderIds,
                vehicleId: selectedVehicle
            }
        );
    };

    // Format hiển thị trạng thái xe
    const formatVehicleStatus = (status) => {
        if (!status) return "Không xác định";

        switch(status) {
            case "AVAILABLE": return "Sẵn sàng";
            case "BUSY": return "Đang sử dụng";
            case "MAINTENANCE": return "Bảo trì";
            default: return status;
        }
    };

    return (
        <div>
            <StandardTable
                title="Danh sách đơn hàng đã thu gom"
                columns={columns}
                data={orders}
                rowKey="id"
                editable={true}
                deletable={false}
                actions={[
                    {
                        iconOnClickHandle: confirmHandle,
                        tooltip: "Duyệt xuất",
                    }
                ]}
                options={{
                    selection: true,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />

            <Modal
                open={showAddModal}
                onClose={() => setShowAddModal(false)}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box
                    sx={{
                        position: "absolute",
                        top: "50%",
                        left: "50%",
                        width: "50%",
                        transform: "translate(-50%, -50%)",
                        bgcolor: "background.paper",
                        border: "2px solid #000",
                        boxShadow: 24,
                        p: 4,
                    }}
                >
                    <Typography variant="h6" component="h2" mb={3}>
                        Chọn phương tiện vận chuyển
                    </Typography>

                    <TextField
                        fullWidth
                        id="vehicle-search"
                        label="Tìm kiếm phương tiện"
                        variant="outlined"
                        value={searchTerm}
                        onChange={handleSearchChange}
                        margin="normal"
                        InputProps={{
                            startAdornment: (
                                <InputAdornment position="start">
                                    <SearchIcon />
                                </InputAdornment>
                            ),
                        }}
                    />

                    <FormControl variant="outlined" fullWidth sx={{ mt: 2 }}>
                        <InputLabel id="vehicle-select-label">Phương tiện</InputLabel>
                        <Select
                            labelId="vehicle-select-label"
                            id="vehicle-select"
                            value={selectedVehicle}
                            label="Phương tiện"
                            onChange={handleVehicleChange}
                            variant="outlined"
                        >
                            {filteredVehicles && filteredVehicles.map((vehicle) => (
                                <MenuItem key={vehicle.vehicleId} value={vehicle.vehicleId}>
                                    {vehicle.plateNumber} - {vehicle.model} - {formatVehicleStatus(vehicle.status)}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleSaveVehicleSelection}
                            startIcon={<SaveIcon />}
                        >
                            Lưu
                        </Button>
                        <Button
                            variant="outlined"
                            onClick={() => setShowAddModal(false)}
                            sx={{ ml: 2 }}
                        >
                            Hủy
                        </Button>
                    </Box>
                </Box>
            </Modal>
        </div>
    );
}

export default OutOrder;