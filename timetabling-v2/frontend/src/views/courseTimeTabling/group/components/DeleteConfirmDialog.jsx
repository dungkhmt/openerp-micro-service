import React from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";

const DeleteConfirmDialog = ({ open, onClose, onConfirm }) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Xác nhận xóa</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Bạn có chắc chắn muốn xóa nhóm này không?
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Hủy</Button>
        <Button onClick={onConfirm} color="error" autoFocus>
          Xóa
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteConfirmDialog;