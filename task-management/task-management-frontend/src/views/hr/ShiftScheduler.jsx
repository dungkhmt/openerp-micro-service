import React, { useState, useEffect, useCallback } from 'react';
import { startOfWeek, addDays, format, subDays, parseISO, isSameDay, isValid } from 'date-fns';
import vi from 'date-fns/locale/vi'; // Vietnamese locale for date-fns
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';

// MUI Core Components
import {
  AppBar, Toolbar, Typography, Button, IconButton, Container, Grid, Paper, Box,
  Modal, TextField, Select, MenuItem, FormControl, InputLabel, Checkbox, Avatar
} from '@mui/material';
import { alpha } from '@mui/material/styles'; // For transparent colors
import { LocalizationProvider, DatePicker, TimePicker } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';


// MUI Icons
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import StarRateIcon from '@mui/icons-material/StarRate';
import AccessTimeFilledIcon from '@mui/icons-material/AccessTimeFilled';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import AddIcon from '@mui/icons-material/Add';
import SettingsIcon from '@mui/icons-material/Settings';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import EventNoteIcon from '@mui/icons-material/EventNote';
import CloseIcon from '@mui/icons-material/Close';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
// import DragIndicatorIcon from '@mui/icons-material/DragIndicator'; // REMOVED as requested
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';


// --- Initial Data and Constants (MUST be defined before ShiftScheduler component) ---

// Sample initial data for shifts
const initialShifts = [
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '09:00', endTime: '12:00', duration: '3h 0m', details: 'Morning Shift', subDetails: 'Task A', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's1-2', userId: 'u1', day: '2025-05-19', startTime: '13:00', endTime: '17:15', duration: '4h 15m', details: 'Afternoon Shift', subDetails: 'Task B', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '09:00', endTime: '17:15', duration: '8h 15m', details: 'Frontend task', subDetails: 'UI implementation', muiColor: 'error.light', muiTextColor: 'error.darkerText' },
  { id: 's3', userId: 'u1', day: '2025-05-21', startTime: '10:00', endTime: '18:30', duration: '8h 30m', details: 'Meeting', subDetails: 'Client discussion', muiColor: 'info.light', muiTextColor: 'info.darkerText' },
  { id: 's4', userId: 'u3', day: '2025-05-22', startTime: '14:00', endTime: '18:00', duration: '4h 0m', details: 'Support', subDetails: 'Client Calls', muiColor: 'warning.light', muiTextColor: 'warning.darkerText' },
];

// Sample initial data for users
const initialUsers = [
  { id: 'u1', name: 'NV Ánh Nguyệt', summary: '8h 15m', avatarLetter: 'AN', avatarBgColor: 'secondary.main' },
  { id: 'u2', name: 'NV Bảo Long', summary: '8h 15m', avatarLetter: 'BL', avatarBgColor: 'primary.main' },
  { id: 'u3', name: 'NV Cẩm Tú', summary: '0h 0m', avatarLetter: 'CT', avatarBgColor: 'warning.main' },
  { id: 'uHPT', name: 'Hieu Phan Trung', summary: '0h 0m', avatarLetter: 'HT', avatarBgColor: 'success.main' },
  { id: 't4', name: 'Test User 4', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
];

// Constants for sticky header calculations
const TOP_BAR_HEIGHT = 61;
const AVAILABLE_SHIFTS_BANNER_HEIGHT = 36;
const PROJECTED_SALES_BANNER_HEIGHT = 36;
const INFO_BANNERS_HEIGHT = AVAILABLE_SHIFTS_BANNER_HEIGHT + PROJECTED_SALES_BANNER_HEIGHT;
const BULK_ACTIONS_BAR_HEIGHT = 50; // Estimated height for the new bar


// --- Utility Functions ---
const getInitials = (name) => {
  if (!name) return '??';
  const parts = name.split(' ');
  if (parts.length === 1 && parts[0].length > 0) return parts[0].substring(0, 2).toUpperCase();
  if (parts.length > 1 && parts[0].length > 0 && parts[parts.length -1].length > 0) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }
  return name.substring(0,2).toUpperCase();
};


// --- Child Components ---

