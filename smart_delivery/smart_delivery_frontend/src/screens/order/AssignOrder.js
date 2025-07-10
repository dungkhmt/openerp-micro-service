import LoadingScreen from "components/common/loading/loading";
import {
    Box, Button, Grid, Tab, TextField, Typography, Dialog, DialogActions,
    DialogContent, DialogTitle, Select, MenuItem, FormControl, InputLabel,
    Checkbox, Chip, Stack, Alert, Autocomplete
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

const AssignOrder = (props) => {
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
    const [collectors, setCollectors] = useState([]);
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

    // Updated: Use objects for selected collectors instead of just IDs
    const [selectedCollectorForEdit, setSelectedCollectorForEdit] = useState(null);
    const [selectedCollectorForBulkEdit, setSelectedCollectorForBulkEdit] = useState(null);

    // Load saved suggestions from localStorage on component mount
    useEffect(() => {
        const saved = localStorage.getItem(`assignmentSuggestion_${hubId}`);
        if (saved) {
            try {
                const parsedSaved = JSON.parse(saved);
                setSavedSuggestions(parsedSaved);
                setHasSavedSuggestion(true);
            } catch (error) {
                console.error("Error parsing saved suggestions:", error);
                localStorage.removeItem(`assignmentSuggestion_${hubId}`);
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

    // Debug collectors data
    useEffect(() => {
        console.log("collectors:", collectors);
    }, [collectors]);

    // Fetch initial data
    useEffect(() => {
        async function fetchData() {
            try {
                // Fetch collectors
                await request(
                    "get",
                    `/smdeli/humanresource/collector/hub/${hubId}`,
                    (res) => {
                        setCollectors(res.data);
                    }
                );

                // Fetch current assignments
                await request(
                    "get",
                    `/smdeli/assignment/collector/order/assign/collector/today/${hubId}`,
                    (res) => {
                        setAssignmentData(res.data);
                    }
                );

                // Fetch orders to be assigned
                await request(
                    "get",
                    `/smdeli/ordermanager/order/hub/today/${hubId}`,
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
            collectorId: null, // Will be assigned later
            collectorName: "Chưa phân công",
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

        if (collectors.length === 0) {
            errorNoti("Không có shipper nào khả dụng");
            return;
        }

        setLoading(true);
        const updatedCollectors = collectors.map(collector => ({
            id: collector.id,
            hubId: hubId,
        }));

        request(
            "post",
            "/smdeli/assignment/collector/order/suggest/collector",
            (res) => {
                setSuggestedAssignments(res.data);
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
                employees: updatedCollectors,
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

        localStorage.setItem(`assignmentSuggestion_${hubId}`, JSON.stringify(dataToSave));
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
        localStorage.removeItem(`assignmentSuggestion_${hubId}`);
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
            employeeId: assignment.collectorId,
            employeeName: assignment.collectorName,
            sequenceNumber: assignment.sequenceNumber || 1
        }));

        // Chỉ gửi orderIds của những order được phân công
        const orderIds = modifiedAssignments.map(assignment => assignment.id);

        request(
            "post",
            "/smdeli/assignment/collector/order/confirm/collector",
            (res) => {
                // Clear saved suggestions after successful confirmation
                localStorage.removeItem(`assignmentSuggestion_${hubId}`);
                successNoti(`Phân công thành công ${orderIds.length} đơn hàng`);
                setLoading(false);
                // Reload trang sau khi xác nhận thành công
                // setTimeout(() => {
                //     window.location.reload();
                // }, 1000); // Delay 1 giây để user thấy notification
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
        if (!selectedCollectorForBulkEdit) {
            errorNoti("Vui lòng chọn collector");
            return;
        }

        const selectedIds = selectedAssignments.map(assignment => assignment.id);
        console.log("Updating assignments for IDs:", selectedIds);
        console.log("New collector:", selectedCollectorForBulkEdit);

        setModifiedAssignments(prev => {
            const updated = prev.map(assignment =>
                selectedIds.includes(assignment.id)
                    ? {
                        ...assignment,
                        collectorId: selectedCollectorForBulkEdit.id,
                        collectorName: selectedCollectorForBulkEdit.name
                    }
                    : assignment
            );
            console.log("Updated assignments:", updated);
            return updated;
        });

        setBulkEditDialogOpen(false);
        setSelectedCollectorForBulkEdit(null);
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

    // State cho single edit dialog
    const [editingAssignment, setEditingAssignment] = useState(null);

    // Open single edit dialog
    const handleEditSingleAssignment = (assignment) => {
        console.log("handleEditSingleAssignment called with:", assignment);
        setEditingAssignment(assignment); // Lưu assignment riêng để edit

        // Find the current collector object
        const currentCollector = collectors.find(c => c.id === assignment.collectorId);
        setSelectedCollectorForEdit(currentCollector || null);
        setEditDialogOpen(true);
    };

    // Save single edit
    const handleSaveSingleEdit = () => {
        if (!selectedCollectorForEdit) {
            errorNoti("Vui lòng chọn collector");
            return;
        }

        console.log("Editing assignment:", editingAssignment);
        console.log("New collector:", selectedCollectorForEdit);

        setModifiedAssignments(prev => {
            const updated = prev.map(assignment =>
                assignment.id === editingAssignment.id
                    ? {
                        ...assignment,
                        collectorId: selectedCollectorForEdit.id,
                        collectorName: selectedCollectorForEdit.name
                    }
                    : assignment
            );
            console.log("Updated assignments after single edit:", updated);
            return updated;
        });

        setEditDialogOpen(false);
        setSelectedCollectorForEdit(null);
        setEditingAssignment(null);
        successNoti("Đã cập nhật phân công");
    };

    // Refresh orders list
    const refreshOrdersList = async () => {
        try {
            await request(
                "get",
                `/smdeli/assignment/collector/order/hub/today/${hubId}`,
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
                `/smdeli/assignment/collector/order/assign/collector/today/${hubId}`,
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

    // Get collector assignment statistics
    const getCollectorStats = () => {
        const stats = {};
        modifiedAssignments.forEach(assignment => {
            if (!stats[assignment.collectorId]) {
                stats[assignment.collectorId] = {
                    name: assignment.collectorName,
                    count: 0
                };
            }
            stats[assignment.collectorId].count++;
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
            title: "Collector",
            field: "collectorName",
            width: 120
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
        { title: "Mã nhân viên", field: "collectorId" },
        { title: "Tên nhân viên", field: "collectorName" },
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
                            window.location.href = `${window.location.pathname}/today/${rowData.collectorId}`;
                        }}
                        color="success"
                        title="Xem chi tiết"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => {
                            window.location.href = `${window.location.pathname}/today/${rowData.collectorId}`;
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
                                {/* Collector statistics */}
                                {modifiedAssignments.length > 0 && (
                                    <Box sx={{ mb: 2 }}>
                                        <Typography variant="h6" sx={{ mb: 1 }}>
                                            Thống kê phân công:
                                        </Typography>
                                        <Stack direction="row" spacing={1} flexWrap="wrap">
                                            {getCollectorStats().map((stat, index) => (
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
                                        Đổi collector cho {selectedAssignments.length > 0 ? selectedAssignments.length : 'các'} mục đã chọn
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
                                            disabled: selectedOrders.length === 0 || collectors.length === 0
                                        },
                                    ]}
                                />
                            </Box>
                        )}
                    </TabPanel>

                    <TabPanel value="4">
                        <StandardTable
                            rowKey="collectorId"
                            title="Bảng phân công lấy hàng hôm nay"
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

            {/* Single Edit Dialog với Autocomplete Search */}
            <Dialog
                open={editDialogOpen}
                onClose={() => {
                    console.log("Closing edit dialog");
                    setEditDialogOpen(false);
                    setSelectedCollectorForEdit(null);
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
                                <strong>Collector hiện tại:</strong> {editingAssignment.collectorName || "Chưa phân công"}
                            </Typography>

                            {/* Autocomplete Search cho Collector */}
                            <Autocomplete
                                value={selectedCollectorForEdit}
                                onChange={(event, newValue) => {
                                    console.log("Selected collector:", newValue);
                                    setSelectedCollectorForEdit(newValue);
                                }}
                                options={collectors}
                                getOptionLabel={(option) => `${option.name} - ${option.phone}`}
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        label="Chọn Collector mới"
                                        placeholder="Tìm kiếm theo tên hoặc số điện thoại..."
                                        fullWidth
                                        sx={{ mt: 2 }}
                                    />
                                )}
                                renderOption={(props, option) => (
                                    <Box component="li" {...props}>
                                        <Box>
                                            <Typography variant="body1" fontWeight="bold">
                                                {option.name}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                {option.phone} - ID: {option.id}
                                            </Typography>
                                        </Box>
                                    </Box>
                                )}
                                isOptionEqualToValue={(option, value) => option.id === value?.id}
                                filterOptions={(options, { inputValue }) => {
                                    return options.filter(option =>
                                        option.name.toLowerCase().includes(inputValue.toLowerCase()) ||
                                        option.phone.includes(inputValue) ||
                                        option.id.toString().includes(inputValue)
                                    );
                                }}
                                clearText="Xóa"
                                noOptionsText="Không tìm thấy collector"
                                isClearable
                            />
                        </Box>
                    ) : (
                        <Typography>Không có dữ liệu phân công</Typography>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => {
                        setEditDialogOpen(false);
                        setSelectedCollectorForEdit(null);
                        setEditingAssignment(null);
                    }}>
                        Hủy
                    </Button>
                    <Button
                        onClick={handleSaveSingleEdit}
                        variant="contained"
                        disabled={!selectedCollectorForEdit}
                    >
                        Lưu thay đổi
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Bulk Edit Dialog với Autocomplete Search */}
            <Dialog
                open={bulkEditDialogOpen}
                onClose={() => setBulkEditDialogOpen(false)}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle>Đổi collector cho nhiều phân công</DialogTitle>
                <DialogContent>
                    <Box sx={{ pt: 2 }}>
                        <Typography variant="body1" sx={{ mb: 2 }}>
                            Bạn đang thay đổi collector cho <strong>{selectedAssignments.length}</strong> phân công được chọn
                        </Typography>

                        {/* Hiển thị danh sách assignments được chọn */}
                        <Box sx={{ mb: 2, p: 2, border: '1px solid #e0e0e0', borderRadius: 1, maxHeight: 200, overflow: 'auto' }}>
                            <Typography variant="subtitle2" sx={{ mb: 1 }}>
                                Danh sách đơn hàng được chọn:
                            </Typography>
                            {selectedAssignments.map((assignment, index) => (
                                <Typography key={assignment.id} variant="body2" sx={{ mb: 0.5 }}>
                                    {index + 1}. {assignment.id} - {assignment.senderName} → {assignment.recipientName}
                                </Typography>
                            ))}
                        </Box>

                        {/* Autocomplete Search cho Bulk Edit */}
                        <Autocomplete
                            value={selectedCollectorForBulkEdit}
                            onChange={(event, newValue) => {
                                console.log("Selected collector for bulk edit:", newValue);
                                setSelectedCollectorForBulkEdit(newValue);
                            }}
                            options={collectors}
                            getOptionLabel={(option) => `${option.name} - ${option.phone}`}
                            renderInput={(params) => (
                                <TextField
                                    {...params}
                                    label="Chọn Collector mới"
                                    placeholder="Tìm kiếm theo tên hoặc số điện thoại..."
                                    fullWidth
                                />
                            )}
                            renderOption={(props, option) => (
                                <Box component="li" {...props}>
                                    <Box>
                                        <Typography variant="body1" fontWeight="bold">
                                            {option.name}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {option.phone} - ID: {option.id}
                                        </Typography>
                                    </Box>
                                </Box>
                            )}
                            isOptionEqualToValue={(option, value) => option.id === value?.id}
                            filterOptions={(options, { inputValue }) => {
                                return options.filter(option =>
                                    option.name.toLowerCase().includes(inputValue.toLowerCase()) ||
                                    option.phone.includes(inputValue) ||
                                    option.id.toString().includes(inputValue)
                                );
                            }}
                            clearText="Xóa"
                            noOptionsText="Không tìm thấy collector"
                            isClearable
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setBulkEditDialogOpen(false)}>
                        Hủy
                    </Button>
                    <Button
                        onClick={handleSaveBulkEdit}
                        variant="contained"
                        disabled={!selectedCollectorForBulkEdit}
                    >
                        Áp dụng cho {selectedAssignments.length} phân công
                    </Button>
                </DialogActions>
            </Dialog>
        </Fragment>
    );
};

const SCR_ID = "SCR_ASSIGN_ORDER";
export default withScreenSecurity(AssignOrder, SCR_ID, true);