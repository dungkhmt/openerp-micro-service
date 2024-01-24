import React, { useEffect, useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Autocomplete } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewGroupScreen({ open, handleClose, existingData, handleRefreshData }) {
  const [newGroup, setNewGroup] = useState('');
  const [newPriorityBuilding, setNewPriorityBuilding] = useState('');
  const [buildings, setBuildings] = useState([]);

  useEffect(() => {
    request("get", "/classroom/get-all-building", (res) => {
      setBuildings(res.data);
    });
  }, [])

  const handleCreate = () => {

    const requestNewGroup = {
      groupName: newGroup,
      priorityBuilding: newPriorityBuilding
    };

    const requestUpdateClassOpened = {
      ids: existingData,
      groupName: newGroup
    };

    request("post", "/group/create", (res) => {

      //if success, do request update class opened
      request("post", "/class-opened/group-assign", (res) => {
        // Call handleRefreshData to refresh the data 
        handleRefreshData();
        //close dialog
        handleClose();
      },
        {},
        requestUpdateClassOpened
      ).then();

      // Call handleRefreshData to refresh the data 
      handleRefreshData();
      //close dialog
      handleClose();
    },
      {
      },
      requestNewGroup
    ).then();
  };

  const handleInputChange = (event) => {
    setNewGroup(event.target.value);
  };

  const handleBuildingpriorityChange = (event, newValue) => {
    setNewPriorityBuilding(newValue);
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
          disabled={!newGroup || !newPriorityBuilding} // Disable the button if the input is empty
        >
          Tạo mới
        </Button>
      </DialogActions>
    </Dialog>
  );
}
