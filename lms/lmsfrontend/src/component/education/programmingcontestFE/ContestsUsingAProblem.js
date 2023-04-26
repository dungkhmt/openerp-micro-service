import React, {useEffect, useState} from "react";
import MaterialTable from "material-table";
import {request} from "api";
import {Link, useHistory} from "react-router-dom";

export default function ContestsUsingAProblem(props) {
  const problemId = props.problemId;
  const history = useHistory();
  const [contests, setContests] = useState([]);
  const columns = [
    {
      title: "contestId",
      field: "contestId",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/contest-manager/" + rowData["contestId"],
          }}
        >
          {rowData["contestId"]}
        </Link>
      ),
    },
    {
      title: "status",
      field: "statusId",
    },
  ];
  function getContests() {
    request("get", "get-contests-using-a-problem/" + problemId, (res) => {
      setContests(res.data);
    });
  }

  useEffect(() => {
    getContests();
  }, []);
  return (
    <div>
      <MaterialTable
        title={"Contests Using the current problem"}
        columns={columns}
        data={contests}
        onRowClick={(event, rowData) => {
          console.log(rowData);
        }}
      />
    </div>
  );
}
