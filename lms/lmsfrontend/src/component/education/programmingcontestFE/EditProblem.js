import {
  Box,
  Button,
  Checkbox,
  Chip,
  FormControl,
  FormControlLabel,
  InputAdornment,
  InputLabel,
  Link as MuiLink,
  ListItemText,
  MenuItem,
  OutlinedInput,
  Select,
  TextField,
  Typography
} from "@mui/material";
import {makeStyles} from "@material-ui/core/styles";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {authGet, authPostMultiPart, request} from "../../../api";
import {CompileStatus} from "./CompileStatus";
import {useParams} from "react-router";
import {randomImageName,} from "../../../utils/FileUpload/covert";
import {useTranslation} from "react-i18next";
import HustContainerCard from "../../common/HustContainerCard";
import HustDropzoneArea from "../../common/HustDropzoneArea";
import RichTextEditor from "../../common/editor/RichTextEditor";
import HustCodeEditor from "../../common/HustCodeEditor";
import {LoadingButton} from "@mui/lab";
import {errorNoti, successNoti, warningNoti} from "../../../utils/notification";
import {CUSTOM_EVALUATION, NORMAL_EVALUATION} from "./Constant";
import ListTestCase from "./ListTestCase";
import {getAllTags} from "./service/TagService";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import ModelAddNewTag from "./ModelAddNewTag";
import FileUploadZone from "../../../utils/FileUpload/FileUploadZone";

const useStyles = makeStyles((theme) => ({
  main: {
    display: "flex",
    justifyContent: "space-between",
    flexWrap: "wrap",
    "& .MuiTextField-root": {
      marginBottom: theme.spacing(2),
      minWidth: 80,
    },
  },
}));

