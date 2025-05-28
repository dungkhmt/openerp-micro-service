import React, {useEffect, useMemo, useState, useCallback} from "react";
import {usePagination, useTable} from "react-table";
import {request}from "@/api";

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
  Typography
} from "@mui/material";
import { theme } from './theme';

import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from "@mui/icons-material/MoreVert";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';

import AddDepartmentModal from "./modals/AddDepartmentModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal.jsx";
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce";
import { exportToPDF } from "./fileExportUtils";
import dayjs from 'dayjs';
import {CSVLink}from "react-csv";
import toast from "react-hot-toast";

// Import state quản lý quyền scope
import { useScopePermissionState, fetchPermittedScopes } from "../../state/scopePermissionState"; // Điều chỉnh đường dẫn nếu cần

const DEPARTMENT_ADMIN_SCOPE = "SCOPE_DEPARTMENT_ADMIN";

const DepartmentScreenInternal = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const [openModal, setOpenModal] = useState(false);
  const [selectedDepartment, setSelectedDepartment] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteDepartment, setDeleteDepartment] = useState(null);

  const [menuAnchorEl, setMenuAnchorEl] = useState(null);
  const [currentMenuDepartmentId, setCurrentMenuDepartmentId] = useState(null);

  // State quyền scope
  const scopeState = useScopePermissionState();
  const { permittedScopeIds, isFetched: scopesFetched, isFetching: scopesFetching } = scopeState.get();

  const canAdminDepartments = useMemo(() => {
    return scopesFetched && permittedScopeIds.has(DEPARTMENT_ADMIN_SCOPE);
  }, [permittedScopeIds, scopesFetched]);

  useEffect(() => {
    // Fetch quyền scope khi component mount
    if (!scopesFetched && !scopesFetching) {
      fetchPermittedScopes();
    }
  }, [scopesFetched, scopesFetching]);

  const fetchData = useCallback(async (pageIndex, pageSize, searchValue, isInitialLoadOrFilterChange = false) => {
    setLoading(true);
    const payload = {
      departmentName: searchValue || null,
      status: "ACTIVE",
      page: pageIndex,
      pageSize: pageSize,
    };
    try {
      await request("get", "/departments", (res) => {
        const { data: departmentsFromApi, meta } = res.data;
        const transformedDepartments = (departmentsFromApi || []).map((dept, index) => ({
          id: String(dept.department_code || `__fallback_dept_id_${index}`),
          departmentCode: dept.department_code,
          departmentName: dept.department_name,
          description: dept.description,
          status: dept.status,
        }));
        setData(transformedDepartments);
        setPageCount(meta?.page_info?.total_page || 0);
        if (!isInitialLoadOrFilterChange) {
          setCurrentPage(meta?.page_info?.page || 0);
        } else {
          setCurrentPage(0);
        }
      }, {
        onError: (err) => { console.error("Lỗi khi tải dữ liệu phòng ban:", err); toast.error("Không thể tải danh sách phòng ban."); },
      }, null, { params: payload });
    } catch (error) {
      console.error("Ngoại lệ khi tải dữ liệu phòng ban:", error);
      toast.error("Lỗi hệ thống khi tải danh sách phòng ban.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData(0, itemsPerPage, debouncedSearchTerm, true);
  }, [itemsPerPage, debouncedSearchTerm, fetchData]);


  const handleMenuOpen = useCallback((event, departmentId) => {
    setMenuAnchorEl(event.currentTarget);
    setCurrentMenuDepartmentId(departmentId);
  }, []);

  const handleMenuClose = useCallback(() => {
    setMenuAnchorEl(null);
    setCurrentMenuDepartmentId(null);
  }, []);

  const handleEditFromMenu = useCallback(() => {
    if (currentMenuDepartmentId) {
      const departmentToEdit = data.find(dept => dept.id === currentMenuDepartmentId);
      if (departmentToEdit) {
        setSelectedDepartment(departmentToEdit);
        setOpenModal(true);
      }
    }
    handleMenuClose();
  }, [currentMenuDepartmentId, data, handleMenuClose]);

  const handleOpenDeleteModalFromMenu = useCallback(() => {
    if (currentMenuDepartmentId) {
      const departmentToDelete = data.find(dept => dept.id === currentMenuDepartmentId);
      if (departmentToDelete) {
        setDeleteDepartment(departmentToDelete);
        setDeleteModalOpen(true);
      }
    }
    handleMenuClose();
  }, [currentMenuDepartmentId, data, handleMenuClose]);

  const columns = useMemo(
    () => {
      const baseColumns = [
        { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, width: 60, disableSortBy: true, id: 'stt' },
        { Header: "Tên phòng ban", accessor: "departmentName", minWidth: 200, id: 'name' },
        {
          Header: "Mô tả",
          accessor: "description",
          Cell: ({ value }) => (
            <Typography
              variant="body1"
              sx={{
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}
            >
              {value || '-'}
            </Typography>
          ),
          minWidth: 250,
          id: 'description'
        },
      ];

      if (canAdminDepartments) {
        baseColumns.push({
          Header: "Hành động",
          id: 'actions',
          Cell: ({ row }) => {
            const departmentId = row.original.id;
            if (!departmentId) return <Typography variant="caption" color="error">ID không hợp lệ</Typography>;
            return (
              <Box sx={{ textAlign: 'center' }}>
                <Tooltip title="Tùy chọn">
                  <IconButton aria-label="menu-hanh-dong" onClick={(event) => handleMenuOpen(event, departmentId)} >
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
    [currentPage, itemsPerPage, handleMenuOpen, canAdminDepartments]
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable( { columns, data, manualPagination: true, pageCount: pageCount, initialState: { pageIndex: currentPage }, }, usePagination );

  const handleDelete = () => {
    if (!deleteDepartment || !deleteDepartment.departmentCode) { toast.error("Không tìm thấy mã phòng ban để xóa."); return; }
    const departmentCodeToDelete = deleteDepartment.departmentCode;
    request( "delete", `/departments/${departmentCodeToDelete}`, () => {
        toast.success("Phòng ban đã được xóa thành công.");
        const newCurrentPage = (data.filter(d => d.departmentCode !== departmentCodeToDelete).length % itemsPerPage === 0 && currentPage > 0 && Math.floor((data.length -1) / itemsPerPage) < currentPage) ? currentPage - 1 : currentPage;

        fetchData(newCurrentPage, itemsPerPage, debouncedSearchTerm, newCurrentPage === 0 && data.length -1 === 0);
        setDeleteModalOpen(false); setDeleteDepartment(null);
      }, { onError: (err) => { console.error("Lỗi khi xóa phòng ban:", err); toast.error(err.response?.data?.message || "Không thể xóa phòng ban."); } }
    );
  };

  const handleExportPDF = () => {
    const pdfColumnsToExport = columns.filter(col => col.id !== 'actions' && col.id !== 'stt').map(col => ({ Header: col.Header, accessor: col.accessor }));
    const dataToExport = data.map((row, index) => ({
      ...row,
      stt_export: currentPage * itemsPerPage + index + 1,
    }));

    exportToPDF({
      data: dataToExport,
      columns: [
        { Header: "#", accessor: "stt_export"},
        ...pdfColumnsToExport
      ],
      title: "Danh sách Phòng Ban",
      fileName: `DanhSachPhongBan_${dayjs().format("YYYYMMDD")}.pdf`,
      themePalette: theme.palette,
      customColumnWidths: {
        0: { cellWidth: 30 },
        1: { cellWidth: 120 },
        2: { cellWidth: 'auto'}
      }
    });
  };

  const csvHeaders = [
    { label: "#", key: "stt" },
    { label: "Tên phòng ban", key: "departmentName" },
    { label: "Mô tả", key: "description" }
  ];

  const csvPreparedData = useMemo(() => {
    if (loading || !data || data.length === 0) return [];
    return data.map((row, index) => ({
      stt: currentPage * itemsPerPage + index + 1,
      departmentName: row.departmentName,
      description: row.description,
    }));
  }, [data, currentPage, itemsPerPage, loading]);


  const handlePageChange = (newPage) => {
    fetchData(newPage, itemsPerPage, debouncedSearchTerm, false);
  };

  const handleItemsPerPageChange = (newValue) => {
    setItemsPerPage(newValue);
  };

  const modalTitleStyle = { fontSize: '1.15rem', fontWeight: 600 };


  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item> <Typography variant="h4" component="h1"> Quản lý Phòng Ban </Typography> </Grid>
          {canAdminDepartments && (
            <Grid item> <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => { setSelectedDepartment(null); setOpenModal(true); }}> Thêm mới </Button> </Grid>
          )}
        </Grid>
      </Paper>

      {canAdminDepartments && openModal && (
        <AddDepartmentModal
          open={openModal}
          onClose={() => { setOpenModal(false); setSelectedDepartment(null); }}
          onSubmit={() => {
            const targetPage = selectedDepartment ? currentPage : 0;
            fetchData(targetPage, itemsPerPage, debouncedSearchTerm, !selectedDepartment);
          }}
          initialData={selectedDepartment}
          titleProps={{sx: modalTitleStyle}}
        />
      )}
      {canAdminDepartments && deleteDepartment && (
        <DeleteConfirmationModal
          open={deleteModalOpen}
          onClose={() => setDeleteModalOpen(false)}
          onSubmit={handleDelete}
          title="Xác nhận xóa Phòng Ban"
          info={`Bạn có chắc chắn muốn xóa phòng ban "${deleteDepartment?.departmentName}" không?`}
          cancelLabel="Hủy"
          confirmLabel="Xóa"
          titleProps={{sx: modalTitleStyle}}
        />
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item xs={12} md="auto">
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} useFlexGap>
              {(csvPreparedData && csvPreparedData.length > 0 && !loading) ? (
                <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} >
                  <CSVLink data={csvPreparedData} headers={csvHeaders} filename={`DanhSachPhongBan_${dayjs().format("YYYYMMDD")}.csv`} style={{ textDecoration: 'none', color: 'inherit' }}>
                    Xuất CSV
                  </CSVLink>
                </Button>
              ) : (
                <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} disabled>
                  Xuất CSV
                </Button>
              )}
              <Button variant="outlined" size="small" startIcon={<PictureAsPdfIcon />} onClick={handleExportPDF} disabled={loading || data.length === 0}> Xuất PDF </Button>
            </Stack>
          </Grid>
          <Grid item xs={12} sm>
            <TextField
              fullWidth
              label="Tìm kiếm theo tên phòng ban"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              size="small"
            />
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{ overflow: 'hidden' }}>
        <TableContainer sx={{ maxHeight: "calc(100vh - 330px)" }} className="custom-scrollbar">
          <Table {...getTableProps()} stickyHeader size="medium">
            <TableHead>
              {headerGroups.map((headerGroup) => (
                <TableRow {...headerGroup.getHeaderGroupProps()}>
                  {headerGroup.headers.map((column) => (
                    <TableCell
                      {...column.getHeaderProps()}
                      align={column.id === 'actions' || column.id === 'stt' ? 'center' : 'left'}
                      sx={{
                        bgcolor: (t) => t.palette.mode === 'light' ? t.palette.grey[100] : t.palette.grey[700],
                        color: (t) => t.palette.getContrastText(t.palette.mode === 'light' ? t.palette.grey[100] : t.palette.grey[700]),
                        fontWeight: '600',
                        whiteSpace: 'nowrap',
                        width: column.width,
                        minWidth: column.minWidth || (column.id === 'stt' ? 60 : 150),
                        py: 1.25,
                        borderBottom: (t) => `1px solid ${t.palette.divider}`,
                        '&:not(:last-child)': {
                          borderRight: (t) => `1px solid ${t.palette.divider}`,
                        }
                      }}
                    >
                      {column.render("Header")}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableHead>
            <TableBody {...getTableBodyProps()}>
              {loading ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <CircularProgress /> <Typography sx={{mt: 1.5}} variant="body1">Đang tải dữ liệu...</Typography> </TableCell></TableRow>
              ) : rows.length > 0 ? (
                rows.map((row) => { prepareRow(row); return ( <TableRow {...row.getRowProps()} hover sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }} > {row.cells.map((cell) => ( <TableCell {...cell.getCellProps()} align={cell.column.id === 'actions' || cell.column.id === 'stt' ? 'center' : 'left'} sx={{ py: 1, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}> {cell.render("Cell")} </TableCell> ))} </TableRow> ); })
              ) : ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy phòng ban nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc thêm phòng ban mới. </Typography> </TableCell></TableRow> )}
            </TableBody>
          </Table>
        </TableContainer>
        {(pageCount > 0 && !loading && data.length > 0) && ( <Pagination currentPage={currentPage} pageCount={pageCount} itemsPerPage={itemsPerPage} onPageChange={handlePageChange} onItemsPerPageChange={handleItemsPerPageChange} /> )}
      </Paper>

      {canAdminDepartments && (
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

const DepartmentScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <DepartmentScreenInternal />
    </ThemeProvider>
  );
};

export default DepartmentScreen;