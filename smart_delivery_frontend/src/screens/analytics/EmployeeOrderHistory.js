import LoadingScreen from "components/common/loading/loading";
import { Box, Button, Grid, Paper, Tab, Typography, FormControl,
    InputLabel, Select, MenuItem, TextField, Chip } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import React, { Fragment, useEffect, useState } from "react";
import useStyles from "screens/styles";
import { errorNoti, successNoti } from "utils/notification";
import { useHistory } from "react-router";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { useSelector } from "react-redux";

const EmployeeOrderHistory = () => {
    const history = useHistory();
    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [tabValue, setTabValue] = useState('1');
    const [orderHistory, setOrderHistory] = useState([]);
    const [filteredHistory, setFilteredHistory] = useState([]);
    const [startDate, setStartDate] = useState(new Date(new Date().setDate(new Date().getDate() - 30)));
    const [endDate, setEndDate] = useState(new Date());
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [searchTerm, setSearchTerm] = useState('');

    // Get user info from Redux store
    const username = useSelector((state) => state.auth.user?.username);
    const role = useSelector((state) => state.auth.user?.role);
    const [employeeId, setEmployeeId] = useState(null);

    // Determine if user is collector or shipper
    const isCollector = role === 'COLLECTOR';
    const isShipper = role === 'SHIPPER';

    // Format date for API calls
    const formatDateForApi = (date) => {
        if (!date) return '';
        const d = new Date(date);
        let month = '' + (d.getMonth() + 1);
        let day = '' + d.getDate();
        const year = d.getFullYear();
        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;
        return [year, month, day].join('-');
    };

    // Format date for input fields
    const formatDateForInput = (date) => {
        if (!date) return '';
        const d = new Date(date);
        let month = '' + (d.getMonth() + 1);
        let day = '' + d.getDate();
        const year = d.getFullYear();
        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;
        return [year, month, day].join('-');
    };

    // Format timestamp for display
    const formatDateTime = (timestamp) => {
        if (!timestamp) return 'N/A';
        const date = new Date(timestamp);
        return date.toLocaleString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // Get status color
    const getStatusColor = (status) => {
        switch (status) {
            case 'COMPLETED':
                return 'success';
            case 'FAILED':
            case 'FAILED_ATTEMPT':
            case 'CANCELED':
                return 'error';
            case 'ASSIGNED':
                return 'primary';
            case 'COLLECTED':
            case 'PICKED_UP':
                return 'info';
            case 'IN_TRANSIT':
                return 'warning';
            default:
                return 'default';
        }
    };

    // Get Vietnamese status text
    const getStatusText = (status) => {
        const statusMap = {
            'ASSIGNED': 'Đã phân công',
            'COLLECTED': 'Đã thu gom',
            'PICKED_UP': 'Đã lấy hàng',
            'IN_TRANSIT': 'Đang vận chuyển',
            'COMPLETED': 'Hoàn thành',
            'FAILED': 'Thất bại',
            'FAILED_ATTEMPT': 'Giao hàng thất bại',
            'RETURNED_TO_HUB': 'Trả về hub',
            'CANCELED': 'Đã hủy'
        };
        return statusMap[status] || status;
    };

    // Get employee ID by username using your existing endpoints
    const getEmployeeIdByUsername = async () => {
        if (!username) return;

        let endpoint = '';
        if (isCollector) {
            endpoint = `/user/get-collector/${username}`;
        } else if (isShipper) {
            endpoint = `/user/get-shipper/${username}`;
        }

        if (endpoint) {
            await request(
                "get",
                endpoint,
                (res) => {
                    if (res.data && res.data.id) {
                        setEmployeeId(res.data.id);
                    }
                },
                {
                    401: () => {
                        console.error("Unauthorized access");
                        setLoading(false);
                    },
                    404: () => {
                        errorNoti("Không tìm thấy thông tin nhân viên");
                        setLoading(false);
                    },
                    503: () => {
                        errorNoti("Có lỗi khi lấy thông tin nhân viên");
                        setLoading(false);
                    }
                }
            );
        }
    };

    // Fetch order history
    const fetchOrderHistory = async () => {
        if (!employeeId) {
            setLoading(false);
            return;
        }

        setLoading(true);
        let endpoint;

        if (isCollector) {
            endpoint = `/smdeli/ordermanager/order/assign/today/collector/${employeeId}`;
        } else if (isShipper) {
            endpoint = `/smdeli/ordermanager/order/assign/shipper/today/${employeeId}`;
        }

        if (endpoint) {
            await request(
                "get",
                endpoint,
                (res) => {
                    setOrderHistory(res.data || []);
                    setFilteredHistory(res.data || []);
                    setLoading(false);
                },
                {
                    401: () => { setLoading(false); },
                    404: () => {
                        // No data found, set empty arrays
                        setOrderHistory([]);
                        setFilteredHistory([]);
                        setLoading(false);
                    },
                    503: () => {
                        errorNoti("Có lỗi khi tải lịch sử đơn hàng");
                        setLoading(false);
                    }
                }
            );
        } else {
            setLoading(false);
        }
    };

    // Filter history based on status and search term
    useEffect(() => {
        let filtered = orderHistory;

        // Filter by status
        if (statusFilter !== 'ALL') {
            filtered = filtered.filter(item => item.assignmentStatus === statusFilter);
        }

        // Filter by search term (order ID, sender/recipient name)
        if (searchTerm) {
            filtered = filtered.filter(item =>
                item.orderId.toString().toLowerCase().includes(searchTerm.toLowerCase()) ||
                (item.senderName && item.senderName.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (item.recipientName && item.recipientName.toLowerCase().includes(searchTerm.toLowerCase()))
            );
        }

        setFilteredHistory(filtered);
    }, [orderHistory, statusFilter, searchTerm]);

    // Initial load - get employee ID first, then fetch order history
    useEffect(() => {
        if (username && (isCollector || isShipper)) {
            getEmployeeIdByUsername();
        }
    }, [username, role]);

    // Fetch order history when employeeId is available
    useEffect(() => {
        if (employeeId) {
            fetchOrderHistory();
        }
    }, [employeeId]);

    // Prepare table columns for collectors
    const collectorColumns = [
        {
            title: "Mã đơn hàng",
            field: "orderId",
            render: (rowData) => (
                <Typography variant="body2" style={{ fontFamily: 'monospace' }}>
                    {rowData.orderId.toString().substring(0, 8)}...
                </Typography>
            )
        },
        { title: "Số thứ tự", field: "sequenceNumber" },
        { title: "Tên người gửi", field: "senderName" },
        { title: "Số điện thoại", field: "senderPhone" },
        { title: "Địa chỉ lấy hàng", field: "senderAddress" },
        {
            title: "Trạng thái đơn hàng",
            field: "status",
            render: (rowData) => (
                <Chip
                    label={getStatusText(rowData.status)}
                    color={getStatusColor(rowData.status)}
                    size="small"
                />
            )
        },
        {
            title: "Trạng thái phân công",
            field: "assignmentStatus",
            render: (rowData) => (
                <Chip
                    label={getStatusText(rowData.assignmentStatus)}
                    color={getStatusColor(rowData.assignmentStatus)}
                    size="small"
                />
            )
        },
        {
            title: "Thời gian tạo",
            field: "orderCreatedAt",
            render: (rowData) => formatDateTime(rowData.orderCreatedAt)
        },
        { title: "Số lượng item", field: "numOfItem" }
    ];

    // Prepare table columns for shippers
    const shipperColumns = [
        {
            title: "Mã đơn hàng",
            field: "orderId",
            render: (rowData) => (
                <Typography variant="body2" style={{ fontFamily: 'monospace' }}>
                    {rowData.orderId.toString().substring(0, 8)}...
                </Typography>
            )
        },
        { title: "Số thứ tự", field: "sequenceNumber" },
        { title: "Tên người nhận", field: "recipientName" },
        { title: "Số điện thoại", field: "recipientPhone" },
        { title: "Địa chỉ giao hàng", field: "recipientAddress" },
        {
            title: "Trạng thái đơn hàng",
            field: "orderStatus",
            render: (rowData) => (
                <Chip
                    label={getStatusText(rowData.orderStatus)}
                    color={getStatusColor(rowData.orderStatus)}
                    size="small"
                />
            )
        },
        {
            title: "Trạng thái phân công",
            field: "assignmentStatus",
            render: (rowData) => (
                <Chip
                    label={getStatusText(rowData.assignmentStatus)}
                    color={getStatusColor(rowData.assignmentStatus)}
                    size="small"
                />
            )
        },
        {
            title: "Thời gian tạo",
            field: "orderCreatedAt",
            render: (rowData) => formatDateTime(rowData.orderCreatedAt)
        }
    ];

    // Get unique statuses for filter dropdown
    const getUniqueStatuses = () => {
        const statuses = new Set();
        orderHistory.forEach(item => {
            statuses.add(item.assignmentStatus);
        });
        return Array.from(statuses);
    };

    // Calculate summary statistics
    const getSummaryStats = () => {
        const total = filteredHistory.length;
        const completed = filteredHistory.filter(item => item.assignmentStatus === 'COMPLETED').length;
        const failed = filteredHistory.filter(item =>
            item.assignmentStatus === 'FAILED' ||
            item.assignmentStatus === 'FAILED_ATTEMPT' ||
            item.assignmentStatus === 'CANCELED'
        ).length;
        const pending = total - completed - failed;

        return { total, completed, failed, pending };
    };

    const summaryStats = getSummaryStats();

    return loading ? (
        <LoadingScreen />
    ) : (
        <Fragment>
            <Box className={classes.bodyBox}>
                <TabContext value={tabValue}>
                    <Box sx={{ borderBottom: 0, borderColor: "divider" }}>
                        <TabList onChange={(event, newValue) => setTabValue(newValue)}>
                            <Tab label="Lịch sử đơn hàng" value="1" />
                            <Tab label="Thống kê tổng quan" value="2" />
                        </TabList>
                    </Box>

                    {/* Filter Controls */}
                    <Box sx={{ mb: 2, p: 2, backgroundColor: '#f5f5f5', borderRadius: 2 }}>
                        <Grid container spacing={2} alignItems="center">
                            <Grid item xs={12} md={3}>
                                <FormControl fullWidth>
                                    <InputLabel>Trạng thái</InputLabel>
                                    <Select
                                        value={statusFilter}
                                        label="Trạng thái"
                                        onChange={(e) => setStatusFilter(e.target.value)}
                                    >
                                        <MenuItem value="ALL">Tất cả</MenuItem>
                                        {getUniqueStatuses().map((status) => (
                                            <MenuItem key={status} value={status}>
                                                {getStatusText(status)}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    label="Tìm kiếm (Mã đơn hàng, tên khách hàng)"
                                    fullWidth
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    placeholder="Nhập để tìm kiếm..."
                                />
                            </Grid>
                            <Grid item xs={12} md={3}>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    fullWidth
                                    onClick={fetchOrderHistory}
                                >
                                    Làm mới
                                </Button>
                            </Grid>
                        </Grid>
                    </Box>

                    {/* Order History Tab */}
                    <TabPanel value="1">
                        {filteredHistory.length > 0 ? (
                            <>
                                <Typography variant="h6" gutterBottom>
                                    Lịch sử đơn hàng - {isCollector ? 'Nhân viên thu gom' : 'Nhân viên giao hàng'}
                                </Typography>
                                <Typography variant="subtitle2" color="textSecondary" gutterBottom>
                                    Tổng cộng: {filteredHistory.length} đơn hàng
                                </Typography>

                                <StandardTable
                                    columns={isCollector ? collectorColumns : shipperColumns}
                                    data={filteredHistory}
                                    options={{
                                        search: false,
                                        paging: true,
                                        pageSize: 10,
                                        pageSizeOptions: [5, 10, 20, 50],
                                        sorting: true,
                                        headerStyle: {
                                            fontWeight: 'bold',
                                            backgroundColor: '#f5f5f5'
                                        },
                                        rowStyle: {
                                            fontSize: '0.875rem'
                                        }
                                    }}
                                />
                            </>
                        ) : (
                            <Box sx={{ textAlign: 'center', p: 4 }}>
                                <Typography variant="h6">
                                    Không có dữ liệu lịch sử đơn hàng
                                </Typography>
                                <Typography variant="body2" color="textSecondary">
                                    Chưa có đơn hàng nào được phân công cho bạn
                                </Typography>
                            </Box>
                        )}
                    </TabPanel>

                    {/* Summary Statistics Tab */}
                    <TabPanel value="2">
                        <Typography variant="h6" gutterBottom>
                            Thống kê tổng quan
                        </Typography>

                        <Grid container spacing={2}>
                            {/* Summary Cards */}
                            <Grid item xs={12} md={3}>
                                <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                    <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                        Tổng số đơn
                                    </Typography>
                                    <Typography variant="h3" sx={{ color: 'primary.main' }}>
                                        {summaryStats.total}
                                    </Typography>
                                </Paper>
                            </Grid>
                            <Grid item xs={12} md={3}>
                                <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                    <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                        Đơn hoàn thành
                                    </Typography>
                                    <Typography variant="h4" sx={{ color: 'success.main' }}>
                                        {summaryStats.completed}
                                    </Typography>
                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                        {summaryStats.total > 0 ?
                                            ((summaryStats.completed / summaryStats.total) * 100).toFixed(1) : 0}% tổng số đơn
                                    </Typography>
                                </Paper>
                            </Grid>
                            <Grid item xs={12} md={3}>
                                <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                    <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                        Đơn thất bại
                                    </Typography>
                                    <Typography variant="h4" sx={{ color: 'error.main' }}>
                                        {summaryStats.failed}
                                    </Typography>
                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                        {summaryStats.total > 0 ?
                                            ((summaryStats.failed / summaryStats.total) * 100).toFixed(1) : 0}% tổng số đơn
                                    </Typography>
                                </Paper>
                            </Grid>
                            <Grid item xs={12} md={3}>
                                <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                    <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                        Đơn đang xử lý
                                    </Typography>
                                    <Typography variant="h4" sx={{ color: 'warning.main' }}>
                                        {summaryStats.pending}
                                    </Typography>
                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                        {summaryStats.total > 0 ?
                                            ((summaryStats.pending / summaryStats.total) * 100).toFixed(1) : 0}% tổng số đơn
                                    </Typography>
                                </Paper>
                            </Grid>

                            {/* Status Distribution */}
                            <Grid item xs={12}>
                                <Paper sx={{ p: 2 }}>
                                    <Typography variant="h6" gutterBottom>
                                        Chi tiết trạng thái
                                    </Typography>
                                    <Grid container spacing={2}>
                                        {getUniqueStatuses().map((status) => {
                                            const count = filteredHistory.filter(item => item.assignmentStatus === status).length;
                                            const percentage = summaryStats.total > 0 ?
                                                ((count / summaryStats.total) * 100).toFixed(1) : 0;

                                            return (
                                                <Grid item xs={12} sm={6} md={4} key={status}>
                                                    <Box sx={{
                                                        p: 2,
                                                        border: '1px solid #e0e0e0',
                                                        borderRadius: 1,
                                                        textAlign: 'center'
                                                    }}>
                                                        <Chip
                                                            label={getStatusText(status)}
                                                            color={getStatusColor(status)}
                                                            sx={{ mb: 1 }}
                                                        />
                                                        <Typography variant="h5">{count}</Typography>
                                                        <Typography variant="body2" color="textSecondary">
                                                            {percentage}% tổng số
                                                        </Typography>
                                                    </Box>
                                                </Grid>
                                            );
                                        })}
                                    </Grid>
                                </Paper>
                            </Grid>
                        </Grid>
                    </TabPanel>
                </TabContext>
            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_EMPLOYEE_ORDER_HISTORY";
export default EmployeeOrderHistory;