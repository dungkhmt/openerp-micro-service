import React, {useEffect, useState} from "react";
import StandardTable from "../../../../table/StandardTable";
import {request} from "../../../../../api";

export default function StudentListOfClass({ classId }) {
  const [studentsOfClass, setStudentsOfClass] = useState([]);

  useEffect(getStudentsOfClass, []);

  function getStudentsOfClass() {
    request("get", `/edu/class/${classId}/students`, (res) => {
      setStudentsOfClass(res.data);
    });
  }

  const columns = [
    { field: "name", title: "Họ và tên" },
    { field: "email", title: "Email",
      render: student => (
        <a href={`mailto:${student.email}`}>{student.email}</a>
      )
    },
  ];

  return (
    <StandardTable  title="Danh sách sinh viên"
                    columns={columns}
                    data={studentsOfClass}
                    hideCommandBar
                    options={{
                      selection: false,
                      search: true,
                      sorting: false
                    }}
    />
  )
}
