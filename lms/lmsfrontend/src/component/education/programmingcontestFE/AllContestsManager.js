import EditIcon from "@mui/icons-material/Edit";
import { Box, Button, IconButton } from "@mui/material";
import TablePagination from "@mui/material/TablePagination";
import { request } from "api";
import withScreenSecurity from "component/withScreenSecurity";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { defaultDatetimeFormat } from "utils/dateutils";
import { successNoti } from "utils/notification";
import StandardTable, {
  TablePaginationActions,
} from "../../table/StandardTable";

function AllContestsManager() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPage] = useState(0);
  const pageSizes = [10, 20, 50];
  const [contests, setContests] = useState([]);

  const switchJudgeMode = (mode) => {
    request("post", "/contests/switch-judge-mode?mode=" + mode, () =>
      successNoti("Saved", 5000)
    ).then();
  };

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const handlePageSizeChange = (event) => {
    setPageSize(event.target.value);
    setPage(0);
    // getProblemContestList();
  };

  async function getContestList() {
    request(
      "get",
      "/admin/contests?size=" + pageSize + "&page=" + page,
      (res) => {
        setTotalPage(res.data.count);
        setContests(res.data.contests);
      }
    ).then();
  }

  useEffect(() => {
    getContestList().then();
  }, [page, pageSize]);

  const columns = [
    {
      title: "Contest",
      render: (contest) => (
        <Link
          to={
            "/programming-contest/contest-manager/" +
            encodeURIComponent(contest.contestId)
          }
          style={{
            textDecoration: "none",
            color: "blue",
          }}
        >
          {contest.contestName}
        </Link>
      ),
    },
    { title: "Status", field: "statusId" },
    { title: "Created By", field: "userId" },
    {
      title: "Created At",
      field: "createdAt",
      render: (contest) => defaultDatetimeFormat(contest.createdAt),
    },
    {
      title: "Edit",
      render: (contest) => (
        <Link
          to={
            "/programming-contest/contest-edit/" +
            encodeURIComponent(contest.contestId)
          }
        >
          <IconButton variant="contained" color="success">
            <EditIcon />
          </IconButton>
        </Link>
      ),
    },
  ];
  return (
    <>
      <Box sx={{ marginBottom: 4 }}>
        <Button
          variant="contained"
          sx={{ marginBottom: "12px", marginRight: "16px" }}
          onClick={() => switchJudgeMode("ASYNCHRONOUS_JUDGE_MODE_QUEUE")}
        >
          Switch all to judge mode QUEUE
        </Button>
        <Button
          variant="contained"
          sx={{ marginBottom: "12px" }}
          onClick={() => switchJudgeMode("SYNCHRONOUS_JUDGE_MODE")}
        >
          Switch all to judge mode non-QUEUE
        </Button>

        <StandardTable
          title={"All Contests"}
          columns={columns}
          data={contests}
          hideCommandBar
          options={{
            selection: false,
            pageSize,
            search: true,
            sorting: true,
          }}
          key={contests.length}
          components={{
            Pagination: (props) => (
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
      </Box>
    </>
  );
}

const screenName = "SCR_ALL_CONTESTS";
export default withScreenSecurity(AllContestsManager, screenName, true);
