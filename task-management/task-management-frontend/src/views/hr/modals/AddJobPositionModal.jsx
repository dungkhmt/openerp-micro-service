import React, {useEffect, useState} from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Stack, // Added Stack
  CircularProgress // Added
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import {request}from "@/api";
import toast from "react-hot-toast"; // Import toast

const AddJobPositionModal = ({ open, onClose, onSubmit, initialData, titleProps }) => { // Renamed initialValues
  const [formValues, setFormValues] = useState({
    name: "",
    description: "",
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (initialData) {
      setFormValues({
        name: initialData.name || "",
        description: initialData.description || "",
      });
    } else {
      setFormValues({ name: "", description: "" });
    }
  }, [initialData, open]);

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
      toast.error("Tên vị trí không được để trống.");
      setLoading(false);
      return;
    }

    const payload = {
      name: formValues.name.trim(),
      description: formValues.description.trim(),
      ...(initialData && initialData.code && { code: initialData.code })
    };

    try {
      const endpoint = initialData?.code
        ? `/jobs/${initialData.code}`
        : "/jobs";
      const methodURL = initialData?.code ? "put" : "post";

      await request(
        methodURL,
        endpoint,
        (response) => {
          toast.success(initialData?.code ? "Cập nhật vị trí thành công!" : "Thêm vị trí thành công!");
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
      <DialogTitle sx={{...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 1.5 }}>
        {initialData?.code ? "Chỉnh sửa Vị trí Công việc" : "Thêm Vị trí Công việc Mới"}
        <IconButton aria-label="đóng" onClick={onClose} sx={{p:0.5}}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{pt: '12px !important'}}>
        <Stack spacing={2.5} sx={{mt:1}}>
          <TextField
            autoFocus
            fullWidth
            label="Tên vị trí (*)"
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
          {loading ? <CircularProgress size={24} color="inherit"/> : (initialData?.code ? "Lưu thay đổi" : "Thêm mới")}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddJobPositionModal;