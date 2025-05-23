import React, { useState, useMemo, useEffect, useCallback } from 'react';
import {
  Container, Box, Typography, TextField, Button, Modal, Paper,
  FormControlLabel, FormGroup, Grid, IconButton,
  Switch, Tooltip, CssBaseline, ThemeProvider, createTheme, Alert,
  Stack, DialogActions, DialogContent, DialogTitle,
  List, ListItem, ListItemText, Chip, Divider, // Bỏ AppBar, Toolbar ở đây nếu không dùng nữa
  Select, MenuItem, Checkbox, InputAdornment, ListSubheader,
  CircularProgress,
  FormControl,
  Menu,
  InputLabel // <<==================== ĐẢM BẢO IMPORT NÀY
} from '@mui/material';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { vi } from 'date-fns/locale';
import { format, parseISO, isValid, differenceInDays } from 'date-fns';

import DeleteIcon from '@mui/icons-material/Delete';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import SaveIcon from '@mui/icons-material/Save';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import CloseIcon from '@mui/icons-material/Close';
// import SettingsIcon from '@mui/icons-material/Settings'; // Bỏ nếu không dùng
import EditIcon from '@mui/icons-material/Edit';
import PlaylistPlayIcon from '@mui/icons-material/PlaylistPlay';
import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import BlockIcon from '@mui/icons-material/Block';
import BusinessHoursIcon from '@mui/icons-material/MoreTime';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import TodayIcon from '@mui/icons-material/Today';
import NightsStayIcon from '@mui/icons-material/NightsStay';
import SearchIcon from '@mui/icons-material/Search';
import ArticleIcon from '@mui/icons-material/Article';


// --- Theme ---
const theme = createTheme({
  palette: {
    primary: { main: '#1e88e5', light: '#6ab7ff', dark: '#005cb2' }, // Xanh dương mới
    secondary: { main: '#7e57c2', light: '#b085f5', dark: '#4d2c91' }, // Tím mới
    success: { main: '#43a047' },
    error: { main: '#e53935' },
    info: { main: '#039be5'},
    background: { default: '#f4f5f7', paper: '#ffffff' }, // Nền xám rất nhạt
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h4: { fontWeight: 700, fontSize: '1.65rem', letterSpacing: '0.01em' }, // Giảm chút
    h5: { fontWeight: 600, fontSize: '1.25rem' }, // Giảm chút
    h6: { fontWeight: 600, fontSize: '1rem' },    // Giảm chút
    subtitle1: { fontWeight: 500, fontSize: '0.9rem'},
    body1: { fontSize: '0.875rem'}, // Giảm chút
    caption: { fontSize: '0.75rem', color: '#546e7a'}
  },
  components: {
    MuiPaper: { styleOverrides: { root: { borderRadius: 10, boxShadow: '0px 4px 12px rgba(0,0,0,0.05)' } } }, // Box shadow nhẹ
    MuiButton: { styleOverrides: { root: { borderRadius: 6, textTransform: 'none', fontWeight: 600 } } },
    MuiTextField: { defaultProps: { variant: 'outlined', size: 'small' } },
    MuiChip: { styleOverrides: { root: { fontWeight: 500, borderRadius: 16 }}},
  }
});

// --- MOCK API FUNCTIONS (Giữ nguyên) ---
const mockApiRequest = (path, params) => { /* ... như cũ ... */
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (path === "/departments") {
        resolve({
          data: {
            data: [
              { department_code: "D001", department_name: "Phòng Kỹ thuật" }, { department_code: "D002", department_name: "Phòng Nhân sự" },
              { department_code: "D003", department_name: "Phòng Kinh doanh" },{ department_code: "D004", department_name: "Phòng Marketing" },
              { department_code: "D005", department_name: "Ban Giám đốc" }, { department_code: "D006", department_name: "Kế toán" },
              { department_code: "D007", department_name: "Kho vận" }, { department_code: "D008", department_name: "Sản xuất" },
            ], meta: { page_info: { total_page: 1, page: 0 } }
          }
        });
      } else if (path === "/jobs") {
        resolve({
          data: {
            data: [
              { code: "J001", name: "Lập trình viên Backend" }, { code: "J002", name: "Chuyên viên Nhân sự" },
              { code: "J003", name: "Nhân viên Kinh doanh" }, { code: "J004", name: "Digital Marketer" },
              { code: "J005", name: "Giám đốc Điều hành (CEO)" }, { code: "J006", name: "Lập trình viên Frontend" },
              { code: "J007", name: "Kế toán trưởng" }, { code: "J008", name: "Nhân viên kho" }, { code: "J009", name: "Công nhân sản xuất" },
            ], meta: { page_info: { total_page: 1, page: 0 } }
          }
        });
      } else { reject(new Error("Unknown API path")); }
    }, 300);
  });
};

