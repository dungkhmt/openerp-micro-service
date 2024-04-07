import {CircularProgress, Grid, Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
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

const Loading = () => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <Grid container justify="center" spacing={4}>
        <Grid item lg={6} xs={12}>
          <div className={classes.content}>
            <CircularProgress size={100} />
            <Typography variant="h5">Loading ....</Typography>
          </div>
        </Grid>
      </Grid>
    </div>
  );
};

export default Loading;
