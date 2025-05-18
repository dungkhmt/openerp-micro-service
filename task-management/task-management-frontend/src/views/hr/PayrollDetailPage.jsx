import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Grid,
  TextField,
  Drawer,
  IconButton,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  TableContainer,
  Paper,
  Button, Chip,
} from "@mui/material";
import FilterListIcon from "@mui/icons-material/FilterList";
import CloseIcon from "@mui/icons-material/Close";
import { request } from "@/api";
import { useParams } from "react-router-dom";
import Pagination from "@/components/item/Pagination";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import SearchSelect from "../../components/item/SearchSelect.jsx";

dayjs.extend(isSameOrBefore);

const PayrollDetailPage = () => {
  const { payrollId } = useParams();
  const [payroll, setPayroll] = useState(null);
  const [details, setDetails] = useState([]);
  const [userMap, setUserMap] = useState({});
  const [searchName, setSearchName] = useState("");
  const [searchNameFilter, setSearchNameFilter] = useState("");
  const [filtersOpen, setFiltersOpen] = useState(false);

  const [departmentFilter, setDepartmentFilter] = useState("");
  const [jobPositionFilter, setJobPositionFilter] = useState("");

  const [department, setDepartment] = useState(null);
  const [jobPosition, setJobPosition] = useState(null);
  const [userFilter, setUserFilter] = useState(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(20);
  const [pageCount, setPageCount] = useState(1);

  // Fetch payroll info
  useEffect(() => {
    if (!payrollId) return;
    request("get", `/payrolls/${payrollId}`, (res) => setPayroll(res.data.data));
  }, [payrollId]);

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
    if (searchName == null && department == null && jobPosition == null) {
      setUserFilter(null);
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
  }, [searchName, department, jobPosition]);

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

  const getBgColorForDay = (dayIndex) => {
    if (dayIndex === 0) return "#fdd";
    if (dayIndex === 6) return "#fdd";
    return "#f2f2f2";
  };

  const clearFilters = () => {
    setSearchNameFilter(null);
    setDepartmentFilter(null);
    setJobPositionFilter(null);
  };

  const applyFilters = () => {
    setCurrentPage(0);
    setSearchName(searchNameFilter);
    setDepartment(departmentFilter);
    setJobPosition(jobPositionFilter);
    setFiltersOpen(false);
  };

  const renderStatusChip = (status) => {
    const color = status === "ACTIVE" ? "success" : "default";
    return <Chip label={status} color={color} size="small" />;
  };

  return (
    <Box sx={{ p: 3 }}>
      <Grid container justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h5">{payroll?.name || "Chi tiết kỳ lương"}</Typography>
        <IconButton onClick={() => setFiltersOpen(true)}>
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





      <Drawer anchor="right" open={filtersOpen} onClose={() => setFiltersOpen(false)}>
        <Box p={2} width={300} display="flex" flexDirection="column" height="100%" sx={{ paddingTop: 12 }}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h6">Bộ lọc</Typography>
            <IconButton onClick={() => setFiltersOpen(false)} size="small">
              <CloseIcon />
            </IconButton>
          </Box>

          <TextField
            fullWidth
            label="Tên nhân viên"
            value={searchNameFilter}
            onChange={(e) => setSearchNameFilter(e.target.value)}
            sx={{ mb: 3 }}
          />

          <SearchSelect
            label="Phòng ban"
            fetchUrl="/departments"
            value={departmentFilter}
            onChange={setDepartmentFilter}
            getOptionLabel={(item) => item.department_name}
            sx={{ mb: 3 }}
            clearOnEscape
          />

          <SearchSelect
            label="Chức vụ"
            fetchUrl="/jobs"
            value={jobPositionFilter}
            onChange={setJobPositionFilter}
            getOptionLabel={(item) => item.name}
            clearOnEscape
          />

          <Box sx={{ mt: "auto", display: "flex", justifyContent: "space-between" }}>
            <Button variant="outlined" color="error" onClick={clearFilters}>
              Xóa hết
            </Button>
            <Button variant="contained" onClick={applyFilters}>
              Áp dụng
            </Button>
          </Box>
        </Box>
      </Drawer>

      <TableContainer component={Paper} sx={{ overflowX: "auto", maxHeight: 460 }}>
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              <TableCell
                rowSpan={2}
                style={{
                  position: "sticky",
                  top: 0,
                  left: 0,
                  background: "#f2f2f2",
                  zIndex: 5,
                  minWidth: 100,
                  maxWidth: 100,
                  textAlign: "center",
                  verticalAlign: "middle",
                }}
              >
                Mã NV
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  position: "sticky",
                  top: 0,
                  left: 100,
                  background: "#f2f2f2",
                  zIndex: 5,
                  minWidth: 150,
                  maxWidth: 150,
                  textAlign: "center",
                  verticalAlign: "middle",
                }}
              >
                Họ và tên
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  position: "sticky",
                  top: 0,
                  textAlign: "center",
                  background: "#f2f2f2",
                  zIndex: 4,
                }}
              >
                Phòng ban
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  position: "sticky",
                  top: 0,
                  textAlign: "center",
                  background: "#f2f2f2",
                  zIndex: 4,
                }}
              >
                Chức vụ
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{
                  position: "sticky",
                  top: 0,
                  textAlign: "center",
                  background: "#f2f2f2",
                  zIndex: 4,
                  minWidth: 40,
                }}
              >
                Loại
              </TableCell>
              {dateArray.map((d, i) => (
                <TableCell
                  key={`d-${i}`}
                  align="center"
                  style={{
                    position: "sticky",
                    top: 0,
                    background: "#f2f2f2",
                    zIndex: 3,
                    minWidth: 40,
                    backgroundColor: getBgColorForDay(d.day()),
                  }}
                >
                  <div>{d.format("DD/MM")}</div>
                  <div style={{ fontSize: 12, color: "#666" }}>{getVietnameseWeekday(d.day())}</div>
                </TableCell>
              ))}
              <TableCell
                rowSpan={2}
                style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3, textAlign: "center",}}
              >
                Tổng giờ làm
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3, textAlign: "center", }}
              >
                Nghỉ có lương
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3, textAlign: "center",}}
              >
                Nghỉ không lương
              </TableCell>
              <TableCell
                rowSpan={2}
                style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3, textAlign: "center", }}
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
                  <TableRow>
                    <TableCell
                      rowSpan={2}
                      align="center"
                      style={{
                        position: "sticky",
                        left: 0,
                        background: "white",
                        zIndex: 3,
                        minWidth: 100,
                        maxWidth: 100,
                        height: 35,
                      }}
                    >
                      {user.staff_code || d.user_id}
                    </TableCell>
                    <TableCell
                      rowSpan={2}
                      align="center"
                      style={{
                        position: "sticky",
                        left: 100,
                        background: "white",
                        zIndex: 3,
                        minWidth: 150,
                        maxWidth: 150,
                        height: 80,
                      }}
                    >
                      {user.fullname || d.user_id}
                    </TableCell>
                    <TableCell rowSpan={2} align="center">
                      {user.department?.department_name || "-"}
                    </TableCell>
                    <TableCell rowSpan={2} align="center">
                      {user.job_position?.job_position_name || "-"}
                    </TableCell>
                    <TableCell align="center">WRK</TableCell>
                    {d.work_hours.map((h, i) => (
                      <TableCell key={`wh-${i}`} align="center">
                        {h}
                      </TableCell>
                    ))}
                    <TableCell rowSpan={2} align="center">
                      {d.total_work_hours}
                    </TableCell>
                    <TableCell rowSpan={2} align="center">
                      {d.pair_leave_hours}
                    </TableCell>
                    <TableCell rowSpan={2} align="center">
                      {d.unpair_leave_hours}
                    </TableCell>
                    <TableCell rowSpan={2} align="center">
                      {d.payroll_amount?.toLocaleString()}
                    </TableCell>
                  </TableRow>

                  {/* Absence hours row */}
                  <TableRow>
                    <TableCell align="center">ABS</TableCell>
                    {d.absence_hours.map((h, i) => (
                      <TableCell key={`ah-${i}`} align="center">
                        {h}
                      </TableCell>
                    ))}
                  </TableRow>
                </React.Fragment>
              );
            })}
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
