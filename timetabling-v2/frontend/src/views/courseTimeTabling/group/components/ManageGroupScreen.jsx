import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  TextField,
  Collapse,
  IconButton,
  Box,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useGroupData } from 'services/useGroupData';

export default function ManageGroupScreen({ open, handleClose, initialGroup }) {
  const [selectedGroup, setSelectedGroup] = useState('');
  const [showRenameInput, setShowRenameInput] = useState(false);
  const [newName, setNewName] = useState('');
  const { allGroups, isLoadingGroups, updateGroupName, isUpdatingName } = useGroupData();

  useEffect(() => {
    if (open) {
      setShowRenameInput(false);
      setNewName('');
    }
  }, [open]);

  useEffect(() => {
    if (open && initialGroup) {
      setSelectedGroup(initialGroup.id);
      setNewName(initialGroup.groupName);
      setShowRenameInput(true);
    }
  }, [open, initialGroup]);

  const handleRename = async () => {
    if (!showRenameInput) {
      setShowRenameInput(true);
    } else {
      try {
        await updateGroupName({
          id: selectedGroup,
          groupName: newName
        });
        setShowRenameInput(false);
        setNewName('');
        handleClose();
      } catch (error) {
        console.error("Error updating group name:", error);
      }
    }
  };

  const handleBack = () => {
    setShowRenameInput(false);
    setNewName('');
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="xs" fullWidth>
      <DialogTitle>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          {showRenameInput && (
            <IconButton 
              edge="start" 
              onClick={handleBack}
              sx={{ mr: 1 }}
            >
              <ArrowBackIcon />
            </IconButton>
          )}
          {showRenameInput ? "Đổi tên nhóm học" : "Chỉnh sửa nhóm học"}
        </Box>
      </DialogTitle>
      <DialogContent>
        <FormControl fullWidth margin="normal">
          <InputLabel>Chọn nhóm học</InputLabel>
          {isLoadingGroups ? (
            <CircularProgress size={20} />
          ) : (
            <Select
              value={selectedGroup}
              onChange={(e) => setSelectedGroup(e.target.value)}
              label="Chọn nhóm học"
            >
              {allGroups?.map((group) => (
                <MenuItem key={group.id} value={group.id}>
                  {group.groupName}
                </MenuItem>
              ))}
            </Select>
          )}
        </FormControl>

        <Collapse in={showRenameInput} timeout="auto">
          <TextField
            fullWidth
            margin="normal"
            label="Tên mới"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            autoFocus
          />
        </Collapse>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Hủy
        </Button>
        <Button 
          onClick={handleRename} 
          color="primary"
          disabled={!selectedGroup || (showRenameInput && !newName) || isUpdatingName}
        >
          {showRenameInput ? 'Lưu tên mới' : 'Đổi tên'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
