import {makeStyles} from "@material-ui/core";
import {Box, Checkbox, Chip, FormControlLabel, Grid, InputAdornment, Link, TextField, Typography} from "@mui/material";
import React, {useEffect, useState} from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useHistory} from "react-router-dom";
import {CompileStatus} from "./CompileStatus";
import {request} from "../../../api";
import {useTranslation} from "react-i18next";
import HustDropzoneArea from "../../common/HustDropzoneArea";
import {errorNoti, successNoti} from "../../../utils/notification";
import HustCodeEditor from "../../common/HustCodeEditor";
import {LoadingButton} from "@mui/lab";
import RichTextEditor from "../../common/editor/RichTextEditor";
import {COMPUTER_LANGUAGES, CUSTOM_EVALUATION, NORMAL_EVALUATION} from "./Constant";
import {getAllTags} from "./service/TagService";
import ModelAddNewTag from "./ModelAddNewTag";
import AddIcon from '@mui/icons-material/Add';
import ProgrammingContestLayout from "./ProgrammingContestLayout";
import StyledSelect from "../../select/StyledSelect";
import TertiaryButton from "../../button/TertiaryButton";
import FilterByTag from "../../table/FilterByTag";
import withScreenSecurity from "../../withScreenSecurity";

const useStyles = makeStyles((theme) => ({
  description: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
  }
}));

export const getLevels = (t) => [
  {
    label: t("easy"),
    value: "easy",
  },
  {
    label: t("medium"),
    value: "medium",
  },
  {
    label: t("hard"),
    value: "hard",
  },
];

export const getPublicOptions = (t) => [
  {
    label: t("common:yes"),
    value: "Y",
  },
  {
    label: t("common:no"),
    value: "N",
  },
];

export const getStatuses = (t) => [
  {
    label: t('open'),
    value: "OPEN",
  },
  {
    label: t('hidden'),
    value: "HIDDEN",
  }
];

