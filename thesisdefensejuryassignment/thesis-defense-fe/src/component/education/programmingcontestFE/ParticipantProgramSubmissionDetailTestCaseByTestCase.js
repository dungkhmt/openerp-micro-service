import InfoIcon from "@mui/icons-material/Info";
import { CircularProgress, IconButton, LinearProgress } from "@mui/material";
import Box from "@mui/material/Box";
import { request } from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import { useEffect, useState } from "react";
import { toFormattedDateTime } from "utils/dateutils";
import StandardTable from "../../table/StandardTable";

export default function ParticipantProgramSubmissionDetailTestCaseByTestCase(
  props
) {
  const { submissionId } = props;
  const [submissionTestCase, setSubmissionTestCase] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [testcaseDetailList, setTestcaseDetailList] = useState([]);
  const [selectedTestcase, setSelectedTestcase] = useState();

  const [isProcessing, setIsProcessing] = useState(true);

  const [score, setScore] = useState(0);

  const columns = [
    { title: "Point", field: "point" },
    // {
    //   title: "Runtime (ms)",
    //   // align: "right",
    //   cellStyle: { minWidth: 150 },
    //   render: (rowData) =>
    //     rowData.runtime.toLocaleString("fr-FR", localeOption),
    // },
    {
      title: "Message",
      field: "message",
      sorting: false,
      cellStyle: { minWidth: 200 },
    },
    {
      title: "Detail",
      sorting: false,
      render: (rowData) => (
        <IconButton
          color="primary"
          onClick={() => {
            for (let i = 0; i < testcaseDetailList.length; i++) {
              if (testcaseDetailList[i].testCaseId === rowData.testCaseId) {
                setSelectedTestcase(testcaseDetailList[i]);
              }
            }

            setOpenModal(true);
          }}
        >
          <InfoIcon />
        </IconButton>
      ),
    },
    {
      title: "",
      render: (rowData) =>
        rowData.viewSubmitSolutionOutputMode == "Y" ? (
          <div>
            {isProcessing ? <CircularProgress /> : ""}
            <button
              color="primary"
              type="submit"
              //onChange={onInputChange}
              onClick={(event) => handleFormSubmit(event, rowData.testCaseId)}
              width="100%"
            >
              UPLOAD
            </button>

            <input
              type="file"
              id="selected-upload-file"
              onChange={(event) => onFileChange(event, rowData.testCaseId)}
            />
          </div>
        ) : (
          ""
        ),
    },
  ];

  function handleFormSubmit(event, testCaseId) {
    let selectedFile = null;
    for (let i = 0; i < testcaseDetailList.length; i++) {
      if (testcaseDetailList[i].testCaseId === testCaseId) {
        selectedFile = testcaseDetailList[i].file;
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
        onError: (e) => {},
      },
      formData,
      config
    );
  }

  function onFileChange(e, testCaseId) {
    //alert("testCase " + testCaseId + " change file " + e.target.files[0].name);
    let arr = [];
    for (let i = 0; i < testcaseDetailList.length; i++) {
      arr.push(testcaseDetailList[i]);
    }
    for (let i = 0; i < arr.length; i++) {
      if (arr[i].testCaseId === testCaseId) {
        arr[i].file = e.target.files[0];
      }
    }
    setTestcaseDetailList(arr);
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
        setTestcaseDetailList(tcl);
      },
      {
        401: () => {},
      }
    ).then(() => setIsProcessing(false));
  }

  useEffect(() => {
    getSubmissionDetailTestCaseByTestCase();
  }, []);

  const ModalPreview = ({ testCase }) => {
    return (
      <HustModal
        open={openModal}
        onClose={() => setOpenModal(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
        maxWidthPaper={800}
      >
        <HustCopyCodeBlock title="Input" text={testCase?.testCase} />
        <Box
          sx={{
            display: "flex",
            flexDirection: "row",
            width: "100%",
            justifyContent: "space-between",
            marginTop: "14px",
          }}
        >
          <Box width="48%">
            <HustCopyCodeBlock
              title="Correct output"
              text={testCase?.testCaseAnswer}
            />
          </Box>
          <Box width="48%">
            <HustCopyCodeBlock
              title="User output"
              text={testCase?.participantAnswer}
            />
          </Box>
        </Box>
      </HustModal>
    );
  };

  return (
    <Box>
      {isProcessing && <LinearProgress />}
      <StandardTable
        columns={columns}
        data={submissionTestCase}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: false,
          sorting: true,
        }}
      />
      <ModalPreview testCase={selectedTestcase} />
    </Box>
  );
}
