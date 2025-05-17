import React, { useEffect, useState } from "react";
import { request } from "api";
import StandardTable from "../../components/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { useSelector } from "react-redux";
import { successNoti, errorNoti } from "../../utils/notification";
import Button from "@mui/material/Button";

function OrderList() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedOrders, setSelectedOrders] = useState([]);

    // Get user data from Redux store
    const role = useSelector((state) => state.auth.user?.role);
    const hubId = useSelector((state) => state.auth.hubId);
    const username = useSelector((state) => state.auth.user.username);

    // Function to parse raw order data if it comes in an unexpected format
    const parseOrderData = (rawData) => {
        if (!Array.isArray(rawData)) {
            console.error("Data is not an array:", rawData);
            return [];
        }

        // If data appears to be already in the correct format
        if (rawData.length > 0 && typeof rawData[0] === 'object' && rawData[0].id) {
            return rawData;
        }

        // For raw string data that needs parsing
        try {
            const parsedOrders = rawData.map(rawOrder => {
                // Try to extract data using regex patterns
                const idMatch = /^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i.exec(rawOrder);
                const statusMatch = /(PENDING|DELIVERED|ASSIGNED_SHIPPER|ASSIGNED_SHIPPE)/i.exec(rawOrder);
                const dateMatch = /(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+\d{2}:\d{2})/i.exec(rawOrder);

                // Extract names (assuming they come after ID and before status)
                let senderName = "";
                let recipientName = "";

                if (idMatch && statusMatch) {
                    const afterId = rawOrder.substring(idMatch[0].length);
                    const beforeStatus = afterId.substring(0, afterId.indexOf(statusMatch[0]));

                    // If there are names, try to extract them
                    if (beforeStatus.length > 0) {
                        const names = beforeStatus.split(/(?=[A-Z][a-z])/);
                        if (names.length >= 2) {
                            // Simple heuristic - divide the string in half for sender and recipient
                            const midpoint = Math.floor(names.length / 2);
                            senderName = names.slice(0, midpoint).join('');
                            recipientName = names.slice(midpoint).join('');
                        }
                    }
                }

                // Extract price info (assuming they are numerical values after the date)
                let totalPrice = 0;
                let shippingPrice = 0;

                if (dateMatch) {
                    const afterDate = rawOrder.substring(rawOrder.indexOf(dateMatch[0]) + dateMatch[0].length);
                    const numbers = afterDate.match(/\d+(\.\d+)?/g);

                    if (numbers && numbers.length >= 2) {
                        totalPrice = parseFloat(numbers[0]);
                        shippingPrice = parseFloat(numbers[1]);
                    }
                }

                return {
                    id: idMatch ? idMatch[0] : `unknown-${Math.random()}`,
                    senderName,
                    recipientName,
                    status: statusMatch ? statusMatch[0] : "Unknown",
                    createdAt: dateMatch ? dateMatch[0] : new Date().toISOString(),
                    totalPrice,
                    shippingPrice,
                    isPrinted: false // Default value since we don't know the actual print status
                };
            });

            return parsedOrders;
        } catch (error) {
            console.error("Error parsing order data:", error);
            return [];
        }
    };

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
            const parsedOrders = parseOrderData(res.data);
            setOrders(parsedOrders);
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
            renderCell: (rowData) => (
                <span style={{ color: 'blue', cursor: 'pointer' }}>
                    {rowData.id.length > 12 ? `${rowData.id.substring(0, 12)}...` : rowData.id}
                </span>
            )
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
            renderCell: (rowData) => {
                let color = "black";
                switch (rowData.status) {
                    case "PENDING":
                        color = "orange";
                        break;
                    case "DELIVERED":
                        color = "green";
                        break;
                    case "ASSIGNED_SHIPPER":
                    case "ASSIGNED_SHIPPE":
                        color = "blue";
                        break;
                    default:
                        color = "black";
                }
                return <span style={{ color }}>{rowData.status}</span>;
            }
        },
        {
            title: "Ngày lập",
            field: "createdAt",
            headerStyle: {
                whiteSpace: 'nowrap',
            },
            renderCell: (rowData) => {
                try {
                    return new Date(rowData.createdAt).toLocaleString("vi-VN", {
                        hour: '2-digit',
                        minute: '2-digit',
                        second: '2-digit',
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric',
                    });
                } catch (error) {
                    console.error("Date parsing error:", error);
                    return rowData.createdAt || "N/A";
                }
            }
        },
        {
            title: "Thu hộ",
            field: "totalPrice",
            cellStyle: { color: 'blue' },
            sorting: false,
            renderCell: (rowData) => (
                <span style={{ color: 'blue' }}>
                    {new Intl.NumberFormat('vi-VN').format(rowData.totalPrice || 0)}
                </span>
            )
        },
        {
            title: "Tổng cước",
            field: "shippingPrice",
            cellStyle: { color: 'blue' },
            sorting: false,
            renderCell: (rowData) => (
                <span style={{ color: 'blue' }}>
                    {new Intl.NumberFormat('vi-VN').format(rowData.shippingPrice || 0)}
                </span>
            )
        },
        {
            title: "In/chưa in",
            field: "isPrinted",
            sorting: false,
            renderCell: (rowData) => rowData.isPrinted ? "Đã in" : "Chưa in"
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
                    {rowData.status === "PENDING" && (
                        <IconButton
                            style={{ padding: '5px' }}
                            onClick={() => handleDelete(rowData)}
                            color="error"
                        >
                            <DeleteIcon />
                        </IconButton>
                    )}
                </div>
            ),
        },
    ];

    // Function to handle receiving selection data from StandardTable
    const handleSelectionChange = (selectedIds, selectedRows) => {
        setSelectedOrders(selectedRows);
        console.log("Selected order IDs:", selectedIds);
        console.log("Selected order data:", selectedRows);
    };

    // Check if an order can be deleted (only PENDING orders)
    const canDeleteOrder = (order) => order.status === "PENDING";

    // Function to handle bulk operations on selected orders
    const handleBulkDelete = () => {
        if (selectedOrders.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một đơn hàng để xóa");
            return;
        }

        // Filter out only PENDING orders
        const pendingOrders = selectedOrders.filter(order => canDeleteOrder(order));

        if (pendingOrders.length === 0) {
            errorNoti("Không có đơn hàng PENDING nào được chọn để xóa");
            return;
        }

        // If some selected orders are not PENDING, show a specific message
        if (pendingOrders.length < selectedOrders.length) {
            const nonPendingCount = selectedOrders.length - pendingOrders.length;
            const isConfirmed = window.confirm(
                `Chỉ ${pendingOrders.length} đơn hàng ở trạng thái PENDING có thể bị xóa. ${nonPendingCount} đơn hàng khác sẽ không bị xóa. Bạn có muốn tiếp tục không?`
            );
            if (!isConfirmed) return;
        } else {
            const isConfirmed = window.confirm(`Bạn có chắc muốn xóa ${pendingOrders.length} đơn hàng đã chọn?`);
            if (!isConfirmed) return;
        }

        // Create a list of promises for each delete request
        const deletePromises = pendingOrders.map(order =>
            new Promise((resolve, reject) => {
                request(
                    "delete",
                    `/smdeli/ordermanager/order/delete/${order.id}`,
                    (res) => {
                        if (res.status === 204) {
                            resolve(order.id);
                        } else {
                            reject(new Error(`Failed to delete order ${order.id}`));
                        }
                    }
                ).catch(error => {
                    reject(error);
                });
            })
        );

        // Execute all delete requests
        Promise.all(deletePromises)
            .then(deletedIds => {
                successNoti(`Đã xóa thành công ${deletedIds.length} đơn hàng`);
                // Remove deleted orders from the state
                setOrders(orders.filter(order => !deletedIds.includes(order.id)));
                setSelectedOrders([]);
            })
            .catch(error => {
                console.error("Error deleting orders:", error);
                errorNoti("Có lỗi xảy ra khi xóa đơn hàng. Vui lòng thử lại.");
            });
    };

    // Example of bulk processing for PENDING orders
    const handleBulkProcess = () => {
        if (selectedOrders.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một đơn hàng để xử lý");
            return;
        }

        // Here you can implement your bulk processing logic
        // For example, print multiple orders, change status, etc.
        console.log("Processing orders:", selectedOrders);
        alert(`Đang xử lý ${selectedOrders.length} đơn hàng đã chọn`);
    };

    const handleView = (order) => {
        // Navigate to view order page
        window.location.href = `/order/view/${order.id}`;
    };

    const handleEdit = (order) => {
        window.location.href = `/order/update/${order.id}`;
    };

    const handleDelete = (order) => {
        // Check if order can be deleted
        if (!canDeleteOrder(order)) {
            errorNoti("Chỉ đơn hàng ở trạng thái PENDING mới có thể bị xóa");
            return;
        }

        const isConfirmed = window.confirm(`Bạn có chắc muốn xóa Đơn: ${order.id}`);
        if (isConfirmed) {
            request(
                "delete",
                `/smdeli/ordermanager/order/delete/${order.id}`,
                (res) => {
                    if (res.status === 204) {
                        successNoti('Xóa đơn hàng thành công!');
                        setOrders(orders.filter(o => o.id !== order.id));
                    }
                }
            ).catch(error => {
                console.error("Error deleting order:", error);
                errorNoti("Có lỗi xảy ra khi xóa đơn hàng");
            });
        }
    };

    const getPageTitle = () => {
        if (role === "CUSTOMER") {
            return "Danh sách đơn hàng của bạn";
        }
        return "Danh sách đơn hàng";
    };

    // Count the number of pending orders among selected orders
    const pendingOrdersCount = selectedOrders.filter(order => canDeleteOrder(order)).length;

    // Custom actions for the table toolbar
    const tableActions = [
    
        {
            tooltip: "Xóa hàng loạt",
            iconOnClickHandle: handleBulkDelete
        }
    ];

    // Additional props for the editable functionality
    const editableConfig = {
        onRowDelete: (selectedIds) => {
            console.log("Handling row delete for ids:", selectedIds);
            // This function is called by the StandardTable's internal delete button
            return Promise.resolve(); // Return a promise to satisfy the interface
        }
    };

    return (
        <div>
            {selectedOrders.length > 0 && (
                <div style={{ marginBottom: '16px', display: 'flex', gap: '8px' }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleBulkProcess}
                    >
                        Xử lý {selectedOrders.length} đơn hàng
                    </Button>
                    {pendingOrdersCount > 0 && (
                        <Button
                            variant="contained"
                            color="error"
                            onClick={handleBulkDelete}
                        >
                            Xóa {pendingOrdersCount} đơn hàng PENDING
                        </Button>
                    )}
                </div>
            )}

            <StandardTable
                title={getPageTitle()}
                columns={columns}
                data={orders}
                options={{
                    selection: true, // Enable selection
                    pageSize: 20,
                    search: true,
                    sorting: true,
                    isLoading: loading
                }}
                rowKey="id"
                defaultOrderBy="createdAt"
                actions={tableActions}
                defaultOrder="desc"  // Add this line
                editable={editableConfig}
                onSelectionChange={handleSelectionChange} // Add this callback prop to get selected rows
            />
        </div>
    );
}

export default OrderList;