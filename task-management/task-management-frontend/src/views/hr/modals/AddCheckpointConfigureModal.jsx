import React, {useEffect, useState} from "react";
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Stack,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import {request}from "@/api";
import toast from "react-hot-toast";

const AddCheckpointConfigureModal = ({ open, onClose, onSubmit, initialValues, titleProps }) => {
  const [formValues, setFormValues] = useState({
    name: "",
    description: "",
  });
  // Bỏ error state cục bộ, dùng toast
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (initialValues) {
      setFormValues({
        name: initialValues.name || "",
        description: initialValues.description || "",
      });
    } else {
      setFormValues({ name: "", description: "" });
    }
  }, [initialValues, open]); // Thêm open để reset form khi mở lại modal cho item mới

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({
      ...formValues,
      [name]: value,
    });
  };

  const handleSubmit = async () => {
    setLoading(true);
    if (!formValues.name.trim()) {
      toast.error("Tên cấu hình không được để trống.");
      setLoading(false);
      return;
    }

    const payload = {
      name: formValues.name.trim(),
      description: formValues.description.trim(),
      // API có thể yêu cầu 'code' nếu là 'put' và 'id' nếu là 'post', cần điều chỉnh
      ...(initialValues && initialValues.code && { code: initialValues.code })
    };

    try {
      const endpoint = initialValues?.id // Hoặc initialValues?.code nếu dùng code làm id
        ? `/checkpoints/configures/${initialValues.id}` // Giả sử API dùng id cho PUT
        : "/checkpoints/configures";
      const methodURL = initialValues?.id ? "put" : "post";

      await request(
        methodURL,
        endpoint,
        (response) => {
          toast.success(initialValues?.id ? "Cập nhật cấu hình thành công!" : "Thêm cấu hình thành công!");
          onSubmit();
          onClose();
        },
        {
          onError: (err) => {
            console.error("API Error:", err.response?.data || err.message);
            const errorMsg = err.response?.data?.meta?.message || err.response?.data?.message || "Thao tác thất bại. Vui lòng thử lại.";
            toast.error(errorMsg);
          }
        },
        payload
      );
    } catch (error) {
      console.error("API request failed:", error);
      toast.error(`Đã xảy ra lỗi. ${error.message || "Vui lòng thử lại."}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ ...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb:1.5 }}>
        {initialValues?.id ? "Chỉnh sửa Cấu hình Checkpoint" : "Thêm Cấu hình Checkpoint Mới"}
        <IconButton aria-label="đóng" onClick={onClose} sx={{p:0.5}}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{pt: '12px !important'}}> {/* Thêm dividers và padding top */}
        <Stack spacing={2.5} sx={{mt:1}}> {/* Sử dụng Stack */}
          <TextField
            autoFocus // Thêm autoFocus
            fullWidth
            label="Tên cấu hình (*)"
            name="name"
            value={formValues.name}
            onChange={handleChange}
            size="small"
          />
          <TextField
            fullWidth
            label="Mô tả"
            name="description"
            value={formValues.description}
            onChange={handleChange}
            size="small"
            multiline
            rows={4}
          />
        </Stack>
      </DialogContent>
      <DialogActions sx={{p:2}}>
        <Button onClick={onClose} color="inherit" variant="outlined" disabled={loading}>
          Hủy
        </Button>
        <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading}>
          {loading ? <CircularProgress size={24} color="inherit" /> : (initialValues?.id ? "Lưu thay đổi" : "Thêm mới")}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddCheckpointConfigureModal;