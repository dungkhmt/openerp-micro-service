import React, { useEffect, useState } from "react";
import {
  Box,
  Button,
  Grid,
  TextField,
  Typography,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Chip,
  IconButton
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { request } from "@/api";
import Pagination from "@/components/item/Pagination";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import "@/assets/css/EmployeeTable.css";

const PayrollListPage = () => {
  const [searchName, setSearchName] = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");
  const [payrolls, setPayrolls] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [pageCount, setPageCount] = useState(0);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);

  const handleSearch = async (page = 0, size = itemsPerPage) => {
    request(
      "get",
      "/payrolls",
      (res) => {
        const list = res.data.data || [];
        const meta = res.data.meta.page_info || {};
        setPayrolls(list);
        setCurrentPage(meta.page || 0);
        setPageCount(meta.total_page || 1);
      },
      {},
      null,
      {
        params: {
          searchName: searchName || null,
          status: statusFilter || null,
          page,
          pageSize: size
        }
      }
    );
  };

  useEffect(() => {
    handleSearch();
  }, []);

  const handleDelete = () => {
    if (!deleteTarget) return;
    request("delete", `/payrolls/${deleteTarget.id}`, () => {
      handleSearch(currentPage, itemsPerPage);
      setConfirmOpen(false);
      setDeleteTarget(null);
    });
  };

  const renderStatusChip = (status) => {
    const color = status === "ACTIVE" ? "success" : "default";
    return <Chip label={status} color={color} size="small" />;
  };

  return (
    <Box className="employee-management" sx={{ padding: 3 }}>
      <Typography variant="h5" gutterBottom>Danh sách kỳ lương</Typography>
      <Grid container spacing={2} alignItems="center" marginBottom={2}>
        <Grid item xs={12} md={3}>
          <TextField
            label="Tìm theo tên"
            fullWidth
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
        </Grid>
        <Grid item xs={12} md={2}>
          <FormControl fullWidth>
            <InputLabel>Trạng thái</InputLabel>
            <Select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              label="Trạng thái"
              renderValue={(value) => renderStatusChip(value)}
            >
              <MenuItem value="ACTIVE"><Chip label="ACTIVE" color="success" size="small" /></MenuItem>
              <MenuItem value="INACTIVE"><Chip label="INACTIVE" size="small" /></MenuItem>
            </Select>
          </FormControl>
        </Grid>
        <Grid item xs={12} md={7}>
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
          <thead style={{ position: "sticky", top: 0, background: "#fff", zIndex: 2 }}>
          <tr>
            <th>#</th>
            <th>Tên kỳ lương</th>
            <th>Từ ngày</th>
            <th>Đến ngày</th>
            <th>Người tạo</th>
            <th>Trạng thái</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          {payrolls.map((item, index) => (
            <tr key={item.id}>
              <td>{currentPage * itemsPerPage + index + 1}</td>
              <td>{item.name}</td>
              <td>{item.fromdate ? new Date(item.fromdate).toLocaleDateString("vi-VN") : "-"}</td>
              <td>{item.thru_date ? new Date(item.thru_date).toLocaleDateString("vi-VN") : "-"}</td>
              <td>{item.created_by || "-"}</td>
              <td>{renderStatusChip(item.status)}</td>
              <td>
                {item.status === "ACTIVE" && (
                  <IconButton
                    size="small"
                    onClick={() => {
                      setDeleteTarget(item);
                      setConfirmOpen(true);
                    }}
                    sx={{ color: "red" }}
                  >
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                )}
              </td>
            </tr>
          ))}
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

      <DeleteConfirmationModal
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onSubmit={handleDelete}
        title="Xoá kỳ lương"
        info={`Bạn có chắc chắn muốn xoá kỳ lương "${deleteTarget?.name}"?`}
      />
    </Box>
  );
};

export default PayrollListPage;
