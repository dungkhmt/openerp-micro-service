import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {Link} from "react-router-dom";
import StandardTable from "../../table/StandardTable";
import Typography from "@mui/material/Typography";
import {toFormattedDateTime} from "../../../utils/dateutils";

export function ListContestManagerByRegistration() {
  const [contests, setContests] = useState([]);

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
    {title: "Reg. Status", field: "registrationStatusId"},
  ];

  function getContestListByUserRole() {
    request("get", "/get-contest-by-user-role", (res) => {
      setContests(res.data);
    }).then();
  }

  useEffect(() => {
    getContestListByUserRole();
  }, []);

  return (
    <div>
      <StandardTable
        title="DS Contests được phân quyền"
        columns={columns}
        data={contests}
        hideCommandBar
        options={{
          pageSize: 10,
          selection: false,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
}
