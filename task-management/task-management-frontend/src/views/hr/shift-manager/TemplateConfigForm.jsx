import React, {useCallback, useState} from 'react';
import {Alert, Button, DialogActions, DialogContent, DialogTitle, IconButton, Stack, TextField} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import CloseIcon from '@mui/icons-material/Close';
import ShiftManager from './ShiftManager';
import ConstraintsManager from './ConstraintsManager';

export default function TemplateConfigForm({ onSave, onCancel, initialTemplateData }) {
  const [templateName, setTemplateName] = useState(initialTemplateData?.templateName || '');
  const [shifts, setShifts] = useState(initialTemplateData?.definedShifts || [
    { id: 's1', name: 'Trực ca sáng', startTime: '08:30', endTime: '11:30', isNightShift: false, minEmployees: 4, maxEmployees: 8 },
    { id: 's2', name: 'Trực ca chiều', startTime: '13:30', endTime: '16:30', isNightShift: false, minEmployees: 5, maxEmployees: 10 },
    { id: 's3', name: 'Đêm Kho', startTime: '22:00', endTime: '01:00', isNightShift: true, minEmployees: 2, maxEmployees: 3 },
  ]);

  const initialHardConstraintsStructure = useCallback(() => ({
    AVOID_SPECIFIC_DAYS_OF_WEEK: {
      description: "Không xếp lịch vào các thứ được chọn trong tuần",
      enabled: false,
      params: {
        daysOfWeek: { label: "Chọn các thứ không làm việc", value: [], type: 'day_of_week_select' }
      },
      tooltip: "Hệ thống sẽ không xếp lịch cho bất kỳ nhân viên nào vào các thứ được chọn."
    },
    NO_WORK_ON_HOLIDAYS: {
      description: "Không xếp lịch vào các ngày Lễ, Tết",
      enabled: true,
      params: null,
      tooltip: "Hệ thống sẽ tự động tải danh sách ngày lễ và không xếp lịch vào những ngày này."
    },
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
    MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE: { description: "Số ca tối đa/ngày/nhân viên (trong lịch mới)", enabled: true, params: { count: { label: "Số ca tối đa/ngày", value: 1, type: 'number', min: 1 } }, tooltip: "Số ca tối đa/ngày" },
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
      enabled: true,
      params: null,
      tooltip: "Nếu bật, hệ thống sẽ kiểm tra lịch làm việc hiện tại của nhân viên và tránh xếp ca mới nếu có sự trùng lặp thời gian."
    },
  }), []);

  const [hardConstraints, setHardConstraints] = useState(() => {
    const baseStructure = initialHardConstraintsStructure();
    if (initialTemplateData?.activeHardConstraints) {
      return Object.entries(baseStructure).reduce((acc, [key, structure]) => {
        if (baseStructure[key]) {
          const isActiveConfig = initialTemplateData.activeHardConstraints[key];
          const isActive = (typeof isActiveConfig === 'boolean' && isActiveConfig === true) ||
            (typeof isActiveConfig === 'object' && isActiveConfig !== null);

          acc[key] = { ...structure, enabled: isActive };

          if (isActive && structure.params && typeof isActiveConfig === 'object') {
            acc[key].params = { ...structure.params };
            for (const pKey in structure.params) {
              if (isActiveConfig[pKey] !== undefined) {
                // NEU: Xử lý giá trị là mảng cho AVOID_SPECIFIC_DAYS_OF_WEEK
                const valueFromTemplate = isActiveConfig[pKey];
                if (Array.isArray(valueFromTemplate)) {
                  acc[key].params[pKey] = { ...structure.params[pKey], value: [...valueFromTemplate] };
                } else {
                  acc[key].params[pKey] = { ...structure.params[pKey], value: valueFromTemplate };
                }
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
          for (const paramKey in constraint.params) {
            const paramDetail = constraint.params[paramKey];
            let valueToSave = paramDetail.value;

            // NEU: Xử lý giá trị là mảng
            if(Array.isArray(valueToSave)) {
              activeParams[paramKey] = valueToSave;
              continue;
            }

            if (paramDetail.type === 'number') {
              if (valueToSave === '' || valueToSave === null || isNaN(Number(valueToSave))) {
                valueToSave = paramDetail.min === undefined ? 0 : paramDetail.min;
              } else {
                valueToSave = Number(valueToSave);
              }
            }
            activeParams[paramKey] = valueToSave;
          }
          activeConstraints[key] = activeParams;
        } else {
          activeConstraints[key] = true;
        }
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
      <DialogTitle sx={{ m: 0, p: 2, backgroundColor: 'primary.main', color: 'white' }} >
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          {initialTemplateData?.id ? "Chỉnh Sửa Bộ Cấu Hình" : "Tạo Mới Bộ Cấu Hình"}
          <IconButton aria-label="close" onClick={onCancel} sx={{ color: 'white' }}><CloseIcon /></IconButton>
        </Stack>
      </DialogTitle>
      <DialogContent dividers sx={{ p: { xs: 1.5, sm: 2 } }} className="custom-scrollbar">
        {formError && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setFormError('')} variant="filled">{formError}</Alert>}
        <TextField
          fullWidth label="Tên bộ cấu hình (*)" value={templateName} onChange={(e) => setTemplateName(e.target.value)}
          required sx={{ mb: 2 }} InputLabelProps={{ shrink: true }}
        />
        <ShiftManager shifts={shifts} setShifts={setShifts}  />
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