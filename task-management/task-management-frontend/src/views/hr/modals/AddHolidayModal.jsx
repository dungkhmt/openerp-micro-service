import React, { useState } from "react";
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
  IconButton,
  Stack,
  Box,
} from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import DeleteIcon from "@mui/icons-material/Delete";
import { request } from "@/api";
import dayjs from "dayjs";

const AddHolidayModal = ({ open, onClose, onSubmit }) => {
  const [form, setForm] = useState({
    name: "",
    type: "IN_YEAR",
    dates: [],
  });

  const [selectedDate, setSelectedDate] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleAddDate = (newDate) => {
    const formatted = dayjs(newDate).format("YYYY-MM-DD");
    if (!form.dates.includes(formatted)) {
      setForm((prev) => ({
        ...prev,
        dates: [...prev.dates, formatted],
      }));
    }
  };

  const handleRemoveDate = (date) => {
    setForm((prev) => ({
      ...prev,
      dates: prev.dates.filter((d) => d !== date),
    }));
  };

  const handleSubmit = () => {
    request(
      "post",
      "/holiday/create",
      () => {
        onSubmit();
      },
      {
        onError: (err) => console.error("Create failed", err),
      },
      {
        name: form.name,
        type: form.type,
        dates: form.dates,
      }
    );
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Thêm ngày nghỉ lễ</DialogTitle>
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
            <DatePicker
              label="Chọn ngày nghỉ"
              value={selectedDate}
              onChange={(date) => {
                setSelectedDate(date);
                handleAddDate(date);
              }}
              format="DD/MM/YYYY"
            />
          </LocalizationProvider>

          {form.dates.length > 0 && (
            <Box>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                Ngày đã chọn:
              </Typography>
              <Stack spacing={1}>
                {form.dates.map((date) => (
                  <Box
                    key={date}
                    display="flex"
                    justifyContent="space-between"
                    alignItems="center"
                    px={2}
                    py={1}
                    border="1px solid #e0e0e0"
                    borderRadius={1}
                  >
                    <Typography>{dayjs(date).format("DD/MM/YYYY")}</Typography>
                    <IconButton
                      color="error"
                      onClick={() => handleRemoveDate(date)}
                      size="small"
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Box>
                ))}
              </Stack>
            </Box>
          )}
        </Stack>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Huỷ</Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={!form.name || form.dates.length === 0}
        >
          Lưu
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddHolidayModal;
