import React, {useEffect, useState} from "react";
import {request} from "api";
import {Link} from "react-router-dom";
import StandardTable from "../../table/StandardTable";
import {Box} from "@mui/material";
import {defaultDatetimeFormat} from "../../../utils/dateutils";

export default function ContestsUsingAProblem(props) {
  const problemId = props.problemId;
  const [contests, setContests] = useState([]);
  const columns = [
    {
      title: "Contest",
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
      title: "Manager",
      field: "userId",
    },
    {
      title: "Status",
      field: "statusId",
    },
    {
      title: "Created At",
      field: "createdAt",
      render: (contest) => defaultDatetimeFormat(contest.createdAt)
    },
  ];

  function getContests() {
    request("get", "problems/" + problemId + "/contests", (res) => {
      setContests(res.data);
    });
  }

  useEffect(() => {
    getContests();
  }, []);
  return (
    <Box sx={{marginTop: "36px"}}>
      <StandardTable
        title={"Contests using this problem"}
        columns={columns}
        data={contests}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </Box>
  );
}
