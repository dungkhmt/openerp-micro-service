import InfoIcon from "@mui/icons-material/Info";
import { IconButton } from "@mui/material";
import Box from "@mui/material/Box";
import { request } from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { localeOption } from "utils/NumberFormat";
import { toFormattedDateTime } from "utils/dateutils";

export default function ManagerViewParticipantProgramSubmissionDetailTestCaseByTestCase(
  props
) {
  const { submissionId } = props;
  const [submissionTestCase, setSubmissionTestCase] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  // const [testcaseDetailList, setTestcaseDetailList] = useState([]);
  const [selectedTestcase, setSelectedTestcase] = useState();

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
        rowData.runtime.toLocaleString("fr-FR", localeOption),
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
            // for (let i = 0; i < testcaseDetailList.length; i++) {
            //   if (testcaseDetailList[i].testCaseId === rowData.testCaseId) {
            //     testcaseDetailList[i].participantAnswer =
            //       rowData.participantAnswer;
            //     setSelectedTestcase(testcaseDetailList[i]);

            //   }
            // }

            setSelectedTestcase({
              testcase: rowData.testCase,
              correctAns: rowData.testCaseAnswer,
              participantAnswer: rowData.participantAnswer,
            });
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

  // function getTestcaseDetail(testcaseId) {
  //   request(
  //     "get",
  //     "/testcases/" + testcaseId,
  //     (res) => {
  //       setTestcaseDetailList((prev) => [...prev, res.data]);
  //     },
  //     {
  //       401: () => {},
  //     }
  //   );
  // }

  useEffect(() => {
    getSubmissionDetailTestCaseByTestCase();
  }, []);

  // useEffect(() => {
  //   var testcaseIdsList = submissionTestCase.map(
  //     (testcase) => testcase.testCaseId
  //   );
  //   testcaseIdsList.forEach((id) => {
  //     getTestcaseDetail(id);
  //   });
  // }, [submissionTestCase]);

  const ModalPreview = ({ testcase }) => {
    return (
      <HustModal
        open={openModal}
        onClose={() => setOpenModal(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
        maxWidthPaper={800}
      >
        <HustCopyCodeBlock title="Input" text={testcase?.testcase} mb={2} />
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
              text={testcase?.correctAns}
            />
          </Box>
          <Box width="48%">
            <HustCopyCodeBlock
              title="User output"
              text={testcase?.participantAnswer}
            />
          </Box>
        </Box>
      </HustModal>
    );
  };

  return (
    <div>
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
      <ModalPreview testcase={selectedTestcase} />
    </div>
  );
}
