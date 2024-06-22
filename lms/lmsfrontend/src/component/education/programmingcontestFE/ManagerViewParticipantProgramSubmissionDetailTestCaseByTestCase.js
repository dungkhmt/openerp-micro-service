import InfoIcon from "@mui/icons-material/Info";
import { IconButton } from "@mui/material";
import { request } from "api";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { localeOption } from "utils/NumberFormat";
import { toFormattedDateTime } from "utils/dateutils";
import { SubmissionTestCaseResultDetail } from "./SubmissionTestCaseResultDetail";

export default function ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase(
  props
) {
  const { submissionId } = props;

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
              setSelectedTestCase(testCasesResultDetail[testCaseId]);
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

                  setSelectedTestCase(res.data[0]);
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

  function getTestCasesResult() {
    request("get", "/teacher/submissions/" + submissionId, (res) => {
      const testCases = res.data.map((tc) => ({
        ...tc,
        createdAt: toFormattedDateTime(tc.createdAt),
      }));

      setTestCasesResult(testCases);
    });
  }

  useEffect(() => {
    getTestCasesResult();
  }, []);

  return (
    <>
      <StandardTable
        columns={columns}
        data={testCasesResult}
        hideCommandBar
        hideToolBar
        options={{
          selection: false,
          pageSize: 5,
          search: false,
          sorting: true,
        }}
      />
      <SubmissionTestCaseResultDetail
        open={open}
        loading={loading}
        data={selectedTestCase}
        handleClose={() => setOpen(false)}
      />
    </>
  );
}
