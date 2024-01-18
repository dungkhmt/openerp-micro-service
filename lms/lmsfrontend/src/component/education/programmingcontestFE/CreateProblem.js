import {makeStyles} from "@material-ui/core";
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
  Link,
  ListItemText,
  MenuItem,
  OutlinedInput,
  Select,
  TextField,
  Typography
} from "@mui/material";
import React, {useEffect, useState} from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useHistory} from "react-router-dom";
import {CompileStatus} from "./CompileStatus";
import {sleep} from "./lib";
import {request} from "../../../api";
import {useTranslation} from "react-i18next";
import HustDropzoneArea from "../../common/HustDropzoneArea";
import {errorNoti, successNoti, warningNoti} from "../../../utils/notification";
import HustCodeEditor from "../../common/HustCodeEditor";
import {LoadingButton} from "@mui/lab";
import RichTextEditor from "../../common/editor/RichTextEditor";
import HustContainerCard from "../../common/HustContainerCard";
import {COMPUTER_LANGUAGES, CUSTOM_EVALUATION, NORMAL_EVALUATION} from "./Constant";
import {getAllTags} from "./service/TagService";
import ModelAddNewTag from "./ModelAddNewTag";
import AddCircleIcon from '@mui/icons-material/AddCircle';

const useStyles = makeStyles((theme) => ({
  description: {
    marginTop: theme.spacing(2),
    marginBottom: theme.spacing(2),
  }
}));

