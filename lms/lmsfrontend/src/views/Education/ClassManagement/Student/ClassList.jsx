import React from 'react';
import {Card, CardContent, MuiThemeProvider} from "@material-ui/core";
import CurrentStudentClassList from "../../../../component/education/classmanagement/student/CurrentStudentClassList";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  }
}));

function ClassList(props) {
  const classes = useStyles();

  return (
    <MuiThemeProvider>
      <Card className={classes.card}>
        <CardContent>
          <CurrentStudentClassList/>
        </CardContent>
      </Card>
    </MuiThemeProvider>
  );
}

export default ClassList;