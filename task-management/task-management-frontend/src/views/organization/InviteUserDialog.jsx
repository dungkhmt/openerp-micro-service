import { useState } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  MenuItem,
  Button,
  IconButton,
  Box,
  Typography,
} from "@mui/material";
import { Icon } from "@iconify/react";
import { ROLE_IDS, ROLE_LIST } from "../../constants/roles";
import { isValidEmail } from "../../utils/stringUtils.js";

export default function InviteUserDialog({ open, onClose, onSend }) {
  const [invites, setInvites] = useState([
    { email: "", roleId: ROLE_IDS.member },
  ]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState({});

  const handleClose = () => {
    onClose?.();
    resetForm();
  };

  const resetForm = () => {
    setInvites([{ email: "", roleId: ROLE_IDS.member }]);
    setErrors({});
    setIsSubmitting(false);
  };

  const handleAdd = () => {
    setInvites([...invites, { email: "", roleId: ROLE_IDS.member }]);
    setErrors({});
  };

  const handleRemove = (index) => {
    const newInvites = invites.filter((_, i) => i !== index);
    setInvites(newInvites);

    const newErrors = { ...errors };
    delete newErrors[index];

    // Re-index errors
    const updatedErrors = {};
    Object.entries(newErrors).forEach(([key, value]) => {
      const newIndex = key > index ? key - 1 : key;
      updatedErrors[newIndex] = value;
    });
    setErrors(updatedErrors);
  };

  const handleChange = (index, field, value) => {
    const updated = [...invites];
    updated[index][field] = value;
    setInvites(updated);

    if (errors[index]) {
      const newErrors = { ...errors };
      delete newErrors[index];
      setErrors(newErrors);
    }
  };

  const validateInvites = () => {
    const newErrors = {};
    invites.forEach((invite, i) => {
      if (!invite.email.trim()) {
        newErrors[i] = "Email không được để trống";
      } else if (!isValidEmail(invite.email)) {
        newErrors[i] = "Email không hợp lệ";
      }
    });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateInvites()) return;
    setIsSubmitting(true);
    await onSend(invites);
    resetForm();
    setIsSubmitting(false);
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Mời mọi người vào tổ chức</DialogTitle>
      <DialogContent>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Mời người khác cùng tham gia tổ chức của bạn.
        </Typography>

        {invites.map((invite, index) => (
          <Box key={index} sx={{ mb: 2 }}>
            <Box
              sx={{
                display: "flex",
                alignItems: "flex-start",
                gap: 2,
              }}
            >
              <Box sx={{ flex: 1 }}>
                <TextField
                  type="email"
                  placeholder="name@company.com"
                  fullWidth
                  value={invite.email}
                  onChange={(e) => handleChange(index, "email", e.target.value)}
                  error={Boolean(errors[index])}
                />
              </Box>
              <TextField
                select
                value={invite.roleId}
                onChange={(e) => handleChange(index, "roleId", e.target.value)}
                sx={{ width: 150 }}
              >
                {ROLE_LIST.map((role) => (
                  <MenuItem key={role} value={role}>
                    {role.charAt(0).toUpperCase() + role.slice(1)}
                  </MenuItem>
                ))}
              </TextField>
              {invites.length > 1 && (
                <IconButton onClick={() => handleRemove(index)} sx={{ mt: 2 }}>
                  <Icon icon="iconoir:cancel" width={20} />
                </IconButton>
              )}
            </Box>

            {errors[index] && (
              <Typography
                variant="caption"
                color="error"
                sx={{ mt: 0.5, ml: 0.5 }}
              >
                {errors[index]}
              </Typography>
            )}
          </Box>
        ))}

        <Box sx={{ mb: 3 }}>
          <Button
            onClick={handleAdd}
            size="small"
            startIcon={<Icon icon="mdi:plus" width={18} />}
          >
            Thêm người
          </Button>
        </Box>

        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2 }}>
          <Button onClick={handleClose}>Hủy</Button>
          <Button
            variant="contained"
            onClick={handleSubmit}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Đang gửi..." : "Gửi lời mời"}
          </Button>
        </Box>
      </DialogContent>
    </Dialog>
  );
}

InviteUserDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSend: PropTypes.func.isRequired,
};
