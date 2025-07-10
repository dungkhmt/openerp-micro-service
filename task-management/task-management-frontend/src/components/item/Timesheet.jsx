// Timesheet.jsx
import React, { useEffect, useState, useCallback } from "react";
import {
  Box,
  Button,
  Paper,
  Typography,
  Grid,
  CircularProgress,
  Divider
} from "@mui/material";
import { request } from "@/api";
import toast from "react-hot-toast";
import dayjs from "dayjs";
import "dayjs/locale/vi";
import { useTheme } from '@mui/material/styles';

dayjs.locale('vi');

const capitalizeFirstLetter = (str) => {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1);
};

const Timesheet = () => {
  const theme = useTheme();
  const [currentTime, setCurrentTime] = useState(dayjs());
  const [firstCheckInTime, setFirstCheckInTime] = useState(null);
  const [lastCheckOutTime, setLastCheckOutTime] = useState(null);
  const [allTodayRecords, setAllTodayRecords] = useState([]); // Lưu tất cả checkin/out trong ngày

  const [hoursWorkedDisplay, setHoursWorkedDisplay] = useState("0.00"); // Giờ làm hiển thị (từ checkin đầu đến checkout cuối, hoặc đến hiện tại)

  const [isLoading, setIsLoading] = useState(false); // Loading cho nút bấm
  const [isFetchingInitialData, setIsFetchingInitialData] = useState(true);


  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(dayjs());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const fetchData = useCallback(async () => {
    if (!isFetchingInitialData) setIsLoading(true);

    const today = dayjs().format("YYYY-MM-DD");
    const payload = { date: today };

    try {
      request(
        "get",
        "/checkinout/me",
        (res) => {
          const data = res.data?.data || [];
          setAllTodayRecords(data); // Lưu trữ tất cả bản ghi

          const checkInsToday = data;
          const checkOutsToday = data;

          const firstCI = checkInsToday.length > 0 ? checkInsToday[0].point_time : null;
          // Lần checkout cuối cùng phải sau lần checkin đầu tiên
          const lastCO = checkOutsToday.length > 1 && firstCI ?
            checkOutsToday.pop()?.point_time
            : null;

          setFirstCheckInTime(firstCI);
          setLastCheckOutTime(lastCO);
        },
        {
          onError: (err) => {
            console.error(err);
            toast.error("Không thể tải dữ liệu chấm công.");
            setAllTodayRecords([]);
            setFirstCheckInTime(null);
            setLastCheckOutTime(null);
          },
        },
        null,
        { params: payload }
      );
    } catch (err) {
      console.error(err);
      toast.error("Lỗi khi tải dữ liệu chấm công.");
      setAllTodayRecords([]);
      setFirstCheckInTime(null);
      setLastCheckOutTime(null);
    } finally {
      if (!isFetchingInitialData) setIsLoading(false);
      setIsFetchingInitialData(false);
    }
  }, [isFetchingInitialData]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    // Tính toán giờ làm việc để hiển thị
    if (firstCheckInTime) {
      const start = dayjs(firstCheckInTime);
      // Nếu có lastCheckOutTime và nó sau firstCheckInTime, tính đến lastCheckOutTime
      // Nếu không, tính đến thời điểm hiện tại
      const end = lastCheckOutTime && dayjs(lastCheckOutTime).isAfter(start) ? dayjs(lastCheckOutTime) : dayjs();

      // Chỉ tính giờ nếu 'end' thực sự sau 'start' (tránh trường hợp lastCheckout là của ngày hôm trước hoặc lỗi dữ liệu)
      if (end.isAfter(start)) {
        const diff = end.diff(start);
        const hours = (diff / (1000 * 60 * 60)).toFixed(2);
        setHoursWorkedDisplay(hours);
      } else if (!lastCheckOutTime) { // Nếu chỉ mới checkin, chưa checkout lần nào trong ngày
        const diffToNow = dayjs().diff(start);
        const hoursToNow = (diffToNow / (1000 * 60 * 60)).toFixed(2);
        setHoursWorkedDisplay(hoursToNow);
      } else {
        setHoursWorkedDisplay("0.00"); // Trường hợp lastCheckout không hợp lệ so với firstCheckIn
      }
    } else {
      setHoursWorkedDisplay("0.00"); // Chưa check-in
    }

    // Cập nhật giờ làm liên tục nếu đang trong phiên làm việc (đã check-in, chưa có check-out cuối cùng hợp lệ)
    let interval;
    if (firstCheckInTime && (!lastCheckOutTime || !dayjs(lastCheckOutTime).isAfter(dayjs(firstCheckInTime)) ) ) {
      interval = setInterval(() => {
        const now = dayjs();
        const diff = now.diff(dayjs(firstCheckInTime));
        setHoursWorkedDisplay((diff / (1000 * 60 * 60)).toFixed(2));
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [firstCheckInTime, lastCheckOutTime, currentTime]); // Thêm currentTime để cập nhật khi nó thay đổi (mặc dù đã có interval)


  const handleButtonClick = () => {
    setIsLoading(true);
    try {
      request(
        "post",
        "/checkinout",
        (res) => {
          fetchData();
          toast.success(res.data?.message || "Thao tác thành công!");
        },
        {
          onError: (err) => {
            setIsLoading(false);
            console.error(err);
            toast.error(err.response?.data?.message || "Thao tác thất bại.");
          },
        }
      );
    } catch (err) {
      setIsLoading(false);
      console.error(err);
      toast.error("Lỗi khi thực hiện thao tác chấm công.");
    }
  };

  const formatTimeDisplay = (timeString) => {
    if (!timeString) return "--:--";
    return dayjs(timeString).format("HH:mm");
  };

  const formattedDate = capitalizeFirstLetter(currentTime.format("dddd, DD/MM/YYYY"));
  const formattedClock = currentTime.format("HH:mm:ss");

  if (isFetchingInitialData) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', p: 3, minHeight: 350 }}>
        <CircularProgress />
        <Typography sx={{ml: 2}} color="text.secondary">Đang tải dữ liệu chấm công...</Typography>
      </Box>
    );
  }

  // Nút luôn là "Check Out" nếu đã có firstCheckInTime, ngược lại là "Check In"
  const buttonText = firstCheckInTime ? "Check Out" : "Check In";
  // Nút có màu error (đỏ) khi là "Check Out", primary khi là "Check In"
  const buttonColor = firstCheckInTime ? "error" : "primary";
  // Nút không bị vô hiệu hóa sau khi đã checkout, cho phép checkout nhiều lần
  const isButtonDisabled = isLoading;


  return (
    <Paper
      elevation={2}
      sx={{
        p: {xs: 2, sm: 3},
        maxWidth: 480,
        width: '100%',
        margin: '24px auto',
        textAlign: 'center',
        borderRadius: 3,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: theme.spacing(2.5)
      }}
    >
      <Grid container justifyContent="space-between" alignItems="flex-start" sx={{ width: '100%', mb: 0.5 }}>
        <Grid item>
          <Typography variant="h5" component="div" sx={{fontWeight: '600', textAlign: 'left', color: theme.palette.text.primary}}>
            Bảng chấm công
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" sx={{display: 'block', textAlign: 'left', fontWeight: '500'}}>
            {formattedDate}
          </Typography>
        </Grid>
        <Grid item>
          <Typography variant="h4" component="div" sx={{fontWeight: 'bold', color: theme.palette.primary.main}}>
            {formattedClock}
          </Typography>
        </Grid>
      </Grid>

      <Divider sx={{width: '100%', my: 1.5 }}/>

      <Box
        sx={{
          width: {xs: 160, sm: 180},
          height: {xs: 160, sm: 180},
          borderRadius: "50%",
          border: `5px solid ${theme.palette.grey[300]}`,
          display: "flex",
          flexDirection: 'column',
          justifyContent: "center",
          alignItems: "center",
          my: 2,
          bgcolor: theme.palette.background.default,
          boxShadow: 'inset 0px 3px 6px rgba(0,0,0,0.08)'
        }}
      >
        <Typography variant="h2" component="div" sx={{ fontWeight: 'bold', color: theme.palette.primary.dark, lineHeight: 1.1 }}>
          {hoursWorkedDisplay}
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{lineHeight: 1.2, mt: 0.5}}>
          Giờ làm
        </Typography>
      </Box>

      <Button
        variant="contained"
        color={buttonColor}
        onClick={handleButtonClick}
        disabled={isButtonDisabled}
        size="large"
        sx={{
          minWidth: 220,
          py: 1.3,
          fontSize: '1.05rem',
          fontWeight: '600',
          transition: 'background-color 0.3s ease, transform 0.2s ease',
          '&:hover': {
            transform: isButtonDisabled ? 'none' : 'scale(1.03)',
          },
          boxShadow: theme.shadows[2]
        }}
      >
        {isLoading && !isFetchingInitialData ? <CircularProgress size={24} color="inherit"/> : buttonText}
      </Button>

      { (firstCheckInTime || lastCheckOutTime) && (
        <Grid container spacing={2} justifyContent="center" sx={{ mt: 0, width: '100%' }}>
          {firstCheckInTime && (
            <Grid item xs={12} sm={lastCheckOutTime ? 6 : 8} md={lastCheckOutTime ? 6 : 'auto'}>
              <Paper variant="outlined" sx={{ p: 1.5, bgcolor: theme.palette.grey[100], textAlign:'center' }}>
                <Typography variant="body1" color="text.secondary" sx={{mb:0.5, fontWeight:'500'}}>
                  Check In
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: '600', color: theme.palette.secondary.dark }}>
                  {formatTimeDisplay(firstCheckInTime)}
                </Typography>
              </Paper>
            </Grid>
          )}
          {lastCheckOutTime && (
            <Grid item xs={12} sm={6}>
              <Paper variant="outlined" sx={{ p: 1.5, bgcolor: theme.palette.grey[100], textAlign:'center' }}>
                <Typography variant="body1" color="text.secondary" sx={{mb:0.5, fontWeight:'500'}}>
                  Check Out
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: '600', color: theme.palette.secondary.dark }}>
                  {formatTimeDisplay(lastCheckOutTime)}
                </Typography>
              </Paper>
            </Grid>
          )}
        </Grid>
      )}
    </Paper>
  );
};

export default Timesheet;
