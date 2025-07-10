import React, { useEffect, useMemo, useState } from "react";
import { format, isValid, parseISO } from "date-fns";
import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close.js";
import { DatePicker, TimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { vi } from 'date-fns/locale';
import toast from "react-hot-toast";
import CustomMultiSelect from "@/components/item/CustomMultiSelect"; // -> IMPORT COMPONENT MỚI

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

  // useEffect để reset state khi modal mở hoặc dữ liệu ban đầu thay đổi (GIỮ NGUYÊN)
  useEffect(() => {
    if (isOpen) {
      const defaultUserIds = Array.isArray(initialFormState.userIds) ? initialFormState.userIds : [];
      setFormState({
        ...initialFormState,
        userIds: isUnassignedContext && !isEditing ? [] : defaultUserIds,
        note: initialFormState.note || '',
        slots: initialFormState.slots !== undefined ? initialFormState.slots : 1,
      });
    }
  }, [initialFormState, isUnassignedContext, isEditing, isOpen]);

  // handleFormChange vẫn dùng cho các trường khác, nhưng không cần cho userIds nữa
  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormState(prev => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (newDate) => {
    setFormState(prev => ({ ...prev, day: isValid(newDate) ? format(newDate, 'yyyy-MM-dd') : null }));
  };

  const handleTimeChange = (name, newTime) => {
    setFormState(prev => ({ ...prev, [name]: isValid(newTime) ? format(newTime, 'HH:mm') : null }));
  };

  // handleSubmit được GIỮ NGUYÊN HOÀN TOÀN
  const handleSubmit = (e) => {
    e.preventDefault();
    if (isSaving) return;
    if (!formState.day) { toast.error("Vui lòng chọn ngày cho ca làm việc."); return; }
    if (!formState.startTime || !formState.endTime) { toast.error("Vui lòng chọn giờ bắt đầu và kết thúc."); return; }
    if (!showSlotsField && (!formState.userIds || formState.userIds.length === 0)) { toast.error("Vui lòng chọn ít nhất một nhân viên."); return; }
    if (showSlotsField && formState.slots < (formState.userIds?.length || 0) ) { toast.error("Số nhân viên được gán không thể vượt quá tổng số slot."); return; }
    onSave({ ...formState, _initiatedAsNewUnassignedContext: isUnassignedContext && !isEditing });
  };

  // Các hàm và memo khác được giữ nguyên
  const modalTitleText = useMemo(() => {
    if (isUnassignedContext && !isEditing) return "Thêm ca mới (tùy chọn gán)";
    if (unassignedShiftBeingEdited) return `Gán / Sửa ca chờ (${unassignedShiftBeingEdited.slots_available || 0} slot trống)`;
    if (isEditing) return "Chỉnh sửa ca làm việc";
    return "Thêm ca làm việc";
  }, [isEditing, isUnassignedContext, unassignedShiftBeingEdited]);

  const getDatePickerValue = () => (formState.day && isValid(parseISO(formState.day)) ? parseISO(formState.day) : null);
  const getTimePickerValue = (timeString) => {
    if (timeString && typeof timeString === 'string') {
      const [hours, minutes] = timeString.split(':');
      if (!isNaN(parseInt(hours)) && !isNaN(parseInt(minutes))) return new Date(0, 0, 0, parseInt(hours), parseInt(minutes));
    }
    return null;
  };
  const showSlotsField = isUnassignedContext || unassignedShiftBeingEdited;
  const handleModalClose = () => { if (!isSaving) onClose(); };

  return (
    <Dialog open={isOpen} onClose={handleModalClose} fullWidth maxWidth="sm" PaperProps={{ component: 'form', onSubmit: handleSubmit }}>
      <DialogTitle sx={{ ...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 1.5 }}>
        <Typography variant="h6" component="div" sx={{ fontWeight: 600, fontSize: titleProps?.sx?.fontSize || '1.2rem'}}>{modalTitleText}</Typography>
        <IconButton aria-label="đóng" onClick={handleModalClose} sx={{ p: 0.5 }} disabled={isSaving}><CloseIcon /></IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{ pt: '12px !important' }}>
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
          <Stack spacing={2.5} sx={{ mt: 1 }}>

            {/* ---- THAY THẾ HOÀN TOÀN BẰNG COMPONENT MỚI ---- */}
            <CustomMultiSelect
              label={`Nhân viên ${showSlotsField ? "(Tùy chọn)" : "(*)"}`}
              placeholder={showSlotsField ? "Gán cho NV hoặc để trống" : "Chọn nhân viên"}
              options={users || []}
              selectedValues={formState.userIds || []}
              onChange={(newUserIds) => setFormState(prev => ({...prev, userIds: newUserIds}))}
              loading={!users}
              valueKey="id"
              labelKey="name"
              isOptionEqualToValue={(option, value) => option.id === value.id}
            />

            {showSlotsField && (
              <TextField
                fullWidth label="Tổng Slot ban đầu (*)" name="slots"
                type="number" value={formState.slots === undefined ? '1' : formState.slots}
                onChange={handleFormChange} size="small"
                InputLabelProps={{ shrink: true }} InputProps={{ inputProps: { min: 1 } }}
                helperText={unassignedShiftBeingEdited ? `Ca chờ này còn ${unassignedShiftBeingEdited.slots_available || 0} slot trống.` : "Số slot sẽ tự động điều chỉnh nếu đã gán NV."}
                disabled={isSaving}
              />
            )}
            <DatePicker
              label="Ngày (*)" value={getDatePickerValue()} onChange={handleDateChange}
              slotProps={{ textField: { fullWidth: true, size: "small", required: true } }}
              disabled={isSaving} format="dd/MM/yyyy"
            />
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TimePicker
                label="Giờ bắt đầu (*)" value={getTimePickerValue(formState.startTime)}
                onChange={(newValue) => handleTimeChange('startTime', newValue)}
                slotProps={{ textField: { fullWidth: true, size: "small", required: true } }}
                ampm={false} disabled={isSaving}
              />
              <TimePicker
                label="Giờ kết thúc (*)" value={getTimePickerValue(formState.endTime)}
                onChange={(newValue) => handleTimeChange('endTime', newValue)}
                slotProps={{ textField: { fullWidth: true, size: "small", required: true } }}
                ampm={false} disabled={isSaving}
              />
            </Stack>
            <TextField
              fullWidth id="note" label="Ghi chú" name="note"
              value={formState.note || ''} onChange={handleFormChange} size="small"
              multiline rows={3}
              placeholder="Thêm ghi chú (không bắt buộc)..."
              disabled={isSaving}
            />
          </Stack>
        </LocalizationProvider>
      </DialogContent>
      <DialogActions sx={{ p: 2 }}>
        <Button onClick={handleModalClose} color="inherit" variant="outlined" disabled={isSaving}>Hủy</Button>
        <Button type="submit" variant="contained" color="primary" disabled={isSaving}>
          {isSaving ? <CircularProgress size={24} color="inherit" /> : "Lưu ca"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}