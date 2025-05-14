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
import MoreVertIcon from "@mui/icons-material/MoreVert";
import Autocomplete from "@mui/material/Autocomplete";
import { CSVLink } from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
import "@/assets/css/EmployeeTable.css";
import deleteIcon from "@/assets/icons/delete.svg";
import editIcon from "@/assets/icons/edit.svg";
import { request } from "@/api";
import { useNavigate  } from "react-router-dom";
import Pagination from "@/components/item/Pagination";
import toast from "react-hot-toast";


const EmployeeManagement = () => {
  const navigate = useNavigate ();
  const [data, setData] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [tempPageInput, setTempPageInput] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDepartment, setSelectedDepartment] = useState(null);
  const [selectedJobPosition, setSelectedJobPosition] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteEmployee, setDeleteEmployee] = useState(null);
  const [dropdownVisible, setDropdownVisible] = useState(null);
  const dropdownRefs = useRef([]);
  const [viewMode, setViewMode] = useState("table");

  const fetchEmployees = async (pageIndex, pageSize, searchValue) => {
    const payload = {
      fullname: searchValue || null,
      departmentCode: selectedDepartment?.department_code || null,
      jobPositionCode: selectedJobPosition?.code || null,
      status: "ACTIVE",
      page: pageIndex,
      pageSize: pageSize,
    };

    try {
      request(
        "get",
        "/staffs/details",
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
        null,
        {params: payload}
      );
    } catch (error) {
      console.error("Error fetching employees:", error);
      setLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      request(
        "get",
        "/departments",
        (res) => {
          setDepartments(res.data.data || []);
        },
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
        "get",
        "/jobs",
        (res) => {
          setJobPositions(res.data.data || []);
        },
        { onError: (err) => console.error("Error fetching job positions:", err) },
        {}
      );
    } catch (error) {
      console.error("Error fetching job positions:", error);
    }
  };

  useEffect(() => {
    fetchEmployees(0, itemsPerPage, searchTerm);
    fetchDepartments();
    fetchJobPositions();
  }, [itemsPerPage, searchTerm, selectedDepartment, selectedJobPosition]);

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (
        dropdownVisible !== null &&
        dropdownRefs.current[dropdownVisible] &&
        !dropdownRefs.current[dropdownVisible].contains(event.target)
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
          <div
            className="employee-name-cell"
            onClick={() => navigate(`/hr/staff/${row.original.staff_code}`)}
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
                  e.stopPropagation();
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
                  onClick={(e) => e.stopPropagation()}
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
        "delete",
        `/staffs/${deleteEmployee.staff_code}`,
        () => {
          fetchEmployees(currentPage, itemsPerPage, searchTerm);
          setDeleteModalOpen(false);
          setDeleteEmployee(null);
          toast.success("Xoá thành công");
        },
        {
          onError: (err) => {
            console.error("Error deleting employee:", err);
          },
        }
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
            <TableRowsIcon
              color={viewMode === "table" ? "primary" : "inherit"}
            />
          </IconButton>
          <IconButton onClick={() => setViewMode("card")}>
            <GridViewIcon
              color={viewMode === "card" ? "primary" : "inherit"}
            />
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
            <Button
              variant="contained"
              className="export-button csv-button"
            >
              Export CSV
            </Button>
          </CSVLink>
          <Button
            variant="contained"
            className="export-button pdf-button"
            onClick={exportPDF}
          >
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
            className="search-bar"
          />
        </div>
      </div>


      {viewMode === "table" ? (
        <div style={{ maxHeight: "450px", overflowY: "auto" }}>
          <table {...getTableProps()} className="employee-table">
            <thead style={{position: "sticky", top: 0, background: "#fff", zIndex: 2}}>
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
        </div>
      ) : (
        <div className="card-view">
          {data.map((employee, index) => (
            <div
              className="employee-card"
              key={employee.staff_code}
              style={{
                position: "relative",
                cursor: "pointer",
              }}
              onClick={() => navigate.push(`/employee/${employee.staff_code}`)}
            >
              <img 
                src={`https://ui-avatars.com/api/?name=${encodeURIComponent(
                employee.fullname
                )}&background=random&size=100`}
                alt="Avatar" 
                className="employee-avatar" 
              />
              <h3>{employee.fullname}</h3>
              <p>{employee.department?.department_name || "No Department"}</p>
              <p>{employee.job_position?.job_position_name || "No Position"}</p>
              <div
                className="card-actions"
                ref={(ref) => (dropdownRefs.current[index] = ref)}
              >
                <IconButton
                  onClick={(e) => {
                    e.stopPropagation();
                    setDropdownVisible(
                      dropdownVisible === index ? null : index
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
                {dropdownVisible === index && (
                  <div
                    style={{
                      position: "absolute",
                      top: "36px",
                      right: "8px", 
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
                        e.stopPropagation(); 
                        handleEdit(employee); 
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
                      onClick={(e) => {
                        e.stopPropagation(); 
                        handleOpenDeleteModal(employee); 
                      }}
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

      <Pagination
        currentPage={currentPage}
        pageCount={pageCount}
        itemsPerPage={itemsPerPage}
        onPageChange={(page) => fetchEmployees(page, itemsPerPage, searchTerm)}
        onItemsPerPageChange={(size) => {
          setItemsPerPage(size);
          fetchEmployees(0, size, searchTerm);
        }}
      />
    </div>
  );
};

export default EmployeeManagement;
