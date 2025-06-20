import React, {useEffect, useMemo, useState, useCallback} from "react";
import {
  ThemeProvider,
  CssBaseline,
  Box,
  Button,
  Chip,
  Grid,
  Paper,
  TextField,
  Typography,
  CircularProgress,
  TableContainer,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Tooltip,
  Card,
  CardHeader,
  CardContent,
  Avatar,
  Stack,
  IconButton,
  ToggleButtonGroup,
  ToggleButton,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Divider,
} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { vi } from 'date-fns/locale';
import { addDays, format, isMonday, startOfWeek, parseISO, isEqual, setHours, setMinutes, setSeconds, setMilliseconds } from "date-fns";

import { theme } from './theme';
import SearchSelect from "@/components/item/SearchSelect";
import {request}from "@/api";
import { useDebounce } from "../../hooks/useDebounce";
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import CalendarViewDayIcon from '@mui/icons-material/CalendarViewDay';
import PeopleOutlineIcon from '@mui/icons-material/PeopleOutline';
import toast from "react-hot-toast";

const parseTimeStringToDateObject = (timeString, baseDate = new Date(2000, 0, 1)) => {
  if (!timeString || typeof timeString !== 'string') return null;
  const [hours, minutes] = timeString.split(':').map(Number);
  if (isNaN(hours) || isNaN(minutes)) return null;
  return setMilliseconds(setSeconds(setMinutes(setHours(baseDate, hours), minutes),0),0);
};


