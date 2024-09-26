import React, {useEffect, useState} from "react";
import Typography from "@mui/material/Typography";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@mui/material/Table";

import {Grid, MenuItem, TableHead, TextField,} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow";
import {StyledTableCell, StyledTableRow,} from "./lib";
import TableBody from "@mui/material/TableBody";
import Pagination from "@material-ui/lab/Pagination";
import {request} from "../../../api";

export default function ContestManagerListParticipant(props) {
  const contestId = props.contestId;
  const [pageSuccessful, setPageSuccessful] = useState(1);
  const [successful, setSuccessful] = useState([]);
  const [pageSuccessfulSize, setPageSuccessfulSize] = useState(10);
  const [totalPageSuccessful, setTotalPageSuccessful] = useState(0);
  const pageSizes = [10, 20, 50, 100, 150];

  useEffect(() => {
    getUserSuccessful(pageSuccessfulSize, 1);
  }, []);
  function getUserSuccessful(s, p) {
    request(
      "get",
      "/contests/" + contestId + "/registered-users" +
        contestId +
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

  const handlePageSuccessfulSizeChange = (event) => {
    setPageSuccessfulSize(event.target.value);
    setPageSuccessful(1);
    getUserSuccessful(event.target.value, 1);
  };

  return (
    <div>
      <section id={"#registered"}>
        <Typography
          variant="h5"
          component="h2"
          style={{ marginTop: 10, marginBottom: 10 }}
        >
          List Student Registered Contest
        </Typography>
      </section>

      <TableContainer component={Paper}>
        <Table
          sx={{ minWidth: window.innerWidth - 500 }}
          aria-label="customized table"
        >
          <TableHead>
            <TableRow>
              <StyledTableCell align="center"></StyledTableCell>
              <StyledTableCell align="center">User Name</StyledTableCell>
              <StyledTableCell align="center">Full Name</StyledTableCell>
              <StyledTableCell align="center">Email</StyledTableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {successful.map((s, index) => (
              <StyledTableRow>
                <StyledTableCell>
                  <b>{index + 1 + (pageSuccessful - 1) * pageSuccessfulSize}</b>
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
            value={pageSuccessfulSize}
            onChange={handlePageSuccessfulSizeChange}
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
            count={totalPageSuccessful}
            page={pageSuccessful}
            siblingCount={1}
            boundaryCount={1}
            variant="outlined"
            shape="rounded"
            onChange={(event, value) => {
              setPageSuccessful(value);
              getUserSuccessful(pageSuccessfulSize, value);
            }}
          />
        </Grid>
      </Grid>
    </div>
  );
}
