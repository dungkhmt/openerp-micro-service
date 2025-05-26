// HolidayScreen.jsx
import React, {useEffect, useMemo, useState, useCallback} from "react";
import {
  Box,
  Button,
  Grid,
  Paper,
  Typography,
  ThemeProvider,
  CssBaseline,
  IconButton,
  Tooltip,
  CircularProgress
} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import "dayjs/locale/vi";
import {request}from "@/api";
import AddHolidayModal from "./modals/AddHolidayModal";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import toast from "react-hot-toast";
import lunar from "lunar-calendar";
import {theme} from "./theme";

dayjs.locale('vi');

const WEEKDAYS = [
  "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật",
];

const holidayCellBgColor = "#fff0f0";
const todayCellBorderColor = theme.palette.primary.main;

const generateCalendarWeeks = (month) => {
  const startOfMonth = month.startOf("month");
  const endOfMonth = month.endOf("month");
  const firstDayOfMonthWeekday = (startOfMonth.day() === 0 ? 6 : startOfMonth.day() - 1);
  const days = [];

  for (let i = 0; i < firstDayOfMonthWeekday; i++) {
    days.push(startOfMonth.subtract(firstDayOfMonthWeekday - i, "day"));
  }
  for (let i = 0; i < endOfMonth.date(); i++) {
    days.push(startOfMonth.add(i, "day"));
  }
  const totalDaysInGrid = days.length <= 35 ? 35 : 42;
  const daysFromNextMonth = totalDaysInGrid - days.length;

  for (let i = 1; i <= daysFromNextMonth; i++) {
    days.push(endOfMonth.add(i, "day"));
  }
  const weeks = [];
  for (let i = 0; i < days.length; i += 7) {
    weeks.push(days.slice(i, i + 7));
  }
  return weeks;
};

const convertToLunarMock = (date) => {
  try {
    if (!date || !date.isValid()) return "";
    const lunarDate = lunar.solarToLunar(date.year(), date.month() + 1, date.date());
    return `${lunarDate.lunarDay}/${lunarDate.lunarMonth}`;
  } catch (e) {
    return "";
  }
};

