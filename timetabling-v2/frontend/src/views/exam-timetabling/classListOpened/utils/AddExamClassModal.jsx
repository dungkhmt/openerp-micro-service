import React, { useState } from 'react';
import { 
  Box, 
  Button, 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  TextField 
} from "@mui/material";

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
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>Thêm lớp thi mới</DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            name="examClassId"
            label="Mã lớp thi"
            fullWidth
            value={formData.examClassId}
            onChange={handleChange}
            error={!!errors.examClassId}
            helperText={errors.examClassId}
            required
          />
          <TextField
            name="classId"
            label="Mã lớp học"
            fullWidth
            value={formData.classId}
            onChange={handleChange}
            error={!!errors.classId}
            helperText={errors.classId}
            required
          />
          <TextField
            name="courseId"
            label="Mã học phần"
            fullWidth
            value={formData.courseId}
            onChange={handleChange}
            error={!!errors.courseId}
            helperText={errors.courseId}
            required
          />
          <TextField
            name="courseName"
            label="Tên học phần"
            fullWidth
            value={formData.courseName}
            onChange={handleChange}
            error={!!errors.courseName}
            helperText={errors.courseName}
            required
          />
          <TextField
            name="description"
            label="Ghi chú"
            fullWidth
            value={formData.description}
            onChange={handleChange}
            error={!!errors.description}
            helperText={errors.description}
            required
          />
          <TextField
            name="groupId"
            label="Mã nhóm"
            fullWidth
            value={formData.groupId}
            onChange={handleChange}
            error={!!errors.groupId}
            helperText={errors.groupId}
            required
          />
          <TextField
            name="numberOfStudents"
            label="Số lượng sinh viên"
            fullWidth
            type="number"
            value={formData.numberOfStudents}
            onChange={handleChange}
            error={!!errors.numberOfStudents}
            helperText={errors.numberOfStudents}
            required
          />
          <TextField
            name="school"
            label="Trường"
            fullWidth
            value={formData.school}
            onChange={handleChange}
            error={!!errors.school}
            helperText={errors.school}
            required
          />
          <TextField
            name="period"
            label="Kỳ học"
            fullWidth
            value={formData.period}
            onChange={handleChange}
            error={!!errors.period}
            helperText={errors.period}
            required
          />
          <TextField
            name="managementCode"
            label="Mã quản lý"
            fullWidth
            value={formData.managementCode}
            onChange={handleChange}
            error={!!errors.managementCode}
            helperText={errors.managementCode}
            required
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary">
          Hủy
        </Button>
        <Button onClick={handleSubmit} color="primary" variant="contained">
          Thêm lớp thi
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddExamClassModal;
