import {request} from "api";
import StandardTable from "component/table/StandardTable";
import {useEffect, useState} from "react";

const columns = [
  { title: "Mã lớp 1", field: "classId1" },
  { title: "Mã học phần", field: "courseId1" },
  { title: "Thời khoá biểu", field: "timetable1", sorting: false },
  { title: "Mã thời khoá biểu", field: "timetableCode1", sorting: false },
  { title: "Mã lớp 2", field: "classId2" },
  { title: "Mã học phần", field: "courseId2" },
  { title: "Thời khoá biểu", field: "timetable2", sorting: false },
  { title: "Mã thời khoá biểu", field: "timetableCode2", sorting: false },
];

function PairConflictTimetableClass({ planId }) {
  const [conflictList, setConflictList] = useState([]);

  // Funcs
  function getConflictTimetableClassList() {
    request(
      "GET",
      `/edu/teaching-assignment/plan/${planId}/class/pair-of-conflict-timetable-class`,
      (res) => {
        setConflictList(res.data);
      }
    );
  }

  useEffect(() => {
    getConflictTimetableClassList();
  }, []);

  return (
    conflictList.length > 0 && (
      <div style={{ marginTop: 64 }}>
        <StandardTable
          title={"Danh sách lớp trùng giờ"}
          columns={columns}
          data={conflictList}
          hideCommandBar
          options={{
            selection: false,
          }}
        />
      </div>
    )
  );
}

export default PairConflictTimetableClass;
