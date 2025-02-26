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
import {request} from "api";
import withScreenSecurity from "component/withScreenSecurity";
import {useEffect, useState} from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useTranslation} from "react-i18next";
import {useHistory, useParams} from "react-router-dom";
import FileUploadZone from "utils/FileUpload/FileUploadZone";
import {randomImageName} from "utils/FileUpload/covert";
import {PROBLEM_ROLE, PROBLEM_STATUS} from "utils/constants";
import RichTextEditor from "../../common/editor/RichTextEditor";
import {COMPUTER_LANGUAGES, CUSTOM_EVALUATION, mapLanguageToCodeBlockLanguage} from "./Constant";
import ContestsUsingAProblem from "./ContestsUsingAProblem";
import ListTestCase from "./ListTestCase";
import {localeOption} from "../../../utils/NumberFormat";
import {detail} from "./ContestProblemSubmissionDetailViewedByManager";
import ProgrammingContestLayout from "./ProgrammingContestLayout";
import PrimaryButton from "../../button/PrimaryButton";
import TertiaryButton from "../../button/TertiaryButton";
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import {getLevels, getStatuses} from "./CreateProblem";
import HustCopyCodeBlock from "../../common/HustCopyCodeBlock";

function ManagerViewProblemDetailV2() {
  const {problemId} = useParams();
  const history = useHistory();

  const {t} = useTranslation([
    "education/programmingcontest/problem",
    "common",
    "validation",
  ]);

  const [fetchedImageArray, setFetchedImageArray] = useState([]);

  const [openCloneDialog, setOpenCloneDialog] = useState(false);
  const [newProblemId, setNewProblemId] = useState("");
  const [newProblemName, setNewProblemName] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(true);

  const [problemDetail, setProblemDetail] = useState({
    problemName: "",
    description: "",
    timeLimitCPP: null,
    timeLimitJAVA: null,
    timeLimitPYTHON: null,
    memoryLimit: null,
    levelId: "",
    correctSolutionLanguage: COMPUTER_LANGUAGES.CPP17,
    correctSolutionSourceCode: "",
    isPreloadCode: false,
    preloadCode: "",
    solutionCheckerSourceLanguage: COMPUTER_LANGUAGES.CPP17,
    solutionCheckerSourceCode: "",
    isCustomEvaluated: false,
    public: false,
    tags: [],
    status: "",
    roles: [],
    sampleTestCase: null
  });

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

      setProblemDetail({
        ...res,
        public: res.publicProblem,
        solutionCheckerSourceCode: res.solutionCheckerSourceCode || "",
        isCustomEvaluated: res.scoreEvaluationType === CUSTOM_EVALUATION,
        description: res.problemDescription,
      })
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
          {(!problemDetail.roles.includes(PROBLEM_ROLE.OWNER) &&
            (!problemDetail.roles.includes(PROBLEM_ROLE.EDITOR) || problemDetail.status !== PROBLEM_STATUS.OPEN)
          ) ? null : (<PrimaryButton
            onClick={() => {
              history.push("/programming-contest/edit-problem/" + problemId);
            }}
            startIcon={<EditIcon/>}
          >
            {t("common:edit", {name: ''})}
          </PrimaryButton>)
          }
          {(!problemDetail.roles.includes(PROBLEM_ROLE.OWNER) &&
            (!problemDetail.roles.includes(PROBLEM_ROLE.EDITOR) ||
              problemDetail.status !== PROBLEM_STATUS.OPEN)) ? null : (<TertiaryButton
            variant="outlined"
            onClick={handleCloneDialogOpen}
            startIcon={<ContentCopyIcon/>}
          >
            {t("clone")}
          </TertiaryButton>)
          }
          {problemDetail.roles.includes(PROBLEM_ROLE.OWNER) && (
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
          [t("problemName"), problemDetail.problemName],
          [t("level"), getLevels(t).find(item => item.value === problemDetail.levelId)?.label],
          [t("status"), getStatuses(t).find(item => item.value === problemDetail.status)?.label],
          [
            t("public", {ns: "common"}),
            problemDetail.public ? t("common:yes") : t("common:no"),
          ],
          [
            t("timeLimit") + ' C/CPP',
            problemDetail.timeLimitCPP ? `${problemDetail.timeLimitCPP.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)` : null,
          ],
          [
            t("timeLimit") + ' Java',
            problemDetail.timeLimitJAVA ? `${problemDetail.timeLimitJAVA.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)` : null,
          ],
          [
            t("timeLimit") + ' Python',
            problemDetail.timeLimitPYTHON ? `${problemDetail.timeLimitPYTHON.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)` : null,
          ],
          [
            t("memoryLimit"),
            problemDetail.memoryLimit ? `${problemDetail.memoryLimit.toLocaleString(
              "fr-FR",
              localeOption
            )} (MB)` : null,
          ],
          [
            t("tag"),
            problemDetail.tags
              ? problemDetail.tags.map((selectedTag) => selectedTag.name).join(", ") : null,
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
          content={problemDetail.description}
          readOnly
        />
      </Box>

      <HustCopyCodeBlock title={t("sampleTestCase")} text={problemDetail.sampleTestCase}/>

      {fetchedImageArray.length !== 0 &&
        fetchedImageArray.map((file) => (
          <FileUploadZone file={file} removable={false}/>
        ))}

      <Box sx={{marginTop: "28px"}}/>
      <HustCopyCodeBlock title={t("solutionSourceCode")}
                         language={mapLanguageToCodeBlockLanguage(problemDetail.correctSolutionLanguage)}
                         text={problemDetail.correctSolutionSourceCode}
                         showLineNumbers/>

      {problemDetail.isPreloadCode && (<Box sx={{marginTop: "12px"}}>
        <HustCopyCodeBlock title={t("preloadCode")}
                           text={problemDetail.preloadCode}
                           showLineNumbers/>
      </Box>)}

      {problemDetail.isCustomEvaluated && (<Box sx={{marginTop: "24px"}}>
        <HustCopyCodeBlock title={t("checkerSourceCode")}
                           language={mapLanguageToCodeBlockLanguage(problemDetail.solutionCheckerSourceLanguage)}
                           text={problemDetail.solutionCheckerSourceCode}
                           showLineNumbers/>
      </Box>)}

      <ListTestCase mode={2}/>

      <ContestsUsingAProblem problemId={problemId}/>
    </ProgrammingContestLayout>
  );
}

const screenName = "SCR_MANAGER_PROBLEM_DETAIL";
export default withScreenSecurity(ManagerViewProblemDetailV2, screenName, true);
