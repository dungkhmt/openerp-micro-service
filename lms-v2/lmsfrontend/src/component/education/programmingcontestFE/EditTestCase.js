import PublishIcon from "@mui/icons-material/Publish";
import {Box, Chip, Divider, Grid, Stack, TextField, Typography,} from "@mui/material";
import {request} from "api";
import withScreenSecurity from "component/withScreenSecurity";
import React, {useEffect, useState} from "react";
import {useHistory, useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {Controller, useForm} from "react-hook-form";
import StyledSelect from "../../select/StyledSelect";
import PrimaryButton from "../../button/PrimaryButton";
import Alert from "@mui/material/Alert";
import TertiaryButton from "../../button/TertiaryButton";
import {LoadingButton} from "@mui/lab";
import {errorNoti, successNoti} from "../../../utils/notification";
import {replaceNonPrintableUnicodeCharsV2, VisuallyHiddenInput} from "./CreateTestCase";
import ProgrammingContestLayout from "./ProgrammingContestLayout";
import TestCaseExecutionResult from "./TestCaseExecutionResult";

function EditTestCase(props) {
  const history = useHistory();

  // const [value, setValue] = useState(0);
  const [input, setInput] = useState("");
  const [result, setResult] = useState("");
  const [screenHeight, setScreenHeight] = useState(
    (window.innerHeight - 300) / 2 + "px"
  );
  const { problemId, testCaseId } = useParams();
  const [description, setDescription] = useState("");
  const [solution, setSolution] = useState("");
  const [load, setLoad] = useState(false);
  const [checkTestcaseResult, setCheckTestcaseResult] = useState(false);
  const [point, setPoint] = useState(0);
  const [isPublic, setIsPublic] = useState("");

  const [isProcessing, setIsProcessing] = useState(false);
  // const [filename, setFilename] = useState("");
  const [uploadMessage, setUploadMessage] = useState("");
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const [submitting, setSubmitting] = useState(false);
  const [fileName, setFileName] = useState(null);
  const [fileContent, setFileContent] = useState(null);
  const [showWarning, setShowWarning] = useState(false);
  const [uploadResult, setUploadResult] = useState(null);

  const {t, i18n} = useTranslation(["common", "validation"]);
  const {t: tTestcase} = useTranslation("education/programmingcontest/testcase");
  const {
    register,
    control,
    handleSubmit,
    errors,
    triggerValidation,
    formState,
    watch,
    reset,
    setValue
  } = useForm({
    defaultValues: { // must be ' ' to trigger TextField
      point: '',
      isPublic: '',
      uploadMode: "NOT_EXECUTE",
      description: '',
      correctAnswer: ''
    }
  });

  const [focusStates, setFocusStates] = useState({});

  const handleFocus = (field) => {
    setFocusStates((prev) => ({...prev, [field]: true}));
  };

  const handleBlur = (field) => {
    setFocusStates((prev) => ({...prev, [field]: false}));
  };

  const isFieldFocused = (field) => !!focusStates[field];

  const publicOptions = [
    {
      label: t("common:yes"),
      value: "Y",
    },
    {
      label: t("common:no"),
      value: "N",
    },
  ];

  const uploadModes = [
    {
      label: tTestcase("notExecute"),
      value: "NOT_EXECUTE",
    },
    {
      label: tTestcase("execute"),
      value: "EXECUTE",
    },
  ];

  // const getTestCaseResult = () => {
  //   console.log("get test case result");
  //   setLoad(true);
  //   let body = {
  //     testcase: input,
  //   };
  //   request(
  //     "POST",
  //     "/testcases/" + problemId + "/result",
  //     (res) => {
  //       console.log("res", res);
  //       setLoad(false);
  //       setResult(res.data.result);
  //       setCheckTestcaseResult(true);
  //     },
  //     {},
  //     body
  //   ).then();
  // };

  // const handleFormSubmit = (event) => {
  //   event.preventDefault();
  //   setIsProcessing(true);
  //   setUploadMessage("");
  //   let body = {
  //     //testCaseId:testCaseId,
  //     problemId: problemId,
  //     point: point,
  //     isPublic: isPublic,
  //     description: description,
  //     correctAnswer: result,
  //   };
  //   let formData = new FormData();
  //   formData.append("inputJson", JSON.stringify(body));
  //
  //   if (filename !== "") {
  //     formData.append("file", filename);
  //
  //     const config = {
  //       headers: {
  //         "content-type": "multipart/form-data",
  //       },
  //     };
  //
  //     request(
  //       "put",
  //       "/testcases/" + testCaseId + "/file-upload",
  //       (res) => {
  //         res = res.data;
  //         setIsProcessing(false);
  //         console.log("handleFormSubmit, res = ", res);
  //         setUploadMessage(res.message);
  //         //if (res.status == "TIME_OUT") {
  //         //  alert("Time Out!!!");
  //         //} else {
  //         //}
  //       },
  //       {
  //         onError: (e) => {
  //           setIsProcessing(false);
  //           console.error(e);
  //           //alert("Time Out!!!");
  //         },
  //       },
  //       formData,
  //       config
  //     );
  //   } else {
  //     // without file attached
  //     request(
  //       "put",
  //       "/testcases/" + testCaseId,
  //       (res) => {
  //         res = res.data;
  //         setIsProcessing(false);
  //         console.log("handleFormSubmit, res = ", res);
  //         setUploadMessage(res.message);
  //         //if (res.status == "TIME_OUT") {
  //         //  alert("Time Out!!!");
  //         //} else {
  //         //}
  //       },
  //       {
  //         onError: (e) => {
  //           setIsProcessing(false);
  //           console.error(e);
  //           //alert("Time Out!!!");
  //         },
  //       },
  //       formData
  //     );
  //   }
  // };

  const onFileChange = (event) => {
    setShowWarning(false);
    setUploadResult(null)

    const file = event.target.files[0];
    if (file) {
      setFileName(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setFileContent(e.target.result);
      };
      reader.onerror = (e) => {
        errorNoti(tTestcase("errorReadingFile"));
      };
      reader.readAsText(file);
      event.target.value = ""
    }
  }

  const handleDeleteFile = () => {
    clearTestcaseFileAndUploadResult()
  }

  const handleExit = () => {
    history.push(`/programming-contest/edit-problem/${problemId}`);
  }

  const clearTestcaseFileAndUploadResult = () => {
    if (fileName) {
      document.getElementById('selected-testcase-file').value = ""
    }

    setShowWarning(false);
    setFileName(null)
    setFileContent(null)
    setUploadResult(null);
  }

  const onSubmit = (data) => {
    setSubmitting(true);
    setUploadResult(null);

    const body = {
      //testCaseId:testCaseId,
      problemId: problemId,
      ...data,
      isPublic: data.isPublic === "Y",
      correctAnswer: data.uploadMode === "EXECUTE" ? null : data.correctAnswer,
    };

    const formData = new FormData();
    formData.append('dto', new Blob([JSON.stringify(body)], {type: 'application/json'}));

    let config = undefined;
    if (fileName) {
      formData.append("file", fileName);
      config = {
        headers: {
          "content-type": "multipart/form-data",
        },
      };
    }

    request(
      "put",
      `/testcases/${testCaseId}`,
      (res) => {
        setSubmitting(false);

        if (body.uploadMode === "EXECUTE") {
          setUploadResult(res.data);

          if (res.data.status.id === 3) {
            successNoti(tTestcase("editSuccess"));
            setValue("correctAnswer", res.data.stdout, {shouldValidate: true});
          }

          document
            .getElementById("result-of-creating-testcase")
            .scrollIntoView({behavior: "smooth"});
        } else {
          setUploadResult(null);
          successNoti(tTestcase("editSuccess"));
          handleExit();
        }
      },
      {
        onError: (e) => {
          setSubmitting(false);
          errorNoti(t("common:error"));
        }
      },
      formData,
      config
    );
  };

  useEffect(() => {
    request(
      "get",
      `/testcases/${testCaseId}`,
      (res) => {
        setValue("point", res.data.point, {shouldValidate: true});
        setValue("isPublic", res.data.isPublic, {shouldValidate: true})
        setValue("correctAnswer", res.data.correctAns, {shouldValidate: true});
        setValue("description", res.data.description, {shouldValidate: true});

        // setDescription(
        //   //res.data.problemDescription != null ? res.data.problemDescription : " "
        //   res.data.description
        // );
        // setSolution(
        //   res.data.problemSolution != null ? res.data.problemSolution : " "
        // );
        // setInput(res.data.testCase != null ? res.data.testCase : " ");
      },
      {
        onError: (e) => {
          errorNoti(t("common:error"))
        },
      });
  }, []);

  useEffect(() => {
    if (fileContent) {
      document.getElementById("testcase-content").innerHTML = replaceNonPrintableUnicodeCharsV2(fileContent, () => setShowWarning(true));
    }
  }, [fileContent]);

  useEffect(() => {
    if (formState.isSubmitted) {
      triggerValidation("point")
    }
  }, [i18n.language]);

  useEffect(() => {
    setUploadResult(null)
  }, [watch("uploadMode")]);

  return (
    <ProgrammingContestLayout title={tTestcase("edit")} onBack={handleExit}>
      <form onSubmit={handleSubmit(onSubmit)} style={{marginTop: 2}}>
        <Grid container spacing={2} mb={2} mt={0}>
          <Grid item xs={2}>
            <TextField
              fullWidth
              autoFocus
              type="number"
              id={tTestcase("point")}
              label={tTestcase("point") + " *"}
              name="point"
              size="small"
              error={!!errors.point}
              helperText={errors.point?.message}
              inputRef={register({
                required: t("required", {ns: "validation"}),
                min: {
                  value: 1,
                  message: t("numberMustGreaterThan", {ns: "validation", fieldName: tTestcase("point"), min: 0})
                }
              })}
              InputLabelProps={{
                shrink: !!watch("point") || isFieldFocused("point"),
              }}
              onFocus={() => handleFocus("point")}
              onBlur={() => handleBlur("point")}
            />
          </Grid>
          <Grid item xs={2}>
            <Controller
              name="isPublic"
              control={control}
              as={<StyledSelect
                fullWidth
                key={t("public", {ns: "common"})}
                label={t("public", {ns: "common"})}
                options={publicOptions}
                sx={{minWidth: 'unset', mr: 'unset'}}
              />}
            />
          </Grid>
          <Grid item xs={2}>
            <Controller
              name="uploadMode"
              control={control}
              as={<StyledSelect
                fullWidth
                key={tTestcase("uploadMode")}
                label={tTestcase("uploadMode")}
                options={uploadModes}
                sx={{minWidth: 'unset', mr: 'unset'}}
              />}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              fullWidth
              id={t("common:description")}
              label={t("common:description")}
              name="description"
              size="small"
              error={!!errors.description}
              helperText={errors.description?.message}
              inputRef={register()}
              InputLabelProps={{
                shrink: !!watch("description") || isFieldFocused("description"),
              }}
              onFocus={() => handleFocus("description")}
              onBlur={() => handleBlur("description")}
            />
          </Grid>
          <Grid item xs={12} sx={watch("uploadMode") === "EXECUTE" ? {display: "none"} : undefined}>
            <TextField
              fullWidth
              multiline
              minRows={3}
              id={tTestcase("correctAnswer")}
              label={tTestcase("correctAnswer") + " *"}
              name="correctAnswer"
              size="small"
              error={!!errors.correctAnswer}
              helperText={errors.correctAnswer?.message}
              inputRef={register({required: t("required", {ns: "validation"}),})}
              InputLabelProps={{
                shrink: !!watch("correctAnswer") || isFieldFocused("correctAnswer"),
              }}
              onFocus={() => handleFocus("correctAnswer")}
              onBlur={() => handleBlur("correctAnswer")}
            />
          </Grid>
        </Grid>

        <PrimaryButton
          component="label"
          role={undefined}
          tabIndex={-1}
          startIcon={<PublishIcon/>}
        >
          {tTestcase("selectTestcaseFile")}
          <VisuallyHiddenInput type="file" hidden id="selected-testcase-file" accept=".txt, text/*"
                               onChange={onFileChange}/>
        </PrimaryButton>

        {fileName && (
          <Chip
            style={{marginLeft: "20px"}}
            color="success"
            variant="outlined"
            label={fileName.name}
            onDelete={handleDeleteFile}
          />
        )}

        <Box>
          {fileContent && <>
            <Typography
              variant="h6"
              sx={{mt: 2, mb: 1}}
            >
              {tTestcase("testcaseContent")}
            </Typography>
            <Alert severity="info"
                   sx={{mb: 1}}>{tTestcase("testcaseContentNote")}: <b>\s</b> - <b>{tTestcase("space")}</b>, <b>\r</b> - <b>{tTestcase("carriageReturn")}</b>, <b>\n</b> - <b>{tTestcase("newLine")}</b>, <b>\t</b> - <b>{tTestcase("tab")}</b>.</Alert>
            {showWarning &&
              <Alert severity="warning" sx={{mb: 1}}><b>{tTestcase("note")}:</b> {tTestcase("testcaseContentWarning")}
              </Alert>}
            <p id="testcase-content" style={{
              borderRadius: "12px",
              margin: 0,
              padding: "24px",
              backgroundColor: "#f6f7f880",
              border: "1px solid #e8eaee",
              whiteSpace: "normal",
              wordBreak: "break-all",
              overflowWrap: "anywhere",
              fontFamily: "'Fira Code', 'JetBrains Mono', monospace",
              fontVariantLigatures: "none",
            }}></p>
          </>}
        </Box>

        <Stack direction="row" spacing={2} mt={2}>
          <TertiaryButton variant="outlined" onClick={handleExit}>
            {t("common:exit")}
          </TertiaryButton>
          <LoadingButton
            color="primary"
            variant="contained"
            type="submit"
            disabled={(watch("uploadMode") === "EXECUTE" && !fileName) || submitting}
            sx={{textTransform: 'capitalize'}}
            loading={submitting}
          >
            {t("save", {ns: "common"})}
          </LoadingButton>
        </Stack>
      </form>

      <Divider sx={{mt: 2, mb: 2}}/>
      <TestCaseExecutionResult uploadResult={uploadResult}/>
    </ProgrammingContestLayout>
  );
}

const screenName = "SCR_EDIT_TEST_CASE";
export default withScreenSecurity(EditTestCase, screenName, true);
