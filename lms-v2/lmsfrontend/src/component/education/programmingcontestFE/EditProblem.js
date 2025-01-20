import {makeStyles} from "@material-ui/core/styles";
import {LoadingButton} from "@mui/lab";
import {Box, Checkbox, FormControlLabel, Grid, InputAdornment, Stack, TextField, Typography,} from "@mui/material";
import {request} from "api";
import withScreenSecurity from "component/withScreenSecurity";
import React, {useEffect, useState} from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useTranslation} from "react-i18next";
import {useParams} from "react-router";
import FileUploadZone from "utils/FileUpload/FileUploadZone";
import {randomImageName} from "utils/FileUpload/covert";
import {errorNoti, successNoti} from "utils/notification";
import HustCodeEditor from "../../common/HustCodeEditor";
import HustDropzoneArea from "../../common/HustDropzoneArea";
import RichTextEditor from "../../common/editor/RichTextEditor";
import {CompileStatus} from "./CompileStatus";
import {COMPUTER_LANGUAGES, CUSTOM_EVALUATION, NORMAL_EVALUATION,} from "./Constant";
import ListTestCase from "./ListTestCase";
import ModelAddNewTag from "./ModelAddNewTag";
import {getAllTags} from "./service/TagService";
import ProgrammingContestLayout from "./ProgrammingContestLayout";
import {useHistory} from "react-router-dom";
import StyledSelect from "../../select/StyledSelect";
import {getLevels, getPublicOptions, getStatuses} from "./CreateProblem";
import FilterByTag from "../../table/FilterByTag";
import TertiaryButton from "../../button/TertiaryButton";
import AddIcon from "@mui/icons-material/Add";

const useStyles = makeStyles((theme) => ({
  description: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
  },
}));

