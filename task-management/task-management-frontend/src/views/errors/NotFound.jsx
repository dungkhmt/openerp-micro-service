import { Box, Container, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { Helmet } from "react-helmet";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import { useEffect } from "react";

const useStyles = makeStyles((theme) => ({
  root: {
    backgroundColor: theme.palette.background.dark,
    height: "100%",
    paddingBottom: theme.spacing(3),
    paddingTop: theme.spacing(3),
  },
  image: {
    marginTop: 50,
    display: "inline-block",
    maxWidth: "100%",
    width: 560,
  },
  wrapper: {
    background: "#f4f6f8",
    height: window.innerHeight - 112,
  },
}));

const NotFound = () => {
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
        <title>Not Found | Task management</title>
      </Helmet>
      <Box
        ref={ref}
        sx={{
          borderRadius: 2,
          overflowY: "auto",
          display: "flex",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <Container maxWidth="md">
          <Typography align="center" color="textPrimary" variant="h4">
            <b>404: Trang bạn đang tìm không tồn tại</b>
          </Typography>
          <Typography align="center" color="textPrimary" variant="subtitle2">
            Bạn đã thử một trang không rõ ràng hoặc bạn đã đến đây theo lỗi. Hãy
            thử sử dụng điều hướng
          </Typography>
          <Box textAlign="center">
            <img
              alt="Đang phát triển"
              className={classes.image}
              src="/static/images/undraw_page_not_found_su7k.svg"
            />
          </Box>
        </Container>
      </Box>
    </>
  );
};

export default NotFound;
