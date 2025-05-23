// src/features/rosterConfiguration/ConfigurableRosterPage.jsx
import React, { useState, useMemo, useEffect, useCallback } from 'react';
import {
  Container, Box, Typography, Button, Modal, Paper,
  CssBaseline, ThemeProvider, CircularProgress, IconButton,
  Snackbar,
  Alert as MuiAlert,
  InputLabel // Đảm bảo InputLabel được import nếu dùng ở đâu đó (renderMultiSelect đã có)
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import ArticleIcon from '@mui/icons-material/Article';

import { theme } from './theme';
// import { mockApiRequest } from './api'; // BỎ DÒNG NÀY
import { request } from "@/api"; // <<==== SỬ DỤNG HÀM REQUEST CỦA BẠN

import TemplateConfigForm from './TemplateConfigForm';
import ApplyConfigForm from './ApplyConfigForm';
import TemplateListDisplay from './TemplateListDisplay';

let moduleDepartmentsForDisplay = [];
let moduleJobPositionsForDisplay = [];

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

export default function ConfigurableRosterPage() {
  const [isTemplateModalOpen, setIsTemplateModalOpen] = useState(false);
  const [isApplyModalOpen, setIsApplyModalOpen] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState(null);
  const [templateToApply, setTemplateToApply] = useState(null);

  const [configTemplates, setConfigTemplates] = useState(() => {
    try { const saved = localStorage.getItem('rosterTemplatesList_final_fix'); return saved ? JSON.parse(saved) : []; }
    catch (e) { console.error("Failed to load templates from localStorage", e); return []; }
  });

  const [departments, setDepartments] = useState([]); // State cho departments
  const [jobPositions, setJobPositions] = useState([]); // State cho jobPositions
  const [loadingApis, setLoadingApis] = useState(true);

  // Fetch Departments
  const fetchDepartmentsAPI = useCallback(async () => {
    try {
      request(
        "get",
        "/departments", // URL API của bạn
        (res) => {
          const transformed = (res.data.data || []).map(dept => ({
            departmentCode: dept.department_code,
            departmentName: dept.department_name,
          }));
          moduleDepartmentsForDisplay = transformed; // Cập nhật biến module
          setDepartments(transformed); // Cập nhật state
        },
        { onError: (err) => console.error("Error fetching departments:", err) },
        null, // Không có body cho GET
        { params: { status: "ACTIVE" } } // Params nếu cần
      );
    } catch (error) {
      console.error("Error fetching departments:", error);
    }
  }, []);

  // Fetch Job Positions
  const fetchJobPositionsAPI = useCallback(async () => {
    try {
      request(
        "get",
        "/jobs", // URL API của bạn
        (res) => {
          const transformed = (res.data.data || []).map(job => ({
            code: job.code,
            name: job.name,
          }));
          moduleJobPositionsForDisplay = transformed; // Cập nhật biến module
          setJobPositions(transformed); // Cập nhật state
        },
        { onError: (err) => console.error("Error fetching job positions:", err) },
        null,
        { params: { status: "ACTIVE" } }
      );
    } catch (error) {
      console.error("Error fetching job positions:", error);
    }
  }, []);

  useEffect(() => {
    let active = true;
    const loadInitialData = async () => {
      setLoadingApis(true);
      try {
        await Promise.all([
          fetchDepartmentsAPI(),
          fetchJobPositionsAPI()
        ]);
      } catch (error) {
        console.error("Error loading initial API data:", error);
      } finally {
        if (active) {
          setLoadingApis(false);
        }
      }
    };
    loadInitialData();
    return () => { active = false; };
  }, [fetchDepartmentsAPI, fetchJobPositionsAPI]);


  useEffect(() => {
    localStorage.setItem('rosterTemplatesList_final_fix', JSON.stringify(configTemplates));
  }, [configTemplates]);

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('info');
  const [isSubmittingRoster, setIsSubmittingRoster] = useState(false);

  const handleOpenTemplateModalForNew = () => { setEditingTemplate(null); setIsTemplateModalOpen(true); };
  const handleOpenTemplateModalForEdit = (template) => { setEditingTemplate(template); setIsTemplateModalOpen(true); };
  const handleCloseTemplateModal = () => { setIsTemplateModalOpen(false); setEditingTemplate(null); };

  const handleSaveTemplate = (templateData) => { /* ... như cũ ... */
    setConfigTemplates(prevList => {
      const existingIndex = prevList.findIndex(t => t.id === templateData.id);
      if (existingIndex > -1) {
        const newList = [...prevList]; newList[existingIndex] = templateData; return newList;
      } else { return [...prevList, templateData]; }
    });
    handleCloseTemplateModal();
    setSnackbarMessage("Đã lưu bộ cấu hình thành công!");
    setSnackbarSeverity("success");
    setSnackbarOpen(true);
  };

  const handleDeleteTemplate = (templateIdToDelete) => { /* ... như cũ ... */
    if (window.confirm("Bạn có chắc chắn muốn xóa bộ cấu hình này không? Thao tác này không thể hoàn tác.")) {
      setConfigTemplates(prevList => prevList.filter(t => t.id !== templateIdToDelete));
      setSnackbarMessage("Đã xóa bộ cấu hình!");
      setSnackbarSeverity("info");
      setSnackbarOpen(true);
    }
  };

  const handleOpenApplyModal = (template) => { /* ... như cũ ... */
    setTemplateToApply(template);
    setIsApplyModalOpen(true);
  };
  const handleCloseApplyModal = () => { /* ... như cũ ... */
    setIsApplyModalOpen(false);
    setTemplateToApply(null);
  };

  const handleActualApplyAndRoster = async (applicationDetails) => { /* ... logic gọi API fetch như cũ ... */
    const payload = {
      template_name: applicationDetails.templateName,
      start_date: applicationDetails.startDate,
      end_date: applicationDetails.endDate,
      department_codes: applicationDetails.departmentCodes,
      job_position_codes: applicationDetails.jobPositionCodes,
      defined_shifts: applicationDetails.shiftsAndConstraints.definedShifts,
      active_hard_constraints: applicationDetails.shiftsAndConstraints.activeHardConstraints
    };

    console.log("DỮ LIỆU GỬI ĐI ĐỂ XẾP LỊCH (đã chuyển đổi):", payload);

    const API_ENDPOINT = "/api/roster/generate";

    try {
      request(
        "post",
        API_ENDPOINT,
        (res) => {
          setIsSubmittingRoster(false);
          const scheduledShifts = res.data;
          console.log("Lịch đã xếp nhận được:", scheduledShifts);
          // Use the corrected parameter name 'applicationDetails' here
          setSnackbarMessage(`Xếp lịch thành công cho "${applicationDetails.templateName}"!`);
          setSnackbarSeverity("success");
          setSnackbarOpen(true);
          handleCloseApplyModal();
        },
        {
          onError: (err) => {
            setIsSubmittingRoster(false);
            let errorMessage = 'Lỗi không xác định từ server.';
            if (err.response) {
              if (err.response.status === 422) {
                errorMessage = "Không thể tạo lịch: Các ràng buộc có thể quá chặt hoặc mâu thuẫn.";
              } else if (err.response.data && err.response.data.message) {
                errorMessage = `Lỗi ${err.response.status}: ${err.response.data.message}`;
              } else if (err.response.statusText) {
                errorMessage = `Lỗi ${err.response.status}: ${err.response.statusText}`;
              } else {
                errorMessage = `Lỗi ${err.response.status} từ server.`;
              }
            } else if (err.request) {
              errorMessage = "Không nhận được phản hồi từ server. Vui lòng kiểm tra kết nối mạng.";
            } else {
              errorMessage = `Lỗi khi gửi yêu cầu: ${err.message}`;
            }
            console.error("Lỗi khi gọi API xếp lịch:", err);
            setSnackbarMessage(errorMessage);
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
          }
        },
        payload
      );
    } catch (error) {
      setIsSubmittingRoster(false);
      console.error("Lỗi cục bộ khi chuẩn bị gọi API xếp lịch:", error);
      setSnackbarMessage("Lỗi cục bộ khi chuẩn bị gửi yêu cầu.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
      handleCloseApplyModal();
    }
  };

  const handleSnackbarClose = (event, reason) => { /* ... như cũ ... */
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarOpen(false);
  };

  if (loadingApis) { /* ... như cũ ... */
    return (
      <ThemeProvider theme={theme}> <CssBaseline />
        <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}>
          <CircularProgress size={50} /> <Typography variant="h6" sx={{ml:2}}>Đang tải dữ liệu...</Typography>
        </Box>
      </ThemeProvider>
    )
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
        {/* Container chứa nội dung chính, bao gồm cả header cố định và danh sách cuộn */}
        <Container component="main" maxWidth="lg" sx={{
          py: 0, // Không padding ở đây, header sẽ tự quản lý
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden' // Quan trọng: để Box con bên trong tự cuộn
        }}>
          <Box sx={{ // Header cố định
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            py: 2,
            px: {xs: 0, sm: 0}, // Không padding ngang ở đây nữa, Container cha đã có
            backgroundColor: theme.palette.background.default,
            zIndex: 10,
            borderBottom: `1px solid ${theme.palette.divider}`,
            // Không cần sticky nữa vì Container cha có overflow:hidden và Box con có overflow:auto
          }}>
            <Typography variant="h5" sx={{ color: 'primary.dark', fontWeight: 700, display:'flex', alignItems:'center' }}>
              <ArticleIcon sx={{mr:1, color: 'primary.main', fontSize: '1.7rem'}} />
              Danh Sách Bộ Cấu Hình
            </Typography>
            <Button
              color="primary"
              startIcon={<AddIcon />}
              onClick={handleOpenTemplateModalForNew}
              variant="contained"
              disabled={isSubmittingRoster}
            >
              Tạo Mới
            </Button>
          </Box>
          {/* Box này sẽ chứa danh sách và có thể cuộn */}
          <Box sx={{flexGrow: 1, overflowY: 'auto', pt: 2, pr:0.5, mr: -0.5 }}> {/* Thêm pt để không bị header che */}
            <TemplateListDisplay
              templates={configTemplates}
              onEdit={handleOpenTemplateModalForEdit}
              onDelete={handleDeleteTemplate}
              onOpenApplyModal={handleOpenApplyModal}
              isSubmittingRoster={isSubmittingRoster}
            />
          </Box>
        </Container>

        <Modal open={isTemplateModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick' && !isSubmittingRoster) handleCloseTemplateModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
          <Paper sx={{ width: '95%', maxWidth: '900px', maxHeight: 'calc(95vh - 32px)', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
            {isTemplateModalOpen &&
              <TemplateConfigForm onSave={handleSaveTemplate} onCancel={handleCloseTemplateModal} initialTemplateData={editingTemplate} />
            }
          </Paper>
        </Modal>

        {templateToApply && (
          <Modal open={isApplyModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick' && !isSubmittingRoster) handleCloseApplyModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
            <Paper sx={{ width: '95%', maxWidth: '750px', maxHeight: '90vh', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
              {isApplyModalOpen &&
                <ApplyConfigForm
                  onApply={handleActualApplyAndRoster}
                  onCancel={handleCloseApplyModal}
                  configTemplate={templateToApply}
                  departments={departments}
                  jobPositions={jobPositions} 
                  isSubmittingRoster={isSubmittingRoster}
                />
              }
            </Paper>
          </Modal>
        )}
        <Snackbar open={snackbarOpen} autoHideDuration={4000} onClose={handleSnackbarClose} anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}>
          <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
            {snackbarMessage}
          </Alert>
        </Snackbar>
      </Box>
    </ThemeProvider>
  );
}