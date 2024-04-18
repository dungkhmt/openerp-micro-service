import { Box, Typography, Button } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import Redirect from "../routers/Redirect";

const Home = () => {
  const { keycloak, initialized } = useKeycloak();

  if (initialized && keycloak.authenticated) {
    return <Redirect to="/dashboard" />;
  }

  const getGreeting = () => {
    const currentHour = new Date().getHours();
    if (currentHour < 12) {
      return "Chào buổi sáng";
    } else if (currentHour < 18) {
      return "Chào buổi chiều";
    } else {
      return "Chào buổi tối";
    }
  };

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
          backgroundImage: (theme) =>
            `linear-gradient(98deg, ${theme.palette.customColors.primaryGradient}, ${theme.palette.primary.main} 94%)`,
          color: (theme) => theme.palette.common.white,
        }}
        onClick={() => keycloak.login()}
      >
        Đăng nhập
      </Button>
    </Box>
  );
};

export default Home;
