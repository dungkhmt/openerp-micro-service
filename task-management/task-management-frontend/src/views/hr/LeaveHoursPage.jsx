import React, {useState, useEffect} from "react"; // Added useEffect
import {
  Box,
  Button,
  CircularProgress,
  MenuItem,
  Paper,
  TextField,
  Typography,
  ThemeProvider, // Added
  CssBaseline,    // Added
  Grid,           // Added
} from "@mui/material";
import {request}from "@/api";
import toast from "react-hot-toast";
import {theme} from "./theme"; // Assuming theme.js is in the same directory


// Renamed component to LeaveHoursPageInternal as per convention
const LeaveHoursPageInternal = () => {
  const [leaveHours, setLeaveHours] = useState("");
  const [updateType, setUpdateType] = useState("ADDITIONAL"); // Default to "ADDITIONAL"
  const [loading, setLoading] = useState(false);
  // const [error, setError] = useState(""); // Errors handled by toast
  // const [success, setSuccess] = useState(""); // Success handled by toast

  const handleSubmit = async () => {
    if (leaveHours === "" || isNaN(parseFloat(leaveHours))) { // Improved validation
      toast.error("Vui lòng nhập số giờ phép hợp lệ.");
      return;
    }
    if (parseFloat(leaveHours) < 0) {
      toast.error("Số giờ phép không được là số âm.");
      return;
    }


    setLoading(true);
    const payload = {
      leave_hours: parseFloat(leaveHours),
      update_type: updateType,
    };

    await request("patch", "/admin/leave-hours/bulk-update", // Updated endpoint
      (res) => {
        toast.success(res.data?.message || "Cập nhật giờ phép thành công!");
        setLeaveHours(""); // Clear input on success
      },
      {
        onError: (err) => {
          toast.error(err.response?.data?.message || err.message || "Cập nhật giờ phép thất bại!");
        }
      },
      payload
    ).finally(() => setLoading(false));
  };

  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Typography variant="h4" component="h1">
          Cập nhật Giờ Nghỉ Phép
        </Typography>
      </Paper>

      <Paper sx={{ p: {xs:1.5, md:2}, maxWidth: 500 }}> {/* Adjusted padding and maxWidth */}
        <Grid container spacing={2.5}> {/* Use Grid for better layout control */}
          <Grid item xs={12}>
            <TextField
              label="Số giờ phép"
              value={leaveHours}
              onChange={(e) => setLeaveHours(e.target.value)}
              type="number"
              inputProps={{ step: "0.1", min: "0" }}
              fullWidth
              size="small"
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              label="Kiểu cập nhật"
              value={updateType}
              onChange={(e) => setUpdateType(e.target.value)}
              select
              fullWidth
              size="small"
              helperText="Chọn cách thức cập nhật số giờ nghỉ phép cho tất cả nhân viên."
            >
              <MenuItem value="ADDITIONAL">Cộng thêm vào hiện tại</MenuItem>
              <MenuItem value="RESET_TO">Thiết lập lại bằng</MenuItem>
              <MenuItem value="SUBTRACT">Trừ bớt từ hiện tại</MenuItem>
            </TextField>
          </Grid>
          <Grid item xs={12}>
            <Button
              variant="contained"
              color="primary"
              onClick={handleSubmit}
              disabled={loading}
              fullWidth
            >
              {loading ? <CircularProgress size={24} color="inherit"/> : "Thực hiện cập nhật"}
            </Button>
          </Grid>
        </Grid>
      </Paper>
    </Box>
  );
};

// Wrapper component for ThemeProvider
const LeaveHoursPage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline/>
      <LeaveHoursPageInternal/>
    </ThemeProvider>
  )
}

export default LeaveHoursPage; // Exporting the wrapper