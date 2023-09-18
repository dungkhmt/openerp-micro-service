import React, {useEffect, useState} from "react";
import TextField from "@mui/material/TextField";
import DateFnsUtils from "@date-io/date-fns";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {Grid, InputAdornment, LinearProgress, MenuItem} from "@mui/material";
import {useParams} from "react-router-dom";

import DateTimePicker from "@mui/lab/DateTimePicker";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import {errorNoti, successNoti} from "../../../utils/notification";
import {useTranslation} from "react-i18next";
import {request} from "../../../api";
import {LoadingButton} from "@mui/lab";
import HustContainerCard from "../../common/HustContainerCard";
import Box from "@mui/material/Box";

export default function EditContest() {
  const {t} = useTranslation(
    ["education/programmingcontest/contest", "common", "validation"]
  );

  const {contestId} = useParams();

  const [loading, setLoading] = useState(true);

  const [contestName, setContestName] = useState("");
  const [contestTime, setContestTime] = useState(Number(0));

  const [startDate, setStartDate] = React.useState(new Date());
  const [countDown, setCountDown] = useState(Number(0));
  const [statusId, setStatusId] = useState("");
  const [listStatusIds, setListStatusIds] = useState([]);
  const [submissionActionType, setSubmissionActionType] = useState("");
  const [listSubmissionActionType, setListSubmissionActionType] = useState([]);
  const [maxNumberSubmission, setMaxNumberSubmission] = useState(10);
  const [participantViewResultMode, setParticipantViewResultMode] = useState("");
  const [listParticipantViewResultModes, setListParticipantViewResultModes] = useState([]);

  const [problemDescriptionViewType, setProblemDescriptionViewType] = useState("");
  const [listProblemDescriptionViewTypes, setListProblemDescriptionViewTypes] = useState([]);

  const [evaluateBothPublicPrivateTestcase, setEvaluateBothPublicPrivateTestcase,] = useState("");

  const [listEvaluateBothPublicPrivateTestcases, setListEvaluateBothPublicPrivateTestcases,] = useState([]);

  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);

  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] = useState(0);
  const [judgeMode, setJudgeMode] = useState("");

  const handleSubmit = () => {
    setLoading(true);

    let body = {
      contestName: contestName,
      contestSolvingTime: contestTime,
      startedAt: startDate,
      countDownTime: countDown,
      statusId: statusId,
      submissionActionType: submissionActionType,
      maxNumberSubmission: maxNumberSubmission,
      participantViewResultMode: participantViewResultMode,
      problemDescriptionViewType: problemDescriptionViewType,
      maxSourceCodeLength: maxSourceCodeLength,
      evaluateBothPublicPrivateTestcase: evaluateBothPublicPrivateTestcase,
      minTimeBetweenTwoSubmissions: minTimeBetweenTwoSubmissions,
      judgeMode: judgeMode,
    };

    request("put", "/contests/" + contestId, () => {
        successNoti("Save contest successfully", 3000);
      },
      {
        onError: () => errorNoti(t("error", {ns: "common"}), 3000)
      }
      , body)
      .then(() => getContestInfo())
  }

  function getContestInfo() {
    request("get", "/contests/" + contestId, (res) => {
      setContestTime(res.data.contestTime);
      setContestName(res.data.contestName);
      setStartDate(res.data.startAt);
      setStatusId(res.data.statusId);
      setListStatusIds(res.data.listStatusIds);
      setSubmissionActionType(res.data.submissionActionType);
      setListSubmissionActionType(res.data.listSubmissionActionTypes);
      setParticipantViewResultMode(res.data.participantViewResultMode);
      setListParticipantViewResultModes(res.data.listParticipantViewModes);
      setMaxNumberSubmission(res.data.maxNumberSubmission);
      setProblemDescriptionViewType(res.data.problemDescriptionViewType);
      setMinTimeBetweenTwoSubmissions(res.data.minTimeBetweenTwoSubmissions);
      setJudgeMode(res.data.judgeMode);
      setListProblemDescriptionViewTypes(res.data.listProblemDescriptionViewTypes);
      setEvaluateBothPublicPrivateTestcase(res.data.evaluateBothPublicPrivateTestcase);
      setMaxSourceCodeLength(res.data.maxSourceCodeLength);
      setListEvaluateBothPublicPrivateTestcases(res.data.listEvaluateBothPublicPrivateTestcases);
    }).then(() => setLoading(false));
  }

  useEffect(() => {
    getContestInfo();
  }, []);


  return (<div>
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <HustContainerCard title={"Edit Contest " + contestId}>
        {loading && <LinearProgress/>}
        {!loading && <Box>
          <Grid container rowSpacing={3} spacing={2}>
            <Grid item xs={9}>
              <TextField
                fullWidth
                autoFocus
                required
                value={contestName}
                id="contestName"
                label="Contest Name"
                onChange={(event) => {
                  setContestName(event.target.value);
                }}
              />
            </Grid>

            <Grid item xs={3}>
              <TextField
                fullWidth
                select
                id="statusId"
                label="Status"
                onChange={(event) => {
                  setStatusId(event.target.value);
                }}
                value={statusId}
              >
                {listStatusIds.map((item) => (<MenuItem key={item} value={item}>
                  {item}
                </MenuItem>))}
              </TextField>
            </Grid>

            <Grid item xs={3}>
              <TextField
                fullWidth
                type="number"
                required
                id="maxNumberSubmission"
                label="Max number of Submissions"
                onChange={(event) => {
                  setMaxNumberSubmission(event.target.value);
                }}
                value={maxNumberSubmission}
                InputProps={{endAdornment: <InputAdornment position="end">per problem</InputAdornment>}}
              />
            </Grid>

            <Grid item xs={3}>
              <TextField
                fullWidth
                type="number"
                id="Max Source Code Length"
                label="Source Length Limit"
                onChange={(event) => {
                  setMaxSourceCodeLength(event.target.value);
                }}
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
                onChange={(event) => {
                  setMinTimeBetweenTwoSubmissions(Number(event.target.value));
                }}
                value={minTimeBetweenTwoSubmissions}
                InputProps={{endAdornment: <InputAdornment position="end">s</InputAdornment>}}
              />
            </Grid>

            <Grid item xs={3}>
              <TextField
                fullWidth
                select
                id="evaluateBothPublicPrivateTestcase"
                label="Evaluate Private Testcases"
                onChange={(event) => {
                  setEvaluateBothPublicPrivateTestcase(event.target.value);
                }}
                value={evaluateBothPublicPrivateTestcase}
              >
                {listEvaluateBothPublicPrivateTestcases.map((item) => (<MenuItem key={item} value={item}>
                  {item}
                </MenuItem>))}
              </TextField>
            </Grid>

            <Grid item xs={3}>
              <TextField
                fullWidth
                select
                id="participantViewResultMode"
                label="Allow viewing testcase detail"
                onChange={(event) => {
                  setParticipantViewResultMode(event.target.value);
                }}
                value={participantViewResultMode}
              >
                {listParticipantViewResultModes.map((item) => (<MenuItem key={item} value={item}>
                  {item}
                </MenuItem>))}
              </TextField>
            </Grid>

            <Grid item xs={3}>
              <TextField
                fullWidth
                select
                id="submissionActionType"
                label="Action on Submission"
                onChange={(event) => {
                  setSubmissionActionType(event.target.value);
                }}
                value={submissionActionType}
              >
                {listSubmissionActionType.map((item) => (<MenuItem key={item} value={item}>
                  {item}
                </MenuItem>))}
              </TextField>
            </Grid>

            <Grid item xs={3}>
              <TextField
                fullWidth
                select
                id="problemDescriptionViewType"
                label="Problem Description View Mode"
                onChange={(event) => {
                  setProblemDescriptionViewType(event.target.value);
                }}
                value={problemDescriptionViewType}
              >
                {listProblemDescriptionViewTypes.map((item) => (<MenuItem key={item} value={item}>
                  {item}
                </MenuItem>))}
              </TextField>
            </Grid>

            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DateTimePicker
                label="Date&Time picker"
                value={startDate}
                onChange={(value) => {
                  setStartDate(value);
                }}
                renderInput={(params) => <TextField {...params} />}
              />
            </LocalizationProvider>
          </Grid>
        </Box>}

        <LoadingButton
          loading={loading}
          variant="contained"
          style={{marginTop: "36px"}}
          onClick={handleSubmit}
        >
          Save
        </LoadingButton>
      </HustContainerCard>
    </MuiPickersUtilsProvider>
  </div>);
}
