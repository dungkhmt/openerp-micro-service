import PublishIcon from "@mui/icons-material/Publish";
import {Box, Chip, Divider, Grid, IconButton, Stack, TextField, Tooltip, Typography,} from "@mui/material";
import {request} from "api";
import withScreenSecurity from "component/withScreenSecurity";
import React, {useEffect, useState} from "react";
import {useHistory, useParams} from "react-router-dom";
import {errorNoti, successNoti} from "utils/notification";
import {styled} from '@mui/material/styles';
import PrimaryButton from "../../button/PrimaryButton";
import Alert from "@mui/material/Alert";
import StyledSelect from "../../select/StyledSelect";
import {useTranslation} from "react-i18next";
import {LoadingButton} from "@mui/lab";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import TertiaryButton from "../../button/TertiaryButton";
import {Controller, useForm} from "react-hook-form";
import ProgrammingContestLayout from "./ProgrammingContestLayout";
import TestCaseExecutionResult from "./TestCaseExecutionResult";
import {getPublicOptions} from "./CreateProblem";

export const getStatusColorById = (statusId) => {
  switch (statusId) {
    case 3:
      return "#2e7d32";
    default:
      return "#d32f2f";
  }
}

export const detail = (key, value, sx, helpText) => (
  <Grid container spacing={1}>
    <Grid item xs={4}>
      <Typography variant="subtitle1" sx={{...sx?.key}}>
        {helpText ? (
          <>
            {key}
            {
              <Tooltip arrow title={helpText}>
                <IconButton sx={{p: 0.5, pt: 0}}>
                  <HelpOutlineIcon sx={{fontSize: 16, color: "#000000de"}}/>
                </IconButton>
              </Tooltip>
            }
          </>
        ) : (
          key
        )}
      </Typography>
    </Grid>
    <Grid item xs={8}>
      <Typography
        variant="subtitle1"
        sx={{
          fontWeight: 400,
          whiteSpace: "pre-wrap",
          overflow: "hidden",
          textOverflow: "ellipsis",
          ...sx?.value,
        }}
      >
        {value}{" "}
      </Typography>
    </Grid>
  </Grid>
);

export const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});

const replacements = {
  // 0x0000: '\\0',         // NULL
  // 0x0007: '\\a',         // BEL (Bell)
  // 0x0008: '\\b',         // BS (Backspace)
  0x0009: '\\t',         // Horizontal Tab
  0x000A: '\\n',         // Line Feed (New Line)
  0x000D: '\\r',         // Carriage Return
  // 0x001B: '\\e',         // Escape
  0x0020: '\\s',         // Space
  // 0x200B: '[ZWSP]',   // Zero Width Space
  // 0x200C: '[ZWNJ]',   // Zero Width Non-Joiner
  // 0x200D: '[ZWJ]',    // Zero Width Joiner
  // 0x2028: '[LS]',     // Line Separator
  // 0x2029: '[PS]',     // Paragraph Separator
  // 0x00A0: '[NBSP]',   // Non-Breaking Space
  // 0x1680: '[OGHAM SPACE MARK]', // Ogham Space Mark
  // 0x2000: '[EN QUAD]', // En Quad
  // 0x2001: '[EM QUAD]', // Em Quad
  // 0x2002: '[EN SPACE]', // En Space
  // 0x2003: '[EM SPACE]', // Em Space
  // 0x2004: '[THIN SPACE]', // Thin Space
  // 0x2005: '[HAIR SPACE]', // Hair Space
  // 0x2006: '[NARROW NO-BREAK SPACE]', // Narrow No-Break Space
  // 0x2008: '[PUNCTUATION SPACE]', // Punctuation Space
  // 0x2009: '[THIN SPACE]', // Thin Space
  // 0x200A: '[HAIR SPACE]', // Hair Space
  // 0x202F: '[NARROW NO-BREAK SPACE]', // Narrow No-Break Space
  // 0x205F: '[MEDIUM MATHEMATICAL SPACE]', // Medium Mathematical Space
  // 0x3000: '[IDEOGRAPHIC SPACE]', // Ideographic Space
};

