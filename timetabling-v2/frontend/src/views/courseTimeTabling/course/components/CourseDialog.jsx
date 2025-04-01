import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";

export default function CourseDialog({ 
  open, 
  handleClose, 
  id,
  courseName,
  credit,
  setId,
  setCourseName,
  setCredit,
  onSubmit, 
  isUpdate 
}) {
  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="course-dialog">
      <DialogTitle id="course-dialog-title">
        {isUpdate ? 'Chỉnh sửa thông tin' : 'Thêm môn học mới'}
      </DialogTitle>
      <DialogContent>
        <TextField
          margin="dense"
          label="Mã môn học"
          fullWidth
          value={id}
          onChange={(e) => setId(e.target.value)}
          disabled={isUpdate}
        />
        <div style={{ margin: '16px' }} />
        <TextField
          autoFocus
          margin="dense"
          label="Tên môn học"
          fullWidth
          value={courseName}
          onChange={(e) => setCourseName(e.target.value)}
        />
        <div style={{ margin: '16px' }} />
        <TextField
          margin="dense"
          label="Số tín chỉ"
          fullWidth
          value={credit}
          onChange={(e) => setCredit(e.target.value)}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">Hủy</Button>
        <Button onClick={onSubmit} color="primary" disabled={!courseName}>
          {isUpdate ? 'Cập nhật' : 'Tạo mới'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}