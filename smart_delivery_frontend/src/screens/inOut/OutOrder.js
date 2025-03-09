import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import CheckIcon from '@mui/icons-material/Check';
import StandardTable from "../../components/StandardTable";
import {Box, Button, FormControl, InputLabel, MenuItem, Modal, Select, Typography} from "@mui/material";
import {useSelector} from "react-redux";
import {errorNoti, successNoti} from "../../utils/notification";

const OutOrder = () => {
    const [orders, setOrders] = useState([]);
    const [showOrderModal, setShowOrderModal] = useState(false);
    const hubId = useSelector((state) => state.auth.hubId);
    const [vehicles, setVehicles] = useState([]);
    const [selectedOrderIds, setSelectedOrderIds] = useState([]);
    const [selectedVehicleId, setSelectedVehicleId] = useState(null);
    const [selectedVehicles, setSelectedVehicles] = useState([]); // This is for StandardTable's selection mechanism
    const [direction, setDirection] = useState('OUTBOUND'); // Default direction

    // Update selectedVehicleId whenever selectedVehicles changes
    useEffect(() => {
        if (selectedVehicles.length > 0) {
            setSelectedVehicleId(selectedVehicles[0].vehicleId);
        } else {
            setSelectedVehicleId(null);
        }
    }, [selectedVehicles]);

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
        // {
        //     title: "Loại hình",
        //     field: "orderType"
        // },
        {
            title: "Hub đích",
            field: "hubName",
            renderCell: (rowData) => {
                // Check if hubCode exists
                if (rowData.hubCode) {
                    return `${rowData.hubName}: ${rowData.hubCode}`;
                }
                // Fallback to just hubName if hubCode doesn't exist
                return rowData.hubName;
            }
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
                        onClick={() => handleSaveVehicleSelection(rowData.id)}
                        color="success"
                    >
                        <CheckIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    const vehicleColumns = [
        {
            title: "ID",
            field: "vehicleId",
        },
        {
            title: "Biển số xe",
            field: "plateNumber",
        },
        {
            title: "Loại xe",
            field: "vehicleType",
        },
        {
            title: "Tình trạng",
            field: "status",
            renderCell: (rowData) => formatVehicleStatus(rowData.status)
        },
        {
            title: "Nhà sản xuất",
            field: "manufacturer"
        },
        {
            title: "Mẫu xe",
            field: "model"
        },
        {
            title: "Tài xế",
            field: "driverName"
        }
    ];

    const handleEdit = (order) => {
        window.location.href = `/hubmanager/hub/update/${order.id}`;
    };

    useEffect(() => {
            const fetchVehicles = () => {
                request(
                    "get",
                    `smdeli/vehicle/getAll/${hubId}`,
                    (res) => {
                        setVehicles(res.data);
                    },
                    {
                        500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
                    }
                );
            }
            fetchVehicles();
        }
        , [hubId]
    )

    // This function will be called when a vehicle row is clicked in the table
    const handleVehicleRowClick = (event, selectedRow) => {
        // If the vehicle is already selected, deselect it
        if (selectedVehicles.some(v => v.vehicleId === selectedRow.vehicleId)) {
            setSelectedVehicles([]);
        } else {
            // Otherwise select only this vehicle
            setSelectedVehicles([selectedRow]);
        }
    };

    // Handle direction change
    const handleDirectionChange = (event) => {
        setDirection(event.target.value);
    };

    const handleFetchOrderForVehicle = () => {
        if (!selectedVehicleId) {
            errorNoti("Vui lòng chọn phương tiện vận chuyển");
            return;
        }

        request("get", `/smdeli/middle-mile/orders/vehicle/${selectedVehicleId}/${hubId}/${direction}`, (res) => {
                setOrders(res.data);
                setShowOrderModal(true);
            },
            {
                400: () => errorNoti("Dữ liệu không hợp lệ"),
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            }
        );
    };

    const handleSaveVehicleSelection = (selectedIds) => {
        if (!selectedVehicleId) {
            errorNoti("Vui lòng chọn phương tiện vận chuyển");
            return;
        }
        const ids = Array.isArray(selectedIds) && selectedIds.length > 1
            ? selectedIds
            : [selectedIds];

        setSelectedOrderIds(ids);
        console.log("selected", selectedIds);

        // Gửi yêu cầu để lưu việc chọn phương tiện cho các đơn hàng
        request(
            "put",
            `smdeli/ordermanager/out-hub/complete/${selectedOrderIds}/${selectedVehicleId}`,
            (res) => {
                successNoti("Đã chọn phương tiện vận chuyển thành công");
                setShowOrderModal(true);
            },
            {
                400: () => errorNoti("Dữ liệu không hợp lệ"),
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
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
                title="Danh sách phương tiện vận chuyển"
                columns={vehicleColumns}
                data={vehicles}
                rowKey="vehicleId"
                onRowClick={handleVehicleRowClick}
                selectedData={selectedVehicles}
                options={{
                    selection: true,
                    selectionProps: {
                        color: 'primary'
                    },
                    pageSize: 5,
                    search: true,
                    sorting: true,
                    headerStyle: {
                        backgroundColor: '#f5f5f5',
                        fontWeight: 'bold'
                    }
                }}
            />

            {/* Status message showing selected vehicle */}
            <Box sx={{mt: 2, p: 2, borderRadius: 1, bgcolor: selectedVehicleId ? '#e8f5e9' : '#f5f5f5'}}>
                <Typography variant="body1" sx={{fontWeight: 'bold', color: selectedVehicleId ? 'green' : 'gray'}}>
                    {selectedVehicleId ?
                        `Phương tiện đã chọn: ${vehicles.find(v => v.vehicleId === selectedVehicleId)?.plateNumber || ''}` :
                        'Vui lòng chọn phương tiện vận chuyển bằng cách nhấp vào hàng tương ứng'}
                </Typography>
            </Box>

            {/* Direction selection */}
            <Box sx={{mt: 2, display: 'flex', alignItems: 'center', gap: 2}}>
                <FormControl sx={{minWidth: 200}}>
                    <InputLabel id="direction-select-label">Hướng vận chuyển</InputLabel>
                    <Select
                        labelId="direction-select-label"
                        id="direction-select"
                        value={direction}
                        label="Hướng vận chuyển"
                        onChange={handleDirectionChange}
                    >
                        <MenuItem value="OUTBOUND">Chiều đi (OUTBOUND)</MenuItem>
                        <MenuItem value="INBOUND">Chiều về (INBOUND)</MenuItem>
                    </Select>
                </FormControl>
            </Box>

            <Box sx={{display: 'flex', justifyContent: 'flex-end', mt: 3}}>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleFetchOrderForVehicle}
                    disabled={!selectedVehicleId}
                >
                    Gán đơn hàng
                </Button>
            </Box>

            <Modal
                open={showOrderModal}
                onClose={() => setShowOrderModal(false)}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box
                    sx={{
                        position: "absolute",
                        top: "50%",
                        left: "50%",
                        width: "80%",
                        maxHeight: "80vh", // Limit the modal height to 80% of viewport height
                        transform: "translate(-50%, -50%)",
                        bgcolor: "background.paper",
                        border: "2px solid #000",
                        boxShadow: 24,
                        p: 4,
                        overflow: "auto", // Add scrolling to the modal if content exceeds height
                    }}
                >
                    <Typography variant="h6" component="h2" sx={{mb: 2}}>
                        {'Danh sách đơn hàng'}
                    </Typography>

                    <Box sx={{ maxHeight: "100vh" }}> {/* Container with max height for the table */}
                        <StandardTable
                            title={`Danh sách đơn hàng dành cho xe - ${direction === 'INBOUND' ? 'CHIỀU VỀ' : 'CHIỀU ĐI'}`}
                            columns={columns}
                            onRowClick={handleVehicleRowClick}
                            data={orders}
                            actions={[
                                {
                                    iconOnClickHandle: handleSaveVehicleSelection,
                                    tooltip: "Duyệt xuất",
                                }
                            ]}
                            options={{
                                selection: true,
                                pageSize: 10,
                                search: true,
                                sorting: true,
                                maxBodyHeight: "350px", // Control table body height
                            }}
                            rowKey={"id"}
                        />
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                            <Button
                                variant="outlined"
                                onClick={() => setShowOrderModal(false)}
                                sx={{ ml: 1 }}
                            >
                                Đóng
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Modal>
        </div>
    );
}

export default OutOrder;