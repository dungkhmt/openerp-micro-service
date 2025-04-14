import React, { useState, useEffect, useMemo } from "react";
import {
  Box,
  Typography,
  Button,
  Stack,
  Paper,
  Grid,
} from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import { request } from "@/api";
import AddHolidayModal from "./modals/AddHolidayModal";

const WEEKDAYS = ["Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"];

const generateCalendarGrid = (month) => {
  const startOfMonth = month.startOf("month");
  const endOfMonth = month.endOf("month");
  const currentMonthDays = endOfMonth.date();

  const startWeekday = (startOfMonth.day() + 6) % 7; // Thứ Hai = 0
  const days = [];

  for (let i = 0; i < startWeekday; i++) {
    days.push(startOfMonth.subtract(startWeekday - i, "day"));
  }

  for (let i = 0; i < currentMonthDays; i++) {
    days.push(startOfMonth.add(i, "day"));
  }

  const totalDays = days.length;
  const remainder = totalDays % 7;
  const trailingDays = remainder === 0 ? 0 : 7 - remainder;

  for (let i = 1; i <= trailingDays; i++) {
    days.push(endOfMonth.add(i, "day"));
  }

  return days;
};


const convertToLunarMock = (date) => {
  const lunarDay = date.date();
  const lunarMonth = date.month(); // 0-based
  return `${lunarDay}/${lunarMonth + 1}`;
};

const HolidayScreen = () => {
  const [selectedMonth, setSelectedMonth] = useState(dayjs());
  const [holidays, setHolidays] = useState({});
  const [openModal, setOpenModal] = useState(false);

  const fetchHolidays = async (month) => {
    const yearMonthStr = month.format("YYYY-MM");
    request(
      "get",
      `/holidays?month=${yearMonthStr}`,
      (res) => {
        setHolidays(res.data.holidays || {});
      },
      {
        onError: (err) => {
          console.error("Failed to load holidays", err);
        },
      }
    );
  };

  useEffect(() => {
    fetchHolidays(selectedMonth);
  }, [selectedMonth]);

  const calendarDates = useMemo(
    () => generateCalendarGrid(selectedMonth),
    [selectedMonth]
  );

  const currentMonth = selectedMonth.month();

  return (
    <Box p={3}>
      <Stack direction="row" justifyContent="space-between" mb={3}>
        <Typography variant="h5">Lịch nghỉ lễ</Typography>
        <Button variant="contained" onClick={() => setOpenModal(true)}>
          + Thêm ngày nghỉ
        </Button>
      </Stack>

      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker
          views={["month", "year"]}
          label="Chọn tháng"
          value={selectedMonth}
          onChange={(newVal) => setSelectedMonth(dayjs(newVal))}
          sx={{ mb: 3, width: 250 }}
        />
      </LocalizationProvider>

      <Grid container spacing={1} mb={1}>
        {WEEKDAYS.map((day, idx) => (
          <Grid item xs={12 / 7} key={idx}>
            <Box
              textAlign="center"
              fontWeight="bold"
              bgcolor="#9a1c1c"
              color="white"
              py={1}
              borderRadius={1}
            >
              {day}
            </Box>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={1}>
        {calendarDates.map((day, idx) => {
          const dateStr = day.format("YYYY-MM-DD");
          const holiday = holidays[dateStr];
          const isCurrentMonth = day.month() === currentMonth;

          return (
            <Grid item xs={12 / 7} key={idx}>
              <Paper
                elevation={1}
                sx={{
                  height: 95,
                  borderRadius: 1,
                  px: 1,
                  py: 0.5,
                  bgcolor: holiday ? "#ffe6e6" : "#fff",
                  border: holiday ? "1px solid red" : "1px solid #ddd",
                  opacity: isCurrentMonth ? 1 : 0.4,
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                }}
              >
                <Box>
                  <Typography fontWeight="bold" fontSize="1.3rem">
                    {day.date()}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {convertToLunarMock(day)}
                  </Typography>
                </Box>
                {holiday && (
                  <Typography
                    variant="caption"
                    fontWeight="bold"
                    color="red"
                    textAlign="center"
                    sx={{
                      fontSize: 'clamp(10px, 1.2vw, 14px)', // auto scale text
                      lineHeight: '1.2em',
                      height: '1.2em',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      overflow: 'hidden',
                      whiteSpace: 'nowrap',
                    }}
                  >
                    {holiday.name}
                  </Typography>
                )}
              </Paper>
            </Grid>
          );
        })}
      </Grid>

      <AddHolidayModal
        open={openModal}
        onClose={() => setOpenModal(false)}
        onSubmit={() => {
          fetchHolidays(selectedMonth);
          setOpenModal(false);
        }}
      />
    </Box>
  );
};

export default HolidayScreen;
