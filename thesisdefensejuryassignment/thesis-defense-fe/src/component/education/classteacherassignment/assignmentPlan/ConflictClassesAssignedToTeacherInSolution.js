import {request} from "api";
import StandardTable from "component/table/StandardTable";
import {useEffect, useState} from "react";

function ConflictClassesAssignedToTeacherInSolution(props) {
  const planId = props.planId;
  const [conflictClasses, setConflictClasses] = useState([]);

  const columns = [
    { title: "Mã giáo viên", field: "teacherId" },
    { title: "Tên giảng viên", field: "teacherName" },
    { title: "Lớp 1", field: "classCode1" },
    { title: "Môn", field: "courseName1" },
    { title: "Thời khoá biểu", field: "timeTable1" },
    { title: "Lớp 2", field: "classCode2" },
    { title: "Môn", field: "courseName2" },
    { title: "Thời khoá biểu", field: "timeTable2" },
  ];

  function getTeacherList() {
    request(
      "GET",
      `edu/teaching-assignment/plan/${planId}/solution/conflict-classes`,
      (res) => {
        setConflictClasses(res.data);
      }
    );
  }

  useEffect(() => {
    getTeacherList();
  }, []);

  return (
    conflictClasses.length > 0 && (
      <div style={{ marginTop: 48 }}>
        <StandardTable
          hideCommandBar
          title={"Danh sách lớp xung đột"}
          columns={columns}
          data={conflictClasses}
          options={{ selection: false, pageSize: 5 }}
        />
      </div>
    )
  );
}

export default ConflictClassesAssignedToTeacherInSolution;
