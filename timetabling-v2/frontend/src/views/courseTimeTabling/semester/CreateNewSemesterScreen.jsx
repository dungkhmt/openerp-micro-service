import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";
import { useState, useEffect } from "react";
import { useSemesterData } from "services/useSemesterData";

export default function CreateNewSemester({
  open,
  handleClose,
  selectedSemester,
}) {
  const { createSemester, updateSemester, isCreating, isUpdating } = useSemesterData();
  const [newSemester, setNewSemester] = useState("");
  const [description, setDescription] = useState("");
  const [id, setId] = useState("");
  const [isUpdate, setIsUpdate] = useState(false);

  useEffect(() => {
    if (selectedSemester) {
      setNewSemester(selectedSemester.semester);
      setDescription(selectedSemester.description);
      setId(selectedSemester.id);
      setIsUpdate(true);
    } else {
      setNewSemester("");
      setDescription("");
      setId("");
      setIsUpdate(false);
    }
  }, [selectedSemester]);

  const handleCreate = async () => {
    const requestData = {
      id: id,
      semester: newSemester,
      description: description,
    };

    try {
      if (isUpdate) {
        await updateSemester(requestData);
      } else {
        await createSemester(requestData);
      }
      handleClose();
    } catch (error) {
      // Errors are handled by the mutation callbacks
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      aria-labelledby="create-new-semester-dialog"
    >
      <DialogTitle id="create-new-semester-dialog-title">
        {isUpdate ? "Chỉnh sửa thông tin" : "Thêm học kỳ mới"}
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
        <div style={{ margin: "16px" }} />
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
          disabled={!newSemester || isCreating || isUpdating}
        >
          {isUpdate ? (isUpdating ? 'Đang cập nhật...' : 'Cập nhật') : 
                     (isCreating ? 'Đang tạo...' : 'Tạo mới')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
