import { useState } from "@hookstate/core";
import { Button, Card } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from "@material-ui/core/styles";
import Alert from "@material-ui/lab/Alert";
import React, { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { request } from "../../../api";
import Quiz from "./Quiz";
import { LinearProgress } from "@mui/material";
import { LoadingButton } from "@mui/lab";
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

export default function StudentQuizDetailListForm(props) {
  const history = useHistory();
  //const testQuizId = history.location.state?.testId;
  const testQuizId = props.testId;
  const classes = useStyles();

  //
  const [questions, setQuestions] = React.useState([]);
  const [requestSuccessfully, setRequestSuccessfully] = React.useState(false);
  const [requestFailed, setRequestFailed] = React.useState(false);
  const [messageRequest, setMessageRequest] = React.useState(false);
  const [quizGroupTestDetail, setQuizGroupTestDetail] = React.useState({});
  const [loading, setLoading] = React.useState(false);
  // Keep track of checking state of all choices of all quiz
  const checkState = useState([]);

  function getQuestionList() {
    setLoading(true);
    request(
      "get",
      "/get-quiz-test-participation-group-question/" + testQuizId,
      (res) => {
        const {
          listQuestion,
          participationExecutionChoice,
          ...quizGroupTestDetail
        } = res.data;

        setQuestions(listQuestion);
        setQuizGroupTestDetail(quizGroupTestDetail);

        // Restore test result
        // TODO: optimize code
        const chkState = [];

        listQuestion.forEach((question) => {
          const choices = {};
          const choseAnswers =
            participationExecutionChoice[question.questionId];

          question.quizChoiceAnswerList.forEach((ans) => {
            choices[ans.choiceAnswerId] = false;
          });

          choices.submitted = false;
          if (choseAnswers) {
            choseAnswers.forEach((choseAnsId) => {
              choices[choseAnsId] = true;
            });

            choices.submitted = true;
            choices["lastSubmittedAnswers"] = choseAnswers;
          } else {
            choices["lastSubmittedAnswers"] = [];
          }

          chkState.push(choices);
        });

        checkState.set(chkState);

        setLoading(false);
      },
      {
        401: () => {
          setLoading(false);
        },
        406: () => {
          setMessageRequest("Time Out!");
          setRequestFailed(true);
          setLoading(false);
        },
      }
    );
  }

  const onSave = (order, questionId, choseAnswers) => {
    setLoading(true);
    request(
      "post",
      //"/quiz-test-choose_answer-by-user",
      //"/quiz-test-choose_answer-by-user-v2-asynchronous",
      "/submit-quiz-test-choose_answer-by-user",
      (res) => {
        setMessageRequest("STORED!");
        setRequestSuccessfully(true);
        checkState[order].submitted.set(true);
        //checkState[order].lastSubmittedAnswers.set(choseAnswers);
        let resChoseAnswer = [];
        //for (let i = 0; i < res.data.length; i++) {
        //  resChoseAnswer.push(res.data[i].choiceAnswerId);
        //}
        if (res.data.submissionId != null) {
          for (let i = 0; i < res.data.choiceAnswerIds.length; i++) {
            resChoseAnswer.push(res.data.choiceAnswerIds[i]);
          }
        }
        console.log("res choseanswer = ", resChoseAnswer);
        checkState[order].lastSubmittedAnswers.set(resChoseAnswer);
        setLoading(false);
      },
      {
        400: () => {
          setMessageRequest("Cannot be empty!");
          setRequestFailed(true);
          setLoading(false);
        },
        406: () => {
          setMessageRequest("Time Out!");
          setRequestFailed(true);
          setLoading(false);
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
  function handleClickViewQuestion() {
    getQuestionList();
  }
  function handleRefresh() {
    request(
      "get",
      "/get-quiz-test-participation-group-question-reload-heavy/" + testQuizId,
      (res) => {
        const {
          listQuestion,
          participationExecutionChoice,
          ...quizGroupTestDetail
        } = res.data;

        setQuestions(listQuestion);
        setQuizGroupTestDetail(quizGroupTestDetail);

        // Restore test result
        // TODO: optimize code
        const chkState = [];

        listQuestion.forEach((question) => {
          const choices = {};
          const choseAnswers =
            participationExecutionChoice[question.questionId];

          question.quizChoiceAnswerList.forEach((ans) => {
            choices[ans.choiceAnswerId] = false;
          });

          choices.submitted = false;
          if (choseAnswers) {
            choseAnswers.forEach((choseAnsId) => {
              choices[choseAnsId] = true;
            });

            choices.submitted = true;
            choices["lastSubmittedAnswers"] = choseAnswers;
          } else {
            choices["lastSubmittedAnswers"] = [];
          }

          chkState.push(choices);
        });

        checkState.set(chkState);
      },
      {
        401: () => {},
        406: () => {
          setMessageRequest("Time Out!");
          setRequestFailed(true);
        },
      }
    );
  }
  const handleCloseSuccess = () => {
    setRequestSuccessfully(false);
  };

  const handleCloseError = () => {
    setRequestFailed(false);
  };

  useEffect(() => {
    getQuestionList();
  }, []);

  return (
    <div className={classes.root}>
      {loading && <LinearProgress />}
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
        <div style={{}}>
          <LoadingButton
            variant="contained"
            color="secondary"
            loading={loading}
            onClick={handleClickViewQuestion}
          >
            View Questions
          </LoadingButton>
          {quizGroupTestDetail.judgeMode === "BATCH_LAZY_EVALUATION" ? (
            <Button
              variant="contained"
              color="secondary"
              onClick={handleRefresh}
            >
              REFRESH (limited)
            </Button>
          ) : null}
        </div>

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
                  loading={loading}
                  judgeMode={quizGroupTestDetail.judgeMode}
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
