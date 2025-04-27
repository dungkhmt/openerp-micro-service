import {
  Box,
  Button,
  Grid,
  IconButton,
  MenuItem,
  Select,
  TextField,
  Typography,
} from "@mui/material";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import {
  DatePicker,
  TimePicker,
  LocalizationProvider,
} from "@mui/x-date-pickers";
import { useEffect, useState } from "react";
import { request } from "@/api";
import toast from "react-hot-toast";
import CloseIcon from "@mui/icons-material/Close";

const AnnounceAbsenceForm = () => {
  const [configs, setConfigs] = useState({});
  const [leaveHours, setLeaveHours] = useState(0);
  const [selectedDates, setSelectedDates] = useState([]);
  const [reason, setReason] = useState("");

  useEffect(() => {
    request("get", "/configs?configGroup=COMPANY_CONFIGS", (res) => {
      const map = {};
      Object.entries(res.data?.data || {}).forEach(([k, v]) => (map[k] = v.config_value));
      setConfigs(map);
    });

    request("get", "/staff", (res) => {
      const staff = res.data?.data;
      if (staff?.leave_hours) setLeaveHours(staff.leave_hours);
    });
  }, []);

  const startWork = configs.START_WORK_TIME && new Date(`1970-01-01T${configs.START_WORK_TIME}`);
  const endWork = configs.END_WORK_TIME && new Date(`1970-01-01T${configs.END_WORK_TIME}`);
  const lunchStart = configs.START_LUNCH_TIME && new Date(`1970-01-01T${configs.START_LUNCH_TIME}`);
  const lunchEnd = configs.END_LUNCH_TIME && new Date(`1970-01-01T${configs.END_LUNCH_TIME}`);

  const addSelectedDate = (date) => {
    if (!date) return;

    const iso = date.toLocaleDateString('en-CA'); // en-CA => YYYY-MM-DD format
    if (selectedDates.find((d) => d.date === iso)) return;

    setSelectedDates([
      ...selectedDates,
      {
        date: iso,
        dateType: "all_day",
        startTime: startWork,
        endTime: endWork,
        type: "PAID_LEAVE",
      },
    ]);
  };


  const updateDateField = (index, field, value) => {
    const updated = [...selectedDates];
    updated[index][field] = value;
    if (field === "dateType" && value === "all_day") {
      updated[index].startTime = startWork;
      updated[index].endTime = endWork;
    }
    setSelectedDates(updated);
  };

  const deleteDate = (index) => {
    const updated = [...selectedDates];
    updated.splice(index, 1);
    setSelectedDates(updated);
  };

  const shouldDisableTime = (time) => {
    return lunchStart && lunchEnd && time >= lunchStart && time <= lunchEnd;
  };

  const calculateWorkHours = (start, end) => {
    if (!start || !end || end <= start) return 0;
    let total = (end - start) / (1000 * 60);
    if (lunchStart && lunchEnd) {
      const overlapStart = start < lunchStart ? lunchStart : start;
      const overlapEnd = end > lunchEnd ? lunchEnd : end;
      const overlap = (overlapEnd - overlapStart) / (1000 * 60);
      if (overlap > 0) total -= overlap;
    }
    return total / 60;
  };

  const submit = async () => {
    if (!reason.trim()) {
      toast.error("Vui lòng nhập lý do nghỉ phép");
      return;
    }

    let totalHoursUsed = 0;
    let successfulDates = [];

    for (let d of selectedDates) {
      const hours = calculateWorkHours(d.startTime, d.endTime);
      if (d.type === "PAID_LEAVE" && totalHoursUsed + hours > leaveHours) {
        toast.error(`Không đủ giờ phép cho ngày ${d.date}`);
        continue;
      }

      await request(
        "post",
        "/absences",
        () => {
          toast.success(`Gửi thành công ${d.date}`);
          totalHoursUsed += hours;
          successfulDates.push(d.date);
        },
        {
          onError: () => toast.error(`Lỗi gửi ngày ${d.date}`),
        },
        {
          date: d.date,
          start_time: d.startTime?.toTimeString().substring(0, 5),
          end_time: d.endTime?.toTimeString().substring(0, 5),
          reason,
          type: d.type,
        }
      );
    }

    // Xoá những ngày thành công khỏi selectedDates
    if (successfulDates.length > 0) {
      setSelectedDates(prev => prev.filter(d => !successfulDates.includes(d.date)));
    }

    // Nếu tất cả thành công → reset lý do
    if (successfulDates.length === selectedDates.length) {
      setReason("");
    }
  };



  return (
    <Box sx={{ px: 3 }}>
      <Typography variant="h5" sx={{ mb: 2, fontWeight: 600 }}>
        Thông báo nghỉ phép
      </Typography>

      <Box sx={{ maxWidth: 800, mx: "auto" }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Thêm ngày nghỉ"
              shouldDisableDate={(date) => {
                const day = date.getDay();
                return day === 0 || day === 6; // 0: Sunday, 6: Saturday
              }}
              onChange={(date) => date && addSelectedDate(date)}
              renderInput={(params) => (
                <TextField {...params} variant="standard" fullWidth sx={{ maxWidth: 250 }} />
              )}
            />

          </LocalizationProvider>
          <Typography variant="body2">
            Số giờ phép còn lại: <strong>{Math.floor(leaveHours)} giờ {Math.round((leaveHours % 1) * 60)} phút</strong>
          </Typography>
        </Box>

        {selectedDates.map((d, i) => (
          <Box
            key={i}
            sx={{
              borderBottom: "1px solid #ddd",
              p: 2,
              mb: 2,
              background: "#fafafa",
              borderRadius: 2,
              position: "relative",
            }}
          >
            <IconButton
              sx={{ position: "absolute", top: 8, right: 8 }}
              onClick={() => deleteDate(i)}
            >
              <CloseIcon />
            </IconButton>

            <Typography variant="subtitle1" fontWeight={600} mb={1}>
              {d.date}
            </Typography>

            <Grid container spacing={2} alignItems="center" sx={{ mt: 1 }}>
              <Grid item xs={12} sm={5}>
                <Box display="flex" alignItems="center">
                  <Typography sx={{ mr: 2, minWidth: 80 }}>Thời gian</Typography>
                  <Select
                    variant="standard"
                    value={d.dateType}
                    onChange={(e) => updateDateField(i, "dateType", e.target.value)}
                    fullWidth
                  >
                    <MenuItem value="all_day">Cả ngày</MenuItem>
                    <MenuItem value="custom">Tuỳ chỉnh</MenuItem>
                  </Select>
                </Box>
              </Grid>
              <Grid item xs={12} sm={1}>
                <Box display="flex" alignItems="center">

                </Box>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Box display="flex" alignItems="center">
                  <Typography sx={{ mr: 2, minWidth: 80 }}>Loại nghỉ</Typography>
                  <Select
                    variant="standard"
                    value={d.type}
                    onChange={(e) => updateDateField(i, "type", e.target.value)}
                    fullWidth
                  >
                    <MenuItem value="PAID_LEAVE">Có lương</MenuItem>
                    <MenuItem value="UNPAID_LEAVE">Không lương</MenuItem>
                  </Select>
                </Box>
              </Grid>
            </Grid>

            {d.dateType === "custom" && (
              <Grid container spacing={2} sx={{ mt: 2 }}>
                <Grid item xs={12} md={3}>
                  <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <TimePicker
                      label="Từ giờ"
                      value={d.startTime}
                      minTime={startWork}
                      maxTime={endWork}
                      shouldDisableTime={shouldDisableTime}
                      onChange={(val) => updateDateField(i, "startTime", val)}
                      renderInput={(params) => (
                        <TextField {...params} variant="standard" fullWidth />
                      )}
                    />
                  </LocalizationProvider>
                </Grid>

                <Grid item xs={12} md={3}>
                  <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <TimePicker
                      label="Đến giờ"
                      value={d.endTime}
                      minTime={startWork}
                      maxTime={endWork}
                      shouldDisableTime={shouldDisableTime}
                      onChange={(val) => updateDateField(i, "endTime", val)}
                      renderInput={(params) => (
                        <TextField {...params} variant="standard" fullWidth />
                      )}
                    />
                  </LocalizationProvider>
                </Grid>
              </Grid>
            )}

          </Box>
        ))}


        <TextField
          sx={{ mt: 2 }}
          label="Lý do nghỉ phép"
          fullWidth
          multiline
          rows={3}
          value={reason}
          onChange={(e) => setReason(e.target.value)}
        />

        <Button
          variant="contained"
          fullWidth
          onClick={submit}
          sx={{ mt: 3 }}
          disabled={!reason.trim() || selectedDates.length <= 0}
        >
          Gửi yêu cầu
        </Button>

      </Box>
    </Box>
  );
};

export default AnnounceAbsenceForm;
