import InfoIcon from "@mui/icons-material/Info";
import {CircularProgress, IconButton} from "@mui/material";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import MaterialTable from "material-table";
import {React, useEffect, useState} from "react";
import {request} from "../../../api";
import {toFormattedDateTime} from "../../../utils/dateutils";
import Box from "@mui/material/Box";

export default function ParticipantProgramSubmissionDetailTestCaseByTestCase(
  props
) {
  const {submissionId} = props;
  const [submissionTestCase, setSubmissionTestCase] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [testcaseDetailList, setTestcaseDetailList] = useState([]);
  const [selectedTestcase, setSelectedTestcase] = useState();

  const [isProcessing, setIsProcessing] = useState(false);

  const [score, setScore] = useState(0);

  const columns = [
    {title: "Contest", field: "contestId"},
    {title: "Problem", field: "problemId"},
    {title: "Message", field: "message"},
    {title: "Point", field: "point"},
    // {title: "Correct result", field: "testCaseAnswer"},
    // {title: "Participant's result", field: "participantAnswer"},
    {title: "Submit at", field: "createdAt"},
    {
      title: "Detail",
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
          <InfoIcon/>
        </IconButton>
      ),
    },
    {
      title: "",
      render: (rowData) =>
        rowData.viewSubmitSolutionOutputMode == "Y" ? (
          <div>
            {isProcessing ? <CircularProgress/> : ""}
            <button
              color="primary"
              type="submit"
              //onChange={onInputChange}
              onClick={() => handleFormSubmit(event, rowData.testCaseId)}
              width="100%"
            >
              UPLOAD
            </button>

            <input
              type="file"
              id="selected-upload-file"
              onChange={() => onFileChange(event, rowData.testCaseId)}
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
        //alert(
        //  "upload solution output for test case " +
        //    testCaseId +
        //    " file " +
        //    testcaseDetailList[i].file.name
        // );
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
      "/submit-solution-output-of-testcase",
      (res) => {
        res = res.data;
        setIsProcessing(false);
        console.log("result submit = ", res);
        //alert("submit solution output OK");
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
          setIsProcessing(false);
          console.error(e);
        },
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
      "/get-contest-problem-submission-detail-by-testcase-of-a-submission-viewed-by-participant/" +
      submissionId,
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
        401: () => {
        },
      }
    );
  }

  function getTestcaseDetail(testcaseId) {
    request(
      "get",
      //"/get-test-case-detail/" + testcaseId,
      "/get-test-case-detail-short/" + testcaseId,
      (res) => {
        setTestcaseDetailList((prev) => [...prev, res.data]);
      },
      {
        401: () => {
        },
      }
    );
  }

  useEffect(() => {
    getSubmissionDetailTestCaseByTestCase();
  }, []);

  /*
  useEffect(() => {
    var testcaseIdsList = submissionTestCase.map(
      (testcase) => testcase.testCaseId
    );
    testcaseIdsList.forEach((id) => {
      getTestcaseDetail(id);
    });
  }, [submissionTestCase]);
  */

  const ModalPreview = (chosenTestcase) => {
    return (
      <HustModal
        open={openModal}
        onClose={() => setOpenModal(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
        maxWidthPaper={800}
      >
        <HustCopyCodeBlock
          title="Input"
          text={chosenTestcase?.chosenTestcase?.testCase}
        />
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
              text={chosenTestcase?.chosenTestcase?.testCaseAnswer}
            />
          </Box>
          <Box width="48%">
            <HustCopyCodeBlock
              title="User output"
              text={chosenTestcase?.chosenTestcase?.participantAnswer}
            />
          </Box>
        </Box>
      </HustModal>
    );
  };

  return (
    <div>
      <MaterialTable
        title={"Problem's Test cases"}
        columns={columns}
        data={submissionTestCase}
      />
      <ModalPreview chosenTestcase={selectedTestcase}/>
    </div>
  );
}
