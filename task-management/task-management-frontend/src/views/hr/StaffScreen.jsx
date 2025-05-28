// StaffScreen.jsx
import React, {useEffect, useMemo, useState, useCallback} from "react";
import {usePagination, useTable} from "react-table";
import {useNavigate}from "react-router-dom";

import {
  ThemeProvider,
  CssBaseline,
  Box,
  Button,
  CircularProgress,
  Grid,
  IconButton,
  Menu,
  MenuItem as MuiMenuItem,
  Paper,
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
  Autocomplete,
  Card,
  CardContent,
  CardHeader,
} from "@mui/material";
import { theme } from './theme';

import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from "@mui/icons-material/MoreVert";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import GridViewIcon from "@mui/icons-material/GridView";
import TableRowsIcon from "@mui/icons-material/TableRows";

import AddStaffModal from "./modals/AddStaffModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal.jsx";
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce";
import {request}from "@/api";
import { exportToPDF } from "./fileExportUtils";

import {CSVLink}from "react-csv";
import toast from "react-hot-toast";
import dayjs from "dayjs";

// Import state quản lý quyền scope
import { useScopePermissionState, fetchPermittedScopes } from "../../state/scopePermissionState"; // Điều chỉnh đường dẫn nếu cần

const STAFF_ADMIN_SCOPE = "SCOPE_STAFF_ADMIN"; // Định nghĩa scope cho quản lý nhân viên

