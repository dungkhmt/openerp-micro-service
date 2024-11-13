import {MenuItem,} from "@material-ui/core";
import React, {useEffect, useMemo, useState} from "react";
import {request} from "../../../../api";
import StandardTable from "../../../../component/table/StandardTable";
import {makeStyles} from "@material-ui/core/styles";
import {defaultDatetimeFormat} from "../../../../utils/dateutils";
import {
  Autocomplete,
  Box,
  Card,
  CardContent,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography
} from "@mui/material";
import Dialog from "@mui/material/Dialog";
import Button from "@mui/material/Button";

import {errorNoti, successNoti} from "../../../../utils/notification";

const useStyles = makeStyles(theme => ({
  teacherUpdateClassPermissionsDlg: {
    '& .MuiDialog-paperWidthLg': {
      minWidth: '960px',
      maxHeight: '680px'
    }
  },
  permissionAssignContainer: {
    margin: '10px 0',
    display: "flex",
    columnGap: '20px',
    alignItems: 'center'
  },
  permissionInput: {
    minWidth: '300px',
    flexGrow: 1
  },
  permissionTableWrapper: {
    '& table thead tr': {
      '& th:nth-child(3)': {
        maxWidth: '160px !important'
      },
      '& th:nth-child(4)': {
        maxWidth: '100px !important'
      }
    }
  }
}))

export default function TClassUpdatePopup(props) {
  const classes = useStyles();
  const { open, setOpen, classId } = props;
  const [roles, setRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [userLoginRolesOfCurrentClass, setUserLoginRolesOfCurrentClass] = useState([]);
  const [enabledUserLoginIds, setEnabledUserLoginIds] = useState([]);
  const [loginIdInput, setLoginIdInput] = useState({ search: "", selected: null })

  const descriptionOfRoles = useMemo(() => {
    let descriptions = {};
    roles.forEach(role => descriptions[role.roleId] = role.description)
    return descriptions
  }, [roles]);

  useEffect(getUserLoginRolesOfCurrentClass, [])
  useEffect(getEnabledUserLoginIds, [loginIdInput.search]);
  useEffect(getRoles, []);

  const userLoginRolesColumns = [
    { field: "userLoginId", title: "User login id" },
    { field: "roleId", title: "Vai trò",
      render: permission => (
        <Typography>{ descriptionOfRoles[permission.roleId] }</Typography>
      )
    },
    { field: "fromDate", title: "Ngày cấp quyền",
      cellStyle: {
        maxWidth: '160px'
      },
      render: permission => (
        <Typography>{ defaultDatetimeFormat(permission.fromDate) }</Typography>
      )
    },
    { field: "", title: "",
      cellStyle: {
        maxWidth: '100px'
      },
      render: permission => (
        <Button variant="outlined"
                onClick={() => revokePermission(permission)}>
          Revoke
        </Button>
      )
    }
  ]

  function revokePermission(deletedPermission) {
    let successHandler = () => {
      successNoti("Đã thu hồi quyền, xem kết quả ở bảng");
      setUserLoginRolesOfCurrentClass(userLoginRolesOfCurrentClass.filter(permission => permission !== deletedPermission));
    }
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi thu hồi quyền")
    }
    request("DELETE", "edu/class/class-user-login-roles", successHandler, errorHandlers, deletedPermission);
  }

  function updateStatus(status) {
    request("get", "/edu/class/update-class-status?classId=" + classId + "&status=" + status).then((res) => {
      setOpen(false);
    });
  }

  function getRoles() {
    request("GET", "/edu/class/get-role-list-educlass-userlogin", (res) => {
      setRoles(res.data);
      console.log("getRoles res = ", res);
    });
  }

  function getEnabledUserLoginIds() {
    request("GET", `/user-login-ids`, (res) => {
      console.log("User login ids", res);
      setEnabledUserLoginIds(res.data);
    }, null, null, {params: { search: loginIdInput.search }})
  }

  function getUserLoginRolesOfCurrentClass() {
    request("GET", `/edu/class/${classId}/user-login-roles`, (res) => {
      console.log("User login role of class", classId, res);
      setUserLoginRolesOfCurrentClass(res.data);
    })
  }



  function assignNewPermission() {
    let newPermission = {
      classId: classId, userLoginId:
      loginIdInput.selected,
      roleId: selectedRole,
    };
    let notifySuccessAndUpdateResult = (res) => {
      successNoti("Phân quyền thành công, xem kết quả ở bảng", 3000);
      setUserLoginRolesOfCurrentClass(prevPermissions => [...prevPermissions, res.data]);
    }
    let errorHandlers = {
      onError: () => errorNoti("Đã có lỗi xảy ra khi phân quyền")
    }
    request("POST", "/edu/class/add-class-user-login-role", notifySuccessAndUpdateResult, errorHandlers, newPermission);
  }


  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)} maxWidth="lg"
      className={classes.teacherUpdateClassPermissionsDlg}>
      <DialogTitle>Phân quyền cho giáo viên</DialogTitle>

      <DialogContent>

        <Box>
          <div className={classes.permissionAssignContainer}>
            <Autocomplete
              disablePortal
              id="asynchronous-demo"
              className={`login-id-inp ${classes.permissionInput}`}
              sx={{ minWidth: 300, flexGrow: 1 }}
              options={enabledUserLoginIds}
              isOptionEqualToValue={(option, value) => option === value}
              getOptionLabel={ (loginId) => loginId }
              onInputChange={ (evt, search) => setLoginIdInput({...loginIdInput, search}) }
              onChange={ (evt, selected) => setLoginIdInput({...loginIdInput, selected }) }
              renderInput={(params) => <TextField {...params} label="Chọn login id" />}
            />


            <TextField
              className={classes.permissionInput}
              style={{ minWidth: "300px", flexGrow: 1 }}
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

            <Button variant="outlined" onClick={assignNewPermission}>Assign</Button>
          </div>
        </Box>

        <Card>
          <CardContent className={classes.permissionTableWrapper}>
            <StandardTable
              classes={"permissionTable"}
              title="Các quyền đã cấp"
              columns={userLoginRolesColumns}
              data={userLoginRolesOfCurrentClass}
              hideCommandBar
              options={{
                search: true,
                paging: false,
                selection: false
              }}
            />
          </CardContent>
        </Card>
      </DialogContent>

      <DialogActions>
        <Button onClick={() => updateStatus("HIDDEN")}>Hide</Button>
        <Button onClick={() => updateStatus("OPEN")}>Open</Button>
        <Button onClick={() => setOpen(false)}>Close</Button>
      </DialogActions>
    </Dialog>
  );
}
