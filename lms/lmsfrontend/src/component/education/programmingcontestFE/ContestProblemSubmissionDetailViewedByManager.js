import { Divider, Link, Paper, Stack, Switch, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { request } from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import displayTime from "utils/DateTimeUtils";
import { successNoti } from "utils/notification";
import ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase from "./ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase";
import { getStatusColor } from "./lib";

export const detail = (key, value, sx) => (
  <>
    <Typography variant="subtitle2" sx={{ fontWeight: 600, ...sx?.key }}>
      {key}
    </Typography>

    <Typography
      variant="subtitle2"
      gutterBottom
      sx={{ mb: 2, fontWeight: 400, ...sx?.value }}
    >
      {value}{" "}
    </Typography>
  </>
);

export const resolveLanguage = (str) => {
  if (str) {
    if (str.startsWith("CPP")) {
      return "cpp";
    }

    if (str.startsWith("JAVA")) {
      return "java";
    }

    if (str.startsWith("PYTHON")) {
      return "python";
    }

    if (str === "C") {
      return "c";
    }
  }

  return undefined;
};

export default function ContestProblemSubmissionDetailViewedByManager() {
  const { problemSubmissionId } = useParams();

  const [submission, setSubmission] = useState({});

  const [submissionSource, setSubmissionSource] = useState("");

  const handleChange = (event) => {
    // console.log(event);
    if (event.target.checked === true) {
      handleEnableSubmission();
    } else {
      handleDisableSubmission();
    }
  };

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

  function handleDisableSubmission() {
    request(
      "post",
      "/teacher/submissions/" + problemSubmissionId + "/disable",
      (res) => {
        setSubmission(res.data);
        successNoti("Submission disabled");
      },
      {}
    );
  }

  function handleEnableSubmission() {
    request(
      "post",
      "/teacher/submissions/" + problemSubmissionId + "/enable",
      (res) => {
        setSubmission(res.data);
        successNoti("Submission enabled");
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
  }, []);

  return (
    <Stack direction="row">
      <Stack
        sx={{
          display: "flex",
          flexGrow: 1,
          boxShadow: 1,
          overflowY: "scroll",
          borderTopLeftRadius: 8,
          borderBottomLeftRadius: 8,
          backgroundColor: "#fff",
          height: "calc(100vh - 112px)",
        }}
      >
        <Paper
          elevation={0}
          sx={{
            p: 2,
            backgroundColor: "transparent",
          }}
        >
          <Box sx={{ mb: 4 }}>
            <HustCopyCodeBlock
              title="Message"
              text={submission.message}
              language="bash"
            />
          </Box>
          <Box sx={{ mb: 4 }}>
            <HustCopyCodeBlock
              title="Source code"
              text={submission.sourceCode}
              language={resolveLanguage(submission.sourceCodeLanguage)}
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
          {submission.status &&
            submission.status !== "Compile Error" &&
            submission.status !== "In Progress" && (
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
            )}
        </Paper>
      </Stack>
      <Box>
        <Paper
          elevation={1}
          sx={{
            p: 2,
            width: 300,
            overflowY: "scroll",
            borderTopRightRadius: 8,
            borderBottomRightRadius: 8,
            height: "calc(100vh - 112px)",
          }}
        >
          <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
            Submission details
          </Typography>
          <Divider sx={{ mb: 1 }} />
          <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
            Enabled
          </Typography>
          {submission.managementStatus && (
            <Switch
              color="success"
              checked={submission.managementStatus === "ENABLED"}
              onChange={handleChange}
              inputProps={{ "aria-label": "Switch enable submission" }}
              sx={{ ml: -1.25, mb: 1.25, mt: -1 }}
            />
          )}
          {[
            [
              "Status",
              submission.status,
              {
                value: {
                  color: getStatusColor(`${submission.status}`),
                },
              },
            ],
            [
              "Pass",
              submission.testCasePass
                ? `${submission.testCasePass} test cases`
                : "",
            ],
            ["Point", submission.point],
            ["Language", submission.sourceCodeLanguage],
            ["Total runtime", `${submission.runtime} ms`],
            // ["Memory usage", `${submission.memoryUsage} KB`],
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
          ].map(([key, value, sx]) => detail(key, value, sx))}

          {/* <Divider />
          {submission.managementStatus === "ENABLED" && (
            <Button
              fullWidth
              variant="outlined"
              color="error"
              onClick={handleDisableSubmission}
              sx={{ marginTop: "18px" }}
            >
              DISABLE THIS SUBMISSION
            </Button>
          )}
          {submission.managementStatus === "DISABLED" && (
            <>
              <Typography
                sx={{ color: "gray", fontSize: "14px", marginTop: "12px" }}
              >
                This submission is currently disabled
              </Typography>
              <Button
                fullWidth
                variant="outlined"
                color="primary"
                onClick={handleEnableSubmission}
                sx={{ marginTop: "8px" }}
              >
                ENABLE THIS SUBMISSION
              </Button>
            </>
          )} */}
        </Paper>
      </Box>
    </Stack>
  );
}
