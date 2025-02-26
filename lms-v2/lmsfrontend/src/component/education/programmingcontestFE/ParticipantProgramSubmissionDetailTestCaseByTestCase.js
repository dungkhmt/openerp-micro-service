import InfoIcon from "@mui/icons-material/Info";
import {IconButton, Paper, Stack, Tooltip} from "@mui/material";
import {request} from "api";
import {useEffect, useState} from "react";
import {toFormattedDateTime} from "utils/dateutils";
import StandardTable from "../../table/StandardTable";
import {SubmissionTestCaseResultDetail} from "./SubmissionTestCaseResultDetail";
import {localeOption} from "../../../utils/NumberFormat";
import {useTranslation} from "react-i18next";
import Typography from "@mui/material/Typography";

export default function ParticipantProgramSubmissionDetailTestCaseByTestCase(props) {
  const {submissionId} = props;
  const {t} = useTranslation(["education/programmingcontest/testcase", 'common']);

  const [submissionTestCase, setSubmissionTestCase] = useState([]);
  const [open, setOpen] = useState(false);
  const [testCaseDetailList, setTestCaseDetailList] = useState([]);
  const [selectedTestCase, setSelectedTestCase] = useState();

  const [isProcessing, setIsProcessing] = useState(true);

  const [score, setScore] = useState(0);

  const columns = [
    {
      title: t("graded"),
      field: "graded",
      cellStyle: {minWidth: 120},
      render: (rowData) => rowData.graded === 'Y' ? t("common:yes") : t("common:no")
    },
    {title: t('point'), field: "point", type: 'numeric'},
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
              for (let i = 0; i < testCaseDetailList.length; i++) {
                if (testCaseDetailList[i].testCaseId === rowData.testCaseId) {
                  setSelectedTestCase(testCaseDetailList[i]);
                }
              }

              setOpen(true);
            }}
          >
            <InfoIcon/>
          </IconButton>
        </Tooltip>
      ),
    },
    // {
    //   title: "",
    //   render: (rowData) =>
    //     rowData.viewSubmitSolutionOutputMode == "Y" ? (
    //       <div>
    //         {isProcessing ? <CircularProgress /> : ""}
    //         <button
    //           color="primary"
    //           type="submit"
    //           //onChange={onInputChange}
    //           onClick={() => handleFormSubmit(event, rowData.testCaseId)}
    //           width="100%"
    //         >
    //           UPLOAD
    //         </button>
    //
    //         <input
    //           type="file"
    //           id="selected-upload-file"
    //           onChange={() => onFileChange(event, rowData.testCaseId)}
    //         />
    //       </div>
    //     ) : (
    //       ""
    //     ),
    // },
  ];

  function handleFormSubmit(event, testCaseId) {
    let selectedFile = null;
    for (let i = 0; i < testCaseDetailList.length; i++) {
      if (testCaseDetailList[i].testCaseId === testCaseId) {
        selectedFile = testCaseDetailList[i].file;
        break;
      }
    }
    event.preventDefault();
    let body = {
      testCaseId: testCaseId,
      submissionId: submissionId,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", selectedFile);

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/submissions/testcases/solution-output",
      (res) => {
        res = res.data;
        setScore(res.score);
        let arr_res = [];
        for (let i = 0; i < submissionTestCase.length; i++) {
          arr_res.push(submissionTestCase[i]);
        }
        for (let i = 0; i < arr_res.length; i++) {
          if (arr_res[i].testCaseId === res.selectedTestCaseId) {
            arr_res[i].point = res.score;
            arr_res[i].message = res.message;
          }
        }
        setSubmissionTestCase(arr_res);
        getSubmissionDetailTestCaseByTestCase();
        //setFilename("");
      },
      {
        onError: (e) => {
        },
      },
      formData,
      config
    );
  }

  function onFileChange(e, testCaseId) {
    //alert("testCase " + testCaseId + " change file " + e.target.files[0].name);
    let arr = [];
    for (let i = 0; i < testCaseDetailList.length; i++) {
      arr.push(testCaseDetailList[i]);
    }
    for (let i = 0; i < arr.length; i++) {
      if (arr[i].testCaseId === testCaseId) {
        arr[i].file = e.target.files[0];
      }
    }
    setTestCaseDetailList(arr);
  }

  function getSubmissionDetailTestCaseByTestCase() {
    request(
      "get",
      "/student/submissions/" + submissionId,
      (res) => {
        let L = res.data.map((c) => ({
          ...c,
          createdAt: toFormattedDateTime(c.createdAt),
        }));
        setSubmissionTestCase(L);

        let tcl = [];
        res.data.map((e) => {
          tcl.push({
            testCaseId: e.testCaseId,
            testCase: e.testCase,
            testCaseAnswer: e.testCaseAnswer,
            participantAnswer: e.participantAnswer,
            file: "",
          });
        });
        // console.log("testCaseDetailList tcl = ", tcl);
        setTestCaseDetailList(tcl);
      },
      {
        401: () => {
        },
      }
    ).then(() => setIsProcessing(false));
  }

  useEffect(() => {
    getSubmissionDetailTestCaseByTestCase();
  }, []);

  return (
    <>
      <Stack direction="row" justifyContent='space-between' mb={1.5}>
        <Typography variant="h6">{t("Test Case")}</Typography>
      </Stack>
      <StandardTable
        columns={columns}
        data={submissionTestCase}
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
        isLoading={isProcessing}
      />
      <SubmissionTestCaseResultDetail
        open={open}
        data={selectedTestCase}
        handleClose={() => setOpen(false)}
      />
    </>
  );
}