function EditProblem() {
  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common", "validation"]
  );

  const classes = useStyles();

  const {problemId} = useParams();
  const token = useSelector((state) => state.auth.token);
  const dispatch = useDispatch();

  const [problemName, setProblemName] = useState("");
  const [description, setDescription] = useState("");
  const [solution, setSolution] = useState("");
  const [timeLimit, setTimeLimit] = useState(1);
  const [memoryLimit, setMemoryLimit] = useState(1);
  const [levelId, setLevelId] = useState("");
  const [codeSolution, setCodeSolution] = useState("");
  const [solutionCheckerLanguage, setSolutionCheckerLanguage] = useState("CPP");
  const [solutionChecker, setSolutionChecker] = useState("");
  const [isCustomEvaluated, setIsCustomEvaluated] = useState(false);
  const [languageSolution, setLanguageSolution] = useState("CPP");
  const [showCompile, setShowCompile] = useState(false);
  const [statusSuccessful, setStatusSuccessful] = useState(false);
  const [isPublic, setIsPublic] = useState(false);
  const [compileMessage, setCompileMessage] = useState("");
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [attachmentFiles, setAttachmentFiles] = useState([]);
  const [fetchedImageArray, setFetchedImageArray] = useState([]);
  const [removedFilesId, setRemovedFileIds] = useState([]);

  const defaultLevel = ["easy", "medium", "hard"];

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

  const handleDeleteImageAttachment = async (fileId) => {
    setFetchedImageArray(
      fetchedImageArray.filter((file) => file.fileName !== fileId)
    );
    setRemovedFileIds([...removedFilesId, fileId]);
  };

  useEffect(() => {
    authGet(dispatch, token, "/problem-details/" + problemId)
      .then((res) => {
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
        setTimeLimit(res.timeLimit);
        setMemoryLimit(res.memoryLimit);
        setIsPublic(res.publicProblem);
        setLanguageSolution(res.correctSolutionLanguage);
        setCodeSolution(res.correctSolutionSourceCode);
        setSolutionCheckerLanguage(res.solutionCheckerLanguage);
        setSolutionChecker(res.solutionCheckerSourceCode || "");
        setIsCustomEvaluated(res.scoreEvaluationType === CUSTOM_EVALUATION)
        setDescription(res.problemDescription);
        setSelectedTags(res.tags);
      }, {})
      .then();

  }, [problemId]);

  function checkCompile() {
    let body = {
      source: codeSolution,
      computerLanguage: languageSolution,
    };

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
      errorNoti(t("missingField", {ns: "validation", fieldName: t("problemName")}), 3000);
      return false;
    }
    if (timeLimit <= 0 || timeLimit > 60) {
      errorNoti(t("numberBetween", {ns: "validation", fieldName: t("timeLimit"), min: 1, max: 60}), 3000);
      return false;
    }
    if (memoryLimit <= 0 || timeLimit > 1024) {
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
      timeLimit: timeLimit,
      levelId: levelId,
      memoryLimit: memoryLimit,
      correctSolutionLanguage: languageSolution,
      solution: solution,
      correctSolutionSourceCode: codeSolution,
      solutionChecker: solutionChecker,
      isPublic: isPublic,
      fileId: fileId,
      removedFilesId: removedFilesId,
      scoreEvaluationType: isCustomEvaluated ? CUSTOM_EVALUATION : NORMAL_EVALUATION,
      tagIds: tagIds,
    };

    let formData = new FormData();
    formData.append("ModelUpdateContestProblem", JSON.stringify(body));
    for (const file of attachmentFiles) {
      formData.append("files", file);
    }

    setLoading(true);
    authPostMultiPart(
      dispatch,
      token,
      "/update-problem-detail/" + problemId,
      formData
    ).then(
      (res) => {
        successNoti("Problem saved successfully", 10000);
      })
      .catch(() => errorNoti(t("error", {ns: "common"}), 3000))
      .finally(() => setLoading(false));
  }

  return (
    <HustContainerCard title={t("editProblem")}>
      <Box className={classes.main}>
        <TextField
          required
          id="problemName"
          label={t("problemName")}
          placeholder="Problem Name"
          value={problemName}
          onChange={(event) => {
            setProblemName(event.target.value);
          }}
          sx={{width: "30%"}}
        />
        <TextField
          select
          id="isPublicProblem"
          label={t("public", {ns: "common"})}
          onChange={(event) => {
            setIsPublic(event.target.value);
          }}
          value={isPublic}
          sx={{width: "15%"}}
        >
          <MenuItem key={"true"} value={true}>
            {t("yes", {ns: "common"})}
          </MenuItem>
          <MenuItem key={"false"} value={false}>
            {t("no", {ns: "common"})}
          </MenuItem>
        </TextField>

        <TextField
          required
          select
          id="levelId"
          label={t("level")}
          value={levelId}
          onChange={(event) => {
            setLevelId(event.target.value);
          }}
          sx={{width: "15%"}}
        >
          {defaultLevel.map((item) => (
            <MenuItem key={item} value={item}>
              {item}
            </MenuItem>
          ))}
        </TextField>

        <TextField
          required
          id="timeLimit"
          label={t("timeLimit")}
          placeholder="Time Limit"
          type="number"
          value={timeLimit}
          onChange={(event) => {
            setTimeLimit(event.target.value);
          }}
          InputProps={{endAdornment: <InputAdornment position="end">s</InputAdornment>,}}
          sx={{width: "15%"}}
        />

        <TextField
          required
          id="memoryLimit"
          label={t("memoryLimit")}
          type="number"
          value={memoryLimit}
          onChange={(event) => {
            setMemoryLimit(event.target.value);
          }}
          InputProps={{endAdornment: <InputAdornment position="end">MB</InputAdornment>,}}
          sx={{width: "15%"}}
        />

        <FormControl sx={{m: 1, width: "90%"}}>
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
                <Checkbox checked={selectedTags.some(selectedTag => selectedTag.tagId === tag.tagId)}/>
                <ListItemText primary={tag.name} secondary={tag?.description}/>
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      <Box className={classes.description}>
        <Typography variant="h5" component="div" sx={{marginTop: "12px", marginBottom: "8px"}}>
          {t("problemDescription")}
        </Typography>
        <RichTextEditor content={description} onContentChange={text => setDescription(text)}/>
        <HustDropzoneArea onChangeAttachment={(files) => handleAttachmentFiles(files)}/>
      </Box>

      {fetchedImageArray.length !== 0 &&
        fetchedImageArray.map((file) => (
          <FileUploadZone file={file} removable={true} onRemove={() => handleDeleteImageAttachment(file.fileName)}/>
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

      <FormControlLabel
        label={t("isCustomEvaluated")}
        control={
          <Checkbox
            checked={isCustomEvaluated}
            onChange={() => setIsCustomEvaluated(!isCustomEvaluated)}
          />}
      />
      <Typography variant="body2" color="gray">{t("customEvaluationNote1")}</Typography>
      <MuiLink href="#" underline="hover">
        <Typography variant="body2" color="gray">{t("customEvaluationNote2")}</Typography>
      </MuiLink>

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

      <ListTestCase/>

      <Box width="100%" sx={{marginTop: "16px"}}>
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

export default EditProblem;
