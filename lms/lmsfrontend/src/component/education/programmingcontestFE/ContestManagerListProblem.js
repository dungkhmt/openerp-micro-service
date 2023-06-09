import * as React from "react";
import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {Button, Grid, InputAdornment} from "@mui/material";
import HustContainerCard from "../../common/HustContainerCard";
import {request} from "../../../api";
import EditIcon from "@mui/icons-material/Edit";
import TextField from "@mui/material/TextField";

export function ContestManagerListProblem(props) {
  const contestId = props.contestId;
  const [contestName, setContestName] = useState("");
  const [isPublic, setIsPublic] = useState(false);
  const [statusId, setStatusId] = useState("");
  const [submissionActionType, setSubmissionActionType] = useState("");
  const [maxNumberSubmission, setMaxNumberSubmission] = useState(10);
  const [participantViewResultMode, setParticipantViewResultMode] = useState("");
  const [problemDescriptionViewType, setProblemDescriptionViewType] = useState("");
  const [evaluateBothPublicPrivateTestcase, setEvaluateBothPublicPrivateTestcase,] = useState("");
  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);

  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] = useState(0);
  const history = useHistory();

  function getContestDetail() {
    request("get", "/get-contest-detail/" + contestId, (res) => {
      setContestName(res.data.contestName);
      setIsPublic(res.data.isPublic);
      setStatusId(res.data.statusId);
      setSubmissionActionType(res.data.submissionActionType);
      setParticipantViewResultMode(res.data.participantViewResultMode);
      setMaxNumberSubmission(res.data.maxNumberSubmission);
      setProblemDescriptionViewType(res.data.problemDescriptionViewType);
      setMinTimeBetweenTwoSubmissions(res.data.minTimeBetweenTwoSubmissions);
      setEvaluateBothPublicPrivateTestcase(res.data.evaluateBothPublicPrivateTestcase);
      setMaxSourceCodeLength(res.data.maxSourceCodeLength);
    }).then();
  }

  useEffect(() => {
    getContestDetail();
  }, []);

  function handleEdit() {
    history.push("/programming-contest/contest-edit/" + contestId);
  }

  return (
    <HustContainerCard
      title={"Contest: " + contestId}
      action={
        <Button
          variant="contained"
          color="info"
          onClick={handleEdit}
          startIcon={<EditIcon sx={{marginRight: "4px"}}/>}
        >
          Edit
        </Button>
      }
    >
      <Grid container rowSpacing={3} spacing={2} mb="16px">
        <Grid item xs={9}>
          <TextField
            fullWidth
            autoFocus
            required
            value={contestName}
            id="contestName"
            label="Contest Name"
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            id="statusId"
            label="Status"
            value={statusId}
          >
          </TextField>
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            id="Public"
            label="Public"
            value={isPublic ? "Yes" : "No"}
          >
          </TextField>
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            type="number"
            required
            id="maxNumberSubmission"
            label="Max number of Submissions"
            value={maxNumberSubmission}
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            type="number"
            id="Max Source Code Length"
            label="Source Length Limit"
            value={maxSourceCodeLength}
            InputProps={{endAdornment: <InputAdornment position="end">chars</InputAdornment>}}
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            type="number"
            id="Submission Interval"
            label="Submission Interval"
            value={minTimeBetweenTwoSubmissions}
            InputProps={{endAdornment: <InputAdornment position="end">s</InputAdornment>}}
          />
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            id="evaluateBothPublicPrivateTestcase"
            label="Evaluate Private Testcases"
            value={evaluateBothPublicPrivateTestcase}
          >
          </TextField>
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            id="participantViewResultMode"
            label="Participant View Result Mode"
            value={participantViewResultMode}
          >
          </TextField>
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            id="submissionActionType"
            label="Action on Submission"
            value={submissionActionType}
          >
          </TextField>
        </Grid>

        <Grid item xs={3}>
          <TextField
            fullWidth
            id="problemDescriptionViewType"
            label="Problem Description View Mode"
            value={problemDescriptionViewType}
          >
          </TextField>
        </Grid>
      </Grid>
      {/*<Box sx={{margin: "14px 0"}}>*/}
      {/*  <ContestManagerManageProblem contestId={contestId}/>*/}
      {/*</Box>*/}
    </HustContainerCard>
  );
}