// ShiftCard.jsx
function ShiftCard({
                     shift,
                     onDeleteShift,
                     onEditShift,
                     onAddAnotherShift,
                     provided,
                     snapshot,
                     isSelected,
                     onToggleSelect,
                     isAnyShiftSelected // NEW: Global selection mode indicator
                   }) {
  const [isHovered, setIsHovered] = useState(false);

  const handleCheckboxClick = (e) => {
    e.stopPropagation(); // Prevent card body click
    onToggleSelect(shift.id);
  };

  // Determine if individual action buttons should be visible
  const showIndividualActionButtons = isHovered && !isAnyShiftSelected && !snapshot.isDragging;
  // Determine if checkbox should be visible
  const showCheckbox = isHovered || isAnyShiftSelected;

  // Click on card body logic
  const handleCardBodyClick = (e) => {
    // This check is important. react-beautiful-dnd calls onClick after a drag.
    // `e.defaultPrevented` is usually set by dnd if a drag occurred.
    if (e.defaultPrevented) {
      return;
    }
    // If the click was on the checkbox area itself, let checkbox handler manage it
    if (e.target.closest('.selection-checkbox-area')) {
      return;
    }
    // If click was on action buttons area, let those buttons handle it
    if (e.target.closest('.action-buttons')) {
      return;
    }

    if (!isAnyShiftSelected) { // Only allow edit click if not in global selection mode
      onEditShift(shift);
    } else {
      // If in global selection mode, a click on the body (not checkbox or action buttons) toggles selection
      onToggleSelect(shift.id);
    }
  };

  return (
    <Paper
      ref={provided.innerRef}
      {...provided.draggableProps} // Draggable props for the entire card
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      elevation={snapshot.isDragging ? 4 : (isSelected ? 3 : 1)}
      sx={{
        p: 0.75,
        my: 0.5,
        bgcolor: snapshot.isDragging
          ? 'primary.lighter'
          : (isSelected ? (theme) => alpha(theme.palette.primary.main, 0.12) : (shift.muiColor || 'grey.200')),
        border: isSelected ? (theme) => `1px solid ${theme.palette.primary.main}` : `1px solid transparent`,
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
        transition: 'background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease',
        overflow: 'hidden',
      }}
    >
      <Box
        className="selection-checkbox-area" // Added class for easier targeting in handleCardBodyClick
        sx={{
          display: 'flex',
          alignItems: 'center',
          pr: 0.25,
          opacity: showCheckbox ? 1 : 0,
          transition: 'opacity 0.15s ease-in-out',
          minWidth: showCheckbox ? 28 : 0, // Approx width of checkbox + padding, or 0
          height: '100%', // Ensure clickable area for checkbox
        }}
      >
        {/* Render checkbox only when it's supposed to be visible to avoid it taking space */}
        {showCheckbox && (
          <Checkbox
            size="small"
            checked={isSelected}
            onChange={handleCheckboxClick}
            onClick={(e) => e.stopPropagation()} // Ensure this click is isolated
            icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
            checkedIcon={<CheckBoxIcon fontSize="small" />}
            sx={{ p: 0.2 }}
            tabIndex={showCheckbox ? 0 : -1}
          />
        )}
      </Box>

      {/* Main content area - This is now the drag handle AND click-to-edit/select target */}
      <Box
        {...provided.dragHandleProps} // Apply drag handle props HERE
        onClick={handleCardBodyClick}
        sx={{
          flexGrow: 1,
          cursor: snapshot.isDragging ? 'grabbing' : (isAnyShiftSelected ? 'pointer' : 'grab'),
          py: 0.5,
          pr: showIndividualActionButtons ? '30px' : '2px', // Make space for action buttons if they are visible
          overflow: 'hidden',
          whiteSpace: 'nowrap',
          userSelect: 'none', // Prevent text selection during drag attempts
        }}
      >
        <Typography variant="caption" component="div" sx={{ fontWeight: 'bold', color: shift.muiTextColor || 'text.primary', fontSize: '0.68rem' }}>
          {`${shift.startTime} - ${shift.endTime}`}
          <Typography variant="caption" sx={{ ml: 0.5, color: shift.muiTextColor, opacity: 0.8, fontSize: '0.65rem' }}>({shift.duration})</Typography>
        </Typography>
        <Typography variant="body2" sx={{ fontSize: '0.65rem', color: shift.muiTextColor, textOverflow: 'ellipsis', overflow:'hidden' }}>{shift.details}</Typography>
        {shift.subDetails && <Typography variant="caption" sx={{ fontSize: '0.6rem', color: shift.muiTextColor, opacity: 0.7, textOverflow: 'ellipsis', overflow:'hidden' }}>{shift.subDetails}</Typography>}
      </Box>

      {/* Individual Action Buttons - Conditionally Visible */}
      <Box
        className="action-buttons"
        sx={{
          position: 'absolute',
          top: '50%',
          transform: 'translateY(-50%)',
          right: 2,
          opacity: showIndividualActionButtons ? 1 : 0,
          transition: 'opacity 0.15s ease-in-out',
          display: 'flex',
          flexDirection: 'column',
          gap: 0.1,
          zIndex: 1,
          pointerEvents: showIndividualActionButtons ? 'auto' : 'none',
        }}
      >
        <IconButton size="small" onClick={(e) => {e.stopPropagation(); onAddAnotherShift(shift.userId, parseISO(shift.day))}} sx={{p:0.1, bgcolor: 'rgba(255,255,255,0.8)', '&:hover': {bgcolor: 'rgba(255,255,255,1)'}}} title="Add another shift for this day">
          <AddCircleOutlineIcon sx={{fontSize: '0.85rem'}} color="success"/>
        </IconButton>
        <IconButton size="small" onClick={(e) => {e.stopPropagation(); onEditShift(shift)}} sx={{p:0.1, bgcolor: 'rgba(255,255,255,0.8)', '&:hover': {bgcolor: 'rgba(255,255,255,1)'}}} title="Edit this shift">
          <SettingsIcon sx={{fontSize: '0.85rem'}} color="primary"/>
        </IconButton>
        <IconButton size="small" onClick={(e) => {e.stopPropagation(); onDeleteShift(shift.id)}} sx={{p:0.1, bgcolor: 'rgba(255,255,255,0.8)', '&:hover': {bgcolor: 'rgba(255,255,255,1)'}}} title="Delete this shift">
          <DeleteForeverIcon sx={{fontSize: '0.85rem'}} color="error"/>
        </IconButton>
      </Box>
    </Paper>
  );
}

