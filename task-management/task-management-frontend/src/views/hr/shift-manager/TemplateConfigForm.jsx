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
import ShiftManager from './ShiftManager';
import ConstraintsManager from './ConstraintsManager';

export default function TemplateConfigForm({ onSave, onCancel, initialTemplateData }) {
  const [templateName, setTemplateName] = useState(initialTemplateData?.templateName || '');
  const [shifts, setShifts] = useState(initialTemplateData?.definedShifts || [
    { id: 's1', name: 'Sáng Hành Chính 1', startTime: '08:00', endTime: '11:00', isNightShift: false, minEmployees: 2, maxEmployees: 5 },
    { id: 's2', name: 'Sáng Hành Chính 2', startTime: '13:00', endTime: '15:00', isNightShift: false, minEmployees: 3, maxEmployees: 5 },
    { id: 's3', name: 'Sáng Hành Chính 3', startTime: '15:00', endTime: '18:00', isNightShift: false, minEmployees: 4, maxEmployees: 5 },
    { id: 's4', name: 'Đêm Kho', startTime: '22:00', endTime: '02:00', isNightShift: true, minEmployees: 1, maxEmployees: 2 },
  ]);

  const initialHardConstraintsStructure = useCallback(() => ({
    MAX_CONSECUTIVE_WORK_DAYS: { description: "Số ngày làm liên tiếp tối đa", enabled: true, params: { days: { label: "Số ngày tối đa", value: 5, type: 'number', min: 1 } }, tooltip: "Không làm quá X ngày liên tục." },
    MIN_REST_BETWEEN_SHIFTS_HOURS: { description: "Nghỉ tối thiểu (giờ) giữa 2 ca", enabled: true, params: { hours: { label: "Số giờ nghỉ tối thiểu", value: 10, type: 'number', min: 1 } }, tooltip: "Đảm bảo phục hồi." },
    MAX_WEEKLY_WORK_HOURS: { description: "Tổng giờ làm tối đa/tuần", enabled: true, params: { hours: { label: "Số giờ tối đa/tuần", value: 40, type: 'number', min: 1 } }, tooltip: "Tuân thủ luật." },
    MAX_DAILY_WORK_HOURS: {
      description: "Số giờ làm việc tối đa trong một ngày",
      enabled: true,
      params: { hours: { label: "Số giờ tối đa/ngày", value: 8, type: 'number', min: 1, max: 24 } },
      tooltip: "Giới hạn tổng số giờ làm việc của một nhân viên trong một ngày (00:00 - 23:59)."
    },
    NO_CLASHING_SHIFTS_FOR_EMPLOYEE: { description: "Không trùng ca cho 1 nhân viên (trong lịch mới)", enabled: true, params: null, tooltip: "Một người không thể ở 2 nơi trong cùng lịch mới tạo." },
    MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE: { description: "Số ca tối đa/ngày/nhân viên (trong lịch mới)", enabled: true, params: { count: { label: "Số ca tối đa/ngày", value: 1, type: 'number', min: 1 } }, tooltip: "Thường là 1." },
    NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT: { description: "Nghỉ ngày sau nếu làm bất kỳ ca đêm nào", enabled: true, params: null, tooltip: "Nếu bật, NV sẽ nghỉ ngày sau khi làm ca được đánh dấu 'Ca đêm'." },
    MIN_WEEKEND_DAYS_OFF_PER_PERIOD: {
      description: "NV nghỉ cuối tuần tối thiểu trong khoảng thời gian",
      enabled: true,
      params: {
        count: { label: "Số ngày T7/CN nghỉ", value: 2, type: 'number', min: 0 },
        periodWeeks: { label: "Trong khoảng (tuần)", value: 4, type: 'number', min: 1 }
      },
      tooltip: "Đảm bảo mỗi nhân viên có ít nhất X ngày Thứ 7 hoặc Chủ Nhật nghỉ trong Y tuần."
    },
    ENSURE_EMPLOYEE_APPROVED_LEAVE: {
      description: "Không xếp lịch vào ngày nghỉ đã duyệt của nhân viên",
      enabled: true,
      params: null,
      tooltip: "Hệ thống sẽ kiểm tra ngày nghỉ phép đã được duyệt của nhân viên và không xếp lịch vào những ngày đó."
    },
    AVOID_OVERLAPPING_EXISTING_SHIFTS: {
      description: "Không xếp lịch trùng với các ca ĐÃ CÓ SẴN của nhân viên",
      enabled: true, // Mặc định là bật
      params: null, // Đây là một toggle boolean, không có tham số con
      tooltip: "Nếu bật, hệ thống sẽ kiểm tra lịch làm việc hiện tại của nhân viên và tránh xếp ca mới nếu có sự trùng lặp thời gian."
    },
  }), []);

  const [hardConstraints, setHardConstraints] = useState(() => {
    const baseStructure = initialHardConstraintsStructure();
    if (initialTemplateData?.activeHardConstraints) {
      return Object.entries(baseStructure).reduce((acc, [key, structure]) => {
        // Kiểm tra xem key từ activeHardConstraints có tồn tại trong baseStructure không để tránh lỗi
        if (baseStructure[key]) {
          const isActiveConfig = initialTemplateData.activeHardConstraints[key];
          // Nếu là boolean (cho các ràng buộc không có params) hoặc là object (cho ràng buộc có params)
          const isActive = (typeof isActiveConfig === 'boolean' && isActiveConfig === true) ||
            (typeof isActiveConfig === 'object' && isActiveConfig !== null);

          acc[key] = { ...structure, enabled: isActive };

          if (isActive && structure.params && typeof isActiveConfig === 'object') {
            acc[key].params = { ...structure.params }; // Sao chép cấu trúc params gốc
            for (const pKey in structure.params) {
              // Chỉ cập nhật nếu giá trị param tồn tại trong activeHardConstraints[key]
              if (isActiveConfig[pKey] !== undefined) {
                acc[key].params[pKey] = { ...structure.params[pKey], value: isActiveConfig[pKey] };
              }
            }
          }
        } else {
          //todo log
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
        if (constraint.params) { // Nếu ràng buộc có tham số
          const activeParams = {};
          let paramsAreValid = true;
          for (const paramKey in constraint.params) {
            const paramDetail = constraint.params[paramKey];
            let valueToSave = paramDetail.value;

            if (paramDetail.type === 'number') {
              if (valueToSave === '' || valueToSave === null || isNaN(Number(valueToSave))) {
                // Nếu giá trị số không hợp lệ, và có giá trị min được định nghĩa, dùng min. Nếu không, báo lỗi.
                // Hoặc có thể set một default hợp lý nếu min không có.
                // Hiện tại, nếu trống hoặc NaN, sẽ dùng giá trị min (hoặc 0 nếu min không có)
                valueToSave = paramDetail.min === undefined ? 0 : paramDetail.min;
                // Hoặc bạn có thể muốn báo lỗi:
                // setFormError(`Tham số '${paramDetail.label}' của ràng buộc '${constraint.description}' không hợp lệ.`);
                // paramsAreValid = false;
                // break;
              } else {
                valueToSave = Number(valueToSave);
              }
            }
            activeParams[paramKey] = valueToSave;
          }
          // if (!paramsAreValid) break; // Thoát nếu có tham số lỗi
          activeConstraints[key] = activeParams;
        } else { // Ràng buộc không có tham số (boolean toggle)
          activeConstraints[key] = true;
        }
      }
    }

    // if (formError) return; // Nếu có lỗi từ việc validate params thì dừng

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
      <DialogContent dividers sx={{ p: { xs: 1.5, sm: 2 } }}>
        {formError && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setFormError('')} variant="filled">{formError}</Alert>}
        <TextField
          fullWidth label="Tên bộ cấu hình (*)" value={templateName} onChange={(e) => setTemplateName(e.target.value)}
          required sx={{ mb: 2 }} InputLabelProps={{ shrink: true }}
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