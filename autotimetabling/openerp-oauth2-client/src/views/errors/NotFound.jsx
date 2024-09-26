import { Box, Container, Typography } from "@mui/material";

const styles = {
  // root: (theme) => ({
  //   backgroundColor: theme.palette.background.dark,
  //   height: "100%",
  //   paddingBottom: theme.spacing(3),
  //   paddingTop: theme.spacing(3),
  // }),
  wrapper: {
    background: "#f4f6f8",
    height: window.innerHeight - 112,
  },
};

const NotFound = () => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      // height="100%"
      justifyContent="center"
      sx={styles.wrapper}
    >
      <Container maxWidth="md">
        <Typography align="center" color="textPrimary" variant="h4">
          <b>404: The page you are looking for isn’t here</b>
        </Typography>
        <Typography align="center" color="textPrimary" variant="subtitle2">
          You either tried some shady route or you came here by mistake.
          Whichever it is, try using the navigation
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
        </Box>
      </Container>
    </Box>
  );
};

export default NotFound;
