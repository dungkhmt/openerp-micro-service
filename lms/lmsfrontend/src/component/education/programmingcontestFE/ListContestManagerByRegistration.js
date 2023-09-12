import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {Link} from "react-router-dom";
import StandardTable from "../../table/StandardTable";
import Typography from "@mui/material/Typography";
import {toFormattedDateTime} from "../../../utils/dateutils";
import AddIcon from "@material-ui/icons/Add";
import HustContainerCard from "../../common/HustContainerCard";
import {LinearProgress} from "@mui/material";

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
    {title: "Created By", field: "userId"},
    {
      title: "Created At",
      field: "startAt",
      render: (rowData) => (
        <Typography>
          {toFormattedDateTime(rowData["startAt"])}
        </Typography>
      ),
    },
    {title: "Status", field: "statusId"},
    {title: "Role", field: "roleId"},
    // {title: "Registration Status", field: "registrationStatusId"},
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
    <HustContainerCard>
      {loading && <LinearProgress/>}
      <StandardTable
        title="Contests"
        columns={columns}
        data={contests}
        hideCommandBar
        options={{
          pageSize: 10,
          selection: false,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: () => {
              return <AddIcon fontSize="large"/>;
            },
            tooltip: "Create new Contest",
            isFreeAction: true,
            onClick: () => {
              window.open("/programming-contest/create-contest")
            }
          }
        ]}
      />
    </HustContainerCard>
  );
}
