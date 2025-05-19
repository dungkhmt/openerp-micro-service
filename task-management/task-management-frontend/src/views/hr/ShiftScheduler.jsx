import React, { useState, useEffect } from 'react';
import { startOfWeek, addDays, format, subDays, parseISO, isSameDay, isValid } from 'date-fns';
import vi from 'date-fns/locale/vi';

// MUI Core Components
import {
  AppBar, Toolbar, Typography, Button, IconButton, Container, Grid, Paper, Box,
  Modal, TextField, Select, MenuItem, FormControl, InputLabel, Checkbox, Avatar
} from '@mui/material';
import { LocalizationProvider, DatePicker, TimePicker } from '@mui/x-date-pickers'; // For date/time pickers

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


// Dữ liệu mẫu (Mock Data)
const initialShifts = [
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '09:00', endTime: '17:15', duration: '8h 15m', details: 'Backend task', subDetails: 'API development', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '09:00', endTime: '17:15', duration: '8h 15m', details: 'Frontend task', subDetails: 'UI implementation', muiColor: 'error.light', muiTextColor: 'error.darkerText' },
  { id: 's3', userId: 'u1', day: '2025-05-21', startTime: '10:00', endTime: '18:30', duration: '8h 30m', details: 'Meeting', subDetails: 'Client discussion', muiColor: 'info.light', muiTextColor: 'info.darkerText' },
];

const initialUsers = [
  { id: 'u1', name: 'NV Ánh Nguyệt', summary: '8h 15m', avatarLetter: 'AN', avatarBgColor: 'secondary.main' },
  { id: 'u2', name: 'NV Bảo Long', summary: '8h 15m', avatarLetter: 'BL', avatarBgColor: 'primary.main' },
  { id: 'u3', name: 'NV Cẩm Tú', summary: '0h 0m', avatarLetter: 'CT', avatarBgColor: 'warning.main' },
  { id: 'uHPT', name: 'Hieu Phan Trung', summary: '0h 0m', avatarLetter: 'HT', avatarBgColor: 'success.main' },
  { id: 't4', name: 'Test User 4', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
];

// Hàm tiện ích lấy chữ cái đầu cho avatar
const getInitials = (name) => {
  if (!name) return '??';
  const parts = name.split(' ');
  if (parts.length === 1 && parts[0].length > 0) return parts[0].substring(0, 2).toUpperCase();
  if (parts.length > 1 && parts[0].length > 0 && parts[parts.length -1].length > 0) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }
  return name.substring(0,2).toUpperCase();
};


// Thành phần hiển thị một ca làm việc
function ShiftCard({ shift, onDeleteShift, onEditShift }) {
  return (
    <Paper
      elevation={2}
      sx={{
        p: 1,
        my: 0.5,
        bgcolor: shift.muiColor || 'grey.200',
        position: 'relative',
        '&:hover .edit-delete-buttons': { opacity: 1 }
      }}
    >
      <Typography variant="caption" component="div" sx={{ fontWeight: 'bold', color: shift.muiTextColor || 'text.primary' }}>
        {`${shift.startTime} - ${shift.endTime}`}
        <Typography variant="caption" sx={{ ml: 0.5, color: shift.muiTextColor, opacity: 0.8 }}>({shift.duration})</Typography>
      </Typography>
      <Typography variant="body2" sx={{ fontSize: '0.7rem', color: shift.muiTextColor }}>{shift.details}</Typography>
      {shift.subDetails && <Typography variant="caption" sx={{ fontSize: '0.65rem', color: shift.muiTextColor, opacity: 0.7 }}>{shift.subDetails}</Typography>}
      <Box className="edit-delete-buttons" sx={{position: 'absolute', top: 2, right: 2, opacity: 0, transition: 'opacity 0.2s'}}>
        <IconButton size="small" onClick={() => onEditShift(shift)} sx={{p:0.2, bgcolor: 'rgba(255,255,255,0.7)', mr: 0.2, '&:hover': {bgcolor: 'rgba(255,255,255,0.9)'}}}>
          <SettingsIcon sx={{fontSize: '0.8rem'}} color="primary"/>
        </IconButton>
        <IconButton size="small" onClick={() => onDeleteShift(shift.id)} sx={{p:0.2, bgcolor: 'rgba(255,255,255,0.7)', '&:hover': {bgcolor: 'rgba(255,255,255,0.9)'}}}>
          <DeleteForeverIcon sx={{fontSize: '0.8rem'}} color="error"/>
        </IconButton>
      </Box>
    </Paper>
  );
}

