import React, {useEffect, useState} from "react";
import MaterialTable from "material-table";
import {request} from "api";
import {Link, useHistory} from "react-router-dom";

export default function QuizTestListAll() {
  const [quizTests, setquizTests] = useState([]);
  const history = useHistory();
  const columns = [
    {
      title: "TestID",
      field: "testId",
      render: (rowData) => (
        <Link
          to={{
            pathname: "/edu/class/quiztest/detail/" + rowData["testId"],
          }}
        >
          {rowData["testId"]}
        </Link>
      ),
    },
    { title: "Role", field: "roleId" },
    { title: "User", field: "userId" },
    { title: "Status", field: "statusId" },
  ];

  function getMyQuizTests() {
    request("get", "get-all-quiz-test", (res) => {
      setquizTests(res.data);
    });
  }

  useEffect(() => {
    getMyQuizTests();
  }, []);
  return (
    <div>
      <MaterialTable
        title={"User Role"}
        columns={columns}
        data={quizTests}
        onRowClick={(event, rowData) => {
          console.log(rowData);
        }}
      />
    </div>
  );
}