// EmptyShiftSlot.jsx
function EmptyShiftSlot({ onAdd }) {
  return (
    <Paper
      variant="outlined"
      sx={{
        minHeight: 52,
        m: 0.5,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        borderColor: 'grey.400',
        borderStyle: 'dashed',
        cursor: 'pointer',
        flexGrow: 1,
        '&:hover': { bgcolor: 'grey.100', borderColor: 'grey.500' }
      }}
      onClick={onAdd}
      title="Thêm ca làm việc mới"
    >
      <AddIcon color="action" />
    </Paper>
  );
}

// DayCell.jsx
function DayCell({ userId, day, shiftsInCell, onAddShift, onDeleteShift, onEditShift, selectedShiftIds, onToggleSelectShift, isAnyShiftSelected }) {
  const droppableId = `user-${userId}-day-${format(day, 'yyyy-MM-dd')}`;
  return (
    <Droppable droppableId={droppableId} type="SHIFT">
      {(provided, snapshot) => (
        <Grid
          item
          xs
          ref={provided.innerRef}
          {...provided.droppableProps}
          sx={{
            p: 0.5,
            borderRight: 1,
            borderColor: 'divider',
            '&:last-child': { borderRight: 0 },
            minHeight: 60,
            display: 'flex',
            flexDirection: 'column',
            bgcolor: snapshot.isDraggingOver ? 'action.focus' : 'transparent',
            transition: 'background-color 0.2s ease',
          }}
        >
          {shiftsInCell.length > 0 ? (
            shiftsInCell.map((shift, index) => (
              <Draggable key={shift.id} draggableId={shift.id} index={index}>
                {(providedDraggable, snapshotDraggable) => (
                  <ShiftCard
                    shift={shift}
                    onDeleteShift={onDeleteShift}
                    onEditShift={onEditShift}
                    onAddAnotherShift={onAddShift} // This onAddShift is for the "add another" button on the card.
                    provided={providedDraggable}
                    snapshot={snapshotDraggable}
                    isSelected={selectedShiftIds.includes(shift.id)}
                    onToggleSelect={onToggleSelectShift}
                    isAnyShiftSelected={isAnyShiftSelected}
                  />
                )}
              </Draggable>
            ))
          ) : (
            !snapshot.isDraggingOver && <EmptyShiftSlot onAdd={() => onAddShift(userId, day)} /> // This onAdd is for the empty slot itself.
          )}
          {provided.placeholder}
        </Grid>
      )}
    </Droppable>
  );
}