function CreateProblem() {
  const history = useHistory();
  const classes = useStyles();

  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common", "validation"]
  );
  const levels = getLevels(t);
  const publicOptions = getPublicOptions(t)
  const statuses = getStatuses(t)

  const [problemId, setProblemID] = useState("");
  const [problemName, setProblemName] = useState("");
  // const [timeLimit, setTimeLimit] = useState(1);
  const [timeLimitCPP, setTimeLimitCPP] = useState(1);
  const [timeLimitJAVA, setTimeLimitJAVA] = useState(1);
  const [timeLimitPYTHON, setTimeLimitPYTHON] = useState(1);
  const [memoryLimit, setMemoryLimit] = useState(256);
  const [levelId, setLevelId] = useState("medium");
  const [description, setDescription] = useState("");
  const [solution, setSolution] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [isPreloadCode, setIsPreloadCode] = useState(false);
  const [preloadCode, setPreloadCode] = useState("");
  const [languageSolution, setLanguageSolution] = useState(COMPUTER_LANGUAGES.CPP17);
  const [solutionChecker, setSolutionChecker] = useState("");
  const [solutionCheckerLanguage, setSolutionCheckerLanguage] = useState(COMPUTER_LANGUAGES.CPP17);
  const [isPublic, setIsPublic] = useState('N');
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [status, setStatus] = useState('HIDDEN');
  const [sampleTestCase, setSampleTestCase] = useState(null);

  const [isCustomEvaluated, setIsCustomEvaluated] = useState(false);
  const [compileMessage, setCompileMessage] = useState("");
  const [attachmentFiles, setAttachmentFiles] = useState([]);
  const [showCompile, setShowCompile] = useState(false);
  const [statusSuccessful, setStatusSuccessful] = useState(false);

  const [loading, setLoading] = useState(false);

  const [openModalAddNewTag, setOpenModalAddNewTag] = useState(false);

  const handleGetTagsSuccess = (res) => setTags(res.data);

  const handleSelectTags = (tags) => {
    setSelectedTags(tags);
  };

  const handleAttachmentFiles = (files) => {
    setAttachmentFiles(files);
  };

  const checkCompile = () => {
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
        setLoading(false)

        setShowCompile(true);
        setStatusSuccessful(res.data.status !== "Compilation Error");
        setCompileMessage(res.data)
      },
      {
        onError: (e) => {
          setLoading(false)
          errorNoti(t("common:error"))
        }
      },
      body
    );
  }

  const isValidProblemId = () => {
    return new RegExp(/[%^/\\|.?;[\]]/g).test(problemId);
  };
  const hasSpecialCharacterProblemId = () => {
    return !new RegExp(/^[0-9a-zA-Z_-]*$/).test(problemId);
  };

  const hasSpecialCharacterProblemName = () => {
    return !new RegExp(/^[0-9a-zA-Z ]*$/).test(problemName);
  };

  const validateSubmit = () => {
    if (problemId === "") {
      errorNoti(t("missingField", {ns: "validation", fieldName: t("problemId")}), 3000);
      return false;
    }
    if (hasSpecialCharacterProblemId()) {
      errorNoti("Problem ID must only contain alphanumeric characters, _, or -.", 3000);
      return false;
    }
    if (problemName === "") {
      errorNoti(t("missingField", {ns: "validation", fieldName: t("problemName")}), 3000);
      return false;
    }
    //if (hasSpecialCharacterProblemName()) {
    //  errorNoti("Problem name must only contain alphanumeric characters.", 3000);
    //  return false;
    //}
    if (timeLimitCPP < 1
      || timeLimitJAVA < 1
      || timeLimitPYTHON < 1
      || timeLimitCPP > 300
      || timeLimitJAVA > 300
      || timeLimitPYTHON > 300) {
      errorNoti(t("numberBetween", {ns: "validation", fieldName: t("timeLimit"), min: 1, max: 300}), 3000);
      return false;
    }
    if (memoryLimit < 3 || memoryLimit > 1024) {
      errorNoti(t("numberBetween", {ns: "validation", fieldName: t("memoryLimit"), min: 1, max: 1024}), 3000);
      return false;
    }
    if (!statusSuccessful) {
      errorNoti(t("validateSubmit.warningCheckSolutionCompile"), 5000);
      return false;
    }
    return true;
  }

  function handleSubmit() {
    if (!validateSubmit()) return;

    setLoading(true);
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
      isPublic: isPublic === 'Y',
      fileId: fileId,
      scoreEvaluationType: isCustomEvaluated ? CUSTOM_EVALUATION : NORMAL_EVALUATION,
      tagIds: tagIds,
      status: status,
      sampleTestCase: sampleTestCase
    };

    const formData = new FormData();
    formData.append("dto", new Blob([JSON.stringify(body)], {type: 'application/json'}));

    for (const file of attachmentFiles) {
      formData.append("files", file);
    }

    const config = {
      headers: {
        "content-type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/problems",
      (res) => {
        setLoading(false);
        successNoti(t("common:addSuccess", {name: t("problem")}), 3000);
        history.push("/programming-contest/list-problems");
      },
      {
        onError: () => {
          errorNoti(t("common:error"), 3000);
          setLoading(false);
        },
      },
      formData,
      config
    );
  }

  const handleExit = () => {
    history.push(`/programming-contest/list-problems`);
  }

  useEffect(() => {
    getAllTags(handleGetTagsSuccess);
  }, [])

  return (
    <ProgrammingContestLayout title={t("common:create", {name: t("problem")})} onBack={handleExit}>
      <Typography variant="h6">
        {t("generalInfo")}
      </Typography>

      <Grid container spacing={2} mt={0}>
        <Grid item xs={3}>
          <TextField
            fullWidth
            size='small'
            autoFocus
            required
            id={"problemId"}
            label={t("problemId")}
            value={problemId}
            onChange={(event) => {
              setProblemID(event.target.value);
            }}
            error={hasSpecialCharacterProblemId()}
            helperText={
              hasSpecialCharacterProblemId()
                ? "Problem ID must not contain special characters including %^/\\|.?;[]"
                : ""
            }
            sx={{marginBottom: "12px"}}
          />
        </Grid>
        <Grid item xs={3}>
          <TextField
            fullWidth
            size='small'
            required
            id="problemName"
            label={t("problemName")}
            value={problemName}
            //error={hasSpecialCharacterProblemName()}
            helperText={
              //hasSpecialCharacterProblemName()
              //  ? "Problem ID must not contain special characters including %^/\\|.?;[]"
              //  : ""
              ""
            }
            onChange={(event) => {
              setProblemName(event.target.value);
            }}
          />
        </Grid>

        <Grid item xs={3}>
          <StyledSelect
            fullWidth
            required
            key={t("status")}
            label={t("status")}
            options={statuses}
            value={status}
            sx={{minWidth: 'unset', mr: 'unset'}}
            onChange={(event) => {
              setStatus(event.target.value);
            }}
          />
        </Grid>

        <Grid item xs={3}>
          <StyledSelect
            fullWidth
            required
            key={t("common:public")}
            label={t("common:public")}
            options={publicOptions}
            sx={{minWidth: 'unset', mr: 'unset'}}
            value={isPublic}
            onChange={(event) => {
              setIsPublic(event.target.value);
            }}
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            size='small'
            required
            id="timeLimitCPP"
            label={t("timeLimit") + ' C/CPP'}
            type="number"
            value={timeLimitCPP}
            onChange={(event) => {
              setTimeLimitCPP(event.target.value);
            }}
            InputProps={{
              endAdornment: <InputAdornment position="end">s</InputAdornment>
            }}
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            size='small'
            required
            id="timeLimitJAVA"
            label={t("timeLimit") + ' Java'}
            type="number"
            value={timeLimitJAVA}
            onChange={(event) => {
              setTimeLimitJAVA(event.target.value);
            }}
            InputProps={{
              endAdornment: <InputAdornment position="end">s</InputAdornment>
            }}
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            size='small'
            required
            id="timeLimitPYTHON"
            label={t("timeLimit") + ' Python'}
            type="number"
            value={timeLimitPYTHON}
            onChange={(event) => {
              setTimeLimitPYTHON(event.target.value);
            }}
            InputProps={{
              endAdornment: <InputAdornment position="end">s</InputAdornment>
            }}
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            size='small'
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

        <Grid item xs={3}>
          <StyledSelect
            fullWidth
            required
            key={t("level")}
            label={t("level")}
            options={levels}
            value={levelId}
            sx={{minWidth: 'unset', mr: 'unset'}}
            onChange={(event) => {
              setLevelId(event.target.value);
            }}
          />
        </Grid>

        <Grid item xs={6}>
          <FilterByTag limitTags={3} tags={tags} onSelect={handleSelectTags} value={selectedTags}/>
        </Grid>
        <Grid item xs={3}>
          <TertiaryButton
            startIcon={<AddIcon/>}
            onClick={() => setOpenModalAddNewTag(true)}
          >
            {t("common:add", {name: t('tag')})}
          </TertiaryButton>
        </Grid>
      </Grid>

      <Link sx={{mt: 3, display: 'inline-block'}} href="/programming-contest/suggest-problem" target="_blank"
            underline="hover">
        <Typography variant="body1" color="primary">
          Struggling to create a fresh and exciting challenge? Try our new <b>Problem Suggestion</b> feature
          <Chip label="Beta" color="secondary" variant="outlined" size="small"
                sx={{marginLeft: "8px", marginBottom: "8px", fontWeight: "bold"}}/></Typography>
      </Link>

      <Box className={classes.description}>
        <Typography variant="h6" sx={{marginTop: "8px", marginBottom: "8px"}}>
          {t("problemDescription")}
        </Typography>
        <RichTextEditor content={description} onContentChange={text => setDescription(text)}/>
        {/*  
         <RichTextEditor content={sampleTestCase} onContentChange={text => setSampleTestCase(text)}/>
        */}
        <HustCodeEditor
          title={t("sampleTestCase")}
          placeholder={null}
          sourceCode={sampleTestCase}
          onChangeSourceCode={(code) => {
            setSampleTestCase(code);
          }}
        />
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
        title={t("solutionSourceCode") + " *"}
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
        variant="outlined"
        loading={loading}
        onClick={checkCompile}
        sx={{margin: "12px 0", textTransform: 'none'}}
      >
        {t("checkSolutionCompile")}
      </LoadingButton>
      <CompileStatus
        showCompile={showCompile}
        statusSuccessful={statusSuccessful}
        detail={compileMessage}
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
          loading={loading}
          onClick={handleSubmit}
          sx={{textTransform: 'capitalize'}}
        >
          {t("save", {ns: "common"})}
        </LoadingButton>
      </Box>

      <ModelAddNewTag
        isOpen={openModalAddNewTag}
        handleSuccess={() => {
          successNoti(t("common:addSuccess", {name: t('tag')}), 3000)
          getAllTags(handleGetTagsSuccess)
        }}
        handleClose={() => setOpenModalAddNewTag(false)}
      />
    </ProgrammingContestLayout>
  );
}

const screenName = "SCR_CREATE_PROBLEM";
export default withScreenSecurity(CreateProblem, screenName, true);
