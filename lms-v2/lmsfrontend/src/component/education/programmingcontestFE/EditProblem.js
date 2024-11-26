import { makeStyles } from "@material-ui/core/styles";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { LoadingButton } from "@mui/lab";
import {
  Box,
  Button,
  Checkbox,
  Chip,
  FormControl,
  FormControlLabel,
  Grid,
  InputAdornment,
  InputLabel,
  ListItemText, ListSubheader,
  MenuItem,
  OutlinedInput,
  Select,
  TextField,
  Typography,
} from "@mui/material";
import { request } from "api";
import withScreenSecurity from "component/withScreenSecurity";
import React, { useEffect, useState } from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import FileUploadZone from "utils/FileUpload/FileUploadZone";
import { randomImageName } from "utils/FileUpload/covert";
import { PROBLEM_STATUS } from "utils/constants";
import { errorNoti, successNoti, warningNoti } from "utils/notification";
import HustCodeEditor from "../../common/HustCodeEditor";
import HustContainerCard from "../../common/HustContainerCard";
import HustDropzoneArea from "../../common/HustDropzoneArea";
import RichTextEditor from "../../common/editor/RichTextEditor";
import { CompileStatus } from "./CompileStatus";
import {
  COMPUTER_LANGUAGES,
  CUSTOM_EVALUATION,
  NORMAL_EVALUATION,
} from "./Constant";
import ListTestCase from "./ListTestCase";
import ModelAddNewTag from "./ModelAddNewTag";
import { getAllTags } from "./service/TagService";

const useStyles = makeStyles((theme) => ({
  description: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(2),
  },
}));

