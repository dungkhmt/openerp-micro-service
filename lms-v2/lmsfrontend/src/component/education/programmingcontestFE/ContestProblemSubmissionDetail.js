import {Divider, Link, Paper, Stack, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import {request} from "api";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import displayTime from "utils/DateTimeUtils";
import {localeOption} from "utils/NumberFormat";
import HustCopyCodeBlock from "../../common/HustCopyCodeBlock";
import {detail, resolveLanguage,} from "./ContestProblemSubmissionDetailViewedByManager";
import ParticipantProgramSubmissionDetailTestCaseByTestCase
  from "./ParticipantProgramSubmissionDetailTestCaseByTestCase";
import {getStatusColor} from "./lib";
import {useTranslation} from "react-i18next";
import {errorNoti} from "../../../utils/notification";
import {mapLanguageToDisplayName} from "./Constant";

export default function ContestProblemSubmissionDetail() {
  const {problemSubmissionId} = useParams();
  const {t} = useTranslation(["education/programmingcontest/testcase", "education/programmingcontest/problem", "education/programmingcontest/contest", 'common']);

  const [submission, setSubmission] = useState({});
  const [comments, setComments] = useState([]);

  useEffect(() => {
    request(
      "get",
      "/student/submissions/" + problemSubmissionId + "/general-info",
      (res) => {
        setSubmission(res.data);
      },
      {
        onError: (e) => {
          errorNoti(t("common:error"))
        }
      },
    );

    request("GET",
      `submissions/${problemSubmissionId}/comments`,
      (res) => {
        setComments(res.data);
      });
  }, [problemSubmissionId]);

  return (
    <Stack sx={{minWidth: 400, flexDirection: {xs: 'column', md: 'row'}, gap: {xs: 2, md: 0}}}>
      <Stack
        sx={{
          display: "flex",
          flexGrow: 1,
          boxShadow: 1,
          overflowY: "auto",
          borderRadius: {xs: 4, md: "16px 0 0 16px"},
          backgroundColor: "#fff",
          height: {md: "calc(100vh - 112px)"},
          order: {xs: 1, md: 0}
        }}
      >
        <Paper
          elevation={0}
          sx={{
            p: 2,
            backgroundColor: "transparent",
          }}
        >
          <Box sx={{mb: 4}}>
            <HustCopyCodeBlock
              title={t('common:message')}
              text={submission.message}
              language="bash"
            />
          </Box>
          {submission.status &&
            submission.status !== "Compile Error" &&
            submission.status !== "In Progress" && (
              <Box sx={{mb: 4}}>
                <ParticipantProgramSubmissionDetailTestCaseByTestCase
                  submissionId={problemSubmissionId}
                />
              </Box>
            )}
          <Box>
            <Typography variant="h6" sx={{mb: 1}}>{t('common:sourceCode')}</Typography>
            <HustCopyCodeBlock
              text={submission.sourceCode}
              language={resolveLanguage(submission.sourceCodeLanguage)}
              showLineNumbers
            />
          </Box>
          <Box sx={{mt: 4}}>
            <Typography variant={"h6"} sx={{mb: 1}}>
              {t('common:comment')}
            </Typography>
            {comments.map((comment) => (
              <Typography key={comment.id} variant="body2" sx={{mb: 1}}>
                <strong>{comment.username}:</strong> {comment.comment}
              </Typography>
            ))}
          </Box>
        </Paper>
      </Stack>
      <Box sx={{order: {xs: 0, md: 1}}}>
        <Paper
          elevation={1}
          sx={{
            p: 2,
            width: {md: 300},
            overflowY: "auto",
            borderRadius: {xs: 4, md: "0 16px 16px 0"},
            height: {md: "calc(100vh - 112px)"},
          }}
        >
          <Typography variant="subtitle1" sx={{fontWeight: 600}}>
            {t('common:submissionDetails')}
          </Typography>
          <Divider sx={{mb: 1}}/>
          <Typography variant="subtitle2" sx={{fontWeight: 600}}>
            {t("common:status")}
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
              t("pass"),
              submission.testCasePass
                ? `${submission.testCasePass} test case`
                : "",
            ],
            [
              t("point"),
              `${
                submission.point
                  ? submission.point.toLocaleString("fr-FR", localeOption)
                  : 0
              }`,
            ],
            [t("common:language"), mapLanguageToDisplayName(submission.sourceCodeLanguage) || ''],
            [
              t("totalRuntime"),
              `${
                submission.runtime
                  ? (submission.runtime / 1000).toLocaleString("fr-FR", localeOption)
                  : 0
              } (s)`,
            ],
            [t("common:createdBy"), submission.submittedByUserId],
            [t("common:createdTime"), displayTime(submission.createdAt)],
            [t("common:lastModified"), displayTime(submission.updateAt)],
            [
              t("education/programmingcontest/problem:problem"),
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
              t("education/programmingcontest/contest:contest"),
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