// --- Component Quản lý Ca Làm Việc (Nội bộ Modal - Đã FIX hiển thị) ---
function ShiftManager({ shifts, setShifts }) {
  const [newShiftName, setNewShiftName] = useState('');
  const [newShiftStart, setNewShiftStart] = useState('07:00');
  const [newShiftEnd, setNewShiftEnd] = useState('15:00');
  const [isNewShiftNightShift, setIsNewShiftNightShift] = useState(false);
  const [newMinEmployees, setNewMinEmployees] = useState('1');
  const [newMaxEmployees, setNewMaxEmployees] = useState('5');
  const [error, setError] = useState('');

  const addShift = () => {
    // ... logic addShift như cũ
    if (!newShiftName.trim()) { setError('Tên ca không được để trống.'); return; }
    if (!newShiftStart || !newShiftEnd) { setError('Giờ bắt đầu và kết thúc không được để trống.'); return; }
    const minEmp = newMinEmployees === '' ? 0 : parseInt(newMinEmployees, 10);
    const maxEmp = newMaxEmployees === '' ? 0 : parseInt(newMaxEmployees, 10);

    if (isNaN(minEmp) || minEmp < 0) { setError('NV tối thiểu không hợp lệ (phải là số >= 0).'); return;}
    if (isNaN(maxEmp) || maxEmp < 0) { setError('NV tối đa không hợp lệ (phải là số >= 0).'); return;}
    if (minEmp > maxEmp && maxEmp > 0) { setError('NV tối thiểu không thể lớn hơn NV tối đa (khi NV tối đa > 0).'); return; }
    if (shifts.some(shift => shift.name.toLowerCase() === newShiftName.trim().toLowerCase())) {
      setError(`Tên ca "${newShiftName.trim()}" đã tồn tại.`); return;
    }
    setError('');
    const newShift = {
      id: `shift_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      name: newShiftName.trim(), startTime: newShiftStart, endTime: newShiftEnd, isNightShift: isNewShiftNightShift,
      minEmployees: minEmp, maxEmployees: maxEmp === 0 ? null : maxEmp
    };
    setShifts(prevShifts => [...prevShifts, newShift]);
    setNewShiftName(''); setNewShiftStart('07:00'); setNewShiftEnd('15:00'); setIsNewShiftNightShift(false);
    setNewMinEmployees('1'); setNewMaxEmployees('5');
  };
  const removeShift = (id) => setShifts(prevShifts => prevShifts.filter(shift => shift.id !== id));
  const handleShiftChange = (id, field, value) => {
    // ... logic handleShiftChange như cũ
    let processedValue = value;
    if (field === 'minEmployees' || field === 'maxEmployees') {
      if (value === '' || !isNaN(parseInt(value,10))) {
        processedValue = value;
      } else {
        return;
      }
    }
    setShifts(prevShifts => prevShifts.map(shift => {
      if (shift.id === id) {
        let finalValue = processedValue;
        if (field === 'minEmployees' || field === 'maxEmployees') {
          const numVal = parseInt(processedValue, 10);
          finalValue = processedValue === '' ? '' : (isNaN(numVal) ? shift[field] : numVal) ;
        }
        if (field === 'maxEmployees' && finalValue === 0) finalValue = null;
        return { ...shift, [field]: finalValue };
      }
      return shift;
    }));
  };

  return (
    <Paper elevation={0} variant="outlined" sx={{ p: { xs: 1.5, sm: 2 }, mb: 2, borderColor: 'rgba(0,0,0,0.08)' }}>
      <Typography variant="h6" gutterBottom sx={{ color: 'primary.dark', fontSize: '1.05rem', display: 'flex', alignItems: 'center', mb: 1.5 }}>
        <EventAvailableIcon sx={{ mr: 1, color: 'primary.main', fontSize: '1.3rem' }} /> Định Nghĩa Ca Làm Việc
      </Typography>
      {error && <Alert severity="error" sx={{ mb: 1.5 }} onClose={() => setError('')} variant="outlined">{error}</Alert>}
      <Box component="form" noValidate autoComplete="off" sx={{ mb: 1.5 }}>
        <Grid container spacing={1.5} alignItems="flex-end"> {/* alignItems flex-end giúp căn chỉnh label và input */}
          <Grid item xs={12} sm={12} md={3}><TextField fullWidth label="Tên ca (*)" value={newShiftName} onChange={(e) => setNewShiftName(e.target.value)} InputLabelProps={{ shrink: true }} /></Grid>
          <Grid item xs={6} sm={4} md={1.5}><TextField fullWidth label="Bắt đầu (*)" type="time" value={newShiftStart} onChange={(e) => setNewShiftStart(e.target.value)} InputLabelProps={{ shrink: true }} /></Grid>
          <Grid item xs={6} sm={4} md={1.5}><TextField fullWidth label="Kết thúc (*)" type="time" value={newShiftEnd} onChange={(e) => setNewShiftEnd(e.target.value)} InputLabelProps={{ shrink: true }} /></Grid>
          <Grid item xs={6} sm={4} md={1.25}><TextField fullWidth label="NV Min" type="number" value={newMinEmployees} onChange={(e) => setNewMinEmployees(e.target.value)} inputProps={{ min: 0 }} InputLabelProps={{ shrink: true }}/></Grid>
          <Grid item xs={6} sm={4} md={1.25}><TextField fullWidth label="NV Max" type="number" value={newMaxEmployees} onChange={(e) => setNewMaxEmployees(e.target.value)} inputProps={{ min: 0 }} helperText="0=KGH" InputLabelProps={{ shrink: true }}/></Grid>
          <Grid item xs={12} sm={4} md={1.75} sx={{display: 'flex', alignItems: 'center', pb: newMaxEmployees ? '20px' : '0px' /* Căn với helperText */ }}>
            <FormControlLabel control={<Switch checked={isNewShiftNightShift} onChange={(e) => setIsNewShiftNightShift(e.target.checked)} />} label="Ca đêm?" sx={{whiteSpace: 'nowrap', m:0}} />
          </Grid>
          <Grid item xs={12} sm={4} md={1.25}>
            <Button fullWidth variant="contained" color="primary" startIcon={<AddCircleOutlineIcon />} onClick={addShift} sx={{ height: '40px' }}>Thêm</Button>
          </Grid>
        </Grid>
      </Box>
      <Typography variant="caption" sx={{ mb: 0.5, color: 'text.secondary', display:'block' }}>Danh sách ca đã định nghĩa ({shifts.length}):</Typography>
      <Box sx={{ maxHeight: '220px', overflowY: 'auto', pr: 0.5 }}>
        {shifts.length === 0 && <Typography sx={{ textAlign: 'center', color: 'text.secondary', p: 2, fontStyle: 'italic' }}>Chưa có ca làm việc nào.</Typography>}
        {shifts.map(shift => (
          <Paper key={shift.id} variant="outlined" sx={{ p: 1.5, mb: 1, borderColor: 'rgba(0,0,0,0.1)'}}>
            <Grid container spacing={1} alignItems="center">
              {/* Sử dụng Typography để hiển thị, TextField khi cần edit (logic phức tạp hơn, tạm dùng TextField standard) */}
              <Grid item xs={12} sm={12} md={2.5}><TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: true}} fullWidth variant="standard" label="Tên ca" value={shift.name} onChange={(e) => handleShiftChange(shift.id, 'name', e.target.value)} /></Grid>
              <Grid item xs={6} sm={3} md={1.5}><TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: true}} fullWidth variant="standard" label="BĐ" type="time" value={shift.startTime} onChange={(e) => handleShiftChange(shift.id, 'startTime', e.target.value)} /></Grid>
              <Grid item xs={6} sm={3} md={1.5}><TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: true}} fullWidth variant="standard" label="KT" type="time" value={shift.endTime} onChange={(e) => handleShiftChange(shift.id, 'endTime', e.target.value)} /></Grid>
              <Grid item xs={6} sm={2.5} md={1.25}><TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: true}} fullWidth variant="standard" label="Min" type="number" value={shift.minEmployees === '' ? '' : shift.minEmployees} onChange={(e) => handleShiftChange(shift.id, 'minEmployees', e.target.value)} inputProps={{ min: 0 }} /></Grid>
              <Grid item xs={6} sm={2.5} md={1.25}><TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: true}} fullWidth variant="standard" label="Max" type="number" value={shift.maxEmployees === null ? '' : shift.maxEmployees} onChange={(e) => handleShiftChange(shift.id, 'maxEmployees', e.target.value)} inputProps={{ min: 0 }} /></Grid>
              <Grid item xs={9} sm={2} md={2.5} sx={{display: 'flex', alignItems:'center'}}> <FormControlLabel control={<Switch size="small" checked={shift.isNightShift} onChange={(e) => handleShiftChange(shift.id, 'isNightShift', e.target.checked)} />} label="Đêm" sx={{fontSize: '0.8rem'}} /> </Grid>
              <Grid item xs={3} sm={1} md={1} sx={{textAlign: 'right'}}><Tooltip title="Xóa ca"><IconButton onClick={() => removeShift(shift.id)} color="error" size="small"><DeleteIcon /></IconButton></Tooltip></Grid>
            </Grid>
          </Paper>
        ))}
      </Box>
    </Paper>
  );
}

// --- Component Quản lý Ràng Buộc (Nội bộ Modal - GIAO DIỆN MỚI) ---
function ConstraintsManager({ constraints, setConstraints }) { /* ... như cũ ... */
  const toggleConstraint = (key) => setConstraints(prev => ({ ...prev, [key]: { ...prev[key], enabled: !prev[key].enabled } }));
  const handleConstraintValueChange = (key, paramName, value, type) => {
    let pValue = type === 'number' ? (value === '' ? '' : Number(value)) : value;
    setConstraints(prev => ({ ...prev, [key]: { ...prev[key], params: { ...prev[key].params, [paramName]: { ...prev[key].params[paramName], value: pValue } } } }));
  };
  return (
    <Paper elevation={0} variant="outlined" sx={{ p: { xs: 1.5, sm: 2 }, mb: 2.5, borderColor: 'rgba(0,0,0,0.08)' }}>
      <Typography variant="h6" gutterBottom sx={{ color: 'primary.dark', fontSize: '1.05rem', display: 'flex', alignItems: 'center', mb: 1.5 }}>
        <BlockIcon sx={{ mr: 1, color: 'primary.main', fontSize: '1.3rem' }} /> Các Ràng Buộc Công Việc
      </Typography>
      <Box sx={{ maxHeight: '220px', overflowY: 'auto', pr: 0.5 }}>
        {Object.entries(constraints).map(([key, constraint]) => (
          <Paper key={key} variant="outlined" sx={{ p: 1.5, mb: 1.5, borderColor: 'rgba(0,0,0,0.1)' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <FormControlLabel control={<Switch checked={constraint.enabled} onChange={() => toggleConstraint(key)} color="secondary" />} label={<Typography sx={{fontSize: '0.9rem'}} fontWeight="500" color={constraint.enabled ? "text.primary" : "text.disabled"}>{constraint.description}</Typography>} />
              {constraint.tooltip && <Tooltip title={constraint.tooltip}><InfoOutlinedIcon color="action" sx={{ ml: 1, cursor: 'help', fontSize: '1rem' }} /></Tooltip>}
            </Box>
            {constraint.enabled && constraint.params && (
              <Box sx={{ pl: { xs: 0, sm: 1 }, pt: 1, mt: 1, borderTop: '1px solid', borderColor: 'divider' }}>
                <Grid container spacing={1.5}>
                  {Object.entries(constraint.params).map(([paramName, paramDetails]) => (
                    <Grid item xs={12} sm={paramDetails.fullWidth ? 12 : 6} key={paramName}>
                      <TextField fullWidth type={paramDetails.type === 'number' ? 'number' : 'text'} label={paramDetails.label} value={paramDetails.value}
                                 inputProps={{ min: paramDetails.type === 'number' ? (paramDetails.min === undefined ? 0 : paramDetails.min) : undefined, step: paramDetails.step || 1 }}
                                 onChange={(e) => handleConstraintValueChange(key, paramName, e.target.value, paramDetails.type)}
                      />
                    </Grid>
                  ))}
                </Grid>
              </Box>
            )}
          </Paper>
        ))}
      </Box>
    </Paper>
  );
}

// --- Component Form Cấu Hình Bộ Template (Nội dung Modal 1) ---
function TemplateConfigForm({ onSave, onCancel, initialTemplateData }) { /* ... như cũ ... */
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

// --- Component Form Áp Dụng Cấu Hình (Nội dung Modal thứ 2 - GIAO DIỆN MỚI) ---
function ApplyConfigForm({ onApply, onCancel, configTemplate, departments, jobPositions }) { /* ... styling có thể điều chỉnh thêm ... */
  const [applyStartDate, setApplyStartDate] = useState(null);
  const [applyEndDate, setApplyEndDate] = useState(null);
  const [applySelectedDepts, setApplySelectedDepts] = useState([]);
  const [applySelectedJobs, setApplySelectedJobs] = useState([]);
  const [applyError, setApplyError] = useState('');

  const [deptSearch, setDeptSearch] = useState('');
  const [jobSearch, setJobSearch] = useState('');

  const filteredDepartments = useMemo(() => deptSearch ? departments.filter(d => d.departmentName.toLowerCase().includes(deptSearch.toLowerCase())) : departments, [departments, deptSearch]);
  const filteredJobPositions = useMemo(() => jobSearch ? jobPositions.filter(j => j.name.toLowerCase().includes(jobSearch.toLowerCase())) : jobPositions, [jobPositions, jobSearch]);

  const handleApplySubmit = () => { /* ... như cũ ... */
    setApplyError('');
    if (!applyStartDate || !isValid(applyStartDate)) { setApplyError("Ngày bắt đầu không hợp lệ."); return; }
    if (!applyEndDate || !isValid(applyEndDate)) { setApplyError("Ngày kết thúc không hợp lệ."); return; }
    if (differenceInDays(applyEndDate, applyStartDate) < 0) { setApplyError("Ngày kết thúc không thể trước ngày bắt đầu."); return; }

    const applicationDetails = {
      templateId: configTemplate.id,
      templateName: configTemplate.templateName,
      startDate: format(applyStartDate, 'yyyy-MM-dd'),
      endDate: format(applyEndDate, 'yyyy-MM-dd'),
      departmentCodes: applySelectedDepts,
      jobPositionCodes: applySelectedJobs,
      shiftsAndConstraints: {
        definedShifts: configTemplate.definedShifts,
        activeHardConstraints: configTemplate.activeHardConstraints
      }
    };
    onApply(applicationDetails);
  };

  const renderMultiSelect = (label, value, onChange, onSearchChange, searchValue, options, optionValueKey, optionLabelKey, placeholder) => (
    <FormControl fullWidth margin="dense" variant="outlined">
      <InputLabel shrink sx={{backgroundColor: 'background.paper', px:0.5, transform: 'translate(14px, -9px) scale(0.75)'}}>{label}</InputLabel>
      <Select multiple value={value} onChange={onChange} onClose={() => onSearchChange('')}
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {selected.map((val) => {
                    const item = options.find(opt => opt[optionValueKey] === val);
                    return <Chip key={val} label={item ? item[optionLabelKey] : val} size="small" onDelete={() => onChange({ target: { value: value.filter(v => v !== val) } })} onMouseDown={(e) => e.stopPropagation()} />;
                  })}
                  {selected.length === 0 && <Typography variant="body2" sx={{color: 'text.secondary', fontStyle: 'italic', pl: 1}}>{placeholder}</Typography> }
                </Box>
              )}
              MenuProps={{ PaperProps: { style: { maxHeight: 250 } } }}
      >
        <ListSubheader sx={{bgcolor: 'background.paper', zIndex:1, pt:1, pb:0.5}}>
          <TextField size="small" placeholder="Tìm kiếm..." fullWidth value={searchValue}
                     onChange={(e) => onSearchChange(e.target.value)}
                     onKeyDown={(e) => e.stopPropagation()} onClick={(e) => e.stopPropagation()}
                     InputProps={{ startAdornment: (<InputAdornment position="start"><SearchIcon color="action" /></InputAdornment>) }}
          />
        </ListSubheader>
        {options.map(opt => (
          <MenuItem key={opt[optionValueKey]} value={opt[optionValueKey]}>
            <Checkbox checked={value.indexOf(opt[optionValueKey]) > -1} size="small" />
            <ListItemText primary={opt[optionLabelKey]} primaryTypographyProps={{fontSize: '0.9rem'}}/>
          </MenuItem>
        ))}
        {options.length === 0 && searchValue && <MenuItem disabled sx={{justifyContent:'center'}}><Typography variant="caption">Không tìm thấy.</Typography></MenuItem>}
      </Select>
    </FormControl>
  );

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <DialogTitle sx={{ m: 0, p: 2, backgroundColor: 'secondary.dark', color: 'white' }}> {/* Đổi màu DialogTitle */}
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          Áp dụng bộ cấu hình: "{configTemplate.templateName}"
          <IconButton aria-label="close" onClick={onCancel} sx={{ color: 'white' }}><CloseIcon /></IconButton>
        </Stack>
      </DialogTitle>
      <DialogContent dividers sx={{ p: { xs: 1.5, sm: 2.5 }, backgroundColor: (th) => th.palette.background.paper }}> {/* Đổi nền */}
        {applyError && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setApplyError('')} variant="filled">{applyError}</Alert>}
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}><DatePicker label="Ngày bắt đầu áp dụng (*)" value={applyStartDate} onChange={setApplyStartDate} slotProps={{textField: {fullWidth:true, required:true, InputLabelProps: { shrink: true }}}} /></Grid>
          <Grid item xs={12} sm={6}><DatePicker label="Ngày kết thúc áp dụng (*)" value={applyEndDate} onChange={setApplyEndDate} slotProps={{textField: {fullWidth:true, required:true, InputLabelProps: { shrink: true }}}} minDate={applyStartDate || undefined} /></Grid>
          <Grid item xs={12} sm={6}>
            {renderMultiSelect("Phòng ban (Để trống là tất cả)", applySelectedDepts, (e) => setApplySelectedDepts(e.target.value), setDeptSearch, deptSearch, departments, 'departmentCode', 'departmentName', "Tất cả phòng ban")}
          </Grid>
          <Grid item xs={12} sm={6}>
            {renderMultiSelect("Chức vụ (Để trống là tất cả)", applySelectedJobs, (e) => setApplySelectedJobs(e.target.value), setJobSearch, jobSearch, jobPositions, 'code', 'name', "Tất cả chức vụ")}
          </Grid>
        </Grid>
        <Alert severity="info" variant="standard" sx={{mt:2, backgroundColor: 'info.lighter', color: 'info.darker', '& .MuiAlert-icon': {color: 'info.main'}}}> {/* Styling Alert */}
          Bộ cấu hình <strong>"{configTemplate.templateName}"</strong> với {configTemplate.definedShifts.length} ca và {Object.keys(configTemplate.activeHardConstraints).length} ràng buộc sẽ được sử dụng.
        </Alert>
      </DialogContent>
      <DialogActions sx={{ p: '16px 24px', borderTop: '1px solid', borderColor: 'divider', backgroundColor: 'grey.100' }}> {/* Đổi màu nền */}
        <Button onClick={onCancel} color="inherit" variant="outlined">Hủy</Button>
        <Button onClick={handleApplySubmit} color="success" variant="contained" startIcon={<PlaylistPlayIcon />}>
          Xếp Lịch Ngay
        </Button>
      </DialogActions>
    </LocalizationProvider>
  );
}


// --- Component Hiển thị Danh sách các Bộ Cấu Hình (GIAO DIỆN "CŨ" HƠN THEO YÊU CẦU) ---
let moduleDepartmentsForDisplay = [];
let moduleJobPositionsForDisplay = [];

function SingleTemplateDetails({ template }) { /* ... Nội dung và styling được làm TO RÕ HƠN ... */
  const { definedShifts, activeHardConstraints } = template;
  const constraintDisplayInfo = {
    MAX_CONSECUTIVE_WORK_DAYS: { label: "Ngày làm LT tối đa", icon: <TodayIcon sx={{mr:0.5, fontSize: '1.1rem'}}/> },
    MIN_REST_BETWEEN_SHIFTS_HOURS: { label: "Nghỉ giữa ca (giờ)", icon: <BusinessHoursIcon sx={{mr:0.5, fontSize: '1.1rem'}}/> },
    MAX_WEEKLY_WORK_HOURS: { label: "Giờ tối đa/tuần", icon: <BusinessHoursIcon sx={{mr:0.5, fontSize: '1.1rem'}}/> },
    NO_CLASHING_SHIFTS_FOR_EMPLOYEE: { label: "Không trùng ca", icon: <BlockIcon sx={{mr:0.5, fontSize: '1.1rem'}}/> },
    MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE: { label: "Ca tối đa/ngày", icon: <EventAvailableIcon sx={{mr:0.5, fontSize: '1.1rem'}}/> },
    NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT: { label: "Nghỉ sau ca đêm", icon: <NightsStayIcon sx={{mr:0.5, fontSize: '1.1rem'}}/> },
  };

  return (
    <Paper elevation={0} sx={{ p: { xs: 1.5, sm: 2 }, mt: 1.5, backgroundColor: '#fafafa', borderRadius: 1.5, border: '1px solid #e0e0e0' }}>
      <Grid container spacing={2.5}>
        <Grid item xs={12} md={5}>
          <Typography variant="subtitle1" gutterBottom sx={{ color: 'secondary.dark', fontWeight: 'bold', fontSize:'1rem', borderBottom: '1px solid', borderColor:'divider', pb: 0.5, mb:1 }}>
            Ca Làm Việc ({definedShifts.length})
          </Typography>
          {definedShifts.length > 0 ? (
            <List dense sx={{ maxHeight: 200, overflow: 'auto', p:0 }}>
              {definedShifts.map(shift => (
                <ListItem key={shift.id} sx={{py: 0.5, pl:0, borderBottom: '1px solid #f0f0f0', '&:last-child': {borderBottom: 'none'} }}>
                  <ListItemText
                    primary={<Typography variant="body1" component="span" fontWeight="bold" color="text.primary">{shift.name}</Typography>}
                    secondary={`${shift.startTime} - ${shift.endTime} (Min: ${shift.minEmployees}, Max: ${shift.maxEmployees === null ? 'KGH' : shift.maxEmployees})`}
                    primaryTypographyProps={{fontSize: '0.95rem'}}
                    secondaryTypographyProps={{fontSize: '0.8rem'}}
                  />
                  {shift.isNightShift && <Chip label="Đêm" color="secondary" size="small" variant="filled" icon={<NightsStayIcon fontSize="inherit"/>} sx={{ml:1, height: '22px', fontSize: '0.75rem', borderRadius:1}}/>}
                </ListItem>
              ))}
            </List>
          ) : <Typography variant="body2" fontStyle="italic">Không có ca nào.</Typography>}
        </Grid>
        <Grid item xs={12} md={7}>
          <Typography variant="subtitle1" gutterBottom sx={{ color: 'secondary.dark', fontWeight: 'bold', fontSize:'1rem', borderBottom: '1px solid', borderColor:'divider', pb: 0.5, mb:1 }}>
            Ràng Buộc Áp Dụng ({Object.keys(activeHardConstraints).length})
          </Typography>
          {Object.keys(activeHardConstraints).length > 0 ? (
            <List dense sx={{ maxHeight: 200, overflow: 'auto', p:0 }}>
              {Object.entries(activeHardConstraints).map(([key, value]) => {
                const displayInfo = constraintDisplayInfo[key] || { label: key.replace(/_/g, ' ') };
                let valueString = "";
                if (typeof value === 'boolean' && value) valueString = "Đang áp dụng";
                else if (typeof value === 'object' && value !== null) valueString = Object.entries(value).map(([pKey, pVal])=>`${pVal}`).join('; '); // Hiển thị giá trị param
                return (
                  <ListItem key={key} sx={{py: 0.25, pl:0, alignItems: 'flex-start', borderBottom: '1px solid #f0f0f0', '&:last-child': {borderBottom: 'none'} }}>
                    <Box sx={{display: 'flex', alignItems: 'center', minWidth: {xs: '150px', sm:'190px'}, mr:1, color: 'text.secondary' }}>
                      {displayInfo.icon}
                      <Typography variant="body2" component="span" fontWeight="500" sx={{ml:0.25}}>{displayInfo.label}:</Typography>
                    </Box>
                    <Typography variant="body1" color="text.primary" sx={{flexGrow: 1, fontWeight: 500}}>{valueString}</Typography>
                  </ListItem>
                );
              })}
            </List>
          ) : <Typography variant="body2" fontStyle="italic">Không có ràng buộc nào.</Typography>}
        </Grid>
      </Grid>
    </Paper>
  );
}

function TemplateListDisplay({ templates, onEdit, onDelete, onOpenApplyModal }) {
  const [anchorEl, setAnchorEl] = useState(null);
  const [currentTemplateId, setCurrentTemplateId] = useState(null);

  const handleMenuOpen = (event, templateId) => { setAnchorEl(event.currentTarget); setCurrentTemplateId(templateId); };
  const handleMenuClose = () => { setAnchorEl(null); setCurrentTemplateId(null); };
  const handleEdit = () => { const templateToEdit = templates.find(t => t.id === currentTemplateId); if (templateToEdit) onEdit(templateToEdit); handleMenuClose(); };
  const handleDelete = () => { if (currentTemplateId) onDelete(currentTemplateId); handleMenuClose(); };

  if (!templates || templates.length === 0) {
    return (
      <Paper elevation={0} sx={{ p: 3, textAlign: 'center', mt: 0, backgroundColor: 'grey.50', border: '1px dashed', borderColor: 'grey.300' }}>
        <Typography variant="h6" color="text.secondary">Chưa có bộ cấu hình nào.</Typography>
        <Typography color="text.secondary">Nhấn "Tạo Mới" ở trên để bắt đầu.</Typography>
      </Paper>
    );
  }

  return (
    <Box sx={{ mt: 0 }}> {/* Bỏ margin top ở đây vì đã có ở header */}
      {templates.map(template => (
        <Paper key={template.id} elevation={3} sx={{ p: 2.5, mb: 3, transition: 'box-shadow .3s', '&:hover': {boxShadow: 6} }}>
          <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5}}>
            <Typography variant="h5" component="div" color="primary.dark" sx={{fontWeight: 700}}> {/* TO HƠN */}
              {template.templateName}
            </Typography>
            <Box>
              <Button
                variant="contained"
                color="secondary" // Màu khác cho nút Áp dụng
                size="medium" // To hơn
                startIcon={<PlaylistPlayIcon />}
                onClick={() => onOpenApplyModal(template)}
                sx={{mr:1}}
              >
                Áp dụng
              </Button>
              <IconButton size="medium" onClick={(e) => handleMenuOpen(e, template.id)}><MoreVertIcon /></IconButton>
            </Box>
          </Box>
          <Divider sx={{my:1.5}}/>
          <SingleTemplateDetails template={template} />
        </Paper>
      ))}
      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleMenuClose} transformOrigin={{horizontal: 'right', vertical: 'top'}} anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}>
        <MenuItem onClick={handleEdit}><EditIcon fontSize="small" sx={{mr:1}}/> Sửa bộ cấu hình</MenuItem>
        <MenuItem onClick={handleDelete} sx={{color: 'error.main'}}><DeleteIcon fontSize="small" sx={{mr:1}}/> Xóa bộ cấu hình</MenuItem>
      </Menu>
    </Box>
  );
}


// --- Component Chính Gộp (Export default) ---
export default function ConfigurableRosterPage() {
  const [isTemplateModalOpen, setIsTemplateModalOpen] = useState(false);
  const [isApplyModalOpen, setIsApplyModalOpen] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState(null);
  const [templateToApply, setTemplateToApply] = useState(null);

  const [configTemplates, setConfigTemplates] = useState(() => {
    try { const saved = localStorage.getItem('rosterTemplatesList_v7_styled'); return saved ? JSON.parse(saved) : []; }
    catch (e) { console.error("Failed to load templates from localStorage", e); return []; }
  });

  const [loadingApis, setLoadingApis] = useState(true);
  const [fetchDone, setFetchDone] = useState({ departments: false, jobs: false });

  useEffect(() => { /* ... Fetch API như cũ ... */
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


  useEffect(() => { /* ... Lưu localStorage như cũ ... */
    localStorage.setItem('rosterTemplatesList_v7_styled', JSON.stringify(configTemplates));
  }, [configTemplates]);

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
  };

  const handleDeleteTemplate = (templateIdToDelete) => { /* ... như cũ ... */
    if (window.confirm("Bạn có chắc chắn muốn xóa bộ cấu hình này không? Thao tác này không thể hoàn tác.")) {
      setConfigTemplates(prevList => prevList.filter(t => t.id !== templateIdToDelete));
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

  const handleActualApplyAndRoster = (applicationDetails) => { /* ... như cũ ... */
    console.log("DỮ LIỆU GỬI ĐI ĐỂ XẾP LỊCH:", applicationDetails);
    alert(`Đã gửi yêu cầu xếp lịch cho: ${applicationDetails.templateName}\nTừ ${applicationDetails.startDate} đến ${applicationDetails.endDate}\nPhòng ban: ${applicationDetails.departmentCodes.map(code => moduleDepartmentsForDisplay.find(d=>d.departmentCode === code)?.departmentName || code).join(', ') || 'Tất cả'}\nChức vụ: ${applicationDetails.jobPositionCodes.map(code => moduleJobPositionsForDisplay.find(j=>j.code === code)?.name || code).join(', ') || 'Tất cả'}`);
    handleCloseApplyModal();
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
      {/* Box ngoài cùng để kiểm soát chiều cao toàn trang và scrolling */}
      <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
        {/* Bỏ AppBar */}
        {/* Container chứa nội dung chính, sẽ cuộn */}
        <Container component="main" maxWidth="lg" sx={{
          pt: 2.5, // Padding top cho Container
          pb: 2.5, // Padding bottom cho Container để không bị che
          flexGrow: 1,
          overflowY: 'auto', // CHO PHÉP CONTAINER NÀY CUỘN
          display: 'flex',
          flexDirection: 'column'
        }}>
          <Box sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            mb: 2,
            // Style cho phần header cố định
            position: 'sticky',
            top: -20, // Điều chỉnh để sát mép trên sau khi bỏ AppBar và pt của Container
            backgroundColor: theme.palette.background.default, // Nền để không bị trong suốt
            zIndex: 1100, // Cao hơn nội dung cuộn
            pt: 2.5, // Bù lại padding top của Container
            ml: -2.5, // Bù lại padding left của Container
            mr: -2.5, // Bù lại padding right của Container
            pl: 2.5, // Thêm padding left lại
            pr: 2.5, // Thêm padding right lại
            borderBottom: '1px solid',
            borderColor: 'divider'
          }}>
            <Typography variant="h5" sx={{ color: 'primary.dark', fontWeight: 700 }}>
              <ArticleIcon sx={{mr:1, verticalAlign: 'middle', color: 'primary.main', fontSize: '1.7rem'}} />
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
          {/* TemplateListDisplay sẽ nằm trong vùng cuộn */}
          <TemplateListDisplay
            templates={configTemplates}
            onEdit={handleOpenTemplateModalForEdit}
            onDelete={handleDeleteTemplate}
            onOpenApplyModal={handleOpenApplyModal}
          />
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

