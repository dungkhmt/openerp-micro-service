import * as React from "react";
import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import Typography from "@mui/material/Typography";
import {Button, CircularProgress, Divider, InputAdornment, TextField} from "@mui/material";
import {pdf} from "@react-pdf/renderer";
import FileSaver from "file-saver";
import SubmissionOfParticipantPDFDocument from "./template/SubmissionOfParticipantPDFDocument";
import Box from "@mui/material/Box";
import HustContainerCard from "../../common/HustContainerCard";
import {request} from "../../../api";
import {ContestManagerManageProblem} from "./ContestManagerManageProblem";

export function ContestManagerListProblem(props) {
  const contestId = props.contestId;
  const [contestName, setContestName] = useState();
  const [contestTime, setContestTime] = useState();
  const [timeLimit, setTimeLimit] = useState();
  const [isProcessing, setIsProcessing] = useState(false);
  const [threshold, setThreshold] = useState(50);
  const history = useHistory();

  const generatePdfDocument = async (documentData, fileName) => {
    const blob = await pdf(
      <SubmissionOfParticipantPDFDocument data={documentData}/>
    ).toBlob();

    FileSaver.saveAs(blob, fileName);
  };

  function getContestDetail() {
    request("get", "/get-contest-detail/" + contestId, (res) => {
      setContestTime(res.data.contestTime);
      setContestName(res.data.contestName);
      setTimeLimit(res.data.contestTime);
    }).then();
  }

  useEffect(() => {
    getContestDetail();
  }, []);

  function handleEdit() {
    history.push("/programming-contest/contest-edit/" + contestId);
  }

  function handleRejudgeContest(event) {
    event.preventDefault();
    setIsProcessing(true);
    request(
      "post",
      "/evaluate-batch-submission-of-contest/" + contestId,
      (res) => {
        console.log("handleRejudgeContest", res.data);
        //alert("Rejudge DONE!!!");
        setIsProcessing(false);
        //setSuccessful(res.data.contents.content);
        //setTotalPageSuccessful(res.data.contents.totalPages);
      }
    ).then();
  }

  function handleJudgeContest(event) {
    event.preventDefault();
    setIsProcessing(true);
    request(
      "post",
      "/evaluate-batch-not-evaluated-submission-of-contest/" + contestId,
      (res) => {
        console.log("handleJudgeContest", res.data);

        setIsProcessing(false);
        //setSuccessful(res.data.contents.content);
        //setTotalPageSuccessful(res.data.contents.totalPages);
      }
    ).then();
  }

  function handleExportParticipantSubmission() {
    // TODO
    request(
      "get",
      "/get-user-judged-problem-submission/" + contestId,
      (res) => {
        console.log("handleJudgeContest", res.data);
        //alert("Rejudge DONE!!!");
        setIsProcessing(false);
        generatePdfDocument(
          res.data,
          `USER_JUDGED_SUBMISSION-${contestId}.pdf`
        );
        //setSuccessful(res.data.contents.content);
        //setTotalPageSuccessful(res.data.contents.totalPages);
      }
    ).then();

    // build and download PDF from data userSubmissions
  }

  function handleCheckPlagiarism(event) {
    event.preventDefault();
    setIsProcessing(true);
    let body = {
      threshold: threshold,
    };
    request(
      "post",
      "/check-code-similarity/" + contestId,

      (res) => {
        console.log("handleCheckPlagiarism, res = ", res.data);
      },
      {},
      body
    );
  }

  return (
    <HustContainerCard title={"Contest: " + contestName}>
      <Typography variant="h5" component="h2">
        Time Limit: {timeLimit} minutes
      </Typography>
      <Box component="div" sx={{width: "100%", display: "flex", justifyContent: "space-between", alignItems: "center"}}>
        <Box sx={{width: "90%", height: "2.5rem", marginTop: "1rem"}}>
          <Button
            sx={{marginRight: "4px"}}
            variant="contained"
            color="primary"
            onClick={handleEdit}
          >
            EDIT
          </Button>
          <Button
            sx={{marginRight: "4px"}}
            variant="contained"
            color="primary"
            onClick={handleRejudgeContest}
          >
            Rejudge
          </Button>
          <Button
            sx={{marginRight: "4px"}}
            variant="contained"
            color="primary"
            onClick={handleJudgeContest}
          >
            Judge
          </Button>
          <Button
            sx={{marginRight: "4px"}}
            variant="contained"
            color="primary"
            onClick={handleExportParticipantSubmission}
          >
            Export participant submissions
          </Button>
          <Button
            sx={{marginRight: "4px"}}
            variant="contained"
            color="primary"
            onClick={handleCheckPlagiarism}
          >
            Check Plagiarism
          </Button>
        </Box>

        <TextField
          autoFocus
          required
          type="number"
          id="Threshold"
          label="Threshold"
          InputProps={{
            endAdornment: <InputAdornment position="end">%</InputAdornment>,
          }}
          value={threshold}
          onChange={(event) => {
            setThreshold(event.target.value);
          }}
          sx={{width: "10%"}}
        />
        {isProcessing ? <CircularProgress/> : ""}
      </Box>

      <Divider sx={{marginTop: "14px"}}/>
      <Box sx={{margin: "14px 0"}}>
        <ContestManagerManageProblem contestId={contestId} />
      </Box>
    </HustContainerCard>
  );
}
