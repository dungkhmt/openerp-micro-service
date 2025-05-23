import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  IconButton,
  Divider // For visual separation
} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import WarningAmberRoundedIcon from '@mui/icons-material/WarningAmberRounded'; // Or a similar appropriate icon

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
      maxWidth="xs" // Keeps the dialog compact
      PaperProps={{
        sx: {
          borderRadius: 2, // Consistent border radius
          border: '1px solid', // Border consistent with Paper in ShiftManager
          borderColor: 'rgba(0,0,0,0.12)', // A common divider/border color
          boxShadow: '0px 4px 12px rgba(0,0,0,0.05)', // Consistent shadow if used elsewhere
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
            sx={{ color: 'text.secondary' }} // Standard icon color
          >
            <CloseIcon />
          </IconButton>
        </Box>
      </DialogTitle>

      <Divider /> {/* Visual separator */}

      <DialogContent sx={{ p: { xs: 2, sm: 2.5 }, textAlign: 'center' }}> {/* Centered content */}
        <Typography variant="body1" color="text.secondary" sx={{ lineHeight: 1.6 }}>
          {info}
        </Typography>
      </DialogContent>

      {/* <Divider /> Optional: another divider before actions */}

      <DialogActions
        sx={{
          justifyContent: "center", // Center buttons for a balanced look
          p: 2, // Consistent padding
          // backgroundColor: 'grey.50', // Optional: light background for action area
          gap: 1.5, // Space between buttons
        }}
      >
        <Button
          onClick={onClose}
          variant="outlined" // Subtle cancel button
          color="inherit" // Neutral color
          sx={{
            borderRadius: 1.5, // Consistent button border radius
            borderColor: 'grey.400',
            color: 'text.primary', // Or text.secondary
            minWidth: '100px'
          }}
        >
          {cancelLabel}
        </Button>
        <Button
          onClick={onSubmit}
          variant="contained"
          color="error" // Clearly indicates a destructive action
          disableElevation
          sx={{
            borderRadius: 1.5, // Consistent button border radius
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
