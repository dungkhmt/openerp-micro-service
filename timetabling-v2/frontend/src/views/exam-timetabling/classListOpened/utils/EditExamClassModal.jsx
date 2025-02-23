import React from 'react';
import { 
  Button, 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  TextField,
  Grid,
  IconButton
} from "@mui/material";
import { Close } from '@mui/icons-material'

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
      maxWidth="md"  // Changed to md for more width
      fullWidth
    >
      <DialogTitle 
        sx={{ 
          pb: 1,
          textAlign: 'center',
          borderBottom: '1px solid #e0e0e0',
          mb: 1,
          position: 'relative'  // Added for absolute positioning of close button
        }}
      >
        Chỉnh sửa thông tin lớp thi
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
              onChange={onChange}
              size="small"
            />
          </Grid>
          <Grid item xs={4}>
            <TextField
              name="classId"
              label="Mã lớp học"
              fullWidth
              value={formData.classId}
              onChange={onChange}
              size="small"
            />
          </Grid>
          <Grid item xs={4}>
            <TextField
              name="managementCode"
              label="Mã quản lý"
              fullWidth
              value={formData.managementCode}
              onChange={onChange}
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
              onChange={onChange}
              size="small"
            />
          </Grid>
          <Grid item xs={8}>
            <TextField
              name="courseName"
              label="Tên học phần"
              fullWidth
              value={formData.courseName}
              onChange={onChange}
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
              onChange={onChange}
              size="small"
            />
          </Grid>
          <Grid item xs={4}>
            <TextField
              name="period"
              label="Đợt"
              fullWidth
              value={formData.period}
              onChange={onChange}
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
              onChange={onChange}
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
              onChange={onChange}
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
              onChange={onChange}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} variant="outlined" color="inherit">
          Hủy
        </Button>
        <Button onClick={onSubmit} variant="contained" color="primary">
          Lưu thay đổi
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default EditExamClassModal;
