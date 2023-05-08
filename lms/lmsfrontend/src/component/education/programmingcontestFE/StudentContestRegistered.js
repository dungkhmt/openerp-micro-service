import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import ContestStudentList from "./ContestStudentList";

export function StudentContestRegistered() {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalPages, setTotalPage] = useState(0);
  const pageSizes = [20, 50, 100];
  const [contests, setContests] = useState([]);
  const [isCountDowns, setIsCountDowns] = useState([]);
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
      "/get-contest-paging-registered?size=" + pageSize + "&page=" + (page - 1),
      (res) => {
        setTotalPage(res.data.totalPages);
        setContests(res.data.contests);
        let arr = res.data.contests.map((c) => {
          return !c.isPublic;
        });
        console.log("arr ", arr);
        setIsCountDowns(arr);
      }
    ).then();
  }

  useEffect(() => {
    getContestList().then();
  }, [page, pageSize]);

  return (
    <div>
      <ContestStudentList/>
      {/*
      <div>
        <div>
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 100 }} aria-label="customized table">
              <TableHead>
                <TableRow>
                  <StyledTableCell></StyledTableCell>
                  <StyledTableCell align="left">Title</StyledTableCell>
                  <StyledTableCell align="center">
                    Register Contest
                  </StyledTableCell>
                  <StyledTableCell align="center">Count Down</StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {contests.map((contest, index) => (
                  <StyledTableRow>
                    <StyledTableCell component="th" scope="row">
                      {pageSize * (page - 1) + index + 1}
                    </StyledTableCell>

                    <StyledTableCell align="left">
                      <b>{contest.contestName}</b>
                    </StyledTableCell>
                    <StyledTableCell align="center">
                      <Link
                        to={
                          "/programming-contest/student-view-contest-detail/" +
                          contest.contestId
                        }
                        style={{
                          textDecoration: "none",
                          color: "black",
                          cursor: "",
                        }}
                      >
                        <Button variant="contained" color="light">
                          Detail
                        </Button>
                      </Link>
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      {isCountDowns[index] ? (
                        <Timer
                          id={contest.contestId}
                          time={contest.countDown}
                          timeOutHandler={() => {
                            isCountDowns[index] = false;
                          }}
                        ></Timer>
                      ) : (
                        <div></div>
                      )}
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
    </div>
  );
}
