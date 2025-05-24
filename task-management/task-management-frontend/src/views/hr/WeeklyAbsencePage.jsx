import React, {useEffect, useMemo, useState} from "react";
import {Box, Button, Chip, Grid, Paper, TextField, Typography,} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
import {addDays, format, isMonday, startOfWeek} from "date-fns";
import SearchSelect from "@/components/item/SearchSelect";
import {request} from "@/api";

const WeeklyAbsencePage = () => {
  const [selectedDate, setSelectedDate] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }));
  const [fetchSearch, setFetchSearch] = useState(1);
  const [employees, setEmployees] = useState([]);
  const [absenceData, setAbsenceData] = useState([]);
  const [selectedDept, setSelectedDept] = useState(null);
  const [selectedPos, setSelectedPos] = useState(null);
  const [loading, setLoading] = useState(false);
  const [startWork, setStartWork] = useState(null);
  const [endWork, setEndWork] = useState(null)


  const calculateWeekDays = useMemo(() => {
    const start = startOfWeek(selectedDate, { weekStartsOn: 1 });
    return Array.from({ length: 5 }).map((_, i) => {
      const date = addDays(start, i);
      const dayLabels = ["Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu"];
      return {
        date,
        dateStr: format(date, "yyyy-MM-dd"),
        label: `${dayLabels[i]} - ${format(date, "dd/MM")}`,
      };
    });
  }, [selectedDate]);

  const weekDays = useMemo(() => {
    return calculateWeekDays;
  }, [fetchSearch]);

  const fetchEmployees = async () => {
    return new Promise((resolve) => {
      request(
        "get",
        "/staffs/details",
        (res) => resolve(res.data?.data || []),
        {},
        null,
        {
          params: {
            departmentCode: selectedDept?.department_code || null,
            jobPositionCode: selectedPos?.code || null,
            status: "ACTIVE",
          }
        }
      );
    });
  };

  const fetchAbsences = async (userIds) => {
    const start = format(calculateWeekDays[0].date, "yyyy-MM-dd");
    const end = format(calculateWeekDays[weekDays.length - 1].date, "yyyy-MM-dd");

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

    const mergedAbsences = absences.map(abs => {
      const emp = employeeList.find(e => e.user_login_id === abs.user_id);
      const startTime = new Date(`1970-01-01T${abs.start_time}`).getTime();
      const endTime = new Date(`1970-01-01T${abs.end_time}`).getTime();
      const isFullDay = (startTime === startWork && endTime === endWork)
      return {
        ...abs,
        staff_name: emp?.fullname || "(Không rõ nhân viên)",
        isFullDay,
      };
    });

    setAbsenceData(mergedAbsences);
    setFetchSearch(fetchSearch + 1);
    setLoading(false);
  };

  useEffect(() => {
    request("get", "/configs?configGroup=COMPANY_CONFIGS", (res) => {
      const map = {};
      Object.entries(res.data?.data || {}).forEach(([k, v]) => (map[k] = v.config_value));
      setStartWork(new Date(`1970-01-01T${map.START_WORK_TIME}`).getTime());
      setEndWork(new Date(`1970-01-01T${map.END_WORK_TIME}`).getTime());
    });
    handleSearch();
  }, [startWork]);

  const absenceByDay = useMemo(() => {
    const result = {};
    weekDays.forEach((d) => {
      result[d.dateStr] = absenceData.filter((a) => a.date === d.dateStr);
    });
    return result;
  }, [absenceData, weekDays]);

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Danh sách nghỉ phép</Typography>

      <Grid container spacing={2} mb={3}>
        <Grid item xs={12} md={3}>
          <SearchSelect
            label="Phòng ban"
            fetchUrl="/departments"
            value={selectedDept}
            onChange={setSelectedDept}
            getOptionLabel={(item) => item.department_name}
            mapFunction={(data) => data.map((d) => ({ ...d, name: d.department_name }))}
          />
        </Grid>

        <Grid item xs={12} md={3}>
          <SearchSelect
            label="Chức vụ"
            fetchUrl="/jobs"
            value={selectedPos}
            onChange={setSelectedPos}
            getOptionLabel={(item) => item.name}
          />
        </Grid>

        <Grid item xs={12} md={3}>
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Chọn tuần"
              value={selectedDate}
              onChange={(date) => date && setSelectedDate(date)}
              shouldDisableDate={(date) => !isMonday(date)}
              renderInput={(params) => <TextField {...params} fullWidth />}
            />
          </LocalizationProvider>
        </Grid>

        <Grid item xs={12} md={3}>
          <Box display="flex" justifyContent="flex-end" alignItems="center">
            <Button
              variant="contained"
              onClick={() => handleSearch()}
              sx={{ height: 55, minWidth: 140 }}
            >
              Tìm kiếm
            </Button>
          </Box>
        </Grid>
      </Grid>

      <Paper sx={{ overflowY: "auto", maxHeight: "calc(100vh - 300px)" }}>
        <table style={{ borderCollapse: "collapse", width: "100%" }}>
          <thead style={{ position: "sticky", top: 0, backgroundColor: "#f3f3f3", zIndex: 1 }}>
          <tr>
            <th style={{ padding: 12, textAlign: "center", backgroundColor: "#fafafa", fontWeight: "bold", borderBottom: "2px solid #ddd" }}>Ngày trong tuần</th>
            <th style={{ padding: 12, textAlign: "center", backgroundColor: "#fafafa", fontWeight: "bold", borderBottom: "2px solid #ddd" }}>Tên nhân viên</th>
            <th style={{ padding: 12, textAlign: "center", backgroundColor: "#fafafa", fontWeight: "bold", borderBottom: "2px solid #ddd" }}>Thời gian</th>
            <th style={{ padding: 12, textAlign: "center", backgroundColor: "#fafafa", fontWeight: "bold", borderBottom: "2px solid #ddd" }}>Lý do nghỉ</th>
            <th style={{ padding: 12, textAlign: "center", backgroundColor: "#fafafa", fontWeight: "bold", borderBottom: "2px solid #ddd" }}>Loại nghỉ</th>
          </tr>
          </thead>
          <tbody>
          {absenceData.length === 0 ? (
            <tr>
              <td colSpan={5} style={{ padding: 12, textAlign: "center", color: "#888", borderBottom: "2px solid #ddd" }}>
                Không có dữ liệu
              </td>
            </tr>
          ) : (
            weekDays.map((d) => {
              const absences = absenceByDay[d.dateStr];

              return absences.map((a, idx) => (
                <tr key={`${d.dateStr}-${a.user_login_id}-${idx}`}>
                  {idx === 0 && (
                    <td rowSpan={absences.length} style={{ padding: 12, textAlign: "center", verticalAlign: "middle", borderBottom: "1px solid #ddd" }}>
                      {d.label}
                    </td>
                  )}
                  <td style={{ padding: 12, borderBottom: "1px solid #ddd" }}>{a.staff_name}</td>
                  <td style={{ padding: 12, textAlign: "center", borderBottom: "1px solid #ddd" }}>
                    {a.isFullDay ? "Cả ngày" : `${a.start_time?.slice(0, 5)} - ${a.end_time?.slice(0, 5)}`}
                  </td>
                  <td style={{ padding: 12, whiteSpace: "pre-wrap", maxWidth: "200px", wordWrap: "break-word", borderBottom: "1px solid #ddd" }}>
                    {a.reason}
                  </td>
                  <td style={{ padding: 12, textAlign: "center", borderBottom: "1px solid #ddd" }}>
                    <Chip
                      label={a.type === "PAID_LEAVE" ? "Có lương" : "Không lương"}
                      color={a.type === "PAID_LEAVE" ? "success" : "warning"}
                      size="small"
                    />
                  </td>
                </tr>
              ));
            })
          )}
          </tbody>
        </table>
      </Paper>
    </Box>
  );
};

export default WeeklyAbsencePage;
