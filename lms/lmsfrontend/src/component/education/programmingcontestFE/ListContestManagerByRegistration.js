import AddIcon from "@material-ui/icons/Add";
import { Box, LinearProgress } from "@mui/material";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { request } from "../../../api";
import { toFormattedDateTime } from "../../../utils/dateutils";
import StandardTable from "../../table/StandardTable";

export function ListContestManagerByRegistration() {
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
    { title: "Role", field: "roleId" },
    { title: "Created By", field: "userId" },
    {
      title: "Created At",
      field: "startAt",
      render: (rowData) => toFormattedDateTime(rowData["startAt"]),
    },
  ];

  function getContestListByUserRole() {
    request("get", "/contests", (res) => {
      setContests(res.data);
    }).then(() => setLoading(false));
  }

  useEffect(() => {
    getContestListByUserRole();
  }, []);

  return (
    <Box mb={2}>
      {loading && <LinearProgress />}
      <StandardTable
        title="My Contests"
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
    </Box>
  );
}
