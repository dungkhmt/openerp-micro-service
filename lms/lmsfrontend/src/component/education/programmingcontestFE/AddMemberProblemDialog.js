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
  const [selectRole, setSelectRole] = useState(null);
  console.log("rolesList", rolesList);
  return (
    <HustModal
      open={open}
      onClose={onClose}
      title={`Add member ${selectedUserId}`}
      textOk="Add"
      onOk={() => onUpdateInfo(selectRole, selectedUserId)}
    >
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
    </HustModal>
  );
}
