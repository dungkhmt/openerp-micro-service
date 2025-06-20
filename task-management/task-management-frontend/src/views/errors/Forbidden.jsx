import { Box, Button, Container, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { Helmet } from "react-helmet";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import { useEffect } from "react";
import { useNavigate } from "react-router";

const useStyles = makeStyles((theme) => ({
  root: {
    backgroundColor: theme.palette.background.dark,
    height: "100%",
    paddingBottom: theme.spacing(3),
    paddingTop: theme.spacing(3),
  },
  image: {
    marginTop: 20,
    marginBottom: 20,
    display: "inline-block",
    maxWidth: "100%",
    width: 400,
  },
  wrapper: {
    background: "#f4f6f8",
    height: window.innerHeight - 112,
  },
}));

const Forbidden = () => {
  const navigate = useNavigate();
  const classes = useStyles();
  const { ref, updateMaxHeight } = usePreventOverflow();

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  return (
    <>
      <Helmet>
        <title>403: Truy cập bị từ chối | Task Management</title>
      </Helmet>
      <Box
        ref={ref}
        sx={{
          borderRadius: 2,
          overflowY: "auto",
          display: "flex",
          alignItems: "center",
          flexDirection: "column",
          pb: 10,
        }}
      >
        <Container maxWidth="md">
          <Typography align="center" variant="h4" color="textPrimary">
            <b>403: Bạn không có quyền truy cập vào trang này</b>
          </Typography>
          <Typography align="center" variant="subtitle2" color="textPrimary">
            Vui lòng liên hệ quản trị viên hoặc thử tài khoản khác có quyền truy
            cập.
          </Typography>
          <Box textAlign="center">
            <img
              alt="Access Denied"
              className={classes.image}
              src="/static/images/403_forbidden.svg"
            />
          </Box>
          <Box mt={4} textAlign="center">
            <Button
              variant="contained"
              color="primary"
              onClick={() => navigate("/")}
              sx={{ textTransform: "none" }}
            >
              Về trang chủ
            </Button>
          </Box>
        </Container>
      </Box>
    </>
  );
};

export default Forbidden;
