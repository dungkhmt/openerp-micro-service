import React, { useState, useEffect, useMemo } from "react";
import { useTable, usePagination } from "react-table";
import {
  TextField,
  Select,
  MenuItem,
  Button,
  CircularProgress,
  Autocomplete,
  Snackbar,
  Alert,
  IconButton,
} from "@mui/material";
import { CSVLink } from "react-csv";
import jsPDF from "jspdf";
import GradeModal from "./modals/GradeModal";
import "jspdf-autotable";
import { request } from "../api";
import "../assets/css/CheckpointEvaluation.css";
import { useHistory, useLocation } from "react-router-dom";
import GradeIcon from "@mui/icons-material/BorderColor";

const CheckpointEvaluation = () => {
  const history = useHistory();
  const [staffData, setStaffData] = useState([]);
  const [periods, setPeriods] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);
  const [selectedPeriod, setSelectedPeriod] = useState(null);
  const [selectedDepartment, setSelectedDepartment] = useState(null);
  const [selectedJobPosition, setSelectedJobPosition] = useState(null);
  const [totalPoints, setTotalPoints] = useState({});
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [tempPageInput, setTempPageInput] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const location = useLocation();
  const period = location.state?.period;

  useEffect(() => {
    if (period) {
      setSelectedPeriod(period); 
    }
  }, [period]);

  const fetchStaffData = async (pageIndex, pageSize) => {
    setLoading(true);
    const payload = {
      fullname: searchTerm || null,
      department_code: selectedDepartment?.department_code || null,
      job_position_code: selectedJobPosition?.code || null,
      status: "ACTIVE",
      pageable_request: {
        page: pageIndex,
        page_size: pageSize,
      },
    };
    try {
      request(
        "post",
        "/staff/search-staff",
        (res) => {
          const { data, meta } = res.data;
          setStaffData(data || []);
          setPageCount(meta.page_info.total_page);
          setCurrentPage(meta.page_info.page);
          setTempPageInput(meta.page_info.page + 1);
        },
        { onError: (err) => console.error("Error fetching staff data:", err) },
        payload
      );
    } catch (error) {
      console.error("Error fetching staff data:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchPeriods = async () => {
    try {
      const payload = { name: null, status: "ACTIVE", pageable_request: null };
      request(
        "post",
        "/checkpoint/get-all-period",
        (res) => setPeriods(res.data.data || []),
        { onError: (err) => console.error("Error fetching periods:", err) },
        payload
      );
    } catch (error) {
      console.error("Error fetching periods:", error);
    }
  };

  const fetchDepartments = async () => {
    try {
      request(
        "post",
        "/department/get-department",
        (res) => setDepartments(res.data.data || []),
        { onError: (err) => console.error("Error fetching departments:", err) },
        {}
      );
    } catch (error) {
      console.error("Error fetching departments:", error);
    }
  };

  const fetchJobPositions = async () => {
    try {
      request(
        "post",
        "/job/get-job-position",
        (res) => setJobPositions(res.data.data || []),
        { onError: (err) => console.error("Error fetching job positions:", err) },
        {}
      );
    } catch (error) {
      console.error("Error fetching job positions:", error);
    }
  };

  const fetchCheckpointData = async () => {
    if (!selectedPeriod) return;
    const payload = {
      period_id: selectedPeriod.id,
      user_ids: staffData.map((staff) => staff.user_login_id),
    };
    try {
      request(
        "post",
        "/checkpoint/get-all-checkpoint",
        (res) => {
          const { data } = res.data;
          const points = {};
          data.forEach((item) => (points[item.user_id] = item.total_point || "Not Evaluated"));
          setTotalPoints(points);
        },
        { onError: (err) => console.error("Error fetching checkpoint data:", err) },
        payload
      );
    } catch (error) {
      console.error("Error fetching checkpoint data:", error);
    }
  };

  const exportPDF = () => {
    const doc = new jsPDF();
    doc.text("Checkpoint Evaluation", 20, 10);
    doc.autoTable({
      head: [["Emp Id", "Fullname", "Email", "Total Points"]],
      body: staffData.map((row) => [
        row.staff_code,
        row.fullname,
        row.email,
        totalPoints[row.user_login_id] || "Not Evaluated",
      ]),
    });
    doc.save("CheckpointEvaluation.pdf");
  };

  useEffect(() => {
    fetchPeriods();
    fetchDepartments();
    fetchJobPositions();
    fetchStaffData(0, itemsPerPage);
  }, [itemsPerPage, searchTerm, selectedDepartment, selectedJobPosition]);

  const handleGradeClick = (staff) => {
    if (!selectedPeriod) {
      setError("Please select a period before grading.");
      return;
    }
    setSelectedStaff(staff);
    setModalOpen(true);
  };

  useEffect(() => {
    fetchStaffData();
    fetchPeriods();
  }, []);

  useEffect(() => {
    if (selectedPeriod) {
      fetchCheckpointData();
    }
  }, [selectedPeriod, staffData]);

  const columns = useMemo(
    () => [
      { Header: "#", accessor: "index", Cell: ({ row }) => currentPage * itemsPerPage + row.index + 1 },
      { Header: "Emp Id", accessor: "staff_code" },
      {
        Header: "Fullname",
        accessor: "fullname",
        Cell: ({ row }) => (
          <div
            className="employee-name-cell"
            onClick={() => history.push(`/employee/${row.original.staff_code}`)}
            style={{
              display: "flex",
              alignItems: "center",
              cursor: "pointer",
            }}
          >
            <img
              src={`https://ui-avatars.com/api/?name=${encodeURIComponent(
                row.original.fullname
              )}&background=random&size=100`}
              alt="Avatar"
              className="employee-avatar"
              style={{ width: "40px", borderRadius: "50%" }}
            />
            <span
              style={{
                marginLeft: "10px",
              }}
            >
              {row.original.fullname}
            </span>
          </div>
        ),
      },
      { Header: "Email", accessor: "email" },
      {
        Header: "Total Points",
        accessor: "total_point",
        Cell: ({ row }) => totalPoints[row.original.user_login_id] || "Not Evaluated",
      },
      {
        Header: "Actions",
        Cell: ({ row }) => (
          <IconButton
            className="icon-button"
            onClick={() => handleGradeClick(row.original)}
            disabled={!selectedPeriod}
          >
            <GradeIcon />
          </IconButton>
        ),
      },
    ],
    [currentPage, itemsPerPage, totalPoints]
  );

  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    rows,
    prepareRow,
  } = useTable(
    {
      columns,
      data: staffData,
      manualPagination: true,
      pageCount,
    },
    usePagination
  );

  return (
    <div className="checkpoint-evaluation">
      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError("")}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert severity="error">{error}</Alert>
      </Snackbar>
      <Snackbar
        open={!!success}
        autoHideDuration={6000}
        onClose={() => setSuccess("")}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert severity="success">{success}</Alert>
      </Snackbar>

      <div className="header">
        <h2>Checkpoint Evaluation</h2>
      </div>

      <div className="period-selection">
        <Autocomplete
          options={periods}
          getOptionLabel={(option) => option.name || ""}
          value={selectedPeriod} 
          onChange={(e, value) => setSelectedPeriod(value)}
          renderInput={(params) => (
            <TextField {...params} label="Select Period" size="small" />
          )}
        />
      </div>

      <GradeModal
        open={modalOpen}
        onClose={() => {setModalOpen(false); fetchCheckpointData(); }}
        staff={selectedStaff}
        period={selectedPeriod}
      />

      <div className="export-search-container">
        <div className="export-buttons">
          <CSVLink
            data={staffData}
            filename="CheckpointEvaluation.csv"
          >
            <Button className="export-button csv-button">Export CSV</Button>
          </CSVLink>
          <Button className="export-button pdf-button" onClick={exportPDF}>
            Export PDF
          </Button>
        </div>

        <div className="search-filters">
          <Autocomplete
            options={departments}
            getOptionLabel={(option) => option.department_name || ""}
            onChange={(e, value) => setSelectedDepartment(value)}
            renderInput={(params) => (
              <TextField {...params} label="Filter by Department" size="small" />
            )}
            className="filter-item"
          />
          <Autocomplete
            options={jobPositions}
            getOptionLabel={(option) => option.name || ""}
            onChange={(e, value) => setSelectedJobPosition(value)}
            renderInput={(params) => (
              <TextField {...params} label="Filter by Job Position" size="small" />
            )}
            className="filter-item"
          />
          <TextField
            label="Search by Name"
            variant="outlined"
            size="small"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {loading ? (
        <CircularProgress />
      ) : (
        <table {...getTableProps()} className="employee-table">
          <thead>
            {headerGroups.map((headerGroup) => (
              <tr {...headerGroup.getHeaderGroupProps()} key={headerGroup.id}>
                {headerGroup.headers.map((column) => (
                  <th {...column.getHeaderProps()} key={column.id}>
                    {column.render("Header")}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody {...getTableBodyProps()}>
            {rows.map((row) => {
              prepareRow(row);
              return (
                <tr {...row.getRowProps()} key={row.id}>
                  {row.cells.map((cell) => (
                    <td {...cell.getCellProps()} key={cell.column.id}>
                      {cell.render("Cell")}
                    </td>
                  ))}
                </tr>
              );
            })}
          </tbody>
        </table>
      )}

      <div className="pagination">
        <div className="page-controls">
          <input
            type="number"
            value={tempPageInput}
            onChange={(e) => setTempPageInput(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                const enteredPage = parseInt(tempPageInput, 10) - 1;
                if (enteredPage >= 0 && enteredPage < pageCount) {
                  fetchStaffData(enteredPage, itemsPerPage);
                }
              }
            }}
            className="page-input"
          />
          <span>of {pageCount} pages</span>
          <button
            onClick={() => fetchStaffData(currentPage - 1, itemsPerPage)}
            disabled={currentPage === 0}
            className="page-button"
          >
            {"<"}
          </button>
          <button
            onClick={() => fetchStaffData(currentPage + 1, itemsPerPage)}
            disabled={currentPage === pageCount - 1}
            className="page-button"
          >
            {">"}
          </button>
        </div>
        <div className="items-per-page">
          <Select
            value={itemsPerPage}
            onChange={(e) => setItemsPerPage(e.target.value)}
            size="small"
          >
            {[5, 10, 15, 20].map((size) => (
              <MenuItem key={size} value={size}>
                {size}
              </MenuItem>
            ))}
          </Select>
          <span>items per page</span>
        </div>
      </div>
    </div>
  );
};

export default CheckpointEvaluation;
