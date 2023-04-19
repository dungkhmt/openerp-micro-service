import {Grid, Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import ErrorIcon from "@material-ui/icons/Error";
import React from "react";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
  },
  content: {
    paddingTop: 150,
    textAlign: "center",
  },
}));

const NotAuthorized = () => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <Grid
        container
        justify="center"
        spacing={4}
        style={{ backgroundColor: "#ffe6e6" }}
      >
        <Grid item lg={6} xs={12}>
          <div className={classes.content}>
            <ErrorIcon color="error" fontSize="large" />
            <Typography variant="h4">You need permissions</Typography>
            <Typography variant="h6">
              You do not have permission to access this component. Contact
              administrator if you need help.
            </Typography>
          </div>
        </Grid>
      </Grid>
    </div>
  );
};

export default NotAuthorized;
