import {useState} from "@hookstate/core";
import Card from "@material-ui/core/Card";
//import Grid from "@material-ui/core/Grid";
import Snackbar from "@material-ui/core/Snackbar";
import {makeStyles} from "@material-ui/core/styles";
import Alert from "@material-ui/lab/Alert";
import React, {useEffect} from "react";
import {useHistory, useParams} from "react-router-dom";
import {request} from "../../../api";
import {errorNoti, successNoti} from "../../../utils/notification";
import StudentQuizDetailListForm from "./StudentQuizDetailListForm";
import StudentQuizDetailStepForm from "./StudentQuizDetailStepForm";
import {Button, Chip} from "@mui/material";
import CheckAndConfirmQuizGroupDialog from "./CheckAndConfirmQuizGroupDialog";
import XLSX from "xlsx";
import {LoadingButton} from "@mui/lab";
import PublishIcon from "@mui/icons-material/Publish";
import SendIcon from "@mui/icons-material/Send";
import {LinearProgress} from "@mui/material";
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

export default function StudentQuizDetail() {
  const history = useHistory();
  //const testQuizId = history.location.state?.testId;
  const params = useParams();
  const testQuizId = params.testId;

  const classes = useStyles();

  //
  const [questions, setQuestions] = React.useState([]);
  const [requestSuccessfully, setRequestSuccessfully] = React.useState(false);
  const [requestFailed, setRequestFailed] = React.useState(false);
  const [messageRequest, setMessageRequest] = React.useState(false);
  const [quizGroupTestDetail, setQuizGroupTestDetail] = React.useState({});
  const [groupCode, setGroupCode] = React.useState(null);
  const [userId, setUserId] = React.useState(null);
  const [open, setOpen] = React.useState(false);
  const [isProcessing, setIsProcessing] = React.useState(false);
  const [importedExcelFile, setImportedExcelFile] = React.useState(null);

  const [viewTypeId, setViewTypeId] = React.useState(null);
  //const [viewTypeId, setViewTypeId] = React.useState(
  //  history.location.state?.viewTypeId
  //);

  // Keep track of checking state of all choices of all quiz
  const checkState = useState([]);

  function onClose() {
    setOpen(false);
  }
  function onUpdateInfo() {
    request(
      "get",
      "/confirm-update-group-code-quiz-test/" + testQuizId + "/" + groupCode,
      (res) => {
        alert("update " + res.data);
        setOpen(false);
      },
      {
        401: () => {},
        406: () => {
          //setMessageRequest("Time Out!");
          setRequestFailed(true);
        },
      }
    );
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
        setQuizGroupTestDetail(quizGroupTestDetail);

        setViewTypeId(quizGroupTestDetail.viewTypeId);

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
  function getQuizTestGroup() {
    request(
      "get",
      "/get-my-quiz-test-group/" + testQuizId,
      (res) => {
        if (res.data.statusId === "OK") {
          setGroupCode(res.data.groupCode);
        } else {
          setGroupCode(null);
        }
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
    getQuizTestGroup();
    getQuestionList();
  }, []);
  function updateCode() {
    alert("update group code " + groupCode);
  }
  function checkoutQuestion() {
    request(
      "get",
      "/check-questions-of-group/" + testQuizId + "/" + groupCode,
      (res) => {
        const {
          listQuestion,
          participationExecutionChoice,
          ...quizGroupTestDetail
        } = res.data;

        setQuestions(listQuestion);
        setQuizGroupTestDetail(quizGroupTestDetail);

        setViewTypeId(quizGroupTestDetail.viewTypeId);

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

    setOpen(true);
  }
  function checkAndConfirmCode() {
    history.push(
      "/edu/class/student/quiztest-detail/check-confirm-code/" + testQuizId
    );
  }
  function handleDownloadExcel() {
    var wbcols = [];
    wbcols.push({ wpx: 80 });
    wbcols.push({ wpx: 120 });
    wbcols.push({ wpx: 120 });
    let data = [];
    for (let i = 0; i < questions.length; i++) {
      let row = {};
      row["Question"] = "Question " + (i + 1);
      row["choices"] = "";
      row["notes"] = "fill codes of choice answers, separated by a comma ,";
      data[i] = row;
    }

    var sheet = XLSX.utils.json_to_sheet(data);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "answers");

    // create data about userId and testId
    var wbhcols = [];
    wbhcols.push({ wpx: 80 });
    wbhcols.push({ wpx: 120 });

    let dataHeader = [];
    let row1 = {};
    //row1["userId"] = quizGroupTestDetail.participantUserId; //"dungpq";
    row1["key"] = "userId";
    row1["value"] = quizGroupTestDetail.participantUserId;
    dataHeader[0] = row1;

    let row2 = {};
    //row2["testId"] = testQuizId;
    row2["key"] = "testId";
    row2["value"] = testQuizId;
    dataHeader[1] = row2;

    let row3 = {};
    //row3["code"] = groupCode; // "a code";
    row3["key"] = "code";
    row3["value"] = groupCode;
    dataHeader[2] = row3;

    var sheetHeader = XLSX.utils.json_to_sheet(dataHeader);
    //var wbh = XLSX.utils.book_new();
    sheetHeader["!cols"] = wbhcols;

    XLSX.utils.book_append_sheet(wb, sheetHeader, "info");

    XLSX.writeFile(wb, "quiz-test.xlsx");
  }

  function handleUploadSolutionQuiz(event) {
    event.preventDefault();

    setIsProcessing(true);
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify({ testQuizId }));
    formData.append("file", importedExcelFile);

    let successHandler = (res) => {
      setIsProcessing(false);
      setImportedExcelFile(undefined);
      successNoti("Upload successfully", true);
    };
    let errorHandlers = {
      onError: (error) => {
        setIsProcessing(false);
        errorNoti("Error when uploading solution", true);
      },
    };
    request(
      "POST",
      "/upload-solution-excel-quiz-of-student",
      successHandler,
      errorHandlers,
      formData
    );
    getQuizTestGroup();
    getQuestionList();
  }
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
        <div style={{ padding: "0px 20px 20px 30px" }}>
          <div style={{ justifyContent: "space-between", display: "flex" }}>
            <h3>Quiz test: {quizGroupTestDetail.testName}</h3>
            <h3>QuizTestID: {testQuizId}</h3>
            <h3>Course: {quizGroupTestDetail.courseName}</h3>
          </div>
          {/*<h4>Start Time: {quizGroupTestDetail.scheduleDatetime}</h4>*/}
          <h4>Duration: {quizGroupTestDetail.duration} minutes</h4>
          <div style={{ justifyContent: "space-between", display: "flex" }}>
            <h3>Code: {groupCode}</h3>
            <Button variant="contained" onClick={checkAndConfirmCode}>
              {" "}
              Check & Confirm Code
            </Button>

            {quizGroupTestDetail.judgeMode === "OFFLINE_VIA_EXCEL_UPLOAD" ? (
              <div>
                <Button variant="contained" onClick={handleDownloadExcel}>
                  {" "}
                  Download Template excel
                </Button>
                <Button color="primary" variant="contained" component="label">
                  <PublishIcon /> Select excel file to import
                  <input
                    type="file"
                    hidden
                    onChange={(event) =>
                      setImportedExcelFile(event.target.files[0])
                    }
                  />
                </Button>
                {importedExcelFile && (
                  <Chip
                    color="success"
                    variant="outlined"
                    label={importedExcelFile.name}
                    onDelete={() => setImportedExcelFile(undefined)}
                  />
                )}
                <LoadingButton
                  loading={isProcessing}
                  endIcon={<SendIcon />}
                  disabled={!importedExcelFile}
                  color="primary"
                  variant="contained"
                  onClick={handleUploadSolutionQuiz}
                >
                  Upload solution excel
                </LoadingButton>
              </div>
            ) : null}

            {/*
              quizGroupTestDetail ? (
              <div>
                <TextField
                  autoFocus
                  required
                  id="groupCode"
                  label="groupCode"
                  placeholder="groupCode"
                  value={groupCode}
                  onChange={(event) => {
                    setGroupCode(event.target.value);
                  }}
                />
              </div>
            ) : (
              <div>
                <TextField
                  autoFocus
                  required
                  id="groupCode"
                  label="groupCode"
                  placeholder="groupCode"
                    value={groupCode}
                    
                  onChange={(event) => {
                    setGroupCode(event.target.value);
                  }}
                />
               
              </div>
                )*/}
          </div>
          {/*<Button onClick={checkoutQuestion}>Check</Button>*/}
        </div>

        {viewTypeId === "VIEW_STEP" ? (
          <StudentQuizDetailStepForm testId={testQuizId} />
        ) : (
          <StudentQuizDetailListForm testId={testQuizId} />
        )}

        {/*
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
            */}
      </Card>
      <CheckAndConfirmQuizGroupDialog
        open={open}
        onClose={onClose}
        onUpdateInfo={onUpdateInfo}
        questions={questions}
        quizGroupTestDetail={quizGroupTestDetail}
        checkState={checkState}
      />
    </div>
  );
}
