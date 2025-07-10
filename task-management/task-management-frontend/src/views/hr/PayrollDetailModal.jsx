import React, { useEffect, useState, useMemo } from "react";
import {
  Dialog, DialogTitle, DialogContent, IconButton, CircularProgress,
  Typography, Box, Table, TableHead, TableBody, TableRow, TableCell, TableContainer, Paper
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import hrService from "@/services/api/hr.service";
import dayjs from "dayjs";
import { vi } from "date-fns/locale";

const holidayHeaderBgColor = "#d2efd3";
const holidayCellBgColor = "#f1f8f1";
const weekendHeaderBgColor = "#ffebee";
const weekendCellBgColor = "#fff8f8";
const defaultHeaderBgColor = "#f0f1f4";
const defaultCellBgColor = "#fff";

function getVietnameseWeekday(dayIndex) {
  const weekdays = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
  return weekdays[dayIndex];
}

export default function PayrollDetailModal({ open, onClose, payrollId, payrollName, userId }) {
  const [loading, setLoading] = useState(true);
  const [payroll, setPayroll] = useState(null);
  const [detail, setDetail] = useState(null);
  const [holidaysMap, setHolidaysMap] = useState({});

  useEffect(() => {
    if (!open || !payrollId || !userId) return;
    setLoading(true);
    Promise.all([
      hrService.getPayrollList({ id: payrollId }),
      hrService.getPayrollDetails(payrollId, { userIds: userId }),
    ]).then(([payrollRes, detailRes]) => {
      setPayroll(payrollRes.response?.data?.data?.[0] || null);
      setDetail(detailRes.response?.data?.data?.[0] || null);
    }).finally(() => setLoading(false));
  }, [open, payrollId, userId]);

  useEffect(() => {
    if (!payroll?.from_date || !payroll?.thru_date) return setHolidaysMap({});
    hrService.getHolidays({
      startDate: payroll.from_date,
      endDate: payroll.thru_date
    }).then((res) => {
      let holidaysData = res.response?.data?.holidays || res.response?.data?.data || {};
      if (Array.isArray(holidaysData)) {
        const map = {};
        holidaysData.forEach(holiday => {
          map[dayjs(holiday.date).format("YYYY-MM-DD")] = holiday.name || true;
        });
        holidaysData = map;
      }
      setHolidaysMap(holidaysData);
    });
  }, [payroll?.from_date, payroll?.thru_date]);

  const dateArray = useMemo(() => {
    if (!payroll?.from_date || !payroll?.thru_date) return [];
    const from = dayjs(payroll.from_date);
    const to = dayjs(payroll.thru_date);
    const result = [];
    let current = from;
    while (current.isSameOrBefore(to)) {
      result.push(current);
      current = current.add(1, "day");
    }
    return result;
  }, [payroll?.from_date, payroll?.thru_date]);

  // Màu header/cell
  const getBgColorForDayHeader = (d) => {
    const dateStr = d.format("YYYY-MM-DD");
    if (holidaysMap[dateStr]) return holidayHeaderBgColor;
    if (d.day() === 0 || d.day() === 6) return weekendHeaderBgColor;
    return defaultHeaderBgColor;
  };
  const getBgColorForDayCell = (d) => {
    const dateStr = d.format("YYYY-MM-DD");
    if (holidaysMap[dateStr]) return holidayCellBgColor;
    if (d.day() === 0 || d.day() === 6) return weekendCellBgColor;
    return defaultCellBgColor;
  };

  // Thông tin tổng
  function InfoSummary({ detail }) {
    return (
      <Paper
        elevation={1}
        sx={{
          display: 'flex',
          justifyContent: 'space-evenly',
          alignItems: 'center',
          background: '#f4f8fb',
          borderRadius: 2,
          p: 2,
          boxShadow: '0 2px 8px #0001',
          mt: 3,
        }}
      >
        <Box textAlign="center">
          <Typography variant="caption" color="text.secondary">Tổng giờ làm</Typography>
          <Typography variant="h6" fontWeight="bold">{detail.total_work_hours ?? "-"}</Typography>
        </Box>
        <Box textAlign="center">
          <Typography variant="caption" color="text.secondary">Nghỉ có lương (giờ) </Typography>
          <Typography variant="h6" fontWeight="bold">{detail.pair_leave_hours ?? "-"}</Typography>
        </Box>
        <Box textAlign="center">
          <Typography variant="caption" color="text.secondary">Nghỉ không lương (giờ)</Typography>
          <Typography variant="h6" fontWeight="bold">{detail.unpair_leave_hours ?? "-"}</Typography>
        </Box>
        <Box textAlign="center">
          <Typography variant="caption" color="text.secondary">Tổng lương (₫)</Typography>
          <Typography variant="h6" fontWeight="bold" color="primary">
            {detail.payroll_amount != null ? detail.payroll_amount.toLocaleString("vi-VN") : "-"}
          </Typography>
        </Box>
      </Paper>
    );
  }

  function PayrollInfo({ payroll }) {
    return (
      <Paper
        elevation={2}
        sx={{
          mb: 3,
          p: 2,
          background: "#f8fafb",
          borderRadius: 2,
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          flexWrap: "wrap",
          gap: 3,
        }}
      >
        <Box>
          <Typography color="text.secondary" fontWeight={500}>Thời gian</Typography>
          <Typography fontWeight={700}>
            {payroll.from_date ? dayjs(payroll.from_date).format("DD/MM/YYYY") : "-"} – {payroll.thru_date ? dayjs(payroll.thru_date).format("DD/MM/YYYY") : "-"}
          </Typography>
        </Box>
        <Box>
          <Typography color="text.secondary" fontWeight={500}>Ngày công</Typography>
          <Typography fontWeight={700}>{payroll.total_work_days ?? "-"}</Typography>
        </Box>
        <Box>
          <Typography color="text.secondary" fontWeight={500}>Giờ/ngày</Typography>
          <Typography fontWeight={700}>{payroll.work_hours_per_day ?? "-"}</Typography>
        </Box>
        <Box>
          <Typography color="text.secondary" fontWeight={500}>Nghỉ lễ</Typography>
          <Typography fontWeight={700}>{payroll.total_holiday_days ?? "-"}</Typography>
        </Box>
      </Paper>
    );
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xl" fullWidth>
      <DialogTitle>
        Chi tiết lương - {payrollName}
        <IconButton onClick={onClose} sx={{ position: 'absolute', right: 8, top: 8 }}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers>
        {loading ? (
          <Box sx={{ textAlign: 'center', py: 3 }}>
            <CircularProgress /> <Typography component="span" sx={{ ml: 1 }}>Đang tải dữ liệu...</Typography>
          </Box>
        ) : !payroll || !detail ? (
          <Typography align="center" sx={{ py: 3 }}>Không có dữ liệu chi tiết.</Typography>
        ) : (
          <Box>
            <PayrollInfo payroll={payroll} />

            <TableContainer component={Paper} sx={{ overflowX: "auto", maxHeight: "60vh" }} className="custom-scrollbar">
              <Table size="small" stickyHeader sx={{ borderCollapse: 'collapse' }}>
                <TableHead>
                  <TableRow>
                    <TableCell align="center" sx={{ fontWeight: "bold" }}>Loại</TableCell>
                    {dateArray.map((d, i) => (
                      <TableCell key={i} align="center"
                                 sx={{ fontWeight: "bold", background: getBgColorForDayHeader(d), minWidth: 50 }}>
                        {d.format("DD/MM")}<br /><span style={{ fontSize: 12 }}>{getVietnameseWeekday(d.day())}</span>
                      </TableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {/* Work hours row */}
                  <TableRow>
                    <TableCell align="center" sx={{ fontWeight: "bold", bgcolor: "#f5f7fa" }}>WRK</TableCell>
                    {dateArray.map((d, i) => (
                      <TableCell align="center" key={i} sx={{ background: getBgColorForDayCell(d) }}>
                        {detail.work_hours?.[i] !== 0 ? detail.work_hours?.[i] : "-"}
                      </TableCell>
                    ))}
                  </TableRow>
                  {/* Absence hours row */}
                  <TableRow>
                    <TableCell align="center" sx={{ fontWeight: "bold", bgcolor: "#f5f7fa" }}>ABS</TableCell>
                    {dateArray.map((d, i) => (
                      <TableCell align="center" key={i} sx={{ background: getBgColorForDayCell(d) }}>
                        {detail.absence_hours?.[i] !== 0 ? detail.absence_hours?.[i] : "-"}
                      </TableCell>
                    ))}
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>

            {/* Thông tin tổng ở dưới bảng */}
            <InfoSummary detail={detail} />
          </Box>
        )}
      </DialogContent>
    </Dialog>
  );
}
