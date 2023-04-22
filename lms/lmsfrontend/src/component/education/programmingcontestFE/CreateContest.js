import React, {useState} from "react";
import TextField from "@mui/material/TextField";
import {request} from "../../../api";
import DateFnsUtils from "@date-io/date-fns";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {makeStyles} from "@material-ui/core/styles";
import {sleep} from "./lib";
import {SubmitSuccess} from "./SubmitSuccess";
import {useHistory} from "react-router-dom";
import DateTimePicker from "@mui/lab/DateTimePicker";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import {MenuItem} from "@mui/material";
import HustContainerCard from "../../common/HustContainerCard";
import {LoadingButton} from "@mui/lab";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4), "& .MuiTextField-root": {
      margin: theme.spacing(1), width: "30%", minWidth: 120,
    },
  }, formControl: {
    margin: theme.spacing(1), minWidth: 120, maxWidth: 300,
  },
}));


export default function CreateContest(props) {
  const history = useHistory();

  const [contestName, setContestName] = useState("");
  const [contestId, setContestId] = useState("");
  const [contestTime, setContestTime] = useState(Number(0));
  const [showSubmitSuccess, setShowSubmitSuccess] = useState(false);
  const [isPublic, setIsPublic] = useState(false);
  const [startDate, setStartDate] = useState(new Date());
  const [maxNumberSubmissions, setMaxNumberSubmissions] = useState(10);
  const [countDown, setCountDown] = useState(Number(0));
  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);
  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] = useState(0);

  const [loading, setLoading] = useState(false);

  const classes = useStyles();

  function handleSubmit() {
    setLoading(true);
    let body = {
      contestId: contestId,
      contestName: contestName,
      contestTime: contestTime,
      problemIds: [],
      isPublic: isPublic,
      maxNumberSubmissions: maxNumberSubmissions,
      startedAt: startDate,
      countDownTime: countDown,
      maxSourceCodeLength: maxSourceCodeLength,
      minTimeBetweenTwoSubmissions: minTimeBetweenTwoSubmissions,
    };

    request("post", "/create-contest", (res) => {
      setShowSubmitSuccess(true);
      sleep(500).then((r) => {
        history.push("/programming-contest/teacher-list-contest-manager");
      });
    }, {}, body)
      .then()
      .finally(() => setLoading(false));
  }

  return (<div>
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <HustContainerCard title={"Create Contest"}>
        <form className={classes.root} noValidate autoComplete="off">
          <TextField
            autoFocus
            required
            id="contestId"
            label="Contest Id"
            placeholder="Contest Id"
            onChange={(event) => {
              setContestId(event.target.value);
            }}
          />

          <TextField
            autoFocus
            required
            id="contestName"
            label="Contest Name"
            placeholder="Contest Name"
            onChange={(event) => {
              setContestName(event.target.value);
            }}
          />

          <TextField
            autoFocus
            type="number"
            required
            id="timeLimit"
            label="Time Limit"
            placeholder="Time Limit"
            onChange={(event) => {
              setContestTime(Number(event.target.value));
            }}
          />

          <TextField
            required
            id="Count Down"
            label="Count Down"
            placeholder="Count Down"
            onChange={(event) => {
              setCountDown(Number(event.target.value));
            }}
          />

          <TextField
            type="number"
            id="Max Number Submissions"
            label="Max number of Submissions"
            placeholder="Max number of Submissions"
            onChange={(event) => {
              setMaxNumberSubmissions(Number(event.target.value));
            }}
            value={maxNumberSubmissions}
          />
          <TextField
            type="number"
            id="Max Source Code Length"
            label="Source Length Limit (characters)"
            placeholder="Max Source Code Length"
            onChange={(event) => {
              setMaxSourceCodeLength(event.target.value);
            }}
            value={maxSourceCodeLength}
          />
          <TextField
            type="number"
            id="Submission Interval"
            label="Submission Interval (s)"
            placeholder="Minimum Time Between Submissions"
            onChange={(event) => {
              setMinTimeBetweenTwoSubmissions(Number(event.target.value));
            }}
            value={minTimeBetweenTwoSubmissions}
          />

          <TextField
            select
            id="Public"
            label="Public"
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

        <LoadingButton
          loading={loading}
          variant="contained"
          color={"success"}
          style={{marginLeft: "45px"}}
          onClick={handleSubmit}
        >
          Save
        </LoadingButton>
        <SubmitSuccess
          showSubmitSuccess={showSubmitSuccess}
          content={"Contest created successfully"}
        />
      </HustContainerCard>
    </MuiPickersUtilsProvider>
  </div>);
}
