import React, {useEffect, useMemo, useState, useCallback} from "react";
import {usePagination, useTable} from "react-table";
import {useNavigate, useLocation}from "react-router-dom";
import {request}from "@/api";

import {
  ThemeProvider,
  CssBaseline,
  Box,
  Button,
  CircularProgress,
  Grid,
  IconButton,
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
  Alert as MuiAlert,
} from "@mui/material";
import { theme } from './theme';

import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import GradeIcon from "@mui/icons-material/BorderColor";
import AssessmentIcon from '@mui/icons-material/Assessment';

import GradeModal from "./modals/GradeModal";
import Pagination from "@/components/item/Pagination";
import { useDebounce } from "../../hooks/useDebounce";
import { exportToPDF, prepareCSVData } from "./fileExportUtils";

import {CSVLink}from "react-csv";
import toast from "react-hot-toast";
import dayjs from "dayjs";


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

  useEffect(() => {
    if (periodFromState) {
      setSelectedPeriod(currentSelectedPeriod => {
        if (!currentSelectedPeriod || currentSelectedPeriod.id !== periodFromState.id) {
          return periodFromState;
        }
        return currentSelectedPeriod;
      });
    }
  }, [periodFromState]);


  const fetchStaffList = useCallback(async (pageIndex, pageSize, searchValue, department, jobPosition, isInitialLoadOrFilterChange = false) => {
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

  const fetchPeriodsForSelection = useCallback(async () => {
    try {
      await request("get", "/checkpoints/periods?status=ACTIVE", (res) => {
        const fetchedPeriods = res.data.data || [];
        setPeriods(fetchedPeriods);
        setSelectedPeriod(currentSelectedPeriod => {
          if (currentSelectedPeriod && fetchedPeriods.length > 0) {
            const fullPeriodObjectFromList = fetchedPeriods.find(p => p.id === currentSelectedPeriod.id);
            if (fullPeriodObjectFromList && fullPeriodObjectFromList !== currentSelectedPeriod) {
              return fullPeriodObjectFromList;
            }
          } else if (!currentSelectedPeriod && fetchedPeriods.length > 0 && !periodFromState) {
            // Tự động chọn kỳ đầu tiên nếu chưa có kỳ nào được chọn và không có state từ router
            // return fetchedPeriods[0];
          }
          return currentSelectedPeriod;
        });
      }, { onError: (err) => console.error("Lỗi tải kỳ checkpoint:", err) });
    } catch (error) { console.error("Lỗi tải kỳ checkpoint:", error); }
  }, [periodFromState]);


  const fetchFilterData = useCallback(async () => {
    try {
      await request("get", "/departments?status=ACTIVE", (res) => setDepartments(res.data.data || []), { onError: (err) => console.error("Lỗi tải phòng ban:", err) });
      await request("get", "/jobs?status=ACTIVE", (res) => setJobPositions(res.data.data || []), { onError: (err) => console.error("Lỗi tải vị trí:", err) });
    } catch (error) { console.error("Lỗi tải dữ liệu filter:", error); }
  }, []);

  useEffect(() => {
    fetchPeriodsForSelection();
    fetchFilterData();
  }, [fetchPeriodsForSelection, fetchFilterData]);

  useEffect(() => {
    fetchStaffList(0, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, true);
  }, [itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, fetchStaffList]);


  const fetchCheckpointPoints = useCallback(async () => {
    if (!selectedPeriod || !selectedPeriod.id || staffData.length === 0) {
      setTotalPointsMap({});
      return;
    }
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
          console.log(evaluations)
          const points = {};
          evaluations.forEach(item => {
            points[item.user_id] = item.total_point !== null && item.total_point !== undefined ? Number(item.total_point).toFixed(2) : "N/A";
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
    fetchCheckpointPoints();
  }, [fetchCheckpointPoints]);


  const handleGradeClick = useCallback((staff) => {
    if (!selectedPeriod) {
      toast.error("Vui lòng chọn một kỳ checkpoint trước khi đánh giá.");
      return;
    }
    setSelectedStaffForGrading(staff);
    setGradeModalOpen(true);
  }, [selectedPeriod]);

  const columns = useMemo(
    () => [
      { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, width: 60, disableSortBy: true, id:'stt' },
      { Header: "Mã NV", accessor: "staff_code", minWidth: 90, id: 'staff_code', Cell: ({value}) => <Typography variant="body2">{value || "-"}</Typography> },
      { Header: "Họ và Tên", accessor: "fullname", minWidth: 220, id: 'fullname', Cell: ({ row }) => (
          <Box onClick={() => navigate(`/hr/staff/${row.original.staff_code}`)} sx={{ display: "flex", alignItems: "center", cursor: "pointer", '&:hover': {textDecoration: 'underline', color: 'primary.main'} }}>
            <Avatar alt={row.original.fullname} src={`https://ui-avatars.com/api/?name=${encodeURIComponent(row.original.fullname || 'N V')}&background=random&size=100&font-size=0.5&bold=true&color=fff`} sx={{ width: 36, height: 36, mr: 1.5 }} />
            <Typography variant="body1" component="span" sx={{fontWeight: 500}}>{row.original.fullname}</Typography>
          </Box>
        )},
      { Header: "Email", accessor: "email", minWidth: 200, id:'email' },
      { Header: "Tổng Điểm", id: "total_points", minWidth: 120, textAlign:'center',
        Cell: ({ row }) => {
          const point = totalPointsMap[row.original.user_login_id];
          return loadingPoints ? <CircularProgress size={16}/> : (point !== undefined ? String(point) : "N/A");
        }
      },
      { Header: "Hành động", id: 'actions', Cell: ({ row }) => (
          <Tooltip title="Đánh giá Checkpoint">
            <span>
                <IconButton onClick={() => handleGradeClick(row.original)} disabled={!selectedPeriod || loadingPoints} size="small" color="primary">
                    <GradeIcon fontSize="small" />
                </IconButton>
            </span>
          </Tooltip>
        ), width: 100, minWidth: 100, disableSortBy: true, textAlign: 'center'
      },
    ],
    [currentPage, itemsPerPage, navigate, totalPointsMap, handleGradeClick, selectedPeriod, loadingPoints]
  );

  // SỬA Ở ĐÂY: Khai báo lại instance của useTable
  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    rows,
    prepareRow
  } = useTable(
    {
      columns,
      data: staffData, // Sử dụng staffData ở đây
      manualPagination: true,
      pageCount: pageCount,
      initialState: { pageIndex: currentPage },
    },
    usePagination
  );

  // ... (pdfExportColumns, handleExportPDF, handlePageChange, handleItemsPerPageChange, csvHeaders, csvPreparedData, modalTitleStyle giữ nguyên) ...
  const pdfExportColumns = useMemo(() => [
    { Header: "#", accessor: (row, i) => currentPage * itemsPerPage + i + 1, id:'stt_export_pdf' },
    { Header: "Mã NV", accessor: "staff_code", id:'staff_code_export_pdf'},
    { Header: "Họ và Tên", accessor: "fullname", id:'fullname_export_pdf' },
    { Header: "Email", accessor: "email", id:'email_export_pdf' },
    { Header: "Tổng Điểm", accessor: (row) => {
        const point = totalPointsMap[row.user_login_id];
        return point !== undefined ? String(point) : "N/A";
      }, id:'total_points_export_pdf'
    }
  ], [currentPage, itemsPerPage, totalPointsMap]);


  const handleExportPDF = () => {
    exportToPDF({
      data: staffData,
      columns: pdfExportColumns,
      title: `Báo cáo Đánh giá Checkpoint - Kỳ: ${selectedPeriod?.name || 'Tất cả'}`,
      fileName: `DS_DanhGiaCheckpoint_${selectedPeriod?.name || 'All'}_${dayjs().format("YYYYMMDD")}.pdf`,
      themePalette: theme.palette,
      customColumnWidths: {
        0: {cellWidth: 30},
        1: {cellWidth: 70},
        2: {cellWidth: 150},
        3: {cellWidth: 150},
        4: {cellWidth: 'auto', halign: 'center'}
      }
    });
  };

  const handlePageChange = (newPage) => {
    fetchStaffList(newPage, itemsPerPage, debouncedSearchTerm, selectedDepartmentFilter, selectedJobPositionFilter, false);
  };

  const handleItemsPerPageChange = (newValue) => {
    setItemsPerPage(newValue);
  };

  const csvHeaders = useMemo(() => [
    { label: "#", key: "stt_csv" },
    { label: "Mã NV", key: "staff_code_csv" },
    { label: "Họ và Tên", key: "fullname_csv" },
    { label: "Email", key: "email_csv" },
    { label: "Tổng Điểm", key: "total_points_csv" }
  ], []);

  const csvPreparedData = useMemo(() => {
    if (loading || !staffData || staffData.length === 0) return [];
    return staffData.map((row, index) => ({
      stt_csv: currentPage * itemsPerPage + index + 1,
      staff_code_csv: row.staff_code || "",
      fullname_csv: row.fullname || "",
      email_csv: row.email || "",
      total_points_csv: totalPointsMap[row.user_login_id] !== undefined ? String(totalPointsMap[row.user_login_id]) : "N/A",
    }));
  }, [staffData, loading, currentPage, itemsPerPage, totalPointsMap]);


  const modalTitleStyle = { fontSize: '1.15rem', fontWeight: 600 };


  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      {/* ... Phần Paper header của trang ... */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center" justifyContent="space-between" wrap="wrap">
          <Grid item xs={12} md={6}> <Typography variant="h4" component="h1" sx={{display:'flex', alignItems:'center'}}> <AssessmentIcon sx={{mr:1.5, color:'primary.main', fontSize: '2rem'}}/> Đánh giá Checkpoint </Typography> </Grid>
          <Grid item xs={12} md={6} lg={4}>
            <Autocomplete
              options={periods}
              getOptionLabel={(option) => option.name || ""}
              value={selectedPeriod}
              isOptionEqualToValue={(option, value) => option && value && option.id === value.id}
              onChange={(event, newValue) => {
                setSelectedPeriod(newValue);
              }}
              renderInput={(params) => <TextField {...params} label="Chọn Kỳ Checkpoint" variant="outlined" size="small" />}
              noOptionsText="Không có kỳ checkpoint nào"
            />
          </Grid>
        </Grid>
      </Paper>

      {/* ... GradeModal ... */}
      {selectedStaffForGrading && selectedPeriod && (
        <GradeModal
          open={gradeModalOpen}
          onClose={() => {setGradeModalOpen(false); fetchCheckpointPoints();}}
          staff={selectedStaffForGrading}
          period={selectedPeriod}
          titleProps={{sx: modalTitleStyle}}
        />
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="flex-end" wrap="wrap">
          <Grid item xs={12} md="auto">
            <Stack direction={{xs: "column", sm: "row"}} spacing={1.5} useFlexGap>
              {(csvPreparedData && csvPreparedData.length > 0 && !loading) ? (
                <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />}>
                  <CSVLink
                    data={csvPreparedData}
                    headers={csvHeaders}
                    filename={`DS_DanhGiaCheckpoint_${selectedPeriod?.name || 'All'}_${dayjs().format("YYYYMMDD")}.csv`}
                    style={{ textDecoration: 'none', color: 'inherit' }}
                  >
                    Xuất CSV
                  </CSVLink>
                </Button>
              ) : ( <Button variant="outlined" size="small" startIcon={<CloudDownloadIcon />} disabled> Xuất CSV </Button> )}
              <Button variant="outlined" size="small" startIcon={<PictureAsPdfIcon />} onClick={handleExportPDF} disabled={!staffData || staffData.length === 0 || loading}> Xuất PDF </Button>
            </Stack>
          </Grid>
          {/* ... Các TextField và Autocomplete filters ... */}
          <Grid item xs={12} sm={6} md>
            <TextField fullWidth label="Tìm kiếm theo tên NV" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} variant="outlined" size="small"/>
          </Grid>
          <Grid item xs={12} sm={6} md>
            <Autocomplete options={departments} getOptionLabel={(option) => option.department_name || ""} isOptionEqualToValue={(option, value) => option && value && option.department_code === value.department_code} onChange={(event, newValue) => setSelectedDepartmentFilter(newValue)} value={selectedDepartmentFilter} renderInput={(params) => <TextField {...params} label="Lọc theo Phòng Ban" variant="outlined" size="small" />} sx={{minWidth: 180}} noOptionsText="Không có phòng ban"/>
          </Grid>
          <Grid item xs={12} sm={6} md>
            <Autocomplete options={jobPositions} getOptionLabel={(option) => option.name || ""} isOptionEqualToValue={(option, value) => option && value && option.code === value.code} onChange={(event, newValue) => setSelectedJobPositionFilter(newValue)} value={selectedJobPositionFilter} renderInput={(params) => <TextField {...params} label="Lọc theo Vị Trí" variant="outlined" size="small" />} sx={{minWidth: 180}} noOptionsText="Không có vị trí"/>
          </Grid>
        </Grid>
      </Paper>

      {/* Phần Table và Pagination */}
      <Paper sx={{ overflow: 'hidden' }}>
        <TableContainer sx={{ maxHeight: "calc(100vh - 320px)" }} className="custom-scrollbar">
          {/* Dòng 347 ở đây, sử dụng getTableProps */}
          <Table {...getTableProps()} stickyHeader size="medium">
            <TableHead>
              {headerGroups.map((headerGroup) => (
                <TableRow {...headerGroup.getHeaderGroupProps()}>
                  {headerGroup.headers.map((column) => (
                    <TableCell {...column.getHeaderProps()} align={column.textAlign || (column.id === 'actions' || column.id === 'stt' ? 'center' : 'left')}
                               sx={{ bgcolor: (t) => t.palette.mode === 'light' ? t.palette.grey[100] : t.palette.grey[700], color: (t) => t.palette.getContrastText(t.palette.mode === 'light' ? t.palette.grey[100] : t.palette.grey[700]), fontWeight: '600', whiteSpace: 'nowrap', width: column.width, minWidth: column.minWidth || (column.id === 'stt' ? 60 : 120), py: 1.25, borderBottom: (t) => `1px solid ${t.palette.divider}`, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.divider}` } }}
                    > {column.render("Header")} </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableHead>
            <TableBody {...getTableBodyProps()}>
              {loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <CircularProgress /> <Typography sx={{mt: 1.5}} variant="body1">Đang tải danh sách nhân viên...</Typography> </TableCell></TableRow>
              ) : !loading && rows.length === 0 ? ( <TableRow><TableCell colSpan={columns.length} align="center" sx={{ py: 5 }}> <Typography variant="h6">Không tìm thấy nhân viên nào.</Typography> <Typography variant="body1" color="text.secondary" sx={{mt:1}}> Vui lòng thử lại với từ khóa khác hoặc điều chỉnh bộ lọc. </Typography> </TableCell></TableRow>
              ) : (
                rows.map((row) => { prepareRow(row); return ( <TableRow {...row.getRowProps()} hover sx={{ '&:nth-of-type(odd)': { backgroundColor: (t) => t.palette.action.hover } }} > {row.cells.map((cell) => ( <TableCell {...cell.getCellProps()} align={cell.column.textAlign || (cell.column.id === 'actions' || cell.column.id === 'stt' ? 'center' : 'left')} sx={{ py: 1, '&:not(:last-child)': { borderRight: (t) => `1px solid ${t.palette.grey[200]}` } }}> {cell.render("Cell")} </TableCell> ))} </TableRow> ); })
              )}
            </TableBody>
          </Table>
        </TableContainer>
        {(pageCount > 0 && !loading && staffData.length > 0) && ( <Pagination currentPage={currentPage} pageCount={pageCount} itemsPerPage={itemsPerPage} onPageChange={handlePageChange} onItemsPerPageChange={handleItemsPerPageChange} /> )}
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