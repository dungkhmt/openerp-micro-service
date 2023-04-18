import {Button, CircularProgress, Grid, MenuItem, TableHead, TextField, Typography,} from "@material-ui/core";
import InfoIcon from "@mui/icons-material/Info";
import {Box, IconButton} from "@mui/material";
import Paper from "@material-ui/core/Paper";
import TableRow from "@material-ui/core/TableRow";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableContainer from "@mui/material/TableContainer";
import {ContentState, EditorState} from "draft-js";
import htmlToDraft from "html-to-draftjs";
import React, {useEffect, useState} from "react";
import {Editor} from "react-draft-wysiwyg";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router";
import {authGet, authPostMultiPart, request} from "../../../api";
import {StyledTableCell, StyledTableRow} from "./lib";
import HustModal from "component/common/HustModal";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import StudentViewSubmission from "./StudentViewSubmission";
import {getFileType, randomImageName, saveByteArray} from "utils/FileUpload/covert";
import {makeStyles} from "@material-ui/core/styles";
import {copyAllTestCases, downloadAllTestCases} from "./service/TestCaseService";

const editorStyle = {
  toolbar: {
    background: "#FFFFFF",
  },
  editor: {
    border: "1px solid black",
    minHeight: "300px",
  },
};
const useStyles = makeStyles((theme) => ({
  fileContainer: {
    marginTop: "12px",
  },
  fileWrapper: {
    position: "relative",
  },
  fileDownload: {
    display: "flex",
    flexDirection: "row",
    marginBottom: "16px",
    alignItems: "center"
  },
  fileName: {
    fontStyle: "italic",
    paddingRight: "12px",
  },
  downloadButton: {
    marginLeft: "12px",
  },
  imageQuiz: {
    maxWidth: "70%",
  },
}));

