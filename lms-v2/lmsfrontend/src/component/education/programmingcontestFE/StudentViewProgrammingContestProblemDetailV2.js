import { LoadingButton } from "@mui/lab";
import { Alert, Box, Button, Divider, Stack, Typography } from "@mui/material";
import { styled } from "@mui/material/styles";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import { ContentState, EditorState } from "draft-js";
import htmlToDraft from "html-to-draftjs";
import React, { forwardRef, useEffect, useRef, useState } from "react";
import { Editor } from "react-draft-wysiwyg";
import { useParams } from "react-router";
import { randomImageName } from "utils/FileUpload/covert";
import { errorNoti, successNoti } from "utils/notification";
import { request } from "../../../api";
import FileUploadZone from "../../../utils/FileUpload/FileUploadZone";
import HustCodeEditor from "../../common/HustCodeEditor";
import HustCodeLanguagePicker from "../../common/HustCodeLanguagePicker";
import HustContainerCard from "../../common/HustContainerCard";
import {
  COMPUTER_LANGUAGES,
  DEFAULT_CODE_SEGMENT_C,
  DEFAULT_CODE_SEGMENT_CPP,
  DEFAULT_CODE_SEGMENT_JAVA,
  DEFAULT_CODE_SEGMENT_PYTHON,
  SUBMISSION_MODE_NOT_ALLOWED,
  SUBMISSION_MODE_SOURCE_CODE,
} from "./Constant";
import StudentViewSubmission from "./StudentViewSubmission";
import {useTranslation} from "react-i18next";

const VisuallyHiddenInput = styled("input")({
  clip: "rect(0 0 0 0)",
  clipPath: "inset(50%)",
  height: 1,
  overflow: "hidden",
  position: "absolute",
  bottom: 0,
  left: 0,
  whiteSpace: "nowrap",
  width: 1,
});

export const InputFileUpload = forwardRef((props, ref) => {
  const { label, buttonProps, ...otherProps } = props;
  return (
    <Button
      component="label"
      variant="outlined"
      sx={{ textTransform: "none" }}
      {...buttonProps}
    >
      {label}
      <VisuallyHiddenInput type="file" ref={ref} {...otherProps} />
    </Button>
  );
});

const editorStyle = {
  editor: {
    // border: "1px solid black",
    // minHeight: "300px",
  },
};

const ERR_STATUS = [
  "TIME_OUT",
  "PARTICIPANT_NOT_APPROVED_OR_REGISTERED",
  "PARTICIPANT_HAS_NOT_PERMISSION_TO_SUBMIT",
  "MAX_NUMBER_SUBMISSIONS_REACHED",
  "MAX_SOURCE_CODE_LENGTH_VIOLATIONS",
  "SUBMISSION_INTERVAL_VIOLATIONS",
  "SUBMISSION_NOT_ALLOWED",
  "ILLEGAL_LANGUAGE",
];

