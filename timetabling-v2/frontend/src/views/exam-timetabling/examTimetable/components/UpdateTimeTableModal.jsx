import { useState } from 'react';
import { useHistory } from 'react-router-dom';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  IconButton,
  CircularProgress
} from '@mui/material';
import { Close } from '@mui/icons-material';

const UpdateTimetableModal = ({ open, onClose, tiemtableId, onUpdateTimetable }) => {
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const history = useHistory();

  const handleChange = (e) => {
    setName(e.target.value);
    if (error) setError('');
  };

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError('Tên lịch thi không được để trống');
      return;
    }

    try {
      setIsSubmitting(true);
      
      // Call API to create timetable
      // const result = await onUpdateTimetable({ 
      //   name, 
      //   examTiemtableId: planId 
      // });

      const result = 1;
      
      setIsSubmitting(false);
      
      // Reset form and close modal
      setName('');
      onClose();
      
      // Redirect to timetable detail page
      if (result && result.id) {
        history.push(`/exam-time-tabling/exam-timetable/${tiemtableId}`);
      }
    } catch (error) {
      setIsSubmitting(false);
      setError('Có lỗi xảy ra khi đổi tên lịch thi');
      console.error('Error creating timetable:', error);
    }
  };

  const handleClose = () => {
    setName('');
    setError('');
    onClose();
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle 
        sx={{ 
          pb: 1,
          textAlign: 'center',
          borderBottom: '1px solid #e0e0e0',
          mb: 1,
          position: 'relative'
        }}
      >
        Đổi tên lịch thi
        <IconButton
          onClick={handleClose}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
            color: (theme) => theme.palette.grey[500],
          }}
        >
          <Close />
        </IconButton>
      </DialogTitle>
      
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Tên lịch thi"
          fullWidth
          value={name}
          onChange={handleChange}
          error={!!error}
          helperText={error}
          required
          sx={{ mt: 2 }}
        />
      </DialogContent>
      
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} variant="outlined" color="inherit">
          Hủy
        </Button>
        <Button 
          onClick={handleSubmit} 
          variant="contained" 
          color="primary"
          disabled={isSubmitting}
          startIcon={isSubmitting ? <CircularProgress size={20} /> : null}
        >
          {isSubmitting ? 'Đang tạo...' : 'Cập nhật lịch thi'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default UpdateTimetableModal;
