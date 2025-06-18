import { Box, Container, Typography, Button } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { Helmet } from "react-helmet";
import { useEffect } from "react";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import { useNavigate } from "react-router";

const useStyles = makeStyles((theme) => ({
  root: {
    backgroundColor: theme.palette.background.dark,
    height: "100%",
    paddingBottom: theme.spacing(3),
    paddingTop: theme.spacing(3),
  },
  image: {
    display: "inline-block",
    maxWidth: "100%",
    width: 300,
  },
  wrapper: {
    background: "#f4f6f8",
    height: window.innerHeight - 112,
  },
}));

const InvitationExpired = () => {
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
        <title>410: Lời mời đã hết hạn | Task Management</title>
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
            <b>Lời mời này đã hết hạn</b>
          </Typography>
          <Typography align="center" variant="subtitle2" color="textPrimary">
            Lời mời không còn hợp lệ. Hãy yêu cầu người quản trị gửi lại lời mời
            mới.
          </Typography>
          <Box textAlign="center">
            <img
              alt="Invitation Expired"
              className={classes.image}
              src="/static/images/invitation_expired.svg"
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

export default InvitationExpired;
