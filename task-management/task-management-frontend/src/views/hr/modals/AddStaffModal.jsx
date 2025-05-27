import React, {useEffect, useState, useCallback} from "react"; // Added useCallback
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Grid, // Added Grid
  IconButton, // Added IconButton
  Stack // Added Stack
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close"; // Added CloseIcon
import Autocomplete from "@mui/material/Autocomplete";
import {request}from "@/api";
import toast from "react-hot-toast";

const AddStaffModal = ({ open, onClose, onSubmitSuccess, initialData, isEditMode, departments = [], jobPositions = [], titleProps }) => {
  const defaultFormState = { fullname: "", email: "", department_code: null, job_position_code: null };
  const [formValues, setFormValues] = useState(defaultFormState);

  const [currentDepartments, setCurrentDepartments] = useState([]);
  const [currentJobPositions, setCurrentJobPositions] = useState([]);

  const [loading, setLoading] = useState(false);

  // Không dùng searchDepartment và searchJob nữa, Autocomplete tự xử lý
  // const [searchDepartment, setSearchDepartment] = useState("");
  // const [searchJob, setSearchJob] = useState("");

  // Không fetch lặp lại nếu prop departments, jobPositions đã có
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
          job_position_code: initialData.job_position?.code || null, // Giả sử job_position có 'code'
        });
        // Set giá trị ban đầu cho Autocomplete nếu đang edit
        if (initialData.department && departments.length > 0) {
          const defaultDept = departments.find(d => d.department_code === initialData.department.department_code);
          // setSelectedDepartmentValue(defaultDept || null); // Không cần state riêng nữa
        }
        if (initialData.job_position && jobPositions.length > 0) {
          const defaultJob = jobPositions.find(j => j.code === initialData.job_position.code);
          // setSelectedJobPositionValue(defaultJob || null); // Không cần state riêng nữa
        }

      } else {
        setFormValues(defaultFormState);
        // setSelectedDepartmentValue(null);
        // setSelectedJobPositionValue(null);
      }
    }
  }, [open, initialData, isEditMode, departments, jobPositions]);


  const handleSubmit = async () => {
    setLoading(true);
    if (!formValues.fullname.trim()) {
      toast.error("Họ và tên không được để trống."); setLoading(false); return;
    }
    if (!isEditMode && !formValues.email.trim()) { // Chỉ bắt buộc email khi thêm mới
      toast.error("Email không được để trống."); setLoading(false); return;
    }
    // Thêm validation email nếu cần
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (formValues.email.trim() && !emailRegex.test(formValues.email.trim())) {
      toast.error("Định dạng email không hợp lệ."); setLoading(false); return;
    }


    const payload = {
      fullname: formValues.fullname.trim(),
      email: formValues.email.trim(),
      department_code: formValues.department_code, // Autocomplete trả về code trực tiếp
      job_position_code: formValues.job_position_code, // Autocomplete trả về code trực tiếp
    };

    // Nếu là edit mode và giá trị không thay đổi so với initial, không gửi field đó
    if (isEditMode) {
      if (payload.department_code === initialData?.department?.department_code) {
        delete payload.department_code;
      }
      if (payload.job_position_code === initialData?.job_position?.code) {
        delete payload.job_position_code;
      }
      // Email không cho sửa, không cần kiểm tra
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
          onSubmitSuccess(); // Gọi lại hàm từ parent
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
            label="Email (*)"
            name="email"
            value={formValues.email}
            onChange={(e) => setFormValues((prev) => ({ ...prev, email: e.target.value }))}
            size="small"
            disabled={isEditMode} // Email không cho sửa khi edit
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