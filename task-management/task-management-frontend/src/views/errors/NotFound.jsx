import { Box, Container, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";

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

  return (
    <Box
      display="flex"
      flexDirection="column"
      // height="100%"
      justifyContent="center"
      className={classes.wrapper}
      sx={{
        borderRadius: 2,
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
  );
};

export default NotFound;
