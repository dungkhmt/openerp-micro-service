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


export default function ShiftModal({ isOpen, onClose, onSave, users, initialFormState, isEditing }) {
  const [formState, setFormState] = useState(initialFormState);

  useEffect(() => {
    // Ensure userIds is always an array and note is initialized
    setFormState({
      ...initialFormState,
      userIds: Array.isArray(initialFormState.userIds) ? initialFormState.userIds : [],
      note: initialFormState.note || ''
    });
  }, [initialFormState]);

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

  return (
    <Modal open={isOpen} onClose={onClose} aria-labelledby="shift-modal-title" aria-describedby="shift-modal-description">
      <Box sx={modalStyle}>
        <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2}}>
          <Typography id="shift-modal-title" variant="h6" component="h2">{isEditing ? "Chỉnh sửa ca làm việc" : "Thêm ca làm việc"}</Typography>
          <IconButton onClick={onClose} size="small"><CloseIcon /></IconButton>
        </Box>
        <Box component="form" onSubmit={handleSubmit} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12}> {/* Changed to full width for better multi-select display */}
              <FormControl fullWidth margin="dense" required>
                <InputLabel id="user-select-label" sx={{fontSize:'0.9rem'}}>Nhân viên (chọn một hoặc nhiều)</InputLabel>
                <Select
                  labelId="user-select-label"
                  id="userIds" // CHANGED
                  name="userIds" // CHANGED
                  multiple // ADDED
                  value={formState.userIds || []} // Ensure value is always an array
                  label="Nhân viên (chọn một hoặc nhiều)" // UPDATED LABEL
                  onChange={handleFormChange}
                  size="small"
                  renderValue={(selected) => {
                    if (!selected || selected.length === 0) {
                      return <em>Chọn nhân viên</em>;
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
            <Grid item xs={12} sm={6}>
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
                placeholder="Thêm ghi chú cho ca làm việc (không bắt buộc)..."
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