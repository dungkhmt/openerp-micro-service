import InfoIcon from "@mui/icons-material/Info";
import {IconButton, Paper, Stack, Tooltip, Typography} from "@mui/material";
import {request} from "api";
import StandardTable from "component/table/StandardTable";
import React, {useEffect, useState} from "react";
import {localeOption} from "utils/NumberFormat";
import {toFormattedDateTime} from "utils/dateutils";
import {SubmissionTestCaseResultDetail} from "./SubmissionTestCaseResultDetail";
import {useTranslation} from "react-i18next";

export default function ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase(props) {
  const {submissionId} = props;
  const {t} = useTranslation(["education/programmingcontest/testcase", 'common']);

  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [testCasesResult, setTestCasesResult] = useState([]);
  const [testCasesResultDetail, setTestCasesResultDetail] = useState({});
  const [selectedTestCase, setSelectedTestCase] = useState();

  const columns = [
    {
      title: "ID",
      sorting: false,
      cellStyle: {minWidth: 112},
      render: (rowData) => rowData.testCaseId.substring(0, 6),
    },
    {
      title: t("graded"),
      field: "graded",
      cellStyle: {minWidth: 120},
      render: (rowData) => rowData.graded === 'Y' ? t("common:yes") : t("common:no")
    },
    {
      title: t('point'),
      field: "point",
      type: 'numeric',
    },
    {
      title: t("message"),
      field: "message",
      sorting: false,
      cellStyle: {minWidth: 200},
    },
    {
      title: t("runtime") + " (ms)",
      type: 'numeric',
      cellStyle: {minWidth: 150},
      render: (rowData) =>
        rowData.runtime?.toLocaleString("fr-FR", localeOption),
    },
    {
      title: t("memory") + " (MB)",
      type: 'numeric',
      cellStyle: {minWidth: 150},
      render: (rowData) =>
        rowData.memoryUsage ? (rowData.memoryUsage / 1024).toLocaleString("fr-FR", localeOption) : null,
    },
    {
      title: t("common:action"),
      sorting: false,
      align: "center",
      cellStyle: {minWidth: 100},
      render: (rowData) => (
        <Tooltip title={t('common:viewDetail')}>
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
            <InfoIcon/>
          </IconButton>
        </Tooltip>
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
      <Stack direction="row" justifyContent='space-between' mb={1.5}>
        <Typography variant="h6">{t("Test Case")}</Typography>
      </Stack>
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
        components={{
          Container: (props) => <Paper {...props} elevation={0}/>,
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
