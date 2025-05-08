import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Grid,
  TextField,
  Button,
  Paper,
  CircularProgress,
  Divider,
  Card,
  CardContent,
  InputAdornment,
} from "@mui/material";
import { LocalizationProvider, TimePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { request } from "@/api";
import toast from "react-hot-toast";

const SalaryConfigPage = () => {
  const [configs, setConfigs] = useState({});
  const [updatedConfigs, setUpdatedConfigs] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [originalConfigs, setOriginalConfigs] = useState({});
  const [validationErrors, setValidationErrors] = useState({});

  useEffect(() => {
    request("get", "/configs?configGroup=COMPANY_CONFIGS", (res) => {
      const configData = res.data?.data || {};
      setConfigs(configData);
      setUpdatedConfigs(configData);
      setOriginalConfigs(configData);
      setLoading(false);
    });
  }, []);

  const parseTime = (timeStr) => {
    if (!timeStr) return null;
    const [hours, minutes] = timeStr.split(":");
    const date = new Date();
    date.setHours(hours);
    date.setMinutes(minutes);
    date.setSeconds(0);
    return date;
  };

  const formatTime = (date) => {
    if (!date) return "";
    return `${date.getHours().toString().padStart(2, "0")}:${date
      .getMinutes()
      .toString()
      .padStart(2, "0")}`;
  };

  const validateTimes = () => {
    const startWork = parseTime(updatedConfigs["START_WORK_TIME"]?.config_value);
    const endWork = parseTime(updatedConfigs["END_WORK_TIME"]?.config_value);
    const errors = {};

    if (startWork && endWork && startWork >= endWork) {
      errors["WORK_TIME"] = "Thời gian bắt đầu làm việc phải trước thời gian kết thúc";
    }

    const startLunch = parseTime(updatedConfigs["START_LUNCH_TIME"]?.config_value);
    const endLunch = parseTime(updatedConfigs["END_LUNCH_TIME"]?.config_value);

    if (startLunch && endLunch && startLunch >= endLunch) {
      errors["LUNCH_TIME"] = "Thời gian bắt đầu nghỉ trưa phải trước thời gian kết thúc";
    }

    setValidationErrors(errors);

    return Object.keys(errors).length === 0;
  };

  const handleChange = (key, value) => {
    setUpdatedConfigs((prev) => ({
      ...prev,
      [key]: {
        ...prev[key],
        config_value: value,
      },
    }));
  };

  const handleCancel = () => {
    setUpdatedConfigs({ ...originalConfigs });
    setError("");
    setValidationErrors({});
  };

  const buildRequestBody = () => {
    const requestBody = {};
    Object.entries(updatedConfigs).forEach(([key, config]) => {
      requestBody[key] = config.config_value;
    });
    return requestBody;
  };

  const handleSubmit = async () => {
    if (!validateTimes()) return;

    setLoading(true);
    setError("");

    const requestBody = buildRequestBody();

    await request("put", "/configs", () => {
        toast.success("Cập nhật cấu hình thành công!");
        setOriginalConfigs({ ...updatedConfigs });
        setLoading(false);
      },
      {
        onError: () => {
          toast.error("Cập nhật cấu hình thất bại!");
          setLoading(false);
        },
      },
      {
        config_map: requestBody
      }
    );
  };


  if (loading) return <CircularProgress />;

  const renderTimePair = (startKey, endKey, title, errorKey) => (
    <Card variant="outlined" sx={{ mb: 1, borderRadius: 2, "&:hover": { backgroundColor: "#fafafa" } }}>
      <CardContent>
        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
          {title}
        </Typography>

        <Grid container spacing={2} >
          <Grid item xs={3}>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <TimePicker
                label="Bắt đầu"
                value={parseTime(updatedConfigs[startKey]?.config_value)}
                onChange={(value) => handleChange(startKey, formatTime(value))}
                renderInput={(params) => <TextField {...params} fullWidth />}
              />
            </LocalizationProvider>
          </Grid>

          <Grid item xs={3}>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <TimePicker
                label="Kết thúc"
                value={parseTime(updatedConfigs[endKey]?.config_value)}
                onChange={(value) => handleChange(endKey, formatTime(value))}
                renderInput={(params) => <TextField {...params} fullWidth />}
              />
            </LocalizationProvider>
          </Grid>
        </Grid>

        {validationErrors[errorKey] && (
          <Typography color="error" variant="body2" sx={{ mt: 1 }}>
            {validationErrors[errorKey]}
          </Typography>
        )}
      </CardContent>
    </Card>
  );

  const renderCustomField = (key, config) => {
    const value = updatedConfigs[key]?.config_value;

    if (key === "HOUR_BEFORE_ANNOUNCE_ABSENCE") {
      return (
        <TextField
          value={value}
          onChange={(e) => handleChange(key, e.target.value)}
          InputProps={{
            endAdornment: <InputAdornment position="end">giờ</InputAdornment>,
          }}
          type="number"
          sx={{ width: 200 }}
        />
      );
    }

    return (
      <TextField
        label={config.config_name}
        value={value}
        onChange={(e) => handleChange(key, e.target.value)}
        fullWidth
      />
    );
  };

  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h5" gutterBottom>
        Cấu hình công ty
      </Typography>

      <Paper elevation={3} sx={{ p: 3 }}>
        {error && (
          <Typography color="error" sx={{ mb: 2 }}>
            {error}
          </Typography>
        )}

        <Grid container spacing={3}>
          <Grid item xs={12}>
            {renderTimePair("START_WORK_TIME", "END_WORK_TIME", "Thời gian làm việc", "WORK_TIME")}
            {renderTimePair("START_LUNCH_TIME", "END_LUNCH_TIME", "Thời gian nghỉ trưa", "LUNCH_TIME")}
          </Grid>

          {Object.entries(configs).map(([key, config]) => {
            if (["START_WORK_TIME", "END_WORK_TIME", "START_LUNCH_TIME", "END_LUNCH_TIME"].includes(key)) {
              return null;
            }

            return (
              <Grid item xs={12} key={key}>
                <Card variant="outlined" sx={{ borderRadius: 2, "&:hover": { backgroundColor: "#fafafa" } }}>
                  <CardContent>
                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                      {config.config_name}
                    </Typography>

                    {config.description && (
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        {config.description}
                      </Typography>
                    )}

                    {renderCustomField(key, config)}
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>

        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2, mt: 4 }}>
          <Button variant="outlined" onClick={handleCancel} disabled={loading}>
            Hủy
          </Button>
          <Button variant="contained" onClick={handleSubmit} disabled={loading}>
            {loading ? "Đang cập nhật..." : "Cập nhật"}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default SalaryConfigPage;