function EditProblem() {
  const history = useHistory();
  const {problemId} = useParams();
  const classes = useStyles();
  const {t} = useTranslation([
    "education/programmingcontest/problem",
    "common",
    "validation",
  ]);
  const levels = getLevels(t);
  const publicOptions = getPublicOptions(t)
  const statuses = getStatuses(t)

  const [problemName, setProblemName] = useState("");
  const [description, setDescription] = useState("");
  const [solution, setSolution] = useState("");
  // const [timeLimit, setTimeLimit] = useState('');
  const [timeLimitCPP, setTimeLimitCPP] = useState('');
  const [timeLimitJAVA, setTimeLimitJAVA] = useState('');
  const [timeLimitPYTHON, setTimeLimitPYTHON] = useState('');
  const [memoryLimit, setMemoryLimit] = useState('');
  const [levelId, setLevelId] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [isPreloadCode, setIsPreloadCode] = useState(false);
  const [preloadCode, setPreloadCode] = useState("");
  const [solutionCheckerLanguage, setSolutionCheckerLanguage] = useState(COMPUTER_LANGUAGES.CPP17);
  const [solutionChecker, setSolutionChecker] = useState("");
  const [isCustomEvaluated, setIsCustomEvaluated] = useState(false);
  const [languageSolution, setLanguageSolution] = useState(COMPUTER_LANGUAGES.CPP17);
  const [showCompile, setShowCompile] = useState(false);
  const [statusSuccessful, setStatusSuccessful] = useState(false);
  const [isPublic, setIsPublic] = useState('');
  const [compileMessage, setCompileMessage] = useState("");
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [attachmentFiles, setAttachmentFiles] = useState([]);
  const [fetchedImageArray, setFetchedImageArray] = useState([]);
  const [removedFilesId, setRemovedFileIds] = useState([]);
  const [status, setStatus] = useState('');
  const [isOwner, setIsOwner] = useState(false);
  const [sampleTestCase, setSampleTestCase] = useState(null);

  const [loading, setLoading] = useState(false);

  const [openModalAddNewTag, setOpenModalAddNewTag] = useState(false);

  const handleGetTagsSuccess = (res) => setTags(res.data);

  const handleSelectTags = (tags) => {
    setSelectedTags(tags);
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
          errorNoti(t("common:error"), true);
        }
      },
      body
    );
  }

  const validateSubmit = () => {
    if (problemName === "") {
      errorNoti(
        t("validation:missingField", {fieldName: t("problemName")}),
        3000
      );
      return false;
    }
    if (timeLimitCPP < 1
      || timeLimitJAVA < 1
      || timeLimitPYTHON < 1
      || timeLimitCPP > 300
      || timeLimitJAVA > 300
      || timeLimitPYTHON > 300
    ) {
      errorNoti(
        t("validation:numberBetween", {
          fieldName: t("timeLimit"),
          min: 1,
          max: 300,
        }),
        3000
      );
      return false;
    }
    if (memoryLimit < 3 || memoryLimit > 1024) {
      errorNoti(
        t("validation:numberBetween", {
          fieldName: t("memoryLimit"),
          min: 1,
          max: 1024,
        }),
        3000
      );
      return false;
    }
    if (!statusSuccessful) {
      errorNoti(t("validateSubmit.warningCheckSolutionCompile"), 5000);
      return false;
    }
    return true;
  };

  function handleSubmit() {
    if (!validateSubmit()) return;

    setLoading(true);
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

    const body = {
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
      removedFilesId: removedFilesId,
      scoreEvaluationType: isCustomEvaluated ? CUSTOM_EVALUATION : NORMAL_EVALUATION,
      tagIds: tagIds,
      status: status,
      sampleTestCase: sampleTestCase,
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
      "put",
      "/problems/" + problemId,
      (res) => {
        setLoading(false);
        successNoti(t("common:editSuccess", {name: t("problem")}), 3000);
        history.push("/programming-contest/manager-view-problem-detail/" + problemId);
      },
      {
        onError: (e) => {
          errorNoti(t("common:error"), 3000);
          setLoading(false);
        },
      },
      formData,
      config
    );
  }

  const handleBackToList = () => {
    history.push(`/programming-contest/list-problems`);
  }

  const handleExit = () => {
    history.push(`/programming-contest/manager-view-problem-detail/` + problemId);
  }

  useEffect(() => {
    request(
      "get",
      "teacher/problems/" + problemId,
      (res) => {
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
        setIsPublic(res.publicProblem ? 'Y' : 'N');
        setLanguageSolution(res.correctSolutionLanguage);
        setCodeSolution(res.correctSolutionSourceCode);
        setIsPreloadCode(res.isPreloadCode);
        setPreloadCode(res.preloadCode);
        setSolutionCheckerLanguage(res.solutionCheckerSourceLanguage);
        setSolutionChecker(res.solutionCheckerSourceCode || "");
        setIsCustomEvaluated(res.scoreEvaluationType === CUSTOM_EVALUATION);
        setDescription(res.problemDescription);
        setSelectedTags(res.tags);
        setStatus(res.status);
        setSampleTestCase(res.sampleTestCase);
        setIsOwner(res.roles?.includes("OWNER"));
      },
      {
        onError: (e) => {
          errorNoti(t("common:error"))
        }
      });
  }, [problemId]);

  useEffect(() => {
    getAllTags(handleGetTagsSuccess);
  }, [])

  return (
    <ProgrammingContestLayout title={t("common:edit", {name: t("problem")})} onBack={handleBackToList}>
      <Typography variant="h6">
        {t("generalInfo")}
      </Typography>

      <Grid container spacing={2} mt={0}>
        <Grid item xs={3}>
          <TextField
            fullWidth
            size='small'
            required
            id="problemName"
            label={t("problemName")}
            value={problemName}
            onChange={(event) => {
              setProblemName(event.target.value);
            }}
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
            disabled={!isOwner}
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
              endAdornment: <InputAdornment position="end">s</InputAdornment>,
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
              endAdornment: <InputAdornment position="end">s</InputAdornment>,
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
              endAdornment: <InputAdornment position="end">s</InputAdornment>,
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
            InputProps={{
              endAdornment: <InputAdornment position="end">MB</InputAdornment>,
            }}
          />
        </Grid>

        <Grid item xs={9}>
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

      <Box className={classes.description}>
        <Typography
          variant="h6"
          sx={{marginTop: "8px", marginBottom: "8px"}}
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
          title={t("sampleTestCase")}
          placeholder={null}
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

      <Box sx={{marginTop: "32px"}}/>
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

      <Box sx={{marginTop: "12px"}}>
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

      <ListTestCase/>

      <Stack direction="row" spacing={2} mt={2}>
        <TertiaryButton variant="outlined" onClick={handleExit}>
          {t("common:exit")}
        </TertiaryButton>
        <LoadingButton
          variant="contained"
          loading={loading}
          onClick={handleSubmit}
          sx={{textTransform: 'capitalize'}}
        >
          {t("save", {ns: "common"})}
        </LoadingButton>
      </Stack>

      <ModelAddNewTag
        isOpen={openModalAddNewTag}
        handleSuccess={() => {
          successNoti(t("common:addSuccess", {name: t('tag')}), 3000)
          getAllTags(handleGetTagsSuccess);
        }}
        handleClose={() => setOpenModalAddNewTag(false)}
      />
    </ProgrammingContestLayout>
  );
}

const screenName = "SCR_EDIT_PROBLEM";
export default withScreenSecurity(EditProblem, screenName, true);
