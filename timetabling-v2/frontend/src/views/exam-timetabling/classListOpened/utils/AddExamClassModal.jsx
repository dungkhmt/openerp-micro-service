import React, { useState } from 'react';
import { 
  Button, 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  Grid, 
  IconButton, 
  TextField 
} from "@mui/material";
import { Close } from '@mui/icons-material'

const AddExamClassModal = ({ 
  open, 
  onClose, 
  onSubmit 
}) => {
  const [formData, setFormData] = useState({
    examClassId: '',
    classId: '',
    courseId: '',
    courseName: '',
    description: '',
    groupId: '',
    numberOfStudents: '',
    school: '',
    period: '',
    managementCode: ''
  });

  const [errors, setErrors] = useState({});

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error when user types
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    Object.keys(formData).forEach(key => {
      if (!formData[key].toString().trim()) {
        newErrors[key] = 'Trường này không được để trống';
      }
    });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {
    if (validateForm()) {
      onSubmit(formData);
      setFormData({
        examClassId: '',
        classId: '',
        courseId: '',
        courseName: '',
        description: '',
        groupId: '',
        numberOfStudents: '',
        school: '',
        period: '',
        managementCode: ''
      });
    }
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle 
        sx={{ 
          pb: 1,
          textAlign: 'center',
          borderBottom: '1px solid #e0e0e0',
          mb: 1,
          position: 'relative'
        }}
      >
        Thêm lớp thi mới
        <IconButton
          onClick={onClose}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
            color: (theme) => theme.palette.grey[500],
          }}
        >
          <Close />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ pt: 1 }}>
          {/* First row - IDs */}
          <Grid item xs={4}>
            <TextField
              name="examClassId"
              label="Mã lớp thi"
              fullWidth
              value={formData.examClassId}
              onChange={handleChange}
              error={!!errors.examClassId}
              helperText={errors.examClassId}
              required
              size="small"
            />
          </Grid>
          <Grid item xs={4}>
            <TextField
              name="classId"
              label="Mã lớp học"
              fullWidth
              value={formData.classId}
              onChange={handleChange}
              error={!!errors.classId}
              helperText={errors.classId}
              required
              size="small"
            />
          </Grid>
          <Grid item xs={4}>
            <TextField
              name="managementCode"
              label="Mã quản lý"
              fullWidth
              value={formData.managementCode}
              onChange={handleChange}
              error={!!errors.managementCode}
              helperText={errors.managementCode}
              required
              size="small"
            />
          </Grid>

          {/* Second row - Course info */}
          <Grid item xs={4}>
            <TextField
              name="courseId"
              label="Mã học phần"
              fullWidth
              value={formData.courseId}
              onChange={handleChange}
              error={!!errors.courseId}
              helperText={errors.courseId}
              required
              size="small"
            />
          </Grid>
          <Grid item xs={8}>
            <TextField
              name="courseName"
              label="Tên học phần"
              fullWidth
              value={formData.courseName}
              onChange={handleChange}
              error={!!errors.courseName}
              helperText={errors.courseName}
              required
              size="small"
            />
          </Grid>

          {/* Third row - Group info */}
          <Grid item xs={4}>
            <TextField
              name="groupId"
              label="Nhóm"
              fullWidth
              value={formData.groupId}
              onChange={handleChange}
              error={!!errors.groupId}
              helperText={errors.groupId}
              required
              size="small"
            />
          </Grid>
          <Grid item xs={4}>
            <TextField
              name="period"
              label="Đợt"
              fullWidth
              value={formData.period}
              onChange={handleChange}
              error={!!errors.period}
              helperText={errors.period}
              required
              size="small"
            />
          </Grid>
          <Grid item xs={4}>
            <TextField
              name="numberOfStudents"
              label="Số lượng SV"
              fullWidth
              type="number"
              value={formData.numberOfStudents}
              onChange={handleChange}
              error={!!errors.numberOfStudents}
              helperText={errors.numberOfStudents}
              required
              size="small"
            />
          </Grid>

          {/* Fourth row - School */}
          <Grid item xs={12}>
            <TextField
              name="school"
              label="Trường/Khoa"
              fullWidth
              value={formData.school}
              onChange={handleChange}
              error={!!errors.school}
              helperText={errors.school}
              required
              size="small"
            />
          </Grid>

          {/* Fifth row - Description */}
          <Grid item xs={12}>
            <TextField
              name="description"
              label="Ghi chú"
              fullWidth
              multiline
              rows={2}
              value={formData.description}
              onChange={handleChange}
              error={!!errors.description}
              helperText={errors.description}
              required
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} variant="outlined" color="inherit">
          Hủy
        </Button>
        <Button onClick={handleSubmit} variant="contained" color="primary">
          Thêm lớp thi
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddExamClassModal;
