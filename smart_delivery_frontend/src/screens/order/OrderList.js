import React, { useEffect, useState } from "react";
import { request } from "api";
import StandardTable  from "../../components/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import MapIcon from "@mui/icons-material/Map";
import { Modal, Box, Typography, Button } from '@mui/material';
import Maps from 'components/map/map';
import withScreenSecurity from "../../components/common/withScreenSecurity";
import {successNoti} from "../../utils/notification";
import VisibilityIcon from "@mui/icons-material/Visibility";
import {useSelector} from "react-redux";

function OrderList() {
    const [orders, setOrders] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const role = useSelector((state) => state.auth.role);
    const hubId = useSelector((state) => state.auth.hubId);
    const auth = useSelector((state) => state.auth);


    useEffect(() => {
        let url = "/smdeli/ordermanager/order"
        console.log("Role",role);
        console.log("auth",auth)
        if(role === "HUB_MANAGER") {
            url += "/hub/" + hubId;
        }
        request("get", url, (res) => {
            setOrders(res.data);
        }).then();
    }, []);



    const columns = [
        {
            title: "Mã đơn hàng",
            field: "id",
            cellStyle: { color: 'blue' }, // Màu cho mã đơn hàng
            sorting: false,


        },
        {
            title: "Người gửi",
            field: "senderName",
            sorting: false,

        },
        {
            title: "Người nhận",
            field: "recipientName",
            sorting: false,
            headerStyle: {
                whiteSpace: 'nowrap', // Ngăn tiêu đề xuống dòng
            },

        },
        {
            title: "Hàng hóa",
            field: "recipient",
            sorting: false,

        },
        {
            title: "Trạng thái",
            field: "status",
            sorting: false,

        },
        {
            title: "Ngày lập",
            field: "createdAt",
            headerStyle: {
                whiteSpace: 'nowrap', // Ngăn tiêu đề xuống dòng
            },
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
            title: "Thu hộ",
            field: "totalPrice",
            cellStyle: { color: 'blue' },
            sorting: false,

        },
        {
            title: "Tổng cước",
            field: "shippingPrice",
            cellStyle: { color: 'blue' },
            sorting: false,

        },
        {
            title: "In/chưa in",
            sorting: false,

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

    const handleShowLocation = (order) => {
        setSelectedOrder(order);
        setOpenModal(true);
    };

    const handleEdit = (order) => {
        window.location.href = `/order/update/${order.id}`;
    };


    const handleDelete = (order) => {
        const isConfirmed = window.confirm(`Bạn có chắc muốn xóa Đơn: ${order.id}`);

        if (isConfirmed) {
            const data = { id: order.id };

            request(
                "delete",
                `/smdeli/ordermanager/order/delete/${order.id}`,
                (res) => {
                    if (res.status === 204) {
                        successNoti('Order deleted successfully!');
                        setOrders(orders.filter(o => o.id !== order.id));
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
                data={orders}
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />

        </div>
    );
}

export default OrderList;