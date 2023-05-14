import React, { useEffect, useState } from "react";

import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import { request } from "../../api";
import { SubmitSuccess } from "../common/SubmitSuccess";
import { useHistory } from "react-router-dom";
import { MenuItem } from "@mui/material";
import DateTimePicker from "@mui/lab/DateTimePicker";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { toFormattedDateTime } from "../../utils/dateutils";

import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';

import { Button, Card, CardActions } from "@mui/material";
import CardContent from "@mui/material/CardContent";
import { makeStyles } from "@mui/styles";

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
export default  function OptimizePassBookForLoan(){
    const [loan, setLoan] = useState(0);
    const [date, setDate] = useState(null);
    const [bankRate, setBankRate] = useState(0);
    const [discountRate, setDiscountRate] = useState(0);
    const[userId, setUserId] = useState(null);
    const[loans, setLoans] = useState([]);
    const [rawRs, setRawRs] = useState(null);
    const columns = [
        {title:"PassBook", field: "passBookName"},
        {title:"Duration", field: "duration"},
        {title:"Start Date", field: "startDate"},
        {title:"End Date", field: "endDate"},
        {title:"Deposit", field: "amountMoneyDeposit"},
        {title:"Rate", field: "rate"},        
        {title:"Money Early", field: "moneyEarly"},
        {title:"Money Mature", field: "moneyMature"},        
        {title:"Loan", field: "amountMoneyLoan"},
        {title:"MoneyEarly", field: "earlyMoneyRetrieved"},
        
    ];

    const classes = useStyles();

    function handleSubmit() {
        let body = {          
          loan: loan,
          date: date,
          userId:userId,
          discountRate: discountRate,
          loadRate: bankRate
        };
        request(
          "post",
          "/compute-loan-solution",
          (res) => {
            //setLoans(res.data.loans);
            const data = res.data.loans.map((c) => ({
              ...c.passBook,
              startDate: toFormattedDateTime(c.passBook.createdDate),
              endDate: toFormattedDateTime(c.passBook.endDate),
              moneyEarly: c.moneyEarly,
              moneyMature: c.moneyMature,
              amountMoneyLoan: c.amountMoneyLoan,
              earlyMoneyRetrieved: c.earlyMoneyRetrieved
            }));
            setLoans(data);
            setRawRs(res.data.info);
          },
          {},
          body
        ).then();
      }
    
    return (
        <div>
            <LocalizationProvider >
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
                id="loan"
                label="loan"
                placeholder="loan"
                onChange={(event) => {
                  setLoan(event.target.value);
                }}
              />
              <TextField
                autoFocus
                required
                id="date"
                label="date"
                placeholder="date"
                onChange={(event) => {
                  setDate(event.target.value);
                }}
              />
             <TextField
                autoFocus
                required
                id="bankRate"
                label="bankRate"
                placeholder="bankDate"
                onChange={(event) => {
                  setBankRate(event.target.value);
                }}
              />
            <TextField
                autoFocus
                required
                id="DiscountRate"
                label="DisCountRate"
                placeholder="DisCountDate"
                onChange={(event) => {
                  setDiscountRate(event.target.value);
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
            </form>
          </CardContent>
          <CardActions>
            <Button
              variant="contained"
              style={{ marginLeft: "45px" }}
              onClick={handleSubmit}
            >
              Compute
            </Button>
            
          </CardActions>
        </Card>
        </LocalizationProvider>
      
        <StandardTable
        title="Loan List"
        columns={columns}
        data={loans}
        // hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
      {rawRs}
      </div>
  
    )
}