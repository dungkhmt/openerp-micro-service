import React, { useEffect, useState, useCallback } from 'react';
import {
  Grid, Typography, Box, CircularProgress, Paper,
  Chip, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Divider, ThemeProvider, CssBaseline, useTheme
} from '@mui/material';
import hrService from '@/services/api/hr.service';
import { format, parseISO, isValid, addDays } from 'date-fns';
import { vi } from 'date-fns/locale';
import toast from 'react-hot-toast';

import BeachAccessIcon from '@mui/icons-material/BeachAccess';
import AccessAlarmIcon from '@mui/icons-material/AccessAlarm';
import HistoryToggleOffIcon from '@mui/icons-material/HistoryToggleOff';
import AssignmentTurnedInIcon from '@mui/icons-material/AssignmentTurnedIn';
import ArticleOutlinedIcon from '@mui/icons-material/ArticleOutlined';
import PaidOutlinedIcon from '@mui/icons-material/PaidOutlined';
import AlarmOffIcon from '@mui/icons-material/AlarmOff';
import FestivalIcon from '@mui/icons-material/Festival';
import TodayIcon from '@mui/icons-material/Today';

import PayrollDetailModal from "./PayrollDetailModal.jsx";
import GradeDetailModal from "./GradeDetailModal.jsx";
import { PayrollList, GradeList } from './PayrollAndGradeList.jsx';
import {theme} from "./theme.js";

const StatCard = ({ title, value, icon, color }) => (
  <Paper elevation={2} sx={{
    height: '100%',
    border: `1px solid ${useTheme().palette.divider}`, borderRadius: 3, boxShadow: 2,
    bgcolor: color === 'info' ? 'rgba(2,132,199,0.06)'
      : color === 'success' ? 'rgba(34,197,94,0.07)'
        : color === 'warning' ? 'rgba(251,191,36,0.07)'
          : color === 'error' ? 'rgba(239,68,68,0.07)'
            : undefined,
  }}>
    <Box sx={{ display: 'flex', alignItems: 'center', p: 2 }}>
      <Box sx={{
        bgcolor: color + '.lighter',
        color: color + '.dark',
        mr: 2,
        width: 48, height: 48,
        fontSize: 28,
        display: "flex", alignItems: "center", justifyContent: "center",
        borderRadius: "50%",
        boxShadow: 1,
      }}>
        {icon}
      </Box>
      <Box>
        <Typography color="text.secondary" variant="body2">{title}</Typography>
        <Typography variant="h6" fontWeight="bold">{value}</Typography>
      </Box>
    </Box>
  </Paper>
);

const safeFormatDate = (dateString, formatStr) => {
  if (!dateString) return "N/A";
  const date = parseISO(dateString);
  return isValid(date) ? format(date, formatStr, { locale: vi }) : "";
};
function parseTimeStringToDate(timeString) {
  if (!timeString || typeof timeString !== 'string') return null;
  const [hours, minutes] = timeString.split(':').map(Number);
  if (isNaN(hours) || isNaN(minutes)) return null;
  return new Date(2000, 0, 1, hours, minutes, 0, 0);
}
function calculateWorkHours(start, end, lunchStart, lunchEnd) {
  if (!start || !end || !lunchStart || !lunchEnd) return 0;
  if (end <= start) return 0;
  let totalMinutes = (end - start) / 60000;
  const overlapStart = Math.max(start.getTime(), lunchStart.getTime());
  const overlapEnd = Math.min(end.getTime(), lunchEnd.getTime());
  if (overlapEnd > overlapStart) totalMinutes -= (overlapEnd - overlapStart) / 60000;
  return Math.max(0, totalMinutes / 60);
}

const InnerEmployeeDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState({
    employee: null,
    stat: {
      leaveHours: 0, lateDays: 0, presentDays: 0, absentDays: 0,
      absencesCount: 0, paidLeaveHours: 0, unpaidLeaveHours: 0, holidayCount: 0
    },
    absences: [],
  });

  const [shifts, setShifts] = useState([]);
  const [shiftsLoading, setShiftsLoading] = useState(true);

  const [payrollPage, setPayrollPage] = useState(0);
  const [payrolls, setPayrolls] = useState([]);
  const [payrollTotalPages, setPayrollTotalPages] = useState(1);

  const [gradePage, setGradePage] = useState(0);
  const [grades, setGrades] = useState([]);
  const [gradeTotalPages, setGradeTotalPages] = useState(1);

  const [payrollModal, setPayrollModal] = useState(null);
  const [gradeModal, setGradeModal] = useState(null);

  const today = new Date();
  const monthStr = format(today, 'yyyy-MM');
  const monthStart = format(today, "yyyy-MM-01");
  const monthEnd = format(new Date(today.getFullYear(), today.getMonth() + 1, 0), "yyyy-MM-dd");

  const fetchPayrolls = useCallback(async (userId, page = 0) => {
    try {
      const res = await hrService.getPayrollList({
        staffId: userId,
        page, pageSize: 5, sortBy: 'fromdate', status: "ACTIVE"
      });
      setPayrolls(res.response?.data?.data || []);
      setPayrollTotalPages(res.response?.data?.meta.page_info?.total_page || 1);
    } catch (e) { setPayrolls([]); }
  }, []);

  const fetchGrades = useCallback(async (userId, page = 0) => {
    try {
      const res = await hrService.getCheckpointPeriods({
        userId, status: 'ACTIVE', page, pageSize: 5
      });
      setGrades(res.response?.data?.data || []);
      console.log(res.response?.data?.meta.page_info.total_page)
      setGradeTotalPages(res.response?.data?.meta.page_info?.total_page || 1);
    } catch (e) { setGrades([]); }
  }, []);

  // Fetch shifts for 7 next days
  const fetchShifts = useCallback(async (userId) => {
    setShiftsLoading(true);
    try {
      const res = await hrService.getShiftList({
        userIds: [userId],
        startDate: format(today, 'yyyy-MM-dd'),
        endDate: format(addDays(today, 6), 'yyyy-MM-dd'),
      });
      setShifts(res.response?.data?.data || []);
    } catch (e) {
      setShifts([]);
    } finally {
      setShiftsLoading(false);
    }
  }, [today]);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      // Lấy thông tin nhân viên
      const staffRes = await hrService.getMyStaffDetails();
      const staff = staffRes.response?.data?.data;
      const userId = staff?.user_login_id || staff?.id;
      fetchShifts(userId);
      fetchPayrolls(userId, payrollPage);
      fetchGrades(userId, gradePage);

      let configs = {
        START_WORK_TIME: "08:00",
        END_WORK_TIME: "17:00",
        START_LUNCH_TIME: "12:00",
        END_LUNCH_TIME: "13:00"
      };
      try {
        const configsRes = await hrService.getCompanyConfigs();
        if (configsRes.response?.data?.data) {
          Object.entries(configsRes.response.data.data).forEach(([k, v]) => {
            configs[k] = v.config_value;
          });
        }
      } catch {}

      // Absence
      const absenceRes = await hrService.getAbsenceList({
        userIds: [userId], page: 0, pageSize: 100, status: "ACTIVE", startDate: monthStart, endDate: monthEnd
      });
      const absences = absenceRes.response?.data?.data || [];
      const workTimes = {
        startWork: parseTimeStringToDate(configs.START_WORK_TIME),
        endWork: parseTimeStringToDate(configs.END_WORK_TIME),
        lunchStart: parseTimeStringToDate(configs.START_LUNCH_TIME),
        lunchEnd: parseTimeStringToDate(configs.END_LUNCH_TIME),
      };
      let paidLeaveHours = 0, unpaidLeaveHours = 0;
      absences.forEach(abs => {
        const start = parseTimeStringToDate(abs.start_time);
        const end = parseTimeStringToDate(abs.end_time);
        if (!start || !end) return;
        const hours = calculateWorkHours(start, end, workTimes.lunchStart, workTimes.lunchEnd);
        if ((abs.type || '').toUpperCase().includes('UNPAID')) unpaidLeaveHours += hours;
        else if ((abs.type || '').toUpperCase().includes('PAID')) paidLeaveHours += hours;
      });

      let holidayCount = 0;
      let holidaySet = new Set();
      try {
        const holidayRes = await hrService.getHolidays({ month: monthStr });
        const holidays = holidayRes.response?.data?.holidays || holidayRes.response?.data?.data || {};
        const daysOfMonth = [];
        let d = parseISO(monthStart);
        const m = d.getMonth();
        while (d.getMonth() === m) { daysOfMonth.push(format(d, 'yyyy-MM-dd')); d.setDate(d.getDate() + 1); }
        if (Array.isArray(holidays)) {
          holidaySet = new Set(holidays.map(h => h.date));
          holidayCount = daysOfMonth.filter(d => holidaySet.has(d)).length;
        } else {
          holidayCount = daysOfMonth.filter(d => holidays[d]).length;
        }
      } catch {}

      // Attendance/stat
      const attendanceRes = await hrService.getMonthAttendance({ userIds: [userId], month: monthStr });
      const attData = attendanceRes.response?.data?.data?.user_attendances?.[userId] || {};
      let late = 0, absent = 0, present = 0;
      Object.entries(attData).forEach(([date, a]) => {
        if (a.attendanceType === "PRESENT") {
          present += 1;
          return;
        }
        const d = parseISO(date);
        const dow = d.getDay();
        if (dow === 0 || dow === 6 || holidaySet.has(date)) return;

        if (a.attendanceType === "LATE" || a.attendanceType === "INCOMPLETE") late += 1;
        else if (a.attendanceType === "ABSENT" || a.attendanceType === "MISSING") absent += 1;
      });

      setData({
        employee: staff,
        stat: {
          leaveHours: staff.leave_hours || staff.leaveHours || 0,
          lateDays: late, presentDays: present, absentDays: absent,
          absencesCount: absences.length,
          paidLeaveHours: Number(paidLeaveHours.toFixed(2)),
          unpaidLeaveHours: Number(unpaidLeaveHours.toFixed(2)),
          holidayCount
        },
        absences
      });
    } catch (e) {
      console.log(e)
      toast.error("Lỗi khi tải dữ liệu dashboard");
    } finally {
      setLoading(false);
    }
  }, [payrollPage, gradePage, fetchShifts, fetchPayrolls, fetchGrades]);

  useEffect(() => { fetchData(); }, []);
  useEffect(() => {
    if (data.employee) fetchPayrolls(data.employee.user_login_id || data.employee.id, payrollPage);
  }, [payrollPage, data.employee, fetchPayrolls]);
  useEffect(() => {
    if (data.employee) fetchGrades(data.employee.user_login_id || data.employee.id, gradePage);
  }, [gradePage, data.employee, fetchGrades]);

  if (loading || !data.employee) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}><CircularProgress /></Box>;
  }

  const statCardData = [
    { title: "Giờ phép còn lại", value: `${data.stat.leaveHours}h`, icon: <BeachAccessIcon />, color: "info" },
    { title: "Số ngày đi muộn", value: data.stat.lateDays, icon: <AccessAlarmIcon />, color: "warning" },
    { title: "Số ngày đi làm", value: data.stat.presentDays, icon: <PaidOutlinedIcon />, color: "success" },
    { title: "Checkout missing", value: data.stat.absentDays, icon: <HistoryToggleOffIcon />, color: "error" },
    { title: "Số đơn nghỉ phép", value: data.stat.absencesCount, icon: <AssignmentTurnedInIcon />, color: "info" },
    { title: "Tổng giờ nghỉ có lương", value: `${data.stat.paidLeaveHours}h`, icon: <PaidOutlinedIcon />, color: "success" },
    { title: "Tổng giờ nghỉ không lương", value: `${data.stat.unpaidLeaveHours}h`, icon: <AlarmOffIcon />, color: "warning" },
    { title: "Số ngày nghỉ lễ", value: data.stat.holidayCount, icon: <FestivalIcon />, color: "info" },
  ];

  const days7 = Array.from({ length: 7 }).map((_, idx) => {
    const d = addDays(today, idx);
    const dayStr = format(d, 'yyyy-MM-dd');
    const dayLabel = format(d, 'EEE', { locale: vi });
    const shiftsInDay = shifts.filter(shift => shift.date === dayStr);
    return {
      date: d,
      dayLabel: dayLabel.charAt(0).toUpperCase() + dayLabel.slice(1),
      displayDate: format(d, 'dd/MM'),
      shifts: shiftsInDay
    };
  });

  return (
    <Box sx={{ p: 3, height: 'calc(100vh - 64px)', overflowY: 'auto', background: '#f7f8fa' }} className="custom-scrollbar">
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Chào mừng trở lại, {data.employee?.fullname || data.employee?.fullName || "bạn"}!
      </Typography>
      <Typography variant="subtitle2" color="primary" gutterBottom>
        Thống kê dữ liệu cá nhân cho tháng {format(today, "MM/yyyy")}
      </Typography>
      {/* Stat cards */}
      <Grid container spacing={2} sx={{ mb: 2 }}>
        {statCardData.map(card => (
          <Grid item xs={12} sm={6} md={3} key={card.title}>
            <StatCard {...card} />
          </Grid>
        ))}
      </Grid>
      <Paper elevation={3} sx={{ p: 2, mb: 2, borderRadius: 3, border: `1px solid ${useTheme().palette.divider}` }}>
        <Typography variant="h6" fontWeight={600} mb={2} display="flex" alignItems="center">
          <TodayIcon sx={{ mr: 1, color: 'primary.main' }} /> Lịch làm việc 7 ngày tới
        </Typography>
        <Box className="custom-scrollbar" sx={{
          display: 'flex', gap: 2, overflowX: 'auto', pb: 1
        }}>
          {days7.map((day, idx) => (
            <Box key={idx} sx={{
              minWidth: 175, flex: '0 0 175px', background: "#fff", borderRadius: 3, boxShadow: 1,
              border: '1px solid #eee', px: 2, py: 2, display: 'flex', flexDirection: 'column', alignItems: 'center'
            }}>
              <Typography fontWeight="bold" sx={{ fontSize: 19 }}>{day.dayLabel}</Typography>
              <Typography color="text.secondary" sx={{ mb: 1 }}>{day.displayDate}</Typography>
              <Divider sx={{ width: '100%', mb: 1 }} />
              <Box sx={{ minHeight: 32, width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
                {shiftsLoading ? <CircularProgress size={18} /> : (
                  day.shifts.length > 0 ? day.shifts.map((shift, i) => (
                    <Chip key={i} label={`${shift.start_time?.substring(0,5)} - ${shift.end_time?.substring(0,5)}`} color="primary" size="small" sx={{ mt: 0.5, fontWeight: 600 }} />
                  )) : (
                    <Typography variant="caption" color="text.disabled">Không có lịch</Typography>
                  )
                )}
              </Box>
            </Box>
          ))}
        </Box>
      </Paper>
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <PayrollList
            data={payrolls}
            page={payrollPage}
            totalPages={payrollTotalPages}
            onPrev={() => setPayrollPage(p => Math.max(0, p - 1))}
            onNext={() => setPayrollPage(p => p + 1)}
            onDetail={p => setPayrollModal({ id: p.id, name: p.name })}
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <GradeList
            data={grades}
            page={gradePage}
            totalPages={gradeTotalPages}
            onPrev={() => setGradePage(p => Math.max(0, p - 1))}
            onNext={() => setGradePage(p => p + 1)}
            onDetail={g => setGradeModal({ id: g.id || g.checkpointPeriodId, name: g.checkpointPeriodName || g.name })}
          />
        </Grid>
      </Grid>
      {/* Absence block (bảng lịch sử nghỉ phép tháng) */}
      {/*<Paper elevation={3} sx={{ p: 2, borderRadius: 3, mt: 3 }}>
        <Typography variant="h6" fontWeight={600} mb={1} display="flex" alignItems="center">
          <ArticleOutlinedIcon sx={{ mr: 1, color: 'info.main' }} /> Đơn nghỉ phép trong tháng
        </Typography>
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Ngày</TableCell>
                <TableCell>Loại nghỉ</TableCell>
                <TableCell>Lý do</TableCell>
                <TableCell>Trạng thái</TableCell>
                <TableCell align="right">Giờ bắt đầu</TableCell>
                <TableCell align="right">Giờ kết thúc</TableCell>
                <TableCell align="right">Số giờ</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {data.absences.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} align="center" style={{ color: "#888" }}>
                    Không có đơn nghỉ phép nào.
                  </TableCell>
                </TableRow>
              ) : (
                data.absences.map(a => {
                  let total = 0;
                  const start = parseTimeStringToDate(a.start_time);
                  const end = parseTimeStringToDate(a.end_time);
                  const lunchStart = parseTimeStringToDate(data.stat.START_LUNCH_TIME || "12:00");
                  const lunchEnd = parseTimeStringToDate(data.stat.END_LUNCH_TIME || "13:00");
                  if (start && end && lunchStart && lunchEnd) {
                    total = calculateWorkHours(start, end, lunchStart, lunchEnd);
                  }
                  return (
                    <TableRow key={a.id}>
                      <TableCell>
                        {safeFormatDate(a.date || a.fromDate, "dd/MM/yyyy")}
                      </TableCell>
                      <TableCell>
                        <Chip label={a.type} color={a.type?.toLowerCase().includes('paid') ? "success" : "warning"} size="small" />
                      </TableCell>
                      <TableCell>{a.reason || "-"}</TableCell>
                      <TableCell>
                        <Chip label={a.status} color={a.status === "APPROVED" ? "success" : "warning"} size="small" />
                      </TableCell>
                      <TableCell align="right">{a.start_time}</TableCell>
                      <TableCell align="right">{a.end_time}</TableCell>
                      <TableCell align="right">{total.toFixed(2)}</TableCell>
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>*/}
      {/* MODAL payroll */}
      <PayrollDetailModal
        open={!!payrollModal}
        onClose={() => setPayrollModal(null)}
        payrollId={payrollModal?.id}
        userId={data.employee?.user_login_id || data.employee?.id}
        payrollName={payrollModal?.name}
      />
      {/* MODAL grade */}
      <GradeDetailModal
        open={!!gradeModal}
        onClose={() => setGradeModal(null)}
        checkpointId={gradeModal?.id}
        userId={data.employee?.user_login_id || data.employee?.id}
        checkpointName={gradeModal?.name}
      />
    </Box>
  );
};

const EmployeeDashboard = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <InnerEmployeeDashboard />
    </ThemeProvider>
  );
};

export default EmployeeDashboard;
