// ShiftModal.jsx
import React, {useEffect, useMemo, useState} from "react";
import {format, isValid, parseISO} from "date-fns";
import {
  Box,
  Button,
  Checkbox,
  Chip,
  FormControl,
  Grid, // Vẫn giữ Grid cho layout tổng thể nếu cần
  IconButton,
  InputAdornment,
  InputLabel,
  ListItemText,
  ListSubheader,
  MenuItem,
  Select,
  TextField,
  Typography,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack, // Đảm bảo Stack được import
  CircularProgress
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close.js";
import SearchIcon from '@mui/icons-material/Search';
import {DatePicker, TimePicker, LocalizationProvider} from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { vi } from 'date-fns/locale';
import toast from "react-hot-toast";

export default function ShiftModal({
                                     isOpen,
                                     onClose,
                                     onSave,
                                     users,
                                     initialFormState,
                                     isEditing,
                                     isUnassignedContext,
                                     unassignedShiftBeingEdited,
                                     isSaving,
                                     titleProps
                                   }) {
  const [formState, setFormState] = useState(initialFormState);
  const [userSearchText, setUserSearchText] = useState('');

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
    if (isOpen) {
      setUserSearchText('');
    }
  }, [initialFormState, isUnassignedContext, isEditing, isOpen]);


  const handleFormChange = (e) => {
    const { name, value } = e.target;
    if (name === "userIds" && typeof value === 'string') {
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
    setFormState(prev => ({ ...prev, day: isValid(newDate) ? format(newDate, 'yyyy-MM-dd') : null }));
  };

  const handleTimeChange = (name, newTime) => {
    setFormState(prev => ({ ...prev, [name]: isValid(newTime) ? format(newTime, 'HH:mm') : null }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isSaving) return;
    if (!formState.day) {
      toast.error("Vui lòng chọn ngày cho ca làm việc.");
      return;
    }
    if (!formState.startTime || !formState.endTime) {
      toast.error("Vui lòng chọn giờ bắt đầu và kết thúc.");
      return;
    }
    if (!showSlotsField && (!formState.userIds || formState.userIds.length === 0)) {
      toast.error("Vui lòng chọn ít nhất một nhân viên.");
      return;
    }
    if (showSlotsField && formState.slots < (formState.userIds?.length || 0) ) {
      toast.error("Số nhân viên được gán không thể vượt quá tổng số slot.");
      return;
    }

    const submissionData = {
      ...formState,
      _initiatedAsNewUnassignedContext: isUnassignedContext && !isEditing,
    };
    onSave(submissionData);
  };

  let modalTitleText = "Thêm ca làm việc";
  if (isUnassignedContext && !isEditing) {
    modalTitleText = "Thêm ca mới (tùy chọn gán)";
  } else if (unassignedShiftBeingEdited) {
    modalTitleText = `Gán / Sửa ca chờ (${unassignedShiftBeingEdited.slots_available || 0} slot trống)`;
  } else if (isEditing) {
    modalTitleText = "Chỉnh sửa ca làm việc";
  }

  const getDatePickerValue = () => {
    if (formState.day) {
      const parsedDate = parseISO(formState.day);
      return isValid(parsedDate) ? parsedDate : null;
    }
    return null;
  };

  const getTimePickerValue = (timeString) => {
    if (timeString && typeof timeString === 'string') {
      const [hours, minutes] = timeString.split(':');
      if (!isNaN(parseInt(hours)) && !isNaN(parseInt(minutes))) {
        const date = new Date();
        date.setHours(parseInt(hours), parseInt(minutes), 0, 0);
        return date;
      }
    }
    return null;
  };

  const formKey = useMemo(() => {
    return isEditing
      ? (unassignedShiftBeingEdited ? `edit-unassigned-${unassignedShiftBeingEdited.id}` : `edit-user-${(initialFormState.userIds && initialFormState.userIds[0]) || 'new'}-${initialFormState.day}`)
      : (isUnassignedContext ? 'add-new-unassigned-context' : 'add-new-user') + `-${initialFormState.day || 'newday'}`;
  }, [isEditing, unassignedShiftBeingEdited, initialFormState.userIds, initialFormState.day, isUnassignedContext]);

  const showSlotsField = isUnassignedContext || unassignedShiftBeingEdited;

  const filteredUsers = useMemo(() => {
    if (!userSearchText) {
      return users;
    }
    return users.filter(user =>
      user.name.toLowerCase().includes(userSearchText.toLowerCase())
    );
  }, [users, userSearchText]);

  const handleModalClose = () => {
    if (isSaving) return;
    setUserSearchText('');
    onClose();
  }

  return (
    <Dialog open={isOpen} onClose={handleModalClose} fullWidth maxWidth="sm" PaperProps={{component: 'form', onSubmit: handleSubmit}}>
      <DialogTitle sx={{...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 1.5}}>
        <Typography variant="h6" component="div" sx={{fontWeight: 600, fontSize: titleProps?.sx?.fontSize || '1.2rem'}}>
          {modalTitleText}
        </Typography>
        <IconButton aria-label="đóng" onClick={handleModalClose} sx={{p:0.5}} disabled={isSaving}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{pt: '12px !important'}}>
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
          <Stack spacing={2} sx={{mt:1}} key={formKey}>
            <FormControl fullWidth size="small" required={!showSlotsField || (formState.userIds && formState.userIds.length > 0)}>
              <InputLabel id="user-select-label-chip">
                Nhân viên {showSlotsField ? "(Để trống nếu chỉ tạo ca chờ)" : "(*)"}
              </InputLabel>
              <Select
                labelId="user-select-label-chip"
                id="userIds"
                name="userIds"
                multiple
                value={formState.userIds || []}
                label={`Nhân viên ${showSlotsField ? "(Để trống nếu chỉ tạo ca chờ)" : "(*)"}`}
                onChange={handleFormChange}
                onClose={() => setUserSearchText('')}
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
                          sx={{height: '24px'}}
                        />
                      );
                    })}
                    {(!selectedUserIds || selectedUserIds.length === 0) &&
                      <Typography variant="body2" sx={{ color: 'text.secondary', fontStyle: 'italic', lineHeight: '24px' }}>
                        {showSlotsField ? "Gán cho NV hoặc để trống" : "Chọn nhân viên"}
                      </Typography>
                    }
                  </Box>
                )}
                MenuProps={{ PaperProps: { style: { maxHeight: 300 } }, autoFocus: false }}
              >
                <ListSubheader sx={{bgcolor: 'background.paper', zIndex:1, p:0.5, pt:1}}>
                  <TextField
                    size="small"
                    autoFocus
                    placeholder="Tìm nhân viên..."
                    fullWidth
                    value={userSearchText}
                    onChange={(e) => setUserSearchText(e.target.value)}
                    onKeyDown={(e) => e.stopPropagation()}
                    onClick={(e) => e.stopPropagation()}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <SearchIcon color="action" sx={{fontSize: '1.2rem'}}/>
                        </InputAdornment>
                      ),
                    }}
                    disabled={isSaving}
                    sx={{px:1}}
                  />
                </ListSubheader>
                {filteredUsers.map(user => (
                  <MenuItem key={user.id} value={user.id} disabled={isSaving}>
                    <Checkbox checked={(formState.userIds || []).indexOf(user.id) > -1} size="small" disabled={isSaving} />
                    <ListItemText primary={user.name} primaryTypographyProps={{variant: 'body2'}} />
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

            {showSlotsField && (
              <TextField
                fullWidth label="Tổng Slot ban đầu (*)" name="slots"
                type="number" value={formState.slots === undefined ? '1' : formState.slots}
                onChange={handleFormChange} size="small"
                InputLabelProps={{ shrink: true }} InputProps={{ inputProps: { min: 1 } }}
                helperText={unassignedShiftBeingEdited ? `Ca chờ này còn ${unassignedShiftBeingEdited.slots_available || 0} slot trống.` : "Nếu đã gán NV, số slot sẽ tự động điều chỉnh."}
                disabled={isSaving}
              />
            )}
            <DatePicker
              label="Ngày (*)"
              value={getDatePickerValue()}
              onChange={handleDateChange}
              slotProps={{ textField: { fullWidth: true, size:"small", required:true, InputLabelProps: { shrink: true } } }}
              disabled={isSaving}
              format="dd/MM/yyyy"
            />
            {/* SỬA Ở ĐÂY: Sử dụng Stack cho TimePickers */}
            <Stack direction={{xs: 'column', sm: 'row'}} spacing={2} sx={{width: '100%'}}>
              <TimePicker
                label="Giờ bắt đầu (*)"
                value={getTimePickerValue(formState.startTime)}
                onChange={(newValue) => handleTimeChange('startTime', newValue)}
                slotProps={{ textField: { fullWidth: true, size:"small", required:true, InputLabelProps: { shrink: true } } }}
                ampm={false}
                disabled={isSaving}
                sx={{ flex: 1 }} // Cho phép TimePicker co giãn đều trong Stack
              />
              <TimePicker
                label="Giờ kết thúc (*)"
                value={getTimePickerValue(formState.endTime)}
                onChange={(newValue) => handleTimeChange('endTime', newValue)}
                slotProps={{ textField: { fullWidth: true, size:"small", required:true, InputLabelProps: { shrink: true } } }}
                ampm={false}
                disabled={isSaving}
                sx={{ flex: 1 }} // Cho phép TimePicker co giãn đều trong Stack
              />
            </Stack>
            <TextField
              fullWidth id="note" label="Ghi chú" name="note"
              value={formState.note || ''} onChange={handleFormChange} size="small"
              multiline rows={3} InputLabelProps={{ shrink: true }}
              placeholder="Thêm ghi chú (không bắt buộc)..."
              disabled={isSaving}
            />
          </Stack>
        </LocalizationProvider>
      </DialogContent>
      <DialogActions sx={{p:2}}>
        <Button onClick={handleModalClose} color="inherit" variant="outlined" disabled={isSaving}>Hủy</Button>
        <Button type="submit" variant="contained" color="primary" disabled={isSaving}>
          {isSaving ? <CircularProgress size={24} color="inherit"/> : "Lưu ca"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}