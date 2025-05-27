import React, {useEffect, useState, useCallback, useMemo} from "react"; // Added useCallback
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton, // Added IconButton
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
  CircularProgress // Added
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close"; // Added CloseIcon
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {request}from "@/api";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import toast from "react-hot-toast";

dayjs.extend(isSameOrBefore);

const AddHolidayModal = ({ open, onClose, onSubmit, initialData = null, titleProps }) => {
  const defaultFormState = { name: "", type: "IN_YEAR", dates: [] };
  const [form, setForm] = useState(defaultFormState);
  const [fromDate, setFromDate] = useState(null);
  const [toDate, setToDate] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open) {
      if (initialData?.id) {
        const dates = initialData.dates || [];
        setForm({
          name: initialData.name || "",
          type: initialData.type || "IN_YEAR",
          dates, // API trả về mảng date strings YYYY-MM-DD
        });
        // Chuyển đổi date strings thành dayjs objects cho DatePicker
        if (dates.length > 0) {
          setFromDate(dayjs(dates[0]));
          setToDate(dayjs(dates[dates.length - 1]));
        } else {
          setFromDate(null);
          setToDate(null);
        }
      } else {
        setForm(defaultFormState);
        setFromDate(null);
        setToDate(null);
      }
    }
  }, [initialData, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const generateDateRange = useCallback((start, end) => {
    if (!start || !end || !dayjs(start).isValid() || !dayjs(end).isValid()) return [];
    const startDate = dayjs(start);
    const endDate = dayjs(end);
    const result = [];
    let current = startDate;
    while (current.isSameOrBefore(endDate, "day")) {
      result.push(current.format("YYYY-MM-DD"));
      current = current.add(1, "day");
    }
    return result;
  }, []);

  const handleSubmit = async () => {
    if (!form.name.trim()) {
      toast.error("Tên ngày nghỉ không được để trống."); return;
    }
    if (!fromDate || !toDate || !fromDate.isValid() || !toDate.isValid()) {
      toast.error("Vui lòng chọn ngày bắt đầu và ngày kết thúc hợp lệ."); return;
    }
    if (toDate.isBefore(fromDate, "day")) {
      toast.error("Ngày kết thúc không thể trước ngày bắt đầu."); return;
    }

    setLoading(true);
    const dateArray = generateDateRange(fromDate, toDate);

    const method = initialData?.id ? "put" : "post";
    const url = initialData?.id ? `/holidays/${initialData.id}` : "/holidays";

    const payload = {
      name: form.name.trim(),
      type: form.type,
      dates: dateArray,
      ...(initialData?.id && { id: initialData.id }) // Thêm id nếu là update
    };

    try {
      await request(
        method,
        url,
        () => {
          toast.success( initialData?.id ? "Cập nhật ngày nghỉ thành công!" : "Thêm ngày nghỉ thành công!");
          onSubmit();
          onClose();
        },
        {
          onError: (err) => {
            console.error("Create/Update holiday failed", err);
            toast.error(err.response?.data?.message || "Thao tác thất bại.");
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

  const calculatedDays = useMemo(() => {
    if (fromDate && toDate && fromDate.isValid() && toDate.isValid() && !toDate.isBefore(fromDate, "day")) {
      return generateDateRange(fromDate, toDate).length;
    }
    return 0;
  }, [fromDate, toDate, generateDateRange]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 1.5 }}>
        {initialData?.id ? "Chỉnh sửa Ngày Nghỉ Lễ" : "Thêm Ngày Nghỉ Lễ Mới"}
        <IconButton aria-label="đóng" onClick={onClose} sx={{p:0.5}}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{pt: '12px !important'}}>
        <Stack spacing={2.5} sx={{mt:1}}>
          <TextField
            autoFocus
            fullWidth
            label="Tên ngày nghỉ (*)"
            name="name"
            value={form.name}
            onChange={handleChange}
            size="small"
          />

          <FormControl fullWidth size="small">
            <InputLabel>Loại ngày nghỉ</InputLabel>
            <Select
              name="type"
              value={form.type}
              onChange={handleChange}
              label="Loại ngày nghỉ"
            >
              <MenuItem value="IN_YEAR">Trong năm (không lặp)</MenuItem>
              <MenuItem value="EVERY_YEAR">Lặp lại hàng năm</MenuItem>
            </Select>
          </FormControl>

          <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="vi">
            <Stack direction={{xs: 'column', sm: 'row'}} spacing={2}>
              <DatePicker
                label="Từ ngày (*)"
                value={fromDate}
                onChange={(date) => setFromDate(date)}
                format="DD/MM/YYYY"
                slotProps={{ textField: { fullWidth: true, size: 'small' } }}
                sx={{ flex: 1 }}
              />
              <DatePicker
                label="Đến ngày (*)"
                value={toDate}
                onChange={(date) => setToDate(date)}
                format="DD/MM/YYYY"
                minDate={fromDate}
                slotProps={{ textField: { fullWidth: true, size: 'small' } }}
                sx={{ flex: 1 }}
                disabled={!fromDate || !fromDate.isValid()}
              />
            </Stack>
          </LocalizationProvider>

          {calculatedDays > 0 && (
            <Typography variant="body2" color="text.secondary" sx={{mt:1}}>
              Tổng số ngày đã chọn: {calculatedDays} ngày
            </Typography>
          )}
        </Stack>
      </DialogContent>

      <DialogActions sx={{p:2}}>
        <Button onClick={onClose} color="inherit" variant="outlined" disabled={loading}>Huỷ</Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          color="primary"
          disabled={loading || !form.name || !fromDate || !toDate || !fromDate.isValid() || !toDate.isValid() || toDate.isBefore(fromDate, "day")}
        >
          {loading ? <CircularProgress size={24} color="inherit"/> : (initialData?.id ? "Lưu thay đổi" : "Thêm mới")}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddHolidayModal;