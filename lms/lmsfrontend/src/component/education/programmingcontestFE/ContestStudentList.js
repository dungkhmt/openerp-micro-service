import {request} from "api";
import StandardTable from "component/table/StandardTable";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {toFormattedDateTime} from "utils/dateutils";

export default function ContestStudentList() {
  const [contests, setContests] = useState([]);

  const columns = [
    // {title: "No.", field: "index"},
    {
      title: "Contest",
      field: "contestName",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/student-view-contest-detail/" +
              rowData["contestId"],
          }}
        >
          {rowData["contestName"]}
        </Link>
      ),
    },
    {title: "Status", field: "status"},
    {title: "Created By", field: "createdBy"},
    {title: "Created At", field: "date"},
  ];

  function getContestList() {
    request("get", "/get-contest-registered-student", (res) => {
      const data = res.data.contests.map((e, index) => ({
        index: index + 1,
        contestId: e.contestId,
        contestName: e.contestName,
        status: e.statusId,
        createdBy: e.userId,
        date: toFormattedDateTime(e.createdAt),
      }));
      setContests(data);
    }).then();
  }

  useEffect(() => {
    getContestList();
  }, []);
  return (
    <div>
      <StandardTable
        title={"Registered Contest"}
        columns={columns}
        data={contests}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
}
