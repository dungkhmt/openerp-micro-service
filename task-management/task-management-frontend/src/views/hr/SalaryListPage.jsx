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
  Divider,
  FormControl,
  Grid,
  IconButton,
  InputAdornment,
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
  Avatar,
  CircularProgress,
} from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import "dayjs/locale/vi";

import { theme } from './theme';
import {request}from "@/api";
import SearchSelect from "@/components/item/SearchSelect";
import Pagination from "@/components/item/Pagination";
import {useDebounce} from "../../hooks/useDebounce";

import EditIcon from "@mui/icons-material/Edit";
import CloseIcon from "@mui/icons-material/Close";
import PeopleAltIcon from '@mui/icons-material/PeopleAlt';
import toast from "react-hot-toast";

dayjs.locale('vi');

const SalaryListPageInternal = () => {
  const navigate = useNavigate();
  const [searchName, setSearchName] = useState("");
  const [selectedDept, setSelectedDept] = useState(null);
  const [selectedPos, setSelectedPos] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [pageCount, setPageCount] = useState(0);
  const [employees, setEmployees] = useState([]);
  const [salaries, setSalaries] = useState({});
  const [loading, setLoading] = useState(true);

  const [editingSalaryInfo, setEditingSalaryInfo] = useState(null);
  const [editForm, setEditForm] = useState({ salary: '', salary_type: 'MONTHLY' });
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const debouncedSearchName = useDebounce(searchName, 500);

  const fetchEmployees = useCallback(async (page = 0, size = itemsPerPage, name = debouncedSearchName, dept = selectedDept, pos = selectedPos) => {
    return new Promise((resolve, reject) => {
      const empPayload = {
        fullname: name || null,
        departmentCode: dept?.department_code || null,
        jobPositionCode: pos?.code || null,
        status: "ACTIVE",
        page,
        pageSize: size
      };
      request("get", "/staffs/details", (res) => {
          const list = (res.data.data || []).map(e => ({ ...e, id: e.staff_code || e.user_login_id }));
          const meta = res.data.meta || {};
          setEmployees(list);
          setPageCount(meta.page_info?.total_page || 0);
          resolve(list);
        }, { onError: (err) => {
            console.error("Lỗi tải danh sách nhân viên:", err);
            toast.error("Không thể tải danh sách nhân viên.");
            setEmployees([]);
            resolve([]);
          }}, null, { params: empPayload }
      );
    });
  }, [itemsPerPage]);


  const fetchSalaries = useCallback(async (userIds) => {
    if (!userIds || userIds.length === 0) {
      setSalaries({});
      return;
    }
    try {
      await request("get", "/salaries", (res) => {
          const data = res.data?.data || [];
          const mapped = {};
          data.forEach((item) => { mapped[item.user_login_id] = item; });
          setSalaries(mapped);
        }, { onError: (err) => {
            console.error("Lỗi tải dữ liệu lương:", err);
            toast.error("Không thể tải dữ liệu lương.");
            setSalaries({});
          }}, null, { params: { userIds: userIds.join(",")} }
      );
    } catch(error) {
      console.error("Ngoại lệ khi tải dữ liệu lương:", error);
      toast.error("Lỗi hệ thống khi tải dữ liệu lương.");
      setSalaries({});
    }
  }, []);

  const handleSearchAndFetchAll = useCallback(async (page = 0, size = itemsPerPage, isInitialLoadOrFilterChange = false) => {
    setLoading(true);
    if (isInitialLoadOrFilterChange) {
      setCurrentPage(0); // Reset page to 0 for initial load or filter change
    } else {
      setCurrentPage(page); // Set page for pagination clicks
    }
    const fetchedEmployees = await fetchEmployees(isInitialLoadOrFilterChange ? 0 : page, size, debouncedSearchName, selectedDept, selectedPos);
    if (fetchedEmployees && fetchedEmployees.length > 0) {
      const userIds = fetchedEmployees.map((e) => e.user_login_id).filter(Boolean);
      if (userIds.length > 0) {
        await fetchSalaries(userIds);
      } else {
        setSalaries({});
      }
    } else {
      setSalaries({});
    }
    setLoading(false);
  }, [fetchEmployees, fetchSalaries, debouncedSearchName, selectedDept, selectedPos]);


  useEffect(() => {
    handleSearchAndFetchAll(0, itemsPerPage, true);
  }, [debouncedSearchName, selectedDept, selectedPos, itemsPerPage, handleSearchAndFetchAll]);

  const handlePageChange = (newPage) => {
    handleSearchAndFetchAll(newPage, itemsPerPage, false);
  };

  const handleItemsPerPageChange = (newValue) => {
    setItemsPerPage(newValue);
  };


  const mapSalaryTypeToVietnamese = (type) => {
    switch (type) {
      case "MONTHLY": return "Theo Tháng";
      case "WEEKLY": return "Theo Tuần";
      case "HOURLY": return "Theo Giờ";
      default: return "-";
    }
  };

  const getStaffCodeDisplay = (code) => {
    if (!code || code === "0") return "-";
    return code;
  };

  const handleEditClick = (employee) => {
    const salaryInfo = salaries[employee.user_login_id];
    setEditForm({
      salary: salaryInfo?.salary || '',
      salary_type: salaryInfo?.salary_type || 'MONTHLY'
    });
    setEditingSalaryInfo({ employee, salaryData: salaryInfo });
    setIsEditModalOpen(true);
  };

  const handleEditModalClose = () => {
    setIsEditModalOpen(false);
    setEditingSalaryInfo(null);
  };

  const handleEditFormChange = (e) => {
    const { name, value } = e.target;
    setEditForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditSubmit = () => {
    if (!editingSalaryInfo || !editingSalaryInfo.employee.user_login_id) return;
    const userId = editingSalaryInfo.employee.user_login_id;

    const salaryValue = String(editForm.salary).replace(/[^\d]/g, "");
    if (salaryValue === "" || isNaN(parseFloat(salaryValue)) || parseFloat(salaryValue) < 0) {
      toast.error("Mức lương không hợp lệ.");
      return;
    }

    const payload = {
      salary: parseFloat(salaryValue),
      salary_type: editForm.salary_type,
    };

    setIsSubmitting(true);
    request("put", `/salaries/${userId}`, () => {
        toast.success("Cập nhật lương thành công!");
        handleEditModalClose();
        handleSearchAndFetchAll(currentPage, itemsPerPage);
      }, { onError: (err) => {
          console.error("Lỗi cập nhật lương:", err);
          toast.error(err.response?.data?.message || "Không thể cập nhật lương.");
        }}, payload
    ).finally(() => {
      setIsSubmitting(false);
    });
  };

  const formatCurrency = (value) => {
    if (value === null || value === undefined || value === '') return "-";
    const num = Number(value);
    if (isNaN(num)) return "-";
    return num.toLocaleString("vi-VN", { style: "currency", currency: "VND" });
  };

  const columns = [
    { id: '#', label: '#', width: 60, minWidth: 60 },
    { id: 'staff_code', label: 'Mã NV', minWidth: 100 },
    { id: 'fullname', label: 'Nhân viên', minWidth: 220 },
    { id: 'department', label: 'Phòng ban', minWidth: 150 },
    { id: 'position', label: 'Chức vụ', minWidth: 150 },
    { id: 'salary', label: 'Lương', minWidth: 130, align: 'right' },
    { id: 'salary_type', label: 'Loại lương', minWidth: 120 },
    { id: 'from_date', label: 'Ngày hiệu lực', minWidth: 130 },
    { id: 'actions', label: 'Sửa', width: 100, minWidth: 100, align: 'center' },
  ];


  return (
    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="vi">
      <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
        <Paper sx={{ p: 2, mb: 2 }}>
          <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
            <Grid item xs={12}>
              <Typography variant="h5" component="h1" sx={{ display: 'flex', alignItems: 'center' }}>
                <PeopleAltIcon sx={{mr:1, color: 'primary.main'}} /> Quản lý Lương Nhân Viên
              </Typography>
            </Grid>
          </Grid>
          <Grid container spacing={2} alignItems="center" sx = {{mt: 0}}>
            <Grid item xs={12} sm={6} md>
              <TextField fullWidth label="Tìm theo tên nhân viên" value={searchName} onChange={(e) => setSearchName(e.target.value)} size="small"/>
            </Grid>
            <Grid item xs={12} sm={6} md>
              <SearchSelect label="Phòng ban" fetchUrl="/departments?status=ACTIVE&pageSize=1000" value={selectedDept} onChange={setSelectedDept} getOptionLabel={(item) => item.department_name} mapFunction={(data) => data.map((d) => ({ ...d, name: d.department_name }))} isClearable size="small"/>
            </Grid>
            <Grid item xs={12} sm={6} md>
              <SearchSelect label="Chức vụ" fetchUrl="/jobs?status=ACTIVE&pageSize=1000" value={selectedPos} onChange={setSelectedPos} getOptionLabel={(item) => item.name} isClearable size="small"/>
            </Grid>
{/*            <Grid item xs={12} sm={6} md="auto">
              <Button variant="contained" onClick={() => { handleSearchAndFetchAll(0, itemsPerPage, true);}} sx={{ height: '40px', width: {xs: '100%', sm: 'auto'} }} disabled={loading}>
                Tìm kiếm
              </Button>
            </Grid>*/}
          </Grid>
        </Paper>

        <Paper sx={{ overflow: 'hidden' }}>
          <TableContainer sx={{ maxHeight: "calc(100vh - 280px)" }} className="custom-scrollbar">
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
                ) : employees.length > 0 ? (
                  employees.map((emp, index) => {
                    const salaryInfo = salaries[emp.user_login_id];
                    return (
                      <TableRow key={emp.id} hover sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }}>
                        <TableCell sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>{currentPage * itemsPerPage + index + 1}</TableCell>
                        <TableCell sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>
                          <Typography variant="body2" sx={{ fontSize: '0.8rem' }}>{getStaffCodeDisplay(emp.staff_code)}</Typography>
                        </TableCell>
                        <TableCell sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>
                          <Stack direction="row" alignItems="center" spacing={1.5}
                                 onClick={() => emp.staff_code && emp.staff_code !== "0" && navigate(`/hr/staff/${emp.staff_code}`)}
                                 sx={{cursor: (emp.staff_code && emp.staff_code !== "0") ? 'pointer' : 'default', '&:hover > .emp-name': {textDecoration: (emp.staff_code && emp.staff_code !== "0") ? 'underline' : 'none', color: (emp.staff_code && emp.staff_code !== "0") ? 'primary.main' : 'inherit'} }}
                          >
                            <Avatar src={`https://ui-avatars.com/api/?name=${encodeURIComponent(emp.fullname || 'N V')}&background=random&size=64&font-size=0.45&bold=true&color=fff`} alt={emp.fullname} sx={{width: 36, height: 36}}/>
                            <Typography variant="body1" className="emp-name">{emp.fullname}</Typography>
                          </Stack>
                        </TableCell>
                        <TableCell sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>{emp.department?.department_name || "-"}</TableCell>
                        <TableCell sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>{emp.job_position?.job_position_name || "-"}</TableCell>
                        <TableCell align="right" sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>
                          {formatCurrency(salaryInfo?.salary)}
                        </TableCell>
                        <TableCell sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>{mapSalaryTypeToVietnamese(salaryInfo?.salary_type)}</TableCell>
                        <TableCell sx={{py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` }}}>{salaryInfo?.from_date ? dayjs(salaryInfo.from_date).format("DD/MM/YYYY") : "-"}</TableCell>
                        <TableCell align="center" sx={{py: 1.2}}>
                          <Tooltip title="Chỉnh sửa lương">
                            <IconButton size="small" onClick={() => handleEditClick(emp)}>
                              <EditIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    );
                  })
                ) : (
                  <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy nhân viên.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác. </Typography></TableCell></TableRow>
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

        {editingSalaryInfo && (
          <Dialog open={isEditModalOpen} onClose={handleEditModalClose} maxWidth="xs" fullWidth PaperProps={{sx:{overflowY:'visible'}}}>
            <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb:1, pt:2, fontSize: '1.15rem' }}> {/* Increased font size */}
              Chỉnh sửa lương
              <IconButton aria-label="đóng" size="small" onClick={handleEditModalClose}> <CloseIcon /> </IconButton>
            </DialogTitle>
            <DialogContent sx={{pt: '10px !important'}}> {/* Maintained compact top padding */}
              <Box display="flex" alignItems="center" gap={2} mb={2}>
                <Avatar src={`https://ui-avatars.com/api/?name=${encodeURIComponent(editingSalaryInfo.employee?.fullname || 'N V')}&background=random&color=fff&size=64&font-size=0.45`} sx={{width: 48, height: 48}}/>
                <Box>
                  <Typography fontWeight="bold">{editingSalaryInfo.employee?.fullname}</Typography>
                  <Typography variant="body2" color="text.secondary">Mã NV: {getStaffCodeDisplay(editingSalaryInfo.employee?.staff_code)}</Typography>
                </Box>
              </Box>
              <Divider sx={{ my: 1.5 }} />
              <Stack spacing={2.5} sx={{mt:2}}>
                <FormControl fullWidth size="small">
                  <InputLabel id="salary-type-label">Loại lương</InputLabel>
                  <Select labelId="salary-type-label" label="Loại lương" name="salary_type" value={editForm.salary_type} onChange={handleEditFormChange} >
                    <MenuItem value="MONTHLY">Theo Tháng</MenuItem>
                    {/*<MenuItem value="WEEKLY">Theo Tuần</MenuItem>*/}
                    <MenuItem value="HOURLY">Theo Giờ</MenuItem>
                  </Select>
                </FormControl>
                <TextField
                  fullWidth
                  name="salary"
                  label="Mức lương"
                  type="text"
                  value={editForm.salary === '' ? '' : Number(String(editForm.salary).replace(/[^\d]/g, "")).toLocaleString("vi-VN")}
                  onChange={(e) => {
                    const rawValue = e.target.value.replace(/[^\d]/g, "");
                    setEditForm((prev) => ({ ...prev, salary: rawValue }));
                  }}
                  InputProps={{
                    endAdornment: <InputAdornment position="end">VNĐ</InputAdornment>,
                  }}
                  size="small"
                />
              </Stack>
            </DialogContent>
            <DialogActions sx={{ px: 3, pb: 2, pt:1 }}>
              <Button onClick={handleEditModalClose} color="inherit"> Hủy </Button>
              <Button variant="contained" onClick={handleEditSubmit} disabled={isSubmitting}>
                {isSubmitting ? <CircularProgress size={24} color="inherit"/> : "Lưu"}
              </Button>
            </DialogActions>
          </Dialog>
        )}
      </Box>
    </LocalizationProvider>
  );
};

const SalaryListPage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <SalaryListPageInternal />
    </ThemeProvider>
  );
};

export default SalaryListPage;