// Thành phần hiển thị ô trống để thêm ca
function EmptyShiftSlot({ onAdd }) {
  return (
    <Paper
      variant="outlined"
      sx={{
        height: 52,
        m: 0.5,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        borderColor: 'grey.400',
        borderStyle: 'dashed',
        cursor: 'pointer',
        '&:hover': { bgcolor: 'grey.100', borderColor: 'grey.500' }
      }}
      onClick={onAdd}
      title="Thêm ca làm việc mới"
    >
      <AddIcon color="action" />
    </Paper>
  );
}

// Thành phần tiêu đề lịch
function CalendarHeader({ currentDate }) {
  const weekStartsOn = 1;
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Grid container sx={{ bgcolor: 'grey.100', borderBottom: 1, borderColor: 'divider', position: 'sticky', top: 101, zIndex: 10 }}>
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

// Lưới hiển thị ca làm việc chính
function ShiftsGrid({ currentDate, shifts, users, onAddShift, onDeleteShift, onEditShift }) {
  const weekStartsOn = 1;
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Box>
      {users.map(user => (
        <Grid container key={user.id} sx={{ borderBottom: 1, borderColor: 'divider', '&:hover': { bgcolor: 'action.hover' } }}>
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
            const shiftsForUserAndDay = shifts.filter(shift =>
              shift.userId === user.id && shift.day && isValid(parseISO(shift.day)) && isSameDay(parseISO(shift.day), day)
            );
            return (
              <Grid item xs key={day.toISOString()} sx={{ p: 0.5, borderRight: 1, borderColor: 'divider', '&:last-child': { borderRight: 0 }, minHeight: 60 }}>
                {shiftsForUserAndDay.length > 0 ? (
                  shiftsForUserAndDay.map(shift => <ShiftCard key={shift.id} shift={shift} onDeleteShift={onDeleteShift} onEditShift={onEditShift} />)
                ) : (
                  <EmptyShiftSlot onAdd={() => onAddShift(user.id, day)} />
                )}
              </Grid>
            );
          })}
        </Grid>
      ))}
    </Box>
  );
}

function TopBar({ currentDate, onPrevWeek, onNextWeek, onToday }) {
  return (
    <AppBar position="sticky" color="default" elevation={1} sx={{zIndex: 20, height: 61}}>
      <Toolbar variant="dense">
        <Typography variant="h6" component="div" sx={{ flexGrow: 0.1, mr:1, fontSize:'1.1rem', color:'text.primary' }}>
          Lịch làm việc
        </Typography>
        <IconButton onClick={onPrevWeek} size="small" aria-label="Tuần trước">
          <ChevronLeftIcon />
        </IconButton>
        <Button onClick={onToday} size="small" variant="outlined" color="inherit" startIcon={<EventNoteIcon />} sx={{ mx: 1, fontSize:'0.75rem', py:0.3}}>
          Hôm nay
        </Button>
        <IconButton onClick={onNextWeek} size="small" aria-label="Tuần sau">
          <ChevronRightIcon />
        </IconButton>
        <Typography variant="subtitle1" component="div" sx={{ ml: 2, color:'text.secondary', fontWeight:'medium' }}>
          {format(currentDate, 'MMMM yyyy', { locale: vi })}
        </Typography>
        <Box sx={{ flexGrow: 1 }} /> {/* Spacer */}
        <Button size="small" variant="text" color="inherit" startIcon={<FilterAltIcon />} sx={{textTransform:'none', fontSize:'0.8rem'}}>Lọc</Button>
        <Button size="small" variant="text" color="inherit" startIcon={<FileDownloadIcon />} sx={{textTransform:'none', fontSize:'0.8rem'}}>Xuất</Button>
        <IconButton color="inherit" size="small">
          <SettingsIcon />
        </IconButton>
      </Toolbar>
    </AppBar>
  );
}

