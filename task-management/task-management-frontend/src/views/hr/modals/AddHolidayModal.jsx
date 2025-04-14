import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  Button,
  InputLabel,
  FormControl,
  Typography,
  Stack,
} from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { request } from "@/api";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import toast from "react-hot-toast";
dayjs.extend(isSameOrBefore);

const AddHolidayModal = ({ open, onClose, onSubmit, initialData = null }) => {
  const [form, setForm] = useState({
    name: "",
    type: "IN_YEAR",
    dates: [],
  });
  const [fromDate, setFromDate] = useState(null);
  const [toDate, setToDate] = useState(null);

  useEffect(() => {
    if (initialData) {
      const dates = initialData.dates || [];
      setForm({
        name: initialData.name || "",
        type: initialData.type || "IN_YEAR",
        dates,
      });
      setFromDate(dayjs(dates[0]));
      setToDate(dayjs(dates[dates.length - 1]));
    }
  }, [initialData]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const generateDateRange = (start, end) => {
    const startDate = dayjs(start);
    const endDate = dayjs(end);
    const result = [];

    let current = startDate;
    while (current.isSameOrBefore(endDate, "day")) {
      result.push(current.format("YYYY-MM-DD"));
      current = current.add(1, "day");
    }

    return result;
  };

  const handleSubmit = () => {
    if (!fromDate || !toDate) return;

    const dateArray = generateDateRange(fromDate, toDate);

    const method = initialData ? "put" : "post";
    const url = initialData ? `/holidays/${initialData.id}` : "/holidays";

    request(
      method,
      url,
      () => {
        onSubmit();
        toast.success( initialData? "Cập nhật thành công" : "Thêm thành công");
      },
      {
        onError: (err) => console.error("Create/Update failed", err),
      },
      {
        name: form.name,
        type: form.type,
        dates: dateArray,
      }
    );
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{initialData ? "Cập nhật ngày nghỉ" : "Thêm ngày nghỉ lễ"}</DialogTitle>
      <DialogContent>
        <Stack spacing={3} mt={2}>
          <TextField
            fullWidth
            label="Tên ngày nghỉ"
            name="name"
            value={form.name}
            onChange={handleChange}
          />

          <FormControl fullWidth>
            <InputLabel>Loại ngày nghỉ</InputLabel>
            <Select
              name="type"
              value={form.type}
              onChange={handleChange}
              label="Loại ngày nghỉ"
            >
              <MenuItem value="IN_YEAR">Chỉ trong năm</MenuItem>
              <MenuItem value="EVERY_YEAR">Lặp lại hàng năm</MenuItem>
            </Select>
          </FormControl>

          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <Stack direction="row" spacing={2}>
              <DatePicker
                label="Từ ngày"
                value={fromDate}
                onChange={(date) => setFromDate(date)}
                format="DD/MM/YYYY"
                slotProps={{ textField: { fullWidth: true } }}
                sx={{ flex: 1 }}
              />
              <DatePicker
                label="Đến ngày"
                value={toDate}
                onChange={(date) => setToDate(date)}
                format="DD/MM/YYYY"
                minDate={fromDate}
                slotProps={{ textField: { fullWidth: true } }}
                sx={{ flex: 1 }}
              />
            </Stack>
          </LocalizationProvider>

          {fromDate && toDate && (
            <Typography variant="body2" color="text.secondary">
              Tổng cộng: {generateDateRange(fromDate, toDate).length} ngày
            </Typography>
          )}
        </Stack>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Huỷ</Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={!form.name || !fromDate || !toDate}
        >
          Lưu
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddHolidayModal;
