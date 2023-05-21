import React, {useEffect, useState} from "react";
import {Button} from "@mui/material";
import ContestManagerViewSubmissionOfAUserDialog from "./ContestManagerViewSubmissionOfAUserDialog";
import ManagerSubmitCodeOfParticipant from "./ManagerSubmitCodeOfParticipant";
import {request} from "../../../api";
import StandardTable from "component/table/StandardTable";
import HustModal from "component/common/HustModal";
import {Link} from "react-router-dom";
import {getStatusColor} from "./lib";

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
            {rowData.contestSubmissionId.substring(0,6)}
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
        render: (rowData) => (
          <Button
            variant="contained"
            color="success"
            onClick={() => {
              handleRejudge(rowData.contestSubmissionId);
            }}
          >
            {" "}
            REJUDGE{" "}
          </Button>
        )
      },
      {
        render: (rowData) => (
          <Button
            variant="contained"
            color="primary"
            onClick={() => {
              setSelectedUserId(rowData.userId);
              setIsOpen(true);
            }}
          >
            {" "}
            ViewByUser{" "}
          </Button>
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
    <div>
      <section id={"#submission"}>
        <Button
          variant="contained"
          onClick={handleSubmitCodeParticipant}
          sx={{marginBottom: "12px"}}
        >
          Submit Code Of Participant
        </Button>
      </section>

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
    </div>
  );
}
