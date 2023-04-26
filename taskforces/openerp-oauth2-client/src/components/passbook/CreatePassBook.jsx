import React, { useEffect, useState } from "react";

import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import { request } from "../../api";
import { SubmitSuccess } from "../common/SubmitSuccess";
import { useHistory } from "react-router-dom";
import { MenuItem } from "@mui/material";
import DateTimePicker from "@mui/lab/DateTimePicker";
import { LocalizationProvider } from "@mui/x-date-pickers";
//import AdapterDateFns from "@mui/lab/AdapterDateFns";
import { Button, Card, CardActions } from "@mui/material";
import CardContent from "@mui/material/CardContent";
import { makeStyles } from "@mui/styles";
//import DateFnsUtils from "@date-io/date-fns";
//import { LocalizationProvider } from "@mui/lab";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "30%",
      minWidth: 120,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));

export default function CreatePassBook() {
  const history = useHistory();

  const SYNCHRONOUS_JUDGE_MODE = "SYNCHRONOUS_JUDGE_MODE";
  const ASYNCHRONOUS_JUDGE_MODE_QUEUE = "ASYNCHRONOUS_JUDGE_MODE_QUEUE";

  const [passBookName, setPassBookName] = useState("");
  const [userId, setUserId] = useState("");
  const [duration, setDuration] = useState(Number(0));
  const [page, setPage] = useState(0);
  const [amountMoney, setAmountMoney] = useState(0);
  const [rate, setRate] = useState(0);
  const [showSubmitSuccess, setShowSubmitSuccess] = useState(false);

  const classes = useStyles();

  function handleSubmit() {
    let body = {
      passBookName: passBookName,
      userId: userId,
      duration: duration,
      amountMoney: amountMoney,
      rate: rate,
    };
    request(
      "post",
      "/create-passbook",
      (res) => {
        // console.log("problem list", res.data);
        setShowSubmitSuccess(true);
      },
      {},
      body
    ).then();
  }

  return (
    <div>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Create Passbook
          </Typography>
          <br />
          <form className={classes.root} noValidate autoComplete="off">
            <TextField
              autoFocus
              required
              id="PassBookName"
              label="PassBook Name"
              placeholder="PassBook Name"
              onChange={(event) => {
                setPassBookName(event.target.value);
              }}
            />
            <TextField
              autoFocus
              required
              id="UserID"
              label="UserID"
              placeholder="UserID"
              onChange={(event) => {
                setUserId(event.target.value);
              }}
            />
            <TextField
              autoFocus
              required
              id="AmountMoney"
              label="Amount Money"
              placeholder="Amount Money"
              onChange={(event) => {
                setAmountMoney(event.target.value);
              }}
            />
            <TextField
              autoFocus
              required
              id="Duration"
              label="Duration"
              placeholder="Duration"
              onChange={(event) => {
                setDuration(event.target.value);
              }}
            />
            <TextField
              autoFocus
              required
              id="Rate"
              label="Rate"
              placeholder="Rate"
              onChange={(event) => {
                setRate(event.target.value);
              }}
            />
          </form>
        </CardContent>
        <CardActions>
          <Button
            variant="contained"
            style={{ marginLeft: "45px" }}
            onClick={handleSubmit}
          >
            Save
          </Button>
          <SubmitSuccess
            showSubmitSuccess={showSubmitSuccess}
            content={"You have saved a passbook"}
          />
        </CardActions>
      </Card>
      {/*</LocalizationProvider>*/}
    </div>
  );
}
