// src/features/rosterConfiguration/ConfigurableRosterPage.jsx
import React, {useCallback, useEffect, useState} from 'react';
import {
  Alert as MuiAlert,
  Backdrop,
  Box,
  Button,
  CircularProgress,
  Container,
  CssBaseline,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Modal,
  Paper,
  Snackbar,
  ThemeProvider,
  Typography
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import ArticleIcon from '@mui/icons-material/Article';
import BarChartIcon from '@mui/icons-material/BarChart';
import DownloadIcon from '@mui/icons-material/Download';
import CloseIcon from '@mui/icons-material/Close';
import UndoIcon from '@mui/icons-material/Undo';

import {theme} from './theme';
import {request} from "@/api";

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
  const [configTemplates, setConfigTemplates] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);
  const [loadingApis, setLoadingApis] = useState(true); // Initial loading for departments, jobs, AND initial templates
  const [loadingTemplatesAction, setLoadingTemplatesAction] = useState(false); // For subsequent template CUD actions

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('info');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [isStatsModalOpen, setIsStatsModalOpen] = useState(false);
  const [rosterStatistics, setRosterStatistics] = useState(null);
  const [lastGeneratedShiftIds, setLastGeneratedShiftIds] = useState([]);
  const [isUndoingShifts, setIsUndoingShifts] = useState(false);
  const [isUndoConfirmModalOpen, setIsUndoConfirmModalOpen] = useState(false);
  const [templateIdToDelete, setTemplateIdToDelete] = useState(null);
  const [isDeleteConfirmModalOpen, setIsDeleteConfirmModalOpen] = useState(false);

  const ensureTemplateStructure = (template) => {
    // Handles potential snake_case from API and provides defaults
    return {
      id: template.id,
      templateName: template.template_name || template.templateName, // Prioritize snake_case if present
      definedShifts: template.defined_shifts || template.definedShifts || [],
      activeHardConstraints: template.active_hard_constraints || template.activeHardConstraints || {},
      departmentFilter: template.department_filter || template.departmentFilter || [],
      jobPositionFilter: template.job_position_filter || template.jobPositionFilter || [],
    };
  };

  const fetchAllRosterTemplates = useCallback(async (showLoadingIndicator = true) => {
    if (showLoadingIndicator) setLoadingTemplatesAction(true);
    try {
      await request(
        "get",
        "/roster-templates",
        (res) => {
          const templatesFromApi = res.data?.data || [];
          setConfigTemplates(templatesFromApi.map(ensureTemplateStructure));
        },
        { onError: (err) => {
            console.error("Error fetching roster templates:", err.response?.data || err.message);
            setSnackbarMessage("Lỗi khi tải danh sách bộ cấu hình.");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
          }
        }
      );
    } catch (error) {
      console.error("Error calling API for roster templates:", error);
      setSnackbarMessage("Lỗi nghiêm trọng khi tải danh sách bộ cấu hình.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
    } finally {
      if (showLoadingIndicator) setLoadingTemplatesAction(false);
    }
  }, []);

  const fetchDepartmentsAPI = useCallback(async () => {
    // Assuming request utility returns a Promise or can be awaited
    try {
      await request( "get", "/departments", (res) => {
        const transformed = (res.data.data || []).map(dept => ({ departmentCode: dept.department_code, departmentName: dept.department_name }));
        setDepartments(transformed);
      }, { onError: (err) => console.error("Error fetching departments:", err.response?.data || err.message) }, null, { params: { status: "ACTIVE" } } );
    } catch (error) {
      console.error("Exception in fetchDepartmentsAPI:", error);
    }
  }, []);

  const fetchJobPositionsAPI = useCallback(async () => {
    try {
      await request( "get", "/jobs", (res) => {
        const transformed = (res.data.data || []).map(job => ({ code: job.code, name: job.name }));
        setJobPositions(transformed);
      }, { onError: (err) => console.error("Error fetching job positions:", err.response?.data || err.message) }, null, { params: { status: "ACTIVE" } } );
    } catch (error) {
      console.error("Exception in fetchJobPositionsAPI:", error);
    }
  }, []);

  useEffect(() => {
    let active = true;
    const loadInitialData = async () => {
      setLoadingApis(true); // Start initial comprehensive loading
      try {
        await Promise.all([
          fetchDepartmentsAPI(),
          fetchJobPositionsAPI(),
          fetchAllRosterTemplates(false) // Initial fetch, don't trigger separate template loading indicator
        ]);
      } catch (error) {
        console.error("Error loading initial API data:", error);
        // Potentially set a global error state or snackbar here
      } finally {
        if (active) setLoadingApis(false); // End initial comprehensive loading
      }
    };
    loadInitialData();
    return () => { active = false; };
  }, [fetchDepartmentsAPI, fetchJobPositionsAPI, fetchAllRosterTemplates]);


  const handleOpenTemplateModalForNew = () => { setEditingTemplate(null); setIsTemplateModalOpen(true); };
  const handleOpenTemplateModalForEdit = (template) => { setEditingTemplate(ensureTemplateStructure(template)); setIsTemplateModalOpen(true); };
  const handleCloseTemplateModal = () => { setIsTemplateModalOpen(false); setEditingTemplate(null); };

  const handleSaveTemplate = async (templateData) => {
    setIsSubmitting(true); // General submitting state
    setLoadingTemplatesAction(true); // Specific for template action
    const isUpdating = !!editingTemplate?.id;
    const endpoint = isUpdating ? `/roster-templates/${editingTemplate.id}` : "/roster-templates";
    const method = isUpdating ? "put" : "post";

    const payload = {
      template_name: templateData.templateName,
      defined_shifts: templateData.definedShifts || [],
      active_hard_constraints: templateData.activeHardConstraints || {}
    };
    if (isUpdating) {
      payload.id = editingTemplate.id;
    }

    try {
      await request( method, endpoint, (res) => {
          setSnackbarMessage(`Đã ${isUpdating ? 'cập nhật' : 'tạo mới'} bộ cấu hình thành công!`);
          setSnackbarSeverity("success");
          fetchAllRosterTemplates(false); // Refresh without its own loading indicator
          handleCloseTemplateModal();
        },
        { onError: (err) => { /* ... error handling ... */
            console.error("Error saving template:", err.response?.data || err.message);
            setSnackbarMessage(`Lỗi khi ${isUpdating ? 'cập nhật' : 'tạo mới'} bộ cấu hình: ${err.response?.data?.message || 'Lỗi không xác định'}`);
            setSnackbarSeverity("error");
          }
        }, payload );
    } catch (error) { /* ... error handling ... */
      console.error("Exception during template save:", error);
      setSnackbarMessage(`Lỗi nghiêm trọng khi ${isUpdating ? 'cập nhật' : 'tạo mới'} bộ cấu hình.`);
      setSnackbarSeverity("error");
    } finally {
      setIsSubmitting(false);
      setLoadingTemplatesAction(false);
      setSnackbarOpen(true);
    }
  };

  const openDeleteConfirmModal = (id) => { setTemplateIdToDelete(id); setIsDeleteConfirmModalOpen(true); };
  const closeDeleteConfirmModal = () => { setTemplateIdToDelete(null); setIsDeleteConfirmModalOpen(false); };

  const handleDeleteTemplateConfirmed = async () => {
    if (!templateIdToDelete) return;
    closeDeleteConfirmModal();
    setIsSubmitting(true);
    setLoadingTemplatesAction(true);
    try {
      await request( "delete", `/roster-templates/${templateIdToDelete}`, () => {
          setSnackbarMessage("Đã xóa bộ cấu hình!");
          setSnackbarSeverity("info");
          fetchAllRosterTemplates(false);
        },
        { onError: (err) => { /* ... error handling ... */
            console.error("Error deleting template:", err.response?.data || err.message);
            setSnackbarMessage(`Lỗi khi xóa bộ cấu hình: ${err.response?.data?.message || 'Lỗi không xác định'}`);
            setSnackbarSeverity("error");
          }
        }
      );
    } catch (error) { /* ... error handling ... */
      console.error("Exception during template delete:", error);
      setSnackbarMessage("Lỗi nghiêm trọng khi xóa bộ cấu hình.");
      setSnackbarSeverity("error");
    } finally {
      setIsSubmitting(false);
      setLoadingTemplatesAction(false);
      setSnackbarOpen(true);
    }
  };

  const handleOpenApplyModal = (template) => { setTemplateToApply(ensureTemplateStructure(template)); setIsApplyModalOpen(true); };
  const handleCloseApplyModal = () => { setIsApplyModalOpen(false); setTemplateToApply(null); };

  const handleActualApplyAndRoster = async (applicationDetails) => {
    setIsSubmitting(true);
    setRosterStatistics(null);
    setLastGeneratedShiftIds([]);

    const originalTemplate = configTemplates.find(t => t.id === applicationDetails.templateId);
    let filtersSuccessfullyPatched = false;

    if (originalTemplate) {
      const currentDeptFilters = applicationDetails.departmentCodes || [];
      const currentJobFilters = applicationDetails.jobPositionCodes || [];
      const originalDeptFilters = originalTemplate.departmentFilter || [];
      const originalJobFilters = originalTemplate.jobPositionFilter || [];

      const depFiltersChanged = JSON.stringify(originalDeptFilters.sort()) !== JSON.stringify(currentDeptFilters.sort());
      const jobFiltersChanged = JSON.stringify(originalJobFilters.sort()) !== JSON.stringify(currentJobFilters.sort());

      if (depFiltersChanged || jobFiltersChanged) {
        const filterPayload = {
          department_filter: currentDeptFilters,
          job_position_filter: currentJobFilters
        };
        try {
          await request( "patch", `/roster-templates/${applicationDetails.templateId}`, () => { filtersSuccessfullyPatched = true; },
            { onError: (err) => { /* ... error handling ... */
                console.error("Error patching template filters:", err.response?.data || err.message);
                setSnackbarMessage(`Lỗi khi cập nhật bộ lọc cho template: ${err.response?.data?.message || 'Lỗi không xác định'}. Tiếp tục xếp lịch với bộ lọc mới đã chọn.`);
                setSnackbarSeverity("warning");
                setSnackbarOpen(true);
              }
            }, filterPayload );
        } catch (patchError) { /* ... error handling ... */
          console.error("Exception during filter patch request:", patchError);
          setSnackbarMessage(`Lỗi nghiêm trọng khi cập nhật bộ lọc. Tiếp tục xếp lịch với bộ lọc mới đã chọn.`);
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
        }
      }
    }

    const rosterGenerationPayload = {
      template_name: applicationDetails.templateName,
      start_date: applicationDetails.startDate,
      end_date: applicationDetails.endDate,
      department_codes: applicationDetails.departmentCodes,
      job_position_codes: applicationDetails.jobPositionCodes,
      defined_shifts: applicationDetails.shiftsAndConstraints.definedShifts || [],
      active_hard_constraints: applicationDetails.shiftsAndConstraints.activeHardConstraints || {}
    };
    const API_ENDPOINT_GENERATE = "/roster/generate";

    try {
      await request( "post", API_ENDPOINT_GENERATE, (res) => {
          const solution = res.data;
          const scheduledShifts = solution?.scheduledShifts || [];
          const stats = solution?.statistics || null;
          const createdIds = solution?.createdShiftIds || [];
          setLastGeneratedShiftIds(createdIds);
          if (filtersSuccessfullyPatched) fetchAllRosterTemplates(false);
          if (stats) {
            setRosterStatistics(stats); setIsStatsModalOpen(true);
            setSnackbarMessage(`Xếp lịch thành công cho "${applicationDetails.templateName}"! ${createdIds.length} ca mới đã được tạo.`);
          } else if (scheduledShifts.length > 0) {
            setSnackbarMessage(`Xếp lịch thành công cho "${applicationDetails.templateName}"! ${createdIds.length} ca mới được tạo. (Không có thống kê chi tiết).`);
          } else {
            setSnackbarMessage(`Hoàn tất xử lý cho "${applicationDetails.templateName}". Solver không tìm thấy giải pháp hoặc không tạo ca nào.`);
          }
          setSnackbarSeverity((stats && (stats.totalAssignedShifts > 0 || scheduledShifts.length > 0)) ? "success" : "warning");
          setSnackbarOpen(true); handleCloseApplyModal();
        },
        { onError: (err) => { /* ... error handling ... */
            let errorMessage = 'Lỗi không xác định từ server.';
            if (err.response) {
              if (err.response.status === 422 && err.response.data && err.response.data.statistics) {
                errorMessage = `Không thể tạo lịch. Xem log thống kê để biết thêm chi tiết.`;
                setRosterStatistics(err.response.data.statistics); setIsStatsModalOpen(true);
              } else if (err.response.status === 422) {
                errorMessage = "Không thể tạo lịch: Các ràng buộc quá chặt, không có nhân viên phù hợp hoặc không tìm thấy giải pháp.";
              } else if (err.response.data && err.response.data.message) { errorMessage = `Lỗi ${err.response.status}: ${err.response.data.message}`;
              } else if (err.response.statusText) { errorMessage = `Lỗi ${err.response.status}: ${err.response.statusText}`;
              } else { errorMessage = `Lỗi ${err.response.status} từ server.`; }
            } else if (err.request) { errorMessage = "Không nhận được phản hồi từ server. Vui lòng kiểm tra kết nối mạng."; }
            else { errorMessage = `Lỗi khi gửi yêu cầu: ${err.message}`; }
            console.error("Lỗi khi gọi API xếp lịch:", err);
            setSnackbarMessage(errorMessage); setSnackbarSeverity("error"); setSnackbarOpen(true);
          }
        }, rosterGenerationPayload );
    } catch (error) { /* ... error handling ... */
      console.error("Lỗi cục bộ khi chuẩn bị gọi API xếp lịch:", error);
      setSnackbarMessage("Lỗi cục bộ khi chuẩn bị gửi yêu cầu."); setSnackbarSeverity("error"); setSnackbarOpen(true);
      handleCloseApplyModal();
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleOpenUndoConfirmModal = () => { if (lastGeneratedShiftIds.length === 0) { setSnackbarMessage("Không có ca nào vừa được tạo để hoàn tác."); setSnackbarSeverity("info"); setSnackbarOpen(true); return; } setIsUndoConfirmModalOpen(true); };
  const handleCloseUndoConfirmModal = () => { setIsUndoConfirmModalOpen(false); };
  const executeUndoLastGeneratedShifts = async () => {
    handleCloseUndoConfirmModal();
    if (lastGeneratedShiftIds.length === 0) return;
    setIsUndoingShifts(true);
    try {
      await request( "delete", `/shifts`, () => { setSnackbarMessage(`Đã hoàn tác ${lastGeneratedShiftIds.length} ca thành công!`); setSnackbarSeverity("success"); setLastGeneratedShiftIds([]); }, { onError: (err) => { console.error("Lỗi khi hoàn tác ca:", err.response?.data || err.message); setSnackbarMessage("Lỗi khi hoàn tác các ca đã tạo."); setSnackbarSeverity("error"); } }, lastGeneratedShiftIds );
    } catch (error) { console.error("Lỗi cục bộ khi hoàn tác:", error); setSnackbarMessage("Lỗi cục bộ khi gửi yêu cầu hoàn tác."); setSnackbarSeverity("error");
    } finally { setIsUndoingShifts(false); setSnackbarOpen(true); if (isStatsModalOpen) setIsStatsModalOpen(false); }
  };
  const handleSnackbarClose = (event, reason) => { if (reason === 'clickaway') { return; } setSnackbarOpen(false); };
  const handleCloseStatsModal = () => setIsStatsModalOpen(false);

  // Updated loading condition: Show full page loading only during the very initial data fetch.
  if (loadingApis) {
    return ( <ThemeProvider theme={theme}> <CssBaseline /> <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}> <CircularProgress size={50} /> <Typography variant="h6" sx={{ml:2}}>Đang tải dữ liệu khởi tạo...</Typography> </Box> </ThemeProvider> )
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {/* More specific Backdrop for actions that affect the list or submit forms */}
      <Backdrop sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.modal + 1 }} open={isSubmitting || isUndoingShifts || loadingTemplatesAction }>
        <CircularProgress color="inherit" sx={{mr: 2}}/>
        <Typography variant="h6">
          {isUndoingShifts ? "Đang hoàn tác..." :
            isSubmitting ? "Đang xử lý..." :
              loadingTemplatesAction ? "Đang tải danh sách cấu hình..." : "Đang xử lý..." // Fallback
          }
        </Typography>
      </Backdrop>

      <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
        <Container component="main" maxWidth="lg" sx={{ py: 0, flexGrow: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 2, px: {xs: 0, sm: 0}, backgroundColor: theme.palette.background.default, zIndex: 10, borderBottom: `1px solid ${theme.palette.divider}`, }}>
            <Typography variant="h5" sx={{ color: 'primary.dark', fontWeight: 700, display:'flex', alignItems:'center' }}>
              <ArticleIcon sx={{mr:1, color: 'primary.main', fontSize: '1.7rem'}} />
              Quản Lý Bộ Cấu Hình Xếp Lịch
            </Typography>
            <Button color="primary" startIcon={<AddIcon />} onClick={handleOpenTemplateModalForNew} variant="contained" disabled={isSubmitting || isUndoingShifts || loadingTemplatesAction}>
              Tạo Mới
            </Button>
          </Box>
          <Box sx={{flexGrow: 1, overflowY: 'auto', pt: 2, pr:0.5, mr: -0.5 }}>
            {/* Show loading for template list only if it's the initial load and list is empty */}
            {loadingTemplatesAction && configTemplates.length === 0 ? (
              <Box sx={{display:'flex', justifyContent:'center', alignItems:'center', height: '50vh'}}><CircularProgress/></Box>
            ) : (
              <TemplateListDisplay
                templates={configTemplates}
                onEdit={handleOpenTemplateModalForEdit}
                onDelete={openDeleteConfirmModal}
                onOpenApplyModal={handleOpenApplyModal}
                isSubmittingRoster={isSubmitting || isUndoingShifts}
              />
            )}
          </Box>
        </Container>

        <Modal open={isTemplateModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick' && !isSubmitting) handleCloseTemplateModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
          <Paper sx={{ width: '95%', maxWidth: '900px', maxHeight: 'calc(95vh - 32px)', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
            {isTemplateModalOpen && <TemplateConfigForm onSave={handleSaveTemplate} onCancel={handleCloseTemplateModal} initialTemplateData={editingTemplate} isSubmitting={isSubmitting} />}
          </Paper>
        </Modal>

        {templateToApply && (
          <Modal open={isApplyModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick' && !isSubmitting) handleCloseApplyModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
            <Paper sx={{ width: '95%', maxWidth: '750px', maxHeight: '90vh', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
              {isApplyModalOpen &&
                <ApplyConfigForm
                  onApply={handleActualApplyAndRoster}
                  onCancel={handleCloseApplyModal}
                  configTemplate={templateToApply}
                  departments={departments} // Pass the state departments
                  jobPositions={jobPositions} // Pass the state jobPositions
                  isSubmittingRoster={isSubmitting}
                />
              }
            </Paper>
          </Modal>
        )}

        {rosterStatistics && (
          <Dialog open={isStatsModalOpen} onClose={handleCloseStatsModal} maxWidth="lg" fullWidth PaperProps={{ sx: { maxHeight: '90vh', display: 'flex', flexDirection: 'column'} }}>
            <DialogTitle sx={{ backgroundColor: 'primary.dark', color: 'white', display: 'flex', justifyContent: 'space-between', alignItems: 'center', py:1.5 }}>
              <Box sx={{display: 'flex', alignItems: 'center'}}> <BarChartIcon sx={{ mr: 1 }} /> Thống Kê Kết Quả Xếp Lịch </Box>
              <IconButton onClick={handleCloseStatsModal} sx={{color: 'white'}}><CloseIcon /></IconButton>
            </DialogTitle>
            <DialogContent dividers sx={{p:2, flexGrow: 1, overflowY: 'auto'}}>
              <Typography variant="body2" gutterBottom> Cho giai đoạn: <strong>{rosterStatistics.rosterStartDate}</strong> đến <strong>{rosterStatistics.rosterEndDate}</strong> </Typography>
              <Divider sx={{my:1}}/>
              <Typography variant="h6" gutterBottom>Tổng Quan</Typography>
              <Paper variant="outlined" sx={{p:1.5, mb:2}}>
                <Typography>Tổng số ca đã xếp: <strong>{rosterStatistics.totalAssignedShifts ?? 'N/A'}</strong></Typography>
                <Typography>Tổng số giờ làm đã xếp: <strong>{rosterStatistics.totalAssignedHours?.toFixed(2) ?? 'N/A'}</strong></Typography>
                {rosterStatistics.fairnessHours && ( <> <Typography sx={{mt:1, fontWeight:'bold'}}>Phân Bổ Giờ Làm:</Typography> <Typography>Giờ làm tối thiểu/NV: {rosterStatistics.fairnessHours.minEmployeeValue?.toFixed(2) ?? 'N/A'}</Typography> <Typography>Giờ làm tối đa/NV: {rosterStatistics.fairnessHours.maxEmployeeValue?.toFixed(2) ?? 'N/A'}</Typography> <Typography>Chênh lệch (Max-Min): {rosterStatistics.fairnessHours.rangeValue?.toFixed(2) ?? 'N/A'} giờ</Typography> </>)}
                {rosterStatistics.fairnessNightShifts && ( <> <Typography sx={{mt:1, fontWeight:'bold'}}>Phân Bổ Ca Đêm:</Typography> <Typography>Ca đêm tối thiểu/NV: {rosterStatistics.fairnessNightShifts.minEmployeeCount ?? 'N/A'}</Typography> <Typography>Ca đêm tối đa/NV: {rosterStatistics.fairnessNightShifts.maxEmployeeCount ?? 'N/A'}</Typography> <Typography>Chênh lệch: {rosterStatistics.fairnessNightShifts.rangeCount ?? 'N/A'} ca</Typography> </>)}
                {rosterStatistics.fairnessSundayShifts && ( <> <Typography sx={{mt:1, fontWeight:'bold'}}>Phân Bổ Ca Chủ Nhật:</Typography> <Typography>Ca CN tối thiểu/NV: {rosterStatistics.fairnessSundayShifts.minEmployeeCount ?? 'N/A'}</Typography> <Typography>Ca CN tối đa/NV: {rosterStatistics.fairnessSundayShifts.maxEmployeeCount ?? 'N/A'}</Typography> <Typography>Chênh lệch: {rosterStatistics.fairnessSundayShifts.rangeCount ?? 'N/A'} ca</Typography> </>)}
              </Paper>
              <Typography variant="h6" gutterBottom>Chi Tiết Theo Nhân Viên</Typography>
              <Paper variant="outlined" sx={{maxHeight: 300, overflowY: 'auto', mb:2}}>
                <List dense>
                  {(rosterStatistics.employeeStats && rosterStatistics.employeeStats.length > 0) ? rosterStatistics.employeeStats.map((emp, index) => (
                    <React.Fragment key={emp.staffCode || index}> <ListItem> <ListItemText primaryTypographyProps={{fontWeight:'500', color:'text.primary'}} secondaryTypographyProps={{fontSize: '0.8rem', color:'text.secondary'}} primary={`${emp.employeeName} (Mã NV: ${emp.staffCode})`} secondary={`Tổng ca: ${emp.totalShifts}, Tổng giờ: ${emp.totalHours?.toFixed(2)}, Ca Đêm: ${emp.nightShifts}, Ca T7: ${emp.saturdayShiftsWorked}, Ca CN: ${emp.sundayShiftsWorked}, Chuỗi LT max: ${emp.maxConsecutiveWorkDays}`} /> </ListItem> {index < rosterStatistics.employeeStats.length - 1 && <Divider component="li" />} </React.Fragment>
                  )) : <ListItem><ListItemText primary="Không có dữ liệu chi tiết nhân viên." /></ListItem>}
                </List>
              </Paper>
              {rosterStatistics.detailedRosterLog && rosterStatistics.detailedRosterLog.length > 0 && (
                <> <Typography variant="h6" gutterBottom>Log Chi Tiết Lịch</Typography>
                  <Paper variant="outlined" sx={{maxHeight: 350, overflowY: 'auto', p:1.5, backgroundColor: 'grey.50'}}>
                    {rosterStatistics.detailedRosterLog.map((line, idx) => ( <Typography key={idx} component="div" variant="caption" sx={{whiteSpace: 'pre-wrap', fontFamily:'monospace', fontSize: '0.75rem', lineHeight: 1.3}}> {line.replace(/📅|🕒|👤|🎉|📊|❌|▶|✅|➡|=========================================================|\n/g, '').trim()} </Typography> ))}
                  </Paper> </>
              )}
            </DialogContent>
            <DialogActions sx={{borderTop: '1px solid', borderColor:'divider', p: '12px 24px', backgroundColor:'grey.100'}}>
              {lastGeneratedShiftIds.length > 0 && ( <Button onClick={handleOpenUndoConfirmModal} color="warning" variant="outlined" startIcon={<UndoIcon />} disabled={isUndoingShifts || isSubmitting}> {isUndoingShifts ? "Đang Hoàn Tác..." : "Hoàn Tác Lịch Vừa Tạo"} </Button> )}
              <Box sx={{ flexGrow: 1 }} />
              <Button onClick={handleCloseStatsModal} color="inherit">Đóng</Button>
              <Button onClick={() => downloadCSV(rosterStatistics)} color="primary" variant="contained" startIcon={<DownloadIcon />}> Tải CSV Thống Kê </Button>
            </DialogActions>
          </Dialog>
        )}

        <DeleteConfirmationModal
          open={isDeleteConfirmModalOpen}
          onClose={closeDeleteConfirmModal}
          onSubmit={handleDeleteTemplateConfirmed}
          title="Xác nhận Xóa Bộ Cấu Hình"
          info={`Bạn có chắc chắn muốn xóa bộ cấu hình này không? Hành động này sẽ xóa vĩnh viễn và không thể hoàn tác.`}
          confirmLabel="Đồng Ý Xóa"
          cancelLabel="Hủy Bỏ"
        />

        <DeleteConfirmationModal
          open={isUndoConfirmModalOpen}
          onClose={handleCloseUndoConfirmModal}
          onSubmit={executeUndoLastGeneratedShifts}
          title="Xác nhận Hoàn Tác Lịch"
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
