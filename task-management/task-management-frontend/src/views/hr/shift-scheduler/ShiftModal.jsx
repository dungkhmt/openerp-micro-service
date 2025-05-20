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
import { UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx"; // Import new constant

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
                                     isUnassignedTemplateMode, // NEW PROP: True if creating/editing unassigned shift TEMPLATE
                                     unassignedShiftBeingEdited // NEW PROP: Details of unassigned shift being edited/assigned from
                                   }) {
  const [formState, setFormState] = useState(initialFormState);

  useEffect(() => {
    let currentInitialState = {
      ...initialFormState,
      userIds: Array.isArray(initialFormState.userIds) ? initialFormState.userIds : [],
      note: initialFormState.note || '',
      slots: initialFormState.slots !== undefined ? initialFormState.slots : 1, // Ensure slots is part of state
    };
    // If editing an unassigned shift, userIds should be empty to prompt selection for assignment,
    // unless isUnassignedTemplateMode is explicitly for template editing without assignment intent yet.
    if (unassignedShiftBeingEdited && !currentInitialState.userIds.includes(UNASSIGNED_SHIFT_USER_ID)) {
      // currentInitialState.userIds = []; // Clear userIds to make user select for assignment.
      // Or, keep it if modal is dual purpose.
      // For now, let ShiftScheduler handle initialUserIds logic.
    }

    setFormState(currentInitialState);
  }, [initialFormState, unassignedShiftBeingEdited]);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormState(prev => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (newDate) => {
    if (isValid(newDate)) {
      setFormState(prev => ({ ...prev, day: format(newDate, 'yyyy-MM-dd')}));
    } else {
      setFormState(prev => ({ ...prev, day: ''}));
    }
  };

  const handleTimeChange = (name, newTime) => {
    if (isValid(newTime)) {
      setFormState(prev => ({ ...prev, [name]: format(newTime, 'HH:mm')}));
    } else {
      setFormState(prev => ({ ...prev, [name]: ''}));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formState);
  };

  const effectiveIsUnassignedTemplateMode = isUnassignedTemplateMode || (formState.userIds && formState.userIds.includes(UNASSIGNED_SHIFT_USER_ID));


  // Determine modal title
  let modalTitle = isEditing ? "Chỉnh sửa ca làm việc" : "Thêm ca làm việc";
  if (effectiveIsUnassignedTemplateMode && !unassignedShiftBeingEdited) {
    modalTitle = "Thêm ca chờ gán";
  } else if (unassignedShiftBeingEdited) {
    modalTitle = `Gán ca / Sửa ca chờ gán (${unassignedShiftBeingEdited.slots} slot còn lại)`;
  }


  return (
    <Modal open={isOpen} onClose={onClose} aria-labelledby="shift-modal-title">
      <Box sx={modalStyle}>
        <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2}}>
          <Typography id="shift-modal-title" variant="h6" component="h2">{modalTitle}</Typography>
          <IconButton onClick={onClose} size="small"><CloseIcon /></IconButton>
        </Box>
        <Box component="form" onSubmit={handleSubmit} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <FormControl fullWidth margin="dense" required>
                <InputLabel id="user-select-label" sx={{fontSize:'0.9rem'}}>
                  {unassignedShiftBeingEdited ? "Gán cho nhân viên (để trống nếu chỉ sửa ca chờ)" : "Nhân viên"}
                </InputLabel>
                <Select
                  labelId="user-select-label"
                  id="userIds"
                  name="userIds"
                  multiple
                  value={formState.userIds || []}
                  label={unassignedShiftBeingEdited ? "Gán cho nhân viên (để trống nếu chỉ sửa ca chờ)" : "Nhân viên"}
                  onChange={handleFormChange}
                  size="small"
                  disabled={effectiveIsUnassignedTemplateMode && !unassignedShiftBeingEdited} // Disable if defining template unless it's for assignment
                  renderValue={(selected) => {
                    if (!selected || selected.length === 0) {
                      return <em>{unassignedShiftBeingEdited ? "Chọn nhân viên để gán" : (effectiveIsUnassignedTemplateMode ? "Ca chờ gán (Unassigned)" : "Chọn nhân viên")}</em>;
                    }
                    if (selected.includes(UNASSIGNED_SHIFT_USER_ID) && effectiveIsUnassignedTemplateMode) {
                      return "Ca chờ gán (Unassigned)";
                    }
                    return selected.map(id => users.find(u => u.id === id)?.name || id).join(', ');
                  }}
                >
                  {/* Option for Unassigned only if explicitly in that mode and not assigning from existing */}
                  {isUnassignedTemplateMode && !unassignedShiftBeingEdited && (
                    <MenuItem key={UNASSIGNED_SHIFT_USER_ID} value={UNASSIGNED_SHIFT_USER_ID}>
                      <Checkbox checked={(formState.userIds || []).indexOf(UNASSIGNED_SHIFT_USER_ID) > -1} size="small" />
                      <ListItemText primary="Tạoเป็น Ca chờ gán (Unassigned)" />
                    </MenuItem>
                  )}
                  {users.map(user => (
                    <MenuItem key={user.id} value={user.id}>
                      <Checkbox checked={(formState.userIds || []).indexOf(user.id) > -1} size="small" />
                      <ListItemText primary={user.name} />
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            { (effectiveIsUnassignedTemplateMode || unassignedShiftBeingEdited) && (
              <Grid item xs={12} sm={6}>
                <TextField
                  margin="dense"
                  fullWidth
                  id="slots"
                  label="Số lượng Slot"
                  name="slots"
                  type="number"
                  value={formState.slots || 1}
                  onChange={handleFormChange}
                  size="small"
                  InputProps={{ inputProps: { min: 1 } }}
                  helperText={unassignedShiftBeingEdited ? `Đang có: ${unassignedShiftBeingEdited.slots}` : "Số ca có thể được gán"}
                />
              </Grid>
            )}

            <Grid item xs={12} sm={ (effectiveIsUnassignedTemplateMode || unassignedShiftBeingEdited) ? 6 : 12}> {/* Adjust width if slots field is shown */}
              <DatePicker
                label="Ngày"
                value={formState.day ? parseISO(formState.day) : new Date()}
                onChange={handleDateChange}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true } }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker
                label="Giờ bắt đầu"
                value={formState.day && formState.startTime ? parseISO(`${formState.day}T${formState.startTime}`) : null}
                onChange={(newValue) => handleTimeChange('startTime', newValue)}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true } }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker
                label="Giờ kết thúc"
                value={formState.day && formState.endTime ? parseISO(`${formState.day}T${formState.endTime}`) : null}
                onChange={(newValue) => handleTimeChange('endTime', newValue)}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true } }}
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
