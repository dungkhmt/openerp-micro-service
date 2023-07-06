import {MenuItem, TextField} from "@mui/material";
import {HustModal} from "erp-hust/lib/HustModal/HustModal";
import React, {useState} from "react";

export default function AddMemberProblemDialog(props) {
  const { open, onClose, onUpdateInfo, selectedUserId, rolesList } = props;
  const [selectRole, setSelectRole] = useState(rolesList[0]);

  return (
    <HustModal
      open={open}
      onClose={onClose}
      title={`Assign role for user:  ${selectedUserId}`}
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
