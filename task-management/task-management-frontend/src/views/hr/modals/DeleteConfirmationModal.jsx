import React from "react";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  Typography
} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import WarningAmberRoundedIcon from '@mui/icons-material/WarningAmberRounded';

const DeleteConfirmationModal = ({
                                   open,
                                   onClose,
                                   onSubmit,
                                   title = "Xác Nhận Xóa Mục", // Default title
                                   info = "Bạn có chắc chắn muốn xóa mục này không? Hành động này không thể hoàn tác và dữ liệu sẽ bị mất vĩnh viễn.", // Default info
                                   cancelLabel = "Hủy Bỏ",
                                   confirmLabel = "Xác Nhận Xóa",
                                 }) => {
  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="xs"
      PaperProps={{
        sx: {
          borderRadius: 2,
          border: '1px solid',
          borderColor: 'rgba(0,0,0,0.12)',
          boxShadow: '0px 4px 12px rgba(0,0,0,0.05)',
        }
      }}
    >
      <DialogTitle sx={{ p: 2, pb: 1.5 }}> {/* Consistent padding */}
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <WarningAmberRoundedIcon sx={{ mr: 1.5, color: 'warning.main', fontSize: '2rem' }} />
            <Typography variant="h6" component="div" sx={{ color: '#f42222', fontSize: '1.1rem', fontWeight: 600 }}>
              {title}
            </Typography>
          </Box>
          <IconButton
            aria-label="close"
            onClick={onClose}
            sx={{ color: 'text.secondary' }}
          >
            <CloseIcon />
          </IconButton>
        </Box>
      </DialogTitle>

      <Divider />

      <DialogContent sx={{ p: { xs: 2, sm: 2.5 }, textAlign: 'center' }}>
        <Typography variant="body1" color="text.secondary" sx={{ lineHeight: 1.6 }}>
          {info}
        </Typography>
      </DialogContent>


      <DialogActions
        sx={{
          justifyContent: "center",
          p: 2,

          gap: 1.5,
        }}
      >
        <Button
          onClick={onClose}
          variant="outlined"
          color="inherit"
          sx={{
            borderRadius: 1.5,
            borderColor: 'grey.400',
            color: 'text.primary',
            minWidth: '100px'
          }}
        >
          {cancelLabel}
        </Button>
        <Button
          onClick={onSubmit}
          variant="contained"
          color="error"
          disableElevation
          sx={{
            borderRadius: 1.5,
            fontWeight: 500,
            minWidth: '100px'
          }}
        >
          {confirmLabel}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteConfirmationModal;
