import React, {useMemo, useState} from 'react';
import {
  Avatar,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography
} from '@mui/material';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import CalendarViewWeekIcon from '@mui/icons-material/CalendarViewWeek';

const MAX_INITIAL_CONFLICTS_DISPLAY = 3;

const OvertimeIcon = ({ type }) => {
  if (type === 'DailyOvertime') {
    return <CalendarTodayIcon color="warning" sx={{ fontSize: '1.25rem' }} />;
  }
  if (type === 'WeeklyOvertime') {
    return <CalendarViewWeekIcon color="warning" sx={{ fontSize: '1.25rem' }} />;
  }
  return <WarningAmberIcon color="warning" sx={{ fontSize: '1.25rem' }} />;
};


const SchedulingConflictModal = ({
                                   open,
                                   onClose,
                                   onConfirm,
                                   conflicts = [],
                                 }) => {
  const [showAllTimeOverlaps, setShowAllTimeOverlaps] = useState(false);
  const [showAllOvertimes, setShowAllOvertimes] = useState(false);

  const handleToggleShowAllOverlaps = () => {
    setShowAllTimeOverlaps(prev => !prev);
  };
  const handleToggleShowAllOvertimes = () => {
    setShowAllOvertimes(prev => !prev);
  };

  const { timeOverlapConflicts, overtimeConflicts } = useMemo(() => {
    const timeOverlapConflicts = conflicts.filter(c => c.type === 'TimeOverlap' || c.type === 'TimeOffOverlap');
    const overtimeConflicts = conflicts.filter(c => c.type === 'DailyOvertime' || c.type === 'WeeklyOvertime');
    return { timeOverlapConflicts, overtimeConflicts };
  }, [conflicts]);


  const visibleTimeOverlaps = showAllTimeOverlaps ? timeOverlapConflicts : timeOverlapConflicts.slice(0, MAX_INITIAL_CONFLICTS_DISPLAY);
  const visibleOvertimes = showAllOvertimes ? overtimeConflicts : overtimeConflicts.slice(0, MAX_INITIAL_CONFLICTS_DISPLAY);

  if (!conflicts || conflicts.length === 0) {
    return null;
  }

  const totalConflicts = timeOverlapConflicts.length + overtimeConflicts.length;

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <Box sx={{ p: 2, borderTop: `4px solid #ed6c02` }}>
        <DialogTitle sx={{ p: 0, mb: 1, display: 'flex', alignItems: 'center' }}>
          <WarningAmberIcon color="warning" sx={{ mr: 1, fontSize: '2rem' }} />
          <Typography variant="h6" component="span">
            {totalConflicts} {totalConflicts > 1 ? 'Cảnh báo' : 'Cảnh báo'} được tìm thấy
          </Typography>
        </DialogTitle>
        <DialogContent sx={{ p: 0, mb: 2 }}>
          {timeOverlapConflicts.length > 0 && (
            <>
              <Typography variant="subtitle2" gutterBottom sx={{color: 'text.secondary', mt: 1}}>
                Xung đột trùng lịch:
              </Typography>
              <List dense sx={{ maxHeight: 200, overflowY: 'auto', bgcolor: 'background.paper', borderRadius: 1, border: '1px solid', borderColor: 'divider' }}>
                {visibleTimeOverlaps.map((conflict, index) => (
                  <ListItem key={`overlap-${index}`} divider={index < visibleTimeOverlaps.length - 1} sx={{alignItems: 'flex-start'}}>
                    <ListItemIcon sx={{minWidth: 30, mt: 0.5}}>
                      <Avatar sx={{ width: 20, height: 20, fontSize: '0.7rem', bgcolor: 'error.dark' }}>
                        {index + 1}
                      </Avatar>
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Typography variant="body2" sx={{color: 'error.main', fontWeight: 'medium'}}>
                          {conflict.message || `Trùng lịch với ca từ ${conflict.existingShiftTime}`}
                        </Typography>
                      }
                    />
                  </ListItem>
                ))}
              </List>
              {timeOverlapConflicts.length > MAX_INITIAL_CONFLICTS_DISPLAY && (
                <Box textAlign="center" mt={1}>
                  <Button
                    size="small"
                    onClick={handleToggleShowAllOverlaps}
                    endIcon={showAllTimeOverlaps ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                  >
                    {showAllTimeOverlaps ? `Ẩn bớt` : `Xem tất cả ${timeOverlapConflicts.length} xung đột`}
                  </Button>
                </Box>
              )}
            </>
          )}

          {overtimeConflicts.length > 0 && (
            <>
              <Typography variant="subtitle2" gutterBottom sx={{color: 'text.secondary', mt: timeOverlapConflicts.length > 0 ? 2 : 1}}>
                Cảnh báo làm thêm giờ:
              </Typography>
              <List dense sx={{ overflowY: 'auto', bgcolor: 'background.paper', borderRadius: 1, border: '1px solid', borderColor: 'divider' }}>
                {visibleOvertimes.map((conflict, index) => (
                  <ListItem key={`overtime-${index}`} divider={index < visibleOvertimes.length - 1} sx={{alignItems: 'flex-start'}}>
                    <ListItemIcon sx={{minWidth: 30, mt: 0.5}}>
                      <OvertimeIcon type={conflict.type} />
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Typography variant="body2" sx={{color: 'warning.dark', fontWeight: 'medium'}}>
                          <Typography component="span" variant="body2" sx={{ fontWeight: 'bold', mr: 0.5 }}>
                            {conflict.type === 'DailyOvertime' ? `Ngày ${conflict.day}:` : (conflict.type === 'WeeklyOvertime' ? 'Tuần này:' : '')}
                          </Typography>
                          {conflict.message}
                        </Typography>
                      }
                    />
                  </ListItem>
                ))}
              </List>
              {overtimeConflicts.length > MAX_INITIAL_CONFLICTS_DISPLAY && (
                <Box textAlign="center" mt={1}>
                  <Button
                    size="small"
                    onClick={handleToggleShowAllOvertimes}
                    endIcon={showAllOvertimes ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                  >
                    {showAllOvertimes ? `Ẩn bớt` : `Xem tất cả ${overtimeConflicts.length} cảnh báo`}
                  </Button>
                </Box>
              )}
            </>
          )}

        </DialogContent>
        <DialogActions sx={{ p: '0px', display: 'flex', flexDirection: 'column', gap: 1 }}>
          <Button
            onClick={onClose}
            variant="outlined"
            color="inherit"
            fullWidth
            sx={{borderColor: 'grey.400', color: 'text.primary'}}
          >
            Hủy, quay lại và thay đổi
          </Button>
          <Button
            onClick={onConfirm}
            variant="contained"
            color="warning"
            fullWidth
          >
            Tiếp tục, vẫn tạo/thay đổi ca này
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
};

export default SchedulingConflictModal;