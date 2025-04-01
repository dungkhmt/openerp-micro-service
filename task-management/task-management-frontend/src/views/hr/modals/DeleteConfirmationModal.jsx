import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
} from "@mui/material";
import "@/assets/css/DeleteConfirmationModal.css";

const DeleteConfirmationModal = ({
  open,
  onClose,
  onSubmit,
  title = "Delete Item",
  info = "Are you sure you want to delete this item?",
  cancelLabel = "Cancel",
  confirmLabel = "Delete",
}) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <div className="dialog-container">
        <DialogTitle className="dialog-title">{title}</DialogTitle>
        <DialogContent className="dialog-content">
          <p>{info}</p>
        </DialogContent>
        <DialogActions className="dialog-actions"
          style={{
            justifyContent: "center", 
          }}
        >
          <Button
            onClick={onClose}
            className="cancel-button"
          >
            {cancelLabel}
          </Button>
          <Button
            onClick={onSubmit}
            className="delete-button"
          >
            {confirmLabel}
          </Button>
        </DialogActions>
      </div>
    </Dialog>
  );
};

export default DeleteConfirmationModal;