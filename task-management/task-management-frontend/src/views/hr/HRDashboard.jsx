import React, { useEffect, useState, useCallback } from 'react';
import { Grid, Card, CardContent, Typography, Box, CircularProgress, useTheme, Paper, Button, ThemeProvider, CssBaseline } from '@mui/material';
import Chart from 'react-apexcharts';
import hrService from '@/services/api/hr.service'; // Đảm bảo đường dẫn chính xác
import PeopleIcon from '@mui/icons-material/People';
import BusinessIcon from '@mui/icons-material/Business';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import RefreshIcon from '@mui/icons-material/Refresh';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import { format, subMonths, eachMonthOfInterval, startOfMonth, endOfMonth } from 'date-fns';
import toast from 'react-hot-toast';
import { theme } from "./theme.js"; // Import theme của bạn

// --- Component phụ cho thẻ thống kê ---
const StatCard = ({ title, value, icon, color }) => (
  <Card elevation={3} sx={{ height: '100%', border: `1px solid ${useTheme().palette.divider}` }}>
    <CardContent>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Box>
          <Typography color="text.secondary" gutterBottom variant="body2">{title}</Typography>
          <Typography variant="h5" component="div" fontWeight="bold">{value}</Typography>
        </Box>
        <Box>
          {React.cloneElement(icon, { style: { fontSize: 40, color: color } })}
        </Box>
      </Box>
    </CardContent>
  </Card>
);

// --- Component render Chart hoặc Loading ---
const ChartCard = ({ isLoading, chartOptions, chartSeries, type, height = 350, emptyState }) => (
  <Card elevation={2} sx={{ height: '100%', border: `1px solid ${useTheme().palette.divider}` }}>
    <CardContent sx={{ height: '100%' }}>
      {isLoading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', minHeight: height }}>
          <CircularProgress />
        </Box>
      ) : chartSeries && chartSeries.length > 0 && chartSeries.some(s => s.data && s.data.length > 0 || !s.data) ? (
        <Chart options={chartOptions} series={chartSeries} type={type} height={height} />
      ) : (
        emptyState || <Typography>Không có dữ liệu</Typography>
      )}
    </CardContent>
  </Card>
);

// --- Component chính cho Dashboard ---
const InnerHRDashboard = () => {
  const theme = useTheme();
  const [stats, setStats] = useState({
    totalEmployees: 0,
    totalActiveDepartments: 0,
    employeesOnLeaveToday: 0,
  });
  const [chartData, setChartData] = useState({
    department: { series: [] },
    jobPosition: { series: [] },
    newHires: { series: [{ data: [] }] },
    absenceType: { series: [] },
  });
  const [loading, setLoading] = useState({
    stats: true,
    department: true,
    jobPosition: true,
    newHires: true,
    absenceType: true,
  });

  const fetchData = useCallback(async () => {
    setLoading({ stats: true, department: true, jobPosition: true, newHires: true, absenceType: true });
    try {
      const [staffRes, departmentRes] = await Promise.all([
        hrService.getAllStaffDetails({ page: 0, size: 2000, status: 'ACTIVE' }),
        hrService.getAllDepartments({ page: 0, size: 500 }),
      ]);

      const staff = staffRes.response?.data?.data || [];
      const departments = departmentRes.response?.data?.data || [];

      const totalEmployees = staffRes.response?.data?.meta?.page_info?.total_records || staff.length;
      const activeDepartmentsCount = departments.filter(d => d.status === 'ACTIVE').length;

      // Xử lý các chart không phụ thuộc vào absence
      const chartTitleStyle = {
        fontSize: '16px', fontWeight: 600, color: theme.palette.text.primary, fontFamily: theme.typography.fontFamily
      };

      const today = new Date();
      const last12MonthsInterval = { start: subMonths(today, 11), end: today };
      const monthSeries = eachMonthOfInterval(last12MonthsInterval);
      const monthlyHiresMap = new Map(monthSeries.map(month => [format(month, 'yyyy-MM'), 0]));
      staff.forEach(s => {
        if (s.date_of_join) {
          const joinMonthKey = format(new Date(s.date_of_join), 'yyyy-MM');
          if (monthlyHiresMap.has(joinMonthKey)) {
            monthlyHiresMap.set(joinMonthKey, monthlyHiresMap.get(joinMonthKey) + 1);
          }
        }
      });
      const sortedMonths = Array.from(monthlyHiresMap.keys()).sort();
      const chartLabels = sortedMonths.map(key => format(startOfMonth(new Date(key)), 'MM/yy'));
      const chartData = sortedMonths.map(key => monthlyHiresMap.get(key));
      setChartData(prev => ({...prev, newHires: {
          options: { chart: { type: 'bar', toolbar: { show: false } }, xaxis: { categories: chartLabels }, title: { text: 'Nhân viên mới (12 tháng qua)', align: 'center', style: chartTitleStyle }, dataLabels: { enabled: false }, colors: [theme.palette.primary.main] },
          series: [{ name: 'Số lượng', data: chartData }]
        }}));

      const deptCounts = staff.reduce((acc, s) => {
        const deptName = s.department?.department_name || 'Chưa phân công';
        acc[deptName] = (acc[deptName] || 0) + 1;
        return acc;
      }, {});
      setChartData(prev => ({...prev, department: {
          options: { chart: { type: 'pie' }, labels: Object.keys(deptCounts), title: { text: 'Phân bổ nhân viên theo phòng ban', align: 'center', style: chartTitleStyle }, legend: { position: 'bottom' }, stroke: { show: false }, colors: [theme.palette.primary.light, theme.palette.success.light, theme.palette.warning.light, theme.palette.success.main, theme.palette.error.light, theme.palette.info.light, theme.palette.warning.dark, theme.palette.info.dark] },
          series: Object.values(deptCounts)
        }}));

      const jobPosCounts = staff.reduce((acc, s) => {
        const jobPosName = s.job_position?.job_position_name || 'Chưa phân công';
        acc[jobPosName] = (acc[jobPosName] || 0) + 1;
        return acc;
      }, {});
      setChartData(prev => ({...prev, jobPosition: {
          options: { chart: { type: 'bar', toolbar: { show: false } }, plotOptions: { bar: { horizontal: true, borderRadius: 4, distributed: true } }, xaxis: { categories: Object.keys(jobPosCounts) }, title: { text: 'Phân bổ nhân viên theo chức vụ', align: 'center', style: chartTitleStyle }, legend: { show: false } },
          series: [{ name: 'Số lượng', data: Object.values(jobPosCounts) }]
        }}));
      setLoading(prev => ({ ...prev, newHires: false, department: false, jobPosition: false }));

      // Xử lý dữ liệu absence
      const userIds = staff.map(s => s.id || s.user_login_id).filter(Boolean).join(',');
      if (userIds) {
        const firstDayCurrentMonth = startOfMonth(today);
        const lastDayCurrentMonth = endOfMonth(today);

        const absenceRes = await hrService.getAbsenceList({
          userIds: userIds,
          startDate: format(firstDayCurrentMonth, 'yyyy-MM-dd'),
          endDate: format(lastDayCurrentMonth, 'yyyy-MM-dd'),
          size: 2000
        });

        const allAbsencesInMonth = absenceRes.response?.data?.data || [];
        const todayString = today.toDateString();
        const uniqueStaffOnLeaveToday = new Set();
        allAbsencesInMonth.forEach(a => {
          if (new Date(a.fromDate).toDateString() === todayString && a.status === 'APPROVED') {
            uniqueStaffOnLeaveToday.add(a.staff?.id);
          }
        });
        setStats({ totalEmployees, totalActiveDepartments: activeDepartmentsCount, employeesOnLeaveToday: uniqueStaffOnLeaveToday.size });
        setLoading(prev => ({...prev, stats: false}));

        const absenceTypeCounts = allAbsencesInMonth.reduce((acc, record) => {
          if (record.type === 'UNPAID_LEAVE') { acc['Nghỉ không lương'] = (acc['Nghỉ không lương'] || 0) + 1; }
          else { acc['Nghỉ có lương'] = (acc['Nghỉ có lương'] || 0) + 1; }
          return acc;
        }, { 'Nghỉ có lương': 0, 'Nghỉ không lương': 0 });
        setChartData(prev => ({...prev, absenceType: {
            options: { chart: { type: 'donut' }, labels: Object.keys(absenceTypeCounts), title: { text: 'Tỷ lệ nghỉ phép (tháng này)', align: 'center', style: chartTitleStyle }, legend: { position: 'bottom' }, dataLabels: { enabled: true, formatter: (val) => `${val.toFixed(1)}%` }, stroke: { show: false }, colors: [theme.palette.primary.main, theme.palette.warning.main] },
            series: Object.values(absenceTypeCounts),
            total: allAbsencesInMonth.length
          }}));
        setLoading(prev => ({...prev, absenceType: false}));
      } else {
        setStats({ totalEmployees, totalActiveDepartments: activeDepartmentsCount, employeesOnLeaveToday: 0 });
        setLoading({ stats: false, newHires: false, department: false, jobPosition: false, absenceType: false });
      }

    } catch (error) {
      console.error("Failed to fetch HR dashboard data", error);
      toast.error("Không thể tải dữ liệu cho dashboard!");
      setLoading({ stats: false, newHires: false, department: false, jobPosition: false, absenceType: false });
    }
  }, [theme]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const EmptyAbsenceState = (
    <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: 350, color: 'text.secondary', textAlign: 'center' }}>
      <EventAvailableIcon sx={{ fontSize: 50, mb: 1 }} />
      <Typography variant="h6">Không có dữ liệu</Typography>
      <Typography variant="body2">Tháng này không có ai nghỉ phép.</Typography>
    </Box>
  );

  return (
    <Box sx={{ p: 2 }} >
      <Grid container justifyContent="space-between" alignItems="center" mb={2}>
        <Grid item>
          <Typography variant="h4" fontWeight={700} color="text.primary">
            Dashboard
          </Typography>
        </Grid>
        <Grid item>
          <Button variant="contained" startIcon={<RefreshIcon />} onClick={fetchData} disabled={loading.stats}>Làm mới</Button>
        </Grid>
      </Grid>

      <Paper elevation={0} sx={{ width: '100%', background: 'transparent' }}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6} md={4}>
                <StatCard title="Tổng số nhân viên" value={loading.stats ? '...' : stats.totalEmployees} icon={<PeopleIcon />} color={theme.palette.primary.main} />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <StatCard title="Phòng ban hoạt động" value={loading.stats ? '...' : stats.totalActiveDepartments} icon={<BusinessIcon />} color={theme.palette.success.main} />
              </Grid>
              <Grid item xs={12} sm={12} md={4}>
                <StatCard title="Nhân viên nghỉ hôm nay" value={loading.stats ? '...' : stats.employeesOnLeaveToday} icon={<EventBusyIcon />} color={theme.palette.error.main} />
              </Grid>
            </Grid>
          </Grid>

          <Grid item xs={12}>
            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <ChartCard isLoading={loading.newHires} chartOptions={chartData.newHires.options} chartSeries={chartData.newHires.series} type="bar" />
              </Grid>
              <Grid item xs={12} md={6}>
                <ChartCard isLoading={loading.department} chartOptions={chartData.department.options} chartSeries={chartData.department.series} type="pie" />
              </Grid>
              <Grid item xs={12} md={6}>
                <ChartCard isLoading={loading.jobPosition} chartOptions={chartData.jobPosition.options} chartSeries={chartData.jobPosition.series} type="bar" />
              </Grid>
              <Grid item xs={12} md={6}>
                <ChartCard isLoading={loading.absenceType} chartOptions={chartData.absenceType.options} chartSeries={chartData.absenceType.series} type="donut" emptyState={EmptyAbsenceState} />
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Paper>
    </Box>
  );
};

const HRDashboard = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ height: 'calc(100vh - 64px)', overflowY: 'auto' }} className="custom-scrollbar">
        <InnerHRDashboard />
      </Box>
    </ThemeProvider>
  );
};

export default HRDashboard;