import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Divider,
} from "@mui/material";
import { Icon } from "@iconify/react";
import PropTypes from "prop-types";

const ConfirmationDialog = ({
  open,
  onClose,
  onConfirm,
  title,
  content,
  confirmText = "Xác nhận",
  cancelText = "Hủy",
  variant = "error",
}) => {
  const colorSchemes = {
    error: {
      iconColor: "error.main",
      backgroundColor: "error.background",
      buttonColor: "error.dark",
      buttonHoverColor: "error.dark",
    },
    warning: {
      iconColor: "warning.main",
      backgroundColor: "warning.background",
      buttonColor: "warning.dark",
      buttonHoverColor: "warning.dark",
    },
  };

  const scheme = colorSchemes[variant] || colorSchemes.error;

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: scheme.iconColor,
              backgroundColor: scheme.backgroundColor,
              padding: 3,
              borderRadius: "50%",
            }}
          >
            <Icon icon={"ion:warning-outline"} fontSize={28} />
          </Box>
          <Typography variant="body1">{content}</Typography>
        </Box>
      </DialogContent>
      <Divider sx={{ width: "90%", mx: "auto" }} />
      <DialogActions sx={{ justifyContent: "flex-end", pr: 7 }}>
        <Button
          onClick={onClose}
          variant="outlined"
          sx={{
            color: "grey.700",
            borderColor: "grey.300",
            textTransform: "none",
            "&:hover": {
              borderColor: "grey.300",
              backgroundColor: "grey.300",
            },
          }}
        >
          {cancelText}
        </Button>
        <Button
          onClick={onConfirm}
          variant="contained"
          sx={{
            color: "white",
            textTransform: "none",
            backgroundColor: scheme.buttonColor,
            "&:hover": {
              backgroundColor: scheme.buttonHoverColor,
            },
          }}
        >
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

ConfirmationDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  content: PropTypes.oneOfType([PropTypes.string, PropTypes.node]).isRequired,
  confirmText: PropTypes.string,
  cancelText: PropTypes.string,
  variant: PropTypes.oneOf(["error", "warning"]),
};

export default ConfirmationDialog;
