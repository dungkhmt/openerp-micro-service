import React, {useEffect, useMemo, useRef, useState, useCallback} from "react";
import {usePagination, useTable} from "react-table";
import {useNavigate} from "react-router-dom";
import {request} from "@/api";

// MUI Components
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
import { theme } from './theme'; // Đường dẫn tới file theme.js của bạn

// Icons
import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from "@mui/icons-material/MoreVert";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import EventNoteIcon from '@mui/icons-material/EventNote'; // Icon cho kỳ checkpoint

// Custom Components & Hooks
import AddPeriodModal from "./modals/AddPeriodModal"; // Điều chỉnh đường dẫn nếu cần
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal.jsx"; // Điều chỉnh đường dẫn nếu cần
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce"; // Điều chỉnh đường dẫn nếu cần

// Libraries
import {CSVLink} from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
import toast from "react-hot-toast";
import dayjs from "dayjs"; // Để định dạng ngày tháng

// import "@/assets/css/CheckpointPeriodTable.css"; // Loại bỏ import CSS

const CheckpointPeriodScreenInternal = () => {
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const [openModal, setOpenModal] = useState(false);
  const [selectedPeriod, setSelectedPeriod] = useState(null); // Dùng cho cả edit và delete
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  // const [deletePeriod, setDeletePeriod] = useState(null); // Không cần state riêng cho delete nếu selectedPeriod đủ

  const [menuAnchorEl, setMenuAnchorEl] = useState(null);
  const [currentMenuPeriodId, setCurrentMenuPeriodId] = useState(null);

  const handleRowClick = useCallback((period) => {
    navigate(`/hr/checkpoint/evaluation`, { state: { period } }); // Điều chỉnh route nếu cần
  }, [navigate]);

  const fetchData = useCallback(async (pageIndex, pageSize, searchValue, isInitialLoadOrFilterChange = false) => {
    setLoading(true);
    const payload = {
      name: searchValue || null,
      status: "ACTIVE",
      page: pageIndex,
      pageSize: pageSize,
    };
    try {
      await request("get", "/checkpoints/periods", (res) => {
        const { data: periodsFromApi, meta } = res.data;
        const transformedPeriods = (periodsFromApi || []).map((period, index) => ({
          ...period,
          id: String(period.id || `__fallback_period_id_${index}`),
          // Định dạng lại ngày tháng nếu cần
          checkpoint_date_formatted: period.checkpoint_date ? dayjs(period.checkpoint_date).format("DD/MM/YYYY") : "Chưa có",
        }));
        setData(transformedPeriods);
        setPageCount(meta?.page_info?.total_page || 0);
        if (!isInitialLoadOrFilterChange) {
          setCurrentPage(meta?.page_info?.page || 0);
        }
      }, {
        onError: (err) => { console.error("Lỗi khi tải kỳ checkpoint:", err); toast.error("Không thể tải danh sách kỳ checkpoint."); },
      }, null, { params: payload });
    } catch (error) {
      console.error("Ngoại lệ khi tải kỳ checkpoint:", error);
      toast.error("Lỗi hệ thống khi tải danh sách kỳ checkpoint.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    setCurrentPage(0);
    fetchData(0, itemsPerPage, debouncedSearchTerm, true);
  }, [itemsPerPage, debouncedSearchTerm, fetchData]);

  const handleMenuOpen = useCallback((event, periodId) => {
    event.stopPropagation(); // Ngăn sự kiện click vào hàng
    setMenuAnchorEl(event.currentTarget);
    setCurrentMenuPeriodId(periodId);
  }, []);

  const handleMenuClose = useCallback(() => {
    setMenuAnchorEl(null);
    setCurrentMenuPeriodId(null);
  }, []);

  const handleEditFromMenu = useCallback(() => {
    if (currentMenuPeriodId) {
      const periodToEdit = data.find(p => p.id === currentMenuPeriodId);
      if (periodToEdit) {
        setSelectedPeriod(periodToEdit);
        setOpenModal(true);
      }
    }
    handleMenuClose();
  }, [currentMenuPeriodId, data]);

  const handleOpenDeleteModalFromMenu = useCallback(() => {
    if (currentMenuPeriodId) {
      const periodToDelete = data.find(p => p.id === currentMenuPeriodId);
      if (periodToDelete) {
        setSelectedPeriod(periodToDelete); // Dùng selectedPeriod cho modal xóa
        setDeleteModalOpen(true);
      }
    }
    handleMenuClose();
  }, [currentMenuPeriodId, data]);

  const columns = useMemo(
    () => [
      { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, width: 60, disableSortBy: true },
      { Header: "Tên Kỳ Checkpoint", accessor: "name", minWidth: 250,
        Cell: ({ row }) => (
          <Typography
            variant="body1"
            sx={{ fontWeight: 500, cursor: 'pointer', '&:hover': { color: 'primary.main', textDecoration: 'underline' } }}
            onClick={() => handleRowClick(row.original)}
          >
            {row.original.name}
          </Typography>
        )
      },
      { Header: "Mô tả", accessor: "description", Cell: ({ value }) => ( <Tooltip title={String(value || '')} placement="bottom-start"> <Typography variant="body1" noWrap sx={{ maxWidth: 350, overflow: 'hidden', textOverflow: 'ellipsis' }}> {value || '-'} </Typography> </Tooltip> ), minWidth: 300 },
      { Header: "Ngày Checkpoint", accessor: "checkpoint_date_formatted", minWidth: 150 }, // Sử dụng trường đã định dạng
      {
        Header: "Hành động",
        id: 'actions',
        Cell: ({ row }) => {
          const periodId = row.original.id;
          if (!periodId) return <Typography variant="caption" color="error">ID không hợp lệ</Typography>;
          return (
            <Box sx={{ textAlign: 'center' }}>
              <Tooltip title="Tùy chọn">
                <IconButton aria-label="menu-hanh-dong" onClick={(event) => handleMenuOpen(event, periodId)}>
                  <MoreVertIcon />
                </IconButton>
              </Tooltip>
            </Box>
          );
        },
        width: 100, minWidth: 100, disableSortBy: true,
      },
    ],
    [currentPage, itemsPerPage, handleMenuOpen, handleRowClick]
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable({ columns, data, manualPagination: true, pageCount: pageCount, initialState: { pageIndex: currentPage }, }, usePagination );

  const handleDelete = () => {
    if (!selectedPeriod || !selectedPeriod.id) { toast.error("Không tìm thấy ID kỳ checkpoint để xóa."); return; }
    const periodIdToDelete = selectedPeriod.id;
    request("delete", `/checkpoints/periods/${periodIdToDelete}`, () => {
        toast.success("Kỳ Checkpoint đã được xóa thành công.");
        const newCurrentPage = (data.filter(d => d.id !== periodIdToDelete).length % itemsPerPage === 0 && currentPage > 0 && Math.floor((data.length -1) / itemsPerPage) < currentPage) ? currentPage - 1 : currentPage;
        if (newCurrentPage !== currentPage) setCurrentPage(newCurrentPage);
        fetchData(newCurrentPage, itemsPerPage, debouncedSearchTerm);
        setDeleteModalOpen(false); setSelectedPeriod(null); // Reset selectedPeriod
      }, { onError: (err) => { console.error("Lỗi khi xóa kỳ checkpoint:", err); toast.error(err.response?.data?.message || "Không thể xóa kỳ checkpoint."); } }
    );
  };

  const exportPDF = () => {
    const doc = new jsPDF();
    const headerColor = theme?.palette?.primary?.main || [30, 136, 229];
    doc.setFont("helvetica", "bold"); doc.text("Danh sách Kỳ Checkpoint", 14, 20); doc.setFont("helvetica", "normal");
    doc.autoTable({
      startY: 30,
      headStyles: { fillColor: headerColor, textColor: "#ffffff", fontStyle: 'bold' },
      head: [["Tên Kỳ", "Mô tả", "Ngày Checkpoint"]],
      body: data.map(row => [
        row.name,
        row.description,
        row.checkpoint_date_formatted, // Sử dụng trường đã định dạng
      ]),
      styles: { font: "helvetica", fontSize: 10 },
    });
    doc.save("DanhSachKyCheckpoint.pdf");
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
      "Tên Kỳ Checkpoint": row.name,
      "Mô tả": row.description,
      "Ngày Checkpoint": row.checkpoint_date_formatted, // Sử dụng trường đã định dạng
    }));
  }, [data, loading]);

  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item> <Typography variant="h4" component="h1">  Kỳ Checkpoint </Typography> </Grid>
          <Grid item> <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => { setSelectedPeriod(null); setOpenModal(true); }}> Thêm mới </Button> </Grid>
        </Grid>
      </Paper>

      <AddPeriodModal open={openModal} onClose={() => { setOpenModal(false); setSelectedPeriod(null); }} onSubmit={() => { const targetPage = selectedPeriod ? currentPage : 0; if (!selectedPeriod) setCurrentPage(0); fetchData(targetPage, itemsPerPage, debouncedSearchTerm, !selectedPeriod); }} initialValues={selectedPeriod} />
      {selectedPeriod && (<DeleteConfirmationModal open={deleteModalOpen} onClose={() => setDeleteModalOpen(false)} onSubmit={handleDelete} title="Xác nhận xóa Kỳ Checkpoint" info={`Bạn có chắc chắn muốn xóa kỳ checkpoint "${selectedPeriod?.name}" không?`} cancelLabel="Hủy" confirmLabel="Xóa" /> )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="flex-end" wrap="wrap">
          <Grid item xs={12} md="auto">
            <Stack direction={{xs: "column", sm: "row"}} spacing={1.5} useFlexGap>
              {(csvData && csvData.length > 0) ? (
                <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />}>
                  <CSVLink data={csvData} filename={`DanhSachKyCheckpoint.csv`} style={{ textDecoration: 'none', color: 'inherit' }}> Xuất CSV </CSVLink>
                </Button>
              ) : ( <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} disabled> Xuất CSV </Button> )}
              <Button variant="outlined" size="small" startIcon={<PictureAsPdfIcon />} onClick={exportPDF}> Xuất PDF </Button>
            </Stack>
          </Grid>
          <Grid item xs={12} sm>
            <TextField fullWidth label="Tìm kiếm theo tên kỳ" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} variant="outlined" size="small"/>
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{ overflow: 'hidden' }}>
        <TableContainer sx={{ maxHeight: "calc(100vh - 320px)" }}>
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
              ) : !loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy kỳ checkpoint nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc thêm kỳ mới. </Typography> </TableCell></TableRow>
              ) : (
                rows.map((row) => { prepareRow(row); return (
                  <TableRow
                    {...row.getRowProps()}
                    hover
                    // onClick={() => handleRowClick(row.original)} // Bỏ onClick ở đây nếu không muốn cả hàng click được
                    // sx={{ cursor: 'pointer' }} // Bỏ cursor pointer nếu không muốn cả hàng click được
                    sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }}
                  >
                    {row.cells.map((cell) => (
                      <TableCell
                        {...cell.getCellProps()}
                        align={cell.column.textAlign || (cell.column.id === 'actions' ? 'center' : 'left')}
                        sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}
                      >
                        {cell.render("Cell")}
                      </TableCell>
                    ))}
                  </TableRow>
                ); })
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

const CheckpointPeriodScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <CheckpointPeriodScreenInternal />
    </ThemeProvider>
  );
};

export default CheckpointPeriodScreen;
