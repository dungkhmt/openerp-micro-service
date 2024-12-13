import React, { useState } from "react";
import { useGroupData } from 'services/useGroupData';
import { useClassroomData } from 'services/useClassroomData';
import { Autocomplete, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";

const CreateNewGroupScreen = React.memo(({ open, handleClose, selectedGroup }) => {
  const [formData, setFormData] = useState({
    id: selectedGroup?.id || "",
    groupName: selectedGroup?.groupName || "",
    priorityBuilding: selectedGroup?.priorityBuilding?.split(",") || []
  });

  const { buildings, isBuildingsLoading } = useClassroomData();
  const { createGroup, updateGroup, isCreating, isUpdating } = useGroupData();

  const handleSubmit = async () => {
    const payload = {
      ...formData,
      priorityBuilding: formData.priorityBuilding.join(",")
    };

    console.log(payload);

    try {
      if (selectedGroup) {
        await updateGroup(payload);
      } else {
        await createGroup(payload);
      }
      handleClose();
    } catch (error) {
      // Error handling is managed by mutations
    }
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>
        {selectedGroup ? "Chỉnh sửa thông tin" : "Thêm nhóm mới"}
      </DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Tên nhóm"
          fullWidth
          value={formData.groupName}
          onChange={(e) => setFormData({...formData, groupName: e.target.value})}
        />
        <Autocomplete
          multiple
          options={buildings}
          loading={isBuildingsLoading}
          value={formData.priorityBuilding}
          onChange={(_, newValue) => setFormData({...formData, priorityBuilding: newValue})}
          renderInput={(params) => (
            <TextField {...params} label="Tòa nhà ưu tiên" margin="dense" />
          )}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Hủy</Button>
        <Button
          onClick={handleSubmit}
          disabled={!formData.groupName || !formData.priorityBuilding.length || isCreating || isUpdating}
        >
          {selectedGroup ? "Cập nhật" : "Tạo mới"}
        </Button>
      </DialogActions>
    </Dialog>
  );
});

export default CreateNewGroupScreen;
