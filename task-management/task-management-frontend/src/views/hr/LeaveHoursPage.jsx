import React, { useState } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  MenuItem,
  Paper,
  CircularProgress,
} from "@mui/material";
import { request } from "@/api";
import toast from "react-hot-toast";

const LeavePolicyPage = () => {
  const [leaveHours, setLeaveHours] = useState("");
  const [updateType, setUpdateType] = useState("ADDITIONAL");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async () => {
    if (!leaveHours) {
      setError("Vui lòng nhập số giờ phép");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    const payload = {
      leave_hours: parseFloat(leaveHours),
      update_type: updateType,
    };

    try {
      await request("patch", "/leave_hours", (res) => {
        toast.success("Cập nhật giờ phép thành công!");
        setLoading(false);
      },
      {
        onError: () => toast.error("Cập nhật giờ phép thất bại!")
      },
        payload);
    } catch {
      setError("Có lỗi xảy ra. Vui lòng thử lại.");
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h5" gutterBottom>
        Cập nhật giờ phép
      </Typography>

      <Paper sx={{ p: 3, maxWidth: 400 }}>
        {error && <Typography color="error" sx={{ mb: 2 }}>{error}</Typography>}
        {success && <Typography color="success.main" sx={{ mb: 2 }}>{success}</Typography>}

        <TextField
          label="Số giờ phép"
          value={leaveHours}
          onChange={(e) => setLeaveHours(e.target.value)}
          type="number"
          inputProps={{ step: "0.1" }}
          fullWidth
          sx={{ mb: 2 }}
        />

        <TextField
          label="Kiểu cập nhật"
          value={updateType}
          onChange={(e) => setUpdateType(e.target.value)}
          select
          fullWidth
          sx={{ mb: 3 }}
        >
          <MenuItem value="ADDITIONAL">Thêm vào</MenuItem>
          <MenuItem value="RESET_TO">Đặt lại bằng</MenuItem>
          <MenuItem value="SUBTRACT">Trừ đi</MenuItem>
        </TextField>

        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={loading}
          fullWidth
        >
          {loading ? <CircularProgress size={24} /> : "Cập nhật"}
        </Button>
      </Paper>
    </Box>
  );
};

export default LeavePolicyPage;
