import LoadingScreen from "components/common/loading/loading";
import {
    Box, Button, Grid, Tab, TextField, Typography, Dialog, DialogActions,
    DialogContent, DialogTitle, Select, MenuItem, FormControl, InputLabel,
    Checkbox, Chip, Stack, Alert
} from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import React, { Fragment, useEffect, useState } from "react";
import useStyles from "screens/styles";
import { errorNoti, processingNoti, successNoti, warningNoti } from "utils/notification";
import withScreenSecurity from "components/common/withScreenSecurity";
import { useHistory } from "react-router";
import { Link, useRouteMatch } from "react-router-dom";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { useSelector } from "react-redux";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import SaveIcon from "@mui/icons-material/Save";
import RestoreIcon from "@mui/icons-material/Restore";
import SwapHorizIcon from "@mui/icons-material/SwapHoriz";

const AssignOrderShipper = (props) => {
    const history = useHistory();
    const { path } = useRouteMatch();
    const orderId = props.match?.params?.id;
    const classes = useStyles();

    // Basic state
    const [loading, setLoading] = useState(true);
    const [tabValue, setTabValue] = useState("1");
    const hubId = useSelector((state) => state.auth.user?.hubId);

    // Data state
    const [orders, setOrders] = useState([]);
    const [shippers, setShippers] = useState([]);
    const [assignmentData, setAssignmentData] = useState([]);

    // Suggestion workflow state
    const [suggestionMode, setSuggestionMode] = useState(false);
    const [suggestedAssignments, setSuggestedAssignments] = useState([]);
    const [modifiedAssignments, setModifiedAssignments] = useState([]);
    const [selectedAssignments, setSelectedAssignments] = useState([]);

    // Selection state for unassigned orders
    const [selectedOrders, setSelectedOrders] = useState([]);

    // Saved suggestions state
    const [savedSuggestions, setSavedSuggestions] = useState([]);
    const [hasSavedSuggestion, setHasSavedSuggestion] = useState(false);

    // Dialogs state
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [bulkEditDialogOpen, setBulkEditDialogOpen] = useState(false);
    const [selectedShipperForEdit, setSelectedShipperForEdit] = useState("");
    const [selectedShipperForBulkEdit, setSelectedShipperForBulkEdit] = useState("");

    // State cho single edit dialog
    const [editingAssignment, setEditingAssignment] = useState(null);

    // Load saved suggestions from localStorage on component mount
    useEffect(() => {
        const saved = localStorage.getItem(`shipperAssignmentSuggestion_${hubId}`);
        if (saved) {
            try {
                const parsedSaved = JSON.parse(saved);
                setSavedSuggestions(parsedSaved);
                setHasSavedSuggestion(true);
            } catch (error) {
                console.error("Error parsing saved suggestions:", error);
                localStorage.removeItem(`shipperAssignmentSuggestion_${hubId}`);
            }
        }
    }, [hubId]);

    // Debug selectedAssignments changes
    useEffect(() => {
        console.log("selectedAssignments updated:", selectedAssignments);
        console.log("selectedAssignments length:", selectedAssignments.length);
    }, [selectedAssignments]);

    // Debug dialog states
    useEffect(() => {
        console.log("editDialogOpen:", editDialogOpen);
        console.log("bulkEditDialogOpen:", bulkEditDialogOpen);
    }, [editDialogOpen, bulkEditDialogOpen]);

    // Debug shippers data
    useEffect(() => {
        console.log("shippers:", shippers);
    }, [shippers]);

    // Fetch initial data
    useEffect(() => {
        async function fetchData() {
            try {
                // Fetch shippers
                await request(
                    "get",
                    `/smdeli/humanresource/shipper/hub/${hubId}`,
                    (res) => {
                        setShippers(res.data);
                    }
                );

                // Fetch current assignments
                await request(
                    "get",
                    `/smdeli/ordermanager/order/assign/hub/shipper/today/${hubId}`,
                    (res) => {
                        setAssignmentData(res.data);
                    }
                );

                // Fetch orders to be assigned
                await request(
                    "get",
                    `/smdeli/ordermanager/order/delivered/hub/${hubId}`,
                    (res) => {
                        setOrders(res.data);
                    }
                );

                setLoading(false);
            } catch (error) {
                errorNoti("Lỗi khi tải dữ liệu");
                setLoading(false);
            }
        }
        fetchData();
    }, [hubId]);

    // Handle selection change for unassigned orders
    const handleOrderSelectionChange = (selectedIds, selectedRows) => {
        setSelectedOrders(selectedRows || []);
    };

    // Handle selection change for assignments - sử dụng callback từ StandardTable
    const handleAssignmentSelectionChange = (selectedIds, selectedRows) => {
        setSelectedAssignments(selectedRows || []);
    };

    // Create manual assignment from selected orders
    const createManualAssignmentButtonHandle = () => {
        if (selectedOrders.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một đơn hàng");
            return;
        }

        // Create assignments with default sequence numbers
        const manualAssignments = selectedOrders.map((order, index) => ({
            id: order.id,
            senderName: order.senderName,
            recipientName: order.recipientName,
            shipperId: null, // Will be assigned later
            shipperName: "Chưa phân công",
            sequenceNumber: index + 1
        }));

        setModifiedAssignments(manualAssignments);
        setSuggestionMode(true);
        setSelectedOrders([]); // Reset orders selection
        setSelectedAssignments([]); // Reset assignments selection
        successNoti(`Đã tạo ${selectedOrders.length} phân công thủ công - Có thể chỉnh sửa trước khi xác nhận`);
    };

    // Get assignment suggestions - chỉ cho orders được selected
    const getSuggestionButtonHandle = () => {
        // Check if there are selected orders
        if (selectedOrders.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một đơn hàng để tạo đề xuất phân công");
            return;
        }

        if (shippers.length === 0) {
            errorNoti("Không có shipper nào khả dụng");
            return;
        }

        setLoading(true);

        const updatedShippers = shippers.map(shipper => ({
            id: shipper.id,
            hubId: hubId,
        }));

        request(
            "post",
            "/smdeli/ordermanager/order/suggest/shipper",
            (res) => {

                // Sắp xếp theo shipper name và sequence number
                const sortedAssignments = [...res.data].sort((a, b) => {
                    // Sắp xếp theo shipperName trước
                    if (a.shipperName < b.shipperName) return -1;
                    if (a.shipperName > b.shipperName) return 1;
                    // Nếu cùng shipper thì sắp xếp theo sequenceNumber
                    return (a.sequenceNumber || 0) - (b.sequenceNumber || 0);
                });

                setModifiedAssignments(sortedAssignments); // Set data đã được sắp xếp
                setModifiedAssignments([...res.data]); // Đảm bảo có thể sửa đổi được
                setSuggestionMode(true);
                setSelectedAssignments([]); // Reset selection
                setSelectedOrders([]); // Reset orders selection
                setLoading(false);
                successNoti(`Đã tạo đề xuất phân công cho ${selectedOrders.length} đơn hàng - Có thể chỉnh sửa trước khi xác nhận`);
            },
            {
                500: () => {
                    errorNoti("Không thể tạo đề xuất phân công");
                    setLoading(false);
                },
                400: () => {
                    errorNoti("Dữ liệu không hợp lệ");
                    setLoading(false);
                },
            },
            {
                employees: updatedShippers,
                orders: selectedOrders, // Chỉ gửi orders được selected
                hubId: hubId,
            }
        );
    };

    // Save suggestions to localStorage
    const saveSuggestionButtonHandle = () => {
        if (modifiedAssignments.length === 0) {
            errorNoti("Không có phân công nào để lưu");
            return;
        }

        const dataToSave = {
            suggestions: modifiedAssignments,
            timestamp: new Date().toISOString(),
            ordersCount: modifiedAssignments.length
        };

        localStorage.setItem(`shipperAssignmentSuggestion_${hubId}`, JSON.stringify(dataToSave));
        setSavedSuggestions(dataToSave);
        setHasSavedSuggestion(true);
        successNoti("Đã lưu đề xuất phân công");
    };

    // Load saved suggestions
    const loadSavedSuggestionButtonHandle = () => {
        if (savedSuggestions.suggestions) {
            setModifiedAssignments([...savedSuggestions.suggestions]);
            setSuggestionMode(true);
            setSelectedAssignments([]); // Reset selection
            setSelectedOrders([]); // Reset orders selection
            successNoti("Đã tải đề xuất phân công đã lưu - Có thể chỉnh sửa trước khi xác nhận");
        } else {
            errorNoti("Không có đề xuất nào đã lưu");
        }
    };

    // Clear saved suggestions
    const clearSavedSuggestionButtonHandle = () => {
        localStorage.removeItem(`shipperAssignmentSuggestion_${hubId}`);
        setSavedSuggestions([]);
        setHasSavedSuggestion(false);
        successNoti("Đã xóa đề xuất đã lưu");
    };

    // Confirm assignment after editing - chỉ gửi những order được chọn
    const confirmAssignmentButtonHandle = () => {
        if (modifiedAssignments.length === 0) {
            errorNoti("Không có phân công nào để xác nhận");
            return;
        }

        setLoading(true);

        // Transform data to match backend DTO - chỉ gửi những assignment có trong modifiedAssignments
        const assignmentsToConfirm = modifiedAssignments.map(assignment => ({
            orderId: assignment.id,
            employeeId: assignment.shipperId,
            employeeName: assignment.shipperName,
            sequenceNumber: assignment.sequenceNumber || 1
        }));

        // Chỉ gửi orderIds của những order được phân công
        const orderIds = modifiedAssignments.map(assignment => assignment.id);

        request(
            "post",
            "/smdeli/ordermanager/order/confirm/shipper",
            (res) => {
                // Clear saved suggestions after successful confirmation
                localStorage.removeItem(`shipperAssignmentSuggestion_${hubId}`);

                successNoti(`Phân công thành công ${orderIds.length} đơn hàng`);

                // Reload trang sau khi xác nhận thành công
                setTimeout(() => {
                    window.location.reload();
                }, 1000); // Delay 1 giây để user thấy notification
            },
            {
                500: () => {
                    errorNoti("Không thể xác nhận phân công");
                    setLoading(false);
                },
                400: () => {
                    errorNoti("Dữ liệu phân công không hợp lệ");
                    setLoading(false);
                },
            },
            {
                hubId: hubId,
                assignments: assignmentsToConfirm,
                orderIds: orderIds // Thêm danh sách orderIds
            }
        );
    };

    // Cancel suggestion
    const cancelSuggestionButtonHandle = () => {
        setSuggestionMode(false);
        setSuggestedAssignments([]);
        setModifiedAssignments([]);
        setSelectedAssignments([]);
        successNoti("Đã hủy đề xuất phân công");
    };

    // Open bulk edit dialog
    const handleBulkEditAssignment = () => {
        console.log("handleBulkEditAssignment called");
        console.log("selectedAssignments:", selectedAssignments);
        console.log("selectedAssignments.length:", selectedAssignments.length);

        if (selectedAssignments.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một phân công để chỉnh sửa");
            return;
        }
        setBulkEditDialogOpen(true);
    };

    // Save bulk edit
    const handleSaveBulkEdit = () => {
        if (!selectedShipperForBulkEdit) {
            errorNoti("Vui lòng chọn shipper");
            return;
        }

        const selectedShipper = shippers.find(s => s.id === selectedShipperForBulkEdit);
        if (!selectedShipper) {
            errorNoti("Shipper không hợp lệ");
            return;
        }

        const selectedIds = selectedAssignments.map(assignment => assignment.id);
        console.log("Updating assignments for IDs:", selectedIds);
        console.log("New shipper:", selectedShipper);

        setModifiedAssignments(prev => {
            const updated = prev.map(assignment =>
                selectedIds.includes(assignment.id)
                    ? {
                        ...assignment,
                        shipperId: selectedShipperForBulkEdit,
                        shipperName: selectedShipper.name
                    }
                    : assignment
            );
            console.log("Updated assignments:", updated);
            return updated;
        });

        setBulkEditDialogOpen(false);
        setSelectedShipperForBulkEdit("");
        setSelectedAssignments([]);
        successNoti(`Đã cập nhật ${selectedIds.length} phân công`);
    };

    // Delete selected assignments
    const handleDeleteSelectedAssignments = () => {
        if (selectedAssignments.length === 0) {
            errorNoti("Vui lòng chọn ít nhất một phân công để xóa");
            return;
        }

        const selectedIds = selectedAssignments.map(assignment => assignment.id);
        setModifiedAssignments(prev =>
            prev.filter(assignment => !selectedIds.includes(assignment.id))
        );
        setSelectedAssignments([]);
        successNoti(`Đã xóa ${selectedIds.length} phân công`);
    };

    // Open single edit dialog
    const handleEditSingleAssignment = (assignment) => {
        console.log("handleEditSingleAssignment called with:", assignment);
        setEditingAssignment(assignment); // Lưu assignment riêng để edit
        setSelectedShipperForEdit(assignment.shipperId || "");
        setEditDialogOpen(true);
    };

    // Save single edit
    const handleSaveSingleEdit = () => {
        if (!selectedShipperForEdit) {
            errorNoti("Vui lòng chọn shipper");
            return;
        }

        const selectedShipper = shippers.find(s => s.id === selectedShipperForEdit);
        if (!selectedShipper) {
            errorNoti("Shipper không hợp lệ");
            return;
        }

        console.log("Editing assignment:", editingAssignment);
        console.log("New shipper:", selectedShipper);

        setModifiedAssignments(prev => {
            const updated = prev.map(assignment =>
                assignment.id === editingAssignment.id
                    ? {
                        ...assignment,
                        shipperId: selectedShipperForEdit,
                        shipperName: selectedShipper.name
                    }
                    : assignment
            );
            console.log("Updated assignments after single edit:", updated);
            return updated;
        });

        setEditDialogOpen(false);
        setSelectedShipperForEdit("");
        setEditingAssignment(null);
        successNoti("Đã cập nhật phân công");
    };

    // Refresh orders list
    const refreshOrdersList = async () => {
        try {
            await request(
                "get",
                `/smdeli/ordermanager/order/delivered/hub/${hubId}`,
                (res) => {
                    setOrders(res.data);
                }
            );
        } catch (error) {
            console.error("Error refreshing orders list:", error);
        }
    };

    // Refresh assignment data
    const refreshAssignmentData = async () => {
        try {
            await request(
                "get",
                `/smdeli/ordermanager/order/assign/hub/shipper/today/${hubId}`,
                (res) => {
                    setAssignmentData(res.data);
                }
            );
        } catch (error) {
            console.error("Error refreshing assignment data:", error);
        }
    };

    // Remove single assignment from suggestions
    const handleRemoveFromSuggestion = (orderId) => {
        setModifiedAssignments(prev =>
            prev.filter(assignment => assignment.id !== orderId)
        );
        successNoti("Đã xóa khỏi danh sách phân công");
    };

    // Get shipper assignment statistics
    const getShipperStats = () => {
        const stats = {};
        modifiedAssignments.forEach(assignment => {
            if (!stats[assignment.shipperId]) {
                stats[assignment.shipperId] = {
                    name: assignment.shipperName,
                    count: 0
                };
            }
            stats[assignment.shipperId].count++;
        });
        return Object.values(stats);
    };

    // Columns for unassigned orders table
    const unassignedOrdersColumns = [
        { title: "Mã đơn hàng", field: "id" },
        { title: "Tên người gửi", field: "senderName" },
        { title: "Tên người nhận", field: "recipientName" },
        { title: "Ngày tạo đơn", field: "createdAt", type: "datetime" },
        { title: "Trạng thái", field: "status" },
        {
            title: "Thao tác",
            sorting: false,
            cellStyle: { textAlign: 'center' },
            headerStyle: { textAlign: "center" },
            renderCell: (rowData) => (
                <div style={{ display: 'flex', justifyContent: 'center', gap: '5px' }}>
                    <IconButton
                        style={{ padding: '5px' }}
                        color="primary"
                        onClick={() => {
                            console.log("View order:", rowData.id);
                        }}
                    >
                        <VisibilityIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    // Columns for suggestion table - sử dụng selection built-in
    const suggestionColumns = [
        {
            title: "Mã đơn hàng",
            field: "id",
            width: 200
        },
        {
            title: "Tên người gửi",
            field: "senderName",
            width: 150
        },
        {
            title: "Tên người nhận",
            field: "recipientName",
            width: 150
        },
        {
            title: "Shipper",
            field: "shipperName",
            width: 120
        },
        {
            title: "Thứ tự",
            field: "sequenceNumber",
            width: 80,
            type: "numeric"
        },
        {
            title: "Thao tác",
            sorting: false,
            width: 120,
            cellStyle: { textAlign: 'center', width: '120px' },
            headerStyle: { textAlign: "center", width: '120px' },
            renderCell: (rowData) => (
                <div style={{ display: 'flex', justifyContent: 'center', gap: '5px' }}>
                    <IconButton
                        style={{ padding: '5px' }}
                        color="primary"
                        onClick={() => handleEditSingleAssignment(rowData)}
                        title="Chỉnh sửa phân công"
                        size="small"
                    >
                        <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        color="error"
                        onClick={() => handleRemoveFromSuggestion(rowData.id)}
                        title="Xóa khỏi phân công"
                        size="small"
                    >
                        <DeleteIcon fontSize="small" />
                    </IconButton>
                </div>
            ),
        },
    ];

    // Columns for today's assignments
    const assignmentColumns = [
        { title: "Mã nhân viên", field: "shipperId" },
        { title: "Tên nhân viên", field: "shipperName" },
        { title: "Số đơn hàng", field: "numOfOrders" },
        { title: "Đã hoàn thành", field: "numOfCompleted" },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div style={{ display: 'flex', gap: '5px', justifyContent: 'center' }}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => {
                            window.location.href = `${window.location.pathname}/today/${rowData.shipperId}`;
                        }}
                        color="success"
                        title="Xem chi tiết"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => {
                            window.location.href = `${window.location.pathname}/today/${rowData.shipperId}`;
                        }}
                        title="Chỉnh sửa"
                    >
                        <EditIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    return loading ? (
        <LoadingScreen />
    ) : (
        <Fragment>
            <Box className={classes.bodyBox}>
                {/* Saved suggestion alert */}
                {hasSavedSuggestion && !suggestionMode && (
                    <Alert
                        severity="info"
                        sx={{ mb: 2 }}
                        action={
                            <Box>
                                <Button
                                    color="inherit"
                                    size="small"
                                    onClick={loadSavedSuggestionButtonHandle}
                                    startIcon={<RestoreIcon />}
                                    sx={{ mr: 1 }}
                                >
                                    Tải đề xuất đã lưu
                                </Button>
                                <Button
                                    color="inherit"
                                    size="small"
                                    onClick={clearSavedSuggestionButtonHandle}
                                >
                                    Xóa
                                </Button>
                            </Box>
                        }
                    >
                        Bạn có một đề xuất phân công đã lưu ({savedSuggestions.ordersCount} đơn hàng)
                        từ {new Date(savedSuggestions.timestamp).toLocaleString()}
                    </Alert>
                )}

                <TabContext value={tabValue}>
                    <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                        <TabList
                            onChange={(event, newValue) => setTabValue(newValue)}
                        >
                            <Tab label="Bảng phân công hôm nay" value="4" />
                            <Tab label="Đơn hàng cần phân công" value="1" />
                        </TabList>
                    </Box>

                    <TabPanel value="1">
                        {suggestionMode ? (
                            <Box>
                                {/* Shipper statistics */}
                                {modifiedAssignments.length > 0 && (
                                    <Box sx={{ mb: 2 }}>
                                        <Typography variant="h6" sx={{ mb: 1 }}>
                                            Thống kê phân công:
                                        </Typography>
                                        <Stack direction="row" spacing={1} flexWrap="wrap">
                                            {getShipperStats().map((stat, index) => (
                                                <Chip
                                                    key={index}
                                                    label={`${stat.name}: ${stat.count} đơn`}
                                                    color="primary"
                                                    variant="outlined"
                                                />
                                            ))}
                                        </Stack>
                                    </Box>
                                )}

                                {/* Selection info for assignments */}
                                {selectedAssignments.length > 0 && (
                                    <Box sx={{ mb: 2 }}>
                                        <Alert severity="info">
                                            Đã chọn {selectedAssignments.length} phân công
                                        </Alert>
                                    </Box>
                                )}

                                {/* Bulk action buttons for assignments */}
                                <Box sx={{ mb: 2 }}>
                                    <Button
                                        variant="outlined"
                                        startIcon={<SwapHorizIcon />}
                                        onClick={handleBulkEditAssignment}
                                        disabled={selectedAssignments.length === 0}
                                        sx={{ mr: 1 }}
                                        color={selectedAssignments.length > 0 ? "primary" : "inherit"}
                                    >
                                        Đổi shipper cho {selectedAssignments.length > 0 ? selectedAssignments.length : 'các'} mục đã chọn
                                    </Button>
                                    <Button
                                        variant="outlined"
                                        color="error"
                                        startIcon={<DeleteIcon />}
                                        onClick={handleDeleteSelectedAssignments}
                                        disabled={selectedAssignments.length === 0}
                                    >
                                        Xóa {selectedAssignments.length > 0 ? selectedAssignments.length : 'các'} mục đã chọn
                                    </Button>
                                </Box>

                                <StandardTable
                                    key="suggestion-table"
                                    rowKey="id"
                                    title="Đề xuất phân công - Có thể chỉnh sửa trước khi xác nhận"
                                    columns={suggestionColumns}
                                    data={modifiedAssignments}
                                    options={{
                                        selection: true, // Bật selection
                                        pageSize: 10,
                                        search: true,
                                        sorting: true,
                                    }}
                                    onSelectionChange={handleAssignmentSelectionChange}
                                    actions={[
                                        {
                                            tooltip: "Lưu đề xuất",
                                            icon: SaveIcon,
                                            iconOnClickHandle: saveSuggestionButtonHandle,
                                            disabled: modifiedAssignments.length === 0
                                        },
                                        {
                                            tooltip: "Xác nhận phân công",
                                            iconOnClickHandle: confirmAssignmentButtonHandle,
                                            disabled: modifiedAssignments.length === 0
                                        },
                                        {
                                            tooltip: "Hủy đề xuất",
                                            iconOnClickHandle: cancelSuggestionButtonHandle,
                                        },
                                    ]}
                                />
                            </Box>
                        ) : (
                            <Box>
                                {/* Selection info for orders */}
                                {selectedOrders.length > 0 && (
                                    <Box sx={{ mb: 2 }}>
                                        <Alert severity="info">
                                            Đã chọn {selectedOrders.length} đơn hàng
                                        </Alert>
                                    </Box>
                                )}

                                <StandardTable
                                    rowKey="id"
                                    title="Danh sách đơn hàng chưa phân công"
                                    columns={unassignedOrdersColumns}
                                    data={orders}
                                    options={{
                                        selection: true, // Bật selection cho bảng orders
                                        pageSize: 10,
                                        search: true,
                                        sorting: true,
                                    }}
                                    onSelectionChange={handleOrderSelectionChange}
                                    actions={[
                                        ...(hasSavedSuggestion ? [{
                                            tooltip: "Tải đề xuất đã lưu",
                                            icon: RestoreIcon,
                                            iconOnClickHandle: loadSavedSuggestionButtonHandle,
                                        }] : []),
                                        {
                                            tooltip: "Tạo phân công thủ công",
                                            iconOnClickHandle: createManualAssignmentButtonHandle,
                                            disabled: selectedOrders.length === 0
                                        },
                                        {
                                            tooltip: "Lấy đề xuất phân công tự động",
                                            iconOnClickHandle: getSuggestionButtonHandle,
                                            disabled: selectedOrders.length === 0 || shippers.length === 0
                                        },
                                    ]}
                                />
                            </Box>
                        )}
                    </TabPanel>

                    <TabPanel value="4">
                        <StandardTable
                            rowKey="shipperId"
                            title="Bảng phân công giao hàng hôm nay"
                            columns={assignmentColumns}
                            data={assignmentData}
                            options={{
                                selection: false,
                                pageSize: 10,
                                search: true,
                                sorting: true,
                            }}
                            actions={[
                                {
                                    tooltip: "Làm mới dữ liệu",
                                    iconOnClickHandle: refreshAssignmentData,
                                },
                            ]}
                        />
                    </TabPanel>
                </TabContext>
            </Box>

            {/* Single Edit Dialog */}
            <Dialog
                open={editDialogOpen}
                onClose={() => {
                    console.log("Closing edit dialog");
                    setEditDialogOpen(false);
                    setSelectedShipperForEdit("");
                    setEditingAssignment(null);
                }}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle>Chỉnh sửa phân công</DialogTitle>
                <DialogContent>
                    {editingAssignment ? (
                        <Box sx={{ pt: 2 }}>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Đơn hàng:</strong> {editingAssignment.id}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Người gửi:</strong> {editingAssignment.senderName}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Người nhận:</strong> {editingAssignment.recipientName}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                <strong>Shipper hiện tại:</strong> {editingAssignment.shipperName || "Chưa phân công"}
                            </Typography>
                            <FormControl fullWidth sx={{ mt: 2 }}>
                                <InputLabel>Chọn Shipper mới</InputLabel>
                                <Select
                                    value={selectedShipperForEdit}
                                    onChange={(e) => {
                                        console.log("Selected shipper:", e.target.value);
                                        setSelectedShipperForEdit(e.target.value);
                                    }}
                                    label="Chọn Shipper mới"
                                >
                                    <MenuItem value="">
                                        <em>Chưa phân công</em>
                                    </MenuItem>
                                    {shippers.map(shipper => (
                                        <MenuItem key={shipper.id} value={shipper.id}>
                                            {shipper.name} - {shipper.phone}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Box>
                    ) : (
                        <Typography>Không có dữ liệu phân công</Typography>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => {
                        setEditDialogOpen(false);
                        setSelectedShipperForEdit("");
                        setEditingAssignment(null);
                    }}>
                        Hủy
                    </Button>
                    <Button
                        onClick={handleSaveSingleEdit}
                        variant="contained"
                        disabled={!selectedShipperForEdit}
                    >
                        Lưu thay đổi
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Bulk Edit Dialog */}
            <Dialog
                open={bulkEditDialogOpen}
                onClose={() => setBulkEditDialogOpen(false)}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle>Đổi shipper cho nhiều phân công</DialogTitle>
                <DialogContent>
                    <Box sx={{ pt: 2 }}>
                        <Typography variant="body1" sx={{ mb: 2 }}>
                            Bạn đang thay đổi shipper cho <strong>{selectedAssignments.length}</strong> phân công được chọn
                        </Typography>
                        <FormControl fullWidth sx={{ mt: 2 }}>
                            <InputLabel>Chọn Shipper mới</InputLabel>
                            <Select
                                value={selectedShipperForBulkEdit}
                                onChange={(e) => setSelectedShipperForBulkEdit(e.target.value)}
                                label="Chọn Shipper mới"
                            >
                                {shippers.map(shipper => (
                                    <MenuItem key={shipper.id} value={shipper.id}>
                                        {shipper.name} - {shipper.phone}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setBulkEditDialogOpen(false)}>
                        Hủy
                    </Button>
                    <Button
                        onClick={handleSaveBulkEdit}
                        variant="contained"
                        disabled={!selectedShipperForBulkEdit}
                    >
                        Áp dụng cho {selectedAssignments.length} phân công
                    </Button>
                </DialogActions>
            </Dialog>
        </Fragment>
    );
};

const SCR_ID = "SCR_ASSIGN_ORDER_SHIPPER";
export default withScreenSecurity(AssignOrderShipper, SCR_ID, true);