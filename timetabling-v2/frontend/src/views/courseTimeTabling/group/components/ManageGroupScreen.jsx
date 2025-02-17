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
  DialogContentText,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useGroupData } from 'services/useGroupData';

export default function ManageGroupScreen({ open, handleClose }) {
  const [selectedGroup, setSelectedGroup] = useState('');
  const [showRenameInput, setShowRenameInput] = useState(false);
  const [newName, setNewName] = useState('');
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);
  const [selectedGroupName, setSelectedGroupName] = useState('');
  const { allGroups, isLoadingGroups, refetchGroups, updateGroupName, isUpdatingName, deleteGroupById, isDeletingById } = useGroupData();

  useEffect(() => {
    if (open) {
      refetchGroups();
      setShowRenameInput(false);
      setNewName('');
    }
  }, [open]);

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

  const handleDeleteClick = () => {
    const group = allGroups?.find(g => g.id === selectedGroup);
    setSelectedGroupName(group?.groupName || '');
    setShowConfirmDelete(true);
  };

  const handleConfirmDelete = async () => {
    try {
      await deleteGroupById(selectedGroup);
      setShowConfirmDelete(false);
      handleClose();
    } catch (error) {
      console.error("Error deleting group:", error);
    }
  };

  const handleBack = () => {
    setShowRenameInput(false);
    setNewName('');
  };

  return (
    <>
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
          {!showRenameInput && (
            <Button 
              onClick={handleDeleteClick}
              color="error"
              disabled={!selectedGroup || isDeletingById}
            >
              Xóa
            </Button>
          )}
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={showConfirmDelete}
        onClose={() => setShowConfirmDelete(false)}
        maxWidth="xs"
        fullWidth
      >
        <DialogTitle>Xác nhận xóa</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Bạn có chắc chắn muốn xóa nhóm "{selectedGroupName}"?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowConfirmDelete(false)}>
            Hủy
          </Button>
          <Button 
            onClick={handleConfirmDelete} 
            color="error"
            disabled={isDeletingById}
          >
            Xóa
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
