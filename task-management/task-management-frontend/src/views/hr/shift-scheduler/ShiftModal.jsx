// ==============
// ShiftModal.jsx
// ==============
import React, {useEffect, useState} from "react";
import {format, isValid, parseISO} from "date-fns";
import {
  Box, Button,
  Checkbox,
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
import { UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx"; // Assuming this is correctly defined

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
                                     isEditing, // True if currentEditingShift is set
                                     // This prop signals if the modal was opened with an "unassigned shift" context
                                     // (either creating a new one or editing/assigning an existing one)
                                     isUnassignedContext,
                                     unassignedShiftBeingEdited // The unassigned shift object if editing/assigning one
                                   }) {
  const [formState, setFormState] = useState(initialFormState);

  useEffect(() => {
    let defaultUserIds = Array.isArray(initialFormState.userIds) ? initialFormState.userIds : [];

    // If opening to "Add New Unassigned Shift (and optionally assign employees)",
    // userIds should start empty to allow selection.
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
    setFormState(prev => ({ ...prev, [name]: value }));
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
      // Flag to help handleSaveShift understand the original intent
      _initiatedAsNewUnassignedContext: isUnassignedContext && !isEditing,
    };
    onSave(submissionData);
  };

  let modalTitle = "Thêm ca làm việc"; // Default for adding a regular user shift
  if (isUnassignedContext && !isEditing) {
    modalTitle = "Thêm ca mới (tùy chọn gán)"; // Title for new unassigned/assignable shift
  } else if (unassignedShiftBeingEdited) {
    // Syntax error was here, ensuring correct template literal:
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

  const showSlotsField = isUnassignedContext; // Show slots if dealing with unassigned context at all

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
              <FormControl fullWidth margin="dense" required={!showSlotsField || (formState.userIds && formState.userIds.length > 0)}> {/* Required if not unassigned or if users are selected */}
                <InputLabel id="user-select-label" sx={{fontSize:'0.9rem'}}>
                  Nhân viên {showSlotsField ? "(Để trống nếu chỉ tạo ca chờ)" : ""}
                </InputLabel>
                <Select
                  labelId="user-select-label"
                  id="userIds"
                  name="userIds"
                  multiple
                  value={formState.userIds || []}
                  label={`Nhân viên ${showSlotsField ? "(Để trống nếu chỉ tạo ca chờ)" : ""}`}
                  onChange={handleFormChange}
                  size="small"
                  // Dropdown is now always enabled if modal is open
                  renderValue={(selected) => {
                    if (!selected || selected.length === 0) {
                      return <em>{showSlotsField ? "Gán cho NV hoặc để trống" : "Chọn nhân viên"}</em>;
                    }
                    return selected.map(id => users.find(u => u.id === id)?.name || id).join(', ');
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
