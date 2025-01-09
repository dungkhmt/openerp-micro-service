import EditIcon from "@mui/icons-material/Edit";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  LinearProgress,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import {styled} from "@mui/material/styles";
import {request} from "api";
import withScreenSecurity from "component/withScreenSecurity";
import {useEffect, useState} from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useTranslation} from "react-i18next";
import {useHistory, useParams} from "react-router-dom";
import FileUploadZone from "utils/FileUpload/FileUploadZone";
import {randomImageName} from "utils/FileUpload/covert";
import {PROBLEM_ROLE, PROBLEM_STATUS} from "utils/constants";
import HustCodeEditor from "../../common/HustCodeEditor";
import RichTextEditor from "../../common/editor/RichTextEditor";
import {COMPUTER_LANGUAGES, CUSTOM_EVALUATION} from "./Constant";
import ContestsUsingAProblem from "./ContestsUsingAProblem";
import ListTestCase from "./ListTestCase";
import {localeOption} from "../../../utils/NumberFormat";
import {detail} from "./ContestProblemSubmissionDetailViewedByManager";
import ProgrammingContestLayout from "./ProgrammingContestLayout";
import PrimaryButton from "../../button/PrimaryButton";
import TertiaryButton from "../../button/TertiaryButton";
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

const CssTextField = styled(TextField)({
  ".MuiInputBase-input.Mui-disabled": {
    WebkitTextFillColor: "gray",
    color: "gray",
  },
  "& label.Mui-disabled": {
    color: "gray",
  },
  "& .MuiOutlinedInput-root": {
    "& fieldset": {
      borderColor: "darkgray",
    },
  },
});

