import React, {useEffect, useState, useCallback, useMemo} from "react";
import {
  ThemeProvider,
  CssBaseline,
  Box,
  Button,
  Grid,
  IconButton,
  MenuItem,
  Select,
  TextField,
  Typography,
  Paper,
  FormControl,
  InputLabel,
  FormHelperText,
  Stack, CircularProgress
} from "@mui/material";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { DatePicker, LocalizationProvider, TimePicker } from "@mui/x-date-pickers";
import { vi } from 'date-fns/locale';
import { format, isEqual, setHours, setMinutes, setSeconds, setMilliseconds } from "date-fns";
import { theme } from './theme';
import {request}from "@/api";
import toast from "react-hot-toast";
import CloseIcon from "@mui/icons-material/Close";
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import EventBusyIcon from '@mui/icons-material/EventBusy';


const parseTimeStringToFixedDate = (timeString) => {
  if (!timeString || typeof timeString !== 'string') return null;
  const [hours, minutes] = timeString.split(':').map(Number);
  if (isNaN(hours) || isNaN(minutes)) return null;
  return setMilliseconds(setSeconds(setMinutes(setHours(new Date(2000, 0, 1), hours), minutes),0),0);
};

const AnnounceAbsenceFormInternal = () => {
  const [configs, setConfigs] = useState({});
  const [leaveHoursRemaining, setLeaveHoursRemaining] = useState(0);
  const [selectedDates, setSelectedDates] = useState([]);
  const [reason, setReason] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [fetchTrigger, setFetchTrigger] = useState(0);

  const [workTimes, setWorkTimes] = useState({
    startWork: null, // Sẽ là Date object (parsed với ngày cố định)
    endWork: null,
    lunchStart: null,
    lunchEnd: null,
  });

  useEffect(() => {
    request("get", "/configs?configGroup=COMPANY_CONFIGS", (res) => {
      const apiConfigs = res.data?.data || {};
      const newConfigs = {};
      Object.entries(apiConfigs).forEach(([k, v]) => (newConfigs[k] = v.config_value));
      setConfigs(newConfigs);

      setWorkTimes({
        startWork: parseTimeStringToFixedDate(newConfigs.START_WORK_TIME),
        endWork: parseTimeStringToFixedDate(newConfigs.END_WORK_TIME),
        lunchStart: parseTimeStringToFixedDate(newConfigs.START_LUNCH_TIME),
        lunchEnd: parseTimeStringToFixedDate(newConfigs.END_LUNCH_TIME),
      });
    }, { onError: (err) => { console.error("Lỗi tải cấu hình công ty:", err); toast.error("Không thể tải cấu hình công ty."); }});
  }, []);

  useEffect(() => {
    request("get", "/staffs/me", (res) => {
      const staff = res.data?.data;
      if (staff?.leave_hours !== undefined) {
        setLeaveHoursRemaining(staff.leave_hours);
      }
    }, { onError: (err) => { console.error("Lỗi tải thông tin nhân viên:", err); toast.error("Không thể tải thông tin giờ nghỉ phép."); }});
  }, [fetchTrigger]);


  const addSelectedDate = (date) => {
    if (!date || !workTimes.startWork || !workTimes.endWork) {
      toast.warn("Vui lòng chờ tải xong cấu hình giờ làm việc.");
      return;
    }
    const isoDate = format(date, 'yyyy-MM-dd');
    if (selectedDates.find((d) => d.date === isoDate)) {
      toast.error("Ngày này đã được chọn.");
      return;
    }

    // Khi thêm ngày mới, mặc định là "Cả ngày" và sử dụng workTimes đã parse
    setSelectedDates((prevDates) => [
      ...prevDates,
      {
        id: Date.now(),
        date: isoDate,
        dateObject: date,
        dateType: "all_day",
        startTime: workTimes.startWork, // Đây là Date object từ workTimes
        endTime: workTimes.endWork,     // Đây là Date object từ workTimes
        type: "PAID_LEAVE",
      },
    ]);
  };


  const updateDateField = (index, field, value) => {
    const updated = [...selectedDates];
    const currentEntry = { ...updated[index] };
    currentEntry[field] = value;

    if (field === "dateType") {
      switch (value) {
        case "all_day":
          currentEntry.startTime = workTimes.startWork;
          currentEntry.endTime = workTimes.endWork;
          break;
        case "morning":
          currentEntry.startTime = workTimes.startWork;
          currentEntry.endTime = workTimes.lunchStart;
          break;
        case "afternoon":
          currentEntry.startTime = workTimes.lunchEnd;
          currentEntry.endTime = workTimes.endWork;
          break;
        case "custom":
          // Giữ nguyên startTime, endTime hiện tại hoặc reset về giờ làm việc nếu chúng null
          currentEntry.startTime = currentEntry.startTime || workTimes.startWork;
          currentEntry.endTime = currentEntry.endTime || workTimes.endWork;
          break;
        default:
          break;
      }
    }
    // TimePicker của MUI X với date-fns trả về Date object đầy đủ (ngày hiện tại + giờ đã chọn)
    // Chúng ta chỉ quan tâm đến phần giờ, phút.
    // Khi lưu, chúng ta sẽ format thành HH:mm. Khi hiển thị lại, chúng ta parse lại với ngày cố định.
    if ((field === "startTime" || field === "endTime") && value instanceof Date && !isNaN(value)) {
      // Tạo Date object mới với ngày cố định (2000-01-01) và giờ/phút từ value
      currentEntry[field] = parseTimeStringToFixedDate(format(value, "HH:mm"));
    }


    updated[index] = currentEntry;
    setSelectedDates(updated);
  };

  const deleteDate = (idToDelete) => {
    setSelectedDates((prevDates) => prevDates.filter(d => d.id !== idToDelete));
  };

  const shouldDisableTime = useCallback((timeValue, clockType) => {
    if (!workTimes.lunchStart || !workTimes.lunchEnd || !timeValue) return false;
    // timeValue từ TimePicker (date-fns) là Date object
    // So sánh chỉ phần giờ phút
    const selectedTimeHours = timeValue.getHours();
    const selectedTimeMinutes = timeValue.getMinutes();

    const lunchStartHours = workTimes.lunchStart.getHours();
    const lunchStartMinutes = workTimes.lunchStart.getMinutes();
    const lunchEndHours = workTimes.lunchEnd.getHours();
    const lunchEndMinutes = workTimes.lunchEnd.getMinutes();

    const selectedTotalMinutes = selectedTimeHours * 60 + selectedTimeMinutes;
    const lunchStartTotalMinutes = lunchStartHours * 60 + lunchStartMinutes;
    const lunchEndTotalMinutes = lunchEndHours * 60 + lunchEndMinutes;


    if (clockType === 'minutes' || clockType === 'hours') {
      return selectedTotalMinutes >= lunchStartTotalMinutes && selectedTotalMinutes < lunchEndTotalMinutes;
    }
    return false;
  }, [workTimes.lunchStart, workTimes.lunchEnd]);


  const calculateWorkHours = useCallback((start, end) => {
    if (!start || !end || !workTimes.lunchStart || !workTimes.lunchEnd || end.getTime() <= start.getTime()) return 0;

    // start và end là Date objects (đã được parse với ngày cố định)
    let totalMinutes = (end.getTime() - start.getTime()) / (1000 * 60);

    const lunchStartTimeMs = workTimes.lunchStart.getTime();
    const lunchEndTimeMs = workTimes.lunchEnd.getTime();
    const selectedStartTimeMs = start.getTime();
    const selectedEndTimeMs = end.getTime();

    const overlapStart = Math.max(selectedStartTimeMs, lunchStartTimeMs);
    const overlapEnd = Math.min(selectedEndTimeMs, lunchEndTimeMs);

    if (overlapEnd > overlapStart) {
      const overlapMinutes = (overlapEnd - overlapStart) / (1000 * 60);
      totalMinutes -= overlapMinutes;
    }

    return Math.max(0, totalMinutes / 60); // Đảm bảo không âm
  }, [workTimes.lunchStart, workTimes.lunchEnd]);

  const totalHoursToRequest = useMemo(() => {
    return selectedDates.reduce((acc, d) => acc + calculateWorkHours(d.startTime, d.endTime), 0);
  }, [selectedDates, calculateWorkHours]);

  const paidHoursToRequest = useMemo(() => {
    return selectedDates
      .filter(d => d.type === "PAID_LEAVE")
      .reduce((acc, d) => acc + calculateWorkHours(d.startTime, d.endTime), 0);
  }, [selectedDates, calculateWorkHours]);


  const handleSubmit = async () => {
    if (!reason.trim()) {
      toast.error("Vui lòng nhập lý do nghỉ phép.");
      return;
    }
    if (selectedDates.length === 0) {
      toast.error("Vui lòng chọn ít nhất một ngày nghỉ.");
      return;
    }

    if (paidHoursToRequest > leaveHoursRemaining) {
      toast.error(`Số giờ nghỉ có lương yêu cầu (${paidHoursToRequest.toFixed(1)} giờ) vượt quá số giờ còn lại (${leaveHoursRemaining.toFixed(1)} giờ).`);
      return;
    }

    setIsSubmitting(true);
    let allSuccessful = true;
    const successfullySubmittedDatesIds = [];

    for (const d of selectedDates) {
      if (!d.startTime || !d.endTime || d.endTime.getTime() <= d.startTime.getTime()) {
        toast.error(`Thời gian không hợp lệ cho ngày ${format(d.dateObject, "dd/MM/yyyy")}. Vui lòng kiểm tra lại.`);
        allSuccessful = false;
        continue;
      }

      const payload = {
        date: d.date, // yyyy-MM-dd
        start_time: format(d.startTime, "HH:mm"), // HH:mm
        end_time: format(d.endTime, "HH:mm"),   // HH:mm
        reason: reason.trim(),
        type: d.type,
      };

      try {
        await request("post", "/absences", () => {
          successfullySubmittedDatesIds.push(d.id);
        }, {
          onError: (err) => {
            toast.error(`Lỗi khi gửi yêu cầu cho ngày ${format(d.dateObject, "dd/MM/yyyy")}: ${err.response?.data?.message || "Lỗi không xác định"}`);
            allSuccessful = false;
          },
        }, payload);
      } catch (error) {
        toast.error(`Lỗi hệ thống khi gửi yêu cầu cho ngày ${format(d.dateObject, "dd/MM/yyyy")}.`);
        allSuccessful = false;
      }
    }

    setIsSubmitting(false);
    if (successfullySubmittedDatesIds.length > 0) {
      toast.success(`Đã gửi thành công ${successfullySubmittedDatesIds.length} yêu cầu nghỉ phép.`);
      setSelectedDates(prev => prev.filter(d => !successfullySubmittedDatesIds.includes(d.id)));
      if (selectedDates.length === successfullySubmittedDatesIds.length) { // Nếu tất cả thành công
        setReason("");
      }
      setFetchTrigger(prev => prev + 1);
    } else if (allSuccessful && selectedDates.length === 0) {
      // Không có gì để gửi
    } else if (!allSuccessful && selectedDates.length > 0) {
      toast.error("Một số yêu cầu không thành công. Vui lòng kiểm tra lại.");
    }
  };

  const getAbsenceSessionDisplay = (dateEntry) => {
    if (!workTimes.startWork || !workTimes.endWork || !dateEntry.startTime || !dateEntry.endTime) {
      return format(dateEntry.startTime || new Date(), "HH:mm") + " - " + format(dateEntry.endTime || new Date(), "HH:mm");
    }
    if (isEqual(dateEntry.startTime, workTimes.startWork) && isEqual(dateEntry.endTime, workTimes.endWork)) return "Cả ngày";
    if (workTimes.lunchStart && isEqual(dateEntry.startTime, workTimes.startWork) && isEqual(dateEntry.endTime, workTimes.lunchStart)) return "Buổi sáng";
    if (workTimes.lunchEnd && isEqual(dateEntry.startTime, workTimes.lunchEnd) && isEqual(dateEntry.endTime, workTimes.endWork)) return "Buổi chiều";
    return `${format(dateEntry.startTime, "HH:mm")} - ${format(dateEntry.endTime, "HH:mm")}`;
  };


  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <Box sx={{ p: { xs: 2, md: 3 } }}>
        <Paper sx={{ p: { xs: 2, md: 3 }, maxWidth: 900, mx: "auto" }}>
          <Typography variant="h5" gutterBottom sx={{ fontWeight: 600, display: 'flex', alignItems: 'center' }}>
            <EventBusyIcon sx={{mr: 1, color: 'primary.main'}} /> Thông báo Nghỉ phép
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{mb: 0.5}}>
            Số giờ phép có lương còn lại: <strong style={{color: theme.palette.primary.dark}}>{leaveHoursRemaining.toFixed(1)} giờ</strong>
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{mb: 2}}>
            Tổng số giờ yêu cầu: <strong style={{color: theme.palette.secondary.dark}}>{totalHoursToRequest.toFixed(1)} giờ</strong>
            {selectedDates.some(d => d.type === "PAID_LEAVE") && ` (Có lương: ${paidHoursToRequest.toFixed(1)} giờ)`}
          </Typography>


          <Grid container spacing={2} alignItems="center" mb={3}>
            <Grid item xs={12} sm={7} md={5}>
              <DatePicker
                label="Thêm ngày nghỉ"
                value={null}
                onChange={(date) => date && addSelectedDate(date)}
                // shouldDisableDate={(date) => { // Bỏ disable Thứ 7, Chủ Nhật
                //   const day = date.getDay();
                //   return day === 0 || day === 6;
                // }}
                minDate={new Date()}
                renderInput={(params) => <TextField {...params} fullWidth helperText="Chọn ngày bạn muốn nghỉ."/>}
              />
            </Grid>
          </Grid>

          {selectedDates.length === 0 && (
            <Typography sx={{my: 3, textAlign: 'center', color: 'text.secondary'}}>
              Vui lòng chọn ngày nghỉ bằng cách nhấn vào ô "Thêm ngày nghỉ" ở trên.
            </Typography>
          )}

          {/* Box cuộn cho danh sách ngày nghỉ */}
          <Box sx={{ maxHeight: selectedDates.length > 2 ? '40vh' : 'auto', overflowY: selectedDates.length > 2 ? 'auto' : 'visible', pr: selectedDates.length > 2 ? 1 : 0, mb: 2 }}>
            {selectedDates.map((d, i) => (
              <Paper variant="outlined" key={d.id} sx={{ p: 2, mb: 2, position: "relative" }}>
                <IconButton
                  aria-label="Xóa ngày"
                  sx={{ position: "absolute", top: 8, right: 8, zIndex:1 }}
                  onClick={() => deleteDate(d.id)}
                  size="small"
                >
                  <CloseIcon fontSize="small"/>
                </IconButton>

                <Typography variant="subtitle1" fontWeight={600} mb={2}>
                  Ngày: {format(d.dateObject, "dd/MM/yyyy (EEEE)", {locale: vi})}
                </Typography>

                <Grid container spacing={2} alignItems="center">
                  <Grid item xs={12} md={6}>
                    <FormControl fullWidth>
                      <InputLabel id={`dateType-label-${i}`}>Thời gian nghỉ</InputLabel>
                      <Select
                        labelId={`dateType-label-${i}`}
                        label="Thời gian nghỉ"
                        value={d.dateType}
                        onChange={(e) => updateDateField(i, "dateType", e.target.value)}
                        size="small"
                      >
                        <MenuItem value="all_day">Cả ngày </MenuItem>
                        <MenuItem value="morning">Buổi sáng</MenuItem>
                        <MenuItem value="afternoon">Buổi chiều</MenuItem>
                        <MenuItem value="custom">Tùy chỉnh</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <FormControl fullWidth>
                      <InputLabel id={`leaveType-label-${i}`}>Loại nghỉ</InputLabel>
                      <Select
                        labelId={`leaveType-label-${i}`}
                        label="Loại nghỉ"
                        value={d.type}
                        onChange={(e) => updateDateField(i, "type", e.target.value)}
                        size="small"
                      >
                        <MenuItem value="PAID_LEAVE">Có lương (Phép năm)</MenuItem>
                        <MenuItem value="UNPAID_LEAVE">Không lương</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  {d.dateType === "custom" && (
                    <>
                      <Grid item xs={12} sm={6} md={6}>
                        <TimePicker
                          label="Từ giờ"
                          value={d.startTime} // Đây là Date object với ngày cố định
                          minTime={workTimes.startWork}
                          maxTime={workTimes.endWork}
                          shouldDisableTime={(timeValue, clockType) => shouldDisableTime(timeValue, clockType)}
                          onChange={(val) => updateDateField(i, "startTime", val)}
                          ampm={false}
                          minutesStep={15}
                          renderInput={(params) => <TextField {...params} fullWidth size="small"/>}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={6}>
                        <TimePicker
                          label="Đến giờ"
                          value={d.endTime}
                          minTime={d.startTime || workTimes.startWork}
                          maxTime={workTimes.endWork}
                          shouldDisableTime={(timeValue, clockType) => shouldDisableTime(timeValue, clockType)}
                          onChange={(val) => updateDateField(i, "endTime", val)}
                          ampm={false}
                          minutesStep={15}
                          renderInput={(params) => <TextField {...params} fullWidth size="small"/>}
                        />
                      </Grid>
                    </>
                  )}
                </Grid>
                <Typography variant="caption" display="block" sx={{mt:1, textAlign: 'right'}}>
                  Số giờ cho ngày này: {calculateWorkHours(d.startTime, d.endTime).toFixed(1)} giờ
                </Typography>
              </Paper>
            ))}
          </Box>

          <TextField
            label="Lý do nghỉ phép"
            fullWidth
            multiline
            rows={3}
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            sx={{ mt: selectedDates.length > 0 ? 2 : 0, mb:3 }}
            required
            InputLabelProps={{ required: true }}
          />

          <Button
            variant="contained"
            color="primary"
            fullWidth
            onClick={handleSubmit}
            disabled={isSubmitting || !reason.trim() || selectedDates.length === 0}
            startIcon={isSubmitting ? <CircularProgress size={20} color="inherit"/> : <AddCircleOutlineIcon />}
            size="large"
          >
            {isSubmitting ? "Đang gửi..." : "Gửi Yêu Cầu Nghỉ Phép"}
          </Button>
        </Paper>
      </Box>
    </LocalizationProvider>
  );
};

const AnnounceAbsenceForm = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AnnounceAbsenceFormInternal />
    </ThemeProvider>
  );
};

export default AnnounceAbsenceForm;