// UserRow.jsx
function UserRow({ user, currentDate, shifts, onAddShift, onDeleteShift, onEditShift, selectedShiftIds, onToggleSelectShift, isAnyShiftSelected }) {
  const weekStartsOn = 1;
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Grid container sx={{ borderBottom: 1, borderColor: 'divider', '&:hover': { bgcolor: 'action.hover' } }}>
      <Grid item sx={{ width: 160, p: 1, borderRight: 1, borderColor: 'divider', bgcolor: 'grey.50', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 0.5 }}>
          <Avatar sx={{ width: 28, height: 28, fontSize: '0.8rem', mr: 1, bgcolor: user.avatarBgColor || 'primary.main' }}>
            {user.avatarLetter || getInitials(user.name)}
          </Avatar>
          <Typography variant="body2" sx={{ fontWeight: 'medium', lineHeight: 1.2 }} title={user.name}>{user.name}</Typography>
        </Box>
        <Typography variant="caption" color="text.secondary" sx={{ ml: '36px' }}>{user.summary}</Typography>
      </Grid>
      {daysOfWeek.map(day => {
        const shiftsInCell = shifts.filter(shift =>
          shift.userId === user.id && shift.day && isValid(parseISO(shift.day)) && isSameDay(parseISO(shift.day), day)
        );
        shiftsInCell.sort((a, b) => (a.startTime || "").localeCompare(b.startTime || ""));

        return (
          <DayCell
            key={format(day, 'yyyy-MM-dd')}
            userId={user.id}
            day={day}
            shiftsInCell={shiftsInCell}
            onAddShift={onAddShift}
            onDeleteShift={onDeleteShift}
            onEditShift={onEditShift}
            selectedShiftIds={selectedShiftIds}
            onToggleSelectShift={onToggleSelectShift}
            isAnyShiftSelected={isAnyShiftSelected}
          />
        );
      })}
    </Grid>
  );
}


// ShiftsGrid.jsx
function ShiftsGrid({ currentDate, shifts, users, onAddShift, onDeleteShift, onEditShift, selectedShiftIds, onToggleSelectShift, isAnyShiftSelected }) {
  return (
    <Box>
      {users.map(user => (
        <UserRow
          key={user.id}
          user={user}
          currentDate={currentDate}
          shifts={shifts}
          onAddShift={onAddShift}
          onDeleteShift={onDeleteShift}
          onEditShift={onEditShift}
          selectedShiftIds={selectedShiftIds}
          onToggleSelectShift={onToggleSelectShift}
          isAnyShiftSelected={isAnyShiftSelected}
        />
      ))}
    </Box>
  );
}

// CalendarHeader.jsx
function CalendarHeader({ currentDate, stickyTopOffset = 0 }) {
  const weekStartsOn = 1;
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));
  return (
    <Grid container sx={{
      bgcolor: 'grey.100', borderBottom: 1, borderColor: 'divider',
      position: 'sticky', top: TOP_BAR_HEIGHT + INFO_BANNERS_HEIGHT + stickyTopOffset,
      zIndex: 10
    }}>
      <Grid item sx={{ width: 160, p: 1, borderRight: 1, borderColor: 'divider', display: 'flex', alignItems: 'center' }}>
        <Typography variant="caption" sx={{ fontWeight: 'medium', textTransform: 'uppercase', color: 'text.secondary' }}>Nhân viên</Typography>
      </Grid>
      {days.map(day => (
        <Grid item xs key={day.toISOString()} sx={{ p: 1, textAlign: 'center', borderRight: 1, borderColor: 'divider', '&:last-child': { borderRight: 0 } }}>
          <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight:'medium' }}>{format(day, 'EEE', { locale: vi }).toUpperCase()}</Typography>
          <Typography variant="h6" sx={{ fontSize: '1.1rem', color: 'text.primary', fontWeight:'medium' }}>{format(day, 'dd')}</Typography>
        </Grid>
      ))}
    </Grid>
  );
}

