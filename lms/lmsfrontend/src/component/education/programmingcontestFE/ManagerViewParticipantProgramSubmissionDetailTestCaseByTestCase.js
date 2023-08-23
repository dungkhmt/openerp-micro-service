import InfoIcon from "@mui/icons-material/Info";
import {IconButton} from "@mui/material";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import MaterialTable from "material-table";
import {React, useEffect, useState} from "react";
import {request} from "../../../api";
import {toFormattedDateTime} from "../../../utils/dateutils";

export default function ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase(
  props
) {
  const { submissionId } = props;
  const [submissionTestCase, setSubmissionTestCase] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [testcaseDetailList, setTestcaseDetailList] = useState([]);
  const [selectedTestcase, setSelectedTestcase] = useState();

  const columns = [
    { title: "Contest", field: "contestId" },
    { title: "Problem", field: "problemId" },
    { title: "Message", field: "message" },
    { title: "Point", field: "point" },
    { title: "Correct result", field: "testCaseAnswer" },
    { title: "Participant's result", field: "participantAnswer" },
    { title: "Submit at", field: "createdAt" },
    {
      title: "",
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
  ];

  function getSubmissionDetailTestCaseByTestCase() {
    request(
      "get",
      "/teacher/contests/submissions/" + submissionId,
      (res) => {
        let L = res.data.map((c) => ({
          ...c,
          createdAt: toFormattedDateTime(c.createdAt),
        }));
        setSubmissionTestCase(L);
      },
      { 401: () => {} }
    );
  }

  function getTestcaseDetail(testcaseId) {
    request(
      "get",
      "/testcases/" + testcaseId,
      (res) => {
        setTestcaseDetailList((prev) => [...prev, res.data]);
      },
      { 401: () => {} }
    );
  }

  useEffect(() => {
    getSubmissionDetailTestCaseByTestCase();
  }, []);

  useEffect(() => {
    var testcaseIdsList = submissionTestCase.map(
      (testcase) => testcase.testCaseId
    );
    testcaseIdsList.forEach((id) => {
      getTestcaseDetail(id);
    });
  }, [submissionTestCase]);

  const ModalPreview = (chosenTestcase) => {
    return (
      <HustModal
        open={openModal}
        onClose={() => setOpenModal(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
      >
        <HustCopyCodeBlock
          title="Input"
          text={chosenTestcase?.chosenTestcase?.testCase}
        />
        <HustCopyCodeBlock
          title="Output"
          text={chosenTestcase?.chosenTestcase?.correctAns}
          mt={2}
        />
      </HustModal>
    );
  };

  return (
    <div>
      <MaterialTable
        title={"Detail"}
        columns={columns}
        data={submissionTestCase}
      />
      <ModalPreview chosenTestcase={selectedTestcase} />
    </div>
  );
}
