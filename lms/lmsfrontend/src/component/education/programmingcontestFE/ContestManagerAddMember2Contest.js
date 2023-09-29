import React, {useEffect, useState} from "react";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import SearchIcon from "@mui/icons-material/Search";
import {Search, SearchIconWrapper} from "./lib";
import {InputBase} from "@mui/material";
import StandardTable from "component/table/StandardTable";
import {Button} from "@mui/material/";
import AddMember2ContestDialog from "./AddMember2ContestDialog";
import {request} from "../../../api";

export default function ContestManagerAddMember2Contest(props) {
  const contestId = props.contestId;
  const [searchUsers, setSearchUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [pageSearchSize, setPageSearchSize] = useState(10);
  const [open, setOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [rolesApproved, setRolesApproved] = useState([]);
  const [rolesNotApproved, setRolesNotApproved] = useState([]);

  const columns = [
    { title: "Index", field: "index" },
    { title: "UserID", field: "userName" },
    { title: "Full Name", field: "fullName" },
    {
      title: "Action",
      render: (row) => (
        <Button variant="contained" onClick={() => handleClick(row["userName"])}>Select</Button>
      ),
    },
  ];
  function handleModalClose() {
    setOpen(false);
  }
  function onUpdateInfo(selectRole, selectedUserId) {
    //alert("onUpdateInfo " + selectRole + ":" + selectedUserId);
    let body = {
      contestId: contestId,
      userId: selectedUserId,
      role: selectRole,
    };

    request(
      "post",
      "/contests/users",
      (res) => {
        alert("Add successully");
        setOpen(false);
      },
      {},
      body
    ).then();
  }
  function handleClick(u) {
    request(
      "get",
      "/contests/" + contestId + "/users/" + u + "/roles",
      (res) => {
        setRolesApproved(res.data.rolesApprovedInContest);
        setRolesNotApproved(res.data.rolesNotInContest);
        setSelectedUserId(u);
        setOpen(true);
      }
    );
  }
  function getRoles() {
    request("get", "/contests/roles", (res) => {
      setRoles(res.data);
    }).then();
  }
  function searchUser(keyword, s, p) {
    request(
      "get",
      // "/contests/" + contestId + "/users" +
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
    searchUser(keyword, pageSearchSize, 1);
    getRoles();
  }, []);
  return (
    <div>
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
                  searchUser(event.target.value, pageSearchSize, 1);
                }}
              />
            </Search>
          </Toolbar>
        </AppBar>
      </Box>

      <StandardTable
        title={"Users"}
        columns={columns}
        data={searchUsers}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: false,
          sorting: true,
        }}
      />
      <AddMember2ContestDialog
        open={open}
        onClose={handleModalClose}
        onUpdateInfo={onUpdateInfo}
        selectedUserId={selectedUserId}
        rolesApproved={rolesApproved}
        rolesNotApproved={rolesNotApproved}
      />
    </div>
  );
}
