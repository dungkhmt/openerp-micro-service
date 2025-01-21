import EditIcon from "@mui/icons-material/Edit";
import { Grid, LinearProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,TextField,
  Typography,Button,
 } from "@mui/material";
import { request } from "api";
import PrimaryButton from "component/button/PrimaryButton";
import HustContainerCard from "component/common/HustContainerCard";
import _ from "lodash";
import { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import { localeOption } from "utils/NumberFormat";
import { detail } from "./ContestProblemSubmissionDetailViewedByManager";

export function ContestManagerDetail(props) {
  const contestId = props.contestId;
  const history = useHistory();

  const [contestDetail, setContestDetail] = useState({
    name: "",
    statusId: "",
    submissionActionType: "",
    maxNumberSubmission: 10,
    participantViewResultMode: "",
    problemDescriptionViewType: "",
    evaluateBothPublicPrivateTestcase: "",
    maxSourceCodeLength: 50000,
    minTimeBetweenTwoSubmissions: 0,
    participantViewSubmissionMode: "",
    contestType:""
  });

  const [loading, setLoading] = useState(true);
  const [openCloneDialog, setOpenCloneDialog] = useState(false);
  const [newContestId, setNewContestId] = useState("");
  const [newContestName, setNewContestName] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  useEffect(() => {
    const getContestDetail = () => {
      request("get", "/contests/" + contestId, (res) => {
        setLoading(false);

        const data = res.data;
        setContestDetail((prev) => ({
          ...prev,
          name: data.contestName,
          statusId: data.statusId,
          submissionActionType: data.submissionActionType,
          participantViewResultMode: data.participantViewResultMode,
          maxNumberSubmission: data.maxNumberSubmission,
          problemDescriptionViewType: data.problemDescriptionViewType,
          minTimeBetweenTwoSubmissions: data.minTimeBetweenTwoSubmissions,
          evaluateBothPublicPrivateTestcase:
            data.evaluateBothPublicPrivateTestcase,
          maxSourceCodeLength: data.maxSourceCodeLength,
          participantViewSubmissionMode: data.participantViewSubmissionMode,
          languagesAllowed: data.languagesAllowed,
          contestType: data.contestType
        }));
      });
    };

    getContestDetail();
  }, []);

  const handleEdit = () => {
    history.push("/programming-contest/contest-edit/" + contestId);
  };

  const hasSpecialCharacterContestId = () => {
    return !new RegExp(/^[0-9a-zA-Z_-]*$/).test(newContestId); 
  };



  const handleCloneDialogOpen = () => {
    setOpenCloneDialog(true);
  };

  const handleCloneDialogClose = () => {
    setOpenCloneDialog(false);
    setNewContestId("");
    setNewContestName("");
    setErrorMessage("");
    history.push("/programming-contest/contest-manager/"+contestId); // comment this line : not return to list-problems when click cancel
  };

  const handleClone = () => {
    if (hasSpecialCharacterContestId()) {
        setErrorMessage("Contest ID can only contain letters, numbers, underscores, and hyphens.");
        return;
    }


    const cloneRequest = {
        fromContestId: contestId,
        toContestId: newContestId,
        toContestName: newContestName,
    };

    request(
        "post", 
        "/clone-contest",
        (res) => { 
            handleCloneDialogClose();
            history.push("/programming-contest/contest-manager/"+newContestId);
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
    <>
    <HustContainerCard
      title={contestId}
      action={
        <PrimaryButton
          color="info"
          onClick={handleEdit}
          startIcon={<EditIcon />}
        >
          Edit
        </PrimaryButton>        
      }      
    >

      {loading && <LinearProgress />}
      <Grid container spacing={2} display={loading ? "none" : ""}>
        {[
          ["Name", contestDetail.name],
          ["Status", contestDetail.statusId],
          ["Type", contestDetail.contestType],
          [
            "View problem description",
            contestDetail.problemDescriptionViewType,
          ],
          [
            "Max submissions",
            `${contestDetail.maxNumberSubmission} (per problem)`,
          ],
          [
            "Source length limit",
            `${contestDetail.maxSourceCodeLength.toLocaleString(
              "fr-FR",
              localeOption
            )} (chars)`,
          ],
          [
            "Submission interval",
            `${contestDetail.minTimeBetweenTwoSubmissions.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)`,
            undefined,
            "Minimum time between two consecutive submissions by a participant",
          ],
          [
            "Languages allowed",
            !contestDetail.languagesAllowed ||
            _.isEmpty(contestDetail.languagesAllowed.trim())
              ? "All supported languages"
              : contestDetail.languagesAllowed,
          ],
          ["Action on submission", contestDetail.submissionActionType],
          [
            "Evaluate both public and private testcases",
            contestDetail.evaluateBothPublicPrivateTestcase,
          ],
          [
            "View testcase detail",
            contestDetail.participantViewResultMode,
            undefined,
            "Allow or disallow participant to view the input and output of each testcase",
          ],
          [
            "Participant view submission",
            contestDetail.participantViewSubmissionMode,
            undefined,
            "Allow or disallow participant to view their own submissions",
          ],
        ].map(([key, value, sx, helpText]) => (
          <Grid item xs={12} sm={12} md={4}>
            {detail(key, value, sx, helpText)}
          </Grid>
        ))}
      </Grid>
      <PrimaryButton onClick={handleCloneDialogOpen}>
        Clone
      </PrimaryButton>
    </HustContainerCard>
    <Dialog open={openCloneDialog} onClose={handleCloneDialogClose}>
        <DialogTitle>{"Clone Contest"}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="New Contest ID"
            type="text"
            fullWidth
            variant="outlined"
            value={newContestId}
            onChange={(e) => setNewContestId(e.target.value)}
            error={hasSpecialCharacterContestId()}
            helperText={hasSpecialCharacterContestId() ? "Invalid characters in Problem ID." : ""}
          />
          <TextField
            margin="dense"
            label="New Contest Name"
            type="text"
            fullWidth
            variant="outlined"
            value={newContestName}
            onChange={(e) => setNewContestName(e.target.value)}
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

    </>
  );
}
