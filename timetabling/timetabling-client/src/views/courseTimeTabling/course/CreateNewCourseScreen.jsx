import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import { request } from "../../../api";

export default function CreateNewCourse({ open, handleClose, handleUpdate, handleRefreshData, selectedCourse }) {
  const [newCourse, setNewCourse] = useState('');
  const [credit, setCredit] = useState(0);
  const [id, setId] = useState('');
  const [isUpdate, setIsUpdate] = useState(false);

  useEffect(() => {
    if (selectedCourse) {
      setId(selectedCourse.id);
      setNewCourse(selectedCourse.courseName);
      setCredit(selectedCourse.credit);
      setIsUpdate(true);
    } else {
      setId('');
      setNewCourse('');
      setCredit('');
      setIsUpdate(false);
    }
  }, [selectedCourse]);

  const handleCreate = () => {
    const requestData = {
      id: id,
      courseName: newCourse,
      credit: credit
    };

    const apiEndpoint = isUpdate ? `/course/update` : "/course/create";

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
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-course-dialog">
      <DialogTitle id="create-new-course-dialog-title">
        {isUpdate ? 'Chỉnh sửa thông tin' : 'Thêm môn học mới'}
      </DialogTitle>
      <DialogContent>
        <TextField
          margin="dense"
          label="Mã môn học"
          fullWidth
          value={id}
          onChange={(event) => setId(event.target.value)}
          disabled={isUpdate}
        />
        <div style={{ margin: '16px' }} />
        <TextField
          autoFocus
          margin="dense"
          label="Tên môn học"
          fullWidth
          value={newCourse}
          onChange={(event) => setNewCourse(event.target.value)}
        />
        <div style={{ margin: '16px' }} />
        <TextField
          margin="dense"
          label="Số tín chỉ"
          fullWidth
          value={credit}
          onChange={(event) => setCredit(event.target.value)}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">
          Hủy
        </Button>
        <Button
          onClick={handleCreate}
          color="primary"
          disabled={!newCourse}
        >
          {isUpdate ? 'Cập nhật' : 'Tạo mới'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
