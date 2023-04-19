import DateFnsUtils from "@date-io/date-fns";
import {Card, CardActions, CardContent, MenuItem, TextField, Typography,} from "@material-ui/core/";
import {makeStyles} from "@material-ui/core/styles";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import React, {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {Editor} from "react-draft-wysiwyg";
import {ContentState, convertToRaw, EditorState} from "draft-js";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {authGet, authPostMultiPart, request} from "../../../api";
import {Button, TableHead} from "@material-ui/core";
import draftToHtml from "draftjs-to-html";
import {SubmitWarming} from "./SubmitWarming";
import {CompileStatus} from "./CompileStatus";
import {SubmitSuccess} from "./SubmitSuccess";
import {useParams} from "react-router";
import {sleep, StyledTableCell, StyledTableRow} from "./lib";
import htmlToDraft from "html-to-draftjs";
import Paper from "@material-ui/core/Paper";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableRow from "@material-ui/core/TableRow";
import TableBody from "@mui/material/TableBody";
import {getFileType, randomImageName, saveByteArray,} from "../../../utils/FileUpload/covert";
import {DropzoneArea} from "material-ui-dropzone";
import {Box} from "@mui/material";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "40%",
      minWidth: 120,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
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
    alignItems: "center",
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
  buttonClearImage: {
    position: "absolute",
    top: "12px",
    right: "12px",
    zIndex: 3,
    color: "red",
    width: 32,
    height: 32,
    cursor: "pointer",
  },
}));
const descriptionStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "100%",
      minWidth: 120,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));

const editorStyle = {
  toolbar: {
    background: "#FFFFFF",
  },
  editor: {
    border: "1px solid black",
    minHeight: "300px",
  },
};

