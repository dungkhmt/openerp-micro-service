import React, { useEffect, useState, useMemo } from "react";
import {
  Grid,
  TextField,
  Button,
  Typography,
  Box,
} from "@mui/material";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { request } from "@/api";
import SearchSelect from "@/components/item/SearchSelect";
import { useNavigate } from "react-router-dom";
import Pagination from "@/components/item/Pagination";

const AttendancePage = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [attendances, setAttendances] = useState({});
  const [selectedDept, setSelectedDept] = useState(null);
  const [selectedPos, setSelectedPos] = useState(null);
  const [searchName, setSearchName] = useState("");
  const [selectedMonth, setSelectedMonth] = useState(new Date());
  const [fetchSearch, setFetchSearch] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [pageCount, setPageCount] = useState(0);
  const [holidays, setHolidays] = useState({});

  const todayStr = new Date().toISOString().split("T")[0];

  const fetchHolidays = async () => {
    const yearMonthStr = `${selectedMonth.getFullYear()}-${String(selectedMonth.getMonth() + 1).padStart(2, "0")}`;
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

  const fetchEmployees = async (page = 0, size = itemsPerPage) => {
    return new Promise((resolve) => {
      request(
        "get",
        "/staffs/details",
        (res) => {
          const list = res.data.data || [];
          const meta = res.data.meta || {};
          setEmployees(list);
          setPageCount(meta.page_info?.total_page || 1);
          setCurrentPage(meta.page_info?.page || 0);
          resolve(list);
        },
        {},
        null,
        {
          params: {
            fullname: searchName || null,
            departmentCode: selectedDept?.department_code || null,
            jobPositionCode: selectedPos?.code || null,
            status: "ACTIVE",
            page,
            pageSize: size
          }
        }

      );
    });
  };

  const fetchAttendances = async (employeeList) => {
    const userIds = employeeList.map((e) => e.user_login_id);
    request(
      "get",
      "/checkinout",
      (res) => {
        setAttendances(res.data?.data?.user_attendances || {});
      },
      {
        onError: (err) => {
          console.error("Failed to load attendance", err);
        },
      },
      null,
      {
        params: {
          userIds: userIds.join(","),
          month: `${selectedMonth.getFullYear()}-${String(selectedMonth.getMonth() + 1).padStart(2, "0")}`,
        }
      }
    );
  };

  const handleSearch = async (page = 0, size = itemsPerPage) => {
    const list = await fetchEmployees(page, size);
    await fetchAttendances(list);
    await fetchHolidays();
    setFetchSearch(fetchSearch + 1)
  };

  useEffect(() => {
    handleSearch();
  }, []);

  const getAttendanceColor = (type) => {
    switch (type) {
      case "PRESENT": return "#e0f7fa";
      case "ABSENT": return "#ffebee";
      case "MISSING": return "#ffebee";
      case "INCOMPLETE": return "#ffebee";
      default: return "#f5f5f5";
    }
  };

  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const days = [];
    const currentDate = new Date(year, month, 1);

    while (currentDate.getMonth() === month) {
      const dateStr = currentDate.toLocaleDateString("en-CA")
      days.push({
        dateStr,
        day: currentDate.getDate(),
        weekday: currentDate.toLocaleDateString("en-CA", { weekday: "short" }),
        isWeekend: currentDate.getDay() === 0 || currentDate.getDay() === 6,
        isFuture: dateStr > todayStr,
      });
      currentDate.setDate(currentDate.getDate() + 1);
    }

    return days;
  };

  const daysInMonth = useMemo(() => getDaysInMonth(selectedMonth), [fetchSearch]);


  const formatTime = (timeStr) => {
    return new Date(timeStr).toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  };

  return (
    <Box sx={{ padding: 3 }}>
      <Typography variant="h5" gutterBottom>Bảng Chấm Công Tháng</Typography>
      <Grid container spacing={2} alignItems="center" marginBottom={2}>
        <Grid item xs={12} md={2}>
          <TextField
            label="Tìm theo tên"
            fullWidth
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
        </Grid>
        <Grid item xs={12} md={2}>
          <SearchSelect
            label="Phòng ban"
            fetchUrl="/departments"
            value={selectedDept}
            onChange={setSelectedDept}
            getOptionLabel={(item) => item.department_name}
            mapFunction={(data) => data.map((d) => ({ ...d, name: d.department_name }))}
          />
        </Grid>
        <Grid item xs={12} md={2}>
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
              views={["month", "year"]}
              label="Chọn tháng"
              value={selectedMonth}
              onChange={(val) => setSelectedMonth(val)}
              renderInput={(params) => <TextField {...params} size="small" fullWidth />}
            />
          </LocalizationProvider>
        </Grid>
        <Grid item xs={12} md={3}>
          <Box display="flex" justifyContent="flex-end" alignItems="center">
            <Button
              variant="contained"
              onClick={() => handleSearch()}
              sx={{ height: 55, minWidth: 140, marginBottom: 1 }}
            >
              Tìm kiếm
            </Button>
          </Box>
        </Grid>
      </Grid>

      <Box sx={{ overflowX: "auto", marginTop: 2 }}>
        <Box sx={{ maxHeight: "calc(100vh - 300px)", overflowY: "auto" }}>
          <table style={{ borderCollapse: "collapse", width: "100%" }}>
            <thead>
            <tr>
              <th style={{
                position: "sticky",
                top: 0,
                left: 0,
                zIndex: 3,
                background: "#fff",
                padding: 8,
                borderBottom: "2px solid #ccc",
                textAlign: "left",
                whiteSpace: "nowrap",
              }}>Nhân viên</th>
              {daysInMonth.map((d) => (
                <th key={d.dateStr} style={{
                  position: "sticky",
                  top: 0,
                  zIndex: 2,
                  padding: 6,
                  textAlign: "center",
                  backgroundColor: d.isWeekend ? "#f3f3f3" : "#fff",
                  color: d.isWeekend ? "#888" : "#000",
                  fontSize: 12,
                  lineHeight: 1.2,
                  borderBottom: "2px solid #ccc",
                }}>
                  <div>{d.day}</div>
                  <div>{d.weekday}</div>
                </th>
              ))}
            </tr>
            </thead>
            <tbody>
            {employees.map((emp) => (
              <tr key={emp.staff_code}>
                <td onClick={() => navigate(`/hr/staff/${emp.staff_code}`)} style={{
                  position: "sticky",
                  left: 0,
                  zIndex: 1,
                  background: "#fff",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: 8,
                  padding: 8,
                  borderBottom: "1px solid #eee",
                  whiteSpace: "nowrap",
                  height: 60,
                }}>
                  <img src={`https://ui-avatars.com/api/?name=${encodeURIComponent(emp.fullname)}&background=random`} alt="Avatar" style={{ width: 30, height: 30, borderRadius: "50%" }} />
                  <span style={{ color: "#1976d2", fontWeight: 500 }}>{emp.fullname}</span>
                </td>
                {daysInMonth.map((d) => {
                  const record = attendances[emp.user_login_id]?.[d.dateStr];
                  const isHoliday = holidays[d.dateStr];
                  const isWeekend = d.isWeekend;
                  const isFuture = d.isFuture;

                  let display = "-";
                  let color = "#bbb";
                  let bg = isHoliday ? (isWeekend ? "#f9f9f9" : "#e8f5e9") :
                    record ? getAttendanceColor(record.attendanceType) :
                      isWeekend ? "#f9f9f9" : "#fff";

                  if (isHoliday) {
                    display = <span style={{ fontWeight: 500, color: "#1b5e20", fontSize: 12 }}>Nghỉ lễ</span>;
                  } else if (!isFuture && !isWeekend && !record) {
                    display = "❌";
                    color = "#000";
                    bg = getAttendanceColor("ABSENT");
                  } else if (record?.startTime && record?.endTime) {
                    display = (<>
                      <div style={{ color: "#005e05", fontWeight: 500 }}>{formatTime(record.startTime)}</div>
                      <div style={{ color: "#005e05", fontWeight: 500 }}>{formatTime(record.endTime)}</div>
                    </>);
                  } else if (record?.startTime) {
                    display = (<div style={{ color: "#c62828", fontWeight: 500 }}>{formatTime(record.startTime)}</div>);
                  } else if (record?.endTime) {
                    display = (<div style={{ color: "#c62828", fontWeight: 500 }}>{formatTime(record.endTime)}</div>);
                  }

                  return (
                    <td key={d.dateStr} style={{
                      backgroundColor: bg,
                      textAlign: "center",
                      padding: 8,
                      borderBottom: "1px solid #eee",
                      fontSize: 13,
                      color,
                      minWidth: 70,
                      height: 60,
                    }}>{display}</td>
                  );
                })}
              </tr>
            ))}
            </tbody>
          </table>
        </Box>
      </Box>
      <Pagination
        currentPage={currentPage}
        pageCount={pageCount}
        itemsPerPage={itemsPerPage}
        onPageChange={(page) => handleSearch(page, itemsPerPage)}
        onItemsPerPageChange={(size) => {
          setItemsPerPage(size);
          handleSearch(0, size);
        }}
      />
    </Box>
  );
};

export default AttendancePage;
