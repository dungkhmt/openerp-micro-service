import React, { useEffect, useState } from "react";
import { request } from "api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import MapIcon from "@mui/icons-material/Map";
import { Modal, Box, Typography, Button } from '@mui/material';
import Maps from 'components/map/map';
import withScreenSecurity from "../../components/common/withScreenSecurity";

function OrderList() {
    const [hubs, setHubs] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedHub, setSelectedHub] = useState(null);

    useEffect(() => {
        request("get", "/smdeli/ordermanager/order", (res) => {
            setHubs(res.data);
        }).then();
    }, []);

    useEffect(() => {
        console.log("hub",hubs);
    },[hubs])

    const columns = [
        {
            title: "Mã đơn hàng",
            field: "id",
        },
        {
            title: "Người gửi",
            field: "sender.name",
        },
        {
            title: "Địa chỉ người gửi",
            field: "sender.address",
        },
        {
            title: "Người nhận",
            field: "recipient.name",
        },
        {
            title: "Địa chỉ người nhận",
            field: "recipient.address",
        },
        {
            title: "Hình thức vận chuyển",
            field: "orderType",
        },
        {
            title: "Tạo lúc",
            field: "createdAt",
            render: rowData => new Date(rowData.createdAt).toLocaleString("vi-VN", {
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            })
        },



        {
            title: "Xem",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        handleEdit(rowData);
                    }}
                    variant="contained"
                    color="success"
                >
                    <EditIcon />
                </IconButton>
            ),
        },

    ];

    const handleShowLocation = (hub) => {
        setSelectedHub(hub);
        setOpenModal(true);
    };

    const handleEdit = (hub) => {
        window.location.href = `/order/createorder`;
    };

    const handleDelete = (hub) => {
        const isConfirmed = window.confirm(`Bạn có chắc muốn xóa Đơn: ${hub.id}`);

        if (isConfirmed) {
            const data = { id: hub.id };

            request(
                "delete",
                `/smdeli/ordermanager/order/delete/${hub.id}`,
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
                title="Danh sách đơn hàng"
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
                    width: '75%',
                    height: '90%',
                    transform: 'translate(-50%, -50%)',
                    bgcolor: 'background.paper',
                    border: '2px solid #000',
                    boxShadow: 24,
                    p: 4,
                }}>
                    <Typography variant="h6" id="modal-modal-title">
                        Vị trí của Hub: {selectedHub?.name}
                    </Typography>
                    <Maps selectPosition={{ lat: selectedHub?.latitude, lon: selectedHub?.longitude }} />
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

export default OrderList;