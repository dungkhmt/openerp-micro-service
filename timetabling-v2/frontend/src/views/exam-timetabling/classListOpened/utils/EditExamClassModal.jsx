import React from 'react';
import { 
  Box, 
  Button, 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  TextField 
} from "@mui/material";

const EditExamClassModal = ({ 
  open, 
  onClose, 
  formData, 
  onChange, 
  onSubmit 
}) => {
  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>Chỉnh sửa thông tin lớp thi</DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            name="examClassId"
            label="Mã lớp thi"
            fullWidth
            value={formData.examClassId}
            onChange={onChange}
          />
          <TextField
            name="classId"
            label="Mã lớp học"
            fullWidth
            value={formData.classId}
            onChange={onChange}
          />
          <TextField
            name="courseId"
            label="Mã học phần"
            fullWidth
            value={formData.courseId}
            onChange={onChange}
          />
          <TextField
            name="courseName"
            label="Tên học phần"
            fullWidth
            value={formData.courseName}
            onChange={onChange}
          />
          <TextField
            name="description"
            label="Ghi chú"
            fullWidth
            multiline
            rows={2}
            value={formData.description}
            onChange={onChange}
          />
          <TextField
            name="groupId"
            label="Nhóm"
            fullWidth
            value={formData.groupId}
            onChange={onChange}
          />
          <TextField
            name="period"
            label="Đợt"
            fullWidth
            value={formData.period}
            onChange={onChange}
          />
          <TextField
            name="numberOfStudents"
            label="Số lượng SV"
            fullWidth
            type="number"
            value={formData.numberOfStudents}
            onChange={onChange}
          />
          <TextField
            name="school"
            label="Trường/Khoa"
            fullWidth
            value={formData.school}
            onChange={onChange}
          />
           <TextField
            name="managementCode"
            label="Mã quản lý"
            fullWidth
            value={formData.managementCode}
            onChange={onChange}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary">
          Hủy
        </Button>
        <Button onClick={onSubmit} color="primary" variant="contained">
          Lưu thay đổi
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default EditExamClassModal;
