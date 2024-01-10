import EditIcon from "@mui/icons-material/Edit";
import { Grid, LinearProgress } from "@mui/material";
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
  });

  const [loading, setLoading] = useState(true);

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
        }));
      });
    };

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
      {loading && <LinearProgress />}
      <Grid container spacing={2} display={loading ? "none" : ""}>
        {[
          ["Name", contestDetail.name],
          ["Status", contestDetail.statusId],
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
            "Evaluate private testcases",
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
    </HustContainerCard>
  );
}
