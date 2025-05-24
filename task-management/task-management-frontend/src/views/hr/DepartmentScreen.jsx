import React, {useEffect, useMemo, useRef, useState} from "react";
import {usePagination, useTable} from "react-table";
import {request} from "@/api";
import {Button, IconButton, MenuItem, Select, TextField,} from "@mui/material";
import AddDepartmentModal from "./modals/AddDepartmentModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import {CSVLink} from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
import "@/assets/css/DepartmentTable.css";
import deleteIcon from "@/assets/icons/delete.svg";
import editIcon from "@/assets/icons/edit.svg";
import toast from "react-hot-toast";

const DepartmentTable = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [tempPageInput, setTempPageInput] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [selectedDepartment, setSelectedDepartment] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteDepartment, setDeleteDepartment] = useState(null);
  const [dropdownVisible, setDropdownVisible] = useState(null);
  const dropdownRefs = useRef([]);

  const fetchData = async (pageIndex, pageSize, searchValue) => {
    const payload = {
      departmentCode: null,
      departmentName: searchValue || null,
      status: "ACTIVE",
      page: pageIndex,
      pageSize: pageSize,
    };

    try {
      request(
        "get",
        "/departments",
        (res) => {
          const { data: departments, meta } = res.data;
          const transformedDepartments = departments.map((dept) => ({
            departmentCode: dept.department_code,
            departmentName: dept.department_name,
            description: dept.description,
            status: dept.status,
          }));

          setData(transformedDepartments);
          setPageCount(meta.page_info.total_page);
          setCurrentPage(meta.page_info.page);
          setTempPageInput(meta.page_info.page + 1);
          setLoading(false);
        },
        {
          onError: (err) => console.error("Error fetching data:", err),
        },
        null,
        {
          params: payload
        }
      );
    } catch (error) {
      console.error("Error fetching data:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(0, itemsPerPage, searchTerm);
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
        Header: "Name",
        accessor: "departmentName",
      },
      {
        Header: "Description",
        accessor: "description",
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

  const handleEdit = (department) => {
    setSelectedDepartment(department);
    setOpenModal(true);
    setDropdownVisible(null); 
  };

  const handleOpenDeleteModal = (department) => {
    setDeleteDepartment(department);
    setDeleteModalOpen(true);
    setDropdownVisible(null); 
  };

  const handleDelete = () => {
    if (!deleteDepartment) return;

    request(
      "delete",
      `/departments/${deleteDepartment.departmentCode}`,
      () => {
        fetchData(currentPage, itemsPerPage, searchTerm);
        setDeleteModalOpen(false);
        setDeleteDepartment(null);
        toast.success("Xoá thành công")
      },
      {
        onError: (err) => {
          console.error("Error deleting department:", err);
        },
      }
    );
  };

  const exportPDF = () => {
    const doc = new jsPDF();
    doc.text("Departments", 20, 10);
    doc.autoTable({
      head: [["#", "Name", "Description"]],
      body: data.map((row, index) => [
        currentPage * itemsPerPage + index + 1,
        row.departmentName,
        row.description,
      ]),
    });
    doc.save("Departments.pdf");
  };

  const handlePageInputChange = (e) => {
    setTempPageInput(e.target.value);
  };

  const handlePageInputKeyDown = (e) => {
    if (e.key === "Enter") {
      const enteredPage = parseInt(tempPageInput, 10) - 1;

      if (enteredPage >= 0 && enteredPage < pageCount) {
        fetchData(enteredPage, itemsPerPage, searchTerm);
      } else if (enteredPage < 0) {
        fetchData(0, itemsPerPage, searchTerm);
      } else {
        fetchData(pageCount - 1, itemsPerPage, searchTerm);
      }
    }
  };

  const handlePageChange = (newPage) => {
    fetchData(newPage, itemsPerPage, searchTerm);
  };

  const handleItemsPerPageChange = (event) => {
    setItemsPerPage(event.target.value);
    fetchData(0, event.target.value, searchTerm);
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div className="department-container">
      <div className="header">
        <h2>Departments</h2>
        <Button
          variant="contained"
          color="success"
          onClick={() => setOpenModal(true)}
        >
          + Add Department
        </Button>
      </div>
      <AddDepartmentModal
        open={openModal}
        onClose={() => {
          setOpenModal(false);
          setSelectedDepartment(null);
        }}
        onSubmit={() => fetchData(0, itemsPerPage, searchTerm)} // Refresh table
        initialValues={selectedDepartment}
      />
      <DeleteConfirmationModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onSubmit={handleDelete}
        title="Delete Department"
        info={`Are you sure you want to delete the department "${deleteDepartment?.departmentName}"?`}
        cancelLabel="Cancel"
        confirmLabel="Delete"
      />
      <div className="export-search-container">
        <div className="export-buttons">
          <CSVLink data={data} filename={`Departments.csv`}>
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
      <div style={{ maxHeight: "450px", overflowY: "auto" }}>
        <table {...getTableProps()} className="department-table">
          <thead style={{position: "sticky", top: 0, background: "#fff", zIndex: 2}}>
            {headerGroups.map((headerGroup) => (
              <tr {...headerGroup.getHeaderGroupProps()}>
                {headerGroup.headers.map((column) => (
                  <th {...column.getHeaderProps()}>{column.render("Header")}</th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody {...getTableBodyProps()}>
          {rows.map((row) => {
            prepareRow(row);
              return (
                <tr {...row.getRowProps()}>
                  {row.cells.map((cell) => (
                    <td {...cell.getCellProps()}>{cell.render("Cell")}</td>
                  ))}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      <div className="pagination">
        <div className="page-controls">
          <input
            type="number"
            value={tempPageInput}
            onChange={handlePageInputChange}
            onKeyDown={handlePageInputKeyDown}
            className="page-input"
          />
          <span>of {pageCount} pages</span>
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 0}
            className="page-button"
          >
            {"<"}
          </button>
          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === pageCount - 1}
            className="page-button"
          >
            {">"}
          </button>
        </div>
        <div className="items-per-page">
          <Select
            value={itemsPerPage}
            onChange={handleItemsPerPageChange}
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

export default DepartmentTable;