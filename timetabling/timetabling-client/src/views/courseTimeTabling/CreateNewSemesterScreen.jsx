import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../api";

export default function CreateNewSemester({ open, handleClose, handleUpdate, handleRefreshData }) {
  const [newSemester, setNewSemester] = useState('');

  const handleCreate = () => {
    // Call your API to create a new semester here
    // Assume there is a function `createSemester` that makes the API call
    // Replace it with your actual API call logic
    request(newSemester)
      .then(() => {
        // Close the dialog and trigger data update
        handleClose();
        handleRefreshData();
      })
      .catch((error) => {
        // Handle API error, if any
        console.error('Error creating semester:', error);
      });
  };

  const handleInputChange = (event) => {
    setNewSemester(event.target.value);
  };

  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-semester-dialog">
      <DialogTitle id="create-new-semester-dialog-title">Thêm học kỳ mới</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Semester Name"
          fullWidth
          value={newSemester}
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
          disabled={!newSemester} // Disable the button if the input is empty
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
}
