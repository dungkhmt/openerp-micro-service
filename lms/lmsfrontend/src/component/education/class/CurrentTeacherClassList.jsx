import React, {useEffect, useState} from 'react';
import {request} from "../../../api";
import StandardTable from "../../table/StandardTable";
import {useHistory} from "react-router-dom";

function CurrentTeacherClassList(props) {
  const columns = [
    { title: "Mã lớp", field: "classCode" },
    { title: "Mã học phần", field: "courseId" },
    { title: "Tên học phần", field: "name" },
    { title: "Loại lớp", field: "classType" },
    { title: "Khoa/Viện", field: "department" },
    { title: "Học kỳ", field: "semester" },
    { title: "Trạng thái", field: "statusId" }
  ];

  const history = useHistory();
  const [classesOfCurrentTeacher, setClassesOfCurrentTeacher] = useState([]);

  useEffect(getClassesOfCurrentTeacher, [])

  function getClassesOfCurrentTeacher() {
    request("get", "/edu/class/list/teacher", (res) => {
      setClassesOfCurrentTeacher(res.data);
    });
  }

  function navigateToClassDetailPage(event, rowData) {
    history.push(`/edu/teacher/class/detail/${rowData.id}`);
  }

  return (
    <StandardTable
      title="Danh sách lớp (Giáo viên)"
      columns={columns}
      data={classesOfCurrentTeacher}
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

export default CurrentTeacherClassList;