const WeeklyAbsencePageInternal = () => {
  const [selectedWeekStart, setSelectedWeekStart] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }));
  const [employees, setEmployees] = useState([]);
  const [absenceData, setAbsenceData] = useState([]);
  const [selectedDept, setSelectedDept] = useState(null);
  const [selectedPos, setSelectedPos] = useState(null);
  const [loading, setLoading] = useState(false);
  const [searchName, setSearchName] = useState("");
  const debouncedSearchName = useDebounce(searchName, 500);
  const [viewMode, setViewMode] = useState("byEmployeeTable");

  const [workTimes, setWorkTimes] = useState({
    startWork: null,
    endWork: null,
    lunchStart: null,
    lunchEnd: null,
  });

  useEffect(() => {
    request("get", "/configs?configGroup=COMPANY_CONFIGS", (res) => {
      const apiConfigs = res.data?.data || {};
      const newConfigs = {};
      Object.entries(apiConfigs).forEach(([k, v]) => (newConfigs[k] = v.config_value));
      const baseDateForTimeParsing = new Date(2000, 0, 1);
      setWorkTimes({
        startWork: parseTimeStringToDateObject(newConfigs.START_WORK_TIME, baseDateForTimeParsing),
        endWork: parseTimeStringToDateObject(newConfigs.END_WORK_TIME, baseDateForTimeParsing),
        lunchStart: parseTimeStringToDateObject(newConfigs.START_LUNCH_TIME, baseDateForTimeParsing),
        lunchEnd: parseTimeStringToDateObject(newConfigs.END_LUNCH_TIME, baseDateForTimeParsing),
      });
    }, { onError: (err) => console.error("Lỗi tải cấu hình công ty:", err) });
  }, []);


  const weekDays = useMemo(() => {
    const start = selectedWeekStart;
    return Array.from({ length: 7 }).map((_, i) => {
      const date = addDays(start, i);
      return {
        date,
        dateStr: format(date, "yyyy-MM-dd"),
        label: `${format(date, "EEEE", {locale: vi})} - ${format(date, "dd/MM")}`,
        shortLabel: `${format(date, "EEE", {locale: vi})} ${format(date, "dd/MM")}`,
        isWeekend: date.getDay() === 0 || date.getDay() === 6,
      };
    });
  }, [selectedWeekStart]);

  const getAbsenceDisplayType = useCallback((absenceStartTimeStr, absenceEndTimeStr) => {
    if (!workTimes.startWork || !workTimes.endWork || !absenceStartTimeStr || !absenceEndTimeStr) {
      return `${absenceStartTimeStr?.slice(0,5) || '?'} - ${absenceEndTimeStr?.slice(0,5) || '?'}`;
    }
    const baseDate = new Date(2000, 0, 1);
    const absenceStart = parseTimeStringToDateObject(absenceStartTimeStr, baseDate);
    const absenceEnd = parseTimeStringToDateObject(absenceEndTimeStr, baseDate);

    if (!absenceStart || !absenceEnd) return `${absenceStartTimeStr?.slice(0,5) || '?'} - ${absenceEndTimeStr?.slice(0,5) || '?'}`;

    const isFullDay = isEqual(absenceStart, workTimes.startWork) && isEqual(absenceEnd, workTimes.endWork);
    if (isFullDay) return "Cả ngày";

    if (workTimes.lunchStart && workTimes.lunchEnd) {
      const isMorning = isEqual(absenceStart, workTimes.startWork) && isEqual(absenceEnd, workTimes.lunchStart);
      if (isMorning) return "Buổi sáng";
      const isAfternoon = isEqual(absenceStart, workTimes.lunchEnd) && isEqual(absenceEnd, workTimes.endWork);
      if (isAfternoon) return "Buổi chiều";
    }
    return `${format(absenceStart, "HH:mm")} - ${format(absenceEnd, "HH:mm")}`;
  }, [workTimes]);


  const fetchAndProcessData = useCallback(async () => {
    if (!selectedWeekStart) return;
    setLoading(true);
    let fetchedEmployees = [];
    const empPayload = {
      fullname: debouncedSearchName || null,
      departmentCode: selectedDept?.department_code || null,
      jobPositionCode: selectedPos?.code || null,
      status: "ACTIVE"
    };
    try {
      const empRes = await new Promise((resolve, reject) => {
        request("get", "/staffs/details", (res) => resolve(res), { onError: (err) => reject(err) }, null, { params: empPayload });
      });
      fetchedEmployees = (empRes.data?.data || []).map(e => ({
        ...e,
        id: String(e.staff_code || e.user_login_id || Date.now()),
        avatar_url: `https://ui-avatars.com/api/?name=${encodeURIComponent(e.fullname || 'N V')}&background=random&size=64&font-size=0.5&bold=true`
      }));
      setEmployees(fetchedEmployees);
    } catch (error) {
      console.error("Lỗi tải danh sách nhân viên:", error);
      toast.error("Không thể tải danh sách nhân viên.");
      setLoading(false); return;
    }

    if (fetchedEmployees.length === 0) {
      setAbsenceData([]); setLoading(false); return;
    }

    const userIds = fetchedEmployees.map((emp) => emp.user_login_id).filter(Boolean);
    const startDateStr = weekDays[0].dateStr;
    const endDateStr = weekDays[weekDays.length - 1].dateStr;

    if (userIds.length === 0) {
      setAbsenceData([]); setLoading(false); return;
    }

    try {
      const absenceParams = new URLSearchParams();
      userIds.forEach(id => absenceParams.append("userIds", id));
      absenceParams.append("startDate", startDateStr);
      absenceParams.append("endDate", endDateStr);
      absenceParams.append("status", "ACTIVE");

      await request("get", `/absences?${absenceParams.toString()}`, (res) => {
        const absencesFromApi = res.data?.data || [];
        const processedAbsences = absencesFromApi.map(abs => {
          const emp = fetchedEmployees.find(e => e.user_login_id === abs.user_id);
          return {
            ...abs,
            id: String(abs.id || `${abs.user_id}_${abs.date}_${abs.start_time}`),
            staff_name: emp?.fullname || `(ID: ${abs.user_id})`,
            staff_avatar_url: emp?.avatar_url, // Sử dụng avatar_url đã tạo
            displayTime: getAbsenceDisplayType(abs.start_time, abs.end_time)
          };
        });
        setAbsenceData(processedAbsences);
      }, { onError: (err) => { console.error("Lỗi tải dữ liệu nghỉ phép:", err); toast.error("Không thể tải dữ liệu nghỉ phép."); setAbsenceData([]); }});
    } catch (error) {
      console.error("Ngoại lệ khi tải dữ liệu nghỉ phép:", error);
      toast.error("Lỗi hệ thống khi tải dữ liệu nghỉ phép.");
      setAbsenceData([]);
    } finally {
      setLoading(false);
    }
  }, [selectedWeekStart, selectedDept, selectedPos, debouncedSearchName, weekDays, getAbsenceDisplayType]);

  useEffect(() => {
    if (workTimes.startWork) {
      fetchAndProcessData();
    }
  }, [fetchAndProcessData, workTimes.startWork]);

  const handleSearch = () => {
    fetchAndProcessData();
  }

  const absenceByDayAndStaffForTable = useMemo(() => {
    const result = {};
    employees.forEach(emp => {
      result[emp.user_login_id] = {};
      weekDays.forEach(day => {
        result[emp.user_login_id][day.dateStr] = absenceData.filter(a => a.user_id === emp.user_login_id && a.date === day.dateStr);
      });
    });
    return result;
  }, [absenceData, employees, weekDays]);

  const absencesGroupedByDay = useMemo(() => {
    const grouped = {};
    weekDays.forEach(day => {
      grouped[day.dateStr] = absenceData.filter(abs => abs.date === day.dateStr);
    });
    return grouped;
  }, [absenceData, weekDays]);


  const renderAbsenceInfo = (absence, forCardView = false) => (
    <Box sx={{width: '100%'}}>
      <Typography variant="body2" sx={{fontWeight: 'bold', display:'block', fontSize: forCardView ? '0.8rem' : '0.75rem', color: theme.palette.text.primary}}>
        {absence.displayTime}
      </Typography>
      <Chip
        label={absence.type === "PAID_LEAVE" ? "Có lương" : "Không lương"}
        size="small"
        color={absence.type === "PAID_LEAVE" ? "success" : "warning"}
        sx={{ fontSize: '0.7rem', height: '20px', my: 0.25, '& .MuiChip-label': { px: '8px'} }}
        variant="outlined"
      />
      <Tooltip title={absence.reason || "Không có lý do"}>
        <Typography variant="caption" display="block" sx={{
          maxWidth: forCardView ? 'none' : '110px',
          textOverflow: 'ellipsis',
          overflow: 'hidden',
          fontStyle:'italic',
          whiteSpace: forCardView ? 'normal' : 'nowrap',
          lineHeight: forCardView ? 1.3 : 'inherit',
          WebkitLineClamp: forCardView ? 2 : 1, // Hiển thị tối đa 2 dòng trong card view
          display: forCardView ? '-webkit-box' : 'block',
          WebkitBoxOrient: forCardView ? 'vertical' : 'inherit',
          color: theme.palette.text.secondary
        }}>
          {absence.reason || "-"}
        </Typography>
      </Tooltip>
    </Box>
  );


  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <Box sx={{ mr: 2 }}>
        <Paper sx={{ p: { xs: 2, md: 3 }, mb: 2 }}> {/* Giảm mb một chút */}
          <Grid container justifyContent="space-between" alignItems="center" sx={{mb: 2.5}}> {/* Tăng mb một chút */}
            <Grid item>
              <Typography variant="h5" sx={{ fontWeight: 600, display: 'flex', alignItems: 'center' }}>
                <EventAvailableIcon sx={{mr:1, color: 'primary.main'}} /> Tổng hợp Nghỉ phép Theo Tuần
              </Typography>
            </Grid>
            <Grid item>
              <Stack direction="row" spacing={0.5}>
                <Tooltip title="Xem theo Nhân Viên (Bảng)">
                  <IconButton onClick={() => setViewMode("byEmployeeTable")} color={viewMode === 'byEmployeeTable' ? 'primary' : 'default'} size="medium">
                    <PeopleOutlineIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Xem theo Ngày (Group)">
                  <IconButton onClick={() => setViewMode("byDayGrouped")} color={viewMode === 'byDayGrouped' ? 'primary' : 'default'} size="medium">
                    <CalendarViewDayIcon />
                  </IconButton>
                </Tooltip>
              </Stack>
            </Grid>
          </Grid>
          <Grid container spacing={1.5} alignItems="center"> {/* Giảm spacing */}
            <Grid item xs={12} sm={6} md>
              <TextField fullWidth label="Tìm theo tên nhân viên" value={searchName} onChange={(e) => setSearchName(e.target.value)} variant="outlined" size="small"/>
            </Grid>
            <Grid item xs={12} sm={6} md>
              <SearchSelect label="Phòng ban" fetchUrl="/departments?status=ACTIVE" value={selectedDept} onChange={setSelectedDept} getOptionLabel={(item) => item.department_name} mapFunction={(data) => data.map((d) => ({ ...d, name: d.department_name }))} isClearable size="small"/>
            </Grid>
            <Grid item xs={12} sm={6} md>
              <SearchSelect label="Chức vụ" fetchUrl="/jobs?status=ACTIVE" value={selectedPos} onChange={setSelectedPos} getOptionLabel={(item) => item.name} isClearable size="small"/>
            </Grid>
            <Grid item xs={12} sm={6} md>
              <DatePicker
                label="Chọn Tuần (Ngày T2)"
                value={selectedWeekStart}
                onChange={(date) => date && setSelectedWeekStart(startOfWeek(date, { weekStartsOn: 1 }))}
                shouldDisableDate={(date) => !isMonday(date)}
                slotProps={{ textField: { size: 'small', fullWidth: true } }}
              />
            </Grid>
            <Grid item xs={12} sm="auto" md="auto">
              <Button variant="contained" onClick={handleSearch} disabled={loading} fullWidth sx={{height: '40px'}}> Tìm kiếm </Button> {/* Set height bằng TextField size small */}
            </Grid>
          </Grid>
        </Paper>

        {loading && (
          <Box sx={{display: 'flex', justifyContent: 'center', py: 5}}><CircularProgress /><Typography sx={{ml:2}}>Đang tải dữ liệu...</Typography></Box>
        )}

        {/* Chế độ xem THEO NHÂN VIÊN (BẢNG) */}
        {!loading && viewMode === 'byEmployeeTable' && (
          employees.length === 0 ? (
            <Paper sx={{p:3, textAlign: 'center', mt:2}}><Typography variant="subtitle1">Không có nhân viên nào phù hợp.</Typography></Paper>
          ) : (
            <Paper sx={{ overflow: 'hidden' }}>
              <TableContainer sx={{ maxHeight: "calc(100vh - 230px)" }} className="custom-scrollbar">
                <Table stickyHeader size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ minWidth: 220, width: 280, fontWeight: 'bold', bgcolor: (t) => t.palette.mode === 'light' ? t.palette.grey[200] : t.palette.grey[700], borderBottom: (t) => `1px solid ${t.palette.divider}`, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.divider}` }, position:'sticky', left:0, zIndex: theme.zIndex.appBar +1 }}>Nhân viên</TableCell>
                      {weekDays.map(day => (
                        <TableCell key={day.dateStr} align="center" sx={{ width:130, minWidth: 130, fontWeight: 'bold', bgcolor: day.isWeekend ? theme.palette.grey[300] : (t) => t.palette.mode === 'light' ? t.palette.grey[200] : t.palette.grey[700], borderBottom: (t) => `1px solid ${t.palette.divider}`, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.divider}` } }}>
                          {day.shortLabel}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {employees.map(emp => (
                      <TableRow key={emp.id} hover>
                        <TableCell sx={{ fontWeight: 500, borderBottom: (t) => `1px solid ${t.palette.divider}`, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[300]}` }, position:'sticky', left:0, bgcolor: theme.palette.background.paper, zIndex: theme.zIndex.appBar, p:0.5 }}>
                          <Stack direction="row" alignItems="center" spacing={1} sx={{p: '2px'}}>
                            <Avatar alt={emp.fullname} src={emp.avatar_url} sx={{width: 32, height: 32}}/>
                            <Typography variant="body2">{emp.fullname}</Typography>
                          </Stack>
                        </TableCell>
                        {weekDays.map(day => {
                          const empAbsencesOnThisDay = absenceByDayAndStaffForTable[emp.user_login_id]?.[day.dateStr] || [];
                          return (
                            <TableCell key={`${emp.id}-${day.dateStr}`} align="left" sx={{ verticalAlign: 'top', p:0.5, borderBottom: (t) => `1px solid ${t.palette.divider}`, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[300]}` }, bgcolor: day.isWeekend ? theme.palette.grey[50] : 'inherit', minHeight: 60 }}>
                              {empAbsencesOnThisDay.length > 0 ? (
                                empAbsencesOnThisDay.map(abs => (
                                  <Box key={abs.id} sx={{ p: 0.5, mb: 0.5, bgcolor: abs.type === "PAID_LEAVE" ? (theme.palette.success.ultralight || '#e6f7f0') : (theme.palette.warning.ultralight || '#fff8e1'), borderRadius: 1, textAlign: 'left' }}>
                                    {renderAbsenceInfo(abs)}
                                  </Box>
                                ))
                              ) : (
                                <Typography variant="caption" color={day.isWeekend ? "textSecondary" : "textDisabled"} sx={{display:'block', textAlign:'center'}}>-</Typography>
                              )}
                            </TableCell>
                          );
                        })}
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          )
        )}

        {/* Chế độ xem THEO NGÀY (GROUP) */}
        {!loading && viewMode === 'byDayGrouped' && (
          Object.values(absencesGroupedByDay).every(arr => arr.length === 0) && !loading ? (
            <Paper sx={{p:3, textAlign: 'center', mt:2}}><Typography variant="h6" color="text.secondary">Không có ai nghỉ trong tuần này theo bộ lọc đã chọn.</Typography></Paper>
          ) : (
            <Box sx={{ maxHeight: "calc(100vh - 230px)", overflowY: 'auto', pr: 1}} className="custom-scrollbar">
              <Grid container spacing={2}>
                {weekDays.map(day => {
                  const absencesThisDay = absencesGroupedByDay[day.dateStr] || [];
                  return (
                    <Grid item xs={12} md={6} lg={4} xl={3} key={day.dateStr}>
                      <Paper sx={{p:1.5, height: '100%', display: 'flex', flexDirection: 'column', borderLeft: day.isWeekend ? `5px solid ${theme.palette.grey[400]}` : `5px solid ${theme.palette.primary.main}`}}>
                        <Typography variant="subtitle1" sx={{fontWeight: 600, mb:1, borderBottom: `1px solid ${theme.palette.divider}`, pb:0.5}}>{day.label}</Typography>
                        {absencesThisDay.length > 0 ? (
                          <List dense disablePadding sx={{flexGrow:1, overflowY:'auto', maxHeight: 350 }}>
                            {absencesThisDay.map(abs => (
                              <ListItem key={abs.id} disableGutters sx={{display:'flex', alignItems:'flex-start', mb:0.5, p:1, borderRadius:1, bgcolor: abs.type === "PAID_LEAVE" ? (theme.palette.success.ultralight || '#e6f7f0') : (theme.palette.warning.ultralight || '#fff8e1')}}>
                                <ListItemAvatar sx={{minWidth: 40, mt:0.5}}>
                                  <Avatar src={abs.staff_avatar_url} alt={abs.staff_name} sx={{width: 32, height: 32}}/>
                                </ListItemAvatar>
                                <ListItemText
                                  primary={<Typography variant="body2" sx={{fontWeight:500}}>{abs.staff_name}</Typography>}
                                  secondary={renderAbsenceInfo(abs, true)}
                                  sx={{m:0}}
                                />
                              </ListItem>
                            ))}
                          </List>
                        ) : (
                          <Typography variant="caption" color="textSecondary" sx={{display:'block', textAlign:'center', fontStyle:'italic', mt:2}}>Không có ai nghỉ.</Typography>
                        )}
                      </Paper>
                    </Grid>
                  )
                })}
              </Grid>
            </Box>
          )
        )}
      </Box>
    </LocalizationProvider>
  );
};

const WeeklyAbsencePage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <WeeklyAbsencePageInternal />
    </ThemeProvider>
  );
};

export default WeeklyAbsencePage;
