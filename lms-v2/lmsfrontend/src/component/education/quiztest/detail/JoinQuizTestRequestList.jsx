import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
import {errorNoti} from "../../../../utils/notification";
import StandardTable from "../../../table/StandardTable";
import {Button, Card, CardContent} from "@mui/material";

export default function JoinQuizTestRequestList(props) {
  let testId = props.testId;
  const [studentsRegisteredQuizTest, setStudentsRegisteredQuizTest] = useState([]);
  const [studentLoginIdsToAccept, setStudentLoginIdsToAccept] = useState([]);

  useEffect(getStudentsRegisteredQuizTest, []);

  function getStudentsRegisteredQuizTest() {
    const successHandler = (res) => {
      let registeredStudents = res.data.filter(student => student.statusId === "STATUS_REGISTERED");
      setStudentsRegisteredQuizTest(registeredStudents);
    }
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", `/get-all-student-in-test?testId=${testId}`, successHandler, errorHandlers);
  }

  function updateSelectedStudentsToAccept(newSelectedStudents) {
    setStudentLoginIdsToAccept(newSelectedStudents.map(student => student.userLoginId));
  }

  function acceptStudentsToQuizTest(acceptedLoginIds) {
    if (!acceptedLoginIds || acceptedLoginIds.length === 0) return;

    let formData = new FormData();
    formData.append("testId", testId);
    formData.append("studentList", acceptedLoginIds.join(";"));

    let refreshRegisteredStudents = (res) => {
      if (res.data < 0) return;
      let remainingRegisteredStudents = studentsRegisteredQuizTest.filter(
        student => !acceptedLoginIds.includes(student.userLoginId)
      );
      setStudentsRegisteredQuizTest(remainingRegisteredStudents);
    }
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi thu hồi quyền")
    }
    request("POST", "/accept-students-in-test", refreshRegisteredStudents, errorHandlers, formData);
  }

  const AcceptStudentButton = ({ acceptedLoginIds }) => (
    <Button color="primary" variant="outlined"
            onClick={() => acceptStudentsToQuizTest(acceptedLoginIds)}>
      Phê duyệt
    </Button>
  )

  const columns = [
    { field: "userLoginId", title: "Login ID" },
    { field: "fullName", title: "Họ và tên" },
    { field: "email", title: "Email" },
    { field: "", title: "",
      render: student => <AcceptStudentButton acceptedLoginIds={[student.userLoginId]}/>
    }
  ]

  const actions = [{ icon: () => <AcceptStudentButton acceptedLoginIds={studentLoginIdsToAccept}/> }]

  return (
    <Card>
      <CardContent>
        <StandardTable
          title="Sinh viên đăng ký"
          columns={columns}
          data={studentsRegisteredQuizTest}
          hideCommandBar
          options={{
            selection: true,
            search: true,
            sorting: true
          }}
          actions={actions}
          onSelectionChange={updateSelectedStudentsToAccept}/>
      </CardContent>
    </Card>
  );
}
