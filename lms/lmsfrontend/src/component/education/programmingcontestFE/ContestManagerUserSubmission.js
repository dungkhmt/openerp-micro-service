import { MuiThemeProvider } from "@material-ui/core/styles";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { LoadingButton } from "@mui/lab";
import { Box, IconButton, Tooltip, LinearProgress } from "@mui/material";
import { pdf } from "@react-pdf/renderer";
import { request } from "api";
import HustModal from "component/common/HustModal";
import StandardTable from "component/table/StandardTable";
import FileSaver from "file-saver";
import { MTableToolbar } from "material-table";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { errorNoti, infoNoti, successNoti } from "utils/notification";
import ContestManagerViewSubmissionOfAUserDialog from "./ContestManagerViewSubmissionOfAUserDialog";
import ManagerSubmitCodeOfParticipant from "./ManagerSubmitCodeOfParticipant";
import { RejudgeButton } from "./RejudgeButton";
import { getStatusColor } from "./lib";
import SubmissionOfParticipantPDFDocument from "./template/SubmissionOfParticipantPDFDocument";

export default function ContestManagerUserSubmission(props) {
  const contestId = props.contestId;

  const [loading, setLoading] = useState(true);
  const [contestSubmissions, setContestSubmissions] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [isOpen, setIsOpen] = useState(false);
  const [
    isOpenManagerSubmitCodeOfParticipant,
    setIsOpenManagerSubmitCodeOfParticipant,
  ] = useState(false);

  const [filterParams, setFilterParams] = useState({
    page: 0,
    size: 10,
    search: "",
  });
  const [totalSizeSubmission, setTotalSizeSubmission] = useState(0);

  const [isProcessing, setIsProcessing] = useState(false);

  function handleCloseManagerSubmitParticipantCode() {
    setIsOpenManagerSubmitCodeOfParticipant(false);
    setFilterParams({
      page: 0,
      size: filterParams.size,
      search: filterParams.search,
    });
    getSubmission();
  }

  function handleCloseDialog() {
    setIsOpen(false);
  }

  function getSubmission() {
    setLoading(true);
    request(
      "get",
      "/teacher/contests/" + contestId + "/submissions",
      (res) => {
        setContestSubmissions(res.data.content);
        setTotalSizeSubmission(res.data.totalElements);
      },
      { onError: (error) => errorNoti("An error happened", 3000) },
      null,
      { params: filterParams }
    ).then(() => setLoading(false));
  }

  function handleRejudgeAll() {
    setIsProcessing(true);
    request(
      "post",
      "/submissions/" + contestId + "/batch-evaluation",
      (res) => {
        successNoti("Submissions will be rejudged", 5000);
      },
      {
        403: () => {
          infoNoti(
            "You don't have privilege to perform this action. Contact admin if needed",
            10000
          );
        },
      }
    ).then(() => setIsProcessing(false));
  }

  function handleJudgeAll() {
    setIsProcessing(true);
    request(
      "post",
      "/submissions/" + contestId + "/batch-non-evaluated-evaluation",
      (res) => {
        successNoti("Submissions will be judged", 5000);
      },
      {
        403: () => {
          infoNoti(
            "You don't have privilege to perform this action. Contact admin if needed",
            10000
          );
        },
      }
    ).then(() => setIsProcessing(false));
  }

  const generatePdfDocument = async (documentData, fileName) => {
    const blob = await pdf(
      <SubmissionOfParticipantPDFDocument data={documentData} />
    ).toBlob();

    FileSaver.saveAs(blob, fileName);
  };

  function handleExportParticipantSubmission() {
    setIsProcessing(true);
    request("get", "/contests/" + contestId + "/judged-submissions", (res) => {
      generatePdfDocument(res.data, `USER_JUDGED_SUBMISSION-${contestId}.pdf`);
    }).then(() => setIsProcessing(false));
  }

  const generateColumns = () => {
    const columns = [
      {
        title: "ID",
        field: "contestSubmissionId",
        cellStyle: { minWidth: "80px" },
        render: (rowData) => (
          <Link
            to={
              "/programming-contest/manager-view-contest-problem-submission-detail/" +
              rowData.contestSubmissionId
            }
          >
            {rowData.contestSubmissionId.substring(0, 6)}
          </Link>
        ),
      },
      { title: "User ID", field: "userId" },
      {
        title: "Full name",
        field: "fullname",
        cellStyle: { minWidth: "170px" },
      },

      {
        title: "Problem ID",
        field: "problemId",
        render: (rowData) => (
          <Tooltip title={rowData.problemName} placement="bottom-start" arrow>
            {rowData.problemId}
          </Tooltip>
        ),
      },
      // { title: "Problem Name", field: "problemName" },

      {
        title: "Testcases Passed",
        field: "testCasePass",
        // cellStyle: { textAlign: "center" },
      },
      { title: "Lang", field: "sourceCodeLanguage" },
      {
        title: "Status",
        field: "status",
        // cellStyle: { textAlign: "center" },
        render: (rowData) => (
          <span style={{ color: getStatusColor(`${rowData.status}`) }}>
            {`${rowData.status}`}
          </span>
        ),
      },
      // {title: "Message", field: "message"},
      { title: "Point", field: "point" },
      {
        title: "Submitted At",
        field: "createAt",
        cellStyle: {
          minWidth: "112px",
          // textAlign: "center"
        },
      },
      {
        title: "Rejudge",
        sortable: "false",
        headerStyle: { textAlign: "center" },
        cellStyle: { textAlign: "center" },
        render: (rowData) => (
          <RejudgeButton submissionId={rowData.contestSubmissionId} />
        ),
      },
      { title: "Man. Status", field: "managementStatus" },
      { title: "Violation", field: "violationForbiddenInstruction" },
      {
        title: "View By User",
        sortable: false,
        headerStyle: { textAlign: "center" },
        cellStyle: { textAlign: "center", minWidth: 96 },
        render: (rowData) => (
          <IconButton
            variant="contained"
            color="success"
            onClick={() => {
              setSelectedUserId(rowData.userId);
              setIsOpen(true);
            }}
          >
            <VisibilityIcon />
          </IconButton>
        ),
      },
    ];
    return columns;
  };

  function handleSubmitCodeParticipant() {
    setIsOpenManagerSubmitCodeOfParticipant(true);
  }

  useEffect(() => {
    getSubmission();
  }, []);

  useEffect(() => {
    getSubmission();
  }, [filterParams]);

  return (
    <Box sx={{ marginTop: "12px" }}>
      {loading && <LinearProgress />}
      <StandardTable
        title={"Contest Submissions"}
        columns={generateColumns()}
        data={contestSubmissions}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: false,
          searchText: filterParams.search,
          debounceInterval: 800,
        }}
        localization={{
          toolbar: {
            searchPlaceholder: "Search by UserID or ProblemID",
          },
        }}
        page={filterParams.page}
        totalCount={totalSizeSubmission}
        onChangePage={(page, size) =>
          setFilterParams({ ...filterParams, page, size })
        }
        onSearchChange={(search) =>
          setFilterParams({ page: 0, size: filterParams.size, search })
        }
        components={{
          Toolbar: (props) => (
            <div>
              <MTableToolbar {...props} searchFieldStyle={{ width: 320 }} />
              <MuiThemeProvider>
                <Box
                  display="flex"
                  justifyContent="flex-end"
                  width="100%"
                  sx={{ padding: "8px 0 16px 16px" }}
                >
                  <Tooltip title="Submit code as a participant" arrow>
                    <LoadingButton
                      loading={isProcessing}
                      loadingPosition="start"
                      variant="contained"
                      sx={{ marginRight: "16px" }}
                      color="primary"
                      onClick={handleSubmitCodeParticipant}
                    >
                      Submit Participant Code
                    </LoadingButton>
                  </Tooltip>
                  <Tooltip
                    title="Judge all submissions that are NOT EVALUATED"
                    arrow
                  >
                    <LoadingButton
                      loading={isProcessing}
                      loadingPosition="start"
                      variant="contained"
                      sx={{ marginRight: "16px" }}
                      color="primary"
                      onClick={handleJudgeAll}
                    >
                      Judge All
                    </LoadingButton>
                  </Tooltip>
                  <Tooltip
                    title="Rejudge all submissions in this contest"
                    arrow
                  >
                    <LoadingButton
                      loading={isProcessing}
                      loadingPosition="start"
                      variant="contained"
                      sx={{ marginRight: "16px" }}
                      color="primary"
                      onClick={handleRejudgeAll}
                    >
                      Rejudge All
                    </LoadingButton>
                  </Tooltip>
                  <Tooltip title="Export all submissions in this contest" arrow>
                    <LoadingButton
                      loading={isProcessing}
                      loadingPosition="start"
                      variant="contained"
                      sx={{ marginRight: "16px" }}
                      color="primary"
                      onClick={handleExportParticipantSubmission}
                    >
                      Export
                    </LoadingButton>
                  </Tooltip>
                </Box>
              </MuiThemeProvider>
            </div>
          ),
        }}
      />

      <ContestManagerViewSubmissionOfAUserDialog
        open={isOpen}
        onClose={handleCloseDialog}
        contestId={contestId}
        userId={selectedUserId}
      />
      {/* <ManagerSubmitCodeOfParticipantDialog
        open={isOpenManagerSubmitCodeOfParticipant}
        onClose={handleCloseManagerSubmitParticipantCode}
        contestId={contestId}
      /> */}
      <HustModal
        open={isOpenManagerSubmitCodeOfParticipant}
        textOk={"OK"}
        onClose={handleCloseManagerSubmitParticipantCode}
        title={"Submit code of participant"}
      >
        <ManagerSubmitCodeOfParticipant contestId={contestId} />
      </HustModal>
    </Box>
  );
}
