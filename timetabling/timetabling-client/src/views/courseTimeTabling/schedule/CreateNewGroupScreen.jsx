import React, { useEffect, useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewGroupScreen({ open, handleClose, existingData, handleRefreshData }) {
  const [newGroup, setNewGroup] = useState('');

  useEffect(() => {
    console.log("data list: ", existingData)
  }, [])

  const handleCreate = () => {
    const requestNewGroup = {
      groupName: newGroup,
    };
    request("post", "/group/create", (res) => {
      // Call handleRefreshData to refresh the data 
      handleRefreshData();
      //close dialog
      handleClose();
    },
      {},
      requestNewGroup
    ).then();

    const requestUpdateClassOpened = {
      ids: existingData,
      groupName: newGroup
    };
    request("post", "/class-opened/update", (res) => {
      // Call handleRefreshData to refresh the data 
      handleRefreshData();
      //close dialog
      handleClose();
    },
      {},
      requestUpdateClassOpened
    ).then();
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
