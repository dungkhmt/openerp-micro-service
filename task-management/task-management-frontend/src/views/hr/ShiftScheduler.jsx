import React, { useState, useEffect, useCallback } from 'react';
import { startOfWeek, addDays, format, subDays, parseISO, isSameDay, isValid, addWeeks, differenceInCalendarDays, getDay } from 'date-fns';
import vi from 'date-fns/locale/vi'; // Vietnamese locale for date-fns
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';

// MUI Core Components
import {
  AppBar, Toolbar, Typography, Button, IconButton, Container, Grid, Paper, Box,
  Modal, TextField, Select, MenuItem, FormControl, InputLabel, Checkbox, Avatar,
  List, ListItem, ListItemButton, ListItemText, ListItemIcon, FormGroup, FormControlLabel, CircularProgress
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
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import IndeterminateCheckBoxIcon from '@mui/icons-material/IndeterminateCheckBox';


// --- Initial Data and Constants (MUST be defined before ShiftScheduler component) ---

// Sample initial data for shifts
const initialShifts = [
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '09:00', endTime: '12:00', duration: '3h 0m', note: 'Morning Shift with very very very long details to test ellipsis functionality and see if it works as expected', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's1-2', userId: 'u1', day: '2025-05-19', startTime: '13:00', endTime: '17:15', duration: '4h 15m', note: 'Afternoon Shift', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '09:00', endTime: '17:15', duration: '8h 15m', note: 'Frontend task with a lot of details that might overflow the container width', muiColor: 'error.light', muiTextColor: 'error.darkerText' },
  { id: 's3', userId: 'u1', day: '2025-05-21', startTime: '10:00', endTime: '18:30', duration: '8h 30m', note: 'Meeting', muiColor: 'info.light', muiTextColor: 'info.darkerText' },
  { id: 's4', userId: 'u3', day: '2025-05-22', startTime: '14:00', endTime: '18:00', duration: '4h 0m', note: 'Support', muiColor: 'warning.light', muiTextColor: 'warning.darkerText' },
];

