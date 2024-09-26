import React, {useEffect, useState} from "react";
import MaterialTable from "material-table";
import {request} from "api";
import AddIcon from "@material-ui/icons/Add";
import {useHistory} from "react-router-dom";

import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import SearchIcon from "@mui/icons-material/Search";
import {Search, SearchIconWrapper} from "../programmingcontestFE/lib";
import {InputBase} from "@mui/material";
import StandardTable from "component/table/StandardTable";
import {Button, MenuItem, TextField} from "@mui/material/";
import AddMember2QuizDialog from "./AddMember2QuizDialog";

export default function QuizUserRole(props) {
  const questionId = props.questionId;
  const [participants, setParticipants] = useState([]);
  const history = useHistory();

  //const contestId = "GENERAL_CONTEST";
  const [searchUsers, setSearchUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [pageSearchSize, setPageSearchSize] = useState(10);
  const [totalPageSearch, setTotalPageSearch] = useState(0);
  const [pageSearch, setPageSearch] = useState(1);

  const [open, setOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [rolesApproved, setRolesApproved] = useState([]);
  const [rolesNotApproved, setRolesNotApproved] = useState([]);

  const columns = [
    { title: "UserID", field: "userId" },
    { title: "Role", field: "roleId" },
  ];

  const columnUsers = [
    { title: "Index", field: "index" },
    { title: "UserID", field: "userName" },
    { title: "FullName", field: "fullName" },
    {
      title: "Action",
      render: (row) => (
        <Button onClick={() => handleClick(row["userName"])}>Select</Button>
      ),
    },
  ];

  function getUserRoles() {
    request(
      "get",
      "get-users-granted-to-quiz-question/" + questionId,
      (res) => {
        setParticipants(res.data);
      }
    );
  }

  function handleModalClose() {
    setOpen(false);
  }
  function onUpdateInfo(selectRole, selectedUserId) {
    //alert("onUpdateInfo " + selectRole + ":" + selectedUserId);
    let body = {
      questionId: questionId,
      userId: selectedUserId,
      roleId: selectRole,
    };

    request(
      "post",
      "/add-quiz-question-user-role",
      (res) => {
        alert("Add successully");
        setOpen(false);
      },
      {},
      body
    ).then();
  }
  function handleClick(u) {
    //alert("click " + u);
    request(
      "get",
      "/get-roles-user-not-granted-in-quiz-question/" + questionId + "/" + u,
      (res) => {
        //console.log("get-roles-user-not-granted-in-quiz-test, res = ", res);
        //setRolesApproved(res.data.rolesApprovedInContest);
        setRolesNotApproved(res.data);
        setSelectedUserId(u);
        setOpen(true);
      }
    );
  }
  function getRoles() {
    request("get", "/get-list-roles-contest", (res) => {
      console.log("getRoles, res.data = ", res.data);
      setRoles(res.data);
    }).then();
  }
  function searchUser(keyword, s, p) {
    request(
      "get",
      "/search-user" +
        //"/search-user/" +
        //  contestId +
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
    getUserRoles();
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
                  searchUser(event.target.value, pageSearchSize, pageSearch);
                }}
              />
            </Search>
          </Toolbar>
        </AppBar>
      </Box>

      <StandardTable
        title={"DS Users"}
        columns={columnUsers}
        data={searchUsers}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />

      <MaterialTable
        title={"User Role"}
        columns={columns}
        data={participants}
        onRowClick={(event, rowData) => {
          console.log(rowData);
        }}
        actions={[
          {
            icon: () => {
              return <AddIcon color="primary" fontSize="large" />;
            },
            tooltip: "Thêm mới",
            isFreeAction: true,
            onClick: () => {
              history.push("");
            },
          },
        ]}
      />

      <AddMember2QuizDialog
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
