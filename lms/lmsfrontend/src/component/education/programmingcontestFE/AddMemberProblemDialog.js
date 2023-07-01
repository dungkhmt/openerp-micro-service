//import { makeStyles } from "@mui/styles"; //"@material-ui/core/styles";
import { makeStyles } from "@material-ui/core/styles";

import { MenuItem, TextField } from "@mui/material";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import { HustModal } from "erp-hust/lib/HustModal/HustModal";
import React, { useState } from "react";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 480,
    minHeight: 64,
  },
  btn: { margin: "4px 8px" },
}));
export default function AddMemberProblemDialog(props) {
  const { open, onClose, onUpdateInfo, selectedUserId, rolesList } = props;
  const classes = useStyles();
  const [selectRole, setSelectRole] = useState(null);
  console.log("rolesList", rolesList);
  return (
    <HustModal
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
            {rolesList.map((role, i) => (
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
            Cancel
          </TertiaryButton>
          <PrimaryButton
            className={classes.btn}
            onClick={() => onUpdateInfo(selectRole, selectedUserId)}
          >
            Add
          </PrimaryButton>
        </>
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}
