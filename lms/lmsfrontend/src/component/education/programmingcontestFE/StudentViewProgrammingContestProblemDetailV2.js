import {Box, Button, CircularProgress, Divider, Grid, Typography,} from "@mui/material";
import {ContentState, EditorState} from "draft-js";
import htmlToDraft from "html-to-draftjs";
import React, {useEffect, useRef, useState} from "react";
import {Editor} from "react-draft-wysiwyg";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router";
import {authGet, authPostMultiPart} from "../../../api";
import HustModal from "component/common/HustModal";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import StudentViewSubmission from "./StudentViewSubmission";
import {randomImageName,} from "utils/FileUpload/covert";
import {errorNoti, successNoti} from "../../../utils/notification";
import HustCodeLanguagePicker from "../../common/HustCodeLanguagePicker";
import FileUploadZone from "../../../utils/FileUpload/FileUploadZone";
import HustContainerCard from "../../common/HustContainerCard";
import HustCodeEditor from "../../common/HustCodeEditor";

const editorStyle = {
  toolbar: {
    background: "#FFFFFF",
  },
  editor: {
    border: "1px solid black",
    minHeight: "300px",
  },
};

export default function StudentViewProgrammingContestProblemDetail() {
  const params = useParams();
  const problemId = params.problemId;
  const contestId = params.contestId;
  const [problem, setProblem] = useState(null);
  const [testCases, setTestCases] = useState([]);
  const [file, setFile] = useState(null);
  const [language, setLanguage] = useState("CPP");
  const [status, setStatus] = useState("");
  const [message, setMessage] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [isSubmitCode, setIsSubmitCode] = useState(0);

  const [openModalPreview, setOpenModalPreview] = useState(false);
  const [selectedTestcase, setSelectedTestcase] = useState();
  const token = useSelector((state) => state.auth.token);
  const dispatch = useDispatch();
  const [isProcessing, setIsProcessing] = React.useState(false);
  const [editorStateDescription, setEditorStateDescription] = useState(
    EditorState.createEmpty()
  );
  const [fetchedImageArray, setFetchedImageArray] = useState([]);

  const ERR_STATUS = ["TIME_OUT",
    "PARTICIPANT_NOT_APPROVED_OR_REGISTERED",
    "PARTICIPANT_HAS_NOT_PERMISSION_TO_SUBMIT",
    "MAX_NUMBER_SUBMISSIONS_REACHED",
    "MAX_SOURCE_CODE_LENGTH_VIOLATIONS",
    "SUBMISSION_INTERVAL_VIOLATIONS"];

  const inputRef = useRef();
  const listSubmissionRef = useRef(null);

  function onFileChange(event) {
    setFile(event.target.files[0]);
  }

  const onInputChange = (event) => {
    let name = event.target.value;
    setFile(name);
  };

  const handleFormSubmit = async (event) => {
    if (event) event.preventDefault();
    setIsProcessing(true);
    let body = {
      problemId: problemId,
      contestId: contestId,
      language: language,
    };

    if (file == null) {
      errorNoti("Please choose a file to submit", 2000);
      setIsProcessing(false);
      return;
    }
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", file);

    await authPostMultiPart(
      dispatch,
      token,
      "/contest-submit-problem-via-upload-file-v3",
      formData
    )
      .then((res) => {
        listSubmissionRef.current.refreshSubmission();
        inputRef.current.value = null;
        if (ERR_STATUS.includes(res.status)) {
          errorNoti(res.message, 3000);
        } else successNoti("Submitted!", 3000)
        setStatus(res.status);
        setMessage(res.message);
      })
      .catch((e) => {
        console.error(e);
      })
      .finally(() => {
        setIsProcessing(false);
        setFile(null);
        inputRef.current.value = null;
      });
  };

  function getProblemDetail() {
    authGet(
      dispatch,
      token,
      "/get-problem-detail-view-by-student-in-contest/" +
      problemId +
      "/" +
      contestId
    )
      .then(
        (res) => {
          setProblem(res);
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
          let {contentBlocks, entityMap} = problemDescriptionHtml;
          let contentDescriptionState = ContentState.createFromBlockArray(
            contentBlocks,
            entityMap
          );
          let statementDescription = EditorState.createWithContent(
            contentDescriptionState
          );
          setEditorStateDescription(statementDescription);
        },
        (e) => console.log(e)
      )
      .then();
  }

  useEffect(() => {
    getProblemDetail();
  }, []);

  const ModalPreview = (chosenTestcase) => {
    return (
      <HustModal
        open={openModalPreview}
        onClose={() => setOpenModalPreview(false)}
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

  async function submitCode() {
    const blob = new Blob(
      [codeSolution],
      {type: "text/plain;charset=utf-8"}
    );
    const now = new Date();
    const file = new File([blob], "SourceCode_" + problemId + now.toLocaleTimeString() + ".txt", {type: 'text/plain;charset=utf-8'});
    setFile(file);
    setIsSubmitCode(isSubmitCode + 1);
  }

  useEffect(() => {
    if (isSubmitCode > 0)
      handleFormSubmit(null);
  }, [isSubmitCode])

  return (
    <HustContainerCard title={"Problem: " + (problem ? problem.problemName : "")}>
      <Box>
        <Typography>
          <h3>Description</h3>
        </Typography>
        <Editor
          editorState={editorStateDescription}
          handlePastedText={() => false}
          toolbarStyle={editorStyle.toolbar}
          editorStyle={editorStyle.editor}
        />
        {fetchedImageArray.length !== 0 &&
          fetchedImageArray.map((file) => (
            <FileUploadZone file={file} removable={false}/>
          ))}
      </Box>

      <Divider/>

      <ModalPreview chosenTestcase={selectedTestcase}/>
      <Box sx={{mt: 2}}>
        <Box>
          <HustCodeEditor
            title={"Source code"}
            language={language}
            onChangeLanguage={(event) => {
              setLanguage(event.target.value);
            }}
            sourceCode={codeSolution}
            onChangeSourceCode={(code) => {
              setCodeSolution(code);
            }}
            height={"480px"}
          />
          <Box sx={{width: "100%", display: "flex", justifyContent: "center"}}>
            <Button
              disabled={isProcessing}
              color="primary"
              variant="contained"
              type="submit"
              onClick={submitCode}
              sx={{mt: 1, mb: 1}}
            >
              SUBMIT CODE
            </Button>
          </Box>

        </Box>

        <Divider>or</Divider>

        <form onSubmit={handleFormSubmit}>
          <Grid container spacing={1} alignItems="center" mt={1}>
            <Grid item xs={3}/>
            <Grid item xs={3}>
              <input
                type="file"
                accept=".c, .cpp, .java, .py"
                id="selected-upload-file"
                onChange={onFileChange}
                ref={inputRef}
              />
            </Grid>
            <Grid item xs={1} mr={1}>
              <HustCodeLanguagePicker language={language} onChangeLanguage={(e) => setLanguage(e.target.value)}/>
            </Grid>

            <Grid item xs={2}>
              <Button
                disabled={isProcessing}
                color="primary"
                variant="contained"
                type="submit"
                onChange={onInputChange}
                width="100%"
              >
                SUBMIT
              </Button>
            </Grid>
            <Grid item xs={3}/>

            {isProcessing ? <CircularProgress/> : ""}
          </Grid>
        </form>
        <div>
          <h3>Status: <em>{status}</em></h3>
        </div>
        <div>
          <h3>Message: <em>{message}</em></h3>
        </div>
      </Box>
      <Box sx={{paddingTop: 2}}>
        <StudentViewSubmission problemId={problemId} ref={listSubmissionRef}/>
      </Box>
    </HustContainerCard>
  );
}
