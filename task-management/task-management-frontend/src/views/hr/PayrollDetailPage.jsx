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
} from "@mui/material";
import FilterListIcon from "@mui/icons-material/FilterList";
import { request } from "@/api";
import { useParams } from "react-router-dom";
import { useDebounce } from "../../hooks/useDebounce";
import Pagination from "@/components/item/Pagination";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";

dayjs.extend(isSameOrBefore);

const PayrollDetailPage = () => {
  const { payrollId } = useParams();
  const [payroll, setPayroll] = useState(null);
  const [details, setDetails] = useState([]);
  const [userMap, setUserMap] = useState({});
  const [searchName, setSearchName] = useState("");
  const debouncedSearch = useDebounce(searchName, 500);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(20);
  const [pageCount, setPageCount] = useState(1);

  useEffect(() => {
    if (!payrollId) return;
    request("get", `/payrolls/${payrollId}`, (res) => setPayroll(res.data.data));
  }, [payrollId]);

  useEffect(() => {
    if (!payrollId) return;
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
      {
        params: {
          searchName: debouncedSearch || null,
          page: currentPage,
          pageSize: itemsPerPage,
        },
      }
    );
  }, [payrollId, debouncedSearch, currentPage, itemsPerPage]);

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

  const getVietnameseWeekday = (dayIndex) => {
    const weekdays = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
    return weekdays[dayIndex];
  };

  const getDateArray = () => {
    if (!payroll?.fromdate || !payroll?.thru_date) return [];
    const from = dayjs(payroll.fromdate);
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

  return (
    <Box sx={{ p: 3 }}>
      <Grid container justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h5">{payroll?.name || "Chi tiết kỳ lương"}</Typography>
        <IconButton onClick={() => setFiltersOpen(true)}>
          <FilterListIcon />
        </IconButton>
      </Grid>

      <Drawer anchor="right" open={filtersOpen} onClose={() => setFiltersOpen(false)}>
        <Box p={2} width={300}>
          <Typography variant="h6" gutterBottom>
            Bộ lọc
          </Typography>
          <TextField
            fullWidth
            label="Tên nhân viên"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
        </Box>
      </Drawer>

      <TableContainer component={Paper} sx={{ overflowX: "auto", maxHeight: 500 }}>
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
                Chức vụ
              </TableCell>
              {dateArray.map((d, i) => (
                <TableCell key={`d-${i}`} align="center" style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3 }}>
                  <div>{d.format("DD/MM")}</div>
                  <div style={{ fontSize: 12, color: "#666" }}>{getVietnameseWeekday(d.day())}</div>
                </TableCell>
              ))}
              <TableCell rowSpan={2} style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3 }}>
                Tổng giờ
              </TableCell>
              <TableCell rowSpan={2} style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3 }}>
                Hưởng lương
              </TableCell>
              <TableCell rowSpan={2} style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3 }}>
                Không lương
              </TableCell>
              <TableCell rowSpan={2} style={{ position: "sticky", top: 0, background: "#f2f2f2", zIndex: 3 }}>
                Tổng lương (₫)
              </TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {details.map((d) => {
              const user = userMap[d.user_id] || {};
              return (
                <React.Fragment key={d.id}>
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
                        height: 35
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
                        height: 80
                      }}
                    >
                      {user.fullname || d.user_id}
                    </TableCell>
                    <TableCell rowSpan={2} align="center">
                      {user.job_position?.job_position_name || "-"}
                    </TableCell>
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
                  <TableRow>
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
