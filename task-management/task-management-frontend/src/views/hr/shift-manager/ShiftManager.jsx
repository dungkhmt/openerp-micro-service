// src/features/rosterConfiguration/ShiftManager.jsx
import React, { useState } from 'react';
import {
  Box, Typography, TextField, Button, Paper,
  FormControlLabel, Grid, IconButton,
  Switch, Tooltip, Alert
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';

export default function ShiftManager({ shifts, setShifts }) {
  const [newShiftName, setNewShiftName] = useState('');
  const [newShiftStart, setNewShiftStart] = useState('07:00');
  const [newShiftEnd, setNewShiftEnd] = useState('15:00');
  const [isNewShiftNightShift, setIsNewShiftNightShift] = useState(false);
  const [newMinEmployees, setNewMinEmployees] = useState('1');
  const [newMaxEmployees, setNewMaxEmployees] = useState('5');
  const [error, setError] = useState('');

  const addShift = () => {
    setError('');
    if (!newShiftName.trim()) { setError('Tên ca không được để trống.'); return; }
    if (!newShiftStart || !newShiftEnd) { setError('Giờ bắt đầu và kết thúc không được để trống.'); return; }

    const minEmp = newMinEmployees === '' ? 0 : parseInt(newMinEmployees, 10);
    const maxEmp = newMaxEmployees === '' || newMaxEmployees === '0' ? null : parseInt(newMaxEmployees, 10); // null nghĩa là không giới hạn

    if (isNaN(minEmp) || minEmp < 0) { setError('NV tối thiểu không hợp lệ (phải là số >= 0).'); return; }
    if (maxEmp !== null && (isNaN(maxEmp) || maxEmp <= 0)) { // Nếu có max thì max phải > 0
      setError('NV tối đa không hợp lệ (phải là số > 0 hoặc để trống/0 cho KGH).'); return;
    }
    if (maxEmp !== null && minEmp > maxEmp) {
      setError('NV tối thiểu không thể lớn hơn NV tối đa.'); return;
    }
    if (shifts.some(shift => shift.name.toLowerCase() === newShiftName.trim().toLowerCase())) {
      setError(`Tên ca "${newShiftName.trim()}" đã tồn tại.`); return;
    }

    const newShift = {
      id: `shift_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      name: newShiftName.trim(), startTime: newShiftStart, endTime: newShiftEnd, isNightShift: isNewShiftNightShift,
      minEmployees: minEmp, maxEmployees: maxEmp
    };
    setShifts(prevShifts => [...prevShifts, newShift]);
    setNewShiftName(''); setNewShiftStart('07:00'); setNewShiftEnd('15:00'); setIsNewShiftNightShift(false);
    setNewMinEmployees('1'); setNewMaxEmployees('5');
  };

  const removeShift = (id) => setShifts(prevShifts => prevShifts.filter(shift => shift.id !== id));

  const handleShiftChange = (id, field, value) => {
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
        if (field === 'minEmployees') {
          const numVal = parseInt(processedValue, 10);
          finalValue = processedValue === '' ? '' : (isNaN(numVal) ? shift[field] : numVal);
        } else if (field === 'maxEmployees') {
          if (processedValue === '' || processedValue === '0') {
            finalValue = null; // Coi như không giới hạn
          } else {
            const numVal = parseInt(processedValue, 10);
            finalValue = isNaN(numVal) ? shift[field] : numVal;
          }
        }
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
      {/* Form thêm ca - căn chỉnh giống ảnh */}
      <Box component="form" noValidate autoComplete="off" sx={{ mb: 1.5 }}>
        <Grid container spacing={1.5} alignItems="center"> {/* Sử dụng alignItems="center" cho căn giữa dọc tốt hơn */}
          <Grid item xs={12} sm={6} md={2.5}><TextField fullWidth label="Tên ca (*)" value={newShiftName} onChange={(e) => setNewShiftName(e.target.value)} InputLabelProps={{ shrink: true }} /></Grid>
          <Grid item xs={6} sm={3} md={1.5}><TextField fullWidth label="Bắt đầu (*)" type="time" value={newShiftStart} onChange={(e) => setNewShiftStart(e.target.value)} InputLabelProps={{ shrink: true }} /></Grid>
          <Grid item xs={6} sm={3} md={1.5}><TextField fullWidth label="Kết thúc (*)" type="time" value={newShiftEnd} onChange={(e) => setNewShiftEnd(e.target.value)} InputLabelProps={{ shrink: true }} /></Grid>
          <Grid item xs={6} sm={3} md={1.5}><TextField fullWidth label="NV Min" type="number" value={newMinEmployees} onChange={(e) => setNewMinEmployees(e.target.value)} inputProps={{ min: 0 }} InputLabelProps={{ shrink: true }}/></Grid>
          <Grid item xs={6} sm={3} md={1.5}><TextField fullWidth label="NV Max" type="number" value={newMaxEmployees} onChange={(e) => setNewMaxEmployees(e.target.value)} inputProps={{ min: 0 }} InputLabelProps={{ shrink: true }} placeholder="Trống/0 = KGH"/></Grid>
          <Grid item xs={12} sm={6} md={2} sx={{display: 'flex', alignItems: 'center', height: '53px' /* Căn với TextField khi có placeholder/helper */}}>
            <FormControlLabel control={<Switch checked={isNewShiftNightShift} onChange={(e) => setIsNewShiftNightShift(e.target.checked)} />} label="Ca đêm?" sx={{whiteSpace: 'nowrap', m:0}} />
          </Grid>
          <Grid item xs={12} sm={6} md={1.5}>
            <Button fullWidth variant="contained" color="primary" startIcon={<AddCircleOutlineIcon />} onClick={addShift} sx={{ height: '40px' }}>Thêm</Button>
          </Grid>
        </Grid>
      </Box>
      <Typography variant="caption" sx={{ mb: 0.5, color: 'text.secondary', display:'block' }}>Danh sách ca đã định nghĩa ({shifts.length}):</Typography>
      <Box sx={{ maxHeight: '250px', overflowY: 'auto', pr: 0.5 }}>
        {shifts.length === 0 && <Typography sx={{ textAlign: 'center', color: 'text.secondary', p: 2, fontStyle: 'italic' }}>Chưa có ca làm việc nào.</Typography>}
        {shifts.map(shift => (
          <Paper key={shift.id} variant="outlined" sx={{ p: 1.5, mb: 1, borderColor: 'rgba(0,0,0,0.1)'}}>
            <Grid container spacing={1} alignItems="center">
              <Grid item xs={12} sm={12} md={2.5}>
                <TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: false}} fullWidth variant="standard" label="Tên ca" value={shift.name} onChange={(e) => handleShiftChange(shift.id, 'name', e.target.value)} />
              </Grid>
              <Grid item xs={6} sm={3} md={1.5}>
                <TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: false}} fullWidth variant="standard" label="BĐ" type="time" value={shift.startTime} onChange={(e) => handleShiftChange(shift.id, 'startTime', e.target.value)} />
              </Grid>
              <Grid item xs={6} sm={3} md={1.5}>
                <TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: false}} fullWidth variant="standard" label="KT" type="time" value={shift.endTime} onChange={(e) => handleShiftChange(shift.id, 'endTime', e.target.value)} />
              </Grid>
              <Grid item xs={6} sm={2.5} md={1.25}>
                <TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: false}} fullWidth variant="standard" label="Min" type="number" value={shift.minEmployees === '' ? '' : shift.minEmployees} onChange={(e) => handleShiftChange(shift.id, 'minEmployees', e.target.value)} inputProps={{ min: 0 }} />
              </Grid>
              <Grid item xs={6} sm={2.5} md={1.25}>
                <TextField InputLabelProps={{ shrink: true }} InputProps={{disableUnderline: false}} fullWidth variant="standard" label="Max" type="number" value={shift.maxEmployees === null ? '' : shift.maxEmployees} onChange={(e) => handleShiftChange(shift.id, 'maxEmployees', e.target.value)} inputProps={{ min: 0 }} placeholder="KGH"/>
              </Grid>
              <Grid item xs={9} sm={2} md={2.5} sx={{display: 'flex', alignItems:'center'}}>
                <FormControlLabel control={<Switch size="small" checked={shift.isNightShift} onChange={(e) => handleShiftChange(shift.id, 'isNightShift', e.target.checked)} />} label="Đêm" sx={{fontSize: '0.8rem'}} />
              </Grid>
              <Grid item xs={3} sm={1} md={1} sx={{textAlign: 'right'}}>
                <Tooltip title="Xóa ca"><IconButton onClick={() => removeShift(shift.id)} color="error" size="small"><DeleteIcon /></IconButton></Tooltip>
              </Grid>
            </Grid>
          </Paper>
        ))}
      </Box>
    </Paper>
  );
}