//import { makeStyles } from "@mui/styles"; //"@material-ui/core/styles";
import {makeStyles} from "@material-ui/core/styles";

import {MenuItem, TextField} from "@mui/material";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import React, {useState} from "react";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 480,
    minHeight: 64,
  },
  btn: { margin: "4px 8px" },
}));
export default function AddMember2ContestDialog(props) {
  const {
    open,
    onClose,
    onUpdateInfo,
    selectedUserId,
    rolesApproved,
    rolesNotApproved,
  } = props;
  const classes = useStyles();
  const [selectRole, setSelectRole] = useState(null);
  return (
    <CustomizedDialogs
      open={open}
      handleClose={onClose}
      title={`Add member ${selectedUserId}`}
      // contentTopDivider
      content={
        <div>
          {/*rolesApproved != null ? rolesApproved.map((r, i) => ({ r })) : ""*/}

          <TextField
            required
            id="ROLE"
            variant="outlined"
            size="small"
            label="Role"
            select
            value={selectRole}
            style={{ width: 280, marginLeft: 8 }}
            onChange={(e) => {
              setSelectRole(e.target.value);
            }}
          >
            {rolesNotApproved.map((role, i) => (
              <MenuItem key={role} value={role}>
                {role}
              </MenuItem>
            ))}
          </TextField>
        </div>
      }
      actions={
        <>
          <TertiaryButton className={classes.btn} onClick={onClose}>
            Huỷ
          </TertiaryButton>
          <PrimaryButton
            className={classes.btn}
            onClick={() => onUpdateInfo(selectRole, selectedUserId)}
          >
            Cập nhật
          </PrimaryButton>
        </>
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}
