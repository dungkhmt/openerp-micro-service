import React, {useEffect, useState, useCallback, useMemo} from "react";
import {
  Box,
  Button,
  Grid,
  MenuItem,
  Select,
  TextField,
  Typography,
  // Paper,
  FormControl,
  InputLabel,
  CircularProgress,
  IconButton,
  Stack,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from "@mui/material";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { DatePicker, LocalizationProvider, TimePicker } from "@mui/x-date-pickers";
import { vi } from 'date-fns/locale';
import { format, parseISO, isEqual, setHours, setMinutes, setSeconds, setMilliseconds, isValid as isValidDate } from "date-fns";

import { theme } from './theme';
import {request}from "@/api";
import toast from "react-hot-toast";
import EventBusyIcon from '@mui/icons-material/EventBusy';
import SaveIcon from '@mui/icons-material/Save';
import CloseIcon from "@mui/icons-material/Close";


const parseTimeStringToFixedDate = (timeString) => {
  if (!timeString || typeof timeString !== 'string') return null;
  const [hours, minutes] = timeString.split(':').map(Number);
  if (isNaN(hours) || isNaN(minutes)) return null;
  return setMilliseconds(setSeconds(setMinutes(setHours(new Date(2000, 0, 1), hours), minutes),0),0);
};

// Props mới: open, onClose, absenceId (quan trọng), onUpdated
const UpdateAnnounceAbsenceFormInternal = ({ open, onClose, absenceId, onUpdated }) => {
  const [configs, setConfigs] = useState({});
  const [leaveHoursRemaining, setLeaveHoursRemaining] = useState(0);
  // Khởi tạo absenceDetails là null hoặc một object rỗng để chờ fetch dữ liệu
  const [absenceDetails, setAbsenceDetails] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingData, setIsLoadingData] = useState(false); // State cho việc load data ban đầu và chi tiết đơn

  const [workTimes, setWorkTimes] = useState({
    startWork: null, endWork: null, lunchStart: null, lunchEnd: null,
  });

  // Fetch configs và thông tin nhân viên MỘT LẦN khi modal được mount lần đầu (hoặc khi `open` lần đầu)
  // Hoặc có thể truyền workTimes và leaveHoursRemaining qua props nếu đã có ở component cha
  useEffect(() => {
    if (!open) return; // Chỉ fetch khi modal thực sự mở
    let isMounted = true;
    setIsLoadingData(true); // Bắt đầu loading chung

    const fetchInitialConfigsAndStaffInfo = async () => {
      try {
        // Fetch configs công ty
        const configRes = await new Promise((resolve, reject) => {
          request("get", "/configs?configGroup=COMPANY_CONFIGS",
            (res) => resolve(res.data?.data || {}),
            {onError: reject}
          );
        });
        if (!isMounted) return;
        const newConfigs = {};
        Object.entries(configRes).forEach(([k, v]) => (newConfigs[k] = v.config_value));
        setConfigs(newConfigs); // Lưu lại nếu cần
        setWorkTimes({
          startWork: parseTimeStringToFixedDate(newConfigs.START_WORK_TIME),
          endWork: parseTimeStringToFixedDate(newConfigs.END_WORK_TIME),
          lunchStart: parseTimeStringToFixedDate(newConfigs.START_LUNCH_TIME),
          lunchEnd: parseTimeStringToFixedDate(newConfigs.END_LUNCH_TIME),
        });

        // Fetch thông tin giờ nghỉ còn lại của nhân viên
        const staffRes = await new Promise((resolve, reject) => {
          request("get", "/staffs/me",
            (res) => resolve(res.data?.data),
            {onError: reject}
          );
        });
        if (!isMounted) return;
        if (staffRes?.leave_hours !== undefined) {
          setLeaveHoursRemaining(staffRes.leave_hours);
        }
      } catch (error) {
        if (!isMounted) return;
        console.error("Lỗi tải dữ liệu khởi tạo cho modal sửa:", error);
        toast.error("Không thể tải dữ liệu cần thiết cho form chỉnh sửa.");
        onClose();
      }

    };

    fetchInitialConfigsAndStaffInfo();
    return () => { isMounted = false; };
  }, [open, onClose]); // Chạy lại khi modal mở để đảm bảo dữ liệu mới nhất (nếu cần)

  // Fetch chi tiết đơn nghỉ phép khi có absenceId và workTimes đã được load
  useEffect(() => {
    if (!open || !absenceId || !workTimes.startWork) { // Chờ workTimes có để parse thời gian chính xác
      if (open && absenceId && !workTimes.startWork) {
        setIsLoadingData(true); // Vẫn loading nếu workTimes chưa sẵn sàng
      } else {
        setAbsenceDetails(null); // Reset nếu không có absenceId hoặc modal không mở
        if (!absenceId && open) setIsLoadingData(false); // Nếu không có id thì không load gì cả
      }
      return;
    }

    let isMounted = true;
    setIsLoadingData(true); // Bắt đầu loading chi tiết đơn
    request("get", `/absences/${absenceId}`, (res) => {
      if (!isMounted) return;
      const data = res.data?.data;
      if (data) {
        const absenceStartTime = parseTimeStringToFixedDate(data.start_time);
        const absenceEndTime = parseTimeStringToFixedDate(data.end_time);
        let determinedDateType = "custom";

        if (workTimes.startWork && workTimes.endWork && absenceStartTime && absenceEndTime) {
          if (isEqual(absenceStartTime, workTimes.startWork) && isEqual(absenceEndTime, workTimes.endWork)) determinedDateType = "all_day";
          else if (workTimes.lunchStart && isEqual(absenceStartTime, workTimes.startWork) && isEqual(absenceEndTime, workTimes.lunchStart)) determinedDateType = "morning";
          else if (workTimes.lunchEnd && isEqual(absenceStartTime, workTimes.lunchEnd) && isEqual(absenceEndTime, workTimes.endWork)) determinedDateType = "afternoon";
        }
        setAbsenceDetails({
          id: data.id, date: data.date, dateObject: parseISO(data.date), // parseISO để chuyển YYYY-MM-DD thành Date object
          dateType: determinedDateType, startTime: absenceStartTime, endTime: absenceEndTime,
          type: data.type || "PAID_LEAVE", reason: data.reason || "", status: data.status || "",
        });
      } else {
        toast.error("Không tìm thấy thông tin nghỉ phép."); onClose();
      }
      setIsLoadingData(false); // Kết thúc loading
    }, { onError: (err) => {
        if (!isMounted) return;
        console.error("Lỗi tải chi tiết nghỉ phép:", err);
        toast.error("Không thể tải chi tiết nghỉ phép."); setIsLoadingData(false); onClose();
      }});
    return () => { isMounted = false; };
  }, [open, absenceId, workTimes.startWork, onClose]);


  const handleFieldChange = (field, value) => {
    setAbsenceDetails(prev => {
      if (!prev) return null;
      const updatedEntry = { ...prev };
      updatedEntry[field] = value;
      if (field === "dateType") {
        switch (value) {
          case "all_day": updatedEntry.startTime = workTimes.startWork; updatedEntry.endTime = workTimes.endWork; break;
          case "morning": updatedEntry.startTime = workTimes.startWork; updatedEntry.endTime = workTimes.lunchStart; break;
          case "afternoon": updatedEntry.startTime = workTimes.lunchEnd; updatedEntry.endTime = workTimes.endWork; break;
          case "custom":
            updatedEntry.startTime = prev.startTime || workTimes.startWork;
            updatedEntry.endTime = prev.endTime || workTimes.endWork;
            break;
          default: break;
        }
      }
      if ((field === "startTime" || field === "endTime") && value instanceof Date && isValidDate(value)) {
        updatedEntry[field] = parseTimeStringToFixedDate(format(value, "HH:mm"));
      }
      if (field === "dateObject" && value instanceof Date && isValidDate(value)) {
        updatedEntry.date = format(value, "yyyy-MM-dd");
      }
      return updatedEntry;
    });
  };

  const shouldDisableTime = useCallback((timeValue, clockType) => {
    if (!workTimes.lunchStart || !workTimes.lunchEnd || !timeValue || !isValidDate(timeValue)) return false;
    const selectedTimeHours = timeValue.getHours(); const selectedTimeMinutes = timeValue.getMinutes();
    const lunchStartHours = workTimes.lunchStart.getHours(); const lunchStartMinutes = workTimes.lunchStart.getMinutes();
    const lunchEndHours = workTimes.lunchEnd.getHours(); const lunchEndMinutes = workTimes.lunchEnd.getMinutes();
    const selectedTotalMinutes = selectedTimeHours * 60 + selectedTimeMinutes;
    const lunchStartTotalMinutes = lunchStartHours * 60 + lunchStartMinutes;
    const lunchEndTotalMinutes = lunchEndHours * 60 + lunchEndMinutes;
    if (clockType === 'minutes' || clockType === 'hours') {
      return selectedTotalMinutes >= lunchStartTotalMinutes && selectedTotalMinutes < lunchEndTotalMinutes;
    }
    return false;
  }, [workTimes.lunchStart, workTimes.lunchEnd]);

  const calculateWorkHours = useCallback((start, end) => {
    if (!start || !end || !workTimes.startWork || !workTimes.endWork || !workTimes.lunchStart || !workTimes.lunchEnd || !isValidDate(start) || !isValidDate(end) || end.getTime() <= start.getTime()) return 0;
    let totalMinutes = (end.getTime() - start.getTime()) / (1000 * 60);
    const lunchStartTimeMs = workTimes.lunchStart.getTime(); const lunchEndTimeMs = workTimes.lunchEnd.getTime();
    const selectedStartTimeMs = start.getTime(); const selectedEndTimeMs = end.getTime();
    const overlapStart = Math.max(selectedStartTimeMs, lunchStartTimeMs);
    const overlapEnd = Math.min(selectedEndTimeMs, lunchEndTimeMs);
    if (overlapEnd > overlapStart) {
      totalMinutes -= (overlapEnd - overlapStart) / (1000 * 60);
    }
    return Math.max(0, totalMinutes / 60);
  }, [workTimes.startWork, workTimes.endWork, workTimes.lunchStart, workTimes.lunchEnd]);

  const currentEntryHours = useMemo(() => {
    if (!absenceDetails) return 0;
    return calculateWorkHours(absenceDetails.startTime, absenceDetails.endTime);
  }, [absenceDetails, calculateWorkHours]);

  const handleSubmit = async () => {
    if (!absenceDetails || !absenceDetails.reason?.trim()) { toast.error("Vui lòng nhập lý do nghỉ phép."); return; }
    if (!absenceDetails.dateObject || !absenceDetails.startTime || !absenceDetails.endTime || !isValidDate(absenceDetails.startTime) || !isValidDate(absenceDetails.endTime) || absenceDetails.endTime.getTime() <= absenceDetails.startTime.getTime()) {
      toast.error("Ngày hoặc thời gian nghỉ không hợp lệ."); return;
    }

    // Chỉ cho phép sửa khi trạng thái là PENDING hoặc rỗng (chưa có status)
    if (absenceDetails.status !== "ACTIVE") {
      toast.error(`Không thể sửa yêu cầu đã ${absenceDetails.status === "ACTIVE" ? "duyệt" : (absenceDetails.status === "INACTIVE" ? "hủy" : "từ chối")}.`); return;
    }

    setIsSubmitting(true);
    const payload = {
      date: format(absenceDetails.dateObject, "yyyy-MM-dd"), // Đảm bảo date được format đúng
      start_time: format(absenceDetails.startTime, "HH:mm"),
      end_time: format(absenceDetails.endTime, "HH:mm"),
      reason: absenceDetails.reason.trim(),
      type: absenceDetails.type,
    };
    try {
      await request("put", `/absences/${absenceId}`, () => {
        toast.success(`Đã cập nhật yêu cầu nghỉ phép.`);
        if (onUpdated) onUpdated();
        onClose();
      }, {
        onError: (err) => toast.error(`Lỗi khi cập nhật: ${err.response?.data?.message || "Lỗi không xác định"}`),
      }, payload);
    } catch (error) {
      toast.error(`Lỗi hệ thống khi cập nhật.`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const getAbsenceSessionDisplay = useCallback((startTime, endTime) => {
    if (!workTimes.startWork || !workTimes.endWork || !startTime || !endTime || !isValidDate(startTime) || !isValidDate(endTime)) {
      return (startTime && endTime && isValidDate(startTime) && isValidDate(endTime)) ? `${format(startTime, "HH:mm")} - ${format(endTime, "HH:mm")}` : "Chưa rõ";
    }
    if (isEqual(startTime, workTimes.startWork) && isEqual(endTime, workTimes.endWork)) return "Cả ngày";
    if (workTimes.lunchStart && isEqual(startTime, workTimes.startWork) && isEqual(endTime, workTimes.lunchStart)) return "Buổi sáng";
    if (workTimes.lunchEnd && isEqual(startTime, workTimes.lunchEnd) && isEqual(endTime, workTimes.endWork)) return "Buổi chiều";
    return `${format(startTime, "HH:mm")} - ${format(endTime, "HH:mm")}`;
  }, [workTimes]);

  if (!open) return null;

  const canEditFields = absenceDetails?.status === "ACTIVE"

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth PaperProps={{sx: {overflowY: 'visible' }}}>
      <DialogTitle sx={{ fontWeight: 600, display: 'flex', alignItems: 'center', justifyContent:'space-between', pb:1, pt: 2 }}>
        <Box sx={{display: 'flex', alignItems: 'center'}}>
          <EventBusyIcon sx={{mr: 1, color: 'primary.main'}} /> Chỉnh sửa Yêu cầu Nghỉ phép
        </Box>
        <IconButton aria-label="close" onClick={onClose} sx={{color: (theme) => theme.palette.grey[500]}}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent sx={{pt: '10px !important', pb: 2,
        overflowY: 'auto',
        maxHeight: '70vh',
        '&::-webkit-scrollbar': { width: '6px' },
        '&::-webkit-scrollbar-track': { background: theme.palette.grey[200], borderRadius: '3px' },
        '&::-webkit-scrollbar-thumb': { background: theme.palette.grey[400], borderRadius: '3px' },
        '&::-webkit-scrollbar-thumb:hover': { background: theme.palette.grey[500] }
      }}>
        {(isLoadingData || !absenceDetails) ? ( // Kiểm tra cả absenceDetails
          <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300, flexDirection: 'column'}}><CircularProgress /><Typography sx={{mt:2}}>Đang tải dữ liệu...</Typography></Box>
        ) : (
          <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
            <Box component="form" noValidate autoComplete="off">
              <Typography variant="body2" color="text.secondary" sx={{mb: 0.5, mt:1}}>
                Số giờ phép có lương còn lại: <strong style={{color: theme.palette.primary.dark}}>{leaveHoursRemaining.toFixed(1)} giờ</strong>
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{mb: 2}}>
                Số giờ yêu cầu cho đơn này: <strong style={{color: theme.palette.secondary.dark}}>{currentEntryHours.toFixed(1)} giờ</strong>
                {absenceDetails.type === "PAID_LEAVE" && ` (Loại: Có lương)`}
              </Typography>

              <Grid container spacing={2} alignItems="flex-start">
                <Grid item xs={12} md={6}>
                  <DatePicker
                    label="Ngày nghỉ"
                    value={absenceDetails.dateObject}
                    onChange={(date) => date && handleFieldChange("dateObject", date)}
                    readOnly={!canEditFields}
                    minDate={absenceDetails.status === "ACTIVE" ? new Date() : undefined}
                    renderInput={(params) => <TextField {...params} fullWidth size="small" helperText={!canEditFields ? "Không thể sửa ngày" : ""}/>}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth size="small">
                    <InputLabel id={`leaveType-label-update`}>Loại nghỉ</InputLabel>
                    <Select
                      labelId={`leaveType-label-update`}
                      label="Loại nghỉ"
                      value={absenceDetails.type}
                      onChange={(e) => handleFieldChange("type", e.target.value)}
                      readOnly={!canEditFields}
                    >
                      <MenuItem value="PAID_LEAVE">Có lương (Phép năm)</MenuItem>
                      <MenuItem value="UNPAID_LEAVE">Không lương</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12}>
                  <FormControl fullWidth size="small">
                    <InputLabel id={`dateType-label-update`}>Thời gian nghỉ</InputLabel>
                    <Select
                      labelId={`dateType-label-update`}
                      label="Thời gian nghỉ"
                      value={absenceDetails.dateType}
                      onChange={(e) => handleFieldChange("dateType", e.target.value)}
                      readOnly={!canEditFields}
                    >
                      <MenuItem value="all_day">Cả ngày</MenuItem>
                      <MenuItem value="morning">Buổi sáng</MenuItem>
                      <MenuItem value="afternoon">Buổi chiều</MenuItem>
                      <MenuItem value="custom">Tùy chỉnh</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>

                {absenceDetails.dateType === "custom" && (
                  <>
                    <Grid item xs={12} sm={6}>
                      <TimePicker
                        label="Từ giờ"
                        value={absenceDetails.startTime}
                        minTime={workTimes.startWork}
                        maxTime={workTimes.endWork}
                        shouldDisableTime={(timeValue, clockType) => shouldDisableTime(timeValue, clockType)}
                        onChange={(val) => handleFieldChange("startTime", val)}
                        ampm={false}
                        minutesStep={15}
                        readOnly={!canEditFields}
                        renderInput={(params) => <TextField {...params} fullWidth size="small"/>}
                      />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <TimePicker
                        label="Đến giờ"
                        value={absenceDetails.endTime}
                        minTime={absenceDetails.startTime || workTimes.startWork}
                        maxTime={workTimes.endWork}
                        shouldDisableTime={(timeValue, clockType) => shouldDisableTime(timeValue, clockType)}
                        onChange={(val) => handleFieldChange("endTime", val)}
                        ampm={false}
                        minutesStep={15}
                        readOnly={!canEditFields}
                        renderInput={(params) => <TextField {...params} fullWidth size="small"/>}
                      />
                    </Grid>
                  </>
                )}
              </Grid>

              <TextField
                label="Lý do nghỉ phép"
                fullWidth
                multiline
                rows={3}
                value={absenceDetails.reason}
                onChange={(e) => handleFieldChange("reason", e.target.value)}
                sx={{ mt: 2 }}
                required
                InputLabelProps={{ required: true }}
                InputProps={{ readOnly: !canEditFields }}
              />
            </Box>
          </LocalizationProvider>
        )}
      </DialogContent>
      <DialogActions sx={{px:3, pb:2, pt:1}}>
        <Button onClick={onClose} color="inherit"> Hủy bỏ </Button>
        <Button
          variant="contained"
          color="primary"
          onClick={handleSubmit}
          disabled={isSubmitting || !absenceDetails?.reason?.trim() || !canEditFields || isLoadingData }
          startIcon={isSubmitting ? <CircularProgress size={20} color="inherit"/> : <SaveIcon />}
        >
          {isSubmitting ? "Đang lưu..." : "Lưu Thay Đổi"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default UpdateAnnounceAbsenceFormInternal;
