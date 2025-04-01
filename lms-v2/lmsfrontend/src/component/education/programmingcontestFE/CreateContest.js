import React, { useState } from "react";
import DateFnsUtils from "@date-io/date-fns";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import { LoadingButton } from "@mui/lab";
import { Grid, InputAdornment } from "@mui/material";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import { request } from "api";
import withScreenSecurity from "component/withScreenSecurity";
import { useHistory } from "react-router-dom";
import { errorNoti, successNoti } from "utils/notification";
import HustContainerCard from "../../common/HustContainerCard";
import { sleep } from "./lib";

function CreateContest(props) {
  const history = useHistory();

  const [contestName, setContestName] = useState("");
  const [contestId, setContestId] = useState("");
  const [startDate, setStartDate] = useState(new Date());
  const [maxNumberSubmissions, setMaxNumberSubmissions] = useState(10);
  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);
  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] = useState(0);
  const [loading, setLoading] = useState(false);

  const isValidContestId = () => {
    return new RegExp(/[%^/\\|.?;[\]]/g).test(contestId);
  };

  const isValidContestName = () => {
    return new RegExp(/[%^/\\|.?;[\]]/g).test(contestName);
  };

  function handleSubmit() {
    setLoading(true);
    let body = {
      contestId: contestId,
      contestName: contestName,
      contestTime: 0,
      problemIds: [],
      maxNumberSubmissions: maxNumberSubmissions,
      startedAt: startDate,
      countDownTime: 0,
      maxSourceCodeLength: maxSourceCodeLength,
      minTimeBetweenTwoSubmissions: minTimeBetweenTwoSubmissions,
    };

    request(
      "post",
      "/contests",
      (res) => {
        successNoti("Contest created successfully");
        sleep(1000).then(() => {
          history.push("/programming-contest/contest-manager/" + res.data.contestId);
        });
      },
      {
        onError: (err) => {
          errorNoti(err?.response?.data?.message, 5000);
        },
      },
      body
    )
      .then()
      .finally(() => setLoading(false));
  }

  return (
    <div>
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <HustContainerCard title={"Create Contest"}>
          {!loading && (
            <Box>
              <Grid container rowSpacing={3} spacing={2}>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    autoFocus
                    required
                    value={contestId}
                    id="contestId"
                    label="Contest Id"
                    onChange={(event) => {
                      setContestId(event.target.value);
                    }}
                    error={isValidContestId()}
                    helperText={
                      isValidContestId()
                        ? "Contest ID must not contain special characters including %^/\\|.?;[]"
                        : ""
                    }
                  />
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    required
                    value={contestName}
                    id="contestName"
                    label="Contest Name"
                    onChange={(event) => {
                      setContestName(event.target.value);
                    }}
                    //error={isValidContestName()}
                    helperText={
                      //isValidContestName()
                      //  ? "Contest Name must not contain special characters including %^/\\|.?;[]"
                      //  : ""
                      ""
                    }
                  />
                </Grid>

                <Grid item xs={3}>
                  <TextField
                    fullWidth
                    type="number"
                    required
                    id="maxNumberSubmission"
                    label="Max number of Submissions"
                    onChange={(event) => {
                      setMaxNumberSubmissions(event.target.value);
                    }}
                    value={maxNumberSubmissions}
                    InputProps={{
                      endAdornment: <InputAdornment position="end">per problem</InputAdornment>,
                    }}
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
                    InputProps={{
                      endAdornment: <InputAdornment position="end">chars</InputAdornment>,
                    }}
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
                    InputProps={{
                      endAdornment: <InputAdornment position="end">s</InputAdornment>,
                    }}
                  />
                </Grid>
              </Grid>
            </Box>
          )}

          <LoadingButton
            loading={loading}
            variant="contained"
            style={{ marginTop: "36px" }}
            onClick={handleSubmit}
            //disabled={isValidContestId() || isValidContestName() || loading}
            disabled={isValidContestId() || loading}
          >
            Save
          </LoadingButton>
        </HustContainerCard>
      </MuiPickersUtilsProvider>
    </div>
  );
}

const screenName = "SCR_CREATE_CONTEST";
export default withScreenSecurity(CreateContest, screenName, true);
