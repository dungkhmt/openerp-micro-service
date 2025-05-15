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
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Stack
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { request } from "@/api";
import Pagination from "@/components/item/Pagination";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import { useDebounce } from "../../hooks/useDebounce";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import toast from "react-hot-toast";
import "@/assets/css/EmployeeTable.css";

dayjs.extend(isSameOrBefore);

const PayrollListPage = () => {
  const [searchName, setSearchName] = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");
  const [payrolls, setPayrolls] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [pageCount, setPageCount] = useState(0);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [newPayroll, setNewPayroll] = useState({ name: "", fromdate: null, thruDate: null });

  const debouncedSearchName = useDebounce(searchName, 500);

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
          searchName: debouncedSearchName || null,
          status: statusFilter || null,
          page,
          pageSize: size
        }
      }
    );
  };

  useEffect(() => {
    handleSearch();
  }, [debouncedSearchName, statusFilter]);

  const handleDelete = () => {
    if (!deleteTarget) return;
    request("delete", `/payrolls/${deleteTarget.id}`, () => {
      handleSearch(currentPage, itemsPerPage);
      setConfirmOpen(false);
      setDeleteTarget(null);
    });
  };

  const handleCreate = () => {
    if (!newPayroll.name || !newPayroll.fromdate || !newPayroll.thruDate) return;
    const from = dayjs(newPayroll.fromdate);
    const thru = dayjs(newPayroll.thruDate);
    if (!from.isBefore(thru, "day")) {
      toast.error("Từ ngày phải nhỏ hơn đến ngày");
      return;
    }

    const payload = {
      name: newPayroll.name,
      from_date: newPayroll.fromdate,
      thru_date: newPayroll.thruDate
    };

    request("post", "/payrolls", () => {
      handleSearch();
      setCreateModalOpen(false);
      setNewPayroll({ name: "", fromdate: null, thruDate: null });
      toast.success("Tạo kỳ lương thành công");
    }, null, payload);
  };

  const renderStatusChip = (status) => {
    const color = status === "ACTIVE" ? "success" : "default";
    return <Chip label={status} color={color} size="small" />;
  };

  return (
    <Box className="employee-management" sx={{ padding: 3 }}>
      <Grid container alignItems="center" justifyContent="space-between" mb={2}>
        <Typography variant="h5">Danh sách kỳ lương</Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={() => setCreateModalOpen(true)}>
          Tạo mới
        </Button>
      </Grid>

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

      <Dialog open={createModalOpen} onClose={() => setCreateModalOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Tạo kỳ lương mới</DialogTitle>
        <DialogContent>
          <Stack spacing={3} mt={2}>
            <TextField
              fullWidth
              label="Tên kỳ lương"
              value={newPayroll.name}
              onChange={(e) => setNewPayroll((prev) => ({ ...prev, name: e.target.value }))}
            />
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <Stack direction="row" spacing={2}>
                <DatePicker
                  label="Từ ngày"
                  value={newPayroll.fromdate ? dayjs(newPayroll.fromdate) : null}
                  onChange={(date) => setNewPayroll((prev) => ({ ...prev, fromdate: date ? date.format("YYYY-MM-DD") : null }))}
                  format="DD/MM/YYYY"
                  slotProps={{ textField: { fullWidth: true } }}
                />
                <DatePicker
                  label="Đến ngày"
                  value={newPayroll.thruDate ? dayjs(newPayroll.thruDate) : null}
                  onChange={(date) => setNewPayroll((prev) => ({ ...prev, thruDate: date ? date.format("YYYY-MM-DD") : null }))}
                  format="DD/MM/YYYY"
                  minDate={newPayroll.fromdate ? dayjs(newPayroll.fromdate) : undefined}
                  slotProps={{ textField: { fullWidth: true } }}
                />
              </Stack>
            </LocalizationProvider>
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateModalOpen(false)}>Huỷ</Button>
          <Button
            variant="contained"
            onClick={handleCreate}
            disabled={!newPayroll.name || !newPayroll.fromdate || !newPayroll.thruDate || !dayjs(newPayroll.fromdate).isBefore(dayjs(newPayroll.thruDate), "day")}
          >
            Tạo
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PayrollListPage;
