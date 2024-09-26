import {Card, CardContent} from "@material-ui/core";
import {makeStyles, MuiThemeProvider} from "@material-ui/core/styles";
import React from "react";
import ClassRegistrationTable from "../../../../component/education/classmanagement/student/ClassRegistrationTable";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
    borderRadius: "6px",
  },
  registrationBtn: {},
}));

function ClassRegistration() {
  const classes = useStyles();

  return (
    <MuiThemeProvider>
      <Card className={classes.card}>
        <CardContent>
          <ClassRegistrationTable/>
        </CardContent>
      </Card>
    </MuiThemeProvider>
  );
}

export default ClassRegistration;
