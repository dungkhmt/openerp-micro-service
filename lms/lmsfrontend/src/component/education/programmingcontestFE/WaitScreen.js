import * as React from 'react';
import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(
  (theme) => ({
    root: {
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
    }
  })
)
export function WaitScreen(){
  const classes  =useStyles();
  return (
    <form className={classes.root} noValidate autoComplete="off">
      <br/><br/><br/><br/><br/><br/><br/><br/><br/>
      <Box sx={{ display: 'flex' }}>
        <CircularProgress />
      </Box>
    </form>

  );
}