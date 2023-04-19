import React, {useEffect, useState} from "react";
import TextField from "@mui/material/TextField";
import DateFnsUtils from "@date-io/date-fns";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {LinearProgress, MenuItem} from "@mui/material";
import {useParams} from "react-router-dom";
import {makeStyles} from "@material-ui/core/styles";

import DateTimePicker from "@mui/lab/DateTimePicker";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import {errorNoti, successNoti} from "../../../utils/notification";
import {useTranslation} from "react-i18next";
import {request} from "../../../api";
import {LoadingButton} from "@mui/lab";
import HustContainerCard from "../../common/HustContainerCard";
import Box from "@mui/material/Box";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4), "& .MuiTextField-root": {
      margin: theme.spacing(1), width: "30%", minWidth: 120,
    },
  }, formControl: {
    margin: theme.spacing(1), minWidth: 120, maxWidth: 300,
  },
}));


export default function EditContest() {
  const {t} = useTranslation(
    ["education/programmingcontest/contest", "common", "validation"]
  );

  const {contestId} = useParams();

  const [loading, setLoading] = useState(true);

  const [contestName, setContestName] = useState("");
  const [contestTime, setContestTime] = useState(Number(0));
  const [isPublic, setIsPublic] = useState(false);
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

  const [useCacheContestProblem, setUseCacheContestProblem] = useState("");
  const [evaluateBothPublicPrivateTestcase, setEvaluateBothPublicPrivateTestcase,] = useState("");

  const [listEvaluateBothPublicPrivateTestcases, setListEvaluateBothPublicPrivateTestcases,] = useState([]);

  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);

  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] = useState(0);
  const [judgeMode, setJudgeMode] = useState("");
  const [listJudgeModes, setListJudgeModes] = useState([]);

  const classes = useStyles();

  const handleSubmit = () => {
    setLoading(true);

    let body = {
      contestName: contestName,
      contestSolvingTime: contestTime,
      isPublic: isPublic,
      startedAt: startDate,
      countDownTime: countDown,
      statusId: statusId,
      submissionActionType: submissionActionType,
      maxNumberSubmission: maxNumberSubmission,
      participantViewResultMode: participantViewResultMode,
      problemDescriptionViewType: problemDescriptionViewType,
      useCacheContestProblem: useCacheContestProblem,
      maxSourceCodeLength: maxSourceCodeLength,
      evaluateBothPublicPrivateTestcase: evaluateBothPublicPrivateTestcase,
      minTimeBetweenTwoSubmissions: minTimeBetweenTwoSubmissions,
      judgeMode: judgeMode,
    };

    request("post", "/edit-contest/" + contestId, () => {
        successNoti("Save contest successfully", 3000);
      },
      {
        onError: () => errorNoti(t("error", {ns: "common"}), 3000)
      }
      , body)
      .then(() => getContestInfo())
  }

  function getContestInfo() {
    request("get", "/get-contest-detail/" + contestId, (res) => {
      setContestTime(res.data.contestTime);
      setContestName(res.data.contestName);
      setIsPublic(res.data.isPublic);
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
      setUseCacheContestProblem(res.data.useCacheContestProblem);
      setEvaluateBothPublicPrivateTestcase(res.data.evaluateBothPublicPrivateTestcase);
      setMaxSourceCodeLength(res.data.maxSourceCodeLength);
      setListEvaluateBothPublicPrivateTestcases(res.data.listEvaluateBothPublicPrivateTestcases);
      setListJudgeModes(res.data.listJudgeModes);

      console.log("res ", res.data);
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
          <form className={classes.root} noValidate autoComplete="off">
            <TextField
              autoFocus
              required
              value={contestName}
              id="contestName"
              label="Contest Name"
              placeholder="Contest Name"
              onChange={(event) => {
                setContestName(event.target.value);
              }}
            />

            <TextField
              type="number"
              value={contestTime}
              required
              id="timeLimit"
              label="Time Limit"
              onChange={(event) => {
                setContestTime(Number(event.target.value));
              }}
            />

            <TextField
              required
              id="Count Down"
              label="Count Down"
              onChange={(event) => {
                setCountDown(Number(event.target.value));
              }}
            />

            <TextField
              select
              id="Public Contest"
              label="Public Contest"
              onChange={(event) => {
                setIsPublic(event.target.value);
              }}
              value={isPublic}
            >
              <MenuItem key={"true"} value={true}>
                {"true"}
              </MenuItem>
              <MenuItem key={"false"} value={false}>
                {"false"}
              </MenuItem>
            </TextField>
            <TextField
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
            <TextField
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

            <TextField
              type="number"
              required
              id="maxNumberSubmission"
              label="Max number of Submissions"
              onChange={(event) => {
                setMaxNumberSubmission(event.target.value);
              }}
              value={maxNumberSubmission}
            />
            <TextField
              type="number"
              id="Max Source Code Length"
              label="Source Length Limit (characters)"
              onChange={(event) => {
                setMaxSourceCodeLength(event.target.value);
              }}
              value={maxSourceCodeLength}
            />
            <TextField
              type="number"
              id="Submission Interval"
              label="Submission Interval (s)"
              onChange={(event) => {
                setMinTimeBetweenTwoSubmissions(Number(event.target.value));
              }}
              value={minTimeBetweenTwoSubmissions}
            />

            <TextField
              select
              id="participantViewResultMode"
              label="Participant View Result Mode"
              onChange={(event) => {
                setParticipantViewResultMode(event.target.value);
              }}
              value={participantViewResultMode}
            >
              {listParticipantViewResultModes.map((item) => (<MenuItem key={item} value={item}>
                {item}
              </MenuItem>))}
            </TextField>
            <TextField
              select
              id="judgeMode"
              label="Judge Mode"
              onChange={(event) => {
                setJudgeMode(event.target.value);
              }}
              value={judgeMode}
            >
              {listJudgeModes.map((item) => (<MenuItem key={item} value={item}>
                {item}
              </MenuItem>))}
            </TextField>

            <TextField
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

            <TextField
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
          </form>

        </Box>}

        <LoadingButton
          loading={loading}
          variant="contained"
          style={{marginLeft: "45px"}}
          onClick={handleSubmit}
        >
          Save
        </LoadingButton>
      </HustContainerCard>
    </MuiPickersUtilsProvider>
  </div>);
}
