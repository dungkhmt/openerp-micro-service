import React, { useState } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";
import { useGroupData } from "services/useGroupData";

const SimpleCreateGroupDialog = ({ open, handleClose }) => {
  const [groupName, setGroupName] = useState("");
  const { createGroupMutation, isCreatingGroup } = useGroupData();

  const handleSubmit = async () => {
    try {
      await createGroupMutation(groupName);  // Send just the string value
      handleClose();
      setGroupName("");
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="xs" fullWidth>
      <DialogTitle>Thêm nhóm mới</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Tên nhóm"
          fullWidth
          value={groupName}
          onChange={(e) => setGroupName(e.target.value)}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Hủy</Button>
        <Button 
          onClick={handleSubmit} 
          disabled={!groupName.trim() || isCreatingGroup}
        >
          Tạo mới
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default SimpleCreateGroupDialog;
