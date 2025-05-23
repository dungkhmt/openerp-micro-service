// src/features/rosterConfiguration/TemplateConfigForm.jsx
import React, { useState, useCallback } from 'react';
import {
  Typography, TextField, Button, Paper,
  Grid, IconButton,
  Stack, DialogActions, DialogContent, DialogTitle,
  Alert
} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import CloseIcon from '@mui/icons-material/Close';
import ShiftManager from './ShiftManager'; // Import component con
import ConstraintsManager from './ConstraintsManager'; // Import component con

// Component Form Cấu Hình Bộ Template (Nội dung Modal 1)
export default function TemplateConfigForm({ onSave, onCancel, initialTemplateData }) {
  const [templateName, setTemplateName] = useState(initialTemplateData?.templateName || '');
  const [shifts, setShifts] = useState(initialTemplateData?.definedShifts || [
    { id: 's1', name: 'Sáng Hành Chính', startTime: '08:00', endTime: '17:00', isNightShift: false, minEmployees: 2, maxEmployees: 5 },
    { id: 's3', name: 'Đêm Kho', startTime: '22:00', endTime: '06:00', isNightShift: true, minEmployees: 1, maxEmployees: 2 },
  ]);

  const initialHardConstraintsStructure = useCallback(() => ({
    MAX_CONSECUTIVE_WORK_DAYS: { description: "Số ngày làm liên tiếp tối đa", enabled: true, params: { days: { label: "Số ngày tối đa", value: 5, type: 'number', min: 1 } }, tooltip: "Không làm quá X ngày liên tục." },
    MIN_REST_BETWEEN_SHIFTS_HOURS: { description: "Nghỉ tối thiểu (giờ) giữa 2 ca", enabled: true, params: { hours: { label: "Số giờ nghỉ tối thiểu", value: 10, type: 'number', min: 1 } }, tooltip: "Đảm bảo phục hồi." },
    MAX_WEEKLY_WORK_HOURS: { description: "Tổng giờ làm tối đa/tuần", enabled: true, params: { hours: { label: "Số giờ tối đa/tuần", value: 40, type: 'number', min: 1 } }, tooltip: "Tuân thủ luật." },
    NO_CLASHING_SHIFTS_FOR_EMPLOYEE: { description: "Không trùng ca cho 1 nhân viên", enabled: true, params: null, tooltip: "Một người không thể ở 2 nơi." },
    MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE: { description: "Số ca tối đa/ngày/nhân viên", enabled: true, params: { count: { label: "Số ca tối đa/ngày", value: 1, type: 'number', min: 1 } }, tooltip: "Thường là 1." },
    NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT: { description: "Nghỉ ngày sau nếu làm bất kỳ ca đêm nào", enabled: true, params: null, tooltip: "Nếu bật, nhân viên sẽ được nghỉ ngày tiếp theo sau khi làm một ca được đánh dấu là 'Ca đêm'." },
  }), []);

  const [hardConstraints, setHardConstraints] = useState(() => {
    const baseStructure = initialHardConstraintsStructure();
    if (initialTemplateData?.activeHardConstraints) {
      return Object.entries(baseStructure).reduce((acc, [key, structure]) => {
        const isActive = !!initialTemplateData.activeHardConstraints[key];
        acc[key] = { ...structure, enabled: isActive };
        if (isActive && structure.params && initialTemplateData.activeHardConstraints[key] && typeof initialTemplateData.activeHardConstraints[key] === 'object') {
          acc[key].params = { ...structure.params };
          for (const pKey in structure.params) {
            if (initialTemplateData.activeHardConstraints[key][pKey] !== undefined) {
              acc[key].params[pKey] = { ...structure.params[pKey], value: initialTemplateData.activeHardConstraints[key][pKey] };
            }
          }
        }
        return acc;
      }, {});
    }
    return baseStructure;
  });
  const [formError, setFormError] = useState('');

  const handleSubmit = () => {
    setFormError('');
    if (!templateName.trim()) { setFormError("Tên bộ cấu hình không được để trống."); return; }
    if (shifts.length === 0) { setFormError("Cần định nghĩa ít nhất một ca làm việc."); return; }

    const activeConstraints = {};
    for (const key in hardConstraints) {
      if (hardConstraints[key].enabled) {
        const constraint = hardConstraints[key];
        if (constraint.params) {
          const activeParams = {};
          for (const paramKey in constraint.params) {
            const paramDetail = constraint.params[paramKey];
            if (paramDetail.type === 'number' && (paramDetail.value === '' || paramDetail.value === null || isNaN(paramDetail.value))) {
              activeParams[paramKey] = paramDetail.min === undefined ? 0 : paramDetail.min;
            } else { activeParams[paramKey] = paramDetail.value; }
          }
          activeConstraints[key] = activeParams;
        } else { activeConstraints[key] = true; }
      }
    }

    const finalTemplateData = {
      id: initialTemplateData?.id || `template_${Date.now()}`,
      templateName: templateName.trim(),
      definedShifts: shifts,
      activeHardConstraints: activeConstraints
    };
    onSave(finalTemplateData);
  };

  return (
    <>
      <DialogTitle sx={{ m: 0, p: 2, backgroundColor: 'primary.main', color: 'white' }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          {initialTemplateData?.id ? "Chỉnh Sửa Bộ Cấu Hình" : "Tạo Mới Bộ Cấu Hình"}
          <IconButton aria-label="close" onClick={onCancel} sx={{ color: 'white' }}><CloseIcon /></IconButton>
        </Stack>
      </DialogTitle>
      <DialogContent dividers sx={{ p: { xs: 1.5, sm: 2 }, backgroundColor: (th) => th.palette.background.paper }}>
        {formError && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setFormError('')} variant="filled">{formError}</Alert>}
        <TextField
          fullWidth
          label="Tên bộ cấu hình (*)"
          value={templateName}
          onChange={(e) => setTemplateName(e.target.value)}
          required
          sx={{ mb: 2 }}
          InputLabelProps={{ shrink: true }}
        />
        <ShiftManager shifts={shifts} setShifts={setShifts} />
        <ConstraintsManager constraints={hardConstraints} setConstraints={setHardConstraints} />
      </DialogContent>
      <DialogActions sx={{ p: '16px 24px', borderTop: '1px solid', borderColor: 'divider', backgroundColor: 'grey.50' }}>
        <Button onClick={onCancel} color="inherit" variant="outlined">Hủy bỏ</Button>
        <Button onClick={handleSubmit} color="primary" variant="contained" startIcon={<SaveIcon />}>
          {initialTemplateData?.id ? "Cập Nhật" : "Lưu Mới"}
        </Button>
      </DialogActions>
    </>
  );
}