import React, {useEffect, useMemo, useRef, useState, useCallback} from "react";
import {usePagination, useTable} from "react-table";
import {request} from "@/api";

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

// Icons
import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from "@mui/icons-material/MoreVert";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';

// Custom Components & Hooks
import AddCheckpointConfigureModal from "./modals/AddCheckpointConfigureModal";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal.jsx";
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce";

import {CSVLink} from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
import toast from "react-hot-toast";

const CheckpointConfigureScreenInternal = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const [openModal, setOpenModal] = useState(false);
  const [selectedCheckpoint, setSelectedCheckpoint] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleteCheckpoint, setDeleteCheckpoint] = useState(null);

  const [menuAnchorEl, setMenuAnchorEl] = useState(null);
  const [currentMenuConfigId, setCurrentMenuConfigId] = useState(null); // Dùng ID của config

  const fetchData = useCallback(async (pageIndex, pageSize, searchValue, isInitialLoadOrFilterChange = false) => {
    setLoading(true);
    const payload = {
      name: searchValue || null, // Tìm kiếm theo tên cấu hình
      status: "ACTIVE", // Hoặc trạng thái bạn muốn lọc
      page: pageIndex,
      pageSize: pageSize,
    };
    try {
      await request("get", "/checkpoints/configures", (res) => {
        const { data: configsFromApi, meta } = res.data;
        const transformedConfigs = (configsFromApi || []).map((config, index) => ({
          ...config,
          // API của bạn có thể trả về 'id' hoặc 'code' làm định danh chính
          // Đảm bảo 'id' là duy nhất và là string
          id: String(config.id || config.code || `__fallback_config_id_${index}`),
        }));
        setData(transformedConfigs);
        setPageCount(meta?.page_info?.total_page || 0);
        if (!isInitialLoadOrFilterChange) {
          setCurrentPage(meta?.page_info?.page || 0);
        }
      }, {
        onError: (err) => { console.error("Lỗi khi tải cấu hình checkpoint:", err); toast.error("Không thể tải danh sách cấu hình checkpoint."); },
      }, null, { params: payload });
    } catch (error) {
      console.error("Ngoại lệ khi tải cấu hình checkpoint:", error);
      toast.error("Lỗi hệ thống khi tải danh sách cấu hình checkpoint.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    setCurrentPage(0);
    fetchData(0, itemsPerPage, debouncedSearchTerm, true);
  }, [itemsPerPage, debouncedSearchTerm, fetchData]);

  const handleMenuOpen = useCallback((event, configId) => {
    setMenuAnchorEl(event.currentTarget);
    setCurrentMenuConfigId(configId);
  }, []);

  const handleMenuClose = useCallback(() => {
    setMenuAnchorEl(null);
    setCurrentMenuConfigId(null);
  }, []);

  const handleEditFromMenu = useCallback(() => {
    if (currentMenuConfigId) {
      const configToEdit = data.find(config => config.id === currentMenuConfigId);
      if (configToEdit) {
        setSelectedCheckpoint(configToEdit); // Giữ tên state cũ nếu modal dùng
        setOpenModal(true);
      }
    }
    handleMenuClose();
  }, [currentMenuConfigId, data]);

  const handleOpenDeleteModalFromMenu = useCallback(() => {
    if (currentMenuConfigId) {
      const configToDelete = data.find(config => config.id === currentMenuConfigId);
      if (configToDelete) {
        setDeleteCheckpoint(configToDelete); // Giữ tên state cũ nếu modal dùng
        setDeleteModalOpen(true);
      }
    }
    handleMenuClose();
  }, [currentMenuConfigId, data]);

  const columns = useMemo(
    () => [
      { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, width: 60, disableSortBy: true },
      { Header: "Mã Cấu Hình", accessor: "code", minWidth: 120 },
      { Header: "Tên Cấu Hình", accessor: "name", minWidth: 250 },
      { Header: "Mô tả", accessor: "description", Cell: ({ value }) => ( <Tooltip title={String(value || '')} placement="bottom-start"> <Typography variant="body1" noWrap sx={{ maxWidth: 350, overflow: 'hidden', textOverflow: 'ellipsis' }}> {value || '-'} </Typography> </Tooltip> ), minWidth: 300 },
      {
        Header: "Hành động",
        id: 'actions',
        Cell: ({ row }) => {
          const configId = row.original.id;
          if (!configId) return <Typography variant="caption" color="error">ID không hợp lệ</Typography>;
          return (
            <Box sx={{ textAlign: 'center' }}>
              <Tooltip title="Tùy chọn">
                <IconButton aria-label="menu-hanh-dong" onClick={(event) => handleMenuOpen(event, configId)}>
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
    // API để xóa config có thể cần `id` hoặc `code`
    // Giả sử API dùng `id` (đã được chuẩn hóa trong fetchData)
    if (!deleteCheckpoint || !deleteCheckpoint.id) { toast.error("Không tìm thấy ID cấu hình để xóa."); return; }
    const configIdToDelete = deleteCheckpoint.id;

    // Endpoint có thể là /checkpoints/configures/{id} hoặc body chứa id/code
    // Dựa theo code gốc, endpoint là /checkpoints/configures và body chứa code
    // Nếu API thực sự dùng code trong body cho DELETE:
    const codeToDelete = deleteCheckpoint.code;
    if (!codeToDelete) { toast.error("Không tìm thấy mã cấu hình để xóa."); return; }

    request("delete", `/checkpoints/configures`, () => { // Code gốc dùng body cho DELETE
        toast.success("Cấu hình Checkpoint đã được xóa thành công.");
        const newCurrentPage = (data.filter(d => d.id !== configIdToDelete).length % itemsPerPage === 0 && currentPage > 0 && Math.floor((data.length -1) / itemsPerPage) < currentPage) ? currentPage - 1 : currentPage;
        if (newCurrentPage !== currentPage) setCurrentPage(newCurrentPage);
        fetchData(newCurrentPage, itemsPerPage, debouncedSearchTerm);
        setDeleteModalOpen(false); setDeleteCheckpoint(null);
      }, { onError: (err) => { console.error("Lỗi khi xóa cấu hình:", err); toast.error(err.response?.data?.message || "Không thể xóa cấu hình checkpoint."); } },
      { code: codeToDelete } // Truyền code trong body nếu API yêu cầu
    );
  };

  const exportPDF = () => {
    const doc = new jsPDF();
    const headerColor = theme?.palette?.primary?.main || [30, 136, 229];
    doc.setFont("helvetica", "bold"); doc.text("Danh sách Cấu hình Checkpoint", 14, 20); doc.setFont("helvetica", "normal");
    doc.autoTable({
      startY: 30,
      headStyles: { fillColor: headerColor, textColor: "#ffffff", fontStyle: 'bold' },
      head: [["Mã", "Tên Cấu Hình", "Mô tả"]],
      body: data.map(row => [
        row.code,
        row.name,
        row.description,
      ]),
      styles: { font: "helvetica", fontSize: 10 },
    });
    doc.save("DanhSachCauHinhCheckpoint.pdf");
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    fetchData(newPage, itemsPerPage, debouncedSearchTerm, false);
  };

  const handleItemsPerPageChange = (newValue) => {
    setItemsPerPage(newValue);
  };

  const csvData = useMemo(() => {
    if (loading || !data || data.length === 0) return [];
    return data.map(row => ({
      "Mã Cấu Hình": row.code,
      "Tên Cấu Hình": row.name,
      "Mô tả": row.description,
    }));
  }, [data, loading]);

  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item> <Typography variant="h4" component="h1"> Cấu hình Checkpoint </Typography> </Grid>
          <Grid item> <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => { setSelectedCheckpoint(null); setOpenModal(true); }}> Thêm mới </Button> </Grid>
        </Grid>
      </Paper>

      <AddCheckpointConfigureModal open={openModal} onClose={() => { setOpenModal(false); setSelectedCheckpoint(null); }} onSubmit={() => { const targetPage = selectedCheckpoint ? currentPage : 0; if (!selectedCheckpoint) setCurrentPage(0); fetchData(targetPage, itemsPerPage, debouncedSearchTerm, !selectedCheckpoint); }} initialValues={selectedCheckpoint} />
      {deleteCheckpoint && (<DeleteConfirmationModal open={deleteModalOpen} onClose={() => setDeleteModalOpen(false)} onSubmit={handleDelete} title="Xác nhận xóa Cấu hình" info={`Bạn có chắc chắn muốn xóa cấu hình "${deleteCheckpoint?.name}" không?`} cancelLabel="Hủy" confirmLabel="Xóa" /> )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="flex-end" wrap="wrap">
          <Grid item xs={12} md="auto">
            <Stack direction={{xs: "column", sm: "row"}} spacing={1.5} useFlexGap>
              {(csvData && csvData.length > 0) ? (
                <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />}>
                  <CSVLink data={csvData} filename={`DanhSachCauHinhCheckpoint.csv`} style={{ textDecoration: 'none', color: 'inherit' }}> Xuất CSV </CSVLink>
                </Button>
              ) : ( <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} disabled> Xuất CSV </Button> )}
              <Button variant="outlined" size="small" startIcon={<PictureAsPdfIcon />} onClick={exportPDF}> Xuất PDF </Button>
            </Stack>
          </Grid>
          <Grid item xs={12} sm>
            <TextField fullWidth label="Tìm kiếm theo tên cấu hình" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} variant="outlined" size="small"/>
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{ overflow: 'hidden' }}>
        <TableContainer sx={{ maxHeight: "calc(100vh - 320px)" }}> {/* Tăng chiều cao bảng */}
          <Table {...getTableProps()} stickyHeader size="medium">
            <TableHead>
              {headerGroups.map((headerGroup) => (
                <TableRow {...headerGroup.getHeaderGroupProps()}>
                  {headerGroup.headers.map((column) => (
                    <TableCell {...column.getHeaderProps()} align={column.textAlign || (column.id === 'actions' ? 'center' : 'left')}
                               sx={{ bgcolor: (t) => t.palette.mode === 'light' ? t.palette.grey[200] : t.palette.grey[700], color: (t) => t.palette.getContrastText(t.palette.mode === 'light' ? t.palette.grey[200] : t.palette.grey[700]), fontWeight: 'bold', whiteSpace: 'nowrap', width: column.width, minWidth: column.minWidth || (column.id === '#' ? 60 : 120), py: 1.5, borderBottom: (t) => `1px solid ${t.palette.divider}`, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.divider}` } }}
                    > {column.render("Header")} </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableHead>
            <TableBody {...getTableBodyProps()}>
              {loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <CircularProgress /> <Typography sx={{mt: 1.5}} variant="body1">Đang tải dữ liệu...</Typography> </TableCell></TableRow>
              ) : !loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy cấu hình checkpoint nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc thêm cấu hình mới. </Typography> </TableCell></TableRow>
              ) : (
                rows.map((row) => { prepareRow(row); return ( <TableRow {...row.getRowProps()} hover sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }} > {row.cells.map((cell) => ( <TableCell {...cell.getCellProps()} align={cell.column.textAlign || (cell.column.id === 'actions' ? 'center' : 'left')} sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}> {cell.render("Cell")} </TableCell> ))} </TableRow> ); })
              )}
            </TableBody>
          </Table>
        </TableContainer>
        {(pageCount > 0 && !loading) && ( <Pagination currentPage={currentPage} pageCount={pageCount} itemsPerPage={itemsPerPage} onPageChange={handlePageChange} onItemsPerPageChange={handleItemsPerPageChange} /> )}
      </Paper>

      <Menu
        anchorEl={menuAnchorEl}
        open={Boolean(menuAnchorEl)}
        onClose={handleMenuClose}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        PaperProps={{ elevation: 0, sx: { overflow: 'visible', filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))', mt: 1.5, '& .MuiAvatar-root': { width: 32, height: 32, ml: -0.5, mr: 1, }, '&::before': { content: '""', display: 'block', position: 'absolute', top: 0, right: 14, width: 10, height: 10, bgcolor: 'background.paper', transform: 'translateY(-50%) rotate(45deg)', zIndex: 0, }, }, }}
      >
        <MuiMenuItem onClick={handleEditFromMenu} sx={{ gap: 1 }}> <EditIcon fontSize="small" /> Sửa </MuiMenuItem>
        <MuiMenuItem onClick={handleOpenDeleteModalFromMenu} sx={{ gap: 1, color: 'error.main' }}> <DeleteIcon fontSize="small" /> Xóa </MuiMenuItem>
      </Menu>
    </Box>
  );
};

const CheckpointConfigureScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <CheckpointConfigureScreenInternal />
    </ThemeProvider>
  );
};

export default CheckpointConfigureScreen;
