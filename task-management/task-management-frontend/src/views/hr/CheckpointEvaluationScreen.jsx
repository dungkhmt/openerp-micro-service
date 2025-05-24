import React, {useEffect, useMemo, useRef, useState, useCallback} from "react";
import {usePagination, useTable} from "react-table";
import {useNavigate, useLocation} from "react-router-dom";
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
  // Menu, // Không có menu Sửa/Xóa cho từng nhân viên ở đây
  // MenuItem as MuiMenuItem,
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
  Snackbar,
  Alert as MuiAlert, // Đổi tên để tránh xung đột
} from "@mui/material";
import { theme } from './theme'; // Đường dẫn tới file theme.js của bạn

// Icons
// import AddIcon from '@mui/icons-material/Add'; // Không có nút Add ở đây
// import MoreVertIcon from "@mui/icons-material/MoreVert"; // Không có menu ở đây
// import EditIcon from '@mui/icons-material/Edit';
// import DeleteIcon from '@mui/icons-material/Delete';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import GradeIcon from "@mui/icons-material/BorderColor"; // Icon Đánh giá
import AssessmentIcon from '@mui/icons-material/Assessment'; // Icon cho tiêu đề

// Custom Components & Hooks
import GradeModal from "./modals/GradeModal"; // Điều chỉnh đường dẫn nếu cần
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce"; // Điều chỉnh đường dẫn nếu cần

