// src/features/rosterConfiguration/ConfigurableRosterPage.jsx
import React, { useState, useMemo, useEffect, useCallback } from 'react';
import {
  Container, Box, Typography, Button, Modal, Paper,
  CssBaseline, ThemeProvider, CircularProgress, IconButton // Bỏ Toolbar, AppBar nếu không dùng trực tiếp ở đây
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add'; // Icon cho nút Tạo Mới
import ArticleIcon from '@mui/icons-material/Article'; // Icon cho tiêu đề danh sách

import { theme } from './theme'; // Import theme
import { mockApiRequest } from './api'; // Import mock API
import TemplateConfigForm from './TemplateConfigForm';
import ApplyConfigForm from './ApplyConfigForm.jsx';
import TemplateListDisplay from './TemplateListDisplay';

// Khai báo biến module cho departments và jobPositions
// để TemplateListDisplay -> SingleTemplateDetails có thể truy cập
// Lưu ý: Đây là cách đơn giản, với ứng dụng lớn nên dùng Context/Redux
let moduleDepartmentsForDisplay = [];
let moduleJobPositionsForDisplay = [];

export default function ConfigurableRosterPage() {
  const [isTemplateModalOpen, setIsTemplateModalOpen] = useState(false);
  const [isApplyModalOpen, setIsApplyModalOpen] = useState(false);

  const [editingTemplate, setEditingTemplate] = useState(null);
  const [templateToApply, setTemplateToApply] = useState(null);

  const [configTemplates, setConfigTemplates] = useState(() => {
    try { const saved = localStorage.getItem('rosterTemplatesList_v7_styled_split'); return saved ? JSON.parse(saved) : []; } // Đổi key
    catch (e) { console.error("Failed to load templates from localStorage", e); return []; }
  });

  const [loadingApis, setLoadingApis] = useState(true);
  const [fetchDone, setFetchDone] = useState({ departments: false, jobs: false });

  useEffect(() => {
    let active = true;
    setLoadingApis(true);
    Promise.all([
      mockApiRequest("/departments", { status: "ACTIVE" }),
      mockApiRequest("/jobs", { status: "ACTIVE" })
    ]).then(([deptRes, jobRes]) => {
      if (active) {
        moduleDepartmentsForDisplay = deptRes.data.data.map(d => ({ departmentCode: d.department_code, departmentName: d.department_name }));
        moduleJobPositionsForDisplay = jobRes.data.data.map(j => ({ code: j.code, name: j.name }));
        setFetchDone({ departments: true, jobs: true });
      }
    }).catch(err => {
      console.error("Error fetching API data:", err);
      if (active) setFetchDone({ departments: true, jobs: true });
    }).finally(() => {
      if(active) setLoadingApis(false);
    });
    return () => { active = false; };
  }, []);


  useEffect(() => {
    localStorage.setItem('rosterTemplatesList_v7_styled_split', JSON.stringify(configTemplates)); // Đổi key
  }, [configTemplates]);

  const handleOpenTemplateModalForNew = () => { setEditingTemplate(null); setIsTemplateModalOpen(true); };
  const handleOpenTemplateModalForEdit = (template) => { setEditingTemplate(template); setIsTemplateModalOpen(true); };
  const handleCloseTemplateModal = () => { setIsTemplateModalOpen(false); setEditingTemplate(null); };

  const handleSaveTemplate = (templateData) => {
    setConfigTemplates(prevList => {
      const existingIndex = prevList.findIndex(t => t.id === templateData.id);
      if (existingIndex > -1) {
        const newList = [...prevList]; newList[existingIndex] = templateData; return newList;
      } else { return [...prevList, templateData]; }
    });
    handleCloseTemplateModal();
  };

  const handleDeleteTemplate = (templateIdToDelete) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa bộ cấu hình này không? Thao tác này không thể hoàn tác.")) {
      setConfigTemplates(prevList => prevList.filter(t => t.id !== templateIdToDelete));
    }
  };

  const handleOpenApplyModal = (template) => {
    setTemplateToApply(template);
    setIsApplyModalOpen(true);
  };
  const handleCloseApplyModal = () => {
    setIsApplyModalOpen(false);
    setTemplateToApply(null);
  };

  const handleActualApplyAndRoster = (applicationDetails) => {
    console.log("DỮ LIỆU GỬI ĐI ĐỂ XẾP LỊCH:", applicationDetails);
    alert(`Đã gửi yêu cầu xếp lịch cho: ${applicationDetails.templateName}\nTừ ${applicationDetails.startDate} đến ${applicationDetails.endDate}\nPhòng ban: ${applicationDetails.departmentCodes.map(code => moduleDepartmentsForDisplay.find(d=>d.departmentCode === code)?.departmentName || code).join(', ') || 'Tất cả'}\nChức vụ: ${applicationDetails.jobPositionCodes.map(code => moduleJobPositionsForDisplay.find(j=>j.code === code)?.name || code).join(', ') || 'Tất cả'}`);
    handleCloseApplyModal();
  };


  if (loadingApis) {
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
          pt: 0, // Bỏ padding top ở đây vì header cố định sẽ có padding riêng
          pb: 2.5,
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden' // Quan trọng: để Box con bên trong tự cuộn
        }}>
          <Box sx={{ // Header cố định
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            py: 2, // Padding y cho header
            px: {xs:0, sm:1}, // Padding x nhỏ
            backgroundColor: theme.palette.background.default, // Nền để không bị trong suốt
            zIndex: 10,
            // Không cần sticky nếu Container cha có overflow hidden và Box con có overflow auto
          }}>
            <Typography variant="h5" sx={{ color: 'primary.dark', fontWeight: 700 }}>
              <ArticleIcon sx={{mr:1, verticalAlign: 'bottom', color: 'primary.main', fontSize: '1.7rem'}} />
              Danh Sách Bộ Cấu Hình
            </Typography>
            <Button
              color="primary"
              startIcon={<AddIcon />}
              onClick={handleOpenTemplateModalForNew}
              variant="contained"
            >
              Tạo Mới
            </Button>
          </Box>
          {/* Box này sẽ chứa danh sách và có thể cuộn */}
          <Box sx={{flexGrow: 1, overflowY: 'auto', pr:0.5, mr: -0.5 /* Bù lại pr để thanh cuộn không tạo khoảng trống thừa */}}>
            <TemplateListDisplay
              templates={configTemplates}
              onEdit={handleOpenTemplateModalForEdit}
              onDelete={handleDeleteTemplate}
              onOpenApplyModal={handleOpenApplyModal}
            />
          </Box>
        </Container>

        <Modal open={isTemplateModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick') handleCloseTemplateModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
          <Paper sx={{ width: '95%', maxWidth: '900px', maxHeight: '95vh', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
            {isTemplateModalOpen &&
              <TemplateConfigForm onSave={handleSaveTemplate} onCancel={handleCloseTemplateModal} initialTemplateData={editingTemplate} />
            }
          </Paper>
        </Modal>

        {templateToApply && (
          <Modal open={isApplyModalOpen} onClose={(event, reason) => { if (reason !== 'backdropClick') handleCloseApplyModal();}} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} closeAfterTransition >
            <Paper sx={{ width: '95%', maxWidth: '750px', maxHeight: '90vh', display: 'flex', flexDirection: 'column', borderRadius: 2, boxShadow: 24, outline: 'none' }}>
              {isApplyModalOpen &&
                <ApplyConfigForm
                  onApply={handleActualApplyAndRoster}
                  onCancel={handleCloseApplyModal}
                  configTemplate={templateToApply}
                  departments={moduleDepartmentsForDisplay}
                  jobPositions={moduleJobPositionsForDisplay}
                />
              }
            </Paper>
          </Modal>
        )}
        {/* Bỏ footer để có thêm không gian */}
      </Box>
    </ThemeProvider>
  );
}