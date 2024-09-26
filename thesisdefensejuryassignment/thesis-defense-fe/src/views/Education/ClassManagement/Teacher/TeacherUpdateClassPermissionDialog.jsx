import {Button, Dialog, DialogContent, DialogTitle, MenuItem, TextField,} from "@material-ui/core";
import React, {useEffect, useState} from "react";
import {request} from "../../../../api";

export default function TClassUpdatePopup(props) {
  const { open, performUpdate, setOpen, classId } = props;
  const [roles, setRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [selectedUserLoginId, setSelectedUserLoginId] = useState(null);
  function updateStatus(status) {
    request(
      "get",
      "/edu/class/update-class-status?classId=" + classId + "&status=" + status
    ).then((res) => {
      setOpen(false);
    });
  }
  function getRoles() {
    request("GET", "/edu/class/get-role-list-educlass-userlogin", (res) => {
      setRoles(res.data);
      console.log("getRoles res = ", res);
    });
  }
  function handleChangeUserLoginId(e) {
    setSelectedUserLoginId(e.target.value);
  }
  function performUpdateRole() {
    let body = {
      classId: classId,
      userLoginId: selectedUserLoginId,
      roleId: selectedRole,
    };
    request(
      "POST",
      "edu/class/add-class-user-login-role",
      (res) => {
        //alert("assign teacher to class " + res.data);
        //setIsProcessing(false);
      },
      { 401: () => {} },
      body
    );
  }
  useEffect(() => {
    getRoles();
  }, []);
  return (
    <Dialog open={open}>
      <DialogTitle>Phân quyền cho giáo viên</DialogTitle>
      <DialogContent>
        <div
          style={{
            display: "flex",
            justifyContent: "space-around",
            minWidth: "500px",
            border: "1px solid black",
          }}
        >
          <TextField
            label="UserLoginId"
            onChange={handleChangeUserLoginId}
          ></TextField>
          <TextField
            style={{ minWidth: "200px" }}
            label="Chọn vai trò"
            required
            select
            onChange={(e) => setSelectedRole(e.target.value)}
          >
            {roles.map((item) => (
              <MenuItem key={item.roleId} value={item.roleId}>
                {item.description}
              </MenuItem>
            ))}
          </TextField>
        </div>
        <div>
          <Button onClick={() => updateStatus("HIDDEN")}>Hide</Button>
          <Button onClick={() => updateStatus("OPEN")}>Open</Button>
          <Button onClick={() => performUpdateRole()}>Save</Button>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
