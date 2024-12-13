import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";

export const DeleteConfirmDialog = ({ open, onClose, onConfirm }) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Xác nhận xóa phòng học</DialogTitle>
      <DialogContent>
        <Typography>Bạn có chắc chắn muốn xóa phòng học này?</Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary">
          Hủy
        </Button>
        <Button onClick={onConfirm} color="primary">
          Xóa
        </Button>
      </DialogActions>
    </Dialog>
  );
};
