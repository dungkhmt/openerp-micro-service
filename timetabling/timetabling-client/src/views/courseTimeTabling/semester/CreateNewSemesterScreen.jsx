import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewSemester({ open, handleClose, handleUpdate, handleRefreshData }) {
  const [newSemester, setNewSemester] = useState('');
  const [description, setDescription] = useState('');

  const handleCreate = () => {
    const requestData = {
      semester: newSemester,
      description: description
    };
    request("post", "/semester/create", (res) => {
      // Call your handleUpdate function if needed
      handleUpdate(res.data);
      // Call handleRefreshData to refresh the data 
      handleRefreshData();
      //close dialog
      handleClose();
    },
      {},
      requestData
    ).then();

    // Close the dialog
    handleClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-semester-dialog">
      <DialogTitle id="create-new-semester-dialog-title">Thêm học kỳ mới</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Tên kỳ học"
          fullWidth
          value={newSemester}
          onChange={(event) => setNewSemester(event.target.value)}
        />
        <div style={{ margin: '16px' }} />
        <TextField
          margin="dense"
          label="Mô tả"
          fullWidth
          value={description}
          onChange={(event) => setDescription(event.target.value)}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">
          Cancel
        </Button>
        <Button
          onClick={handleCreate}
          color="primary"
          disabled={!newSemester} // Disable the button if the input is empty
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
}
