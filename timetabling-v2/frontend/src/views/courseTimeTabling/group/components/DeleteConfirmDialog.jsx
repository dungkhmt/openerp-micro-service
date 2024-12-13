
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from '@mui/material';
import React from 'react';

const DeleteConfirmDialog = React.memo(({ open, onClose, onConfirm }) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Xác nhận xóa nhóm</DialogTitle>
      <DialogContent>
        <Typography>Bạn có chắc chắn muốn xóa nhóm này?</Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Hủy</Button>
        <Button onClick={onConfirm} color="error">Xóa</Button>
      </DialogActions>
    </Dialog>
  );
});

export default DeleteConfirmDialog;