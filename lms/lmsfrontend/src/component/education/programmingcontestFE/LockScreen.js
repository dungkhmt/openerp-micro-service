import {makeStyles} from "@material-ui/core/styles";
import React from "react";
import {Typography} from "@material-ui/core";
import LockIcon from '@mui/icons-material/Lock';

const useStyles = makeStyles(
  (theme) => ({
    root: {
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
    }
  })
)

export default function LockScreen(){
  const classes  =useStyles();
  return(
    <div>
      <form className={classes.root} noValidate autoComplete="off">
        <LockIcon/>
        <Typography variant="h5" component="h2">
          Subscribe to unlock.
        </Typography>
      </form>
    </div>
  );
}