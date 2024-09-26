import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewSemester({ open, handleClose, handleUpdate, handleRefreshData, selectedSemester }) {
  const [newSemester, setNewSemester] = useState('');
  const [description, setDescription] = useState('');
  const [id, setId] = useState('');
  const [isUpdate, setIsUpdate] = useState(false);

  useEffect(() => {
    if (selectedSemester) {
      setNewSemester(selectedSemester.semester);
      setDescription(selectedSemester.description);
      setId(selectedSemester.id);
      setIsUpdate(true);
    } else {
      setNewSemester('');
      setDescription('');
      setId('');
      setIsUpdate(false);
    }
  }, [selectedSemester]);

  const handleCreate = () => {
    const requestData = {
      id: id,
      semester: newSemester,
      description: description
    };

    const apiEndpoint = isUpdate ? `/semester/update` : "/semester/create";

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
        {isUpdate ? 'Chỉnh sửa thông tin' : 'Thêm học kỳ mới'}
      </DialogTitle>
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
          Hủy
        </Button>
        <Button
          onClick={handleCreate}
          color="primary"
          disabled={!newSemester}
        >
          {isUpdate ? 'Cập nhật' : 'Tạo mới'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