// TopBar.jsx
function TopBar({ currentDate, onPrevWeek, onNextWeek, onToday }) {
  return (
    <AppBar position="sticky" color="default" elevation={1} sx={{zIndex: 20, height: TOP_BAR_HEIGHT}}>
      <Toolbar variant="dense">
        <Typography variant="h6" component="div" sx={{ flexGrow: 0.1, mr:1, fontSize:'1.1rem', color:'text.primary' }}>Lịch làm việc</Typography>
        <IconButton onClick={onPrevWeek} size="small" aria-label="Previous week"><ChevronLeftIcon /></IconButton>
        <Button onClick={onToday} size="small" variant="outlined" color="inherit" startIcon={<EventNoteIcon />} sx={{ mx: 1, fontSize:'0.75rem', py:0.3}}>Hôm nay</Button>
        <IconButton onClick={onNextWeek} size="small" aria-label="Next week"><ChevronRightIcon /></IconButton>
        <Typography variant="subtitle1" component="div" sx={{ ml: 2, color:'text.secondary', fontWeight:'medium' }}>{format(currentDate, 'MMMM yyyy' , { locale: vi })}</Typography>
        <Box sx={{ flexGrow: 1 }} />
        <Button size="small" variant="text" color="inherit" startIcon={<FilterAltIcon />} sx={{textTransform:'none', fontSize:'0.8rem'}}>Lọc</Button>
        <Button size="small" variant="text" color="inherit" startIcon={<FileDownloadIcon />} sx={{textTransform:'none', fontSize:'0.8rem'}}>Xuất</Button>
        <IconButton color="inherit" size="small"><SettingsIcon /></IconButton>
      </Toolbar>
    </AppBar>
  );
}

