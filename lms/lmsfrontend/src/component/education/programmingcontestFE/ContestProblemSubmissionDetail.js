import { Divider, Link, Paper, Stack, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import displayTime from "utils/DateTimeUtils";
import { request } from "../../../api";
import HustCopyCodeBlock from "../../common/HustCopyCodeBlock";
import {
  detail,
  resolveLanguage,
} from "./ContestProblemSubmissionDetailViewedByManager";
import ParticipantProgramSubmissionDetailTestCaseByTestCase from "./ParticipantProgramSubmissionDetailTestCaseByTestCase";
import { getStatusColor } from "./lib";

export default function ContestProblemSubmissionDetail() {
  const { problemSubmissionId } = useParams();

  const [submission, setSubmission] = useState({});

  useEffect(() => {
    request(
      "get",
      "/student/submissions/" + problemSubmissionId + "/general-info",
      (res) => {
        setSubmission(res.data);
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
          height: "calc(100vh - 112px)",
        }}
      >
        <Paper
          elevation={1}
          sx={{
            p: 2,
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
          </Box>
          <Box>
            <Typography variant={"h6"} sx={{ mb: 1 }}>
              Test cases
            </Typography>
            <ParticipantProgramSubmissionDetailTestCaseByTestCase
              submissionId={problemSubmissionId}
            />
          </Box>
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
                href={`/programming-contest/student-view-contest-problem-detail/${submission.contestId}/${submission.problemId}`}
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
                href={`/programming-contest/student-view-contest-detail/${submission.contestId}`}
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
