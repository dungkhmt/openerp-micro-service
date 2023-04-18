import React, {useEffect, useState} from "react";
import MaterialTable from "material-table";
import {request} from "api";
import {Link, useHistory} from "react-router-dom";
import {Button, MenuItem, TextField} from "@mui/material/";

export default function QuizTestsOfParticipantRole() {
  const [quizTests, setquizTests] = useState([]);
  const contestId = "GENERAL_CONTEST";
  const history = useHistory();
  const [searchUsers, setSearchUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [keyword, setKeyword] = useState("");

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
    { title: "Status", field: "statusId" },
  ];

  function getMyQuizTests() {
    request("get", "get-quiz-tests-of-user-login", (res) => {
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
