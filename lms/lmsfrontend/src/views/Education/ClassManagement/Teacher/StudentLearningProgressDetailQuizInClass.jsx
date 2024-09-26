import React, {useEffect, useState} from "react";
import MaterialTable from "material-table";
import {request} from "../../../../api";
import {toFormattedDateTime} from "../../../../utils/dateutils";

export default function StudentLearningProgressDetailQuizInClass(props) {
  const studentId = props.studentId;
  const [quizs, setQuizs] = useState([]);
  const columns = [
    { title: "UserName", field: "userLoginId" },
    { title: "FullName", field: "fullName" },
    { title: "Question", field: "questionId" },
    { title: "ClassCode", field: "classId" },
    { title: "CourseId", field: "courseId" },
    { title: "Grade", field: "grade" },
    { title: "TestQuiz", field: "testId" },
    { title: "Session", field: "sessionName" },
    { title: "DateTime", field: "date" },
  ];

  function getUserDoQuizInClass() {
    request(
      // token, history,
      "get",
      "/get-quiz-test-participation-execution-result-of-user-login/" +
        studentId,
      (res) => {
        let lst = [];
        res.data.map((e) => {
          lst.push({
            ...e,
            date: toFormattedDateTime(e.date),
          });
        });
        console.log("getUserDoQuizInClass, res.data = ", res.data);
        //console.log("getClassesOfUser, lst = ", lst);
        setQuizs(lst);
      }
    );
  }
  useEffect(() => {
    getUserDoQuizInClass();
  }, []);
  return (
    <div>
      <h1>View Quiz In Class</h1>
      <MaterialTable columns={columns} data={quizs}></MaterialTable>
    </div>
  );
}
