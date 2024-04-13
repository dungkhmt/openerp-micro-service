import React from "react";
import {Box, Container, Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";

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
            className={classes.image}
            src="/static/images/undraw_page_not_found_su7k.svg"
          />
        </Box>
      </Container>
    </Box>
  );
};

export default NotFound;
