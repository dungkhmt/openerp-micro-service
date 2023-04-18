import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {Link} from "react-router-dom";
import StandardTable from "../../table/StandardTable";
import Typography from "@mui/material/Typography";
import {toFormattedDateTime} from "../../../utils/dateutils";

export function ListContestManagerByRegistration() {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalPages, setTotalPage] = useState(0);
  const pageSizes = [20, 50, 100];
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
  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const handlePageSizeChange = (event) => {
    setPageSize(event.target.value);
    setPage(1);
    // getProblemContestList();
  };

  function getContestListByUserRole() {
    request("get", "/get-contest-by-user-role", (res) => {
      setContests(res.data);
    }).then();
  }

  async function getContestList() {
    request(
      "get",
      "/get-contest-paging-by-user-manager?size=" +
      pageSize +
      "&page=" +
      (page - 1),
      (res) => {
        console.log("contest list", res.data);
        setTotalPage(res.data.totalPages);
        setContests(res.data.contests);
      }
    ).then();
  }

  /*
  useEffect(() => {
    console.log("use effect");
    getContestList().then();
  }, [page, pageSize]);
  */
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