// Libraries
import {CSVLink} from "react-csv";
import jsPDF from "jspdf";
import "jspdf-autotable";
// import toast from "react-hot-toast"; // Mặc dù không thấy dùng trong code gốc, nhưng có thể cần
// import "@/assets/css/CheckpointEvaluation.css"; // Loại bỏ import CSS

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const CheckpointEvaluationScreenInternal = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const periodFromState = location.state?.period;

  const [staffData, setStaffData] = useState([]);
  const [periods, setPeriods] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);

  const [selectedPeriod, setSelectedPeriod] = useState(null);
  const [selectedDepartmentFilter, setSelectedDepartmentFilter] = useState(null);
  const [selectedJobPositionFilter, setSelectedJobPositionFilter] = useState(null);
  const [totalPointsMap, setTotalPointsMap] = useState({});

  const [loading, setLoading] = useState(true);
  const [loadingPoints, setLoadingPoints] = useState(false);

  const [pageCount, setPageCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const [gradeModalOpen, setGradeModalOpen] = useState(false);
  const [selectedStaffForGrading, setSelectedStaffForGrading] = useState(null);

  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "info" });

  // Effect để set selectedPeriod từ state của router (chỉ chạy khi periodFromState thay đổi)
  useEffect(() => {
    // console.log("EFFECT (periodFromState): periodFromState is", periodFromState);
    if (periodFromState) {
      setSelectedPeriod(currentSelectedPeriod => {
        // Chỉ cập nhật nếu chưa có hoặc ID khác để tránh set lại object giống hệt
        if (!currentSelectedPeriod || currentSelectedPeriod.id !== periodFromState.id) {
          // console.log("Setting selectedPeriod from periodFromState:", periodFromState);
          return periodFromState; // periodFromState có thể là object đầy đủ hoặc chỉ chứa ID
        }
        return currentSelectedPeriod;
      });
    }
  }, [periodFromState]);


  const fetchStaffList = useCallback(async (pageIndex, pageSize, searchValue, department, jobPosition, isInitialLoadOrFilterChange = false) => {
    // console.log("fetchStaffList called with:", { pageIndex, pageSize, searchValue, department, jobPosition, isInitialLoadOrFilterChange });
    setLoading(true);
    const payload = {
      fullname: searchValue || null,
      departmentCode: department?.department_code || null,
      jobPositionCode: jobPosition?.code || null,
      status: "ACTIVE",
      page: pageIndex,
      pageSize: pageSize,
    };
    try {
      await request("get", "/staffs", (res) => {
        const { data: staffListFromApi, meta } = res.data;
        const transformedStaff = (staffListFromApi || []).map((staff, index) => ({
          ...staff,
          id: String(staff.user_login_id || staff.staff_code || `__fallback_staff_id_${index}`),
        }));
        setStaffData(transformedStaff);
        setPageCount(meta?.page_info?.total_page || 0);
        if (!isInitialLoadOrFilterChange) {
          setCurrentPage(meta?.page_info?.page || 0);
        }
        // console.log("fetchStaffList success, currentPage (from API if not initial):", meta?.page_info?.page);
      }, {
        onError: (err) => { console.error("Lỗi khi tải danh sách nhân viên:", err); setSnackbar({open: true, message: "Không thể tải danh sách nhân viên.", severity: "error"}); },
      }, null, { params: payload });
    } catch (error) {
      console.error("Ngoại lệ khi tải danh sách nhân viên:", error);
      setSnackbar({open: true, message: "Lỗi hệ thống khi tải danh sách nhân viên.", severity: "error"});
    } finally {
      setLoading(false);
    }
  }, []); // Dependencies rỗng vì các hàm set state không cần đưa vào

  const fetchPeriodsForSelection = useCallback(async () => {
    // console.log("fetchPeriodsForSelection called");
    try {
      await request("get", "/checkpoints/periods?status=ACTIVE&pageSize=1000", (res) => {
        const fetchedPeriods = res.data.data || [];
        setPeriods(fetchedPeriods);
        // console.log("Periods fetched:", fetchedPeriods.length);
        // Sau khi fetch xong list periods, cập nhật selectedPeriod để đảm bảo nó là object đầy đủ
        // nếu nó đã được set từ periodFromState (có thể chỉ là partial object)
        setSelectedPeriod(currentSelectedPeriod => {
          if (currentSelectedPeriod && fetchedPeriods.length > 0) {
            const fullPeriodObjectFromList = fetchedPeriods.find(p => p.id === currentSelectedPeriod.id);
            // Chỉ cập nhật nếu tìm thấy và nó thực sự là object khác (hoặc để đảm bảo là object từ list mới nhất)
            if (fullPeriodObjectFromList && fullPeriodObjectFromList !== currentSelectedPeriod) {
              // console.log("Updating selectedPeriod with full object from fetched list:", fullPeriodObjectFromList);
              return fullPeriodObjectFromList;
            }
          }
          return currentSelectedPeriod; // Không thay đổi nếu không tìm thấy hoặc đã là object đúng
        });
      }, { onError: (err) => console.error("Lỗi tải kỳ checkpoint:", err) });
    } catch (error) { console.error("Lỗi tải kỳ checkpoint:", error); }
  }, [setPeriods, setSelectedPeriod]); // setPeriods, setSelectedPeriod là stable


  const fetchFilterData = useCallback(async () => {
    // console.log("fetchFilterData called");
    try {
      await request("get", "/departments?status=ACTIVE", (res) => setDepartments(res.data.data || []), { onError: (err) => console.error("Lỗi tải phòng ban:", err) });
      await request("get", "/jobs?status=ACTIVE", (res) => setJobPositions(res.data.data || []), { onError: (err) => console.error("Lỗi tải vị trí:", err) });
    } catch (error) { console.error("Lỗi tải dữ liệu filter:", error); }
  }, [setDepartments, setJobPositions]); // setX là stable

  useEffect(() => {
    // console.log("EFFECT: Initial data fetch for dropdowns (periods, filters)");
    fetchPeriodsForSelection();
    fetchFilterData();
  }, [fetchPeriodsForSelection, fetchFilterData]); // Callbacks này ổn định

  useEffect(() => {
    // console.log("EFFECT (staff list): Filters changed. Resetting to page 0 and fetching.");
    setCurrentPage(0);
    fetchStaffList(0, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, true);
  }, [itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, fetchStaffList]);


  const fetchCheckpointPoints = useCallback(async () => {
    if (!selectedPeriod || !selectedPeriod.id || staffData.length === 0) {
      setTotalPointsMap({});
      return;
    }
    // console.log("fetchCheckpointPoints called for period:", selectedPeriod?.id, "staffData length:", staffData.length);
    setLoadingPoints(true);
    const userLoginIds = staffData.map(staff => staff.user_login_id).filter(Boolean);
    if (userLoginIds.length === 0) {
      setTotalPointsMap({});
      setLoadingPoints(false);
      return;
    }

    const payload = { user_ids: userLoginIds.join(',') };
    try {
      await request("get", `/checkpoints/${selectedPeriod.id}`, (res) => {
          const evaluations = res.data.data || [];
          const points = {};
          evaluations.forEach(item => {
            points[item.user_id] = item.total_point !== null && item.total_point !== undefined ? item.total_point : "Chưa đánh giá";
          });
          setTotalPointsMap(points);
        }, {
          onError: (err) => { console.error("Lỗi tải điểm checkpoint:", err); setTotalPointsMap({}); }
        }, null, { params: payload }
      );
    } catch (error) {
      console.error("Ngoại lệ khi tải điểm checkpoint:", error);
      setTotalPointsMap({});
    } finally {
      setLoadingPoints(false);
    }
  }, [selectedPeriod, staffData]);

  useEffect(() => {
    // console.log("EFFECT: fetchCheckpointPoints due to change in its definition (selectedPeriod or staffData)");
    fetchCheckpointPoints();
  }, [fetchCheckpointPoints]);


  const handleGradeClick = useCallback((staff) => {
    if (!selectedPeriod) {
      setSnackbar({open: true, message: "Vui lòng chọn một kỳ checkpoint trước khi đánh giá.", severity: "warning"});
      return;
    }
    setSelectedStaffForGrading(staff);
    setGradeModalOpen(true);
  }, [selectedPeriod]);

  const columns = useMemo(
    () => [
      { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, width: 60, disableSortBy: true },
      { Header: "Mã NV", accessor: "staff_code", minWidth: 90, Cell: ({value}) => <Typography variant="body2">{value}</Typography> },
      { Header: "Họ và Tên", accessor: "fullname", minWidth: 220, Cell: ({ row }) => (
          <Box onClick={() => navigate(`/hr/staff/${row.original.staff_code}`)} sx={{ display: "flex", alignItems: "center", cursor: "pointer", '&:hover': {textDecoration: 'underline', color: 'primary.main'} }}>
            <Avatar alt={row.original.fullname} src={`https://ui-avatars.com/api/?name=${encodeURIComponent(row.original.fullname || 'N V')}&background=random&size=100&font-size=0.5&bold=true`} sx={{ width: 36, height: 36, mr: 1.5 }} />
            <Typography variant="body1" component="span" sx={{fontWeight: 500}}>{row.original.fullname}</Typography>
          </Box>
        )},
      { Header: "Email", accessor: "email", minWidth: 200 },
      { Header: "Tổng Điểm", id: "total_points", minWidth: 120,
        Cell: ({ row }) => {
          const point = totalPointsMap[row.original.user_login_id]; // Dùng user_login_id nếu đó là key trong map
          return loadingPoints ? <CircularProgress size={20}/> : (point !== undefined ? String(point) : "Chưa có");
        }
      },
      { Header: "Hành động", id: 'actions', Cell: ({ row }) => (
          <Tooltip title="Đánh giá">
            <span>
                <IconButton onClick={() => handleGradeClick(row.original)} disabled={!selectedPeriod || loadingPoints} size="small">
                    <GradeIcon fontSize="small" />
                </IconButton>
            </span>
          </Tooltip>
        ), width: 100, minWidth: 100, disableSortBy: true, textAlign: 'center'
      },
    ],
    [currentPage, itemsPerPage, navigate, totalPointsMap, handleGradeClick, selectedPeriod, loadingPoints]
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable({ columns, data: staffData, manualPagination: true, pageCount: pageCount, initialState: { pageIndex: currentPage }, }, usePagination );

  const exportPDF = () => {
    const doc = new jsPDF();
    const headerColor = theme?.palette?.primary?.main || [30, 136, 229];
    doc.setFont("helvetica", "bold"); doc.text(`Đánh giá Checkpoint - Kỳ: ${selectedPeriod?.name || 'Chưa chọn kỳ'}`, 14, 20); doc.setFont("helvetica", "normal");
    doc.autoTable({
      startY: 30,
      headStyles: { fillColor: headerColor, textColor: "#ffffff", fontStyle: 'bold' },
      head: [["Mã NV", "Họ Tên", "Email", "Tổng Điểm"]],
      body: staffData.map(row => [
        row.staff_code,
        row.fullname,
        row.email,
        totalPointsMap[row.user_login_id] !== undefined ? String(totalPointsMap[row.user_login_id]) : "Chưa đánh giá",
      ]),
      styles: { font: "helvetica", fontSize: 10 },
    });
    doc.save(`DanhSachDanhGiaCheckpoint_${selectedPeriod?.name || 'All'}.pdf`);
  };

  const handlePageChange = (newPage) => {
    // setCurrentPage(newPage); // Không cần thiết nếu fetchStaffList cập nhật đúng
    fetchStaffList(newPage, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, false);
  };

  const handleItemsPerPageChange = (newValue) => {
    setItemsPerPage(newValue);
    // useEffect cho itemsPerPage sẽ tự động gọi fetchStaffList với trang 0
  };

  const csvData = useMemo(() => {
    if (!staffData || staffData.length === 0) return [];
    return staffData.map(row => ({
      "Mã NV": row.staff_code,
      "Họ Tên": row.fullname,
      "Email": row.email,
      "Tổng Điểm": totalPointsMap[row.user_login_id] !== undefined ? String(totalPointsMap[row.user_login_id]) : "Chưa đánh giá",
    }));
  }, [staffData, totalPointsMap]);

  const handleSnackbarClose = (event, reason) => {
    if (reason === 'clickaway') return;
    setSnackbar({...snackbar, open: false});
  };


  return (
    <Box sx={{ p: { xs: 1, sm: 2, md: 3 }, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Snackbar open={snackbar.open} autoHideDuration={6000} onClose={handleSnackbarClose} anchorOrigin={{ vertical: "top", horizontal: "right" }}>
        <Alert onClose={handleSnackbarClose} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item> <Typography variant="h4" component="h1"> <AssessmentIcon sx={{mr:1, verticalAlign: 'middle'}}/> Đánh giá Checkpoint </Typography> </Grid>
          <Grid item xs={12} sm={6} md={4} lg={3}>
            <Autocomplete
              options={periods}
              getOptionLabel={(option) => option.name || ""}
              value={selectedPeriod}
              isOptionEqualToValue={(option, value) => option && value && option.id === value.id} // Thêm kiểm tra null
              onChange={(event, newValue) => {
                // console.log("Autocomplete period changed:", newValue);
                setSelectedPeriod(newValue);
              }}
              renderInput={(params) => <TextField {...params} label="Chọn Kỳ Checkpoint" variant="outlined" size="small" />}
            />
          </Grid>
        </Grid>
      </Paper>

      {selectedStaffForGrading && selectedPeriod && (
        <GradeModal
          open={gradeModalOpen}
          onClose={() => {setGradeModalOpen(false); fetchCheckpointPoints(); /* Tải lại điểm sau khi đóng modal */}}
          staff={selectedStaffForGrading}
          period={selectedPeriod}
        />
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="flex-end" wrap="wrap">
          <Grid item xs={12} md="auto">
            <Stack direction={{xs: "column", sm: "row"}} spacing={1.5} useFlexGap>
              {(csvData && csvData.length > 0) ? (
                <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />}>
                  <CSVLink data={csvData} filename={`DanhSachDanhGiaCheckpoint_${selectedPeriod?.name || 'TatCa'}.csv`} style={{ textDecoration: 'none', color: 'inherit' }}> Xuất CSV </CSVLink>
                </Button>
              ) : ( <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} disabled> Xuất CSV </Button> )}
              <Button variant="outlined" size="small" startIcon={<PictureAsPdfIcon />} onClick={exportPDF} disabled={!staffData || staffData.length === 0}> Xuất PDF </Button>
            </Stack>
          </Grid>
          <Grid item xs={12} sm={6} md>
            <TextField fullWidth label="Tìm kiếm theo tên NV" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} variant="outlined" size="small"/>
          </Grid>
          <Grid item xs={12} sm={6} md>
            <Autocomplete options={departments} getOptionLabel={(option) => option.department_name || ""} isOptionEqualToValue={(option, value) => option && value && option.department_code === value.department_code} onChange={(event, newValue) => setSelectedDepartmentFilter(newValue)} value={selectedDepartmentFilter} renderInput={(params) => <TextField {...params} label="Lọc theo Phòng Ban" variant="outlined" size="small" />} sx={{minWidth: 180}}/>
          </Grid>
          <Grid item xs={12} sm={6} md>
            <Autocomplete options={jobPositions} getOptionLabel={(option) => option.name || ""} isOptionEqualToValue={(option, value) => option && value && option.code === value.code} onChange={(event, newValue) => setSelectedJobPositionFilter(newValue)} value={selectedJobPositionFilter} renderInput={(params) => <TextField {...params} label="Lọc theo Vị Trí" variant="outlined" size="small" />} sx={{minWidth: 180}}/>
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{ overflow: 'hidden' }}>
        <TableContainer sx={{ maxHeight: "calc(100vh - 350px)" }}> {/* Điều chỉnh chiều cao nếu cần */}
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
              {loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <CircularProgress /> <Typography sx={{mt: 1.5}} variant="body1">Đang tải danh sách nhân viên...</Typography> </TableCell></TableRow>
              ) : !loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy nhân viên nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc điều chỉnh bộ lọc. </Typography> </TableCell></TableRow>
              ) : (
                rows.map((row) => { prepareRow(row); return ( <TableRow {...row.getRowProps()} hover sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }} > {row.cells.map((cell) => ( <TableCell {...cell.getCellProps()} align={cell.column.textAlign || (cell.column.id === 'actions' ? 'center' : 'left')} sx={{ py: 1.2, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}> {cell.render("Cell")} </TableCell> ))} </TableRow> ); })
              )}
            </TableBody>
          </Table>
        </TableContainer>
        {(pageCount > 0 && !loading) && ( <Pagination currentPage={currentPage} pageCount={pageCount} itemsPerPage={itemsPerPage} onPageChange={handlePageChange} onItemsPerPageChange={handleItemsPerPageChange} /> )}
      </Paper>
    </Box>
  );
};

const CheckpointEvaluationScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <CheckpointEvaluationScreenInternal />
    </ThemeProvider>
  );
};

export default CheckpointEvaluationScreen;
