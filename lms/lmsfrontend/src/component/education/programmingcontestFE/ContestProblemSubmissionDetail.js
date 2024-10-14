import { Divider, Link, Paper, Stack, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { request } from "api";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import displayTime from "utils/DateTimeUtils";
import { localeOption } from "utils/NumberFormat";
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
  const [comments, setComments] = useState([]);

  useEffect(() => {
    request(
      "get",
      "/student/submissions/" + problemSubmissionId + "/general-info",
      (res) => {
        setSubmission(res.data);
      },
      {}
    );

    const getComments = async () => {
        const res = await request("get", `submissions/${problemSubmissionId}/comments`);
        setComments(res.data);

    };

    getComments();
  }, [problemSubmissionId]);

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
          <Box
            sx={{
              mb: 4,
              fontFamily: "'Fira Code', 'JetBrains Mono', monospace",
              fontVariantLigatures: "none",
            }}
          >
            <HustCopyCodeBlock
              title="Message"
              text={submission.message}
              language="bash"
            />
          </Box>
          {submission.status &&
            submission.status !== "Compile Error" &&
            submission.status !== "In Progress" && (
              <Box sx={{ mb: 4 }}>
                <Typography variant={"h6"} sx={{ mb: 1 }}>
                  Test cases
                </Typography>
                <ParticipantProgramSubmissionDetailTestCaseByTestCase
                  submissionId={problemSubmissionId}
                />
              </Box>
            )}
          <Box
            sx={{
              fontFamily: "'JetBrains Mono', monospace",
              fontVariantLigatures: "none",
            }}
          >
            <HustCopyCodeBlock
              title="Source code"
              text={submission.sourceCode}
              language={resolveLanguage(submission.sourceCodeLanguage)}
              showLineNumbers
            />
          </Box>
          <Box sx={{ mt: 4 }}>
            <Typography variant={"h6"} sx={{ mb: 1 }}>
              Comments
            </Typography>
            {comments.map((comment) => (
              <Typography key={comment.id} variant="body2" sx={{ mb: 1 }}>
                <strong>{comment.username}:</strong> {comment.comment}
              </Typography>
            ))}
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
            [
              "Point",
              `${
                submission.point
                  ? submission.point.toLocaleString("fr-FR", localeOption)
                  : 0
              }`,
            ],
            ["Language ", submission.sourceCodeLanguage],
            [
              "Total runtime",
              `${
                submission.runtime
                  ? submission.runtime.toLocaleString("fr-FR", localeOption)
                  : 0
              } ms`,
            ],
            ["Submited by", submission.submittedByUserId],
            ["Submited at", displayTime(submission.createdAt)],
            ["Last modified", displayTime(submission.updateAt)],
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