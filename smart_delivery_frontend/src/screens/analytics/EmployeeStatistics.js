import LoadingScreen from "components/common/loading/loading";
import { Box, Button, Grid, Paper, Tab, Typography, FormControl,
    InputLabel, Select, MenuItem, TextField } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable";
import React, { Fragment, useEffect, useState } from "react";
import useStyles from "screens/styles";
import { errorNoti, successNoti } from "utils/notification";
import { useHistory } from "react-router";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { useSelector } from "react-redux";
import {
    LineChart,
    Line,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell
} from 'recharts';

const EmployeeStatistics = () => {
    const history = useHistory();
    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [tabValue, setTabValue] = useState('1');
    const [statsData, setStatsData] = useState(null);
    const [startDate, setStartDate] = useState(new Date(new Date().setDate(new Date().getDate() - 30)));
    const [endDate, setEndDate] = useState(new Date());
    const [selectedEmployee, setSelectedEmployee] = useState('me');
    const [employeeList, setEmployeeList] = useState([]);
    const [employeeType, setEmployeeType] = useState('');

    // Get user info from Redux store
    const username = useSelector((state) => state.auth.user?.username);
    const role = useSelector((state) => state.auth.user?.role);
    const hubId = useSelector((state) => state.auth.user?.hubId);

    // Determine if user is admin or manager
    const isAdmin = role === 'ADMIN' || role === 'HUB_MANAGER';

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

    // Fetch employee list for admin/managers
    useEffect(() => {
        if (isAdmin) {
            const fetchCollectors = async () => {
                await request(
                    "get",
                    `/user/hub/${hubId}/collectors`,
                    (res) => {
                        if (res.data) {
                            setEmployeeList(prev => [...prev, ...res.data.map(c => ({
                                id: c.id,
                                name: c.fullName || c.username,
                                role: 'COLLECTOR'
                            }))]);
                        }
                    },
                    {
                        401: () => { },
                        503: () => { errorNoti("Có lỗi khi tải danh sách nhân viên thu gom") }
                    }
                );
            };

            const fetchShippers = async () => {
                await request(
                    "get",
                    `/user/hub/${hubId}/shippers`,
                    (res) => {
                        if (res.data) {
                            setEmployeeList(prev => [...prev, ...res.data.map(s => ({
                                id: s.id,
                                name: s.fullName || s.username,
                                role: 'SHIPPER'
                            }))]);
                        }
                    },
                    {
                        401: () => { },
                        503: () => { errorNoti("Có lỗi khi tải danh sách nhân viên giao hàng") }
                    }
                );
            };

            fetchCollectors();
            fetchShippers();
        }
    }, [isAdmin, hubId]);

    // Determine employee type based on role
    useEffect(() => {
        if (role === 'COLLECTOR') {
            setEmployeeType('collector');
        } else if (role === 'SHIPPER') {
            setEmployeeType('shipper');
        }
    }, [role]);

    // Fetch statistics data
    const fetchStatistics = async () => {
        setLoading(true);
        let endpoint;

        if (selectedEmployee === 'me') {
            // For current user
            if (employeeType === 'collector') {
                endpoint = `/smdeli/statistics/collector/me/range?startDate=${formatDateForApi(startDate)}&endDate=${formatDateForApi(endDate)}`;
            } else if (employeeType === 'shipper') {
                endpoint = `/smdeli/statistics/shipper/me/range?startDate=${formatDateForApi(startDate)}&endDate=${formatDateForApi(endDate)}`;
            }
        } else if (isAdmin && selectedEmployee !== 'me') {
            // For admin viewing specific employee
            const selectedEmployeeData = employeeList.find(e => e.id === selectedEmployee);
            if (selectedEmployeeData) {
                if (selectedEmployeeData.role === 'COLLECTOR') {
                    endpoint = `/smdeli/statistics/collector/${selectedEmployee}/range?startDate=${formatDateForApi(startDate)}&endDate=${formatDateForApi(endDate)}`;
                } else if (selectedEmployeeData.role === 'SHIPPER') {
                    endpoint = `/smdeli/statistics/shipper/${selectedEmployee}/range?startDate=${formatDateForApi(startDate)}&endDate=${formatDateForApi(endDate)}`;
                }
            }
        }

        if (endpoint) {
            await request(
                "get",
                endpoint,
                (res) => {
                    setStatsData(res.data);
                    setLoading(false);
                },
                {
                    401: () => { setLoading(false); },
                    503: () => {
                        errorNoti("Có lỗi khi tải dữ liệu thống kê");
                        setLoading(false);
                    }
                }
            );
        } else {
            setLoading(false);
        }
    };

    // Fetch statistics on initial load and when filters change
    useEffect(() => {
        if (employeeType) {
            fetchStatistics();
        }
    }, [employeeType]);

    // Generate data for status pie chart
    const prepareStatusData = () => {
        if (!statsData?.assignmentStatusCounts) return [];
        return Object.entries(statsData.assignmentStatusCounts)
            .filter(([status, count]) => count > 0) // Only include non-zero statuses
            .map(([status, count]) => ({
                name: status,
                value: count
            }));
    };

    // Generate colors for pie charts
    const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#FF6384'];

    // Calculate percentage for display
    const calculatePercentage = (value, total) => {
        if (!total) return 0;
        return ((value / total) * 100).toFixed(1);
    };

    // Prepare data for status breakdown table
    const prepareStatusTableData = () => {
        if (!statsData?.assignmentStatusCounts) return [];
        return Object.entries(statsData.assignmentStatusCounts)
            .map(([status, count]) => ({
                status,
                count,
                percentage: calculatePercentage(count, statsData.totalAssignments)
            }))
            .sort((a, b) => b.count - a.count); // Sort by count in descending order
    };

    return loading ? (
        <LoadingScreen />
    ) : (
        <Fragment>
            <Box className={classes.bodyBox}>
                <TabContext value={tabValue}>
                    <Box sx={{ borderBottom: 0, borderColor: "divider" }}>
                        <TabList onChange={(event, newValue) => setTabValue(newValue)}>
                            <Tab label="Tổng quan" value="1" />
                        </TabList>
                    </Box>

                    {/* Filter Controls */}
                    <Box sx={{ mb: 2, p: 2, backgroundColor: '#f5f5f5', borderRadius: 2 }}>
                        <Grid container spacing={2} alignItems="center">
                            {isAdmin && (
                                <Grid item xs={12} md={3}>
                                    <FormControl fullWidth>
                                        <InputLabel>Nhân viên</InputLabel>
                                        <Select
                                            value={selectedEmployee}
                                            label="Nhân viên"
                                            onChange={(e) => setSelectedEmployee(e.target.value)}
                                        >
                                            <MenuItem value="me">Bản thân tôi</MenuItem>
                                            {employeeList.map((employee) => (
                                                <MenuItem key={employee.id} value={employee.id}>
                                                    {employee.name} ({employee.role === 'COLLECTOR' ? 'Thu gom' : 'Giao hàng'})
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                            )}
                            <Grid item xs={12} md={isAdmin ? 3 : 4}>
                                <TextField
                                    label="Từ ngày"
                                    type="date"
                                    fullWidth
                                    value={startDate ? formatDateForInput(startDate) : ''}
                                    onChange={(e) => setStartDate(new Date(e.target.value))}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                />
                            </Grid>
                            <Grid item xs={12} md={isAdmin ? 3 : 4}>
                                <TextField
                                    label="Đến ngày"
                                    type="date"
                                    fullWidth
                                    value={endDate ? formatDateForInput(endDate) : ''}
                                    onChange={(e) => setEndDate(new Date(e.target.value))}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                />
                            </Grid>
                            <Grid item xs={12} md={isAdmin ? 3 : 4}>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    fullWidth
                                    onClick={fetchStatistics}
                                >
                                    Xem thống kê
                                </Button>
                            </Grid>
                        </Grid>
                    </Box>

                    {/* Overview Tab */}
                    <TabPanel value="1">
                        {statsData ? (
                            <>
                                <Typography variant="h6" gutterBottom>
                                    Thống kê {statsData.employeeRole === 'COLLECTOR' ? 'Thu gom' : 'Giao hàng'}: {statsData.employeeName}
                                </Typography>
                                <Typography variant="subtitle2" color="textSecondary" gutterBottom>
                                    Khoảng thời gian: {formatDateForInput(startDate)} đến {formatDateForInput(endDate)}
                                </Typography>

                                <Grid container spacing={2}>
                                    {/* Summary Cards */}
                                    <Grid item xs={12} md={3}>
                                        <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                            <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                                Tổng số đơn
                                            </Typography>
                                            <Typography variant="h3" sx={{ color: 'primary.main' }}>
                                                {statsData.totalAssignments || 0}
                                            </Typography>
                                        </Paper>
                                    </Grid>
                                    <Grid item xs={12} md={3}>
                                        <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                            <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                                Đơn hoàn thành
                                            </Typography>
                                            <Typography variant="h4" sx={{ color: 'success.main' }}>
                                                {statsData.completedAssignments || 0}
                                            </Typography>
                                            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                {calculatePercentage(statsData.completedAssignments, statsData.totalAssignments)}% tổng số đơn
                                            </Typography>
                                        </Paper>
                                    </Grid>
                                    <Grid item xs={12} md={3}>
                                        <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                            <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                                Đơn thất bại
                                            </Typography>
                                            <Typography variant="h4" sx={{ color: 'error.main' }}>
                                                {statsData.failedAssignments || 0}
                                            </Typography>
                                            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                {calculatePercentage(statsData.failedAssignments, statsData.totalAssignments)}% tổng số đơn
                                            </Typography>
                                        </Paper>
                                    </Grid>
                                    <Grid item xs={12} md={3}>
                                        <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
                                            <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                                Đơn đang xử lý
                                            </Typography>
                                            <Typography variant="h4" sx={{ color: 'warning.main' }}>
                                                {statsData.pendingAssignments || 0}
                                            </Typography>
                                            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                {calculatePercentage(statsData.pendingAssignments, statsData.totalAssignments)}% tổng số đơn
                                            </Typography>
                                        </Paper>
                                    </Grid>

                                    {/* Performance Metrics */}
                                    <Grid item xs={12} md={6}>
                                        <Paper sx={{ p: 2, height: '100%' }}>
                                            <Typography variant="h6" gutterBottom>
                                                Hiệu suất
                                            </Typography>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12} md={6}>
                                                    <Box sx={{ textAlign: 'center', p: 2, border: '1px solid #e0e0e0', borderRadius: 1 }}>
                                                        <Typography variant="body1">Tỉ lệ thành công</Typography>
                                                        <Typography variant="h4" sx={{ color: 'primary.main' }}>
                                                            {(statsData.successRate || 0).toFixed(1)}%
                                                        </Typography>
                                                    </Box>
                                                </Grid>
                                                <Grid item xs={12} md={6}>
                                                    <Box sx={{ textAlign: 'center', p: 2, border: '1px solid #e0e0e0', borderRadius: 1 }}>
                                                        <Typography variant="body1">Thời gian xử lý TB</Typography>
                                                        <Typography variant="h4" sx={{ color: 'secondary.main' }}>
                                                            {(statsData.averageCompletionTime || 0).toFixed(1)} phút
                                                        </Typography>
                                                    </Box>
                                                </Grid>
                                            </Grid>
                                        </Paper>
                                    </Grid>

                                    {/* Status Distribution */}
                                    <Grid item xs={12} md={6}>
                                        <Paper sx={{ p: 2, height: '100%' }}>
                                            <Typography variant="h6" gutterBottom>
                                                Phân bố trạng thái
                                            </Typography>
                                            <Box sx={{ height: 250 }}>
                                                <ResponsiveContainer width="100%" height="100%">
                                                    <PieChart>
                                                        <Pie
                                                            data={prepareStatusData()}
                                                            cx="50%"
                                                            cy="50%"
                                                            labelLine={false}
                                                            label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(1)}%`}
                                                            outerRadius={80}
                                                            fill="#8884d8"
                                                            dataKey="value"
                                                        >
                                                            {prepareStatusData().map((entry, index) => (
                                                                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                                            ))}
                                                        </Pie>
                                                        <Tooltip />
                                                    </PieChart>
                                                </ResponsiveContainer>
                                            </Box>
                                        </Paper>
                                    </Grid>
                                </Grid>
                            </>
                        ) : (
                            <Box sx={{ textAlign: 'center', p: 4 }}>
                                <Typography variant="h6">
                                    Không có dữ liệu thống kê trong khoảng thời gian đã chọn
                                </Typography>
                            </Box>
                        )}
                    </TabPanel>



                </TabContext>
            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_EMPLOYEE_STATISTICS";
export default EmployeeStatistics;