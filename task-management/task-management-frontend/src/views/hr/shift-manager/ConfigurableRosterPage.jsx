// src/features/rosterConfiguration/ConfigurableRosterPage.jsx
import React, { useState, useEffect, useCallback } from 'react';
import {
  Container, Box, Typography, Button, Modal, Paper,
  CssBaseline, ThemeProvider, CircularProgress, IconButton,
  Snackbar,
  Alert as MuiAlert,
  Dialog, DialogTitle, DialogContent, DialogActions,
  Grid, List, ListItem, ListItemText, Divider, Backdrop
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import ArticleIcon from '@mui/icons-material/Article';
import BarChartIcon from '@mui/icons-material/BarChart';
import DownloadIcon from '@mui/icons-material/Download';
import CloseIcon from '@mui/icons-material/Close';
import UndoIcon from '@mui/icons-material/Undo';

import { theme } from './theme';
import { request } from "@/api";

import TemplateConfigForm from './TemplateConfigForm';
import ApplyConfigForm from './ApplyConfigForm';
import TemplateListDisplay from './TemplateListDisplay';
import DeleteConfirmationModal from "../modals/DeleteConfirmationModal.jsx";

let moduleDepartmentsForDisplay = [];
let moduleJobPositionsForDisplay = [];

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const downloadCSV = (statistics, fileName = "roster_statistics.csv") => {
  if (!statistics) {
    alert("Không có dữ liệu thống kê để tải.");
    return;
  }

  let csvContent = "\uFEFF"; // BOM for UTF-8

  // Roster Period
  csvContent += "Roster Period\r\n";
  csvContent += `Start Date,"${statistics.rosterStartDate || 'N/A'}"\r\n`; // Sửa ở đây
  csvContent += `End Date,"${statistics.rosterEndDate || 'N/A'}"\r\n`;   // Và ở đây
  csvContent += "\r\n";

  // Overall Statistics
  csvContent += "Overall Statistics\r\n";
  csvContent += `Total Assigned Shifts,"${statistics.totalAssignedShifts || 'N/A'}"\r\n`;
  csvContent += `Total Assigned Hours,"${statistics.totalAssignedHours?.toFixed(2) || 'N/A'}"\r\n`;
  if (statistics.fairnessHours) {
    csvContent += "Work Hours Fairness\r\n";
    csvContent += `Min Employee Hours,"${statistics.fairnessHours.minEmployeeValue?.toFixed(2) || 'N/A'}"\r\n`;
    csvContent += `Max Employee Hours,"${statistics.fairnessHours.maxEmployeeValue?.toFixed(2) || 'N/A'}"\r\n`;
    csvContent += `Range (Hours),"${statistics.fairnessHours.rangeValue?.toFixed(2) || 'N/A'}"\r\n`;
  }
  if (statistics.fairnessNightShifts) {
    csvContent += "Night Shifts Fairness\r\n";
    csvContent += `Min Night Shifts/Emp,"${statistics.fairnessNightShifts.minEmployeeCount || 'N/A'}"\r\n`;
    csvContent += `Max Night Shifts/Emp,"${statistics.fairnessNightShifts.maxEmployeeCount || 'N/A'}"\r\n`;
    csvContent += `Range (Night Shifts),"${statistics.fairnessNightShifts.rangeCount || 'N/A'}"\r\n`;
  }
  if (statistics.fairnessSundayShifts) {
    csvContent += "Sunday Shifts Fairness\r\n";
    csvContent += `Min Sunday Shifts/Emp,"${statistics.fairnessSundayShifts.minEmployeeCount || 'N/A'}"\r\n`;
    csvContent += `Max Sunday Shifts/Emp,"${statistics.fairnessSundayShifts.maxEmployeeCount || 'N/A'}"\r\n`;
    csvContent += `Range (Sunday Shifts),"${statistics.fairnessSundayShifts.rangeCount || 'N/A'}"\r\n`;
  }
  csvContent += "\r\n";

  // Employee Detailed Statistics
  csvContent += "Employee Detailed Statistics\r\n";
  csvContent += "Staff Code,Employee Name,Total Shifts,Total Hours,Night Shifts,Saturday Shifts,Sunday Shifts,Max Consecutive Work Days\r\n";
  if (statistics.employeeStats && statistics.employeeStats.length > 0) {
    statistics.employeeStats.forEach(emp => {
      csvContent += `"${emp.staffCode || ''}","${emp.employeeName || ''}","${emp.totalShifts || 0}","${emp.totalHours?.toFixed(2) || 0}","${emp.nightShifts || 0}","${emp.saturdayShiftsWorked || 0}","${emp.sundayShiftsWorked || 0}","${emp.maxConsecutiveWorkDays || 0}"\r\n`;
    });
  } else {
    csvContent += "No detailed employee statistics available.\r\n";
  }
  csvContent += "\r\n";

  if (statistics.detailedRosterLog && statistics.detailedRosterLog.length > 0) {
    csvContent += "Detailed Roster Generation Log\r\n";
    statistics.detailedRosterLog.forEach(logLine => {
      let cleanLine = logLine.replace(/📅|🕒|👤|🎉|📊|❌|▶|✅|➡|=========================================================|\n/g, '').trim();
      cleanLine = cleanLine.replace(/^[\s\t]+/, '');
      cleanLine = `"${cleanLine.replace(/"/g, '""')}"\r\n`; // Escape double quotes
      if (cleanLine.length > 3) { // Avoid empty "","" lines
        csvContent += cleanLine;
      }
    });
  }

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement("a");
  if (link.download !== undefined) {
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", fileName);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  } else {
    alert("Trình duyệt của bạn không hỗ trợ tải file trực tiếp.");
  }
};

export default function ConfigurableRosterPage() {
  const [isTemplateModalOpen, setIsTemplateModalOpen] = useState(false);
  const [isApplyModalOpen, setIsApplyModalOpen] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState(null);
  const [templateToApply, setTemplateToApply] = useState(null);
  const [configTemplates, setConfigTemplates] = useState(() => { try { const saved = localStorage.getItem('rosterTemplatesList_final_fix'); return saved ? JSON.parse(saved) : []; } catch (e) { return []; }});
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);
  const [loadingApis, setLoadingApis] = useState(true);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('info');
  const [isSubmittingRoster, setIsSubmittingRoster] = useState(false);
  const [isStatsModalOpen, setIsStatsModalOpen] = useState(false);
  const [rosterStatistics, setRosterStatistics] = useState(null);
  const [lastGeneratedShiftIds, setLastGeneratedShiftIds] = useState([]);
  const [isUndoingShifts, setIsUndoingShifts] = useState(false);
  const [isUndoConfirmModalOpen, setIsUndoConfirmModalOpen] = useState(false);


  const fetchDepartmentsAPI = useCallback(async () => { request( "get", "/departments", (res) => { const transformed = (res.data.data || []).map(dept => ({ departmentCode: dept.department_code, departmentName: dept.department_name })); moduleDepartmentsForDisplay = transformed; setDepartments(transformed); }, { onError: (err) => console.error("Error fetching departments:", err) }, null, { params: { status: "ACTIVE" } } ); }, []);
  const fetchJobPositionsAPI = useCallback(async () => { request( "get", "/jobs", (res) => { const transformed = (res.data.data || []).map(job => ({ code: job.code, name: job.name })); moduleJobPositionsForDisplay = transformed; setJobPositions(transformed); }, { onError: (err) => console.error("Error fetching job positions:", err) }, null, { params: { status: "ACTIVE" } } ); }, []);

  useEffect(() => { let active = true; const loadInitialData = async () => { setLoadingApis(true); try { await Promise.all([ fetchDepartmentsAPI(), fetchJobPositionsAPI() ]); } catch (error) { console.error("Error loading initial API data:", error); } finally { if (active) { setLoadingApis(false); } } }; loadInitialData(); return () => { active = false; }; }, [fetchDepartmentsAPI, fetchJobPositionsAPI]);
  useEffect(() => { localStorage.setItem('rosterTemplatesList_final_fix', JSON.stringify(configTemplates)); }, [configTemplates]);

  const handleOpenTemplateModalForNew = () => { setEditingTemplate(null); setIsTemplateModalOpen(true); };
  const handleOpenTemplateModalForEdit = (template) => { setEditingTemplate(template); setIsTemplateModalOpen(true); };
  const handleCloseTemplateModal = () => { setIsTemplateModalOpen(false); setEditingTemplate(null); };

  const handleSaveTemplate = (templateData) => { setConfigTemplates(prevList => { const existingIndex = prevList.findIndex(t => t.id === templateData.id); if (existingIndex > -1) { const newList = [...prevList]; newList[existingIndex] = templateData; return newList; } else { return [...prevList, templateData]; } }); handleCloseTemplateModal(); setSnackbarMessage("Đã lưu bộ cấu hình thành công!"); setSnackbarSeverity("success"); setSnackbarOpen(true); };
  const handleDeleteTemplate = (templateIdToDelete) => { if (window.confirm("Bạn có chắc chắn muốn xóa bộ cấu hình này không?")) { setConfigTemplates(prevList => prevList.filter(t => t.id !== templateIdToDelete)); setSnackbarMessage("Đã xóa bộ cấu hình!"); setSnackbarSeverity("info"); setSnackbarOpen(true); } };

  const handleOpenApplyModal = (template) => { setTemplateToApply(template); setIsApplyModalOpen(true); };
  const handleCloseApplyModal = () => { setIsApplyModalOpen(false); setTemplateToApply(null); };

  const handleActualApplyAndRoster = async (applicationDetails) => { /* ... như cũ ... */
    setIsSubmittingRoster(true);
    setRosterStatistics(null);
    setLastGeneratedShiftIds([]);

    const payload = {
      template_name: applicationDetails.templateName,
      start_date: applicationDetails.startDate,
      end_date: applicationDetails.endDate,
      department_codes: applicationDetails.departmentCodes,
      job_position_codes: applicationDetails.jobPositionCodes,
      defined_shifts: applicationDetails.shiftsAndConstraints.definedShifts,
      active_hard_constraints: applicationDetails.shiftsAndConstraints.activeHardConstraints
    };
    const API_ENDPOINT = "/roster/generate";

    try {
      request(
        "post", API_ENDPOINT,
        (res) => {
          setIsSubmittingRoster(false);
          const solution = res.data;
          const scheduledShifts = solution.scheduledShifts || [];
          const stats = solution.statistics || null;
          const createdIds = solution.createdShiftIds || [];

          setLastGeneratedShiftIds(createdIds);
          console.log("Created Shift IDs:", createdIds);

          if (stats) {
            setRosterStatistics(stats);
            setIsStatsModalOpen(true);
            setSnackbarMessage(`Xếp lịch thành công cho "${applicationDetails.templateName}"! ${createdIds.length} ca mới đã được tạo.`);
          } else if (scheduledShifts.length > 0) {
            setSnackbarMessage(`Xếp lịch thành công cho "${applicationDetails.templateName}"! ${createdIds.length} ca mới được tạo. (Không có thống kê chi tiết).`);
          } else {
            setSnackbarMessage(`Hoàn tất xử lý cho "${applicationDetails.templateName}". Solver không tìm thấy giải pháp hoặc không tạo ca nào.`);
          }
          setSnackbarSeverity((stats && (stats.totalAssignedShifts > 0 || scheduledShifts.length > 0)) ? "success" : "warning");
          setSnackbarOpen(true);
          handleCloseApplyModal();
        },
        { onError: (err) => {
            setIsSubmittingRoster(false);
            let errorMessage = 'Lỗi không xác định từ server.';
            if (err.response) {
              if (err.response.status === 422) { errorMessage = "Không thể tạo lịch: Các ràng buộc có thể quá chặt, không có nhân viên phù hợp hoặc không tìm thấy giải pháp."; }
              else if (err.response.data && err.response.data.message) { errorMessage = `Lỗi ${err.response.status}: ${err.response.data.message}`; }
              else if (err.response.data && err.response.data.statistics && err.response.data.statistics.detailedRosterLog) {
                errorMessage = `Không thể tạo lịch. Xem log thống kê để biết thêm chi tiết.`; // Rút gọn thông báo lỗi
                setRosterStatistics(err.response.data.statistics);
                setIsStatsModalOpen(true);
              }
              else if (err.response.statusText) { errorMessage = `Lỗi ${err.response.status}: ${err.response.statusText}`; }
              else { errorMessage = `Lỗi ${err.response.status} từ server.`; }
            } else if (err.request) { errorMessage = "Không nhận được phản hồi từ server. Vui lòng kiểm tra kết nối mạng."; }
            else { errorMessage = `Lỗi khi gửi yêu cầu: ${err.message}`; }
            console.error("Lỗi khi gọi API xếp lịch:", err);
            setSnackbarMessage(errorMessage); setSnackbarSeverity("error"); setSnackbarOpen(true);
          }
        },
        payload
      );
    } catch (error) {
      setIsSubmittingRoster(false);
      console.error("Lỗi cục bộ khi chuẩn bị gọi API xếp lịch:", error);
      setSnackbarMessage("Lỗi cục bộ khi chuẩn bị gửi yêu cầu."); setSnackbarSeverity("error"); setSnackbarOpen(true);
      handleCloseApplyModal();
    }
  };

  const handleOpenUndoConfirmModal = () => {
    if (lastGeneratedShiftIds.length === 0) {
      setSnackbarMessage("Không có ca nào vừa được tạo để hoàn tác.");
      setSnackbarSeverity("info");
      setSnackbarOpen(true);
      return;
    }
    setIsUndoConfirmModalOpen(true); // Mở modal xác nhận
  };

  const handleCloseUndoConfirmModal = () => {
    setIsUndoConfirmModalOpen(false);
  };

  const executeUndoLastGeneratedShifts = async () => {
    handleCloseUndoConfirmModal(); // Đóng modal xác nhận trước khi thực hiện
    if (lastGeneratedShiftIds.length === 0) return; // Kiểm tra lại cho chắc

    setIsUndoingShifts(true);
    // setIsSubmittingRoster(true); // Backdrop sẽ được kích hoạt bởi isUndoingShifts

    try {
      await request(
        "delete",
        `/shifts`,
        () => {
          setSnackbarMessage(`Đã hoàn tác ${lastGeneratedShiftIds.length} ca thành công!`);
          setSnackbarSeverity("success");
          setLastGeneratedShiftIds([]);
        },
        {
          onError: (err) => {
            console.error("Lỗi khi hoàn tác ca:", err.response?.data || err.message);
            setSnackbarMessage("Lỗi khi hoàn tác các ca đã tạo.");
            setSnackbarSeverity("error");
          }
        },
        lastGeneratedShiftIds
      );
    } catch (error) {
      console.error("Lỗi cục bộ khi hoàn tác:", error);
      setSnackbarMessage("Lỗi cục bộ khi gửi yêu cầu hoàn tác.");
      setSnackbarSeverity("error");
    } finally {
      setIsUndoingShifts(false);
      // setIsSubmittingRoster(false);
      setSnackbarOpen(true);
      // Đóng modal thống kê nếu nó đang mở và việc hoàn tác xảy ra từ đó
      if (isStatsModalOpen) setIsStatsModalOpen(false);
    }
  };


  const handleSnackbarClose = (event, reason) => { if (reason === 'clickaway') { return; } setSnackbarOpen(false); };
  const handleCloseStatsModal = () => setIsStatsModalOpen(false);

  if (loadingApis) { return ( <ThemeProvider theme={theme}> <CssBaseline /> <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}> <CircularProgress size={50} /> <Typography variant="h6" sx={{ml:2}}>Đang tải dữ liệu...</Typography> </Box> </ThemeProvider> ) }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Backdrop sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.modal + 1 }} open={isSubmittingRoster || isUndoingShifts}>
        <CircularProgress color="inherit" sx={{mr: 2}}/>
        <Typography variant="h6">{isUndoingShifts ? "Đang hoàn tác..." : "Đang xử lý yêu cầu xếp lịch, vui lòng chờ..."}</Typography>
      </Backdrop>

      <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
        <Container component="main" maxWidth="lg" sx={{ py: 0, flexGrow: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 2, px: {xs: 0, sm: 0}, backgroundColor: theme.palette.background.default, zIndex: 10, borderBottom: `1px solid ${theme.palette.divider}`, }}>
            <Typography variant="h5" sx={{ color: 'primary.dark', fontWeight: 700, display:'flex', alignItems:'center' }}>
              <ArticleIcon sx={{mr:1, color: 'primary.main', fontSize: '1.7rem'}} />
              Danh Sách Bộ Cấu Hình
            </Typography>
            <Button color="primary" startIcon={<AddIcon />} onClick={handleOpenTemplateModalForNew} variant="contained" disabled={isSubmittingRoster || isUndoingShifts}>
              Tạo Mới
            </Button>
          </Box>
          <Box sx={{flexGrow: 1, overflowY: 'auto', pt: 2, pr:0.5, mr: -0.5 }}>
            <TemplateListDisplay templates={configTemplates} onEdit={handleOpenTemplateModalForEdit} onDelete={handleDeleteTemplate} onOpenApplyModal={handleOpenApplyModal} isSubmittingRoster={isSubmittingRoster || isUndoingShifts} />
          </Box>
        </Container>

        <Modal open={isTemplateModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick' && !isSubmittingRoster && !isUndoingShifts) handleCloseTemplateModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
          <Paper sx={{ width: '95%', maxWidth: '900px', maxHeight: 'calc(95vh - 32px)', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
            {isTemplateModalOpen && <TemplateConfigForm onSave={handleSaveTemplate} onCancel={handleCloseTemplateModal} initialTemplateData={editingTemplate} />}
          </Paper>
        </Modal>

        {templateToApply && (
          <Modal open={isApplyModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick' && !isSubmittingRoster && !isUndoingShifts) handleCloseApplyModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
            <Paper sx={{ width: '95%', maxWidth: '750px', maxHeight: '90vh', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
              {isApplyModalOpen && <ApplyConfigForm onApply={handleActualApplyAndRoster} onCancel={handleCloseApplyModal} configTemplate={templateToApply} departments={departments} jobPositions={jobPositions} isSubmittingRoster={isSubmittingRoster || isUndoingShifts} />}
            </Paper>
          </Modal>
        )}

        {rosterStatistics && (
          <Dialog open={isStatsModalOpen} onClose={handleCloseStatsModal} maxWidth="lg" fullWidth PaperProps={{ sx: { maxHeight: '90vh', display: 'flex', flexDirection: 'column'} }}>
            {/* ... DialogTitle và DialogContent của Statistics Modal như cũ ... */}
            <DialogTitle sx={{ backgroundColor: 'primary.dark', color: 'white', display: 'flex', justifyContent: 'space-between', alignItems: 'center', py:1.5 }}>
              <Box sx={{display: 'flex', alignItems: 'center'}}>
                <BarChartIcon sx={{ mr: 1 }} /> Thống Kê Kết Quả Xếp Lịch
              </Box>
              <IconButton onClick={handleCloseStatsModal} sx={{color: 'white'}}><CloseIcon /></IconButton>
            </DialogTitle>
            <DialogContent dividers sx={{p:2, flexGrow: 1, overflowY: 'auto'}}>
              <Typography variant="body2" gutterBottom>
                Cho giai đoạn: <strong>{rosterStatistics.rosterStartDate}</strong> đến <strong>{rosterStatistics.rosterEndDate}</strong>
              </Typography>
              <Divider sx={{my:1}}/>

              <Typography variant="h6" gutterBottom>Tổng Quan</Typography>
              <Paper variant="outlined" sx={{p:1.5, mb:2}}>
                <Typography>Tổng số ca đã xếp: <strong>{rosterStatistics.totalAssignedShifts ?? 'N/A'}</strong></Typography>
                <Typography>Tổng số giờ làm đã xếp: <strong>{rosterStatistics.totalAssignedHours?.toFixed(2) ?? 'N/A'}</strong></Typography>
                {rosterStatistics.fairnessHours && ( <>
                  <Typography sx={{mt:1, fontWeight:'bold'}}>Phân Bổ Giờ Làm:</Typography>
                  <Typography>Giờ làm tối thiểu/NV: {rosterStatistics.fairnessHours.minEmployeeValue?.toFixed(2) ?? 'N/A'}</Typography>
                  <Typography>Giờ làm tối đa/NV: {rosterStatistics.fairnessHours.maxEmployeeValue?.toFixed(2) ?? 'N/A'}</Typography>
                  <Typography>Chênh lệch (Max-Min): {rosterStatistics.fairnessHours.rangeValue?.toFixed(2) ?? 'N/A'} giờ</Typography>
                </>)}
                {rosterStatistics.fairnessNightShifts && ( <>
                  <Typography sx={{mt:1, fontWeight:'bold'}}>Phân Bổ Ca Đêm:</Typography>
                  <Typography>Ca đêm tối thiểu/NV: {rosterStatistics.fairnessNightShifts.minEmployeeCount ?? 'N/A'}</Typography>
                  <Typography>Ca đêm tối đa/NV: {rosterStatistics.fairnessNightShifts.maxEmployeeCount ?? 'N/A'}</Typography>
                  <Typography>Chênh lệch: {rosterStatistics.fairnessNightShifts.rangeCount ?? 'N/A'} ca</Typography>
                </>)}
                {rosterStatistics.fairnessSundayShifts && ( <>
                  <Typography sx={{mt:1, fontWeight:'bold'}}>Phân Bổ Ca Chủ Nhật:</Typography>
                  <Typography>Ca CN tối thiểu/NV: {rosterStatistics.fairnessSundayShifts.minEmployeeCount ?? 'N/A'}</Typography>
                  <Typography>Ca CN tối đa/NV: {rosterStatistics.fairnessSundayShifts.maxEmployeeCount ?? 'N/A'}</Typography>
                  <Typography>Chênh lệch: {rosterStatistics.fairnessSundayShifts.rangeCount ?? 'N/A'} ca</Typography>
                </>)}
              </Paper>

              <Typography variant="h6" gutterBottom>Chi Tiết Theo Nhân Viên</Typography>
              <Paper variant="outlined" sx={{maxHeight: 300, overflowY: 'auto', mb:2}}>
                <List dense>
                  {(rosterStatistics.employeeStats && rosterStatistics.employeeStats.length > 0) ? rosterStatistics.employeeStats.map((emp, index) => (
                    <React.Fragment key={emp.staffCode || index}>
                      <ListItem>
                        <ListItemText
                          primaryTypographyProps={{fontWeight:'500', color:'text.primary'}}
                          secondaryTypographyProps={{fontSize: '0.8rem', color:'text.secondary'}}
                          primary={`${emp.employeeName} (Mã NV: ${emp.staffCode})`}
                          secondary={`Tổng ca: ${emp.totalShifts}, Tổng giờ: ${emp.totalHours?.toFixed(2)}, Ca Đêm: ${emp.nightShifts}, Ca T7: ${emp.saturdayShiftsWorked}, Ca CN: ${emp.sundayShiftsWorked}, Chuỗi LT max: ${emp.maxConsecutiveWorkDays}`}
                        />
                      </ListItem>
                      {index < rosterStatistics.employeeStats.length - 1 && <Divider component="li" />}
                    </React.Fragment>
                  )) : <ListItem><ListItemText primary="Không có dữ liệu chi tiết nhân viên." /></ListItem>}
                </List>
              </Paper>

              {rosterStatistics.detailedRosterLog && rosterStatistics.detailedRosterLog.length > 0 && (
                <>
                  <Typography variant="h6" gutterBottom>Log Chi Tiết Lịch</Typography>
                  <Paper variant="outlined" sx={{maxHeight: 300, overflowY: 'auto', p:1.5, backgroundColor: 'grey.50'}}> {/* Increased maxHeight slightly */}
                    {rosterStatistics.detailedRosterLog.map((line, idx) => ( // REMOVED .slice(0, 100)
                      <Typography key={idx} component="div" variant="caption" sx={{whiteSpace: 'pre-wrap', fontFamily:'monospace', fontSize: '0.75rem', lineHeight: 1.3}}>
                        {line.replace(/📅|🕒|👤|🎉|📊|❌|▶|✅|➡|=========================================================|\n/g, '').trim()}
                      </Typography>
                    ))}
                  </Paper>
                </>
              )}
            </DialogContent>
            <DialogActions sx={{borderTop: '1px solid', borderColor:'divider', p: '12px 24px', backgroundColor:'grey.100'}}>
              {lastGeneratedShiftIds.length > 0 && (
                <Button onClick={handleOpenUndoConfirmModal} color="warning" variant="outlined" startIcon={<UndoIcon />} disabled={isUndoingShifts || isSubmittingRoster}>
                  {isUndoingShifts ? "Đang Hoàn Tác..." : "Hoàn Tác Lịch Vừa Tạo"}
                </Button>
              )}
              <Box sx={{ flexGrow: 1 }} />
              <Button onClick={handleCloseStatsModal} color="inherit">Đóng</Button>
              <Button onClick={() => downloadCSV(rosterStatistics)} color="primary" variant="contained" startIcon={<DownloadIcon />}>
                Tải CSV Thống Kê
              </Button>
            </DialogActions>
          </Dialog>
        )}

        {/* Modal xác nhận hoàn tác */}
        <DeleteConfirmationModal
          open={isUndoConfirmModalOpen}
          onClose={handleCloseUndoConfirmModal}
          onSubmit={executeUndoLastGeneratedShifts}
          title="Xác nhận Hoàn Tác"
          info={`Bạn có chắc chắn muốn hoàn tác ${lastGeneratedShiftIds.length} ca làm việc vừa được tạo không? Các ca này sẽ bị xóa khỏi hệ thống.`}
          confirmLabel="Đồng Ý Hoàn Tác"
          cancelLabel="Hủy Bỏ"
        />

        <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleSnackbarClose} anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}>
          <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
            {snackbarMessage}
          </Alert>
        </Snackbar>
      </Box>
    </ThemeProvider>
  );
}