function EditProblem() {
  const { problemId } = useParams();
  const token = useSelector((state) => state.auth.token);
  const dispatch = useDispatch();
  const history = useHistory();
  const [problemName, setProblemName] = useState("");
  const [problemDescriptions, setProblemDescription] = useState();
  const [timeLimit, setTimeLimit] = useState(1);
  const [memoryLimit, setMemoryLimit] = useState(1);
  const [levelId, setLevelId] = useState("");
  const [categoryId, setCategoryId] = useState();
  const defaultLevel = ["easy", "medium", "hard"];
  const listCategory = [];
  const classes = useStyles();
  const descriptionClass = descriptionStyles();
  const [editorStateDescription, setEditorStateDescription] = useState(
    EditorState.createEmpty()
  );
  const [editorStateSolution, setEditorStateSolution] = useState(
    EditorState.createEmpty()
  );
  const [codeSolution, setCodeSolution] = useState("");
  const [codeChecker, setCodeChecker] = useState("");
  const [languageSolution, setLanguageSolution] = useState("CPP");
  const computerLanguageList = ["CPP", "GOLANG", "JAVA", "PYTHON3"];
  const [showSubmitWarming, setShowSubmitWarming] = useState(false);
  const [showCompile, setShowCompile] = useState(false);
  const [statusSuccessful, setStatusSuccessful] = useState(false);
  const [showSubmitSuccess, setShowSubmitSuccess] = useState(false);
  const [isPublic, setIsPublic] = useState(false);
  const [testCases, setTestCases] = useState([]);
  const [compileMessage, setCompileMessage] = useState("");
  const [attachmentFiles, setAttachmentFiles] = useState([]);
  const [fetchedImageArray, setFetchedImageArray] = useState([]);
  const [removedFilesId, setRemovedFileIds] = useState([]);

  const handleAttachmentFiles = (files) => {
    setAttachmentFiles(files);
  };

  const handleDeleteImageAttachment = async (fileId) => {
    setFetchedImageArray(
      fetchedImageArray.filter((file) => file.fileName !== fileId)
    );
    setRemovedFileIds([...removedFilesId, fileId]);
  };

  useEffect(() => {
    authGet(dispatch, token, "/problem-details/" + problemId)
      .then((res) => {
        console.log("PROBLEM DETAIL", res.attachment);
        // setEditorStateDescription(EditorState.set(res.data.problemDescription));

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

        setProblemName(res.problemName);
        setLevelId(res.levelId);
        setMemoryLimit(res.memoryLimit);
        setCodeSolution(res.correctSolutionSourceCode);

        setCodeChecker(
          res.solutionCheckerSourceCode != null
            ? res.solutionCheckerSourceCode
            : " "
        );
        setTimeLimit(res.timeLimit);
        setIsPublic(res.publicProblem);
        let problemDescriptionHtml = htmlToDraft(res.problemDescription);
        let { contentBlocks, entityMap } = problemDescriptionHtml;
        let contentDescriptionState = ContentState.createFromBlockArray(
          contentBlocks,
          entityMap
        );
        let statementDescription = EditorState.createWithContent(
          contentDescriptionState
        );
        setEditorStateDescription(statementDescription);

        let solutionHtml = htmlToDraft(res.solution);
        let contentBlocks1 = solutionHtml.contentBlocks;
        let entityMap1 = solutionHtml.entityMap;
        let contentSolutionState = ContentState.createFromBlockArray(
          contentBlocks1,
          entityMap1
        );
        let statementSolution =
          EditorState.createWithContent(contentSolutionState);
        setEditorStateSolution(statementSolution);
      }, {})
      .then();

    getTestCases();
  }, [problemId]);

  function getTestCases() {
    request(
      "GET",
      "/get-test-case-list-by-problem/" + problemId,

      (res) => {
        console.log("res", res.data);
        setTestCases(res.data);
      },
      {}
    );
  }

  function rerunTestCase(problemId, testCaseId) {
    request(
      "GET",
      "/rerun-create-testcase-solution/" + problemId + "/" + testCaseId,

      (res) => {
        getTestCases();
      },
      {}
    );
  }

  const onChangeEditorStateDescription = (editorState) => {
    setEditorStateDescription(editorState);
  };

  const onChangeEditorStateSolution = (editorState) => {
    setEditorStateSolution(editorState);
  };

  function checkCompile() {
    let body = {
      source: codeSolution,
      computerLanguage: languageSolution,
    };
    request(
      "post",
      "/check-compile",
      (res) => {
        if (res.data.status == "Successful") {
          setShowCompile(true);
          setShowSubmitWarming(false);
          setStatusSuccessful(true);
        } else {
          setShowCompile(true);
          setStatusSuccessful(false);
        }
        setCompileMessage(res.data.message);
      },
      {},
      body
    ).then();
  }

  function handleSubmit() {
    if (!statusSuccessful) {
      setShowSubmitWarming(true);
      return;
    }
    let description = draftToHtml(
      convertToRaw(editorStateDescription.getCurrentContent())
    );
    let solution = draftToHtml(
      convertToRaw(editorStateSolution.getCurrentContent())
    );

    let fileId = [];
    if (attachmentFiles.length > 0) {
      fileId = attachmentFiles.map((file) => {
        if (typeof file.name !== "undefined") {
          return file.name;
        }
        if (typeof file.fileName !== "undefined") {
          return file.fileName;
        }
        return file.id;
      });
    }

    let body = {
      problemName: problemName,
      problemDescription: description,
      timeLimit: timeLimit,
      levelId: levelId,
      categoryId: categoryId,
      memoryLimit: memoryLimit,
      correctSolutionLanguage: languageSolution,
      solution: solution,
      correctSolutionSourceCode: codeSolution,
      solutionChecker: codeChecker,
      isPublic: isPublic,
      fileId: fileId,
      removedFilesId: removedFilesId,
    };

    let formData = new FormData();
    formData.append("ModelUpdateContestProblem", JSON.stringify(body));
    for (const file of attachmentFiles) {
      formData.append("files", file);
    }

    try {
      authPostMultiPart(
        dispatch,
        token,
        "/update-problem-detail/" + problemId,
        formData
      ).then(
        (res) => {
          setShowSubmitSuccess(true);
          sleep(1000).then((r) => {
            history.push("/programming-contest/list-problems");
          });
        },
        {},
        () => {
          alert("Cập nhật thất bại");
        }
      );
    } catch (error) {
      alert(error);
    }
  }

  return (
    <div>
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              Edit Problem <Typography variant="h4"> {problemId}</Typography>
            </Typography>
            <form className={classes.root} noValidate autoComplete="off">
              <div>
                <TextField
                  value={problemName}
                  autoFocus
                  required
                  id="problemName"
                  label="Problem Name"
                  placeholder="Problem Name"
                  onChange={(event) => {
                    setProblemName(event.target.value);
                  }}
                ></TextField>

                <TextField
                  autoFocus
                  required
                  id="timeLimit"
                  label="Time Limit"
                  placeholder="Time Limit"
                  onChange={(event) => {
                    setTimeLimit(event.target.value);
                  }}
                  value={timeLimit}
                ></TextField>

                <TextField
                  autoFocus
                  required
                  id="memoryLimit"
                  label="Memory Limit"
                  placeholder="Memory Limit"
                  onChange={(event) => {
                    setMemoryLimit(event.target.value);
                  }}
                  value={memoryLimit}
                ></TextField>

                <TextField
                  autoFocus
                  required
                  select
                  id="levelId"
                  label="Level ID"
                  placeholder="Level ID"
                  onChange={(event) => {
                    setLevelId(event.target.value);
                  }}
                  value={levelId}
                >
                  {defaultLevel.map((item) => (
                    <MenuItem key={item} value={item}>
                      {item}
                    </MenuItem>
                  ))}
                </TextField>

                <TextField
                  autoFocus
                  // required
                  select
                  id="categoryId"
                  label="Category ID"
                  placeholder="Category ID"
                  onChange={(event) => {
                    setCategoryId(event.target.value);
                  }}
                  value={categoryId}
                >
                  {listCategory.map((item) => (
                    <MenuItem key={item} value={item}>
                      {item}
                    </MenuItem>
                  ))}
                </TextField>

                <TextField
                  autoFocus
                  // required
                  select
                  id="Public Problem"
                  label="Public Problem"
                  placeholder="Public Problem"
                  onChange={(event) => {
                    setIsPublic(event.target.value);
                  }}
                  value={isPublic}
                >
                  <MenuItem key={"true"} value={true}>
                    {"true"}
                  </MenuItem>
                  <MenuItem key={"false"} value={false}>
                    {"false"}
                  </MenuItem>
                </TextField>
              </div>
            </form>
            <form
              className={descriptionClass.root}
              noValidate
              autoComplete="off"
            >
              <div>
                <Typography>
                  <h2>Problem Description</h2>
                </Typography>
                <Editor
                  editorState={editorStateDescription}
                  handlePastedText={() => false}
                  onEditorStateChange={onChangeEditorStateDescription}
                  toolbarStyle={editorStyle.toolbar}
                  editorStyle={editorStyle.editor}
                />
                <Typography
                  variant="subtitle1"
                  display="block"
                  style={{ margin: "5px 10px 0 5px", width: "100%" }}
                >
                  File đính kèm
                </Typography>
                <DropzoneArea
                  dropzoneClass={classes.dropZone}
                  filesLimit={20}
                  maxFileSize={10 * 1024 * 1024}
                  showPreviews={true}
                  showPreviewsInDropzone={false}
                  useChipsForPreview
                  dropzoneText="Kéo thả tệp vào đây hoặc nhấn để chọn tệp"
                  previewText="Xem trước:"
                  previewChipProps={{
                    variant: "outlined",
                    color: "primary",
                    size: "medium",
                  }}
                  getFileAddedMessage={(fileName) =>
                    `Tệp ${fileName} tải lên thành công`
                  }
                  getFileRemovedMessage={(fileName) =>
                    `Tệp ${fileName} đã loại bỏ`
                  }
                  getFileLimitExceedMessage={(filesLimit) =>
                    `Vượt quá số lượng tệp tối đa được cho phép. Chỉ được phép tải lên tối đa ${filesLimit} tệp.`
                  }
                  alertSnackbarProps={{
                    anchorOrigin: {vertical: "bottom", horizontal: "right"},
                    autoHideDuration: 1800,
                  }}
                  onChange={(files) => handleAttachmentFiles(files)}
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
                            <Typography
                              variant="subtitle2"
                              className={classes.fileName}
                            >
                              {file.fileName}
                            </Typography>
                            <Button
                              variant="contained"
                              color="success"
                              className={classes.downloadButton}
                              onClick={() =>
                                saveByteArray(
                                  file.fileName,
                                  file.content,
                                  "pdf"
                                )
                              }
                            >
                              Download
                            </Button>
                          </Box>
                        )}
                        {getFileType(file.fileName) === "word" && (
                          <Box className={classes.fileDownload}>
                            <Typography
                              variant="subtitle2"
                              className={classes.fileName}
                            >
                              {file.fileName}
                            </Typography>
                            <Button
                              variant="contained"
                              color="success"
                              className={classes.downloadButton}
                              onClick={() =>
                                saveByteArray(
                                  file.fileName,
                                  file.content,
                                  "word"
                                )
                              }
                            >
                              Download
                            </Button>
                          </Box>
                        )}
                        {getFileType(file.fileName) === "txt" && (
                          <Box className={classes.fileDownload}>
                            <Typography
                              variant="subtitle2"
                              className={classes.fileName}
                            >
                              {file.fileName}
                            </Typography>
                            <Button
                              variant="contained"
                              color="success"
                              className={classes.downloadButton}
                              onClick={() =>
                                saveByteArray(
                                  file.fileName,
                                  file.content,
                                  "txt"
                                )
                              }
                            >
                              Download
                            </Button>
                          </Box>
                        )}
                        <HighlightOffIcon
                          className={classes.buttonClearImage}
                          onClick={() =>
                            handleDeleteImageAttachment(file.fileName)
                          }
                        />
                      </div>
                    </div>
                  ))}
              </div>
              <div>
                <Typography>
                  <h2>Problem Solution</h2>
                </Typography>
                <Editor
                  editorState={editorStateSolution}
                  handlePastedText={() => false}
                  onEditorStateChange={onChangeEditorStateSolution}
                  toolbarStyle={editorStyle.toolbar}
                  editorStyle={editorStyle.editor}
                />
              </div>
            </form>
            <Typography>
              <h2>Correct Solution Source Code</h2>
            </Typography>
            <TextField
              style={{ width: 0.075 * window.innerWidth, margin: 20 }}
              variant={"outlined"}
              size={"small"}
              autoFocus
              value={languageSolution}
              select
              id="computerLanguage"
              onChange={(event) => {
                setLanguageSolution(event.target.value);
              }}
            >
              {computerLanguageList.map((item) => (
                <MenuItem key={item} value={item}>
                  {item}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              style={{
                width: 0.65 * window.innerWidth,
                margin: 20,
              }}
              multiline
              maxRows={20}
              value={codeSolution}
              onChange={(event) => {
                setCodeSolution(event.target.value);
              }}
            />
            <Typography>
              <h2>Solution Checker</h2>
            </Typography>
            <TextField
              style={{
                width: 0.65 * window.innerWidth,
                margin: 20,
              }}
              multiline
              maxRows={20}
              value={codeChecker}
              onChange={(event) => {
                setCodeChecker(event.target.value);
              }}
            />
            <TextField
              style={{ width: 0.075 * window.innerWidth, margin: 20 }}
              variant={"outlined"}
              size={"small"}
              autoFocus
              value={languageSolution}
              select
              id="computerLanguage"
              onChange={(event) => {
                setLanguageSolution(event.target.value);
              }}
            >
              {computerLanguageList.map((item) => (
                <MenuItem key={item} value={item}>
                  {item}
                </MenuItem>
              ))}
            </TextField>
            <br />
            <CompileStatus
              showCompile={showCompile}
              statusSuccessful={statusSuccessful}
              message={compileMessage}
            />
          </CardContent>
          <CardActions>
            <Button
              variant="contained"
              color="light"
              style={{ marginLeft: "45px" }}
              onClick={checkCompile}
            >
              Check Solution Compile
            </Button>
            <SubmitWarming
              showSubmitWarming={showSubmitWarming}
              content={"Your source must be pass compile process"}
            />
          </CardActions>

          <Typography>
            <h2>Test case</h2>
          </Typography>

          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 750 }} aria-label="customized table">
              <TableHead>
                <TableRow>
                  <StyledTableCell></StyledTableCell>
                  <StyledTableCell align="left">TestCase</StyledTableCell>
                  <StyledTableCell align="left">Correct Answer</StyledTableCell>
                  <StyledTableCell align="left">Point</StyledTableCell>
                  <StyledTableCell align="left">Public</StyledTableCell>
                  <StyledTableCell align="left">Description</StyledTableCell>
                  <StyledTableCell align="left">Status</StyledTableCell>
                  <StyledTableCell align="left">Edit</StyledTableCell>
                  <StyledTableCell align="left">Rerun</StyledTableCell>
                  <StyledTableCell align="left">Delete</StyledTableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {testCases.map((testCase, idx) => (
                  <StyledTableRow>
                    <StyledTableCell component="th" scope="row">
                      {idx}
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
                    <StyledTableCell align="left">
                      {testCase.isPublic}
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      {testCase.description}
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      {testCase.status}
                    </StyledTableCell>

                    <StyledTableCell align="left">
                      <Link
                        to={
                          "/programming-contest/edit-testcase/" +
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
                          Edit
                        </Button>
                      </Link>
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      <Button
                        variant="contained"
                        color="light"
                        onClick={() => {
                          rerunTestCase(problemId, testCase.testCaseId);
                        }}
                      >
                        Rerun
                      </Button>
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      <Button
                        variant="contained"
                        color="light"
                        onClick={() => {
                          request(
                            "delete",
                            "/delete-test-case/" + testCase.testCaseId,

                            (res) => {
                              request(
                                "GET",
                                "/get-test-case-list-by-problem/" + problemId,

                                (res) => {
                                  console.log("res", res.data);
                                  setTestCases(res.data);
                                },
                                {}
                              ).then();
                            },
                            {}
                          ).then();
                        }}
                      >
                        Delete
                      </Button>
                    </StyledTableCell>
                  </StyledTableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <CardActions>
            <Button
              variant="contained"
              color="light"
              style={{ marginLeft: "45px" }}
              onClick={handleSubmit}
            >
              Save
            </Button>

            <SubmitSuccess
              showSubmitSuccess={showSubmitSuccess}
              content={"You have saved problem"}
            />
          </CardActions>
        </Card>
      </MuiPickersUtilsProvider>
    </div>
  );
}
export default EditProblem;
