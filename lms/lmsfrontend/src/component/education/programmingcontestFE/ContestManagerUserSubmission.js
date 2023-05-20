import React, {useEffect, useState} from "react";
import Typography from "@mui/material/Typography";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@mui/material/Table";
import {Link} from "react-router-dom";
import {Button, Grid, MenuItem, TableHead, TextField,} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow";
import TableBody from "@mui/material/TableBody";
import Pagination from "@material-ui/lab/Pagination";
import {getStatusColor, StyledTableCell, StyledTableRow} from "./lib";
import ContestManagerViewSubmissionOfAUserDialog from "./ContestManagerViewSubmissionOfAUserDialog";
import ManagerSubmitCodeOfParticipantDialog from "./ManagerSubmitCodeOfParticipantDialog";
import ManagerSubmitCodeOfParticipant from "./ManagerSubmitCodeOfParticipant";
import {request} from "../../../api";
import StandardTable from "component/table/StandardTable";
import HustModal from "component/common/HustModal";

export default function ContestManagerUserSubmission(props) {
  const contestId = props.contestId;

  const [contestSubmissions, setContestSubmissions] = useState([]);
  const [pageSubmissionSize, setPageSubmissionSize] = useState(10);
  const [totalPageSubmission, setTotalPageSubmission] = useState(0);
  const [pageSubmission, setPageSubmission] = useState(1);
  const pageSizes = [10, 20, 50, 100, 150];
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [isOpen, setIsOpen] = useState(false);
  const [userId, setUserId] = useState(null);
  const [
    isOpenManagerSubmitCodeOfParticipant,
    setIsOpenManagerSubmitCodeOfParticipant,
  ] = useState(false);

  function handleCloseManagerSubmitParticipantCode() {
    setIsOpenManagerSubmitCodeOfParticipant(false);
    getSubmission(pageSubmissionSize, 1);
  }
  function handleCloseDialog() {
    setIsOpen(false);
  }
  const handlePageSubmissionSizeChange = (event) => {
    setPageSubmissionSize(event.target.value);
    setPageSubmission(1);
    getSubmission(event.target.value, 1);
  };
  function getSubmission(s, p) {
    request(
      "get",
      "/get-contest-submission-paging/" +
        contestId +
        "?size=" +
        s +
        "&page=" +
        (p - 1),
      (res) => {
        console.log("res submission", res.data);
        setContestSubmissions(res.data.content);
        console.log("contest submission", contestSubmissions);
        setTotalPageSubmission(res.data.totalPages);
      }
    ).then();
  }

  const generateColumns = () => {
    const columns = [
      {title: "Submission ID", field: "contestSubmissionId"},
      {title: "UserID", field: "userId"},
      {title: "FullName", field: "fullname"},
      {title: "Problem Id", field: "problemId"},
      {title: "Test Case Pass", field: "testCasePass"},
      {title: "Lang", field: "sourceCodeLanguage"},
      {title: "Status", field: "status"},
      {title: "Message", field: "message"},
      {title: "Point", field: "point"},
      {title: "Submitted At", field: "submissionDate"},
      {title: "Action", render: (rowData) => (
        <Button
          onClick={() => {
            handleRejudge(rowData.contestSubmissionId);
          }}
        >
          {" "}
          REJUDGE{" "}
        </Button>
      )},
      {title: "Action", render: (rowData) => (
        <Button
          onClick={() => {
            setSelectedUserId(rowData.userId);
            setIsOpen(true);
          }}
        >
          {" "}
          ViewByUser{" "}
        </Button>
      )},
    ]
    return columns;
  }

  function handleRejudge(submissionId) {
    //alert("rejudge submission " + submissionId);
    request("post", "/evaluate-submission/" + submissionId, (res) => {
      console.log("evaluate submission", res.data);
    }).then();
  }
  function ViewAllSubmissions() {
    alert("view all submission");
  }
  function getSubmissionOfUser() {
    //alert('view submission of user ' + userId);
    request(
      "get",
      "/get-contest-submission-of-a-user-paging/" +
        contestId +
        "/" +
        userId +
        "?size=" +
        pageSubmissionSize +
        "&page=" +
        (pageSubmission - 1),
      (res) => {
        console.log("res submission", res.data);
        setContestSubmissions(res.data.content);
        console.log("contest submission", contestSubmissions);
        setTotalPageSubmission(res.data.totalPages);
      }
    ).then();
  }

  function handleSubmitCodeParticipant() {
    setIsOpenManagerSubmitCodeOfParticipant(true);
  }
  useEffect(() => {
    getSubmission(pageSubmissionSize, 1);
  }, []);
  return (
    <div>
      <section id={"#submission"}>
        <Typography
          variant="h5"
          component="h2"
          style={{ marginTop: 10, marginBottom: 10 }}
        >
          User Submission
        </Typography>

        <Button
          variant="contained"
          color="secondary"
          onClick={handleSubmitCodeParticipant}
        >
          Submit Code Of Participant
        </Button>
      </section>

      <StandardTable
          // title={"Contest Ranking"}
        columns={generateColumns()}
        data={contestSubmissions}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
      />

      <Grid container spacing={12}>
        <Grid item xs={6}>
          <TextField
            variant={"outlined"}
            autoFocus
            size={"small"}
            required
            select
            id="pageSize"
            value={pageSubmissionSize}
            onChange={handlePageSubmissionSizeChange}
          >
            {pageSizes.map((item) => (
              <MenuItem key={item} value={item}>
                {item}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item>
          <Pagination
            className="my-3"
            count={totalPageSubmission}
            page={pageSubmission}
            siblingCount={1}
            boundaryCount={1}
            variant="outlined"
            shape="rounded"
            onChange={(event, value) => {
              setPageSubmission(value);
              getSubmission(pageSubmissionSize, value);
            }}
          />
        </Grid>
      </Grid>
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
          onClose={handleCloseManagerSubmitParticipantCode}
        />
      </HustModal>
    </div>
  );
}
