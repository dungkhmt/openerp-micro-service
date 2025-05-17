import React, { useEffect, useState } from "react";
import { request } from "api";
import StandardTable  from "../../../components/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from '@mui/icons-material/Visibility';

import MapIcon from "@mui/icons-material/Map";
import { Modal, Box, Typography, Button } from '@mui/material';
import Maps from 'components/map/map';
import {successNoti} from "../../../utils/notification";
import {useSelector} from "react-redux";

function DriverList() {
    const [drivers, setDrivers] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedDriver, setSelectedDriver] = useState(null);
    const hubId = useSelector((state) => state.auth.user?.hubId);
    const role = useSelector((state) => state.auth.role);


    useEffect(() => {
        let url = "/smdeli/humanresource/driver"
        if(role === "HUB_MANAGER") {
            url += "/hub/" + hubId;
        }
        request("get", url, (res) => {
            setDrivers(res.data);
        }).then();
    }, []);

    const columns = [
        {
            title: "Mã nhân viên",
            field: "id",
        },
        {
            title: "Tên nhân viên",
            field: "name",
        },
        {
            title: "Số điện thoại",
            field: "phone",
        },
        {
            title: "Email",
            field: "email",
        },
        {
            title: "Thao tác",
            field: "actions", // Field này vẫn cần để tránh lỗi nếu StandardTable sử dụng nó
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => ( // Sử dụng renderCell thay vì render
                <div style={{ display: 'flex', gap: '5px', padding: '0px'}}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEdit(rowData)}
                        color="success"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEdit(rowData)}
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleDelete(rowData)}
                        color="error"
                    >
                        <DeleteIcon />
                    </IconButton>
                </div>
            ),
        },



    ];

    const handleShowLocation = (driver) => {
        setSelectedDriver(driver);
        setOpenModal(true);
    };

    const handleEdit = (driver) => {
        window.location.href = `/employee/driver/update/${driver.id}`;
    };

    const handleDelete = (driver) => {
        const isConfirmed = window.confirm(`Bạn có chắc muốn xóa Hub: ${driver.name} - Địa chỉ: ${driver.address}?`);

        if (isConfirmed) {

            request(
                "delete",
                `/smdeli/humanresource/driver/${driver.driverId}`,
                (res) => {
                    if (res.status === 200) {
                        successNoti("Xóa driver thành công",2000);
                        setDrivers(drivers.filter(c => c.driverId !== driver.driverId));
                    }
                },
                {},

            );
        }
    };

    return (
        <div>
            <StandardTable
                title="Danh sách lái xe"
                columns={columns}
                data={drivers}
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />

            {/* Modal để hiển thị bản đồ */}
            <Modal
                open={openModal}
                onClose={() => setOpenModal(false)}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    width: '75%',
                    height: '90%',
                    transform: 'translate(-50%, -50%)',
                    bgcolor: 'background.paper',
                    border: '2px solid #000',
                    boxShadow: 24,
                    p: 4,
                }}>
                    <Typography variant="h6" id="modal-modal-title">
                        Vị trí của Hub: {selectedDriver?.name}
                    </Typography>
                    <Maps selectPosition={{ lat: selectedDriver?.latitude, lon: selectedDriver?.longitude }} />
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => setOpenModal(false)}
                        style={{ marginTop: 16 }}
                    >
                        Đóng
                    </Button>
                </Box>
            </Modal>
        </div>
    );
}

export default DriverList;
