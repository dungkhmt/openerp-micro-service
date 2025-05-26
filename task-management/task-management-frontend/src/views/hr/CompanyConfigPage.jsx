import React, {useEffect, useState, useCallback} from "react"; // Added useCallback
import {
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  InputAdornment,
  Paper,
  TextField,
  Typography,
  ThemeProvider, // Added
  CssBaseline,    // Added
} from "@mui/material";
import {LocalizationProvider, TimePicker} from "@mui/x-date-pickers";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns"; // Using DateFns
import { vi } from 'date-fns/locale'; // Locale for DateFns
import {request}from "@/api";
import toast from "react-hot-toast";
import {theme} from "./theme"; // Assuming theme.js is in the same directory

const CompanyConfigPageInternal = () => {
  const [configs, setConfigs] = useState({});
  // updatedConfigs will hold the current form state, including parsed time values for TimePicker
  const [updatedConfigsForm, setUpdatedConfigsForm] = useState({});
  const [loading, setLoading] = useState(true);
  // const [error, setError] = useState(""); // Error is shown via toast
  const [originalConfigs, setOriginalConfigs] = useState({});
  const [validationErrors, setValidationErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false); // For submit button loading state


  const parseTimeForState = (timeStr) => {
    if (!timeStr || typeof timeStr !== 'string') return null;
    const [hours, minutes] = timeStr.split(':');
    if (isNaN(parseInt(hours, 10)) || isNaN(parseInt(minutes, 10))) return null;
    const date = new Date(); // Use a fixed date for time comparison consistency
    date.setHours(parseInt(hours, 10), parseInt(minutes, 10), 0, 0);
    return date;
  };

  const formatTimeToString = (dateObj) => {
    if (!dateObj || !(dateObj instanceof Date)) return "";
    return `${String(dateObj.getHours()).padStart(2, '0')}:${String(dateObj.getMinutes()).padStart(2, '0')}`;
  };


  useEffect(() => {
    setLoading(true);
    request("get", "/configs?configGroup=COMPANY_CONFIGS", (res) => {
      const configData = res.data?.data || {};
      const initialFormState = {};
      Object.entries(configData).forEach(([key, config]) => {
        initialFormState[key] = {
          ...config,
          // For time fields, parse to Date objects for TimePicker component
          config_value: (key.includes("_TIME"))
            ? parseTimeForState(config.config_value)
            : config.config_value,
        };
      });
      setConfigs(configData); // Keep original API response structure if needed elsewhere
      setUpdatedConfigsForm(initialFormState);
      setOriginalConfigs(initialFormState); // Store original form state for cancellation
      setLoading(false);
    }, { onError: () => {
        toast.error("Không thể tải cấu hình công ty.");
        setLoading(false);
      }});
  }, []);


  const validateTimes = (currentFormState) => {
    const startWork = currentFormState["START_WORK_TIME"]?.config_value; // Already a Date object or null
    const endWork = currentFormState["END_WORK_TIME"]?.config_value;
    const errors = {};

    if (startWork && endWork && startWork.getTime() >= endWork.getTime()) {
      errors["WORK_TIME"] = "Giờ bắt đầu làm phải trước giờ kết thúc";
    }

    const startLunch = currentFormState["START_LUNCH_TIME"]?.config_value;
    const endLunch = currentFormState["END_LUNCH_TIME"]?.config_value;

    if (startLunch && endLunch && startLunch.getTime() >= endLunch.getTime()) {
      errors["LUNCH_TIME"] = "Giờ bắt đầu nghỉ trưa phải trước giờ kết thúc";
    }

    if (startLunch && endLunch && startWork && endWork) {
      if (!(startLunch.getTime() >= startWork.getTime() && endLunch.getTime() <= endWork.getTime())) {
        errors["LUNCH_IN_WORK_TIME"] = "Thời gian nghỉ trưa phải nằm trong thời gian làm việc";
      }
      if (startLunch.getTime() >= endLunch.getTime()){
      }
    }


    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleChange = (key, value) => {
    setUpdatedConfigsForm((prev) => ({
      ...prev,
      [key]: {
        ...prev[key], // Keep other properties like config_name, description
        config_value: value,
      },
    }));
    // Clear related validation error when user types
    if (key === "START_WORK_TIME" || key === "END_WORK_TIME") {
      setValidationErrors(prev => ({...prev, WORK_TIME: undefined, LUNCH_IN_WORK_TIME: undefined }));
    }
    if (key === "START_LUNCH_TIME" || key === "END_LUNCH_TIME") {
      setValidationErrors(prev => ({...prev, LUNCH_TIME: undefined, LUNCH_IN_WORK_TIME: undefined}));
    }
  };

  const handleCancel = () => {
    setUpdatedConfigsForm({ ...originalConfigs });
    setValidationErrors({});
  };

  const buildRequestBody = () => {
    const requestBody = {};
    Object.entries(updatedConfigsForm).forEach(([key, config]) => {
      // Format Date objects back to "HH:mm" strings for time fields
      requestBody[key] = (key.includes("_TIME"))
        ? formatTimeToString(config.config_value)
        : config.config_value;
    });
    return requestBody;
  };

  const handleSubmit = async () => {
    if (!validateTimes(updatedConfigsForm)) return;

    setIsSubmitting(true);
    const requestBody = buildRequestBody();

    await request("put", "/configs", () => {
        toast.success("Cập nhật cấu hình thành công!");
        // Update originalConfigs to current state after successful save
        setOriginalConfigs({ ...updatedConfigsForm });
      },
      {
        onError: (err) => {
          toast.error(err.response?.data?.message || "Cập nhật cấu hình thất bại!");
        },
      },
      { config_map: requestBody } // Payload for PUT
    ).finally(() => setIsSubmitting(false));
  };


  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 128px)' }}>
        <CircularProgress />
      </Box>
    );
  }

  const renderTimePair = (startKey, endKey, title, errorKey, dependentErrorKey) => (
    <Card variant="outlined" sx={{ mb: 2, borderRadius: 2}}>
      <CardContent>
        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
          {title}
        </Typography>
        <Grid container spacing={2} >
          <Grid item xs={12} sm={6} md={3}>
            <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
              <TimePicker
                label="Bắt đầu"
                ampm={false}
                value={updatedConfigsForm[startKey]?.config_value || null} // Ensure value is Date or null
                onChange={(value) => handleChange(startKey, value)}
                slotProps={{ textField: { size: 'small', fullWidth: true } }}
              />
            </LocalizationProvider>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
              <TimePicker
                label="Kết thúc"
                ampm={false}
                value={updatedConfigsForm[endKey]?.config_value || null} // Ensure value is Date or null
                onChange={(value) => handleChange(endKey, value)}
                slotProps={{ textField: { size: 'small', fullWidth: true } }}
              />
            </LocalizationProvider>
          </Grid>
        </Grid>
        {validationErrors[errorKey] && (
          <Typography color="error" variant="caption" sx={{ mt: 1, display:'block' }}>
            {validationErrors[errorKey]}
          </Typography>
        )}
        {dependentErrorKey && validationErrors[dependentErrorKey] && (
          <Typography color="error" variant="caption" sx={{ mt: 1, display:'block' }}>
            {validationErrors[dependentErrorKey]}
          </Typography>
        )}
      </CardContent>
    </Card>
  );

  const renderCustomField = (key, config) => {
    const value = updatedConfigsForm[key]?.config_value;
    let inputType = "text";
    if (key === "HOUR_BEFORE_ANNOUNCE_ABSENCE" || key === "DEFAULT_WORK_HOURS_PER_DAY" || key === "MAX_CONSECUTIVE_WORK_DAYS") {
      inputType = "number";
    }

    return (
      <TextField
        label={config.config_name}
        value={value || ''} // Ensure value is not null/undefined for TextField
        onChange={(e) => handleChange(key, e.target.value)}
        type={inputType}
        InputProps={{
          endAdornment: key === "HOUR_BEFORE_ANNOUNCE_ABSENCE" ? <InputAdornment position="end">giờ</InputAdornment> : (key === "DEFAULT_WORK_HOURS_PER_DAY" ? <InputAdornment position="end">giờ/ngày</InputAdornment> : (key === "MAX_CONSECUTIVE_WORK_DAYS" ? <InputAdornment position="end">ngày</InputAdornment>: null)),
          inputProps: inputType === "number" ? { min: 0, step: key === "DEFAULT_WORK_HOURS_PER_DAY" ? 0.1 : 1 } : {},
        }}
        size="small"
        fullWidth={!(key === "HOUR_BEFORE_ANNOUNCE_ABSENCE" || key === "DEFAULT_WORK_HOURS_PER_DAY" || key === "MAX_CONSECUTIVE_WORK_DAYS" )} // Not fullWidth for number types with adornment
        sx={ (key === "HOUR_BEFORE_ANNOUNCE_ABSENCE" || key === "DEFAULT_WORK_HOURS_PER_DAY" || key === "MAX_CONSECUTIVE_WORK_DAYS") ? {width: 250} : {}}
      />
    );
  };

  const isFormDirty = JSON.stringify(updatedConfigsForm) !== JSON.stringify(originalConfigs);


  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Typography variant="h4" component="h1">
          Cấu hình công ty
        </Typography>
      </Paper>

      <Paper elevation={0} sx={{ p: {xs: 1.5, md: 2} }}>
        <Grid container spacing={2}> {/* Changed spacing to 2 for tighter packing of cards */}
          <Grid item xs={12}>
            {renderTimePair("START_WORK_TIME", "END_WORK_TIME", "Thời gian làm việc", "WORK_TIME", "LUNCH_IN_WORK_TIME")}
            {renderTimePair("START_LUNCH_TIME", "END_LUNCH_TIME", "Thời gian nghỉ trưa", "LUNCH_TIME")}
          </Grid>

          {Object.entries(configs) // Iterate over original configs to maintain order and structure
            .filter(([key]) => !["START_WORK_TIME", "END_WORK_TIME", "START_LUNCH_TIME", "END_LUNCH_TIME"].includes(key))
            .map(([key, config]) => (
              <Grid item xs={12} md={6} key={key}> {/* Changed to md={6} for two columns */}
                <Card variant="outlined" sx={{ borderRadius: 2, height: '100%'}}>
                  <CardContent>
                    <Typography variant="subtitle1" sx={{ fontWeight: 600, minHeight: '2.5em' }}> {/* Ensure min height for title */}
                      {updatedConfigsForm[key]?.config_name || config.config_name}
                    </Typography>
                    { (updatedConfigsForm[key]?.description || config.description) && (
                      <Typography variant="caption" color="text.secondary" sx={{ mb: 1.5, display: 'block', minHeight: '2em' }}> {/* Ensure min height for description */}
                        {updatedConfigsForm[key]?.description || config.description}
                      </Typography>
                    )}
                    {renderCustomField(key, updatedConfigsForm[key] || config)}
                  </CardContent>
                </Card>
              </Grid>
            ))}
        </Grid>

        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2, mt: 3 }}>
          <Button variant="outlined" onClick={handleCancel} disabled={isSubmitting || !isFormDirty}>
            Hủy
          </Button>
          <Button variant="contained" onClick={handleSubmit} disabled={isSubmitting || !isFormDirty}>
            {isSubmitting ? <CircularProgress size={24}/> : "Lưu thay đổi"}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

const CompanyConfigPage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline/>
      <CompanyConfigPageInternal/>
    </ThemeProvider>
  )
}

export default CompanyConfigPage;