// EmployeeDetailsScreen.jsx
import React, {useEffect, useState, useCallback} from "react";
import {useParams} from "react-router-dom";
import {request}from "@/api";
import AddStaffModal from "./modals/AddStaffModal"; // Giả sử đường dẫn đúng
import EditIcon from "@mui/icons-material/Edit";
import {
  Divider,
  Tab,
  Tabs,
  Box,
  Paper,
  Typography,
  Grid,
  Avatar,
  IconButton,
  Chip,
  CircularProgress,
  Alert as MuiAlert, // Đổi tên để tránh xung đột nếu có Alert khác
  Tooltip
} from "@mui/material";
import TimelineComponent from "@/components/item/TimelineItem"; // Đổi tên import cho rõ ràng
import SalaryTab from "@/components/tab/SalaryTab";           // Đổi tên import cho rõ ràng
import toast from "react-hot-toast";
import { ThemeProvider, CssBaseline } from "@mui/material";
import { theme } from "./theme"; // Giả sử theme.js cùng cấp hoặc có đường dẫn đúng

const formatDate = (dateString) => {
  if (!dateString) return "Hiện tại";
  // Dùng dayjs nếu đã import, hoặc giữ nguyên new Date nếu muốn đơn giản
  // Hoặc có thể dùng dayjs(dateString).format("DD/MM/YYYY") nếu dayjs được tích hợp
  const options = { year: "numeric", month: "short", day: "numeric" };
  try {
    return new Date(dateString).toLocaleDateString("vi-VN", options);
  } catch (e) {
    return dateString; // Trả về chuỗi gốc nếu không parse được
  }
};

