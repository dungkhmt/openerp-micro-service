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
import AddMemberProblemDialog from "./AddMemberProblemDialog";
import { PROBLEM_ROLE } from "utils/constants";
import { errorNoti, successNoti } from "utils/notification";

export default function UserContestProblemRole() {
  const {problemId} = useParams();
  const [searchUsers, setSearchUsers] = useState([]);
  const [pageSearchSize, setPageSearchSize] = useState(5);
  const [pageSearch, setPageSearch] = useState(1);
  const [userRoles, setUserRoles] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);

  const columnUserRoles = [
    {title: "User Id", field: "userLoginId"},
    {title: "Full Name", field: "fullname"},
    {title: "Role", field: "roleId"},
    {
      title: "Action",
      render: (row) => (
        <Button onClick={() => handleRemove(row["userLoginId"],row["roleId"])}>Remove</Button>
      ),
    },
  ];
  const columns = [
    {title: "Index", field: "index"},
    {title: "User ID", field: "userName"},
    {title: "Full Name", field: "fullName"},
    {
      title: "Action",
      render: (row) => (
        <Button onClick={() => handleClick(row["userName"])}>Select</Button>
      ),
    },
  ];
  function handleRemove(userId, roleId){
    console.log('remove ' + userId + ',' + roleId);
    let body = {
      problemId: problemId,
      userId: userId,
      roleId: roleId,
    };
    request(
      "delete",
      "/problems/users/role",
      (res) => {
        if (res.data) successNoti("Remove role of user to problem successfully", 3000);
        else errorNoti("Cannot remove user " + userId + " with role " + roleId + " from the problem", 3000);
        //setOpen(false);
      },
      {
        500: () => { 
          errorNoti("Server error", 3000);
          setOpen(false);
        },
      },
      body
    ).then();
  }
  function handleClick(u) {
    setSelectedUserId(u);
    setOpen(true);
  }

  function handleModalClose() {
    setOpen(false);
  }

  function onUpdateInfo(selectRole, selectedUserId) {
    let body = {
      problemId: problemId,
      userId: selectedUserId,
      roleId: selectRole,
    };

    request(
      "post",
      "/problems/users/role",
      (res) => {
        if (res.data) successNoti("Add user to problem successfully", 3000);
        else errorNoti("You have already added this user to this problem before", 3000);
        setOpen(false);
      },
      {
        500: () => { 
          errorNoti("Server error", 3000);
          setOpen(false);
        },
      },
      body
    ).then();
  }


  function getUserRoles() {
    request("get", "/problems/" + problemId + "/users/role", (res) => {
      setUserRoles(res.data);
    }).then();
  }

  function searchUser(keyword, s, p) {
    request(
      "get",
      "/users" +
      "?size=" +
      s +
      "&page=" +
      (p - 1) +
      "&keyword=" +
      keyword,
      (res) => {
        const data = res.data.content.map((e, index) => ({
          index: index + 1,
          userName: e.userLoginId,
          fullName: (e.lastName ? e.lastName : "") + " " + (e.firstName ? e.firstName : ""),
        }));
        setSearchUsers(data);
      }
    ).then();
  }

  useEffect(() => {
    getUserRoles();
  }, []);

  useEffect(() => {
    getUserRoles();
  }, []);


  return (
    <div>
      <Box sx={{flexGrow: 1}}>
        <AppBar position="static" color={"inherit"}>
          <Toolbar>
            <Search>
              <SearchIconWrapper>
                <SearchIcon />
              </SearchIconWrapper>
              <InputBase
                style={{ paddingLeft: 50, paddingRight: 800 }}
                placeholder={"Search User to add role"}
                onChange={(event) => {
                  searchUser(event.target.value, pageSearchSize, pageSearch);
                }}
              />
            </Search>
          </Toolbar>
        </AppBar>
      </Box>

      <StandardTable
        title={"Search Result"}
        columns={columns}
        data={searchUsers}
        hideCommandBar
        options={{
          selection: false,
          pageSize: pageSearchSize,
          search: false,
          sorting: true,
        }}
      />

      <Box sx={{margin: "1.5rem"}}/>
      <StandardTable
        title={"Users & Roles Management"}
        columns={columnUserRoles}
        data={userRoles}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
      />
      <AddMemberProblemDialog
        open={open}
        onClose={handleModalClose}
        onUpdateInfo={onUpdateInfo}
        selectedUserId={selectedUserId}
        rolesList={Object.values(PROBLEM_ROLE).filter((e) => e !== PROBLEM_ROLE.OWNER)}
      />
    </div>
  );
}
