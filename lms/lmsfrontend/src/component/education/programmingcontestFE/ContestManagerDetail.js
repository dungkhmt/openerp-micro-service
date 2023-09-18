import * as React from "react";
import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {Button, Grid, InputAdornment, LinearProgress} from "@mui/material";
import HustContainerCard from "../../common/HustContainerCard";
import {request} from "../../../api";
import EditIcon from "@mui/icons-material/Edit";
import TextField from "@mui/material/TextField";
import {styled} from "@mui/material/styles";

const CssTextField = styled(TextField)({
  ".MuiInputBase-input.Mui-disabled": {
    WebkitTextFillColor: "gray",
    color: "gray"
  },
  '& label.Mui-disabled': {
    color: 'gray',
  },
  '& .MuiOutlinedInput-root': {
    '& fieldset': {
      borderColor: 'darkgray',
    },
  },
});

export function ContestManagerDetail(props) {
  const contestId = props.contestId;
  const [contestName, setContestName] = useState("");
  const [statusId, setStatusId] = useState("");
  const [submissionActionType, setSubmissionActionType] = useState("");
  const [maxNumberSubmission, setMaxNumberSubmission] = useState(10);
  const [participantViewResultMode, setParticipantViewResultMode] = useState("");
  const [problemDescriptionViewType, setProblemDescriptionViewType] = useState("");
  const [evaluateBothPublicPrivateTestcase, setEvaluateBothPublicPrivateTestcase,] = useState("");
  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);
  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] = useState(0);

  const [loading, setLoading] = useState(true);

  const history = useHistory();

  function getContestDetail() {
    request("get", "/contests/" + contestId, (res) => {
      setContestName(res.data.contestName);
      setStatusId(res.data.statusId);
      setSubmissionActionType(res.data.submissionActionType);
      setParticipantViewResultMode(res.data.participantViewResultMode);
      setMaxNumberSubmission(res.data.maxNumberSubmission);
      setProblemDescriptionViewType(res.data.problemDescriptionViewType);
      setMinTimeBetweenTwoSubmissions(res.data.minTimeBetweenTwoSubmissions);
      setEvaluateBothPublicPrivateTestcase(res.data.evaluateBothPublicPrivateTestcase);
      setMaxSourceCodeLength(res.data.maxSourceCodeLength);
    }).then(() => setLoading(false));
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
      {loading && <LinearProgress/>}
      <Grid container rowSpacing={3} spacing={2} mb="16px" display={loading ? "none" : ""}>
        <Grid item xs={9}>
          <CssTextField
            disabled
            fullWidth
            value={contestName}
            id="contestName"
            label="Contest Name"
          />
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            id="statusId"
            label="Status"
            value={statusId}
          >
          </CssTextField>
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            type="number"
            id="maxNumberSubmission"
            label="Max number of Submissions"
            value={maxNumberSubmission}
            InputProps={{endAdornment: <InputAdornment position="end">per problem</InputAdornment>}}
          />
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            type="number"
            id="Max Source Code Length"
            label="Source Length Limit"
            value={maxSourceCodeLength}
            InputProps={{endAdornment: <InputAdornment position="end">chars</InputAdornment>}}
          />
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            type="number"
            id="Submission Interval"
            label="Submission Interval"
            value={minTimeBetweenTwoSubmissions}
            InputProps={{endAdornment: <InputAdornment position="end">s</InputAdornment>}}
          />
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            id="evaluateBothPublicPrivateTestcase"
            label="Evaluate Private Testcases"
            value={evaluateBothPublicPrivateTestcase}
          >
          </CssTextField>
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            id="participantViewResultMode"
            label="Allow viewing Testcase Detail"
            value={participantViewResultMode}
          >
          </CssTextField>
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            id="submissionActionType"
            label="Action on Submission"
            value={submissionActionType}
          >
          </CssTextField>
        </Grid>

        <Grid item xs={3}>
          <CssTextField
            disabled
            fullWidth
            id="problemDescriptionViewType"
            label="Problem Description View Mode"
            value={problemDescriptionViewType}
          >
          </CssTextField>
        </Grid>
      </Grid>
    </HustContainerCard>
  );
}
