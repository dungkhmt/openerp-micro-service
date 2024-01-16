import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewSemester({ open, handleClose, handleUpdate, handleRefreshData, selectedClassroom }) {
  const [newClassroom, setNewClassroom] = useState('');
  const [building, setBuilding] = useState('');
  const [quantityMax, setQuantityMax] = useState('');
  const [description, setDescription] = useState('');
  const [id, setId] = useState('');
  const [isUpdate, setIsUpdate] = useState(false);

  useEffect(() => {
    if (selectedClassroom) {
      setNewClassroom(selectedClassroom.classroom);
      setBuilding(selectedClassroom.building);
      setQuantityMax(selectedClassroom.quantityMax);
      setDescription(selectedClassroom.description);
      setId(selectedClassroom.id);
      setIsUpdate(true);
    } else {
      setNewClassroom('');
      setBuilding('');
      setQuantityMax('');
      setDescription('');
      setId('');
      setIsUpdate(false);
    }
  }, [selectedClassroom]);

  const handleCreate = () => {
    const requestData = {
      id: id,
      classroom: newClassroom,
      building: building,
      quantityMax: quantityMax,
      description: description
    };

    const apiEndpoint = isUpdate ? `/classroom/update` : "/classroom/create";

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

  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-semester-dialog">
      <DialogTitle id="create-new-semester-dialog-title">
        {isUpdate ? 'Chỉnh sửa thông tin' : 'Thêm phòng học mới'}
      </DialogTitle>
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
          Hủy
        </Button>
        <Button
          onClick={handleCreate}
          color="primary"
          disabled={!newClassroom || !building || !quantityMax}
        >
          {isUpdate ? 'Cập nhật' : 'Tạo mới'}        
        </Button>
      </DialogActions>
    </Dialog>
  );
}
