import React, {useEffect, useState} from "react";
import Typography from "@mui/material/Typography";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@mui/material/Table";

import {Button, Grid, MenuItem, TableHead, TextField,} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow";
import {StyledTableCell, StyledTableRow,} from "./lib";
import TableBody from "@mui/material/TableBody";
import Pagination from "@material-ui/lab/Pagination";
import {request} from "../../../api";

export default function ContestManagerListRequestingParticipant(props) {
  const contestId = props.contestId;
  const [totalPagePending, setTotalPagePending] = useState(0);
  const [pagePending, setPagePending] = useState(1);
  const [pagePendingSize, setPagePendingSize] = useState(10);
  const [load, setLoad] = useState(true);
  const [successful, setSuccessful] = useState([]);
  const [pendings, setPendings] = useState([]);
  const pageSizes = [10, 20, 50, 100, 150];
  const [totalPageSuccessful, setTotalPageSuccessful] = useState(0);
  const [pageSuccessfulSize, setPageSuccessfulSize] = useState(10);

  function getUserPending(s, p) {
    request(
      "get",
      "/contests/" + contestId + "/pending-users" +
        "?size=" +
        s +
        "&page=" +
        (p - 1),
      (res) => {
        console.log("res pending", res.data);
        setPendings(res.data.contents.content);
        setTotalPagePending(res.data.contents.totalPages);
      }
    ).then();
  }
  function getUserSuccessful(s, p) {
    request(
      "get",
      "/contests/" + contestId + "/registered-users" +
        "?size=" +
        s +
        "&page=" +
        (p - 1),
      (res) => {
        console.log("res pending", res.data);
        setSuccessful(res.data.contents.content);
        setTotalPageSuccessful(res.data.contents.totalPages);
      }
    ).then();
  }

  const handlePagePendingSizeChange = (event) => {
    setPagePendingSize(event.target.value);
    setPagePending(1);
    getUserPending(event.target.value, 1);
    // getProblemContestList();
  };

  useEffect(() => {
    getUserPending(pagePendingSize, 1);
    getUserSuccessful(pageSuccessfulSize, 1);
  }, []);
  return (
    <div>
      <div>
        <section id={"#pending"}>
          <Typography
            variant="h5"
            component="h2"
            style={{ marginTop: 10, marginBottom: 10 }}
          >
            List Student Request
          </Typography>
        </section>
        <TableContainer component={Paper}>
          <Table
            sx={{ minWidth: window.innerWidth - 500 }}
            aria-label="customized table"
          >
            <TableHead>
              <TableRow>
                <StyledTableCell></StyledTableCell>
                <StyledTableCell align="center">User Name</StyledTableCell>
                <StyledTableCell align="center">Full Name</StyledTableCell>
                <StyledTableCell align="center">Email</StyledTableCell>
                <StyledTableCell align="center">Approve</StyledTableCell>
                <StyledTableCell align="center">Reject</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {pendings.map((s, index) => (
                <StyledTableRow>
                  <StyledTableCell>
                    <b>{index + 1 + (pagePending - 1) * pagePendingSize}</b>
                  </StyledTableCell>

                  <StyledTableCell align="center">
                    <b>{s.userName}</b>
                  </StyledTableCell>

                  <StyledTableCell align="center">
                    <b>
                      {s.firstName} {s.middleName} {s.lastName}
                    </b>
                  </StyledTableCell>

                  <StyledTableCell align="center">
                    <b>{s.email}</b>
                  </StyledTableCell>

                  <StyledTableCell align="center">
                    <Button
                      variant="contained"
                      color="light"
                      onClick={() => {
                        let body = {
                          contestId: contestId,
                          userId: s.userName,
                          status: "SUCCESSES",
                        };
                        request(
                          "post",
                          "/contests/registers/approval-management",
                          () => {
                            successful.push(s);
                            // setSuccessful(successful)
                            // setSuccessful(successful)
                            pendings.splice(index, 1);
                            // setPendings(pendings);
                            console.log("successful ", successful);
                            console.log("pendings ", pendings);
                            setLoad(false);
                            setLoad(true);
                          },
                          {},
                          body
                        ).then();
                      }}
                    >
                      Approve
                    </Button>
                  </StyledTableCell>

                  <StyledTableCell align="center">
                    <Button
                      variant="contained"
                      color="light"
                      onClick={() => {
                        let body = {
                          contestId: contestId,
                          userId: s.userName,
                          status: "FAILED",
                        };
                        request(
                          "post",
                          "/contests/registers/approval-management",
                          () => {
                            pendings.splice(index, 1);
                            setLoad(false);
                            setLoad(true);
                          },
                          {},
                          body
                        ).then();
                      }}
                    >
                      Reject
                    </Button>
                  </StyledTableCell>
                </StyledTableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

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
              value={pagePendingSize}
              onChange={handlePagePendingSizeChange}
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
              count={totalPagePending}
              page={pagePending}
              siblingCount={1}
              boundaryCount={1}
              variant="outlined"
              shape="rounded"
              onChange={(event, value) => {
                setPagePending(value);
                getUserPending(pagePendingSize, value);
              }}
            />
          </Grid>
        </Grid>
      </div>
    </div>
  );
}