// InfoBanners.jsx
function InfoBanners({ currentDate, stickyTopOffset = 0 }) {
  const weekStartsOn = 1;
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));
  return (
    <Box sx={{position: 'sticky', top: TOP_BAR_HEIGHT + stickyTopOffset, zIndex: 15}}>
      <Paper elevation={0} square sx={{ p: 1, bgcolor: 'green.50', borderBottom: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', height: AVAILABLE_SHIFTS_BANNER_HEIGHT }}>
        <StarRateIcon fontSize="small" sx={{ mr: 1, color: 'orange.400' }} />
        <Typography variant="caption" sx={{ fontWeight: 'medium', color: 'green.800' }}>CA CÓ SẴN</Typography>
      </Paper>
      <Grid container sx={{ bgcolor: 'grey.200', borderBottom: 1, borderColor: 'divider', height: PROJECTED_SALES_BANNER_HEIGHT }}>
        <Grid item sx={{ width: 160, p: 1, borderRight: 1, borderColor: 'divider', display: 'flex', alignItems: 'center' }}>
          <Typography variant="caption" sx={{ fontWeight: 'medium', textTransform: 'uppercase', color: 'text.secondary' }}>Doanh số dự kiến</Typography>
        </Grid>
        {days.map(day => (
          <Grid item xs key={`sales-${day.toISOString()}`} sx={{ p: 1, textAlign: 'center', borderRight: 1, borderColor: 'divider', '&:last-child': { borderRight: 0 }, display: 'flex', alignItems: 'center', justifyContent:'center' }}>
            <Typography variant="caption" color="text.secondary">$0.00</Typography>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

// ShiftModal.jsx
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

function ShiftModal({ isOpen, onClose, onSave, users, initialFormState, isEditing }) {
  const [formState, setFormState] = useState(initialFormState);

  useEffect(() => {
    setFormState(initialFormState);
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
          <Typography id="shift-modal-title" variant="h6" component="h2">{isEditing ? "Chỉnh sửa ca làm việc" : "Thêm ca làm việc mới"}</Typography>
          <IconButton onClick={onClose} size="small"><CloseIcon /></IconButton>
        </Box>
        <Box component="form" onSubmit={handleSubmit} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth margin="dense" required>
                <InputLabel id="user-select-label" sx={{fontSize:'0.9rem'}}>Nhân viên</InputLabel>
                <Select labelId="user-select-label" id="userId" name="userId" value={formState.userId} label="Nhân viên" onChange={handleFormChange} size="small">
                  <MenuItem value="" disabled><em>Chọn nhân viên</em></MenuItem>
                  {users.map(user => (<MenuItem key={user.id} value={user.id}>{user.name}</MenuItem>))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <DatePicker label="Ngày" value={formState.day ? parseISO(formState.day) : new Date()} onChange={handleDateChange} slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true } }}/>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker label="Giờ bắt đầu" value={formState.day && formState.startTime ? parseISO(`${formState.day}T${formState.startTime}`) : null} onChange={(newValue) => handleTimeChange('startTime', newValue)} slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true } }}/>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TimePicker label="Giờ kết thúc" value={formState.day && formState.endTime ? parseISO(`${formState.day}T${formState.endTime}`) : null} onChange={(newValue) => handleTimeChange('endTime', newValue)} slotProps={{ textField: { fullWidth: true, margin: "dense", size:"small", required:true } }}/>
            </Grid>
            <Grid item xs={12}>
              <TextField margin="dense" required fullWidth id="details" label="Chi tiết chính" name="details" value={formState.details} onChange={handleFormChange} size="small" placeholder="Ví dụ: Họp team, Làm dự án X..."/>
            </Grid>
            <Grid item xs={12}>
              <TextField margin="dense" fullWidth id="subDetails" label="Chi tiết phụ (không bắt buộc)" name="subDetails" value={formState.subDetails} onChange={handleFormChange} size="small" placeholder="Ví dụ: Chuẩn bị slide, Fix bug Y..."/>
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

// BulkActionsBar.jsx
function BulkActionsBar({
                          selectedCount,
                          onDeleteSelected,
                          onCopySelectedToNextWeek,
                          onDeselectAll,
                          currentDate
                        }) {
  if (selectedCount === 0) {
    return null;
  }

  const nextWeekStartDate = format(addDays(startOfWeek(currentDate, { weekStartsOn: 1 }), 7), 'dd/MM/yyyy');

  return (
    <AppBar
      position="sticky"
      color="default" // Changed from inherit to default for a bit more standard appearance
      elevation={2}
      sx={{
        top: TOP_BAR_HEIGHT,
        height: BULK_ACTIONS_BAR_HEIGHT,
        zIndex: 19,
        bgcolor: 'primary.lighter', // Or use theme.palette.background.paper for consistency
        borderBottom: theme => `1px solid ${theme.palette.divider}`
      }}
    >
      <Toolbar variant="dense" sx={{ justifyContent: 'space-between', alignItems: 'center', height: '100%' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography variant="subtitle2" sx={{ mr: 2, color: 'primary.darkerText', fontWeight:'bold' }}>
            Đã chọn: {selectedCount} ca
          </Typography>
          <Button
            size="small"
            variant="contained"
            color="error"
            startIcon={<DeleteForeverIcon />}
            onClick={onDeleteSelected}
            sx={{ mr: 1, color:'white' }}
          >
            Xóa ({selectedCount})
          </Button>
          <Button
            size="small"
            variant="contained"
            color="secondary"
            startIcon={<ContentCopyIcon />}
            onClick={onCopySelectedToNextWeek}
            sx={{ mr: 1, color:'white' }}
            title={`Sao chép sang tuần bắt đầu ${nextWeekStartDate}`}
          >
            Chép sang tuần sau
          </Button>
        </Box>
        <Button
          size="small"
          variant="outlined"
          onClick={onDeselectAll}
          color="primary" // Ensure this contrasts with primary.lighter
          sx={{borderColor: 'primary.dark', color:'primary.dark'}}
        >
          Bỏ chọn tất cả
        </Button>
      </Toolbar>
    </AppBar>
  );
}


// --- Main Application Component (ShiftScheduler.jsx) ---
export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date(new Date().setHours(0,0,0,0)));
  const [shifts, setShifts] = useState(initialShifts);
  const [users, setUsers] = useState([]); // Initialized empty, populated by useEffect
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentEditingShift, setCurrentEditingShift] = useState(null);
  const [modalInitialFormState, setModalInitialFormState] = useState({ userId: '', day: format(new Date(), 'yyyy-MM-dd'), startTime: '09:00', endTime: '17:00', details: '', subDetails: '' });
  const [selectedShiftIds, setSelectedShiftIds] = useState([]);

  const isAnyShiftSelected = selectedShiftIds.length > 0;

  useEffect(() => {
    const updatedUsers = initialUsers.map(user => {
      const userShifts = shifts.filter(s => s.userId === user.id);
      let totalMs = 0;
      userShifts.forEach(s => {
        if (s.day && s.startTime && s.endTime) {
          const start = parseISO(`${s.day}T${s.startTime}`);
          const end = parseISO(`${s.day}T${s.endTime}`);
          if(isValid(start) && isValid(end)) {
            let diff = end.getTime() - start.getTime();
            if (diff < 0) diff += 24 * 60 * 60 * 1000;
            totalMs += diff;
          }
        }
      });
      const totalHours = Math.floor(totalMs / (1000 * 60 * 60));
      const totalMinutes = Math.floor((totalMs % (1000 * 60 * 60)) / (1000 * 60));
      return { ...user, summary: `${totalHours}h ${totalMinutes}m` };
    });
    setUsers(updatedUsers);
  }, [shifts]); // Removed initialUsers from deps as it's stable


  const handlePrevWeek = () => setCurrentDate(prev => subDays(prev, 7));
  const handleNextWeek = () => setCurrentDate(prev => addDays(prev, 7));
  const handleToday = () => setCurrentDate(new Date(new Date().setHours(0,0,0,0)));

  const handleOpenModal = (userId, day, shiftToEdit = null) => {
    // Prevent opening modal if a drag operation might be concluding or if in selection mode.
    // This check is primarily for the card body click.
    // If coming from an explicit edit button, this might not be necessary.
    // For now, the ShiftCard's handleCardBodyClick already gates on !isAnyShiftSelected for edit.

    if (shiftToEdit) {
      setCurrentEditingShift(shiftToEdit);
      setModalInitialFormState({
        userId: shiftToEdit.userId,
        day: shiftToEdit.day,
        startTime: shiftToEdit.startTime,
        endTime: shiftToEdit.endTime,
        details: shiftToEdit.details,
        subDetails: shiftToEdit.subDetails || '',
      });
    } else {
      setCurrentEditingShift(null);
      setModalInitialFormState({
        userId: userId || '',
        day: format(day || new Date(), 'yyyy-MM-dd'),
        startTime: '09:00',
        endTime: '17:00',
        details: '',
        subDetails: '',
      });
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setCurrentEditingShift(null);
  };

  const handleSaveShift = (formData) => {
    const { userId, day, startTime, endTime, details, subDetails } = formData;

    if (!userId || !day || !startTime || !endTime || !details) {
      alert("Vui lòng điền đầy đủ các trường bắt buộc: Nhân viên, Ngày, Giờ bắt đầu, Giờ kết thúc, Chi tiết chính.");
      return;
    }

    const start = parseISO(`${day}T${startTime}`);
    const end = parseISO(`${day}T${endTime}`);

    if (!isValid(start) || !isValid(end)) {
      alert("Ngày hoặc giờ không hợp lệ. Vui lòng kiểm tra lại định dạng (Ngày: yyyy-MM-dd, Giờ: HH:mm).");
      return;
    }

    let durationMs = end.getTime() - start.getTime();
    if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; }

    const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
    const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));

    // Get user specific color for new shifts, or use existing for edited ones
    const userForColor = users.find(u => u.id === userId); // Find user from the current users state
    const defaultColor = userForColor ? userForColor.avatarBgColor?.replace('.main','.light') || 'grey.200' : 'grey.200';
    const defaultTextColor = userForColor ? userForColor.avatarBgColor?.replace('.main','.darkerText') || 'text.primary' : 'text.primary';


    const shiftData = {
      userId, day, startTime, endTime,
      duration: `${durationHours}h ${durationMinutes}m`,
      details, subDetails,
      muiColor: currentEditingShift ? currentEditingShift.muiColor : defaultColor,
      muiTextColor: currentEditingShift ? currentEditingShift.muiTextColor : defaultTextColor,
    };

    if (currentEditingShift) {
      setShifts(prevShifts => prevShifts.map(s => s.id === currentEditingShift.id ? { ...s, ...shiftData, id: currentEditingShift.id } : s));
    } else {
      setShifts(prevShifts => [...prevShifts, { ...shiftData, id: `s${Date.now()}` }]);
    }
    handleCloseModal();
  };

  const handleDeleteSingleShift = (shiftIdToDelete) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa ca làm việc này không?")) {
      setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
      setSelectedShiftIds(prevSelected => prevSelected.filter(id => id !== shiftIdToDelete));
    }
  };

  const onDragEnd = (result) => {
    const { source, destination, draggableId } = result;

    if (!destination) return;
    if (destination.droppableId === source.droppableId && destination.index === source.index) return;

    const draggedShift = shifts.find(shift => shift.id === draggableId);
    if (!draggedShift) return;

    const destParts = destination.droppableId.split('-');
    const newUserId = destParts[1];
    const newDay = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

    setShifts(prevShifts =>
      prevShifts.map(shift =>
        shift.id === draggableId
          ? { ...shift, userId: newUserId, day: newDay }
          : shift
      )
    );
  };

  const handleToggleSelectShift = useCallback((shiftId) => {
    setSelectedShiftIds(prevSelected =>
      prevSelected.includes(shiftId)
        ? prevSelected.filter(id => id !== shiftId)
        : [...prevSelected, shiftId]
    );
  }, []);

  const handleDeselectAll = useCallback(() => {
    setSelectedShiftIds([]);
  }, []);

  const handleDeleteSelectedShifts = useCallback(() => {
    if (!isAnyShiftSelected || !window.confirm(`Bạn có chắc chắn muốn xóa ${selectedShiftIds.length} ca đã chọn không?`)) return;
    setShifts(prevShifts => prevShifts.filter(shift => !selectedShiftIds.includes(shift.id)));
    setSelectedShiftIds([]);
  }, [selectedShiftIds, isAnyShiftSelected]);

  const handleCopySelectedToNextWeek = useCallback(() => {
    if (!isAnyShiftSelected) return;
    const nextWeekStartDate = format(addDays(startOfWeek(currentDate, { weekStartsOn: 1 }), 7), 'dd/MM/yyyy');
    if (!window.confirm(`Bạn có chắc muốn sao chép ${selectedShiftIds.length} ca đã chọn sang tuần bắt đầu từ ${nextWeekStartDate} không?`)) return;

    const shiftsToCopyDetails = shifts.filter(shift => selectedShiftIds.includes(shift.id));
    const newCopiedShifts = shiftsToCopyDetails.map(shift => {
      const originalShiftDate = parseISO(shift.day);
      const nextWeekShiftDate = addDays(originalShiftDate, 7);
      return {
        ...shift,
        id: `s${Date.now()}-${Math.random().toString(16).slice(2)}`,
        day: format(nextWeekShiftDate, 'yyyy-MM-dd'),
      };
    });
    setShifts(prevShifts => [...prevShifts, ...newCopiedShifts]);
    setSelectedShiftIds([]);
  }, [selectedShiftIds, shifts, currentDate, isAnyShiftSelected]);

  const dynamicStickyOffset = isAnyShiftSelected ? BULK_ACTIONS_BAR_HEIGHT : 0;

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <DragDropContext onDragEnd={onDragEnd}>
        <Container maxWidth={false} disableGutters sx={{ bgcolor: 'grey.200', minHeight: '100vh' }}>
          <TopBar
            currentDate={currentDate}
            onPrevWeek={handlePrevWeek}
            onNextWeek={handleNextWeek}
            onToday={handleToday}
          />
          <BulkActionsBar
            selectedCount={selectedShiftIds.length}
            onDeleteSelected={handleDeleteSelectedShifts}
            onCopySelectedToNextWeek={handleCopySelectedToNextWeek}
            onDeselectAll={handleDeselectAll}
            currentDate={currentDate}
          />
          <Box sx={{px: {xs: 0, sm:1, md:2}, py:1}}>
            <InfoBanners currentDate={currentDate} stickyTopOffset={dynamicStickyOffset} />
            <Paper elevation={2} sx={{ overflow: 'hidden', mt:1 }}>
              <CalendarHeader currentDate={currentDate} stickyTopOffset={dynamicStickyOffset} />
              <Box sx={{ overflowX: 'auto' }}>
                <Box sx={{ minWidth: 1100 }}> {/* Ensure this minWidth is appropriate */}
                  <ShiftsGrid
                    currentDate={currentDate}
                    shifts={shifts}
                    users={users}
                    onAddShift={handleOpenModal} // For EmptyShiftSlot and AddAnother button on card
                    onDeleteShift={handleDeleteSingleShift} // For single delete button on card
                    onEditShift={(shift) => handleOpenModal(shift.userId, parseISO(shift.day), shift)} // For edit button or click
                    selectedShiftIds={selectedShiftIds}
                    onToggleSelectShift={handleToggleSelectShift}
                    isAnyShiftSelected={isAnyShiftSelected}
                  />
                </Box>
              </Box>
            </Paper>
          </Box>

          <Paper elevation={3} sx={{ position: 'fixed', bottom: 16, right: 16, p: 1.5, display: 'flex', alignItems: 'center', borderRadius: 2 }}>
            <AccessTimeFilledIcon color="primary" sx={{mr:1}}/>
            <Typography variant="caption">Bật đồng hồ chấm công</Typography>
            <Checkbox size="small" sx={{ml:0.5, p:0.2}}/>
          </Paper>

          <ShiftModal
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            onSave={handleSaveShift}
            users={users}
            initialFormState={modalInitialFormState}
            isEditing={!!currentEditingShift}
          />
        </Container>
      </DragDropContext>
    </LocalizationProvider>
  );
}

