// EmployeeDetailsScreen.jsx
import React, {useEffect, useState, useCallback, useMemo} from "react";
import {useParams}from "react-router-dom";
import {request}from "@/api";
import AddStaffModal from "./modals/AddStaffModal";
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
  Alert as MuiAlert,
  Tooltip
} from "@mui/material";
import TimelineComponent from "@/components/item/TimelineItem";
import SalaryTab from "@/components/tab/SalaryTab";
import toast from "react-hot-toast";
import { ThemeProvider, CssBaseline } from "@mui/material";
import { theme } from "./theme";

// Import state quản lý quyền scope
import { useScopePermissionState, fetchPermittedScopes } from "../../state/scopePermissionState"; // Điều chỉnh đường dẫn nếu cần

const STAFF_ADMIN_SCOPE = "SCOPE_STAFF_ADMIN"; // Scope để sửa thông tin nhân viên chung
const SALARY_ADMIN_SCOPE = "SCOPE_SALARY_ADMIN"; // Scope để xem và sửa lương của bất kỳ ai

const formatDate = (dateString) => {
  if (!dateString) return "Hiện tại";
  const options = { year: "numeric", month: "short", day: "numeric" };
  try {
    return new Date(dateString).toLocaleDateString("vi-VN", options);
  } catch (e) {
    return dateString;
  }
};

const EmployeeDetailsInternal = () => {
  const { staffCode } = useParams();
  const [employeeDetails, setEmployeeDetails] = useState(null);
  const [jobHistory, setJobHistory] = useState([]);
  const [departmentHistory, setDepartmentHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [openEditModal, setOpenEditModal] = useState(false);
  const [selectedTab, setSelectedTab] = useState("profile");
  const [currentUserLoginId, setCurrentUserLoginId] = useState(null);

  // State quyền scope
  const scopeState = useScopePermissionState();
  const { permittedScopeIds, isFetched: scopesFetched, isFetching: scopesFetching } = scopeState.get();

  const canEditStaffGeneralInfo = useMemo(() => {
    return scopesFetched && permittedScopeIds.has(STAFF_ADMIN_SCOPE);
  }, [permittedScopeIds, scopesFetched]);

  const canAdminSalary = useMemo(() => {
    return scopesFetched && permittedScopeIds.has(SALARY_ADMIN_SCOPE);
  }, [permittedScopeIds, scopesFetched]);

  const isOwnProfile = useMemo(() => {
    return currentUserLoginId && employeeDetails?.user_login_id && currentUserLoginId === employeeDetails.user_login_id;
  }, [currentUserLoginId, employeeDetails]);

  const canViewSalaryTab = useMemo(() => {
    return canAdminSalary || isOwnProfile;
  }, [canAdminSalary, isOwnProfile]);

  const canEditSalaryData = useMemo(() => {
    return canAdminSalary; // Chỉ admin lương mới được sửa
  }, [canAdminSalary]);


  useEffect(() => {
    // Fetch quyền scope
    if (!scopesFetched && !scopesFetching) {
      fetchPermittedScopes();
    }

    // Fetch thông tin người dùng hiện tại
    request(
      "get",
      `/users/me`, // API lấy thông tin người dùng đang đăng nhập
      (res) => {
        if (res.data?.id) {
          setCurrentUserLoginId(res.data.id);
        }
      },
      { onError: (err) => console.error("Lỗi khi lấy thông tin người dùng hiện tại:", err) }
    );
  }, [scopesFetched, scopesFetching]);


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
      } else {
        // Nếu không có user_login_id, reset lịch sử
        setJobHistory([]);
        setDepartmentHistory([]);
      }
    } catch (err) {
      console.error(err);
      const errorMessage = err.response?.data?.message || err.message || "Không thể tải thông tin chi tiết nhân viên.";
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  }, [staffCode]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleEditSuccess = () => {
    fetchData();
    setOpenEditModal(false);
    toast.success("Cập nhật thông tin nhân viên thành công!");
  };


  if (loading && !employeeDetails) { // Chỉ hiển thị loading toàn màn hình khi chưa có dữ liệu lần đầu
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 128px)' }}>
        <CircularProgress />
        <Typography sx={{ml: 2}}>Đang tải thông tin nhân viên...</Typography>
      </Box>
    );
  }

  if (error && !employeeDetails) { // Chỉ hiển thị lỗi toàn màn hình khi không load được lần đầu
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 128px)', p:3 }}>
        <MuiAlert severity="error" sx={{width: '100%', maxWidth: 600}}>
          Lỗi: {error}
        </MuiAlert>
      </Box>
    );
  }

  if (!employeeDetails) { // Trường hợp không loading, không error nhưng không có employeeDetails
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 128px)', p:3 }}>
        <Typography>Không tìm thấy thông tin nhân viên.</Typography>
      </Box>
    );
  }


  const statusChip = employeeDetails.status === "ACTIVE"
    ? <Chip label="Đang hoạt động" color="success" size="small" />
    : <Chip label={employeeDetails.status || "Không xác định"} color="default" size="small" />;

  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: {xs: 2, md: 3}, mb: 2, position: 'relative' }}>
        {canEditStaffGeneralInfo && (
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
        )}
        <Grid container spacing={{xs: 2, md: 3}}>
          <Grid item xs={12} md="auto">
            <Avatar
              src={`https://ui-avatars.com/api/?name=${encodeURIComponent(
                employeeDetails.fullname || "N V"
              )}&background=random&size=128&font-size=0.33&bold=true&color=fff`}
              alt={employeeDetails.fullname}
              sx={{
                width: {xs: 80, md: 120},
                height: {xs: 80, md: 120},
                border: `3px solid ${theme.palette.divider}`,
                fontSize: '3rem'
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
          variant="fullWidth"
        >
          <Tab label="Hồ sơ & Lịch sử" value="profile" />
          {canViewSalaryTab && (
            <Tab label="Thông tin lương" value="salary" />
          )}
        </Tabs>
      </Paper>

      <Box sx={{pt: 0}}>
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

        {selectedTab === "salary" && canViewSalaryTab && employeeDetails.user_login_id && (
          <SalaryTab
            userLoginId={employeeDetails.user_login_id}
            canEdit={canEditSalaryData} // Truyền quyền sửa lương vào SalaryTab
          />
        )}
      </Box>

      {canEditStaffGeneralInfo && openEditModal && (
        <AddStaffModal
          open={openEditModal}
          onClose={() => setOpenEditModal(false)}
          onSubmitSuccess={handleEditSuccess}
          initialData={employeeDetails}
          isEditMode={true}
          // Giả sử AddStaffModal đã có props departments và jobPositions, nếu không thì cần thêm vào
          // departments={departmentsListFromSomewhere}
          // jobPositions={jobPositionsListFromSomewhere}
          titleProps={{sx: {fontSize: '1.15rem'}}}
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