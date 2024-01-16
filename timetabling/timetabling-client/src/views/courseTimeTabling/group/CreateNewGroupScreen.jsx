import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Autocomplete } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewSemester({ open, handleClose, handleUpdate, handleRefreshData, selectedGroup }) {
  const [newGroup, setNewGroup] = useState('');
  const [buildings, setBuildings] = useState('');
  const [newPriorityBuilding, setNewPriorityBuilding] = useState('');
  const [id, setId] = useState('');
  const [isUpdate, setIsUpdate] = useState(false);

  useEffect(() => {
    request("get", "/classroom/get-all-building", (res) => {
      setBuildings(res.data);
    });

    if (selectedGroup) {
      setNewGroup(selectedGroup.groupName);
      setNewPriorityBuilding(selectedGroup.priorityBuilding);
      setId(selectedGroup.id);
      setIsUpdate(true);
    } else {
      setNewGroup('');
      setNewPriorityBuilding('');
      setId('');
      setIsUpdate(false);
    }
  }, [selectedGroup]);

  const handleCreate = () => {
    const requestData = {
      id: id,
      groupName: newGroup,
      priorityBuilding: newPriorityBuilding,
    };

    const apiEndpoint = isUpdate ? `/group/update` : "/group/create";

    request("post", apiEndpoint, (res) => {
      handleUpdate(res.data);
      handleRefreshData();
      handleClose();
    },
      (error) => {
        toast.error(error.response.data);
      },
      requestData
    ).then();

    // handleClose();
  };

  const handleBuildingpriorityChange = (event, newValue) => {
    setNewPriorityBuilding(newValue);
  };

  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-semester-dialog">
      <DialogTitle id="create-new-group-dialog-title">
        {isUpdate ? 'Chỉnh sửa thông tin' : 'Thêm nhóm mới'}
      </DialogTitle>
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
          disabled={!newGroup || !newPriorityBuilding}
        >
          {isUpdate ? 'Cập nhật' : 'Tạo mới'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
