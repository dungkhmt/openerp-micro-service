// ==============
// ShiftModal.jsx
// ==============
import React, {useEffect, useState} from "react";
import {format, isValid, parseISO} from "date-fns";
import {
  Box, Button,
  Checkbox,
  Chip, // Import Chip
  FormControl,
  Grid,
  IconButton,
  InputLabel, ListItemText,
  MenuItem,
  Modal,
  Select, TextField,
  Typography
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close.js";
import {DatePicker, TimePicker} from "@mui/x-date-pickers";
import { FRONTEND_UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx";

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: { xs: '90%', sm: 500 },
  bgcolor: 'background.paper',
  border: '1px solid #ccc',
  boxShadow: 24,
  p: 3,
  borderRadius: 1,
};

export default function ShiftModal({
                                     isOpen,
                                     onClose,
                                     onSave,
                                     users,
                                     initialFormState,
                                     isEditing,
                                     isUnassignedContext,
                                     unassignedShiftBeingEdited
                                   }) {
  const [formState, setFormState] = useState(initialFormState);

  useEffect(() => {
    let defaultUserIds = Array.isArray(initialFormState.userIds) ? initialFormState.userIds : [];
    if (isUnassignedContext && !isEditing) {
      defaultUserIds = [];
    }
    setFormState({
      ...initialFormState,
      userIds: defaultUserIds,
      note: initialFormState.note || '',
      slots: initialFormState.slots !== undefined ? initialFormState.slots : 1,
    });
  }, [initialFormState, isUnassignedContext, isEditing]);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    if (name === "userIds" && typeof value === 'string') {
      setFormState(prev => ({...prev, [name]: value.split(',')}));
    } else {
      setFormState(prev => ({ ...prev, [name]: value }));
    }
  };

  // Handler for deleting a user chip
  const handleUserChipDelete = (userIdToDelete) => {
    setFormState(prev => ({
      ...prev,
      userIds: prev.userIds.filter(id => id !== userIdToDelete)
    }));
  };

  const handleDateChange = (newDate) => {
    setFormState(prev => ({ ...prev, day: isValid(newDate) ? format(newDate, 'yyyy-MM-dd') : '' }));
  };

  const handleTimeChange = (name, newTime) => {
    setFormState(prev => ({ ...prev, [name]: isValid(newTime) ? format(newTime, 'HH:mm') : '' }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const submissionData = {
      ...formState,
      _initiatedAsNewUnassignedContext: isUnassignedContext && !isEditing,
    };
    onSave(submissionData);
  };

  let modalTitle = "Thêm ca làm việc";
  if (isUnassignedContext && !isEditing) {
    modalTitle = "Thêm ca mới (tùy chọn gán)";
  } else if (unassignedShiftBeingEdited) {
    modalTitle = `Gán ca / Sửa ca chờ gán (${unassignedShiftBeingEdited.slots || 0} slot còn lại)`;
  } else if (isEditing) {
    modalTitle = "Chỉnh sửa ca làm việc";
  }

  const getDatePickerValue = () => {
    if (formState.day) {
      const parsedDate = parseISO(formState.day);
      return isValid(parsedDate) ? parsedDate : null;
    }
    return null;
  };

  const getTimePickerValue = (timeString) => {
    if (formState.day && timeString) {
      const parsedDateTime = parseISO(`${formState.day}T${timeString}`);
      return isValid(parsedDateTime) ? parsedDateTime : null;
    }
    return null;
  };

  const formKey = isEditing
    ? (unassignedShiftBeingEdited ? `edit-unassigned-${unassignedShiftBeingEdited.id}` : `edit-user-${(initialFormState.userIds && initialFormState.userIds[0]) || 'new'}`)
    : (isUnassignedContext ? 'add-new-unassigned-context' : 'add-new-user');

  const showSlotsField = isUnassignedContext;

  return (
    <Modal open={isOpen} onClose={onClose} aria-labelledby="shift-modal-title">
      <Box sx={modalStyle}>
        <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2}}>
          <Typography id="shift-modal-title" variant="h6" component="h2">{modalTitle}</Typography>
          <IconButton onClick={onClose} size="small"><CloseIcon /></IconButton>
        </Box>
        <Box component="form" key={formKey} onSubmit={handleSubmit} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <FormControl fullWidth margin="dense" required={!showSlotsField || (formState.userIds && formState.userIds.length > 0)}>
                <InputLabel id="user-select-label-chip" sx={{fontSize:'0.9rem'}}>
                  Nhân viên {showSlotsField ? "(Để trống nếu chỉ tạo ca chờ)" : ""}
                </InputLabel>
                <Select
                  labelId="user-select-label-chip"
                  id="userIds"
                  name="userIds"
                  multiple
                  value={formState.userIds || []}
                  label={`Nhân viên ${showSlotsField ? "(Để trống nếu chỉ tạo ca chờ)" : ""}`}
                  onChange={handleFormChange}
                  size="small"
                  renderValue={(selectedUserIds) => (
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                      {selectedUserIds.map((userId) => {
                        const user = users.find(u => u.id === userId);
                        return (
                          <Chip
                            key={userId}
                            label={user ? user.name : userId}
                            size="small"
                            onDelete={() => handleUserChipDelete(userId)} // ADDED onDelete handler
                            onMouseDown={(event) => { // Prevent modal from closing on chip delete click
                              event.stopPropagation();
                            }}
                          />
                        );
                      })}
                      {(!selectedUserIds || selectedUserIds.length === 0) &&
                        <em>{showSlotsField ? "Gán cho NV hoặc để trống" : "Chọn nhân viên"}</em>
                      }
                    </Box>
                  )}
                  MenuProps={{
                    PaperProps: {
                      style: {
                        maxHeight: 224,
                      },
                    },
                  }}
                >
                  {users.map(user => (
                    <MenuItem key={user.id} value={user.id}>
                      <Checkbox checked={(formState.userIds || []).indexOf(user.id) > -1} size="small" />
                      <ListItemText primary={user.name} />
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            {showSlotsField && (
              <Grid item xs={12} sm={6}>
                <TextField
                  margin="dense"
                  fullWidth
                  id="slots"
                  label="Tổng Slot ban đầu"
                  name="slots"
                  type="number"
                  value={formState.slots || '1'}
                  onChange={handleFormChange}
                  size="small"
                  InputLabelProps={{ shrink: true }}
                  InputProps={{ inputProps: { min: 1 } }}
                  helperText={unassignedShiftBeingEdited ? `Đang sửa ca chờ (${unassignedShiftBeingEdited.slots} slot)` : "Nếu gán NV, slot sẽ giảm"}
                />
              </Grid>
            )}

            <Grid item xs={12} sm={showSlotsField ? 6 : 12}>
              <DatePicker
                label="Ngày"
                value={getDatePickerValue()}
                onChange={handleDateChange}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true, InputLabelProps: { shrink: true } } }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker
                label="Giờ bắt đầu"
                value={getTimePickerValue(formState.startTime)}
                onChange={(newValue) => handleTimeChange('startTime', newValue)}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true, InputLabelProps: { shrink: true } } }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker
                label="Giờ kết thúc"
                value={getTimePickerValue(formState.endTime)}
                onChange={(newValue) => handleTimeChange('endTime', newValue)}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true, InputLabelProps: { shrink: true } } }}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                margin="dense"
                fullWidth
                id="note"
                label="Ghi chú"
                name="note"
                value={formState.note || ''}
                onChange={handleFormChange}
                size="small"
                multiline
                rows={3}
                InputLabelProps={{ shrink: true }}
                placeholder="Thêm ghi chú (không bắt buộc)..."
              />
            </Grid>
          </Grid>
          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
            <Button onClick={onClose} variant="outlined" color="inherit">Hủy</Button>
            <Button type="submit" variant="contained" color="primary">Lưu ca</Button>
          </Box>
        </Box>
      </Box>
    </Modal>
  );
}
