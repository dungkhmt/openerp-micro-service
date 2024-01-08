import AddIcon from "@material-ui/icons/Add";
import { LinearProgress } from "@mui/material";
import { request } from "api";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toFormattedDateTime } from "utils/dateutils";
import StandardTable from "../../table/StandardTable";

export function ListContestAll() {
  const [contests, setContests] = useState([]);
  const [loading, setLoading] = useState(true);

  const columns = [
    {
      title: "Name",
      field: "contestName",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/contest-manager/" + rowData["contestId"],
          }}
        >
          {rowData["contestName"]}
        </Link>
      ),
    },
    { title: "Status", field: "statusId" },
    // { title: "Role", field: "roleId" },
    { title: "Created By", field: "userId" },
    {
      title: "Created At",
      field: "startAt",
      render: (rowData) => toFormattedDateTime(rowData["startAt"]),
    },
  ];

  function getContestListByUserRole() {
    request("get", "/all-contests", (res) => {
      setContests(res.data);
    }).then(() => setLoading(false));
  }

  useEffect(() => {
    getContestListByUserRole();
  }, []);

  return (
    <>
      {loading && <LinearProgress />}
      <StandardTable
        title="All Contests"
        columns={columns}
        data={contests}
        hideCommandBar
        options={{
          pageSize: 5,
          selection: false,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: () => {
              return <AddIcon fontSize="large" />;
            },
            tooltip: "Create new Contest",
            isFreeAction: true,
            onClick: () => {
              window.open("/programming-contest/create-contest");
            },
          },
        ]}
      />
    </>
  );
}
