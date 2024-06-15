import { makeStyles } from "@material-ui/core/styles";
import InfoIcon from "@mui/icons-material/Info";
import { IconButton, Stack } from "@mui/material";
import Box from "@mui/material/Box";
import { request } from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import { FacebookCircularProgress } from "component/common/progressBar/CustomizedCircularProgress";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { localeOption } from "utils/NumberFormat";
import { toFormattedDateTime } from "utils/dateutils";

const useStyles = makeStyles((theme) => ({
  paper: { maxWidth: 900 },
  content: {
    minWidth: 640,
    maxHeight: 500,
    overflowY: "auto",
    marginBottom: 12,
    padding: "4px 12px",
  },
}));

export default function ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase(
  props
) {
  const { submissionId } = props;

  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [testCasesResult, setTestCasesResult] = useState([]);
  const [testCasesResultDetail, setTestCasesResultDetail] = useState({});
  const [selectedTestCase, setSelectedTestCase] = useState();

  const columns = [
    {
      title: "TestCase ID",
      sorting: false,
      cellStyle: { minWidth: 112 },
      render: (rowData) => rowData.testCaseId.substring(0, 6),
    },
    {
      title: "Point",
      field: "point",
      // align: "right"
    },
    {
      title: "Runtime (ms)",
      // align: "right",
      cellStyle: { minWidth: 150 },
      render: (rowData) =>
        rowData.runtime?.toLocaleString("fr-FR", localeOption),
    },
    {
      title: "Message",
      field: "message",
      sorting: false,
      cellStyle: { minWidth: 200 },
    },
    {
      title: "Detail",
      sorting: false,
      // align: "center",
      render: (rowData) => (
        <IconButton
          color="primary"
          onClick={() => {
            const testCaseId = rowData.testCaseId;

            setOpen(true);

            if (testCasesResultDetail[testCaseId]) {
              fillModalData(testCasesResultDetail[testCaseId]);
            } else {
              setLoading(true);

              request(
                "get",
                `/teacher/submissions/${submissionId}/testcases/${testCaseId}`,
                (res) => {
                  setLoading(false);
                  setTestCasesResultDetail((prev) => ({
                    ...prev,
                    [testCaseId]: res.data[0] || {},
                  }));

                  fillModalData(res.data[0]);
                },
                {
                  onError: (e) => {
                    setLoading(false);
                  },
                }
              );
            }
          }}
        >
          <InfoIcon />
        </IconButton>
      ),
    },
  ];

  function fillModalData(testCaseResult) {
    setSelectedTestCase({
      testCase: testCaseResult.testCase,
      correctAns: testCaseResult.testCaseAnswer,
      participantAnswer: testCaseResult.participantAnswer,
    });
  }

  function getTestCasesResult() {
    request("get", "/teacher/submissions/" + submissionId, (res) => {
      const testCases = res.data.map((tc) => ({
        ...tc,
        createdAt: toFormattedDateTime(tc.createdAt),
      }));

      setTestCasesResult(testCases);
    });
  }

  const ModalPreview = ({ data }) => {
    return (
      <CustomizedDialogs
        open={open}
        handleClose={() => setOpen(false)}
        contentTopDivider
        classNames={{ paper: classes.paper, content: classes.content }}
        content={
          loading ? (
            <Stack
              direction="row"
              justifyContent="center"
              alignItems="center"
              sx={{ minHeight: 128 }}
            >
              <FacebookCircularProgress />
            </Stack>
          ) : (
            <Box sx={{ minWidth: 859 }}>
              <Stack
                direction="row"
                justifyContent="space-between"
                spacing={2}
                sx={{ mb: 2 }}
              >
                <Box sx={{ width: "calc(50% - 8px)" }}>
                  <HustCopyCodeBlock
                    title="Correct output"
                    text={data?.correctAns}
                  />
                </Box>
                <Box sx={{ width: "calc(50% - 8px)" }}>
                  <HustCopyCodeBlock
                    title="User output"
                    text={data?.participantAnswer}
                  />
                </Box>
              </Stack>
              <HustCopyCodeBlock title="Input" text={data?.testCase} />
            </Box>
          )
        }
      />
    );
  };

  useEffect(() => {
    getTestCasesResult();
  }, []);

  return (
    <>
      <StandardTable
        columns={columns}
        data={testCasesResult}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: false,
          sorting: true,
        }}
      />
      <ModalPreview data={selectedTestCase} />
    </>
  );
}
