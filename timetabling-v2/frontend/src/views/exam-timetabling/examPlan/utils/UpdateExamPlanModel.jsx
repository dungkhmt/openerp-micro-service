import { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Grid,
  CircularProgress,
  IconButton
} from '@mui/material';
import { Close } from '@mui/icons-material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { vi } from 'date-fns/locale';

const UpdateExamPlanModal = ({ open, onClose, onSave, isSaving, examPlan }) => {
  const [formData, setFormData] = useState({
    id: '',
    name: '',
    description: '',
    startTime: null,
    endTime: null
  });


  useEffect(() => {
    if (examPlan && open) {
      setFormData({
        id: examPlan.id || '',
        name: examPlan.name || '',
        description: examPlan.description || '',
        startTime: examPlan.startTime ? new Date(examPlan.startTime) : null,
        endTime: examPlan.endTime ? new Date(examPlan.endTime) : null
      });
    }
  }, [examPlan, open]);

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
    
    // Clear error when user types
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ''
      });
    }
  };

  const handleDateChange = (name, date) => {
    setFormData({
      ...formData,
      [name]: date
    });
    
    // Clear error when user selects a date
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ''
      });
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Tên kế hoạch không được để trống';
    }
    
    if (!formData.startTime) {
      newErrors.startTime = 'Ngày bắt đầu không được để trống';
    }
    
    if (!formData.endTime) {
      newErrors.endTime = 'Ngày kết thúc không được để trống';
    }
    
    if (formData.startTime && formData.endTime && formData.startTime > formData.endTime) {
      newErrors.endTime = 'Ngày kết thúc phải sau ngày bắt đầu';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {
    if (validateForm()) {
      onSave(formData);

      setFormData({
        id: '',
        name: '',
        description: '',
        startTime: null,
        endTime: null
      });
      setErrors({});
    }
  };

  const handleCloseModal = () => {
    setFormData({
      id: '',
      name: '',
      description: '',
      startTime: null,
      endTime: null
    });
    setErrors({});
    onClose();
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleCloseModal}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle 
        sx={{ 
          pb: 1,
          textAlign: 'center',
          borderBottom: '1px solid #e0e0e0',
          mb: 1,
          position: 'relative',
          fontWeight: 600,
          background: 'linear-gradient(90deg, #1976D2, #2196F3)',
        }}

        color={'white'}
        fontSize={'h5.fontSize'}
      >
        Cập Nhật Kế Hoạch Thi
        <IconButton
          onClick={handleCloseModal}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
            color:  'white'
          }}
        >
          <Close />
        </IconButton>
      </DialogTitle>
      
      <DialogContent>
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
          <Box sx={{ pt: 2 }}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField
                  name="name"
                  label="Tên kế hoạch thi"
                  fullWidth
                  value={formData.name}
                  onChange={handleChange}
                  error={!!errors.name}
                  helperText={errors.name}
                  required
                />
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  name="description"
                  label="Mô tả (tùy chọn)"
                  fullWidth
                  multiline
                  rows={3}
                  value={formData.description}
                  onChange={handleChange}
                />
              </Grid>
              
         {/* Row with date pickers */}
              <Grid item xs={12}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <DatePicker
                      label="Ngày bắt đầu"
                      value={formData.startTime}
                      onChange={(date) => handleDateChange('startTime', date)}
                      slotProps={{
                        textField: {
                          fullWidth: true,
                          required: true,
                          error: !!errors.startTime,
                          helperText: errors.startTime,
                          size: "small"
                        }
                      }}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <DatePicker
                      label="Ngày kết thúc"
                      value={formData.endTime}
                      onChange={(date) => handleDateChange('endTime', date)}
                      slotProps={{
                        textField: {
                          fullWidth: true,
                          required: true,
                          error: !!errors.endTime,
                          helperText: errors.endTime,
                          size: "small"
                        }
                      }}
                    />
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </Box>
        </LocalizationProvider>
      </DialogContent>
      
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleCloseModal} variant="outlined" color="inherit">
          Hủy
        </Button>
        <Button 
          onClick={handleSubmit} 
          variant="contained" 
          color="primary"
          disabled={isSaving}
          startIcon={isSaving ? <CircularProgress size={20} /> : null}
        >
          {isSaving ? 'Đang lưu...' : 'Lưu kế hoạch'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default UpdateExamPlanModal;