const EmployeeDetailsInternal = () => {
  const { staffCode } = useParams();
  const [employeeDetails, setEmployeeDetails] = useState(null);
  const [jobHistory, setJobHistory] = useState([]);
  const [departmentHistory, setDepartmentHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null); // Lưu trữ thông điệp lỗi
  const [openEditModal, setOpenEditModal] = useState(false);
  const [selectedTab, setSelectedTab] = useState("profile");

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const empRes = await new Promise((resolve, reject) => {
        request("get", `/staffs/${staffCode}`, (res) => resolve(res.data?.data), { onError: reject });
      });
      setEmployeeDetails(empRes);

      if (empRes?.user_login_id) {
        const userLoginId = empRes.user_login_id;
        // Fetch Job History
        request(
          "get",
          `/staffs/${userLoginId}/job-position`,
          (res) => {
            const history = res.data?.data || [];
            setJobHistory(
              history.map((job) => ({
                title: job.job_position?.name || "Không có thông tin",
                fromDate: formatDate(job.from_date),
                thruDate: formatDate(job.thru_date),
              }))
            );
          },
          { onError: (err) => toast.error("Không thể tải lịch sử chức vụ.") }
        );

        // Fetch Department History
        request(
          "get",
          `/staffs/${userLoginId}/department`,
          (res) => {
            const history = res.data?.data || [];
            setDepartmentHistory(
              history.map((dept) => ({
                title: dept.department_model?.department_name || "Không có thông tin",
                fromDate: formatDate(dept.from_date),
                thruDate: formatDate(dept.thru_date),
              }))
            );
          },
          { onError: (err) => toast.error("Không thể tải lịch sử phòng ban.") }
        );
      }
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.message || "Không thể tải thông tin chi tiết nhân viên.");
      toast.error(err.response?.data?.message || err.message || "Lỗi tải dữ liệu.");
    } finally {
      setLoading(false);
    }
  }, [staffCode]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleEditSuccess = (updatedData) => {
    // setEmployeeDetails((prev) => ({ ...prev, ...updatedData }));
    fetchData(); // Gọi lại fetchData để làm mới toàn bộ thông tin
    setOpenEditModal(false);
    toast.success("Cập nhật thông tin nhân viên thành công!");
  };


  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 128px)' }}>
        <CircularProgress />
        <Typography sx={{ml: 2}}>Đang tải thông tin nhân viên...</Typography>
      </Box>
    );
  }

  if (error || !employeeDetails) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 128px)', p:3 }}>
        <MuiAlert severity="error" sx={{width: '100%', maxWidth: 600}}>
          Lỗi: {error || "Không thể tải thông tin chi tiết nhân viên."}
        </MuiAlert>
      </Box>
    );
  }

  const statusChip = employeeDetails.status === "ACTIVE"
    ? <Chip label="Đang hoạt động" color="success" size="small" />
    : <Chip label={employeeDetails.status || "Không xác định"} color="default" size="small" />;

  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: {xs: 2, md: 3}, mb: 2, position: 'relative' }}>
        <Tooltip title="Chỉnh sửa thông tin">
          <IconButton
            onClick={() => setOpenEditModal(true)}
            sx={{
              position: "absolute",
              top: {xs: 8, md:16},
              right: {xs: 8, md:16},
              color: "primary.main"
            }}
          >
            <EditIcon />
          </IconButton>
        </Tooltip>
        <Grid container spacing={{xs: 2, md: 3}}>
          <Grid item xs={12} md="auto">
            <Avatar
              src={`https://ui-avatars.com/api/?name=${encodeURIComponent(
                employeeDetails.fullname || "N V" // Handle potential null fullname
              )}&background=random&size=128&font-size=0.33&bold=true&color=fff`}
              alt={employeeDetails.fullname}
              sx={{
                width: {xs: 80, md: 120},
                height: {xs: 80, md: 120},
                border: `3px solid ${theme.palette.divider}`,
                fontSize: '3rem' // Fallback for ui-avatars font size if needed
              }}
            />
          </Grid>
          <Grid item xs={12} md>
            <Typography variant="h4" component="h1" gutterBottom sx={{fontWeight: 'bold'}}>
              {employeeDetails.fullname}
            </Typography>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              {employeeDetails.job_position?.job_position_name || "Chưa cập nhật chức vụ"}
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', mt: 1, mb: 1.5 }}>
              <Typography variant="body2" color="text.secondary" sx={{ mr: 1 }}>Trạng thái:</Typography>
              {statusChip}
            </Box>
            <Divider sx={{ my: 1.5 }} />
            <Grid container spacing={1.5}>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="text.secondary">
                  <strong>Mã nhân viên:</strong> {employeeDetails.staff_code || "-"}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="text.secondary">
                  <strong>Phòng ban:</strong>{" "}
                  {employeeDetails.department?.department_name || "Chưa cập nhật"}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="text.secondary">
                  <strong>Email:</strong> {employeeDetails.email || "-"}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="text.secondary">
                  <strong>Ngày vào làm:</strong>{" "}
                  {employeeDetails.date_of_join ? formatDate(employeeDetails.date_of_join) : "-"}
                </Typography>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Paper>

      <Paper sx={{mb:2}}>
        <Tabs
          value={selectedTab}
          onChange={(e, value) => setSelectedTab(value)}
          indicatorColor="primary"
          textColor="primary"
          variant="fullWidth" // hoặc "scrollable" nếu có nhiều tab
        >
          <Tab label="Hồ sơ & Lịch sử" value="profile" />
          <Tab label="Thông tin lương" value="salary" />
        </Tabs>
      </Paper>

      <Box sx={{pt: 0}}> {/* Giảm padding top cho nội dung tab */}
        {selectedTab === "profile" && (
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <TimelineComponent title="Lịch sử Chức vụ" items={jobHistory} />
            </Grid>
            <Grid item xs={12} md={6}>
              <TimelineComponent title="Lịch sử Phòng ban" items={departmentHistory} />
            </Grid>
          </Grid>
        )}

        {selectedTab === "salary" && employeeDetails.user_login_id && (
          <SalaryTab userLoginId={employeeDetails.user_login_id} />
        )}
      </Box>

      {openEditModal && (
        <AddStaffModal
          open={openEditModal}
          onClose={() => setOpenEditModal(false)}
          onSubmitSuccess={handleEditSuccess} // Changed prop name for clarity
          initialData={employeeDetails} // Changed prop name to initialData
          isEditMode={true} // Indicate edit mode
          titleProps={{sx: {fontSize: '1.15rem'}}} // Example for title styling
        />
      )}
    </Box>
  );
};

const EmployeeDetailsScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <EmployeeDetailsInternal />
    </ThemeProvider>
  );
};

export default EmployeeDetailsScreen;
