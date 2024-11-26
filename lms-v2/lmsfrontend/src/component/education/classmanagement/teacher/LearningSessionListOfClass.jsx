import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
import {Link as RouterLink, useHistory} from "react-router-dom";
import {Button} from "@mui/material";
import StandardTable from "../../../table/StandardTable";
import CreateLearningSessionDlg from "./CreateLearningSessionDlg";
import PropTypes from "prop-types";
import {Link, Typography} from "@material-ui/core/";

export default function LearningSessionListOfClass(props) {
  const history = useHistory();
  const classId = props.classId;
  const [learningSessionsOfClass, setLearningSessionsOfClass] = useState([]);
  const [createLearningSessionDlgOpen, setCreateLearningSessionDlgOpen] =
    useState(false);

  useEffect(getLearningSessionsOfClass, []);

  function getLearningSessionsOfClass() {
    request("GET", `/edu/class/get-sessions-of-class/${classId}`, (res) => {
      setLearningSessionsOfClass(res.data);
    });
  }

  function navigateToLearningSessionDetailPage(learningSessionId) {
    history.push(`/edu/teacher/class/session/detail/${learningSessionId}`);
  }

  function updateLearningSessionsWhenCreateSuccess(res) {
    console.log("Newly created learning sessions");
    setLearningSessionsOfClass([...learningSessionsOfClass, res.data]);
  }

  const columns = [
    {
      title: "Tên buổi học",
      field: "sessionId",
      render: (rowData) => (
        <Link
          component={RouterLink}
          to={`/edu/student/class/session/detail/${rowData["sessionId"]}`}
        >
          {rowData["sessionName"]}
        </Link>
      ),
    },
    { title: "Mô tả", field: "description" },
    { title: "Người tạo", field: "createdByUserLoginId" },
    { title: "Trạng thái", field: "statusId" },
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
        data={learningSessionsOfClass}
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
        <CreateLearningSessionDlg
          classId={classId}
          open={createLearningSessionDlgOpen}
          setOpen={setCreateLearningSessionDlgOpen}
          onCreateSuccess={updateLearningSessionsWhenCreateSuccess}
        />
      )}
    </div>
  );
}

LearningSessionListOfClass.propTypes = {
  classId: PropTypes.string.isRequired,
  enableCreateSession: PropTypes.bool,
  viewDetailOnRowClick: PropTypes.bool,
};

LearningSessionListOfClass.defaultProps = {
  enableCreateSession: false,
  viewDetailOnRowClick: false,
};
