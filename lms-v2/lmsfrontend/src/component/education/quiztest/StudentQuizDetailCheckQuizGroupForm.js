import Card from "@material-ui/core/Card";
import Grid from "@material-ui/core/Grid";
import Snackbar from "@material-ui/core/Snackbar";
import {makeStyles} from "@material-ui/core/styles";
import Alert from "@material-ui/lab/Alert";
import React, {useEffect} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import Quiz from "./Quiz";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  paper: {
    padding: theme.spacing(2),
    textAlign: "left",
    //color: theme.palette.text.secondary,
    color: "black",
    borderRadius: "20px",
    //background: "beige",
    background: "#EBEDEF",
  },
}));

export default function StudentQuizDetailCheckQuizGroupForm(props) {
  const { questions, quizGroupTestDetail, checkState } = props;

  const history = useHistory();
  const testQuizId = history.location.state?.testId;
  const classes = useStyles();

  //
  //const [questions, setQuestions] = React.useState([]);
  const [requestSuccessfully, setRequestSuccessfully] = React.useState(false);
  const [requestFailed, setRequestFailed] = React.useState(false);
  const [messageRequest, setMessageRequest] = React.useState(false);
  //const [quizGroupTestDetail, setQuizGroupTestDetail] = React.useState({});

  // Keep track of checking state of all choices of all quiz
  //const checkState = useState([]);

  const onSave = (order, questionId, choseAnswers) => {
    request(
      "post",
      "/quiz-test-choose_answer-by-user",
      (res) => {
        setMessageRequest("STORED!");
        setRequestSuccessfully(true);
        checkState[order].submitted.set(true);
        checkState[order].lastSubmittedAnswers.set(choseAnswers);
      },
      {
        400: () => {
          setMessageRequest("Cannot be empty!");
          setRequestFailed(true);
        },
        406: () => {
          setMessageRequest("Time Out!");
          setRequestFailed(true);
        },
      },
      {
        testId: testQuizId,
        questionId: questionId,
        quizGroupId: quizGroupTestDetail.quizGroupId,
        chooseAnsIds: choseAnswers,
      }
    );
  };

  const handleCloseSuccess = () => {
    setRequestSuccessfully(false);
  };

  const handleCloseError = () => {
    setRequestFailed(false);
  };

  useEffect(() => {
    //getQuestionList();
  }, []);

  return (
    <div className={classes.root}>
      <Card style={{ padding: "20px 20px 20px 20px" }}>
        <Snackbar
          open={requestSuccessfully}
          autoHideDuration={2000}
          onClose={handleCloseSuccess}
        >
          <Alert variant="filled" severity="success">
            {messageRequest}
          </Alert>
        </Snackbar>
        <Snackbar
          open={requestFailed}
          autoHideDuration={8000}
          onClose={handleCloseError}
        >
          <Alert variant="filled" severity="error">
            {messageRequest}
          </Alert>
        </Snackbar>

        {/* Quiz */}
        <Grid container spacing={3}>
          {quizGroupTestDetail.quizGroupId ? (
            questions != null ? (
              questions.map((question, idx) => (
                <Quiz
                  key={question.questionId}
                  question={question}
                  choseAnswers={checkState[idx]}
                  order={idx}
                  onSave={onSave}
                />
              ))
            ) : (
              <p style={{ justifyContent: "center" }}>
                {" "}
                Questions not available
              </p>
            )
          ) : (
            <p style={{ justifyContent: "center" }}>
              {" "}
              Exam has not been available to student yet{" "}
            </p>
          )}
        </Grid>
      </Card>
    </div>
  );
}