// Sample initial data for users
const initialUsers = [
  { id: 'u1', name: 'NV Ánh Nguyệt', summary: '8h 15m', avatarLetter: 'AN', avatarBgColor: 'secondary.main' },
  { id: 'u2', name: 'NV Bảo Long', summary: '8h 15m', avatarLetter: 'BL', avatarBgColor: 'primary.main' },
  { id: 'u3', name: 'NV Cẩm Tú', summary: '0h 0m', avatarLetter: 'CT', avatarBgColor: 'warning.main' },
  { id: 'uHPT', name: 'Hieu Phan Trung', summary: '0h 0m', avatarLetter: 'HT', avatarBgColor: 'success.main' },
  { id: 't77', name: 'Test User 1', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't74', name: 'Test User 2', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't714', name: 'Test User 3', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't34', name: 'Test User 5', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't64', name: 'Test User 9', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  // For testing scroll, add more users:
  { id: 'u4', name: 'NV Đăng Khoa', summary: '0h 0m', avatarLetter: 'ĐK', avatarBgColor: 'error.main' },
  { id: 'u5', name: 'NV Thu Hà', summary: '0h 0m', avatarLetter: 'TH', avatarBgColor: 'secondary.light' },
  { id: 'u6', name: 'NV Minh Quân', summary: '0h 0m', avatarLetter: 'MQ', avatarBgColor: 'primary.light' },
  { id: 'u7', name: 'NV Phương Mai', summary: '0h 0m', avatarLetter: 'PM', avatarBgColor: 'warning.light' },
  { id: 'u8', name: 'NV Gia Huy', summary: '0h 0m', avatarLetter: 'GH', avatarBgColor: 'success.light' },
  { id: 'u9', name: 'NV Ngọc Lan', summary: '0h 0m', avatarLetter: 'NL', avatarBgColor: 'info.light' },
  { id: 'u10', name: 'NV Tiến Dũng', summary: '0h 0m', avatarLetter: 'TD', avatarBgColor: 'error.light' },
  { id: 'u11', name: 'NV Hoài An', summary: '0h 0m', avatarLetter: 'HA', avatarBgColor: 'secondary.main' },
  { id: 'u12', name: 'NV Tuấn Kiệt', summary: '0h 0m', avatarLetter: 'TK', avatarBgColor: 'primary.main' },

];

// Constants for sticky header calculations
const TOP_BAR_HEIGHT = 61;
const AVAILABLE_SHIFTS_BANNER_HEIGHT = 36;
const PROJECTED_SALES_BANNER_HEIGHT = 36;
const INFO_BANNERS_TOTAL_HEIGHT = AVAILABLE_SHIFTS_BANNER_HEIGHT + PROJECTED_SALES_BANNER_HEIGHT;
const BULK_ACTIONS_BAR_HEIGHT = 50;
const WEEK_STARTS_ON = 1; // Monday

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
                     isAnyShiftSelected
                   }) {
  const [isHovered, setIsHovered] = useState(false);

  const handleCheckboxClick = (e) => {
    e.stopPropagation();
    onToggleSelect(shift.id);
  };

  const handleCardBodyClick = (e) => {
    if (e.defaultPrevented) {
      return;
    }
    if (e.target.closest('.selection-checkbox-area') || e.target.closest('.add-action-button-area')) {
      return;
    }

    if (!isAnyShiftSelected) {
      onEditShift(shift);
    } else {
      onToggleSelect(shift.id);
    }
  };

  const showCheckbox = (isHovered || isAnyShiftSelected) && !snapshot.isDragging;
  const showAddButtonOnly = isHovered && !isAnyShiftSelected && !snapshot.isDragging;

  return (
    <Paper
      ref={provided.innerRef}
      {...provided.draggableProps}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      elevation={snapshot.isDragging ? 4 : (isSelected ? 3 : 1)}
      sx={{
        my: 0.5,
        height: 50,
        boxSizing: 'border-box',
        bgcolor: snapshot.isDragging
          ? 'primary.lighter'
          : (isSelected ? (theme) => alpha(theme.palette.primary.main, 0.12) : 'background.paper'),
        border: isSelected
          ? (theme) => `2px solid ${theme.palette.primary.main}`
          : (theme) => `1px dashed ${alpha(theme.palette.primary.main, 0.5)}`,
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
        transition: 'background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease',
        width: '100%',
        maxWidth: '100%',
        overflow: 'hidden',
      }}
    >
      <Box
        className="selection-checkbox-area"
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          width: showCheckbox ? 32 : 0,
          flexShrink: 0,
          height: '100%',
          transition: 'width 0.15s ease-in-out',
          overflow: 'hidden',
        }}
      >
        <Checkbox
          size="small"
          checked={isSelected}
          onChange={handleCheckboxClick}
          onClick={(e) => e.stopPropagation()}
          icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
          checkedIcon={<CheckBoxIcon fontSize="small" />}
          sx={{
            p: 0.5,
            opacity: showCheckbox ? 1 : 0,
            visibility: showCheckbox ? 'visible' : 'hidden',
            transition: 'opacity 0.1s ease-in-out, visibility 0.1s ease-in-out',
          }}
          tabIndex={showCheckbox ? 0 : -1}
        />
      </Box>

      <Box
        {...provided.dragHandleProps}
        onClick={handleCardBodyClick}
        sx={{
          flexGrow: 1,
          minWidth: 0,
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          pl: showCheckbox ? 0.5 : 1.5,
          pr: showAddButtonOnly ? 4.5 : 1.5,
          cursor: snapshot.isDragging ? 'grabbing' : (isAnyShiftSelected ? 'pointer' : 'grab'),
          overflow: 'hidden',
          whiteSpace: 'nowrap',
          userSelect: 'none',
          transition: 'padding-left 0.15s ease-in-out, padding-right 0.15s ease-in-out',
        }}
      >
        <Typography
          variant="caption"
          component="div"
          sx={{
            fontWeight: 'bold',
            color: shift.muiTextColor || 'text.primary',
            fontSize: '0.68rem',
            lineHeight: 1.3,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis'
          }}
        >
          {`${shift.startTime} - ${shift.endTime}`}
          <Typography component="span" variant="caption" sx={{ ml: 0.5, color: shift.muiTextColor, opacity: 0.8, fontSize: '0.65rem' }}>
            ({shift.duration})
          </Typography>
        </Typography>

        {shift.note && shift.note.trim() !== '' && (
          <Typography
            variant="body2"
            sx={{
              fontSize: '0.65rem',
              color: shift.muiTextColor ? shift.muiTextColor : 'text.secondary',
              opacity: shift.muiTextColor ? 0.85 : 1.0,
              lineHeight: 1.3,
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
            title={shift.note}
          >
            {shift.note}
          </Typography>
        )}
      </Box>

      <Box
        className="add-action-button-area"
        sx={{
          position: 'absolute',
          top: '50%',
          right: '4px',
          transform: 'translateY(-50%)',
          width: 28,
          height: 28,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          opacity: showAddButtonOnly ? 1 : 0,
          visibility: showAddButtonOnly ? 'visible' : 'hidden',
          transition: 'opacity 0.15s ease-in-out, visibility 0.15s ease-in-out',
          zIndex: 1,
          pointerEvents: showAddButtonOnly ? 'auto' : 'none',
        }}
      >
        <IconButton
          size="small"
          onClick={(e) => {
            e.stopPropagation();
            onAddAnotherShift(shift.userId, parseISO(shift.day));
          }}
          sx={{
            p: 0.3,
            bgcolor: 'rgba(230,230,230,0.85)',
            '&:hover': { bgcolor: 'rgba(210,210,210,1)' },
            borderRadius: '4px',
            width: '100%',
            height: '100%',
          }}
          title="Thêm ca làm việc khác vào ngày này"
        >
          <AddIcon sx={{ fontSize: '1rem' }} color="action" />
        </IconButton>
      </Box>
    </Paper>
  );
}