const HolidayScreenInternal = () => {
  const defaultCellBgColor = theme.palette.background.paper;
  const [selectedMonth, setSelectedMonth] = useState(dayjs());
  const [holidays, setHolidays] = useState({});
  const [openModal, setOpenModal] = useState(false);
  const [editingHoliday, setEditingHoliday] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [loadingHolidays, setLoadingHolidays] = useState(false);

  const fetchHolidays = useCallback(async (monthToFetch) => {
    setLoadingHolidays(true);
    const yearMonthStr = monthToFetch.format("YYYY-MM");
    try {
      await request(
        "get",
        `/holidays?month=${yearMonthStr}`,
        (res) => {
          const apiData = res.data.data || res.data.holidays;
          if (typeof apiData === 'object' && apiData !== null && !Array.isArray(apiData)) {
            setHolidays(apiData);
          } else if (Array.isArray(apiData)) {
            const holidaysMap = {};
            apiData.forEach(holiday => {
              holidaysMap[dayjs(holiday.date).format("YYYY-MM-DD")] = holiday;
            });
            setHolidays(holidaysMap);
          } else {
            console.warn("Received unexpected holiday data format:", apiData);
            setHolidays({});
          }
        },
        {
          onError: (err) => {
            console.error("Failed to load holidays for month " + yearMonthStr, err);
            toast.error(`Không thể tải danh sách ngày nghỉ cho tháng ${monthToFetch.format("MM/YYYY")}.`);
            setHolidays({});
          },
        }
      );
    } catch (error) {
      console.error("Exception while fetching holidays", error);
      toast.error("Lỗi hệ thống khi tải ngày nghỉ.");
      setHolidays({});
    } finally {
      setLoadingHolidays(false);
    }
  }, []);

  useEffect(() => {
    fetchHolidays(selectedMonth);
  }, [selectedMonth, fetchHolidays]);

  const calendarWeeks = useMemo(
    () => generateCalendarWeeks(selectedMonth),
    [selectedMonth]
  );

  const currentMonthValue = selectedMonth.month();
  const todayStr = dayjs().format("YYYY-MM-DD");


  const handleDelete = () => {
    if (!deleteTarget) return;
    request(
      "delete",
      `/holidays/${deleteTarget.id}`,
      () => {
        toast.success("Xoá ngày nghỉ thành công!");
        setDeleteTarget(null);
        setConfirmOpen(false);
        fetchHolidays(selectedMonth);
      },
      {
        onError: (err) => {
          console.error("Xóa thất bại", err);
          toast.error(err.response?.data?.message || "Xóa ngày nghỉ thất bại.");
        },
      }
    );
  };

  const calendarMaxHeight = `calc(100vh - 240px)`;

  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} justifyContent="space-between" alignItems="center" wrap="wrap">
          <Grid item xs={12} md="auto">
            <Typography variant="h4" component="h1">Lịch nghỉ lễ</Typography>
          </Grid>
          <Grid item xs={12} md="auto" sx={{display: 'flex', flexDirection: {xs: 'column', md:'row'}, gap: 2, alignItems: 'center', width: {xs: '100%', md: 'auto'}}}>
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="vi">
              <DatePicker
                views={["month", "year"]}
                label="Chọn tháng & năm"
                value={selectedMonth}
                onChange={(newVal) => newVal && newVal.isValid() && setSelectedMonth(newVal)}
                slotProps={{ textField: { size: 'small', fullWidth: {xs:true, md:false} } }}
                sx={{ width: {xs: '100%', md: 200} }}
              />
            </LocalizationProvider>
            <Button
              variant="contained"
              onClick={() => {setEditingHoliday(null); setOpenModal(true);}}
              startIcon={<AddIcon />}
              sx={{ width: {xs: '100%', md: 'auto'} }}
            >
              Thêm ngày nghỉ
            </Button>
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{p:1, overflow: 'hidden'}}>
        <Grid container spacing={{xs: 0.25, sm:0.5}} sx={{mb: {xs: 0.25, sm:0.5}}}>
          {WEEKDAYS.map((day, idx) => (
            <Grid item xs key={idx} sx={{ flexBasis: 'calc(100%/7)', flexGrow: 1, maxWidth: 'calc(100%/7)'}}>
              <Box
                textAlign="center"
                fontWeight="bold"
                bgcolor={theme.palette.primary.dark}
                color={theme.palette.primary.contrastText}
                py={1}
                borderRadius={1}
                fontSize={{xs: '0.7rem', sm: '0.8rem', md: '0.9rem'}}
              >
                {day}
              </Box>
            </Grid>
          ))}
        </Grid>
        <Box sx={{ overflowY: "auto", maxHeight: calendarMaxHeight,
          '&::-webkit-scrollbar': { width: '6px' },
          '&::-webkit-scrollbar-track': { background: theme.palette.grey[200], borderRadius: '3px' },
          '&::-webkit-scrollbar-thumb': { background: theme.palette.grey[400], borderRadius: '3px' },
          '&::-webkit-scrollbar-thumb:hover': { background: theme.palette.grey[500] }
        }}>
          {loadingHolidays ? (
            <Box sx={{display: 'flex', justifyContent:'center', alignItems:'center', height: 150}}>
              <CircularProgress size={30}/>
              <Typography sx={{ml:1.5}} color="text.secondary">Đang tải lịch...</Typography>
            </Box>
          ) : calendarWeeks.map((week, weekIdx) => (
            <Grid container spacing={{xs: 0.25, sm:0.5}} key={weekIdx} sx={{width: '100%', display:'flex', flexWrap:'nowrap'}}>
              {week.map((day, dayIdx) => {
                const dateStr = day.format("YYYY-MM-DD");
                const holiday = holidays[dateStr];
                const isCurrentMonthDay = day.month() === currentMonthValue;
                const isToday = dateStr === todayStr;

                return (
                  <Grid item xs key={`${weekIdx}-${dayIdx}`} sx={{
                    flexBasis: 'calc(100%/7)',
                    flexGrow: 1,
                    maxWidth: 'calc(100%/7)',
                    aspectRatio: '1/0.6',
                    minHeight: {xs: 55, sm: 70, md:80},
                    mb: 0.5
                  }}>
                    <Paper
                      variant="outlined"
                      sx={{
                        position: "relative",
                        height: "100%",
                        borderRadius: 1,
                        p: {xs: 0.5, sm: 0.75},
                        bgcolor: holiday && isCurrentMonthDay ? holidayCellBgColor : defaultCellBgColor,
                        borderColor: isToday ? todayCellBorderColor : (holiday && isCurrentMonthDay ? theme.palette.error.light : theme.palette.divider),
                        borderWidth: isToday ? '2px' : '1px',
                        opacity: isCurrentMonthDay ? 1 : 0.45,
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "flex-start",
                        "&:hover .action-buttons-cell": { opacity: 1, visibility: 'visible' },
                        overflow: 'hidden',
                        boxSizing: 'border-box',
                      }}
                    >
                      <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%', mb: 0.25}}>
                        <Typography
                          fontWeight="bold"
                          fontSize={{xs: '1rem', sm: '1.1rem', md: '1.3rem'}}
                          color={isToday ? theme.palette.primary.main : 'text.primary'}
                        >
                          {day.date()}
                        </Typography>
                        <Typography variant="caption" color="text.secondary" sx={{fontSize: {xs: '0.7rem', sm: '0.8rem'}}}>
                          {isCurrentMonthDay ? convertToLunarMock(day) : ""}
                        </Typography>
                      </Box>

                      <Box sx={{flexGrow: 1, display: 'flex', flexDirection:'column', justifyContent:'center', alignItems:'center', overflow:'hidden', width: '100%'}}>
                        {holiday && isCurrentMonthDay && (
                          <Tooltip title={holiday.name} placement="bottom" arrow>
                            <Typography
                              variant="body2"
                              fontWeight="600"
                              color={theme.palette.error.dark}
                              textAlign="center"
                              sx={{
                                fontSize: {xs: "0.7rem", sm:"0.8rem", md: "0.9rem"},
                                lineHeight: 1.35,
                                maxHeight: {xs: "2.7em", sm: "4.05em"},
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                display: '-webkit-box',
                                WebkitLineClamp: {xs:2, sm:3},
                                WebkitBoxOrient: 'vertical',
                                wordBreak: 'break-word',
                                width: 'calc(100% - 4px)',
                                p: '0 2px'
                              }}
                            >
                              {holiday.name}
                            </Typography>
                          </Tooltip>
                        )}
                      </Box>

                      {isCurrentMonthDay && (
                        <Box
                          className="action-buttons-cell"
                          sx={{
                            display: 'flex',
                            flexDirection: "row",
                            justifyContent: 'flex-end',
                            alignItems: 'center',
                            gap: 0.1,
                            opacity: 0,
                            visibility: 'hidden',
                            transition: 'opacity 0.2s ease-in-out, visibility 0.2s ease-in-out',
                            height: '28px',
                            position: 'absolute',
                            bottom: 2,
                            right: 2,
                          }}
                        >
                          {holiday ? (
                            <>
                              <Tooltip title="Xóa ngày nghỉ">
                                <IconButton size="small" sx={{p:0.15}} onClick={(e) => { e.stopPropagation(); setDeleteTarget(holiday); setConfirmOpen(true); }}>
                                  <DeleteIcon sx={{fontSize: {xs: '1rem', sm: '1.1rem'}}} color="error" />
                                </IconButton>
                              </Tooltip>
                              <Tooltip title="Sửa ngày nghỉ">
                                <IconButton size="small" sx={{p:0.15}} onClick={(e) => { e.stopPropagation(); setEditingHoliday(holiday); setOpenModal(true);}}>
                                  <EditIcon sx={{fontSize: {xs: '1rem', sm: '1.1rem'}}} />
                                </IconButton>
                              </Tooltip>
                            </>
                          ) : (
                            <Tooltip title="Thêm ngày nghỉ">
                              <IconButton
                                size="small"
                                sx={{p:0.15}}
                                onClick={(e) => {
                                  e.stopPropagation();
                                  setEditingHoliday({ date: dateStr, name: '', type: 'PAID_LEAVE' });
                                  setOpenModal(true);
                                }}
                              >
                                <AddIcon sx={{fontSize: {xs: '1rem', sm: '1.1rem'}}} color="primary"/>
                              </IconButton>
                            </Tooltip>
                          )}
                        </Box>
                      )}
                    </Paper>
                  </Grid>
                );
              })}
            </Grid>
          ))}
        </Box>
      </Paper>

      <AddHolidayModal
        open={openModal || editingHoliday !== null}
        onClose={() => {
          setOpenModal(false);
          setEditingHoliday(null);
        }}
        onSubmit={() => {
          fetchHolidays(selectedMonth);
          setOpenModal(false);
          setEditingHoliday(null);
        }}
        initialData={editingHoliday}
        titleProps={{ sx: { fontSize: '1.15rem' } }}
      />

      <DeleteConfirmationModal
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onSubmit={handleDelete}
        title="Xoá ngày nghỉ"
        info={`Bạn có chắc chắn muốn xoá ngày nghỉ "${deleteTarget?.name || ''}"?`}
        titleProps={{ sx: { fontSize: '1.15rem' } }}
      />
    </Box>
  );
};

const HolidayScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <HolidayScreenInternal />
    </ThemeProvider>
  )
}

export default HolidayScreen;