export default function StudentViewProgrammingContestProblemDetail() {
  const params = useParams();
  const problemId = params.problemId;
  const contestId = params.contestId;
  const {t} = useTranslation(["education/programmingcontest/problem"]);

  const [problem, setProblem] = useState(null);
  const [testCases, setTestCases] = useState([]);
  const [file, setFile] = useState(null);
  const [language, setLanguage] = useState(COMPUTER_LANGUAGES.CPP17);
  const [listLanguagesAllowed, setListLanguagesAllowed] = useState([]);
  const [status, setStatus] = useState("");
  const [message, setMessage] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [submissionMode, setSubmissionMode] = useState(
    SUBMISSION_MODE_SOURCE_CODE
  );
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
  const [sampleTestCase, setSampleTestCase] = useState(
    null//EditorState.createEmpty()
  );
  
  const [fetchedImageArray, setFetchedImageArray] = useState([]);

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
      "/contests/" + contestId + "/problems/" + problemId,
      (res) => {
        res = res.data;
        setProblem(res);
        if (res.listLanguagesAllowed != null && res.listLanguagesAllowed.length > 0) {
          setLanguage(res.listLanguagesAllowed[0])
          setListLanguagesAllowed(res.listLanguagesAllowed);
        }
        if (res.isPreloadCode) setCodeSolution(res.preloadCode);
        if (res.submissionMode) setSubmissionMode(res.submissionMode);
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
        let { contentBlocks, entityMap } = problemDescriptionHtml;
        let contentDescriptionState = ContentState.createFromBlockArray(
          contentBlocks,
          entityMap
        );
        let statementDescription = EditorState.createWithContent(
          contentDescriptionState
        );
        setEditorStateDescription(statementDescription);

        // public testcase    
        /*  
        let sampleTestCaseHtml = htmlToDraft(res.sampleTestCase);
        let { contentBlocksTestCase, entityMapTestCase } = sampleTestCaseHtml;
        let contentDescriptionStateTestCase = ContentState.createFromBlockArray(
          contentBlocksTestCase,
          entityMapTestCase
        );
        let editorSampleTestCase = EditorState.createWithContent(
          contentDescriptionStateTestCase
        );
        //setSampleTestCase(editorSampleTestCase);
        */
        setSampleTestCase(res.sampleTestCase);    
        //console.log('GetProblemDetail, res = ',res);
      },
      { onError: (e) => console.log(e) }
    );
  }

  useEffect(() => {
    getProblemDetail();
  }, []);

  useEffect(() => {
    if (problem && problem.isPreloadCode === true) return;
    switch (language) {
      case COMPUTER_LANGUAGES.C:
        setCodeSolution(DEFAULT_CODE_SEGMENT_C);
        break;
      case COMPUTER_LANGUAGES.CPP11:
      case COMPUTER_LANGUAGES.CPP14:
      case COMPUTER_LANGUAGES.CPP17:
        setCodeSolution(DEFAULT_CODE_SEGMENT_CPP);
        break;
      case COMPUTER_LANGUAGES.JAVA:
        setCodeSolution(DEFAULT_CODE_SEGMENT_JAVA);
        break;
      case COMPUTER_LANGUAGES.PYTHON:
        setCodeSolution(DEFAULT_CODE_SEGMENT_PYTHON);
        break;
    }
  }, [language]);

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
    const blob = new Blob([codeSolution], { type: "text/plain;charset=utf-8" });
    const now = new Date();
    const file = new File(
      [blob],
      "SourceCode_" + problemId + now.toLocaleTimeString() + ".txt",
      { type: "text/plain;charset=utf-8" }
    );
    setFile(file);
    setIsSubmitCode(isSubmitCode + 1);
  }

  useEffect(() => {
    if (isSubmitCode > 0) handleFormSubmit(null);
  }, [isSubmitCode]);

  return (
    <HustContainerCard
      title={"Problem: " + (problem ? problem.problemName : "")}
    >
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
        {/*
        <Typography variant="h5">Sample testcase</Typography>
        
        <Editor
          toolbarHidden
          editorState={sampleTestCase}
          handlePastedText={() => false}
          readOnly
          editorStyle={editorStyle.editor}
        />
      */}
        {/*ReactHtmlParser(sampleTestCase)*/}
        {/*sampleTestCase*/}
        
        <HustCodeEditor
        title={t("sampleTestCase")}
        language={COMPUTER_LANGUAGES.C}
        sourceCode={sampleTestCase}
         /> 

        {fetchedImageArray.length !== 0 &&
          fetchedImageArray.map((file) => (
            <FileUploadZone file={file} removable={false} />
          ))}
      </Box>

      <Divider />

      <ModalPreview chosenTestcase={selectedTestcase} />
      <Box sx={{ mt: 2 }}>
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
            listLanguagesAllowed={listLanguagesAllowed}
          />
          <Box
            sx={{
              width: "100%",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <LoadingButton
              disabled={
                isProcessing || submissionMode === SUBMISSION_MODE_NOT_ALLOWED
              }
              sx={{ width: 160, mt: 1, mb: 1 }}
              // loading={isProcessing}
              // loadingIndicator="Submitting…"
              variant="contained"
              color="primary"
              type="submit"
              onClick={submitCode}
            >
              <span>SUBMIT CODE</span>
            </LoadingButton>

            {submissionMode === SUBMISSION_MODE_NOT_ALLOWED && (
              <Typography color="gray" ml={1}>
                Currently, this contest problem is not open for submissions
              </Typography>
            )}
          </Box>
        </Box>

        <Divider>Or</Divider>

        <form onSubmit={handleFormSubmit}>
          <Stack alignItems={"center"} spacing={2} sx={{ mt: 1 }}>
            <Stack
              direction="row"
              justifyContent={"center"}
              alignItems="center"
              spacing={4}
            >
              <HustCodeLanguagePicker
                listLanguagesAllowed={listLanguagesAllowed}
                language={language}
                onChangeLanguage={(e) => setLanguage(e.target.value)}
              />
              <Stack direction="row" spacing={1} alignItems="center">
                <InputFileUpload
                  id="selected-upload-file"
                  label="Select file"
                  accept=".c, .cpp, .java, .py"
                  onChange={onFileChange}
                  ref={inputRef}
                />
                {file && <Typography variant="body1">{file.name}</Typography>}
              </Stack>
            </Stack>

            <LoadingButton
              disabled={
                isProcessing || submissionMode === SUBMISSION_MODE_NOT_ALLOWED
              }
              sx={{ width: 128 }}
              // loading={isProcessing}
              // loadingIndicator="Submitting…"
              variant="contained"
              color="primary"
              type="submit"
              onChange={onInputChange}
            >
              <span>Submit</span>
            </LoadingButton>
          </Stack>
        </form>
        {/* <div>
          <h3>
            Status: <em>{status}</em>
          </h3>
        </div>
        <div>
          <h3>
            Message: <em>{message}</em>
          </h3>
        </div> */}
      </Box>
      {language === COMPUTER_LANGUAGES.JAVA && (
        <Alert
          variant="outlined"
          severity="info"
          sx={{
            borderRadius: 1.5,
            bgcolor: "#e5f6fd",
            mt: 3,
          }}
        >
          With Java, the public class must be declared as:{" "}
          <b>public class Main {"{...}"}</b>
        </Alert>
      )}
      <Box sx={{ mt: 3 }}>
        <StudentViewSubmission problemId={problemId} ref={listSubmissionRef} />
      </Box>
    </HustContainerCard>
  );
}
