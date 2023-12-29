import EditIcon from "@mui/icons-material/Edit";
import { Grid } from "@mui/material";
import TextField from "@mui/material/TextField";
import { styled } from "@mui/material/styles";
import PrimaryButton from "component/button/PrimaryButton";
import { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import { localeOption } from "utils/NumberFormat";
import { request } from "../../../api";
import HustContainerCard from "../../common/HustContainerCard";
import { detail } from "./ContestProblemSubmissionDetailViewedByManager";

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

export function ContestManagerDetail(props) {
  const contestId = props.contestId;
  const [contestName, setContestName] = useState("");
  const [statusId, setStatusId] = useState("");
  const [submissionActionType, setSubmissionActionType] = useState("");
  const [maxNumberSubmission, setMaxNumberSubmission] = useState(10);
  const [participantViewResultMode, setParticipantViewResultMode] =
    useState("");
  const [problemDescriptionViewType, setProblemDescriptionViewType] =
    useState("");
  const [
    evaluateBothPublicPrivateTestcase,
    setEvaluateBothPublicPrivateTestcase,
  ] = useState("");
  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);
  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] =
    useState(0);
  const [participantViewSubmissionMode, setParticipantViewSubmissionMode] =
    useState("");

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
      setEvaluateBothPublicPrivateTestcase(
        res.data.evaluateBothPublicPrivateTestcase
      );
      setMaxSourceCodeLength(res.data.maxSourceCodeLength);
      setParticipantViewSubmissionMode(res.data.participantViewSubmissionMode);
    }).then(() => setLoading(false));
  }

  useEffect(() => {
    getContestDetail();
  }, []);

  const handleEdit = () => {
    history.push("/programming-contest/contest-edit/" + contestId);
  };

  return (
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
      {/* {loading && <LinearProgress />} */}
      <Grid container spacing={2} display={loading ? "none" : ""}>
        {[
          ["Name", contestName],
          ["Status", statusId],
          ["View problem description", problemDescriptionViewType],
          ["Max submissions", `${maxNumberSubmission} (per problem)`],
          [
            "Source length limit",
            `${maxSourceCodeLength.toLocaleString(
              "fr-FR",
              localeOption
            )} (chars)`,
          ],
          [
            "Submission interval",
            `${minTimeBetweenTwoSubmissions.toLocaleString(
              "fr-FR",
              localeOption
            )} (s)`,
            undefined,
            "Minimum time between two consecutive submissions by a participant",
          ],
          ["Action on submission", submissionActionType],
          ["Evaluate private testcases", evaluateBothPublicPrivateTestcase],
          [
            "View testcase detail",
            participantViewResultMode,
            undefined,
            "Allow or disallow participant to view the input and output of each testcase",
          ],
          [
            "Participant view submission",
            participantViewSubmissionMode,
            undefined,
            "Allow or disallow participant to view their own submissions",
          ],
        ].map(([key, value, sx, helpText]) => (
          <Grid item sm={12} md={4}>
            {detail(key, value, sx, helpText)}
          </Grid>
        ))}
      </Grid>
    </HustContainerCard>
  );
}
