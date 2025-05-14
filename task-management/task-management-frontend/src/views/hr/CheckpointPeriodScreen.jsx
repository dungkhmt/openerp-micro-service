import React, { useState, useEffect, useMemo, useRef } from "react";
import { useTable, usePagination } from "react-table";
import {
  Button,
  IconButton,
  TextField,
  Select,
  MenuItem,
  CircularProgress,
} from "@mui/material";
import AddPeriodModal from "./modals/AddPeriodModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import { useNavigate } from "react-router-dom";
import { CSVLink } from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
import "@/assets/css/CheckpointPeriodTable.css";
import deleteIcon from "@/assets/icons/delete.svg";
import editIcon from "@/assets/icons/edit.svg";
import { request } from "@/api";
import toast from "react-hot-toast";

const CheckpointPeriodScreen = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [tempPageInput, setTempPageInput] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [selectedPeriod, setSelectedPeriod] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deletePeriod, setDeletePeriod] = useState(null);
  const [dropdownVisible, setDropdownVisible] = useState(null);
  const dropdownRefs = useRef([]);
  const navigate = useNavigate();
  
  const handleRowClick = (period) => {
    navigate(`/hr/checkpoint/evaluation`, { state: { period } });
  };

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

  const fetchPeriods = async (pageIndex, pageSize, searchValue) => {
    const payload = {
      name: searchValue || null,
      status: "ACTIVE",
      page: pageIndex,
      pageSize: pageSize,
    };

    try {
      request(
        "get",
        "/checkpoints/periods",
        (res) => {
          const { data: periods, meta } = res.data;
          setData(periods || []);
          setPageCount(meta.page_info.total_page);
          setCurrentPage(meta.page_info.page);
          setTempPageInput(meta.page_info.page + 1);
          setLoading(false);
        },
        {
          onError: (err) => console.error("Error fetching periods:", err),
        },
        null,
        {params: payload}
      );
    } catch (error) {
      console.error("Error fetching periods:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPeriods(0, itemsPerPage, searchTerm);
  }, [itemsPerPage, searchTerm]);

  const exportPDF = () => {
    const doc = new jsPDF();
    doc.text("Checkpoint Periods", 20, 10);
    doc.autoTable({
      head: [["Name", "Description", "Checkpoint Date"]],
      body: data.map((row) => [
        row.name,
        row.description,
        row.checkpoint_date,
      ]),
    });
    doc.save("Checkpoint_Periods.pdf");
  };

  const columns = useMemo(
    () => [
      {
        Header: "#",
        accessor: "index",
        Cell: ({ row }) => currentPage * itemsPerPage + row.index + 1,
      },
      { Header: "Name", accessor: "name" },
      { Header: "Description", accessor: "description" },
      { Header: "Checkpoint Date", accessor: "checkpoint_date" },
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
                style={{ width: "40px", height: "40px" }}
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

  const handleEdit = (period) => {
    setSelectedPeriod(period);
    setOpenModal(true);
    setDropdownVisible(null);
  };

  const handleOpenDeleteModal = (period) => {
    setDeletePeriod(period);
    setDeleteModalOpen(true);
    setDropdownVisible(null);
  };

  const handleDelete = async () => {
    if (!deletePeriod) return;

    try {
      await request(
        "delete",
        `/checkpoints/periods/${deletePeriod.id}`,
        () => {
          fetchPeriods(currentPage, itemsPerPage, searchTerm);
          setDeleteModalOpen(false);
          setDeletePeriod(null);
          toast.success("Xoá thành công")
        },
        {
          onError: (err) => {
            console.error("Error deleting period:", err);
          },
        }
      );
    } catch (error) {
      console.error("Error deleting period:", error);
    }
  };

  const handlePageInputKeyDown = (e) => {
    if (e.key === "Enter") {
      const enteredPage = parseInt(tempPageInput, 10) - 1;
      if (enteredPage >= 0 && enteredPage < pageCount) {
        fetchPeriods(enteredPage, itemsPerPage, searchTerm);
      }
    }
  };

  const handlePageChange = (newPage) => {
    fetchPeriods(newPage, itemsPerPage, searchTerm);
  };

  if (loading) return <CircularProgress />;

  return (
    <div className="checkpoint-management">
      <div className="header">
        <h2>Checkpoint Periods</h2>
        <Button
          variant="contained"
          color="success"
          onClick={() => setOpenModal(true)}
        >
          + Add Period
        </Button>
      </div>

      <div className="export-search-container">
        <div className="export-buttons">
          <CSVLink data={data} filename="Checkpoint_Periods.csv">
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
      <div style={{maxHeight: "460px", overflowY: "auto"}}>
        <table {...getTableProps()} className="checkpoint-table">
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
                <tr
                  {...row.getRowProps()}
                  key={row.id}
                  onClick={() => handleRowClick(row.original)}
                  style={{ cursor: "pointer" }}
                >
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

      <div className="pagination">
        <div className="page-controls">
          <input
            type="number"
            value={tempPageInput}
            onChange={(e) => setTempPageInput(e.target.value)}
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
            onChange={(e) => {
              setItemsPerPage(e.target.value);
              fetchPeriods(0, e.target.value, searchTerm);
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

      <AddPeriodModal
        open={openModal}
        onClose={() => {
          setOpenModal(false);
          setSelectedPeriod(null);
        }}
        onSubmit={() => fetchPeriods(0, itemsPerPage, searchTerm)}
        initialValues={selectedPeriod}
      />

      <DeleteConfirmationModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onSubmit={handleDelete}
        title="Delete Period"
        info={`Are you sure you want to delete the period "${deletePeriod?.name}"?`}
        cancelLabel="Cancel"
        confirmLabel="Delete"
      />
    </div>
  );
};

export default CheckpointPeriodScreen;
