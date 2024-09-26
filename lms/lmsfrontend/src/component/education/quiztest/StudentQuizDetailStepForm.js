import {useState} from "@hookstate/core";
import {Button} from "@material-ui/core";
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

export default function StudentQuizDetailStepForm(props) {
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

  // Keep track of checking state of all choices of all quiz
  const checkState = useState([]);
  //const [checkState, setCheckState] = useState([]);
  const [currentIndex, setCurrentIndex] = React.useState(0);
  const [question, setQuestion] = React.useState(null);
  const [currentCheckState, setCurrentCheckState] = React.useState(null);
  const [hasData, setHasData] = React.useState(false);

  function handleNextQuestionClick() {
    let i = currentIndex;
    //console.log("click Next, currentIndex = i = ", i);
    setCurrentIndex(i + 1);
    setQuestion(questions[i + 1]);
    setCurrentCheckState(checkState[i + 1]);
  }
  function handlePrevQuestionClick() {
    let i = currentIndex;
    //console.log("click Prev, currentIndex = i = ", i);
    setCurrentIndex(i - 1);
    setQuestion(questions[i - 1]);
    setCurrentCheckState(checkState[i - 1]);
  }
  function getQuestionList() {
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
        setQuestion(listQuestion[currentIndex]);
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
        //setCheckState(chkState);

        console.log("get list questions, checkState = ", chkState);
        setCurrentCheckState(checkState[currentIndex]);
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

  const onSave = (order, questionId, choseAnswers) => {
    request(
      "post",
      "/quiz-test-choose_answer-by-user",
      (res) => {
        //currentCheckState.submitted.set(true);

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

  function handleClickViewQuestion() {
    getQuestionList();
    setHasData(true);
  }
  const handleCloseSuccess = () => {
    setRequestSuccessfully(false);
  };

  const handleCloseError = () => {
    setRequestFailed(false);
  };

  useEffect(() => {
    getQuestionList();
    //console.log("finished getQuestionList");
  }, []);

  return (
    <div>
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
        <Button
          variant="contained"
          color="secondary"
          onClick={handleClickViewQuestion}
        >
          View Questions
        </Button>
      </div>
      <div style={{ display: "flex", justifyContent: "center" }}>
        {hasData && currentIndex < questions.length - 1 ? (
          <Button
            variant="contained"
            color="primary"
            onClick={handleNextQuestionClick}
          >
            Next
          </Button>
        ) : (
          ""
        )}
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        {hasData && currentIndex > 0 ? (
          <Button
            variant="contained"
            color="primary"
            onClick={handlePrevQuestionClick}
          >
            Prev
          </Button>
        ) : (
          ""
        )}
      </div>

      <div>
        {hasData && question && currentCheckState ? (
          <Quiz
            key={question.questionId}
            question={question}
            //choseAnswers={currentCheckState}
            choseAnswers={checkState[currentIndex]}
            order={currentIndex}
            onSave={onSave}
          />
        ) : (
          ""
        )}
      </div>
    </div>
  );
}
