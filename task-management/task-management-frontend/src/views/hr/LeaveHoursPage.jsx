import React, { useState, useEffect, useCallback } from "react";
import {
  Box,
  Button,
  CircularProgress,
  MenuItem,
  Paper,
  TextField,
  Typography,
  ThemeProvider,
  CssBaseline,
  Stack,
  FormControl,
  RadioGroup,
  FormControlLabel,
  Radio,
  Grid
} from "@mui/material";
import toast from "react-hot-toast";
import { theme } from "./theme";
import hrService from '@/services/api/hr.service';
import CustomMultiSelect from "@/components/item/CustomMultiSelect";
import {request} from "../../api.js";

const LeaveHoursPageInternal = () => {
  const [leaveHours, setLeaveHours] = useState("");
  const [updateType, setUpdateType] = useState("ADDITIONAL");
  const [loading, setLoading] = useState(false);

  const [updateScope, setUpdateScope] = useState('ALL'); // 'ALL', 'FILTER', hoặc 'STAFF'
  const [options, setOptions] = useState({ departments: [], jobPositions: [], staff: [] });
  const [selected, setSelected] = useState({ departmentCodes: [], jobPositionCodes: [], userLoginIds: [] });
  const [optionsLoading, setOptionsLoading] = useState(false);

  useEffect(() => {
    const fetchDropdownOptions = async () => {
      setOptionsLoading(true);
      try {
        const [deptRes, jobPosRes, staffRes] = await Promise.all([
          hrService.getAllDepartments({ page: 0, size: 500, status: 'ACTIVE' }),
          hrService.getAllJobPositions({ page: 0, size: 500, status: 'ACTIVE' }),
          hrService.getAllStaffDetails({ page: 0, size: 2000, status: 'ACTIVE' })
        ]);
        setOptions({
          departments: deptRes.response?.data?.data || [],
          jobPositions: jobPosRes.response?.data?.data || [],
          staff: staffRes.response?.data?.data || [],
        });
      } catch (error) {
        toast.error("Không thể tải danh sách các lựa chọn.");
      } finally {
        setOptionsLoading(false);
      }
    };
    fetchDropdownOptions();
  }, []);

  const handleScopeChange = (event) => {
    setUpdateScope(event.target.value);
    setSelected({ departmentCodes: [], jobPositionCodes: [], userLoginIds: [] });
  };

  const handleSubmit = async () => {
    if (leaveHours === "" || isNaN(parseFloat(leaveHours)) || parseFloat(leaveHours) < 0) {
      toast.error("Vui lòng nhập số giờ phép hợp lệ (là số không âm).");
      return;
    }
    setLoading(true);

    const payload = {
      leave_hours: parseFloat(leaveHours),
      update_type: updateType,
      department_codes: null,
      job_position_codes: null,
      user_ids: null,
    };

    if (updateScope === 'FILTER') {
      if (selected.departmentCodes.length === 0 && selected.jobPositionCodes.length === 0) {
        toast.error("Vui lòng chọn ít nhất một phòng ban hoặc chức vụ để lọc.");
        setLoading(false);
        return;
      }
      payload.department_codes = selected.departmentCodes;
      payload.job_position_codes = selected.jobPositionCodes;
    } else if (updateScope === 'STAFF') {
      if (selected.userLoginIds.length === 0) {
        toast.error("Vui lòng chọn ít nhất một nhân viên.");
        setLoading(false);
        return;
      }
      payload.user_ids = selected.userLoginIds;
    }

    await request("patch", "/leave-hours",
      () => {
        toast.success("Yêu cầu cập nhật giờ phép đã được gửi đi!");
        setLeaveHours("");
        setSelected({ departmentCodes: [], jobPositionCodes: [], userLoginIds: [] });
      },
      {
        onError: (err) => {
          toast.error(err.response?.data?.message || err.message || "Cập nhật giờ phép thất bại!");
        }
      },
      payload
    ).finally(() => setLoading(false));
  };

  return (
    <Box sx={{ p: { xs:2, md: 2 } }}>
      <Typography variant="h4" component="h1" fontWeight="bold" mb={2}>
        Cập nhật giờ phép
      </Typography>

      <Paper sx={{ p: { xs: 2, md: 3 }, maxWidth: 800 }}>
        <Stack spacing={3.5}>
          <FormControl component="fieldset">
            <Typography variant="h6" gutterBottom fontWeight={600}>1. Chọn đối tượng áp dụng</Typography>
            <RadioGroup row name="update-scope" value={updateScope} onChange={handleScopeChange}>
              <FormControlLabel value="ALL" control={<Radio />} label="Toàn bộ nhân viên" />
              <FormControlLabel value="FILTER" control={<Radio />} label="Theo phòng ban / chức vụ" />
              <FormControlLabel value="STAFF" control={<Radio />} label="Theo nhân viên cụ thể" />
            </RadioGroup>
          </FormControl>

          {updateScope === 'FILTER' && (
            <Paper variant="outlined" sx={{p: 2.5, borderColor: 'divider'}}>
              <Stack spacing={2}>
                <CustomMultiSelect
                  label="Phòng ban"
                  options={options.departments}
                  selectedValues={selected.departmentCodes}
                  onChange={(newValue) => setSelected(prev => ({...prev, departmentCodes: newValue}))}
                  loading={optionsLoading}
                  valueKey="department_code"
                  labelKey="department_name"
                  placeholder="Chọn một hoặc nhiều phòng ban"
                />
                <CustomMultiSelect
                  label="Chức vụ"
                  options={options.jobPositions}
                  selectedValues={selected.jobPositionCodes}
                  onChange={(newValue) => setSelected(prev => ({...prev, jobPositionCodes: newValue}))}
                  loading={optionsLoading}
                  valueKey="code"
                  labelKey="name"
                  placeholder="Chọn một hoặc nhiều chức vụ"
                />
              </Stack>
            </Paper>
          )}

          {updateScope === 'STAFF' && (
            <Paper variant="outlined" sx={{p: 2.5, borderColor: 'divider'}}>
              <CustomMultiSelect
                label="Nhân viên"
                options={options.staff}
                selectedValues={selected.userLoginIds}
                onChange={(newValue) => setSelected(prev => ({...prev, userLoginIds: newValue}))}
                loading={optionsLoading}
                valueKey="user_login_id"
                labelKey="fullname"
                secondaryLabelKey="staff_code"
                placeholder="Tìm và chọn một hoặc nhiều nhân viên"
              />
            </Paper>
          )}

          <Box>
            <Typography variant="h6" gutterBottom fontWeight={600}>2. Nhập thông tin cập nhật</Typography>
            <Grid container spacing={2} mt={0.5}>
              <Grid item xs={12} sm={6}>
                <TextField label="Số giờ phép (*)" value={leaveHours} onChange={(e) => setLeaveHours(e.target.value)} type="number" inputProps={{ step: "0.5", min: "0" }} fullWidth />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField label="Kiểu cập nhật" value={updateType} onChange={(e) => setUpdateType(e.target.value)} select fullWidth>
                  <MenuItem value="ADDITIONAL">Cộng thêm</MenuItem>
                  <MenuItem value="RESET_TO">Thiết lập lại bằng</MenuItem>
                  <MenuItem value="SUBTRACT">Trừ đi</MenuItem>
                </TextField>
              </Grid>
            </Grid>
          </Box>

          <Button variant="contained" color="primary" onClick={handleSubmit} disabled={loading} sx={{ py: 1.5, mt: 2 }}>
            {loading ? <CircularProgress size={24} color="inherit" /> : "Thực hiện cập nhật"}
          </Button>
        </Stack>
      </Paper>
    </Box>
  );
};

const LeaveHoursPage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <LeaveHoursPageInternal />
    </ThemeProvider>
  )
}

export default LeaveHoursPage;