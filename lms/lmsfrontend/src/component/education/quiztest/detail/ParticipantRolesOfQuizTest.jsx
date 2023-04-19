import React, {useEffect, useState} from "react";
import {request} from "api";
import {errorNoti, successNoti} from "../../../../utils/notification";
import {Button, Card, CardContent} from "@mui/material";
import StandardTable from "../../../table/StandardTable";
import AddMemberToQuizTestDialog from "./AddMemberToQuizTestDialog";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(theme => ({
  tableWrapper: {
    '& table thead tr': {
      '& th:nth-child(3)': {
        maxWidth: '160px !important'
      }
    }
  }
}))

export default function ParticipantRolesOfQuizTest(props) {
  const testId = props.testId;
  const classes = useStyles();
  const [participants, setParticipants] = useState([]);
  const [addMemberDialogOpen, setAddMemberDialogOpen] = useState(false);

  useEffect(getParticipantRoles, []);

  function getParticipantRoles() {
    let successHandler = res => setParticipants(res.data);
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", true)
    }
    request("get", `get-users-role-of-quiz-test/${testId}`, successHandler, errorHandlers);
  }

  function deleteParticipantRole(participantRole) {
    if (!window.confirm("Bạn có chắc muốn xóa vai trò của thành viên này?")) return;

    const config = { params: participantRole}
    let successHandler = res => {
      successNoti("Xóa vai trò thành viên thành công!", true);
      getParticipantRoles();
    }
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi xóa vai trò thành viên!", true)
    }
    request("DELETE", "/quiz-test-participant-role", successHandler, errorHandlers, null, config)
  }

  const AddMemberToQuizTestButton = (
    <Button color="primary"
            variant="contained"
            onClick={() => setAddMemberDialogOpen(true)}>
      Thêm mới
    </Button>
  )

  const DeleteParticipantRoleButton = ({ participantRole }) => (
    <Button color="error"
            variant="outlined"
            onClick={() => deleteParticipantRole(participantRole)}>
      Xóa
    </Button>
  )

  const columns = [
    { title: "User ID", field: "userId" },
    { title: "Vai trò", field: "roleId" },
    { title: "", field: "", cellStyle: { maxWidth: '160px'},
      render: (participantRole) => <DeleteParticipantRoleButton participantRole={participantRole}/>
    }
  ];

  const actions = [{ icon: () => AddMemberToQuizTestButton, isFreeAction: true }];

  return (
    <>
      <Card>
        <CardContent className={classes.tableWrapper}>
          <StandardTable
            title="User roles"
            columns={columns}
            data={participants}
            actions={actions}
            hideCommandBar
            options={{
              selection: false,
              search: true,
              sorting: true,
            }}
          />
        </CardContent>
      </Card>

      <AddMemberToQuizTestDialog testId={testId}
                                 open={addMemberDialogOpen}
                                 onClose={() => setAddMemberDialogOpen(false)}
                                 onAddSuccess={getParticipantRoles}/>
    </>
  );
}
