import React, {useEffect, useState, useCallback, useMemo} from "react"; // Added useCallback
import {
  Box,
  Chip, CssBaseline,
  Grid,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow, ThemeProvider,
  Typography,
  CircularProgress, Tooltip, // Added CircularProgress for loading state
} from "@mui/material";
import FilterListIcon from "@mui/icons-material/FilterList";
import {request}from "@/api";
import {useParams} from "react-router-dom";
import Pagination from "@/components/item/Pagination";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import UserFilterDrawer from "./shift-scheduler/UserFilterDrawer.jsx"; // Assuming this path is correct relative to this file
import {theme} from "./theme.js"; // Assuming this path is correct

dayjs.extend(isSameOrBefore);

// Define colors for clarity
const holidayHeaderBgColor = "#d2efd3";
const holidayCellBgColor = "#f1f8f1";
const weekendHeaderBgColor = "#ffebee";
const weekendCellBgColor = "#fff8f8";
const defaultHeaderBgColor = theme.palette.mode === 'light' ? theme.palette.grey[200] : theme.palette.grey[700]; // Using theme color
const defaultCellBgColor = theme.palette.background.paper;


const InnerPayrollDetailPage = () => {
  const [loadingUserDetail, setLoadingUserDetail] = useState(false);
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
  const [userFilter, setUserFilter] = useState(null); // This will store array of user_login_ids

  const [departmentOptions, setDepartmentOptions] = useState([]);
  const [jobPositionOptions, setJobPositionOptions] = useState([]);
  const [holidaysMap, setHolidaysMap] = useState({});

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(200); // Default to 20, consistent
  const [pageCount, setPageCount] = useState(1);
  const [loadingPayroll, setLoadingPayroll] = useState(true);
  const [loadingDetails, setLoadingDetails] = useState(false); // Separate loading for details

  const cellBorderStyle = {
    border: `1px solid ${theme.palette.divider}`,
  };

  const headerCellStyle = {
    ...cellBorderStyle,
    fontWeight: 'bold',
    padding: '8px 10px',
    textAlign: 'center',
    verticalAlign: 'middle',
    backgroundColor: defaultHeaderBgColor, // Moved common background here
    color: theme.palette.getContrastText(defaultHeaderBgColor),
  };

  const stickyHeaderCellStyle = (isDateHeader = false, dayObject = null) => ({
    ...headerCellStyle,
    position: "sticky",
    top: 0,
    zIndex: 5, // Kept user's zIndex for now
    backgroundColor: isDateHeader && dayObject ? getBgColorForDayHeader(dayObject) : defaultHeaderBgColor,
    color: isDateHeader && dayObject ? theme.palette.getContrastText(getBgColorForDayHeader(dayObject)) : theme.palette.getContrastText(defaultHeaderBgColor),
  });

  const stickyBodyCellStyle = (leftPosition) => ({
    ...cellBorderStyle,
    position: "sticky",
    left: leftPosition,
    background: defaultCellBgColor,
    zIndex: 3,
    padding: '6px 10px',
  });


  useEffect(() => {
    if (!payrollId) return;
    setLoadingPayroll(true);
    request("get", `/payrolls/${payrollId}`, (res) => {
      setPayroll(res.data.data);
      setLoadingPayroll(false);
    }, {onError: () => setLoadingPayroll(false)});
  }, [payrollId]);

  useEffect(() => {
    if (payroll?.from_date && payroll?.thru_date) {
      const startTime = dayjs(payroll.from_date).format("YYYY-MM-DD");
      const endTime = dayjs(payroll.thru_date).format("YYYY-MM-DD");
      request(
        "get",
        `/holidays?startDate=${startTime}&endDate=${endTime}`,
        (res) => {
          let holidaysData = res.data.holidays || res.data.data || {};
          if (Array.isArray(holidaysData)) {
            const map = {};
            holidaysData.forEach(holiday => {
              map[dayjs(holiday.date).format("YYYY-MM-DD")] = holiday.name || true;
            });
            holidaysData = map;
          }
          setHolidaysMap(holidaysData);
        },
        {
          onError: (err) => {
            console.error("Failed to load holidays for payroll period:", err);
            setHolidaysMap({});
          },
        }
      );
    }
  }, [payroll?.from_date, payroll?.thru_date]);

  useEffect(() => {
    request("get", "/departments?status=ACTIVE", (res) => {
      const data = res.data.data || []
      setDepartmentOptions(data.map((d) => {return {code: d.department_code, name: d.department_name}}));
    });
    request("get", "/jobs?status=ACTIVE", (res) => {
      setJobPositionOptions(res.data.data || []);
    });
  }, []);

  const fetchPayrollDetails = useCallback(() => {
    if (!payrollId) return;
    setLoadingDetails(true);
    const params = {
      userIds: userFilter === null || userFilter.length === 0 && (searchName || department || jobPosition) ? undefined : (userFilter && userFilter.length > 0 ? userFilter.join(",") : undefined),
      page: currentPage,
      pageSize: itemsPerPage,
    };
    if((searchName || department || jobPosition) && userFilter && userFilter.length === 0){
      params.userIds = "EMPTY_FILTER_NO_MATCH";
    }

    request(
      "get",
      `/payrolls/${payrollId}/details`,
      (res) => {
        const list = res.data.data || [];
        const meta = res.data.meta?.page_info || {};
        setDetails(list);
        setCurrentPage(meta.page || 0);
        setPageCount(meta.total_page || 1);
        if (list.length > 0) fetchUsers(list); else setUserMap({});
        setLoadingDetails(false);
      },
      {onError: () => setLoadingDetails(false)},
      null,
      { params }
    );
  }, [payrollId, currentPage, itemsPerPage, userFilter, searchName, department, jobPosition]);

  const fetchUsers = useCallback((list) => {
    if (list.length === 0) {
      setUserMap({});
      return;
    }
    const userIds = [...new Set(list.map((item) => item.user_id))].filter(Boolean);
    if(userIds.length === 0) {
      setUserMap({});
      return;
    }
    setLoadingUserDetail(true);
    request(
      "get",
      "/staffs/details",
      (res) => {
        const map = {};
        for (const staff of res.data.data || []) {
          map[staff.user_login_id] = staff;
        }
        setUserMap(map);
        setLoadingUserDetail(false);
      },
      {onError: () => setLoadingUserDetail(false)},
      null,
      { params: { userIds: userIds.join(","), page: 0, pageSize: 250} }
    );
  }, []);

  const sortedDetails = useMemo(() => {
    if (!details.length || !Object.keys(userMap).length) return details;
    return [...details].sort((a, b) => {
      const staffCodeA = (userMap[a.user_id]?.staff_code || a.user_id || "").toString();
      const staffCodeB = (userMap[b.user_id]?.staff_code || b.user_id || "").toString();
      return staffCodeA.localeCompare(staffCodeB, "vi", {numeric: true});
    });
  }, [details, userMap]);


  useEffect(() => {
    fetchPayrollDetails();
  }, [fetchPayrollDetails]);


  const applyStaffFilters = useCallback(() => {
    if (!payrollId) return;
    if (tempSearchName === "" && tempDepartmentFilter == null && tempJobPositionFilter == null) {
      setUserFilter(null);
      setSearchName("");
      setDepartment(null);
      setJobPosition(null);
      setCurrentPage(0);
      return;
    }
    request(
      "get",
      "/staffs/details",
      (res) => {
        const staff_ids = (res.data.data || []).map((staff) => staff.user_login_id);
        setUserFilter(staff_ids);
        setSearchName(tempSearchName);
        setDepartment(tempDepartmentFilter);
        setJobPosition(tempJobPositionFilter);
        setCurrentPage(0);
      },
      { onError: () => { setUserFilter([]); setCurrentPage(0); }},
      null,
      {
        params: {
          fullname: tempSearchName || null,
          departmentCode: tempDepartmentFilter?.code || null,
          jobPositionCode: tempJobPositionFilter?.code || null,
          status: "ACTIVE",
          page: 0,
          pageSize: 250
        },
      }
    );
  }, [payrollId, tempSearchName, tempDepartmentFilter, tempJobPositionFilter ]);


  const getVietnameseWeekday = (dayIndex) => {
    const weekdays = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
    return weekdays[dayIndex];
  };

  const getDateArray = useCallback(() => {
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

  const dateArray = getDateArray();

  const getBgColorForDayHeader = (dayObject) => {
    const dateStr = dayObject.format("YYYY-MM-DD");
    if (holidaysMap[dateStr]) {
      return holidayHeaderBgColor;
    }
    if (dayObject.day() === 0 || dayObject.day() === 6) {
      return weekendHeaderBgColor;
    }
    return defaultHeaderBgColor;
  };

  const getBgColorForDayCell = (dayObject) => {
    const dateStr = dayObject.format("YYYY-MM-DD");
    if (holidaysMap[dateStr]) {
      return holidayCellBgColor;
    }
    if (dayObject.day() === 0 || dayObject.day() === 6) {
      return weekendCellBgColor;
    }
    return defaultCellBgColor;
  }

  const handleClearFiltersInDrawer = () => {
    setTempSearchName("");
    setTempDepartmentFilter(null);
    setTempJobPositionFilter(null);
  };

  const handleApplyFiltersFromDrawer = () => {
    applyStaffFilters();
    setUserFilterDrawerOpen(false);
  };

  const openFilterDrawer = () => {
    setTempSearchName(searchName);
    setTempDepartmentFilter(department);
    setTempJobPositionFilter(jobPosition);
    setUserFilterDrawerOpen(true);
  };

  const renderStatusChip = (status) => {
    if (!status) return <Chip label="-" size="small" />;
    const color = status === "ACTIVE" ? "success" : (status === "PENDING" ? "warning" : "default");
    const label = status === "ACTIVE" ? "Hoạt động" : (status === "PENDING" ? "Chờ duyệt" : (status === "COMPLETED" ? "Hoàn thành" : status));
    return <Chip label={label} color={color} size="small" />;
  };

  if (loadingPayroll) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 128px)' }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>Đang tải thông tin kỳ lương...</Typography>
      </Box>
    );
  }

  if (!payroll) {
    return <Typography sx={{p:3}}>Không tìm thấy thông tin kỳ lương.</Typography>;
  }

  const tableMaxHeight = `calc(100vh - 260px)`;


  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)', p:0 }}> {/* Adjusted main Box padding */}
      <Paper sx={{p:2, mb:2}}>
        <Grid container justifyContent="space-between" alignItems="center">
          <Grid item>
            <Typography variant="h4" component="h1" fontWeight="bold">{payroll?.name || "Chi tiết kỳ lương"}</Typography>
          </Grid>
          <Grid item>
            <Tooltip title="Lọc nhân viên">
              <IconButton onClick={openFilterDrawer} color="primary">
                <FilterListIcon />
              </IconButton>
            </Tooltip>
          </Grid>
        </Grid>
      </Paper>

      <Paper
        elevation={2}
        sx={{
          width: "100%",
          p: 2,
          borderRadius: 2,
          mb: 2,
          bgcolor: theme.palette.mode === 'light' ? "#f7f9fc" : theme.palette.grey[900]
        }}
      >
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="body2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Tổng ngày công:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.total_work_days ?? "-"} ngày
              {payroll?.total_work_days && payroll?.work_hours_per_day && (
                <Typography
                  component="span"
                  fontWeight={500}
                  variant="body2"
                  sx={{ ml: 0.5, color: 'text.secondary' }}
                >
                  ({(payroll.total_work_days * payroll.work_hours_per_day).toFixed(1)} giờ)
                </Typography>
              )}
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="body2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Giờ làm mỗi ngày:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.work_hours_per_day ?? "-"} giờ
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="body2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Ngày nghỉ lễ:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.total_holiday_days ?? "-"} ngày
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Typography variant="body2" color="text.secondary" component="span" sx={{ whiteSpace: "nowrap" }}>
              Thời gian:
            </Typography>{" "}
            <Typography variant="body1" fontWeight={600} component="span">
              {payroll?.from_date ? dayjs(payroll.from_date).format("DD/MM/YYYY") : "-"} –{" "}
              {payroll?.thru_date ? dayjs(payroll.thru_date).format("DD/MM/YYYY") : "-"}
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Box display="flex" alignItems="center" gap={0.5}> {/* Changed to flex for better alignment */}
              <Typography variant="body2" color="text.secondary" sx={{ whiteSpace: "nowrap" }}>
                Trạng thái:
              </Typography>
              {renderStatusChip(payroll?.status)}
            </Box>
          </Grid>
          {/* <Grid item xs={12} sm={4} /> */}
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
        onApply={handleApplyFiltersFromDrawer}
        onClear={handleClearFiltersInDrawer}
      />

      <TableContainer
        component={Paper}
        sx={{ overflowX: "auto", maxHeight: tableMaxHeight, border: `1px solid ${theme.palette.divider}`, borderRadius:1 }}
        className="custom-scrollbar"
      >
        <Table stickyHeader size="small" sx={{borderCollapse: 'collapse'}}>
          <TableHead>
            <TableRow>
              <TableCell rowSpan={2} sx={{...stickyHeaderCellStyle(), left: 0, minWidth: 100, maxWidth: 100, zIndex: 6 }}>Mã NV</TableCell>
              <TableCell rowSpan={2} sx={{...stickyHeaderCellStyle(), left: 100, minWidth: 180, maxWidth: 180, zIndex: 6 }}>Họ và tên</TableCell>
              <TableCell rowSpan={2} sx={{...stickyHeaderCellStyle(), minWidth: 150, zIndex: 4 }}>Phòng ban</TableCell>
              <TableCell rowSpan={2} sx={{...stickyHeaderCellStyle(), minWidth: 150, zIndex: 4 }}>Chức vụ</TableCell>
              <TableCell rowSpan={2} sx={{...stickyHeaderCellStyle(), minWidth: 60, zIndex: 4 }}>Loại</TableCell>
              {dateArray.map((d, i) => (
                <TableCell key={`d-${i}`} align="center" sx={{...stickyHeaderCellStyle(true, d), minWidth: 55, top:0 }}> {/* Removed zIndex here as parent has it */}
                  <div>{d.format("DD/MM")}</div>
                  <div style={{ fontSize: '0.75rem' }}>{getVietnameseWeekday(d.day())}</div>
                </TableCell>
              ))}
              <TableCell rowSpan={2} sx={{ ...stickyHeaderCellStyle(), minWidth: 90, zIndex:4 }}>Tổng giờ làm</TableCell>
              <TableCell rowSpan={2} sx={{ ...stickyHeaderCellStyle(), minWidth: 90, zIndex:4 }}>Nghỉ có lương</TableCell>
              <TableCell rowSpan={2} sx={{ ...stickyHeaderCellStyle(), minWidth: 90, zIndex:4 }}>Nghỉ không lương</TableCell>
              <TableCell rowSpan={2} sx={{ ...stickyHeaderCellStyle(), minWidth: 120, zIndex:4 }}>Tổng lương (₫)</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {(loadingDetails || loadingUserDetail) ? (
              <TableRow>
                <TableCell colSpan={dateArray.length + 9} align="center" sx={{p:3, ...cellBorderStyle}}>
                  <CircularProgress size={24} /> <Typography variant="body2" sx={{ml:1, display:'inline'}}>Đang tải chi tiết...</Typography>
                </TableCell>
              </TableRow>
            ) : details.length > 0 ? (
              sortedDetails.map((d_row) => {
                const user = userMap[d_row.user_id] || {};
                return (
                  <React.Fragment key={d_row.user_id + '_payroll_detail'}> {/* Changed key for stability */}
                    <TableRow hover>
                      <TableCell rowSpan={2} align="center" sx={{...stickyBodyCellStyle(0), fontWeight: 'normal' }}>{user.staff_code || d_row.user_id}</TableCell>
                      <TableCell rowSpan={2} sx={{...stickyBodyCellStyle(100), textAlign: 'left', verticalAlign: 'middle' }}>{user.fullname || d_row.user_id}</TableCell>
                      <TableCell rowSpan={2} align="left" sx={{ ...cellBorderStyle, verticalAlign: 'middle', padding: '6px 10px', minWidth: 150, }}>{user.department?.department_name || "-"}</TableCell>
                      <TableCell rowSpan={2} align="left" sx={{ ...cellBorderStyle, verticalAlign: 'middle', padding: '6px 10px', minWidth: 150, }}>{user.job_position?.job_position_name || "-"}</TableCell>
                      <TableCell align="center" sx={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: theme.palette.grey[50] }}>WRK</TableCell>
                      {dateArray.map((dateVal, i) => (
                        <TableCell
                          key={`wh-${d_row.user_id}-${i}`}
                          align="center"
                          sx={{ ...cellBorderStyle, padding: '6px 10px', minWidth: 55, backgroundColor: getBgColorForDayCell(dateVal) }}
                        >
                          {d_row.work_hours[i] !== 0 ? d_row.work_hours[i] : "-"}
                        </TableCell>
                      ))}
                      <TableCell rowSpan={2} align="center" sx={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: theme.palette.grey[100] }}>{d_row.total_work_hours}</TableCell>
                      <TableCell rowSpan={2} align="center" sx={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: theme.palette.grey[100] }}>{d_row.pair_leave_hours}</TableCell>
                      <TableCell rowSpan={2} align="center" sx={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: theme.palette.grey[100] }}>{d_row.unpair_leave_hours}</TableCell>
                      <TableCell rowSpan={2} align="right" sx={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: theme.palette.grey[200] }}>{d_row.payroll_amount != null ? d_row.payroll_amount.toLocaleString() : "-"}</TableCell>
                    </TableRow>

                    <TableRow hover>
                      <TableCell align="center" sx={{ ...cellBorderStyle, fontWeight: "bold", padding: '6px 10px', backgroundColor: theme.palette.grey[50]}}>ABS</TableCell>
                      {dateArray.map((dateVal, i) => (
                        <TableCell
                          key={`ah-${d_row.user_id}-${i}`}
                          align="center"
                          sx={{ ...cellBorderStyle, padding: '6px 10px', minWidth: 55, backgroundColor: getBgColorForDayCell(dateVal) }}
                        >
                          {d_row.absence_hours[i] !== 0 ? d_row.absence_hours[i] : "-"}
                        </TableCell>
                      ))}
                    </TableRow>
                  </React.Fragment>
                );
              })
            ) : (
              <TableRow>
                <TableCell colSpan={dateArray.length + 9} align="center" sx={{padding: '20px', ...cellBorderStyle}}>
                  Không có dữ liệu chi tiết để hiển thị cho bộ lọc hiện tại.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      { details.length > 0 && pageCount > 1 &&
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
      }
    </Box>
  );
};

const PayrollDetailPage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <InnerPayrollDetailPage />
    </ThemeProvider>
  );
};

export default PayrollDetailPage;

