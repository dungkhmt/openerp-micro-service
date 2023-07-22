import React, {useEffect, useState} from "react";
import {Box, IconButton} from "@mui/material";
import ContestManagerViewSubmissionOfAUserDialog from "./ContestManagerViewSubmissionOfAUserDialog";
import ManagerSubmitCodeOfParticipant from "./ManagerSubmitCodeOfParticipant";
import {request} from "../../../api";
import StandardTable from "component/table/StandardTable";
import HustModal from "component/common/HustModal";
import {Link} from "react-router-dom";
import {getStatusColor} from "./lib";
import {successNoti} from "../../../utils/notification";
import {LoadingButton} from "@mui/lab";
import CodeIcon from '@mui/icons-material/Code';
import VisibilityIcon from '@mui/icons-material/Visibility';
import {pdf} from "@react-pdf/renderer";
import SubmissionOfParticipantPDFDocument from "./template/SubmissionOfParticipantPDFDocument";
import FileSaver from "file-saver";

export default function ContestManagerUserSubmission(props) {
  const contestId = props.contestId;

  const [contestSubmissions, setContestSubmissions] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [isOpen, setIsOpen] = useState(false);
  const [
    isOpenManagerSubmitCodeOfParticipant,
    setIsOpenManagerSubmitCodeOfParticipant,
  ] = useState(false);

  const [filterParams, setFilterParams] = useState({page: 0, size: 10});
  const [totalSizeSubmission, setTotalSizeSubmission] = useState(0);

  const [isProcessing, setIsProcessing] = useState(false);

  function handleCloseManagerSubmitParticipantCode() {
    setIsOpenManagerSubmitCodeOfParticipant(false);
    getSubmission(filterParams.size, 0);
  }

  function handleCloseDialog() {
    setIsOpen(false);
  }

  function getSubmission(s, p) {
    request(
      "get",
      "/get-contest-submission-paging/" +
      contestId +
      "?size=" +
      filterParams.size +
      "&page=" +
      filterParams.page,
      (res) => {
        setContestSubmissions(res.data.content);
        setTotalSizeSubmission(res.data.totalElements)
      }
    ).then();
  }

  function handleRejudgeAll() {
    setIsProcessing(true);
    request(
      "post",
      "/evaluate-batch-submission-of-contest/" + contestId,
      (res) => {
        setIsProcessing(false);
        successNoti("Submissions will be rejudged", 5000)
      }
    ).then();
  }

  function handleJudgeAll() {
    setIsProcessing(true);
    request(
      "post",
      "/evaluate-batch-not-evaluated-submission-of-contest/" + contestId,
      (res) => {
        setIsProcessing(false);
        successNoti("Submissions will be judged", 5000)
      }
    ).then();
  }

  const generatePdfDocument = async (documentData, fileName) => {
    const blob = await pdf(
      <SubmissionOfParticipantPDFDocument data={documentData}/>
    ).toBlob();

    FileSaver.saveAs(blob, fileName);
  };

  function handleExportParticipantSubmission() {
    setIsProcessing(true);
    request(
      "get",
      "/get-user-judged-problem-submission/" + contestId,
      (res) => {
        generatePdfDocument(
          res.data,
          `USER_JUDGED_SUBMISSION-${contestId}.pdf`
        );
      }
    ).then(() => setIsProcessing(false));
  }

  const generateColumns = () => {
    const columns = [
      {
        title: "ID",
        field: "contestSubmissionId",
        render: (rowData) => (
          <Link
            to={
              "/programming-contest/manager-view-contest-problem-submission-detail/" +
              rowData.contestSubmissionId
            }
          >
            {rowData.contestSubmissionId.substring(0, 6)}
          </Link>
        )
      },
      {title: "User ID", field: "userId"},
      {title: "FullName", field: "fullname"},
      {title: "Problem ID", field: "problemId"},
      {title: "Testcases Passed", field: "testCasePass"},
      {title: "Lang", field: "sourceCodeLanguage"},
      {
        title: "Status",
        field: "status",
        render: (rowData) => (
          <span
            style={{color: getStatusColor(`${rowData.status}`)}}
          >
            {`${rowData.status}`}
          </span>
        )
      },
      {title: "Message", field: "message"},
      {title: "Point", field: "point"},
      {title: "Submitted At", field: "createAt"},
      {
        title: "Rejudge",
        sortable: "false",
        render: (rowData) => (
          <IconButton
            variant="contained"
            color="primary"
            onClick={() => {
              handleRejudge(rowData.contestSubmissionId);
            }}
          >
            <CodeIcon/>
          </IconButton>
        )
      },
      {
        title: "View By User",
        sortable: false,
        render: (rowData) => (
          <IconButton
            variant="contained"
            color="success"
            onClick={() => {
              setSelectedUserId(rowData.userId);
              setIsOpen(true);
            }}
          >
            <VisibilityIcon/>
          </IconButton>
        )
      },
    ]
    return columns;
  }

  function handleRejudge(submissionId) {
    //alert("rejudge submission " + submissionId);
    request("post", "/evaluate-submission/" + submissionId, (res) => {
      console.log("evaluate submission", res.data);
    }).then();
  }

  function handleSubmitCodeParticipant() {
    setIsOpenManagerSubmitCodeOfParticipant(true);
  }

  useEffect(() => {
    getSubmission(filterParams.size, 0);
  }, []);

  useEffect(() => {
    getSubmission(filterParams.size, filterParams.page);
  }, [filterParams]);

  return (
    <Box sx={{marginTop: "12px"}}>
      <StandardTable
        title={"User Submissions"}
        columns={generateColumns()}
        data={contestSubmissions}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: false,
          sorting: false,
        }}
        page={filterParams.page}
        totalCount={totalSizeSubmission}
        onChangePage={(page, size) => setFilterParams({...filterParams, page, size})}
        actions={[
          {
            icon: () => {
              return (
                <LoadingButton loading={isProcessing} loadingPosition="center" variant="contained"
                               onClick={handleSubmitCodeParticipant} color="primary">
                  Submit Participant Code
                </LoadingButton>
              )
            },
            tooltip: 'Submit code as a participant',
            isFreeAction: true
          },
          {
            icon: () => {
              return (
                <LoadingButton loading={isProcessing} loadingPosition="center" variant="contained"
                               onClick={handleJudgeAll} color="primary">
                  Judge All
                </LoadingButton>
              )
            },
            tooltip: 'Judge all submissions that are NOT EVALUATED',
            isFreeAction: true
          },
          {
            icon: () => {
              return (
                <LoadingButton loading={isProcessing} loadingPosition="start" variant="contained"
                               onClick={handleRejudgeAll} color="primary">
                  Rejudge All
                </LoadingButton>
              )
            },
            tooltip: 'Rejudge all submissions in this contest',
            isFreeAction: true
          },
          {
            icon: () => {
              return (
                <LoadingButton loading={isProcessing} loadingPosition="start" variant="contained"
                               onClick={handleExportParticipantSubmission} color="primary">
                  Export
                </LoadingButton>
              )
            },
            tooltip: 'Export all submissions in this contest',
            isFreeAction: true
          }
        ]}
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
        textOk={'OK'}
        onClose={handleCloseManagerSubmitParticipantCode}
        title={'Submit code of participant'}
      >
        <ManagerSubmitCodeOfParticipant
          contestId={contestId}
        />
      </HustModal>
    </Box>
  );
}
