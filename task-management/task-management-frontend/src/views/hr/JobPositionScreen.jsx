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

import AddJobPositionModal from "./modals/AddJobPositionModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal.jsx";
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce";
import { exportToPDF } from "./fileExportUtils";

import {CSVLink}from "react-csv";
import toast from "react-hot-toast";
import dayjs from "dayjs";

const JobPositionScreenInternal = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const [openModal, setOpenModal] = useState(false);
  const [selectedJob, setSelectedJob] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteJob, setDeleteJob] = useState(null);

  const [menuAnchorEl, setMenuAnchorEl] = useState(null);
  const [currentMenuJobId, setCurrentMenuJobId] = useState(null);

  const fetchData = useCallback(async (pageIndex, pageSize, searchValue, isInitialLoadOrFilterChange = false) => {
    setLoading(true);
    const payload = { name: searchValue || null, status: "ACTIVE", page: pageIndex, pageSize: pageSize };
    try {
      await request("get", "/jobs", (res) => {
        const { data: jobsFromApi, meta } = res.data;
        const transformedJobs = (jobsFromApi || []).map((job, index) => ({
          id: String(job.code || `__fallback_job_id_${index}`),
          code: job.code,
          name: job.name,
          description: job.description,
        }));
        setData(transformedJobs);
        setPageCount(meta?.page_info?.total_page || 0);
        if (!isInitialLoadOrFilterChange) {
          setCurrentPage(meta?.page_info?.page || 0);
        } else {
          setCurrentPage(0);
        }
      }, {
        onError: (err) => { console.error("Lỗi khi tải dữ liệu vị trí:", err); toast.error("Không thể tải danh sách vị trí công việc."); },
      }, null, { params: payload });
    } catch (error) {
      console.error("Ngoại lệ khi tải dữ liệu vị trí:", error);
      toast.error("Lỗi hệ thống khi tải danh sách vị trí công việc.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData(0, itemsPerPage, debouncedSearchTerm, true);
  }, [itemsPerPage, debouncedSearchTerm, fetchData]);

  const handleMenuOpen = useCallback((event, jobId) => {
    setMenuAnchorEl(event.currentTarget);
    setCurrentMenuJobId(jobId);
  }, []);

  const handleMenuClose = useCallback(() => {
    setMenuAnchorEl(null);
    setCurrentMenuJobId(null);
  }, []);

  const handleEditFromMenu = useCallback(() => {
    if (currentMenuJobId) {
      const jobToEdit = data.find(job => job.id === currentMenuJobId);
      if (jobToEdit) {
        setSelectedJob(jobToEdit);
        setOpenModal(true);
      }
    }
    handleMenuClose();
  }, [currentMenuJobId, data, handleMenuClose]);

  const handleOpenDeleteModalFromMenu = useCallback(() => {
    if (currentMenuJobId) {
      const jobToDelete = data.find(job => job.id === currentMenuJobId);
      if (jobToDelete) {
        setDeleteJob(jobToDelete);
        setDeleteModalOpen(true);
      }
    }
    handleMenuClose();
  }, [currentMenuJobId, data, handleMenuClose]);

  const columns = useMemo(
    () => [
      { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, width: 60, disableSortBy: true, id: 'stt' },
      { Header: "Tên vị trí", accessor: "name", minWidth: 200, id: 'name' },
      {
        Header: "Mô tả",
        accessor: "description",
        Cell: ({ value }) => (
          <Typography
            variant="body1"
            sx={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}
          >
            {value || '-'}
          </Typography>
        ),
        minWidth: 250,
        id: 'description'
      },
      {
        Header: "Hành động",
        id: 'actions',
        Cell: ({ row }) => {
          const jobId = row.original.id;
          if (!jobId) return <Typography variant="caption" color="error">ID không hợp lệ</Typography>;
          return (
            <Box sx={{ textAlign: 'center' }}>
              <Tooltip title="Tùy chọn">
                <IconButton aria-label="menu-hanh-dong" onClick={(event) => handleMenuOpen(event, jobId)}>
                  <MoreVertIcon />
                </IconButton>
              </Tooltip>
            </Box>
          );
        },
        width: 100, minWidth: 100, disableSortBy: true,
      },
    ],
    [currentPage, itemsPerPage, handleMenuOpen]
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable({ columns, data, manualPagination: true, pageCount: pageCount, initialState: { pageIndex: currentPage }, }, usePagination );

  const handleDelete = () => {
    if (!deleteJob || !deleteJob.code) { toast.error("Không tìm thấy mã vị trí để xóa."); return; }
    const jobCodeToDelete = deleteJob.code;
    request("delete", `/jobs/${jobCodeToDelete}`, () => {
        toast.success("Vị trí công việc đã được xóa thành công.");
        const newCurrentPage = (data.filter(d => d.code !== jobCodeToDelete).length % itemsPerPage === 0 && currentPage > 0 && Math.floor((data.length -1) / itemsPerPage) < currentPage) ? currentPage - 1 : currentPage;
        fetchData(newCurrentPage, itemsPerPage, debouncedSearchTerm, newCurrentPage === 0 && data.length - 1 === 0);
        setDeleteModalOpen(false); setDeleteJob(null);
      }, { onError: (err) => { console.error("Lỗi khi xóa vị trí:", err); toast.error(err.response?.data?.message || "Không thể xóa vị trí công việc.");}}
    );
  };

  const pdfExportColumnsDefinition = useMemo(() => [
    { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1 },
    { Header: "Tên vị trí", accessor: "name" },
    { Header: "Mô tả", accessor: "description" }
  ], [currentPage, itemsPerPage]);

  const handleExportPDF = () => {
    exportToPDF({
      data: data,
      columns: pdfExportColumnsDefinition,
      title: "Danh sách Vị trí Công việc",
      fileName: `DSViTriCongViec_${dayjs().format("YYYYMMDD")}.pdf`,
      themePalette: theme.palette,
      customColumnWidths: {
        0: { cellWidth: 30 },
        1: { cellWidth: 150 },
        2: { cellWidth: 'auto' }
      }
    });
  };

  const csvHeaders = useMemo(() => [
    { label: "#", key: "stt_export" },
    { label: "Tên vị trí", key: "name" },
    { label: "Mô tả", key: "description" }
  ], []);

  const csvPreparedData = useMemo(() => {
    if (loading || !data || data.length === 0) return [];
    return data.map((row, index) => ({
      stt_export: currentPage * itemsPerPage + index + 1,
      name: row.name,
      description: row.description,
    }));
  }, [data, loading, currentPage, itemsPerPage]);

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
          <Grid item> <Typography variant="h4" component="h1"> Quản lý Vị trí Công việc </Typography> </Grid>
          <Grid item> <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => { setSelectedJob(null); setOpenModal(true); }}> Thêm mới </Button> </Grid>
        </Grid>
      </Paper>

      <AddJobPositionModal
        open={openModal}
        onClose={() => { setOpenModal(false); setSelectedJob(null); }}
        onSubmit={() => {
          const targetPage = selectedJob ? currentPage : 0;
          fetchData(targetPage, itemsPerPage, debouncedSearchTerm, !selectedJob);
        }}
        initialData={selectedJob}
        titleProps={{sx: modalTitleStyle}}
      />
      {deleteJob && ( <DeleteConfirmationModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onSubmit={handleDelete}
        title="Xác nhận xóa Vị trí Công việc"
        info={`Bạn có chắc chắn muốn xóa vị trí "${deleteJob?.name}" không?`}
        cancelLabel="Hủy"
        confirmLabel="Xóa"
        titleProps={{sx: modalTitleStyle}}
      /> )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item xs={12} md="auto"> <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} useFlexGap>
            {(csvPreparedData && csvPreparedData.length > 0 && !loading) ? (
              <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />}>
                <CSVLink data={csvPreparedData} headers={csvHeaders} filename={`DSViTriCongViec_${dayjs().format("YYYYMMDD")}.csv`} style={{ textDecoration: 'none', color: 'inherit' }}>
                  Xuất CSV
                </CSVLink>
              </Button>
            ) : (
              <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} disabled> Xuất CSV </Button>
            )}
            <Button variant="outlined" size="small" startIcon={<PictureAsPdfIcon />} onClick={handleExportPDF} disabled={loading || data.length === 0}> Xuất PDF </Button> </Stack> </Grid>
          <Grid item xs={12} sm>
            <TextField
              fullWidth
              label="Tìm kiếm theo tên vị trí"
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
              ) : ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy vị trí công việc nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc thêm vị trí mới. </Typography> </TableCell></TableRow> )}
            </TableBody>
          </Table>
        </TableContainer>
        {(pageCount > 0 && !loading && data.length > 0 )&& ( <Pagination currentPage={currentPage} pageCount={pageCount} itemsPerPage={itemsPerPage} onPageChange={handlePageChange} onItemsPerPageChange={handleItemsPerPageChange} /> )}
      </Paper>

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
    </Box>
  );
};

const JobPositionScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <JobPositionScreenInternal />
    </ThemeProvider>
  );
};

export default JobPositionScreen;