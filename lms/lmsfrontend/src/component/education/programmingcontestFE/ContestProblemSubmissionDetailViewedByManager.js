import { Divider, Link, Paper, Stack, Typography, Button } from "@mui/material";
import Box from "@mui/material/Box";
import { request } from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import displayTime from "utils/DateTimeUtils";
import ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase from "./ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase";
import { getStatusColor } from "./lib";
//import { Button } from "@material-ui/core";

const detail = (key, value) => (
  <>
    <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
      {key}
    </Typography>

    <Typography
      variant="subtitle2"
      gutterBottom
      sx={{ mb: 2, fontWeight: 400 }}
    >
      {value}{" "}
    </Typography>
  </>
);

export default function ContestProblemSubmissionDetailViewedByManager() {
  const { problemSubmissionId } = useParams();

  //
  const [submission, setSubmission] = useState({});

  //
  const [contestId, setContestId] = useState();
  const [listProblemIds, setListProblemIds] = useState([]);
  const [listProblems, setListProblems] = useState([]);
  const [submissionSource, setSubmissionSource] = useState("");

  function updateCode() {
    let body = {
      contestSubmissionId: problemSubmissionId,
      modifiedSourceCodeSubmitted: submissionSource,
      problemId: submission.problemId,
      contestId: submission.contestId,
    };

    request(
      "put",
      "/submissions/source-code",
      (res) => {
        console.log("update submission source code", res.data);
      },
      {},
      body
    ).then();
  }

  function handleDisableSubmission(){
    //alert('disable submission ' + problemSubmissionId);
    request(
      "get",
      "/teacher/disable-submissions/" + problemSubmissionId,
      (res) => {
        setSubmission(res.data);

        setSubmissionSource(res.data.sourceCode);
      },
      {}
    );
  }
  useEffect(() => {
    request(
      "get",
      "/teacher/submissions/" + problemSubmissionId + "/general-info",
      (res) => {
        setSubmission(res.data);

        setSubmissionSource(res.data.sourceCode);
      },
      {}
    );

    request(
      "get",
      "/subsmissions/" + problemSubmissionId + "/contest",
      (res) => {
        setListProblemIds(res.data.problemIds);
        setListProblems(res.data.problems);
        setContestId(res.data.contestId);
      },
      {}
    );
  }, []);


  return (
    <Stack direction="row">
      <Stack
        sx={{
          display: "flex",
          flexGrow: 1,
          height: "calc(100vh - 112px)",
          overflowY: "scroll",
        }}
      >
        <Paper
          elevation={0}
          sx={{
            p: 2,
            borderRadius: 2,
          }}
        >
          <Button variant="outlined" onClick={handleDisableSubmission}>DISABLE</Button>
          <Box sx={{ mb: 4 }}>
            <Typography variant={"h6"} sx={{ mb: 1 }}>
              Source code
            </Typography>
            <HustCopyCodeBlock
              // title="User output"
              text={submission.sourceCode}
            />
            {/* <TextField
            style={{
              // width: 1.0 * window.innerWidth,
              margin: 20,
            }}
            multiline
            maxRows={100}
            value={submissionSource}
            onChange={(event) => {
              setSubmissionSource(event.target.value);
              console.log(submissionSource);
            }}
          ></TextField> */}
          </Box>
          <Box sx={{ mb: 4 }}>
            <Typography variant={"h6"} sx={{ mb: 1 }}>
              Compile message
            </Typography>
            <HustCopyCodeBlock text={submission.message} />
          </Box>
          <Box>
            {/* <TextField
          autoFocus
          // required
          select
          id="problemId"
          label="Problem"
          placeholder="Problem"
          onChange={(event) => {
            setProblemId(event.target.value);
          }}
          value={problemId}
        >
          {listProblems.map((item) => (
            <MenuItem key={item.problemId} value={item.problemId}>
              {item.problemName}
            </MenuItem>
          ))}
        </TextField>
        <Button onClick={updateCode}>Update Code</Button> */}
            {/*
      <CodeMirror
        height={"400px"}
        width="100%"
        extensions={getExtension()}
        editable={false}
        autoFocus={false}
        value={submissionSource}
      />
      */}
            <Typography variant={"h6"} sx={{ mb: 1 }}>
              Test cases
            </Typography>
            <ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase
              submissionId={problemSubmissionId}
            />
          </Box>
        </Paper>
      </Stack>
      <Box>
        <Paper
          elevation={1}
          sx={{
            width: 300,
            height: "calc(100vh - 112px)",
            p: 2,
            borderRadius: 2,
            overflowY: "scroll",
          }}
        >
          <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
            Submission details
          </Typography>
          <Divider sx={{ mb: 1 }} />
          <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
            Status
          </Typography>
          <Typography
            variant="subtitle2"
            gutterBottom
            sx={{
              color: getStatusColor(`${submission.status}`),
              mb: 2,
              fontWeight: 400,
            }}
          >
            {submission.status}
          </Typography>
          {[
            ["Pass", `${submission.testCasePass} test cases`],
            ["Point", submission.point],
            ["Language", submission.sourceCodeLanguage],
            ["Run time", `${submission.runtime} ms`],
            ["Memory usage", `${submission.memoryUsage} KB`],
            ["Submited by", submission.submittedByUserId],
            ["Submited at", displayTime(new Date(submission.createdAt))],
            ["Last modified", displayTime(new Date(submission.updateAt))],
            [
              "Problem",
              <Link
                href={`/programming-contest/manager-view-problem-detail/${submission.problemId}`}
                variant="subtitle2"
                underline="none"
                target="_blank"
              >
                {submission.problemId}
              </Link>,
            ],
            [
              "Contest",
              <Link
                href={`/programming-contest/contest-manager/${submission.contestId}`}
                variant="subtitle2"
                underline="none"
                target="_blank"
              >
                {submission.contestId}
              </Link>,
            ],
          ].map(([key, value]) => detail(key, value))}
        </Paper>
      </Box>
    </Stack>
  );
}
