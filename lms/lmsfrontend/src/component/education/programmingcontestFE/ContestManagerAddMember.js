import React, {useEffect, useState} from "react";
import Typography from "@mui/material/Typography";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@mui/material/Table";

import {Button, Grid, MenuItem, TableHead, TextField,} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow";
import {getColorRegisterStatus, Search, SearchIconWrapper, StyledTableCell, StyledTableRow,} from "./lib";
import TableBody from "@mui/material/TableBody";
import Pagination from "@material-ui/lab/Pagination";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import SearchIcon from "@mui/icons-material/Search";
import {InputBase} from "@mui/material";
import {request} from "../../../api";

export default function ContestManagerAddMember(props) {
  const contestId = props.contestId;
  const [pageSearchSize, setPageSearchSize] = useState(10);
  const [totalPageSearch, setTotalPageSearch] = useState(0);
  const [pageSearch, setPageSearch] = useState(1);
  const [searchUsers, setSearchUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [successful, setSuccessful] = useState([]);
  const [load, setLoad] = useState(true);
  const [pendings, setPendings] = useState([]);
  const pageSizes = [10, 20, 50, 100, 150];
  const [pagePendingSize, setPagePendingSize] = useState(10);
  const [pageSuccessfulSize, setPageSuccessfulSize] = useState(10);
  const [totalPagePending, setTotalPagePending] = useState(0);
  const [totalPageSuccessful, setTotalPageSuccessful] = useState(0);

  function searchUser(keyword, s, p) {
    request(
      "get",
      "/search-user/" +
        contestId +
        "?size=" +
        s +
        "&page=" +
        (p - 1) +
        "&keyword=" +
        keyword,
      (res) => {
        console.log("res search", res);
        setSearchUsers(res.data.contents.content);
        setTotalPageSearch(res.data.contents.totalPages);
      }
    ).then();
  }

  function getRoles() {
    request("get", "/get-list-roles-contest", (res) => {
      console.log("getRoles, res.data = ", res.data);
      setRoles(res.data);
    }).then();
  }
  const handlePageSearchSizeChange = (event) => {
    setPageSearchSize(event.target.value);
    setPageSearch(1);
    searchUser(keyword, event.target.value, 1);
  };
  function getUserPending(s, p) {
    request(
      "get",
      "/get-user-register-pending-contest/" +
        contestId +
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
      "/get-user-register-successful-contest/" +
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

  useEffect(() => {
    getUserPending(pagePendingSize, 1);
    getUserSuccessful(pageSuccessfulSize, 1);

    searchUser(keyword, pageSearchSize, 1);
    getRoles();
  }, []);

  return (
    <div>
      <div>
        <section id={"#search"}>
          <Typography
            variant="h5"
            component="h2"
            style={{ marginTop: 10, marginBottom: 10 }}
          >
            Add Member
          </Typography>
        </section>

        <Box sx={{ flexGrow: 1, marginBottom: 2 }}>
          <AppBar position="static" color={"transparent"}>
            <Toolbar>
              <Search>
                <SearchIconWrapper>
                  <SearchIcon />
                </SearchIconWrapper>
                <InputBase
                  style={{ paddingLeft: 50 }}
                  placeholder={"search..."}
                  onChange={(event) => {
                    setKeyword(event.target.value);
                    searchUser(event.target.value, pageSearchSize, pageSearch);
                  }}
                />
              </Search>
            </Toolbar>
          </AppBar>
        </Box>

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
                <StyledTableCell align="center">Status</StyledTableCell>
                <StyledTableCell align="center">Role</StyledTableCell>
                <StyledTableCell align="center">Add</StyledTableCell>
                <StyledTableCell align="center">Delete</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {searchUsers.map((s, index) => (
                <StyledTableRow>
                  <StyledTableCell>
                    <b>{index + 1 + (pageSearch - 1) * pageSearchSize}</b>
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
                    {s.status != null ? (
                      <b>
                        <span
                          style={{
                            color: getColorRegisterStatus(`${s.status}`),
                          }}
                        >{`${s.status}`}</span>
                      </b>
                    ) : (
                      <b>
                        <span
                          style={{
                            color: getColorRegisterStatus("NOT REGISTER"),
                          }}
                        >
                          NOT REGISTER
                        </span>
                      </b>
                    )}
                  </StyledTableCell>

                  <StyledTableCell>
                    <TextField
                      autoFocus
                      // required
                      select
                      id="Role"
                      label="Role"
                      placeholder="Role"
                      onChange={(event) => {
                        //setIsPublic(event.target.value);
                        let t = [...searchUsers];
                        t[index].role = event.target.value;
                        setSearchUsers(t);
                      }}
                      value={searchUsers[index].role}
                    >
                      {roles.map((role, i) => (
                        <MenuItem key={role} value={role}>
                          {role}
                        </MenuItem>
                      ))}
                    </TextField>
                  </StyledTableCell>

                  <StyledTableCell align="center">
                    {s.status === "PENDING" ? (
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
                            "/techer-manager-student-register-contest",
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
                    ) : s.status !== "SUCCESSFUL" ? (
                      <Button
                        variant="contained"
                        color="light"
                        style={{ marginLeft: "45px" }}
                        onClick={() => {
                          let body = {
                            contestId: contestId,
                            userId: s.userName,
                            role: s.role,
                          };
                          console.log("body of add user to contest ", body);
                          successful.push(s);
                          request(
                            "POST",
                            "/add-user-to-contest",
                            {},
                            {},
                            body
                          ).then(() => {
                            setLoad(false);
                            setLoad(true);
                            searchUser(keyword, pageSearchSize, pageSearch);
                          });
                        }}
                      >
                        ADD
                      </Button>
                    ) : (
                      <div></div>
                    )}
                  </StyledTableCell>

                  <StyledTableCell align="center">
                    {s.status === "SUCCESSFUL" ? (
                      <Button
                        variant="contained"
                        color="light"
                        style={{ marginLeft: "45px" }}
                        onClick={() => {
                          let body = {
                            contestId: contestId,
                            userId: s.userName,
                          };

                          request(
                            "POST",
                            "/delete-user-contest",
                            {},
                            {},
                            body
                          ).then(() => {
                            setLoad(false);
                            setLoad(true);
                            searchUser(keyword, pageSearchSize, pageSearch);
                          });
                        }}
                      >
                        DELETE
                      </Button>
                    ) : (
                      <div></div>
                    )}
                  </StyledTableCell>
                </StyledTableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        <br />
        <br />
        <Grid container spacing={12}>
          <Grid item xs={6}>
            <TextField
              variant={"outlined"}
              autoFocus
              size={"small"}
              required
              select
              id="pageSize"
              value={pageSearchSize}
              onChange={handlePageSearchSizeChange}
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
              count={totalPageSearch}
              page={pageSearch}
              siblingCount={1}
              boundaryCount={1}
              variant="outlined"
              shape="rounded"
              onChange={(event, value) => {
                setPageSearch(value);
                searchUser(keyword, pageSearchSize, pageSearch);
              }}
            />
          </Grid>
        </Grid>

        <br />
        <br />
      </div>
    </div>
  );
}
