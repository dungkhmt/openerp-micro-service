import React from 'react';
import withScreenSecurity from "../../../../component/withScreenSecurity";
import {Card, CardContent} from "@material-ui/core";
import QuizTestListCreatedByCurrentTeacher
  from "../../../../component/education/teacher/QuizTestListCreatedByCurrentTeacher";
import QuizTestListParticipatedByCurrentTeacher
  from "../../../../component/education/teacher/QuizTestListParticipatedByCurrentTeacher";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(theme => ({
  currentTeacherQuizTestContainer: {
    display: 'flex',
    flexDirection: 'column',
    rowGap: '40px'
  }
}))

function QuizTestListOfCurrentTeacher(props) {
  const classes = useStyles();

  return (
    <div className={classes.currentTeacherQuizTestContainer}>
      <Card>
        <CardContent>
          <QuizTestListCreatedByCurrentTeacher/>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <QuizTestListParticipatedByCurrentTeacher/>
        </CardContent>
      </Card>
    </div>
  );
}

const screenName = "SCREEN_VIEW_QUIZ_TEST_TEACHER";
export default withScreenSecurity(QuizTestListOfCurrentTeacher, screenName, true);