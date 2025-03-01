import React, { useEffect, useState } from "react";
import { request } from "api";
import StandardTable  from "../../components/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import MapIcon from "@mui/icons-material/Map";
import { Modal, Box, Typography, Button } from '@mui/material';
import Maps from 'components/map/map';
import VisibilityIcon from "@mui/icons-material/Visibility";

function ListHub() {
    const [hubs, setHubs] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedHub, setSelectedHub] = useState(null);

    useEffect(() => {
        request("get", "/smdeli/hubmanager/hub", (res) => {
            setHubs(res.data);
        }).then();
    }, []);

    const columns = [
        {
            title: "Mã hub",
            field: "id",
        },
        {
            title: "Tên Hub",
            field: "name",
        },
        {
            title: "Vị trí",
            field: "address",
        },
        {
            title: "Xem vị trí",
            sorting: false,
            renderCell: (rowData) => (
                <IconButton
                    onClick={() => {
                        handleShowLocation(rowData);
                    }}
                    variant="contained"
                    color="primary"
                >
                    <MapIcon />
                </IconButton>
            ),
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

    const handleShowLocation = (hub) => {
        setSelectedHub(hub);
        setOpenModal(true);
    };

    const handleEdit = (hub) => {
        window.location.href = `/hubmanager/hub/update/${hub.id}`;
    };

    const handleDelete = (hub) => {
        const isConfirmed = window.confirm(`Bạn có chắc muốn xóa Hub: ${hub.name} - Địa chỉ: ${hub.address}?`);

        if (isConfirmed) {
            const data = { id: hub.id };

            request(
                "delete",
                `/smdeli/hubmanager/hub/delete`,
                (res) => {
                    if (res.status === 200) {
                        alert("Xóa hub thành công");
                        setHubs(hubs.filter(h => h.id !== hub.id));
                    }
                },
                {},
                data
            );
        }
    };

    return (
        <div>
            <StandardTable
                title="Hub List"
                columns={columns}
                data={hubs}
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
                    width: '60%',
                    height: '80%',
                    transform: 'translate(-50%, -50%)',
                    bgcolor: 'background.paper',
                    border: '2px solid #000',
                    boxShadow: 24,
                    p: 4,

                }}><Box sx={{ height: "10%" }}>
                    <Typography variant="h6" id="modal-modal-title" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        Vị trí của Hub: {selectedHub?.name}
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={() => setOpenModal(false)}
                            style={{marginBottom: '1%'  }}
                        >
                            Đóng
                        </Button>
                    </Typography>
                </Box>


                    <Box sx={{ height: "90%" }}>
                        <Maps selectPosition={{ lat: selectedHub?.latitude, lon: selectedHub?.longitude }} />
                    </Box>
                </Box>
            </Modal>
        </div>
    );
}

export default ListHub;