// EmptyShiftSlot.jsx
function EmptyShiftSlot({ onAdd }) {
  const [isHovered, setIsHovered] = useState(false);

  return (
    <Paper
      elevation={0}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      sx={{
        minHeight: 52,
        m: 0.5,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        borderWidth: 1,
        borderStyle: 'dashed',
        borderColor: isHovered ? 'grey.500' : 'transparent',
        bgcolor: isHovered ? 'grey.100' : 'transparent',
        cursor: 'pointer',
        flexGrow: 1,
        transition: 'background-color 0.2s ease-in-out, border-color 0.2s ease-in-out',
      }}
      onClick={onAdd}
      title="Thêm ca làm việc mới"
    >
      <AddIcon
        color="action"
        sx={{
          transition: 'opacity 0.2s ease-in-out',
          opacity: isHovered ? 1 : 0,
        }}
      />
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
            overflow: 'hidden',
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
                    onAddAnotherShift={onAddShift}
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
            !snapshot.isDraggingOver && <EmptyShiftSlot onAdd={() => onAddShift(userId, day)} />
          )}
          {provided.placeholder}
        </Grid>
      )}
    </Droppable>
  );
}

// UserRow.jsx
function UserRow({ user, currentDate, shifts, onAddShift, onDeleteShift, onEditShift, selectedShiftIds, onToggleSelectShift, isAnyShiftSelected }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
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
function CalendarHeader({
                          currentDate,
                          stickyTopOffset = 0,
                          onToggleSelectAll,
                          isAllSelectedInView,
                          isIndeterminateInView
                        }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));
  return (
    <Grid container sx={{
      bgcolor: 'grey.100', borderBottom: 1, borderColor: 'divider',
      position: 'sticky',
      top: 0,
      zIndex: 10
    }}>
      <Grid item sx={{ width: 160, p: 0.5, borderRight: 1, borderColor: 'divider', display: 'flex', alignItems: 'center' }}>
        <Checkbox
          size="small"
          checked={isAllSelectedInView}
          indeterminate={isIndeterminateInView && !isAllSelectedInView}
          onChange={onToggleSelectAll}
          icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
          checkedIcon={<CheckBoxIcon fontSize="small" />}
          indeterminateIcon={<IndeterminateCheckBoxIcon fontSize="small" />}
          sx={{mr: 0.5, p:0.25}}
          title={isAllSelectedInView ? "Bỏ chọn tất cả ca trong tuần" : (isIndeterminateInView ? "Chọn tất cả ca trong tuần" : "Chọn tất cả ca trong tuần")}
        />
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
        <Typography variant="subtitle1" component="div" sx={{ ml: 2, color:'text.secondary', fontWeight:'medium' }}>
          {format(currentDate, 'MMMM yyyy', { locale: vi })}
        </Typography>
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
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
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
    setFormState({ ...initialFormState, note: initialFormState.note || '' });
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

// BulkActionsBar.jsx
function BulkActionsBar({
                          selectedCount,
                          onDeleteSelected,
                          onOpenCopyModal, // MODIFIED: Renamed prop
                          onDeselectAll,
                        }) {
  if (selectedCount === 0) {
    return null;
  }

  return (
    <AppBar
      position="sticky"
      color="default"
      elevation={2}
      sx={{
        top: TOP_BAR_HEIGHT,
        height: BULK_ACTIONS_BAR_HEIGHT,
        zIndex: 19,
        bgcolor: 'primary.lighter',
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
            onClick={onOpenCopyModal} // MODIFIED: Using new prop
            sx={{ mr: 1, color:'white' }}
            title="Sao chép các ca đã chọn" // MODIFIED: Updated title
          >
            Sao chép {/* MODIFIED: Changed text */}
          </Button>
        </Box>
        <Button
          size="small"
          variant="outlined"
          onClick={onDeselectAll}
          color="primary"
          sx={{borderColor: 'primary.dark', color:'primary.dark'}}
        >
          Bỏ chọn tất cả
        </Button>
      </Toolbar>
    </AppBar>
  );
}

// CopyShiftsModal.jsx
const copyModalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: { xs: '90%', sm: 400 }, // Smaller width for this modal
  maxHeight: '80vh',
  bgcolor: 'background.paper',
  border: '1px solid #ccc',
  boxShadow: 24,
  p: 3,
  borderRadius: 1,
  display: 'flex',
  flexDirection: 'column'
};

function CopyShiftsModal({ isOpen, onClose, onConfirmCopy, currentDate, numSelectedShifts }) {
  const [selectedWeeks, setSelectedWeeks] = useState([]);
  const [weekOptions, setWeekOptions] = useState([]);
  const [isLoading, setIsLoading] = useState(false);


  useEffect(() => {
    if (isOpen) {
      const options = [];
      // Generate options for the next 12 weeks, starting from the week *after* the current one
      const startOfNextWeek = startOfWeek(addDays(currentDate, 7), { weekStartsOn: WEEK_STARTS_ON });
      for (let i = 0; i < 12; i++) {
        const weekStartDate = addWeeks(startOfNextWeek, i);
        const weekEndDate = addDays(weekStartDate, 6);
        options.push({
          value: weekStartDate.toISOString(), // Store ISO string as value
          label: `Tuần ${format(weekStartDate, 'dd/MM')} - ${format(weekEndDate, 'dd/MM/yyyy')}`,
        });
      }
      setWeekOptions(options);
      setSelectedWeeks([]); // Reset selected weeks when modal opens
    }
  }, [isOpen, currentDate]);

  const handleToggleWeek = (weekValue) => {
    setSelectedWeeks((prev) =>
      prev.includes(weekValue)
        ? prev.filter((w) => w !== weekValue)
        : [...prev, weekValue]
    );
  };

  const handleConfirm = async () => {
    if (selectedWeeks.length === 0) {
      alert("Vui lòng chọn ít nhất một tuần để sao chép.");
      return;
    }
    setIsLoading(true);
    // Convert ISO strings back to Date objects for the callback
    const targetWeekStartDates = selectedWeeks.map(isoString => parseISO(isoString));
    await onConfirmCopy(targetWeekStartDates);
    setIsLoading(false);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <Modal open={isOpen} onClose={onClose} aria-labelledby="copy-shifts-modal-title">
      <Box sx={copyModalStyle}>
        <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2, flexShrink: 0}}>
          <Typography id="copy-shifts-modal-title" variant="h6" component="h2">
            Sao chép {numSelectedShifts} ca
          </Typography>
          <IconButton onClick={onClose} size="small" disabled={isLoading}><CloseIcon /></IconButton>
        </Box>

        <Typography variant="body2" sx={{mb:1}}>Chọn (các) tuần muốn sao chép đến:</Typography>

        <Box sx={{ overflowY: 'auto', mb: 2, flexGrow: 1 }}>
          <List dense>
            {weekOptions.map((option) => (
              <ListItem key={option.value} disablePadding>
                <ListItemButton onClick={() => handleToggleWeek(option.value)} dense disabled={isLoading}>
                  <ListItemIcon sx={{minWidth: 32}}>
                    <Checkbox
                      edge="start"
                      checked={selectedWeeks.includes(option.value)}
                      tabIndex={-1}
                      disableRipple
                      size="small"
                      disabled={isLoading}
                    />
                  </ListItemIcon>
                  <ListItemText primary={option.label} primaryTypographyProps={{fontSize: '0.9rem'}}/>
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </Box>

        <Box sx={{ mt: 'auto', display: 'flex', justifyContent: 'flex-end', gap: 1, flexShrink: 0, pt:1, borderTop: '1px solid', borderColor:'divider' }}>
          <Button onClick={onClose} variant="outlined" color="inherit" disabled={isLoading}>Hủy</Button>
          <Button
            onClick={handleConfirm}
            variant="contained"
            color="primary"
            disabled={isLoading || selectedWeeks.length === 0}
            startIcon={isLoading ? <CircularProgress size={16} color="inherit"/> : null}
          >
            {isLoading ? "Đang sao chép..." : `Sao chép (${selectedWeeks.length} tuần)`}
          </Button>
        </Box>
      </Box>
    </Modal>
  );
}


// --- Main Application Component (ShiftScheduler.jsx) ---
export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date(new Date().setHours(0,0,0,0)));
  const [shifts, setShifts] = useState(initialShifts);
  const [users, setUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentEditingShift, setCurrentEditingShift] = useState(null);
  const [modalInitialFormState, setModalInitialFormState] = useState({
    userId: '',
    day: format(new Date(), 'yyyy-MM-dd'),
    startTime: '09:00',
    endTime: '17:00',
    note: ''
  });
  const [selectedShiftIds, setSelectedShiftIds] = useState([]);
  const [isCopyModalOpen, setIsCopyModalOpen] = useState(false); // State for new copy modal


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
  }, [shifts]);


  const handlePrevWeek = () => setCurrentDate(prev => subDays(prev, 7));
  const handleNextWeek = () => setCurrentDate(prev => addDays(prev, 7));
  const handleToday = () => setCurrentDate(new Date(new Date().setHours(0,0,0,0)));

  const handleOpenModal = (userId, day, shiftToEdit = null) => {
    if (shiftToEdit) {
      setCurrentEditingShift(shiftToEdit);
      setModalInitialFormState({
        userId: shiftToEdit.userId,
        day: shiftToEdit.day,
        startTime: shiftToEdit.startTime,
        endTime: shiftToEdit.endTime,
        note: shiftToEdit.note || '',
      });
    } else {
      setCurrentEditingShift(null);
      setModalInitialFormState({
        userId: userId || '',
        day: format(day || new Date(), 'yyyy-MM-dd'),
        startTime: '09:00',
        endTime: '17:00',
        note: '',
      });
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setCurrentEditingShift(null);
  };

  const handleSaveShift = (formData) => {
    const { userId, day, startTime, endTime, note } = formData;

    if (!userId || !day || !startTime || !endTime) {
      alert("Vui lòng điền đầy đủ các trường bắt buộc: Nhân viên, Ngày, Giờ bắt đầu, Giờ kết thúc.");
      return;
    }

    const parsedDay = parseISO(day);
    if (!isValid(parsedDay)) {
      alert("Ngày không hợp lệ. Vui lòng kiểm tra lại định dạng (YYYY-MM-DD).");
      return;
    }

    const start = parseISO(`${format(parsedDay, 'yyyy-MM-dd')}T${startTime}`);
    const end = parseISO(`${format(parsedDay, 'yyyy-MM-dd')}T${endTime}`);

    if (!isValid(start) || !isValid(end)) {
      alert("Giờ bắt đầu hoặc kết thúc không hợp lệ. Vui lòng kiểm tra lại định dạng (HH:mm).");
      return;
    }

    let durationMs = end.getTime() - start.getTime();
    if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; }

    const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
    const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));

    const userForColor = users.find(u => u.id === userId);
    const shiftMuiColor = currentEditingShift?.muiColor ||
      (userForColor?.avatarBgColor?.replace('.main','.light') || 'grey.200');
    const shiftMuiTextColor = currentEditingShift?.muiTextColor ||
      (userForColor?.avatarBgColor?.replace('.main','.darkerText') || 'text.primary');

    const shiftData = {
      userId,
      day: format(parsedDay, 'yyyy-MM-dd'),
      startTime,
      endTime,
      duration: `${durationHours}h ${durationMinutes}m`,
      note,
      muiColor: shiftMuiColor,
      muiTextColor: shiftMuiTextColor,
    };

    if (currentEditingShift) {
      setShifts(prevShifts => prevShifts.map(s => s.id === currentEditingShift.id ? { ...s, ...shiftData } : s));
    } else {
      setShifts(prevShifts => [...prevShifts, { ...shiftData, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}` }]);
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
    const newUserId = destParts[1]; // Assuming format user-USERID-day-YYYY-MM-DD
    const newDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

    const userForColor = users.find(u => u.id === newUserId);
    const newMuiColor = userForColor?.avatarBgColor?.replace('.main','.light') || 'grey.200';
    const newMuiTextColor = userForColor?.avatarBgColor?.replace('.main','.darkerText') || 'text.primary';

    setShifts(prevShifts =>
      prevShifts.map(shift =>
        shift.id === draggableId
          ? { ...shift, userId: newUserId, day: newDayString, muiColor: newMuiColor, muiTextColor: newMuiTextColor }
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

  // --- "Select All" in current view logic ---
  const getAllShiftIdsInCurrentView = useCallback(() => {
    const weekStart = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
    const weekEnd = addDays(weekStart, 6);
    const idsInView = [];
    shifts.forEach(shift => {
      if (isValid(parseISO(shift.day))) {
        const shiftDate = parseISO(shift.day);
        if (shiftDate >= weekStart && shiftDate <= weekEnd) {
          // Check if the user of the shift is in the current list of users (implicitly all users are shown)
          if (users.find(u => u.id === shift.userId)) {
            idsInView.push(shift.id);
          }
        }
      }
    });
    return idsInView;
  }, [shifts, users, currentDate]);

  const allShiftIdsInView = getAllShiftIdsInCurrentView();
  const selectedShiftsInViewCount = allShiftIdsInView.filter(id => selectedShiftIds.includes(id)).length;

  const isAllSelectedInView = allShiftIdsInView.length > 0 && selectedShiftsInViewCount === allShiftIdsInView.length;
  const isIndeterminateInView = selectedShiftsInViewCount > 0 && selectedShiftsInViewCount < allShiftIdsInView.length;

  const handleToggleSelectAllInView = useCallback(() => {
    const idsInView = getAllShiftIdsInCurrentView();
    if (isAllSelectedInView) { // If all are selected, deselect them
      setSelectedShiftIds(prev => prev.filter(id => !idsInView.includes(id)));
    } else { // Else (none or some are selected), select all in view
      setSelectedShiftIds(prev => [...new Set([...prev, ...idsInView])]);
    }
  }, [getAllShiftIdsInCurrentView, isAllSelectedInView]);


  // --- Enhanced Copy Logic ---
  const handleOpenCopyModal = () => {
    if (!isAnyShiftSelected) return;
    setIsCopyModalOpen(true);
  };

  const handleCloseCopyModal = () => {
    setIsCopyModalOpen(false);
  };

  const handleConfirmCopyToWeeks = useCallback(async (targetWeekStartDates) => {
    if (!isAnyShiftSelected || targetWeekStartDates.length === 0) return;

    const shiftsToCopyDetails = shifts.filter(shift => selectedShiftIds.includes(shift.id));
    let newCopiedShifts = [];

    targetWeekStartDates.forEach(targetWeekStartDate => {
      shiftsToCopyDetails.forEach(shift => {
        const originalShiftDate = parseISO(shift.day);
        if (!isValid(originalShiftDate)) return;

        const startOfOriginalWeek = startOfWeek(originalShiftDate, { weekStartsOn: WEEK_STARTS_ON });

        // Calculate the day index within its week (0 for Monday, 1 for Tuesday, etc.)
        // getDay returns 0 for Sunday, 1 for Monday .. 6 for Saturday. Adjust if WEEK_STARTS_ON is different.
        // For WEEK_STARTS_ON = 1 (Monday):
        // Monday: getDay = 1 -> index = 0
        // Sunday: getDay = 0 -> index = 6
        let dayIndexOfWeek = getDay(originalShiftDate);
        if (WEEK_STARTS_ON === 1) { // Monday is start of week
          dayIndexOfWeek = (dayIndexOfWeek === 0) ? 6 : dayIndexOfWeek - 1;
        }
        // else if WEEK_STARTS_ON === 0 (Sunday is start of week), dayIndexOfWeek is already correct.

        const newShiftDate = addDays(targetWeekStartDate, dayIndexOfWeek);

        newCopiedShifts.push({
          ...shift,
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${newCopiedShifts.length}`, // Ensure unique ID
          day: format(newShiftDate, 'yyyy-MM-dd'),
        });
      });
    });

    // Simulate API delay for demo
    await new Promise(resolve => setTimeout(resolve, 500));

    setShifts(prevShifts => [...prevShifts, ...newCopiedShifts]);
    setSelectedShiftIds([]); // Deselect all after copying
    // No need to close modal here, onConfirmCopy in modal does it.
  }, [selectedShiftIds, shifts, isAnyShiftSelected]);


  const dynamicStickyOffset = isAnyShiftSelected ? BULK_ACTIONS_BAR_HEIGHT : 0;
  const PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER = 8 + 8 + 8; // px + mt + py for Box containing Paper
  const FIXED_FOOTER_RESERVED_SPACE = 70; // Approximate height for the fixed footer
  const paperContentMaxHeight = `calc(100vh - ${TOP_BAR_HEIGHT}px - ${dynamicStickyOffset}px - ${INFO_BANNERS_TOTAL_HEIGHT}px - ${PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER}px - ${FIXED_FOOTER_RESERVED_SPACE}px)`;


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
            onOpenCopyModal={handleOpenCopyModal} // MODIFIED
            onDeselectAll={handleDeselectAll}
          />
          <Box sx={{px: {xs: 0, sm:1, md:2}, py:1}}> {/* Added py:1 for consistent spacing */}
            <InfoBanners currentDate={currentDate} stickyTopOffset={dynamicStickyOffset} />
            <Paper
              elevation={2}
              sx={{
                mt:1,
                overflowY: 'auto',
                maxHeight: paperContentMaxHeight,
              }}
            >
              <CalendarHeader
                currentDate={currentDate}
                stickyTopOffset={dynamicStickyOffset}
                onToggleSelectAll={handleToggleSelectAllInView}
                isAllSelectedInView={isAllSelectedInView}
                isIndeterminateInView={isIndeterminateInView}
              />
              <Box sx={{ overflowX: 'auto' }}> {/* Ensures horizontal scroll for content if needed */}
                <Box sx={{ minWidth: 1100 }}> {/* Minimum width for the grid content */}
                  <ShiftsGrid
                    currentDate={currentDate}
                    shifts={shifts}
                    users={users}
                    onAddShift={handleOpenModal}
                    onDeleteShift={handleDeleteSingleShift}
                    onEditShift={(shift) => handleOpenModal(shift.userId, parseISO(shift.day), shift)}
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
          <CopyShiftsModal
            isOpen={isCopyModalOpen}
            onClose={handleCloseCopyModal}
            onConfirmCopy={handleConfirmCopyToWeeks}
            currentDate={currentDate}
            numSelectedShifts={selectedShiftIds.length}
          />
        </Container>
      </DragDropContext>
    </LocalizationProvider>
  );
}

