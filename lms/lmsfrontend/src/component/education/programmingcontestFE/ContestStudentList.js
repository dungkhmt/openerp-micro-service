import { LinearProgress } from "@mui/material";
import { request } from "api";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toFormattedDateTime } from "utils/dateutils";

export default function ContestStudentList() {
  const [contests, setContests] = useState([]);
  const [loading, setLoading] = useState(true);

  const columns = [
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
    { title: "Status", field: "status" },
    { title: "Created By", field: "createdBy" },
    { title: "Created At", field: "createdAt" },
  ];

  function getContestList() {
    request("get", "/students/contests", (res) => {
      const data = res.data.contests.map((e, index) => ({
        index: index + 1,
        contestId: e.contestId,
        contestName: e.contestName,
        status: e.statusId,
        createdBy: e.userId,
        createdAt: toFormattedDateTime(e.startAt),
      }));
      setContests(data);
    }).then(() => setLoading(false));
  }

  useEffect(() => {
    getContestList();
  }, []);

  return (
    <>
      {loading && <LinearProgress />}
      <StandardTable
        title={"Registered Contest"}
        columns={columns}
        data={contests}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
      />
    </>
  );
}
