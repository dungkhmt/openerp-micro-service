import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import {
  Divider,
  IconButton,
  Link,
  Paper,
  Stack,
  Switch,
  Tooltip,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import { request } from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import withScreenSecurity from "component/withScreenSecurity";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import displayTime from "utils/DateTimeUtils";
import { localeOption } from "utils/NumberFormat";
import { successNoti } from "utils/notification";
import ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase from "./ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase";
import { getStatusColor } from "./lib";

export const detail = (key, value, sx, helpText) => (
  <>
    <Typography variant="subtitle2" sx={{ fontWeight: 600, ...sx?.key }}>
      {helpText ? (
        <>
          {key}
          {
            <Tooltip arrow title={helpText}>
              <IconButton sx={{ p: 0.5, pt: 0 }}>
                <HelpOutlineIcon sx={{ fontSize: 16, color: "#000000de" }} />
              </IconButton>
            </Tooltip>
          }
        </>
      ) : (
        key
      )}
    </Typography>

    <Typography
      variant="subtitle2"
      gutterBottom
      sx={{
        mb: 1.5,
        fontWeight: 400,
        whiteSpace: "pre-wrap",
        overflow: "hidden",
        textOverflow: "ellipsis",
        ...sx?.value,
      }}
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

function ContestProblemSubmissionDetailViewedByManager() {
  const { problemSubmissionId } = useParams();

  const [submission, setSubmission] = useState({});
  const [submissionSource, setSubmissionSource] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  const [comment, setComment] = useState('');
  const userLoginId = "exampleUserId"; 

  const handleChange = (event) => {
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

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleSaveComment = () => {
    const body = {
      submissionId: problemSubmissionId,
      userId: userLoginId,
      comment: comment,
      createdStamp: new Date(), 
    };

    request(
      "post",
      `/teacher/submissions/${problemSubmissionId}/comments`,
      (res) => {
        console.log("Comment saved:", res.data);
        handleCloseDialog();
        successNoti("Comment added successfully");
      },
      {},
      body
    ).catch((error) => {
      console.error("Error saving comment:", error);
    });
  };

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
  }, [problemSubmissionId]);

  return (
    <Stack direction="row">
      <Stack
        sx={{
          display: "flex",
          flexGrow: 1,
          boxShadow: 1,
          overflowY: "scroll ",
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
          <Box
            sx={{
              mb: 4,
              fontFamily: "'JetBrains Mono', monospace",
              fontVariantLigatures: "none",
            }}
          >
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 1 }}>
              <Typography variant="h6">Source code</Typography>
              <Button onClick={handleOpenDialog}>
                Comment
              </Button>
            </Box>
            <HustCopyCodeBlock
              text={submission.sourceCode}
              language={resolveLanguage(submission.sourceCodeLanguage)}
              showLineNumbers
            />
          </Box>
          {submission.status &&
            submission.status !== "Compile Error" &&
            submission.status !== "In Progress" && (
              <Box>
                <Typography variant={"h6"} sx={{ mb: 1 }}>
                  Test cases
                </Typography>
                <ManagerViewParticipantProgramSubmissionDetailTestCaseBy TestCase
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
          {submission.managementStatus !== undefined && (
            <Switch
              color="success"
              checked={
                submission.managementStatus === "ENABLED" ||
                submission.managementStatus === null
              }
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
                  color: getStatusColor(submission.status),
                },
              },
            ],
            [
              "Pass",
              submission.testCasePass
                ? `${submission.testCasePass} test cases`
                : "",
            ],
            [
              "Point",
              submission.point
                ? submission.point.toLocaleString("fr-FR", localeOption)
                : 0,
            ],
            ["Language", submission.sourceCodeLanguage],
            [
              "Total runtime",
              submission.runtime
                ? submission.runtime.toLocaleString("fr-FR", localeOption)
                : 0,
            ],
            // ["Memory usage", `${submission.memoryUsage} KB`],
            ["Submited by", submission.submittedByUserId],
            ["Submited at", displayTime(submission.createdAt)],
            ["Last modified", displayTime(submission.updateAt)],
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
          <Dialog
            open={openDialog}
            onClose={handleCloseDialog}
            fullWidth
          >
            <DialogTitle>Comment on Submission Code</DialogTitle>
            <DialogContent
              sx={{
                width: "100%",
                height: "80vh",
                overflowY: "scroll",
                
              }}
            >
              <HustCopyCodeBlock
                title="Source code"
                text={submission.sourceCode}
                language={resolveLanguage(submission.sourceCodeLanguage)}
                showLineNumbers
              />
              <TextField
                label="Comment"
                multiline
                rows={8} 
                value={comment}
                onChange={(event) => setComment(event.target.value)}
                sx={{
                  width: "100%",
                  height: 300, 
                  mt: 2, 
                }}
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={handleCloseDialog}>Cancel</Button>
              <Button onClick={handleSaveComment}>Save Comment</Button>
            </DialogActions>
          </Dialog>
        </Paper>
      </Box>
    </Stack>
  );
}

const screenName = "SCR_MANAGER_CONTEST_SUBMISSION_DETAIL";
export default withScreenSecurity(
  ContestProblemSubmissionDetailViewedByManager,
  screenName,
  true
);