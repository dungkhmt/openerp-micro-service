import React, { useEffect, useState } from "react";
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Stack,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  InputAdornment,
  IconButton
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import Autocomplete from "@mui/material/Autocomplete";
import { request } from "@/api";
import toast from "react-hot-toast";
import hrService from '@/services/api/hr.service'; // Import hrService

const AddStaffModal = ({ open, onClose, onSubmitSuccess, initialData, isEditMode, titleProps }) => {
  const defaultFormState = {
    fullname: "",
    email: "",
    department_code: null,
    job_position_code: null,
    salary: "",
    salary_type: "MONTHLY"
  };
  const [formValues, setFormValues] = useState(defaultFormState);

  // State để lưu danh sách phòng ban và chức vụ lấy từ API
  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);
  const [optionsLoading, setOptionsLoading] = useState(false); // State loading cho dropdown

  const [loading, setLoading] = useState(false);

  // useEffect này để điền dữ liệu vào form khi ở chế độ edit
  useEffect(() => {
    if (open) {
      if (isEditMode && initialData) {
        console.log(initialData)
        setFormValues({
          fullname: initialData.fullname || "",
          email: initialData.email || "",
          department_code: initialData.department?.department_code || null,
          job_position_code: initialData.job_position?.job_position_code || null,
          salary: "",
          salary_type: "MONTHLY"
        });
      } else {
        setFormValues(defaultFormState);
      }
    }
  }, [open, initialData, isEditMode]);

  // useEffect mới để tự động fetch danh sách phòng ban/chức vụ khi modal mở
  useEffect(() => {
    const fetchDropdownOptions = async () => {
      setOptionsLoading(true);
      try {
        const [deptRes, jobPosRes] = await Promise.all([
          hrService.getAllDepartments({ page: 0, size: 500, status: 'ACTIVE' }),
          hrService.getAllJobPositions({ page: 0, size: 500, status: 'ACTIVE' })
        ]);

        setDepartments(deptRes.response?.data?.data || []);
        setJobPositions(jobPosRes.response?.data?.data || []);
      } catch (error) {
        toast.error("Không thể tải danh sách phòng ban hoặc chức vụ.");
        console.error("Failed to fetch dropdown options:", error);
      } finally {
        setOptionsLoading(false);
      }
    };

    if (open) {
      fetchDropdownOptions();
    }
  }, [open]);


  const handleSubmit = async () => {
    setLoading(true);
    // ... (logic của hàm handleSubmit giữ nguyên như cũ) ...
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
      if (salaryValue) {
        payload.salary = parseFloat(salaryValue);
        payload.salary_type = formValues.salary_type;
      }
    }
    if (isEditMode) {
      if (payload.department_code === initialData?.department?.department_code) delete payload.department_code;
      if (payload.job_position_code === initialData?.job_position?.code) delete payload.job_position_code;
      delete payload.salary;
      delete payload.salary_type;
    }
    try {
      const apiEndpoint = isEditMode ? `/staffs/${initialData?.staff_code}` : "/staffs";
      const methodURL = isEditMode ? "put" : "post";
      await request(methodURL, apiEndpoint, () => {
        toast.success(isEditMode ? "Cập nhật nhân viên thành công!" : "Thêm nhân viên thành công!");
        onSubmitSuccess();
        onClose();
      }, {
        onError: (err) => {
          const errorMsg = err.response?.data?.meta?.message || err.response?.data?.message || "Thao tác thất bại.";
          toast.error(errorMsg);
        },
      }, payload);
    } catch (error) {
      toast.error(`Đã xảy ra lỗi: ${error.message || "Vui lòng thử lại."}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ ...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 1.5 }}>
        {isEditMode ? "Chỉnh sửa Thông tin Nhân viên" : "Thêm Nhân viên Mới"}
        <IconButton aria-label="đóng" onClick={onClose} sx={{ p: 0.5 }}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{ pt: '12px !important' }}>
        <Stack spacing={2.5} sx={{ mt: 1 }}>
          <TextField
            autoFocus
            fullWidth
            label="Họ và tên (*)"
            name="fullname"
            value={formValues.fullname}
            onChange={(e) => setFormValues((prev) => ({ ...prev, fullname: e.target.value }))}
            size="small"
          />
          <TextField
            fullWidth
            label={isEditMode ? "Email" : "Email (*)"}
            name="email"
            value={formValues.email}
            onChange={(e) => setFormValues((prev) => ({ ...prev, email: e.target.value }))}
            size="small"
            disabled={isEditMode}
            InputProps={{ sx: isEditMode ? { color: "text.disabled", backgroundColor: "action.disabledBackground" } : {} }}
          />
          <Autocomplete
            fullWidth
            options={departments}
            getOptionLabel={(option) => option.department_name || ""}
            isOptionEqualToValue={(option, value) => option.department_code === value.department_code}
            value={departments.find(dept => dept.department_code === formValues.department_code) || null}
            onChange={(event, newValue) => {
              setFormValues((prev) => ({ ...prev, department_code: newValue?.department_code || null }));
            }}
            loading={optionsLoading}
            loadingText="Đang tải..."
            renderInput={(params) => (
              <TextField
                {...params}
                label="Phòng ban"
                size="small"
                InputProps={{ ...params.InputProps, endAdornment: <>{optionsLoading ? <CircularProgress color="inherit" size={20} /> : null}{params.InputProps.endAdornment}</> }}
              />
            )}
            noOptionsText="Không tìm thấy phòng ban"
            size="small"
          />
          <Autocomplete
            fullWidth
            options={jobPositions}
            getOptionLabel={(option) => option.name || ""}
            isOptionEqualToValue={(option, value) => option.code === value.code}
            value={jobPositions.find(job => job.code === formValues.job_position_code) || null}
            onChange={(event, newValue) => {
              setFormValues((prev) => ({ ...prev, job_position_code: newValue?.code || null }));
            }}
            loading={optionsLoading}
            loadingText="Đang tải..."
            renderInput={(params) => (
              <TextField
                {...params}
                label="Vị trí công việc"
                size="small"
                InputProps={{ ...params.InputProps, endAdornment: <>{optionsLoading ? <CircularProgress color="inherit" size={20} /> : null}{params.InputProps.endAdornment}</> }}
              />
            )}
            noOptionsText="Không tìm thấy vị trí"
            size="small"
          />

          {!isEditMode && (
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
                  {/*<MenuItem value="WEEKLY">Theo Tuần</MenuItem>*/}
                  <MenuItem value="HOURLY">Theo Giờ</MenuItem>
                </Select>
              </FormControl>
              <TextField
                fullWidth
                name="salary"
                label="Mức lương"
                value={formValues.salary === '' ? '' : Number(String(formValues.salary).replace(/[^\d]/g, "")).toLocaleString("vi-VN")}
                onChange={(e) => {
                  const rawValue = e.target.value.replace(/[^\d]/g, "");
                  setFormValues((prev) => ({ ...prev, salary: rawValue }));
                }}
                InputProps={{ endAdornment: <InputAdornment position="end">VNĐ</InputAdornment> }}
                size="small"
                placeholder="Để trống nếu chưa có thông tin"
              />
            </>
          )}
        </Stack>
      </DialogContent>
      <DialogActions sx={{ p: 2 }}>
        <Button onClick={onClose} color="inherit" variant="outlined" disabled={loading}>
          Hủy
        </Button>
        <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading}>
          {loading ? <CircularProgress size={24} color="inherit" /> : (isEditMode ? "Lưu thay đổi" : "Thêm mới")}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddStaffModal;