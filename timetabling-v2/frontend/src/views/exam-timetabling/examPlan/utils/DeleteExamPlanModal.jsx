import React from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Typography,
  Box,
  CircularProgress
} from '@mui/material';
import { Warning, DeleteForever } from '@mui/icons-material';

const DeleteConfirmModal = ({ open, onClose, onConfirm, planName, isDeleting }) => {
  return (
    <Dialog
      open={open}
      onClose={isDeleting ? null : onClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 2,
          overflow: 'hidden'
        }
      }}
    >
      <DialogTitle
        sx={{ 
          background: 'linear-gradient(90deg, #d32f2f, #f44336)',
          color: 'white',
          py: 2
        }}
      >
        Xác nhận xóa kế hoạch
      </DialogTitle>
      
      <DialogContent sx={{ pt: 3, pb: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Warning color="error" sx={{ fontSize: 40, mr: 2 }} />
          <Typography variant="h6">
            Bạn có chắc chắn muốn xóa kế hoạch này?
          </Typography>
        </Box>
        
        <Typography variant="body1" sx={{ ml: 7 }}>
          Kế hoạch "<strong>{planName}</strong>" và tất cả lịch thi thuộc kế hoạch này sẽ bị xóa vĩnh viễn. Hành động này không thể hoàn tác.
        </Typography>
      </DialogContent>
      
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button 
          onClick={onClose} 
          variant="outlined" 
          color="inherit"
          disabled={isDeleting}
          sx={{ borderRadius: 2 }}
        >
          Hủy
        </Button>
        <Button 
          onClick={onConfirm} 
          variant="contained" 
          color="error"
          disabled={isDeleting}
          startIcon={isDeleting ? <CircularProgress size={20} color="inherit" /> : <DeleteForever />}
          sx={{ 
            borderRadius: 2,
            bgcolor: 'error.main',
            '&:hover': { bgcolor: '#b71c1c' }
          }}
        >
          {isDeleting ? 'Đang xóa...' : 'Xóa kế hoạch'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteConfirmModal;
