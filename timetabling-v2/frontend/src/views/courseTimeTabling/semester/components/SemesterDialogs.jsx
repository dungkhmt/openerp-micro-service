import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";
import CreateNewSemester from "../CreateNewSemesterScreen";

export const DeleteConfirmDialog = ({ open, onClose, onConfirm, isDeleting }) => (
  <Dialog open={open} onClose={onClose}>
    <DialogTitle>Xác nhận xóa kỳ học</DialogTitle>
    <DialogContent>
      <Typography>Bạn có chắc chắn muốn xóa kỳ học này?</Typography>
    </DialogContent>
    <DialogActions>
      <Button onClick={onClose} color="secondary" disabled={isDeleting}>Hủy</Button>
      <Button onClick={onConfirm} color="primary" disabled={isDeleting}>{isDeleting ? 'Đang xóa...' : 'Xóa'}</Button>
    </DialogActions>
  </Dialog>
);

export const CreateEditDialog = ({ open, handleClose, selectedSemester }) => (
  <CreateNewSemester
    open={open}
    handleClose={handleClose}
    selectedSemester={selectedSemester}
  />
);