import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";


export default function DeleteConfirmDialog({ open, onClose, onConfirm, isDeleting }) {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Xác nhận xóa môn học</DialogTitle>
      <DialogContent>
        <Typography>Bạn có chắc chắn muốn xóa môn học này?</Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary">Hủy</Button>
        <Button onClick={onConfirm} color="primary" disabled={isDeleting}>Xóa</Button>
      </DialogActions>
    </Dialog>
  );
}