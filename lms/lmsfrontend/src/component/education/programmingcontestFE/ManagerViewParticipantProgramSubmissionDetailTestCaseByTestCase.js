import InfoIcon from "@mui/icons-material/Info";
import { IconButton } from "@mui/material";
import Box from "@mui/material/Box";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { toFormattedDateTime } from "../../../utils/dateutils";
import StandardTable from "../../table/StandardTable";

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
    { title: "Submit at", field: "createdAt" },
    {
      title: "",
      render: (rowData) => (
        <IconButton
          color="primary"
          onClick={() => {
            for (let i = 0; i < testcaseDetailList.length; i++) {
              if (testcaseDetailList[i].testCaseId === rowData.testCaseId) {
                testcaseDetailList[i].participantAnswer =
                  rowData.participantAnswer;
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
      "/teacher/submissions/" + submissionId,
      (res) => {
        let L = res.data.map((c) => ({
          ...c,
          createdAt: toFormattedDateTime(c.createdAt),
        }));
        setSubmissionTestCase(L);
      },
      {
        401: () => {},
      }
    );
  }

  function getTestcaseDetail(testcaseId) {
    request(
      "get",
      "/testcases/" + testcaseId,
      (res) => {
        setTestcaseDetailList((prev) => [...prev, res.data]);
      },
      {
        401: () => {},
      }
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
        maxWidthPaper={800}
      >
        <HustCopyCodeBlock
          title="Input"
          text={chosenTestcase?.chosenTestcase?.testCase}
          mb={2}
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
              text={chosenTestcase?.chosenTestcase?.correctAns}
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
      <StandardTable
        title={"Testcases"}
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
      <ModalPreview chosenTestcase={selectedTestcase} />
    </div>
  );
}
