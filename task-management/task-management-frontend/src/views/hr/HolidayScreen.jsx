import React, { useState, useEffect } from "react";
import { Button, Typography, Box } from "@mui/material";
import AddHolidayModal from "./modals/AddHolidayModal";
import dayjs from "dayjs";
import CalendarMonth from "@mui/lab/CalendarPicker"; // dùng hoặc thay thế tùy thư viện bạn dùng
import { request } from "@/api";

const HolidayScreen = () => {
  const [openModal, setOpenModal] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState(dayjs());
  const [holidays, setHolidays] = useState({});

  const fetchHolidays = async (month) => {
    const yearMonthStr = month.format("YYYY-MM");
    request(
      "get",
      `/holiday?month=${yearMonthStr}`,
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

  const handleMonthChange = (newDate) => {
    setSelectedMonth(dayjs(newDate));
  };

  return (
    <Box p={3}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h5">Lịch nghỉ lễ</Typography>
        <Button variant="contained" onClick={() => setOpenModal(true)}>
          + Thêm ngày nghỉ
        </Button>
      </Box>

      <CalendarMonth
        date={selectedMonth.toDate()}
        onChange={() => {}} // bỏ qua chọn ngày
        onMonthChange={handleMonthChange}
        renderDay={(day, _value, DayComponentProps) => {
          const dateStr = day.format("YYYY-MM-DD");
          const holiday = holidays[dateStr];

          return (
            <div {...DayComponentProps}>
              <span style={{ color: holiday ? "red" : "inherit" }}>
                {day.format("D")}
              </span>
              {holiday && (
                <div style={{ fontSize: 10, color: "red" }}>{holiday.name}</div>
              )}
            </div>
          );
        }}
      />

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
