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
import {
    PieChart,
    Pie,
    Cell,
    ResponsiveContainer,
    Tooltip
} from 'recharts';

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
    const formatDateTime = (timestamp) => {
        if (!timestamp) return 'N/A';

        try {
            const date = new Date(timestamp);

            // Format as DD/MM/YYYY HH:MM
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');

            return `${day}/${month}/${year} ${hours}:${minutes}`;
        } catch (error) {
            console.error("Error formatting timestamp:", error);
            return 'Invalid date';
        }
    };
    // Get status color
    const getStatusColor = (status) => {
        switch (status) {
            case 'COMPLETED':
                return 'success';
            case 'FAILED':
            case 'FAILED_ATTEMPT':
            case 'FAILED_ONCE':
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
            'CANCELED': 'Đã hủy',
            'FAILED_ONCE': 'Thất bại một lần'
        };
        return statusMap[status] || status;
    };

    // Get Order Status text
    const getOrderStatusText = (status) => {
        const statusMap = {
            'PENDING': 'Chờ xử lý',
            'ASSIGNED': 'Đã phân công',
            'COLLECTED_COLLECTOR': 'Đã thu gom',
            'COLLECTED_HUB': 'Đã về hub',
            'DELIVERING': 'Đang vận chuyển',
            'DELIVERED': 'Đã giao',
            'ASSIGNED_SHIPPER': 'Đã phân công shipper',
            'SHIPPING': 'Đang giao hàng',
            'COMPLETED': 'Hoàn thành',
            'CANCELLED': 'Đã hủy',
            'SHIPPED_FAILED': 'Giao hàng thất bại'
        };
        return statusMap[status] || status;
    };

    // Get employee ID by username using existing endpoints
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

    // Fetch order history using the new endpoints
    const fetchOrderHistory = async () => {
        if (!employeeId) {
            setLoading(false);
            return;
        }

        setLoading(true);
        let endpoint;

        // Build endpoint with date parameters
        const startDateParam = formatDateForApi(startDate);
        const endDateParam = formatDateForApi(endDate);
        const dateParams = `?startDate=${startDateParam}&endDate=${endDateParam}`;

        if (isCollector) {
            endpoint = `/smdeli/ordermanager/order/history/collector/${employeeId}${dateParams}`;
        } else if (isShipper) {
            endpoint = `/smdeli/ordermanager/order/history/shipper/${employeeId}${dateParams}`;
        }

        if (endpoint) {
            await request(
                "get",
                endpoint,
                (res) => {
                    // Format dates before setting state
                    const formattedData = (res.data || []).map(item => {
                        // Make a copy of the item to avoid mutating the original
                        const formattedItem = { ...item };

                        // Format the orderCreatedAt date
                        if (formattedItem.orderCreatedAt) {
                            try {
                                const date = new Date(formattedItem.orderCreatedAt);
                                const day = String(date.getDate()).padStart(2, '0');
                                const month = String(date.getMonth() + 1).padStart(2, '0');
                                const year = date.getFullYear();
                                const hours = String(date.getHours()).padStart(2, '0');
                                const minutes = String(date.getMinutes()).padStart(2, '0');

                                // Replace the ISO timestamp with formatted version
                                formattedItem.orderCreatedAt = `${day}/${month}/${year} ${hours}:${minutes}`;
                            } catch (error) {
                                console.error("Error formatting orderCreatedAt:", error);
                            }
                        }

                        // Also format the doneAt date
                        if (formattedItem.doneAt) {
                            try {
                                const date = new Date(formattedItem.doneAt);
                                const day = String(date.getDate()).padStart(2, '0');
                                const month = String(date.getMonth() + 1).padStart(2, '0');
                                const year = date.getFullYear();
                                const hours = String(date.getHours()).padStart(2, '0');
                                const minutes = String(date.getMinutes()).padStart(2, '0');

                                // Replace the ISO timestamp with formatted version
                                formattedItem.doneAt = `${day}/${month}/${year} ${hours}:${minutes}`;
                            } catch (error) {
                                console.error("Error formatting doneAt:", error);
                            }
                        }

                        return formattedItem;
                    });

                    setOrderHistory(formattedData);
                    setFilteredHistory(formattedData);
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

    // Fetch order history when employeeId is available or date range changes
    useEffect(() => {
        if (employeeId) {
            fetchOrderHistory();
        }
    }, [employeeId, startDate, endDate]);

    // Handle date range change
    const handleDateRangeChange = () => {
        if (employeeId) {
            fetchOrderHistory();
        }
    };

    // Prepare table columns for collectors - SIMPLIFIED TO SHOW KEY DATA ONLY
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
        { title: "STT", field: "sequenceNumber" },
        { title: "Tên người gửi", field: "senderName" },
        { title: "Số điện thoại", field: "senderPhone" },
        {
            title: "Trạng thái đơn",
            field: "orderStatus",
            render: (rowData) => (
                <Chip
                    label={getOrderStatusText(rowData.orderStatus)}
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
            title: "Thời gian xong",
            field: "doneAt",
            render: (rowData) => formatDateTime(rowData.doneAt)
        }
    ];

    // Prepare table columns for shippers - SIMPLIFIED TO SHOW KEY DATA ONLY
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
        { title: "STT", field: "sequenceNumber" },
        { title: "Tên người nhận", field: "recipientName" },
        { title: "Số điện thoại", field: "recipientPhone" },
        {
            title: "Trạng thái đơn",
            field: "orderStatus",
            render: (rowData) => (
                <Chip
                    label={getOrderStatusText(rowData.orderStatus)}
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
            title: "Thời gian xong",
            field: "doneAt",
            render: (rowData) => formatDateTime(rowData.doneAt)
        }
    ];

    // Generate data for status pie chart
    const prepareStatusData = () => {
        const statusCounts = {};
        filteredHistory.forEach(item => {
            if (statusCounts[item.assignmentStatus]) {
                statusCounts[item.assignmentStatus]++;
            } else {
                statusCounts[item.assignmentStatus] = 1;
            }
        });

        return Object.entries(statusCounts)
            .filter(([status, count]) => count > 0)
            .map(([status, count]) => ({
                name: getStatusText(status),
                value: count,
                status: status
            }));
    };

    // Colors for pie chart
    const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#FF6384'];

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
            item.assignmentStatus === 'FAILED_ONCE' ||

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
            <Box className={classes.bodyBox} style={{ width: '100%', maxWidth: '100%' }}>
                <TabContext value={tabValue}>
                    <Box sx={{ borderBottom: 0, borderColor: "divider", width: '100%' }}>
                        <TabList onChange={(event, newValue) => setTabValue(newValue)}>
                            <Tab label="Lịch sử đơn hàng" value="1" />
                            <Tab label="Thống kê tổng quan" value="2" />
                        </TabList>
                    </Box>

                    {/* Filter Controls */}
                    <Box sx={{ mb: 2, p: 2, backgroundColor: '#f5f5f5', borderRadius: 2, width: '100%' }}>
                        <Grid container spacing={2} alignItems="center">
                            <Grid item xs={12} md={3}>
                                <TextField
                                    label="Từ ngày"
                                    type="date"
                                    fullWidth
                                    value={formatDateForInput(startDate)}
                                    onChange={(e) => setStartDate(new Date(e.target.value))}
                                    InputLabelProps={{ shrink: true }}
                                />
                            </Grid>
                            <Grid item xs={12} md={3}>
                                <TextField
                                    label="Đến ngày"
                                    type="date"
                                    fullWidth
                                    value={formatDateForInput(endDate)}
                                    onChange={(e) => setEndDate(new Date(e.target.value))}
                                    InputLabelProps={{ shrink: true }}
                                />
                            </Grid>
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
                            <Grid item xs={12} md={3}>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    fullWidth
                                    onClick={handleDateRangeChange}
                                >
                                    Làm mới
                                </Button>
                            </Grid>
                        </Grid>
                    </Box>

                    {/* Order History Tab */}
                    <TabPanel value="1" sx={{ padding: 0, width: '100%' }}>
                        {filteredHistory.length > 0 ? (
                            <Box sx={{ width: '100%' }}>


                                <Box sx={{ width: '100%', overflowX: 'auto', mt: 0, pt: 0 }}>
                                    <StandardTable
                                        title={<><Typography variant="h6" gutterBottom>
                                            Lịch sử đơn hàng - {isCollector ? 'Nhân viên thu gom' : 'Nhân viên giao hàng'}
                                        </Typography>
                                            <Typography variant="subtitle2" color="textSecondary" gutterBottom>
                                            Tổng cộng: {filteredHistory.length} đơn hàng
                                        ({formatDateForApi(startDate)} - {formatDateForApi(endDate)})
                                </Typography></>}
                                        columns={isCollector ? collectorColumns : shipperColumns}
                                        data={filteredHistory}
                                        options={{
                                            search: true,
                                            searchFieldAlignment: "left",
                                            searchFieldStyle: { width: '100%' },
                                            searchFieldVariant: "outlined",
                                            searchPlaceholder: "Tìm kiếm đơn hàng...",
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
                                            },
                                            maxBodyHeight: '600px',
                                            minBodyHeight: '400px',
                                            fixedColumns: {
                                                left: 2
                                            }
                                        }}
                                    />
                                </Box>
                            </Box>
                        ) : (
                            <Box sx={{ textAlign: 'center', p: 4 }}>
                                <Typography variant="h6">
                                    Không có dữ liệu lịch sử đơn hàng
                                </Typography>
                                <Typography variant="body2" color="textSecondary">
                                    Chưa có đơn hàng nào được phân công cho bạn trong khoảng thời gian này
                                </Typography>
                            </Box>
                        )}
                    </TabPanel>

                    {/* Summary Statistics Tab */}
                    <TabPanel value="2" sx={{ padding: 0, width: '100%' }}>
                        <Box sx={{ width: '100%' }}>
                            <Typography variant="h6" gutterBottom>
                                Thống kê tổng quan ({formatDateForApi(startDate)} - {formatDateForApi(endDate)})
                            </Typography>

                            <Grid container spacing={2}>
                                {/* Summary Cards */}
                                <Grid item xs={12} sm={6} md={3}>
                                    <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                        <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                            Tổng số đơn
                                        </Typography>
                                        <Typography variant="h3" sx={{ color: 'primary.main' }}>
                                            {summaryStats.total}
                                        </Typography>
                                    </Paper>
                                </Grid>
                                <Grid item xs={12} sm={6} md={3}>
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
                                <Grid item xs={12} sm={6} md={3}>
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
                                <Grid item xs={12} sm={6} md={3}>
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

                                {/* Status Distribution with Pie Chart */}
                                <Grid item xs={12} md={6}>
                                    <Paper sx={{ p: 2 }}>
                                        <Typography variant="h6" gutterBottom>
                                            Phân bố trạng thái (Biểu đồ)
                                        </Typography>
                                        <Box sx={{ height: 300 }}>
                                            <ResponsiveContainer width="100%" height="100%">
                                                <PieChart>
                                                    <Pie
                                                        data={prepareStatusData()}
                                                        cx="50%"
                                                        cy="50%"
                                                        labelLine={false}
                                                        label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(1)}%`}
                                                        outerRadius={100}
                                                        fill="#8884d8"
                                                        dataKey="value"
                                                    >
                                                        {prepareStatusData().map((entry, index) => (
                                                            <Cell
                                                                key={`cell-${index}`}
                                                                fill={COLORS[index % COLORS.length]}
                                                            />
                                                        ))}
                                                    </Pie>
                                                    <Tooltip
                                                        formatter={(value, name) => [value, name]}
                                                        labelFormatter={() => ''}
                                                    />
                                                </PieChart>
                                            </ResponsiveContainer>
                                        </Box>
                                    </Paper>
                                </Grid>

                                {/* Status Distribution Table */}
                                <Grid item xs={12} md={6}>
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
                                                    <Grid item xs={12} sm={6} md={12} key={status}>
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

                                {/* Date Range Summary */}
                                <Grid item xs={12}>
                                    <Paper sx={{ p: 2 }}>
                                        <Typography variant="h6" gutterBottom>
                                            Hiệu suất theo thời gian
                                        </Typography>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12} md={4}>
                                                <Typography variant="body1" fontWeight="bold">
                                                    Khoảng thời gian: {formatDateForApi(startDate)} đến {formatDateForApi(endDate)}
                                                </Typography>
                                            </Grid>
                                            <Grid item xs={12} md={4}>
                                                <Typography variant="body1">
                                                    Tỷ lệ hoàn thành: {summaryStats.total > 0 ?
                                                    ((summaryStats.completed / summaryStats.total) * 100).toFixed(1) : 0}%
                                                </Typography>
                                            </Grid>
                                            <Grid item xs={12} md={4}>
                                                <Typography variant="body1">
                                                    Trung bình đơn/ngày: {summaryStats.total > 0 ?
                                                    (summaryStats.total / Math.max(1, Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24)))).toFixed(1) : 0}
                                                </Typography>
                                            </Grid>
                                        </Grid>
                                    </Paper>
                                </Grid>
                            </Grid>
                        </Box>
                    </TabPanel>
                </TabContext>
            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_EMPLOYEE_ORDER_HISTORY";
export default EmployeeOrderHistory;