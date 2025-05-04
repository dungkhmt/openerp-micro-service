import React, { useEffect, useMemo, useState } from "react";
import {
  Box,
  Typography,
  Grid,
  TextField,
  Button,
  Chip,
  Paper,
} from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { startOfWeek, addDays, format, isMonday } from "date-fns";
import SearchSelect from "@/components/item/SearchSelect";
import { request } from "@/api";

const WeeklyAbsencePage = () => {
  const [selectedDate, setSelectedDate] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }));
  const [employees, setEmployees] = useState([]);
  const [absenceData, setAbsenceData] = useState([]);

  const [selectedDept, setSelectedDept] = useState(null);
  const [selectedPos, setSelectedPos] = useState(null);
  const [loading, setLoading] = useState(false);

  const weekDays = useMemo(() => {
    const start = startOfWeek(selectedDate, { weekStartsOn: 1 });
    return Array.from({ length: 5 }).map((_, i) => {
      const date = addDays(start, i);
      return {
        date,
        dateStr: format(date, "yyyy-MM-dd"),
        label: format(date, "EEEE - dd/MM"),
      };
    });
  }, [selectedDate]);

  const fetchEmployees = async () => {
    return new Promise((resolve) => {
      request(
        "post",
        "/staff/get-all-staff-info",
        (res) => {
          resolve(res.data?.data || []);
        },
        {},
        {
          department_code: selectedDept?.department_code || null,
          job_position_code: selectedPos?.code || null,
          status: "ACTIVE",
        }
      );
    });
  };

  const fetchAbsences = async (userIds) => {
    const start = format(weekDays[0].date, "yyyy-MM-dd");
    const end = format(weekDays[weekDays.length - 1].date, "yyyy-MM-dd");

    if (!userIds.length) return [];

    return new Promise((resolve) => {
      const params = new URLSearchParams();
      userIds.forEach(id => params.append("userIds", id));
      params.append("startDate", start);
      params.append("endDate", end);

      request(
        "get",
        `/absences?${params.toString()}`,
        (res) => resolve(res.data?.data || []),
        {},
        null
      );
    });
  };

  const handleSearch = async () => {
    setLoading(true);
    const employeeList = await fetchEmployees();
    setEmployees(employeeList);

    const userIds = employeeList.map((emp) => emp.user_login_id);
    const absences = await fetchAbsences(userIds);

    // Merge absences + staff name
    const mergedAbsences = absences.map(abs => {
      const emp = employeeList.find(e => e.user_login_id === abs.user_id);
      return {
        ...abs,
        staff_name: emp?.fullname || "(Không rõ nhân viên)"
      };
    });

    setAbsenceData(mergedAbsences);
    setLoading(false);
  };

  useEffect(() => {
    handleSearch();
  }, []);

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Bảng nghỉ phép theo tuần</Typography>

      <Grid container spacing={2} mb={3}>
        <Grid item xs={12} md={3}>
          <SearchSelect
            label="Phòng ban"
            fetchUrl="/department/"
            value={selectedDept}
            onChange={setSelectedDept}
            getOptionLabel={(item) => item.department_name}
            mapFunction={(data) => data.map((d) => ({ ...d, name: d.department_name }))}
          />
        </Grid>

        <Grid item xs={12} md={3}>
          <SearchSelect
            label="Chức vụ"
            fetchUrl="/job/"
            value={selectedPos}
            onChange={setSelectedPos}
            getOptionLabel={(item) => item.name}
          />
        </Grid>

        <Grid item xs={12} md={3}>
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Chọn ngày trong tuần"
              value={selectedDate}
              onChange={(date) => date && setSelectedDate(date)}
              shouldDisableDate={(date) => !isMonday(date)}
              renderInput={(params) => <TextField {...params} fullWidth />}
            />
          </LocalizationProvider>
        </Grid>

        <Grid item xs={12} md={3}>
          <Button variant="contained" fullWidth sx={{ height: "100%" }} onClick={handleSearch}>
            Tìm kiếm
          </Button>
        </Grid>
      </Grid>

      <Paper sx={{ overflowX: "auto" }}>
        <table style={{ borderCollapse: "collapse", width: "100%" }}>
          <thead>
          <tr>
            <th style={{ padding: 12, borderBottom: "2px solid #ddd", backgroundColor: "#fafafa", textAlign: "left" }}>Thứ - Ngày</th>
            <th style={{ padding: 12, borderBottom: "2px solid #ddd", backgroundColor: "#fafafa", textAlign: "left" }}>Tên nhân viên</th>
            <th style={{ padding: 12, borderBottom: "2px solid #ddd", backgroundColor: "#fafafa", textAlign: "center" }}>Thời gian</th>
            <th style={{ padding: 12, borderBottom: "2px solid #ddd", backgroundColor: "#fafafa", textAlign: "center" }}>Lý do</th>
            <th style={{ padding: 12, borderBottom: "2px solid #ddd", backgroundColor: "#fafafa", textAlign: "center" }}>Loại nghỉ</th>
          </tr>
          </thead>
          <tbody>
          {weekDays.map((d) => {
            const absencesInDay = absenceData.filter((a) => a.date === d.dateStr);

            if (absencesInDay.length === 0) {
              return (
                <tr key={d.dateStr}>
                  <td style={{ padding: 12, borderBottom: "1px solid #eee" }}>{d.label}</td>
                  <td colSpan={4} style={{ padding: 12, textAlign: "center", color: "#aaa", borderBottom: "1px solid #eee" }}>
                    Không có ai nghỉ
                  </td>
                </tr>
              );
            }

            return absencesInDay.map((a, index) => (
              <tr key={`${d.dateStr}-${a.user_login_id}-${index}`}>
                <td style={{ padding: 12, borderBottom: "1px solid #eee" }}>{d.label}</td>
                <td style={{ padding: 12, borderBottom: "1px solid #eee", whiteSpace: "nowrap" }}>{a.staff_name}</td>
                <td style={{ padding: 12, borderBottom: "1px solid #eee", textAlign: "center", whiteSpace: "nowrap" }}>
                  {a.start_time?.slice(0, 5)} - {a.end_time?.slice(0, 5)}
                </td>
                <td style={{ padding: 12, borderBottom: "1px solid #eee", whiteSpace: "normal" }}>
                  {a.reason}
                </td>
                <td style={{ padding: 12, borderBottom: "1px solid #eee", textAlign: "center", whiteSpace: "nowrap" }}>
                  <Chip
                    label={a.type === "PAID_LEAVE" ? "Có lương" : "Không lương"}
                    color={a.type === "PAID_LEAVE" ? "success" : "warning"}
                    size="small"
                  />
                </td>
              </tr>
            ));
          })}
          </tbody>
        </table>
      </Paper>
    </Box>
  );
};

export default WeeklyAbsencePage;
