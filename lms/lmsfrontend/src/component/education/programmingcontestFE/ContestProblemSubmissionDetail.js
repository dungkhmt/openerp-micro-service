import {useParams} from "react-router-dom";
import * as React from "react";
import {useEffect, useState} from "react";
import {Typography} from "@mui/material";
import Box from "@mui/material/Box";
import {Grid} from "@material-ui/core";
import {getStatusColor} from "./lib";
import ParticipantProgramSubmissionDetailTestCaseByTestCase
  from "./ParticipantProgramSubmissionDetailTestCaseByTestCase";
import HustCopyCodeBlock from "../../common/HustCopyCodeBlock";
import {toFormattedDateTime} from "../../../utils/dateutils";
import {request} from "../../../api";

export default function ContestProblemSubmissionDetail() {
  const { problemSubmissionId } = useParams();
  const [memoryUsage, setMemoryUsage] = useState();
  const [problemId, setProblemId] = useState("");
  const [runTime, setRunTime] = useState();
  const [score, setScore] = useState();
  const [submissionLanguage, setSubmissionLanguage] = useState();
  const [submissionSource, setSubmissionSource] = useState("");
  const [submittedAt, setSubmittedAt] = useState();
  const [testCasePass, setTestCasePass] = useState();
  const [status, setStatus] = useState();
  const [message, setMessage] = useState("");

  useEffect(() => {
    request(
      "get",
      "/get-contest-problem-submission-detail-viewed-by-participant/" +
        problemSubmissionId,
      (res) => {
        setMemoryUsage(res.data.memoryUsage);
        setProblemId(res.data.problemId);
        setRunTime(res.data.runTime);
        setScore(res.data.point);
        setSubmissionLanguage(res.data.sourceCodeLanguage);
        setSubmissionSource(res.data.sourceCode);
        setSubmittedAt(res.data.createdAt);
        setTestCasePass(res.data.testCasePass);
        setStatus(res.data.status);
        setMessage(res.data.message);
      },
      {}
    ).then();
  }, []);

  return (
    <div>
      <Typography variant={"h5"}>
        Submission detail - <em>{problemId}</em>
      </Typography>
      <Box
        sx={{
          width: "100%",
          bgcolor: "background.paper",
          height: "120px",
          border: "2px solid gray",
          borderRadius: "8px",
          boxShadow: "4px",
          padding: "10px",
          marginTop: "14px",
          justifyItems: "center",
          justifySelf: "center",
        }}
      >
        <Grid container alignItems="center">
          <Grid item xs>
            <Typography variant="h6">
              <b>{testCasePass}</b> test cases passed
            </Typography>
          </Grid>
          <Grid item xs>
            <Typography variant="h6" align="right">
              status:{" "}
              <span style={{ color: getStatusColor(`${status}`) }}>
                {`${status}`}
              </span>
            </Typography>
          </Grid>
        </Grid>
        <Grid container alignItems="center">
          <Grid item xs>
            <Typography variant="h6">
              Run Time: <i>{runTime}</i>
              <br />
              Memory Usage: <i>{memoryUsage} kb</i>
            </Typography>
          </Grid>
          <Grid item xs>
            <Typography variant="h6" align="right">
              Total point: <b>{score}</b>
              <br />
              Submitted at: {toFormattedDateTime(submittedAt)}
            </Typography>
          </Grid>
        </Grid>
      </Box>

      <br />
      {
        <ParticipantProgramSubmissionDetailTestCaseByTestCase
          submissionId={problemSubmissionId}
        />
      }
      <br />
      <HustCopyCodeBlock
        title={"Source code - " + submissionLanguage}
        text={submissionSource}
      />
      {message && message.length > 0 && <h3>Compile Message: {message}</h3>}
    </div>
  );
}
