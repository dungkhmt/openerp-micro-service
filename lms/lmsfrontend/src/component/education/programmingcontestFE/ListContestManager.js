import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {ListContestManagerByRegistration} from "./ListContestManagerByRegistration";

export function ListContestManager() {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalPages, setTotalPage] = useState(0);
  const pageSizes = [20, 50, 100];
  const [contests, setContests] = useState([]);

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const handlePageSizeChange = (event) => {
    setPageSize(event.target.value);
    setPage(1);
    // getProblemContestList();
  };

  async function getContestList() {
    request(
      "get",
      "/get-contest-paging-by-user-create?size=" +
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

  useEffect(() => {
    getContestList().then();
  }, [page, pageSize]);

  return (
    <div>
      <ListContestManagerByRegistration />
      {/*
      <div>
        <div>
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 100 }} aria-label="customized table">
              <TableHead>
                <TableRow>
                  <StyledTableCell></StyledTableCell>
                  <StyledTableCell align="left">Title</StyledTableCell>
                  <StyledTableCell align="left">Status</StyledTableCell>
                  <StyledTableCell align="left">Created By</StyledTableCell>
                  <StyledTableCell align="left">Created Date</StyledTableCell>
                  <StyledTableCell align="center">Detail</StyledTableCell>
                  <StyledTableCell align="center">Edit</StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {contests.map((contest, index) => (
                  <StyledTableRow>
                    <StyledTableCell component="th" scope="row">
                      {pageSize * (page - 1) + index + 1}
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      <Link
                        to={
                          "/programming-contest/contest-manager/" +
                          contest.contestId
                        }
                        style={{
                          textDecoration: "none",
                          color: "#000000",
                          hover: { color: "#00D8FF", textPrimary: "#00D8FF" },
                        }}
                      >
                        <b>{contest.contestName}</b>
                      </Link>
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      <b>{contest.statusId}</b>
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      <b>{contest.userId}</b>
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      <b>{contest.createdAt}</b>
                    </StyledTableCell>

                    <StyledTableCell align="left">
                      <Link
                        to={
                          "/programming-contest/contest-manager/" +
                          contest.contestId
                        }
                        style={{
                          textDecoration: "none",
                          color: "#000000",
                          hover: { color: "#00D8FF", textPrimary: "#00D8FF" },
                        }}
                      >
                        <Button variant="contained" color="light">
                          Detail
                        </Button>
                      </Link>
                    </StyledTableCell>

                    <StyledTableCell align="center">
                      <Link
                        to={
                          "/programming-contest/contest-edit/" +
                          contest.contestId
                        }
                        style={{
                          textDecoration: "none",
                          color: "black",
                          cursor: "",
                        }}
                      >
                        <Button variant="contained" color="light">
                          Edit
                        </Button>
                      </Link>
                    </StyledTableCell>
                  </StyledTableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </div>
        <br></br>
        <Grid container spacing={12}>
          <Grid item xs={6}>
            <TextField
              variant={"outlined"}
              autoFocus
              size={"small"}
              required
              select
              id="pageSize"
              value={pageSize}
              onChange={handlePageSizeChange}
            >
              {pageSizes.map((item) => (
                <MenuItem key={item} value={item}>
                  {item}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          <Grid item>
            <Pagination
              className="my-3"
              count={totalPages}
              page={page}
              siblingCount={1}
              boundaryCount={1}
              variant="outlined"
              shape="rounded"
              onChange={handlePageChange}
            />
          </Grid>
        </Grid>
      </div>
              */}
      {/*
      <ListContestByRole />
      */}
    </div>
  );
}
