// src/features/rosterConfiguration/ApplyConfigForm.jsx
import React, { useState, useMemo } from 'react';
import {
  Box, Typography, TextField, Button, Paper,
  Grid, IconButton,
  Stack, DialogActions, DialogContent, DialogTitle,
  Alert, Select, MenuItem, Checkbox, InputAdornment, ListSubheader,
  Chip, FormControl, InputLabel, ListItemText, CircularProgress
} from '@mui/material';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { vi } from 'date-fns/locale';
import { format, isValid, differenceInDays } from 'date-fns';
import CloseIcon from '@mui/icons-material/Close';
import PlaylistPlayIcon from '@mui/icons-material/PlaylistPlay';
import SearchIcon from '@mui/icons-material/Search';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';

export default function ApplyConfigForm({ onApply, onCancel, configTemplate, departments, jobPositions, isSubmittingRoster }) {
  const [applyStartDate, setApplyStartDate] = useState(null);
  const [applyEndDate, setApplyEndDate] = useState(null);
  const [applySelectedDepts, setApplySelectedDepts] = useState([]);
  const [applySelectedJobs, setApplySelectedJobs] = useState([]);
  const [applyError, setApplyError] = useState('');

  const [deptSearch, setDeptSearch] = useState('');
  const [jobSearch, setJobSearch] = useState('');

  const filteredDepartments = useMemo(() => deptSearch ? departments.filter(d => d.departmentName.toLowerCase().includes(deptSearch.toLowerCase())) : departments, [departments, deptSearch]);
  const filteredJobPositions = useMemo(() => jobSearch ? jobPositions.filter(j => j.name.toLowerCase().includes(jobSearch.toLowerCase())) : jobPositions, [jobPositions, jobSearch]);

  const handleApplySubmit = () => {
    if (isSubmittingRoster) return;
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
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, p: '2px 0' }}> {/* Thêm padding nhẹ cho Box renderValue */}
                  {selected.map((val) => {
                    const item = options.find(opt => opt[optionValueKey] === val);
                    return <Chip key={val} label={item ? item[optionLabelKey] : val} size="small" onDelete={() => onChange({ target: { value: value.filter(v => v !== val) } })} onMouseDown={(e) => e.stopPropagation()} />;
                  })}
                  {selected.length === 0 && <Typography variant="body2" sx={{color: 'text.secondary', fontStyle: 'italic', pl: 1}}>{placeholder}</Typography> }
                </Box>
              )}
              MenuProps={{ PaperProps: { style: { maxHeight: 250 } } }}
              sx={{'& .MuiSelect-select': {minHeight: '28px', display:'flex', alignItems:'center'}}} // Đảm bảo chiều cao tối thiểu cho Select
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
      <DialogTitle sx={{ m: 0, p: 2, backgroundColor: 'secondary.dark', color: 'white' }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          Áp dụng bộ cấu hình: "{configTemplate.templateName}"
          <IconButton aria-label="close" onClick={onCancel} sx={{ color: 'white' }} disabled={isSubmittingRoster}><CloseIcon /></IconButton>
        </Stack>
      </DialogTitle>
      <DialogContent dividers sx={{ p: { xs: 2, sm: 3 }, backgroundColor: (th) => th.palette.background.paper }}>
        {applyError && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setApplyError('')} variant="filled">{applyError}</Alert>}
        <Grid container spacing={2.5}> {/* Tăng spacing */}
          <Grid item xs={12} sm={6}><DatePicker label="Ngày bắt đầu áp dụng (*)" value={applyStartDate} onChange={setApplyStartDate} slotProps={{textField: {fullWidth:true, required:true, InputLabelProps: { shrink: true }}}} disabled={isSubmittingRoster} /></Grid>
          <Grid item xs={12} sm={6}><DatePicker label="Ngày kết thúc áp dụng (*)" value={applyEndDate} onChange={setApplyEndDate} slotProps={{textField: {fullWidth:true, required:true, InputLabelProps: { shrink: true }}}} minDate={applyStartDate || undefined} disabled={isSubmittingRoster} /></Grid>
          <Grid item xs={12} sm={6}>
            {renderMultiSelect("Phòng ban (Để trống là tất cả)", applySelectedDepts, (e) => setApplySelectedDepts(e.target.value), setDeptSearch, deptSearch, departments, 'departmentCode', 'departmentName', "Tất cả phòng ban")}
          </Grid>
          <Grid item xs={12} sm={6}>
            {renderMultiSelect("Chức vụ (Để trống là tất cả)", applySelectedJobs, (e) => setApplySelectedJobs(e.target.value), setJobSearch, jobSearch, jobPositions, 'code', 'name', "Tất cả chức vụ")}
          </Grid>
        </Grid>
        <Alert severity="info" variant="standard" icon={<InfoOutlinedIcon />} sx={{mt:2.5, backgroundColor: 'info.lighter', color: 'info.darker', '& .MuiAlert-icon': {color: 'info.main'}}}>
          Bộ cấu hình <strong>"{configTemplate.templateName}"</strong> với {configTemplate.definedShifts.length} ca và {Object.keys(configTemplate.activeHardConstraints).length} ràng buộc sẽ được sử dụng.
        </Alert>
      </DialogContent>
      <DialogActions sx={{ p: '16px 24px', borderTop: '1px solid', borderColor: 'divider', backgroundColor: 'grey.100' }}>
        <Button onClick={onCancel} color="inherit" variant="outlined" disabled={isSubmittingRoster}>Hủy</Button>
        <Button
          onClick={handleApplySubmit}
          color="success"
          variant="contained"
          startIcon={isSubmittingRoster ? <CircularProgress size={20} color="inherit"/> : <PlaylistPlayIcon />}
          disabled={isSubmittingRoster}
        >
          {isSubmittingRoster ? "Đang Xếp Lịch..." : "Xếp Lịch Ngay"}
        </Button>
      </DialogActions>
    </LocalizationProvider>
  );
}