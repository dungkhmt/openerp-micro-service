import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Grid,
  TextField,
  Drawer,
  IconButton,
  Stack,
} from "@mui/material";
import FilterListIcon from "@mui/icons-material/FilterList";
import { request } from "@/api";
import { useParams } from "react-router-dom";
import { useDebounce } from "../../hooks/useDebounce";
import Pagination from "@/components/item/Pagination";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import "@/assets/css/EmployeeTable.css";

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
  const [pageCount, setPageCount] = useState(0);

  useEffect(() => {
    if (!payrollId) return;
    request("get", `/payrolls/${payrollId}`,
      (res) => setPayroll(res.data.data)
    );
  }, [payrollId]);

  useEffect(() => {
    if (!payrollId) return;
    request(
      "get",
      `/payrolls/${payrollId}/details`,
      (res) => {
        const list = res.data.data || [];
        setDetails(list);
        fetchUsers(list);
      },
      {},
      null,
      {
        params: {
          searchName: debouncedSearch || null,
          page: currentPage,
          pageSize: itemsPerPage
        }
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
          <Typography variant="h6" gutterBottom>Bộ lọc</Typography>
          <TextField
            fullWidth
            label="Tên nhân viên"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
        </Box>
      </Drawer>

      <div className="table-container" style={{overflowX: "auto", maxHeight: "500px", overflowY: "auto"}}>
        <table className="employee-table" style={{borderCollapse: "collapse", width: "max-content", minWidth: "100%"}}>
          <thead>
          <tr style={{position: "sticky", top: 0, zIndex: 2}}>
            <th rowSpan={2} style={{position: "sticky", left: 0, zIndex: 3}}>Mã NV</th>
            <th rowSpan={2} style={{position: "sticky", left: 100, zIndex: 3}}>Họ và tên
            </th>
            <th rowSpan={2} style={{ left: 250, zIndex: 3}}>Chức vụ</th>
            {dateArray.map((d, i) => (
              <th key={i}>{d.format("DD")}</th>
            ))}
            <th rowSpan={2}>Tổng giờ</th>
            <th rowSpan={2}>Hưởng lương</th>
            <th rowSpan={2}>Không lương</th>
            <th rowSpan={2}>Tổng lương (₫)</th>
          </tr>
          <tr style={{position: "sticky", top: 41, backgroundColor: "#f5f5f5", zIndex: 1}}>
            {dateArray.map((_, i) => (
              <th key={`sub-${i}`}></th>
            ))}
          </tr>
          </thead>
          <tbody>
          {details.map((d) => {
            const user = userMap[d.user_id] || {};
            return (
              <React.Fragment key={d.id}>
                <tr>
                  <td rowSpan={2} style={{position: "sticky", left: 0, backgroundColor: "#fff", zIndex: 1}}>
                    {user.staff_code || d.user_id}
                  </td>
                  <td rowSpan={2} style={{position: "sticky", left: 100, backgroundColor: "#fff", zIndex: 1}}>
                    {user.fullname || d.user_id}
                  </td>
                  <td rowSpan={2} style={{left: 250, backgroundColor: "#fff", zIndex: 1}}>
                    {user.job_position?.job_position_name || "-"}
                  </td>
                  {d.work_hours.map((h, i) => (
                    <td key={`w-${i}`}>{h}</td>
                  ))}
                  <td rowSpan={2}>{d.total_work_hours}</td>
                  <td rowSpan={2}>{d.pair_leave_hours}</td>
                  <td rowSpan={2}>{d.unpair_leave_hours}</td>
                  <td rowSpan={2}>{d.payroll_amount?.toLocaleString()}</td>
                </tr>
                <tr>
                  {d.absence_hours.map((h, i) => (
                    <td key={`a-${i}`}>{h}</td>
                  ))}
                </tr>
              </React.Fragment>
            );
          })}
          </tbody>
        </table>
      </div>


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