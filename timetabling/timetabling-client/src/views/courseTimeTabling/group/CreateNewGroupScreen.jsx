import React, { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Autocomplete } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewSemester({ open, handleClose, handleUpdate, handleRefreshData }) {
  const [newGroup, setNewGroup] = useState('');
  const [buildings, setBuildings] = useState('');
  const [newPriorityBuilding, setNewPriorityBuilding] = useState('');

  useEffect(() => {
    request("get", "/classroom/get-all-building", (res) => {
      setBuildings(res.data);
    });
  }, [])

  const handleCreate = () => {
    const requestData = {
      groupName: newGroup,
      priorityBuilding: newPriorityBuilding,
    };
    request("post", "/group/create", (res) => {
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

  const handleBuildingpriorityChange = (event, newValue) => {
    setNewPriorityBuilding(newValue);
  };

  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-semester-dialog">
      <DialogTitle id="create-new-group-dialog-title">Thêm nhóm mới</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Tên nhóm"
          fullWidth
          value={newGroup}
          onChange={(event) => setNewGroup(event.target.value)}
        />
        <div style={{ margin: '16px' }} />
        <Autocomplete
          options={buildings}
          getOptionLabel={(option) => option}
          style={{ width: 250, marginTop: '8px' }}
          value={newPriorityBuilding}
          renderInput={(params) => <TextField {...params} label="Tòa nhà ưu tiên" />}
          onChange={handleBuildingpriorityChange}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">
          Hủy
        </Button>
        <Button
          onClick={handleCreate}
          color="primary"
          disabled={!newGroup} // Disable the button if the input is empty
        >
          Tạo mới
        </Button>
      </DialogActions>
    </Dialog>
  );
}
