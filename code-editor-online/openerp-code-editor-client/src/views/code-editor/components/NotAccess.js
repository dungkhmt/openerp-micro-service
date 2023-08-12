import { Box, Button, Container, Typography } from "@mui/material";
import { useHistory } from "react-router";

const styles = {
  wrapper: {
    background: "#f4f6f8",
    height: window.innerHeight - 112,
  },
};

const NotAccess = () => {
  const history = useHistory();
  return (
    <Box
      display="flex"
      flexDirection="column"
      height="100%"
      justifyContent="center"
      sx={styles.wrapper}
    >
      <Container maxWidth="md">
        <Typography align="center" color="textPrimary" variant="h4">
          <b>Bạn cần có quyền truy cập</b>
        </Typography>
        <Typography align="center" color="textPrimary" variant="subtitle2">
          Hãy yêu cầu quyền truy cập hoặc chuyển sang một tài khoản khác có quyền truy cập
        </Typography>
        <Box textAlign="center">
          <img
            alt="Under development"
            style={{
              marginTop: 50,
              display: "inline-block",
              maxWidth: "100%",
              width: 560,
            }}
            src="/static/images/undraw_page_not_found_su7k.svg"
          />
          <Box mt={2}>
            <Button
              variant="contained"
              color="primary"
              onClick={() => {
                history.push("/code-editor/create-join-room");
              }}
            >
              Quay lại
            </Button>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default NotAccess;
