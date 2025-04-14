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
import AddCheckpointModal from "./modals/AddCheckpointConfigureModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import { CSVLink } from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
import "@/assets/css/CheckpointConfigureTable.css";
import deleteIcon from "@/assets/icons/delete.svg";
import editIcon from "@/assets/icons/edit.svg";
import { request } from "@/api";
import toast from "react-hot-toast";

const CheckpointConfigureScreen = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [tempPageInput, setTempPageInput] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [selectedCheckpoint, setSelectedCheckpoint] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteCheckpoint, setDeleteCheckpoint] = useState(null);
  const [dropdownVisible, setDropdownVisible] = useState(null);
  const dropdownRefs = useRef([]);

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

  const fetchCheckpoints = async (pageIndex, pageSize, searchValue) => {
    const payload = {
      name: searchValue || null,
      status: "ACTIVE",
      pageable_request: {
        page: pageIndex,
        page_size: pageSize,
      },
    };

    try {
      request(
        "post",
        "/checkpoint/get-all-configure",
        (res) => {
          const { data: checkpoints, meta } = res.data;
          setData(checkpoints || []);
          setPageCount(meta.page_info.total_page);
          setCurrentPage(meta.page_info.page);
          setTempPageInput(meta.page_info.page + 1);
          setLoading(false);
        },
        {
          onError: (err) => console.error("Error fetching checkpoints:", err),
        },
        payload
      );
    } catch (error) {
      console.error("Error fetching checkpoints:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCheckpoints(0, itemsPerPage, searchTerm);
  }, [itemsPerPage, searchTerm]);

  const exportPDF = () => {
    const doc = new jsPDF();
    doc.text("Checkpoint Configurations", 20, 10);
    doc.autoTable({
      head: [["Code", "Name", "Description"]],
      body: data.map((row) => [row.code, row.name, row.description]),
    });
    doc.save("Checkpoint_Configurations.pdf");
  };

  const columns = useMemo(
    () => [
      {
        Header: "#",
        accessor: "index",
        Cell: ({ row }) => currentPage * itemsPerPage + row.index + 1,
      },
      { Header: "Code", accessor: "code" },
      { Header: "Name", accessor: "name" },
      { Header: "Description", accessor: "description" },
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

  const handleEdit = (checkpoint) => {
    setSelectedCheckpoint(checkpoint);
    setOpenModal(true);
    setDropdownVisible(null);
  };

  const handleOpenDeleteModal = (checkpoint) => {
    setDeleteCheckpoint(checkpoint);
    setDeleteModalOpen(true);
    setDropdownVisible(null);
  };

  const handleDelete = async () => {
    if (!deleteCheckpoint) return;

    try {
      await request(
        "post",
        "/checkpoint/delete-configure",
        () => {
          fetchCheckpoints(currentPage, itemsPerPage, searchTerm);
          setDeleteModalOpen(false);
          setDeleteCheckpoint(null);
          toast.success("Xoá thành công")
        },
        {
          onError: (err) => {
            console.error("Error deleting checkpoint:", err);
          },
        },
        { code: deleteCheckpoint.code }
      );
    } catch (error) {
      console.error("Error deleting checkpoint:", error);
    }
  };

  const handlePageInputKeyDown = (e) => {
    if (e.key === "Enter") {
      const enteredPage = parseInt(tempPageInput, 10) - 1;
      if (enteredPage >= 0 && enteredPage < pageCount) {
        fetchCheckpoints(enteredPage, itemsPerPage, searchTerm);
      }
    }
  };

  const handlePageChange = (newPage) => {
    fetchCheckpoints(newPage, itemsPerPage, searchTerm);
  };

  if (loading) return <CircularProgress />;

  return (
    <div className="checkpoint-management">
      <div className="header">
        <h2>Checkpoint Configurations</h2>
        <Button
          variant="contained"
          color="success"
          onClick={() => setOpenModal(true)}
        >
          + Add Configuration
        </Button>
      </div>

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

      <table {...getTableProps()} className="checkpoint-table">
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
              fetchCheckpoints(0, e.target.value, searchTerm);
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

      <AddCheckpointModal
        open={openModal}
        onClose={() => {
          setOpenModal(false);
          setSelectedCheckpoint(null);
        }}
        onSubmit={() => fetchCheckpoints(0, itemsPerPage, searchTerm)}
        initialValues={selectedCheckpoint}
      />

      <DeleteConfirmationModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onSubmit={handleDelete}
        title="Delete Checkpoint"
        info={`Are you sure you want to delete the checkpoint "${deleteCheckpoint?.name}"?`}
        cancelLabel="Cancel"
        confirmLabel="Delete"
      />
    </div>
  );
};

export default CheckpointConfigureScreen;
