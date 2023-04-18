import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {successNoti} from "../../../utils/notification";
import HustContainerCard from "../../common/HustContainerCard";
import StandardTable, {TablePaginationActions} from "../../table/StandardTable";
import TablePagination from "@mui/material/TablePagination";
import {Button} from "@mui/material";

export function StudentContestNotRegistered() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [totalPages, setTotalPage] = useState(0);
  const pageSizes = [10, 20, 50, 100];
  const [contests, setContests] = useState([]);

  const [loading, setLoading] = useState(false);

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const handlePageSizeChange = (event) => {
    setPageSize(event.target.value);
    setPage(0);
  };

  async function getContestList() {
    request(
      "get",
      "/get-contest-paging-not-registered?size=" +
      pageSize +
      "&page=" +
      page,
      (res) => {
        setTotalPage(res.data.count);
        setContests(res.data.contests);
      }
    ).then();
  }

  useEffect(() => {
    getContestList().then();
  }, [page, pageSize]);

  function handleRegister(contestId) {
    setLoading(true);
    request(
      "post",
      "/student-register-contest/" + contestId,
      (res) => {
        successNoti(res.data.message, true);
        getContestList();
      }
    ).then(() => setLoading(false));
  }

  const columns = [
      {
        title: "Contest",
        field: "contestName",
      },
      {
        title: "Created By",
        field: "userId",
      },
      {
        render: (contest) => (
          <Button
            variant="contained"
            disabled={loading}
            onClick={() => handleRegister(contest.contestId)}
          >
            Register
          </Button>
        ),
      },
    ]
  ;

  return (
    <HustContainerCard>
      <StandardTable
        title={"List Contests"}
        columns={columns}
        data={contests}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
        components={{
          Pagination: props => (
            <TablePagination
              {...props}
              rowsPerPageOptions={pageSizes}
              rowsPerPage={pageSize}
              count={totalPages}
              page={page}
              onPageChange={handlePageChange}
              onRowsPerPageChange={handlePageSizeChange}
              ActionsComponent={TablePaginationActions}
            />
          ),
        }}
      />
    </HustContainerCard>
  );
}
