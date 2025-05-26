import React, {useEffect, useMemo, useState, useCallback} from "react";
import {useNavigate} from "react-router-dom";
import {
  ThemeProvider,
  CssBaseline,
  Box,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  Grid,
  IconButton,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Typography,
  CircularProgress,
} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import "dayjs/locale/vi";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";

import { theme } from './theme';
import {request}from "@/api";
import Pagination from "@/components/item/Pagination";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import {useDebounce} from "../../hooks/useDebounce";

import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import PaymentsIcon from '@mui/icons-material/Payments';
import CloseIcon from "@mui/icons-material/Close";
import toast from "react-hot-toast";

dayjs.extend(isSameOrBefore);
dayjs.locale('vi');

const PayrollListPageInternal = () => {
  const navigate = useNavigate();
  const [searchName, setSearchName] = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");
  const [payrolls, setPayrolls] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [pageCount, setPageCount] = useState(0);
  const [loading, setLoading] = useState(true);

  const [deleteTarget, setDeleteTarget] = useState(null);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);

  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [newPayroll, setNewPayroll] = useState({ name: "", fromDate: null, thruDate: null });
  const [isCreating, setIsCreating] = useState(false);
  const [userMap, setUserMap] = useState({});

  const debouncedSearchName = useDebounce(searchName, 500);

  const fetchUserNames = useCallback(async (userIds) => {
    if (!userIds || userIds.length === 0) return;
    try {
      await request("get", "/staffs/details", (res) => {
        const userMapResult = {};
        (res.data.data || []).forEach(user => {
          userMapResult[user.user_login_id] = user.fullname;
        });
        setUserMap(prevMap => ({ ...prevMap, ...userMapResult }));
      }, {}, null, { params: { userIds: userIds.join(",") } });
    } catch (error) {
      console.error("Lỗi tải tên người dùng:", error);
    }
  }, []);

  const fetchPayrolls = useCallback(async (page = 0, size = itemsPerPage, name = debouncedSearchName, status = statusFilter, isInitialLoadOrFilterChange = false) => {
    setLoading(true);
    const payload = {
      searchName: name || null,
      status: status || null,
      page,
      pageSize: size
    };
    try {
      await request("get", "/payrolls", async (res) => {
        const list = res.data.data || [];
        const meta = res.data.meta?.page_info || {};
        setPayrolls(list.map(item => ({...item, id: item.id || item.payroll_id })));
        setPageCount(meta.total_page || 0);
        if (!isInitialLoadOrFilterChange) {
          setCurrentPage(meta.page || 0);
        } else {
          setCurrentPage(0);
        }

        const userIdsToFetch = [...new Set(list.map(item => item.created_by).filter(id => id && !userMap[id]))];
        if (userIdsToFetch.length > 0) {
          fetchUserNames(userIdsToFetch);
        }
      }, { onError: (err) => {
          console.error("Lỗi tải danh sách kỳ lương:", err);
          toast.error("Không thể tải danh sách kỳ lương.");
          setPayrolls([]);
        }}, null, { params: payload });
    } catch (error) {
      console.error("Ngoại lệ khi tải danh sách kỳ lương:", error);
      toast.error("Lỗi hệ thống khi tải danh sách kỳ lương.");
      setPayrolls([]);
    } finally {
      setLoading(false);
    }
  }, [itemsPerPage, fetchUserNames, userMap]);

  useEffect(() => {
    fetchPayrolls(0, itemsPerPage, debouncedSearchName, statusFilter, true);
  }, [debouncedSearchName, statusFilter, itemsPerPage, fetchPayrolls]);

  const handlePageChange = (newPage) => {
    fetchPayrolls(newPage, itemsPerPage, debouncedSearchName, statusFilter, false);
  };

  const handleItemsPerPageChange = (newValue) => {
    setItemsPerPage(newValue);
  };


  const handleDelete = () => {
    if (!deleteTarget) return;
    request("delete", `/payrolls/${deleteTarget.id}`, () => {
      toast.success(`Đã hủy kỳ lương "${deleteTarget.name}".`);
      const newCurrentPageAfterDelete = (payrolls.length - 1 === itemsPerPage * currentPage && currentPage > 0)
        ? currentPage -1
        : currentPage;

      const itemsOnCurrentPage = payrolls.filter(p => p.status === "ACTIVE").length % itemsPerPage;
      let targetPage = newCurrentPageAfterDelete;
      if(itemsOnCurrentPage === 1 && newCurrentPageAfterDelete > 0 && payrolls.length > 1){
        targetPage = newCurrentPageAfterDelete -1;
      }
      if (payrolls.length -1 === 0 ) targetPage = 0;


      fetchPayrolls(targetPage, itemsPerPage, debouncedSearchName, statusFilter);
      setConfirmDeleteOpen(false);
      setDeleteTarget(null);
    }, {onError: (err) => {
        toast.error(err.response?.data?.message || "Không thể hủy kỳ lương.");
        setConfirmDeleteOpen(false);
      }});
  };

  const handleCreatePayroll = () => {
    if (!newPayroll.name || !newPayroll.fromDate || !newPayroll.thruDate) {
      toast.error("Vui lòng điền đầy đủ thông tin.");
      return;
    }
    const from = dayjs(newPayroll.fromDate);
    const thru = dayjs(newPayroll.thruDate);
    if (!from.isSameOrBefore(thru, "day")) {
      toast.error("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
      return;
    }

    setIsCreating(true);
    const payload = {
      name: newPayroll.name,
      from_date: from.format("YYYY-MM-DD"),
      thru_date: thru.format("YYYY-MM-DD")
    };

    request("post", "/payrolls", () => {
      fetchPayrolls(0, itemsPerPage, debouncedSearchName, statusFilter, true);
      setCreateModalOpen(false);
      setNewPayroll({ name: "", fromDate: null, thruDate: null });
      toast.success("Tạo kỳ lương thành công!");
    }, {onError: (err) => {
        toast.error(err.response?.data?.message || "Không thể tạo kỳ lương.");
      }}, payload).finally(() => setIsCreating(false));
  };

  const renderStatusChip = (status) => {
    const color = status === "ACTIVE" ? "success" : (status === "INACTIVE" ? "default" : "warning");
    const label = status === "ACTIVE" ? "Hoạt động" : (status === "INACTIVE" ? "Đã hủy" : (status || "Chưa rõ"));
    return <Chip label={label} color={color} size="small" sx={{minWidth: 100, justifyContent: 'center'}}/>;
  };

  const columns = [
    { id: '#', label: '#', width: 60, minWidth: 60, align: 'left'},
    { id: 'name', label: 'Tên kỳ lương', minWidth: 250 },
    { id: 'from_date', label: 'Từ ngày', minWidth: 120, align: 'center' },
    { id: 'thru_date', label: 'Đến ngày', minWidth: 120, align: 'center' },
    { id: 'created_by', label: 'Người tạo', minWidth: 180 },
    { id: 'status', label: 'Trạng thái', minWidth: 130, align: 'center' },
    { id: 'actions', label: 'Hủy', width: 100, minWidth:100, align: 'center' },
  ];

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="vi">
      <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
        <Paper sx={{ p: 2, mb: 2 }}>
          <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
            <Grid item xs={12} md="auto">
              <Typography variant="h5" component="h1" sx={{ display: 'flex', alignItems: 'center' }}>
                <PaymentsIcon sx={{mr:1, color: 'primary.main'}}/> Quản lý Kỳ Lương
              </Typography>
            </Grid>
            <Grid item xs={12} md="auto">
              <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => {setNewPayroll({ name: "", fromDate: dayjs(), thruDate: dayjs().add(1, 'month').subtract(1,'day') }); setCreateModalOpen(true);}}>
                Tạo Kỳ Lương Mới
              </Button>
            </Grid>
          </Grid>
          <Grid container spacing={2} alignItems="center" sx={{ mt: 0 }}>
            <Grid item xs={12} sm={6} md={5}>
              <TextField fullWidth label="Tìm theo tên kỳ lương" value={searchName} onChange={(e) => setSearchName(e.target.value)} size="small"/>
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <FormControl fullWidth size="small">
                <InputLabel>Trạng thái</InputLabel>
                <Select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)} label="Trạng thái">
                  <MenuItem value="ACTIVE">Hoạt động</MenuItem>
                  <MenuItem value="INACTIVE">Đã hủy</MenuItem>
                  <MenuItem value=""><em>Tất cả</em></MenuItem>
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </Paper>

        <Paper sx={{ overflow: 'hidden' }}>
          <TableContainer sx={{ maxHeight: "calc(100vh - 280px)" }}>
            <Table stickyHeader size="medium">
              <TableHead>
                <TableRow>
                  {columns.map((column) => (
                    <TableCell
                      key={column.id}
                      align={column.align || 'left'}
                      sx={{
                        bgcolor: (t) => t.palette.mode === 'light' ? t.palette.grey[200] : t.palette.grey[700],
                        color: (t) => t.palette.getContrastText(t.palette.mode === 'light' ? t.palette.grey[200] : t.palette.grey[700]),
                        fontWeight: 'bold',
                        whiteSpace: 'nowrap',
                        width: column.width,
                        minWidth: column.minWidth,
                        py: 1.5,
                        borderBottom: (t) => `1px solid ${t.palette.divider}`,
                        '&:not(:last-child)': {
                          borderRight: (t) => `1px solid ${t.palette.divider}`,
                        }
                      }}
                    >
                      {column.label}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {loading ? (
                  <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <CircularProgress /> <Typography sx={{mt: 1.5}} variant="body1">Đang tải dữ liệu...</Typography> </TableCell></TableRow>
                ) : payrolls.length > 0 ? (
                  payrolls.map((item, index) => (
                    <TableRow key={item.id} hover sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }}>
                      <TableCell sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}>{currentPage * itemsPerPage + index + 1}</TableCell>
                      <TableCell sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}>
                        <Button variant="text" onClick={() => navigate(`${item.id}`)} sx={{textTransform: "none", p:0, justifyContent:'flex-start', fontWeight: 500, color: 'primary.main'}}>
                          {item.name}
                        </Button>
                      </TableCell>
                      <TableCell align="center" sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}>{item.from_date ? dayjs(item.from_date).format("DD/MM/YYYY") : "-"}</TableCell>
                      <TableCell align="center" sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}>{item.thru_date ? dayjs(item.thru_date).format("DD/MM/YYYY") : "-"}</TableCell>
                      <TableCell sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}>{userMap[item.created_by] || item.created_by || "-"}</TableCell>
                      <TableCell align="center" sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}>{renderStatusChip(item.status)}</TableCell>
                      <TableCell align="center" sx={{ py: 1.2 }}>
                        {item.status === "ACTIVE" && (
                          <Tooltip title="Hủy kỳ lương này">
                            <IconButton size="small" onClick={() => { setDeleteTarget(item); setConfirmDeleteOpen(true); }} color="error" >
                              <DeleteIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy kỳ lương nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc tạo kỳ lương mới. </Typography></TableCell></TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
          {(pageCount > 0 && !loading) && (
            <Pagination
              currentPage={currentPage}
              pageCount={pageCount}
              itemsPerPage={itemsPerPage}
              onPageChange={handlePageChange}
              onItemsPerPageChange={handleItemsPerPageChange}
            />
          )}
        </Paper>

        <DeleteConfirmationModal
          open={confirmDeleteOpen}
          onClose={() => setConfirmDeleteOpen(false)}
          onSubmit={handleDelete}
          title="Xác nhận Hủy Kỳ Lương"
          info={`Bạn có chắc chắn muốn hủy kỳ lương "${deleteTarget?.name}" không? Hành động này sẽ chuyển trạng thái thành INACTIVE và không thể hoàn tác.`}
          confirmLabel="Đồng ý Hủy"
          cancelLabel="Không"
        />

        <Dialog open={createModalOpen} onClose={() => setCreateModalOpen(false)} maxWidth="sm" fullWidth PaperProps={{sx:{overflowY:'visible'}}}>
          <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '1.15rem' }}>
            Tạo kỳ lương mới
            <IconButton aria-label="đóng" size="small" onClick={() => setCreateModalOpen(false)}> <CloseIcon /> </IconButton>
          </DialogTitle>
          <DialogContent sx={{ pt: '12px' }}>
            <Stack spacing={2.5} sx={{ alignItems: 'stretch' }}>
              <TextField fullWidth label="Tên kỳ lương" value={newPayroll.name} onChange={(e) => setNewPayroll((prev) => ({ ...prev, name: e.target.value }))} required size="small"/>
              <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="vi">
                <Grid container sx={{ width: '100%' }}> {/* Bỏ spacing ở container */}
                  <Grid item xs={12} sm={6} sx={{ pr: { xs: 0, sm: 1 } }}> {/* Thêm padding-right cho item trái (trên màn hình sm trở lên) */}
                    <DatePicker
                      label="Từ ngày"
                      value={newPayroll.fromDate}
                      onChange={(date) => setNewPayroll((prev) => ({ ...prev, fromDate: date }))}
                      format="DD/MM/YYYY"
                      slotProps={{ textField: { fullWidth: true, required: true, size: 'small' } }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} sx={{ pl: { xs: 0, sm: 1 } , mt: {xs: 2, sm: 0} }}> {/* Thêm padding-left cho item phải (trên màn hình sm trở lên), và margin-top cho xs */}
                    <DatePicker
                      label="Đến ngày"
                      value={newPayroll.thruDate}
                      onChange={(date) => setNewPayroll((prev) => ({ ...prev, thruDate: date }))}
                      format="DD/MM/YYYY"
                      minDate={newPayroll.fromDate || undefined}
                      slotProps={{ textField: { fullWidth: true, required: true, size: 'small' } }}
                    />
                  </Grid>
                </Grid>
              </LocalizationProvider>
            </Stack>
          </DialogContent>
          <DialogActions sx={{px:3, pb:2, pt:2.5}}>
            <Button onClick={() => setCreateModalOpen(false)} color="inherit">Huỷ</Button>
            <Button
              variant="contained"
              onClick={handleCreatePayroll}
              disabled={isCreating || !newPayroll.name || !newPayroll.fromDate || !newPayroll.thruDate || !dayjs(newPayroll.fromDate).isSameOrBefore(dayjs(newPayroll.thruDate), "day")}
            >
              {isCreating ? <CircularProgress size={24} color="inherit"/> : "Tạo"}
            </Button>
          </DialogActions>
        </Dialog>

      </Box>
    </LocalizationProvider>
  );
};

const PayrollListPage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <PayrollListPageInternal />
    </ThemeProvider>
  );
};

export default PayrollListPage;

