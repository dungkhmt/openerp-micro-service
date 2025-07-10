import React, {useEffect, useMemo, useState, useCallback} from "react";
import {useNavigate} from "react-router-dom";
import {
  ThemeProvider,
  CssBaseline,
  Box,
  Button,
  Grid,
  TextField,
  Typography,
  Paper,
  CircularProgress,
  TableContainer,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Tooltip,
  Avatar,
  Autocomplete,
  Snackbar,
  Alert as MuiAlert,
} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

import {theme} from './theme'; // Đường dẫn tới file theme.js của bạn
import {request}from "@/api";
import SearchSelect from "@/components/item/SearchSelect";
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce";

import dayjs from "dayjs";
import "dayjs/locale/vi";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import toast from "react-hot-toast";

dayjs.extend(isSameOrBefore);
dayjs.locale('vi');

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const AttendancePageInternal = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [attendances, setAttendances] = useState({});
  const [selectedDept, setSelectedDept] = useState(null);
  const [selectedPos, setSelectedPos] = useState(null);
  const [searchName, setSearchName] = useState("");
  const [selectedMonth, setSelectedMonth] = useState(dayjs());
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [pageCount, setPageCount] = useState(0);
  const [holidays, setHolidays] = useState({});
  const [loading, setLoading] = useState(true);
  const [loadingAttendances, setLoadingAttendances] = useState(false);

  const debouncedSearchName = useDebounce(searchName, 500);
  const todayStr = dayjs().format("YYYY-MM-DD");

  const fetchHolidays = useCallback(async (monthDate) => {
    const yearMonthStr = dayjs(monthDate).format("YYYY-MM");
    try {
      await request("get", `/holidays?month=${yearMonthStr}`, (res) => {
        const holidayData = res.data.holidays || {};
        setHolidays(holidayData);
      }, { onError: (err) => console.error("Lỗi tải ngày lễ:", err) });
    } catch (error) { console.error("Ngoại lệ khi tải ngày lễ:", error); }
  }, []);

  const fetchEmployeesAndAttendances = useCallback(async (page = 0, size = itemsPerPage, name = debouncedSearchName, dept = selectedDept, pos = selectedPos, month = selectedMonth) => {
    setLoading(true);
    const empPayload = {
      fullname: name || null,
      departmentCode: dept?.department_code || null,
      jobPositionCode: pos?.code || null,
      status: "ACTIVE",
      page,
      pageSize: size
    };

    let fetchedEmployees = [];
    try {
      const empRes = await new Promise((resolve, reject) => {
        request("get", "/staffs/details", (res) => resolve(res), { onError: (err) => reject(err) }, null, { params: empPayload });
      });
      fetchedEmployees = (empRes.data.data || []).map(e => ({ ...e, id: e.staff_code }));
      const meta = empRes.data.meta || {};
      setEmployees(fetchedEmployees);
      setPageCount(meta.page_info?.total_page || 1);
      setCurrentPage(meta.page_info?.page || 0);
    } catch (err) {
      console.error("Lỗi tải danh sách nhân viên:", err);
      toast.error("Không thể tải danh sách nhân viên.");
      setEmployees([]);
    }

    await fetchHolidays(month);

    if (fetchedEmployees.length > 0) {
      setLoadingAttendances(true);
      const userIds = fetchedEmployees.map((e) => e.user_login_id).filter(Boolean);
      if (userIds.length > 0) {
        try {
          await request("get", "/checkinout", (res) => {
            setAttendances(res.data?.data?.user_attendances || {});
          }, {
            onError: (err) => { console.error("Lỗi tải dữ liệu chấm công:", err); setAttendances({});},
          }, null, {
            params: {
              userIds: userIds.join(","),
              month: dayjs(month).format("YYYY-MM"),
            }
          });
        } catch (attErr) {
          console.error("Ngoại lệ khi tải dữ liệu chấm công:", attErr);
          setAttendances({});
        } finally {
          setLoadingAttendances(false);
        }
      } else {
        setAttendances({});
        setLoadingAttendances(false);
      }
    } else {
      setAttendances({});
    }
    setLoading(false);
  }, [itemsPerPage, debouncedSearchName, selectedDept, selectedPos, selectedMonth, fetchHolidays]);

  useEffect(() => {
    setCurrentPage(0);
    fetchEmployeesAndAttendances(0, itemsPerPage, debouncedSearchName, selectedDept, selectedPos, selectedMonth);
  }, [debouncedSearchName, selectedDept, selectedPos, selectedMonth, itemsPerPage, fetchEmployeesAndAttendances]);


  const getAttendanceCellStyle = useCallback((attendanceType, isHoliday, holidayDataForDay, isWeekend, isFuture, record) => {
    let backgroundColor = theme.palette.background.paper;
    let color = theme.palette.text.primary;
    let cellContent = "-"; // Đổi tên biến để tránh nhầm lẫn với prop `content` của sx

    if (isHoliday && holidayDataForDay) {
      backgroundColor = isWeekend ? theme.palette.grey[100] : "#e8f5e9"; // Màu xanh lá nhạt cho ngày lễ
      let holidayTitleText = "Ngày lễ"; // Tiêu đề mặc định cho tooltip
      if (typeof holidayDataForDay === 'string') {
        holidayTitleText = holidayDataForDay;
      } else if (typeof holidayDataForDay === 'object' && holidayDataForDay !== null && holidayDataForDay.name) {
        holidayTitleText = holidayDataForDay.name; // Giả sử object ngày lễ có trường 'name'
      }
      cellContent = <Tooltip title={holidayTitleText}><Typography variant="caption" sx={{ fontWeight: 'bold', color: "#1b5e20" }}>Lễ</Typography></Tooltip>;
    } else if (isWeekend) {
      backgroundColor = theme.palette.grey[100];
      cellContent = <Typography variant="caption" sx={{color: theme.palette.text.disabled}}>-</Typography>;
    }

    if (record) { // Ưu tiên hiển thị record chấm công nếu có, trừ khi là ngày lễ (holiday style đã được set ở trên)
      if (!isHoliday) { // Chỉ thay đổi màu nền nếu không phải ngày lễ (để giữ màu của ngày lễ)
        switch (record.attendanceType) {
          case "PRESENT": backgroundColor = "#e0f7fa"; break;
          case "ABSENT": backgroundColor = "#ffebee"; break;
          case "MISSING": backgroundColor = "#fff3e0"; break;
          case "INCOMPLETE": backgroundColor = "#fffde7"; break;
          case "LATE": backgroundColor = "#fff0eb"; break;
          default: if (!isWeekend) backgroundColor = theme.palette.grey[50]; // Chỉ nếu không phải cuối tuần
        }
      }
      // Cập nhật cellContent dựa trên record
      if (record.startTime && record.endTime) {
        cellContent = (<>
          <Typography variant="caption" sx={{ color: "#005e05", fontWeight: 500, display: 'block' }}>{formatTime(record.startTime)}</Typography>
          <Typography variant="caption" sx={{ color: "#005e05", fontWeight: 500, display: 'block' }}>{formatTime(record.endTime)}</Typography>
        </>);
      } else if (record.startTime) {
        cellContent = (<Typography variant="caption" sx={{ color: "#c62828", fontWeight: 500, display: 'block' }}>{formatTime(record.startTime)}</Typography>);
        if (!isHoliday && !isWeekend) backgroundColor = "#fffde7"; // Thiếu checkout
      } else if (record.endTime) { // Trường hợp này ít xảy ra (chỉ có checkout)
        cellContent = (<Typography variant="caption" sx={{ color: "#c62828", fontWeight: 500, display: 'block' }}>{formatTime(record.endTime)}</Typography>);
        if (!isHoliday && !isWeekend) backgroundColor = "#fffde7"; // Thiếu checkin
      }
    } else if (!isHoliday && !isWeekend && !isFuture) { // Không có record, không phải lễ, không cuối tuần, không tương lai => Vắng
      cellContent = <Typography variant="h6" sx={{color: theme.palette.error.light}}>×</Typography>;
      backgroundColor = "#ffebee";
    } else if (isFuture && !isHoliday && !isWeekend) { // Ngày tương lai (không lễ, không cuối tuần)
      cellContent = <Typography variant="caption" sx={{color: theme.palette.text.disabled}}>-</Typography>;
      if (backgroundColor === theme.palette.background.paper) backgroundColor = theme.palette.grey[50]; // Chỉ set nếu chưa phải cuối tuần/lễ
    }
    // Nếu cellContent vẫn là "-", và không phải là ngày tương lai, cuối tuần, hay lễ, đó là ngày trong quá khứ không có record => đã xử lý ở trên.

    return { sx: { backgroundColor, textAlign: "center", padding: 0.5, fontSize: "0.75rem", color, minWidth: 70, height: 60, borderBottom: `1px solid ${theme.palette.divider}`, '&:not(:last-child)': { borderRight: `1px solid ${theme.palette.divider}`}}, content: cellContent };
  }, [theme]); // formatTime không cần là dependency nếu nó là pure function hoặc memoized

  const getDaysInMonth = useCallback((date) => {
    const year = dayjs(date).year();
    const month = dayjs(date).month();
    const days = [];
    let currentDate = dayjs(new Date(year, month, 1));

    while (currentDate.month() === month) {
      const dateStr = currentDate.format("YYYY-MM-DD");
      days.push({
        dateStr,
        day: currentDate.date(),
        weekday: currentDate.format("ddd"),
        isWeekend: currentDate.day() === 0 || currentDate.day() === 6,
        isFuture: dateStr > todayStr,
      });
      currentDate = currentDate.add(1, 'day');
    }
    return days;
  }, [todayStr]);

  const daysInMonth = useMemo(() => getDaysInMonth(selectedMonth), [selectedMonth, getDaysInMonth]);

  const formatTime = (timeStr) => { // formatTime có thể để ngoài component nếu không dùng hook nào
    if (!timeStr) return "";
    return dayjs(timeStr).format("HH:mm");
  };

  const handlePageChangeForPagination = (newPage) => {
    fetchEmployeesAndAttendances(newPage, itemsPerPage, debouncedSearchName, selectedDept, selectedPos, selectedMonth);
  };

  const handleItemsPerPageChangeForPagination = (newSize) => {
    setItemsPerPage(newSize);
  };

  const handleSearchButtonClick = () => {
    setCurrentPage(0);
    fetchEmployeesAndAttendances(0, itemsPerPage, searchTerm, selectedDept, selectedPos, selectedMonth, true);
  };

  return (
    <Box sx={{ mr: 2 }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Typography variant="h5" gutterBottom component="h1">Bảng Chấm Công Tháng</Typography>
        <Grid container spacing={2} alignItems="flex-end" wrap="wrap" marginBottom={2}>
          <Grid item xs={12} sm={6} md>
            <TextField label="Tìm theo tên nhân viên" fullWidth value={searchName} onChange={(e) => setSearchName(e.target.value)} />
          </Grid>
          <Grid item xs={12} sm={6} md>
            <SearchSelect label="Phòng ban" fetchUrl="/departments?status=ACTIVE" value={selectedDept} onChange={setSelectedDept} getOptionLabel={(item) => item.department_name} mapFunction={(data) => data.map((d) => ({ ...d, name: d.department_name }))} />
          </Grid>
          <Grid item xs={12} sm={6} md>
            <SearchSelect label="Chức vụ" fetchUrl="/jobs?status=ACTIVE" value={selectedPos} onChange={setSelectedPos} getOptionLabel={(item) => item.name} />
          </Grid>
          <Grid item xs={12} sm={6} md="auto">
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="vi">
              <DatePicker views={["month", "year"]} label="Chọn tháng năm" value={selectedMonth} onChange={(val) => setSelectedMonth(val || dayjs())} renderInput={(params) => <TextField {...params} fullWidth />} />
            </LocalizationProvider>
          </Grid>
          {/*<Grid item xs={12} md="auto">
            <Button
              variant="contained"
              onClick={handleSearchButtonClick}
              sx={{ height: '100%' }}
            >
              Tìm kiếm
            </Button>
          </Grid>*/}
        </Grid>
      </Paper>

      <TableContainer component={Paper} sx={{ maxHeight: "calc(100vh - 270px)" }} className="custom-scrollbar">
        <Table stickyHeader sx={{ borderCollapse: "separate", borderSpacing: 0}}>
          <TableHead>
            <TableRow>
              <TableCell sx={{
                position: "sticky", top: 0, left: 0, zIndex: theme.zIndex.appBar + 2,
                minWidth: 230,
                fontWeight: 'bold',
                backgroundColor: theme.palette.background.paper,
                borderBottom: `1px solid ${theme.palette.divider}`,
                borderRight: `1px solid ${theme.palette.divider}`,
                py: 0.5,
              }}>
                Nhân viên
              </TableCell>
              {daysInMonth.map((d) => (
                <TableCell key={d.dateStr} sx={{
                  position: "sticky", top: 0, zIndex: theme.palette.mode === 'dark' ? theme.zIndex.appBar : theme.zIndex.appBar + 1,
                  textAlign: "center",
                  bgcolor: d.isWeekend ? theme.palette.grey[200] : theme.palette.background.paper,
                  color: d.isWeekend ? theme.palette.text.secondary : theme.palette.text.primary,
                  fontSize: "0.75rem", lineHeight: 1.3, fontWeight: 'bold',
                  minWidth: 65, p:0.5,
                  borderBottom: `1px solid ${theme.palette.divider}`,
                  '&:not(:last-child)': { borderRight: `1px solid ${theme.palette.divider}` }
                }}>
                  <Box>{d.day}</Box>
                  <Box sx={{fontSize: '0.65rem'}}>{d.weekday}</Box>
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {loading && employees.length === 0 ? (
              <TableRow>
                <TableCell colSpan={daysInMonth.length + 1} align="center" sx={{ py: 5 }}>
                  <CircularProgress />
                  <Typography sx={{mt: 1.5}}>Đang tải dữ liệu nhân viên...</Typography>
                </TableCell>
              </TableRow>
            ) : !loading && employees.length === 0 ? (
              <TableRow>
                <TableCell colSpan={daysInMonth.length + 1} align="center" sx={{ py: 5 }}>
                  <Typography variant="h6">Không tìm thấy nhân viên.</Typography>
                </TableCell>
              </TableRow>
            ) : (
              employees.map((emp) => (
                <TableRow key={emp.id} hover>
                  <TableCell
                    onClick={() => navigate(`/hr/staff/${emp.staff_code}`)}
                    sx={{
                      position: "sticky", left: 0, zIndex: theme.zIndex.appBar -1,
                      bgcolor: theme.palette.background.paper,
                      cursor: "pointer", display: "flex", alignItems: "center", gap: 1,
                      py: 0.5, px: 1, whiteSpace: "nowrap", height: 60,
                      borderBottom: `1px solid ${theme.palette.divider}`,
                      borderRight: `1px solid ${theme.palette.divider}`
                    }}
                  >
                    <Avatar src={`https://ui-avatars.com/api/?name=${encodeURIComponent(emp.fullname || 'N V')}&background=random&size=100&font-size=0.5`} alt={emp.fullname} sx={{ width: 32, height: 32 }} />
                    <Typography variant="body2" sx={{ color: "primary.main", fontWeight: 500 }}>{emp.fullname}</Typography>
                  </TableCell>
                  {daysInMonth.map((d) => {
                    const record = attendances[emp.user_login_id]?.[d.dateStr];
                    const holidayDataForDay = holidays[d.dateStr]; // Đây có thể là string hoặc object
                    const cellStyleResult = getAttendanceCellStyle(record?.attendanceType, !!holidayDataForDay, holidayDataForDay, d.isWeekend, d.isFuture, record);
                    return (
                      <TableCell key={d.dateStr} sx={cellStyleResult.sx}>
                        {loadingAttendances && !record && !holidayDataForDay && !d.isWeekend && !d.isFuture ? <CircularProgress size={16}/> : cellStyleResult.content}
                      </TableCell>
                    );
                  })}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
      <Box>
        <Pagination
          currentPage={currentPage}
          pageCount={pageCount}
          itemsPerPage={itemsPerPage}
          onPageChange={handlePageChangeForPagination}
          onItemsPerPageChange={handleItemsPerPageChangeForPagination}
        />
      </Box>
    </Box>
  );
};

const AttendancePage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AttendancePageInternal />
    </ThemeProvider>
  );
};

export default AttendancePage;