function ManagerViewProblemDetailV2() {
  const {t} = useTranslation([
    "education/programmingcontest/problem",
    "common",
    "validation",
  ]);

  const {problemId} = useParams();
  const history = useHistory();

  const [problemName, setProblemName] = useState("");
  const [description, setDescription] = useState("");
  // const [timeLimit, setTimeLimit] = useState(1);
  const [timeLimitCPP, setTimeLimitCPP] = useState(1);
  const [timeLimitJAVA, setTimeLimitJAVA] = useState(1);
  const [timeLimitPYTHON, setTimeLimitPYTHON] = useState(1);
  const [memoryLimit, setMemoryLimit] = useState(1);
  const [levelId, setLevelId] = useState("");
  const [languageSolution, setLanguageSolution] = useState(
    COMPUTER_LANGUAGES.CPP17
  );
  const [codeSolution, setCodeSolution] = useState("");
  const [isPreloadCode, setIsPreloadCode] = useState(false);
  const [preloadCode, setPreloadCode] = useState("");
  const [solutionCheckerLanguage, setSolutionCheckerLanguage] = useState(
    COMPUTER_LANGUAGES.CPP17
  );
  const [solutionChecker, setSolutionChecker] = useState("");
  const [isCustomEvaluated, setIsCustomEvaluated] = useState(false);
  const [isPublic, setIsPublic] = useState(false);
  const [selectedTags, setSelectedTags] = useState([]);
  const [fetchedImageArray, setFetchedImageArray] = useState([]);
  const [status, setStatus] = useState("");
  const [roles, setRoles] = useState([]);
  const [sampleTestCase, setSampleTestCase] = useState(null);

  const [openCloneDialog, setOpenCloneDialog] = useState(false);
  const [newProblemId, setNewProblemId] = useState("");
  const [newProblemName, setNewProblemName] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(true);

  const handleExit = () => {
    history.push(`/programming-contest/list-problems`);
  }

  useEffect(() => {
    request("get", "teacher/problems/" + problemId, (res) => {
      setLoading(false);

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
      setRoles(res.roles);
      setStatus(res.status);
      setSampleTestCase(res.sampleTestCase);
    });
  }, [problemId]);

  const hasSpecialCharacterProblemId = () => {
    return !new RegExp(/^[0-9a-zA-Z_-]*$/).test(newProblemId);
  };

  const hasSpecialCharacterProblemName = () => {
    return !new RegExp(/^[0-9a-zA-Z ]*$/).test(newProblemName);
  };

  const handleCloneDialogOpen = () => {
    setOpenCloneDialog(true);
  };

  const handleCloneDialogClose = () => {
    setOpenCloneDialog(false);
    setNewProblemId("");
    setNewProblemName("");
    setErrorMessage("");
    history.push("/programming-contest/list-problems"); // comment this line : not return to list-problems when click cancel
  };

  const handleClone = () => {
    if (hasSpecialCharacterProblemId()) {
      setErrorMessage("Problem ID can only contain letters, numbers, underscores, and hyphens.");
      return;
    }
    if (hasSpecialCharacterProblemName()) {
      setErrorMessage("Problem Name can only contain letters and numbers.");
      return;
    }

    const cloneRequest = {
      oldProblemId: problemId,
      newProblemId: newProblemId,
      newProblemName: newProblemName,
    };

    request(
      "post",
      "/teachers/problems/clone",
      (res) => {
        handleCloneDialogClose();
        history.push("/programming-contest/list-problems");
      },
      {
        onError: (error) => {
          setErrorMessage("Failed to clone the problem. Please try again.");
          console.error("Error cloning problem:", error);
        },
        400: (error) => {
          setErrorMessage("Invalid request. Please check your input.");
        },
        404: (error) => {
          setErrorMessage("Original problem not found.");
        },
        500: (error) => {
          setErrorMessage("Original problem already exists.");
        },
      },
      cloneRequest
    );
  };

  return (
    <ProgrammingContestLayout title={t("viewProblem")} onBack={handleExit}>
      <Stack direction="row" spacing={2} mb={1.5} justifyContent="space-between">
        <Typography variant="h6" component='span'>
          {t("generalInfo")}
        </Typography>

        <Stack direction="row" spacing={2}>
          {(!roles.includes(PROBLEM_ROLE.OWNER) &&
            (!roles.includes(PROBLEM_ROLE.EDITOR) || status !== PROBLEM_STATUS.OPEN)
          ) ? null : (<PrimaryButton
            onClick={() => {
              history.push("/programming-contest/edit-problem/" + problemId);
            }}
            startIcon={<EditIcon/>}
          >
            {t("edit", {ns: "common"})}
          </PrimaryButton>)
          }
          {(!roles.includes(PROBLEM_ROLE.OWNER) &&
            (!roles.includes(PROBLEM_ROLE.EDITOR) ||
              status !== PROBLEM_STATUS.OPEN)) ? null : (<TertiaryButton
            variant="outlined"
            onClick={handleCloneDialogOpen}
            startIcon={<ContentCopyIcon/>}
          >
            {t("clone")}
          </TertiaryButton>)
          }
          {roles.includes(PROBLEM_ROLE.OWNER) && (
            <TertiaryButton
              variant="outlined"
              onClick={() => {
                history.push(
                  "/programming-contest/user-contest-problem-role-management/" +
                  problemId
                );
              }}
            >
              {t("manageRole")}
            </TertiaryButton>
          )}</Stack>
      </Stack>

      <Dialog open={openCloneDialog} onClose={handleCloneDialogClose}>
        <DialogTitle>{"Clone Problem"}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="New Problem ID"
            type="text"
            fullWidth
            variant="outlined"
            value={newProblemId}
            onChange={(e) => setNewProblemId(e.target.value)}
            error={hasSpecialCharacterProblemId()}
            helperText={hasSpecialCharacterProblemId() ? "Invalid characters in Problem ID." : ""}
          />
          <TextField
            margin="dense"
            label="New Problem Name"
            type="text"
            fullWidth
            variant="outlined"
            value={newProblemName}
            onChange={(e) => setNewProblemName(e.target.value)}
            //error={hasSpecialCharacterProblemName()}
            //helperText={hasSpecialCharacterProblemName() ? "Invalid characters in Problem Name." : ""}
            helperText={""}
          />
          {errorMessage && <Typography color="error">{errorMessage}</Typography>}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloneDialogClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleClone} color="primary">
            Create
          </Button>
        </DialogActions>
      </Dialog>
      {loading && <LinearProgress/>}
      <Grid container spacing={2} display={loading ? "none" : ""}>
        {[
          [t("problemName"), problemName],
          [t("status"), status],
          [t("level"), levelId],
          [
            t("public", {ns: "common"}),
            isPublic ? t("yes", {ns: "common"}) : t("no", {ns: "common"}),
          ],
          [
            t("timeLimit") + ' C/CPP',
            `${timeLimitCPP.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)`,
          ],
          [
            t("timeLimit") + ' Java',
            `${timeLimitJAVA.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)`,
          ],
          [
            t("timeLimit") + ' Python',
            `${timeLimitPYTHON.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)`,
          ],
          [
            t("memoryLimit"),
            `${memoryLimit.toLocaleString(
              "fr-FR",
              localeOption
            )} (MB)`,
          ],
          [
            "Tags",
            selectedTags
              ? selectedTags.map((selectedTag) => selectedTag.name).join(", ") : null,
          ],
        ].map(([key, value, sx, helpText]) => (
          <Grid item xs={12} sm={12} md={3}>
            {detail(key, value, sx, helpText)}
          </Grid>
        ))}
      </Grid>

      <Box sx={{marginTop: "24px", marginBottom: "24px"}}>
        <Typography variant="h6" sx={{marginBottom: "8px"}}>
          {t("problemDescription")}
        </Typography>
        <RichTextEditor
          toolbarHidden
          content={description}
          onContentChange={(text) => setDescription(text)}
        />
        <HustCodeEditor
          title="Sample TestCase"
          // language={COMPUTER_LANGUAGES.C}
          sourceCode={sampleTestCase}
        />
      </Box>

      {fetchedImageArray.length !== 0 &&
        fetchedImageArray.map((file) => (
          <FileUploadZone file={file} removable={false}/>
        ))}

      <Box sx={{marginTop: "28px"}}/>
      <HustCodeEditor
        title={t("correctSourceCode")}
        // language={languageSolution}
        sourceCode={codeSolution}
      />

      {isPreloadCode && (<Box sx={{marginTop: "12px"}}>
        <HustCodeEditor
          title={t("preloadCode")}
          sourceCode={preloadCode}
          height="280px"/>
      </Box>)}

      {isCustomEvaluated && (<Box sx={{marginTop: "24px"}}>
        <HustCodeEditor
          title={t("checkerSourceCode")}
          language={solutionCheckerLanguage}
          sourceCode={solutionChecker}
          placeholder={t("checkerSourceCodePlaceholder")}
        />
      </Box>)}

      <ListTestCase mode={2}/>

      <ContestsUsingAProblem problemId={problemId}/>
    </ProgrammingContestLayout>
  );
}

const screenName = "SCR_MANAGER_PROBLEM_DETAIL";
export default withScreenSecurity(ManagerViewProblemDetailV2, screenName, true);
