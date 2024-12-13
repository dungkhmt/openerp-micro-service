import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import { useState, useEffect } from 'react';
import { useClassroomData } from 'services/useClassroomData';

export default function CreateNewClassroomScreen({ open, handleClose, selectedClassroom }) {
  const { createClassroom, updateClassroom, refetchClassrooms } = useClassroomData();
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

  const handleCreate = async () => {
    const requestData = {
      id: id,
      classroom: newClassroom,
      building: building,
      quantityMax: quantityMax,
      description: description
    };

    try {
      if (isUpdate) {
        await updateClassroom(requestData);
      } else {
        await createClassroom(requestData);
      }
      await refetchClassrooms();
      handleClose();
    } catch (error) {
      // Error handling is managed by the hook
    }
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