function EditProblem() {
  const { t } = useTranslation([
    "education/programmingcontest/problem",
    "common",
    "validation",
  ]);

  const classes = useStyles();

  const { problemId } = useParams();

  const [problemName, setProblemName] = useState("");
  const [description, setDescription] = useState("");
  const [solution, setSolution] = useState("");
  // const [timeLimit, setTimeLimit] = useState(1);
  const [timeLimitCPP, setTimeLimitCPP] = useState(1);
  const [timeLimitJAVA, setTimeLimitJAVA] = useState(1);
  const [timeLimitPYTHON, setTimeLimitPYTHON] = useState(1);
  const [memoryLimit, setMemoryLimit] = useState(1);
  const [levelId, setLevelId] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [isPreloadCode, setIsPreloadCode] = useState(false);
  const [preloadCode, setPreloadCode] = useState("");
  const [solutionCheckerLanguage, setSolutionCheckerLanguage] = useState(
    COMPUTER_LANGUAGES.CPP17
  );
  const [solutionChecker, setSolutionChecker] = useState("");
  const [isCustomEvaluated, setIsCustomEvaluated] = useState(false);
  const [languageSolution, setLanguageSolution] = useState(
    COMPUTER_LANGUAGES.CPP17
  );
  const [showCompile, setShowCompile] = useState(false);
  const [statusSuccessful, setStatusSuccessful] = useState(false);
  const [isPublic, setIsPublic] = useState(false);
  const [compileMessage, setCompileMessage] = useState("");
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [attachmentFiles, setAttachmentFiles] = useState([]);
  const [fetchedImageArray, setFetchedImageArray] = useState([]);
  const [removedFilesId, setRemovedFileIds] = useState([]);
  const [status, setStatus] = useState("");
  const [isOwner, setIsOwner] = useState(false);
  const [sampleTestCase, setSampleTestCase] = useState(null);

  const defaultLevel = ["easy", "medium", "hard"];

  const [loading, setLoading] = useState(false);

  const [openModalAddNewTag, setOpenModalAddNewTag] = useState(false);

  const handleGetTagsSuccess = (res) => setTags(res.data);
  useEffect(() => {
    getAllTags(handleGetTagsSuccess);
  }, []);

  const handleSelectTags = (event) => {
    const selectingTags = event.target.value;

    const filteredTags = [
      ...new Map(selectingTags.map((tag) => [tag.tagId, tag])).values(),
    ];

    setSelectedTags(filteredTags);
  };

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
    request("get", "teacher/problems/" + problemId, (res) => {
      res = res.data;
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
      // setTimeLimit(res.timeLimit);
      setTimeLimitCPP(res.timeLimitCPP);
      setTimeLimitJAVA(res.timeLimitJAVA);
      setTimeLimitPYTHON(res.timeLimitPYTHON);
      setMemoryLimit(res.memoryLimit);
      setIsPublic(res.publicProblem);
      setLanguageSolution(res.correctSolutionLanguage);
      setCodeSolution(res.correctSolutionSourceCode);
      setIsPreloadCode(res.isPreloadCode);
      setPreloadCode(res.preloadCode);
      setSolutionCheckerLanguage(res.solutionCheckerLanguage);
      setSolutionChecker(res.solutionCheckerSourceCode || "");
      setIsCustomEvaluated(res.scoreEvaluationType === CUSTOM_EVALUATION);
      setDescription(res.problemDescription);
      setSelectedTags(res.tags);
      setStatus(res.status);
      setSampleTestCase(res.sampleTestCase);
      setIsOwner(res.roles?.includes("OWNER"));
    });
  }, [problemId]);

  function checkCompile() {
    let body = {
      source: codeSolution,
      computerLanguage: languageSolution,
    };

    setShowCompile(false);
    setLoading(true);
    request(
      "post",
      "/check-compile",
      (res) => {
        if (res.data.status === "Successful") {
          setShowCompile(true);
          setStatusSuccessful(true);
        } else {
          setShowCompile(true);
          setStatusSuccessful(false);
        }
        setCompileMessage(res.data.message);
      },
      {},
      body
    ).then(() => setLoading(false));
  }

  const validateSubmit = () => {
    if (problemName === "") {
      errorNoti(
        t("missingField", { ns: "validation", fieldName: t("problemName") }),
        3000
      );
      return false;
    }
    if (
      timeLimitCPP <= 0 ||
      timeLimitJAVA <= 0 ||
      timeLimitPYTHON <= 0 ||
      timeLimitCPP > 300 ||
      timeLimitJAVA > 300 ||
      timeLimitPYTHON > 300
    ) {
      errorNoti(
        t("numberBetween", {
          ns: "validation",
          fieldName: t("timeLimit"),
          min: 1,
          max: 300,
        }),
        3000
      );
      return false;
    }
    if (memoryLimit <= 0 || memoryLimit > 1024) {
      errorNoti(
        t("numberBetween", {
          ns: "validation",
          fieldName: t("memoryLimit"),
          min: 1,
          max: 1024,
        }),
        3000
      );
      return false;
    }
    if (!statusSuccessful) {
      warningNoti(t("validateSubmit.warningCheckSolutionCompile"), 5000);
      return false;
    }
    return true;
  };

  function handleSubmit() {
    if (!validateSubmit()) return;

    const tagIds = selectedTags.map((tag) => tag.tagId);

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
      // timeLimit: timeLimit,
      timeLimitCPP: timeLimitCPP,
      timeLimitJAVA: timeLimitJAVA,
      timeLimitPYTHON: timeLimitPYTHON,
      levelId: levelId,
      memoryLimit: memoryLimit,
      correctSolutionLanguage: languageSolution,
      solution: solution,
      correctSolutionSourceCode: codeSolution,
      isPreloadCode: isPreloadCode,
      preloadCode: preloadCode,
      solutionChecker: solutionChecker,
      isPublic: isPublic,
      fileId: fileId,
      removedFilesId: removedFilesId,
      scoreEvaluationType: isCustomEvaluated
        ? CUSTOM_EVALUATION
        : NORMAL_EVALUATION,
      tagIds: tagIds,
      status: status,
      sampleTestCase: sampleTestCase,
    };

    let formData = new FormData();
    formData.append("ModelUpdateContestProblem", JSON.stringify(body));
    for (const file of attachmentFiles) {
      formData.append("files", file);
    }

    setLoading(true);

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "put",
      "/problems/" + problemId,
      (res) => {
        setLoading(false);
        successNoti("Problem saved successfully", 10000);
      },
      {
        onError: (e) => {
          errorNoti(t("error", { ns: "common" }), 3000);
          setLoading(false);
        },
      },
      formData,
      config
    );
  }

  return (
    <HustContainerCard title={t("editProblem")}>
      <Grid container spacing={2}>
        <Grid item xs={10}>
          <TextField
            fullWidth
            required
            id="problemName"
            label={t("problemName")}
            placeholder="Problem Name"
            value={problemName}
            onChange={(event) => {
              setProblemName(event.target.value);
            }}
          />
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            id="status"
            label={t("status")}
            select
            value={status}
            onChange={(event) => {
              setStatus(event.target.value);
            }}
            disabled={!isOwner}
          >
            {Object.values(PROBLEM_STATUS).map((item) => (
              <MenuItem key={item} value={item}>
                {item}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            select
            id="levelId"
            label={t("level")}
            value={levelId}
            onChange={(event) => {
              setLevelId(event.target.value);
            }}
          >
            {defaultLevel.map((item) => (
              <MenuItem key={item} value={item}>
                {item}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            select
            id="isPublicProblem"
            label={t("public", { ns: "common" })}
            onChange={(event) => {
              setIsPublic(event.target.value);
            }}
            value={isPublic}
          >
            <MenuItem key={"true"} value={true}>
              {t("yes", { ns: "common" })}
            </MenuItem>
            <MenuItem key={"false"} value={false}>
              {t("no", { ns: "common" })}
            </MenuItem>
          </TextField>
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            id="timeLimitCPP"
            label={t("timeLimit")}
            type="number"
            value={timeLimitCPP}
            onChange={(event) => {
              setTimeLimitCPP(event.target.value);
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">C/CPP: </InputAdornment>
              ),
              endAdornment: <InputAdornment position="end">s</InputAdornment>,
            }}
          />
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            id="timeLimitJAVA"
            label={t("timeLimit")}
            type="number"
            value={timeLimitJAVA}
            onChange={(event) => {
              setTimeLimitJAVA(event.target.value);
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">JAVA: </InputAdornment>
              ),
              endAdornment: <InputAdornment position="end">s</InputAdornment>,
            }}
          />
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            id="timeLimitPYTHON"
            label={t("timeLimit")}
            type="number"
            value={timeLimitPYTHON}
            onChange={(event) => {
              setTimeLimitPYTHON(event.target.value);
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">PYTHON: </InputAdornment>
              ),
              endAdornment: <InputAdornment position="end">s</InputAdornment>,
            }}
          />
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            id="memoryLimit"
            label={t("memoryLimit")}
            type="number"
            value={memoryLimit}
            onChange={(event) => {
              setMemoryLimit(event.target.value);
            }}
            InputProps={{
              endAdornment: <InputAdornment position="end">MB</InputAdornment>,
            }}
          />
        </Grid>

        <Grid item xs={12}>
          <FormControl sx={{ width: "100%" }}>
            <InputLabel id="select-tag-label">Tags</InputLabel>
            <Select
              labelId="select-tag-label"
              id="select-tag"
              multiple
              value={selectedTags}
              onChange={handleSelectTags}
              input={<OutlinedInput label="Tags" />}
              renderValue={(selectedTags) => (
                <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.8 }}>
                  {selectedTags?.map((selectedTag) => (
                    <Chip
                      size="small"
                      label={selectedTag.name}
                      sx={{
                        marginRight: "6px",
                        marginBottom: "6px",
                        border: "1px solid lightgray",
                        fontStyle: "italic",
                      }}
                    />
                  ))}
                </Box>
              )}
            >
              <ListSubheader>
                <Button
                  sx={{marginLeft: "20px"}}
                  startIcon={<AddCircleIcon/>}
                  onClick={() => setOpenModalAddNewTag(true)}
                >
                  {t("common:addNew")}
                </Button>
              </ListSubheader>

              {tags.map((tag) => (
                <MenuItem key={tag.tagId} value={tag}>
                  <Checkbox
                    checked={selectedTags.some(
                      (selectedTag) => selectedTag.tagId === tag.tagId
                    )}
                  />
                  <ListItemText
                    primary={tag.name}
                    secondary={tag?.description}
                  />
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>
      </Grid>

      <Box className={classes.description}>
        <Typography
          variant="h5"
          component="div"
          sx={{ marginTop: "12px", marginBottom: "8px" }}
        >
          {t("problemDescription")}
        </Typography>
        <RichTextEditor
          content={description}
          onContentChange={(text) => setDescription(text)}
        />
        {/*
        <RichTextEditor content={sampleTestCase} onContentChange={text => setSampleTestCase(text)}/>
              */}
        <HustCodeEditor
          title="Sample TestCase"
          language={COMPUTER_LANGUAGES.C}
          sourceCode={sampleTestCase}
          onChangeSourceCode={(code) => {
            setSampleTestCase(code);
          }}
        />

        <HustDropzoneArea
          onChangeAttachment={(files) => handleAttachmentFiles(files)}
        />
      </Box>

      {fetchedImageArray.length !== 0 &&
        fetchedImageArray.map((file) => (
          <FileUploadZone
            file={file}
            removable={true}
            onRemove={() => handleDeleteImageAttachment(file.fileName)}
          />
        ))}
      {/* this function is not implemented yet
              <Box>
                <Typography>
                  <h2>{t("problemSuggestion")}</h2>
                </Typography>
                <RichTextEditor
                  content={solution}
                  onContentChange={text => setSolution(text)}
                />
              </Box>
              */}

      <Box sx={{ marginTop: "32px" }} />
      <HustCodeEditor
        title={t("correctSourceCode")}
        language={languageSolution}
        onChangeLanguage={(event) => {
          setLanguageSolution(event.target.value);
        }}
        sourceCode={codeSolution}
        onChangeSourceCode={(code) => {
          setCodeSolution(code);
        }}
      />

      <LoadingButton
        variant="contained"
        loading={loading}
        onClick={checkCompile}
        sx={{ marginTop: "12px", marginBottom: "6px" }}
      >
        {t("checkSolutionCompile")}
      </LoadingButton>

      <CompileStatus
        showCompile={showCompile}
        statusSuccessful={statusSuccessful}
        message={compileMessage}
      />

      <Box sx={{ marginTop: "12px" }}>
        <FormControlLabel
          label={t("isPreloadCode")}
          control={
            <Checkbox
              checked={isPreloadCode}
              onChange={() => setIsPreloadCode(!isPreloadCode)}
            />
          }
        />
        {isPreloadCode && (
          <HustCodeEditor
            title={t("preloadCode")}
            sourceCode={preloadCode}
            onChangeSourceCode={(code) => {
              setPreloadCode(code);
            }}
            height="280px"
            placeholder="Write the initial code segment that provided to the participants here"
          />
        )}
      </Box>

      <Box sx={{ marginTop: "12px" }}>
        <FormControlLabel
          label={t("isCustomEvaluated")}
          control={
            <Checkbox
              checked={isCustomEvaluated}
              onChange={() => setIsCustomEvaluated(!isCustomEvaluated)}
            />
          }
        />
        <Typography variant="body2" color="gray">
          {t("customEvaluationNote1")}
        </Typography>

        {isCustomEvaluated && (
          <HustCodeEditor
            title={t("checkerSourceCode")}
            language={solutionCheckerLanguage}
            onChangeLanguage={(event) => {
              setSolutionCheckerLanguage(event.target.value);
            }}
            sourceCode={solutionChecker}
            onChangeSourceCode={(code) => {
              setSolutionChecker(code);
            }}
            placeholder={t("checkerSourceCodePlaceholder")}
          />
        )}
      </Box>

      <ListTestCase />

      <Box width="100%" sx={{ marginTop: "16px" }}>
        <LoadingButton
          variant="contained"
          color="success"
          loading={loading}
          onClick={handleSubmit}
        >
          {t("save", { ns: "common" })}
        </LoadingButton>
      </Box>

      <ModelAddNewTag
        isOpen={openModalAddNewTag}
        handleSuccess={() => {
          getAllTags(handleGetTagsSuccess);
        }}
        handleClose={() => setOpenModalAddNewTag(false)}
      />
    </HustContainerCard>
  );
}

const screenName = "SCR_EDIT_PROBLEM";
export default withScreenSecurity(EditProblem, screenName, true);
