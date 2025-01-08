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
  confirmText = "Delete",
  cancelText = "Cancel",
}) => {
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
              backgroundColor: "rgba(255, 0, 0, 0.1)",
              padding: 3,
              borderRadius: "50%",
            }}
          >
            <Icon
              icon="ion:warning-outline"
              fontSize={24}
              style={{ color: "#FF0000" }}
            />
          </Box>
          <Typography variant="body1">{content}</Typography>
        </Box>
      </DialogContent>
      <Divider />
      <DialogActions sx={{ justifyContent: "flex-end", pr: 7 }}>
        <Button
          onClick={onClose}
          variant="outlined"
          sx={{
            color: "#696969",
            borderColor: "#D3D3D3",
            textTransform: "none",
            "&:hover": {
              borderColor: "#B0B0B0",
              backgroundColor: "rgba(211, 211, 211, 0.5)",
            },
          }}
        >
          {cancelText}
        </Button>
        <Button
          onClick={onConfirm}
          variant="contained"
          sx={{
            color: "rgba(255, 255, 255, 0.9)",
            textTransform: "none",
            backgroundColor: "#DC143C",
            "&:hover": {
              backgroundColor: "#DC143C",
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
};

export default ConfirmationDialog;