export default function StudentViewProgrammingContestProblemDetail() {
  const params = useParams();
  const classes = useStyles();

  const problemId = params.problemId;
  const contestId = params.contestId;
  const [problem, setProblem] = useState(null);
  const [testCases, setTestCases] = useState([]);
  const [filename, setFilename] = useState("");
  const [language, setLanguage] = useState("CPP");
  const [score, setScore] = React.useState("");
  const [status, setStatus] = useState("");
  const [message, setMessage] = useState("");
  const [nbTestCasePassed, setNbTestCasePassed] = useState("");
  const [nbTotalTestCase, setNbTotalTestCase] = useState("");
  const [runTime, setRunTime] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [selectedTestcase, setSelectedTestcase] = useState();
  const token = useSelector((state) => state.auth.token);
  const dispatch = useDispatch();
  const [isProcessing, setIsProcessing] = React.useState(false);
  const [editorStateDescription, setEditorStateDescription] = useState(
    EditorState.createEmpty()
  );
  const [fetchedImageArray, setFetchedImageArray] = useState([]);

  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }
  const onInputChange = (event) => {
    let name = event.target.value;
    setFilename(name);
  };

  const handleFormSubmit = (event) => {
    event.preventDefault();
    setIsProcessing(true);
    let body = {
      problemId: problemId,
      contestId: contestId,
      language: language,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", filename);

    authPostMultiPart(
      dispatch,
      token,
      "/contest-submit-problem-via-upload-file",
      formData
    )
      .then((res) => {
        setIsProcessing(false);
        if (res.status == "TIME_OUT") {
          alert("Time Out!!!");
          setScore("");
          setNbTestCasePassed("");
          setNbTotalTestCase("");
          setRunTime("");
          setStatus(res.status);
          setMessage(res.message);
        } else {
          setScore(res.score);
          setNbTestCasePassed(res.numberTestCasePassed);
          setNbTotalTestCase(res.totalNumberTestCase);
          setRunTime(runTime);
          setStatus(res.status);
          setMessage(res.message);
        }
      })
      .catch((e) => {
        setIsProcessing(false);
        console.error(e);
        //alert("Time Out!!!");
      });
  };

  function getTestCases() {
    request(
      "GET",
      "/get-test-case-list-by-problem/" + problemId,

      (res) => {
        setTestCases(res.data.filter((item) => item.isPublic === "Y"));
      },
      {}
    );
  }

  function getProblemDetail() {
    authGet(
      dispatch,
      token,
      "/get-problem-detail-view-by-student-in-contest/" + problemId + "/" + contestId
    ).then((res) => {
      setProblem(res);
      console.log(res);
      //setProblemStatement(res.data.problemStatement);
      if (res.attachment && res.attachment.length !== 0) {
        const newFileURLArray = res.attachment.map((url) => ({
          id: randomImageName(),
          content: url,
        }));
        newFileURLArray.forEach((file, idx) => {
          file.fileName = res.attachmentNames[idx];
        });
        setFetchedImageArray(newFileURLArray);
      }

      let problemDescriptionHtml = htmlToDraft(res.problemStatement);
      let { contentBlocks, entityMap } = problemDescriptionHtml;
      let contentDescriptionState = ContentState.createFromBlockArray(
        contentBlocks,
        entityMap
      );
      let statementDescription = EditorState.createWithContent(
        contentDescriptionState
      );
      setEditorStateDescription(statementDescription);
    }, (e) => console.log(e))
    .then();
  }

  useEffect(() => {
    getProblemDetail();
    //getTestCases();
  }, []);

  // const downloadHandler = (event) => {
  //   if (testCases.length === 0) {
  //     return;
  //   }
  //   var wbcols = [
  //     { wpx: 300 },
  //     { wpx: 300 },
  //     { wpx: 50 },
  //   ];

  //   var publicTestCases = testCases.filter(item => item.isPublic === "Y")

  //   var data = publicTestCases.map((item) => ({
  //     "Input": item.testCase,
  //     "Output": item.correctAns,
  //     "Point": item.point,
  //   }));

  //   var sheet = XLSX.utils.json_to_sheet(data);
  //   var wb = XLSX.utils.book_new();
  //   sheet["!cols"] = wbcols;

  //   const wb_opts = {bookType: 'xlsx', type: 'binary'};
  //   XLSX.utils.book_append_sheet(wb, sheet, "testCases");
  //   XLSX.writeFile(wb, "TestCasesProblem.xlsx", wb_opts);
  // };

  const ModalPreview = (chosenTestcase) => {
    return (
      <HustModal
        open={openModal}
        onClose={() => setOpenModal(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
      >
        <HustCopyCodeBlock
          title="Input"
          text={chosenTestcase?.chosenTestcase?.testCase}
        />
        <HustCopyCodeBlock
          title="Output"
          text={chosenTestcase?.chosenTestcase?.correctAns}
          mt={2}
        />
      </HustModal>
    );
  };

  return (
    <div>
      <div>
        <h3>Name: {problem ? problem.problemName : ""}</h3>
      </div>

      <div>
        <Typography>
          <h2>Description</h2>
        </Typography>
        <Editor
          editorState={editorStateDescription}
          handlePastedText={() => false}
          toolbarStyle={editorStyle.toolbar}
          editorStyle={editorStyle.editor}
        />
        {fetchedImageArray.length !== 0 &&
          fetchedImageArray.map((file) => (
            <div key={file.id} className={classes.fileContainer}>
              <div className={classes.fileWrapper}>
                {getFileType(file.fileName) === "img" && (
                  <img
                    src={`data:image/jpeg;base64,${file.content}`}
                    alt={file.fileName}
                    className={classes.imageQuiz}
                  />
                )}
                {getFileType(file.fileName) === "pdf" && (
                  <Box className={classes.fileDownload}>
                    <Typography variant="subtitle2" className={classes.fileName}>{file.fileName}</Typography>
                    <Button
                      variant="contained"
                      color="success"
                      className={classes.downloadButton}
                      onClick={() =>
                        saveByteArray(file.fileName, file.content, "pdf")
                      }
                    >
                      Download
                    </Button>
                  </Box>
                )}
                {getFileType(file.fileName) === "word" && (
                  <Box className={classes.fileDownload}>
                    <Typography variant="subtitle2" className={classes.fileName}>{file.fileName}</Typography>
                    <Button
                      variant="contained"
                      color="success"
                      className={classes.downloadButton}
                      onClick={() =>
                        saveByteArray(file.fileName, file.content, "word")
                      }
                    >
                      Download
                    </Button>
                  </Box>
                )}
                {getFileType(file.fileName) === "txt" && (
                  <Box className={classes.fileDownload}>
                    <Typography variant="subtitle2" className={classes.fileName}>{file.fileName}</Typography>
                    <Button
                      variant="contained"
                      color="success"
                      className={classes.downloadButton}
                      onClick={() =>
                        saveByteArray(file.fileName, file.content, "txt")
                      }
                    >
                      Download
                    </Button>
                  </Box>
                )}
              </div>
            </div>
          ))}
      </div>

      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 750 }} aria-label="customized table">
          <TableHead>
            <TableRow>
              <StyledTableCell></StyledTableCell>
              <StyledTableCell align="left">Test case</StyledTableCell>
              <StyledTableCell align="left">Correct answer</StyledTableCell>
              <StyledTableCell align="left">Point</StyledTableCell>
              {/*
              <StyledTableCell align="left">Submit Output</StyledTableCell>
              */}
              <StyledTableCell align="left">
                <Button variant="contained" onClick={() => copyAllTestCases(testCases)}>
                  Copy Tests
                </Button>
              </StyledTableCell>
              <StyledTableCell align="left">
                <Button variant="contained" onClick={() => downloadAllTestCases(testCases)}>
                  Download Tests
                </Button>
              </StyledTableCell>
              {/* <StyledTableCell align="center">
                <Button variant="contained" onClick={downloadHandler}>
                  Download all
                </Button>
              </StyledTableCell> */}
            </TableRow>
          </TableHead>

          <TableBody>
            {testCases.map((testCase, idx) => {
              return (
                testCase.isPublic === "Y" && (
                  <StyledTableRow>
                    <StyledTableCell component="th" scope="row">
                      <h6>{idx + 1}</h6>
                    </StyledTableCell>
                    <StyledTableCell
                      align="left"
                      sx={{
                        maxWidth: "120px",
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                      }}
                    >
                      {testCase.testCase}
                    </StyledTableCell>
                    <StyledTableCell
                      align="left"
                      sx={{
                        maxWidth: "120px",
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                      }}
                    >
                      {testCase.correctAns}
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      {testCase.point}
                    </StyledTableCell>

                    {/*
                    <StyledTableCell align="left">
                      <Link
                        to={
                          "/programming-contest/submit-solution-output/" +
                          contestId +
                          "/" +
                          problemId +
                          "/" +
                          testCase.testCaseId
                        }
                        style={{
                          textDecoration: "none",
                          color: "black",
                          cursor: "",
                        }}
                      >
                        <Button variant="contained" color="light">
                          Submit Solution
                        </Button>
                      </Link>
                    </StyledTableCell>
                      */}
                    <StyledTableCell align="left">
                      <IconButton
                        color="primary"
                        onClick={() => {
                          setSelectedTestcase(testCase);
                          setOpenModal(true);
                        }}
                      >
                        <InfoIcon />
                      </IconButton>
                    </StyledTableCell>
                    <StyledTableCell align="left"></StyledTableCell>
                  </StyledTableRow>
                )
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
      <ModalPreview chosenTestcase={selectedTestcase} />
      <div>
        <form onSubmit={handleFormSubmit}>
          <Grid container spacing={1} alignItems="flex-end">
            <Grid item xs={3}>
              <input
                type="file"
                accept=".c, .cpp, .java, .py"
                id="selected-upload-file"
                onChange={onFileChange}
              />
            </Grid>
            <Grid item xs={2}>
              <TextField
                autoFocus
                // required
                select
                id="language"
                label="Language"
                placeholder="Language"
                onChange={(event) => {
                  setLanguage(event.target.value);
                }}
                value={language}
              >
                <MenuItem key={"CPP"} value="CPP">
                  {"CPP"}
                </MenuItem>
                <MenuItem key={"JAVA"} value="JAVA">
                  {"JAVA"}
                </MenuItem>
                <MenuItem key={"PYTHON3"} value="PYTHON3">
                  {"PYTHON3"}
                </MenuItem>
              </TextField>
            </Grid>

            <Grid item xs={2}>
              <Button
                color="primary"
                variant="contained"
                type="submit"
                onChange={onInputChange}
                width="100%"
              >
                SUBMIT
              </Button>
            </Grid>

            {isProcessing ? <CircularProgress /> : ""}
          </Grid>
        </form>

        <div>
          <h2>Status: {status}</h2>
        </div>
        <div>
          <h2>Message: </h2> {message}
        </div>
        <div>
          <h2>Score: {score}</h2>
        </div>
        <div>
          <h2>Number of Test Cases Passed: {nbTestCasePassed}</h2>
        </div>
        <div>
          <h2>Total number of Test Cases : {nbTotalTestCase}</h2>
        </div>
        <div>
          <h2>RunTime : {runTime}(ms)</h2>
        </div>
      </div>
      <div>
        <br></br>
        <StudentViewSubmission />
      </div>
    </div>
  );
}
