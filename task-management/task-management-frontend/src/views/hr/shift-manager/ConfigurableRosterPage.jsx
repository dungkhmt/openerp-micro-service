// src/features/rosterConfiguration/ConfigurableRosterPage.jsx
import React, {useCallback, useEffect, useState} from 'react';
import {
  Alert as MuiAlert,
  Backdrop,
  Box,
  Button,
  CircularProgress,
  CssBaseline,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider, Grid,
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

import {theme} from '../theme';
import {request}from "@/api";

import TemplateConfigForm from './TemplateConfigForm';
import ApplyConfigForm from './ApplyConfigForm';
import TemplateListDisplay from './TemplateListDisplay';
import DeleteConfirmationModal from "../modals/DeleteConfirmationModal.jsx";

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const downloadCSV = (statistics, fileName = "roster_statistics.csv") => {
  if (!statistics) {
    console.warn("Không có dữ liệu thống kê để tải.");
    return;
  }

  let csvContent = "\uFEFF"; // BOM for UTF-8

  csvContent += "Thông tin Kỳ Xếp Lịch\r\n";
  csvContent += `Ngày bắt đầu,"${statistics.rosterStartDate || 'N/A'}"\r\n`;
  csvContent += `Ngày kết thúc,"${statistics.rosterEndDate || 'N/A'}"\r\n`;
  csvContent += "\r\n";

  csvContent += "Thống Kê Tổng Quan\r\n";
  csvContent += `Tổng số ca đã xếp,"${statistics.totalAssignedShifts || 'N/A'}"\r\n`;
  csvContent += `Tổng số giờ làm đã xếp,"${statistics.totalAssignedHours?.toFixed(2) || 'N/A'}"\r\n`;
  if (statistics.fairnessHours) {
    csvContent += "Phân bổ giờ làm (Công bằng)\r\n";
    csvContent += `Giờ làm tối thiểu/NV,"${statistics.fairnessHours.minEmployeeValue?.toFixed(2) || 'N/A'}"\r\n`;
    csvContent += `Giờ làm tối đa/NV,"${statistics.fairnessHours.maxEmployeeValue?.toFixed(2) || 'N/A'}"\r\n`;
    csvContent += `Chênh lệch (giờ),"${statistics.fairnessHours.rangeValue?.toFixed(2) || 'N/A'}"\r\n`;
  }
  // ... (các phần CSV khác tương tự, có thể thêm tiếng Việt cho header)
  csvContent += "\r\n";

  csvContent += "Thống Kê Chi Tiết Theo Nhân Viên\r\n";
  csvContent += "Mã NV,Tên Nhân Viên,Tổng Ca,Tổng Giờ,Ca Đêm,Ca Thứ 7,Ca Chủ Nhật,Chuỗi Ngày Làm Việc Liên Tục Tối Đa\r\n";
  if (statistics.employeeStats && statistics.employeeStats.length > 0) {
    statistics.employeeStats.forEach(emp => {
      csvContent += `"${emp.staffCode || ''}","${emp.employeeName || ''}","${emp.totalShifts || 0}","${emp.totalHours?.toFixed(2) || 0}","${emp.nightShifts || 0}","${emp.saturdayShiftsWorked || 0}","${emp.sundayShiftsWorked || 0}","${emp.maxConsecutiveWorkDays || 0}"\r\n`;
    });
  } else {
    csvContent += "Không có thống kê chi tiết theo nhân viên.\r\n";
  }
  csvContent += "\r\n";

  if (statistics.detailedRosterLog && statistics.detailedRosterLog.length > 0) {
    csvContent += "Log Chi Tiết Quá Trình Xếp Lịch\r\n";
    statistics.detailedRosterLog.forEach(logLine => {
      let cleanLine = logLine.replace(/📅|🕒|👤|🎉|📊|❌|▶|✅|➡|=========================================================|\n/g, '').trim();
      cleanLine = cleanLine.replace(/^[\s\t]+/, '');
      cleanLine = `"${cleanLine.replace(/"/g, '""')}"\r\n`;
      if (cleanLine.length > 3) {
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
    toast.error("Trình duyệt của bạn không hỗ trợ tải file trực tiếp.");
    console.warn("Trình duyệt của bạn không hỗ trợ tải file trực tiếp.");
  }
};


const ConfigurableRosterPageInternal = () => {
  const [isTemplateModalOpen, setIsTemplateModalOpen] = useState(false);
  const [isApplyModalOpen, setIsApplyModalOpen] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState(null);
  const [templateToApply, setTemplateToApply] = useState(null);
  const [configTemplates, setConfigTemplates] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);
  const [loadingApis, setLoadingApis] = useState(true);
  const [loadingTemplatesAction, setLoadingTemplatesAction] = useState(false);

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('info'); // Default 'info'
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [isStatsModalOpen, setIsStatsModalOpen] = useState(false);
  const [rosterStatistics, setRosterStatistics] = useState(null);
  const [lastGeneratedShiftIds, setLastGeneratedShiftIds] = useState([]);
  const [isUndoingShifts, setIsUndoingShifts] = useState(false);
  const [isUndoConfirmModalOpen, setIsUndoConfirmModalOpen] = useState(false);
  const [templateIdToDelete, setTemplateIdToDelete] = useState(null);
  const [isDeleteConfirmModalOpen, setIsDeleteConfirmModalOpen] = useState(false);

  const ensureTemplateStructure = (template) => {
    return {
      id: template.id,
      templateName: template.template_name || template.templateName || `Bộ cấu hình ${template.id}`.slice(0,10),
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
    try {
      await request( "get", "/departments", (res) => {
        const transformed = (res.data.data || []).map(dept => ({ departmentCode: dept.department_code, departmentName: dept.department_name }));
        setDepartments(transformed);
      }, { onError: (err) => console.error("Error fetching departments:", err.response?.data || err.message) }, null, { params: { status: "ACTIVE", pageSize: 1000 } } ); // Thêm pageSize
    } catch (error) {
      console.error("Exception in fetchDepartmentsAPI:", error);
    }
  }, []);

  const fetchJobPositionsAPI = useCallback(async () => {
    try {
      await request( "get", "/jobs", (res) => {
        const transformed = (res.data.data || []).map(job => ({ code: job.code, name: job.name }));
        setJobPositions(transformed);
      }, { onError: (err) => console.error("Error fetching job positions:", err.response?.data || err.message) }, null, { params: { status: "ACTIVE", pageSize: 1000 } } ); // Thêm pageSize
    } catch (error) {
      console.error("Exception in fetchJobPositionsAPI:", error);
    }
  }, []);

  useEffect(() => {
    let active = true;
    const loadInitialData = async () => {
      setLoadingApis(true);
      try {
        await Promise.all([
          fetchDepartmentsAPI(),
          fetchJobPositionsAPI(),
          fetchAllRosterTemplates(false)
        ]);
      } catch (error) {
        console.error("Error loading initial API data:", error);
        setSnackbarMessage("Lỗi tải dữ liệu khởi tạo cho trang.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      } finally {
        if (active) setLoadingApis(false);
      }
    };
    loadInitialData();
    return () => { active = false; };
  }, [fetchDepartmentsAPI, fetchJobPositionsAPI, fetchAllRosterTemplates]);


  const handleOpenTemplateModalForNew = () => { setEditingTemplate(null); setIsTemplateModalOpen(true); };
  const handleOpenTemplateModalForEdit = (template) => { setEditingTemplate(ensureTemplateStructure(template)); setIsTemplateModalOpen(true); };
  const handleCloseTemplateModal = () => { if(!isSubmitting) { setIsTemplateModalOpen(false); setEditingTemplate(null); }};

  const handleSaveTemplate = async (templateData) => {
    setIsSubmitting(true);
    setLoadingTemplatesAction(true);
    const isUpdating = !!editingTemplate?.id;
    const endpoint = isUpdating ? `/roster-templates/${editingTemplate.id}` : "/roster-templates";
    const method = isUpdating ? "put" : "post";

    const payload = {
      template_name: templateData.templateName,
      defined_shifts: templateData.definedShifts || [],
      active_hard_constraints: templateData.activeHardConstraints || {}
      // department_filter và job_position_filter sẽ được cập nhật riêng khi áp dụng nếu cần
    };
    if (isUpdating) {
      payload.id = editingTemplate.id;
    }

    try {
      await request( method, endpoint, (res) => {
          setSnackbarMessage(`Đã ${isUpdating ? 'cập nhật' : 'tạo mới'} bộ cấu hình thành công!`);
          setSnackbarSeverity("success");
          fetchAllRosterTemplates(false);
          handleCloseTemplateModal();
        },
        { onError: (err) => {
            console.error("Error saving template:", err.response?.data || err.message);
            setSnackbarMessage(`Lỗi khi ${isUpdating ? 'cập nhật' : 'tạo mới'} bộ cấu hình: ${err.response?.data?.message || 'Lỗi không xác định'}`);
            setSnackbarSeverity("error");
          }
        }, payload );
    } catch (error) {
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
        { onError: (err) => {
            console.error("Error deleting template:", err.response?.data || err.message);
            setSnackbarMessage(`Lỗi khi xóa bộ cấu hình: ${err.response?.data?.message || 'Lỗi không xác định'}`);
            setSnackbarSeverity("error");
          }
        }
      );
    } catch (error) {
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
  const handleCloseApplyModal = () => { if(!isSubmitting) {setIsApplyModalOpen(false); setTemplateToApply(null);} };

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
          await request( "patch", `/roster-templates/${applicationDetails.templateId}/filters`, // Giả sử có endpoint riêng để patch filter
            () => { filtersSuccessfullyPatched = true; },
            { onError: (err) => {
                console.error("Error patching template filters:", err.response?.data || err.message);
                setSnackbarMessage(`Lỗi khi cập nhật bộ lọc. Tiếp tục xếp lịch với bộ lọc mới.`);
                setSnackbarSeverity("warning");
                setSnackbarOpen(true);
              }
            }, filterPayload );
        } catch (patchError) {
          console.error("Exception during filter patch request:", patchError);
          setSnackbarMessage(`Lỗi nghiêm trọng khi cập nhật bộ lọc. Tiếp tục xếp lịch với bộ lọc mới.`);
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
        }
      } else {
        filtersSuccessfullyPatched = true; // Coi như thành công nếu không có gì thay đổi
      }
    }


    const rosterGenerationPayload = {
      template_id: applicationDetails.templateId, // Gửi template_id thay vì toàn bộ template_name
      start_date: applicationDetails.startDate,
      end_date: applicationDetails.endDate,
      department_codes: applicationDetails.departmentCodes,
      job_position_codes: applicationDetails.jobPositionCodes,
      // defined_shifts và active_hard_constraints sẽ được lấy từ template trên backend dựa vào template_id
    };
    const API_ENDPOINT_GENERATE = "/roster/generate-from-template"; // Endpoint mới

    try {
      await request( "post", API_ENDPOINT_GENERATE, (res) => {
          const solution = res.data?.data || res.data; // Kiểm tra cả res.data.data
          const stats = solution?.statistics || null;
          const createdIds = solution?.created_shift_ids || solution?.createdShiftIds || [];
          setLastGeneratedShiftIds(createdIds);
          if (filtersSuccessfullyPatched) fetchAllRosterTemplates(false);

          if (stats) {
            setRosterStatistics(stats); setIsStatsModalOpen(true);
            setSnackbarMessage(`Xếp lịch thành công! ${createdIds.length} ca mới đã được tạo.`);
            setSnackbarSeverity("success");
          } else {
            setSnackbarMessage(`Hoàn tất xử lý. ${createdIds.length > 0 ? `${createdIds.length} ca mới được tạo.` : 'Không có ca nào được tạo.'} (Không có thống kê chi tiết).`);
            setSnackbarSeverity(createdIds.length > 0 ? "success" : "info");
          }
          setSnackbarOpen(true); handleCloseApplyModal();
        },
        { onError: (err) => {
            let errorMessage = 'Lỗi không xác định từ server.';
            if (err.response) {
              const errorData = err.response.data;
              if (err.response.status === 422 && errorData && errorData.statistics) {
                errorMessage = errorData.message || `Không thể tạo lịch. Xem log thống kê.`;
                setRosterStatistics(errorData.statistics); setIsStatsModalOpen(true);
              } else if (errorData && errorData.message) { errorMessage = `Lỗi ${err.response.status}: ${errorData.message}`;
              } else if (err.response.statusText) { errorMessage = `Lỗi ${err.response.status}: ${err.response.statusText}`;
              } else { errorMessage = `Lỗi ${err.response.status} từ server.`; }
            } else if (err.request) { errorMessage = "Không nhận được phản hồi từ server."; }
            else { errorMessage = `Lỗi khi gửi yêu cầu: ${err.message}`; }
            console.error("Lỗi khi gọi API xếp lịch:", err);
            setSnackbarMessage(errorMessage); setSnackbarSeverity("error"); setSnackbarOpen(true);
          }
        }, rosterGenerationPayload );
    } catch (error) {
      console.error("Lỗi cục bộ khi chuẩn bị gọi API xếp lịch:", error);
      setSnackbarMessage("Lỗi cục bộ khi chuẩn bị gửi yêu cầu."); setSnackbarSeverity("error"); setSnackbarOpen(true);
      // Không đóng apply modal ở đây để người dùng có thể thử lại nếu là lỗi mạng tạm thời
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
      await request( "delete", `/shifts/bulk-delete`, () => { setSnackbarMessage(`Đã hoàn tác ${lastGeneratedShiftIds.length} ca thành công!`); setSnackbarSeverity("success"); setLastGeneratedShiftIds([]); }, { onError: (err) => { console.error("Lỗi khi hoàn tác ca:", err.response?.data || err.message); setSnackbarMessage("Lỗi khi hoàn tác các ca đã tạo."); setSnackbarSeverity("error"); } }, { shift_ids: lastGeneratedShiftIds } ); // Gửi ID trong body
    } catch (error) { console.error("Lỗi cục bộ khi hoàn tác:", error); setSnackbarMessage("Lỗi cục bộ khi gửi yêu cầu hoàn tác."); setSnackbarSeverity("error");
    } finally { setIsUndoingShifts(false); setSnackbarOpen(true); if (isStatsModalOpen) setIsStatsModalOpen(false); }
  };
  const handleSnackbarClose = (event, reason) => { if (reason === 'clickaway') { return; } setSnackbarOpen(false); };
  const handleCloseStatsModal = () => setIsStatsModalOpen(false);


  if (loadingApis) {
    return (
      // ThemeProvider và CssBaseline đã được áp dụng ở ConfigurableRosterPage
      <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 64px)', bgcolor: 'background.default', mr:2 }}>
        <CircularProgress size={50} />
        <Typography variant="h6" sx={{ml:2, color: 'text.secondary'}}>Đang tải dữ liệu...</Typography>
      </Box>
    )
  }

  const modalTitleStyle = { fontSize: '1.15rem', fontWeight: 600 }; // Style cho tiêu đề modal

  return (
    // ThemeProvider và CssBaseline đã có ở ConfigurableRosterPage
    <>
      <Backdrop sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }} open={isSubmitting || isUndoingShifts || loadingTemplatesAction }> {/* Tăng zIndex cho Backdrop */}
        <CircularProgress color="inherit" sx={{mr: 2}}/>
        <Typography variant="body1">
          {isUndoingShifts ? "Đang hoàn tác..." :
            isSubmitting ? "Đang xử lý yêu cầu..." :
              loadingTemplatesAction ? "Đang tải danh sách cấu hình..." : "Vui lòng chờ..."
          }
        </Typography>
      </Backdrop>

      <Box sx={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 64px)', overflow: 'hidden', bgcolor: 'background.default', mr:2 }}>
        {/* Header Paper */}
        <Paper sx={{
          p: 2,
          mb: 0,
          borderBottom: `1px solid ${theme.palette.divider}`,
          borderRadius:0,
          boxShadow: 'none'
        }}>
          <Grid container justifyContent="space-between" alignItems="center">
            <Grid item>
              <Typography variant="h4" component="h1" sx={{ color: 'text.primary', fontWeight: 600, display:'flex', alignItems:'center' }}>
                <ArticleIcon sx={{mr:1.5, color: 'primary.main', fontSize: '2rem'}} />
                Quản Lý Bộ Cấu Hình Xếp Lịch
              </Typography>
            </Grid>
            <Grid item>
              <Button color="primary" startIcon={<AddIcon />} onClick={handleOpenTemplateModalForNew} variant="contained" disabled={isSubmitting || isUndoingShifts || loadingTemplatesAction}>
                Tạo Mới Cấu Hình
              </Button>
            </Grid>
          </Grid>
        </Paper>

        {/* Content Area */}
        <Box sx={{flexGrow: 1, overflowY: 'auto', p: 2}} className= "custom-scrollbar">
          {loadingTemplatesAction && configTemplates.length === 0 ? (
            <Box sx={{display:'flex', justifyContent:'center', alignItems:'center', height: 'calc(100% - 40px)'}}><CircularProgress/></Box> // Điều chỉnh height
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
      </Box>

      {/* Modals */}
      <Modal open={isTemplateModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick' && !isSubmitting) handleCloseTemplateModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
        <Paper sx={{ width: '95%', maxWidth: '1000px', maxHeight: 'calc(95vh - 32px)', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
          {/* Truyền titleProps vào TemplateConfigForm nếu nó hỗ trợ, hoặc style DialogTitle bên trong nó */}
          {isTemplateModalOpen && <TemplateConfigForm onSave={handleSaveTemplate} onCancel={handleCloseTemplateModal} initialTemplateData={editingTemplate} isSubmitting={isSubmitting} titleStyle={modalTitleStyle} />}
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
                departments={departments}
                jobPositions={jobPositions}
                isSubmittingRoster={isSubmitting}
                titleStyle={modalTitleStyle}
              />
            }
          </Paper>
        </Modal>
      )}

      {rosterStatistics && (
        <Dialog open={isStatsModalOpen} onClose={handleCloseStatsModal} maxWidth="lg" fullWidth
                PaperProps={{ sx: { maxHeight: 'calc(90vh - 64px)', display: 'flex', flexDirection: 'column', m: {xs:1, sm:2} }}} // Thêm margin cho Dialog
        >
          <DialogTitle sx={{ ...modalTitleStyle, backgroundColor: 'primary.main', color: 'primary.contrastText', display: 'flex', justifyContent: 'space-between', alignItems: 'center', py:1.5, px:2 }}>
            <Box sx={{display: 'flex', alignItems: 'center'}}> <BarChartIcon sx={{ mr: 1 }} /> Thống Kê Kết Quả Xếp Lịch </Box>
            <IconButton onClick={handleCloseStatsModal} sx={{color: 'primary.contrastText'}}><CloseIcon /></IconButton>
          </DialogTitle>
          <DialogContent dividers sx={{p:2, flexGrow: 1,
            '&::-webkit-scrollbar': { width: '6px' },
            '&::-webkit-scrollbar-track': { background: theme.palette.grey[100] },
            '&::-webkit-scrollbar-thumb': { background: theme.palette.grey[300] },
            '&::-webkit-scrollbar-thumb:hover': { background: theme.palette.grey[400] }
          }}>
            <Typography variant="body1" gutterBottom> Cho giai đoạn: <strong>{rosterStatistics.rosterStartDate}</strong> đến <strong>{rosterStatistics.rosterEndDate}</strong> </Typography>
            <Divider sx={{my:1.5}}/>
            <Typography variant="h6" gutterBottom sx={{fontWeight:500}}>Tổng Quan</Typography>
            <Paper variant="outlined" sx={{p:1.5, mb:2, bgcolor: 'background.default'}}>
              <Typography>Tổng số ca đã xếp: <strong>{rosterStatistics.totalAssignedShifts ?? 'N/A'}</strong></Typography>
              <Typography>Tổng số giờ làm đã xếp: <strong>{rosterStatistics.totalAssignedHours?.toFixed(2) ?? 'N/A'}</strong></Typography>
              {rosterStatistics.fairnessHours && ( <> <Typography sx={{mt:1, fontWeight:'500'}}>Phân Bổ Giờ Làm:</Typography> <Typography variant="body2">Giờ làm tối thiểu/NV: {rosterStatistics.fairnessHours.minEmployeeValue?.toFixed(2) ?? 'N/A'}</Typography> <Typography variant="body2">Giờ làm tối đa/NV: {rosterStatistics.fairnessHours.maxEmployeeValue?.toFixed(2) ?? 'N/A'}</Typography> <Typography variant="body2">Chênh lệch (Max-Min): {rosterStatistics.fairnessHours.rangeValue?.toFixed(2) ?? 'N/A'} giờ</Typography> </>)}
            </Paper>
            <Typography variant="h6" gutterBottom sx={{fontWeight:500}}>Chi Tiết Theo Nhân Viên</Typography>
            <Paper variant="outlined" sx={{maxHeight: 250, overflowY: 'auto', mb:2, bgcolor: 'background.default'}}>
              <List dense disablePadding>
                {(rosterStatistics.employeeStats && rosterStatistics.employeeStats.length > 0) ? rosterStatistics.employeeStats.map((emp, index) => (
                  <React.Fragment key={emp.staffCode || index}>
                    <ListItem sx={{py:0.5}}>
                      <ListItemText
                        primaryTypographyProps={{fontWeight:'500', color:'text.primary', fontSize: '0.9rem'}}
                        secondaryTypographyProps={{fontSize: '0.75rem', color:'text.secondary'}}
                        primary={`${emp.employeeName} (Mã NV: ${emp.staffCode || 'N/A'})`}
                        secondary={`Tổng ca: ${emp.totalShifts}, Tổng giờ: ${emp.totalHours?.toFixed(2)}, Ca Đêm: ${emp.nightShifts}, Ca T7: ${emp.saturdayShiftsWorked}, Ca CN: ${emp.sundayShiftsWorked}, Chuỗi LT max: ${emp.maxConsecutiveWorkDays}`} />
                    </ListItem>
                    {index < rosterStatistics.employeeStats.length - 1 && <Divider component="li" variant="inset" />}
                  </React.Fragment>
                )) : <ListItem><ListItemText primary="Không có dữ liệu chi tiết nhân viên." /></ListItem>}
              </List>
            </Paper>
            {rosterStatistics.detailedRosterLog && rosterStatistics.detailedRosterLog.length > 0 && (
              <> <Typography variant="h6" gutterBottom sx={{fontWeight:500}}>Log Chi Tiết Quá Trình Xếp Lịch</Typography>
                <Paper variant="outlined" sx={{maxHeight: 300, overflowY: 'auto', p:1.5, backgroundColor: theme.palette.grey[50]}}> {/* Nền nhạt hơn cho log */}
                  {rosterStatistics.detailedRosterLog.map((line, idx) => ( <Typography key={idx} component="div" variant="caption" sx={{whiteSpace: 'pre-wrap', fontFamily:'monospace', fontSize: '0.7rem', lineHeight: 1.25}}> {line.replace(/📅|🕒|👤|🎉|📊|❌|▶|✅|➡|=========================================================|\n/g, '').trim()} </Typography> ))}
                </Paper> </>
            )}
          </DialogContent>
          <DialogActions sx={{borderTop: `1px solid ${theme.palette.divider}`, p: '12px 24px', backgroundColor:theme.palette.grey[50]}}> {/* Nền nhạt cho actions */}
            {lastGeneratedShiftIds.length > 0 && ( <Button onClick={handleOpenUndoConfirmModal} color="warning" variant="outlined" startIcon={<UndoIcon />} disabled={isUndoingShifts || isSubmitting}> {isUndoingShifts ? "Đang Hoàn Tác..." : "Hoàn Tác Lịch"} </Button> )}
            <Box sx={{ flexGrow: 1 }} />
            <Button onClick={handleCloseStatsModal} color="inherit">Đóng</Button>
            <Button onClick={() => downloadCSV(rosterStatistics)} color="primary" variant="contained" startIcon={<DownloadIcon />}> Tải CSV </Button>
          </DialogActions>
        </Dialog>
      )}

      <DeleteConfirmationModal
        open={isDeleteConfirmModalOpen}
        onClose={closeDeleteConfirmModal}
        onSubmit={handleDeleteTemplateConfirmed}
        title="Xác nhận Xóa Bộ Cấu Hình"
        info={`Bạn có chắc chắn muốn xóa vĩnh viễn bộ cấu hình này không?`}
        confirmLabel="Xóa"
        cancelLabel="Hủy"
        titleProps={{sx: modalTitleStyle}}
      />

      <DeleteConfirmationModal
        open={isUndoConfirmModalOpen}
        onClose={handleCloseUndoConfirmModal}
        onSubmit={executeUndoLastGeneratedShifts}
        title="Xác nhận Hoàn Tác Lịch"
        info={`Bạn có chắc chắn muốn hoàn tác ${lastGeneratedShiftIds.length} ca làm việc vừa tạo không?`}
        confirmLabel="Hoàn Tác"
        cancelLabel="Hủy"
        titleProps={{sx: modalTitleStyle}}
      />

      <Snackbar open={snackbarOpen} autoHideDuration={5000} onClose={handleSnackbarClose} anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}>
        <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </>
  );
}


export default function ConfigurableRosterPage() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ConfigurableRosterPageInternal />
    </ThemeProvider>
  );
}