const StaffScreenInternal = () => {
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const [selectedDepartmentFilter, setSelectedDepartmentFilter] = useState(null);
  const [selectedJobPositionFilter, setSelectedJobPositionFilter] = useState(null);

  const [openModal, setOpenModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteEmployee, setDeleteEmployee] = useState(null);

  const [menuAnchorEl, setMenuAnchorEl] = useState(null);
  const [currentMenuStaffId, setCurrentMenuStaffId] = useState(null);
  const [viewMode, setViewMode] = useState("table");

  // State quyền scope
  const scopeState = useScopePermissionState();
  const { permittedScopeIds, isFetched: scopesFetched, isFetching: scopesFetching } = scopeState.get();

  const canAdminStaff = useMemo(() => {
    return scopesFetched && permittedScopeIds.has(STAFF_ADMIN_SCOPE);
  }, [permittedScopeIds, scopesFetched]);

  useEffect(() => {
    // Fetch quyền scope khi component mount
    if (!scopesFetched && !scopesFetching) {
      fetchPermittedScopes();
    }
  }, [scopesFetched, scopesFetching]);

  const fetchStaffList = useCallback(async (pageIndex, pageSize, searchValue, department, jobPosition, isInitialLoadOrFilterChange = false) => {
    setLoading(true);
    const payload = {
      fullname: searchValue || null,
      departmentCode: department?.department_code || null,
      jobPositionCode: jobPosition?.code || null,
      status: "ACTIVE", // Giả sử bạn chỉ muốn lấy nhân viên đang hoạt động
      page: pageIndex,
      pageSize: pageSize,
    };
    try {
      await request("get", "/staffs/details", (res) => { // Endpoint có thể cần thay đổi nếu khác
        const { data: staffListFromApi, meta } = res.data;
        const transformedStaff = (staffListFromApi || []).map((staff, index) => ({
          ...staff,
          id: String(staff.staff_code || staff.user_login_id || `__fallback_staff_id_${index}`), // Đảm bảo có id duy nhất
        }));
        setData(transformedStaff);
        setPageCount(meta?.page_info?.total_page || 0);
        if (!isInitialLoadOrFilterChange) {
          setCurrentPage(meta?.page_info?.page || 0);
        } else {
          setCurrentPage(0);
        }
      }, {
        onError: (err) => { console.error("Lỗi khi tải danh sách nhân viên:", err); toast.error("Không thể tải danh sách nhân viên."); },
      }, null, { params: payload });
    } catch (error) {
      console.error("Ngoại lệ khi tải danh sách nhân viên:", error);
      toast.error("Lỗi hệ thống khi tải danh sách nhân viên.");
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchFilterData = useCallback(async () => {
    try {
      // Lấy danh sách phòng ban đang hoạt động
      await request("get", "/departments?status=ACTIVE&pageSize=1000", (res) => setDepartments(res.data.data || []), { onError: (err) => console.error("Lỗi tải phòng ban:", err) }, {});
      // Lấy danh sách vị trí công việc đang hoạt động
      await request("get", "/jobs?status=ACTIVE&pageSize=1000", (res) => setJobPositions(res.data.data || []), { onError: (err) => console.error("Lỗi tải vị trí công việc:", err) }, {});
    } catch (error) {
      console.error("Lỗi tải dữ liệu filter:", error);
    }
  }, []);

  useEffect(() => {
    fetchFilterData();
  }, [fetchFilterData]);

  useEffect(() => {
    fetchStaffList(0, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, true);
  }, [itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, fetchStaffList]);

  const handleMenuOpen = useCallback((event, staffId) => {
    setMenuAnchorEl(event.currentTarget);
    setCurrentMenuStaffId(staffId);
  }, []);

  const handleMenuClose = useCallback(() => {
    setMenuAnchorEl(null);
    setCurrentMenuStaffId(null);
  }, []);

  const handleEditFromMenu = useCallback(() => {
    if (currentMenuStaffId) {
      const staffToEdit = data.find(staff => staff.id === currentMenuStaffId);
      if (staffToEdit) {
        setSelectedEmployee(staffToEdit);
        setOpenModal(true);
      }
    }
    handleMenuClose();
  }, [currentMenuStaffId, data, handleMenuClose]);

  const handleOpenDeleteModalFromMenu = useCallback(() => {
    if (currentMenuStaffId) {
      const staffToDelete = data.find(staff => staff.id === currentMenuStaffId);
      if (staffToDelete) {
        setDeleteEmployee(staffToDelete);
        setDeleteModalOpen(true);
      }
    }
    handleMenuClose();
  }, [currentMenuStaffId, data, handleMenuClose]);

  const columns = useMemo(
    () => {
      const baseColumns = [
        { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, width: 40, disableSortBy: true, id: 'stt' },
        { Header: "Mã NV", accessor: "staff_code", minWidth: 80, id: 'staffCode' },
        {
          Header: "Họ và Tên",
          accessor: "fullname",
          minWidth: 220,
          id: 'fullname',
          Cell: ({ row }) => (
            <Box
              onClick={() => navigate(`/hr/staff/${row.original.staff_code}`)}
              sx={{ display: "flex", alignItems: "center", cursor: "pointer", '&:hover': {textDecoration: 'underline', color: 'primary.main'} }}
            >
              <Avatar
                alt={row.original.fullname}
                src={`https://ui-avatars.com/api/?name=${encodeURIComponent(row.original.fullname || 'N V')}&background=random&size=100&font-size=0.5&bold=true&color=fff`}
                sx={{ width: 36, height: 36, mr: 1.5 }}
              />
              <Typography variant="body1" component="span" sx={{fontWeight: 500}}>
                {row.original.fullname}
              </Typography>
            </Box>
          ),
        },
        { Header: "Email", accessor: "email", minWidth: 200, id: 'email' },
        {
          Header: "Phòng Ban",
          accessor: "department.department_name", // Truy cập sâu hơn vào object
          Cell: ({ row }) => row.original.department?.department_name || "Chưa có",
          minWidth: 150,
          id: 'department'
        },
        {
          Header: "Vị Trí",
          accessor: "job_position.job_position_name", // Truy cập sâu hơn vào object
          Cell: ({ row }) => row.original.job_position?.job_position_name || "Chưa có",
          minWidth: 150,
          id: 'jobPosition'
        },
      ];
      if(canAdminStaff) {
        baseColumns.push({
          Header: "Hành động",
          id: 'actions',
          Cell: ({ row }) => {
            const staffId = row.original.id;
            if (!staffId) return <Typography variant="caption" color="error">ID không hợp lệ</Typography>;
            return (
              <Box sx={{ textAlign: 'center' }}>
                <Tooltip title="Tùy chọn">
                  <IconButton aria-label="menu-hanh-dong" onClick={(event) => handleMenuOpen(event, staffId)}>
                    <MoreVertIcon />
                  </IconButton>
                </Tooltip>
              </Box>
            );
          },
          width: 100, minWidth: 100, disableSortBy: true,
        });
      }
      return baseColumns;
    },
    [currentPage, itemsPerPage, handleMenuOpen, navigate, canAdminStaff] // Thêm canAdminStaff vào dependencies
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable({ columns, data, manualPagination: true, pageCount: pageCount, initialState: { pageIndex: currentPage }, }, usePagination );

  const handleDelete = () => {
    if (!deleteEmployee || !deleteEmployee.staff_code) { toast.error("Không tìm thấy mã nhân viên để xóa."); return; }
    const staffCodeToDelete = deleteEmployee.staff_code;
    request("delete", `/staffs/${staffCodeToDelete}`, () => { // Endpoint có thể cần thay đổi
        toast.success("Nhân viên đã được xóa thành công.");
        const newCurrentPage = (data.filter(d => d.staff_code !== staffCodeToDelete).length % itemsPerPage === 0 && currentPage > 0 && Math.floor((data.length -1) / itemsPerPage) < currentPage) ? currentPage - 1 : currentPage;
        fetchStaffList(newCurrentPage, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, newCurrentPage === 0 && data.length - 1 === 0);
        setDeleteModalOpen(false); setDeleteEmployee(null);
      }, { onError: (err) => { console.error("Lỗi khi xóa nhân viên:", err); toast.error(err.response?.data?.message || "Không thể xóa nhân viên."); } }
    );
  };

  const pdfExportColumnsDefinition = useMemo(
    () => [
      { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1 },
      { Header: "Mã NV", accessor: "staff_code" },
      { Header: "Họ và Tên", accessor: "fullname" },
      { Header: "Email", accessor: "email" },
      {
        Header: "Phòng Ban",
        accessor: (row) => row.department?.department_name || "Chưa có"
      },
      {
        Header: "Vị Trí",
        accessor: (row) => row.job_position?.job_position_name || "Chưa có"
      },
    ],
    [currentPage, itemsPerPage]
  );

  const handleExportPDF = () => {
    exportToPDF({
      data: data,
      columns: pdfExportColumnsDefinition,
      title: "Danh sách Nhân viên",
      fileName: `DSNhanVien_${dayjs().format("YYYYMMDD")}.pdf`,
      themePalette: theme.palette,
      customColumnWidths: {
        0: { cellWidth: 30 },
        1: { cellWidth: 70 },
        2: { cellWidth: 120 },
        3: { cellWidth: 130 },
        4: { cellWidth: 100 },
        5: { cellWidth: 100 }
      }
    });
  };

  const csvExportHeaders = useMemo(() => [
    { label: "#", key: "stt_export" },
    { label: "Mã NV", key: "staff_code" },
    { label: "Họ và Tên", key: "fullname" },
    { label: "Email", key: "email" },
    { label: "Phòng Ban", key: "departmentNameCsv" },
    { label: "Vị Trí", key: "jobPositionNameCsv" }
  ], []);

  const csvExportPreparedData = useMemo(() => {
    if (loading || !data || data.length === 0) return [];
    return data.map((row, index) => ({
      stt_export: currentPage * itemsPerPage + index + 1,
      staff_code: row.staff_code || "",
      fullname: row.fullname || "",
      email: row.email || "",
      departmentNameCsv: row.department?.department_name || "Chưa có",
      jobPositionNameCsv: row.job_position?.job_position_name || "Chưa có",
    }));
  }, [data, loading, currentPage, itemsPerPage]);


  const handlePageChange = (newPage) => {
    fetchStaffList(newPage, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, false);
  };

  const handleItemsPerPageChange = (newValue) => {
    setItemsPerPage(newValue);
  };

  const modalTitleStyle = { fontSize: '1.15rem', fontWeight: 600 };


  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item> <Typography variant="h4" component="h1"> Quản lý Nhân viên </Typography> </Grid>
          <Grid item>
            <Stack direction="row" spacing={1}>
              <Tooltip title="Chế độ Bảng">
                <IconButton onClick={() => setViewMode("table")} color={viewMode === 'table' ? 'primary' : 'default'}> <TableRowsIcon /> </IconButton>
              </Tooltip>
              <Tooltip title="Chế độ Thẻ">
                <IconButton onClick={() => setViewMode("card")} color={viewMode === 'card' ? 'primary' : 'default'}> <GridViewIcon /> </IconButton>
              </Tooltip>
              {canAdminStaff && (
                <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => { setSelectedEmployee(null); setOpenModal(true); }}> Thêm mới </Button>
              )}
            </Stack>
          </Grid>
        </Grid>
      </Paper>

      {canAdminStaff && openModal && (
        <AddStaffModal
          open={openModal}
          onClose={() => { setOpenModal(false); setSelectedEmployee(null); }}
          onSubmitSuccess={() => {
            const targetPage = selectedEmployee ? currentPage : 0; // Tải lại trang hiện tại nếu sửa, về trang đầu nếu thêm mới
            fetchStaffList(targetPage, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, !selectedEmployee);
          }}
          initialData={selectedEmployee}
          isEditMode={!!selectedEmployee}
          departments={departments} // Truyền danh sách phòng ban
          jobPositions={jobPositions} // Truyền danh sách vị trí
          titleProps={{sx: modalTitleStyle}}
        />
      )}
      {canAdminStaff && deleteEmployee && deleteModalOpen && (
        <DeleteConfirmationModal
          open={deleteModalOpen}
          onClose={() => setDeleteModalOpen(false)}
          onSubmit={handleDelete}
          title="Xác nhận xóa Nhân viên"
          info={`Bạn có chắc chắn muốn xóa nhân viên "${deleteEmployee?.fullname}" không?`}
          cancelLabel="Hủy"
          confirmLabel="Xóa"
          titleProps={{sx: modalTitleStyle}}
        />
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="flex-end" wrap="wrap">
          <Grid item xs={12} md="auto">
            <Stack direction={{xs: "column", sm: "row"}} spacing={1.5} useFlexGap>
              {(csvExportPreparedData && csvExportPreparedData.length > 0 && !loading) ? (
                <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />}>
                  <CSVLink data={csvExportPreparedData} headers={csvExportHeaders} filename={`DSNhanVien_${dayjs().format("YYYYMMDD")}.csv`} style={{ textDecoration: 'none', color: 'inherit' }}> Xuất CSV </CSVLink>
                </Button>
              ) : ( <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} disabled> Xuất CSV </Button> )}
              <Button variant="outlined" size="small" startIcon={<PictureAsPdfIcon />} onClick={handleExportPDF} disabled={loading || data.length === 0}> Xuất PDF </Button>
            </Stack>
          </Grid>
          <Grid item xs={12} sm={6} md>
            <TextField fullWidth label="Tìm kiếm theo tên" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} variant="outlined" size="small"/>
          </Grid>
          <Grid item xs={12} sm={6} md>
            <Autocomplete
              options={departments}
              getOptionLabel={(option) => option.department_name || ""}
              isOptionEqualToValue={(option, value) => option.department_code === value.department_code}
              onChange={(event, newValue) => setSelectedDepartmentFilter(newValue)}
              value={selectedDepartmentFilter}
              renderInput={(params) => <TextField {...params} label="Lọc theo Phòng Ban" variant="outlined" size="small" />}
              sx={{minWidth: 180}}
              size="small"
            />
          </Grid>
          <Grid item xs={12} sm={6} md>
            <Autocomplete
              options={jobPositions}
              getOptionLabel={(option) => option.name || ""}
              isOptionEqualToValue={(option, value) => option.code === value.code}
              onChange={(event, newValue) => setSelectedJobPositionFilter(newValue)}
              value={selectedJobPositionFilter}
              renderInput={(params) => <TextField {...params} label="Lọc theo Vị Trí" variant="outlined" size="small" />}
              sx={{minWidth: 180}}
              size="small"
            />
          </Grid>
        </Grid>
      </Paper>

      {viewMode === "table" ? (
        <Paper sx={{ overflow: 'hidden' }}>
          <TableContainer sx={{ maxHeight: "calc(100vh - 320px)" }} className="custom-scrollbar"> {/* Điều chỉnh chiều cao nếu cần */}
            <Table {...getTableProps()} stickyHeader size="medium">
              <TableHead>
                {headerGroups.map((headerGroup) => (
                  <TableRow {...headerGroup.getHeaderGroupProps()}>
                    {headerGroup.headers.map((column) => (
                      <TableCell {...column.getHeaderProps()} align={column.id === 'actions' || column.id === 'stt' ? 'center' : 'left'}
                                 sx={{ bgcolor: (t) => t.palette.mode === 'light' ? t.palette.grey[100] : t.palette.grey[700], color: (t) => t.palette.getContrastText(t.palette.mode === 'light' ? t.palette.grey[100] : t.palette.grey[700]), fontWeight: '600', whiteSpace: 'nowrap', width: column.width, minWidth: column.minWidth || (column.id === 'stt' ? 40 : 120), py: 1.25, borderBottom: (t) => `1px solid ${t.palette.divider}`, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.divider}` } }}
                      > {column.render("Header")} </TableCell>
                    ))}
                  </TableRow>
                ))}
              </TableHead>
              <TableBody {...getTableBodyProps()}>
                {loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <CircularProgress /> <Typography sx={{mt: 1.5}} variant="body1">Đang tải dữ liệu...</Typography> </TableCell></TableRow>
                ) : !loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy nhân viên nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc điều chỉnh bộ lọc. </Typography> </TableCell></TableRow>
                ) : (
                  rows.map((row) => { prepareRow(row); return ( <TableRow {...row.getRowProps()} hover sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }} > {row.cells.map((cell) => ( <TableCell {...cell.getCellProps()} align={cell.column.id === 'actions' || cell.column.id === 'stt' ? 'center' : 'left'} sx={{ py: 1, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}> {cell.render("Cell")} </TableCell> ))} </TableRow> ); })
                )}
              </TableBody>
            </Table>
          </TableContainer>
          {(pageCount > 0 && !loading && data.length > 0 )&& ( <Pagination currentPage={currentPage} pageCount={pageCount} itemsPerPage={itemsPerPage} onPageChange={handlePageChange} onItemsPerPageChange={handleItemsPerPageChange} /> )}
        </Paper>
      ) : ( // Chế độ xem Card
        <Paper sx={{ display: 'flex', flexDirection: 'column', height: '100%', overflow: 'hidden', p:1, bgcolor:'transparent', boxShadow:'none' }}>
          <Box sx={{ flexGrow: 1, maxHeight: "calc(100vh - 320px)",overflowY: 'auto', p: 1 }} className="custom-scrollbar">
            {loading && data.length === 0 ? (
              <Box sx={{display: 'flex', justifyContent: 'center', py: 5}}> <CircularProgress /> <Typography sx={{ml: 2}}>Đang tải...</Typography> </Box>
            ) : !loading && data.length === 0 ? (
              <Box sx={{p:3, textAlign: 'center', mt: 0}}>
                <Typography variant="h6">Không tìm thấy nhân viên nào.</Typography>
                <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc điều chỉnh bộ lọc. </Typography>
              </Box>
            ) : (
              <Grid container spacing={2}>
                {data.map((employee) => {
                  return (
                    <Grid item xs={12} sm={6} md={4} lg={3} key={employee.id}>
                      <Card sx={{ display: 'flex', flexDirection: 'column', height: '100%', transition: 'box-shadow .2s ease-in-out', '&:hover': {boxShadow: 3} }}>
                        <CardHeader
                          avatar={
                            <Avatar
                              alt={employee.fullname}
                              src={`https://ui-avatars.com/api/?name=${encodeURIComponent(employee.fullname || 'N V')}&background=random&size=100&font-size=0.5&bold=true&color=fff`}
                              sx={{ width: 48, height: 48 }}
                            />
                          }
                          action={ canAdminStaff && ( // Chỉ hiển thị nút action nếu có quyền
                            <IconButton aria-label="tùy chọn thẻ" onClick={(event) => handleMenuOpen(event, employee.id)}>
                              <MoreVertIcon />
                            </IconButton>
                          )
                          }
                          title={<Typography variant="subtitle1" component="div" noWrap title={employee.fullname} onClick={() => navigate(`/hr/staff/${employee.staff_code}`)} sx={{cursor: 'pointer', '&:hover': {textDecoration: 'underline', color: 'primary.main'}, fontWeight: 600}}>{employee.fullname}</Typography>}
                          subheader={<Typography variant="caption" color="text.secondary" noWrap title={employee.staff_code}>Mã NV: {employee.staff_code}</Typography>}
                          sx={{pb: 0, alignItems: 'flex-start'}}
                        />
                        <CardContent sx={{flexGrow: 1, pt:1}}>
                          <Typography variant="body2" color="text.secondary" gutterBottom noWrap title={employee.email}>
                            <Tooltip title={employee.email || ''} placement="bottom-start" arrow><span>Email: {employee.email || "Chưa có"}</span></Tooltip>
                          </Typography>
                          <Typography variant="body2" color="text.secondary" gutterBottom noWrap title={employee.department?.department_name}>
                            Phòng ban: {employee.department?.department_name || "Chưa có"}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" noWrap title={employee.job_position?.job_position_name}>
                            Vị trí: {employee.job_position?.job_position_name || "Chưa có"}
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                  );
                })}
              </Grid>
            )}
          </Box>
          {(pageCount > 0 && !loading && data.length > 0) && ( <Pagination currentPage={currentPage} pageCount={pageCount} itemsPerPage={itemsPerPage} onPageChange={handlePageChange} onItemsPerPageChange={handleItemsPerPageChange} /> )}
        </Paper>
      )}

      {canAdminStaff && ( // Chỉ hiển thị Menu nếu có quyền
        <Menu
          anchorEl={menuAnchorEl}
          open={Boolean(menuAnchorEl)}
          onClose={handleMenuClose}
          transformOrigin={{ horizontal: 'right', vertical: 'top' }}
          anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          PaperProps={{ elevation: 2, sx: { overflow: 'visible', filter: 'drop-shadow(0px 1px 4px rgba(0,0,0,0.2))', mt: 1.5, '& .MuiAvatar-root': { width: 32, height: 32, ml: -0.5, mr: 1, }, '&::before': { content: '""', display: 'block', position: 'absolute', top: 0, right: 14, width: 10, height: 10, bgcolor: 'background.paper', transform: 'translateY(-50%) rotate(45deg)', zIndex: 0, borderLeft: `1px solid ${theme.palette.divider}`, borderTop: `1px solid ${theme.palette.divider}` }, }, }}
        >
          <MuiMenuItem onClick={handleEditFromMenu} sx={{ gap: 1, fontSize:'0.9rem', color: 'text.secondary' }}> <EditIcon fontSize="small" /> Sửa </MuiMenuItem>
          <MuiMenuItem onClick={handleOpenDeleteModalFromMenu} sx={{ gap: 1, color: 'error.main', fontSize:'0.9rem' }}> <DeleteIcon fontSize="small" /> Xóa </MuiMenuItem>
        </Menu>
      )}
    </Box>
  );
};

const StaffScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <StaffScreenInternal />
    </ThemeProvider>
  );
};

export default StaffScreen;