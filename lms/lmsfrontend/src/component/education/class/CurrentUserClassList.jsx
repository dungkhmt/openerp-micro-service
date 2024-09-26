import React, {useEffect, useState} from 'react';
import {request} from "../../../api";
import {useHistory} from "react-router-dom";
import StandardTable from "../../table/StandardTable";

function CurrentUserClassList(props) {
  const columns = [
    { title: "Mã lớp", field: "classCode" },
    { title: "Mã môn", field: "courseId" },
    { title: "Tên môn", field: "courseName" },
    { title: "Người tạo", field: "createdByUserLoginId" },
    { title: "Học kỳ", field: "semester" },
    { title: "Trạng thái", field: "statusId" }
  ];

  const history = useHistory();
  const [classesOfCurrentUser, setClassesOfCurrentUser] = useState([]);

  useEffect(getClassesOfCurrentUser, [])

  function getClassesOfCurrentUser() {
    request("get", "/edu/class/get-classes-of-user/null", (res) => {
        setClassesOfCurrentUser(res.data);
    });
  }

  function navigateToClassDetailPage(event, rowData) {
    history.push(`/edu/teacher/class/detail/${rowData.classId}`);
  }

  return (
    <StandardTable
      title="Danh sách lớp (Người dùng hiện tại)"
      columns={columns}
      data={classesOfCurrentUser}
      hideCommandBar
      options={{
        selection: false,
        search: true,
        sorting: true,
      }}
      onRowClick={navigateToClassDetailPage}
    />
  );
}

export default CurrentUserClassList;