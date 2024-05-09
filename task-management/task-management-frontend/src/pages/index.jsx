import { Icon } from "@iconify/react";
import { Box, Button, Typography, useTheme } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import Redirect from "../routers/Redirect";

const Home = () => {
  const { keycloak, initialized } = useKeycloak();

  const theme = useTheme();

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
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          mt: 6,
          backgroundImage:
            "linear-gradient(56deg, #45c4f9, #7d09ff 50.33%, #ff0be5)",
          color: theme.palette.common.white,
          borderRadius: "12px",
          inlineSize: "220px",
          blockSize: "48px",
          boxShadow: "0 4px 4px 0 rgba(87,75,172,.15)",
          position: "relative",
          transform: "translateY(0)",
          transition: "0.25s cubic-bezier(.5,0,.5,1)",
          fontSize: "18px",
          fontWeight: 700,
          textTransform: "capitalize",

          "& svg": {
            fontSize: "14px",
            marginInlineStart: "9px",

            [theme.breakpoints.up("md")]: {
              fontSize: "18px",
              marginInlineStart: "12px",
            },
          },

          [theme.breakpoints.up("md")]: {
            inlineSize: "300px",
            blockSize: "66px",
            fontSize: "24px",
          },

          "&:hover": {
            transform: "translateY(1px)",

            "& svg": {
              transform: "scale(1.2)",
            },
          },

          "&::before,&::after": {
            content: '""',
            position: "absolute",
            insetInlineStart: 0,
            insetBlockStart: 0,
            inlineSize: "100%",
            blockSize: "100%",
            transition: "opacity 0.25s cubic-bezier(.5,0,.5,1)",
            borderRadius: "12px",
            border: "1px solid rgba(0,0,0,.1)",
          },
        }}
        onClick={() => keycloak.login()}
      >
        <div>Đăng nhập</div>
        <Icon icon="grommet-icons:link-next" />
      </Button>
    </Box>
  );
};

export default Home;
