import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import Checkbox from "@material-ui/core/Checkbox";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from "@material-ui/core/styles";
import Alert from "@material-ui/lab/Alert";
import React, { useEffect, useState } from "react";
//import { useHistory } from "react-router-dom";
import { request } from "../../../../api";
import { useParams } from "react-router";

// const useCheckBoxStyles = makeStyles((theme) => ({
//   root: {
//     display: "flex",
//   },
//   formControl: {
//     margin: theme.spacing(3),
//   },
// }));

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

export default function LearningSessionStudentViewQuizTestList(props) {
  const params = useParams();
  //const testQuizId = history.location.state.testId;
  const [testQuizId, setTestQuizId] = useState(null);
  const testId = params.testId;
  const [ListQuestions, setListQuestions] = useState([]);
  const [sucessRequest, setSucessRequest] = useState(false);
  const [errorRequest, setErrorRequest] = useState(false);
  const [messageRequest, setMessageRequest] = useState(false);
  const [sessionDetail, setSessionDetail] = useState(null);
  const [quizGroupTestDetail, setquizGroupTestDetail] = useState({});
  const classes = useStyles();
  // const Checkboxclasses = useCheckBoxStyles();
  const [stateCheckBox, setStateCheckBox] = useState({});
  const [mapS, setMapS] = useState({});

  function getSessionDetail() {
    request(
      // token,
      // history,
      "get",
      "/get-sessions-of-class/" + testId,
      (res) => {
        setSessionDetail(res.data);
      }
    );
  }
  async function getQuestionList() {
    request(
      // token,
      // history,
      "get",
      "/get-questions-of-interactive-quiz-student/" + testId,
      (res) => {
        setListQuestions(res.data);
        // setquizGroupTestDetail(res.data);
        setTestQuizId(testId);
        let tmpObj = {};
        let tmpMap = {};
        res.data.listQuestion.forEach((element) => {
          tmpMap[element["questionId"]] = false;

          tmpObj[element["questionId"]] = new Object();
          element["quizChoiceAnswerList"].forEach((ele) => {
            tmpObj[element["questionId"]][ele["choiceAnswerId"]] =
              res.data.participationExecutionChoice.hasOwnProperty(
                element["questionId"]
              )
                ? res.data.participationExecutionChoice[
                    element["questionId"]
                  ].includes(ele["choiceAnswerId"])
                : false;
          });
        });
        setMapS(tmpMap);
        //console.log(tmpObj);
        setStateCheckBox(tmpObj);
      },
      {
        401: () => {},
        406: () => {
          setMessageRequest("Time Out!");
          setErrorRequest(true);
        },
        403: () => {
          setMessageRequest("The test has not started!");
          setErrorRequest(true);
        },
      }
    );
  }

  const handleClick = (quesId) => {
    console.log(quesId);
    console.log(stateCheckBox);
    if (Object.keys(stateCheckBox).length === 0) {
      setMessageRequest("Không được để trống!");
      setErrorRequest(true);
      return;
    }
    let listAns = [];
    Object.keys(stateCheckBox[quesId]).map((element, index) => {
      if (stateCheckBox[quesId][element] === true) {
        listAns.push(element);
      }
    });
    let tmpOb = {
      interactiveQuizId: testId,
      questionId: quesId,
      // quizGroupId: quizGroupTestDetail.quizGroupId,
      choiceAnswerId: listAns,
    };
    if (listAns.length === 0) {
      setMessageRequest("Không được để trống!");
      setErrorRequest(true);
      return;
    }
    console.log(tmpOb);
    request(
      // token,
      // history,
      "post",
      "/submit-interactive-quiz-answer-by-user",
      (res) => {
        console.log(res);
        setMessageRequest("Đã lưu vào hệ thống!");
        setSucessRequest(true);
        mapS[quesId] = true;
        setMapS(mapS);
      },
      {
        400: () => {
          setMessageRequest("Quá thời gian làm bài!");
          setErrorRequest(true);
        },
        406: () => {
          setMessageRequest("Quá thời gian làm bài!");
          setErrorRequest(true);
        },
      },
      tmpOb
    );
  };

  const handleChange = (event, quesID) => {
    setStateCheckBox({
      ...stateCheckBox,
      [quesID]: {
        ...stateCheckBox[quesID],
        [event.target.name]: event.target.checked,
      },
    });
  };

  const handleCloseSucess = () => {
    setSucessRequest(false);
  };
  const handleCloseError = () => {
    setErrorRequest(false);
  };
  const handleClickGetQuiz = () => {
    getQuestionList();
  };
  useEffect(() => {
    //getQuestionList();
    //getSessionDetail();
  }, []);

  return (
    <div className={classes.root}>
      <Card style={{ padding: "20px 20px 20px 20px" }}>
        <Snackbar
          open={sucessRequest}
          autoHideDuration={2000}
          onClose={handleCloseSucess}
        >
          <Alert variant="filled" severity="success">
            {messageRequest}
          </Alert>
        </Snackbar>
        <Snackbar
          open={errorRequest}
          autoHideDuration={8000}
          onClose={handleCloseError}
        >
          <Alert variant="filled" severity="error">
            {messageRequest}
          </Alert>
        </Snackbar>
        <div style={{ padding: "0px 20px 20px 30px" }}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleClickGetQuiz}
          >
            View Question
          </Button>
        </div>
        <Grid container spacing={3}>
          {ListQuestions != null ? (
            ListQuestions.map((element, index) => {
              return (
                <Grid item xs={12} key={index}>
                  <Paper className={classes.paper}>
                    <div className={classes.root}>
                      <h4>Quiz {index + 1}</h4>{" "}
                      <p
                        dangerouslySetInnerHTML={{
                          __html: element["statement"],
                        }}
                      />
                      {element.attachment &&
                        element.attachment.length !== 0 &&
                        element.attachment.map((url, index) => (
                          <div key={index} className={classes.imageContainer}>
                            <div className={classes.imageWrapper}>
                              <img
                                src={`data:image/jpeg;base64,${url}`}
                                alt="quiz test"
                                className={classes.imageQuiz}
                              />
                            </div>
                          </div>
                        ))}
                      {element["quizChoiceAnswerList"].map((answer, ind) => {
                        return (
                          <div key={ind} style={{ display: "flex" }}>
                            <Checkbox
                              key={answer["choiceAnswerId"]}
                              checked={
                                stateCheckBox[element["questionId"]]
                                  ? stateCheckBox[element["questionId"]][
                                      answer["choiceAnswerId"]
                                    ]
                                  : false
                              }
                              color="primary"
                              inputProps={{
                                "aria-label": "secondary checkbox",
                              }}
                              onChange={(event) =>
                                handleChange(event, element["questionId"])
                              }
                              name={answer["choiceAnswerId"]}
                            />
                            <p
                              dangerouslySetInnerHTML={{
                                __html: answer.choiceAnswerContent,
                              }}
                            />
                          </div>
                        );
                      })}
                    </div>
                    <div style={{ textAlign: "right" }}>
                      <Button
                        variant="contained"
                        color="primary"
                        disabled={mapS[element["questionId"]]}
                        onClick={() => {
                          handleClick(element["questionId"]);
                        }}
                      >
                        Select
                      </Button>
                    </div>
                  </Paper>
                </Grid>
              );
            })
          ) : (
            <p style={{ justifyContent: "center" }}> </p>
          )}
        </Grid>
      </Card>
    </div>
  );
}