const replaceNonPrintableUnicodeCharsV1 = (inputString, onDetectSpecialCharacter) => {
  // Regex nhận diện các ký tự Unicode có thể in được (bao gồm ký hiệu, emoji, chữ cái, số, v.v.)
  // const printableRegex = /^[\p{L}\p{N}\p{P}\p{S}\p{Z}]+$/u;
  const printableRegex = /^[\p{L}\p{N}\p{P}\p{S}]+$/u; // TODO: review regex

  // Hàm escape mọi ký tự HTML đặc biệt
  const escapeHtml = (str) => {
    return str
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;")
      .replace(/`/g, "&#96;");
  };

  // Duyệt qua từng ký tự trong chuỗi (hỗ trợ ký tự Unicode mở rộng)
  return [...inputString]
    .map((char) => {
      const codePoint = char.codePointAt(0); // Lấy mã Unicode của ký tự

      // Kiểm tra ký tự in được
      if (printableRegex.test(char)) {
        return escapeHtml(char); // Escape tất cả các ký tự HTML đặc biệt
      }

      if (replacements[codePoint]) {
        return replacements[codePoint];
      } else {
        onDetectSpecialCharacter()

        // Tạo HTML thay thế
        return `<span style="
                    background-color: #fffbeb80;
                    font-weight: bold;
                    padding: 0 4px;
                    color: #8a5300;
                    white-space: nowrap;
                    border-radius: 8px;
                    border: 1px solid #8a5300;"
                  >
                    U+${codePoint.toString(16).toUpperCase().padStart(4, '0')}
                  </span>`
      }
    })
    .join(""); // Ghép các phần tử HTML lại thành chuỗi
}

export const replaceNonPrintableUnicodeCharsV2 = (inputString, onDetectSpecialCharacter) => {
  // Regex nhận diện các ký tự Unicode có thể in được (bao gồm ký hiệu, emoji, chữ cái, số, v.v.)
  // const printableRegex = /^[\p{L}\p{N}\p{P}\p{S}\p{Z}]+$/u;
  const printableRegex = /^[\p{L}\p{N}\p{P}\p{S}]+$/u;

  // Tạo một container tạm trong bộ nhớ
  const tempContainer = document.createElement("p");

  let buffer = ""; // Lưu trữ tạm các ký tự in được

  // Duyệt qua từng ký tự trong chuỗi (hỗ trợ ký tự Unicode mở rộng)
  [...inputString].forEach((char) => {
    const codePoint = char.codePointAt(0); // Lấy mã Unicode của ký tự

    // Kiểm tra ký tự in được
    if (printableRegex.test(char)) {
      buffer += char; // Gom ký tự in được vào buffer
    } else {
      if (replacements[codePoint]) {
        buffer += replacements[codePoint];
      } else {
        onDetectSpecialCharacter()

        if (buffer.length > 0) {
          // Nếu buffer có nội dung, thêm nó vào container tạm bằng textContent
          const textNode = document.createTextNode(buffer);
          tempContainer.appendChild(textNode);
          buffer = ""; // Reset buffer
        }

        // Xử lý ký tự không in được bằng cách tạo HTML thay thế
        const span = document.createElement("span");
        span.textContent = `U+${codePoint.toString(16).toUpperCase().padStart(4, '0')}`;
        span.style.cssText = `
        background-color: #fffbeb80; 
        font-weight: bold; 
        padding: 0 4px; 
        color: #8a5300; 
        white-space: nowrap; 
        border-radius: 8px; 
        border: 1px solid #8a5300;
      `;
        tempContainer.appendChild(span);
      }
    }
  });

  // Xử lý phần còn lại trong buffer
  if (buffer.length > 0) {
    const textNode = document.createTextNode(buffer);
    tempContainer.appendChild(textNode);
  }

  // Trả về chuỗi HTML từ container tạm
  return tempContainer.innerHTML;
};

//TODO: improve this screen
// 1. Add sample file: When user choose upload -> open a modal with sample file to download
function CreateTestCase(props) {
  const history = useHistory();
  const {problemId} = useParams();

  const [input, setInput] = useState("");
  const [result, setResult] = useState(null);
  const [load, setLoad] = useState(false);
  const [checkTestcaseResult, setCheckTestcaseResult] = useState(false);
  const [loading, setLoading] = useState(false);
  const [disableSaveButton, setDisableSaveButton] = useState(false);
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
    watch
  } = useForm({
    defaultValues: {
      point: 1,
      isPublic: "N",
      uploadMode: "EXECUTE",
      description: null,
      correctAnswer: null
    }
  });

  const publicOptions = getPublicOptions(t)

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
  //
  //   request(
  //     "POST",
  //     "/get-test-case-result/" + problemId,
  //     (res) => {
  //       console.log("res", res);
  //       setLoad(false);
  //       setResult(res.data.result);
  //       setCheckTestcaseResult(true);
  //       if (res.data.status != "ok") {
  //         warningNoti(res.data.status, false);
  //       }
  //     },
  //     {},
  //     body
  //   ).then();
  // };
  //
  // const saveTestCase = () => {
  //   if (!checkTestcaseResult) {
  //     // setShowSubmitWarming(true);
  //     warningNoti("You must test your test case result before save", true);
  //     return;
  //   }
  //
  //   let body = {
  //     input: input,
  //     result: result,
  //     point: point,
  //     isPublic: isPublic,
  //   };
  //
  //   request(
  //     "POST",
  //     "/testcases/" + problemId,
  //     (res) => {
  //       console.log("res", res);
  //       // setShowSubmitSuccess(true);
  //       history.goBack();
  //       successNoti("Your test case is saved", true);
  //     },
  //     {},
  //     body
  //   ).then();
  // };

  const onFileChange = (event) => {
    setDisableSaveButton(false)
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
    reset()
  }

  const handleExit = () => {
    history.push(`/programming-contest/edit-problem/${problemId}`);
  }

  const reset = () => {
    if (fileName) {
      document.getElementById('selected-testcase-file').value = ""
    }

    setShowWarning(false);
    setFileName(null)
    setFileContent(null)
    setUploadResult(null);
  }

  const onSubmit = (data) => {
    setLoading(true);
    setUploadResult(null);

    const body = {
      problemId: problemId,
      ...data,
      isPublic: data.isPublic === "Y",
      correctAnswer: data.uploadMode === "EXECUTE" ? null : data.correctAnswer,
    };

    const formData = new FormData();
    formData.append('dto', new Blob([JSON.stringify(body)], {type: 'application/json'}));
    formData.append("file", fileName);

    const config = {
      headers: {
        "content-type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/testcases",
      (res) => {
        setDisableSaveButton(true);
        setLoading(false);

        if (body.uploadMode === "EXECUTE") {
          setUploadResult(res.data);

          if (res.data.status.id === 3) {
            successNoti(tTestcase("addSuccess"))
          }

          document.getElementById('result-of-creating-testcase').scrollIntoView({behavior: "smooth"});
        } else {
          setUploadResult(null);
          successNoti(tTestcase("addSuccess"))
          handleExit()
        }
      },
      {
        onError: (e) => {
          setLoading(false);
          errorNoti(t("common:error"))
        },
      },
      formData,
      config
    );
  };

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
    setDisableSaveButton(false);
  }, [watch('correctAnswer')]);

  return (
    <ProgrammingContestLayout title={tTestcase("add")} onBack={handleExit}>
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
            />
          </Grid>
          {watch("uploadMode") === "NOT_EXECUTE" && <Grid item xs={12}>
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
            />
          </Grid>
          }
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
            disabled={(watch("uploadMode") === "EXECUTE" && !fileName) || loading || disableSaveButton}
            sx={{textTransform: 'capitalize'}}
            loading={loading}
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

const screenName = "SCR_CREATE_TESTCASE";
export default withScreenSecurity(CreateTestCase, screenName, true);
