import React, {useState} from "react";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import {request} from "../../../api";
import DateFnsUtils from "@date-io/date-fns";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {Button, Card, CardActions} from "@material-ui/core";
import CardContent from "@material-ui/core/CardContent";
import {makeStyles} from "@material-ui/core/styles";
import {sleep} from "./lib";
import {SubmitSuccess} from "./SubmitSuccess";
import {useHistory} from "react-router-dom";
import {MenuItem} from "@material-ui/core/";
import DateTimePicker from "@mui/lab/DateTimePicker";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";

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

  const classes = useStyles();

  function handleSubmit() {
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
      // console.log("problem list", res.data);
      setShowSubmitSuccess(true);
      sleep(1000).then((r) => {
        history.push("/programming-contest/teacher-list-contest-manager");
      });
    }, {}, body).then();
  }

  return (<div>
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Create Contest
          </Typography>
          <br/>
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
        </CardContent>
        <CardActions>
          <Button
            variant="contained"
            color="light"
            style={{marginLeft: "45px"}}
            onClick={handleSubmit}
          >
            Save
          </Button>
          <SubmitSuccess
            showSubmitSuccess={showSubmitSuccess}
            content={"Contest created successfully"}
          />
        </CardActions>
      </Card>
    </MuiPickersUtilsProvider>
  </div>);
}