function CreateProblem() {
  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common", "validation"]
  );
  const classes = useStyles();
  const history = useHistory();

  const [problemId, setProblemID] = useState("");
  const [problemName, setProblemName] = useState("");
  // const [timeLimit, setTimeLimit] = useState(1);
  const [timeLimitCPP, setTimeLimitCPP] = useState(1);
  const [timeLimitJAVA, setTimeLimitJAVA] = useState(1);
  const [timeLimitPYTHON, setTimeLimitPYTHON] = useState(1);
  const [memoryLimit, setMemoryLimit] = useState(256);
  const [levelId, setLevelId] = useState("medium");
  const defaultLevel = ["easy", "medium", "hard"];
  const [description, setDescription] = useState("");
  const [solution, setSolution] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [isPreloadCode, setIsPreloadCode] = useState(false);
  const [preloadCode, setPreloadCode] = useState("");
  const [languageSolution, setLanguageSolution] = useState(COMPUTER_LANGUAGES.CPP17);
  const [solutionChecker, setSolutionChecker] = useState("");
  const [solutionCheckerLanguage, setSolutionCheckerLanguage] = useState(COMPUTER_LANGUAGES.CPP17);
  const [isPublic, setIsPublic] = useState(false);
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [sampleTestCase, setSampleTestCase] = useState(null);

  const [isCustomEvaluated, setIsCustomEvaluated] = useState(false);
  const [compileMessage, setCompileMessage] = useState("");
  const [attachmentFiles, setAttachmentFiles] = useState([]);
  const [showCompile, setShowCompile] = useState(false);
  const [statusSuccessful, setStatusSuccessful] = useState(false);

  const [loading, setLoading] = useState(false);

  const [openModalAddNewTag, setOpenModalAddNewTag] = useState(false);

  const handleGetTagsSuccess = (res) => setTags(res.data);
  useEffect(() => {
    getAllTags(handleGetTagsSuccess);
  }, [])

  const handleSelectTags = (event) => {
    const selectingTags = event.target.value;

    const filteredTags = [...new Map(selectingTags.map(tag => [tag.tagId, tag])).values()]

    setSelectedTags(filteredTags);
  };

  const handleAttachmentFiles = (files) => {
    setAttachmentFiles(files);
  };

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

  const isValidProblemId = () => {
    return new RegExp(/[%^/\\|.?;[\]]/g).test(problemId);
  };

  const validateSubmit = () => {
    if (problemId === "") {
      errorNoti(t("missingField", {ns: "validation", fieldName: t("problemId")}), 3000);
      return false;
    }
    if (problemName === "") {
      errorNoti(t("missingField", {ns: "validation", fieldName: t("problemName")}), 3000);
      return false;
    }
    if (timeLimitCPP <= 0 || timeLimitJAVA <= 0 || timeLimitPYTHON <=0 || timeLimitCPP > 300 || timeLimitJAVA > 300 || timeLimitPYTHON > 300) {
      errorNoti(t("numberBetween", {ns: "validation", fieldName: t("timeLimit"), min: 1, max: 300}), 3000);
      return false;
    }
    if (memoryLimit <= 0 || memoryLimit > 1024) {
      errorNoti(t("numberBetween", {ns: "validation", fieldName: t("memoryLimit"), min: 1, max: 1024}), 3000);
      return false;
    }
    if (!statusSuccessful) {
      warningNoti(t("validateSubmit.warningCheckSolutionCompile"), 5000);
      return false;
    }
    return true;
  }

  function handleSubmit() {
    if (!validateSubmit()) return;

    const fileId = attachmentFiles.map((file) => file.name);
    const tagIds = selectedTags.map((tag) => tag.tagId);

    let body = {
      problemId: problemId,
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
      solutionCheckerLanguage: solutionCheckerLanguage,
      isPublic: isPublic,
      fileId: fileId,
      scoreEvaluationType: isCustomEvaluated ? CUSTOM_EVALUATION : NORMAL_EVALUATION,
      tagIds: tagIds,
      sampleTestCase: sampleTestCase
    };

    let formData = new FormData();
    formData.append("ModelCreateContestProblem", JSON.stringify(body));

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
      "post",
      "/problems",
      (res) => {
        setLoading(false);
        successNoti("Problem saved successfully", 1000);
        sleep(1000).then(() => {
          history.push("/programming-contest/list-problems");
        });
      },
      {
        onError: () => {
          errorNoti(t("error", {ns: "common"}), 3000);
          setLoading(false);
        },
      },
      formData,
      config
    );

  }

  return (
    <HustContainerCard title={t("createProblem")}>
      <Grid container spacing={2}>
        <Grid item xs={4}>
          <TextField
            fullWidth
            autoFocus={true}
            required
            id={"problemId"}
            label={t("problemId")}
            placeholder="Problem ID"
            value={problemId}
            onChange={(event) => {
              setProblemID(event.target.value);
            }}
            error={isValidProblemId()}
            helperText={
              isValidProblemId()
                ? "Problem ID must not contain special characters including %^/\\|.?;[]"
                : ""
            }
            sx={{marginBottom: "12px"}}
          />
        </Grid>
        <Grid item xs={8}>
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
            label={t("public", {ns: "common"})}
            onChange={(event) => {
              setIsPublic(event.target.value);
            }}
            value={isPublic}
          >
            <MenuItem key={"true"} value={true}>
              {t("yes", {ns: "common"})}
            </MenuItem>
            <MenuItem key={"false"} value={false}>
              {t("no", {ns: "common"})}
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
              startAdornment: <InputAdornment position="start">C/CPP: </InputAdornment>,
              endAdornment: <InputAdornment position="end">s</InputAdornment>
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
              startAdornment: <InputAdornment position="start">JAVA: </InputAdornment>,
              endAdornment: <InputAdornment position="end">s</InputAdornment>
            }}
          />
        </Grid>

        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            id="timeLimitPYTHON"
            label={t("timeLimit") + " - PYTHON"}
            type="number"
            value={timeLimitPYTHON}
            onChange={(event) => {
              setTimeLimitPYTHON(event.target.value);
            }}
            InputProps={{
              startAdornment: <InputAdornment position="start">PYTHON: </InputAdornment>,
              endAdornment: <InputAdornment position="end">s</InputAdornment>
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
            InputProps={{endAdornment: <InputAdornment position="end">MB</InputAdornment>,}}
          />
        </Grid>

        <Grid item xs={12}>
          <FormControl sx={{width: "100%"}}>
            <InputLabel id="select-tag-label">Tags</InputLabel>
            <Select
              labelId="select-tag-label"
              id="select-tag"
              multiple
              value={selectedTags}
              onChange={handleSelectTags}
              input={<OutlinedInput label="Tags"/>}
              renderValue={(selectedTags) => (
                <Box sx={{display: 'flex', flexWrap: 'wrap', gap: 0.8}}>
                  {selectedTags?.map((selectedTag) => (
                    <Chip size="small" label={selectedTag.name} sx={{
                      marginRight: "6px",
                      marginBottom: "6px",
                      border: "1px solid lightgray",
                      fontStyle: "italic"
                    }}/>
                  ))}
                </Box>
              )}
            >
              <Button
                sx={{marginLeft: "20px"}}
                startIcon={<AddCircleIcon/>}
                onClick={() => setOpenModalAddNewTag(true)}
              >
                {t("common:addNew")}
              </Button>
              <ModelAddNewTag
                isOpen={openModalAddNewTag}
                handleSuccess={() => {
                  getAllTags(handleGetTagsSuccess)
                }}
                handleClose={() => setOpenModalAddNewTag(false)}
              />
              {tags.map((tag) => (
                <MenuItem key={tag.tagId} value={tag}>
                  <Checkbox checked={selectedTags.indexOf(tag) > -1}/>
                  <ListItemText primary={tag.name} secondary={tag?.description}/>
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>

      </Grid>

      <Box sx={{marginTop: "24px"}}>
        <Link href="/programming-contest/suggest-problem" target="_blank" underline="hover">
          <Typography variant="body1" color="primary">
            Struggling to create a fresh and exciting challenge? Try our new <b>Problem Suggestion</b> feature
            <Chip label="Beta" color="secondary" variant="outlined" size="small"
                  sx={{marginLeft: "8px", marginBottom: "8px", fontWeight: "bold"}}/></Typography>
        </Link>
      </Box>


      <Box className={classes.description}>
        <Typography variant="h5" component="div" sx={{marginTop: "8px", marginBottom: "8px"}}>
          {t("problemDescription")}
        </Typography>
        <RichTextEditor content={description} onContentChange={text => setDescription(text)}/>
        <RichTextEditor content={sampleTestCase} onContentChange={text => setSampleTestCase(text)}/>
        <HustDropzoneArea onChangeAttachment={(files) => handleAttachmentFiles(files)}/>
      </Box>
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
        sx={{marginTop: "12px", marginBottom: "6px"}}
      >
        {t("checkSolutionCompile")}
      </LoadingButton>
      <CompileStatus
        showCompile={showCompile}
        statusSuccessful={statusSuccessful}
        message={compileMessage}
      />

      <Box sx={{marginTop: "12px"}}>
        <FormControlLabel
          label={t("isPreloadCode")}
          control={
            <Checkbox
              checked={isPreloadCode}
              onChange={() => setIsPreloadCode(!isPreloadCode)}
            />}
        />
        {isPreloadCode &&
          <HustCodeEditor
            title={t("preloadCode")}
            sourceCode={preloadCode}
            onChangeSourceCode={(code) => {
              setPreloadCode(code);
            }}
            height="280px"
            placeholder="Write the initial code segment that provided to the participants here"
          />
        }
      </Box>

      <Box sx={{marginTop: "12px"}}>
        <FormControlLabel
          label={t("isCustomEvaluated")}
          control={
            <Checkbox
              checked={isCustomEvaluated}
              onChange={() => setIsCustomEvaluated(!isCustomEvaluated)}
            />}
        />
        <Typography variant="body2" color="gray">{t("customEvaluationNote1")}</Typography>

        {isCustomEvaluated &&
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
        }
      </Box>

      <Box width="100%" sx={{marginTop: "20px"}}>
        <LoadingButton
          variant="contained"
          color="success"
          loading={loading}
          onClick={handleSubmit}
        >
          {t("save", {ns: "common"})}
        </LoadingButton>
      </Box>
    </HustContainerCard>
  );
}

export default CreateProblem;
