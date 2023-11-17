import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../api";

export default function CreateNewGroupScreen({ open, handleClose, handleUpdate, handleRefreshData }) {
  const [newGroup, setNewGroup] = useState('');

  const handleCreate = () => {
    request(newGroup)
      .then(() => {
        // Close the dialog and trigger data update
        handleClose();
        handleRefreshData();
      })
      .catch((error) => {
        // Handle API error, if any
        console.error('Error creating group:', error);
      });
  };

  const handleInputChange = (event) => {
    setNewGroup(event.target.value);
  };

  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-semester-dialog">
      <DialogTitle id="create-new-semester-dialog-title">Thêm vào nhóm mới</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Tên nhóm"
          fullWidth
          value={newGroup}
          onChange={handleInputChange}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">
          Cancel
        </Button>
        <Button
          onClick={handleCreate}
          color="primary"
          disabled={!newGroup} // Disable the button if the input is empty
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
}
