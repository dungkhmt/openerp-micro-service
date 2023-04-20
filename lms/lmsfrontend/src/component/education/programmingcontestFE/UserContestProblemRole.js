import React, {useEffect, useState} from "react";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import SearchIcon from "@mui/icons-material/Search";
import {Search, SearchIconWrapper} from "./lib";
import {Button, InputBase} from "@mui/material";
import StandardTable from "component/table/StandardTable";
import {useParams} from "react-router-dom";
import {request} from "../../../api";

export default function UserContestProblemRole() {
  const {problemId} = useParams();
  const [searchUsers, setSearchUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [pageSearchSize, setPageSearchSize] = useState(10);
  const [totalPageSearch, setTotalPageSearch] = useState(0);
  const [pageSearch, setPageSearch] = useState(1);
  const [userRoles, setUserRoles] = useState([]);

  const columnUserRoles = [
    {title: "UserId", field: "userLoginId"},
    {title: "FullName", field: "fullname"},
    {title: "RoleId", field: "roleId"},
  ];
  const columns = [
    {title: "Index", field: "index"},
    {title: "UserID", field: "userName"},
    {title: "FullName", field: "fullName"},
    {
      title: "Action",
      render: (row) => (
        <Button onClick={() => handleClick(row["userName"])}>Select</Button>
      ),
    },
  ];

  function handleClick(u) {
    alert("add role to user " + u);
  }

  function getRoles() {
    request("get", "/get-list-roles-contest", (res) => {
      console.log("getRoles, res.data = ", res.data);
      setRoles(res.data);
    }).then();
  }

  function getUserRoles() {
    request("get", "/get-user-contest-problem-roles/" + problemId, (res) => {
      console.log("getUserRoles, res.data = ", res.data);
      setUserRoles(res.data);
    }).then();
  }

  function searchUser(keyword, s, p) {
    request(
      "get",
      "/search-user-based-keyword" +
      "?size=" +
      s +
      "&page=" +
      (p - 1) +
      "&keyword=" +
      keyword,
      (res) => {
        console.log("res search", res);
        //setSearchUsers(res.data.contents.content);
        const data = res.data.contents.content.map((e, index) => ({
          index: index + 1,
          userName: e.userName,
          fullName: e.lastName + " " + e.middleName + " " + e.firstName,
        }));
        setSearchUsers(data);
        setTotalPageSearch(res.data.contents.totalPages);
      }
    ).then();
  }

  useEffect(() => {
    getRoles();
    getUserRoles();
  }, []);

  return (
    <div>
      <Box sx={{flexGrow: 1, marginBottom: 2}}>
        <AppBar position="static" color={"transparent"}>
          <Toolbar>
            <Search>
              <SearchIconWrapper>
                <SearchIcon/>
              </SearchIconWrapper>
              <InputBase
                style={{paddingLeft: 50}}
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

      <StandardTable
        title={"DS Users"}
        columns={columns}
        data={searchUsers}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
      <StandardTable
        title={"DS User & Roles"}
        columns={columnUserRoles}
        data={userRoles}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 50,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
}
