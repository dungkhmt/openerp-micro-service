// ==============
// SchedulingConflictModal.jsx
// ==============
import React, { useState } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, Typography, Box, List, ListItem, ListItemText,
  ListItemIcon, Avatar, Link, Collapse, IconButton
} from '@mui/material';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';

const MAX_INITIAL_CONFLICTS_DISPLAY = 3;

const SchedulingConflictModal = ({
                                   open,
                                   onClose,
                                   onConfirm,
                                   conflicts = [],
                                 }) => {
  const [showAll, setShowAll] = useState(false);

  const handleToggleShowAll = () => {
    setShowAll(prev => !prev);
  };

  const visibleConflicts = showAll ? conflicts : conflicts.slice(0, MAX_INITIAL_CONFLICTS_DISPLAY);

  if (!conflicts || conflicts.length === 0) {
    return null; // Or some fallback if modal is opened without conflicts (should not happen)
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <Box sx={{ p: 2, borderTop: `4px solid #ed6c02` /* orange.main */ }}>
        <DialogTitle sx={{ p: 0, mb: 1, display: 'flex', alignItems: 'center' }}>
          <WarningAmberIcon color="warning" sx={{ mr: 1, fontSize: '2rem' }} />
          <Typography variant="h6" component="span">
            {conflicts.length} {conflicts.length > 1 ? 'Xung đột' : 'Xung đột'} được tìm thấy
          </Typography>
        </DialogTitle>
        <DialogContent sx={{ p: 0, mb: 2 }}>
          {/* Could add a section for overtime warnings here if needed, like in images */}
          <Typography variant="subtitle2" gutterBottom sx={{color: 'text.secondary'}}>
            Xung đột lịch trình:
          </Typography>
          <List dense sx={{ maxHeight: 200, overflowY: 'auto', bgcolor: 'background.paper', borderRadius: 1, border: '1px solid', borderColor: 'divider' }}>
            {visibleConflicts.map((conflict, index) => (
              <ListItem key={index} divider sx={{alignItems: 'flex-start'}}>
                <ListItemIcon sx={{minWidth: 30, mt: 0.5}}>
                  <Avatar sx={{ width: 20, height: 20, fontSize: '0.7rem', bgcolor: 'grey.500' }}>
                    {index + 1}
                  </Avatar>
                </ListItemIcon>
                <ListItemText
                  primary={
                    <Typography variant="body2" sx={{color: 'error.main', fontWeight: 'medium'}}>
                      {conflict.message || `Trùng lịch với ca từ ${conflict.existingShiftTime}`}
                    </Typography>
                  }
                  // secondary={`Nhân viên: ${conflict.userName} - Ngày: ${conflict.day}`}
                />
              </ListItem>
            ))}
          </List>
          {conflicts.length > MAX_INITIAL_CONFLICTS_DISPLAY && (
            <Box textAlign="center" mt={1}>
              <Button
                size="small"
                onClick={handleToggleShowAll}
                endIcon={showAll ? <ExpandLessIcon /> : <ExpandMoreIcon />}
              >
                {showAll ? `Ẩn bớt` : `Xem tất cả ${conflicts.length} xung đột`}
              </Button>
            </Box>
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