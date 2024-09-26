import React, { useEffect, useState } from "react";
import { request } from "../../../../api";
import { Link as RouterLink, useHistory } from "react-router-dom";
import { Button } from "@mui/material";
import StandardTable from "../../../table/StandardTable";
// import CreateLearningSessionDlg from "./CreateLearningSessionDlg";
import PropTypes from "prop-types";
import { Link, Typography } from "@material-ui/core/";
import CreateCourseSessionDialog from "./CreateCourseSessionDialog";

export default function LearningSessionListOfCourse(props) {
  const history = useHistory();
  const courseId = props.courseId;
  const [learningSessionsOfCourse, setLearningSessionsOfCourse] = useState([]);
  const [createLearningSessionDlgOpen, setCreateLearningSessionDlgOpen] =
    useState(false);

  useEffect(getLearningSessionsOfCourse, []);

  function getLearningSessionsOfCourse() {
    request("GET", `/edu/course/get-course-sessions/${courseId}`, (res) => {
      setLearningSessionsOfCourse(res.data);
    });
  }

  function navigateToLearningSessionDetailPage(learningSessionId) {
    // history.push(`/edu/teacher/class/session/detail/${learningSessionId}`);
  }

  function updateLearningSessionsWhenCreateSuccess(res) {
    setLearningSessionsOfCourse([...learningSessionsOfCourse, res.data]);
  }

  const columns = [
    {
      title: "Tên buổi học",
      field: "sessionId",
      render: (rowData) => (
        <Link
          component={RouterLink}
          to={`/edu/teacher/course/session/detail/${rowData["id"]}`}
        >
          {rowData["sessionName"]}
        </Link>
      ),
    },
    { title: "Mô tả", field: "description" },
    { title: "Người tạo", field: "createdByUserLoginId" },
    { title: "Ngày tạo", field: "createdStamp" },
  ];
  const actions = props.enableCreateSession
    ? [{ icon: () => CreateLearningSessionButton, isFreeAction: true }]
    : [];

  const CreateLearningSessionButton = (
    <Button
      variant="outlined"
      onClick={() => setCreateLearningSessionDlgOpen(true)}
    >
      Thêm mới
    </Button>
  );

  const onRowClick = props.viewDetailOnRowClick
    ? (event, session) => navigateToLearningSessionDetailPage(session.sessionId)
    : null;

  return (
    <div>
      <StandardTable
        title="Danh sách buổi học"
        columns={columns}
        data={learningSessionsOfCourse}
        hideCommandBar
        options={{
          selection: false,
          search: true,
          sorting: true,
        }}
        onRowClick={onRowClick}
        actions={actions}
      />

      {props.enableCreateSession && (
        <CreateCourseSessionDialog
          courseId={courseId}
          open={createLearningSessionDlgOpen}
          setOpen={setCreateLearningSessionDlgOpen}
          onCreateSuccess={updateLearningSessionsWhenCreateSuccess}
        />
      )}
    </div>
  );
}

LearningSessionListOfCourse.propTypes = {
  courseId: PropTypes.string.isRequired,
  enableCreateSession: PropTypes.bool,
  viewDetailOnRowClick: PropTypes.bool,
};

LearningSessionListOfCourse.defaultProps = {
  enableCreateSession: false,
  viewDetailOnRowClick: false,
};