function InfoBanners({ currentDate }) {
  const weekStartsOn = 1;
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Box sx={{position: 'sticky', top: 61, zIndex: 15}}> {/* Adjusted sticky top */}
      <Paper elevation={0} square sx={{ p: 1, bgcolor: 'green.50', borderBottom: 1, borderColor: 'divider', display: 'flex', alignItems: 'center' }}>
        <StarRateIcon fontSize="small" sx={{ mr: 1, color: 'orange.400' }} />
        <Typography variant="caption" sx={{ fontWeight: 'medium', color: 'green.800' }}>CA CÓ SẴN</Typography>
      </Paper>
      <Grid container sx={{ bgcolor: 'grey.200', borderBottom: 1, borderColor: 'divider' }}>
        <Grid item sx={{ width: 160, p: 1, borderRight: 1, borderColor: 'divider', display: 'flex', alignItems: 'center' }}>
          <Typography variant="caption" sx={{ fontWeight: 'medium', textTransform: 'uppercase', color: 'text.secondary' }}>Doanh số dự kiến</Typography>
        </Grid>
        {days.map(day => (
          <Grid item xs key={`sales-${day.toISOString()}`} sx={{ p: 1, textAlign: 'center', borderRight: 1, borderColor: 'divider', '&:last-child': { borderRight: 0 } }}>
            <Typography variant="caption" color="text.secondary">$0.00</Typography>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: { xs: '90%', sm: 500 }, // Responsive width
  bgcolor: 'background.paper',
  border: '1px solid #ccc',
  boxShadow: 24,
  p: 3,
  borderRadius: 1,
};

// Thành phần chính của ứng dụng
export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date(new Date().setHours(0,0,0,0))); // Start with today
  const [shifts, setShifts] = useState(initialShifts);
  const [users, setUsers] = useState(initialUsers);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentEditingShift, setCurrentEditingShift] = useState(null);
  const [formState, setFormState] = useState({ userId: '', day: format(new Date(), 'yyyy-MM-dd'), startTime: '09:00', endTime: '17:00', details: '', subDetails: '' });

  useEffect(() => {
    // Update user summary when shifts change (example logic)
    const updatedUsers = initialUsers.map(user => {
      const userShifts = shifts.filter(s => s.userId === user.id);
      let totalMs = 0;
      userShifts.forEach(s => {
        const start = parseISO(`${s.day}T${s.startTime}`);
        const end = parseISO(`${s.day}T${s.endTime}`);
        if(isValid(start) && isValid(end)) {
          let diff = end.getTime() - start.getTime();
          if (diff < 0) diff += 24 * 60 * 60 * 1000;
          totalMs += diff;
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
      setFormState({
        userId: shiftToEdit.userId,
        day: shiftToEdit.day,
        startTime: shiftToEdit.startTime,
        endTime: shiftToEdit.endTime,
        details: shiftToEdit.details,
        subDetails: shiftToEdit.subDetails || '',
      });
    } else {
      setCurrentEditingShift(null);
      setFormState({
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

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormState(prev => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (newDate) => {
    setFormState(prev => ({ ...prev, day: format(newDate, 'yyyy-MM-dd')}));
  };

  const handleTimeChange = (name, newTime) => {
    setFormState(prev => ({ ...prev, [name]: format(newTime, 'HH:mm')}));
  };


  const handleSaveShift = (e) => {
    e.preventDefault();
    const { userId, day, startTime, endTime, details, subDetails } = formState;

    if (!userId || !day || !startTime || !endTime || !details) {
      alert("Vui lòng điền đầy đủ các trường bắt buộc: Nhân viên, Ngày, Giờ bắt đầu, Giờ kết thúc, Chi tiết chính.");
      return;
    }

    const start = parseISO(`${day}T${startTime}`);
    const end = parseISO(`${day}T${endTime}`);

    if (!isValid(start) || !isValid(end)) {
      alert("Ngày hoặc giờ không hợp lệ. Vui lòng kiểm tra lại.");
      return;
    }

    let durationMs = end.getTime() - start.getTime();
    if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; }

    const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
    const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));

    const shiftData = {
      userId, day, startTime, endTime,
      duration: `${durationHours}h ${durationMinutes}m`,
      details, subDetails,
      muiColor: currentEditingShift ? currentEditingShift.muiColor : 'primary.light',
      muiTextColor: currentEditingShift ? currentEditingShift.muiTextColor : 'primary.darkerText',
    };

    if (currentEditingShift) {
      setShifts(prevShifts => prevShifts.map(s => s.id === currentEditingShift.id ? { ...s, ...shiftData } : s));
    } else {
      setShifts(prevShifts => [...prevShifts, { ...shiftData, id: `s${Date.now()}` }]);
    }
    handleCloseModal();
  };

  const handleDeleteShift = (shiftIdToDelete) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa ca làm việc này không?")) {
      setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
    }
  };

  return (
    <LocalizationProvider  adapterLocale={vi}>
      <Container maxWidth={false} disableGutters sx={{ bgcolor: 'grey.200', minHeight: '100vh' }}>
        <TopBar
          currentDate={currentDate}
          onPrevWeek={handlePrevWeek}
          onNextWeek={handleNextWeek}
          onToday={handleToday}
        />
        <Box sx={{px: {xs: 0, sm:1, md:2}, py:1}}> {/* Add some padding for content area */}
          <InfoBanners currentDate={currentDate} />
          <Paper elevation={2} sx={{ overflow: 'hidden', mt:1 }}> {/* Main grid container */}
            <CalendarHeader currentDate={currentDate} />
            <Box sx={{ overflowX: 'auto' }}>
              <Box sx={{ minWidth: 1100 }}> {/* Ensure content has min width */}
                <ShiftsGrid
                  currentDate={currentDate}
                  shifts={shifts}
                  users={users}
                  onAddShift={(userId, day) => handleOpenModal(userId, day)}
                  onDeleteShift={handleDeleteShift}
                  onEditShift={(shift) => handleOpenModal(shift.userId, parseISO(shift.day), shift)}
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

        <Modal
          open={isModalOpen}
          onClose={handleCloseModal}
          aria-labelledby="shift-modal-title"
          aria-describedby="shift-modal-description"
        >
          <Box sx={modalStyle}>
            <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2}}>
              <Typography id="shift-modal-title" variant="h6" component="h2">
                {currentEditingShift ? "Chỉnh sửa ca làm việc" : "Thêm ca làm việc mới"}
              </Typography>
              <IconButton onClick={handleCloseModal} size="small">
                <CloseIcon />
              </IconButton>
            </Box>
            <Box component="form" onSubmit={handleSaveShift} noValidate>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth margin="dense" required>
                    <InputLabel id="user-select-label" sx={{fontSize:'0.9rem'}}>Nhân viên</InputLabel>
                    <Select
                      labelId="user-select-label"
                      id="userId"
                      name="userId"
                      value={formState.userId}
                      label="Nhân viên"
                      onChange={handleFormChange}
                      size="small"
                    >
                      <MenuItem value="" disabled><em>Chọn nhân viên</em></MenuItem>
                      {users.map(user => (
                        <MenuItem key={user.id} value={user.id}>{user.name}</MenuItem>
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
                    margin="dense" required fullWidth
                    id="details" label="Chi tiết chính" name="details"
                    value={formState.details} onChange={handleFormChange}
                    size="small"
                    placeholder="Ví dụ: Họp team, Làm dự án X..."
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    margin="dense" fullWidth
                    id="subDetails" label="Chi tiết phụ (không bắt buộc)" name="subDetails"
                    value={formState.subDetails} onChange={handleFormChange}
                    size="small"
                    placeholder="Ví dụ: Chuẩn bị slide, Fix bug Y..."
                  />
                </Grid>
              </Grid>
              <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                <Button onClick={handleCloseModal} variant="outlined" color="inherit">Hủy</Button>
                <Button type="submit" variant="contained" color="primary">Lưu ca</Button>
              </Box>
            </Box>
          </Box>
        </Modal>
      </Container>
    </LocalizationProvider>
  );
}
