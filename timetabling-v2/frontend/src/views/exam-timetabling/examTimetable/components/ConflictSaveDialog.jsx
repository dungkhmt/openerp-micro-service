import React from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Typography
} from '@mui/material';
import { Error } from '@mui/icons-material';

/**
 * Dialog component for showing timetable assignment conflicts
 */
const ConflictDialog = ({ 
  open, 
  conflicts, 
  onClose,
  onContinue
}) => {
  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle sx={{ 
        backgroundColor: '#ffebee',
        display: 'flex',
        alignItems: 'center'
      }}>
        <Error color="error" sx={{ mr: 1 }} />
        Phát hiện xung đột
      </DialogTitle>
      <DialogContent sx={{ py: 2 }}>
        <Typography variant="body1" sx={{ mb: 2 }}>
          Các lớp học sau đây có xung đột lịch thi (cùng phòng, cùng thời gian):
          {conflicts.length > 10 && (
            <Typography component="span" color="error.main" fontWeight={500}>
              {` (${conflicts.length} xung đột)`}
            </Typography>
          )}
        </Typography>
        
        {/* Add fixed height and scroll for many conflicts */}
        <Box sx={{ 
          height: conflicts.length > 5 ? '300px' : 'auto', // Fixed height for many conflicts
          maxHeight: '50vh',  // Limit to half the viewport height
          overflowY: 'auto',  // Enable vertical scrolling
          pr: 1  // Add some padding for scrollbar
        }}>
          {conflicts.map((conflict, index) => (
            <Box 
              key={index} 
              sx={{ 
                mb: 2, 
                p: 2, 
                border: '1px solid #ffccbc', 
                borderRadius: 1,
                backgroundColor: '#fff8e1'
              }}
            >
              <Typography variant="subtitle2" sx={{ 
                fontWeight: 600,
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <span>Xung đột {index + 1} / {conflicts.length}</span>
                <span style={{ fontSize: '0.9em', fontWeight: 400 }}>
                  {conflict.class1.examClassId} & {conflict.class2.examClassId}
                </span>
              </Typography>
              <Box sx={{ ml: 2, mt: 1 }}>
                <Typography variant="body2">
                  • Lớp 1: {conflict.class1.examClassId} - {conflict.class1.className}
                </Typography>
                <Typography variant="body2">
                  • Lớp 2: {conflict.class2.examClassId} - {conflict.class2.className}
                </Typography>
                <Typography variant="body2" sx={{ mt: 1, fontStyle: 'italic' }}>
                  Phòng thi: {getDisplayName(conflict.class1.roomId, 'room')} | 
                  Thời gian: {formatTime(conflict.class1)}
                </Typography>
              </Box>
            </Box>
          ))}
        </Box>
        
        {conflicts.length > 10 && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 2, fontStyle: 'italic' }}>
            * Hiển thị {conflicts.length} xung đột. Cuộn để xem thêm.
          </Typography>
        )}
        
        <Typography variant="body1" sx={{ mt: 2, fontWeight: 500 }}>
          Bạn có muốn tiếp tục lưu dù có xung đột?
        </Typography>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button 
          onClick={onClose} 
          variant="outlined"
          color="inherit"
        >
          Hủy
        </Button>
        <Button 
          onClick={onContinue} 
          color="error" 
          variant="contained"
        >
          Lưu dù có xung đột
        </Button>
      </DialogActions>
    </Dialog>
  );
};

// Helper function to format time information
const formatTime = (classItem) => {
  // This would typically get the display names from your options data
  // Here's a simple placeholder implementation
  const weekName = classItem.weekId || '?';
  const dateName = classItem.dateId || '?';
  const slotName = classItem.slotId || '?';
  
  return `${weekName}, ${dateName}, ${slotName}`;
};

// Helper function to get display names for rooms, etc.
const getDisplayName = (id, type) => {
  // This would typically look up names from your options
  // Here's a placeholder implementation
  return id || '?';
};

export default ConflictDialog;
