import {Box, Button, CircularProgress, Divider, Grid, Typography,} from "@mui/material";
import React, {useEffect, useRef, useState} from "react";
import {useParams} from "react-router";
import HustModal from "component/common/HustModal";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import StudentViewSubmission from "./StudentViewSubmission";
import {randomImageName,} from "utils/FileUpload/covert";
import {errorNoti, successNoti} from "../../../utils/notification";
import HustCodeLanguagePicker from "../../common/HustCodeLanguagePicker";
import FileUploadZone from "../../../utils/FileUpload/FileUploadZone";
import HustContainerCard from "../../common/HustContainerCard";
import HustCodeEditor from "../../common/HustCodeEditor";
import {request} from "../../../api";
import {
  COMPUTER_LANGUAGES,
  DEFAULT_CODE_SEGMENT_CPP,
  DEFAULT_CODE_SEGMENT_JAVA,
  DEFAULT_CODE_SEGMENT_PYTHON
} from "./Constant";
import ReactHtmlParser from 'react-html-parser';
import {ContentState, EditorState} from "draft-js";
import htmlToDraft from "html-to-draftjs";
import {Editor} from "react-draft-wysiwyg";

const editorStyle = {
  editor: {
    // border: "1px solid black",
    // minHeight: "300px",
  },
};

export default function StudentViewProgrammingContestProblemDetail() {
  const params = useParams();
  const problemId = params.problemId;
  const contestId = params.contestId;
  const [problem, setProblem] = useState(null);
  const [testCases, setTestCases] = useState([]);
  const [file, setFile] = useState(null);
  const [language, setLanguage] = useState(COMPUTER_LANGUAGES.CPP);
  const [status, setStatus] = useState("");
  const [message, setMessage] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [isSubmitCode, setIsSubmitCode] = useState(0);

  const [openModalPreview, setOpenModalPreview] = useState(false);
  const [selectedTestcase, setSelectedTestcase] = useState();
  const [isProcessing, setIsProcessing] = React.useState(false);
  // const [problemDescription, setProblemDescription] = useState(
  //   ""
  // );
  const [editorStateDescription, setEditorStateDescription] = useState(
    EditorState.createEmpty()
  );
  const [fetchedImageArray, setFetchedImageArray] = useState([]);

  const ERR_STATUS = [
    "TIME_OUT",
    "PARTICIPANT_NOT_APPROVED_OR_REGISTERED",
    "PARTICIPANT_HAS_NOT_PERMISSION_TO_SUBMIT",
    "MAX_NUMBER_SUBMISSIONS_REACHED",
    "MAX_SOURCE_CODE_LENGTH_VIOLATIONS",
    "SUBMISSION_INTERVAL_VIOLATIONS",
  ];

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

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    //TODO: consider remove duplicate code
    request(
      "post",
      "/submissions/file-upload",
      (res) => {
        res = res.data;
        listSubmissionRef.current.refreshSubmission();
        inputRef.current.value = null;

        if (ERR_STATUS.includes(res.status)) {
          errorNoti(res.message, 3000);
        } else successNoti("Submitted!", 3000);

        setStatus(res.status);
        setMessage(res.message);

        setIsProcessing(false);
        setFile(null);
        inputRef.current.value = null;
      },
      {
        onError: (e) => {
          setIsProcessing(false);
          setFile(null);
          inputRef.current.value = null;

          console.error(e);
        },
      },
      formData,
      config
    );
  };

  function getProblemDetail() {
    request(
      "get",
      "/student/problems/" +
      problemId +
      "/" +
      contestId,
      (res) => {
        res = res.data;
        setProblem(res);
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

        // setProblemDescription(res?.problemStatement || "");
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
      {onError: (e) => console.log(e)}
    );
  }

  useEffect(() => {
    getProblemDetail();
  }, []);

  useEffect(() => {
    switch (language) {
      case COMPUTER_LANGUAGES.CPP:
        setCodeSolution(DEFAULT_CODE_SEGMENT_CPP);
        break;
      case COMPUTER_LANGUAGES.JAVA:
        setCodeSolution(DEFAULT_CODE_SEGMENT_JAVA);
        break;
      case COMPUTER_LANGUAGES.PYTHON:
        setCodeSolution(DEFAULT_CODE_SEGMENT_PYTHON);
        break;
    }
  }, [language])

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
    const file = new File(
      [blob],
      "SourceCode_" + problemId + now.toLocaleTimeString() + ".txt",
      {type: "text/plain;charset=utf-8"}
    );
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
        <Typography variant="h5">Description</Typography>
        {/*{ReactHtmlParser(problemDescription)}*/}
        <Editor
          toolbarHidden
          editorState={editorStateDescription}
          handlePastedText={() => false}
          readOnly
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
