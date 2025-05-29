import React, {useEffect, useState, useCallback} from "react";
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Grid,
  IconButton,
  Stack,
  MenuItem, // Thêm MenuItem
  FormControl, // Thêm FormControl
  InputLabel, // Thêm InputLabel
  Select, // Thêm Select
  InputAdornment // Thêm InputAdornment
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import Autocomplete from "@mui/material/Autocomplete";
import {request}from "@/api";
import toast from "react-hot-toast";

const AddStaffModal = ({ open, onClose, onSubmitSuccess, initialData, isEditMode, departments = [], jobPositions = [], titleProps }) => {
  const defaultFormState = {
    fullname: "",
    email: "",
    department_code: null,
    job_position_code: null,
    salary: "", // Thêm trường lương
    salary_type: "MONTHLY" // Thêm trường loại lương, mặc định là Theo Tháng
  };
  const [formValues, setFormValues] = useState(defaultFormState);

  const [currentDepartments, setCurrentDepartments] = useState([]);
  const [currentJobPositions, setCurrentJobPositions] = useState([]);

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (departments) setCurrentDepartments(departments);
  }, [departments]);

  useEffect(() => {
    if (jobPositions) setCurrentJobPositions(jobPositions);
  }, [jobPositions]);


  useEffect(() => {
    if (open) {
      if (isEditMode && initialData) {
        setFormValues({
          fullname: initialData.fullname || "",
          email: initialData.email || "",
          department_code: initialData.department?.department_code || null,
          job_position_code: initialData.job_position?.code || null,
          salary: "",
          salary_type: "MONTHLY"
        });
      } else {
        setFormValues(defaultFormState);
      }
    }
  }, [open, initialData, isEditMode, departments, jobPositions]);


  const handleSubmit = async () => {
    setLoading(true);
    if (!formValues.fullname.trim()) {
      toast.error("Họ và tên không được để trống."); setLoading(false); return;
    }
    if (!isEditMode && !formValues.email.trim()) {
      toast.error("Email không được để trống."); setLoading(false); return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (formValues.email.trim() && !emailRegex.test(formValues.email.trim())) {
      toast.error("Định dạng email không hợp lệ."); setLoading(false); return;
    }

    const payload = {
      fullname: formValues.fullname.trim(),
      email: formValues.email.trim(),
      department_code: formValues.department_code,
      job_position_code: formValues.job_position_code,
    };

    if (!isEditMode) {
      const salaryValue = String(formValues.salary).replace(/[^\d]/g, "");
      if (salaryValue !== "" && (isNaN(parseFloat(salaryValue)) || parseFloat(salaryValue) < 0)) {
        toast.error("Mức lương không hợp lệ. Vui lòng nhập số dương hoặc để trống.");
        setLoading(false);
        return;
      }
      if (salaryValue !== "") {
        payload.salary = parseFloat(salaryValue);
        payload.salary_type = formValues.salary_type;
      } else {
        // Nếu lương để trống, có thể không gửi hoặc gửi null/undefined tùy theo yêu cầu API
        payload.salary = null; // Ví dụ: gửi null nếu lương trống
        payload.salary_type = formValues.salary_type; // Vẫn gửi loại lương
      }
    }


    if (isEditMode) {
      if (payload.department_code === initialData?.department?.department_code) {
        delete payload.department_code;
      }
      if (payload.job_position_code === initialData?.job_position?.code) {
        delete payload.job_position_code;
      }
      // Đảm bảo không gửi salary và salary_type khi edit
      delete payload.salary;
      delete payload.salary_type;
    }


    try {
      const apiEndpoint = isEditMode
        ? `/staffs/${initialData?.staff_code}`
        : "/staffs";
      const methodURL = isEditMode ? "put" : "post";

      await request(
        methodURL,
        apiEndpoint,
        () => {
          toast.success(isEditMode ? "Cập nhật nhân viên thành công!" : "Thêm nhân viên thành công!");
          onSubmitSuccess();
          onClose();
        },
        {
          onError: (err) => {
            console.error("API Error:", err.response?.data || err.message);
            const errorMsg = err.response?.data?.meta?.message || err.response?.data?.message || "Thao tác thất bại.";
            toast.error(errorMsg);
          },
        },
        payload
      );
    } catch (error) {
      console.error("API request failed:", error);
      toast.error(`Đã xảy ra lỗi: ${error.message || "Vui lòng thử lại."}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 1.5 }}>
        {isEditMode ? "Chỉnh sửa Thông tin Nhân viên" : "Thêm Nhân viên Mới"}
        <IconButton aria-label="đóng" onClick={onClose} sx={{p:0.5}}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{pt: '12px !important'}}>
        <Stack spacing={2.5} sx={{mt:1}}>
          <TextField
            autoFocus
            fullWidth
            label="Họ và tên (*)"
            name="fullname"
            value={formValues.fullname}
            onChange={(e) => setFormValues((prev) => ({ ...prev, fullname: e.target.value })) }
            size="small"
          />
          <TextField
            fullWidth
            label={isEditMode ? "Email" : "Email (*)"}
            name="email"
            value={formValues.email}
            onChange={(e) => setFormValues((prev) => ({ ...prev, email: e.target.value }))}
            size="small"
            disabled={isEditMode} // Email không được sửa khi edit mode
            InputProps={{
              sx: isEditMode ? { color: "text.disabled", backgroundColor: "action.disabledBackground" } : {},
            }}
          />
          <Autocomplete
            fullWidth
            options={currentDepartments}
            getOptionLabel={(option) => option.department_name || ""}
            isOptionEqualToValue={(option, value) => option.department_code === value.department_code}
            value={currentDepartments.find(dept => dept.department_code === formValues.department_code) || null}
            onChange={(event, newValue) => {
              setFormValues((prev) => ({ ...prev, department_code: newValue?.department_code || null}));
            }}
            renderInput={(params) => (
              <TextField {...params} label="Phòng ban" size="small" />
            )}
            noOptionsText="Không tìm thấy phòng ban"
            size="small"
          />
          <Autocomplete
            fullWidth
            options={currentJobPositions}
            getOptionLabel={(option) => option.name || ""}
            isOptionEqualToValue={(option, value) => option.code === value.code }
            value={currentJobPositions.find(job => job.code === formValues.job_position_code) || null}
            onChange={(event, newValue) => {
              setFormValues((prev) => ({ ...prev, job_position_code: newValue?.code || null}));
            }}
            renderInput={(params) => (
              <TextField {...params} label="Vị trí công việc" size="small" />
            )}
            noOptionsText="Không tìm thấy vị trí"
            size="small"
          />

          {!isEditMode && ( // Chỉ hiển thị khi thêm mới
            <>
              <FormControl fullWidth size="small">
                <InputLabel id="add-staff-salary-type-label">Loại lương</InputLabel>
                <Select
                  labelId="add-staff-salary-type-label"
                  label="Loại lương"
                  name="salary_type"
                  value={formValues.salary_type}
                  onChange={(e) => setFormValues((prev) => ({ ...prev, salary_type: e.target.value }))}
                >
                  <MenuItem value="MONTHLY">Theo Tháng</MenuItem>
                  <MenuItem value="WEEKLY">Theo Tuần</MenuItem>
                  <MenuItem value="HOURLY">Theo Giờ</MenuItem>
                </Select>
              </FormControl>
              <TextField
                fullWidth
                name="salary"
                label="Mức lương"
                type="text" // Để có thể format và chỉ cho nhập số
                value={formValues.salary === '' ? '' : Number(String(formValues.salary).replace(/[^\d]/g, "")).toLocaleString("vi-VN")}
                onChange={(e) => {
                  const rawValue = e.target.value.replace(/[^\d]/g, "");
                  setFormValues((prev) => ({ ...prev, salary: rawValue }));
                }}
                InputProps={{
                  endAdornment: <InputAdornment position="end">VNĐ</InputAdornment>,
                }}
                size="small"
                placeholder="Để trống nếu chưa có thông tin"
              />
            </>
          )}
        </Stack>
      </DialogContent>
      <DialogActions sx={{p:2}}>
        <Button onClick={onClose} color="inherit" variant="outlined" disabled={loading}>
          Hủy
        </Button>
        <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading}>
          {loading ? <CircularProgress size={24} color="inherit"/> : (isEditMode ? "Lưu thay đổi" : "Thêm mới")}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddStaffModal;