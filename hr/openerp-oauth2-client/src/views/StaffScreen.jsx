import React, { useState, useEffect, useMemo, useRef } from "react";
import { useTable, usePagination } from "react-table";
import {
  Button,
  Select,
  MenuItem,
  TextField,
  IconButton,
} from "@mui/material";
import AddStaffModal from "./modals/AddStaffModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import GridViewIcon from "@mui/icons-material/GridView";
import TableRowsIcon from "@mui/icons-material/TableRows";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import { CSVLink } from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
import userAvatar from "../assets/img/user.jpg";
import "../assets/css/EmployeeTable.css";
import deleteIcon from "../assets/icons/delete.svg";
import editIcon from "../assets/icons/edit.svg";
import { request } from "../api";

const EmployeeManagement = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [tempPageInput, setTempPageInput] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteEmployee, setDeleteEmployee] = useState(null);
  const [dropdownVisible, setDropdownVisible] = useState(null);
  const dropdownRefs = useRef([]);
  const [viewMode, setViewMode] = useState("table"); // "table" or "card"

  const fetchEmployees = async (pageIndex, pageSize, searchValue) => {
    const payload = {
      fullname: searchValue || null,
      department_code: null,
      job_position_code: null,
      status: "ACTIVE",
      pageable_request: {
        page: pageIndex,
        page_size: pageSize,
      },
    };

    try {
      request(
        "post",
        "/staff/get-all-staff-info",
        (res) => {
          const { data: employees, meta } = res.data;
          setData(employees || []);
          setPageCount(meta.page_info.total_page);
          setCurrentPage(meta.page_info.page);
          setTempPageInput(meta.page_info.page + 1);
          setLoading(false);
        },
        {
          onError: (err) => console.error("Error fetching employees:", err),
        },
        payload
      );
    } catch (error) {
      console.error("Error fetching employees:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmployees(0, itemsPerPage, searchTerm);
  }, [itemsPerPage, searchTerm]);

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (
        dropdownVisible !== null &&
        (!dropdownRefs.current[dropdownVisible] ||
          !dropdownRefs.current[dropdownVisible].contains(event.target))
      ) {
        setDropdownVisible(null);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);
    return () => {
      document.removeEventListener("mousedown", handleOutsideClick);
    };
  }, [dropdownVisible]);

  const columns = useMemo(
    () => [
      {
        Header: "#",
        accessor: "index",
        Cell: ({ row }) => currentPage * itemsPerPage + row.index + 1,
      },
      {
        Header: "Emp Id",
        accessor: "staff_code",
      },
      {
        Header: "Fullname",
        accessor: "fullname",
        Cell: ({ row }) => (
          <div className="employee-name-cell">
            <img
              src={userAvatar}
              alt="Avatar"
              className="employee-avatar"
              style={{ width: "40px", borderRadius: "50%" }}
            />
            <span>{row.original.fullname}</span>
          </div>
        ),
      },
      {
        Header: "Email",
        accessor: "email",
      },
      {
        Header: "Department",
        accessor: "department.department_name",
        Cell: ({ row }) =>
          row.original.department?.department_name || "No Department",
      },
      {
        Header: "Job Position",
        accessor: "job_position.job_position_name",
        Cell: ({ row }) =>
          row.original.job_position?.job_position_name || "No Position",
      },
      {
        Header: "Actions",
        Cell: ({ row }) => {
          const rowIndex = row.index;

          return (
            <div
              style={{ position: "relative" }}
              ref={(ref) => (dropdownRefs.current[rowIndex] = ref)}
            >
              <IconButton
                onClick={(e) => {
                  e.stopPropagation(); // Prevent click from propagating to the document listener
                  setDropdownVisible(
                    dropdownVisible === rowIndex ? null : rowIndex
                  );
                }}
                style={{
                  width: "40px",
                  height: "40px",
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                }}
              >
                <MoreVertIcon />
              </IconButton>
              {dropdownVisible === rowIndex && (
                <div
                  style={{
                    position: "absolute",
                    top: "100%",
                    right: "0",
                    backgroundColor: "#fff",
                    boxShadow: "0px 4px 10px rgba(0, 0, 0, 0.1)",
                    borderRadius: "4px",
                    zIndex: 1000,
                    width: "150px",
                    padding: "8px 0",
                  }}
                >
                  <div
                    onClick={() => handleEdit(row.original)}
                    style={{
                      display: "flex",
                      alignItems: "center",
                      padding: "8px 16px",
                      cursor: "pointer",
                      borderBottom: "1px solid #f0f0f0",
                    }}
                  >
                    <img
                      src={editIcon}
                      alt="Edit"
                      style={{ marginRight: "8px", width: "16px" }}
                    />
                    Edit
                  </div>
                  <div
                    onClick={() => handleOpenDeleteModal(row.original)}
                    style={{
                      display: "flex",
                      alignItems: "center",
                      padding: "8px 16px",
                      cursor: "pointer",
                    }}
                  >
                    <img
                      src={deleteIcon}
                      alt="Delete"
                      style={{ marginRight: "8px", width: "16px" }}
                    />
                    Delete
                  </div>
                </div>
              )}
            </div>
          );
        },
      },
    ],
    [currentPage, itemsPerPage, dropdownVisible]
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
      data,
      manualPagination: true,
      pageCount,
    },
    usePagination
  );

  const handleEdit = (employee) => {
    setSelectedEmployee(employee);
    setOpenModal(true);
    setDropdownVisible(null);
  };

  const handleOpenDeleteModal = (employee) => {
    setDeleteEmployee(employee);
    setDeleteModalOpen(true);
    setDropdownVisible(null);
  };

  const handleDelete = async () => {
    if (!deleteEmployee) return;
  
    try {
      await request(
        "post",
        "/staff/delete-staff",
        () => {
          fetchEmployees(currentPage, itemsPerPage, searchTerm); 
          setDeleteModalOpen(false); 
          setDeleteEmployee(null); 
        },
        {
          onError: (err) => {
            console.error("Error deleting employee:", err);
          },
        },
        { staff_code: deleteEmployee.staff_code } 
      );
    } catch (error) {
      console.error("Error deleting employee:", error);
    }
  };
  

  const exportPDF = () => {
    const doc = new jsPDF();
    doc.text("Employees", 20, 10);
    doc.autoTable({
      head: [["Emp Id", "Fullname", "Email", "Department", "Job Position"]],
      body: data.map((row) => [
        row.staff_code,
        row.fullname,
        row.email,
        row.department?.department_name || "No Department",
        row.job_position?.job_position_name || "No Position",
      ]),
    });
    doc.save("Employees.pdf");
  };

  const handlePageInputChange = (e) => {
    setTempPageInput(e.target.value);
  };

  const handlePageInputKeyDown = (e) => {
    if (e.key === "Enter") {
      const enteredPage = parseInt(tempPageInput, 10) - 1;

      if (enteredPage >= 0 && enteredPage < pageCount) {
        fetchEmployees(enteredPage, itemsPerPage, searchTerm);
      }
    }
  };

  const handlePageChange = (newPage) => {
    fetchEmployees(newPage, itemsPerPage, searchTerm);
  };

  const handleItemsPerPageChange = (event) => {
    setItemsPerPage(event.target.value);
    fetchEmployees(0, event.target.value, searchTerm);
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div className="employee-management">
      <div className="header">
        <h2>Employees</h2>
        <div>
          <IconButton onClick={() => setViewMode("table")}>
            <TableRowsIcon color={viewMode === "table" ? "primary" : "inherit"} />
          </IconButton>
          <IconButton onClick={() => setViewMode("card")}>
            <GridViewIcon color={viewMode === "card" ? "primary" : "inherit"} />
          </IconButton>
          <Button
            variant="contained"
            color="success"
            onClick={() => setOpenModal(true)}
          >
            + Add Employee
          </Button>
        </div>
      </div>
      <AddStaffModal
        open={openModal}
        onClose={() => {
          setOpenModal(false);
          setSelectedEmployee(null);
        }}
        onSubmit={() => fetchEmployees(0, itemsPerPage, searchTerm)}
        initialValues={selectedEmployee}
      />
      <DeleteConfirmationModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onSubmit={handleDelete}
        title="Delete Employee"
        info={`Are you sure you want to delete the employee "${deleteEmployee?.fullname}"?`}
        cancelLabel="Cancel"
        confirmLabel="Delete"
      />

      <div className="export-search-container">
        <div className="export-buttons">
          <CSVLink data={data} filename="Employees.csv">
            <Button variant="contained" color="primary">
              Export CSV
            </Button>
          </CSVLink>
          <Button variant="contained" color="secondary" onClick={exportPDF}>
            Export PDF
          </Button>
        </div>
        <div className="search-bar">
          <TextField
            label="Search by Name"
            variant="outlined"
            size="small"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {viewMode === "table" ? (
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
      ) : (
        <div className="card-view">
          {data.map((employee, index) => (
            <div className="employee-card" key={employee.staff_code} style={{ position: "relative" }}>
              <img src={userAvatar} alt="Avatar" className="employee-avatar" />
              <h3>{employee.fullname}</h3>
              <p>{employee.department?.department_name || "No Department"}</p>
              <p>{employee.job_position?.job_position_name || "No Position"}</p>
              <div className="card-actions">
                <IconButton
                  onClick={(e) => {
                    e.stopPropagation();
            
                    setDropdownVisible(dropdownVisible === index ? null : index);
                  }}
                  ref={(ref) => (dropdownRefs.current[index] = ref)}
                  style={{
                    width: "40px",
                    height: "40px",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                  }}
                >
                  <MoreVertIcon />
                </IconButton>
                {dropdownVisible === index && (
                  <div
                    style={{
                      position: "absolute",
                      top: "36px", // Adjusted height to align better below the icon
                      right: "8px", // Keeps it aligned to the right edge of the card
                      backgroundColor: "#fff",
                      boxShadow: "0px 4px 10px rgba(0, 0, 0, 0.1)",
                      borderRadius: "4px",
                      zIndex: 1000,
                      width: "100px", 
                      padding: "4px 0", 
                    }}
                    onClick={(e) => e.stopPropagation()}
                  >
                    <div
                      onClick={(e) => {
                        e.stopPropagation(); // Prevent dropdown from closing
                        handleEdit(employee); // Open the modal for Edit
                      }}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        padding: "8px 12px", 
                        cursor: "pointer",
                        fontSize: "14px", 
                        borderBottom: "1px solid #f0f0f0",
                      }}
                    >
                      <img
                        src={editIcon}
                        alt="Edit"
                        style={{ marginRight: "8px", width: "16px", height: "19px", marginTop: "10px"}} 
                      />
                      Edit
                    </div>
                    <div
                      onClick={() => handleOpenDeleteModal(employee)}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        padding: "8px 12px", 
                        cursor: "pointer",
                        fontSize: "14px",
                      }}
                    >
                      <img
                        src={deleteIcon}
                        alt="Delete"
                        style={{ marginRight: "8px", width: "16px", height: "19px", marginTop: "10px"}}
                      />
                      Delete
                    </div>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      
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
                  fetchEmployees(enteredPage, itemsPerPage, searchTerm);
                }
              }
            }}
            className="page-input"
          />
          <span>of {pageCount} pages</span>
          <button
            onClick={() => fetchEmployees(currentPage - 1, itemsPerPage, searchTerm)}
            disabled={currentPage === 0}
            className="page-button"
          >
            {"<"}
          </button>
          <button
            onClick={() => fetchEmployees(currentPage + 1, itemsPerPage, searchTerm)}
            disabled={currentPage === pageCount - 1}
            className="page-button"
          >
            {">"}
          </button>
        </div>
        <div className="items-per-page">
          <Select
            value={itemsPerPage}
            onChange={(e) => {
              setItemsPerPage(e.target.value);
              fetchEmployees(0, e.target.value, searchTerm);
            }}
            displayEmpty
            className="items-per-page-select"
          >
            <MenuItem value={5}>5</MenuItem>
            <MenuItem value={10}>10</MenuItem>
            <MenuItem value={15}>15</MenuItem>
            <MenuItem value={20}>20</MenuItem>
          </Select>
          <span>items per page</span>
        </div>
      </div>
    </div>
  );
};

export default EmployeeManagement;
