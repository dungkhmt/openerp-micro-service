// ==============
// ShiftModal.jsx
// ==============
import React, {useEffect, useState, useMemo} from "react"; // Added useMemo
import {format, isValid, parseISO} from "date-fns";
import {
  Box, Button,
  Checkbox,
  Chip,
  FormControl,
  Grid,
  IconButton,
  InputAdornment, // Added
  InputLabel, ListItemText,
  ListSubheader, // Added
  MenuItem,
  Modal,
  Select, TextField,
  Typography
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close.js";
import SearchIcon from '@mui/icons-material/Search'; // Added
import {DatePicker, TimePicker} from "@mui/x-date-pickers";
// FRONTEND_UNASSIGNED_SHIFT_USER_ID is not directly used in this file after changes
// but ensure it's available if any logic relies on it implicitly from props.

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
                                     users, // Danh sách tất cả nhân viên
                                     initialFormState,
                                     isEditing,
                                     isUnassignedContext,
                                     unassignedShiftBeingEdited,
                                     isSaving // Thêm prop này để disable nút khi đang lưu
                                   }) {
  const [formState, setFormState] = useState(initialFormState);
  const [userSearchText, setUserSearchText] = useState(''); // State cho tìm kiếm nhân viên

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
    // Reset user search text when modal opens with new initialFormState
    if (isOpen) {
      setUserSearchText('');
    }
  }, [initialFormState, isUnassignedContext, isEditing, isOpen]);


  const handleFormChange = (e) => {
    const { name, value } = e.target;
    if (name === "userIds" && typeof value === 'string') {
      // This case might not be hit if using standard multi-select with checkboxes
      setFormState(prev => ({...prev, [name]: value.split(',')}));
    } else {
      setFormState(prev => ({ ...prev, [name]: value }));
    }
  };

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
    if (isSaving) return; // Không cho submit nếu đang lưu
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

  const formKey = useMemo(() => { // Sử dụng useMemo để key chỉ thay đổi khi cần
    return isEditing
      ? (unassignedShiftBeingEdited ? `edit-unassigned-${unassignedShiftBeingEdited.id}` : `edit-user-${(initialFormState.userIds && initialFormState.userIds[0]) || 'new'}-${initialFormState.day}`)
      : (isUnassignedContext ? 'add-new-unassigned-context' : 'add-new-user') + `-${initialFormState.day}`;
  }, [isEditing, unassignedShiftBeingEdited, initialFormState.userIds, initialFormState.day, isUnassignedContext]);


  const showSlotsField = isUnassignedContext;

  // Lọc danh sách nhân viên dựa trên userSearchText
  const filteredUsers = useMemo(() => {
    if (!userSearchText) {
      return users;
    }
    return users.filter(user =>
      user.name.toLowerCase().includes(userSearchText.toLowerCase())
    );
  }, [users, userSearchText]);

  const handleModalClose = () => {
    if (isSaving) return; // Không cho đóng modal nếu đang lưu
    setUserSearchText(''); // Reset search text khi modal đóng
    onClose();
  }

  return (
    <Modal open={isOpen} onClose={handleModalClose} aria-labelledby="shift-modal-title">
      <Box sx={modalStyle}>
        <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2}}>
          <Typography id="shift-modal-title" variant="h6" component="h2">{modalTitle}</Typography>
          <IconButton onClick={handleModalClose} size="small" disabled={isSaving}><CloseIcon /></IconButton>
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
                  onClose={() => setUserSearchText('')} // Reset search khi Select đóng
                  size="small"
                  disabled={isSaving}
                  renderValue={(selectedUserIds) => (
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                      {selectedUserIds.map((userId) => {
                        const user = users.find(u => u.id === userId);
                        return (
                          <Chip
                            key={userId}
                            label={user ? user.name : userId}
                            size="small"
                            onDelete={isSaving ? undefined : () => handleUserChipDelete(userId)}
                            onMouseDown={(event) => { event.stopPropagation(); }}
                            disabled={isSaving}
                          />
                        );
                      })}
                      {(!selectedUserIds || selectedUserIds.length === 0) &&
                        <em>{showSlotsField ? "Gán cho NV hoặc để trống" : "Chọn nhân viên"}</em>
                      }
                    </Box>
                  )}
                  MenuProps={{
                    PaperProps: { style: { maxHeight: 300 } }, // Tăng maxHeight cho dropdown
                    autoFocus: false // Ngăn autoFocus vào TextField khi mở Select
                  }}
                >
                  {/* Trường tìm kiếm */}
                  <ListSubheader sx={{bgcolor: 'background.paper', zIndex:1}}>
                    <TextField
                      size="small"
                      autoFocus // Tự động focus vào ô tìm kiếm khi mở dropdown
                      placeholder="Tìm nhân viên..."
                      fullWidth
                      value={userSearchText}
                      onChange={(e) => setUserSearchText(e.target.value)}
                      onKeyDown={(e) => e.stopPropagation()} // Ngăn các sự kiện keydown của Select
                      onClick={(e) => e.stopPropagation()} // Ngăn Select đóng khi click vào search
                      sx={{my: 1, px: 1}}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <SearchIcon color="action" />
                          </InputAdornment>
                        ),
                      }}
                      disabled={isSaving}
                    />
                  </ListSubheader>

                  {/* Danh sách nhân viên đã lọc */}
                  {filteredUsers.map(user => (
                    <MenuItem key={user.id} value={user.id} disabled={isSaving}>
                      <Checkbox checked={(formState.userIds || []).indexOf(user.id) > -1} size="small" disabled={isSaving} />
                      <ListItemText primary={user.name} />
                    </MenuItem>
                  ))}
                  {filteredUsers.length === 0 && userSearchText && (
                    <MenuItem disabled sx={{ justifyContent: 'center' }}>
                      <Typography variant="caption" color="textSecondary">
                        Không tìm thấy nhân viên.
                      </Typography>
                    </MenuItem>
                  )}
                </Select>
              </FormControl>
            </Grid>

            {showSlotsField && (
              <Grid item xs={12} sm={6}>
                <TextField
                  margin="dense" fullWidth id="slots" label="Tổng Slot ban đầu" name="slots"
                  type="number" value={formState.slots === undefined ? '1' : formState.slots} // Sửa giá trị mặc định
                  onChange={handleFormChange} size="small"
                  InputLabelProps={{ shrink: true }} InputProps={{ inputProps: { min: 1 } }}
                  helperText={unassignedShiftBeingEdited ? `Đang sửa ca chờ (${unassignedShiftBeingEdited.slots} slot)` : "Nếu gán NV, slot sẽ giảm"}
                  disabled={isSaving}
                />
              </Grid>
            )}

            <Grid item xs={12} sm={showSlotsField ? 6 : 12}>
              <DatePicker
                label="Ngày" value={getDatePickerValue()} onChange={handleDateChange}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true, InputLabelProps: { shrink: true } } }}
                disabled={isSaving}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker
                label="Giờ bắt đầu" value={getTimePickerValue(formState.startTime)}
                onChange={(newValue) => handleTimeChange('startTime', newValue)}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true, InputLabelProps: { shrink: true } } }}
                disabled={isSaving}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker
                label="Giờ kết thúc" value={getTimePickerValue(formState.endTime)}
                onChange={(newValue) => handleTimeChange('endTime', newValue)}
                slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true, InputLabelProps: { shrink: true } } }}
                disabled={isSaving}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                margin="dense" fullWidth id="note" label="Ghi chú" name="note"
                value={formState.note || ''} onChange={handleFormChange} size="small"
                multiline rows={3} InputLabelProps={{ shrink: true }}
                placeholder="Thêm ghi chú (không bắt buộc)..."
                disabled={isSaving}
              />
            </Grid>
          </Grid>
          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
            <Button onClick={handleModalClose} variant="outlined" color="inherit" disabled={isSaving}>Hủy</Button>
            <Button type="submit" variant="contained" color="primary" disabled={isSaving}>
              {isSaving ? "Đang lưu..." : "Lưu ca"}
            </Button>
          </Box>
        </Box>
      </Box>
    </Modal>
  );
}
