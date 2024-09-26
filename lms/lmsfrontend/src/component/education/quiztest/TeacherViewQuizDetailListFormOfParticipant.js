import {useState} from "@hookstate/core";
import Card from "@material-ui/core/Card";
import Grid from "@material-ui/core/Grid";
import Snackbar from "@material-ui/core/Snackbar";
import {makeStyles} from "@material-ui/core/styles";
import Alert from "@material-ui/lab/Alert";
import React, {useEffect} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
//import Quiz from "./Quiz";
import TeacherViewAQuiz from "./TeacherViewAQuiz";

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

export default function TeacherViewQuizDetailListFormOfParticipant(props) {
  const history = useHistory();
  const testId = props.testId;
  const classes = useStyles();
  const participantId = props.participantId;
  //
  const [questions, setQuestions] = React.useState([]);
  const [requestSuccessfully, setRequestSuccessfully] = React.useState(false);
  const [requestFailed, setRequestFailed] = React.useState(false);
  const [messageRequest, setMessageRequest] = React.useState(false);
  const [quizGroupTestDetail, setQuizGroupTestDetail] = React.useState({});

  // Keep track of checking state of all choices of all quiz
  const checkState = useState([]);

  function getQuestionList() {
    request(
      "get",
      "/get-quiz-questions-assigned-to-participant/" +
        testId +
        "/" +
        participantId,
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
          }

          chkState.push(choices);
        });

        checkState.set(chkState);
      },
      {
        401: () => {},
        406: () => {
          setMessageRequest("Quá thời gian làm bài!");
          setRequestFailed(true);
        },
      }
    );
  }

  const onSave = (order, questionId, choseAnswers) => {
    request(
      "post",
      "/quiz-test-choose_answer-by-user",
      (res) => {
        checkState[order].submitted.set(true);
        setMessageRequest("Đã lưu vào hệ thống!");
        setRequestSuccessfully(true);
      },
      {
        400: () => {
          setMessageRequest("Không được để trống!");
          setRequestFailed(true);
        },
        406: () => {
          setMessageRequest("Quá thời gian làm bài!");
          setRequestFailed(true);
        },
      },
      {
        testId: testId,
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
    getQuestionList();
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
                <TeacherViewAQuiz
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
                Chưa có câu hỏi cho mã đề này
              </p>
            )
          ) : (
            <p style={{ justifyContent: "center" }}>
              {" "}
              Chưa phát đề cho sinh viên{" "}
            </p>
          )}
        </Grid>
      </Card>
    </div>
  );
}
