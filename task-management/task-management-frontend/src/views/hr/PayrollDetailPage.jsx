import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Grid,
  IconButton,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  TableContainer,
  Paper,
  Chip,
} from "@mui/material";
import FilterListIcon from "@mui/icons-material/FilterList";
import { request } from "@/api";
import { useParams } from "react-router-dom";
import Pagination from "@/components/item/Pagination";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import UserFilterDrawer from "./shift-scheduler/UserFilterDrawer.jsx";

dayjs.extend(isSameOrBefore);

const PayrollDetailPage = () => {
  const { payrollId } = useParams();
  const [payroll, setPayroll] = useState(null);
  const [details, setDetails] = useState([]);
  const [userMap, setUserMap] = useState({});

  const [searchName, setSearchName] = useState("");
  const [department, setDepartment] = useState(null);
  const [jobPosition, setJobPosition] = useState(null);

  const [tempSearchName, setTempSearchName] = useState("");
  const [tempDepartmentFilter, setTempDepartmentFilter] = useState(null);
  const [tempJobPositionFilter, setTempJobPositionFilter] = useState(null);

  const [userFilterDrawerOpen, setUserFilterDrawerOpen] = useState(false);
  const [userFilter, setUserFilter] = useState(null);

  const [departmentOptions, setDepartmentOptions] = useState([]);
  const [jobPositionOptions, setJobPositionOptions] = useState([]);

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(20);
  const [pageCount, setPageCount] = useState(1);

  // Common cell border style
  const cellBorderStyle = {
    border: '1px solid #e0e0e0', // Light grey border
  };

  const headerCellStyle = {
    ...cellBorderStyle,
    fontWeight: 'bold',
    padding: '8px 10px', // Adjusted padding for headers
    textAlign: 'center',
    verticalAlign: 'middle',
  };

  const stickyHeaderCellStyle = {
    ...headerCellStyle,
    position: "sticky",
    top: 0,
    background: "#f2f2f2", // Light grey background for headers
    zIndex: 5,
  };

  const stickyBodyCellStyle = {
    ...cellBorderStyle,
    position: "sticky",
    background: "white",
    zIndex: 3,
    padding: '6px 10px', // Adjusted padding for body cells
  };


  useEffect(() => {
    if (!payrollId) return;
    request("get", `/payrolls/${payrollId}`, (res) => setPayroll(res.data.data));
  }, [payrollId]);

  useEffect(() => {
    request("get", "/departments", (res) => {
      setDepartmentOptions(res.data.data || []);
    });
  }, []);

  useEffect(() => {
    request("get", "/jobs", (res) => {
      setJobPositionOptions(res.data.data || []);
    });
  }, []);

  useEffect(() => {
    if (!payrollId) return;
    const params = {
      userIds: userFilter == null ? null : userFilter.join(","),
      page: currentPage,
      pageSize: itemsPerPage,
    };

    request(
      "get",
      `/payrolls/${payrollId}/details`,
      (res) => {
        const list = res.data.data || [];
        const meta = res.data.meta.page_info || {};
        setDetails(list);
        setCurrentPage(meta.page || 0);
        setPageCount(meta.total_page || 1);
        fetchUsers(list);
      },
      {},
      null,
      { params }
    );
  }, [payrollId, currentPage, itemsPerPage, userFilter]);

  const fetchUsers = (list) => {
    if (list.length === 0) {
      setUserMap({});
      return;
    }
    const userIds = [...new Set(list.map((item) => item.user_id))];
    request(
      "get",
      "/staffs/details",
      (res) => {
        const map = {};
        for (const staff of res.data.data || []) {
          map[staff.user_login_id] = staff;
        }
        setUserMap(map);
      },
      {},
      null,
      { params: { userIds: userIds.join(",") } }
    );
  };

  useEffect(() => {
    if (!payrollId) return;
    if (searchName === "" && department == null && jobPosition == null) {
      setUserFilter(null);
      return;
    }
    request(
      "get",
      "/staffs/details",
      (res) => {
        setUserFilter(res.data.data.map((staff) => staff.user_login_id));
      },
      {},
      null,
      {
        params: {
          fullname: searchName || null,
          departmentCode: department?.department_code || null,
          jobPositionCode: jobPosition?.code || null,
        },
      }
    );
  }, [payrollId, searchName, department, jobPosition]);

  const getVietnameseWeekday = (dayIndex) => {
    const weekdays = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
    return weekdays[dayIndex];
  };

  const getDateArray = () => {
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
  };

  const dateArray = getDateArray();

  const getBgColorForDayHeader = (dayIndex) => {
    if (dayIndex === 0 || dayIndex === 6) return "#ffebee"; // Lighter red for weekend headers
    return "#f2f2f2"; // Standard header background
  };

  const getBgColorForDayCell = (dayIndex) => {
    if (dayIndex === 0 || dayIndex === 6) return "#fff8f8"; // Very light red for weekend cells
    return "white"; // Standard cell background
  }

  const handleClearFilters = () => {
    setTempSearchName("");
    setTempDepartmentFilter(null);
    setTempJobPositionFilter(null);
  };

  const handleApplyFilters = () => {
    setCurrentPage(0);
    setSearchName(tempSearchName);
    setDepartment(tempDepartmentFilter);
    setJobPosition(tempJobPositionFilter);
    setUserFilterDrawerOpen(false);
  };

  const openFilterDrawer = () => {
    setTempSearchName(searchName);
    setTempDepartmentFilter(department);
    setTempJobPositionFilter(jobPosition);
    setUserFilterDrawerOpen(true);
  };

  const renderStatusChip = (status) => {
    if (!status) return null;
    const color = status === "ACTIVE" ? "success" : "default";
    return <Chip label={status} color={color} size="small" />;
  };

  return (
    <Box sx={{ p: 3 }}>
      <Grid container justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h5" fontWeight="bold">{payroll?.name || "Chi tiết kỳ lương"}</Typography>
        <IconButton onClick={openFilterDrawer}>
          <FilterListIcon />
        </IconButton>
      </Grid>

      <Paper
        elevation={2}
        sx={{
          width: "100%",
          p: 2,
          borderRadius: 2,
          mb: 3,
          bgcolor: "#f7f9fc",
        }}
      >
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={4}>
            <Typography variant="subtitle2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Tổng số ngày làm việc:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.total_work_days ?? "-"} ngày
              {payroll?.total_work_days && payroll?.work_hours_per_day && (
                <Typography
                  component="span"
                  fontWeight={600}
                  sx={{ ml: 0.8 }}
                >
                  ({(payroll.total_work_days * payroll.work_hours_per_day).toFixed(1)} giờ)
                </Typography>
              )}
            </Typography>
          </Grid>

          <Grid item xs={12} sm={4}>
            <Typography variant="subtitle2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Giờ làm việc mỗi ngày:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.work_hours_per_day ?? "-"} giờ
            </Typography>
          </Grid>

          <Grid item xs={12} sm={4}>
            <Typography variant="subtitle2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Tổng số ngày nghỉ lễ:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.total_holiday_days ?? "-"} ngày
            </Typography>
          </Grid>

          <Grid item xs={12} sm={4}>
            <Typography variant="subtitle2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Thời gian:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.from_date ? dayjs(payroll.from_date).format("DD/MM/YYYY") : "-"} –{" "}
              {payroll?.thru_date ? dayjs(payroll.thru_date).format("DD/MM/YYYY") : "-"}
            </Typography>
          </Grid>

          <Grid item xs={12} sm={4}>
            <Box display="inline-flex" alignItems="center" gap={1}>
              <Typography variant="subtitle2" color="text.secondary" sx={{ whiteSpace: "nowrap" }}>
                Trạng thái:
              </Typography>
              {renderStatusChip(payroll?.status)}
            </Box>
          </Grid>
          <Grid item xs={12} sm={4} />
        </Grid>
      </Paper>

      <UserFilterDrawer
        open={userFilterDrawerOpen}
        onClose={() => setUserFilterDrawerOpen(false)}
        nameFilterValue={tempSearchName}
        onNameFilterChange={(e) => setTempSearchName(e.target.value)}
        departmentFilterValue={tempDepartmentFilter}
        onDepartmentFilterChange={(event, newValue) => {
          setTempDepartmentFilter(newValue);
        }}
        jobPositionFilterValue={tempJobPositionFilter}
        onJobPositionFilterChange={(event, newValue) => {
          setTempJobPositionFilter(newValue);
        }}
        departmentOptions={departmentOptions}
        jobPositionOptions={jobPositionOptions}
        onApply={handleApplyFilters}
        onClear={handleClearFilters}
      />

      <TableContainer component={Paper} sx={{ overflowX: "auto", maxHeight: 500, border: '1px solid #e0e0e0', borderRadius:1 }}>
        <Table stickyHeader size="small" sx={{borderCollapse: 'collapse'}}>
          <TableHead>
            <TableRow>
              <TableCell
                rowSpan={2}
                style={{
                  ...stickyHeaderCellStyle,
                  left: 0,
                  minWidth: 100,
                  maxWidth: 100,
                }}
              >
                Mã NV
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  ...stickyHeaderCellStyle,
                  left: 100, // Adjust based on Mã NV width + border
                  minWidth: 180, // Increased width for name
                  maxWidth: 180,
                }}
              >
                Họ và tên
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  ...stickyHeaderCellStyle,
                  zIndex: 4, // Ensure it's above scrolling content but below first two
                  minWidth: 150,
                }}
              >
                Phòng ban
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  ...stickyHeaderCellStyle,
                  zIndex: 4,
                  minWidth: 150,
                }}
              >
                Chức vụ
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  ...stickyHeaderCellStyle,
                  zIndex: 4,
                  minWidth: 60, // Wider for "Loại"
                }}
              >
                Loại
              </TableCell>
              {dateArray.map((d, i) => (
                <TableCell
                  key={`d-${i}`}
                  align="center"
                  style={{
                    ...headerCellStyle, // Use common header style
                    position: "sticky", // Keep sticky for date headers
                    top: 0,
                    zIndex: 3, // Lower zIndex for date headers
                    minWidth: 55, // Slightly wider for DD/MM + Day
                    backgroundColor: getBgColorForDayHeader(d.day()), // Use dynamic bg color
                  }}
                >
                  <div>{d.format("DD/MM")}</div>
                  <div style={{ fontSize: '0.75rem', color: "#555" }}>{getVietnameseWeekday(d.day())}</div>
                </TableCell>
              ))}
              <TableCell
                rowSpan={2}
                style={{ ...stickyHeaderCellStyle, zIndex:3, minWidth: 90 }} // Summary header
              >
                Tổng giờ làm
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{ ...stickyHeaderCellStyle, zIndex:3, minWidth: 90 }} // Summary header
              >
                Nghỉ có lương
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{ ...stickyHeaderCellStyle, zIndex:3, minWidth: 90 }} // Summary header
              >
                Nghỉ không lương
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{ ...stickyHeaderCellStyle, zIndex:3, minWidth: 120 }} // Summary header for currency
              >
                Tổng lương (₫)
              </TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {details.map((d) => {
              const user = userMap[d.user_id] || {};
              return (
                <React.Fragment key={d.id}>
                  {/* Work hours row */}
                  <TableRow hover>
                    <TableCell
                      rowSpan={2}
                      align="center"
                      style={{
                        ...stickyBodyCellStyle,
                        left: 0,
                        minWidth: 100,
                        maxWidth: 100,
                        fontWeight: 'normal',
                      }}
                    >
                      {user.staff_code || d.user_id}
                    </TableCell>
                    <TableCell
                      rowSpan={2}
                      style={{
                        ...stickyBodyCellStyle,
                        left: 100, // Match header
                        minWidth: 180,
                        maxWidth: 180,
                        textAlign: 'left', // Align name to left
                        verticalAlign: 'middle',
                      }}
                    >
                      {user.fullname || d.user_id}
                    </TableCell>
                    <TableCell rowSpan={2} align="left" style={{ ...cellBorderStyle, verticalAlign: 'middle', padding: '6px 10px', minWidth: 150, }}>
                      {user.department?.department_name || "-"}
                    </TableCell>
                    <TableCell rowSpan={2} align="left" style={{ ...cellBorderStyle, verticalAlign: 'middle', padding: '6px 10px', minWidth: 150, }}>
                      {user.job_position?.job_position_name || "-"}
                    </TableCell>
                    <TableCell align="center" style={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: '#fcfcfc' }}>
                      WRK
                    </TableCell>
                    {dateArray.map((dateVal, i) => (
                      <TableCell key={`wh-${d.id}-${i}`} align="center" style={{ ...cellBorderStyle, padding: '6px 10px', minWidth: 55, backgroundColor: getBgColorForDayCell(dateVal.day())}}>
                        {d.work_hours[i] !== 0 ? d.work_hours[i] : "-"}
                      </TableCell>
                    ))}
                    <TableCell rowSpan={2} align="center" style={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: '#f5f5f5' }}>
                      {d.total_work_hours}
                    </TableCell>
                    <TableCell rowSpan={2} align="center" style={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: '#f5f5f5' }}>
                      {d.pair_leave_hours}
                    </TableCell>
                    <TableCell rowSpan={2} align="center" style={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: '#f5f5f5' }}>
                      {d.unpair_leave_hours}
                    </TableCell>
                    <TableCell rowSpan={2} align="right" style={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: '#f0f0f0' }}> {/* Align currency right */}
                      {d.payroll_amount != null ? d.payroll_amount.toLocaleString() : "-"}
                    </TableCell>
                  </TableRow>

                  {/* Absence hours row */}
                  <TableRow hover>
                    <TableCell align="center" style={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: '#fcfcfc'}}>
                      ABS
                    </TableCell>
                    {dateArray.map((dateVal, i) => (
                      <TableCell key={`ah-${d.id}-${i}`} align="center" style={{ ...cellBorderStyle, padding: '6px 10px', minWidth: 55, backgroundColor: getBgColorForDayCell(dateVal.day()) }}>
                        {d.absence_hours[i] !== 0 ? d.absence_hours[i] : "-"}
                      </TableCell>
                    ))}
                  </TableRow>
                </React.Fragment>
              );
            })}
            {details.length === 0 && (
              <TableRow>
                <TableCell colSpan={dateArray.length + 9} align="center" style={{padding: '20px', ...cellBorderStyle}}>
                  Không có dữ liệu để hiển thị.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Pagination
        currentPage={currentPage}
        pageCount={pageCount}
        itemsPerPage={itemsPerPage}
        onPageChange={(page) => setCurrentPage(page)}
        onItemsPerPageChange={(size) => {
          setItemsPerPage(size);
          setCurrentPage(0);
        }}
      />
    </Box>
  );
};

export default PayrollDetailPage;

