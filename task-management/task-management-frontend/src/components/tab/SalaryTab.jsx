import React, { useState, useEffect, useCallback } from "react";
import {
  TextField,
  Button,
  CircularProgress,
  // Snackbar, // Sẽ dùng react-hot-toast
  // Alert, // Sẽ dùng react-hot-toast
  Divider,
  Paper,
  Typography,
  Box,
  Grid,
  MenuItem,
  InputAdornment // Để hiển thị đơn vị tiền tệ
} from "@mui/material";
import { request } from "@/api";
import toast from "react-hot-toast"; // Sử dụng react-hot-toast

const SalaryTab = ({ userLoginId }) => {
  const [salaryData, setSalaryData] = useState({
    salary_type: "MONTHLY",
    salary: "",
  });
  const [initialSalaryData, setInitialSalaryData] = useState(null);
  const [loading, setLoading] = useState(true); // Đổi thành true để thể hiện đang fetch lần đầu
  const [saving, setSaving] = useState(false);

  const fetchSalary = useCallback(async () => {
    if (!userLoginId) {
      setLoading(false); // Không có userLoginId, không fetch
      return;
    }
    setLoading(true);
    try {
      await request(
        "get",
        `/salaries/${userLoginId}`, // Endpoint GET lương của nhân viên
        (res) => {
          if (res.data?.data) {
            const fetchedData = {
              salary_type: res.data.data.salary_type || "MONTHLY",
              salary: res.data.data.salary || "",
            };
            setSalaryData(fetchedData);
            setInitialSalaryData(fetchedData);
          } else {
            // Nếu không có dữ liệu, thiết lập giá trị mặc định
            const defaultData = { salary_type: "MONTHLY", salary: "" };
            setSalaryData(defaultData);
            setInitialSalaryData(defaultData);
            // Có thể thông báo cho người dùng biết là chưa có thông tin lương
            // toast.info("Nhân viên này hiện chưa có thông tin lương.");
          }
        },
        {
          onError: (err) => {
            console.error("Error fetching salary:", err);
            const defaultDataOnError = { salary_type: "MONTHLY", salary: "" };
            setSalaryData(defaultDataOnError);
            setInitialSalaryData(defaultDataOnError);
            toast.error(err.response?.data?.message || "Không thể tải thông tin lương của nhân viên.");
          },
        }
      );
    } catch (err) {
      console.error("Failed to fetch salary data:", err);
      const defaultDataOnErrorCatch = { salary_type: "MONTHLY", salary: "" };
      setSalaryData(defaultDataOnErrorCatch);
      setInitialSalaryData(defaultDataOnErrorCatch);
      toast.error("Lỗi không mong muốn khi tải thông tin lương.");
    } finally {
      setLoading(false);
    }
  }, [userLoginId]);

  useEffect(() => {
    fetchSalary();
  }, [fetchSalary]);


  const handleSave = async () => {
    setSaving(true);
    const salaryAmount = parseFloat(String(salaryData.salary).replace(/[^\d.-]/g, '')); // Chấp nhận số thập phân

    if (isNaN(salaryAmount) || salaryAmount < 0) {
      toast.error("Số tiền lương không hợp lệ. Vui lòng nhập số dương.");
      setSaving(false);
      return;
    }

    const payload = {
      salary_type: salaryData.salary_type,
      salary: salaryAmount,
    };

    try {
      await request(
        "put",
        `/salaries/${userLoginId}`,
        (res) => {
          toast.success(res.data?.message || "Lưu thông tin lương thành công.");
          // Cập nhật initialSalaryData sau khi lưu thành công để isFormDirty hoạt động đúng
          setInitialSalaryData({...payload, salary: String(payload.salary) });
          setSalaryData({...payload, salary: String(payload.salary) });

        },
        {
          onError: (err) => {
            toast.error(err.response?.data?.message || "Lỗi khi lưu thông tin lương.");
          },
        },
        payload
      );
    } catch (err) {
      console.error("Error while saving salary:", err);
      toast.error("Lỗi không mong muốn khi lưu thông tin lương.");
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setSalaryData((prev) => ({ ...prev, [name]: value }));
  };

  // Kiểm tra xem form có thay đổi so với dữ liệu ban đầu không
  const isFormDirty = JSON.stringify(salaryData) !== JSON.stringify(initialSalaryData);

  if (loading) {
    return (
      <Paper sx={{ p: {xs: 2, md:3}, mt: {xs:2, md:0}, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 150 }} variant="outlined">
        <CircularProgress size={24} />
        <Typography sx={{ml:1.5}} color="text.secondary">Đang tải thông tin lương...</Typography>
      </Paper>
    );
  }

  return (
    <Paper sx={{ p: {xs: 2, md:3}, mt: {xs:2, md:0} }} variant="outlined">
      <Typography variant="h6" gutterBottom sx={{fontWeight: 600}}>
        Thông tin Lương Cơ bản
      </Typography>
      <Divider sx={{ mb: 2.5 }} />
      <Grid container spacing={2.5}>
        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            select
            label="Loại lương"
            name="salary_type"
            value={salaryData.salary_type}
            onChange={handleChange}
            variant="outlined"
            size="small"
            helperText="Chọn loại hình nhận lương (theo tháng, tuần, giờ)."
          >
            <MenuItem value="MONTHLY">Theo Tháng</MenuItem>
            <MenuItem value="WEEKLY">Theo Tuần</MenuItem>
            <MenuItem value="HOURLY">Theo Giờ</MenuItem>
          </TextField>
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            type="text" // Để cho phép nhập dấu phẩy, xử lý bằng parseFloat khi submit
            label="Số tiền lương"
            name="salary"
            value={salaryData.salary === "" ? "" : Number(String(salaryData.salary).replace(/[^\d]/g, "")).toLocaleString("vi-VN")}
            onChange={(e) => {
              const rawValue = e.target.value.replace(/[^\d]/g, "");
              handleChange({ target: { name: "salary", value: rawValue } });
            }}
            variant="outlined"
            size="small"
            InputProps={{
              endAdornment: <InputAdornment position="end">VNĐ</InputAdornment>,
              inputProps: { min: 0 }
            }}
            helperText="Nhập mức lương cơ bản của nhân viên."
          />
        </Grid>
      </Grid>
      <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          variant="contained"
          color="primary"
          onClick={handleSave}
          disabled={saving || !isFormDirty || loading}
        >
          {saving ? <CircularProgress size={24} color="inherit"/> : "Lưu Thay Đổi"}
        </Button>
      </Box>
    </Paper>
  );
};

export default SalaryTab;
