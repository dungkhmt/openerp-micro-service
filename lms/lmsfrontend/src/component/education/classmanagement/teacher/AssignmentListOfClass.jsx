import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
import {defaultDatetimeFormat} from "../../../../utils/dateutils";
import StandardTable from "../../../table/StandardTable";
import moment from "moment";
import {useHistory} from "react-router-dom";
import {Button} from "@mui/material";
import {makeStyles} from "@material-ui/core/styles";
import PropTypes from "prop-types";

const useStyles = makeStyles(theme => ({
  assignmentsTableContainer: {
    '& button:has(span > .create-assignment-btn):hover': {
      backgroundColor: 'unset'
    },
    '& span:has(.create-assignment-btn) + span': {
      display: 'none'
    }
  }
}))

export default function AssignmentListOfClass(props) {
  const classes = useStyles();
  const history = useHistory();
  const classId = props.classId;

  const [assignments, setAssignments] = useState([]);

  useEffect(getAssignments, []);

  function getAssignments() {
    request("GET", `/edu/class/${classId}/assignments/teacher`, (res) => {
      let assignments = res.data;
      assignments.forEach(setAssignmentStatus);
      setAssignments(assignments);
    });
  }

  function navigateToAssignmentDetailPage(assignmentId) {
    history.push(`/edu/${props.userRole}/class/${classId}/assignment/${assignmentId}`);
  }

  function navigateToCreateAssignmentPage() {
    history.push(`/edu/teacher/class/${classId}/assignment/create`);
  }

  const columns = [
    { field: "name", title: "Tên bài tập" },
    {
      field: "openTime", title: "Thời gian bắt đầu",
      render: assignment => defaultDatetimeFormat(assignment.openTime),
    },
    {
      field: "closeTime", title: "Thời gian kết thúc",
      render: assignment => defaultDatetimeFormat(assignment.closeTime)
    },
    { field: "status",  title: "Trạng thái", lookup: ASSIGNMENT_STATUSES }
  ];

  const actions = props.enableCreateAssignment ?
    [{ icon: () => CreateNewAssignmentButton, isFreeAction: true }] : [];

  const CreateNewAssignmentButton = (
    <Button variant="outlined"
            className="create-assignment-btn"
            onClick={navigateToCreateAssignmentPage}>
      Tạo mới
    </Button>
  )

  return (
    <div className={classes.assignmentsTableContainer}>
      <StandardTable  title="Danh sách bài tập"
                      columns={columns}
                      data={assignments}
                      hideCommandBar
                      options={{
                        selection: false,
                        search: true,
                        sorting: true
                      }}
                      onRowClick={ (event, assignment) => navigateToAssignmentDetailPage(assignment.id) }
                      actions={actions}/>
    </div>
  )
}

const ASSIGNMENT_STATUSES = {
  CLOSED: "Đã hết hạn",
  STARTED: "Đã giao",
  DELETED: "Đã xóa",
  'NOT-STARTED': "Chưa giao",
}

function setAssignmentStatus(assignment) {
  let now = moment();
  let openTime = moment(assignment.openTime);
  let closeTime = moment(assignment.closeTime);

  if (assignment.deleted) {
    assignment.status = 'DELETED';
  } else if (now.isBefore(openTime)) {
    assignment.status = 'NOT-STARTED';
  } else if (now.isAfter(closeTime)) {
    assignment.status = 'CLOSED';
  } else {
    assignment.status = 'STARTED';
  }
}

AssignmentListOfClass.propTypes = {
  classId: PropTypes.string.isRequired,
  userRole: PropTypes.oneOf<String>(['student', 'teacher']),
  enableCreateAssignment: PropTypes.bool,
}

AssignmentListOfClass.defaultProps = {
  userRole: 'student',
  enableCreateAssignment: false
}
