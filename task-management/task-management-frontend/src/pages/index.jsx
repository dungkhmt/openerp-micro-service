import React, { useEffect, useMemo } from "react";
import { Icon } from "@iconify/react";
import { Box, Button, Typography, useTheme, CircularProgress } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import Redirect from "../routers/Redirect";
import { useScopePermissionState, fetchPermittedScopes } from "@/state/scopePermissionState";

const ADMIN_SCOPE = "SCOPE_DASHBOARD_ADMIN";
const STAFF_SCOPE = "SCOPE_DASHBOARD_STAFF";

const Home = () => {
  const { keycloak, initialized } = useKeycloak();
  const theme = useTheme();

  const scopeState = useScopePermissionState();
  const { permittedScopeIds, isFetched: scopesFetched, isFetching: scopesFetching } = scopeState.get();

  // useEffect giờ đây chỉ dùng để fetch dữ liệu (side effect)
  useEffect(() => {
    // Fetch quyền scope khi component mount nếu chưa có
    if (initialized && keycloak.authenticated && !scopesFetched && !scopesFetching) {
      fetchPermittedScopes();
    }
  }, [initialized, keycloak.authenticated, scopesFetched, scopesFetching]);

  const admin = useMemo(() => {
    return scopesFetched && permittedScopeIds.has(ADMIN_SCOPE);
  }, [permittedScopeIds, scopesFetched]);

  const staff = useMemo(() => {
    return scopesFetched && permittedScopeIds.has(STAFF_SCOPE);
  }, [permittedScopeIds, scopesFetched]);

  // Hiển thị loading trong khi chờ Keycloak và scope được khởi tạo
  if (!initialized || (keycloak.authenticated && scopesFetching)) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }


  if (initialized && keycloak.authenticated) {
    if (scopesFetched) {
      if(admin){
        return  <Redirect to="/dashboard" />;
      }
      else if(staff){
        return <Redirect to="/dashboard/employee" />;
      }
    }
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
        <Typography sx={{ml: 2}}>Đang kiểm tra quyền truy cập...</Typography>
      </Box>
    );
  }

  // Hàm chào hỏi
  const getGreeting = () => {
    const currentHour = new Date().getHours();
    if (currentHour < 12) return "Chào buổi sáng";
    if (currentHour < 18) return "Chào buổi chiều";
    return "Chào buổi tối";
  };

  // Nếu không đăng nhập, hiển thị trang chủ
  return (
    <Box
      sx={{
        width: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
        gap: 4,
        mt: 10,
      }}
    >
      <Typography variant="h4" className="greet">
        {getGreeting()}
      </Typography>
      <Typography variant="h6" color="text.secondary">
        Đăng nhập để trải nghiệm những tính năng dành riêng cho bạn
      </Typography>
      <Button
        sx={{
          display: "flex", alignItems: "center", justifyContent: "center", mt: 6,
          backgroundImage: "linear-gradient(56deg, #45c4f9, #7d09ff 50.33%, #ff0be5)",
          color: theme.palette.common.white, borderRadius: "12px", inlineSize: "220px",
          blockSize: "48px", boxShadow: "0 4px 4px 0 rgba(87,75,172,.15)",
          position: "relative", transform: "translateY(0)", transition: "0.25s cubic-bezier(.5,0,.5,1)",
          fontSize: "18px", fontWeight: 700, textTransform: "capitalize",
          "& svg": {
            fontSize: "14px", marginInlineStart: "9px",
            [theme.breakpoints.up("md")]: { fontSize: "18px", marginInlineStart: "12px" },
          },
          [theme.breakpoints.up("md")]: { inlineSize: "300px", blockSize: "66px", fontSize: "24px" },
          "&:hover": {
            transform: "translateY(1px)",
            "& svg": { transform: "scale(1.2)" },
          },
          "&::before,&::after": {
            content: '""', position: "absolute", insetInlineStart: 0, insetBlockStart: 0,
            inlineSize: "100%", blockSize: "100%", transition: "opacity 0.25s cubic-bezier(.5,0,.5,1)",
            borderRadius: "12px", border: "1px solid rgba(0,0,0,.1)",
          },
        }}
        onClick={() => keycloak.login()}
      >
        <span>Đăng nhập</span>
        <Icon icon="grommet-icons:link-next" />
      </Button>
    </Box>
  );
};

export default Home;