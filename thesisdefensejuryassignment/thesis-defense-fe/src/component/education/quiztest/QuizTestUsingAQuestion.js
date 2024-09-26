import React, {useEffect, useState} from "react";
import MaterialTable from "material-table";
import {request} from "api";

export default function QuizTestUsingAQuestion(props) {
  const questionId = props.questionId;
  const [quizTests, setQuizTests] = useState([]);
  const columns = [
    { title: "TestId", field: "testId" },
    { title: "Test Name", field: "testName" },
    { title: "Date", field: "scheduleDatetime" },
    { title: " Status", field: "statusId" },
  ];

  function getQuizTestUsingQuestion() {
    request("get", "/get-quiz-test-using-question/" + questionId, (res) => {
      setQuizTests(res.data);
    });
  }
  useEffect(() => {
    getQuizTestUsingQuestion();
  }, []);
  return (
    <MaterialTable
      title="Quiz tests using the question"
      columns={columns}
      data={quizTests}
    ></MaterialTable>
  );
}
