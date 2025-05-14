import React, { useEffect, useState, useMemo } from "react";
import {
  Box,
  Button,
  Grid,
  TextField,
  Typography
} from "@mui/material";
import { request } from "@/api";
import SearchSelect from "@/components/item/SearchSelect";
import Pagination from "@/components/item/Pagination";
import { useNavigate } from "react-router-dom";
import "@/assets/css/EmployeeTable.css";

const SalaryListPage = () => {
  const navigate = useNavigate();
  const [searchName, setSearchName] = useState("");
  const [selectedDept, setSelectedDept] = useState(null);
  const [selectedPos, setSelectedPos] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [pageCount, setPageCount] = useState(0);
  const [employees, setEmployees] = useState([]);
  const [salaries, setSalaries] = useState({});

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

  const fetchSalaries = async (userIds) => {
    request(
      "get",
      "/salaries",
      (res) => {
        const data = res.data?.data || [];
        const mapped = {};
        data.forEach((item) => {
          mapped[item.user_login_id] = item;
        });
        setSalaries(mapped);
      },
      {
        onError: (err) => console.error("Failed to load salaries", err),
      },
      null,
      {
        params: {
          userIds: userIds.join(","),
        },
      }
    );
  };

  const handleSearch = async (page = 0, size = itemsPerPage) => {
    const list = await fetchEmployees(page, size);
    const userIds = list.map((e) => e.user_login_id);
    await fetchSalaries(userIds);
  };

  useEffect(() => {
    handleSearch();
  }, []);

  const mapSalaryType = (type) => {
    switch (type) {
      case "MONTHLY": return "Tháng";
      case "WEEKLY": return "Tuần";
      case "HOURLY": return "Giờ";
      default: return "-";
    }
  };

  const getStaffCodeDisplay = (code) => {
    if (!code || code === "0") return "N/A";
    return code;
  };

  return (
    <Box className="employee-management" sx={{ padding: 3 }}>
      <Typography variant="h5" gutterBottom>Danh sách lương nhân viên</Typography>
      <Grid container spacing={2} alignItems="center" marginBottom={2}>
        <Grid item xs={12} md={3}>
          <TextField
            label="Tìm theo tên"
            fullWidth
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
        </Grid>
        <Grid item xs={12} md={3}>
          <SearchSelect
            label="Phòng ban"
            fetchUrl="/departments"
            value={selectedDept}
            onChange={setSelectedDept}
            getOptionLabel={(item) => item.department_name}
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
          <Box display="flex" justifyContent="flex-end">
            <Button
              variant="contained"
              onClick={() => handleSearch()}
              sx={{ height: 55, minWidth: 120 }}
            >
              Tìm kiếm
            </Button>
          </Box>
        </Grid>
      </Grid>

      <div className="table-container" style={{ maxHeight: "500px", overflowY: "auto" }}>
        <table className="employee-table">
          <thead style={{ position: "sticky", top: 0, background: "#fff", zIndex: 1 }}>
          <tr>
            <th>#</th>
            <th>Mã NV</th>
            <th>Nhân viên</th>
            <th>Phòng ban</th>
            <th>Chức vụ</th>
            <th>Lương</th>
            <th>Loại lương</th>
          </tr>
          </thead>
          <tbody>
          {employees.map((emp, index) => {
            const salaryInfo = salaries[emp.user_login_id];
            return (
              <tr key={emp.staff_code}>
                <td>{currentPage * itemsPerPage + index + 1}</td>
                <td>{getStaffCodeDisplay(emp.staff_code)}</td>
                <td>
                  <div
                    className="employee-name-cell"
                    onClick={() => navigate(`/hr/staff/${emp.staff_code}`)}
                  >
                    <img
                      src={`https://ui-avatars.com/api/?name=${encodeURIComponent(emp.fullname)}&background=random`}
                      alt="Avatar"
                      className="employee-avatar"
                    />
                    <span>{emp.fullname}</span>
                  </div>
                </td>
                <td>{emp.department?.department_name || "-"}</td>
                <td>{emp.job_position?.job_position_name || "-"}</td>
                <td>{salaryInfo?.salary?.toLocaleString("vi-VN") || "-"}</td>
                <td>{mapSalaryType(salaryInfo?.salary_type)}</td>
              </tr>
            );
          })}
          </tbody>
        </table>
      </div>

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

export default SalaryListPage;
