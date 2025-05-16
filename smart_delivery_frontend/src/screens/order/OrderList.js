import React, { useEffect, useState } from "react";
import { request } from "api";
import StandardTable from "../../components/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { useSelector } from "react-redux";
import { successNoti } from "../../utils/notification";

function OrderList() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    // Get user data from Redux store
    const role = useSelector((state) => state.auth.user?.role);
    const hubId = useSelector((state) => state.auth.hubId);
    const username = useSelector((state) => state.auth.user.username);

    useEffect(() => {
        // Set the appropriate URL based on user role
        let url = "/smdeli/ordermanager/order";
        console.log("role", role)
        if (role === "CUSTOMER") {
            // For customers, fetch only their orders using senderId endpoint
            url = `/smdeli/ordermanager/order/sender/${username}`;
        } else if (role === "HUB_MANAGER") {
            // For hub managers, fetch orders for their hub
            url += "/hub/" + hubId;
        }

        setLoading(true);
        request("get", url, (res) => {
            setOrders(res.data);
            setLoading(false);
        }).catch(error => {
            console.error("Error fetching orders:", error);
            setLoading(false);
        });
    }, [role, hubId, username]);

    const columns = [
        {
            title: "Mã đơn hàng",
            field: "id",
            cellStyle: { color: 'blue' },
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
                whiteSpace: 'nowrap',
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
                whiteSpace: 'nowrap',
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
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div style={{ display: 'flex', gap: '5px', padding: '0px'}}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleView(rowData)}
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

    const handleView = (order) => {
        // Navigate to view order page
        window.location.href = `/order/view/${order.id}`;
    };

    const handleEdit = (order) => {
        window.location.href = `/order/update/${order.id}`;
    };

    const handleDelete = (order) => {
        const isConfirmed = window.confirm(`Bạn có chắc muốn xóa Đơn: ${order.id}`);
        if (isConfirmed) {
            request(
                "delete",
                `/smdeli/ordermanager/order/delete/${order.id}`,
                (res) => {
                    if (res.status === 204) {
                        successNoti('Order deleted successfully!');
                        setOrders(orders.filter(o => o.id !== order.id));
                    }
                }
            );
        }
    };

    const getPageTitle = () => {
        if (role === "CUSTOMER") {
            return "Danh sách đơn hàng của bạn";
        }
        return "Danh sách đơn hàng";
    };

    return (
        <div>
            <StandardTable
                title={getPageTitle()}
                columns={columns}
                data={orders}
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                    isLoading: loading
                }}
            />
        </div>
    );
}

export default OrderList;