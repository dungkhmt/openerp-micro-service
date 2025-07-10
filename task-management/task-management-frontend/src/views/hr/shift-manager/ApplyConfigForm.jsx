import React, { useEffect, useState } from 'react';
import {
  Alert,
  Button,
  CircularProgress,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  Stack,
  Typography
} from '@mui/material';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { vi } from 'date-fns/locale';
import { addDays, differenceInDays, format, isValid } from 'date-fns';
import CloseIcon from '@mui/icons-material/Close';
import PlaylistPlayIcon from '@mui/icons-material/PlaylistPlay';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import CustomMultiSelect from "@/components/item/CustomMultiSelect";

export default function ApplyConfigForm({ onApply, onCancel, configTemplate, departments, jobPositions, isSubmittingRoster }) {
  const [applyStartDate, setApplyStartDate] = useState(null);
  const [applyEndDate, setApplyEndDate] = useState(null);
  const [applySelectedDepts, setApplySelectedDepts] = useState([]);
  const [applySelectedJobs, setApplySelectedJobs] = useState([]);
  const [applyError, setApplyError] = useState('');

  useEffect(() => {
    if (configTemplate) {
      setApplySelectedDepts(configTemplate.departmentFilter || []);
      setApplySelectedJobs(configTemplate.jobPositionFilter || []);
    } else {
      setApplySelectedDepts([]);
      setApplySelectedJobs([]);
    }
  }, [configTemplate]);

  // handleApplySubmit được GIỮ NGUYÊN HOÀN TOÀN
  const handleApplySubmit = () => {
    if (isSubmittingRoster) return;
    setApplyError('');
    if (!applyStartDate || !isValid(applyStartDate)) { setApplyError("Ngày bắt đầu không hợp lệ."); return; }
    if (!applyEndDate || !isValid(applyEndDate)) { setApplyError("Ngày kết thúc không hợp lệ."); return; }
    if (differenceInDays(applyEndDate, applyStartDate) < 0) { setApplyError("Ngày kết thúc không thể trước ngày bắt đầu."); return; }
    if (differenceInDays(applyEndDate, applyStartDate) > 90) {
      setApplyError("Khoảng thời gian áp dụng không nên quá 90 ngày để đảm bảo hiệu suất."); return;
    }

    const applicationDetails = {
      templateId: configTemplate.id,
      templateName: configTemplate.templateName,
      startDate: format(applyStartDate, 'yyyy-MM-dd'),
      endDate: format(applyEndDate, 'yyyy-MM-dd'),
      departmentCodes: applySelectedDepts,
      jobPositionCodes: applySelectedJobs,
      shiftsAndConstraints: {
        definedShifts: configTemplate.definedShifts || [],
        activeHardConstraints: configTemplate.activeHardConstraints || {}
      }
    };
    onApply(applicationDetails);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <DialogTitle sx={{ m: 0, p: 2, backgroundColor: 'secondary.dark', color: 'white' }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant='h6' fontWeight={600}>Áp dụng bộ cấu hình: "{configTemplate?.templateName || 'N/A'}"</Typography>
          <IconButton aria-label="close" onClick={onCancel} sx={{ color: 'white' }} disabled={isSubmittingRoster}><CloseIcon /></IconButton>
        </Stack>
      </DialogTitle>
      <DialogContent dividers sx={{ p: { xs: 2, sm: 3 } }}>
        {applyError && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setApplyError('')} variant="filled">{applyError}</Alert>}
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <DatePicker
              label="Ngày bắt đầu áp dụng (*)" value={applyStartDate} onChange={setApplyStartDate}
              slotProps={{ textField: { fullWidth: true, required: true, size: 'small' } }}
              disabled={isSubmittingRoster} disablePast
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <DatePicker
              label="Ngày kết thúc áp dụng (*)" value={applyEndDate} onChange={setApplyEndDate}
              slotProps={{ textField: { fullWidth: true, required: true, size: 'small' } }}
              minDate={applyStartDate ? addDays(applyStartDate, 0) : undefined}
              disabled={isSubmittingRoster}
            />
          </Grid>

          {/* ---- THAY THẾ HOÀN TOÀN BẰNG COMPONENT MỚI ---- */}
          <Grid item xs={12}>
            <CustomMultiSelect
              label="Phòng ban (Để trống là tất cả)"
              options={departments || []}
              selectedValues={applySelectedDepts}
              onChange={setApplySelectedDepts} // -> Cập nhật trực tiếp vào state
              loading={!departments}
              valueKey="departmentCode"
              labelKey="departmentName"
              placeholder="Áp dụng cho tất cả phòng ban"
            />
          </Grid>
          <Grid item xs={12}>
            <CustomMultiSelect
              label="Chức vụ (Để trống là tất cả)"
              options={jobPositions || []}
              selectedValues={applySelectedJobs}
              onChange={setApplySelectedJobs} // -> Cập nhật trực tiếp vào state
              loading={!jobPositions}
              valueKey="code"
              labelKey="name"
              placeholder="Áp dụng cho tất cả chức vụ"
            />
          </Grid>
        </Grid>
        <Alert severity="info" variant="standard" icon={<InfoOutlinedIcon />} sx={{ mt: 2.5, backgroundColor: 'info.lighter', color: 'info.darker', '& .MuiAlert-icon': { color: 'info.main' } }}>
          Bộ cấu hình <strong>"{configTemplate?.templateName || 'N/A'}"</strong>
          với {(configTemplate?.definedShifts || []).length} ca và
          {' '}{Object.keys(configTemplate?.activeHardConstraints || {}).length} ràng buộc sẽ được sử dụng.
        </Alert>
      </DialogContent>
      <DialogActions sx={{ p: '16px 24px', borderTop: '1px solid', borderColor: 'divider' }}>
        <Button onClick={onCancel} color="inherit" variant="outlined" disabled={isSubmittingRoster}>Hủy</Button>
        <Button
          onClick={handleApplySubmit}
          color="success"
          variant="contained"
          startIcon={isSubmittingRoster ? <CircularProgress size={20} color="inherit" /> : <PlaylistPlayIcon />}
          disabled={isSubmittingRoster}
        >
          {isSubmittingRoster ? "Đang áp dụng..." : "Áp dụng và tạo lịch"}
        </Button>
      </DialogActions>
    </LocalizationProvider>
  );
}