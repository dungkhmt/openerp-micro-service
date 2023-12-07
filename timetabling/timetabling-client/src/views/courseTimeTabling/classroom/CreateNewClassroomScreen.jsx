import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewSemester({ open, handleClose, handleUpdate, handleRefreshData }) {
  const [newClassroom, setNewClassroom] = useState('');
  const [building, setBuilding] = useState('');
  const [quantityMax, setQuantityMax] = useState('');
  const [description, setDescription] = useState('');

  const handleCreate = () => {
    // Call your API to create a new semester here
    // Assume there is a function `createSemester` that makes the API call
    // Replace it with your actual API call logic
    const requestData = {
      classroom: newClassroom,
      building: building,
      quantityMax: quantityMax,
      description: description
    };
    request("post", "/classroom/create", (res) => {
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
      <DialogTitle id="create-new-semester-dialog-title">Thêm lớp học mới</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Phòng học"
          fullWidth
          value={newClassroom}
          onChange={(event) => setNewClassroom(event.target.value)}
        />
        <div style={{ margin: '16px' }} />
        <TextField
          margin="dense"
          label="Tòa nhà"
          fullWidth
          value={building}
          onChange={(event) => setBuilding(event.target.value)}
        />
        <div style={{ margin: '16px' }} />
        <TextField
          margin="dense"
          label="Số lượng chỗ ngồi"
          fullWidth
          value={quantityMax}
          onChange={(event) => setQuantityMax(event.target.value)}
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
          disabled={!newClassroom} // Disable the button if the input is empty
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
}
