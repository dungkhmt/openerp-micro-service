// src/features/rosterConfiguration/ApplyConfigForm.jsx
import React, {useEffect, useMemo, useState} from 'react'; // Thêm useEffect
import {
  Alert,
  Box,
  Button,
  Checkbox,
  Chip,
  CircularProgress,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  Grid,
  IconButton,
  InputAdornment,
  InputLabel,
  ListItemText,
  ListSubheader,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import {DatePicker, LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDateFns} from '@mui/x-date-pickers/AdapterDateFns';
import {vi} from 'date-fns/locale';
import {addDays, differenceInDays, format, isValid} from 'date-fns';
import CloseIcon from '@mui/icons-material/Close';
import PlaylistPlayIcon from '@mui/icons-material/PlaylistPlay';
import SearchIcon from '@mui/icons-material/Search';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';

export default function ApplyConfigForm({ onApply, onCancel, configTemplate, departments, jobPositions, isSubmittingRoster }) {
  const [applyStartDate, setApplyStartDate] = useState(null);
  const [applyEndDate, setApplyEndDate] = useState(null);
  // Khởi tạo state rỗng ban đầu, useEffect sẽ cập nhật chúng
  const [applySelectedDepts, setApplySelectedDepts] = useState([]);
  const [applySelectedJobs, setApplySelectedJobs] = useState([]);
  const [applyError, setApplyError] = useState('');

  const [deptSearch, setDeptSearch] = useState('');
  const [jobSearch, setJobSearch] = useState('');

  // Sử dụng useEffect để cập nhật các filter đã chọn khi configTemplate thay đổi
  useEffect(() => {
    if (configTemplate) {
      // configTemplate.departmentFilter và jobPositionFilter đã được chuẩn hóa thành mảng
      // bởi ensureTemplateStructure trong ConfigurableRosterPage.jsx
      setApplySelectedDepts(configTemplate.departmentFilter || []);
      setApplySelectedJobs(configTemplate.jobPositionFilter || []);
    } else {
      // Nếu không có configTemplate (trường hợp hiếm khi modal mở), reset về rỗng
      setApplySelectedDepts([]);
      setApplySelectedJobs([]);
    }
  }, [configTemplate]); // Chạy lại effect này khi configTemplate thay đổi

  const filteredDepartments = useMemo(() => {
    if (!departments) return []; // Guard clause
    return deptSearch
      ? departments.filter(d => d.departmentName.toLowerCase().includes(deptSearch.toLowerCase()))
      : departments;
  }, [departments, deptSearch]);

  const filteredJobPositions = useMemo(() => {
    if (!jobPositions) return []; // Guard clause
    return jobSearch
      ? jobPositions.filter(j => j.name.toLowerCase().includes(jobSearch.toLowerCase()))
      : jobPositions;
  }, [jobPositions, jobSearch]);

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
        definedShifts: configTemplate.definedShifts || [], // Đảm bảo có giá trị mặc định
        activeHardConstraints: configTemplate.activeHardConstraints || {} // Đảm bảo có giá trị mặc định
      }
    };
    onApply(applicationDetails);
  };

  const handleMultiSelectChange = (setter) => (event) => {
    const { target: { value } } = event;
    setter(typeof value === 'string' ? value.split(',') : value);
  };

  const renderMultiSelect = (label, value, onChange, onSearchChange, searchValue, allOptions, optionValueKey, optionLabelKey, placeholder, filteredOptions) => (
    <FormControl fullWidth margin="dense" variant="outlined" disabled={isSubmittingRoster}>
      <InputLabel shrink sx={{backgroundColor: 'background.paper', px:0.5, transform: 'translate(14px, -9px) scale(0.75)'}}>{label}</InputLabel>
      <Select
        multiple
        value={value} // Đây là mảng các code đã chọn, ví dụ: ["DEPT-0001", "DEPT-0002"]
        onChange={onChange}
        onClose={() => onSearchChange('')} // Reset search khi đóng dropdown
        renderValue={(selected) => ( // `selected` ở đây là mảng các code
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, p: '2px 0' }}>
            {selected.map((code) => {
              // Tìm item đầy đủ từ danh sách TẤT CẢ options (không phải filteredOptions) để lấy label
              const item = allOptions.find(opt => opt[optionValueKey] === code);
              return <Chip key={code} label={item ? item[optionLabelKey] : code} size="small"
                           onDelete={(e) => { e.stopPropagation(); onChange({ target: { value: value.filter(v => v !== code) } }); }}
                           onMouseDown={(e) => e.stopPropagation()}
              />;
            })}
            {selected.length === 0 && <Typography variant="body2" sx={{color: 'text.secondary', fontStyle: 'italic', pl: 1}}>{placeholder}</Typography> }
          </Box>
        )}
        MenuProps={{ PaperProps: { style: { maxHeight: 300 } } }} // Tăng maxHeight
        sx={{'& .MuiSelect-select': {minHeight: '28px', display:'flex', alignItems:'center'}}}
      >
        <ListSubheader sx={{bgcolor: 'background.paper', zIndex:1, pt:1, pb:0.5, top: -8, position:'sticky'}}> {/*sticky header */}
          <TextField
            size="small"
            placeholder="Tìm kiếm theo tên..."
            fullWidth
            value={searchValue}
            onChange={(e) => onSearchChange(e.target.value)}
            onKeyDown={(e) => e.stopPropagation()} // Ngăn sự kiện keydown đóng Select
            onClick={(e) => e.stopPropagation()} // Ngăn click vào TextField đóng Select
            InputProps={{ startAdornment: (<InputAdornment position="start"><SearchIcon color="action" /></InputAdornment>) }}
          />
        </ListSubheader>
        {/* Hiển thị các options đã được lọc */}
        {filteredOptions.map(opt => (
          <MenuItem key={opt[optionValueKey]} value={opt[optionValueKey]}>
            <Checkbox checked={value.indexOf(opt[optionValueKey]) > -1} size="small" />
            <ListItemText primary={opt[optionLabelKey]} primaryTypographyProps={{fontSize: '0.9rem'}}/>
          </MenuItem>
        ))}
        {filteredOptions.length === 0 && searchValue && <MenuItem disabled sx={{justifyContent:'center'}}><Typography variant="caption">Không tìm thấy kết quả nào.</Typography></MenuItem>}
        {filteredOptions.length === 0 && !searchValue && allOptions.length > 0 && <MenuItem disabled sx={{justifyContent:'center'}}><Typography variant="caption">Không có lựa chọn nào.</Typography></MenuItem>}
      </Select>
    </FormControl>
  );

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <DialogTitle sx={{ m: 0, p: 2, backgroundColor: 'secondary.dark', color: 'white' }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          Áp dụng bộ cấu hình: "{configTemplate?.templateName || 'N/A'}" {/* Thêm fallback */}
          <IconButton aria-label="close" onClick={onCancel} sx={{ color: 'white' }} disabled={isSubmittingRoster}><CloseIcon /></IconButton>
        </Stack>
      </DialogTitle>
      <DialogContent dividers sx={{ p: { xs: 2, sm: 3 }, backgroundColor: (th) => th.palette.background.paper }}>
        {applyError && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setApplyError('')} variant="filled">{applyError}</Alert>}
        <Grid container spacing={2.5}>
          <Grid item xs={12} sm={6}>
            <DatePicker
              label="Ngày bắt đầu áp dụng (*)"
              value={applyStartDate}
              onChange={setApplyStartDate}
              slotProps={{textField: {fullWidth:true, required:true, InputLabelProps: { shrink: true }}}}
              disabled={isSubmittingRoster}
              disablePast
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <DatePicker
              label="Ngày kết thúc áp dụng (*)"
              value={applyEndDate}
              onChange={setApplyEndDate}
              slotProps={{textField: {fullWidth:true, required:true, InputLabelProps: { shrink: true }}}}
              minDate={applyStartDate ? addDays(applyStartDate, 0) : undefined} // End date >= start date
              disabled={isSubmittingRoster}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            {renderMultiSelect(
              "Phòng ban (Để trống là tất cả)",
              applySelectedDepts,
              handleMultiSelectChange(setApplySelectedDepts), // Sử dụng hàm chung
              setDeptSearch,
              deptSearch,
              departments, // Danh sách đầy đủ TẤT CẢ phòng ban
              'departmentCode',
              'departmentName',
              "Tất cả phòng ban",
              filteredDepartments // Danh sách phòng ban đã được LỌC để hiển thị
            )}
          </Grid>
          <Grid item xs={12} sm={6}>
            {renderMultiSelect(
              "Chức vụ (Để trống là tất cả)",
              applySelectedJobs,
              handleMultiSelectChange(setApplySelectedJobs),
              setJobSearch,
              jobSearch,
              jobPositions,
              'code',
              'name',
              "Tất cả chức vụ",
              filteredJobPositions
            )}
          </Grid>
        </Grid>
        <Alert severity="info" variant="standard" icon={<InfoOutlinedIcon />} sx={{mt:2.5, backgroundColor: 'info.lighter', color: 'info.darker', '& .MuiAlert-icon': {color: 'info.main'}}}>
          Bộ cấu hình <strong>"{configTemplate?.templateName || 'N/A'}"</strong> {/* fallback */}
          với {(configTemplate?.definedShifts || []).length} ca và {/* fallback */}
          {Object.keys(configTemplate?.activeHardConstraints || {}).length} ràng buộc sẽ được sử dụng. {/* fallback */}
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
